package com.cfcc.itfe.webservice.guangdong;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.ocsp.RespData;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.ParamConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsMankeymodeDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.pk.TsOrganPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.dataquery.findatastatdown.FinDataStatDownService;
import com.cfcc.itfe.service.dataquery.tipsdataexport.TipsDataExportService;
import com.cfcc.itfe.service.para.paramtransform.ParamTransformService;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/*
 * 业务数据导出
 */
public class BizDataExport {
	private static Log log = LogFactory.getLog(BizDataExport.class);
	/*
	 * /<billType,业务类型billType在数组ParamConstant.TABS中的位置>
	 */
	private Map<String, Integer> billTypeMap = new HashMap<String, Integer>();
	/**
	 * 文件路径分隔符
	 */
	private static String separator = System.getProperty("file.separator");

	/*
	 * 初始化billTypeMap
	 */
	public BizDataExport() {
		billTypeMap.put("30", 0);
		billTypeMap.put("31", 1);
		billTypeMap.put("32", 2);
		billTypeMap.put("33", 3);
		billTypeMap.put("34", 4);
		billTypeMap.put("35", 5);
		billTypeMap.put("36", 6);
		billTypeMap.put("37", 7);
		billTypeMap.put("38", 8);
		billTypeMap.put("39", 9);
		billTypeMap.put("40", 10);
		billTypeMap.put("41", 11);
		billTypeMap.put("42", 12);
		billTypeMap.put("43", 13);
		billTypeMap.put("44", 14);
		billTypeMap.put("45", 15);
		billTypeMap.put("46", 16);
	}

	/*
	 * 业务数据导出 billType业务类型 paramStr以“,”分割的参数---国库代码,委托日期
	 */
	public String bizFileExport(String billType, String paramStr) {
		// 返回数据
		String ls_Return = "";
		try {
			/**
			 * 1、校验各种参数是否符合要求
			 */
			String[] params = paramStr.split(",");
			String[] tempParams = new String[2];
			Arrays.fill(tempParams, "");
			for (int i = 0; i < params.length; i++) {
				tempParams[i] = params[i];
			}
			Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			String trecode = tempParams[0];
			if (StringUtils.isBlank(trecode)) { // 国库代码
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[国库代码]不能为空!");
			} else {
				if (!treasuryInfo.containsKey(trecode)) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "国库代码" + trecode + "不存在!");
				}
			}
			String ls_CommitDate = tempParams[1];
			if (StringUtils.isBlank(ls_CommitDate)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[委托日期]不能为空!");
			} else {
				if (!ls_CommitDate.matches("^20[0-9]{6}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[委托日期]格式需为[YYYYMMDD]!");
				}
			}
			String orgcode = treasuryInfo.get(trecode).getSorgcode();
			// 查询文件与包对应关系表
			TvFilepackagerefDto filepackagerefDto = new TvFilepackagerefDto();
			filepackagerefDto.setSorgcode(orgcode);
			filepackagerefDto.setStrecode(trecode);
			/** 调拨支出和一般预算支出在文件与包对应关系表里面都存的是17 */
			filepackagerefDto.setSoperationtypecode(StringUtils.equals(billType, BizTypeConstant.BIZ_TYPE_PAY_OUT2) ? BizTypeConstant.BIZ_TYPE_PAY_OUT : billType);
			filepackagerefDto.setScommitdate(ls_CommitDate);
			filepackagerefDto.setSretcode(DealCodeConstants.DEALCODE_ITFE_RECEIPT);
			filepackagerefDto.setSchkstate(StateConstant.CONFIRMSTATE_YES);
			List<TvFilepackagerefDto> fileDtoList = CommonFacade.getODB().findRsByDtoWithUR(filepackagerefDto);
			if (fileDtoList != null && fileDtoList.size() > 0) {
				List<String> reports = new ArrayList<String>();
				for (TvFilepackagerefDto temDto : fileDtoList) {
					String fileName = temDto.getSfilename();
					if (StringUtils.isNotBlank(fileName) && //
							StringUtils.equals(billType, BizTypeConstant.BIZ_TYPE_PAY_OUT2) && //
							(endsWithIgnoreCase(fileName, "170.txt") || endsWithIgnoreCase(fileName, "171.txt"))) {
						continue; 
					}
					String fullFileName = ITFECommonConstant.FILE_ROOT_PATH + ITFECommonConstant.FILE_UPLOAD + orgcode + separator + ls_CommitDate + separator + fileName;
					File tempFile = new File(fullFileName);
					if (tempFile.isFile() && tempFile.exists()) {
						String reportString = FileUtil.getInstance().readFile(fullFileName);
						if (StringUtils.isNotBlank(reportString)) {
							reports.add(reportString);
						}
					}
				}
				if (reports != null && reports.size() > 0) {
					ls_Return = WsUtils.generateResultXml(StateConstant.COMMON_YES, "", "业务数据申请成功!", reports);
				} else {
					ls_Return = WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "请求的业务数据不存在(只能申请业务状态为'已回执'的数据)!");
				}
			} else {
				return WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "请求的业务数据不存在(只能申请业务状态为'已回执'的数据)!");
			}
		} catch (Exception e) {
			log.error("广东业务数据申请出现异常(gd_webservice): " + e.getMessage(), e);
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "广东业务数据申请出现异常!");
		}
		return ls_Return;
	}

	/*
	 * 参数数据导出 billType业务类型 paramStr为国库代码
	 */
	public String ParamDataExport(String billType, String paramStr,String splitStr) {
		String ls_Return = "";
		try {
			// 查询Dto
			TrStockdayrptDto fdto = new TrStockdayrptDto();
			/**
			 * 1、校验各种参数是否符合要求
			 */
			Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			String trecode = paramStr;
			if (StringUtils.isBlank(trecode)) { // 国库代码
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[国库代码]不能为空!");
			} else {
				if (!treasuryInfo.containsKey(trecode)) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "国库代码" + trecode + "不存在！");
				}
			}
			String orgcode = treasuryInfo.get(trecode).getSorgcode();
			ParamTransformService paramTransformService = new ParamTransformService();
			paramTransformService.setLoginInfo(packLoginInfo(orgcode));
			// String lineSplit = ",";// 字段分割符
			List checkList = new ArrayList();
			int nuber = billTypeMap.get(billType);
			TdEnumvalueDto dto = new TdEnumvalueDto();
			dto.setSvalue(ParamConstant.TABS[nuber][0]);
			dto.setSvaluecmt(ParamConstant.TABS[nuber][1]);
			checkList.add(dto);
			List<String> filelist = paramTransformService.export(checkList, splitStr, orgcode); 
			if (filelist != null && filelist.size() > 0) {
				if (filelist.get(0).startsWith("NODATA")) {
					return WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "请求的参数数据不存在!");
				} else {
					String reportFileName = filelist.get(0); // 目前控制一次只能申请一个文件
					String path = ITFECommonConstant.FILE_ROOT_PATH;// + separator;
					reportFileName = path + reportFileName;
					ls_Return = WsUtils.generateResultXml(StateConstant.COMMON_YES, "参数申请成功!", FileUtil.getInstance().readFile(reportFileName));
				}
			} else {
				return WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "请求的参数数据不存在!");
			}
		} catch (Exception e) {
			log.error("广东参数申请出现异常(gd_webservice): " + e.getMessage(), e);
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "广东参数申请出现异常(gd_webservice): " + e.getMessage());
		}
		return ls_Return;
	}

	/*
	 * 财政报表库存日报表(3128) Data Export(库表格式) paramStr以“,”分割的参数 paramStr规则 财政报表-预算收入日报表(3128) :依次分别为国库代码 ，报表开始日期日期，终止日期,征收机关代码，辖属标志，电子税票导出范围
	 */
	public String IncomeDayrptDataExportTable(String billType, String paramStr) {
		String ls_Return = "";
		try {
			// 查询Dto
			// TrStockdayrptDto fdto = new TrStockdayrptDto();
			/**
			 * 1、校验各种参数是否符合要求
			 */
			String[] params = paramStr.split(",");
			String[] tempParams = new String[6];
			Arrays.fill(tempParams, "");
			for (int i = 0; i < params.length; i++) {
				tempParams[i] = params[i];
			}
			Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			Map<String, TsConvertfinorgDto> cacheFincInfo = SrvCacheFacade.cacheFincInfo(null);
			String trecode = tempParams[0];
			if (StringUtils.isBlank(trecode)) { // 国库代码
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[国库代码]不能为空!");
			} else {
				if (!treasuryInfo.containsKey(trecode)) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "国库代码" + trecode + "不存在！");
				}
				if (!cacheFincInfo.containsKey(trecode)) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "国库代码[" + trecode + "]对应的财政代码参数不存在或未维护!");
				}
				// fdto.setStrecode(trecode);
			}
			String startDate = tempParams[1]; // 起始日期
			if (StringUtils.isBlank(startDate)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[开始日期]不能为空!");
			} else {
				if (!startDate.matches("^20[0-9]{6}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[开始日期]格式需为[YYYYMMDD]!");
				}
			}
			String endDate = tempParams[2]; // 终止日期
			if (StringUtils.isBlank(endDate)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[终止日期]不能为空!");
			} else {
				if (!endDate.matches("^20[0-9]{6}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[终止日期]格式需为[YYYYMMDD]!");
				}
			}
			if (endDate.compareToIgnoreCase(startDate) < 0) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "开始日期必须小于结束日期!");
			}

			String orgcode = treasuryInfo.get(trecode).getSorgcode(); // 核算主体代码
			String finOrgcode = cacheFincInfo.get(trecode).getSfinorgcode(); // 财政机构代码
			TipsDataExportService tipsDataExportService = new TipsDataExportService();
			tipsDataExportService.setLoginInfo(packLoginInfo(orgcode));

			// 接收文件导出结果
			Map<String, List<String>> map = new HashMap<String, List<String>>();

			/*
			 * 正式开始导出
			 */

			List checklist = new ArrayList();
			TipsParamDto paramdto = new TipsParamDto();
			// paramdto.setSorgcode(orgcode);
			paramdto.setSorgcode(finOrgcode);
			paramdto.setStrecode(trecode);
			String staxorgcode = tempParams[3]; // 征收机关代码
			if (null != staxorgcode && !"".equals(staxorgcode)) {
				paramdto.setStaxorgcode(staxorgcode);
			}
			String sbeflag = tempParams[4]; // 辖属标志
			if (null != sbeflag && !"".equals(sbeflag)) {
				paramdto.setSbeflag(sbeflag);
			}

			paramdto.setStartdate(new java.sql.Date(TimeFacade.parseDate(startDate).getTime()));
			paramdto.setEnddate(new java.sql.Date(TimeFacade.parseDate(endDate).getTime()));

			String ifSub = tempParams[5]; // 电子税票导出范围
			if (!StringUtils.isBlank(ifSub)) {
				if (!ifSub.matches("^[01]{1}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "电子税票导出范围必须为[0-不包含下级国库1-包含下级国库]!");
				}
				paramdto.setIfsub(ifSub);
			}
			paramdto.setExptype("0"); // //采用0库表格式或采用福建格式1
			if ("13".equals(billType)) {
				checklist.add(initValue("3128财政申请收入日报表", StateConstant.RecvTips_3128_SR));
			} else if ("14".equals(billType)) {
				checklist.add(initValue("3128财政申请库存日报表", StateConstant.RecvTips_3128_KC));
			} else if ("15".equals(billType)) {
				checklist.add(initValue("3139财政申请入库流水明细", StateConstant.RecvTips_3139));
			} else if ("16".equals(billType)) {
				checklist.add(initValue("3129财政申请电子税票信息", StateConstant.RecvTips_3129));
			}
			map = tipsDataExportService.generateTipsToFile(checklist, paramdto, "");
			// 返回的文件路径
			List filelist = map.get("files");
			if (filelist != null && filelist.size() > 0) {
				// String reportFileName = (String) filelist.get(0); // 目前控制一次只能申请一个文件
				ls_Return = WsUtils.generateResultXml(StateConstant.COMMON_YES, "报表申请成功!", packFileList2Result(filelist));
			} else {
				return WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "请求的报表不存在或未生成!");
			}
			tipsDataExportService.deleteTheFiles(filelist); // 数据传送之后删除服务器上数据
		} catch (Exception e) {
			log.error("报表接口数据申请出现异常(gd_webservice): " + e.getMessage(), e);
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表接口数据申请出现异常(gd_webservice)!");
		}
		return ls_Return;
	}

	/**
	 * 将导出的多个文件合并到一个字符串中
	 * 
	 * @param fileList
	 * @return
	 * @throws Exception
	 */
	private String packFileList2Result(List fileList) throws Exception {
		StringBuffer sb = new StringBuffer();
		for (Object obj : fileList) {
			String reportFileName = (String) obj;
			sb.append(FileUtil.getInstance().readFile(reportFileName)).append("\r\n");
		}
		return sb.toString();
	}

	public TdEnumvalueDto initValue(String type, String mark) {
		TdEnumvalueDto dto = new TdEnumvalueDto();
		dto.setSvalue("是否导出");
		dto.setSremark(mark);
		dto.setSvaluecmt(type);
		return dto;
	}

	/*
	 * 财政报表-预算收入日报表(3128) Data Export billType报表类型，paramStr以“,”分割的参数 paramStr规则 财政报表-预算收入日报表(3128) :依次分别为国库代码 ，报表日期，预算种类，辖属标志，含款合计标志，调整期标志，征收机关代码
	 */
	public String IncomeDayrptDataExport(String billType, String paramStr) {
		try {
			/**
			 * 1、校验各种参数是否符合要求
			 */
			String[] params = paramStr.split(",");
			String[] tempParams = new String[7];
			Arrays.fill(tempParams, "");
			for (int i = 0; i < params.length; i++) {
				tempParams[i] = params[i];
			}
			Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			String trecode = tempParams[0];
			if (StringUtils.isBlank(trecode)) { // 国库代码
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[国库代码]不能为空!");
			} else {
				if (!treasuryInfo.containsKey(trecode)) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "国库代码" + trecode + "不存在！");
				}
			}
			String orgcode = treasuryInfo.get(trecode).getSorgcode();
			String reportDate = tempParams[1]; // 报表日期
			if (StringUtils.isBlank(reportDate)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[报表日期]不能为空!");
			} else {
				if (!reportDate.matches("^20[0-9]{6}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数中[报表日期]格式需为[YYYYMMDD]!");
				}
			}
			String budgettype = tempParams[2]; // 预算种类
			if (StringUtils.isBlank(budgettype)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "预算种类不能为空![1-预算内 2-预算外]");
			} else {
				if (!budgettype.matches("^[12]{1}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "预算种类必须为[1-预算内 2-预算外]!");
				}
			}
			String belongflag = tempParams[3]; // 辖属标志
			if (StringUtils.isBlank(belongflag)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "辖属标志不能为空![0-本级 1-全辖 3-全辖非本级]");
			} else {
				if (!belongflag.matches("^[013]{1}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "辖属标志必须为[0-本级 1-全辖 3-全辖非本级]!");
				}
			}
			String dividegroup = tempParams[4]; // 是否含款合计
			if (StringUtils.isBlank(dividegroup)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "含款合计标志不能为空![0-否 1-是]");
			} else {
				if (!dividegroup.matches("^[01]{1}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "含款合计标志必须为[0-否 1-是]!");
				}
			}
			String strimflag = tempParams[5]; // 调整期标志
			if (StringUtils.isBlank(strimflag)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "调整期标志不能为空![0-正常期 1-调整期]");
			} else {
				if (!strimflag.matches("^[01]{1}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "调整期标志必须为[0-正常期 1-调整期]!");
				}
			}
			String taxorgcode = tempParams[6]; // 征收机关代码
			if (StringUtils.isBlank(taxorgcode)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "征收机关代码不能为空!");
			}

			/**
			 * 2、开始组装后台调用需要的参数dto
			 */
			TrIncomedayrptDto incomedto = new TrIncomedayrptDto();
			incomedto.setStrecode(trecode);

			HashMap<String, TsConvertfinorgDto> finMap = SrvCacheFacade.cacheFincInfo(orgcode);
			if (!finMap.containsKey(trecode)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "国库代码[" + trecode + "]对应的财政代码参数不存在或未维护!");
			}
			// 财政机关代码
			incomedto.setSfinorgcode(finMap.get(trecode).getSfinorgcode());
			// 日期
			incomedto.setSrptdate(reportDate);
			// 预算种类
			incomedto.setSbudgettype(budgettype);
			// 辖属标志
			incomedto.setSbelongflag(belongflag);
			// 是否含款合计
			incomedto.setSdividegroup(dividegroup);
			// 调整期标志
			incomedto.setStrimflag(strimflag);
			// 征收机关代码
			incomedto.setStaxorgcode(taxorgcode);

			/**
			 * 3、将报表类型封装到list, 目前只允许一次申请一种类型的报表
			 */
			List<TdEnumvalueDto> reportTypeList = new ArrayList<TdEnumvalueDto>();
			TdEnumvalueDto reportTypeDto = new TdEnumvalueDto();
			reportTypeDto.setSvalue(billType);
			reportTypeList.add(reportTypeDto);

			/**
			 * 4、实例化报表服务并调用
			 */
			FinDataStatDownService finDataStatDownService = new FinDataStatDownService();
			finDataStatDownService.setLoginInfo(packLoginInfo(orgcode));

			List<String> reportFileList = finDataStatDownService.makeRptFile(incomedto, reportTypeList);

			/**
			 * 5、处理返回结果, 并将报表返回给调用端
			 */
			if (reportFileList == null || reportFileList.size() == 0) {
				return WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "请求的报表不存在或未生成!");
			}
			String reportFileName = ITFECommonConstant.FILE_ROOT_PATH + reportFileList.get(0); // 目前控制一次只能申请一个文件
			return WsUtils.generateResultXml(StateConstant.COMMON_YES, "报表申请成功!", FileUtil.getInstance().readFile(reportFileName));

		} catch (Exception e) {
			log.error("广东报表申请出现异常(gd_webservice): " + e.getMessage(), e);
			if(e != null && e.getCause() != null && e.getCause().getMessage().contains("地方横联征收机关代码")) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "广东报表申请出现异常(gd_webservice): " + e.getCause().getMessage());
			}
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "广东报表申请出现异常(gd_webservice): " + e.getMessage());
		}
	}

	/**
	 * 为了模拟登录, 自己组装登录信息 @TODO 考虑做成缓存
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public ITFELoginInfo packLoginInfo(String orgcode) throws Exception {
		ITFELoginInfo itfeinfo = new ITFELoginInfo();
		TsUsersDto queryDto = new TsUsersDto();
		queryDto.setSorgcode(orgcode);
		queryDto.setSusertype(StateConstant.User_Type_Normal);
		List userList = CommonFacade.getODB().findRsByDto(queryDto);
		if (userList == null || userList.size() == 0) {
			throw new RuntimeException("用户信息不存在!");
		}
		TsUsersDto userDto = (TsUsersDto) userList.get(0);
		itfeinfo.setSuserName(userDto.getSusername());
		itfeinfo.setSuserType(userDto.getSusertype());
		itfeinfo.setSuserCode(userDto.getSusercode());
		itfeinfo.setOrgKind(userDto.getSorgcode());
		itfeinfo.setSorgcode(userDto.getSorgcode());
		itfeinfo.setCurrentDate(TimeFacade.getCurrent2StringTime());
		itfeinfo.setScertId(userDto.getScertid());

		/**
		 * 取收入是否汇总的值, 由于缓存并没有核算主体的数据(以核算主体代码为key的),所以只能从数据库中取值
		 */
		TsOrganPK topk = new TsOrganPK();
		topk.setSorgcode(userDto.getSorgcode());
		IDto toDto = DatabaseFacade.getODB().find(topk);
		if (toDto == null) {
			throw new RuntimeException("核算主体代码[" + userDto.getSorgcode() + "]在核算主体参数信息中不存在!");
		}
		itfeinfo.setIscollect(((TsOrganDto) toDto).getSiscollect());

		// 取地方特色和加密方式,放在对应loginfo中
		HashMap<String, String> encryptMode = (HashMap<String, String>) ContextFactory.getApplicationContext().getBean("encryptMode");
		String area = AreaSpecUtil.getInstance().getArea();
		String sysflag = AreaSpecUtil.getInstance().getSysflag();
		itfeinfo.setEncryptMode(encryptMode);
		itfeinfo.setArea(area);
		itfeinfo.setSysflag(sysflag);
		// 取加密方式是按核算主体加密还是全省统一
		List<TsMankeymodeDto> _dtoList = DatabaseFacade.getODB().find(TsMankeymodeDto.class);
		if (_dtoList.size() > 0) {
			TsMankeymodeDto _dto = _dtoList.get(0);
			if (StateConstant.KEY_ALL.equals(_dto.getSkeymode())) {
				itfeinfo.setMankeyMode(StateConstant.KEY_ALL);// 全省统一维护
			} else if (StateConstant.KEY_BOOK.equals(_dto.getSkeymode())) {
				itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // 按核算主体维护
			} else {// 
				itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // 目前仅支持按核算主体和全省统一
			}

		} else {
			itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // 默认按照核算主体维护
		}
		// 公共参数配置
		itfeinfo.setPublicparam(ITFECommonConstant.PUBLICPARAM);
		return itfeinfo;
	}

	public static boolean endsWithIgnoreCase(String str, String suffix) {
		return endsWith(str, suffix, true);
	}

	private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
		if ((str == null) || (suffix == null)) {
			return (str == null) && (suffix == null);
		}
		if (suffix.length() > str.length()) {
			return false;
		}
		int strOffset = str.length() - suffix.length();
		return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
	}
}

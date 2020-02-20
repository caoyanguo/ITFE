package com.cfcc.itfe.webservice.guangdong;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.FileResolveCommonService;
import com.cfcc.itfe.service.recbiz.uploadmodule.UploadUIService;
import com.cfcc.itfe.util.FileRootPathUtil;
import com.cfcc.itfe.util.FileUtil;

/**
 * 提供给广东调用的webservice接口<br>
 * 1、业务上传接口<br>
 * 2、报表回传接口<br>
 * 
 * @author hua
 * 
 */
@WebService(endpointInterface = "com.cfcc.itfe.webservice.guangdong.IfileTransfor", serviceName = "IfileTransfor")
public class fileTransforServiceImp implements IfileTransfor {
	private static Log log = LogFactory.getLog(fileTransforServiceImp.class);
	private BizDataExport bizDataExport = new BizDataExport();;

	/**
	 * 业务数据发送
	 * 
	 * @param fileHandler
	 *            文件内容
	 * @param biztype
	 *            业务类型
	 * @param paramStr
	 *            原前置界面需要录入的要素，以逗号分隔
	 * @param fileName
	 *            文件名称
	 * @param treCode
	 *            国库代码
	 * @return
	 */
	public String sendCommBizData(String fileContent, String biztype, String paramStr, String fileName, String treCode) {
		long moment = System.currentTimeMillis();
		log.debug("====广东WebService业务接口调用开始(" + moment + "):[biztype: " + biztype + ", paramStr: " + paramStr + ", fileName: " + fileName + ", treCode: " + treCode + "]====");
		String ls_filekind = WsUtils.verifyBizType(biztype);
		if ("".equals(ls_filekind)) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "业务类型值" + biztype + "不合规范!");
		}
		if (StringUtils.isBlank(treCode)) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "国库代码不能为空!");
		}
		if (!WsUtils.checkFileName(biztype, fileName)) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "文件名称不合规范!");
		}

		List<String> fileList = new ArrayList<String>();
		Boolean isErrorFileNeedDel = Boolean.FALSE;// 定义是否需要删除错误文件
		// 本地文件输出流
		OutputStream os = null;
		try {
			// 根据国库代码缓存取出所在的机构代码
			Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			if (!treasuryInfo.containsKey(treCode)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "国库代码" + treCode + "不存在！");
			}
			String ls_OrgCode = treasuryInfo.get(treCode).getSorgcode();

			// 检查文件是否已经存在，限制文件名称不能重复，因为查询时要以文件名称做查询
			String rootPath = FileRootPathUtil.getInstance().getRoot();
			String uploadFilePath = ITFECommonConstant.FILE_UPLOAD + ls_OrgCode + "/" + DateUtil.date2String2(TimeFacade.getCurrentDate());
			String filePath = rootPath + uploadFilePath;
			fileList.add(uploadFilePath + "/" + fileName);
			File file = new File(filePath, fileName);
			// 1.检查当日文件目录中是否存在文件名称为fileName的文件，2.检查表TV_FILEPACKAGEREF中是否存在该文件名称的记录
			if (WsUtils.verifyImportRepeat(ls_OrgCode, treCode, fileName)) {// "表记录文件存在")
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "该文件已经已经存在, 请不要重复上传!");
			} else {
				createDir(filePath, file); // 创建目录
				FileUtil.getInstance().writeFile(file.getAbsolutePath(), fileContent);
				// 记录接受日志
				saveTvrecvlog(file.getAbsolutePath(), biztype, ls_OrgCode);
			}

			FileResolveCommonService frs = new FileResolveCommonService();
			ITFELoginInfo packLoginInfo = bizDataExport.packLoginInfo(ls_OrgCode);
			frs.setLoginInfo(packLoginInfo);
			MulitTableDto bizDto = null;
			if (BizTypeConstant.BIZ_TYPE_INCOME.equals(biztype)) { // 收入文件
				try {
					FileResultDto fileResultDto = WsUtils.processIncome(fileName, file.getAbsolutePath(), ls_OrgCode, paramStr); // TODO 资金收纳流水号怎么处理?
					// fileResultDto.setStrasrlno(paramStr);//资金收纳流水号
					UploadUIService uploadUIService = new UploadUIService();
					uploadUIService.setLoginInfo(packLoginInfo);
					uploadUIService.UploadDate(fileResultDto);
				} catch (Exception e) { // 由于收入导入接口没有相关的结果返回值, 所以通过异常来判断是否导入成功
					log.error("导入收入文件出现异常(gd_webservice)", e);
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "收入文件上传失败");
				}
				return WsUtils.generateResultXml(StateConstant.COMMON_YES, "文件上传成功!");

			} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY.equals(biztype)//
					|| BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY.equals(biztype)) { // 集中支付额度
				bizDto = frs.loadFile(fileList, "", ls_filekind, null);

			} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK.equals(biztype)//
					|| BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK.equals(biztype)) { // 集中支付清算
				// 退款需要补录来帐资金信息
				if (StringUtils.isBlank(paramStr)) {
					isErrorFileNeedDel = Boolean.TRUE;
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "集中支付退款清算业务参数信息不能为空!(来帐资金信息)");
				}
				String[] tempParams = paramStr.split(",");
				if (tempParams == null || tempParams.length != 5) {
					isErrorFileNeedDel = Boolean.TRUE;
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "集中支付退款清算业务参数信息不合规范或参数个数不足, 多个参数需以逗号(,)分割!");
				}
				String errorInfo = WsUtils.checkPayreckBackParam(tempParams);
				if (StringUtils.isNotBlank(errorInfo)) {
					isErrorFileNeedDel = Boolean.TRUE;
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, errorInfo);
				}
				String famt = tempParams[0]; // 来帐资金总额
				String paydictno = tempParams[1]; // 支付交易序号
				String payentrustdate = tempParams[2]; // 支付委托日期
				String paymsgno = tempParams[3]; // 支付报文编号
				String paysndbnkno = tempParams[4]; // 支付发起行行号
				TvPayreckBankBackDto payreckbackdto = new TvPayreckBankBackDto();
				try {
					payreckbackdto.setFamt(new BigDecimal(famt));
				} catch (Exception e) {
					isErrorFileNeedDel = Boolean.TRUE;
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "集中支付退款清算业务参数信息中[来帐资金总额]格式非法!");
				}
				payreckbackdto.setSpaydictateno(paydictno);
				payreckbackdto.setDpayentrustdate(new java.sql.Date(TimeFacade.parseDate(payentrustdate, "yyyyMMdd").getTime()));
				payreckbackdto.setSpaymsgno(paymsgno);
				payreckbackdto.setSpaysndbnkno(paysndbnkno);
				bizDto = frs.loadFile(fileList, "", ls_filekind, payreckbackdto);

			} else {
				bizDto = frs.loadFile(fileList, biztype, ls_filekind, null); // 实拨、退库

			}
			if (bizDto != null) {
				if (bizDto.getErrorCount() == 0 && (bizDto.getErrorList() == null || bizDto.getErrorList().size() == 0)) {// "MulitTableDto".equals("校验加载成功")
					return WsUtils.generateResultXml(StateConstant.COMMON_YES, "文件上传成功！");

				} else {
					// 如果出错则删除错误文件
					WsUtils.delServerWrongFile(fileList);
					// FileU
					isErrorFileNeedDel = Boolean.TRUE;
					String errorInfo = WsUtils.packErrorInfo(bizDto.getErrorList());
					if (StringUtils.isNotBlank(errorInfo)) { // 只有有正常的错误信息时才拼接错误信息, 这样提示更友好一些.
						return WsUtils.generateResultXml(StateConstant.COMMON_NO, "文件上传失败, 错误信息如下：\r\n" + errorInfo);
					} else {
						return WsUtils.generateResultXml(StateConstant.COMMON_NO, "文件上传失败!");
					}
				}
			}

		} catch (Exception e) {
			log.error("广东发送文件调用异常(gd_webservice)！", e);
			isErrorFileNeedDel = Boolean.TRUE;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
					log.error("关闭输出流异常！", e);
				}
			}
			if (isErrorFileNeedDel) {
				try {
					WsUtils.delServerWrongFile(fileList);
				} catch (Exception e) {
					log.error("删除错误文件出现异常!(gd_webservice)!");
				}
			}
			log.debug("====================广东WebService业务接口调用结束(" + moment + ")!====================");
		}

		return WsUtils.generateResultXml(StateConstant.COMMON_NO, "系统出现未知异常, 请再次尝试或联系管理员！");
	}

	/**
	 * 业务流水明细数据导出
	 * 
	 * @param billType
	 *            业务类型
	 * @param params
	 *            原报表数据导出界面查询条件，以字符串传入，以逗号分隔；<br>
	 * @param separator 指定用于分割的符号(1-逗号，2-tab符号) 暂时只能用于参数导出
	 * @return
	 */
	public String readBizSeriData(String billType, String paramStr, String separator) {
		long moment = System.currentTimeMillis();
		log.debug("====广东WebService报表接口调用开始(" + moment + "):[billType: " + billType + ", paramStr: " + paramStr + ", separator: " + separator + "]====");
		/**
		 * 1、校验各种参数是否符合要求
		 */
		if (StringUtils.isBlank(billType)) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表类型不能为空!");
		}
		if (StringUtils.isBlank(paramStr) || StringUtils.isBlank(paramStr.replace(",", ""))) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数不能为空!");
		}

		String[] params = paramStr.split(",");
		if (null == params || params.length == 0) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "报表查询参数如有多个需以逗号(,)分割!");
		}
		String ls_Return = "";
		String useSplitStr = ","; 
		if(StringUtils.isNotBlank(separator)) {
			if(StringUtils.equals("1", separator.trim())) {
				useSplitStr = ",";
			} else if(StringUtils.equals("2", separator.trim())) {
				useSplitStr = "	";
			}
		}
		
		// 以参数个数判断导出类型
		if (params.length == 1) {
			// 参数导出
			ls_Return = bizDataExport.ParamDataExport(billType, paramStr,useSplitStr);
		} else if (params.length == 2) {
			// 以参数个数判断是否为业务数据导出
			ls_Return = bizDataExport.bizFileExport(billType, paramStr);
		} else if (params.length == 6) {
			// TIPS下发财政预算收入报表--库表格式
			ls_Return = bizDataExport.IncomeDayrptDataExportTable(billType, paramStr);
		} else if (params.length == 7) {
			// 财政报表-预算收入日报表(3128)
			ls_Return = bizDataExport.IncomeDayrptDataExport(billType, paramStr);
		} else {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "参数个数为" + params.length + "报表查询参数个数不正确!");
		}
		
		// 对要打印的日志进行处理，防止出现严重的性能问题。
		try {
			if(log.isDebugEnabled()) {
				if (ls_Return.length() > 1024) { // 结果长度大于1024不就打印具体报表内容
					if (StringUtils.contains(ls_Return, "<result>1</result>")) { //成功才打印
						String replaceXml = ls_Return.replaceAll("(<report>)(\\S*?)(</report>)", "$1报表内容太大,不展示!$3");
						log.debug("报表类型:" + billType + ";申请参数:" + paramStr + ";返回的数据：" + replaceXml);
					} else {
						log.debug("报表类型:" + billType + ";申请参数:" + paramStr + ";返回的数据：结果为失败(结果内容长度大于1024,且不存在<result>1</result>)!" );
					}
				} else {
					log.debug("报表类型:" + billType + ";申请参数:" + paramStr + ";返回的数据：" + ls_Return);
				}
			} else {
				log.debug("报表类型:" + billType + ";申请参数:" + paramStr + ";返回的数据：" + (ls_Return.contains("<result>1</result>")?"成功!":"申请异常或无数据!"));
			}
		} catch (Exception e) {
			log.error(e);
		}
		log.debug("=======================广东WebService报表接口调用结束(" + moment + ")!=======================");
		return ls_Return;
	}

	/**
	 * 数据加载时记接收日志
	 */
	private void saveTvrecvlog(String filePath, String biztype, String orgCode) throws ITFEBizException {
		// 文件上传根路径
		try {
			TvRecvlogDto dto = new TvRecvlogDto();
			// 取接收日志流水
			dto.setSrecvno(StampFacade.getStampSendSeq("JS"));
			// 发送机构代码
			dto.setSrecvorgcode(orgCode);
			// 上传日期
			dto.setSdate(DateUtil.date2String2(TSystemFacade.findDBSystemDate()));
			// 业务类型
			dto.setSoperationtypecode(biztype);
			// 文件在服务器的地址
			dto.setStitle(filePath);
			// 发送时间
			dto.setSrecvtime(TSystemFacade.getDBSystemTime());
			// 接收日期
			dto.setSsenddate(TimeFacade.getCurrentStringTime());
			// 处理码说明
			dto.setSretcodedesc("上传成功");
			dto.setSifsend(StateConstant.MSG_SENDER_FLAG_3);// 文件方式
			dto.setSturnsendflag(StateConstant.SendFinNo);// 转发标志
			dto.setSdemo("文件上传成功");
			DatabaseFacade.getODB().create(dto);
		} catch (Exception e) {
			log.error("保存接收日志失败！", e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * 创建目录
	 * 
	 * @param filePath
	 * @param file
	 */
	private void createDir(String filePath, File file) {
		if (file.exists()) {// "目录文件存在")
			file.delete();
		} else {
			// 如果目录不存在, 先创建
			File dir = new File(filePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
	}
}

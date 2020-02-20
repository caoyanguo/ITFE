package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.CommonParamDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * author hua 退库文件导入解析类
 */
public class DwbkTipsFileOperForSD extends AbstractTipsFileOper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> dmap) throws ITFEBizException {
		// 区分不同地方接口
		MulitTableDto multi = new MulitTableDto();
		multi.setBiztype(biztype);// 业务类型
		multi.setSbookorgcode(bookorgCode);// 核算主体代码

		String fname = new File(file).getName();
		String ctrimflag = fname.substring(fname.length() - 5,
				fname.length() - 4);
		File fi = new File(file);
		BigDecimal famt = new BigDecimal("0.00");

		String trecode = "";

		// 取文件中的信息转换为dwbkDtoList
		List<TbsTvDwbkDto> dwbkDtoList = new ArrayList();
		// 根据征收机关代码分包的Map
		Map<String, String> taxOrgCodeMap = new HashMap<String, String>();
		// 针对每一个征收机关代码对应的dwbkDtoList，然后再根据dwbkDtoList的个数按照1000笔进行分包
		Map<String, List> taxOrgCodeDtoListMap = new HashMap();
		// 组装后要返回的dwbkDtoList
		List<IDto> dwbkDtoListReturn = new ArrayList();
		// 组装后要返回的包信息packageFileList
		List<IDto> packageFileList = new ArrayList();
		// 凭证编号Set，用于验证凭证编号是否重复
		Set<String> voucodeSet = new HashSet<String>();
		// 凭证编号列表,用于返回变量，校验当日凭证编号是否重复使用
		List<String> voulist = new ArrayList<String>();
		// 取得国库代码与级次对应关系
		HashMap treMap = getTreLevelMap(bookorgCode);
		// 取得辅助标志对照表
		HashMap viceMap = getViceSignMap(bookorgCode);
		try {
			String key = "";
			String encyptMode = "";
			List<String[]> fileContent = super.readFile(file, ",");
			CommonParamDto _dto = (CommonParamDto) paramdto;
			encyptMode = _dto.getEncryptMode();
			int record = fileContent.size();
			if (StateConstant.SM3_ENCRYPT.equals(encyptMode) && record > 1) {
				record = record - 1;
			}
			TsTreasuryDto finddto = new TsTreasuryDto();//查找核算主体能操作的国库
			finddto.setSorgcode(bookorgCode);
			Map<String,TsTreasuryDto> treOrg = new HashMap<String,TsTreasuryDto>();
			List<TsTreasuryDto> treList = CommonFacade.getODB().findRsByDto(finddto);
			if(treList!=null&&treList.size()>0)
			{
				for(TsTreasuryDto fordto:treList)
					treOrg.put(fordto.getStrecode(), fordto);
			}
			for (int i = 0; i < record; i++) {
				String[] singDto = fileContent.get(i);
				// 在第0行获取一次密钥，根据不同情况获取密钥
				if (i == 0) {
					if ("SHANDONG".equals(_dto.getArea())) {
						key = this.findKeyForValidate("", ""); // 获得文件校验Key
					} else {// 按照传入的密钥设置模式取对应的密钥//修改为按照TIPS的征收机关设置密钥
						TsMankeyDto keydto = null;
						if(ITFECommonConstant.ISCONVERT.equals("0")){
								keydto = TipsFileDecrypt.findKeyByKeyMode(_dto
										.getKeyMode(), bookorgCode, singDto[2]);
						}else{
							HashMap<String, TsConverttaxorgDto> mapTaxInfo = SrvCacheFacade.cacheTaxInfo(bookorgCode);
							TsConverttaxorgDto tmpdto = mapTaxInfo.get(singDto[2]+ singDto[1]);
							if (null != tmpdto) {
								if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(_dto.getArea())) {
									// 此处修改为按照TCBS征收机关设置密钥
									keydto = TipsFileDecrypt.findKeyByKeyMode(_dto
											.getKeyMode(), bookorgCode, tmpdto
											.getStcbstaxorgcode());
								}else{
									// 此处修改为按照TCBS征收机关设置密钥
									keydto = TipsFileDecrypt.findKeyByKeyMode(_dto
											.getKeyMode(), bookorgCode, singDto[2]);
								}
								
							} else {
								multi.getErrorList().add(
										"征收机关对照表中没有维护" + singDto[1]
												+ "对应的TIPS征收机关！");
								return multi;
							}
						}
						
						
						

						if (null != keydto) {
							key = keydto.getSkey();
						} else {
							multi.getErrorList().add(
									"退库文件[" + fi.getName() + "没有查找到对应的解密密钥！");
							return multi;
						}
					}
				}
				// 不加密的方式需要校验密钥，山东和四川的方式作为不加密模式处理
				// 不加密的方式中，文件中有20个字段，加密方式中只有19个
				if (StateConstant.NO_ENCRYPT.equals(encyptMode)) {
					if (!this.importValidator(singDto[0], singDto[6],
							singDto[15], new BigDecimal(singDto[13]),
							singDto[19], key)) {
						multi.getErrorList().add(
								"退库文件[" + fi.getName() + "]中第 " + (i + 1)
										+ " 行存在校验码验证不符!");
					}
					if (singDto.length != StateConstant.DWBK_CONTENTNUM_SD
							.intValue()) {
						multi.getErrorList().add(
								"正在进行退库导入的文件[" + fi.getName() + "]中字段数目不符!");
					}
				}
				if (StateConstant.SD_ENCRYPT.equals(encyptMode)) {
					if (!SM3Process.verifySM3Sign(StringUtils
							.join(singDto, ","), key)) {
						multi.getErrorList().add(
								"实拨资金文件[" + fi.getName() + "]中第 " + (i + 1)
										+ " 行数字签名校验失败!");
					}
				}
				// 此处将文件中的信息项按照对应的关系放在dwbkDto中
				TbsTvDwbkDto dwbkDto = new TbsTvDwbkDto();
				dwbkDto.setSpayertrecode(singDto[2]);// 收款国库代码
				trecode = singDto[2];
				if (i == 0) {
					// 校验文件重复,国库代码+文件名
					String errorInfo = this.checkFileExsit(bookorgCode,
							trecode, fi.getName(), MsgConstant.MSG_NO_1104);
					if (null != errorInfo && errorInfo.length() > 0) {
						multi.getErrorList().add(errorInfo);
						return multi;
					}
					// 按照文件验证SM3算法的正确性
					if (StateConstant.SM3_ENCRYPT.equals(encyptMode)) {
						if (!SM3Process.verifySM3SignFile(file, key)) {
							multi.getErrorList().add(
									"实拨资金文件[" + fi.getName() + "]SM3签名验证失败!");
							return multi;
						}
					}
				}
				if (!treOrg.containsKey(trecode)) {
					multi.getErrorList().add(
							"退库文件[" + fi.getName() + "]中国库主体代码：" + trecode
									+ " 没有在'国库主体信息参数'中查找到!");
				}
				if (singDto[3] != null && !singDto[3].trim().equals("")) {
					dwbkDto.setSaimtrecode(singDto[3]); // 目的国库代码
				} else {
					dwbkDto.setSaimtrecode(singDto[2]); // 目的国库代码
				}
				String strTipsTaxOrg = "";
				if(ITFECommonConstant.ISCONVERT.equals("0")){
					strTipsTaxOrg = singDto[1];
					dwbkDto.setStaxorgcode(strTipsTaxOrg);
					taxOrgCodeMap.put(strTipsTaxOrg, "");
				}else{
					TsConverttaxorgDto convertTaxOrgDto = new TsConverttaxorgDto();
					// 核算主体代码
					convertTaxOrgDto.setSorgcode(bookorgCode);
					// 国库主体代码
					convertTaxOrgDto.setStrecode(trecode);
					// TBS征收机关代码
					convertTaxOrgDto.setStbstaxorgcode(singDto[1]);
					List dtoL = CommonFacade.getODB().findRsByDtoWithUR(
							convertTaxOrgDto);
					
					if (dtoL == null || dtoL.size() == 0) {
						multi.getErrorList().add(
								"退库文件[" + fi.getName() + "]中国库主体代码：" + trecode
										+ ",征收机关代码：" + singDto[1]
										+ " 没有维护'征收机关对照'!");
					} else {
						TsConverttaxorgDto taxogdto = (TsConverttaxorgDto) dtoL
								.get(0);
						// Tips征收机关代码
						strTipsTaxOrg = taxogdto.getStcbstaxorgcode();
						// 征收机关代码
						dwbkDto.setStaxorgcode(strTipsTaxOrg);
						taxOrgCodeMap.put(strTipsTaxOrg, "");
					}
				}
				

				//旧接口校验凭证编号不超过8位并且为数字
				if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_FALSE)) {
					//不采用新接口，就需要进行字符截取
					if(singDto[0].length() > 8) {
						dwbkDto.setSdwbkvoucode(singDto[0].substring(singDto[0].length()-8));//退库凭证号
					}else {
						if(singDto[0].length() < 8){
							multi.getErrorList().add("退库文件[" + fi.getName()+ "]中凭证编号"+singDto[0]+"必须为8位!");
						}
						dwbkDto.setSdwbkvoucode(singDto[0]);//退库凭证号
					}
					if(!VerifyParamTrans.isNumber(singDto[0])){
						multi.getErrorList().add("退库文件[" + fi.getName()+ "]中存在凭证编号"+singDto[0]+"中包含数字之外的字符!");
					}
				}else if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_TRUE)){
					dwbkDto.setSdwbkvoucode(singDto[0]);//退库凭证号
				}
				voulist.add(strTipsTaxOrg.trim() + "," + singDto[0].trim());
				int voucodeSetOldSize = voucodeSet.size();
				voucodeSet.add(strTipsTaxOrg.trim() + singDto[0].trim());
				int voucodeSetNewSize = voucodeSet.size();
				if (voucodeSetOldSize == voucodeSetNewSize)
					multi.getErrorList().add(
							"退库文件[" + fi.getName() + "]中存在凭证编号" + singDto[0]
									+ "重复!");

				dwbkDto.setCbdglevel(singDto[4]); // 预算级次
				// 国库级次与预算级次校验
				if (!"0".equals(ITFECommonConstant.IFVERIFYTRELEVEL)) {
					String errorinfo = VerifyParamTrans.verifyTreasuryLevelDW(
							trecode, (String) treMap.get(trecode), singDto[4]);
					if (!"".equals(errorinfo)) {
						multi.getErrorList().add(
								"退库文件[" + fi.getName() + "]凭证编号为[" + singDto[0]
										+ "]中的预算级次：" + singDto[4]
										+ " 对应的国库级次校验错误!" + "[" + errorinfo
										+ "]");
					}
				}
				dwbkDto.setCbdgkind(singDto[5]); // 预算种类

				TsBudgetsubjectDto bsdto = new TsBudgetsubjectDto();
				bsdto.setSorgcode(bookorgCode);
				bsdto.setSsubjectcode(singDto[6].trim());
				List<IDto> list = CommonFacade.getODB()
						.findRsByDtoWithUR(bsdto);
				if (list == null || list.size() == 0) {
					multi.getErrorList().add(
							"退库文件[" + fi.getName() + "]中的科目代码：" + singDto[6]
									+ " 没有找到对应信息!");
				}
				dwbkDto.setSbdgsbtcode(singDto[6]); // 科目代码
				if (StateConstant.shareBudgetLevel.equals(singDto[4]) && null==singDto[7]) {
					multi.getErrorList().add("预算级次为共享，辅助标志不能为空！");
				}
				// 判断辅助标志对应关系的存在性
				if(!ITFECommonConstant.ISCONVERT.equals("0")){
					if (null != singDto[7] && !"".equals(singDto[7])) { // 辅助标志
						if (!viceMap.containsKey(singDto[2] + singDto[6]
								+ singDto[7])) {
							if (!viceMap.containsKey(singDto[2] + singDto[7])) {
								if (!viceMap.containsKey(singDto[6] + singDto[7])) {
									if (!viceMap.containsKey(singDto[7])) {
										multi.getErrorList().add(
												"退库文件[" + fi.getName() + "]中辅助标志："
														+ singDto[7]
														+ " 没有在'辅助标志对照维护'参数中维护!");
									}
								}
							}
						}
					}
				}
				dwbkDto.setSastflag(singDto[7]);
				dwbkDto.setSdwbkreasoncode(singDto[8]); // 退库原因代码
				dwbkDto.setSdwbkby(singDto[9]); // 退库依据
				dwbkDto.setSexamorg(singDto[10]); // 审批机关
				dwbkDto.setDaccept(TimeFacade.getCurrentDateTime());// D_ACCEPT受理日期不能为空;

				if (singDto[11] != null && !singDto[11].trim().equals("")) {
					dwbkDto.setFdwbkratio(new BigDecimal(singDto[11])); // 退库比例
				} else {
					dwbkDto.setFdwbkratio(new BigDecimal("0.00"));
				}

				if (singDto[12] != null && !singDto[12].trim().equals("")) {
					dwbkDto.setFdwbkamt(new BigDecimal(singDto[12])); // 退库总额
				} else {
					dwbkDto.setFdwbkamt(new BigDecimal("0.00"));
				}

				if (singDto[13] != null && !singDto[13].trim().equals("")) {
					dwbkDto.setFamt(new BigDecimal(singDto[13])); // 发生额
					famt = famt.add(new BigDecimal(singDto[13]));
				} else {
					dwbkDto.setFamt(new BigDecimal("0.00"));
				}

				dwbkDto.setCbckflag(singDto[14]); // 退回标志
				dwbkDto.setSpayeeacct(singDto[15]); // 收款帐号
				dwbkDto.setSpayeecode(singDto[16]); // 收款单位代码
				String errChi_17 = VerifyParamTrans.verifyNotUsableChinese(singDto[17]);
				if(null != errChi_17 && !"".equals(errChi_17)) {
					multi.getErrorList().add("退库文件[" + fi.getName()+ "]凭证编号为[" + singDto[0]+ "]中收款人名称存在非法字符："+errChi_17);
				}
				if (singDto[17] != null && singDto[17].length() > 60) {
					dwbkDto.setSpayeename(singDto[17].substring(0, 60)); // 收款人账号名称
				} else {
					dwbkDto.setSpayeename(singDto[17]); // 收款人账号名称
				}
				if (singDto[18].trim().length() == 12) {// 行号如果为12位按照支付系统行号处理
					dwbkDto.setSpayeeopnbnkno(singDto[18].trim());
				} else {
					TsGenbankandreckbankDto bankdto = new TsGenbankandreckbankDto();
					bankdto.setSbookorgcode(bookorgCode);
					bankdto.setSgenbankcode(singDto[18].trim());
					List dot = CommonFacade.getODB().findRsByDtoWithUR(bankdto);
					if (dot == null || dot.size() == 0) {
						multi.getErrorList().add(
								"退库文件[" + fi.getName() + "]根据开户行大类：'"
										+ singDto[18].trim()
										+ "' 没有在'银行代码与支付行号对应关系参数' 中找到相应"
										+ "对照关系，请先维护!");
					} else {
						TsGenbankandreckbankDto bdto = (TsGenbankandreckbankDto) dot
								.get(0);
						String spayeebnkno = bdto.getSreckbankcode();
						dwbkDto.setSpayeeopnbnkno(spayeebnkno);// 开户行大类(经过转换成收款人开户行行号)
					}
				}
				/**
				 * 广东增加附言字段，判断如果字段数目为20，则将最后一位填入附言字段，否则忽略，加了发现没有用，TIPS填不上
				 */
				if (singDto.length == 20) {
					String errChi_19 = VerifyParamTrans.verifyNotUsableChinese(singDto[19]);
					if(null != errChi_19 && !"".equals(errChi_19)) {
						multi.getErrorList().add("退库文件[" + fi.getName()+ "]凭证编号为[" + singDto[0]+ "]中附言存在非法字符："+errChi_19);
					}
					if (null != singDto[19] && !"".equals(singDto[19])) {
						dwbkDto.setSaddword(singDto[19]);
					}
				}

				dwbkDto.setSbookorgcode(bookorgCode);// 核算主体代码
				dwbkDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
				dwbkDto.setCtrimflag(ctrimflag); // 调整期标志不为空
				dwbkDto.setSbiztype(biztype);// 业务类型
				dwbkDto.setDvoucher(CommonUtil.strToDate(fi.getName()
						.substring(0, 8))); // 开票日期不为空
				dwbkDto.setSelecvouno(singDto[0]); // 电子凭证编号不能为空

				dwbkDto.setSfilename(fi.getName()); // 导入文件名
				dwbkDto.setSpackageno(""); // 包流水号
				dwbkDtoList.add(dwbkDto);
				taxOrgCodeMap.put(dwbkDto.getStaxorgcode(), "");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("退库导入文件解析出错 \n" + e.getMessage(), e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("数据库查询异常 \n" + e.getMessage(), e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("数据库查询异常 \n" + e.getMessage(), e);
		} catch (IOException e) {
			log.error(e);
			throw new ITFEBizException("文件验证签名异常 \n" + e.getMessage(), e);
		}
		for (String taxOrgCode : taxOrgCodeMap.keySet()) {
			List<TbsTvDwbkDto> taxOrgCodeDwbkDtoList = new ArrayList();
			if (null != taxOrgCode && !"".equals(taxOrgCode)) {
				for (int k = 0; k < dwbkDtoList.size(); k++) {
					if (taxOrgCode.equals(dwbkDtoList.get(k).getStaxorgcode())) {
						taxOrgCodeDwbkDtoList.add(dwbkDtoList.get(k));
					}
				}
				taxOrgCodeDtoListMap.put(taxOrgCode, taxOrgCodeDwbkDtoList);
			}
		}

		for (String taxOrgCode : taxOrgCodeDtoListMap.keySet()) {
			List<TbsTvDwbkDto> taxOrgCodeDwbkDtoList = taxOrgCodeDtoListMap
					.get(taxOrgCode);
			int li_Detail = (taxOrgCodeDwbkDtoList.size() - 1) / 1000;
			for (int k = 0; k <= li_Detail; k++) {
				// 每个包得总金额
				BigDecimal famtAll = new BigDecimal("0.00");
				// 总笔数
				int li_Count = 0;
				int li_TempCount = 0;
				if (li_Detail == k) {
					li_TempCount = taxOrgCodeDwbkDtoList.size();
				} else {
					li_TempCount = (k + 1) * 1000;
				}
				String tmpPackNo = "";
				try {
					tmpPackNo = SequenceGenerator
							.changePackNoForLocal(SequenceGenerator
									.getNextByDb2(
											SequenceName.FILENAME_PACKNO_REF_SEQ,
											SequenceName.TRAID_SEQ_CACHE,
											SequenceName.TRAID_SEQ_STARTWITH,
											MsgConstant.SEQUENCE_MAX_DEF_VALUE));
				} catch (SequenceException e) {
					log.error(e);
					throw new ITFEBizException("获取包流水号异常 \n" + e.getMessage(),
							e);
				}
				String ls_TreCode = "";
				for (int j = k * 1000; j < li_TempCount; j++) {
					li_Count++;
					TbsTvDwbkDto tvDwbkDto = taxOrgCodeDwbkDtoList.get(j);
					famtAll = famtAll.add(tvDwbkDto.getFamt());
					tvDwbkDto.setSpackageno(tmpPackNo);
					dwbkDtoListReturn.add(tvDwbkDto);
					if (ls_TreCode.equals(""))
						ls_TreCode = tvDwbkDto.getSaimtrecode();
				}
				TvFilepackagerefDto packdto = new TvFilepackagerefDto();
				packdto.setSorgcode(bookorgCode);
				// 国库主体代码
				packdto.setStrecode(ls_TreCode);
				packdto.setSfilename(fi.getName());
				// 征收机关代码
				packdto.setStaxorgcode(taxOrgCode);
				packdto.setScommitdate(TimeFacade.getCurrentStringTime());
				packdto.setSaccdate(TimeFacade.getCurrentStringTime());
				packdto.setSpackageno(tmpPackNo);
				packdto.setSoperationtypecode(biztype);
				packdto.setIcount(li_Count);
				packdto.setNmoney(famtAll);

				packdto
						.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
				packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
				packdto.setSusercode(userid);
				packdto.setImodicount(0);
				packdto.setSpackageno(tmpPackNo);
				packageFileList.add(packdto);
			}
		}
		multi.setFatherDtos(dwbkDtoListReturn);
		multi.setPackDtos(packageFileList);
		multi.setVoulist(voulist);
		return multi;
	}

	/**
	 * 取得国库代码与级次的对应关系
	 * 
	 * @throws ITFEBizException
	 */
	private HashMap<String, String> getTreLevelMap(String sbookorgcode)
			throws ITFEBizException {
		HashMap<String, String> tremap = new HashMap<String, String>();
		TsTreasuryDto trepk = new TsTreasuryDto();
		trepk.setSorgcode(sbookorgcode);
		try {
			List list = CommonFacade.getODB().findRsByDto(trepk);
			for (int i = 0; i < list.size(); i++) {
				TsTreasuryDto dto = (TsTreasuryDto) list.get(i);
				tremap.put(dto.getStrecode(), dto.getStrelevel());
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("取得国库代码与级次的对应关系异常 \n" + e.getMessage(),
					e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("取得国库代码与级次的对应关系异常 \n" + e.getMessage(),
					e);
		}
		return tremap;
	}

	/**
	 * 辅助标志对照关系表
	 * 
	 * @throws ITFEBizException
	 */
	public HashMap<String, String> getViceSignMap(String sbookorgcode)
			throws ITFEBizException {
		HashMap<String, String> vicemap = new HashMap<String, String>();
		TsConvertassitsignDto viceDto = new TsConvertassitsignDto();
		viceDto.setSorgcode(sbookorgcode);
		try {
			List list = CommonFacade.getODB().findRsByDto(viceDto);
			for (int i = 0; i < list.size(); i++) {
				TsConvertassitsignDto dto = (TsConvertassitsignDto) list.get(i);
				vicemap.put(dto.getStrecode() + dto.getSbudgetsubcode()
						+ dto.getStbsassitsign(), dto.getStipsassistsign());
				vicemap.put(dto.getSbudgetsubcode() + dto.getStbsassitsign(),
						dto.getStipsassistsign());
				vicemap.put(dto.getStrecode() + dto.getStbsassitsign(), dto
						.getStipsassistsign());
				vicemap.put(dto.getStbsassitsign(), dto.getStipsassistsign());
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("取得辅助标志对照关系表异常 \n" + e.getMessage(), e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("取得辅助标志对照关系表异常 \n" + e.getMessage(), e);
		}
		return vicemap;
	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
}

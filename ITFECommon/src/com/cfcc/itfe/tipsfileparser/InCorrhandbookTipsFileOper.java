package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * author hua 更正
 */
public class InCorrhandbookTipsFileOper extends AbstractTipsFileOper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> dmap) throws ITFEBizException {
		try {
			/**进行解析初始化 */
			File fi = new File(file);
			List<String[]> fileContent = super.readFile(file, ","); // 按规则读取文件
			
			int fbcount = 0; // 分包记数器
			int recordNum = 1; // 记录记录器
			int checkFile = 0; // 校验文件名重复

			String fname = new File(file).getName(); // 文件名
			String ctrimflag = ""; // 调整期标志
			//珠海的更正文件为14位，调整期标志为（.txt）前倒数第二位
//			String ctrimflag = fname.substring(fname.length() - 5, fname.length() - 4); // 调整期标志
			if(fname.toLowerCase().replaceAll(".txt", "").length() == 14 ){
				ctrimflag = fname.substring(12, 13); // 调整期标志
			}else{
				ctrimflag = fname.substring(fname.length() - 5, fname.length() - 4); // 调整期标志
			}

			MulitTableDto multi = new MulitTableDto();
			multi.setBiztype(biztype);// 业务类型
			multi.setSbookorgcode(bookorgCode);// 核算主体代码
			
			String trecode = ""; // 国库代码
			String tmpPackNo = ""; // 包流水号
			
			HashMap<String, TsConverttaxorgDto> mapTaxInfo = SrvCacheFacade.cacheTaxInfo(bookorgCode); // 缓存征收机关对照集合
			TsTreasuryDto finddto = new TsTreasuryDto();//查找核算主体能操作的国库
			finddto.setSorgcode(bookorgCode);
			Map<String,TsTreasuryDto> treMap = new HashMap<String,TsTreasuryDto>();
			List<TsTreasuryDto> treList = CommonFacade.getODB().findRsByDto(finddto);
			if(treList!=null&&treList.size()>0)
			{
				for(TsTreasuryDto fordto:treList)
					treMap.put(fordto.getStrecode(), fordto);
			}
			BigDecimal famt = new BigDecimal("0.00"); // 整个文件的总金额
			BigDecimal famtPack = new BigDecimal("0.00"); // 分包设置的金额计数

			List<IDto> listdto = new ArrayList<IDto>(); // 更正记录容器
			List<IDto> packdtos = new ArrayList<IDto>(); // 文件与包对应关系容器
			HashSet<String> taxorgset = new HashSet<String>(); // 存放征收机关，用于分包
			List<String> voulist = new ArrayList<String>(); // 凭证编号集和
			HashMap<String, List<String[]>> splitMap = new HashMap<String, List<String[]>>();// 征收机关-对应记录
			
			/**更正增加按照原征收机关分包   by hua 2013-04-02*/
			/**
			 * 1、首先找出所有的征收机关
			 */
			for(String[] tmpS : fileContent) {
				if(null != tmpS[3] && !"".equals(tmpS[3])) {
					taxorgset.add(tmpS[3].trim());
				}else { //如果原列征收机关不存在直接返回
					multi.getErrorList().add("更正文件["+fi.getName()+"]中存在存在'原征收机关代码'为空!");
					return multi;
				}
			}
			
			/**
			 * 2、将所有的征收机关和对应的记录逻辑上分开
			 */
			for(String taxcode : taxorgset) {
				List<String[]> tmpList = new ArrayList<String[]>();
				for(String[] tmpS2 : fileContent) {
					if(tmpS2[3].trim().equals(taxcode)) {
						tmpList.add(tmpS2);
					}
				}
				splitMap.put(taxcode, tmpList);
			}
			
			/**
			 * 3、正式分包，填入包流水号并产生包与文件对应关系
			 */
			for(String taxcode1 : taxorgset) {
				List<String[]> tmpInCorrList = splitMap.get(taxcode1);
				int li_Detail = (tmpInCorrList.size()-1) / 1000;
				// 取得辅助标志对照表
				HashMap viceMap = getViceSignMap(bookorgCode);
				for (int k = 0; k <= li_Detail; k++) {
					int li_TempCount = 0;
					if (li_Detail == k) {
						li_TempCount = tmpInCorrList.size();
					} else {
						li_TempCount = (k + 1) * 1000;
					}
					//产生包流水号
					tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
							.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
									SequenceName.TRAID_SEQ_CACHE,
									SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
					for (int j = k * 1000; j < li_TempCount; j++) {
						String[] singDto = tmpInCorrList.get(j);
						TbsTvInCorrhandbookDto incorrDto = new TbsTvInCorrhandbookDto();
						incorrDto.setCcurbdgkind(singDto[0]); // 预算种类（1-预算内，2－预算外）					
						if(singDto[1] == null || "".equals(singDto[1].trim())) {
							multi.getErrorList().add("更正文件["+fi.getName()+"]中存在'原收款国库代码'为空!");
						}
						trecode = singDto[1];
						if(!treMap.containsKey(trecode)) {
							multi.getErrorList().add("更正文件["+fi.getName()+"]中原收款国库主体代码："+trecode+" 没有在'国库主体信息参数'中查找到!");
						}
						incorrDto.setSoripayeetrecode(singDto[1]); // 原收款国库代码
						
						checkFile++;
						if(checkFile == 1) {
							//校验文件重复,国库代码+文件名
							String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_1105);
							if(null != errorInfo && errorInfo.length() > 0) {
								multi.getErrorList().add(errorInfo);
								return multi;
							}
						}
						
						
						if(singDto[2] == null || "".equals(singDto[2])) {
							multi.getErrorList().add("更正文件["+fi.getName()+"]中存在'原目的国库代码'为空!");
						}else {
							incorrDto.setSoriaimtrecode(singDto[2]);// 原目的国库代码
						}
						if(!ITFECommonConstant.ISCONVERT.equals("0")){
							if (!mapTaxInfo.containsKey(singDto[2]+singDto[3])) {
								multi.getErrorList().add("更正文件["+fi.getName()+"]中原征收机关代码对照表中没有维护" + singDto[3]
													+ "对应的TIPS征收机关！");
							}
						}
						incorrDto.setSoritaxorgcode(singDto[3]); // 原征收机关代码
						incorrDto.setCoribdglevel(singDto[4]); // 原预算级次
						incorrDto.setSoriastflag(singDto[5]); // 原辅助标志
						
						if (StateConstant.shareBudgetLevel.equals(singDto[4]) && null==singDto[5]) {
							multi.getErrorList().add("原预算级次为共享，原辅助标志不能为空！");
						}
						// 判断辅助标志对应关系的存在性
						if(!ITFECommonConstant.ISCONVERT.equals("0")){
							if (null != singDto[5] && !"".equals(singDto[5])) { // 辅助标志
								if (!viceMap.containsKey(singDto[2] + singDto[6]
										+ singDto[5])) {
									if (!viceMap.containsKey(singDto[2] + singDto[5])) {
										if (!viceMap.containsKey(singDto[6] + singDto[5])) {
											if (!viceMap.containsKey(singDto[5])) {
												multi.getErrorList().add(
														"更正文件[" + fi.getName() + "]中辅助标志："
																+ singDto[7]
																+ " 没有在'辅助标志对照维护'参数中维护!");
											}
										}
									}
								}
							}
						}
						
						if(singDto[6] == null || "".equals(singDto[6].trim())) {
							multi.getErrorList().add("更正文件["+fi.getName()+"]中存在'原科目代码'为空!");
						}
						String err = this.verifySubject(dmap, singDto[6].trim(), MsgConstant.MSG_NO_1105, "1", fi.getName(),singDto[16]);
						if(!"".equals(err)) {
							multi.getErrorList().add(err);
						}
						incorrDto.setSoribdgsbtcode(singDto[6]); // 原科目代码
						incorrDto.setForicorramt(new BigDecimal(singDto[7])); // 原发生额
						incorrDto.setSreasoncode(singDto[8]); // 更正原因代码
						if(singDto[9] == null || "".equals(singDto[9].trim())) {
							multi.getErrorList().add("更正文件["+fi.getName()+"]中存在'现收款国库代码'为空!");
						}

						if(!this.checkTreasury(singDto[9], bookorgCode)) {
							multi.getErrorList().add("更正文件["+fi.getName()+"]中现收款国库主体代码："+singDto[9]+" 没有在'国库主体信息参数'中查找到!");
						}
						incorrDto.setScurpayeetrecode(singDto[9]); // 现收款国库代码
						
						if(singDto[10] == null || "".equals(singDto[10])) {
							multi.getErrorList().add("更正文件["+fi.getName()+"]中存在'现目的国库代码'为空!");
						}else {
							incorrDto.setScuraimtrecode(singDto[10]); // 现目的国库代码
						}
						
						if(singDto[11] == null || "".equals(singDto[11].trim())) {
							multi.getErrorList().add("更正文件["+fi.getName()+"]中存在'现征收机关代码'为空!");
						}
						if(!ITFECommonConstant.ISCONVERT.equals("0")){
							if (!mapTaxInfo.containsKey(singDto[10]+singDto[11])) {
								multi.getErrorList().add("更正文件["+fi.getName()+"]中现征收机关代码对照表中没有维护" + singDto[11]
													+ "对应的TIPS征收机关！");
							}
						}
						incorrDto.setScurtaxorgcode(singDto[11]); // 现征收机关代码
						incorrDto.setCcurbdglevel(singDto[12]); // 现预算级次（0－共享，1--中央，2--省，3--市，4--区县，5--乡镇）
						incorrDto.setScurastflag(singDto[13]); // 现辅助标志
						if (StateConstant.shareBudgetLevel.equals(singDto[12]) && null==singDto[13]) {
							multi.getErrorList().add("现预算级次为共享，现辅助标志不能为空");
						}
						// 判断辅助标志对应关系的存在性
						if(!ITFECommonConstant.ISCONVERT.equals("0")){
							if (null != singDto[13] && !"".equals(singDto[13])) { // 辅助标志
								if (!viceMap.containsKey(singDto[10] + singDto[14]
										+ singDto[13])) {
									if (!viceMap.containsKey(singDto[10] + singDto[13])) {
										if (!viceMap.containsKey(singDto[14] + singDto[13])) {
											if (!viceMap.containsKey(singDto[13])) {
												multi.getErrorList().add(
														"更正文件[" + fi.getName() + "]中辅助标志："
																+ singDto[7]
																+ " 没有在'辅助标志对照维护'参数中维护!");
											}
										}
									}
								}
							}
						}
						if(singDto[14] == null || "".equals(singDto[14].trim())) {
							multi.getErrorList().add("更正文件["+fi.getName()+"]中存在'现科目代码'为空!");
						}
						err = this.verifySubject(dmap, singDto[14].trim(), MsgConstant.MSG_NO_1105, "1", fi.getName(),singDto[16]);
						if(!"".equals(err)) {
							multi.getErrorList().add(err);
						}
						incorrDto.setScurbdgsbtcode(singDto[14]); // 现科目代码
						incorrDto.setFcurcorramt(new BigDecimal(singDto[15])); // 现发生额(#.00)
						
						if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_FALSE)) {
							//不采用新接口，就需要进行字符截取，必须为8位的数字
							if(!VerifyParamTrans.isNumber(singDto[16])){
								multi.getErrorList().add("更正文件[" + fi.getName()+ "]中凭证编号"+singDto[16]+"中包含数字之外的字符!");
							}
							if(singDto[16].length() > 8) {
								incorrDto.setScorrvouno(singDto[16].substring(singDto[16].length()-8));//凭证编号
							}else {
								if(singDto[16].length() < 8){
									multi.getErrorList().add("更正文件[" + fi.getName()+ "]中凭证编号"+singDto[16]+"必须为8位!");
								}
								incorrDto.setScorrvouno(singDto[16]); // 凭证编号
							}
						}else if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_TRUE)){
							incorrDto.setScorrvouno(singDto[16]); // 凭证编号
						}					
						//TODO 是否需要按照国库或者征收机关来校验凭证编号，待考虑！！！
						voulist.add(incorrDto.getScorrvouno());
						
						incorrDto.setSelecvouno(singDto[16]); //电子凭证编号不为空
						incorrDto.setCtrimflag(ctrimflag); //现调整期不为空
						incorrDto.setCoribdgkind(StateConstant.BudgetType_IN); //元预算种类不为空
						incorrDto.setDaccept(TimeFacade.getCurrentDateTime()); //受理日期
						incorrDto.setDvoucher(CommonUtil.strToDate(fi.getName().substring(0, 8)));
						famt = famt.add(new BigDecimal(singDto[7]));
						famtPack = famtPack.add(new BigDecimal(singDto[7]));
						incorrDto.setSbookorgcode(bookorgCode);// 核算主体代码
						incorrDto.setSpackageno(tmpPackNo);
						incorrDto.setSfilename(fi.getName());
						incorrDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
						fbcount ++;
						listdto.add(incorrDto);
					}
					TvFilepackagerefDto packdto = new TvFilepackagerefDto();
					packdto.setSorgcode(bookorgCode);
					packdto.setStrecode(trecode);
					packdto.setSfilename(fi.getName());
					packdto.setStaxorgcode(taxcode1);
					packdto.setScommitdate(TimeFacade.getCurrentStringTime());
					packdto.setSaccdate(TimeFacade.getCurrentStringTime());
					packdto.setSpackageno(tmpPackNo);
					packdto.setSoperationtypecode(biztype);
					packdto.setIcount(fbcount);
					packdto.setNmoney(famtPack);
					packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
					packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
					packdto.setSusercode(userid);
					packdto.setImodicount(0);
					fbcount = 0;
					famtPack = new BigDecimal("0.00");
					packdtos.add(packdto);
				}
			}
			
			//校验文件中凭证编号重复性
			int oldSize = 0;
			int newSize = 0;
			Set<String> sets = new HashSet<String>();
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					multi.getErrorList().add("更正文件["+fi.getName()+"]中存在凭证编号重复");
				}
			}
			
			multi.setFatherDtos(listdto);
			multi.setPackDtos(packdtos);
			multi.setVoulist(voulist);
			multi.setFamt(famt);  //总金额
			multi.setTotalCount(recordNum); //总笔数
			return multi; 

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			throw new ITFEBizException("更正导入文件解析出错 \n"+e.getMessage(), e);
		}
	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 辅助标志对照关系表
	 * 
	 * @throws ITFEBizException
	 */
	private HashMap<String, String> getViceSignMap(String sbookorgcode)
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

}

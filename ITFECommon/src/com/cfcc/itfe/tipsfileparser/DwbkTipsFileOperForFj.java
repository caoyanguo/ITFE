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
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * author hua 退库文件导入解析类
 */
public class DwbkTipsFileOperForFj extends AbstractTipsFileOper{
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {
		// 区分不同地方接口
		MulitTableDto multi = new MulitTableDto();
		multi.setBiztype(biztype);// 业务类型
		multi.setSbookorgcode(bookorgCode);// 核算主体代码
		int fbcount = 0; // 分包记数器
		int recordNum = 0; // 记录记录器
		int recordhead = 0; 
		String fname = new File(file).getName();
			
		List<IDto> listdto = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		
		File fi = new File(file);
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal famtPack = new BigDecimal("0.00");
		String trecode = "";
		String tmpPackNo = "";
		BigDecimal famthead = new BigDecimal("0.00");
		List<String> voulist = new ArrayList<String>();
		try {
			HashMap<String,TsPaybankDto> paymap = this.makeBankMap();
			HashMap<String, TsConverttaxorgDto> taxmap =this.makeTaxMap(bookorgCode);
			HashMap<String, TsConvertfinorgDto> fincmap =this.makeFincMap(bookorgCode);
			List<String[]> fileContent = super.readFile(file, ",");
			String[] firstStr = fileContent.get(0);
			//属于文件头
			recordhead = Integer.parseInt(firstStr[2]);
			famthead = new BigDecimal(firstStr[3]);
			fileContent.remove(0); //去掉文件头
			fileContent.remove(fileContent.size() - 1); //去掉校验码
			int checkFile = 0;
			int li_Detail = (fileContent.size()-1) / 1000;
			for (int k = 0; k <= li_Detail; k++) {
				int li_TempCount = 0;
				if (li_Detail == k) {
					li_TempCount = fileContent.size();
				} else {
					li_TempCount = (k + 1) * 1000;
				}
				tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
						.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
				for (int j = k * 1000; j < li_TempCount; j++) {
					String[] singDto = fileContent.get(j);
					TbsTvDwbkDto dwbkDto = new TbsTvDwbkDto();

					dwbkDto.setSelecvouno(singDto[0]); //序号
					if(singDto[1] == null || "".equals(singDto[1].trim())) {
						multi.getErrorList().add("退库文件["+fi.getName()+"]中存在'退库国库代码'为空!");
					}
					if(!this.checkTreasury(singDto[1]+"00", bookorgCode)) {
						multi.getErrorList().add("退库文件["+fi.getName()+"]中国库主体代码："+singDto[1]+"00"+" 没有在'国库主体信息参数'中查找到!");
					}
					dwbkDto.setSpayertrecode(singDto[1]+"00"); //退库国库代码
					trecode = singDto[1]+"00";
					checkFile++;
					if(checkFile == 1) {
						//校验文件重复,国库代码+文件名
						String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_5101);
						if(null != errorInfo && errorInfo.length() > 0) {
							multi.getErrorList().add(errorInfo);
							return multi;
						}
					}
					
					if(ITFECommonConstant.ISCONVERT.equals("0")){
						dwbkDto.setStaxorgcode(singDto[2]); // 征收机关
					}else{
						// TBS征收机关代码
						String taxkey= trecode+singDto[2];
						String strTipsTaxOrg ="";
						if (!taxmap.containsKey(taxkey)) {
							multi.getErrorList().add("退库文件[" + fi.getName()
									+ "]中国库主体代码："+trecode+",征收机关代码：" + singDto[2]
									+ " 没有维护'征收机关对照'!");
						}else {
							TsConverttaxorgDto taxogdto = taxmap.get(taxkey);
							// Tips征收机关代码
							strTipsTaxOrg = taxogdto.getStcbstaxorgcode();
						}
						//退库机关
						dwbkDto.setStaxorgcode(strTipsTaxOrg);
					}
										
					dwbkDto.setSagenttaxorgcode(singDto[3]);//填发机关
					dwbkDto.setSpayeecode(singDto[4]);//收款单位
					dwbkDto.setSecocode(singDto[5]);//退库人经济类型代码
					dwbkDto.setSetpcode(singDto[6]);//退库人行业类型代码
					dwbkDto.setDbill(CommonUtil.strToDate(singDto[7])); //填发日期
					if(singDto[8] == null || "".equals(singDto[8].trim())) {
						multi.getErrorList().add(fi.getName() +" 退库文件["+fi.getName()+"]中存在'退库凭证号'为空!");
					}
					if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_FALSE)) {
						//不采用新接口，就需要进行字符截取
						if(singDto[8].length() > 8) {
							dwbkDto.setSdwbkvoucode(singDto[8].substring(singDto[8].length()-8));//退库凭证号
						}else {
							dwbkDto.setSdwbkvoucode(singDto[8]);//退库凭证号
						}
						if(!VerifyParamTrans.isNumber(singDto[8])){
							multi.getErrorList().add("退库文件[" + fi.getName()
									+ "]中存在凭证编号"+singDto[8]+"中包含数字之外的字符!");
						}
					}else if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_TRUE)){
						dwbkDto.setSdwbkvoucode(singDto[8]);//退库凭证号
					}
					
					voulist.add(singDto[2].trim()+","+dwbkDto.getSdwbkvoucode().trim());
					if(singDto[9] == null || "".equals(singDto[9].trim())) {
						multi.getErrorList().add(fi.getName() +" 退库文件["+fi.getName()+"]中存在'收款单位开户行'为空!");
					}
					TsPaybankDto bnkpaydto = paymap.get(singDto[9].trim());
					if(null == bnkpaydto) {
						multi.getErrorList().add("退库文件["+fi.getName()+"]根据收款单位开户行：'"+singDto[9].trim()+"'没有在'支付行号查询参数' 中找到相应清算行行号!");
					}else {
						if(null != bnkpaydto.getSstate() && "0".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("退库文件["+fi.getName()+"]中收款单位开户行：'"+singDto[9].trim()+"' 处于'生效前'状态!");
						}else if (null != bnkpaydto.getSstate() && "2".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("退库文件["+fi.getName()+"]中收款单位开户行：'"+singDto[9].trim()+"' 处于'注销'状态!");
						}	
					}							
					dwbkDto.setSpayeeopnbnkno(singDto[9]);//收款单位开户行
					if(singDto[10] == null || "".equals(singDto[10].trim())) {
						multi.getErrorList().add(fi.getName() +" 退库文件["+fi.getName()+"]中存在'收款单位名称'为空!");
					}
					String errChi_10 = VerifyParamTrans.verifyNotUsableChinese(singDto[10]);
					if(null != errChi_10 && !"".equals(errChi_10)) {
						multi.getErrorList().add(fi.getName() +" 退库文件["+fi.getName()+"]中收款人名称存在非法字符："+errChi_10);
					}
					dwbkDto.setSpayeename(singDto[10]);//收款单位名称
					if(singDto[11] == null || "".equals(singDto[11].trim())) {
						multi.getErrorList().add(fi.getName() +" 退库文件["+fi.getName()+"]中存在'收款单位账号'为空!");
					}
					dwbkDto.setSpayeeacct(singDto[11]);//收款单位账号
					if(singDto[12] == null || "".equals(singDto[12].trim())) {
						multi.getErrorList().add(fi.getName() +" 退库文件["+fi.getName()+"]中存在'退库级次'为空!");
					}
					dwbkDto.setCbdglevel(singDto[12]);//退库级次
					if(singDto[13] == null || "".equals(singDto[13].trim())) {
						multi.getErrorList().add(fi.getName() +" 退库文件["+fi.getName()+"]中存在'退库科目代码'为空!");
					}
					String err = this.verifySubject(dmap, singDto[13], MsgConstant.MSG_NO_1104, "", fi.getName(),singDto[8]);
					if(!"".equals(err)) {
						multi.getErrorList().add(err);
					}
					dwbkDto.setSbdgsbtcode(singDto[13]);//退库科目代码
					dwbkDto.setSdwbkreasoncode(singDto[14]);//退库原因
					dwbkDto.setSexamorg(singDto[15]);//退库审批机关
					dwbkDto.setSdwbkby(singDto[16]);//退库依据
					dwbkDto.setFdwbkratio(new BigDecimal(singDto[17]));//退库比例
					dwbkDto.setFdwbkamt(new BigDecimal(singDto[18])); //退库总额
					dwbkDto.setFamt(new BigDecimal(singDto[19]));//退库金额
					famt = famt.add(new BigDecimal(singDto[19]));
					famtPack = famtPack.add(new BigDecimal(singDto[19]));
					if(singDto.length == 20) {
						dwbkDto.setSastflag("");//辅助标志
					}else {
						dwbkDto.setSastflag(singDto[20]);//辅助标志
					}
					dwbkDto.setSbookorgcode(bookorgCode);// 核算主体代码
					dwbkDto.setCtrimflag("0"); // 调整期标志不为空
					dwbkDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					dwbkDto.setSbiztype(biztype);// 业务类型
					dwbkDto.setSfilename(fi.getName()); //导入文件名
					dwbkDto.setDvoucher(CommonUtil.strToDate(fi.getName().substring(0,8))); // 开票日期不为空
					dwbkDto.setCbdgkind(MsgConstant.BDG_KIND_IN); //预算种类不能为空 默认填1，预算内
					dwbkDto.setCbckflag("0"); //退回标志不能为空
					dwbkDto.setDaccept(TimeFacade.getCurrentDateTime()); //受理日期不能为空
					dwbkDto.setSaimtrecode(trecode);
					dwbkDto.setSpackageno(tmpPackNo);
					dwbkDto.setSfilename(fi.getName());
					listdto.add(dwbkDto);
					recordNum++;
					fbcount++;
				}
				TvFilepackagerefDto packdto = new TvFilepackagerefDto();
				
				packdto.setSorgcode(bookorgCode);
				packdto.setStrecode(trecode);
				packdto.setSfilename(fi.getName());
				
				
				if (!fincmap.containsKey(trecode)) {
					multi.getErrorList().add("退库文件["+fi.getName()+"]中根据核算主体代码："+bookorgCode+"、 国库主体代码："+trecode+" 没有在 '财政机构信息维护参数'中找到对应财政机构代码!");
				}else {
					TsConvertfinorgDto gd = fincmap.get(trecode);
					packdto.setStaxorgcode(gd.getSfinorgcode());
				}				
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
			
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("退库导入文件解析异常 \n"+e.getMessage(), e);
		} catch (SequenceException e) {
			log.error(e);
			throw new ITFEBizException("退库导入文件解析异常 \n"+e.getMessage(),e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询数据异常 \n"+e.getMessage(),e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("查询数据异常 \n"+e.getMessage(),e);
		}
		if(famthead.compareTo(famt) != 0) {
			multi.getErrorList().add("退库文件["+fi.getName()+"]中文件头金额[" + famthead + "]与明细总金额[" + famt
						+ "]不符");			
		}
		
		//文件中凭证编号校验
		int oldSize = 0;
		int newSize = 0;
		Set<String> sets = new HashSet<String>();
		for (String item : voulist) {
			oldSize = sets.size();
			sets.add(item.trim());
			newSize = sets.size();
			if (newSize == oldSize) {
				multi.getErrorList().add("退库文件["+fi.getName()+"]中存在凭证编号重复");
			}
		}
		
		multi.setFatherDtos(listdto);
		multi.setPackDtos(packdtos);
		multi.setVoulist(voulist);
		multi.setFamt(famt); //总金额
		multi.setTotalCount(recordhead); //总笔数
		return multi;

	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
}

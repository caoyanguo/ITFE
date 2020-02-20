package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * author hua 退库文件导入解析类
 */
public class DwbkTipsFileOperForXm extends AbstractTipsFileOper {
	
	
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
		String fname = new File(file).getName();
		String ctrimflag = fname.substring(fname.length()-5, fname.length()-4);
		List<IDto> listdto = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		List<IDto> famtdtos = new ArrayList<IDto>();
		File fi = new File(file);
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal famtPack = new BigDecimal("0.00");
		String trecode = "";
		String tmpPackNo = "";
		List<String> voulist = new ArrayList<String>(); //凭证编号
		try {
			HashMap<String, TsConvertfinorgDto> fincmap =this.makeFincMap(bookorgCode);
			HashMap<String,TsPaybankDto> paymap = this.makeBankMap();
			List<String[]> fileContent = super.readFile(file, ",");
			if(file.endsWith("txt")) {  //明细刚接口
				int li_Detail = (fileContent.size()-1) / 1000;
				if(fileContent.size() == (li_Detail * 1000)) {
					li_Detail = li_Detail - 1;
				}
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
						
						dwbkDto.setSpayertrecode(singDto[2]);// 收款国库代码
						trecode = singDto[2];
						if(!this.checkTreasury(trecode, bookorgCode)) {
							multi.getErrorList().add("退库文件["+fi.getName()+"]中国库主体代码："+trecode+" 没有在'国库主体信息参数'中查找到!");
						}
						if (singDto[3] != null || !singDto[3].trim().equals("")) {
							dwbkDto.setSaimtrecode(singDto[3]); // 目的国库代码
						} else {
							dwbkDto.setSaimtrecode(singDto[2]); // 目的国库代码
						}
						TsConverttaxorgDto convertTaxOrgDto = new TsConverttaxorgDto();
						// 核算主体代码
						convertTaxOrgDto.setSorgcode(bookorgCode);
						// 国库主体代码
						convertTaxOrgDto.setStrecode(trecode);
						// TBS征收机关代码
						convertTaxOrgDto.setStbstaxorgcode(singDto[1]);
						List dtoL = CommonFacade.getODB().findRsByDtoWithUR(
								convertTaxOrgDto);
						String strTipsTaxOrg = "";
						if (dtoL == null || dtoL.size() == 0) {
							multi.getErrorList().add("退库文件[" + fi.getName()
									+ "]中国库主体代码："+trecode+",征收机关代码：" + singDto[1]
									+ " 没有维护'征收机关对照'!");
						}else {
							TsConverttaxorgDto taxogdto = (TsConverttaxorgDto) dtoL.get(0);
							// Tips征收机关代码
							strTipsTaxOrg = taxogdto.getStcbstaxorgcode();
							// 征收机关代码
							dwbkDto.setStaxorgcode(strTipsTaxOrg);
						}						
						
						dwbkDto.setSdwbkvoucode(singDto[0]); // 凭证编号
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
						voulist.add(strTipsTaxOrg.trim()+","+singDto[0].trim());
						
						dwbkDto.setCbdglevel(singDto[4]); // 预算级次
						dwbkDto.setCbdgkind(singDto[5]); // 预算种类
						
						String err = this.verifySubject(dmap, singDto[6], MsgConstant.MSG_NO_1104, "", fi.getName(),singDto[0]);
						if(!"".equals(err)) {
							multi.getErrorList().add(err);
						}
						dwbkDto.setSbdgsbtcode(singDto[6]); // 科目代码
						dwbkDto.setSastflag(singDto[7]); // 辅助标志
						dwbkDto.setSdwbkreasoncode(singDto[8]); // 退库原因代码
						dwbkDto.setSdwbkby(singDto[9]); // 退库依据
						dwbkDto.setSexamorg(singDto[10]); // 审批机关
						
						dwbkDto.setDaccept(TimeFacade.getCurrentDateTime());// D_ACCEPT受理日期不能为空

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
							famtPack = famtPack.add(new BigDecimal(singDto[13]));
						} else {
							dwbkDto.setFamt(new BigDecimal("0.00"));
						}

						dwbkDto.setCbckflag(singDto[14]); // 退回标志
						dwbkDto.setSpayeeacct(singDto[15]); // 收款帐号
						dwbkDto.setSpayeecode(singDto[16]); // 收款单位代码
						
						dwbkDto.setSbookorgcode(bookorgCode);// 核算主体代码
						dwbkDto.setCtrimflag(ctrimflag); // 调整期标志不为空
						dwbkDto.setSbiztype(biztype);// 业务类型
						dwbkDto.setDvoucher(CommonUtil.strToDate(fi.getName().substring(0,8))); // 开票日期不为空
						dwbkDto.setSelecvouno(singDto[0]); //电子凭证编号不能为空
						dwbkDto.setSpackageno(tmpPackNo);
						dwbkDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
						dwbkDto.setSfilename(fi.getName()); //导入文件名
						listdto.add(dwbkDto);
						fbcount++;
						recordNum++;
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
			}else if (file.endsWith("tmp")) { //资金岗接口
				String srcvreno = fi.getName().substring(0, 12);
				for (int i = 0; i < fileContent.size(); i++) {
					TvPayoutfinanceDto dto = new TvPayoutfinanceDto();
					dto.setSrcvreckbnkno(srcvreno);
					String[] strs = fileContent.get(i);
					if("<CA>".equals(strs[0].toUpperCase())) {
						break ;
					}
					dto.setSsndbnkno(strs[0]);   //发起行行号
					dto.setSrcvbnkno(strs[1]);   //接受行行号
					dto.setFamt(new BigDecimal(strs[2]));  //金额
					famt = famt.add(new BigDecimal(strs[2]));
					famtPack = famtPack.add(new BigDecimal(strs[2]));
					dto.setSpayeropnbnkno(strs[3]);    //付款人开户行行号
					dto.setSpayeracct(strs[4]);   //付款人账号
					dto.setSpayername(strs[5]);   //付款人名称
					dto.setSpayeraddr(strs[6]);   //付款人地址
					if(strs[7] == null || "".equals(strs[7].trim())) {
						multi.getErrorList().add(fi.getName() +" 退库文件["+fi.getName()+"]中存在'收款单位开户行'为空!");
					}
					TsPaybankDto bnkpaydto = paymap.get(strs[7].trim());
					if(null == bnkpaydto) {
						multi.getErrorList().add("退库文件["+fi.getName()+"]根据收款人开户行：'"+strs[7].trim()+"'没有在'支付行号查询参数' 中找到相应清算行行号!");
					}else {
						if(null != bnkpaydto.getSstate() && "0".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("退库文件["+fi.getName()+"]中收款人开户行：'"+strs[7].trim()+"' 处于'生效前'状态!");
						}else if (null != bnkpaydto.getSstate() && "2".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("退库文件["+fi.getName()+"]中收款人开户行：'"+strs[7].trim()+"' 处于'注销'状态!");
						}
					}					
					dto.setSpayeeopnbnkno(strs[7]);   //收款人开户行行号
					dto.setSpayeeacct(strs[8]);     //收款人账号	
					String errChi_9 = VerifyParamTrans.verifyNotUsableChinese(strs[9]);
					if(null != errChi_9 && !"".equals(errChi_9)) {
						multi.getErrorList().add("退库文件["+fi.getName()+"]中收款人名称存在非法字符："+errChi_9);
					}
					dto.setSpayeename(strs[9]);		//收款人名称
					dto.setSpayeeaddr(strs[10]);	//收款人地址
					dto.setSpaybizkind(strs[11]);   //支付业务种类
					dto.setSbiztype(strs[12]);   //业务类型号
					String errChi_13 = VerifyParamTrans.verifyNotUsableChinese(strs[13]);
					if(null != errChi_13 && !"".equals(errChi_13)) {
						multi.getErrorList().add("退库文件["+fi.getName()+"]中附言存在非法字符："+errChi_13);
					}
					dto.setSaddword(strs[13]);      //附言
					
					famtdtos.add(dto);
				}
			}		
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("退库导入文件解析出错 \n"+e.getMessage(),e);
		} catch (SequenceException e) {
			log.error(e);
			throw new ITFEBizException("退库导入文件解析异常 \n"+e.getMessage(),e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("数据查询异常 \n"+e.getMessage(),e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("数据查询异常 \n"+e.getMessage(),e);
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
		multi.setSonDtos(famtdtos);
		multi.setVoulist(voulist);
		multi.setFamt(famt); //总金额
		multi.setTotalCount(recordNum); //总笔数
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

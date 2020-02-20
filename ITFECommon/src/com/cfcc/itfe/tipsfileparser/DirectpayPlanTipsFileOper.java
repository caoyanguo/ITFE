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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua  直接支付额度文件导入解析类
 */
public class DirectpayPlanTipsFileOper extends AbstractTipsFileOper {
	/* (non-Javadoc)
	 * @see com.cfcc.itfe.tipsfileparser.ITipsFileOper#fileParser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.cfcc.jaf.persistence.jaform.parent.IDto, java.util.Map)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,String userid,
			 String biztype,
			String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap) throws ITFEBizException {

		int recordNum = 0; // 记录记录器
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// 业务类型
		mulitDto.setSbookorgcode(bookorgCode);// 核算主体代码
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal sonFamt = new BigDecimal("0.00");
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> sondtos = new ArrayList<IDto>();
		List<IDto> sondtos_tmp = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		List<String> voulist = new ArrayList<String>(); //凭证编号集
		File fi = new File(file);
		Map<String,BigDecimal> diremap = new HashMap<String, BigDecimal>();
		
		String strecode="";
		String mainvou ="";
		String mainvou_tmp ="";
		String svo = "";
		try {
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgCode); //法人代码缓存
			HashMap<String, TsTreasuryDto> tremap =SrvCacheFacade.cacheTreasuryInfo(bookorgCode);//国库代码缓存
			HashMap<String, TsConvertfinorgDto> finmap =SrvCacheFacade.cacheFincInfo(bookorgCode);//财政代码缓存
			Map<String,String> bankInfo= this.getBankInfo(bookorgCode);//从行名对照表获取银行名（财政）与行号信息
			List<String[]> fileContent = super.readFile(file, ",");
			String trecode="";
			for (int i = 0; i < fileContent.size(); i++) {
				String[] strs = fileContent.get(i);
				if (i == 0) {
					/**
					 *  文件第一行为主体信息	
					 */
					mainvou = SequenceGenerator.getNextByDb2(SequenceName.DIRECTPAY_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH);
					mainvou_tmp = mainvou;
					//包流水号默认为"",待多文件组包时再生成
					String tmpPackNo = "";
					
					TbsTvDirectpayplanMainDto mainDto = new TbsTvDirectpayplanMainDto();
					mainDto.setIvousrlno(Long.parseLong(mainvou));
					strecode =strs[0];
					if(!this.checkTreasury(strs[0], bookorgCode)) {
						mulitDto.getErrorList().add("直接支付额度文件["+fi.getName()+"] 凭证编号为["+strs[9].trim()+"]的记录中国库主体代码："+strs[0]+" 在'国库主体信息参数'中不存在!");
					}
				    trecode =strs[0];
					mainDto.setStrecode(strs[0]); // 国库代码
					String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_5102);
					if(null != errorInfo && errorInfo.length() > 0) {
						mulitDto.getErrorList().add(errorInfo);
					}
					mainDto.setSbiztype(biztype);// 业务类型
					mainDto.setSpayeracct(strs[1]); // 付款人帐号
					//付款人全称(单位代码)
					mainDto.setSpayeropnbnkno(strs[3]); // 付款人开户银行
					mainDto.setSpayeeacct(strs[4]); // 收款人帐号
					mainDto.setSpayeename(strs[5]); // 收款人全称
					mainDto.setSpayeeopnbnkno(strs[6]); // 收款人开户行
					mainDto.setCbdgkind(strs[7]); // 预算种类代码
					mainDto.setFamt(new BigDecimal(strs[8])); // 合计金额
					mainDto.setSvouno(strs[9]); // 凭证编号
					svo = strs[9];
					voulist.add(trecode.trim()+","+strs[9].trim());
					mainDto.setScrpvoucode(strs[10]); // 对应凭证编号
					mainDto.setDvoucher(CommonUtil.strToDate(strs[11]));// 凭证日期
					famt = famt.add(new BigDecimal(strs[8]));
					
					//增加判断是否是上海的补录行名
					if(ITFECommonConstant.ISMATCHBANKNAME.equals(StateConstant.IF_MATCHBNKNAME_YES)) {
						if(strs.length > 12 && null != strs[12] && !"".equals(strs[12])) {
							mainDto.setSpayeeopnbnkname(strs[12]);
							if(null != strs[6] && !"".equals(strs[6])) {
								mainDto.setSpayeeopnbnkno(strs[6]);
								mainDto.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);
							}else {
								if(null != bankInfo.get(strs[12]) && !"".equals(bankInfo.get(strs[12]))) {
									mainDto.setSpayeeopnbnkno(bankInfo.get(strs[12]));
									mainDto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								}else {
									mainDto.setSpayeeopnbnkno("");
									mainDto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								}
							}
						}else {
							mulitDto.getErrorList().add("直接支付额度文件["+fi.getName()+"] 凭证编号为["+strs[9].trim()+"]的记录中 收款人开户行行名 为空，请查证!");
						}
					}else {
						mainDto.setSpayeeopnbnkno(strs[6]); // 收款人开户行
					}
					
					mainDto.setIofyear(Integer.parseInt(fi.getName().substring(0,4))); //所属年度不为空
					mainDto.setDaccept(TimeFacade.getCurrentDateTime()); //受理日期不为空(默认当前日期)
					mainDto.setSstatus(StateConstant.CONFIRMSTATE_NO);  //交易状态
					mainDto.setSpackageno(tmpPackNo);
					mainDto.setSbookorgcode(bookorgCode);// 核算主体代码
					mainDto.setSfilename(fi.getName());
					//文件名与包对应关系
					TvFilepackagerefDto packdto = new TvFilepackagerefDto();
					packdto.setSorgcode(bookorgCode);
					packdto.setStrecode(strs[0]);
					packdto.setSfilename(fi.getName());
					if (finmap.containsKey(strs[0])) {
						packdto.setStaxorgcode(finmap.get(strs[0]).getSfinorgcode());
					}else{
						mulitDto.getErrorList().add("直接支付额度文件["+fi.getName()+"] 凭证编号为["+strs[9].trim()+"]的记录中根据核算主体代码："+bookorgCode+"、 国库主体代码："+strs[0]+" 在 '财政机构信息维护参数'中不存在!");
					}				
					packdto.setScommitdate(TimeFacade.getCurrentStringTime());
					packdto.setSaccdate(TimeFacade.getCurrentStringTime());
					packdto.setSpackageno(tmpPackNo);
					packdto.setSoperationtypecode(biztype);
					packdto.setIcount(1);
					packdto.setNmoney(famt);
					packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
					packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
					packdto.setSusercode(userid);
					packdto.setImodicount(0);
					
					packdtos.add(packdto);
					fatherdtos.add(mainDto);
				} else {
					//sub信息
					if(strs.length>1){
						TbsTvDirectpayplanSubDto subDto = new TbsTvDirectpayplanSubDto();
						String err = this.verifySubject(dmap, strs[0], MsgConstant.MSG_NO_5102, "1", fi.getName(),svo);
						if(!"".equals(err)) {
							mulitDto.getErrorList().add(err);
						}
						subDto.setSfuncsbtcode(strs[0]); // 功能科目代码
						
						if(null != tremap.get(trecode)) {
							if (StateConstant.COMMON_YES.equals(tremap.get(trecode).getSisuniontre())) { //启用的情况下判断经济代码不能为空
								err = this.verifySubject(dmap, strs[1], MsgConstant.MSG_NO_5102, "2", fi.getName(),svo);
								if(!"".equals(err)) {
									mulitDto.getErrorList().add(err);
								}
								subDto.setSecosbtcode(strs[1]); // 经济科目代码
							} else { //不启用的情况下，不管文件中是否有经济代码，都填写空值
								subDto.setSecosbtcode("");
							}
						}
						
						subDto.setSbdgorgcode(strs[2]); // 预算单位代码
						//修改为按照国库代码+法人代码进行维护
						if(!rpmap.containsKey(trecode+subDto.getSbdgorgcode().trim())) {
							mulitDto.getErrorList().add("直接支付额度文件["+fi.getName()+"] 凭证编号为["+svo+"]的记录中预算单位代码 '"+subDto.getSbdgorgcode()+"' 在法人代码表中不存在!");
						}
						subDto.setFamt(new BigDecimal(strs[3]));// 发生额
						sonFamt = sonFamt.add(new BigDecimal(strs[3]));
						subDto.setSbookorgcode(bookorgCode); //核算主体代码
						subDto.setIvousrlno(Long.parseLong(mainvou)); //凭证流水号
						if(diremap.containsKey(strs[0]+","+strs[1]+","+strs[2])) { //对相同预算科目、功能代码、经济代码进行汇总
							diremap.put(strs[0]+","+strs[1]+","+strs[2], diremap.get(strs[0]+","+strs[1]+","+strs[2]).add(new BigDecimal(strs[3])));
						}else {
							diremap.put(strs[0]+","+strs[1]+","+strs[2], new BigDecimal(strs[3]));
						}
						sondtos_tmp.add(subDto);
						recordNum++; 
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException e)
		{
			log.error(e);
			throw new ITFEBizException("直接支付额度文件"+fi.getName()+"解析出错 \n"+e.getMessage(), e);
		}catch(FileOperateException e)
		{
			log.error(e);
			throw new ITFEBizException("直接支付额度文件"+fi.getName()+"解析出错 \n"+e.getMessage(), e);
		}catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("直接支付额度文件"+fi.getName()+"解析出错：\n"+e.getMessage(), e);
		}
		if (famt.compareTo(new BigDecimal("0.00")) == 0) {
			mulitDto.getErrorList().add("直接支付额度文件["+fi.getName()+"]发生额不能为0");
		}
		if (famt.compareTo(sonFamt) != 0) {
			mulitDto.getErrorList().add("直接支付额度文件["+fi.getName()+"]中汇总金额[" + famt + "]与明细总金额[" + sonFamt
					+ "]不符");
		}
		
		//文件中凭证编号校验
		int oldSize = 0;
		int newSize = 0;
		Set<String> sets = new HashSet<String>();
		for (String item : voulist) {
			oldSize = sets.size();
			sets.add(item);
			newSize = sets.size();
			if (newSize == oldSize) {
				mulitDto.getErrorList().add("直接支付额度文件["+fi.getName()+"]中存在凭证编号重复");
			}
		}
		//按照功能、经济、预算单位汇总
		if(recordNum >499) {
			for(String single : diremap.keySet()) {
				String[] strs = single.split(",");
				TbsTvDirectpayplanSubDto diredto = new TbsTvDirectpayplanSubDto();
				diredto.setIvousrlno(Long.parseLong(mainvou_tmp));
				diredto.setSfuncsbtcode(strs[0]);
				diredto.setSecosbtcode(strs[1]);
				diredto.setSbdgorgcode(strs[2]);
				diredto.setFamt(diremap.get(single));
				diredto.setSbookorgcode(bookorgCode);
				sondtos.add(diredto);
			}
		}else {
			sondtos.addAll(sondtos_tmp);
		}
		if(sondtos.size() > 499) {
			mulitDto.getErrorList().add("直接支付额度文件["+fi.getName()+"]中明细子体信息按照功能类科目代码、经济类科目代码、预算单位代码进行汇总后，总笔数为["+sondtos.size()+"],超过499笔，请手工拆包后导入!");			
		}
		for(int i = 1 ; i <= sondtos.size() ; i++) {
			TbsTvDirectpayplanSubDto d = (TbsTvDirectpayplanSubDto) sondtos.get(i-1);	
			d.setIdetailseqno(i);
		}
		mulitDto.setFatherDtos(fatherdtos);
		mulitDto.setSonDtos(sondtos);
		mulitDto.setPackDtos(packdtos);
		mulitDto.setVoulist(voulist);
		mulitDto.setFamt(famt);

		return mulitDto;

	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

}

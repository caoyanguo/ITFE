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
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua 授权支付额度文件导入解析类
 */
public class GrantpayPlanTipsFileOper extends AbstractTipsFileOper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {

		MulitTableDto mulitDto = new MulitTableDto();
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal sonfamt = new BigDecimal("0.00");
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> sondtos = new ArrayList<IDto>();
		List<IDto> sondtos_tmp = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		File fi = new File(file);
		int recordNum = 0; // 记录记录器
		List<String> voulist = new ArrayList<String>();
		Map<String,BigDecimal> grantmap = new HashMap<String, BigDecimal>();
		Map<String,BigDecimal> zeromap = new HashMap<String, BigDecimal>();
		Map<String,BigDecimal> smallmap = new HashMap<String, BigDecimal>();
		String mainvou = "";
		String mainvou_tmp = "";
		String trecode = "";
		String svo = "";
		try {
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgCode); //法人代码缓存
			HashMap<String, TsTreasuryDto> tremap =SrvCacheFacade.cacheTreasuryInfo(bookorgCode);
			HashMap<String, TsConvertfinorgDto> finmap =SrvCacheFacade.cacheFincInfo(bookorgCode);//财政代码缓存
			List<String[]> fileContent = super.readFile(file, ",");
			for (int i = 0; i < fileContent.size(); i++) {
				String[] strs = fileContent.get(i);
				if (i == 0) {
					//第一行为文件主主体信息
					mainvou = SequenceGenerator.getNextByDb2(SequenceName.GRANTPAY_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH);
					mainvou_tmp = mainvou;
					//包流水号默认为"",待多文件组包时再生成
					String tmpPackNo = "";
					
					TbsTvGrantpayplanMainDto mainDto = new TbsTvGrantpayplanMainDto();
					mainDto.setIvousrlno(Long.parseLong(mainvou)); //凭证流水号
					trecode = strs[0];
					mainDto.setStrecode(trecode); // 国库代码
					String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_5103);
					if(null != errorInfo && errorInfo.length() > 0) {
						mulitDto.getErrorList().add(errorInfo);
					}
					if(!this.checkTreasury(strs[0].trim(), bookorgCode)) {
						mulitDto.getErrorList().add("授权支付额度文件["+fi.getName()+"] 凭证编号为["+strs[1].trim()+"]的记录中国库主体代码："+strs[0].trim()+" 在'国库主体信息参数'中不存在!");
					}
					mainDto.setSvouno(strs[1]); // 凭证编号
					svo = strs[1];
					voulist.add(trecode.trim()+","+strs[1].trim());
					mainDto.setSbnkno(strs[2]); // 代理银行代码
					mainDto.setSbdgorgcode(strs[3]); // 预算单位代码
					if(!rpmap.containsKey(strs[0]+mainDto.getSbdgorgcode().trim())) {
						mulitDto.getErrorList().add("授权支付额度文件["+fi.getName()+"] 凭证编号为["+strs[1].trim()+"]的记录中预算单位代码 '"+mainDto.getSbdgorgcode()+"' 在法人代码表中不存在!");
					}
					if (strs[4] != null && !strs[4].trim().equals("")) {
						mainDto.setIofmonth(new Integer(strs[4].trim())); // 所属月份
					}
					mainDto.setCbdgkind(strs[5]); // 预算种类
					if (strs[6] != null && !strs[6].trim().equals("")) {
						mainDto.setFzerosumamt(new BigDecimal(strs[6].trim())); // 零余额发生额
					} else {
						mainDto.setFzerosumamt(new BigDecimal("0.00")); // 零余额发生额
					}
					if (strs[7] != null && !strs[7].trim().equals("")) { // 小额现金发生额
						mainDto.setFsmallsumamt(new BigDecimal(strs[7].trim()));
					} else {
						mainDto.setFsmallsumamt(new BigDecimal("0.00"));
					}
					famt = famt.add(mainDto.getFzerosumamt()).add(
							mainDto.getFsmallsumamt());
					mainDto.setDaccept(TimeFacade.getCurrentDateTime()); //受理日期不能为空
					mainDto.setSbiztype(biztype);// 业务类型
					mainDto.setSfilename(fi.getName());
					mainDto.setSpackageno(tmpPackNo); //包流水号
					
					//文件名与包对应关系
					TvFilepackagerefDto packdto = new TvFilepackagerefDto();
					packdto.setSorgcode(bookorgCode);					
					packdto.setStrecode(strs[0]);
					packdto.setSfilename(fi.getName());
					if (finmap.containsKey(strs[0])) {
						packdto.setStaxorgcode(finmap.get(strs[0]).getSfinorgcode());
					}else{
						mulitDto.getErrorList().add("授权支付额度文件["+fi.getName()+"] 凭证编号为["+strs[1].trim()+"]的记录中根据核算主体代码："+bookorgCode+"、 国库主体代码："+strs[0]+" 在 '财政机构信息维护参数'中不存在!");
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
					packdto.setSorgcode(bookorgCode);					
					mainDto.setSbookorgcode(bookorgCode);// 核算主体代码
					mainDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					packdtos.add(packdto);
					fatherdtos.add(mainDto);

				} else {
					if(strs.length>1){
						TbsTvGrantpayplanSubDto subDto = new TbsTvGrantpayplanSubDto();
						subDto.setIvousrlno(Long.parseLong(mainvou));
						
						String err = this.verifySubject(dmap, strs[0], MsgConstant.MSG_NO_5103, "1", fi.getName(),svo);
						if(!"".equals(err)) {
							mulitDto.getErrorList().add(err);
						}
						subDto.setSfuncsbtcode(strs[0]); // 功能科目代码
						if(null != tremap.get(trecode)) {
							if (StateConstant.COMMON_YES.equals(tremap.get(trecode).getSisuniontre())) { //启用的情况下判断经济代码不能为空
								err = this.verifySubject(dmap, strs[1], MsgConstant.MSG_NO_5103, "2", fi.getName(),svo);
								if(!"".equals(err)) {
									mulitDto.getErrorList().add(err);
								}
								subDto.setSecosbtcode(strs[1]); // 经济科目代码
							} else { //不启用的情况下，不管文件中是否有经济代码，都填写空值
								subDto.setSecosbtcode("");
							}
						}					
						subDto.setFzerosumamt(new BigDecimal(strs[2])); // 零余额发生额
						subDto.setFsmallsumamt(new BigDecimal(strs[3]));// 小额现金发生额
						subDto.setIvousrlno(Long.parseLong(mainvou));
						sonfamt = sonfamt.add(new BigDecimal(strs[2])).add(
								new BigDecimal(strs[3]));
						if(grantmap.containsKey(strs[0]+","+strs[1])) { //对相同功能代码、经济代码进行汇总
							grantmap.put(strs[0]+","+strs[1], grantmap.get(strs[0]+","+strs[1]).add(new BigDecimal(strs[2])).add(new BigDecimal(strs[3])));
							zeromap.put(strs[0]+","+strs[1], zeromap.get(strs[0]+","+strs[1]).add(new BigDecimal(strs[2])));
							smallmap.put(strs[0]+","+strs[1], smallmap.get(strs[0]+","+strs[1]).add(new BigDecimal(strs[3])));
						}else {
							grantmap.put(strs[0]+","+strs[1], new BigDecimal(strs[2]).add(new BigDecimal(strs[3])));
							zeromap.put(strs[0]+","+strs[1], new BigDecimal(strs[2]));
							smallmap.put(strs[0]+","+strs[1], new BigDecimal(strs[3]));
						}
						sondtos_tmp.add(subDto);
						recordNum ++;
//						sondtos.add(subDto);
					}
				}
			}	
		}catch(ArrayIndexOutOfBoundsException e)
		{
			log.error(e);
			throw new ITFEBizException("授权支付额度文件"+fi.getName()+"解析出错 \n"+e.getMessage(), e);
		}catch(FileOperateException e)
		{
			log.error(e);
			throw new ITFEBizException("授权支付额度文件"+fi.getName()+"解析出错 \n"+e.getMessage(), e);
		}catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("授权支付额度文件"+fi.getName()+"解析出错 \n"+e.getMessage(), e);
		}

		if (famt.compareTo(new BigDecimal(0)) == 0) {
			mulitDto.getErrorList().add("授权支付额度文件["+fi.getName()+"]零余额发生额、小额现金发生额不能同时为 0 ");
		}
		if (famt.compareTo(sonfamt) != 0) {
			mulitDto.getErrorList().add("授权支付额度文件["+fi.getName()+"]中汇总金额[" + famt + "]与明细总金额[" + sonfamt
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
				mulitDto.getErrorList().add("授权支付额度文件["+fi.getName()+"]中存在凭证编号重复");
			}
		}
		
		//按照功能、经济汇总
		if(recordNum >499) {
			for(String single : grantmap.keySet()) {
				String[] strs = single.split(",");
				TbsTvGrantpayplanSubDto grantdto = new TbsTvGrantpayplanSubDto();
				grantdto.setIvousrlno(Long.parseLong(mainvou_tmp));
				grantdto.setSfuncsbtcode(strs[0]);
				if(strs.length == 1) {
					grantdto.setSecosbtcode("");
				}else {
					grantdto.setSecosbtcode(strs[1]);
				}
				
				grantdto.setFzerosumamt(zeromap.get(single));
				grantdto.setFsmallsumamt(smallmap.get(single));
				sondtos.add(grantdto);
			}
		}else {
			sondtos.addAll(sondtos_tmp);
		}
		
		if(sondtos.size() > 499) {
			mulitDto.getErrorList().add("授权支付额度文件["+fi.getName()+"]中明细子体信息按照功能类科目代码、经济类科目代码进行汇总后，总笔数为["+sondtos.size()+"],超过499笔，请手工拆包后导入!");			
		}
		for(int i = 1 ; i <= sondtos.size() ; i++) {
			TbsTvGrantpayplanSubDto d = (TbsTvGrantpayplanSubDto) sondtos.get(i-1);	
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

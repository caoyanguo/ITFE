package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 划款申请退款转化
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor2202 {

	private static Log logger = LogFactory.getLog(Dto2MapFor2202.class);
	
	/**
	 * DTO转化XML报文(划款申请退款业务)
	 * 
	 * @param List
	 *            <TvDirectpaymsgmainDto> list 发送报文对象集合
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            packno 包流水号
	 * @param boolean isRepeat 是否重发报文
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TvPayreckBankBackDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
		if (null == list || list.size() == 0) {
			throw new ITFEBizException("要转化的对象为空,请确认!");
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		// 报文头 HEAD
		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.APPLYPAY_BACK_DAORU);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		headMap.put("Reserve", MsgConstant.CFCCHL);
		 
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}

		// 设置报文消息体 MSG
		HashMap<String, Object> head2202Map = new HashMap<String, Object>();
		
		head2202Map.put("FinOrgCode", list.get(0).getSfinorgcode());
		head2202Map.put("TreCode", list.get(0).getStrecode()); // 国库代码
		head2202Map.put("AgentBnkCode", list.get(0).getSagentbnkcode());
		head2202Map.put("EntrustDate", list.get(0).getDentrustdate().toString().replaceAll("-", ""));
		head2202Map.put("PackNo", list.get(0).getSpackno()); // 包序号
		head2202Map.put("AllNum", String.valueOf(list.size())); // 总笔数
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		head2202Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // 支出凭证类型: 0、无纸凭证1、有纸凭证
		//支付方式赋值方式修改（解决无纸化与地方横联冲突问题）
		head2202Map.put("PayMode",list.get(0).getSpaymode());
		
		/**String biztype="";
		
		if(!filename.equals("") && filename.length()>13){
			String name=filename.toLowerCase().replace(".txt", "");
			if(name.length()==13){
				biztype=name.substring(10, 12);
			}else if (name.length()==15){
				biztype=name.substring(12, 14);
			}
		}else{
		    biztype=filename;
		}
		
		//不同的业务类型具有不同的支付方式（0-直接支付  1-授权支付）
		if(biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)){
			head2202Map.put("PayMode", MsgConstant.directPay);
		}else if(biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
			head2202Map.put("PayMode",MsgConstant.grantPay);
		}else{
			head2202Map.put("PayMode",MsgConstant.directPay+MsgConstant.grantPay);
		}**/

		List<Object> bill2202List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill2202Map = new HashMap<String, Object>();
			List<Object> detail2202List = new ArrayList<Object>();

			TvPayreckBankBackDto maindto = list.get(i);
			
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			if(ITFECommonConstant.PUBLICPARAM.contains(",bankpaysub=sum,")&&(filename!=null&&filename.endsWith(".msg")))
				sublist = tranList(sublist);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("要转化的对象对应的子记录为空,请确认![凭证编号" + list.get(i).getSvouno()+ "]");
			}else if(sublist.size()>=500)
			{
				if(ITFECommonConstant.PUBLICPARAM.contains(",bankpaysub=sum,"))
					throw new ITFEBizException("明细按预算单位和科目汇总大于500笔,请确认![凭证编号" + list.get(i).getSvouno()+ "]");
			}

			allamt = allamt.add(maindto.getFamt());

			bill2202Map.put("TraNo", maindto.getStrano()); // 交易流水号
			bill2202Map.put("VouNo", maindto.getSvouno()); // 凭证编号
			bill2202Map.put("VouDate", maindto.getDvoudate().toString().replaceAll("-", "")); // 凭证日期[开票日期]
			bill2202Map.put("OriTraNo",maindto.getSoritrano() );
			bill2202Map.put("OriEntrustDate",maindto.getDacceptdate().toString().replaceAll("-", "") );
			bill2202Map.put("OriVouNo",maindto.getSorivouno());
			bill2202Map.put("OriVouDate",maindto.getDorivoudate().toString().replaceAll("-", ""));
			bill2202Map.put("OriPayerAcct",maindto.getSpayeracct() );
			bill2202Map.put("OriPayerName",maindto.getSpayername() );
			bill2202Map.put("OriPayeeAcct",maindto.getSpayeeacct() );
			bill2202Map.put("OriPayeeName",maindto.getSpayeename());
			bill2202Map.put("PayDictateNo", maindto.getSpaydictateno());
			bill2202Map.put("PayMsgNo",maindto.getSpaymsgno());
			bill2202Map.put("PayEntrustDate",maindto.getDpayentrustdate().toString().replaceAll("-", "") );
			bill2202Map.put("PaySndBnkNo", maindto.getSpaysndbnkno());
			bill2202Map.put("BudgetType", maindto.getSbudgettype());
			bill2202Map.put("TrimSign", maindto.getStrimsign());
			bill2202Map.put("OfYear", maindto.getSofyear());
			bill2202Map.put("Amt",  MtoCodeTrans.transformString(maindto.getFamt()));
			bill2202Map.put("StatInfNum", sublist.size());
			String s = "0";
			try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("查找国库代码异常!", e);
				throw new ITFEBizException("查找国库代码异常！", e);
			}
			
			// 循环子信息
			for (int j = 0; j < sublist.size(); j++) {
				HashMap<String, Object> detail2202Map = new HashMap<String, Object>();
				TvPayreckBankBackListDto subdto = (TvPayreckBankBackListDto) sublist.get(j);
				detail2202Map.put("SeqNo", subdto.getIseqno().toString());
				detail2202Map.put("BdgOrgCode", subdto.getSbdgorgcode());
				detail2202Map.put("FuncSbtCode", subdto.getSfuncbdgsbtcode());
				if (StateConstant.COMMON_YES.equals(s)) {
					detail2202Map.put("EcnomicSubjectCode", subdto.getSecnomicsubjectcode());
				}else{
					detail2202Map.put("EcnomicSubjectCode", "");
				}
				detail2202Map.put("Amt", MtoCodeTrans.transformString(subdto.getFamt()) );
				detail2202Map.put("AcctProp", subdto.getSacctprop());
				detail2202List.add(detail2202Map);
			}

			bill2202Map.put("Detail2202", detail2202List);
			bill2202List.add(bill2202Map);
		}

		head2202Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("Head2202", head2202Map);
		msgMap.put("Bill2202", bill2202List);

		

		return map;
	}
	public static List<IDto> tranList(List<IDto> sublist)
	{
		List<IDto> returnlist = new ArrayList<IDto>();
		if(sublist!=null&&sublist.size()>0)
		{
			TvPayreckBankBackListDto subdto = null;
			Map<String,TvPayreckBankBackListDto> packMap = new HashMap<String,TvPayreckBankBackListDto>();
			for(int i=0;i<sublist.size();i++)
			{
				subdto = (TvPayreckBankBackListDto)sublist.get(i);
				subdto.setSbdgorgcode(subdto.getSbdgorgcode().substring(0, 7));
				if(packMap.get(subdto.getSbdgorgcode()+subdto.getSfuncbdgsbtcode())==null)
				{
					packMap.put(subdto.getSbdgorgcode()+subdto.getSfuncbdgsbtcode(), subdto);
				}else
				{
					packMap.get(subdto.getSbdgorgcode()+subdto.getSfuncbdgsbtcode()).setFamt(packMap.get(subdto.getSbdgorgcode()+subdto.getSfuncbdgsbtcode()).getFamt().add(subdto.getFamt()));
				}
			}
			Set<String> set = packMap.keySet();
			for(String key:set)
				returnlist.add(packMap.get(key));
		}
		return returnlist;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

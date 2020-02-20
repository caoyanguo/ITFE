package com.cfcc.itfe.util.transformer;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 实拨资金额度转化
 * 
 * @author wangwenbo
 * 
 */
public class Dto2MapFor1000_5101 {

	private static Log logger = LogFactory.getLog(Dto2MapFor1000_5101.class);
	
	/**
	 * DTO转化XML报文(实拨资金业务)
	 * 
	 * @param List
	 *            <TvPayoutmsgmainDto> list 发送报文对象集合
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
	public static Map tranfor(List<TvPayoutmsgmainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_1000);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
		headMap.put("Reserve", "");	// 预留字段

		// 设置报文消息体 MSG
		HashMap<String, Object> head1000Map = new HashMap<String, Object>();
		head1000Map.put("BillOrg", list.get(0).getSpayunit()); // 出票单位
		head1000Map.put("EntrustDate", list.get(0).getScommitdate().replaceAll("-", "")); // 委托日期
		head1000Map.put("PackNo", list.get(0).getSpackageno()); // 包序号
		head1000Map.put("TreCode", list.get(0).getStrecode()); // 国库代码
		head1000Map.put("AllNum", String.valueOf(list.size())); // 总笔数

		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		head1000Map.put("PayoutVouType", MsgConstant.SAME_BANK_PAYOUT_VOU_TYPE1); //凭证类型: 1实拨, 2退库, 3商行划款, 4其他
		

		list=MtoCodeTrans.transformZeroAmtMainDto(list);
		List<Object> bill1000List = new ArrayList<Object>();
		
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill1000Map = new HashMap<String, Object>();

			TvPayoutmsgmainDto maindto = list.get(i);
			
			allamt = allamt.add(maindto.getNmoney());

			bill1000Map.put("TraNo", maindto.getSdealno()); // 交易流水号
			bill1000Map.put("VouNo", maindto.getStaxticketno()); // 凭证编号
			bill1000Map.put("VouDate", maindto.getSgenticketdate().replaceAll("-", "")); // 凭证日期[开票日期]
			bill1000Map.put("PayerAcct", maindto.getSpayeracct()); // 付款人账号
			bill1000Map.put("PayerName", maindto.getSpayername()); // 付款人名称
			bill1000Map.put("PayerAddr", maindto.getSpayeraddr()); // 付款人地址
			bill1000Map.put("Amt", MtoCodeTrans.transformString(maindto.getNmoney())); // 交易金额
			bill1000Map.put("PayeeBankNo", maindto.getSpayeebankno());	//收款行行号
			bill1000Map.put("PayeeOpBkNo", maindto.getSrecbankno()); // 收款人开户行行号
			bill1000Map.put("PayeeAcct", maindto.getSrecacct()); // 收款人账号
			bill1000Map.put("PayeeName", maindto.getSrecname()); // 收款人名称
			bill1000Map.put("PayReason", maindto.getSdemo());	//拨款或退库原因
			bill1000Map.put("AddWord", maindto.getSaddword()); // 附言
			bill1000Map.put("OfYear", maindto.getSofyear()); // 所属年度
			bill1000Map.put("Flag", MsgConstant.MSG_1000_FLAG2); // 1-本行业务 2-同城跨行 3-异地跨行

			bill1000List.add(bill1000Map);
		}

		head1000Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead1000", head1000Map);
		msgMap.put("BillSend1000", bill1000List);
		
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 划款申请转化
 * 
 * @author wangwenbo
 * 
 */
public class Dto2MapFor1000_2201 {

	private static Log logger = LogFactory.getLog(Dto2MapFor1000_2201.class);
	
	/**
	 * DTO转化XML报文(划款申请业务)
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
	public static Map tranfor(List<TvPayreckBankDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.APPLYPAY_DAORU);
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
		HashMap<String, Object> head1000Map = new HashMap<String, Object>();
		
		head1000Map.put("BillOrg",list.get(0).getSfinorgcode() ); ///出票单位, TvPayreckBankDto 中没有此项?
		
		head1000Map.put("EntrustDate", list.get(0).getDentrustdate().toString().replaceAll("-", ""));
		head1000Map.put("PackNo", list.get(0).getSpackno()); // 包序号
		head1000Map.put("TreCode", list.get(0).getStrecode()); // 国库代码
		head1000Map.put("AllNum", String.valueOf(list.size())); // 总笔数
		
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		head1000Map.put("PayoutVouType", MsgConstant.SAME_BANK_PAYOUT_VOU_TYPE3); // 支出凭证类型: 0、无纸凭证1、有纸凭证

		List<Object> bill1000List = new ArrayList<Object>();
		
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill1000Map = new HashMap<String, Object>();
			TvPayreckBankDto maindto = list.get(i);

			allamt = allamt.add(maindto.getFamt());

			bill1000Map.put("TraNo", maindto.getStrano()); // 交易流水号
			bill1000Map.put("VouNo", maindto.getSvouno()); // 凭证编号
			bill1000Map.put("VouDate", maindto.getDvoudate().toString().replaceAll("-", "")); // 凭证日期[开票日期]
			bill1000Map.put("PayerAcct", maindto.getSpayeracct());
			bill1000Map.put("PayerName", maindto.getSpayername());
			bill1000Map.put("PayerAddr", maindto.getSpayeraddr());
			bill1000Map.put("Amt", MtoCodeTrans.transformString(maindto.getFamt())); //金额
			
			bill1000Map.put("PayeeBankNo", maindto.getSpayeeopbkno()); ///收款行行号,  TvPayreckBankDto 中没有此项?
			bill1000Map.put("PayeeOpBkNo", maindto.getSpayeeopbkno());
			bill1000Map.put("PayeeAcct", maindto.getSpayeeacct());
			bill1000Map.put("PayeeName", maindto.getSpayeename());
			
			bill1000Map.put("PayReason",maindto.getSaddword() ); ///拨款或退库原因, TvPayreckBankDto 中没有此项?
			bill1000Map.put("AddWord", maindto.getSaddword());bill1000Map.put("OfYear", maindto.getSofyear());
			bill1000Map.put("OfYear", maindto.getSofyear());
			bill1000Map.put("Flag", MsgConstant.MSG_1000_FLAG2); //收款方标识: 1-本行业务 2-同城跨行 3-异地跨行

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

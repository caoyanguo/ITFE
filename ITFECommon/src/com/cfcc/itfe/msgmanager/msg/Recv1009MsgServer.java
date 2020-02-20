/**
 * 银行申报查询回执1009
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

/**
 * 
 * @author renqingbin
 * 
 */
public class Recv1009MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv1009MsgServer.class);

	/**
	 * 报文信息处理
	 * 
	 * @param eventContext
	 * @throws ITFEBizException
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		HashMap realHead1009 = (HashMap) msgMap.get("RealHead1009");
		HashMap payment1009 = (HashMap) msgMap.get("Payment1009");
		// 解析报文头 headMap
		String orgcode = (String) headMap.get("DES");// 接收机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号

		// 解析银行申报回执msgMap --> RequestHead5001
		String bankno = (String) realHead1009.get("BankNo");// 银行代码
		String entrustDate = (String) realHead1009.get("OriEntrustDate");// 委托日期
		String taxOrgCode = (String) payment1009.get("TaxOrgCode");// 征收机关
		/*
		 * 转发报文，数据不做处理
		 */

		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		String sendseqno;// 发送日志流水号
		String bookorgcode;// 核算主体代码
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取接收日志SEQ出错");
		}

		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, (String) headMap
				.get("SRC"), entrustDate, msgNo, orgcode, 
				(String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, bankno, taxOrgCode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER),  
				MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_RECV+msgNo);

		// 写发送日志
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, orgcode,
				(String) headMap.get("DES"), entrustDate,msgNo, 
				(String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, bankno, taxOrgCode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER),  
				MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_SEND+msgNo);

		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

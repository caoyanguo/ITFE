/**
 * 征收机关申请报表（1025）
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
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

public class Recv1025MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv1025MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		HashMap requesthead1025 = (HashMap) msgMap.get("RequestHead1025");

		// 解析报文头 headMap
		String orgcode = (String) headMap.get("DES");// 接收机构代码
		// String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号

		// 解析财政收入日报表申请头 msgMap --> RequestHead1025
		String sendorgcode = (String) requesthead1025.get("SendOrgCode");// 发送机构代码
		String reportdate = (String) requesthead1025.get("ReportDate");// 报表日期
		String reportarea = (String) requesthead1025.get("ReportArea");// 报表范围

		// 解析所需报表 msgMap --> RequestReport1025
		HashMap requestreport1025 = (HashMap) msgMap.get("RequestReport1025");

		String NrBudget = (String) requestreport1025.get("NrBudget");// 正常期预算收入报表
		String NrDrawBack = (String) requestreport1025.get("NrDrawBack");// 正常期退库报表
		String NrRemove = (String) requestreport1025.get("NrRemove");// 正常期调拨收入报表
		String Amount = (String) requestreport1025.get("Amount");// 总额分成报表
		String NrShare = (String) requestreport1025.get("NrShare");// 正常期共享分成报表
		String TrBudget = (String) requestreport1025.get("TrBudget");// 调整期预算收入报表
		String TrDrawBack = (String) requestreport1025.get("TrDrawBack");// 调整期退库报表
		String TrRemove = (String) requestreport1025.get("TrRemove");// 调整期调拨收入报表
		String TrShare = (String) requestreport1025.get("TrShare");// 调整期共享分成报表
		String Stock = (String) requestreport1025.get("Stock");// 库存日报

		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		String sendseqno;// 发送日志流水号
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取接收日志SEQ出错");
		}

		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, (String) headMap.get("SRC"), 
				reportdate, (String) headMap.get("MsgNo"),
				sendorgcode, (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV+msgNo);

		// 写发送日志
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, (String) headMap.get("SRC"), 
				(String) headMap.get("DES"), reportdate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND+msgNo);

		// 记录接收财政的消息记录--广西MQ消息ID匹配机制修改
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, orgcode, msgNo, msgid, TimeFacade.getCurrentStringTime(), "", jmsMessageID, jmsCorrelationID, sendorgcode);
		
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

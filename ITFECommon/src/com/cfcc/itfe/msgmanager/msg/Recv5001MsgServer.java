/**
 * 财政收入日报表申请报文
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
 * @author wangtuo
 * 
 */
public class Recv5001MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv5001MsgServer.class);

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

		HashMap requesthead5001 = (HashMap) msgMap.get("RequestHead5001");

		// 解析报文头 headMap
		String orgcode = (String) headMap.get("DES");// 接收机构代码
		String sendnode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号

		// 解析财政收入日报表申请头 msgMap --> RequestHead5001
		String sendorgcode = (String) requesthead5001.get("SendOrgCode");// 发送机构代码
		String reportdate = (String) requesthead5001.get("ReportDate");// 所属日期

		// 解析所需报表 msgMap --> requestreport5001
		HashMap requestreport5001 = (HashMap) msgMap.get("RequestReport5001");

//		/*
//		 * 转发报文，数据不做处理
//		 */
//		String NrBudget = (String) requestreport5001.get("NrBudget");// 正常期预算收入报表
//		String NrDrawBack = (String) requestreport5001.get("NrDrawBack");// 正常期退库报表
//		String NrRemove = (String) requestreport5001.get("NrRemove");// 正常期调拨收入报表
//		String Amount = (String) requestreport5001.get("Amount");// 总额分成报表
//		String NrShare = (String) requestreport5001.get("NrShare");// 正常期共享分成报表
//		String TrBudget = (String) requestreport5001.get("TrBudget");// 调整期预算收入报表
//		String TrDrawBack = (String) requestreport5001.get("TrDrawBack");// 调整期退库报表
//		String TrRemove = (String) requestreport5001.get("TrRemove");// 调整期调拨收入报表
//		String TrShare = (String) requestreport5001.get("TrShare");// 调整期共享分成报表
//		String Stock = (String) requestreport5001.get("Stock");// 库存日报

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
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, (String) headMap
				.get("SRC"), reportdate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER),  MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_RECV+(String) headMap.get("MsgNo"));

		// 写发送日志
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, (String) headMap
				.get("SRC"), (String) headMap.get("DES"), reportdate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER),  MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_SEND+(String) headMap.get("MsgNo"));
		
		// 记录接收财政的消息记录--广西MQ消息ID匹配机制修改
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendnode, orgcode, msgNo, msgid, "", "", jmsMessageID, jmsCorrelationID, "");

		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

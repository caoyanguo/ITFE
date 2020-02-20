/**
 * 财政申请入库流水
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
 * @author wangtuo
 * 
 */
public class Recv5002MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Recv5002MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		// 报文的处理方式 1 手工 2 MQ
		String bankInput = (String) eventContext.getMessage().getProperty(
				"BANK_INPUT");
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// 解析报文头 headMap
		String orgcode = (String) headMap.get("DES");// 接收机构代码
		String sendnode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号

		HashMap requestbill5002 = (HashMap) msgMap.get("RequestBill5002");

		// 解析请求信息 msgMap -- > RequestBill5002
		String sendorgcode = (String) requestbill5002.get("SendOrgCode");// 发起机构代码
		String intredate = (String) requestbill5002.get("InTreDate");// 入库日期

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
			throw new ITFEBizException("取接收/发送日志SEQ出错");
		}

		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, (String) headMap
				.get("SRC"), intredate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_RECV);

		// 写发送日志
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, (String) headMap
				.get("SRC"), (String) headMap.get("DES"), intredate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_SEND);
		// 设置消息体
		eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_5002);
		// 记录接收财政的消息记录--广西MQ消息ID匹配机制修改
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendnode, orgcode, msgNo, msgid, "", "", jmsMessageID, jmsCorrelationID, "");
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

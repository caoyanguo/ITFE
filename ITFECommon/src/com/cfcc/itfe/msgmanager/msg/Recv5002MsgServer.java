/**
 * �������������ˮ
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
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		// ���ĵĴ���ʽ 1 �ֹ� 2 MQ
		String bankInput = (String) eventContext.getMessage().getProperty(
				"BANK_INPUT");
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// ��������ͷ headMap
		String orgcode = (String) headMap.get("DES");// ���ջ�������
		String sendnode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��

		HashMap requestbill5002 = (HashMap) msgMap.get("RequestBill5002");

		// ����������Ϣ msgMap -- > RequestBill5002
		String sendorgcode = (String) requestbill5002.get("SendOrgCode");// �����������
		String intredate = (String) requestbill5002.get("InTreDate");// �������

		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡ����/������־SEQ����");
		}

		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, (String) headMap
				.get("SRC"), intredate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_RECV);

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, (String) headMap
				.get("SRC"), (String) headMap.get("DES"), intredate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_SEND);
		// ������Ϣ��
		eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_5002);
		// ��¼���ղ�������Ϣ��¼--����MQ��ϢIDƥ������޸�
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendnode, orgcode, msgNo, msgid, "", "", jmsMessageID, jmsCorrelationID, "");
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

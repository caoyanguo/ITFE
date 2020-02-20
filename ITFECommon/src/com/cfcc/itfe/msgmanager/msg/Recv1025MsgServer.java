/**
 * ���ջ������뱨��1025��
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
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		HashMap requesthead1025 = (HashMap) msgMap.get("RequestHead1025");

		// ��������ͷ headMap
		String orgcode = (String) headMap.get("DES");// ���ջ�������
		// String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��

		// �������������ձ�������ͷ msgMap --> RequestHead1025
		String sendorgcode = (String) requesthead1025.get("SendOrgCode");// ���ͻ�������
		String reportdate = (String) requesthead1025.get("ReportDate");// ��������
		String reportarea = (String) requesthead1025.get("ReportArea");// ����Χ

		// �������豨�� msgMap --> RequestReport1025
		HashMap requestreport1025 = (HashMap) msgMap.get("RequestReport1025");

		String NrBudget = (String) requestreport1025.get("NrBudget");// ������Ԥ�����뱨��
		String NrDrawBack = (String) requestreport1025.get("NrDrawBack");// �������˿ⱨ��
		String NrRemove = (String) requestreport1025.get("NrRemove");// �����ڵ������뱨��
		String Amount = (String) requestreport1025.get("Amount");// �ܶ�ֳɱ���
		String NrShare = (String) requestreport1025.get("NrShare");// �����ڹ���ֳɱ���
		String TrBudget = (String) requestreport1025.get("TrBudget");// ������Ԥ�����뱨��
		String TrDrawBack = (String) requestreport1025.get("TrDrawBack");// �������˿ⱨ��
		String TrRemove = (String) requestreport1025.get("TrRemove");// �����ڵ������뱨��
		String TrShare = (String) requestreport1025.get("TrShare");// �����ڹ���ֳɱ���
		String Stock = (String) requestreport1025.get("Stock");// ����ձ�

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
			throw new ITFEBizException("ȡ������־SEQ����");
		}

		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, (String) headMap.get("SRC"), 
				reportdate, (String) headMap.get("MsgNo"),
				sendorgcode, (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV+msgNo);

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, (String) headMap.get("SRC"), 
				(String) headMap.get("DES"), reportdate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND+msgNo);

		// ��¼���ղ�������Ϣ��¼--����MQ��ϢIDƥ������޸�
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, orgcode, msgNo, msgid, TimeFacade.getCurrentStringTime(), "", jmsMessageID, jmsCorrelationID, sendorgcode);
		
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

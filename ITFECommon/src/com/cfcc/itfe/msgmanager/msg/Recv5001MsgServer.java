/**
 * ���������ձ������뱨��
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
	 * ������Ϣ����
	 * 
	 * @param eventContext
	 * @throws ITFEBizException
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		HashMap requesthead5001 = (HashMap) msgMap.get("RequestHead5001");

		// ��������ͷ headMap
		String orgcode = (String) headMap.get("DES");// ���ջ�������
		String sendnode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��

		// �������������ձ�������ͷ msgMap --> RequestHead5001
		String sendorgcode = (String) requesthead5001.get("SendOrgCode");// ���ͻ�������
		String reportdate = (String) requesthead5001.get("ReportDate");// ��������

		// �������豨�� msgMap --> requestreport5001
		HashMap requestreport5001 = (HashMap) msgMap.get("RequestReport5001");

//		/*
//		 * ת�����ģ����ݲ�������
//		 */
//		String NrBudget = (String) requestreport5001.get("NrBudget");// ������Ԥ�����뱨��
//		String NrDrawBack = (String) requestreport5001.get("NrDrawBack");// �������˿ⱨ��
//		String NrRemove = (String) requestreport5001.get("NrRemove");// �����ڵ������뱨��
//		String Amount = (String) requestreport5001.get("Amount");// �ܶ�ֳɱ���
//		String NrShare = (String) requestreport5001.get("NrShare");// �����ڹ���ֳɱ���
//		String TrBudget = (String) requestreport5001.get("TrBudget");// ������Ԥ�����뱨��
//		String TrDrawBack = (String) requestreport5001.get("TrDrawBack");// �������˿ⱨ��
//		String TrRemove = (String) requestreport5001.get("TrRemove");// �����ڵ������뱨��
//		String TrShare = (String) requestreport5001.get("TrShare");// �����ڹ���ֳɱ���
//		String Stock = (String) requestreport5001.get("Stock");// ����ձ�

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
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, (String) headMap
				.get("SRC"), reportdate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER),  MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_RECV+(String) headMap.get("MsgNo"));

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, (String) headMap
				.get("SRC"), (String) headMap.get("DES"), reportdate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, sendorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER),  MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_SEND+(String) headMap.get("MsgNo"));
		
		// ��¼���ղ�������Ϣ��¼--����MQ��ϢIDƥ������޸�
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendnode, orgcode, msgNo, msgid, "", "", jmsMessageID, jmsCorrelationID, "");

		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

/**
 * ���ջ��ء��籣�������������ˮ��1024��
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

/**
 * @author wangtuo
 * 
 */
public class Recv1024MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv1024MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// ��������ͷ headMap
		String orgcode = (String) headMap.get("DES");// ���ջ�������
		String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��

		HashMap requestbill1024 = (HashMap) msgMap.get("RequestBill1024");

		// ����������Ϣ msgMap -- > RequestBill5002
		String billorgcode = (String) requestbill1024.get("SendOrgCode");// �����������
		String intredate = (String) requestbill1024.get("InTreDate");// �������

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
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sendorgcode, intredate, msgNo,
				sendorgcode, (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, billorgcode, null,
				msgid,
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV+msgNo);

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, sendorgcode, (String) headMap.get("DES"), intredate,
				msgNo, (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, billorgcode, null,
				msgid,
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND+msgNo);
		
		// ��¼���ղ�������Ϣ��¼--����MQ��ϢIDƥ������޸�
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, orgcode, msgNo, msgid, TimeFacade.getCurrentStringTime(), "", jmsMessageID, jmsCorrelationID, billorgcode);
		
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

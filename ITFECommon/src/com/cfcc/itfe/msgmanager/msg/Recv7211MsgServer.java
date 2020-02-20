package com.cfcc.itfe.msgmanager.msg;


import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

public class Recv7211MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv7211MsgServer.class);

	/**
	 * ���ղ���ϵͳ��9005�������ӱ��ģ�ת����Tips
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		HashMap cfxMap = (HashMap) muleMessage.getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
//		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		
		// ��������ͷ headMap
		String recvnode = (String) headMap.get("DES");// ���ջ�������
		String sendnode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��

		String _srecvno = null;
		String _ssendno = null;
		try {
			_srecvno = StampFacade.getStampSendSeq("JS");
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("ȡ���ջ��߷�����ˮ�Ŵ���!", e1);
			throw new ITFEBizException("ȡ���ջ��߷�����ˮ�Ŵ���!", e1);
		}

		// �ǽ�����־
		MsgLogFacade.writeRcvLog(_srecvno, _ssendno, (String) headMap
				.get("SRC"), TimeFacade.getCurrentStringTime(),
				(String) headMap.get("MsgNo"), (String) headMap.get("SRC"),
				(String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH"), 0, null, null, null, null,
				null, null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV+(String) headMap.get("MsgNo"));

		// д������־
		MsgLogFacade.writeSendLog(_ssendno, _srecvno, (String) headMap
				.get("SRC"), (String) headMap.get("DES"), TimeFacade
				.getCurrentStringTime(), (String) headMap.get("MsgNo"),
				(String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH"), 0, null, null, null, null,
				null, null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND+(String) headMap.get("MsgNo"));
		// ��¼���ղ�������Ϣ��¼
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendnode, recvnode, msgNo, msgid, TimeFacade.getCurrentStringTime(), "", jmsMessageID, jmsCorrelationID, "");
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);

	}
}

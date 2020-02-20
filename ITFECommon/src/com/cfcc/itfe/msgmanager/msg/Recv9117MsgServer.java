package com.cfcc.itfe.msgmanager.msg;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

public class Recv9117MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9117MsgServer.class);

	/**
	 * ֧���˶԰��ط�����9117�� �ò���ϵͳδ�յ�֧���˶԰�3200ʱ������TIPS���·���
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();

		HashMap cfxMap = (HashMap) muleMessage.getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap getMsg9117Map = (HashMap) msgMap.get("GetMsg9117");

		// ���������е�������ϢCFX->MSG->GetMsg9117
		String sendOrgCode = (String) getMsg9117Map.get("SendOrgCode"); // �����������
		String ntrustDate = (String) getMsg9117Map.get("EntrustDate"); // ί������
		String oriPackMsgNo = (String) getMsg9117Map.get("OriPackMsgNo");// ԭ�����ı��
		String oriChkDate = (String) getMsg9117Map.get("OriChkDate");// ԭ�˶�����
		String oriPackNo = (String) getMsg9117Map.get("OriPackNo");// ԭ����ˮ��
		String orgType = (String) getMsg9117Map.get("OrgType");// ��������

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
				.get("DES"), ntrustDate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0, null, null, null,
				null, sendOrgCode, null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, null);

		// д������־
		MsgLogFacade.writeSendLog(_ssendno, _srecvno, (String) headMap
				.get("SRC"), (String) headMap.get("DES"), ntrustDate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				null, null, null, null, sendOrgCode, null, (String) headMap
						.get("MsgID"), DealCodeConstants.DEALCODE_ITFE_SEND,
				null, null, (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, null);

		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

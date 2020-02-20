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
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

public class Recv9103MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9103MsgServer.class);

	/**
	 * 9103ǿ���˳�֪ͨ ����ֻ��д������־��������ͷ���
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();

		HashMap cfxMap = (HashMap) muleMessage.getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap forceLogout9103Map = (HashMap) msgMap.get("ForceLogout9103");

		// ���������е�������ϢCFX->MSG->ForceLogout9103
		String forceQuitReason = (String) forceLogout9103Map
				.get("ForceQuitReason");
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
		MsgLogFacade.writeRcvLog(_srecvno, null, (String) headMap.get("DES"),
				TimeFacade.getCurrentStringTime(), (String) headMap
						.get("MsgNo"), (String) headMap.get("SRC"),
				(String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH"), 0, null, null, null, null, null,
				null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, null);

		// ������һ������
		eventContext.setStopFurtherProcessing(true);

	}

}

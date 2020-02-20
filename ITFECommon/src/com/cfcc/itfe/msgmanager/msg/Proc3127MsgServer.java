/**
 * ���ջ��ر���
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

/**
 * @author wangtuo
 * 
 */
public class Proc3127MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3127MsgServer.class);

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
		String msgref = (String) headMap.get("MsgRef"); // ����id��

		/**
		 * ��������ͷ��Ϣ msgMap-->BillHead3127
		 */
		// ����ͷ
		HashMap BillHead3127 = (HashMap) msgMap.get("BillHead3127");

		String ReportDate = (String) BillHead3127.get("ReportDate"); // ������������

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
				.get("DES"), ReportDate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, null, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, null);
		// ������һ������
		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

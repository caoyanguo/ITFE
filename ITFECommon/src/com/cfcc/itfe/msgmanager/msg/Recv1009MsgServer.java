/**
 * �����걨��ѯ��ִ1009
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
 * @author renqingbin
 * 
 */
public class Recv1009MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv1009MsgServer.class);

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

		HashMap realHead1009 = (HashMap) msgMap.get("RealHead1009");
		HashMap payment1009 = (HashMap) msgMap.get("Payment1009");
		// ��������ͷ headMap
		String orgcode = (String) headMap.get("DES");// ���ջ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��

		// ���������걨��ִmsgMap --> RequestHead5001
		String bankno = (String) realHead1009.get("BankNo");// ���д���
		String entrustDate = (String) realHead1009.get("OriEntrustDate");// ί������
		String taxOrgCode = (String) payment1009.get("TaxOrgCode");// ���ջ���
		/*
		 * ת�����ģ����ݲ�������
		 */

		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		String bookorgcode;// �����������
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡ������־SEQ����");
		}

		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, (String) headMap
				.get("SRC"), entrustDate, msgNo, orgcode, 
				(String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, bankno, taxOrgCode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER),  
				MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_RECV+msgNo);

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, orgcode,
				(String) headMap.get("DES"), entrustDate,msgNo, 
				(String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, bankno, taxOrgCode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER),  
				MsgConstant.OTHER_SEND,MsgConstant.LOG_ADDWORD_SEND+msgNo);

		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

/**
 * ����������֤�볷��
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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * @author wangtuo
 * 
 */
public class Recv9114MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9114MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// ��������ͷ headMap
		String desorgcode = (String) headMap.get("DES");// ���ջ�������
		String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��
		String sbookorgcode=sendorgcode;
		
		HashMap proveinfo9114 = (HashMap) msgMap.get("ProveInfo9114");
		
		String billOrgCode = (String) proveinfo9114.get("SendOrgCode"); //���𷽴���
		String EntrustDate = (String) proveinfo9114.get("EntrustDate");//ί������
		String VCNo = (String) proveinfo9114.get("VCNo");//��֤�������
		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(billOrgCode);
			if (null!=_dto) {
				sbookorgcode =_dto.getSorgcode();
			}
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡ����/������־SEQ����");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("�������ջ��ش���ȡ��������������");
		}
		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode, EntrustDate, msgNo,
				billOrgCode, (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), VCNo, null, null, billOrgCode, null,msgid,
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV+msgNo);

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, sbookorgcode, desorgcode, EntrustDate,
				msgNo, (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), VCNo, null, null, billOrgCode, null,
				msgid,
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND+msgNo);
		
		// ��¼���ղ�������Ϣ��¼--����MQ��ϢIDƥ������޸�
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, desorgcode, msgNo, msgid, TimeFacade.getCurrentStringTime(), "", jmsMessageID, jmsCorrelationID, billOrgCode);
		
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

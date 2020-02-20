/**
 * ʵʱ��˰��������
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvTaxCancelDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * @author wangtuo
 * 
 */
public class Recv1021MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Recv1021MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap rushapply1021 = (HashMap) msgMap.get("RushApply1021");
	

		// ��������ͷ headMap
		String orgcode = (String) headMap.get("DES");// ���ջ�������
		String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��
		String sbookorgcode=sendorgcode;

		// ��������������Ϣ msgMap-->rushapply1021
		String taxorgcode = (String) rushapply1021.get("TaxOrgCode");// ���ջ��ش���
		String entrustdate = (String) rushapply1021.get("EntrustDate");// ί������
		String cancelno = (String) rushapply1021.get("CancelNo");// ���������
		String orientrustdate = (String) rushapply1021.get("OriEntrustDate");// ԭί������
		String oritrano = (String) rushapply1021.get("OriTraNo");// ԭ������ˮ��
		String cancelreason = (String) rushapply1021.get("CancelReason");// ����ԭ��

		String dealCode = StateConstant.DEAL_CODE_0000;// Ĭ�ϳɹ�
		String detailDealCode = StateConstant.DATA_FLAG_CHECK;// Ĭ�Ͻ��ճɹ�
		String cancelanswer = StateConstant.CANCELANSWER_FLAG_NOCHECK;// Ĭ��δӦ��

		// ��֯DTo׼����������******************************************************
		// ʵʱ������Ϣ(1021) TvTaxCancelDto
		TvTaxCancelDto taxcanceldto = new TvTaxCancelDto();

		String bizseq;// ҵ����ˮ��
		try {
			bizseq = StampFacade.getBizSeq("SSCZ");
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡҵ����ˮ��SEQ����");
		}
		taxcanceldto.setSseq(bizseq);// ҵ����ˮ��
		taxcanceldto.setStaxorgcode(taxorgcode);// ���ջ��ش���
		taxcanceldto.setSentrustdate(entrustdate);// ί������
		taxcanceldto.setScancelappno(cancelno);// ���������
		taxcanceldto.setSmsgid(msgid);// ���ı�ʶ��
		taxcanceldto.setSorientrustdate(orientrustdate);// ԭί������
		taxcanceldto.setSoritrano(oritrano);// ԭ������ˮ��
		taxcanceldto.setScancelreason(cancelreason);// ����ԭ��
		taxcanceldto.setCcancelanswer(cancelanswer);// ����Ӧ��
		taxcanceldto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// ����ʱ�䲻��Ϊ��

		// ����ʵʱ������Ϣ���� ,������dto�ǿյ����
		if (null != taxcanceldto) {
			try {
				// ��������
				DatabaseFacade.getDb().create(taxcanceldto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("���ݿ����", e);
			}
		}

		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(taxorgcode);
			if (null!=_dto) {
				sbookorgcode =_dto.getSorgcode();
			}
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡ������־SEQ����");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("���ݲ�������ȡ�����������ʧ�ܣ�");
		}

		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode, entrustdate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), cancelno, null, null, taxorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV);

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, sbookorgcode, (String) headMap.get("DES"), entrustdate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, taxorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND);
		
		// ��¼���ղ�������Ϣ��¼--����MQ��ϢIDƥ������޸�
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, orgcode, msgNo, msgid, TimeFacade.getCurrentStringTime(), "", jmsMessageID, jmsCorrelationID, taxorgcode);

		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

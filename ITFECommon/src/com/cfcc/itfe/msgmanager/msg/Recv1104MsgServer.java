package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


/**
 *�����ı���漰��1104�˿�-���������˿�ת��tips��
 * 
 * @author zhouchuan
 * 
 */
public class Recv1104MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv1104MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// ��������ͷ headMap
		String orgcode = null;
			//(String) headMap.get("DES");// ���ջ�������
		String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��


		/**
		 * ��������ҵ��ͷ msgMap-->BatchHead1102,����ֵbatchtaxdto
		 */
		HashMap batchhead1104 = (HashMap) msgMap.get("BatchHead1104");
		// ���ջ��ش���
		String taxorgcode = (String) batchhead1104.get("TaxOrgCode");
		// ί������
		String entrustdate = (String) batchhead1104.get("EntrustDate");
		// ����ˮ��
		String packno = (String) batchhead1104.get("PackNo");
		// �ܱ���
		Integer allnum = Integer.valueOf((String) batchhead1104.get("AllNum"));
		// �ܽ��
		BigDecimal allamt = MtoCodeTrans.transformBigDecimal(batchhead1104.get("AllAmt"));
		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		TsConvertfinorgDto finorgdto = new TsConvertfinorgDto();//���ݲ�������ȡ�����������
		finorgdto.setSfinorgcode(taxorgcode);
		
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
			List<TsConvertfinorgDto> orglist = CommonFacade.getODB().findRsByDto(finorgdto);
			if(orglist!=null&&orglist.size()>0){
				orgcode = orglist.get(0).getSorgcode();
			}else{
				orgcode = sendorgcode;
			}
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡ������־SEQ����");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("���ݲ��������ѯ�����������");
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("���ݲ��������ѯ�����������");
		}
		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, orgcode, entrustdate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, null, null,
				taxorgcode, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV);

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, orgcode, (String) headMap.get("DES"), entrustdate,
				(String) headMap.get("MsgNo"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, null,
				null, taxorgcode, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
		// ��¼���ղ�������Ϣ��¼--����MQ��ϢIDƥ������޸�
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, orgcode, msgNo, msgid, entrustdate, packno, jmsMessageID, jmsCorrelationID, taxorgcode);
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}	


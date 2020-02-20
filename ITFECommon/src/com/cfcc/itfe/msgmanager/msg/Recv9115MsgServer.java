/**
 * ����Э����֤�볷��Ӧ��9115��
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.deptone.common.core.exception.MessageException;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * @author wangtuo
 * 
 */
public class Recv9115MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9115MsgServer.class);

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
		String MsgRef = (String) headMap.get("MsgRef");// ���Ĳο���
		String sbookorgcode = desorgcode;
		
		HashMap ProveReturn9115 = (HashMap) msgMap.get("ProveReturn9115");
		
		String OriSendOrgCode = (String) ProveReturn9115.get("OriSendOrgCode"); //���𷽴���
		String OriEntrustDate = (String) ProveReturn9115.get("OriEntrustDate");
		String OriVCNo = (String) ProveReturn9115.get("OriVCNo");
//		String VCResult = (String) ProveReturn9115.get("VCResult");
		String AddWord = (String) ProveReturn9115.get("AddWord");
		

		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(OriSendOrgCode);
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
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode, OriEntrustDate, msgNo,
				sendorgcode, (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), OriVCNo, null, null, OriSendOrgCode, null,msgid,
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV_TIPS+msgNo+"  ["+AddWord+"] ");

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno,  sbookorgcode, OriSendOrgCode,OriEntrustDate,
				msgNo, (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), OriVCNo, null, null, OriSendOrgCode, null,
				msgid,
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND+msgNo+"  ["+AddWord+"] ");
//		if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
//		{
			// ���ԭ���ģ����·���
			TvMqmessageDto dto = null;
			String correlationId = "ID:524551000000000000000000000000000000000000000000";
			if(MsgRef!=null&&!"".equals(MsgRef))
				dto = MsgLogFacade.queryMQMSGID("9114", MsgRef);
			if(dto==null){
				eventContext.getMessage().setCorrelationId(correlationId);
			}else{
				correlationId = dto.getSmqmsgid();
				eventContext.getMessage().setCorrelationId(correlationId);
			}
			try {
				Object msg = eventContext.getMessage().getProperty("MSG_INFO");// ���ԭ���ģ����·���
//				if(MsgConstant.BATCH_MSG_NO.contains("9114")){//���������еĻ�ִ���͵���������
//					JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_BATCH, (String)msg, correlationId, false);
//				}else{
				if(desorgcode!=null&&desorgcode.contains("000002700009"))
					JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, correlationId, false,desorgcode);
				else
					JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, correlationId, false,desorgcode);
//				}
				eventContext.setStopFurtherProcessing(true);// ������һ������
			} catch (MessageException e) {
				logger.error(e);
				throw new ITFEBizException("����yak���ͣ��Է��ͱ�������ʱЧ��ʧ�ܣ�",e);
			}
//		}
	}
}

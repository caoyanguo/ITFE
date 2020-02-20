/**
 * ʵʱ��˰������ִ
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.deptone.common.core.exception.MessageException;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvTaxCancelDto;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangtuo
 * 
 */
public class Recv2021MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv1021MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap rushreturn2021 = (HashMap) msgMap.get("RushReturn2021");

		// ��������ͷ headMap
		String orgcode = (String) headMap.get("DES");// ���ջ�������
//		String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
//		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��
		String msgref = (String) headMap.get("MsgRef"); // ����id��
		String sbookorgcode=orgcode;

		// ��������Ӧ����Ϣ msgMap -->RushReturn2021
		String taxorgcode = (String) rushreturn2021.get("TaxOrgCode");// ���ջ��ش���
		String oricanceldate = (String) rushreturn2021.get("OriCancelDate");// ԭ��������ί������
		String oricancelno = (String) rushreturn2021.get("OriCancelNo");// ԭ�����������
		String oritrano = (String) rushreturn2021.get("OriTraNo");// ԭ������ˮ��
		String orientrustdate = (String) rushreturn2021.get("OriEntrustDate");// ԭί������
		String cancelanswer = (String) rushreturn2021.get("CancelAnswer");// ����Ӧ��
		String addword = (String) rushreturn2021.get("AddWord");// ����

		// ��֯DTo׼����������******************************************************

		// ������Ϣ��ѯdto TvTaxCancelDto cancelquerydto
		TvTaxCancelDto cancelquerydto = new TvTaxCancelDto();
		cancelquerydto.setStaxorgcode(taxorgcode);// ���ջ��ش���
		cancelquerydto.setScancelappno(oricancelno);// ���������
		cancelquerydto.setSorientrustdate(orientrustdate);// ԭί������

		// �������� ,������dto�ǿյ����
		if (null != cancelquerydto) {
			try {
				// ��ѯ���������Ӧ��Ϣ
				List<TvTaxCancelDto> cancellist = CommonFacade.getODB()
						.findRsByDto(cancelquerydto);

				if (null != cancellist && 0 != cancellist.size()) {
					// ��ִ״̬����
					// ���¶�Ӧ������Ϣ --����Ӧ��״̬
					// ������Ϣ����dto TvTaxCancelDto updatedto
					TvTaxCancelDto updatedto = new TvTaxCancelDto();
					updatedto.setSseq(cancellist.get(0).getSseq());// ���ջ��ش���
					updatedto.setStaxorgcode(taxorgcode);// ���ջ��ش���
					updatedto.setScancelappno(oricancelno);// ���������
					updatedto.setSorientrustdate(orientrustdate);// ԭί������
					updatedto.setSentrustdate(oricanceldate);// ί������
					updatedto.setSmsgid(msgid);// ���ı�ʶ��
					updatedto.setSoritrano(oritrano);// ԭ������ˮ��
					updatedto.setCcancelanswer(cancelanswer);// ����Ӧ��
					updatedto.setScancelreason(cancellist.get(0)
							.getScancelreason());// ����ԭ��
					updatedto.setTsupdate(new Timestamp(new java.util.Date()
							.getTime()));// ����ʱ��
					updatedto.setSaddword(addword);// ����
					/*
					 * ���¶�Ӧ������Ϣ --����Ӧ��״̬������ʱ�䡢����
					 */
					DatabaseFacade.getDb().update(updatedto);
				}

			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("�������ݿ����", e);
			} catch (ValidateException e) {
				logger.error(e);
				throw new ITFEBizException("�������ݿ����", e);
			}
		}
		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ��
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(taxorgcode);
			if (null!=_dto) {
				sbookorgcode =_dto.getSorgcode();
			}
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡ����/������־SEQ����");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("���ݲ�������ȡ�����������ʧ�ܣ�");
		}
		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode, orientrustdate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), oricancelno, null, null, taxorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV_TIPS);

		// �ж��Ƿ���Ҫת�������������Ϊ2��ת�����������Ƿ�����־
		TvSendlogDto tvsendlog = null;
		List tvsendloglist = null;
		try {
			tvsendlog = new TvSendlogDto();
			tvsendlog.setSbillorg(taxorgcode);
			tvsendlog.setSdate(orientrustdate);
			tvsendlog.setSpackno(oricancelno);
			tvsendloglist = CommonFacade.getODB().findRsByDto(tvsendlog);
		    if (null!=tvsendloglist && tvsendloglist.size()> 0 ) {
		    	tvsendlog = (TvSendlogDto) tvsendloglist.get(0);
				tvsendlog.setSretcode(DealCodeConstants.DEALCODE_TIPS_SUCCESS);
				tvsendlog.setSdemo(addword);
				tvsendlog
						.setSproctime(new Timestamp(new java.util.Date().getTime()));
				DatabaseFacade.getDb().update(tvsendlog);
			} 
		} catch (JAFDatabaseException e1) {
			logger.error("���·�����־����״̬�������ݿ��쳣!", e1);
			throw new ITFEBizException("���·�����־����״̬�������ݿ��쳣!", e1);
		} catch (ValidateException e) {
			logger.error("���·�����־����״̬�������ݿ��쳣!", e);
			throw new ITFEBizException("���·�����־����״̬�������ݿ��쳣!", e);
		}
		if (StateConstant.MSG_SENDER_FLAG_2.equals(tvsendlog.getSifsend())) {
			// д������־
			MsgLogFacade.writeSendLog(sendseqno, recvseqno, sbookorgcode, taxorgcode, orientrustdate,
					(String) headMap.get("MsgNo"), (String) eventContext
							.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
					new BigDecimal(0), oricancelno, null, null, taxorgcode, null,
					(String) headMap.get("MsgID"),
					DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND);
			Object msg = eventContext.getMessage().getProperty("MSG_INFO");// ���ԭ���ģ����·���
			if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
			{
				//ȡ��ԭ�����ĵ�MQMSGID
				TvMqmessageDto dto = MsgLogFacade.queryMQMSGID("1021", msgref);
				String correlationId = "ID:524551000000000000000000000000000000000000000000";
				if(dto==null){
					eventContext.getMessage().setCorrelationId(correlationId);
				}else{
					correlationId = dto.getSmqmsgid();
					eventContext.getMessage().setCorrelationId(correlationId);
				}
				try {
					String orimsgno = "1021";
					if(MsgConstant.BATCH_MSG_NO.contains(orimsgno)){//���������еĻ�ִ���͵���������
						if(orgcode!=null&&orgcode.startsWith("000002700009"))
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_BATCHCITY, (String)msg, correlationId, false,orgcode);
						else
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_BATCH, (String)msg, correlationId, false,orgcode);
					}else{
						if(orgcode!=null&&orgcode.contains("000002700009"))
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, correlationId, false,orgcode);
						else
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, correlationId, false,orgcode);
					}
					eventContext.setStopFurtherProcessing(true);// ������һ������
				} catch (MessageException e) {
					logger.error(e);
					throw new ITFEBizException("����yak���ͣ��Է��ͱ�������ʱЧ��ʧ�ܣ�",e);
				}
			}else
				eventContext.getMessage().setPayload(msg);
		} else {
			// ������һ������
			eventContext.setStopFurtherProcessing(true);
			return;
		}
	}
}

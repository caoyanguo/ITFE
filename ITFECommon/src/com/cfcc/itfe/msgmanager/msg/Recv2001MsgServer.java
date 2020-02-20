/**
 * ʵʱ��˰��ִ
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
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvTaxDto;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 
 * @author wangtuo
 * 
 */
public class Recv2001MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv2001MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		HashMap singlereturn2001 = (HashMap) msgMap.get("SingleReturn2001");

		// ��������ͷ headMap
		String orgcode = (String) headMap.get("DES");// ���ջ�������
//		String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
//		String msgNo = (String) headMap.get("MsgNo");// ���ı��
//		String msgid = (String) headMap.get("MsgID"); // ����id��
		String msgRefid = (String) headMap.get("MsgRef"); // �ο�����id��
		String sbookorgcode=orgcode;

		/**
		 * ����ʵʱ��˰��ִ��Ϣ msgMap-->SingleReturn2001
		 */
		String oritaxorgcode = (String) singlereturn2001.get("OriTaxOrgCode");// ԭ���ջ��ش���
		String oritrano = (String) singlereturn2001.get("OriTraNo");// ԭ������ˮ��
		String orientrustdate = (String) singlereturn2001.get("OriEntrustDate");// ԭί������
		String taxvouno = (String) singlereturn2001.get("TaxVouNo");// ˰Ʊ����
		String taxdate = (String) singlereturn2001.get("TaxDate");// ��˰����
		String result = (String) singlereturn2001.get("Result");// ������
		String addword = (String) singlereturn2001.get("AddWord");// ����

		// ��֯DTO׼����������******************************************************
		// ʵʱ��˰��ִ��Ϣ TvTaxDto
		TvTaxDto querytaxdto = new TvTaxDto();

		querytaxdto.setStaxorgcode(oritaxorgcode);// ԭ���ջ��ش���
		querytaxdto.setStrano(oritrano);// ԭ������ˮ��
		querytaxdto.setSentrustdate(orientrustdate);// ԭί������

		// �������� ,������dto�ǿյ����
		if (null != querytaxdto) {
			try {
				// ��ѯʵʱ��˰��Ӧ��Ϣ
				List<TvTaxDto> taxlist = CommonFacade.getODB().findRsByDto(
						querytaxdto);
				if (null != taxlist &&  taxlist.size() > 0) {
					/*
					 * ����ʵʱ��˰��ִ����
					 */
					SQLExecutor updateExce = DatabaseFacade.getDb()
							.getSqlExecutorFactory().getSQLExecutor();

					String updateSql = "update "
							+ TvTaxDto.tableName()
							+ " set S_TAXVOUNO = ? , S_PAYDATE = ? ,S_RESULT = ? ,S_ADDWORD = ? ,S_ACCEPTDATE = ? ,TS_UPDATE = ? "
							+ " where S_TAXORGCODE = ? and S_ENTRUSTDATE = ? and S_TRANO = ?";
					updateExce.addParam(taxvouno);
					updateExce.addParam(taxdate);
					updateExce.addParam(result);
					updateExce.addParam(addword);
					updateExce.addParam(TimeFacade.getCurrentStringTime());
					updateExce.addParam(new Timestamp(new java.util.Date()
							.getTime()));
					updateExce.addParam(oritaxorgcode);
					updateExce.addParam(orientrustdate);
					updateExce.addParam(oritrano);

					updateExce.runQueryCloseCon(updateSql);
				}
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("���ݿ����1", e);
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
			sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(oritaxorgcode);
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
				new BigDecimal(0), oritrano, null, null, oritaxorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV_TIPS);

		// �ж��Ƿ���Ҫת�������������Ϊ2��ת�����������Ƿ�����־
		TvSendlogDto tvsendlog = null;
		List tvsendloglist = null;
		try {
			tvsendlog = new TvSendlogDto();
			tvsendlog.setSbillorg(oritaxorgcode);
			tvsendlog.setSdate(orientrustdate);
			tvsendlog.setSpackno(oritrano);
			tvsendloglist = CommonFacade.getODB().findRsByDto(tvsendlog);
		    if (null!=tvsendloglist && tvsendloglist.size() > 0) {
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
			MsgLogFacade.writeSendLog(sendseqno, recvseqno,sbookorgcode, oritaxorgcode, orientrustdate,
					(String) headMap.get("MsgNo"), (String) eventContext
							.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
					new BigDecimal(0), null, null, null, oritaxorgcode, null,
					(String) headMap.get("MsgID"),
					DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND);
			Object msg = eventContext.getMessage().getProperty("MSG_INFO");// ���ԭ���ģ����·���
			if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
			{
				//ȡ��ԭ�����ĵ�MQMSGID
				TvMqmessageDto dto = MsgLogFacade.queryMQMSGID("1001", msgRefid);
				String correlationId = "ID:524551000000000000000000000000000000000000000000";
				if(dto==null){
					eventContext.getMessage().setCorrelationId(correlationId);
				}else{
					correlationId = dto.getSmqmsgid();
					eventContext.getMessage().setCorrelationId(correlationId);
				}
				try {
//					String orimsgno = "1001";
//					if(MsgConstant.BATCH_MSG_NO.contains(orimsgno)){//���������еĻ�ִ���͵���������
//						JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_BATCH, (String)msg, correlationId, false);
//					}else{
					if(orgcode!=null&&orgcode.contains("000002700009"))
						JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, correlationId, false,orgcode);
					else
						JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, correlationId, false,orgcode);
//					}
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

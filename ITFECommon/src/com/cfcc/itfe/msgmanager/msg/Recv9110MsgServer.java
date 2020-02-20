package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
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
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFreeDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class Recv9110MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9110MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		String msgRef = (String) headMap.get("MsgRef");
		String msgid = (String) headMap.get("MsgID");
		String recvorg = (String) headMap.get("DES");
		String sdate = (String) headMap.get("WorkDate");
		/**
		 * ������Ϣͷ
		 */
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead9110");
		String orimsgno = (String) batchheadMap.get("OriMsgNo"); // ԭ���ı��
		String oriorgcode = (String) batchheadMap.get("OriSendOrgCode"); // ԭ�����������[����ԭ���׵Ļ����Ĵ�]
		String orientrustdate = (String) batchheadMap.get("OriEntrustDate");// ԭί������[�ۿ�����������]
		String oripackno = (String) batchheadMap.get("OriPackNo");// ԭ����ˮ��
//		int allnum = Integer.valueOf((String) (batchheadMap.get("AllNum")));// �ܱ���[���а����Ľ���������Ҫ���ܱ���������1000]
//		BigDecimal allamt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt"));// �ܽ��[�������н��׵��ܽ��]
		String result = (String) batchheadMap.get("Result");// ������
		String addword = (String) batchheadMap.get("AddWord"); // ����
		String sendno = null ;//������ˮ��
		String sbillorg = null;//��Ʊ��λ
		String packno = null ;//ԭ����
		String strecode = null ;//�������
		String sstatus = "";//����״̬
		
		// ������־��ˮ
		String _srecvno = null;
		try {
			_srecvno = StampFacade.getStampSendSeq("JS");
		} catch (SequenceException e1) {
			logger.error("ȡ������ˮ�ų���!", e1);
			throw new ITFEBizException("ȡ������ˮ�ų���!", e1);
		}
		// ����ԭ������־
		TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,
				orimsgno);
		if (null != senddto) {
			// ����ԭ������־��ˮ��
			//��������
			String pkgstate = PublicSearchFacade.getPackageStateByDealCode(result);
			MsgRecvFacade.updateMsgSendLogByMsgId(senddto, pkgstate, _srecvno,
					addword);
			//���°���Ӧ��ϵ��
			MsgRecvFacade.updateMsgHeadByMsgId(msgRef,pkgstate,addword);
			recvorg =senddto.getSsendorgcode();
			sendno = senddto.getSsendno();
			sbillorg = senddto.getSbillorg();
		    packno = senddto.getSpackno();
			strecode = senddto.getStrecode();
			if (StateConstant.MSG_SENDER_FLAG_2.equals(senddto.getSifsend())&&("1001".equals(orimsgno)||"1102".equals(orimsgno))) {
				// д������־
				MsgLogFacade.writeSendLog(_srecvno, sendno,recvorg, oriorgcode, orientrustdate,
						(String) headMap.get("MsgNo"), (String) eventContext
								.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
						new BigDecimal(0), null, null, null, oriorgcode, null,
						(String) headMap.get("MsgID"),
						DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND);
				Object msg = eventContext.getMessage().getProperty("MSG_INFO");// ���ԭ���ģ����·���
				if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
				{
					//ȡ��ԭ�����ĵ�MQMSGID
					TvMqmessageDto dto = MsgLogFacade.queryMQMSGID(orimsgno, msgRef);
					String correlationId = "ID:524551000000000000000000000000000000000000000000";
					if(dto==null){
						eventContext.getMessage().setCorrelationId(correlationId);
					}else{
						correlationId = dto.getSmqmsgid();
						eventContext.getMessage().setCorrelationId(correlationId);
					}
					try {
						if(!"1102".equals(orimsgno)&&!"1104".equals(orimsgno)&&!"1105".equals(orimsgno))
						{	
							if(recvorg!=null&&recvorg.contains("000002700009"))
								JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, correlationId, false,recvorg);
							else
								JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, correlationId, false,recvorg);
							eventContext.setStopFurtherProcessing(true);// ������һ������
						}
						else
						{
							eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");//����������Ϣͷд����id
							eventContext.getMessage().setPayload(msg);
						}
					} catch (MessageException e) {
						logger.error(e);
						throw new ITFEBizException("����yak���ͣ��Է��ͱ�������ʱЧ��ʧ�ܣ�",e);
					}
				}else
					eventContext.getMessage().setPayload(msg);
			}else
				eventContext.setStopFurtherProcessing(true);
		}
		String sendorg = (String) headMap.get("SRC");
		String biztype = (String) headMap.get("MsgNo");
		String filepath = (String) eventContext.getMessage().getProperty(
		"XML_MSG_FILE_PATH");
//        String stamp = TimeFacade.getCurrentStringTime();
        String ifsend = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER);
       // �ǽ�����־
       MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sdate, biztype,
		sendorg, filepath, 0, null, packno, strecode, null,
		sbillorg, null, msgid,
		DealCodeConstants.DEALCODE_ITFE_RECEIVER, addword, null,
		ifsend, "0", addword + "ԭ����ˮ��:" + packno);

		List returnList = (List) msgMap.get("BatchReturn9110");
		if (null == returnList || returnList.size() == 0) {
			return;
		}
		String updateSqlCommon = "";
		/*
		 * �˿�����
		 */
		if(MsgConstant.MSG_NO_1104.equals(orimsgno)){
			updateSqlCommon = "update " + TvDwbkDto.tableName() + " set S_STATUS = ?,S_DEMO=? "
			+ " where S_TAXORGCODE = ? and D_ACCEPT = ? and S_DEALNO = ? and S_PACKAGENO = ? and F_AMT = ? "
			+ " and (S_DWBKVOUCODE = ? or S_ELECVOUNO = ? )";
		}else if(MsgConstant.MSG_NO_1105.equals(orimsgno)){
			//��������
			updateSqlCommon = "update " + TvInCorrhandbookDto.tableName() + " set S_STATUS = ?,S_DEMO=? "
			+ " where S_CURTAXORGCODE = ? and D_ACCEPT = ? and S_DEALNO = ? and S_PACKAGENO = ? and F_CURCORRAMT = ? "
			+ " and (S_CORRVOUNO = ? or S_ELECVOUNO = ? )";
		}else if(MsgConstant.MSG_NO_1106.equals(orimsgno)) {
			//��ֵ�
			updateSqlCommon = "update " + TvFreeDto.tableName() + " set S_STATUS = ?,S_ADDWORD=? "
			+ " where S_TAXORGCODE = ? and D_ACCEPTDATE = ? and S_TRANO = ? and S_PACKNO = ? and F_FREEPLUAMT = ? "
			+ " and (S_FREEVOUNO = ? or S_ELECTROTAXVOUNO = ? )";
		}
		
		if(!updateSqlCommon.equals("")){
			SQLExecutor updateExce = null;
			try {
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				int count = returnList.size();
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();

					HashMap returnmap = (HashMap) returnList.get(i);
					String oritrano = (String) returnmap.get("OriTraNo"); // ԭ������ˮ��[��ҵ������д������ԭ������˰���ѣ�������ϸ�е�˰���ѣ�Ʊ������ˮ��]
					BigDecimal traamt = MtoCodeTrans.transformBigDecimal(returnmap.get("TraAmt"));// ���׽��[�����Ľ��]
					String papertaxvouno = (String) returnmap.get("PaperTaxVouNo");// ӡˢƾ֤����[��Ʊ����]
					String electortaxvouno = (String) returnmap.get("ElectroTaxVouNo"); // ����ƾ֤����
					String returnresult = (String) returnmap.get("Result");// ������
					String addWord = MtoCodeTrans.transformString(returnmap.get("AddWord"));// ����
					
					sstatus = PublicSearchFacade.getDetailStateByDealCode(returnresult);

					updateExce.addParam(sstatus);
					updateExce.addParam(addWord);
					updateExce.addParam(oriorgcode);
					updateExce.addParam(CommonUtil.strToDate(orientrustdate));
					updateExce.addParam(oritrano);
					updateExce.addParam(oripackno);
					updateExce.addParam(traamt);
					updateExce.addParam(papertaxvouno);
					updateExce.addParam(electortaxvouno);

					updateExce.runQuery(updateSqlCommon);
				}

				updateExce.closeConnection();
				
			} catch (JAFDatabaseException e) {
				String error = "�����˿��������������ִ״̬ʱ�������ݿ��쳣��";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		
		//����ƾ֤������״̬
		try{
			Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
					MsgConstant.VOUCHER);
			voucher.VoucherReceiveTIPSResult(oripackno, sstatus ,addword, MsgConstant.MSG_NO_9110);
		} catch(Exception e){
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		}

		/**
		 * ���ڸñ���ֻ������Խɺ�����ʱ��������Ĵ���
		 */
		if (MsgConstant.MSG_NO_1103.equals(orimsgno)) {
			SQLExecutor updateExce = null;
			try {
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String updateSql = "update " + TvInfileDto.tableName() + " set S_STATUS = ? , S_DEMO = ? "
						+ " where S_TAXORGCODE = ? and S_COMMITDATE = ? and S_DEALNO = ? and S_PACKAGENO = ? and N_MONEY = ? "
						+ " and (S_TAXTICKETNO = ? or S_TAXTICKETNO = ? )";

				int count = returnList.size();
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();

					HashMap returnmap = (HashMap) returnList.get(i);
					String oritrano = (String) returnmap.get("OriTraNo"); // ԭ������ˮ��[��ҵ������д������ԭ������˰���ѣ�������ϸ�е�˰���ѣ�Ʊ������ˮ��]
					BigDecimal traamt = MtoCodeTrans.transformBigDecimal(returnmap.get("TraAmt"));// ���׽��[�����Ľ��]
					String papertaxvouno = (String) returnmap.get("PaperTaxVouNo");// ӡˢƾ֤����[��Ʊ����]
					String electortaxvouno = (String) returnmap.get("ElectroTaxVouNo"); // ����ƾ֤����
					String returnresult = (String) returnmap.get("Result");// ������

					sstatus = PublicSearchFacade.getDetailStateByDealCode(returnresult);

					updateExce.addParam(sstatus);
					updateExce.addParam(returnresult);
					updateExce.addParam(oriorgcode);
					updateExce.addParam(orientrustdate);
					updateExce.addParam(oritrano);
					updateExce.addParam(oripackno);
					updateExce.addParam(traamt);
					updateExce.addParam(papertaxvouno);
					updateExce.addParam(electortaxvouno);

					updateExce.runQuery(updateSql);
				}

				updateExce.closeConnection();
				
			} catch (JAFDatabaseException e) {
				String error = "����˰Ʊ�����ִ״̬ʱ�������ݿ��쳣��";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}

}

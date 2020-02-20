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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;

import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;

import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * ��������TIPS��ĳ��������ת����������ر�����Ϣ
 * ��Ҫ���ܣ����մ������������ִ
 * @author wangyunbin
 * 
 */
public class Recv3136MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3136MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		
		// ����ͷ��ϢCFX->HEAD
		String sorgcode = (String) headMap.get("SRC"); // �����������
		String sdescode = (String) headMap.get("DES");// ���սڵ����
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgID = (String) headMap.get("MsgID");// ���ı�ʶ��
		String msgRef = (String) headMap.get("MsgRef");// ���Ĳο���
		String sdate = (String) headMap.get("WorkDate");// ��������
		
		/**
		 * �����������ͷ MSG->BillHead3136
		 */
		// ����ʵʱҵ��ͷ CFX->MSG->BatchReturn3136
		HashMap batchheadMap = (HashMap) msgMap.get("BatchReturn3136");
		String treCode = (String) batchheadMap.get("TreCode"); // �����������
		String oriBillOrg = (String) batchheadMap.get("OriBillOrg"); // ԭ��Ʊ��λ
		String entrustDate = (String) batchheadMap.get("EntrustDate"); // ί������
		String packNo = (String) batchheadMap.get("PackNo"); // ����ˮ��
		String oriPackNo = (String) batchheadMap.get("OriPackNo"); // ԭ����ˮ��
		String oriEntrustDate = (String) batchheadMap.get("OriEntrustDate"); // ԭί������
		
		int allNum = Integer.parseInt(batchheadMap.get("AllNum").toString().trim()); // �ܱ���
		BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt")); // �ܽ��
		
		String payoutVouType = (String) batchheadMap.get("PayoutVouType"); // ֧��ƾ֤����

		String vouNo = (String) batchheadMap.get("VouNo"); // ƾ֤���
		String vouDate = (String) batchheadMap.get("VouDate"); // ƾ֤����
		String result = (String) batchheadMap.get("Result"); // ������
		String addWord = (String) batchheadMap.get("AddWord"); // ����
		String sendno = null;
		String sdemo ="ԭ���ı��:"+MsgConstant.MSG_NO_5112+",ԭ����ˮ:"+oriPackNo.trim()+",ԭ�����������"+oriBillOrg.trim()+",ԭί������:"+oriEntrustDate.trim();
		String sbookorgcode = sdescode;
		

		
		SQLExecutor updateExce = null;
		try {
			
			TsTreasuryDto findto = SrvCacheFacade.cacheTreasuryInfo(null).get(treCode);
			if (null!=findto) {
				sbookorgcode =findto.getSorgcode();
			}
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql = "update " + TvPayoutfinanceMainDto.tableName() + " set S_STATUS = ? , S_RESULT = ? "
					+ " where S_BILLORG = ? and S_ENTRUSTDATE  = ? and S_PACKAGENO = ? and N_AMT = ? "
					+ " and S_VOUNO = ? ";
			updateExce.clearParams();
			String sstatus = PublicSearchFacade.getDetailStateByDealCode(result);
			updateExce.addParam(sstatus);
			updateExce.addParam(MtoCodeTrans.transformString(addWord));
			updateExce.addParam(oriBillOrg);
			updateExce.addParam(oriEntrustDate);
			updateExce.addParam(oriPackNo);
			updateExce.addParam(allAmt);
			updateExce.addParam(vouNo);
			updateExce.runQuery(updateSql);
			updateExce.closeConnection();
			//ȡԭ���Ͱ�
			TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(MsgConstant.MSG_NO_5112, oriBillOrg, oriEntrustDate, oriPackNo, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
			//������־��ˮd
			String _srecvno = StampFacade.getStampSendSeq("JS");
			//����ԭ��״̬
			if (null!=senddto) {
				//����ԭ������־��ˮ��
				MsgRecvFacade.updateMsgSendLogByMsgId(senddto, DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno, "");
				sendno = senddto.getSsendno();
				// �������������ļ�����Ķ�Ӧ��ϵ��
				MsgRecvFacade.updateMsgHeadByCon( oriBillOrg, oriPackNo, oriEntrustDate, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
			}
			String filepath = (String) eventContext.getMessage().getProperty(
					"XML_MSG_FILE_PATH");
			String stamp = TimeFacade.getCurrentStringTime();
			String ifsend = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_SENDER);
			// �ǽ�����־
			MsgLogFacade.writeRcvLog(_srecvno, sendno, sbookorgcode, entrustDate, msgNo,
					sorgcode, filepath, allNum, allAmt, packNo, treCode, null,
					oriBillOrg, null, msgID,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, addWord, null,
					ifsend, MsgConstant.ITFE_SEND, sdemo );
		} catch (JAFDatabaseException e) {
			String error = "���½��մ������������ִ״̬ʱ�������ݿ��쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} catch (SequenceException e) {
			String error = "���½��մ������������ִ״̬ʱ�������ݿ��쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

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
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * ���ա������������������а���֧���ޣ��У�ֽƾ֤��ִ(3135) ��Ҫ���ܣ����ܡ�����������3135����
 * 
 * @author zhangxh
 * 
 */
public class Recv3135MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3135MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		/*
		 * ��1�� ��������ͷ��Ϣ����¼������־
		 */
		// ���ĵĴ���ʽ 1 �ֹ� 2 MQ
		String bankInput = (String) eventContext.getMessage().getProperty(
				"BANK_INPUT");
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		int count = 0;

		// ����ͷ��ϢCFX->HEAD
		String sorgcode = (String) headMap.get("SRC"); // �����������
		String sdescode = (String) headMap.get("DES");// ���սڵ����
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgID = (String) headMap.get("MsgID");// ���ı�ʶ��
		String MsgRef = (String) headMap.get("MsgRef");// ���Ĳο���
		String WorkDate = (String) headMap.get("WorkDate");// ��������

		// ����ʵʱҵ��ͷ CFX->MSG->BatchHead3135
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3135");
		String treCode = (String) batchheadMap.get("TreCode"); // �����������
		String billOrg = (String) batchheadMap.get("BillOrg"); // ��Ʊ��λ
		String entrustDate = (String) batchheadMap.get("EntrustDate"); // ί������
		String packNo = (String) batchheadMap.get("PackNo"); // ����ˮ��
		String oriEntrustDate = (String) batchheadMap.get("OriEntrustDate"); // ԭί������
		String oriPackNo = (String) batchheadMap.get("OriPackNo"); // ԭ����ˮ��
		int AllNum = Integer.parseInt(batchheadMap.get("AllNum").toString()
				.trim()); // �ܱ���
		BigDecimal AllAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
				.get("AllAmt")); // �ܽ��
		String payoutVouType = (String) batchheadMap.get("PayoutVouType"); // ֧��ƾ֤����
		String PayMode = (String) batchheadMap.get("PayMode"); // ֧����ʽ
		String sendno = null;
		String recvorg = sdescode;

		/*
		 * ��2�� ������������Ϣ���������ݿ���Ϣ
		 */
		// �����Ϣ CFX->MSG->BatchReturn3135
		SQLExecutor updateExce = null;
		try {
			List BatchReturn3135s = (List) msgMap.get("BatchReturn3135");
			if (null == BatchReturn3135s || BatchReturn3135s.size() == 0) {
				return;
			} else {
				count = BatchReturn3135s.size();

				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();

				String updateSql = "update "
						+ TvPbcpayMainDto.tableName()
						+ " set S_STATUS = ?,S_ADDWORD = ?,D_ACCT =? ,S_DESCRIPTION=?"
						+ " where S_TRECODE = ?  and S_BillORG = ? and S_ENTRUSTDATE = ? and S_PACKNO = ? "
						+ " and S_VOUNO = ?  and S_traNO = ? ";

				for (int i = 0; i < BatchReturn3135s.size(); i++) {
					HashMap BatchReturn3135 = (HashMap) BatchReturn3135s.get(i);
					String VouNo = (String) BatchReturn3135.get("VouNo"); // ƾ֤���
					String VouDate = (String) BatchReturn3135.get("VouDate"); // ƾ֤����
					String OriTraNo = (String) BatchReturn3135.get("OriTraNo"); // ԭ������ˮ��
					BigDecimal Amt = MtoCodeTrans
							.transformBigDecimal(BatchReturn3135.get("Amt")); // �ϼƽ��
					String AcctDate = (String) BatchReturn3135.get("AcctDate"); // ��������
					String Result = (String) BatchReturn3135.get("Result"); // ������
					String Description = (String) BatchReturn3135
							.get("Description"); // ˵��

					// ���ݴ�����ת������״̬
					String sstatus = PublicSearchFacade
							.getDetailStateByDealCode(Result);
					//�Ϻ���ֽ��������5201ҵ�������ƾ֤������״̬
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0)
						VoucherUtil.updateVoucherStatusRecvTCBSDirectMsg(treCode, AcctDate, oriPackNo, VouDate, 
								VouNo, Amt, sstatus, Result, OriTraNo, oriEntrustDate, StateConstant.BIZTYPE_CODE_SINGLE);					
					updateExce.addParam(sstatus);
					updateExce.addParam(Result);
					updateExce.addParam(AcctDate);
					updateExce.addParam(Description);
					updateExce.addParam(treCode);
					updateExce.addParam(billOrg);
					updateExce.addParam(entrustDate);
					updateExce.addParam(oriPackNo);
					updateExce.addParam(VouNo);
					updateExce.addParam(OriTraNo);
					updateExce.runQuery(updateSql);
				}
				updateExce.closeConnection();

				// ȡԭ���Ͱ�
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(
						MsgConstant.MSG_NO_5104, billOrg, entrustDate, packNo,
						DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				// ������־��ˮd
				String _srecvno = StampFacade.getStampSendSeq("JS");
				// ����ԭ��״̬
				if (null != senddto) {
					// ����ԭ������־��ˮ��
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno,
							"");
					sendno = senddto.getSsendno();
					recvorg = senddto.getSsendorgcode();
					// �������������ļ�����Ķ�Ӧ��ϵ��
					MsgRecvFacade.updateMsgHeadByCon(billOrg, packNo,
							entrustDate,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// �ǽ�����־
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg,
						entrustDate, msgNo, sorgcode, filepath,
						BatchReturn3135s.size(), AllAmt, packNo, treCode, null,
						billOrg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, null);

			}

		} catch (JAFDatabaseException e) {
			String error = "����3135���Ĵ���ʧ�ܣ�";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} catch (SequenceException e) {
			String error = "����3135���Ĵ���ʧ�ܣ�";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		}finally{
			if(null != updateExce){
				updateExce.closeConnection();
			}
		}
	}
}

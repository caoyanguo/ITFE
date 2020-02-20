/**
 * ����:�յ����������˻ػ�ִ����
 */
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
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author zhouchuan
 * 
 */
public class Recv3144MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3144MsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		/**
		 * ȡ�ñ���ͷ��Ϣ
		 */
		// ����ͷ��ϢCFX->HEAD
		String desnode = (String) headMap.get("DES");// ���սڵ����
		String srcnode = (String) headMap.get("SRC");// ����ڵ����
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ���ı�ʶ��

		/**
		 * ȡ�û�ִͷ��Ϣ
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead3144");
		String agentBnkCode = (String) batchHeadMap.get("AgentBnkCode"); // ���������к�
		String finorgcode = (String) batchHeadMap.get("FinOrgCode");// �������ش���
		String ls_TreCode = (String) batchHeadMap.get("TreCode"); // �����������
		String entrustdate = (String) batchHeadMap.get("EntrustDate"); // ί������
		String packno = (String) batchHeadMap.get("PackNo"); // ����ˮ��
		String oriEntrustDate = (String) batchHeadMap.get("OriEntrustDate"); // ԭί������
		String oriPackNo = (String) batchHeadMap.get("OriPackNo"); // ԭ����ˮ��
		String PayMode = (String) batchHeadMap.get("PayMode"); // ֧����ʽ
		int count = 0;
		BigDecimal allamt = new BigDecimal("0.00");

		String sdemo = "ԭ���ı��:" + MsgConstant.APPLYPAY_BACK_DAORU + ",ԭ����ˮ:"
				+ oriPackNo.trim() + ",ԭ�����������" + finorgcode.trim() + ",ԭί������:"
				+ oriEntrustDate.trim();
		String sendno = null;
		String recvorg = desnode;

		/**
		 * ȡ�û�ִ������Ϣ
		 */
		List<Object> batchReturnList = (List<Object>) msgMap
				.get("BatchReturn3144");
		String result = null;
		if (null == batchReturnList || batchReturnList.size() == 0) {
			return;
		} else {
			SQLExecutor updateExce = null;
			try {
				/**
				 * ȡ����ƾ֤Ҫ����Ϊ����ȥ������ƾ֤����״̬
				 */
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				count = batchReturnList.size();
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) batchReturnList.get(i);
					String vouno = (String) tmpmap.get("VouNo"); // ƾ֤���
					String voudate = (String) tmpmap.get("VouDate"); // ƾ֤����
					// String orivouno = (String) tmpmap.get("OriVouNo"); //
					// ԭƾ֤���
					String orivoudate = (String) tmpmap.get("OriVouDate"); // ԭƾ֤����
					String soritrano = (String) tmpmap.get("OriTraNo"); // ԭ������ˮ��
					String acctdate = (String) tmpmap.get("AcctDate");// ��������
					String sAmt = (String) tmpmap.get("Amt");// ���
					BigDecimal Amt = MtoCodeTrans.transformBigDecimal(sAmt); // ���
					result = (String) tmpmap.get("Result");// ������
					String description = (String) tmpmap.get("Description");// ˵��

					String demo;

					// ���ݴ�����ת������״̬
					String sstatus = PublicSearchFacade
							.getDetailStateByDealCode(result);
					if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)) {
						demo = DealCodeConstants.PROCESS_SUCCESS;
					} else {
						demo = DealCodeConstants.PROCESS_FAIL;
					}

					// ������Ϻ���ֽ��������ֱ��֧���������տ������˿�֪ͨ2252ҵ�������ƾ֤������״̬
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
							&& PayMode.equals(MsgConstant.directPay))
						VoucherUtil.updateVoucherStatusRecvTCBSBankRefund(
								ls_TreCode, acctdate, oriPackNo, orivoudate,
								vouno, Amt, sstatus, demo, soritrano,
								oriEntrustDate);

					updateExce.addParam(sstatus);
					// ������Ϻ���ֽ����������Ȩ֧��
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
							&& PayMode.equals(MsgConstant.grantPay)) {
						// �ص����� ��д 3144�е�ԭ������ˮ��
						updateExce.addParam(soritrano);
					} else {
						updateExce.addParam(demo);
					}
					updateExce.addParam(Amt);
					updateExce.addParam(CommonUtil.strToDate(acctdate));
					updateExce.addParam(agentBnkCode);// ��XPaySndBnkNo֧���������кš�
					// ��д3144����д���������к�
					updateExce.addParam(agentBnkCode);// �������д���
					updateExce.addParam(vouno);// ԭƾ֤���
					updateExce.addParam(Amt);// ������
					updateExce
							.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// ������״̬
					if (ITFECommonConstant.ISITFECOMMIT
							.equals(StateConstant.COMMON_YES)) {
						updateExce.addParam(oriPackNo);// ԭ����ˮ��
						updateExce.addParam(soritrano);// ԭ������ˮ��
						updateExce
								.runQuery("update "
										+ TvPayreckBankBackDto.tableName()
										+ " set S_STATUS = ? , S_ADDWORD = ? ,S_XPAYAMT = ? ,S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ? "
										+ " where S_AGENTBNKCODE = ? and S_VOUNO = ? and F_AMT = ? and S_STATUS = ? and S_PACKNO = ? and S_TRANO = ?");
					} else {
						updateExce
								.runQuery("update "
										+ TvPayreckBankBackDto.tableName()
										+ " set S_STATUS = ? , S_ADDWORD = ? ,S_XPAYAMT = ? ,S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ? "
										+ " where S_AGENTBNKCODE = ? and S_VOUNO = ? and F_AMT = ? and S_STATUS = ? ");
					}

					try {
						Voucher voucher = (Voucher) ContextFactory
								.getApplicationContext().getBean(
										MsgConstant.VOUCHER);

						voucher.VoucherReceiveTCBS(ls_TreCode,
								MsgConstant.VOUCHER_NO_2302, oriPackNo,
								voudate, vouno, MtoCodeTrans
										.transformBigDecimal("-" + sAmt),
								sstatus, description);
					} catch (Exception e) {
						logger.error(e);
						VoucherException.saveErrInfo(null, e);
					}
				}

				// ȡԭ���Ͱ�
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(
						MsgConstant.APPLYPAY_BACK_DAORU, agentBnkCode,
						oriEntrustDate, oriPackNo,
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
					MsgRecvFacade.updateMsgHeadByCon(finorgcode, oriPackNo,
							oriEntrustDate,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// �ǽ�����־
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg,
						entrustdate, msgNo, srcnode, filepath, batchReturnList
								.size(), allamt, packno, ls_TreCode,
						agentBnkCode, finorgcode, null, msgid,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo);
			} catch (JAFDatabaseException e) {
				String error = "���»��������˿�ҵ���ִ״̬ʱ�������ݿ��쳣��";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "ȡ������ˮ�ŵ�ʱ��������ݿ��쳣��";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (updateExce != null)
					updateExce.closeConnection();
			}
		}
		// if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
		// {
		eventContext.getMessage().setCorrelationId(
				"ID:524551000000000000000000000000000000000000000000");
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
		// }else
		// eventContext.setStopFurtherProcessing(true);
		return;
	}
}

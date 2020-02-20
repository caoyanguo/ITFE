/**
 * ����:�յ����������ִ����
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
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.CommonUtil;
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
public class Recv3143MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3143MsgServer.class);

	@SuppressWarnings( { "unchecked" })
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");

		// ����ͷ��ϢCFX->HEAD
		String sorgcode = (String) headMap.get("SRC"); // �����������
		String sdescode = (String) headMap.get("DES");// ���սڵ����
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgID = (String) headMap.get("MsgID");// ���ı�ʶ��
		// String msgRef = (String) headMap.get("MsgRef");// ���Ĳο���
		// String sdate = (String) headMap.get("WorkDate");// ��������

		/**
		 * ȡ�û�ִͷ��Ϣ
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead3143");

		String strecode = (String) batchHeadMap.get("TreCode"); // �������
		String sfinorgcode = (String) batchHeadMap.get("FinOrgCode");// �������ش���
		String sagentbnkcode = (String) batchHeadMap.get("AgentBnkCode");// ���������к�
		String sentrustDate = (String) batchHeadMap.get("EntrustDate");// ί������
		String spackno = (String) batchHeadMap.get("PackNo"); // ����ˮ��
		String soripackno = (String) batchHeadMap.get("OriPackNo");// ԭ����ˮ��
		String sorientrustdate = (String) batchHeadMap.get("OriEntrustDate");// ԭί������
		// String sallnum = (String) batchHeadMap.get("AllNum");//�ܱ���
		// String sallamt = (String) batchHeadMap.get("AllAmt");//�ܽ��
		// String spayoutvoutype = (String)
		// batchHeadMap.get("PayoutVouType");//֧��ƾ֤����
		String spaymode = (String) batchHeadMap.get("PayMode");// ֧����ʽ

		int count = 0;
		BigDecimal allamt = new BigDecimal("0.00");
		String sdemo = "ԭ���ı��:" + MsgConstant.APPLYPAY_DAORU + ",ԭ����ˮ:"
				+ soripackno.trim() + ",������������" + sfinorgcode.trim()
				+ ",ԭί������:" + sorientrustdate.trim();
		String sendno = null;
		String recvorg = sdescode;

		/**
		 * ȡ�û�ִ������Ϣ
		 */
		List<Object> batchReturnList = (List<Object>) msgMap.get("Bill3143");
		if (null == batchReturnList || batchReturnList.size() == 0) {
			return;
		} else {
			count = batchReturnList.size();
			SQLExecutor updateExce = null;
			try {
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				String updateSql;
				if (ITFECommonConstant.ISITFECOMMIT
						.equals(StateConstant.COMMON_YES)) {
					updateSql = "update "
							+ TvPayreckBankDto.tableName()
							+ " set S_RESULT = ? , S_ADDWORD = ? ,D_ACCTDATE= ? ,S_XPAYAMT = ? , S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ? "
							+ " where S_AGENTBNKCODE = ? and S_VOUNO = ? and F_AMT = ? and S_RESULT = ? and S_PACKNO = ? and S_TRANO = ?";
				} else {
					updateSql = "update "
							+ TvPayreckBankDto.tableName()
							+ " set S_RESULT = ? , S_ADDWORD = ? ,D_ACCTDATE= ? ,S_XPAYAMT = ? , S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ? "
							+ " where S_AGENTBNKCODE = ? and S_VOUNO = ? and F_AMT = ? and S_RESULT = ? ";
				}

				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) batchReturnList.get(i);
					String sorivouno = (String) tmpmap.get("VouNo"); // ƾ֤���
					String sorivoudate = (String) tmpmap.get("VouDate"); // ƾ֤����
					String soritrano = (String) tmpmap.get("OriTraNo"); // ԭ������ˮ��
					String sPayDictateNo = (String) tmpmap.get("PayDictateNo");// ֧���������
					String sPayMsgNo = (String) tmpmap.get("PayMsgNo");// ֧�����ı��
					String sPayEntrustDate = (String) tmpmap
							.get("PayEntrustDate");// ֧��ί������
					String sPaySndBnkNo = (String) tmpmap.get("PaySndBnkNo");// ֧���������к�
					String sPayResult = (String) tmpmap.get("PayResult");// �������
					String sAddWord = (String) tmpmap.get("AddWord");// ����
					String sacctdate = (String) tmpmap.get("AcctDate"); // TCBSϵͳ����ñ�ҵ�����������
					String sAmt = (String) tmpmap.get("Amt");// ���
					allamt = allamt.add(new BigDecimal(sAmt)); // ����ϼƽ��
					if (DealCodeConstants.DEALCODE_TIPS_SUCCESS
							.equals(sPayResult)
							|| StateConstant.COMMON_NO.equals(sPayResult))
						sAddWord = DealCodeConstants.PROCESS_SUCCESS;

					// ���ݴ�����ת������״̬
					String sstatus = PublicSearchFacade
							.getDetailStateByDealCode(sPayResult);

					// �Ϻ���ֽ��������ֱ��֧����֧����ʽ��0-ֱ�� 1-��Ȩ��������5201ҵ�������ƾ֤������״̬
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
							&& spaymode.equals(MsgConstant.directPay)) {
						VoucherUtil.updateVoucherStatusRecvTCBSDirectMsg(
								strecode, sacctdate, soripackno, sorivoudate,
								sorivouno, allamt, sstatus, sAddWord,
								soritrano, sorientrustdate,
								StateConstant.BIZTYPE_CODE_BATCH);
					}
					updateExce.addParam(sstatus);
					// �Ϻ���ֽ����������Ȩ֧����֧����ʽ��0-ֱ�� 1-��Ȩ��
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
							&& spaymode.equals(MsgConstant.grantPay)) {
						// �ص����� ��д3143��֧���������+���ı��+֧��ί������
						updateExce.addParam(sPayDictateNo + sPayMsgNo
								+ sPayEntrustDate);
					} else {
						updateExce.addParam(sAddWord);
					}
					updateExce.addParam(CommonUtil.strToDate(sacctdate));
					updateExce.addParam(BigDecimal
							.valueOf(Double.valueOf(sAmt)));
					updateExce.addParam(CommonUtil.strToDate(sacctdate));
					updateExce.addParam(sPaySndBnkNo);// ֧���������к�(�Ϻ���ֽ���ص���ʹ��)
					updateExce.addParam(sagentbnkcode);// ���������к�
					updateExce.addParam(sorivouno);// ԭƾ֤���
					updateExce.addParam(sAmt);// ������
					updateExce
							.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// ������״̬
					if (ITFECommonConstant.ISITFECOMMIT
							.equals(StateConstant.COMMON_YES)) {
						updateExce.addParam(soripackno);// ԭ����ˮ��
						updateExce.addParam(soritrano);// ԭ������ˮ��
					}
					updateExce.runQuery(updateSql);
					try {
						Voucher voucher = (Voucher) ContextFactory
								.getApplicationContext().getBean(
										MsgConstant.VOUCHER);
						voucher.VoucherReceiveTCBS(strecode,
								MsgConstant.VOUCHER_NO_2301, soripackno,
								sorivoudate, sorivouno, new BigDecimal(sAmt),
								sstatus, sAddWord);
					} catch (Exception e) {
						logger.error(e);
						VoucherException.saveErrInfo(null, e);
					}

				}
				updateExce.closeConnection();
				// ȡԭ���Ͱ�
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(
						MsgConstant.APPLYPAY_DAORU, sagentbnkcode,
						sorientrustdate, soripackno,
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
					MsgRecvFacade.updateMsgHeadByCon(sfinorgcode, soripackno,
							sorientrustdate,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);

				// �ǽ�����־
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg,
						sentrustDate, msgNo, sorgcode, filepath,
						batchReturnList.size(), allamt, spackno, strecode,
						null, sfinorgcode, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo);

			} catch (JAFDatabaseException e) {
				String error = "����3143���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "����3143���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
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

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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 
 * ��Ҫ���ܣ�6.2.15��TCBS����������֪ͨ(3190)
 * 
 * @author wangyunbin
 * 
 */
public class Recv3190MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3190MsgServer.class);

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
		String recvorg = sdescode;

		/**
		 * �����������ͷ MSG->BillHead3190
		 */
		HashMap billhead3139 = (HashMap) msgMap.get("BatchHead3190");

		String OriMsgNo = (String) billhead3139.get("OriMsgNo");// ԭ���ı��
		String OriTaxOrgCode = (String) billhead3139.get("OriTaxOrgCode"); // ԭ���ջ�������
		String OriEntrustDate = (String) billhead3139.get("OriEntrustDate"); // ԭί������
		String OriPackNo = (String) billhead3139.get("OriPackNo"); // ԭ����ˮ��
		int allNum = Integer.valueOf((String) billhead3139.get("AllNum")); // �ܱ���
		BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(billhead3139
				.get("AllAmt"));// �ܽ��
		String sdemo = "ԭ���ı��:" + OriMsgNo + ",ԭ����ˮ:" + OriPackNo + ",ԭ�����������"
				+ OriTaxOrgCode + ",ԭί������:" + OriEntrustDate;
		String updateSql = "";
		String sendno = null;
		List returnList = (List) msgMap.get("BatchReturn3190");
		HashMap<String, String> tabMap = ITFECommonConstant.bizMsgNoList;
		String tabname = tabMap.get(OriMsgNo);
		if (null == returnList || returnList.size() == 0) {
			return;
		} else {
			updateSql = "update "+ tabname
			+ " set S_STATUS = ? ";
			//����ҵ��������һ��
			if (MsgConstant.MSG_NO_1104.equals(OriMsgNo)) {//�˿�
				updateSql+=", D_ACCT = ? , XPAYAMT = ? where S_TAXORGCODE = ? and D_ACCEPT  = ?  and S_DEALNO = ?";
			}else if(MsgConstant.MSG_NO_1105.equals(OriMsgNo)){//����
				updateSql+=" where S_ORITAXORGCODE = ? and D_ACCEPT  = ?  and S_DEALNO = ?";
			}else if(MsgConstant.MSG_NO_1106.equals(OriMsgNo)){//��ֵ�
				updateSql+=" where S_TAXORGCODE = ? and D_ACCEPTDATE  = ?  and S_TRANO = ?";
			}
			int count = returnList.size();
			for (int i = 0; i < count; i++) {
				HashMap BatchReturn3190Map = (HashMap) returnList.get(i);
				String OriTraNo = (String) BatchReturn3190Map.get("OriTraNo"); // ԭ������ˮ��
				String Vouno = (String) BatchReturn3190Map.get("VouNo"); // ƾ֤���
				BigDecimal TraAmt = MtoCodeTrans
						.transformBigDecimal(BatchReturn3190Map.get("TraAmt"));// ���׽��
				String OpStat = (String) BatchReturn3190Map.get("OpStat"); // ����״̬
				String AddWord = (String) BatchReturn3190Map.get("AddWord"); // ����
				String ChannelCode = (String) BatchReturn3190Map
						.get("ChannelCode"); // ��������
				SQLExecutor updateExce = null;
				try {
					updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					updateExce.clearParams();
					String sstatus = PublicSearchFacade
							.getDetailStateByDealCode(OpStat);
					updateExce.addParam(sstatus);
					if(MsgConstant.MSG_NO_1104.equals(OriMsgNo)){
						updateExce.addParam(CommonUtil.strToDate(sdate));
						updateExce.addParam(TraAmt.toString());
					}
					updateExce.addParam(OriTaxOrgCode);
					updateExce.addParam(CommonUtil.strToDate(OriEntrustDate)
							.toString());
					updateExce.addParam(OriTraNo);
					updateExce.runQuery(updateSql);
					updateExce.closeConnection();
					/**
					 * �˿�ҵ�����ƾ֤��������ֽ��ҵ��
					 */
					if(MsgConstant.MSG_NO_1104.equals(OriMsgNo)){
						try{
							Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
							voucher.VoucherReceiveTCBS(OriTaxOrgCode, MsgConstant.VOUCHER_NO_5209, OriPackNo,
									OriEntrustDate, Vouno, TraAmt, sstatus, "");
						}catch(Exception e){
							logger.error(e);
							VoucherException.saveErrInfo(null, e);
						}
					}
				} catch (JAFDatabaseException e) {
					String error = "���½���TCBS����������֪ͨ(3190)��ִ״̬ʱ�������ݿ��쳣��";
					logger.error(error, e);
					throw new ITFEBizException(error, e);
				} finally {
					if (null != updateExce) {
						updateExce.closeConnection();
					}
				}
			}
			// ȡԭ���Ͱ�
			TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(OriMsgNo,
					OriTaxOrgCode, OriEntrustDate, OriPackNo,
					DealCodeConstants.DEALCODE_ITFE_RECEIPT);
			// ������־��ˮ
			String _srecvno;
			try {
				_srecvno = StampFacade.getStampSendSeq("JS");

				// ����ԭ��״̬
				if (null != senddto) {
					// ����ԭ������־��ˮ��
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno,
							"");
					sendno = senddto.getSsendno();
					recvorg = senddto.getSsendorgcode();
					// �������������ļ�����Ķ�Ӧ��ϵ��
					MsgRecvFacade.updateMsgHeadByCon(OriTaxOrgCode, OriPackNo,
							OriEntrustDate,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// �ǽ�����־
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sdate,
						msgNo, sorgcode, filepath, allNum, allAmt, OriPackNo,
						null, null, OriTaxOrgCode, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo);

			} catch (SequenceException e) {
				String error = "����(3190)��ִ״̬ʱ�������ݿ��쳣��";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			}

		}
		
		eventContext.getMessage().setCorrelationId(
		"ID:524551000000000000000000000000000000000000000000");
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
		
//		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

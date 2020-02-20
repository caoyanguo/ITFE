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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 
 * ��Ҫ���ܣ�6.2.15��TCBS����������֪ͨ(3190)
 * 
 * @author wangwenbo
 * 
 */
public class Recv2000_3190MsgServer extends AbstractMsgManagerServer {

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
		 * ȡ�û�ִͷ��Ϣ
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead2000");
		String strecode = (String) batchHeadMap.get("TreCode"); // �������
		String sentrustDate = (String) batchHeadMap.get("EntrustDate");// ί������
		String spackno = (String) batchHeadMap.get("PackNo"); // ����ˮ��
		String sbillorg = (String) batchHeadMap.get("BillOrg"); // ��Ʊ��λ
		String sorientrustDate = (String) batchHeadMap.get("OriEntrustDate"); // ԭί������
		String soripackno = (String) batchHeadMap.get("OriPackNo"); // ԭ����ˮ��
		int sumnum = Integer.parseInt((String) batchHeadMap.get("AllNum")); // �ܱ���
		BigDecimal sumamt = new BigDecimal((String) batchHeadMap.get("AllAmt")); // �ܽ��
		
		String sdemo = "ԭ���ı��:" + MsgConstant.MSG_NO_1000 + ",ԭ����ˮ:" + soripackno + ","+ ",ԭί������:" + sorientrustDate;
		String updateSql = "";
		String sendno = null;
		List billReturnList = (List) msgMap.get("BillReturn2000");
		HashMap<String, String> tabMap = ITFECommonConstant.bizMsgNoList;
		String tabname = tabMap.get(MsgConstant.MSG_NO_1104);
		if (null == billReturnList || billReturnList.size() == 0) {
			return;
		} else {
			updateSql = "update "+ tabname+ " set S_STATUS = ? ";
			updateSql+=" where S_TAXORGCODE = ? and D_ACCEPT  = ?  and S_DEALNO = ?";
			int count = billReturnList.size();
			TvVoucherinfoDto tmpInfoDto = null;
			for (int i = 0; i < count; i++) {
				HashMap tmpmap = (HashMap) billReturnList.get(i);
				String sorivouno = (String) tmpmap.get("VouNo"); // ԭƾ֤���
				String sorivoudate = (String) tmpmap.get("VouDate"); // ԭƾ֤����
				String soritrano = (String) tmpmap.get("OriTraNo"); // ԭ������ˮ��
				String dsumamt = (String) tmpmap.get("Amt"); // ���
				//allamt = allamt.add(new BigDecimal(dsumamt)); // ����ϼƽ��
				String sacctdate = (String) tmpmap.get("AcctDate"); // �ñ�ҵ�����������
				String sresult = (String) tmpmap.get("Result"); // ������
				String ls_Description = (String) tmpmap.get("Description"); // ��������˵��
				SQLExecutor updateExce = null;
				try {
					updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					updateExce.clearParams();
					String sstatus = PublicSearchFacade.getDetailStateByDealCode(sresult);
					updateExce.addParam(sstatus);
					updateExce.addParam(sbillorg);
					updateExce.addParam(CommonUtil.strToDate(sorientrustDate)
							.toString());
					updateExce.addParam(soritrano);
					updateExce.runQuery(updateSql);
					updateExce.closeConnection();
					
					tmpInfoDto = new TvVoucherinfoDto();
					tmpInfoDto.setStrecode(strecode);
					tmpInfoDto.setSvoucherno(sorivouno);
					tmpInfoDto.setScreatdate(sorivoudate);
					tmpInfoDto.setSext1("40");
					tmpInfoDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(tmpInfoDto).get(0);
					if(null == tmpInfoDto ){
						logger.error("�����ʽ��ִ����2000��δ�ҵ�ԭƾ֤��");
						throw new ITFEBizException("�����ʽ��ִ����2000��δ�ҵ�ԭƾ֤��");
					}
					if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)){
						tmpInfoDto.setSext1("41");
						tmpInfoDto.setSdemo("����ɹ�");
					}else{
						tmpInfoDto.setSext1("42");
						tmpInfoDto.setSdemo("����ʧ��");
					}
					DatabaseFacade.getODB().update(tmpInfoDto);
					
//					/**
//					 * �˿�ҵ�����ƾ֤��������ֽ��ҵ��
//					 */
//					try{
//						Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
//						voucher.VoucherReceiveTCBS(sbillorg, MsgConstant.VOUCHER_NO_5209, soripackno,
//								sorientrustDate, sorivouno, new BigDecimal(dsumamt), sstatus, "");
//					}catch(Exception e){
//						logger.error(e);
//						VoucherException.saveErrInfo(null, e);
//					}
				} catch (JAFDatabaseException e) {
					String error = "���½���TCBS����������֪ͨ(3190)��ִ״̬ʱ�������ݿ��쳣��";
					logger.error(error, e);
					throw new ITFEBizException(error, e);
				} catch (ValidateException e) {
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
			//TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(OriMsgNo,OriTaxOrgCode, OriEntrustDate, OriPackNo,DealCodeConstants.DEALCODE_ITFE_RECEIPT);
			TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(msgRef, msgNo);
			// ������־��ˮ
			String _srecvno;
			try {
				_srecvno = StampFacade.getStampSendSeq("JS");
				// ����ԭ��״̬
				if (null != senddto) {
					// ����ԭ������־��ˮ��
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto,DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno,"");
					sendno = senddto.getSsendno();
					recvorg = senddto.getSsendorgcode();
					// �������������ļ�����Ķ�Ӧ��ϵ��
					MsgRecvFacade.updateMsgHeadByCon( sbillorg, soripackno, sorientrustDate, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// �ǽ�����־
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sentrustDate, msgNo,
						sorgcode, filepath, billReturnList.size(), sumamt, spackno, strecode, null,
						sbillorg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo );

			} catch (SequenceException e) {
				String error = "����(2000-3190)��ִ״̬ʱ�������ݿ��쳣��";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			}

		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

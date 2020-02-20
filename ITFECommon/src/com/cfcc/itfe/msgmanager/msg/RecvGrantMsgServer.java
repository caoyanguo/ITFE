/**
 * ����:�յ���Ȩ֧����Ȼ�ִ����
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author zhouchuan
 *
 */
public class RecvGrantMsgServer extends AbstractMsgManagerServer {
	
	private static Log logger = LogFactory.getLog(RecvGrantMsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		
		// ����ͷ��ϢCFX->HEAD
		String sorgcode = (String) headMap.get("SRC"); // �����������
		String sdescode = (String) headMap.get("DES");// ���սڵ����
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgID = (String) headMap.get("MsgID");// ���ı�ʶ��
//		String msgRef = (String) headMap.get("MsgRef");// ���Ĳο���
//		String sdate = (String) headMap.get("WorkDate");// ��������

		/**
		 * ȡ�û�ִͷ��Ϣ
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead3134");
		String strecode = (String) batchHeadMap.get("TreCode"); // �������
		String sentrustDate = (String) batchHeadMap.get("EntrustDate");//ί������
		String spackno = (String) batchHeadMap.get("PackNo"); // ����ˮ��
		String sbillorg = (String) batchHeadMap.get("BillOrg"); // ��Ʊ��λ
		String sorientrustDate = (String) batchHeadMap.get("OriEntrustDate"); // ԭί������
		String soripackno = (String) batchHeadMap.get("OriPackNo"); // ԭ����ˮ��
//		String spayoutvoutype = (String) batchHeadMap.get("PayoutVouType");//֧��ƾ֤����
		int count = 0 ;
		BigDecimal allamt = new BigDecimal("0.00");
		String sdemo ="ԭ���ı��:"+MsgConstant.MSG_NO_5103+",ԭ����ˮ:"+soripackno.trim()+",ԭ�����������"+sbillorg.trim()+",ԭί������:"+sorientrustDate.trim();
		String sendno = null;
		String recvorg = sdescode;

		/**
		 * ȡ�û�ִ������Ϣ
		 */
		List<Object> batchReturnList = (List<Object>) msgMap.get("BatchReturn3134");
		if (null == batchReturnList || batchReturnList.size() == 0) {
			return;
		} else {
			count = batchReturnList.size();
			SQLExecutor updateExce = null;
			try {
				//ȡԭ���Ͱ�
				TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(MsgConstant.MSG_NO_5103, sbillorg, sorientrustDate, soripackno, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				if (null!=senddto) {
					String ifsendfinc = senddto.getSifsend();
					//������־��ˮd
					String _srecvno = StampFacade.getStampSendSeq("JS");
					if (null!=ifsendfinc && ifsendfinc.equals(StateConstant.MSG_SENDER_FLAG_2)) {//����ǲ�������ֱ��ת��������
						// ��ȡԭ����ֱ�ӷ���
						eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
						Object msg = eventContext.getMessage().getProperty("MSG_INFO");
						eventContext.getMessage().setPayload(msg);
						// д������־
						MsgLogFacade.writeSendLog(_srecvno, senddto.getSrecvno(), senddto.getSsendorgcode(), (String) headMap.get("DES"), sentrustDate,
								(String) headMap.get("MsgNo"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 0, new BigDecimal("0.0"), spackno, null,
								null, sbillorg, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
								(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
						return;
					}
				}
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				//����ҵ���״̬
				String updateSql = "update " + TvGrantpaymsgmainDto.tableName() + " set S_STATUS = ? , S_DEMO = ? ,S_ACCDATE = ? ,N_XALLAMT = ? "
				+ " where S_TRECODE = ? and S_PAYUNIT = ? and S_COMMITDATE = ? and S_PACKAGENO = ? "
				+ " and S_PACKAGETICKETNO = ? and S_GENTICKETDATE = ? and S_DEALNO = ? and (S_STATUS NOT IN (? , ? )) ";
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) batchReturnList.get(i);
					String sorivouno = (String) tmpmap.get("VouNo"); // ԭƾ֤���
					String sorivoudate = (String) tmpmap.get("VouDate"); // ԭƾ֤����
					String soritrano = (String) tmpmap.get("OriTraNo"); // ԭ������ˮ��
					String dsumamt = (String) tmpmap.get("SumAmt"); // �ϼƽ��
					allamt = allamt.add(new BigDecimal(dsumamt)); // ����ϼƽ��
					String sacctdate = (String) tmpmap.get("AcctDate"); // TCBSϵͳ����ñ�ҵ�����������
					String sresult = (String) tmpmap.get("Result"); // ������
//					String ls_Description = (String) tmpmap.get("Description"); // ˵��
					String demo;
					
					//���ݴ�����ת������״̬   
					String sstatus = PublicSearchFacade.getDetailStateByDealCode(sresult);
					if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)) {
						demo = DealCodeConstants.PROCESS_SUCCESS;
					} else {
						demo = DealCodeConstants.PROCESS_FAIL;
					}
					//����TCBS��ִ���²��ƾ֤������
					if(VoucherUtil.updateVoucherSplitReceiveTCBS(soritrano, sstatus, new BigDecimal(dsumamt), sacctdate, sorientrustDate, demo))
						continue;
					
					updateExce.addParam(sstatus);
					updateExce.addParam(demo);
					updateExce.addParam(sacctdate);
					updateExce.addParam(new BigDecimal(dsumamt));
					updateExce.addParam(strecode);//�������
					updateExce.addParam(sbillorg);//��Ʊ��λ
					updateExce.addParam(sorientrustDate);//ԭί������
					updateExce.addParam(soripackno);//ԭ����
					updateExce.addParam(sorivouno);//ԭƾ֤���
					updateExce.addParam(sorivoudate);//ԭƾ֤����
					updateExce.addParam(soritrano);//ԭ������ˮ
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);//������״̬ [80000] ����ɹ�  ��ƾ֤
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);//������״̬  [80001] ����ʧ�� ��ƾ֤
					updateExce.runQuery(updateSql);
				}
				
				//����ƾ֤������״̬�������в�ֵ�ƾ֤ȫ�����ճɹ�
				VoucherUtil.updateVoucherStatusRecvTCBSGrantMsg(soripackno, sorientrustDate);
				
				//������־��ˮd
				String _srecvno = StampFacade.getStampSendSeq("JS");
				//����ԭ��״̬
				if (null!=senddto) {
					//����ԭ������־��ˮ��
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto, DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno, "");
					sendno = senddto.getSsendno();
					recvorg =senddto.getSsendorgcode(); 
					// �������������ļ�����Ķ�Ӧ��ϵ��
					MsgRecvFacade.updateMsgHeadByCon( sbillorg, soripackno, sorientrustDate, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH");
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// �ǽ�����־
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sentrustDate, msgNo,
						sorgcode, filepath, batchReturnList.size(), allamt, spackno, strecode, null,
						sbillorg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo );


			} catch (JAFDatabaseException e) {
				String error = "����3134���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "����3134���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			}  finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
		{
			eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
			// ���ԭ���ģ����·���
			Object msg = eventContext.getMessage().getProperty("MSG_INFO");
			eventContext.getMessage().setPayload(msg);
		}else
			eventContext.setStopFurtherProcessing(true);
		return;
	}
}

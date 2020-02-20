/**
 * ����:�յ�ʵ���ʽ��Ȼ�ִ����
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
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
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangyunbin
 * 
 */
public class Recv2000_3131MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(RecvPayOutMsgServer.class);

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
		String msgRef = (String) headMap.get("MsgRef");// ���Ĳο���
		String sdate = (String) headMap.get("WorkDate");// ��������

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
		int count = 0 ;
		//BigDecimal allamt = new BigDecimal("0.00");
		String sendno = null;
		String recvorg = sdescode;
		String sdemo ="ԭ���ı��:"+MsgConstant.MSG_NO_1000+"-"+MsgConstant.MSG_NO_5101+",ԭ����ˮ:"+soripackno.trim()+",ԭ�����������"+sbillorg.trim()+",ԭί������:"+sorientrustDate.trim();
		/**
		 * ȡ�û�ִ������Ϣ
		 */
		List<Object> batchReturnList = (List<Object>) msgMap.get("BillReturn2000");
		if (null == batchReturnList || batchReturnList.size() == 0) {
			return;
		} else {
			count = batchReturnList.size();
			SQLExecutor updateExce = null;
			
			try {
				//ȡԭ���Ͱ�
				TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(MsgConstant.MSG_NO_1000, sbillorg, sorientrustDate, soripackno, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				//������־��ˮ
				String _srecvno = StampFacade.getStampSendSeq("JS");
				if (null!=senddto) {
					String ifsendfinc = senddto.getSifsend();
					if (null!=ifsendfinc && ifsendfinc.equals(StateConstant.MSG_SENDER_FLAG_2)) {//����ǲ�������ֱ��ת��������
						// ��ȡԭ����ֱ�ӷ���
						Object msg = eventContext.getMessage().getProperty("MSG_INFO");
						eventContext.getMessage().setPayload(msg);
						// д������־
						MsgLogFacade.writeSendLog(_srecvno, senddto.getSrecvno(), senddto.getSsendorgcode(), (String) headMap.get("DES"), sentrustDate,
								(String) headMap.get("MsgNo"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), sumnum, sumamt, spackno, null,
								null, sbillorg, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
								(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
						return;
					}
				}
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				
				String updateSql = "update " + TvPayoutmsgmainDto.tableName() + " set S_STATUS = ? , S_DEMO = ? , S_XPAYAMT = ? , S_XPAYDATE = ? , S_XAGENTBUSINESSNO = ?"
						+ " where S_TRECODE = ?  and S_PAYUNIT = ? and S_COMMITDATE = ? and S_PACKAGENO = ? "
						+ " and S_TAXTICKETNO = ? and S_GENTICKETDATE = ? and S_DEALNO = ? and S_STATUS NOT IN (? , ? ) ";
				
				
				TvVoucherinfoDto tmpInfoDto = null;
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) batchReturnList.get(i);
					String sorivouno = (String) tmpmap.get("VouNo"); // ԭƾ֤���
					String sorivoudate = (String) tmpmap.get("VouDate"); // ԭƾ֤����
					String soritrano = (String) tmpmap.get("OriTraNo"); // ԭ������ˮ��
					String sAmt = (String) tmpmap.get("Amt"); // ���
					//allamt = allamt.add(new BigDecimal(dsumamt)); // ����ϼƽ��
					String sacctdate = (String) tmpmap.get("AcctDate"); // �ñ�ҵ�����������
					String sresult = (String) tmpmap.get("Result"); // ������
					String ls_Description = (String) tmpmap.get("Description"); // ��������˵��
					
					//���ݴ�����ת������״̬   
					String sstatus = PublicSearchFacade.getDetailStateByDealCode(sresult);
					updateExce.addParam(sstatus);//״̬
					updateExce.addParam(sresult);
					updateExce.addParam(sAmt);//������
					updateExce.addParam(sacctdate);//��������
					updateExce.addParam(soritrano);//ԭ������ˮ��
					updateExce.addParam(strecode);
					updateExce.addParam(sbillorg);
					updateExce.addParam(sorientrustDate);
					updateExce.addParam(soripackno);
					
					updateExce.addParam(sorivouno);
					updateExce.addParam(sorivoudate);
					updateExce.addParam(soritrano);
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);//������״̬ [80000] ����ɹ�  ��ƾ֤
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);//������״̬  [80001] ����ʧ�� ��ƾ֤
					updateExce.runQuery(updateSql);
					
					
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
//					 * �������� ����ƾ֤״̬
//					 */
//					try{
//						Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
//								MsgConstant.VOUCHER);
//						//����ʵ���ʽ�5207������״̬
//						voucher.VoucherReceiveTCBS(strecode, MsgConstant.VOUCHER_NO_5207, soripackno,
//								sorivoudate, sorivouno,new BigDecimal(sAmt), sstatus,ls_Description);
//					}catch(Exception e){
//						logger.error(e);
//						VoucherException.saveErrInfo(null, e);
//					}
				}
				
				//����ԭ��״̬
				if (null!=senddto) {
					//����ԭ������־��ˮ��
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto, DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno, "");
					sendno = senddto.getSsendno();
				    recvorg = senddto.getSsendorgcode();
					// �������������ļ�����Ķ�Ӧ��ϵ��
					MsgRecvFacade.updateMsgHeadByCon( sbillorg, soripackno, sorientrustDate, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// �ǽ�����־
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sentrustDate, msgNo,
						sorgcode, filepath, batchReturnList.size(), sumamt, spackno, strecode, null,
						sbillorg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo );
				
			} catch (JAFDatabaseException e) {
				String error = "����2000-3131���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "����2000-3131���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (ValidateException e) {
				String error = "����2000-3131���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			}  finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}

}


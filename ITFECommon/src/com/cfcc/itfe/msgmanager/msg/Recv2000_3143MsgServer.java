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
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangwenbo
 * 
 */
public class Recv2000_3143MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv2000_3143MsgServer.class);

	@SuppressWarnings({ "unchecked" })
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
		String sdemo ="ԭ���ı��:"+MsgConstant.MSG_NO_1000+MsgConstant.APPLYPAY_DAORU+",ԭ����ˮ:"+soripackno.trim()+",ԭ�����������"+sbillorg.trim()+",ԭί������:"+sorientrustDate.trim();
		String sendno = null;
		String recvorg = sdescode;
		
		/**
		 * ȡ�û�ִ������Ϣ
		 */
		List<Object> billReturnList = (List<Object>) msgMap.get("BillReturn2000");
		if (null == billReturnList || billReturnList.size() == 0) {
			return;
		} else {
			count = billReturnList.size();
			SQLExecutor updateExce = null;
			try {
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String updateSql = "update " + TvPayreckBankDto.tableName() + " set S_RESULT = ? , S_ADDWORD = ? ,D_ACCTDATE= ? ,S_XPAYAMT = ? , S_XCLEARDATE = ?  "
				+ " where  S_VOUNO = ? and F_AMT = ? and S_RESULT = ? and S_PACKNO = ? and S_TRANO = ?";
				TvVoucherinfoDto tmpInfoDto = null;
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) billReturnList.get(i);
					String sorivouno = (String) tmpmap.get("VouNo"); // ԭƾ֤���
					String sorivoudate = (String) tmpmap.get("VouDate"); // ԭƾ֤����
					String soritrano = (String) tmpmap.get("OriTraNo"); // ԭ������ˮ��
					String sAmt = (String) tmpmap.get("Amt"); // ���
					String sacctdate = (String) tmpmap.get("AcctDate"); // �ñ�ҵ�����������
					String sresult = (String) tmpmap.get("Result"); // ������
					String sAddWord = (String) tmpmap.get("Description"); // ��������˵��
					
					if (DealCodeConstants.DEALCODE_TIPS_SUCCESS.equals(sresult) || StateConstant.COMMON_NO.equals(sresult)) 
						sAddWord = DealCodeConstants.PROCESS_SUCCESS;
					 
					//���ݴ�����ת������״̬   
					String sstatus = PublicSearchFacade.getDetailStateByDealCode(sresult);
					updateExce.addParam(sstatus);
					updateExce.addParam(sAddWord);
					updateExce.addParam(CommonUtil.strToDate(sacctdate));
					updateExce.addParam(BigDecimal.valueOf(Double.valueOf(sAmt)));
					updateExce.addParam(CommonUtil.strToDate(sacctdate));
					updateExce.addParam(sorivouno);//ԭƾ֤���
					updateExce.addParam(sAmt);//������
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);//������״̬
					updateExce.addParam(soripackno);//ԭ����ˮ��
					updateExce.addParam(soritrano);//ԭ������ˮ��
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
//					try{
//						Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
//								MsgConstant.VOUCHER);
//						voucher.VoucherReceiveTCBS(strecode, MsgConstant.VOUCHER_NO_2301, soripackno,
//								sorivoudate, sorivouno,new BigDecimal(sAmt), sstatus, sAddWord);
//					}catch(Exception e){
//						logger.error(e);
//						VoucherException.saveErrInfo(null, e);
//					}
				
				}
				updateExce.closeConnection();
				//ȡԭ���Ͱ�
				//TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(MsgConstant.APPLYPAY_DAORU, sagentbnkcode, sentrustDate, soripackno, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(msgRef, msgNo);
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
						sorgcode, filepath, billReturnList.size(), sumamt, spackno, strecode, null,
						sbillorg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo );


			} catch (JAFDatabaseException e) {
				String error = "����2000-3143���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "����2000-3143���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (ValidateException e) {
				String error = "����2000-3143���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			}   finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		eventContext.setStopFurtherProcessing(true);
		return;
		
	}

}

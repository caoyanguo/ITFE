/**
 * ����:�յ�ʵ���ʽ��Ȼ�ִ����
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * 
 */
public class RecvPayOutMsgServer extends AbstractMsgManagerServer {

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
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead3131");
		String strecode = (String) batchHeadMap.get("TreCode"); // �������
		String spayeebankno = (String) batchHeadMap.get("PayeeBankNo");// �տ����к�
		String sentrustDate = (String) batchHeadMap.get("EntrustDate");// ί������
		String spackno = (String) batchHeadMap.get("PackNo"); // ����ˮ��
		String sbillorg = (String) batchHeadMap.get("BillOrg"); // ��Ʊ��λ
		String sorientrustDate = (String) batchHeadMap.get("OriEntrustDate"); // ԭί������
		String soripackno = (String) batchHeadMap.get("OriPackNo"); // ԭ����ˮ��
		String spayoutvoutype = (String) batchHeadMap.get("PayoutVouType");// ֧��ƾ֤����
		int sumnum = Integer.parseInt((String) batchHeadMap.get("AllNum")); // �ܱ���
		BigDecimal sumamt = new BigDecimal((String) batchHeadMap.get("AllAmt")); // �ܽ��
		int count = 0;
		BigDecimal allamt = new BigDecimal("0.00");
		String sendno = null;
		String recvorg = sdescode;
		String sdemo = "ԭ���ı��:" + MsgConstant.MSG_NO_5101 + ",ԭ����ˮ:"
				+ soripackno.trim() + ",ԭ�����������" + sbillorg.trim() + ",ԭί������:"
				+ sorientrustDate.trim();
		/**
		 * ȡ�û�ִ������Ϣ
		 */
		List<Object> batchReturnList = (List<Object>) msgMap
				.get("BatchReturn3131");
		if (null == batchReturnList || batchReturnList.size() == 0) {
			return;
		} else {
			count = batchReturnList.size();
			SQLExecutor updateExce = null;

			try {
				// ȡԭ���Ͱ�
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(
						MsgConstant.MSG_NO_5101, sbillorg, sorientrustDate,
						soripackno, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				// ������־��ˮ
				String _srecvno = StampFacade.getStampSendSeq("JS");
				if (null != senddto) {
					String ifsendfinc = senddto.getSifsend();
					if (null != ifsendfinc
							&& ifsendfinc
									.equals(StateConstant.MSG_SENDER_FLAG_2)) {// ����ǲ�������ֱ��ת��������
						// ��ȡԭ����ֱ�ӷ���
						Object msg = eventContext.getMessage().getProperty(
								"MSG_INFO");
						eventContext.getMessage().setPayload(msg);
						// д������־
						MsgLogFacade.writeSendLog(_srecvno, senddto
								.getSrecvno(), senddto.getSsendorgcode(),
								(String) headMap.get("DES"), sentrustDate,
								(String) headMap.get("MsgNo"),
								(String) eventContext.getMessage().getProperty(
										"XML_MSG_FILE_PATH"), sumnum, sumamt,
								spackno, null, null, sbillorg, null,
								(String) headMap.get("MsgID"),
								DealCodeConstants.DEALCODE_ITFE_SEND, null,
								null,
								(String) eventContext.getMessage().getProperty(
										MessagePropertyKeys.MSG_SENDER), null,
								MsgConstant.LOG_ADDWORD_SEND);
						return;
					}
				}
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				
				TsTreasuryDto tredto = SrvCacheFacade.cacheTreasuryInfo(null).get(strecode);
				if (StateConstant.COMMON_YES.equals(String.valueOf(tredto==null?"":tredto.getSpayunitname()))) {
					updatePayoutDetailState(updateExce, batchReturnList, soripackno);
				}else{
					updatePayoutMainStatus(updateExce, batchReturnList, soripackno, strecode, spayeebankno, sbillorg, sorientrustDate);
				}

				
				// ����ԭ��״̬
				if (null != senddto) {
					// ����ԭ������־��ˮ��
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno,
							"");
					sendno = senddto.getSsendno();
					recvorg = senddto.getSsendorgcode();
					// �������������ļ�����Ķ�Ӧ��ϵ��
					MsgRecvFacade.updateMsgHeadByCon(sbillorg, soripackno,
							sorientrustDate,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// �ǽ�����־
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg,
						sentrustDate, msgNo, sorgcode, filepath,
						batchReturnList.size(), allamt, spackno, strecode,
						null, sbillorg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo);

			} catch (JAFDatabaseException e) {
				String error = "����3131���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "����3131���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (ValidateException e) {
				String error = "����3131���Ĵ���ʧ�ܣ�";
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

	/***
	 * �����嵥ģʽ��״̬���£�����ϸȫ�����³ɹ��󣬸���������Ϣ
	 * @throws JAFDatabaseException
	 * @throws ValidateException 
	 */
	private void updatePayoutDetailState(SQLExecutor updateExce,
			List<Object> batchReturnList, String soripackno)
			throws JAFDatabaseException, ValidateException {
		String updatesqldetail = "update "
				+ TvPayoutmsgsubDto.tableName()
				+ " set S_STATUS = ?,S_XADDWORD=?,S_XPAYAMT = ?,S_XPAYDATE=?,S_XAGENTBUSINESSNO = ?,S_ACCDATE=? "
				+ " where S_PACKAGENO = ?  AND S_TRASNO = ?  AND S_VOUCHERNO =? AND (S_STATUS <> '80000' OR S_STATUS IS NULL)";
		int count = batchReturnList.size();
		String sorivouno = null;
		String soritrano = null;
		String sacctdate = null;
	    
		for (int i = 0; i < count; i++) {
			updateExce.clearParams();
			HashMap tmpmap = (HashMap) batchReturnList.get(i);
			sorivouno = (String) tmpmap.get("VouNo"); // ԭƾ֤���
			String sorivoudate = (String) tmpmap.get("VouDate"); // ԭƾ֤����
			soritrano = (String) tmpmap.get("OriTraNo"); // ԭ������ˮ��
			String dsumamt = (String) tmpmap.get("Amt"); // ���
			sacctdate = (String) tmpmap.get("AcctDate"); // TCBSϵͳ����ñ�ҵ�����������
			String sresult = (String) tmpmap.get("Result"); // ������
			String ls_Description = (String) tmpmap.get("Description"); // ��������˵��

			// ���ݴ�����ת������״̬
			String sstatus = PublicSearchFacade
					.getDetailStateByDealCode(sresult);
			updateExce.addParam(sstatus);// ״̬
			if (ls_Description.length() > 100) {
				ls_Description.substring(0, 100);
			}
			if (DealCodeConstants.DEALCODE_ITFE_FAIL.equals(sstatus)) {
				dsumamt = "0.00";
			}
			updateExce.addParam(ls_Description);// ����ԭ��
			updateExce.addParam(dsumamt);// ������
			updateExce.addParam(sacctdate);// ��������
			updateExce.addParam(soritrano);// ԭ������ˮ��
			updateExce.addParam(sacctdate);// ��������
			updateExce.addParam(soripackno);// �����
			updateExce.addParam(soritrano);// ������ˮ��
			updateExce.addParam(sorivouno);// ƾ֤���
			updateExce.runQuery(updatesqldetail);
		}
	
		TvPayoutmsgsubDto _dto = new TvPayoutmsgsubDto();
		_dto.setSpackageno(soripackno);
		_dto.setStrasno(soritrano);
		_dto.setSvoucherno(sorivouno);
		List <TvPayoutmsgsubDto> list = CommonFacade.getODB().findRsByDto(_dto);
		String bizno ;
		Boolean b =Boolean.TRUE;
		BigDecimal allamt=BigDecimal.ZERO;
		String errinfo = "";
		String status = DealCodeConstants.VOUCHER_FAIL_TCBS;
		Boolean isFail = Boolean.FALSE;
		if (list.size()> 0) {
			bizno =list.get(0).getSbizno();
			//�ж�bizno����ϸ�Ƿ��յ���ִ��������յ��Ļ���������������������������û�յ��ģ��򲻽��и���
			TvPayoutmsgsubDto qdto = new TvPayoutmsgsubDto();
			qdto.setSbizno(bizno);
			List <TvPayoutmsgsubDto> qlist = CommonFacade.getODB().findRsByDto(qdto);
			for (TvPayoutmsgsubDto  comp : qlist) {
				if (null==comp.getSstatus()) {
					b =Boolean.FALSE;
					continue;
				}
				if (DealCodeConstants.DEALCODE_ITFE_FAIL.equals(comp.getSstatus())) {
					isFail =Boolean.TRUE; 
					errinfo +=comp.getSvoucherno()+":"+comp.getSxaddword()+"||";
				}
				if (comp.getSstatus().equals(DealCodeConstants.DEALCODE_ITFE_SUCCESS)) {
					allamt = allamt.add(comp.getNmoney()); // ����ϼƽ��
				}
			}
			String sdemo="";
			String voustatus="";
            if (b) {//˵���Ѿ�ȫ���ص�����Ҫ����������Ϣ��ƾ֤������
            	//������ϸ�Ļ�ִ������ж�һ��������Ӧ����ϸ�Ƿ��Ѿ�ȫ�����յ���ִ������������Ϣ���������״̬
              //���㴦��ɹ����ܽ��
            	//����ƾ֤������
               if (errinfo.length() > 1000) {
   				 sdemo = errinfo.substring(0, 1000);
   			   }else{
   				 sdemo = errinfo;
   			   }
               if (StringUtils.isBlank(sdemo)) {
   				  sdemo= "TCBS����ɹ�";
 			   }
               if (sdemo.endsWith("||")) {
            	   sdemo =sdemo.substring(0, sdemo.length() -2);
			   }
    		   /*if (!isFail) {
    			   status = DealCodeConstants.VOUCHER_SUCCESS_NO_BACK;  
  			   }else{
  				   status = DealCodeConstants.VOUCHER_FAIL_TCBS;
			   }*/
              if (allamt.compareTo(BigDecimal.ZERO)>0) {
            	   status = DealCodeConstants.VOUCHER_SUCCESS_NO_BACK; 
            	   voustatus= DealCodeConstants.DEALCODE_ITFE_SUCCESS;
		       }else{
		    	   status = DealCodeConstants.VOUCHER_FAIL_TCBS; 
		    	   voustatus= DealCodeConstants.DEALCODE_ITFE_FAIL;
		       }
  			  updatesqldetail = "update TV_PAYOUTMSGMAIN SET  S_STATUS= ? , S_XPAYAMT = ?,S_XPAYDATE = ?,S_XAGENTBUSINESSNO = ? WHERE S_BIZNO = ?";
  			  updateExce.clearParams();
  			  updateExce.addParam(voustatus);// 
  			  updateExce.addParam(allamt);// ���
  			  updateExce.addParam(sacctdate);// ����
  			  updateExce.addParam(soritrano);// ������ˮ
  			  updateExce.addParam(bizno);// ƾ֤��ˮ��
  			  updateExce.runQuery(updatesqldetail);
  			  
  			  updatesqldetail = "update TV_VOUCHERINFO SET  S_STATUS = ?,S_DEMO = ?  WHERE S_DEALNO = ?";
  			  updateExce.clearParams();
  			  updateExce.addParam(status);// ����
  			  updateExce.addParam(sdemo);//����
  			  updateExce.addParam(bizno);// ����
  			  updateExce.runQuery(updatesqldetail);
			}
			
		}
		updateExce.closeConnection();
		
  
		
	}
	
	private void updatePayoutMainStatus(SQLExecutor updateExce,List<Object> batchReturnList, String soripackno,String strecode,String spayeebankno,String sbillorg,String sorientrustDate) throws JAFDatabaseException{

		String updateSql = "update "
				+ TvPayoutmsgmainDto.tableName()
				+ " set S_STATUS = ? , S_DEMO = ? , S_XPAYAMT = ? , S_XPAYDATE = ? , S_XAGENTBUSINESSNO = ?"
				+ " where S_TRECODE = ? and S_PAYEEBANKNO = ? and S_PAYUNIT = ? and S_COMMITDATE = ? and S_PACKAGENO = ? "
				+ " and S_TAXTICKETNO = ? and S_GENTICKETDATE = ? and S_DEALNO = ? and S_STATUS NOT IN (? , ? ) ";
		
		int count = batchReturnList.size();

		for (int i = 0; i < count; i++) {
			updateExce.clearParams();
			HashMap tmpmap = (HashMap) batchReturnList.get(i);
			String sorivouno = (String) tmpmap.get("VouNo"); // ԭƾ֤���
			String sorivoudate = (String) tmpmap.get("VouDate"); // ԭƾ֤����
			String soritrano = (String) tmpmap.get("OriTraNo"); // ԭ������ˮ��
			String dsumamt = (String) tmpmap.get("Amt"); // ���
			//allamt = allamt.add(new BigDecimal(dsumamt)); // ����ϼƽ��
			String sacctdate = (String) tmpmap.get("AcctDate"); // TCBSϵͳ����ñ�ҵ�����������
			String sresult = (String) tmpmap.get("Result"); // ������
			String ls_Description = (String) tmpmap.get("Description"); // ��������˵��

			// ���ݴ�����ת������״̬
			String sstatus = PublicSearchFacade
					.getDetailStateByDealCode(sresult);
			updateExce.addParam(sstatus);// ״̬
			updateExce.addParam(sresult);
			updateExce.addParam(dsumamt);// ������
			updateExce.addParam(sacctdate);// ��������
			updateExce.addParam(soritrano);// ԭ������ˮ��
			updateExce.addParam(strecode);
			updateExce.addParam(spayeebankno);
			updateExce.addParam(sbillorg);
			updateExce.addParam(sorientrustDate);
			updateExce.addParam(soripackno);
			updateExce.addParam(sorivouno);
			updateExce.addParam(sorivoudate);
			updateExce.addParam(soritrano);
			updateExce
					.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// ������״̬
																		// [80000]
																		// ����ɹ�
																		// ��ƾ֤
			updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);// ������״̬
																		// [80001]
																		// ����ʧ��
																		// ��ƾ֤
			updateExce.runQuery(updateSql);
			// ����TCBS��ִ��������ҵ��֧����ϸ8207����״̬���Ϻ���
			VoucherUtil
					.updateMatinDtoReceiveTCBS(
							MsgConstant.VOUCHER_NO_8207, sstatus,
							sresult, sacctdate,
							new BigDecimal(dsumamt), strecode,
							sorivouno, sorivoudate,
							MsgConstant.VOUCHER_NO_5207);

			/**
			 * �������� ����ƾ֤״̬
			 */
			try {
				Voucher voucher = (Voucher) ContextFactory
						.getApplicationContext().getBean(
								MsgConstant.VOUCHER);
				// ����ʵ���ʽ�5207������״̬
				voucher.VoucherReceiveTCBS(strecode,
						MsgConstant.VOUCHER_NO_5207, soripackno,
						sorivoudate, sorivouno,
						new BigDecimal(dsumamt), sstatus,
						ls_Description);
				/**
				 * ��������ҵ��������8207��ƾ֤״̬���Ϻ���
				 * 
				 * @author �Ż��
				 */
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)
					voucher.VoucherReceiveTCBS(strecode,
							MsgConstant.VOUCHER_NO_8207,
							MsgConstant.VOUCHER_NO_5207, sorivoudate,
							sorivouno, new BigDecimal(dsumamt),
							sstatus, ls_Description);
			} catch (Exception e) {
				logger.error(e);
				VoucherException.saveErrInfo(null, e);
			}
		}
		updateExce.closeConnection();
	}

}

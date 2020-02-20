/**
 * ����:�յ�ʵ���ʽ��Ȼ�ִ����
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

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
import com.cfcc.itfe.persistence.dto.TfFundAppropriationDto;
import com.cfcc.itfe.persistence.dto.TfFundClearReceiptDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangyunbin
 * 
 */
public class Recv2000MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv2000MsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		String updateSql = null,_srecvno = null ,_ssendno = null;
		int count = 0;
		SQLExecutor updateExce = null;
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

		TfFundClearReceiptDto tfFundClearReceiptDto = new TfFundClearReceiptDto();
		tfFundClearReceiptDto.setSmsgid(msgID);
		tfFundClearReceiptDto.setSmsgref(msgRef);
		tfFundClearReceiptDto.setSmsgno(msgNo);
		/**
		 * ȡ�û�ִͷ��Ϣ
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead2000");
		String strecode = (String) batchHeadMap.get("TreCode"); // �������
		String sentrustDate = (String) batchHeadMap.get("EntrustDate");// ί������
		String spackno = (String) batchHeadMap.get("PackNo"); // ����ˮ��
		String soripackno = (String) batchHeadMap.get("OriPackNo"); // ԭ����ˮ��
		int sumnum = Integer.parseInt((String) batchHeadMap.get("AllNum")); // �ܱ���
		BigDecimal sumamt = new BigDecimal((String) batchHeadMap.get("AllAmt")); // �ܽ��
		
		tfFundClearReceiptDto.setStrecode(strecode);
		tfFundClearReceiptDto.setSentrustdate(sentrustDate);
		tfFundClearReceiptDto.setSpackno(spackno);
		tfFundClearReceiptDto.setSoripackno(soripackno);
		tfFundClearReceiptDto.setNallamt(sumamt);
		
		//��¼�շ���־������ˮ�Ų��ܴ���8λ
		if(spackno.length()>8){
			spackno = spackno.substring(spackno.length()-8);
		}
		
		String sendno = null;
		String recvorg = sdescode;
		String sdemo = "ԭ���ı��:" + MsgConstant.MSG_NO_1000 + ",ԭ����ˮ:" + soripackno.trim() + "�ѻ�ִ";
		
		String returnWorld = "���ճɹ�";
		String returnStatus = "90000";
		/**
		 * ȡ�û�ִ������Ϣ
		 */
		try {
			List<Object> batchReturnList = (List<Object>) msgMap.get("BillReturn2000");
			if (null == batchReturnList || batchReturnList.size() == 0) {
				return;
			} else {
				count = batchReturnList.size();
				// ȡԭ���Ͱ�
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,MsgConstant.MSG_NO_1000);
				if (null == senddto) {
					returnWorld = "��ȡԭ���Ͱ�����ʧ��";
					returnStatus = "90001";
					logger.error("ƾ֤��ţ�1000,msgRef��" + msgRef);
					throw new ITFEBizException("��ȡ���Ͱ�����ʧ��msgRef��" + msgRef);
				}
				if(senddto.getSretcode().equals(DealCodeConstants.DEALCODE_ITFE_RECEIPT)){
					returnWorld = "�յ��������ִ�����ظ�";
					returnStatus = "90002";
					logger.error("���ı�ʶ�ţ�" + msgID + "�������ִ�ظ���");
					throw new ITFEBizException("���յ������ִ�ظ���msgid��" + msgID);
				}
				// ���շ�����־��ˮ
				_srecvno = StampFacade.getStampSendSeq("JS");
				_ssendno = StampFacade.getStampSendSeq("FS");
				if ("1".equals((String) batchHeadMap.get("PayoutVouType"))) { //ʵ��
					updateSql = "update "
							+ TvPayoutmsgmainDto.tableName()
							+ " set S_STATUS = ? , S_DEMO = ? , S_XPAYAMT = ? , S_XPAYDATE = ? , S_XAGENTBUSINESSNO = ?"
							+ " where S_TRECODE = ?   and S_PACKAGENO = ? "
							+ " and S_TAXTICKETNO = ? and S_GENTICKETDATE = ? and S_STATUS NOT IN (? , ? ) ";
				} else if ("2".equals((String) batchHeadMap.get("PayoutVouType"))) { //�˿�
//					HashMap<String, String> tabMap = ITFECommonConstant.bizMsgNoList;
//					tabname = tabMap.get(MsgConstant.MSG_NO_1104);
//					updateSql = "update "+ tabname+ " set S_STATUS = ?  where S_TAXORGCODE = ? and D_ACCEPT  = ?  and S_DEALNO = ?";
				} else if ("3".equals((String) batchHeadMap.get("PayoutVouType"))) { //���л���
					updateSql = "update "
							+ TvPayreckBankDto.tableName()
							+ " set S_RESULT = ? , S_ADDWORD = ? ,D_ACCTDATE= ? ,S_XPAYAMT = ? , S_XCLEARDATE = ?  "
							+ " where  S_VOUNO = ? and F_AMT = ? and S_RESULT = ? and S_PACKNO = ? and S_TRANO = ?";
				} else { //����
					returnWorld = "ƾ֤������д����";
					returnStatus = "90001";
					logger.error("ƾ֤������д����");
					throw new ITFEBizException("ƾ֤������д����");
				}
				
				
				tfFundClearReceiptDto.setIallnum(count);
				tfFundClearReceiptDto.setSpayoutvoutype((String) batchHeadMap.get("PayoutVouType"));
				
				List tmpList = null;
				TvVoucherinfoDto tmpInfoDto = null;
				String sstatus = null;
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				TfFundAppropriationDto tfFundAppropriationDto = null;
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) batchReturnList.get(i);
					String sorivouno = (String) tmpmap.get("VouNo"); // ԭƾ֤���
					String sorivoudate = (String) tmpmap.get("VouDate"); // ԭƾ֤����
					String soritrano = (String) tmpmap.get("OriTraNo"); // ԭ������ˮ��
					String sAmt = (String) tmpmap.get("Amt"); // ���
					// allamt = allamt.add(new BigDecimal(dsumamt)); // ����ϼƽ��
					String sacctdate = (String) tmpmap.get("AcctDate"); // �ñ�ҵ�����������
					String sresult = (String) tmpmap.get("Result"); // ������
					String sAddWord = (String) tmpmap.get("Description"); // ��������˵��
					
					tfFundClearReceiptDto.setSvouno(sorivouno);
					tfFundClearReceiptDto.setSvoudate(sorivoudate);
					tfFundClearReceiptDto.setSoritrano(soritrano);
					tfFundClearReceiptDto.setNamt(new BigDecimal(sAmt));
					tfFundClearReceiptDto.setSacctdate(sacctdate);
					tfFundClearReceiptDto.setSresult(PublicSearchFacade.getDetailStateByDealCode(sresult));
					tfFundClearReceiptDto.setSdescription(sAddWord);
					DatabaseFacade.getODB().create(tfFundClearReceiptDto);
					
					if ("3".equals((String) batchHeadMap.get("PayoutVouType"))) { //���л���
						if (DealCodeConstants.DEALCODE_TIPS_SUCCESS.equals(sresult) || StateConstant.COMMON_NO.equals(sresult))
							sAddWord = DealCodeConstants.PROCESS_SUCCESS;
					}
					// ���ݴ�����ת������״̬
					sstatus = PublicSearchFacade.getDetailStateByDealCode(sresult);
					if ("1".equals((String) batchHeadMap.get("PayoutVouType"))) { //ʵ��
						updateExce.addParam(sstatus);// ״̬
						updateExce.addParam(sresult);
						updateExce.addParam(sAmt);// ������
						updateExce.addParam(sacctdate);// ��������
						updateExce.addParam(soritrano);// ԭ������ˮ��
						updateExce.addParam(strecode);
						updateExce.addParam(soripackno);
						updateExce.addParam(sorivouno);
						updateExce.addParam(sorivoudate);
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// ������״̬[80000]����ɹ���ƾ֤
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);// ������״̬[80001]����ʧ�ܵ�ƾ֤
						updateExce.runQuery(updateSql);
					}else if("2".equals((String) batchHeadMap.get("PayoutVouType"))){ //�˿�
						/*tfFundAppropriationDto = new TfFundAppropriationDto();
						tfFundAppropriationDto.setSmsgref(msgRef);
						tfFundAppropriationDto.setSpackno(soritrano);
						tmpList = CommonFacade.getODB().findRsByDto(
								tfFundAppropriationDto);
						if (null == tmpList) {
							returnWorld = "δ��ѯ���ʽ���";
							returnStatus = "90001";
							throw new ITFEBizException("δ��ѯ���ʽ���" + msgRef);
						}
						tfFundAppropriationDto = (TfFundAppropriationDto) tmpList
								.get(0);
						if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)) {
							tfFundAppropriationDto.setSstatus("0"); // ����ɹ�
						} else {
							tfFundAppropriationDto.setSaddword(sAddWord);
						}
						DatabaseFacade.getODB().update(tfFundAppropriationDto);
						continue;*/
					}else if("3".equals((String) batchHeadMap.get("PayoutVouType"))){ //���л���
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
					}
					tmpInfoDto = new TvVoucherinfoDto();
					tmpInfoDto.setStrecode(strecode);
					tmpInfoDto.setSvoucherno(sorivouno);
					tmpInfoDto.setSext4(sorivoudate); //ƾ֤����
					tmpInfoDto.setSext1("40");
					tmpList = CommonFacade.getODB().findRsByDto(tmpInfoDto);
					if (null == tmpList || tmpList.size() == 0) {
						returnWorld = "�����ʽ��ִ����2000��δ�ҵ�ԭƾ֤";
						returnStatus = "90001";
						logger.error("�����ʽ��ִ����2000��δ�ҵ�ԭƾ֤��");
						throw new ITFEBizException("�����ʽ��ִ����2000��δ�ҵ�ԭƾ֤��");
					}
					tmpInfoDto = (TvVoucherinfoDto) tmpList.get(0);
					// �����ʽ���״̬1000
					tfFundAppropriationDto = new TfFundAppropriationDto();
					tfFundAppropriationDto.setSmsgref(msgRef);
					tfFundAppropriationDto.setSpackno(soritrano); //ԭ����ˮ����ԭ������ˮ����ͬ(�ڷ����ʽ���ʱ����ˮ���뽻����ˮ����ͬ--��������)
					tmpList = CommonFacade.getODB().findRsByDto(tfFundAppropriationDto);
					if (null == tmpList || tmpList.size() == 0) {
						returnWorld = "δ��ѯ���ʽ���";
						returnStatus = "90001";
						logger.error("δ��ѯ���ʽ���" + msgRef);
						throw new ITFEBizException("δ��ѯ���ʽ���" + msgRef);
					}
					tfFundAppropriationDto = (TfFundAppropriationDto) tmpList.get(0);
					if(!"71727380".contains(tmpInfoDto.getSstatus()))
					{
						if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)) {
							tmpInfoDto.setSext1("41");
							tmpInfoDto.setSdemo("����ɹ�");
							tmpInfoDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);//����ɹ�
							tfFundAppropriationDto.setSstatus("0"); // ����ɹ�
						} else {
							tmpInfoDto.setSext1("42");
							tmpInfoDto.setSdemo("����ʧ��");
							tmpInfoDto.setSstatus(DealCodeConstants.VOUCHER_FAIL_TCBS);//����ʧ��
							tfFundAppropriationDto.setSaddword(sAddWord);
						}
					}
					DatabaseFacade.getODB().update(tfFundAppropriationDto);
					DatabaseFacade.getODB().update(tmpInfoDto);
				}

				// ����ԭ��״̬
				MsgRecvFacade.updateMsgSendLogByMsgId(senddto,DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno, "");
				sendno = senddto.getSsendno();
				recvorg = senddto.getSsendorgcode();
				String filepath = (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER);
				
				// �ǽ�����־
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg,
						sentrustDate, msgNo, sorgcode, filepath,
						batchReturnList.size(), sumamt, spackno, strecode,
						null, null, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo);
			}
		} catch (JAFDatabaseException e) {
			logger.error(e.getMessage(), e);
			returnStatus = "90006";
			returnWorld = e.getMessage();
			throw new ITFEBizException(e.getMessage(), e);
		} catch (SequenceException e) {
			logger.error(e.getMessage(), e);
			returnStatus = "90006";
			returnWorld = e.getMessage();
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error(e.getMessage(), e);
			returnStatus = "90006";
			returnWorld = e.getMessage();
			throw new ITFEBizException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			returnStatus = "90006";
			returnWorld = e.getMessage();
			throw new ITFEBizException(e.getMessage(), e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
			try {
				TvVoucherinfoDto vDto = new TvVoucherinfoDto();
				vDto.setSpaybankcode(sorgcode);
				vDto.setStrecode(strecode);
				
				MuleClient client = new MuleClient();

				Map map = new HashMap();
				MuleMessage message = new DefaultMuleMessage(map);
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY,"TBS_3001_OUT");
				message.setProperty(MessagePropertyKeys.MSG_STATE, returnStatus);
				message.setProperty(MessagePropertyKeys.MSG_ADDWORD, returnWorld);
				message.setProperty(MessagePropertyKeys.MSG_TAG_KEY, msgNo);
				message.setProperty(MessagePropertyKeys.MSG_DESC, sorgcode);
				message.setProperty(MessagePropertyKeys.MSG_REF, msgID);
				message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
				message.setProperty(MessagePropertyKeys.MSG_PACK_NO, spackno);
				//���շ�������ֵ
				message.setProperty(MessagePropertyKeys.MSG_ORGCODE, _srecvno);	//����
				message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, _ssendno);	//����
				
				message.setPayload(map);

				message = client.send("vm://ManagerMsgWithCommBank", message);

			} catch (MuleException e) {
				logger.error(e);
				throw new ITFEBizException(e.getMessage(),e);
			} 
		}
	}

}

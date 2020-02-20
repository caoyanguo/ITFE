package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.deptone.common.core.exception.MessageException;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;

public class Recv9122MsgServer extends AbstractMsgManagerServer {
	
	private static Log logger = LogFactory.getLog(Recv9122MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
			HashMap headMap  = (HashMap) cfxMap.get("HEAD");
			String msgRef  = (String)headMap.get("MsgRef");
			HashMap returnmap = (HashMap) msgMap.get("MsgReturn9122");
			String orimsgno = (String) returnmap.get("OriMsgNo");
			String sorgcode = (String) returnmap.get("OriSendOrgCode");
			String orientrustdate = (String) returnmap.get("OriEntrustDate");
			String orirequestno = (String) returnmap.get("OriRequestNo");
			String result = (String) returnmap.get("Result");
			String addword = (String) returnmap.get("AddWord");
			String msgid = (String) headMap.get("MsgID");
			String sdate = (String) headMap.get("WorkDate");
			String sendno = null ;//������ˮ��
			String sbillorg = null;//��Ʊ��λ
			String packno = null ;//ԭ����
			String strecode = null ;//�������
			
            String sdemo ="ԭ���ı��:"+orimsgno.trim()+",ԭ����ˮ/������ˮ��:"+orirequestno.trim()+",ԭ�����������"+sorgcode.trim()+",ԭί������:"+orientrustdate.trim();
			
			//ƾ֤������
			String state = PublicSearchFacade.getDetailStateByDealCode(result);
			//��־������
			String pkgstate = PublicSearchFacade.getPackageStateByDealCode(result);
			//������־��ˮd
			String _srecvno = StampFacade.getStampSendSeq("JS");
			String recvorg = (String) headMap.get("DES");
			// ����ԭ������־
			TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,
					orimsgno);
			if (null!=senddto) {
				//����ԭ������־��ˮ��
				MsgRecvFacade.updateMsgSendLogByMsgId(senddto, pkgstate, _srecvno, addword);
				HashMap<String, String> bizMsgNoMap = ITFECommonConstant.bizMsgNoList;
				// �����ҵ������Ҫ�����ļ�����Ķ�Ӧ��ϵ��ҵ�����ϸ��״̬
				if (bizMsgNoMap.containsKey(orimsgno)) {
					// �������������ļ�����Ķ�Ӧ��ϵ��
					MsgRecvFacade.updateMsgHeadByMsgId(msgRef, pkgstate, addword);
					// ����ҵ���״̬
					if (!state.equals( DealCodeConstants.DEALCODE_ITFE_SUCCESS)) {
						MsgRecvFacade.updateMsgBizDetailByOriMsgNo(msgRef,
								orimsgno, state, orirequestno, bizMsgNoMap);
					}
				}
				 recvorg =senddto.getSsendorgcode();
				 sendno = senddto.getSsendno();
				 sbillorg = senddto.getSbillorg();
				 packno = senddto.getSpackno();
				 strecode = senddto.getStrecode();
				 if (StateConstant.MSG_SENDER_FLAG_2.equals(senddto.getSifsend())) {
						// д������־
						MsgLogFacade.writeSendLog(_srecvno, sendno,recvorg, sorgcode, orientrustdate,
								(String) headMap.get("MsgNo"), (String) eventContext
										.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
								new BigDecimal(0), null, null, null, sorgcode, null,
								(String) headMap.get("MsgID"),
								DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
								(String) eventContext.getMessage().getProperty(
										MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND);
						Object msg = eventContext.getMessage().getProperty("MSG_INFO");// ���ԭ���ģ����·���
						if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
						{
							//ȡ��ԭ�����ĵ�MQMSGID
							TvMqmessageDto dto = MsgLogFacade.queryMQMSGID(orimsgno, msgRef);
							String correlationId = "ID:524551000000000000000000000000000000000000000000";
							if(dto==null){
								eventContext.getMessage().setCorrelationId(correlationId);
							}else{
								correlationId = dto.getSmqmsgid();
								eventContext.getMessage().setCorrelationId(correlationId);
							}
							try {
								if(!"1102".equals(orimsgno)&&!"1104".equals(orimsgno)&&!"1105".equals(orimsgno))
								{	
									if(recvorg!=null&&recvorg.contains("000002700009"))
									{
										JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, correlationId, false,recvorg);
									}else
									{
										JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, correlationId, false,recvorg);
									}
									eventContext.setStopFurtherProcessing(true);// ������һ������
								}
								else
								{
									eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");//����������Ϣͷд����id
									eventContext.getMessage().setPayload(msg);
								}
							} catch (MessageException e) {
								logger.error(e);
								throw new ITFEBizException("����yak���ͣ��Է��ͱ�������ʱЧ��ʧ�ܣ�",e);
							}
						}else
							 eventContext.getMessage().setPayload(msg);
				 }
			}
			try{
				Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
						MsgConstant.VOUCHER);
				voucher.VoucherReceiveTIPSResult(packno,state,addword,MsgConstant.MSG_NO_9122);
			} catch(Exception e){
				logger.error(e);
				VoucherException.saveErrInfo(null, e);
			}
			
			String sendorg = (String) headMap.get("SRC");
			String biztype = (String) headMap.get("MsgNo");
			String filepath = (String) eventContext.getMessage().getProperty(
					"XML_MSG_FILE_PATH");
//			String stamp = TimeFacade.getCurrentStringTime();
			String ifsend = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_SENDER);
			// �ǽ�����־
			MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sdate, biztype,
					sendorg, filepath, 0, null, packno, strecode, null,
					sbillorg, null, msgid,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, addword, null,
					ifsend, MsgConstant.ITFE_SEND, sdemo );
			

		} catch (SequenceException e) {
			logger.error("����9122����ʧ��!",e);
			throw new ITFEBizException("����9122����ʧ��!",e);
		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

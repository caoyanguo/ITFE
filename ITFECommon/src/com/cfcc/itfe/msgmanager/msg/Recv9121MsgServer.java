package com.cfcc.itfe.msgmanager.msg;

import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
@SuppressWarnings("unchecked")
public class Recv9121MsgServer extends AbstractMsgManagerServer {
	
	private static Log logger = LogFactory.getLog(Recv9121MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			String msgRef  = (String)headMap.get("MsgRef");
			HashMap returnmap = (HashMap) msgMap.get("MsgReturn9121");
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
			String recvorg = (String) headMap.get("DES");
			String sdemo ="ԭ���ı��:"+orimsgno.trim()+",ԭ����ˮ/������ˮ��:"+orirequestno.trim()+",ԭ�����������"+sorgcode.trim()+",ԭί������:"+orientrustdate.trim();
			String ifsendfinc = "";
			//ƾ֤������
			String state = PublicSearchFacade.getDetailStateByDealCode(result);
			//��־������
			String pkgstate = PublicSearchFacade.getPackageStateByDealCode(result);
			//������־��ˮd
			String _srecvno = StampFacade.getStampSendSeq("JS");
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
					//����ҵ���ļ������Ӧ��ϵ������������ף��Ͳ��ڸ��� ��ֹ�ظ�������� ��ӡ������ʾ�ظ� 
					if ( MsgConstant.MSG_NO_7211.equals(orimsgno) && ! DealCodeConstants.DEALCODE_ITFE_RECEIVER.equals(senddto.getSretcode()) ) {
						MsgRecvFacade.updateMsgHeadByMsgId(msgRef, pkgstate, addword);
					}else{
						MsgRecvFacade.updateMsgHeadByMsgId(msgRef, pkgstate, addword);
					}
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
				 ifsendfinc = senddto.getSifsend();
			}
			String sendorg = (String) headMap.get("SRC");
			String biztype = (String) headMap.get("MsgNo");
			String filepath = (String) eventContext.getMessage().getProperty(
					"XML_MSG_FILE_PATH");
			String ifsend = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_SENDER);
			// �ǽ�����־
			MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sdate, biztype,
					sendorg, filepath, 0, null, packno, strecode, null,
					sbillorg, null, msgid,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, addword, null,
					ifsend, MsgConstant.ITFE_SEND, sdemo );
			try{
				
				Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
						MsgConstant.VOUCHER);
				TsTreasuryDto tredto = SrvCacheFacade.cacheTreasuryInfo(null).get(strecode);
				//������ϸ�յ�һ�ʻ�ִ����Ϊ��������
				if (StateConstant.COMMON_YES.equals(String.valueOf(tredto==null?"":tredto.getSpayunitname())) && MsgConstant.MSG_NO_5101.equals(orimsgno)) {
					voucher.VoucherRecTipsByBizno(packno, state, addword);
				}else{
					voucher.VoucherReceiveTIPS(packno,state,addword);
				}
				
			} catch(Exception e){
				logger.error(e);
				VoucherException.saveErrInfo(null, e);
			}
            //���Ϸ��������Ľ���ת��
			if (null!=ifsendfinc && ifsendfinc.equals(StateConstant.MSG_SENDER_FLAG_2)) {
				Object msg = eventContext.getMessage().getProperty("MSG_INFO");
				if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE)){
					//ȡ��ԭ�����ĵ�MQMSGID
					TvMqmessageDto dto = MsgLogFacade.queryMQMSGID(orimsgno, msgRef);
					String mqmsgid = null;
					if(dto==null){
						mqmsgid = "ID:524551000000000000000000000000000000000000000000";
					}else{
						mqmsgid = dto.getSmqmsgid();
						eventContext.getMessage().setCorrelationId(mqmsgid);
					}
					if(!"1102".equals(orimsgno)&&!"1104".equals(orimsgno)&&!"1105".equals(orimsgno)&&!"5001".equals(orimsgno)&&!"5002".equals(orimsgno)&&!"5003".equals(orimsgno)&&!"5101".equals(orimsgno)&&!"5102".equals(orimsgno)&&!"5103".equals(orimsgno))//ʵʱ���ж�̬ȡ��ԭ��Ϣͷ�е�id
					{	
						if(recvorg!=null&&recvorg.contains("000002700009"))
						{
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, mqmsgid, false,recvorg);
						}else{
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, mqmsgid, false,recvorg);
						}
						eventContext.setStopFurtherProcessing(true);// ������һ������
					}
					else
					{
						eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");//����������Ϣͷд����id
						eventContext.getMessage().setPayload(msg);
					}
				}else
					eventContext.getMessage().setPayload(msg);
			} else {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
		}catch (Exception e) {
			logger.error("����9121���Ĵ���ʧ��!", e);
			throw new ITFEBizException("����9121���Ĵ���ʧ��", e);
		}
		
	}

}

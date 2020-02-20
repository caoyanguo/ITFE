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
public class Recv9120MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Recv9120MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			String msgRef = (String) headMap.get("MsgRef");
			String msgid = (String) headMap.get("MsgID");
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
			HashMap returnmap = (HashMap) msgMap.get("MsgReturn9120");
			String orimsgno = (String) returnmap.get("OriMsgNo");
			String result = (String) returnmap.get("Result");
			String addword = (String) returnmap.get("AddWord");
			String info = (String) returnmap.get("Information");
			String sdate = (String) headMap.get("WorkDate");
			String recvorg = (String) headMap.get("DES");
			String sendorg = (String) headMap.get("SRC");
			String sdemo = "ԭ���ı�ţ�" + orimsgno;
			String sendno = null ;//������ˮ��
			String sbillorg = null;//��Ʊ��λ
			String packno = null ;//ԭ����
			String strecode = null ;//�������
			
			String ifsendfinc = "";
			if (null != info) {
				if (info.length() > 100) {
					info = info.substring(0, 100);
				}
			}
			// ת��������
			String state = PublicSearchFacade.getDetailStateByDealCode(result);
			//��������
			String pkgstate = PublicSearchFacade.getPackageStateByDealCode(result);
			// ������־��ˮ
			String _srecvno = StampFacade.getStampSendSeq("JS");
			// ����ԭ������־
			TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,
					orimsgno);
			if (null != senddto) {
				// ����ԭ������־��ˮ��
				MsgRecvFacade.updateMsgSendLogByMsgId(senddto, pkgstate, _srecvno,
						addword);
				HashMap<String, String> bizMsgNoMap = ITFECommonConstant.bizMsgNoList;
				// �����ҵ������Ҫ�����ļ�����Ķ�Ӧ��ϵ��ҵ�����ϸ��״̬
				if (bizMsgNoMap.containsKey(orimsgno)) {
					// �������������ļ�����Ķ�Ӧ��ϵ��
					MsgRecvFacade.updateMsgHeadByMsgId(msgRef, pkgstate, addword);
					if (!state.equals( DealCodeConstants.DEALCODE_ITFE_SUCCESS)) {
					// ����ҵ���״̬
					MsgRecvFacade.updateMsgBizDetailByOriMsgNo(msgRef,
							orimsgno, state, senddto.getSpackno(), bizMsgNoMap);
					}
				}
				sendno = senddto.getSsendno();
				sbillorg = senddto.getSbillorg();
			    packno = senddto.getSpackno();
				strecode = senddto.getStrecode();
				ifsendfinc =senddto.getSifsend(); 
				recvorg =senddto.getSsendorgcode();
			}
		
			try{
				Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
						MsgConstant.VOUCHER);
				TsTreasuryDto tredto = SrvCacheFacade.cacheTreasuryInfo(null).get(strecode);
				//������ϸ�յ�һ�ʻ�ִ����Ϊ�����ѻ�ִ
				if (StateConstant.COMMON_YES.equals(String.valueOf(tredto==null?"":tredto.getSpayunitname())) && MsgConstant.MSG_NO_5101.equals(orimsgno)) {
					voucher.VoucherRecTipsByBizno(packno, state, addword);
				}else{
				    voucher.VoucherReceiveTIPS(packno,state,addword);
				}
			} catch(Exception e){
				logger.error(e);
				VoucherException.saveErrInfo(null, e);
			}
			String biztype = (String) headMap.get("MsgNo");
			String filepath = (String) eventContext.getMessage().getProperty(
					"XML_MSG_FILE_PATH");
			
			String ifsend = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_SENDER);
			// �ǽ�����־
			MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sdate, biztype,
					sendorg, filepath, 0, null, packno, strecode, null,
					sbillorg, null, msgid,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, info, null,
					ifsend, "0", sdemo + "ԭ����ˮ��:" + packno);
			
			if (null!=ifsendfinc && ifsendfinc.equals(StateConstant.MSG_SENDER_FLAG_2)) {
					//ȡ��ԭ�����ĵ�MQMSGID
					TvMqmessageDto dto = MsgLogFacade.queryMQMSGID(orimsgno, msgRef);
					String mqmsgid = "";
					if(dto==null){
						mqmsgid = "ID:524551000000000000000000000000000000000000000000";
					}else{
						mqmsgid = dto.getSmqmsgid();
						eventContext.getMessage().setCorrelationId(mqmsgid);
					}
					Object msg = eventContext.getMessage().getProperty("MSG_INFO");
					if(!"1102".equals(orimsgno)&&!"1104".equals(orimsgno)&&!"1105".equals(orimsgno)&&!"5001".equals(orimsgno)&&!"5002".equals(orimsgno)&&!"5003".equals(orimsgno)&&!"5101".equals(orimsgno)&&!"5102".equals(orimsgno)&&!"5103".equals(orimsgno))
					{
						if(recvorg!=null&&recvorg.contains("000002700009"))
						{
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, mqmsgid, false,recvorg);
						}
						else
						{
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, mqmsgid, false,recvorg);
						}
						eventContext.setStopFurtherProcessing(true);// ������һ������
					}
					else
					{
						eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");//����������Ϣͷд����id
						eventContext.getMessage().setPayload(msg);
					}
			} else {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
			
		} catch (Exception e) {
			logger.error("����9120���Ĵ���ʧ��!", e);
			throw new ITFEBizException("����9120���Ĵ���ʧ��", e);
		}
		
		
		
	}

}

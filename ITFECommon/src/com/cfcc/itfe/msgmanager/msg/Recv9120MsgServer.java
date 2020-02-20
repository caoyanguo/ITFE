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
	 * 报文信息处理
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
			String sdemo = "原报文编号：" + orimsgno;
			String sendno = null ;//发送流水号
			String sbillorg = null;//出票单位
			String packno = null ;//原包号
			String strecode = null ;//国库代码
			
			String ifsendfinc = "";
			if (null != info) {
				if (info.length() > 100) {
					info = info.substring(0, 100);
				}
			}
			// 转换处理码
			String state = PublicSearchFacade.getDetailStateByDealCode(result);
			//包处理吗
			String pkgstate = PublicSearchFacade.getPackageStateByDealCode(result);
			// 接收日志流水
			String _srecvno = StampFacade.getStampSendSeq("JS");
			// 查找原发送日志
			TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,
					orimsgno);
			if (null != senddto) {
				// 更新原发送日志流水号
				MsgRecvFacade.updateMsgSendLogByMsgId(senddto, pkgstate, _srecvno,
						addword);
				HashMap<String, String> bizMsgNoMap = ITFECommonConstant.bizMsgNoList;
				// 如果是业务报文需要更新文件与包的对应关系和业务表明细的状态
				if (bizMsgNoMap.containsKey(orimsgno)) {
					// 根据条件更新文件与包的对应关系表
					MsgRecvFacade.updateMsgHeadByMsgId(msgRef, pkgstate, addword);
					if (!state.equals( DealCodeConstants.DEALCODE_ITFE_SUCCESS)) {
					// 更新业务表状态
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
				//拨款明细收到一笔回执就认为是是已回执
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
			// 记接收日志
			MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sdate, biztype,
					sendorg, filepath, 0, null, packno, strecode, null,
					sbillorg, null, msgid,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, info, null,
					ifsend, "0", sdemo + "原包流水号:" + packno);
			
			if (null!=ifsendfinc && ifsendfinc.equals(StateConstant.MSG_SENDER_FLAG_2)) {
					//取得原发起报文的MQMSGID
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
						eventContext.setStopFurtherProcessing(true);// 不做进一步处理
					}
					else
					{
						eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");//批量队列消息头写死的id
						eventContext.getMessage().setPayload(msg);
					}
			} else {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
			
		} catch (Exception e) {
			logger.error("接收9120报文处理失败!", e);
			throw new ITFEBizException("接收9120报文处理失败", e);
		}
		
		
		
	}

}

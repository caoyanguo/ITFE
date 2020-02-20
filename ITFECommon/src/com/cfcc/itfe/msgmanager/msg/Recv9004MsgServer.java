package com.cfcc.itfe.msgmanager.msg;


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
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.JmsSendUtil;

public class Recv9004MsgServer extends AbstractMsgManagerServer {
	
	private static Log logger = LogFactory.getLog(Recv9004MsgServer.class);

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			String msgRef  = (String)headMap.get("MsgRef");
			HashMap TraStatusReturn9004 = (HashMap) msgMap.get("TraStatusReturn9004");
			String SendOrgCode = (String)TraStatusReturn9004.get("SendOrgCode");
//			String SearchType = (String)TraStatusReturn9004.get("SearchType");
			String OriMsgNo = (String)TraStatusReturn9004.get("OriMsgNo");
			String OriEntrustDate = (String)TraStatusReturn9004.get("OriEntrustDate");
//			String OriPackNo = (String)TraStatusReturn9004.get("OriPackNo");
			String OriTraNo = (String)TraStatusReturn9004.get("OriTraNo");
//			String ChkDate = (String)TraStatusReturn9004.get("ChkDate");
			String OpStat = (String)TraStatusReturn9004.get("OpStat");
			
//			String msgid = (String) headMap.get("MsgID");
//			String sdate = (String) headMap.get("WorkDate");
//			String recvorg = (String) headMap.get("DES");
//			String sendorg = (String) headMap.get("SRC");
//			String biztype = (String) headMap.get("MsgNo");
//			String filepath = (String) eventContext.getMessage().getProperty(
//					"XML_MSG_FILE_PATH");
//			String stamp = TimeFacade.getCurrentStringTime();
//			String ifsend = (String) eventContext.getMessage().getProperty(
//					MessagePropertyKeys.MSG_SENDER);
			String _srecvno  = StampFacade.getStampSendSeq("JS");
			String _ssendno= StampFacade.getStampSendSeq("FS");
//			String orisend=null;

			// 记接收日志
			MsgLogFacade.writeRcvLog(_srecvno, _ssendno, (String) headMap
					.get("DES"), OriEntrustDate, (String) headMap.get("MsgNo"),
					(String) headMap.get("SRC"), (String) eventContext.getMessage()
							.getProperty("XML_MSG_FILE_PATH"), 0, null, OriTraNo, null,
							SendOrgCode, SendOrgCode, null, (String) headMap.get("MsgID"),
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, OpStat, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV_TIPS+(String) headMap.get("MsgNo"));
			//需要查找
			TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,
					MsgConstant.MSG_NO_9003);
		
			if (null!=senddto && null!=senddto.getSifsend() && senddto.getSifsend().equals(StateConstant.MSG_SENDER_FLAG_2)) {
				// 写发送日志
				MsgLogFacade.writeSendLog(_ssendno, _srecvno, (String) headMap
						.get("DES"), (String) headMap.get("DES"), OriEntrustDate,
						(String) headMap.get("MsgNo"), (String) eventContext
								.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
						null, OriTraNo, null, null, SendOrgCode, null, (String) headMap
								.get("MsgID"), DealCodeConstants.DEALCODE_ITFE_SEND,
						null, null, (String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND+(String) headMap.get("MsgNo"));
				Object msg = eventContext.getMessage().getProperty("MSG_INFO");// 获得原报文，重新发送
				if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
				{
					//取得原发起报文的MQMSGID
					TvMqmessageDto dto = MsgLogFacade.queryMQMSGID(OriMsgNo, msgRef);
					String correlationId = "ID:524551000000000000000000000000000000000000000000";
					if(dto==null){
						eventContext.getMessage().setCorrelationId(correlationId);
					}else{
						correlationId = dto.getSmqmsgid();
						eventContext.getMessage().setCorrelationId(correlationId);
					}
					try {
						if(MsgConstant.BATCH_MSG_NO.contains(OriMsgNo)){//走批量队列的回执发送到批量队列
							if(senddto.getStrecode()!=null&&senddto.getStrecode().startsWith("1702"))
								JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_BATCHCITY, (String)msg, correlationId, false,senddto.getStrecode());
							else
								JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_BATCH, (String)msg, correlationId, false,senddto.getStrecode());
						}else{
							if(senddto.getStrecode()!=null&&senddto.getStrecode().startsWith("1702"))
								JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, correlationId, false,senddto.getStrecode());
							else
								JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, correlationId, false,senddto.getStrecode());
						}
						eventContext.setStopFurtherProcessing(true);// 不做进一步处理
					} catch (MessageException e) {
						logger.error(e);
						throw new ITFEBizException("不用yak发送，自发送报文设置时效性失败：",e);
					}
				}else
					eventContext.getMessage().setPayload(msg);
			} else{
				eventContext.setStopFurtherProcessing(true);
				return;
			}
		}catch (Exception e) {
			logger.error("接收9004报文处理失败!", e);
			throw new ITFEBizException("接收9004报文处理失败", e);
		}

	}

}

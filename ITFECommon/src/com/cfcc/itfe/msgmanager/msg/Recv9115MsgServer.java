/**
 * 三方协议验证与撤销应答（9115）
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.deptone.common.core.exception.MessageException;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * @author wangtuo
 * 
 */
public class Recv9115MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9115MsgServer.class);

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		
		// 解析报文头 headMap
		String desorgcode = (String) headMap.get("DES");// 接收机构代码
		String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号
		String MsgRef = (String) headMap.get("MsgRef");// 报文参考号
		String sbookorgcode = desorgcode;
		
		HashMap ProveReturn9115 = (HashMap) msgMap.get("ProveReturn9115");
		
		String OriSendOrgCode = (String) ProveReturn9115.get("OriSendOrgCode"); //发起方代码
		String OriEntrustDate = (String) ProveReturn9115.get("OriEntrustDate");
		String OriVCNo = (String) ProveReturn9115.get("OriVCNo");
//		String VCResult = (String) ProveReturn9115.get("VCResult");
		String AddWord = (String) ProveReturn9115.get("AddWord");
		

		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		String sendseqno;// 发送日志流水号
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(OriSendOrgCode);
			if (null!=_dto) {
				sbookorgcode =_dto.getSorgcode();
			}
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取接收/发送日志SEQ出错");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("根据征收机关代码取核算主体代码出错！");
		}
		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode, OriEntrustDate, msgNo,
				sendorgcode, (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), OriVCNo, null, null, OriSendOrgCode, null,msgid,
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV_TIPS+msgNo+"  ["+AddWord+"] ");

		// 写发送日志
		MsgLogFacade.writeSendLog(sendseqno, recvseqno,  sbookorgcode, OriSendOrgCode,OriEntrustDate,
				msgNo, (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), OriVCNo, null, null, OriSendOrgCode, null,
				msgid,
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND+msgNo+"  ["+AddWord+"] ");
//		if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
//		{
			// 获得原报文，重新发送
			TvMqmessageDto dto = null;
			String correlationId = "ID:524551000000000000000000000000000000000000000000";
			if(MsgRef!=null&&!"".equals(MsgRef))
				dto = MsgLogFacade.queryMQMSGID("9114", MsgRef);
			if(dto==null){
				eventContext.getMessage().setCorrelationId(correlationId);
			}else{
				correlationId = dto.getSmqmsgid();
				eventContext.getMessage().setCorrelationId(correlationId);
			}
			try {
				Object msg = eventContext.getMessage().getProperty("MSG_INFO");// 获得原报文，重新发送
//				if(MsgConstant.BATCH_MSG_NO.contains("9114")){//走批量队列的回执发送到批量队列
//					JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_BATCH, (String)msg, correlationId, false);
//				}else{
				if(desorgcode!=null&&desorgcode.contains("000002700009"))
					JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, correlationId, false,desorgcode);
				else
					JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, correlationId, false,desorgcode);
//				}
				eventContext.setStopFurtherProcessing(true);// 不做进一步处理
			} catch (MessageException e) {
				logger.error(e);
				throw new ITFEBizException("不用yak发送，自发送报文设置时效性失败：",e);
			}
//		}
	}
}

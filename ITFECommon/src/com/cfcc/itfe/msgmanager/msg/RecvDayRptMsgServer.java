/**
 * 收入日报表信息
 */
package com.cfcc.itfe.msgmanager.msg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

/**
 * 
 * @author wangtuo
 * 
 */
public class RecvDayRptMsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(RecvDayRptMsgServer.class);
		/**
		 * 报文信息处理
		 */
		public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
			
//			eventContext.getMessage().setStringProperty(MuleProperties.MULE_REPLY_TO_STOP_PROPERTY, "true");
//			
//			eventContext.getMessage().setReplyTo(null);		
//			
//			Proc3128MsgServer server = new Proc3128MsgServer();
//			
//			server.dealMsg(eventContext);
//			
//			MsgRecvFacade.recvMsglog(eventContext, MsgConstant.MSG_NO_3128);
			
			
			try {
//				MsgRecvFacade.recvMsglog(eventContext, MsgConstant.MSG_NO_3128);
				if (StateConstant.SendFinYes.equals(ITFECommonConstant.IFSENDMSGTOFIN)) {
					//设置TIPS主动下发的MQCORRELID
					eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
					Object msg = eventContext.getMessage().getProperty("MSG_INFO");
					eventContext.getMessage().setPayload(msg);
				} else {
					eventContext.setStopFurtherProcessing(true);
				}
				String path = (String) eventContext.getMessage().getProperty(
				"XML_MSG_FILE_PATH");
				
				MuleMessage message = new DefaultMuleMessage(
						path);
				message.setPayload(path);
				MuleClient client = new MuleClient();
				message.setStringProperty(
						MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_3128);
				message.setStringProperty(
						MessagePropertyKeys.MSG_SENDER, StateConstant.MSG_SENDER_FLAG_9);
				message.setStringProperty(
						MessagePropertyKeys.MSG_FILE_NAME,
						path);
				message = client.send("vm://timersaveprocmsg",message);
//				ServiceUtil.checkResult(message);
			} catch (MuleException e) {
				logger.error(e);
				throw new ITFEBizException("接收3128报文失败!", e);
			}
		}
	}


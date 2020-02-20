package com.cfcc.itfe.component.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.message.ExceptionMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.util.ServiceUtil;

/**
 * 
 * 记录异常的Component
 * 
 */
public class ErrorMsgLogComponent implements Callable {
	private static Log logger = LogFactory.getLog(ErrorMsgLogComponent.class);
	public Object onCall(MuleEventContext p_eventContext) throws Exception {
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode") >= 0){
			return null;
		}
	    p_eventContext.transformMessage();
		MuleMessage msg = p_eventContext.getMessage();
		MsgLogFacade.saveErrInfo(msg);
		String sendby = (String) p_eventContext.getMessage().getProperty("MSG_SENDER");
		if (StateConstant.MSG_SENDER_FLAG_2.equals(sendby)) {
			try {
				String orimsg = (String) p_eventContext.getMessage().getProperty("MSG_INFO");
				if (null == orimsg) {
					orimsg = msg.getOrginalPayload().toString();
				}
				String msgno = (String) msg.getProperty(MessagePropertyKeys.MSG_NO_KEY);
				if (null == msgno) {
					msgno ="0000";
				}
				String s = msg.getExceptionPayload().getRootException().getMessage();
				String msgref="";
				int msgidBegin = orimsg.indexOf("<MsgID>")+7;
				int msgrefEnd = orimsg.indexOf("</MsgID>");
				if (msgidBegin>0 && msgrefEnd > msgidBegin && orimsg.length()>msgidBegin) {
					msgref = orimsg.substring(msgidBegin, msgrefEnd);
				}
				orimsg = orimsg.replace("<", "||");
				orimsg = orimsg.replace(">", "||");
				orimsg = orimsg.replace("</", "||");
			    s = s + "【"+orimsg +"】";
				String desc = "";
				if (null != (s)) {
					desc = s.getBytes().length >= 1024 ? s.substring(0, 1000) : s;
				}
				
				MuleMessage message = new DefaultMuleMessage(desc);
				//报文处理码
				message.setStringProperty(MessagePropertyKeys.MSG_STATE, MsgConstant.MSG_STATE_FAIL);
				//报文处理附言
				message.setStringProperty(MessagePropertyKeys.MSG_ADDWORD, MsgConstant.MSG_STATE_FAIL_ADDWORD);
				//报文标号
				message.setStringProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_9120+"_OUT");
				//报文描述
				message.setStringProperty(MessagePropertyKeys.MSG_DESC, desc);
				//报文标识号
				message.setStringProperty(MessagePropertyKeys.MSG_REF, msgref);
				//报文编号
				message.setStringProperty(MessagePropertyKeys.MSG_TAG_KEY, msgno);
				if(orimsg!=null&&orimsg.contains("<TreCode>1702"))
					p_eventContext.dispatchEvent(message, "vm://ManagerMsgToMofCity");
				else
					p_eventContext.dispatchEvent(message, "vm://ManagerMsgToMof");

			} catch (MuleException e) {
				logger.error("调用后台报文处理的时候出现异常!", e);
				throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);
			}
		} else{
			// 不做进一步处理
			p_eventContext.setStopFurtherProcessing(true);
		}
		p_eventContext.setStopFurtherProcessing(true);
		return "";

	}

}

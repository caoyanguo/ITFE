package com.cfcc.itfe.component;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.msgmanager.msg.ReSendMsgServer;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.core.loader.ContextFactory;
/**
 * 重发报文
 * @author db2admin
 *
 */
public class ReSendMsgLogComponent implements Callable {
	
	public Object onCall(MuleEventContext p_eventContext) throws Exception {
		p_eventContext.transformMessage();
		MuleMessage msg = p_eventContext.getMessage();
		// 取得对应的报文处理类
		ReSendMsgServer msgServer = (ReSendMsgServer) ContextFactory
				.getApplicationContext().getBean(
						"RESEND");
		return msg.getPayload();
	}
}

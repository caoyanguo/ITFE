package com.cfcc.itfe.component;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.msgmanager.msg.ReSendMsgServer;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.core.loader.ContextFactory;
/**
 * �ط�����
 * @author db2admin
 *
 */
public class ReSendMsgLogComponent implements Callable {
	
	public Object onCall(MuleEventContext p_eventContext) throws Exception {
		p_eventContext.transformMessage();
		MuleMessage msg = p_eventContext.getMessage();
		// ȡ�ö�Ӧ�ı��Ĵ�����
		ReSendMsgServer msgServer = (ReSendMsgServer) ContextFactory
				.getApplicationContext().getBean(
						"RESEND");
		return msg.getPayload();
	}
}

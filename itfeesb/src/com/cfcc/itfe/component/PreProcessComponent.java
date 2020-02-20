package com.cfcc.itfe.component;

import org.mule.api.MuleEventContext;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.Callable;

public class PreProcessComponent implements Callable {

	public Object onCall(MuleEventContext eventContext) throws Exception {
		// Object msg=eventContext.transformMessage();
		eventContext.getMessage().setReplyTo(null);
		eventContext.getMessage().setStringProperty(MuleProperties.MULE_REPLY_TO_STOP_PROPERTY, "true");
		// eventContext.getMessage().setPayload(msg);
		return eventContext.getMessage();
	}
}

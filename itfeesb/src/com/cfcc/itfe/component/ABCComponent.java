package com.cfcc.itfe.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.msgmanager.core.IMsgManagerServer;
import com.cfcc.jaf.core.loader.ContextFactory;

/**
 * 
 * 报文发送接收
 * 
 */
public class ABCComponent implements Callable {
	
	private static Log logger = LogFactory.getLog(ABCComponent.class);

	public Object onCall(MuleEventContext eventContext) throws Exception {
		
	
		
//		logger.debug("进行MessageComponent类transformMessage方法处理[1]");
		
		// 接收报文时调用配置的插件进行转化
		eventContext.transformMessage();
		
	eventContext.getMessage().setPayload("ABC");

		return eventContext.getMessage().getPayload();
	}

}

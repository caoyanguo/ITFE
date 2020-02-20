package com.cfcc.itfe.component.common;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.msgmanager.core.IMsgManagerServer;
import com.cfcc.itfe.util.datamove.DataMoveUtil;
import com.cfcc.jaf.core.loader.ContextFactory;

public class DayCutComponent implements Callable {

	public Object onCall(MuleEventContext eventContext) throws Exception {
		DataMoveUtil.timerTaskForDataMove();

//		IMsgManagerServer msgServer = (IMsgManagerServer) ContextFactory
//		.getApplicationContext().getBean(
//				MsgConstant.SPRING_MSG_SERVER + "9999");
//		msgServer.dealMsg(eventContext);
		
		return eventContext.getMessage();
	}
}

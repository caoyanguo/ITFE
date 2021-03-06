package com.cfcc.itfe.msgmanager.msg;


import org.mule.api.MuleEventContext;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;


/**
 *（报文编号涉及：9100　公共数据更新通知）
 * 
 * 只转发财政，不做任何处理
 * 
 */
public class Recv9100MsgServer extends AbstractMsgManagerServer {

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		// 接收Tips发起业务--广西MQ消息规则初始化
		eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}	


/**
 * 征收机关报表
 */
package com.cfcc.itfe.msgmanager.msg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

/**
 * @author wangtuo
 * 
 */
public class Recv3127MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3127MsgServer.class);


	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MsgRecvFacade.recvMsglog(eventContext, MsgConstant.MSG_NO_3127);
	}
}

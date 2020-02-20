package com.cfcc.itfe.msgmanager.msg;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

public class Send1105MsgServer extends AbstractMsgManagerServer {

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		String filename = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_FILE_NAME); // 文件名称 (这里为业务类型)
		String orgcode = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_ORGCODE); // 机构代码
		String packno = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_PACK_NO); // 包流水号 
		String commitdate = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DATE); // 委托日期
		boolean isRepeat =  (Boolean) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_REPEAT); // 是否重发报文
		
	}
}

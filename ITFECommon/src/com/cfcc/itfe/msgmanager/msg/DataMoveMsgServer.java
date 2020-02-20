package com.cfcc.itfe.msgmanager.msg;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.util.datamove.DataMoveUtil;

public class DataMoveMsgServer {

	/**
	 * 报文信息处理
	 */
	public void dealMsg() throws ITFEBizException {
		try {
			DataMoveUtil.timerTaskForDataMove();
		} catch (Exception e1) {
			throw new ITFEBizException("定时转移数据出错!", e1);
		}
	}
}

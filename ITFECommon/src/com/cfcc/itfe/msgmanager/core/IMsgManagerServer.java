package com.cfcc.itfe.msgmanager.core;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.exception.ITFEBizException;

public interface IMsgManagerServer {
	
	/**
	 * ������Ϣ����
	 * @param eventContext
	 * @throws ITFEBizException
	 */
	public abstract void dealMsg(MuleEventContext eventContext) throws ITFEBizException;
}

package com.cfcc.itfe.msgmanager.core;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.core.interfaces.support.AbstractService;

public class AbstractMsgManagerServer extends AbstractService implements IMsgManagerServer{

	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getServiceDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * ÊÂÎï»Ø¹ö
	 */
	public void setRollbackOnly() {
		super.setRollBack(true);
	}

}

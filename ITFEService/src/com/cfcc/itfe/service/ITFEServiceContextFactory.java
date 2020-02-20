package com.cfcc.itfe.service;

import com.cfcc.jaf.core.interfaces.IRequestExtInfo;
import com.cfcc.jaf.core.interfaces.IResponseExtInfo;
import com.cfcc.jaf.core.interfaces.IServiceContext;
import com.cfcc.jaf.core.interfaces.support.BaseServiceContext;
import com.cfcc.jaf.core.interfaces.support.JafServiceContextFactory;

/**
 * @author Caoyg
 * @time 09-10-12 16:40:34
 */
public class ITFEServiceContextFactory extends JafServiceContextFactory {

	public IRequestExtInfo getRequestExtInfo() {
		return new ITFERequestExtInfo();
	}

	public IResponseExtInfo getResponseExtInfo() {
		return new ITFEResponseExtInfo();
	}

	public IServiceContext getServiceContext() {
		BaseServiceContext context = new BaseServiceContext();
		context.setResponseExtInfo(getResponseExtInfo());
		context.setRequestExtInfo(getRequestExtInfo());
		return context;
	}
}

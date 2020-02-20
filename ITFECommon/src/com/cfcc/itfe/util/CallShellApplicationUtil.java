package com.cfcc.itfe.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.config.ITFEGlobalBeanId;


public class CallShellApplicationUtil {


    private static Log log = LogFactory.getLog(CallShellApplicationUtil.class);
	private String appPath = "";
    public static CallShellApplicationUtil getInstance()
    {
    	CallShellApplicationUtil util = (CallShellApplicationUtil) ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.APPLICATION_PATH);
        return util;
    }
	public String getAppPath() {
		return appPath;
	}
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
	
	
}

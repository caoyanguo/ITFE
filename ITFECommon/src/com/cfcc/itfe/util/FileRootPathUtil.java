package com.cfcc.itfe.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.jaf.core.loader.ContextFactory;


public class FileRootPathUtil {


    @SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(FileRootPathUtil.class);
	private String root = "";
    public static FileRootPathUtil getInstance()
    {
    	FileRootPathUtil util = (FileRootPathUtil) ContextFactory.getApplicationContext().getBean("FileRootPathUtil");
        return util;
    }
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}

    
}

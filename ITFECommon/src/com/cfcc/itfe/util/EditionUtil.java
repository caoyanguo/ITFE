package com.cfcc.itfe.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.config.ITFEGlobalBeanId;


public class EditionUtil {


    private static Log log = LogFactory.getLog(EditionUtil.class);
	private String edition = "";
    public static EditionUtil getInstance()
    {
    	EditionUtil util = (EditionUtil) ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.EDITION_UTIL);
        return util;
    }
	public String getEdition() {
		return edition;
	}
	public void setEdition(String edition) {
		this.edition = edition;
	}
	
}

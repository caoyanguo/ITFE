package com.cfcc.itfe.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.config.ITFEGlobalBeanId;


public class AreaSpecUtil {


    private static Log log = LogFactory.getLog(AreaSpecUtil.class);
	private String area = "";
	private String version = "";
	private String sysflag = "";
    public static AreaSpecUtil getInstance()
    {
    	AreaSpecUtil util = (AreaSpecUtil) ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.AREA_UTIL);
        return util;
    }
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSysflag() {
		return sysflag;
	}
	public void setSysflag(String sysflag) {
		this.sysflag = sysflag;
	}
	
	
}

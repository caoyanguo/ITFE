package com.cfcc.itfe.client.dataquery.tmpfilestatistics;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.itfe.service.dataquery.tmpfilestatistics.ITmpfilestatisticsService;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:tmpfilestatistics
 * 组件:Tmpfilestatistics
 */
public abstract class AbstractTmpfilestatisticsBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITmpfilestatisticsService tmpfilestatisticsService = (ITmpfilestatisticsService)getService(ITmpfilestatisticsService.class);
		
	/** 属性列表 */
    String starttime = null;
    String endtime = null;
    String reportpath = null;
    List reportlist = null;
    Map reportmap = null;
    String orgcode = null;
    String trecode = null;
    List trecodelist = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tmpfilestatistics_tmpfilestatistics_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTmpfilestatisticsBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tmpfilestatistics_tmpfilestatistics.properties");
			MESSAGE_PROPERTIES.load(is);
			Set set = MESSAGE_PROPERTIES.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = MESSAGE_PROPERTIES.getProperty((String) key);
				value = new String(value.getBytes("ISO-8859-1"), "GBK");
				MESSAGE_PROPERTIES.setProperty((String) key, value);
			}
		} catch (Exception e) {
			log.warn("为itfe_dataquery_tmpfilestatistics_tmpfilestatistics读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 信息查询
	 * ename: searchDto
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchDto(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 打印信息
	 * ename: toReport
	 * 引用方法: 
	 * viewers: 打印结果
	 * messages: 
	 */
    public String toReport(Object o){
        
        return "打印结果";
    }
    
	/**
	 * Direction: 返回查询界面
	 * ename: toback
	 * 引用方法: 
	 * viewers: 查询信息
	 * messages: 
	 */
    public String toback(Object o){
        
        return "查询信息";
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
		return null;
	}
	

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.rcp.mvc.IModel#getMESSAGE_PROPERTIES()
	 */
	public Properties getMESSAGE_PROPERTIES() {
		return MESSAGE_PROPERTIES;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.rcp.mvc.IModel#getMessage(java.lang.String, java.lang.String)
	 */
	public String getMessage(String _direction, String _msgkey) {
		return MESSAGE_PROPERTIES.getProperty(MESSAGE_KEY_PREFIX + _direction
				+ "_" + _msgkey);
	}

	/**
	 * =========================================================================
	 * getter and setter
	 * =========================================================================
	 */
        
    public java.lang.String getStarttime() {
        return starttime;
    }

    public void setStarttime(java.lang.String _starttime) {
        starttime = _starttime;
    }
    
    public java.lang.String getEndtime() {
        return endtime;
    }

    public void setEndtime(java.lang.String _endtime) {
        endtime = _endtime;
    }
    
    public java.lang.String getReportpath() {
        return reportpath;
    }

    public void setReportpath(java.lang.String _reportpath) {
        reportpath = _reportpath;
    }
    
    public java.util.List getReportlist() {
        return reportlist;
    }

    public void setReportlist(java.util.List _reportlist) {
        reportlist = _reportlist;
    }
    
    public java.util.Map getReportmap() {
        return reportmap;
    }

    public void setReportmap(java.util.Map _reportmap) {
        reportmap = _reportmap;
    }
    
    public java.lang.String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(java.lang.String _orgcode) {
        orgcode = _orgcode;
    }
    
    public java.lang.String getTrecode() {
        return trecode;
    }

    public void setTrecode(java.lang.String _trecode) {
        trecode = _trecode;
    }
    
    public java.util.List getTrecodelist() {
        return trecodelist;
    }

    public void setTrecodelist(java.util.List _trecodelist) {
        trecodelist = _trecodelist;
    }
}
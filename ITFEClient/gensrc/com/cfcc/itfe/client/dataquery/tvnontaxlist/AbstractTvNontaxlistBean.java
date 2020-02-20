package com.cfcc.itfe.client.dataquery.tvnontaxlist;

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
import com.cfcc.itfe.service.dataquery.tvnontaxlist.ITvNontaxlistService;
import com.cfcc.itfe.facade.data.NontaxDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * 子系统: DataQuery
 * 模块:TvNontaxlist
 * 组件:TvNontaxlist
 */
public abstract class AbstractTvNontaxlistBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITvNontaxlistService tvNontaxlistService = (ITvNontaxlistService)getService(ITvNontaxlistService.class);
		
	/** 属性列表 */
    NontaxDto searchDto = null;
    String reportPath = null;
    List rsList = null;
    HashMap paramMap = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tvnontaxlist_tvnontaxlist_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTvNontaxlistBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tvnontaxlist_tvnontaxlist.properties");
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
			log.warn("为itfe_dataquery_tvnontaxlist_tvnontaxlist读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询到结果界面
	 * ename: queryResult
	 * 引用方法: 
	 * viewers: 报表
	 * messages: 
	 */
    public String queryResult(Object o){
        
        return "报表";
    }
    
	/**
	 * Direction: 返回到查询界面
	 * ename: backQuery
	 * 引用方法: 
	 * viewers: 查询界面
	 * messages: 
	 */
    public String backQuery(Object o){
        
        return "查询界面";
    }
    
	/**
	 * Direction: 导出txt
	 * ename: exporttxt
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exporttxt(Object o){
        
        return "";
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
        
    public com.cfcc.itfe.facade.data.NontaxDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.facade.data.NontaxDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public java.lang.String getReportPath() {
        return reportPath;
    }

    public void setReportPath(java.lang.String _reportPath) {
        reportPath = _reportPath;
    }
    
    public java.util.List getRsList() {
        return rsList;
    }

    public void setRsList(java.util.List _rsList) {
        rsList = _rsList;
    }
    
    public java.util.HashMap getParamMap() {
        return paramMap;
    }

    public void setParamMap(java.util.HashMap _paramMap) {
        paramMap = _paramMap;
    }
}
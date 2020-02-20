package com.cfcc.itfe.client.dataquery.querylogs;

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
import com.cfcc.itfe.service.dataquery.querylogs.IQueryLogsService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: DataQuery
 * 模块:querylogs
 * 组件:QueryLogs
 */
public abstract class AbstractQueryLogsBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IQueryLogsService queryLogsService = (IQueryLogsService)getService(IQueryLogsService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_querylogs_querylogs_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractQueryLogsBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_querylogs_querylogs.properties");
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
			log.warn("为itfe_dataquery_querylogs_querylogs读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 收发日志查询
	 * ename: queryLogs
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryLogs(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 日志明细查询
	 * ename: queryLogDetail
	 * 引用方法: 
	 * viewers: 日志详细信息
	 * messages: 
	 */
    public String queryLogDetail(Object o){
        
        return "日志详细信息";
    }
    
	/**
	 * Direction: 返回日志查询
	 * ename: returnQueryLogs
	 * 引用方法: 
	 * viewers: 收发日志查询
	 * messages: 
	 */
    public String returnQueryLogs(Object o){
        
        return "收发日志查询";
    }
    
	/**
	 * Direction: 返回明细查询
	 * ename: returnQueryLogDetail
	 * 引用方法: 
	 * viewers: 收发日志查询
	 * messages: 
	 */
    public String returnQueryLogDetail(Object o){
        
        return "收发日志查询";
    }
    
	/**
	 * Direction: 单选一条记录
	 * ename: selOneRecode
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selOneRecode(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 附件下载
	 * ename: attachDownload
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String attachDownload(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 作废收到的信息
	 * ename: recvDelete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String recvDelete(Object o){
        
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
    }
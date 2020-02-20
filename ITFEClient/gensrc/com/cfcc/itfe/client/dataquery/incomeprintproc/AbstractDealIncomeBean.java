package com.cfcc.itfe.client.dataquery.incomeprintproc;

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
import com.cfcc.itfe.service.dataquery.incomeprintproc.IDealIncomeService;
import com.cfcc.itfe.persistence.dto.TvInfileDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: DataQuery
 * 模块:incomePrintProc
 * 组件:DealIncome
 */
public abstract class AbstractDealIncomeBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IDealIncomeService dealIncomeService = (IDealIncomeService)getService(IDealIncomeService.class);
		
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_incomeprintproc_dealincome_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractDealIncomeBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_incomeprintproc_dealincome.properties");
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
			log.warn("为itfe_dataquery_incomeprintproc_dealincome读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 国库收入查询
	 * ename: queryIncome
	 * 引用方法: 
	 * viewers: 查询处理
	 * messages: 
	 */
    public String queryIncome(Object o){
        
        return "查询处理";
    }
    
	/**
	 * Direction: 返回国库收入查询结果
	 * ename: backSearchResult
	 * 引用方法: 
	 * viewers: 查询处理
	 * messages: 
	 */
    public String backSearchResult(Object o){
        
        return "查询处理";
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
	 * Direction: 报解清单打印
	 * ename: queryPrint
	 * 引用方法: 
	 * viewers: 国库收入报解清单打印
	 * messages: 
	 */
    public String queryPrint(Object o){
        
        return "国库收入报解清单打印";
    }
    
	/**
	 * Direction: 返回国库收入查询
	 * ename: backSearch
	 * 引用方法: 
	 * viewers: 国库收入查询
	 * messages: 
	 */
    public String backSearch(Object o){
        
        return "国库收入查询";
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
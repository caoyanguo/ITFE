package com.cfcc.itfe.client.dataquery.settleaccounts;

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
import com.cfcc.itfe.service.dataquery.settleaccounts.ISettleAccountsService;
import com.cfcc.itfe.persistence.dto.TsUsersDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:settleAccounts
 * 组件:SettleAccounts
 */
public abstract class AbstractSettleAccountsBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ISettleAccountsService settleAccountsService = (ISettleAccountsService)getService(ISettleAccountsService.class);
		
	/** 属性列表 */
    TsUsersDto detailTsUsersDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_settleaccounts_settleaccounts_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractSettleAccountsBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_settleaccounts_settleaccounts.properties");
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
			log.warn("为itfe_dataquery_settleaccounts_settleaccounts读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回
	 * ename: returnback
	 * 引用方法: 
	 * viewers: 查询
	 * messages: 
	 */
    public String returnback(Object o){
        
        return "查询";
    }
    
	/**
	 * Direction: 双击
	 * ename: doubleclick
	 * 引用方法: 
	 * viewers: 用户明细
	 * messages: 
	 */
    public String doubleclick(Object o){
        
        return "用户明细";
    }
    
	/**
	 * Direction: 查询上级
	 * ename: searchupleve
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchupleve(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 查询下级
	 * ename: searchnextleve
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchnextleve(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 表格单击
	 * ename: singleClick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleClick(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TsUsersDto getDetailTsUsersDto() {
        return detailTsUsersDto;
    }

    public void setDetailTsUsersDto(com.cfcc.itfe.persistence.dto.TsUsersDto _detailTsUsersDto) {
        detailTsUsersDto = _detailTsUsersDto;
    }
}
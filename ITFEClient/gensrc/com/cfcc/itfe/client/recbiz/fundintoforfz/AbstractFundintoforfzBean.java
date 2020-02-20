package com.cfcc.itfe.client.recbiz.fundintoforfz;

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
import com.cfcc.itfe.service.recbiz.fundintoforfz.IFundintoforfzService;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:fundintoforfz
 * 组件:Fundintoforfz
 */
public abstract class AbstractFundintoforfzBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IFundintoforfzService fundintoforfzService = (IFundintoforfzService)getService(IFundintoforfzService.class);
		
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_fundintoforfz_fundintoforfz_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractFundintoforfzBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_fundintoforfz_fundintoforfz.properties");
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
			log.warn("为itfe_recbiz_fundintoforfz_fundintoforfz读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 工资文件加载
	 * ename: salarydataload
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String salarydataload(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转工资文件销号列表
	 * ename: gosalarydestoryview
	 * 引用方法: 
	 * viewers: 工资文件销号
	 * messages: 
	 */
    public String gosalarydestoryview(Object o){
        
        return "工资文件销号";
    }
    
	/**
	 * Direction: 返回工资加载页
	 * ename: backsalaryloadview
	 * 引用方法: 
	 * viewers: 工资文件加载
	 * messages: 
	 */
    public String backsalaryloadview(Object o){
        
        return "工资文件加载";
    }
    
	/**
	 * Direction: 工资文件销号操作
	 * ename: salarydestory
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String salarydestory(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 工资文件查询
	 * ename: salarysearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String salarysearch(Object o){
        
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
package com.cfcc.itfe.client.para.tdtaxorgmerger;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tdtaxorgmerger.ITstaxorgcodemergerService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * 子系统: Para
 * 模块:tdtaxorgmerger
 * 组件:Tstaxorgcodemerger
 */
public abstract class AbstractTstaxorgcodemergerBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected ITstaxorgcodemergerService tstaxorgcodemergerService = (ITstaxorgcodemergerService)getService(ITstaxorgcodemergerService.class);
		
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tdtaxorgmerger_tstaxorgcodemerger_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTstaxorgcodemergerBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tdtaxorgmerger_tstaxorgcodemerger.properties");
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
			log.warn("为itfe_para_tdtaxorgmerger_tstaxorgcodemerger读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 增加
	 * ename: add
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String add(Object o){
        
        return "录入界面";
    }
    
	/**
	 * Direction: 修改
	 * ename: modify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String modify(Object o){
        
        return "修改界面";
    }
    
	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String delete(Object o){
        
        return "维护界面";
    }
    
	/**
	 * Direction: 返回维护界面
	 * ename: backmain
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String backmain(Object o){
        
        return "维护界面";
    }
    
	/**
	 * Direction: 到录入界面
	 * ename: toadd
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String toadd(Object o){
        
        return "录入界面";
    }
    
	/**
	 * Direction: 到修改界面
	 * ename: tomodify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String tomodify(Object o){
        
        return "修改界面";
    }
    
	/**
	 * Direction: 单选
	 * ename: singleselect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleselect(Object o){
        
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
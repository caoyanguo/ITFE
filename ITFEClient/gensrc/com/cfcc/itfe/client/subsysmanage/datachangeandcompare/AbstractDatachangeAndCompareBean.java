package com.cfcc.itfe.client.subsysmanage.datachangeandcompare;

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
import com.cfcc.itfe.service.subsysmanage.datachangeandcompare.IDatachangeAndCompareService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:43
 * @generated
 * 子系统: SubSysManage
 * 模块:datachangeAndCompare
 * 组件:DatachangeAndCompare
 */
public abstract class AbstractDatachangeAndCompareBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IDatachangeAndCompareService datachangeAndCompareService = (IDatachangeAndCompareService)getService(IDatachangeAndCompareService.class);
		
	/** 属性列表 */
    String strsql = null;
    List tcfile = null;
    List qcfile = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_subsysmanage_datachangeandcompare_datachangeandcompare_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractDatachangeAndCompareBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_subsysmanage_datachangeandcompare_datachangeandcompare.properties");
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
			log.warn("为itfe_subsysmanage_datachangeandcompare_datachangeandcompare读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 执行SQL
	 * ename: runsql
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String runsql(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 比对
	 * ename: comparedata
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String comparedata(Object o){
        
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
        
    public java.lang.String getStrsql() {
        return strsql;
    }

    public void setStrsql(java.lang.String _strsql) {
        strsql = _strsql;
    }
    
    public java.util.List getTcfile() {
        return tcfile;
    }

    public void setTcfile(java.util.List _tcfile) {
        tcfile = _tcfile;
    }
    
    public java.util.List getQcfile() {
        return qcfile;
    }

    public void setQcfile(java.util.List _qcfile) {
        qcfile = _qcfile;
    }
}
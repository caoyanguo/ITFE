package com.cfcc.itfe.client.dataaudit.finpaystatisticalreport;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.service.dataaudit.finpaystatisticalreport.IFinpaystatisticalreportService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * 子系统: DataAudit
 * 模块:finpaystatisticalreport
 * 组件:Finpaystatisticalreport
 */
public abstract class AbstractFinpaystatisticalreportBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService)getService(IItfeCacheService.class);
	protected IFinpaystatisticalreportService finpaystatisticalreportService = (IFinpaystatisticalreportService)getService(IFinpaystatisticalreportService.class);
		
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataaudit_finpaystatisticalreport_finpaystatisticalreport_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractFinpaystatisticalreportBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataaudit_finpaystatisticalreport_finpaystatisticalreport.properties");
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
			log.warn("为itfe_dataaudit_finpaystatisticalreport_finpaystatisticalreport读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: query
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String query(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回
	 * ename: goBack
	 * 引用方法: 
	 * viewers: 财政预算支出统计报表查询条件
	 * messages: 
	 */
    public String goBack(Object o){
        
        return "财政预算支出统计报表查询条件";
    }
    
	/**
	 * Direction: 导出文件
	 * ename: exportFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportFile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出pdf文件
	 * ename: exportPdfFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportPdfFile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出标准Excel文件
	 * ename: exportExcelFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportExcelFile(Object o){
        
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
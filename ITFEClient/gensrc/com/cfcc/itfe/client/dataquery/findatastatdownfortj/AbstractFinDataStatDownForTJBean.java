package com.cfcc.itfe.client.dataquery.findatastatdownfortj;

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
import com.cfcc.itfe.service.dataquery.findatastatdownfortj.IFinDataStatDownForTJService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:FinDataStatDownForTJ
 * 组件:FinDataStatDownForTJ
 */
public abstract class AbstractFinDataStatDownForTJBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IFinDataStatDownForTJService finDataStatDownForTJService = (IFinDataStatDownForTJService)getService(IFinDataStatDownForTJService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_findatastatdownfortj_findatastatdownfortj_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractFinDataStatDownForTJBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_findatastatdownfortj_findatastatdownfortj.properties");
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
			log.warn("为itfe_dataquery_findatastatdownfortj_findatastatdownfortj读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
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
	 * Direction: 全选
	 * ename: allSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String allSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 下辖数据导出选项
	 * ename: goSelectExport
	 * 引用方法: 
	 * viewers: 下辖国库数据导出选项
	 * messages: 
	 */
    public String goSelectExport(Object o){
        
        return "下辖国库数据导出选项";
    }
    
	/**
	 * Direction: 返回
	 * ename: goback
	 * 引用方法: 
	 * viewers: 报表数据导出（天津）
	 * messages: 
	 */
    public String goback(Object o){
        
        return "报表数据导出（天津）";
    }
    
	/**
	 * Direction: 单选项输入
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 单选项输入
	 * messages: 
	 */
    public String goInput(Object o){
        
        return "单选项输入";
    }
    
	/**
	 * Direction: 单选项输入返回
	 * ename: inputBack
	 * 引用方法: 
	 * viewers: 报表数据导出（天津）
	 * messages: 
	 */
    public String inputBack(Object o){
        
        return "报表数据导出（天津）";
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
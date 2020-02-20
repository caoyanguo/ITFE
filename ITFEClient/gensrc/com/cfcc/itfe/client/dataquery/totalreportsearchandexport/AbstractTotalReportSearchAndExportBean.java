package com.cfcc.itfe.client.dataquery.totalreportsearchandexport;

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
import com.cfcc.itfe.service.dataquery.totalreportsearchandexport.ITotalReportSearchAndExportService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:totalReportSearchAndExport
 * 组件:TotalReportSearchAndExport
 */
public abstract class AbstractTotalReportSearchAndExportBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITotalReportSearchAndExportService totalReportSearchAndExportService = (ITotalReportSearchAndExportService)getService(ITotalReportSearchAndExportService.class);
		
	/** 属性列表 */
    TrIncomedayrptDto findto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_totalreportsearchandexport_totalreportsearchandexport_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTotalReportSearchAndExportBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_totalreportsearchandexport_totalreportsearchandexport.properties");
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
			log.warn("为itfe_dataquery_totalreportsearchandexport_totalreportsearchandexport读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询列表
	 * ename: searchTotalReport
	 * 引用方法: 
	 * viewers: 总额分成列表
	 * messages: 
	 */
    public String searchTotalReport(Object o){
        
        return "总额分成列表";
    }
    
	/**
	 * Direction: 返回查询
	 * ename: returnTotalReport
	 * 引用方法: 
	 * viewers: 总额分成查询
	 * messages: 
	 */
    public String returnTotalReport(Object o){
        
        return "总额分成查询";
    }
    
	/**
	 * Direction: 导出TXT
	 * ename: exportTotalReport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportTotalReport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 全选
	 * ename: selectedAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectedAll(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TrIncomedayrptDto getFindto() {
        return findto;
    }

    public void setFindto(com.cfcc.itfe.persistence.dto.TrIncomedayrptDto _findto) {
        findto = _findto;
    }
}
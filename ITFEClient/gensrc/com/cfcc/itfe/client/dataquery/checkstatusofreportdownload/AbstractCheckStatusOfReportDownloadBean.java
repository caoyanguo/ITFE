package com.cfcc.itfe.client.dataquery.checkstatusofreportdownload;

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
import com.cfcc.itfe.service.dataquery.checkstatusofreportdownload.ICheckStatusOfReportDownloadService;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:checkStatusOfReportDownload
 * 组件:CheckStatusOfReportDownload
 */
public abstract class AbstractCheckStatusOfReportDownloadBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICheckStatusOfReportDownloadService checkStatusOfReportDownloadService = (ICheckStatusOfReportDownloadService)getService(ICheckStatusOfReportDownloadService.class);
		
	/** 属性列表 */
    PagingContext pagingContext = null;
    Date searchdate = null;
    String strecode = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_checkstatusofreportdownload_checkstatusofreportdownload_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractCheckStatusOfReportDownloadBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_checkstatusofreportdownload_checkstatusofreportdownload.properties");
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
			log.warn("为itfe_dataquery_checkstatusofreportdownload_checkstatusofreportdownload读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询下载报表情况检查结果
	 * ename: search
	 * 引用方法: 
	 * viewers: 下载报表情况检查界面
	 * messages: 
	 */
    public String search(Object o){
        
        return "下载报表情况检查界面";
    }
    
	/**
	 * Direction: 打印预览
	 * ename: queryprint
	 * 引用方法: 
	 * viewers: 下载报表情况检查结果报表界面
	 * messages: 
	 */
    public String queryprint(Object o){
        
        return "下载报表情况检查结果报表界面";
    }
    
	/**
	 * Direction: 返回
	 * ename: reback
	 * 引用方法: 
	 * viewers: 下载报表情况检查界面
	 * messages: 
	 */
    public String reback(Object o){
        
        return "下载报表情况检查界面";
    }
    
	/**
	 * Direction: 导出
	 * ename: exportcheckresult
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportcheckresult(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出报表到服务器
	 * ename: exportToServer
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportToServer(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出业务数据到服务器
	 * ename: exportBusData
	 * 引用方法: 
	 * viewers: 导出业务数据到服务器
	 * messages: 
	 */
    public String exportBusData(Object o){
        
        return "导出业务数据到服务器";
    }
    
	/**
	 * Direction: 下载业务数据
	 * ename: downloadBus
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String downloadBus(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 转到导出业务数据页
	 * ename: forwardexportbusdata
	 * 引用方法: 
	 * viewers: 导出业务数据到服务器
	 * messages: 
	 */
    public String forwardexportbusdata(Object o){
        
        return "导出业务数据到服务器";
    }
    
	/**
	 * Direction: 导出tips下发报表
	 * ename: exporttipsreport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exporttipsreport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 勾兑入库
	 * ename: blend
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String blend(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 参数传送
	 * ename: exportParam
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportParam(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 报表传送
	 * ename: exportReport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportReport(Object o){
        
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
        
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingContext() {
        return pagingContext;
    }

    public void setPagingContext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingContext) {
        pagingContext = _pagingContext;
    }
    
    public java.sql.Date getSearchdate() {
        return searchdate;
    }

    public void setSearchdate(java.sql.Date _searchdate) {
        searchdate = _searchdate;
    }
    
    public java.lang.String getStrecode() {
        return strecode;
    }

    public void setStrecode(java.lang.String _strecode) {
        strecode = _strecode;
    }
}
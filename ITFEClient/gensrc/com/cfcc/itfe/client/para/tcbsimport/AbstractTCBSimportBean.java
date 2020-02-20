package com.cfcc.itfe.client.para.tcbsimport;

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
import com.cfcc.itfe.service.para.tcbsimport.ITCBSimportService;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:38
 * @generated
 * 子系统: Para
 * 模块:TCBSimport
 * 组件:TCBSimport
 */
public abstract class AbstractTCBSimportBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITCBSimportService tCBSimportService = (ITCBSimportService)getService(ITCBSimportService.class);
		
	/** 属性列表 */
    List fileList = null;
    List tbsbankList = null;
    List bankCodeList = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tcbsimport_tcbsimport_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTCBSimportBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tcbsimport_tcbsimport.properties");
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
			log.warn("为itfe_para_tcbsimport_tcbsimport读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 导入
	 * ename: fileImport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String fileImport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导入TBS格式行名行号
	 * ename: tbsBankImport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String tbsBankImport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导入
	 * ename: bankCodeImport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String bankCodeImport(Object o){
        
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
        
    public java.util.List getFileList() {
        return fileList;
    }

    public void setFileList(java.util.List _fileList) {
        fileList = _fileList;
    }
    
    public java.util.List getTbsbankList() {
        return tbsbankList;
    }

    public void setTbsbankList(java.util.List _tbsbankList) {
        tbsbankList = _tbsbankList;
    }
    
    public java.util.List getBankCodeList() {
        return bankCodeList;
    }

    public void setBankCodeList(java.util.List _bankCodeList) {
        bankCodeList = _bankCodeList;
    }
}
package com.cfcc.itfe.client.recbiz.uploadmoduleforzj;

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
import com.cfcc.itfe.service.recbiz.uploadmoduleforzj.IUploadUIForZjService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ISequenceHelperService;
import com.cfcc.itfe.persistence.dto.FileResultDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:UploadModuleForZj
 * 组件:UploadUIForZj
 */
public abstract class AbstractUploadUIForZjBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IUploadUIForZjService uploadUIForZjService = (IUploadUIForZjService)getService(IUploadUIForZjService.class);
	protected ISequenceHelperService sequenceHelperService = (ISequenceHelperService)getService(ISequenceHelperService.class);
		
	/** 属性列表 */
    List fileList = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_uploadmoduleforzj_uploaduiforzj_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractUploadUIForZjBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_uploadmoduleforzj_uploaduiforzj.properties");
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
			log.warn("为itfe_recbiz_uploadmoduleforzj_uploaduiforzj读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 数据加载提交
	 * ename: upload
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String upload(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 关闭窗口
	 * ename: close
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String close(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转收入确认提交
	 * ename: gotocommitincome
	 * 引用方法: 
	 * viewers: 收入确认提交
	 * messages: 
	 */
    public String gotocommitincome(Object o){
        
        return "收入确认提交";
    }
    
	/**
	 * Direction: 收入确认提交
	 * ename: commitincome
	 * 引用方法: 
	 * viewers: 数据加载界面
	 * messages: 
	 */
    public String commitincome(Object o){
        
        return "数据加载界面";
    }
    
	/**
	 * Direction: 删除错误数据
	 * ename: delErrorData
	 * 引用方法: 
	 * viewers: 数据加载界面
	 * messages: 
	 */
    public String delErrorData(Object o){
        
        return "数据加载界面";
    }
    
	/**
	 * Direction: 查询文件列表
	 * ename: searchFileListBySrlno
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchFileListBySrlno(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回到数据加载窗口
	 * ename: goback
	 * 引用方法: 
	 * viewers: 数据加载界面
	 * messages: 
	 */
    public String goback(Object o){
        
        return "数据加载界面";
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
}
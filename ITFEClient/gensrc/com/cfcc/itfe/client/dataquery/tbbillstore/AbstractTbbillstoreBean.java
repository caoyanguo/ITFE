package com.cfcc.itfe.client.dataquery.tbbillstore;

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
import com.cfcc.itfe.service.dataquery.tbbillstore.ITbbillstoreService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TbBillstoreDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * 子系统: DataQuery
 * 模块:tbbillstore
 * 组件:Tbbillstore
 */
public abstract class AbstractTbbillstoreBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITbbillstoreService tbbillstoreService = (ITbbillstoreService)getService(ITbbillstoreService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    List fileList = null;
    TbBillstoreDto uploadDto = null;
    TbBillstoreDto searchDto = null;
    List resultList = null;
    TbBillstoreDto selectedDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tbbillstore_tbbillstore_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTbbillstoreBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tbbillstore_tbbillstore.properties");
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
			log.warn("为itfe_dataquery_tbbillstore_tbbillstore读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: searchFileContent
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchFileContent(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 文件下载
	 * ename: download
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String download(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 打开文件
	 * ename: openFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String openFile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 文件上传
	 * ename: fileUpload
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String fileUpload(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 表格单击事件
	 * ename: singletableClick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singletableClick(Object o){
        
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
    
    public com.cfcc.itfe.persistence.dto.TbBillstoreDto getUploadDto() {
        return uploadDto;
    }

    public void setUploadDto(com.cfcc.itfe.persistence.dto.TbBillstoreDto _uploadDto) {
        uploadDto = _uploadDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TbBillstoreDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TbBillstoreDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public java.util.List getResultList() {
        return resultList;
    }

    public void setResultList(java.util.List _resultList) {
        resultList = _resultList;
    }
    
    public com.cfcc.itfe.persistence.dto.TbBillstoreDto getSelectedDto() {
        return selectedDto;
    }

    public void setSelectedDto(com.cfcc.itfe.persistence.dto.TbBillstoreDto _selectedDto) {
        selectedDto = _selectedDto;
    }
}
package com.cfcc.itfe.client.recbiz.tfunitrecordmain;

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
import com.cfcc.itfe.service.recbiz.tfunitrecordmain.ITfUnitrecordmainService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto;
import com.cfcc.itfe.persistence.dto.TfUnitrecordsubDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * 子系统: RecBiz
 * 模块:TfUnitrecordmain
 * 组件:TfUnitrecordmain
 */
public abstract class AbstractTfUnitrecordmainBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITfUnitrecordmainService tfUnitrecordmainService = (ITfUnitrecordmainService)getService(ITfUnitrecordmainService.class);
		
	/** 属性列表 */
    TfUnitrecordmainDto searchDto = null;
    TfUnitrecordmainDto detailDto = null;
    PagingContext pagingContext = null;
    TfUnitrecordsubDto subSearchDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_tfunitrecordmain_tfunitrecordmain_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTfUnitrecordmainBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_tfunitrecordmain_tfunitrecordmain.properties");
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
			log.warn("为itfe_recbiz_tfunitrecordmain_tfunitrecordmain读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: searchDtoList
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String searchDtoList(Object o){
        
        return "查询结果";
    }
    
	/**
	 * Direction: 返回查询
	 * ename: backToSearch
	 * 引用方法: 
	 * viewers: 查询条件
	 * messages: 
	 */
    public String backToSearch(Object o){
        
        return "查询条件";
    }
    
	/**
	 * Direction: 返回查询结果
	 * ename: backToResult
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String backToResult(Object o){
        
        return "查询结果";
    }
    
	/**
	 * Direction: 打开明细
	 * ename: toDetail
	 * 引用方法: 
	 * viewers: 明细信息
	 * messages: 
	 */
    public String toDetail(Object o){
        
        return "明细信息";
    }
    
	/**
	 * Direction: 返回查询结果
	 * ename: detailToResult
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String detailToResult(Object o){
        
        return "查询结果";
    }
    
	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 双击
	 * ename: doubleClick
	 * 引用方法: 
	 * viewers: 子表查询结果
	 * messages: 
	 */
    public String doubleClick(Object o){
        
        return "子表查询结果";
    }
    
	/**
	 * Direction: 子表列表返回主表列表
	 * ename: subBackToSearch
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String subBackToSearch(Object o){
        
        return "查询结果";
    }
    
	/**
	 * Direction: 查询到子表结果页面
	 * ename: subQueryResult
	 * 引用方法: 
	 * viewers: 子表查询结果
	 * messages: 
	 */
    public String subQueryResult(Object o){
        
        return "子表查询结果";
    }
    
	/**
	 * Direction: 法人代码导出
	 * ename: dataExport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dataExport(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto getDetailDto() {
        return detailDto;
    }

    public void setDetailDto(com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto _detailDto) {
        detailDto = _detailDto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingContext() {
        return pagingContext;
    }

    public void setPagingContext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingContext) {
        pagingContext = _pagingContext;
    }
    
    public com.cfcc.itfe.persistence.dto.TfUnitrecordsubDto getSubSearchDto() {
        return subSearchDto;
    }

    public void setSubSearchDto(com.cfcc.itfe.persistence.dto.TfUnitrecordsubDto _subSearchDto) {
        subSearchDto = _subSearchDto;
    }
}
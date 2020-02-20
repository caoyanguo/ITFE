package com.cfcc.itfe.client.para.tsconvertax;

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
import com.cfcc.itfe.service.para.tsconvertax.ITsconvertaxService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsConvertaxDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * 子系统: Para
 * 模块:tsconvertax
 * 组件:Tsconvertax
 */
public abstract class AbstractTsconvertaxBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITsconvertaxService tsconvertaxService = (ITsconvertaxService)getService(ITsconvertaxService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    TsConvertaxDto searchDto = null;
    TsConvertaxDto detailDto = null;
    TsConvertaxDto oriDto = null;
    List searchresult = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsconvertax_tsconvertax_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTsconvertaxBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsconvertax_tsconvertax.properties");
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
			log.warn("为itfe_para_tsconvertax_tsconvertax读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 表格单击
	 * ename: singleclick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclick(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回默认页面
	 * ename: gomainview
	 * 引用方法: 
	 * viewers: 查询
	 * messages: 
	 */
    public String gomainview(Object o){
        
        return "查询";
    }
    
	/**
	 * Direction: 删除
	 * ename: del
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String del(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转录入页面
	 * ename: goaddview
	 * 引用方法: 
	 * viewers: 信息录入
	 * messages: 
	 */
    public String goaddview(Object o){
        
        return "信息录入";
    }
    
	/**
	 * Direction: 跳转更新页面
	 * ename: gomodview
	 * 引用方法: 
	 * viewers: 信息修改
	 * messages: 
	 */
    public String gomodview(Object o){
        
        return "信息修改";
    }
    
	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 添加信息
	 * ename: addInfo
	 * 引用方法: 
	 * viewers: 查询
	 * messages: 
	 */
    public String addInfo(Object o){
        
        return "查询";
    }
    
	/**
	 * Direction: 修改信息
	 * ename: modInfo
	 * 引用方法: 
	 * viewers: 查询
	 * messages: 
	 */
    public String modInfo(Object o){
        
        return "查询";
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
        
    public com.cfcc.itfe.persistence.dto.TsConvertaxDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TsConvertaxDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TsConvertaxDto getDetailDto() {
        return detailDto;
    }

    public void setDetailDto(com.cfcc.itfe.persistence.dto.TsConvertaxDto _detailDto) {
        detailDto = _detailDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TsConvertaxDto getOriDto() {
        return oriDto;
    }

    public void setOriDto(com.cfcc.itfe.persistence.dto.TsConvertaxDto _oriDto) {
        oriDto = _oriDto;
    }
    
    public java.util.List getSearchresult() {
        return searchresult;
    }

    public void setSearchresult(java.util.List _searchresult) {
        searchresult = _searchresult;
    }
}
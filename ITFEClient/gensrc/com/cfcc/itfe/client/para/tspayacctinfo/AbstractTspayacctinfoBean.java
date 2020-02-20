package com.cfcc.itfe.client.para.tspayacctinfo;

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
import com.cfcc.itfe.service.para.tspayacctinfo.ITspayacctinfoService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * 子系统: Para
 * 模块:tspayacctinfo
 * 组件:Tspayacctinfo
 */
public abstract class AbstractTspayacctinfoBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITspayacctinfoService tspayacctinfoService = (ITspayacctinfoService)getService(ITspayacctinfoService.class);
		
	/** 属性列表 */
    PagingContext pagingcontext = null;
    TsPayacctinfoDto searchDto = null;
    TsPayacctinfoDto detailDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tspayacctinfo_tspayacctinfo_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTspayacctinfoBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tspayacctinfo_tspayacctinfo.properties");
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
			log.warn("为itfe_para_tspayacctinfo_tspayacctinfo读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 表格点击
	 * ename: singleclick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclick(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转录入
	 * ename: goinput
	 * 引用方法: 
	 * viewers: 信息录入
	 * messages: 
	 */
    public String goinput(Object o){
        
        return "信息录入";
    }
    
	/**
	 * Direction: 跳转修改
	 * ename: gomod
	 * 引用方法: 
	 * viewers: 修改信息
	 * messages: 
	 */
    public String gomod(Object o){
        
        return "修改信息";
    }
    
	/**
	 * Direction: 删除信息
	 * ename: del
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String del(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 保存
	 * ename: save
	 * 引用方法: 
	 * viewers: 参数查询一览表
	 * messages: 
	 */
    public String save(Object o){
        
        return "参数查询一览表";
    }
    
	/**
	 * Direction: 返回
	 * ename: gomain
	 * 引用方法: 
	 * viewers: 参数查询一览表
	 * messages: 
	 */
    public String gomain(Object o){
        
        return "参数查询一览表";
    }
    
	/**
	 * Direction: 修改信息
	 * ename: mod
	 * 引用方法: 
	 * viewers: 参数查询一览表
	 * messages: 
	 */
    public String mod(Object o){
        
        return "参数查询一览表";
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
        
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TsPayacctinfoDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TsPayacctinfoDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TsPayacctinfoDto getDetailDto() {
        return detailDto;
    }

    public void setDetailDto(com.cfcc.itfe.persistence.dto.TsPayacctinfoDto _detailDto) {
        detailDto = _detailDto;
    }
}
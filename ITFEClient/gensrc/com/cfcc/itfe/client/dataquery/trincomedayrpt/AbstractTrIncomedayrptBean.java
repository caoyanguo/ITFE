package com.cfcc.itfe.client.dataquery.trincomedayrpt;

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
import com.cfcc.itfe.service.dataquery.trincomedayrpt.ITrIncomedayrptService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: DataQuery
 * 模块:TrIncomedayrpt
 * 组件:TrIncomedayrpt
 */
public abstract class AbstractTrIncomedayrptBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITrIncomedayrptService trIncomedayrptService = (ITrIncomedayrptService)getService(ITrIncomedayrptService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService)getService(IItfeCacheService.class);
		
	/** 属性列表 */
    TrIncomedayrptDto dto = null;
    PagingContext pagingcontext = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_trincomedayrpt_trincomedayrpt_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTrIncomedayrptBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_trincomedayrpt_trincomedayrpt.properties");
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
			log.warn("为itfe_dataquery_trincomedayrpt_trincomedayrpt读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
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
	 * Direction: 查询
	 * ename: query
	 * 引用方法: 
	 * viewers: 财政申请收入日报表查询结果
	 * messages: 
	 */
    public String query(Object o){
        
        return "财政申请收入日报表查询结果";
    }
    
	/**
	 * Direction: 返回
	 * ename: goBack
	 * 引用方法: 
	 * viewers: 财政申请收入日报表查询
	 * messages: 
	 */
    public String goBack(Object o){
        
        return "财政申请收入日报表查询";
    }
    
	/**
	 * Direction: 导出文档
	 * ename: exporttxt
	 * 引用方法: 
	 * viewers: 财政申请收入日报表查询
	 * messages: 
	 */
    public String exporttxt(Object o){
        
        return "财政申请收入日报表查询";
    }
    
	/**
	 * Direction: 导出数据
	 * ename: exportTable
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportTable(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出文档不分征收机关
	 * ename: exporttxt0
	 * 引用方法: 
	 * viewers: 财政申请收入日报表查询
	 * messages: 
	 */
    public String exporttxt0(Object o){
        
        return "财政申请收入日报表查询";
    }
    
	/**
	 * Direction: 返回到查询结果
	 * ename: rebackToSelectResult
	 * 引用方法: 
	 * viewers: 财政申请收入日报表查询结果
	 * messages: 
	 */
    public String rebackToSelectResult(Object o){
        
        return "财政申请收入日报表查询结果";
    }
    
	/**
	 * Direction: 报表预览
	 * ename: printReport
	 * 引用方法: 
	 * viewers: 财政申请收入日报表打印界面
	 * messages: 
	 */
    public String printReport(Object o){
        
        return "财政申请收入日报表打印界面";
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
        
    public com.cfcc.itfe.persistence.dto.TrIncomedayrptDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TrIncomedayrptDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
}
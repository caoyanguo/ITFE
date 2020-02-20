package com.cfcc.itfe.client.dataquery.tvdwbkquery;

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
import com.cfcc.itfe.service.dataquery.tvdwbkquery.ITvDwbkQueryService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: DataQuery
 * 模块:tvDwbkQuery
 * 组件:TvDwbkQuery
 */
public abstract class AbstractTvDwbkQueryBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITvDwbkQueryService tvDwbkQueryService = (ITvDwbkQueryService)getService(ITvDwbkQueryService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    TvDwbkDto dto = null;
    PagingContext pagingcontext = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tvdwbkquery_tvdwbkquery_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTvDwbkQueryBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tvdwbkquery_tvdwbkquery.properties");
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
			log.warn("为itfe_dataquery_tvdwbkquery_tvdwbkquery读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: 退库信息列表
	 * messages: 
	 */
    public String search(Object o){
        
        return "退库信息列表";
    }
    
	/**
	 * Direction: 返回
	 * ename: reback
	 * 引用方法: 
	 * viewers: 退库查询界面
	 * messages: 
	 */
    public String reback(Object o){
        
        return "退库查询界面";
    }
    
	/**
	 * Direction: 退库对账单打印
	 * ename: queryPrint
	 * 引用方法: 
	 * viewers: 退库对账单
	 * messages: 
	 */
    public String queryPrint(Object o){
        
        return "退库对账单";
    }
    
	/**
	 * Direction: 返回查询结果
	 * ename: backQueryResult
	 * 引用方法: 
	 * viewers: 退库信息列表
	 * messages: 
	 */
    public String backQueryResult(Object o){
        
        return "退库信息列表";
    }
    
	/**
	 * Direction: 更新失败
	 * ename: updateFail
	 * 引用方法: 
	 * viewers: 退库信息列表
	 * messages: 
	 */
    public String updateFail(Object o){
        
        return "退库信息列表";
    }
    
	/**
	 * Direction: 全选
	 * ename: selectAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 更新成功
	 * ename: updateSuccess
	 * 引用方法: 
	 * viewers: 退库信息列表
	 * messages: 
	 */
    public String updateSuccess(Object o){
        
        return "退库信息列表";
    }
    
	/**
	 * Direction: 设置退回
	 * ename: setback
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String setback(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出文件csv
	 * ename: exportfile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportfile(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TvDwbkDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TvDwbkDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
}
package com.cfcc.itfe.client.dataquery.trstockdayrpt;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.trstockdayrpt.ITrStockdayrptService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: DataQuery
 * 模块:TrStockdayrpt
 * 组件:TrStockdayrpt
 */
public abstract class AbstractTrStockdayrptBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected ITrStockdayrptService trStockdayrptService = (ITrStockdayrptService)getService(ITrStockdayrptService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService)getService(IItfeCacheService.class);
		
	/** 属性列表 */
    TrStockdayrptDto dto = null;
    PagingContext pagingcontext = null;
    String startDate = null;
    String endDate = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_trstockdayrpt_trstockdayrpt_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTrStockdayrptBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_trstockdayrpt_trstockdayrpt.properties");
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
			log.warn("为itfe_dataquery_trstockdayrpt_trstockdayrpt读取messages出错", e);
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
	 * viewers: 库存日报表查询结果
	 * messages: 
	 */
    public String query(Object o){
        
        return "库存日报表查询结果";
    }
    
	/**
	 * Direction: 返回
	 * ename: goBack
	 * 引用方法: 
	 * viewers: 库存日报表查询条件
	 * messages: 
	 */
    public String goBack(Object o){
        
        return "库存日报表查询条件";
    }
    
	/**
	 * Direction: 导出库存txt
	 * ename: exporttxt
	 * 引用方法: 
	 * viewers: 库存日报表查询条件
	 * messages: 
	 */
    public String exporttxt(Object o){
        
        return "库存日报表查询条件";
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
	 * Direction: 导出同账户同日累计余额数据
	 * ename: exportBalData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportBalData(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TrStockdayrptDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TrStockdayrptDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public java.lang.String getStartDate() {
        return startDate;
    }

    public void setStartDate(java.lang.String _startDate) {
        startDate = _startDate;
    }
    
    public java.lang.String getEndDate() {
        return endDate;
    }

    public void setEndDate(java.lang.String _endDate) {
        endDate = _endDate;
    }
}
package com.cfcc.itfe.client.dataquery.tffundclearreceipt;

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
import com.cfcc.itfe.service.dataquery.tffundclearreceipt.ITfFundClearReceiptService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TfFundClearReceiptDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * 子系统: DataQuery
 * 模块:TfFundClearReceipt
 * 组件:TfFundClearReceipt
 */
public abstract class AbstractTfFundClearReceiptBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITfFundClearReceiptService tfFundClearReceiptService = (ITfFundClearReceiptService)getService(ITfFundClearReceiptService.class);
		
	/** 属性列表 */
    TfFundClearReceiptDto searchDto = null;
    PagingContext pagingcontext = null;
    TfFundClearReceiptDto printDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tffundclearreceipt_tffundclearreceipt_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTfFundClearReceiptBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tffundclearreceipt_tffundclearreceipt.properties");
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
			log.warn("为itfe_dataquery_tffundclearreceipt_tffundclearreceipt读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询到结果界面
	 * ename: queryResult
	 * 引用方法: 
	 * viewers: 查询结果界面
	 * messages: 
	 */
    public String queryResult(Object o){
        
        return "查询结果界面";
    }
    
	/**
	 * Direction: 返回到查询界面
	 * ename: backQuery
	 * 引用方法: 
	 * viewers: 查询界面
	 * messages: 
	 */
    public String backQuery(Object o){
        
        return "查询界面";
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
	 * Direction: 报文明细
	 * ename: detail
	 * 引用方法: 
	 * viewers: 报文明细
	 * messages: 
	 */
    public String detail(Object o){
        
        return "报文明细";
    }
    
	/**
	 * Direction: 返回明细
	 * ename: backToDetail
	 * 引用方法: 
	 * viewers: 报文明细
	 * messages: 
	 */
    public String backToDetail(Object o){
        
        return "报文明细";
    }
    
	/**
	 * Direction: 返回明细
	 * ename: backToModifyDetail
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String backToModifyDetail(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TfFundClearReceiptDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TfFundClearReceiptDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TfFundClearReceiptDto getPrintDto() {
        return printDto;
    }

    public void setPrintDto(com.cfcc.itfe.persistence.dto.TfFundClearReceiptDto _printDto) {
        printDto = _printDto;
    }
}
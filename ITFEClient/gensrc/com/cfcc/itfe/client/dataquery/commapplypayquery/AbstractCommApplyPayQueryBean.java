package com.cfcc.itfe.client.dataquery.commapplypayquery;

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
import com.cfcc.itfe.service.dataquery.commapplypayquery.ICommApplyPayQueryService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.HtvPayreckBankDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:commApplyPayQuery
 * 组件:CommApplyPayQuery
 */
public abstract class AbstractCommApplyPayQueryBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommApplyPayQueryService commApplyPayQueryService = (ICommApplyPayQueryService)getService(ICommApplyPayQueryService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    List selecteddatalist = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_commapplypayquery_commapplypayquery_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractCommApplyPayQueryBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_commapplypayquery_commapplypayquery.properties");
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
			log.warn("为itfe_dataquery_commapplypayquery_commapplypayquery读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询列表事件
	 * ename: searchList
	 * 引用方法: 
	 * viewers: 商行办理支付划款申请主体信息列表
	 * messages: 
	 */
    public String searchList(Object o){
        
        return "商行办理支付划款申请主体信息列表";
    }
    
	/**
	 * Direction: 返回查询界面
	 * ename: rebackSearch
	 * 引用方法: 
	 * viewers: 商行办理支付划款申请查询界面
	 * messages: 
	 */
    public String rebackSearch(Object o){
        
        return "商行办理支付划款申请查询界面";
    }
    
	/**
	 * Direction: 主信息单击事件
	 * ename: singleclickMain
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclickMain(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出txt
	 * ename: exportCommApplyPay
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportCommApplyPay(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出选中回单
	 * ename: exportPartCommApplyPay
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportPartCommApplyPay(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 主信息双击事件
	 * ename: doubleclickMain
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleclickMain(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出文件CSV
	 * ename: exportFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportFile(Object o){
        
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
        
    public java.util.List getSelecteddatalist() {
        return selecteddatalist;
    }

    public void setSelecteddatalist(java.util.List _selecteddatalist) {
        selecteddatalist = _selecteddatalist;
    }
}
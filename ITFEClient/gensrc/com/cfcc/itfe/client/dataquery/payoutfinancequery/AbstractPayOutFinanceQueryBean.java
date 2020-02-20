package com.cfcc.itfe.client.dataquery.payoutfinancequery;

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
import com.cfcc.itfe.service.dataquery.payoutfinancequery.IPayOutFinanceQueryService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceMainDto;
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
 * 模块:PayOutFinanceQuery
 * 组件:PayOutFinanceQuery
 */
public abstract class AbstractPayOutFinanceQueryBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IPayOutFinanceQueryService payOutFinanceQueryService = (IPayOutFinanceQueryService)getService(IPayOutFinanceQueryService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_payoutfinancequery_payoutfinancequery_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractPayOutFinanceQueryBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_payoutfinancequery_payoutfinancequery.properties");
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
			log.warn("为itfe_dataquery_payoutfinancequery_payoutfinancequery读取messages出错", e);
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
	 * viewers: 批量拨付信息列表
	 * messages: 
	 */
    public String searchList(Object o){
        
        return "批量拨付信息列表";
    }
    
	/**
	 * Direction: 返回查询界面
	 * ename: rebackSearch
	 * 引用方法: 
	 * viewers: 批量拨付查询界面
	 * messages: 
	 */
    public String rebackSearch(Object o){
        
        return "批量拨付查询界面";
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
	 * Direction: 退库对账单打印
	 * ename: queryPrint
	 * 引用方法: 
	 * viewers: 批量拨付对账单
	 * messages: 
	 */
    public String queryPrint(Object o){
        
        return "批量拨付对账单";
    }
    
	/**
	 * Direction: 往帐清单打印
	 * ename: wzqueryPrint
	 * 引用方法: 
	 * viewers: 批量拨付往帐清单
	 * messages: 
	 */
    public String wzqueryPrint(Object o){
        
        return "批量拨付往帐清单";
    }
    
	/**
	 * Direction: 返回列表界面
	 * ename: rebackInfo
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String rebackInfo(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出
	 * ename: dataExport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dataExport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 全选/反选
	 * ename: selectAllOrNone
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAllOrNone(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 更新成功
	 * ename: updateSuccess
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String updateSuccess(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 更新失败
	 * ename: updateFail
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String updateFail(Object o){
        
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
    }
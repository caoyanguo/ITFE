package com.cfcc.itfe.client.dataquery.remindinfosearch;

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
import com.cfcc.itfe.service.dataquery.remindinfosearch.IRemindInfoSearchService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * 子系统: DataQuery
 * 模块:remindInfoSearch
 * 组件:RemindInfoSearch
 */
public abstract class AbstractRemindInfoSearchBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IRemindInfoSearchService remindInfoSearchService = (IRemindInfoSearchService)getService(IRemindInfoSearchService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    List voucherTypeList = null;
    Date acctDate = null;
    String voucherType = null;
    List remindList = null;
    String remindIs = null;
    List voucherSourceList = null;
    String voucherSource = null;
    PagingContext pagingcontext = null;
    List serverResult = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_remindinfosearch_remindinfosearch_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractRemindInfoSearchBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_remindinfosearch_remindinfosearch.properties");
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
			log.warn("为itfe_dataquery_remindinfosearch_remindinfosearch读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询提醒信息
	 * ename: searchInfo
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchInfo(Object o){
        
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
        
    public java.util.List getVoucherTypeList() {
        return voucherTypeList;
    }

    public void setVoucherTypeList(java.util.List _voucherTypeList) {
        voucherTypeList = _voucherTypeList;
    }
    
    public java.sql.Date getAcctDate() {
        return acctDate;
    }

    public void setAcctDate(java.sql.Date _acctDate) {
        acctDate = _acctDate;
    }
    
    public java.lang.String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(java.lang.String _voucherType) {
        voucherType = _voucherType;
    }
    
    public java.util.List getRemindList() {
        return remindList;
    }

    public void setRemindList(java.util.List _remindList) {
        remindList = _remindList;
    }
    
    public java.lang.String getRemindIs() {
        return remindIs;
    }

    public void setRemindIs(java.lang.String _remindIs) {
        remindIs = _remindIs;
    }
    
    public java.util.List getVoucherSourceList() {
        return voucherSourceList;
    }

    public void setVoucherSourceList(java.util.List _voucherSourceList) {
        voucherSourceList = _voucherSourceList;
    }
    
    public java.lang.String getVoucherSource() {
        return voucherSource;
    }

    public void setVoucherSource(java.lang.String _voucherSource) {
        voucherSource = _voucherSource;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public java.util.List getServerResult() {
        return serverResult;
    }

    public void setServerResult(java.util.List _serverResult) {
        serverResult = _serverResult;
    }
}
package com.cfcc.itfe.client.dataquery.organdmoneydetail;

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
import com.cfcc.itfe.service.para.tstreasury.ITsTreasuryService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
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
 * 模块:orgAndMoneyDetail
 * 组件:OrgAndMoneyDetail
 */
public abstract class AbstractOrgAndMoneyDetailBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITsTreasuryService tsTreasuryService = (ITsTreasuryService)getService(ITsTreasuryService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    List filepath = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_organdmoneydetail_organdmoneydetail_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractOrgAndMoneyDetailBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_organdmoneydetail_organdmoneydetail.properties");
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
			log.warn("为itfe_dataquery_organdmoneydetail_organdmoneydetail读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 省级预算收入报表解付
	 * ename: provinceDetail
	 * 引用方法: 
	 * viewers: 预算收入报表解付报表界面
	 * messages: 
	 */
    public String provinceDetail(Object o){
        
        return "预算收入报表解付报表界面";
    }
    
	/**
	 * Direction: 市级预算收入报表解付
	 * ename: cityDetail
	 * 引用方法: 
	 * viewers: 预算收入报表解付报表界面
	 * messages: 
	 */
    public String cityDetail(Object o){
        
        return "预算收入报表解付报表界面";
    }
    
	/**
	 * Direction: 区县级预算收入报表解付
	 * ename: townDetail
	 * 引用方法: 
	 * viewers: 预算收入报表解付报表界面
	 * messages: 
	 */
    public String townDetail(Object o){
        
        return "预算收入报表解付报表界面";
    }
    
	/**
	 * Direction: 返回
	 * ename: reback
	 * 引用方法: 
	 * viewers: 文件导入界面
	 * messages: 
	 */
    public String reback(Object o){
        
        return "文件导入界面";
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
        
    public java.util.List getFilepath() {
        return filepath;
    }

    public void setFilepath(java.util.List _filepath) {
        filepath = _filepath;
    }
}
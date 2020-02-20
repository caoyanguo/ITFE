package com.cfcc.itfe.client.dataquery.deletedataquery;

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
 * 模块:deleteDataQuery
 * 组件:DeleteDataQuery
 */
public abstract class AbstractDeleteDataQueryBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    String sbiztype = null;
    String svouno = null;
    Date startdate = null;
    Date enddate = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_deletedataquery_deletedataquery_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractDeleteDataQueryBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_deletedataquery_deletedataquery.properties");
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
			log.warn("为itfe_dataquery_deletedataquery_deletedataquery读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询打印
	 * ename: queryService
	 * 引用方法: 
	 * viewers: 销号日志界面
	 * messages: 
	 */
    public String queryService(Object o){
        
        return "销号日志界面";
    }
    
	/**
	 * Direction: 返回到默认界面
	 * ename: backToDef
	 * 引用方法: 
	 * viewers: 销号操作数据查询
	 * messages: 
	 */
    public String backToDef(Object o){
        
        return "销号操作数据查询";
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
        
    public java.lang.String getSbiztype() {
        return sbiztype;
    }

    public void setSbiztype(java.lang.String _sbiztype) {
        sbiztype = _sbiztype;
    }
    
    public java.lang.String getSvouno() {
        return svouno;
    }

    public void setSvouno(java.lang.String _svouno) {
        svouno = _svouno;
    }
    
    public java.sql.Date getStartdate() {
        return startdate;
    }

    public void setStartdate(java.sql.Date _startdate) {
        startdate = _startdate;
    }
    
    public java.sql.Date getEnddate() {
        return enddate;
    }

    public void setEnddate(java.sql.Date _enddate) {
        enddate = _enddate;
    }
}
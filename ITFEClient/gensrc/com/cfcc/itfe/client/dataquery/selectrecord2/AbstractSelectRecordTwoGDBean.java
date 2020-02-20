package com.cfcc.itfe.client.dataquery.selectrecord2;

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
import com.cfcc.itfe.service.dataquery.selectrecord1.ISelectRecordGDService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * 子系统: DataQuery
 * 模块:selectRecord2
 * 组件:SelectRecordTwoGD
 */
public abstract class AbstractSelectRecordTwoGDBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ISelectRecordGDService selectRecordGDService = (ISelectRecordGDService)getService(ISelectRecordGDService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    TvPayreckBankDto searchDto = null;
    PagingContext pagingcontext = null;
    Date startDate = null;
    Date endDate = null;
    BigDecimal startMoney = null;
    BigDecimal endMoney = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_selectrecord2_selectrecordtwogd_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractSelectRecordTwoGDBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_selectrecord2_selectrecordtwogd.properties");
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
			log.warn("为itfe_dataquery_selectrecord2_selectrecordtwogd读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: queryInfo
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryInfo(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 打印
	 * ename: queryPrint
	 * 引用方法: 
	 * viewers: 打印界面
	 * messages: 
	 */
    public String queryPrint(Object o){
        
        return "打印界面";
    }
    
	/**
	 * Direction: 返回
	 * ename: backQuery
	 * 引用方法: 
	 * viewers: 查询界面
	 * messages: 
	 */
    public String backQuery(Object o){
        
        return "查询界面";
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
        
    public com.cfcc.itfe.persistence.dto.TvPayreckBankDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TvPayreckBankDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public java.sql.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.sql.Date _startDate) {
        startDate = _startDate;
    }
    
    public java.sql.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.sql.Date _endDate) {
        endDate = _endDate;
    }
    
    public java.math.BigDecimal getStartMoney() {
        return startMoney;
    }

    public void setStartMoney(java.math.BigDecimal _startMoney) {
        startMoney = _startMoney;
    }
    
    public java.math.BigDecimal getEndMoney() {
        return endMoney;
    }

    public void setEndMoney(java.math.BigDecimal _endMoney) {
        endMoney = _endMoney;
    }
}
package com.cfcc.itfe.client.dataquery.tvamtcontrolinfo;

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
import com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:TvAmtControlInfo
 * 组件:TvAmtControlInfo
 */
public abstract class AbstractTvAmtControlInfoBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    TvAmtControlInfoDto selectedDto = null;
    TvAmtControlInfoDto searchDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tvamtcontrolinfo_tvamtcontrolinfo_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTvAmtControlInfoBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tvamtcontrolinfo_tvamtcontrolinfo.properties");
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
			log.warn("为itfe_dataquery_tvamtcontrolinfo_tvamtcontrolinfo读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 单击
	 * ename: singleclick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclick(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 双击
	 * ename: doubleclick
	 * 引用方法: 
	 * viewers: 明细信息
	 * messages: 
	 */
    public String doubleclick(Object o){
        
        return "明细信息";
    }
    
	/**
	 * Direction: 返回
	 * ename: backmain
	 * 引用方法: 
	 * viewers: 额度控制信息查询
	 * messages: 
	 */
    public String backmain(Object o){
        
        return "额度控制信息查询";
    }
    
	/**
	 * Direction: 查询
	 * ename: searchinfo
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchinfo(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转至详细界面
	 * ename: godetailview
	 * 引用方法: 
	 * viewers: 明细信息
	 * messages: 
	 */
    public String godetailview(Object o){
        
        return "明细信息";
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
        
    public com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto getSelectedDto() {
        return selectedDto;
    }

    public void setSelectedDto(com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto _selectedDto) {
        selectedDto = _selectedDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto _searchDto) {
        searchDto = _searchDto;
    }
}
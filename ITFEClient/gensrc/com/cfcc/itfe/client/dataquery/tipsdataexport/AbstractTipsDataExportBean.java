package com.cfcc.itfe.client.dataquery.tipsdataexport;

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
import com.cfcc.itfe.service.dataquery.tipsdataexport.ITipsDataExportService;
import com.cfcc.itfe.service.dataquery.querylogs.IQueryLogsService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: DataQuery
 * 模块:TipsDataExport
 * 组件:TipsDataExport
 */
public abstract class AbstractTipsDataExportBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITipsDataExportService tipsDataExportService = (ITipsDataExportService)getService(ITipsDataExportService.class);
	protected IQueryLogsService queryLogsService = (IQueryLogsService)getService(IQueryLogsService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService)getService(IItfeCacheService.class);
		
	/** 属性列表 */
    String sorgcode = null;
    String staxorgcode = null;
    String strecode = null;
    String sbeflag = null;
    Date startdate = null;
    Date enddate = null;
    String ifsub = null;
    String exptype = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tipsdataexport_tipsdataexport_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTipsDataExportBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tipsdataexport_tipsdataexport.properties");
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
			log.warn("为itfe_dataquery_tipsdataexport_tipsdataexport读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 导出数据
	 * ename: exportData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportData(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 定时导出
	 * ename: timerExport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String timerExport(Object o){
        
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
        
    public java.lang.String getSorgcode() {
        return sorgcode;
    }

    public void setSorgcode(java.lang.String _sorgcode) {
        sorgcode = _sorgcode;
    }
    
    public java.lang.String getStaxorgcode() {
        return staxorgcode;
    }

    public void setStaxorgcode(java.lang.String _staxorgcode) {
        staxorgcode = _staxorgcode;
    }
    
    public java.lang.String getStrecode() {
        return strecode;
    }

    public void setStrecode(java.lang.String _strecode) {
        strecode = _strecode;
    }
    
    public java.lang.String getSbeflag() {
        return sbeflag;
    }

    public void setSbeflag(java.lang.String _sbeflag) {
        sbeflag = _sbeflag;
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
    
    public java.lang.String getIfsub() {
        return ifsub;
    }

    public void setIfsub(java.lang.String _ifsub) {
        ifsub = _ifsub;
    }
    
    public java.lang.String getExptype() {
        return exptype;
    }

    public void setExptype(java.lang.String _exptype) {
        exptype = _exptype;
    }
}
package com.cfcc.itfe.client.dataquery.trincomereport;

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
import com.cfcc.itfe.service.dataquery.trincomereport.ITrIncomeReportService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:trIncomeReport
 * 组件:TrIncomeReport
 */
public abstract class AbstractTrIncomeReportBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected ITrIncomeReportService trIncomeReportService = (ITrIncomeReportService)getService(ITrIncomeReportService.class);
		
	/** 属性列表 */
    TrStockdayrptDto stockDto = null;
    TrIncomedayrptDto incomeDto = null;
    List stockFilePath = null;
    List incomeFilePath = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_trincomereport_trincomereport_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTrIncomeReportBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_trincomereport_trincomereport.properties");
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
			log.warn("为itfe_dataquery_trincomereport_trincomereport读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: TCBS收入类报表导入
	 * ename: importFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String importFile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 分户账查询
	 * ename: ledgersearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ledgersearch(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TrStockdayrptDto getStockDto() {
        return stockDto;
    }

    public void setStockDto(com.cfcc.itfe.persistence.dto.TrStockdayrptDto _stockDto) {
        stockDto = _stockDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TrIncomedayrptDto getIncomeDto() {
        return incomeDto;
    }

    public void setIncomeDto(com.cfcc.itfe.persistence.dto.TrIncomedayrptDto _incomeDto) {
        incomeDto = _incomeDto;
    }
    
    public java.util.List getStockFilePath() {
        return stockFilePath;
    }

    public void setStockFilePath(java.util.List _stockFilePath) {
        stockFilePath = _stockFilePath;
    }
    
    public java.util.List getIncomeFilePath() {
        return incomeFilePath;
    }

    public void setIncomeFilePath(java.util.List _incomeFilePath) {
        incomeFilePath = _incomeFilePath;
    }
}
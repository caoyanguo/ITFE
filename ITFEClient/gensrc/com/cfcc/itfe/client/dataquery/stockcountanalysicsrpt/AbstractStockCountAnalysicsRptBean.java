package com.cfcc.itfe.client.dataquery.stockcountanalysicsrpt;

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

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:StockCountAnalysicsRpt
 * 组件:StockCountAnalysicsRpt
 */
public abstract class AbstractStockCountAnalysicsRptBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    	
	/** 属性列表 */
    String startqueryyear = null;
    String endqueryyear = null;
    String startdate = null;
    String enddate = null;
    String sleTreCode = null;
    List treList = null;
    String sleOfFlag = null;
    String sleBudKind = null;
    String sleMoneyUnit = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_stockcountanalysicsrpt_stockcountanalysicsrpt_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractStockCountAnalysicsRptBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_stockcountanalysicsrpt_stockcountanalysicsrpt.properties");
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
			log.warn("为itfe_dataquery_stockcountanalysicsrpt_stockcountanalysicsrpt读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: queryReport
	 * 引用方法: 
	 * viewers: 库存统计分析查询结果界面
	 * messages: 
	 */
    public String queryReport(Object o){
        
        return "库存统计分析查询结果界面";
    }
    
	/**
	 * Direction: 返回
	 * ename: goBack
	 * 引用方法: 
	 * viewers: 库存统计分析查询条件界面
	 * messages: 
	 */
    public String goBack(Object o){
        
        return "库存统计分析查询条件界面";
    }
    
	/**
	 * Direction: 导出Excel文件
	 * ename: exportExcelFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportExcelFile(Object o){
        
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
        
    public java.lang.String getStartqueryyear() {
        return startqueryyear;
    }

    public void setStartqueryyear(java.lang.String _startqueryyear) {
        startqueryyear = _startqueryyear;
    }
    
    public java.lang.String getEndqueryyear() {
        return endqueryyear;
    }

    public void setEndqueryyear(java.lang.String _endqueryyear) {
        endqueryyear = _endqueryyear;
    }
    
    public java.lang.String getStartdate() {
        return startdate;
    }

    public void setStartdate(java.lang.String _startdate) {
        startdate = _startdate;
    }
    
    public java.lang.String getEnddate() {
        return enddate;
    }

    public void setEnddate(java.lang.String _enddate) {
        enddate = _enddate;
    }
    
    public java.lang.String getSleTreCode() {
        return sleTreCode;
    }

    public void setSleTreCode(java.lang.String _sleTreCode) {
        sleTreCode = _sleTreCode;
    }
    
    public java.util.List getTreList() {
        return treList;
    }

    public void setTreList(java.util.List _treList) {
        treList = _treList;
    }
    
    public java.lang.String getSleOfFlag() {
        return sleOfFlag;
    }

    public void setSleOfFlag(java.lang.String _sleOfFlag) {
        sleOfFlag = _sleOfFlag;
    }
    
    public java.lang.String getSleBudKind() {
        return sleBudKind;
    }

    public void setSleBudKind(java.lang.String _sleBudKind) {
        sleBudKind = _sleBudKind;
    }
    
    public java.lang.String getSleMoneyUnit() {
        return sleMoneyUnit;
    }

    public void setSleMoneyUnit(java.lang.String _sleMoneyUnit) {
        sleMoneyUnit = _sleMoneyUnit;
    }
}
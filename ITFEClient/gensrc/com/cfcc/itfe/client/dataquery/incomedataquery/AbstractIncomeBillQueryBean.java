package com.cfcc.itfe.client.dataquery.incomedataquery;

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
import com.cfcc.itfe.service.dataquery.incomedataquery.IIncomeBillService;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: DataQuery
 * 模块:incomeDataQuery
 * 组件:IncomeBillQuery
 */
public abstract class AbstractIncomeBillQueryBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IIncomeBillService incomeBillService = (IIncomeBillService)getService(IIncomeBillService.class);
		
	/** 属性列表 */
    List selectedIncomeList = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_incomedataquery_incomebillquery_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractIncomeBillQueryBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_incomedataquery_incomebillquery.properties");
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
			log.warn("为itfe_dataquery_incomedataquery_incomebillquery读取messages出错", e);
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
	 * viewers: 收入税票列表界面
	 * messages: 
	 */
    public String searchList(Object o){
        
        return "收入税票列表界面";
    }
    
	/**
	 * Direction: 导出税票事件
	 * ename: exportBillData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportBillData(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 补发报文事件
	 * ename: reSendMsg
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String reSendMsg(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回查询界面
	 * ename: rebackSearch
	 * 引用方法: 
	 * viewers: 收入税票查询界面
	 * messages: 
	 */
    public String rebackSearch(Object o){
        
        return "收入税票查询界面";
    }
    
	/**
	 * Direction: 收入税票明细单击事件
	 * ename: singleclickIncome
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclickIncome(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回列表
	 * ename: returnlist
	 * 引用方法: 
	 * viewers: 收入税票列表界面
	 * messages: 
	 */
    public String returnlist(Object o){
        
        return "收入税票列表界面";
    }
    
	/**
	 * Direction: 保存数据
	 * ename: savedata
	 * 引用方法: 
	 * viewers: 收入税票列表界面
	 * messages: 
	 */
    public String savedata(Object o){
        
        return "收入税票列表界面";
    }
    
	/**
	 * Direction: 数据列表双击事件
	 * ename: doubleclickIncome
	 * 引用方法: 
	 * viewers: 收入税票编辑页面
	 * messages: 
	 */
    public String doubleclickIncome(Object o){
        
        return "收入税票编辑页面";
    }
    
	/**
	 * Direction: 全选
	 * ename: selectAllOrNone
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAllOrNone(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出选择税票事件
	 * ename: exportSelectedBillData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportSelectedBillData(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出所有税票事件
	 * ename: exportAllBillData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportAllBillData(Object o){
        
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
        
    public java.util.List getSelectedIncomeList() {
        return selectedIncomeList;
    }

    public void setSelectedIncomeList(java.util.List _selectedIncomeList) {
        selectedIncomeList = _selectedIncomeList;
    }
}
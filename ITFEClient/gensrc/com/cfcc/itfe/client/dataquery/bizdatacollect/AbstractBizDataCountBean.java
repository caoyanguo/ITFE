package com.cfcc.itfe.client.dataquery.bizdatacollect;

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
import com.cfcc.itfe.service.dataquery.bizdatacollect.IBizDataCountService;
import com.cfcc.itfe.facade.data.TipsParamDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:bizDataCollect
 * 组件:BizDataCount
 */
public abstract class AbstractBizDataCountBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IBizDataCountService bizDataCountService = (IBizDataCountService)getService(IBizDataCountService.class);
		
	/** 属性列表 */
    TipsParamDto param = null;
    String biztype = null;
    List bizlist = null;
    TipsParamDto paramdto = null;
    List biztypelist = null;
    List bizbudgetlist = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_bizdatacollect_bizdatacount_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractBizDataCountBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_bizdatacollect_bizdatacount.properties");
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
			log.warn("为itfe_dataquery_bizdatacollect_bizdatacount读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 报表打印
	 * ename: printReport
	 * 引用方法: 
	 * viewers: 报表显示界面
	 * messages: 
	 */
    public String printReport(Object o){
        
        return "报表显示界面";
    }
    
	/**
	 * Direction: 回到查询界面
	 * ename: backToQuery
	 * 引用方法: 
	 * viewers: 业务量统计
	 * messages: 
	 */
    public String backToQuery(Object o){
        
        return "业务量统计";
    }
    
	/**
	 * Direction: 查询代理行业务量
	 * ename: printBankReport
	 * 引用方法: 
	 * viewers: 代理行报表显示界面
	 * messages: 
	 */
    public String printBankReport(Object o){
        
        return "代理行报表显示界面";
    }
    
	/**
	 * Direction: 返回代理行查询界面
	 * ename: bacTokBankQuery
	 * 引用方法: 
	 * viewers: 代理行业务查询界面
	 * messages: 
	 */
    public String bacTokBankQuery(Object o){
        
        return "代理行业务查询界面";
    }
    
	/**
	 * Direction: 导出代理行报表
	 * ename: exportBankReport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportBankReport(Object o){
        
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
        
    public com.cfcc.itfe.facade.data.TipsParamDto getParam() {
        return param;
    }

    public void setParam(com.cfcc.itfe.facade.data.TipsParamDto _param) {
        param = _param;
    }
    
    public java.lang.String getBiztype() {
        return biztype;
    }

    public void setBiztype(java.lang.String _biztype) {
        biztype = _biztype;
    }
    
    public java.util.List getBizlist() {
        return bizlist;
    }

    public void setBizlist(java.util.List _bizlist) {
        bizlist = _bizlist;
    }
    
    public com.cfcc.itfe.facade.data.TipsParamDto getParamdto() {
        return paramdto;
    }

    public void setParamdto(com.cfcc.itfe.facade.data.TipsParamDto _paramdto) {
        paramdto = _paramdto;
    }
    
    public java.util.List getBiztypelist() {
        return biztypelist;
    }

    public void setBiztypelist(java.util.List _biztypelist) {
        biztypelist = _biztypelist;
    }
    
    public java.util.List getBizbudgetlist() {
        return bizbudgetlist;
    }

    public void setBizbudgetlist(java.util.List _bizbudgetlist) {
        bizbudgetlist = _bizbudgetlist;
    }
}
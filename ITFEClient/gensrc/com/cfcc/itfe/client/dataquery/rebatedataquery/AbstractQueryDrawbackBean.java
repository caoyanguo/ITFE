package com.cfcc.itfe.client.dataquery.rebatedataquery;

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
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: DataQuery
 * 模块:RebateDataQuery
 * 组件:QueryDrawback
 */
public abstract class AbstractQueryDrawbackBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    	
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_rebatedataquery_querydrawback_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractQueryDrawbackBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_rebatedataquery_querydrawback.properties");
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
			log.warn("为itfe_dataquery_rebatedataquery_querydrawback读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 公共查询
	 * ename: queryDrawback
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryDrawback(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 显示申报表明细
	 * ename: queryReportDatas
	 * 引用方法: 
	 * viewers: 报表显示
	 * messages: 
	 */
    public String queryReportDatas(Object o){
        
        return "报表显示";
    }
    
	/**
	 * Direction: 还原退库凭证
	 * ename: backdwbkvou
	 * 引用方法: 
	 * viewers: 报表显示
	 * messages: 
	 */
    public String backdwbkvou(Object o){
        
        return "报表显示";
    }
    
	/**
	 * Direction: 汇总退还书
	 * ename: sumdwbkreport
	 * 引用方法: 
	 * viewers: 报表显示
	 * messages: 
	 */
    public String sumdwbkreport(Object o){
        
        return "报表显示";
    }
    
	/**
	 * Direction: 退税明细打印
	 * ename: detaildwbkprint
	 * 引用方法: 
	 * viewers: 报表显示
	 * messages: 
	 */
    public String detaildwbkprint(Object o){
        
        return "报表显示";
    }
    
	/**
	 * Direction: 发送回执报文
	 * ename: senddwbkreport
	 * 引用方法: 
	 * viewers: 退税查询发送回执
	 * messages: 
	 */
    public String senddwbkreport(Object o){
        
        return "退税查询发送回执";
    }
    
	/**
	 * Direction: 取消校验
	 * ename: cancelcheck
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String cancelcheck(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 删除报文
	 * ename: deletereport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String deletereport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 生成退库退回
	 * ename: makedwbkbackreport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String makedwbkbackreport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回报文查询
	 * ename: goback
	 * 引用方法: 
	 * viewers: 通用查询条件
	 * messages: 
	 */
    public String goback(Object o){
        
        return "通用查询条件";
    }
    
	/**
	 * Direction: 全选
	 * ename: selectAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回报文处理界面
	 * ename: gobackproc
	 * 引用方法: 
	 * viewers: 退税查询处理
	 * messages: 
	 */
    public String gobackproc(Object o){
        
        return "退税查询处理";
    }
    
	/**
	 * Direction: 清算失败汇总退还书
	 * ename: sumdwbkfailreport
	 * 引用方法: 
	 * viewers: 报表显示2
	 * messages: 
	 */
    public String sumdwbkfailreport(Object o){
        
        return "报表显示2";
    }
    
	/**
	 * Direction: 到数据页面
	 * ename: goDataview
	 * 引用方法: 
	 * viewers: 退税报文查询结果
	 * messages: 
	 */
    public String goDataview(Object o){
        
        return "退税报文查询结果";
    }
    
	/**
	 * Direction: 到回执界面
	 * ename: godwbkbackview
	 * 引用方法: 
	 * viewers: 退税查询发送回执
	 * messages: 
	 */
    public String godwbkbackview(Object o){
        
        return "退税查询发送回执";
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
    }
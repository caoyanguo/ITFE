package com.cfcc.itfe.client.dataquery.tipsmsgproc;

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
import com.cfcc.itfe.service.dataquery.tipsmsgproc.IDealSendMsgService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: DataQuery
 * 模块:TipsMsgProc
 * 组件:DealSendMsg
 */
public abstract class AbstractDealSendMsgBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IDealSendMsgService dealSendMsgService = (IDealSendMsgService)getService(IDealSendMsgService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tipsmsgproc_dealsendmsg_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractDealSendMsgBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tipsmsgproc_dealsendmsg.properties");
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
			log.warn("为itfe_dataquery_tipsmsgproc_dealsendmsg读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 发送报文查询处理
	 * ename: queryMsg
	 * 引用方法: 
	 * viewers: 报文查询处理
	 * messages: 
	 */
    public String queryMsg(Object o){
        
        return "报文查询处理";
    }
    
	/**
	 * Direction: 返回日志查询
	 * ename: backSearch
	 * 引用方法: 
	 * viewers: 发送报文查询
	 * messages: 
	 */
    public String backSearch(Object o){
        
        return "发送报文查询";
    }
    
	/**
	 * Direction: 单选一条记录
	 * ename: selOneRecode
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selOneRecode(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 报文重发
	 * ename: sendMsgRepeat
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String sendMsgRepeat(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 发送报文查询打印
	 * ename: queryPrint
	 * 引用方法: 
	 * viewers: 报文查询打印
	 * messages: 
	 */
    public String queryPrint(Object o){
        
        return "报文查询打印";
    }
    
	/**
	 * Direction: 刷新
	 * ename: refreshrs
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String refreshrs(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 文件列表全选方法
	 * ename: chooseAllFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String chooseAllFile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 文件列表单选方法
	 * ename: chooseOneFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String chooseOneFile(Object o){
        
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
    }
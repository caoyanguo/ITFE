package com.cfcc.itfe.client.recbiz.certrec;

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
import com.cfcc.itfe.service.recbiz.certrec.IDownloadAllFileService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TvFilesDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:CertRec
 * 组件:ReceiveUI
 */
public abstract class AbstractReceiveUIBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IDownloadAllFileService downloadAllFileService = (IDownloadAllFileService)getService(IDownloadAllFileService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_certrec_receiveui_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractReceiveUIBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_certrec_receiveui.properties");
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
			log.warn("为itfe_recbiz_certrec_receiveui读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 凭证明细查询
	 * ename: queryCerDetail
	 * 引用方法: 
	 * viewers: 凭证明细
	 * messages: 
	 */
    public String queryCerDetail(Object o){
        
        return "凭证明细";
    }
    
	/**
	 * Direction: 返回凭证接收
	 * ename: backReceiveCer
	 * 引用方法: 
	 * viewers: 新凭证接收
	 * messages: 
	 */
    public String backReceiveCer(Object o){
        
        return "新凭证接收";
    }
    
	/**
	 * Direction: 附件批量下载
	 * ename: downloadSelectedFiles
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String downloadSelectedFiles(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 单个附件下载
	 * ename: downloadOneFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String downloadOneFile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 列表数据选择
	 * ename: selectOneRow
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectOneRow(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 下载路径选择
	 * ename: pathSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String pathSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 备份路径选择
	 * ename: backupPathSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String backupPathSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 接收日志查询
	 * ename: queryRecvLog
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryRecvLog(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 接收数据统计
	 * ename: queryRecvLogReport
	 * 引用方法: 
	 * viewers: 接收数据统计
	 * messages: 
	 */
    public String queryRecvLogReport(Object o){
        
        return "接收数据统计";
    }
    
	/**
	 * Direction: 作废收到的信息
	 * ename: recvDelete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String recvDelete(Object o){
        
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
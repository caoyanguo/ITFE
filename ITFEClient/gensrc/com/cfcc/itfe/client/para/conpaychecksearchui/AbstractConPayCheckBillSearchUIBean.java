package com.cfcc.itfe.client.para.conpaychecksearchui;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IFileResolveCommonService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.service.para.paramtransform.IParamTransformService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * 子系统: Para
 * 模块:ConPayCheckSearchUI
 * 组件:ConPayCheckBillSearchUI
 */
public abstract class AbstractConPayCheckBillSearchUIBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService)getService(IFileResolveCommonService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService)getService(IItfeCacheService.class);
	protected IParamTransformService paramTransformService = (IParamTransformService)getService(IParamTransformService.class);
		
	/** 属性列表 */
    List filePath = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_conpaychecksearchui_conpaycheckbillsearchui_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractConPayCheckBillSearchUIBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_conpaychecksearchui_conpaycheckbillsearchui.properties");
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
			log.warn("为itfe_para_conpaychecksearchui_conpaycheckbillsearchui读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: queryBudget
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String queryBudget(Object o){
        
        return "查询结果";
    }
    
	/**
	 * Direction: 返回
	 * ename: goBack
	 * 引用方法: 
	 * viewers: 信息查询
	 * messages: 
	 */
    public String goBack(Object o){
        
        return "信息查询";
    }
    
	/**
	 * Direction: 导入数据
	 * ename: dataimport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dataimport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转导入数据
	 * ename: goImport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String goImport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出数据
	 * ename: dataexport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dataexport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 保存数据
	 * ename: savedate
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String savedate(Object o){
        
        return "查询结果";
    }
    
	/**
	 * Direction: 返回
	 * ename: returnqueryresult
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String returnqueryresult(Object o){
        
        return "查询结果";
    }
    
	/**
	 * Direction: 跳转查询结果
	 * ename: goqueryresult
	 * 引用方法: 
	 * viewers: 信息修改
	 * messages: 
	 */
    public String goqueryresult(Object o){
        
        return "信息修改";
    }
    
	/**
	 * Direction: 清算查询
	 * ename: queryQs
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryQs(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 额度对账单删除
	 * ename: eddelete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String eddelete(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 清算对账单删除
	 * ename: qsdelete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String qsdelete(Object o){
        
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
        
    public java.util.List getFilePath() {
        return filePath;
    }

    public void setFilePath(java.util.List _filePath) {
        filePath = _filePath;
    }
}
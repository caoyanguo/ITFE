package com.cfcc.itfe.client.para.tsbankcodeconverttype;

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
import com.cfcc.itfe.service.para.tsbankcodeconverttype.ITsConvertbanktypeService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: Para
 * 模块:TsBankcodeConverttype
 * 组件:TsConvertbanktype
 */
public abstract class AbstractTsConvertbanktypeBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITsConvertbanktypeService tsConvertbanktypeService = (ITsConvertbanktypeService)getService(ITsConvertbanktypeService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsbankcodeconverttype_tsconvertbanktype_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTsConvertbanktypeBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsbankcodeconverttype_tsconvertbanktype.properties");
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
			log.warn("为itfe_para_tsbankcodeconverttype_tsconvertbanktype读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: queryConvert
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String queryConvert(Object o){
        
        return "查询结果";
    }
    
	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转录入界面
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String goInput(Object o){
        
        return "录入界面";
    }
    
	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回到维护界面
	 * ename: backMaintenance
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String backMaintenance(Object o){
        
        return "查询结果";
    }
    
	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String goModify(Object o){
        
        return "修改界面";
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
	 * Direction: 修改保存
	 * ename: modifySave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出
	 * ename: expfile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String expfile(Object o){
        
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
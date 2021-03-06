package com.cfcc.itfe.client.para.tsbankcode;

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
import com.cfcc.itfe.service.para.tsbankcode.ITsBankcodeService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsBankcodeDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:38
 * @generated
 * 子系统: Para
 * 模块:TsBankcode
 * 组件:TsBankcode
 */
public abstract class AbstractTsBankcodeBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITsBankcodeService tsBankcodeService = (ITsBankcodeService)getService(ITsBankcodeService.class);
		
	/** 属性列表 */
    TsBankcodeDto dto = null;
    PagingContext pagingcontext = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsbankcode_tsbankcode_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTsBankcodeBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsbankcode_tsbankcode.properties");
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
			log.warn("为itfe_para_tsbankcode_tsbankcode读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
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
	 * viewers: 维护界面
	 * messages: 
	 */
    public String backMaintenance(Object o){
        
        return "维护界面";
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
	 * Direction: 修改保存
	 * ename: modifySave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 到查询结果界面
	 * ename: goSearchResult
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String goSearchResult(Object o){
        
        return "维护界面";
    }
    
	/**
	 * Direction: 返回到查询条件界面
	 * ename: goSearch
	 * 引用方法: 
	 * viewers: 查询界面
	 * messages: 
	 */
    public String goSearch(Object o){
        
        return "查询界面";
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
        
    public com.cfcc.itfe.persistence.dto.TsBankcodeDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TsBankcodeDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
}
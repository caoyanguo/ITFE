package com.cfcc.itfe.client.para.tsmankey;

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
import com.cfcc.itfe.service.para.tsmankey.ITsMankeyService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * 子系统: Para
 * 模块:tsmankey
 * 组件:TsMankey
 */
public abstract class AbstractTsMankeyBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITsMankeyService tsMankeyService = (ITsMankeyService)getService(ITsMankeyService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    TsMankeyDto dto = null;
    PagingContext pagingContext = null;
    String repeatkey = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsmankey_tsmankey_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTsMankeyBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsmankey_tsmankey.properties");
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
			log.warn("为itfe_para_tsmankey_tsmankey读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 转到密钥参数录入
	 * ename: toKeysave
	 * 引用方法: 
	 * viewers: 密钥参数录入
	 * messages: 
	 */
    public String toKeysave(Object o){
        
        return "密钥参数录入";
    }
    
	/**
	 * Direction: 密钥参数删除
	 * ename: keyDelete
	 * 引用方法: 
	 * viewers: 密钥维护列表
	 * messages: 
	 */
    public String keyDelete(Object o){
        
        return "密钥维护列表";
    }
    
	/**
	 * Direction: 转到密钥参数修改
	 * ename: toKeymodify
	 * 引用方法: 
	 * viewers: 密钥参数修改
	 * messages: 
	 */
    public String toKeymodify(Object o){
        
        return "密钥参数修改";
    }
    
	/**
	 * Direction: 密钥参数录入
	 * ename: keySave
	 * 引用方法: 
	 * viewers: 密钥维护列表
	 * messages: 
	 */
    public String keySave(Object o){
        
        return "密钥维护列表";
    }
    
	/**
	 * Direction: 转到密钥参数列表
	 * ename: toKeylist
	 * 引用方法: 
	 * viewers: 密钥维护列表
	 * messages: 
	 */
    public String toKeylist(Object o){
        
        return "密钥维护列表";
    }
    
	/**
	 * Direction: 密钥参数修改
	 * ename: keyModify
	 * 引用方法: 
	 * viewers: 密钥维护列表
	 * messages: 
	 */
    public String keyModify(Object o){
        
        return "密钥维护列表";
    }
    
	/**
	 * Direction: 单击选中对象
	 * ename: clickSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String clickSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 更新密钥并导出
	 * ename: updateAndExport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String updateAndExport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 自动获取出票单位
	 * ename: autoGetBillOrg
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String autoGetBillOrg(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 生成新密钥
	 * ename: geneNewKey
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String geneNewKey(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 全选
	 * ename: allSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String allSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 已生效密钥导出
	 * ename: affKeyExport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String affKeyExport(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TsMankeyDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TsMankeyDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingContext() {
        return pagingContext;
    }

    public void setPagingContext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingContext) {
        pagingContext = _pagingContext;
    }
    
    public java.lang.String getRepeatkey() {
        return repeatkey;
    }

    public void setRepeatkey(java.lang.String _repeatkey) {
        repeatkey = _repeatkey;
    }
}
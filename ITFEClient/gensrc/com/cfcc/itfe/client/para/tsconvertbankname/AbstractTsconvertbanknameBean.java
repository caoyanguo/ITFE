package com.cfcc.itfe.client.para.tsconvertbankname;

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
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * 子系统: Para
 * 模块:tsconvertbankname
 * 组件:Tsconvertbankname
 */
public abstract class AbstractTsconvertbanknameBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    	
	/** 属性列表 */
    List resultlist = null;
    TsConvertbanknameDto searchDto = null;
    TsConvertbanknameDto detailDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsconvertbankname_tsconvertbankname_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTsconvertbanknameBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsconvertbankname_tsconvertbankname.properties");
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
			log.warn("为itfe_para_tsconvertbankname_tsconvertbankname读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 表格单击
	 * ename: singleclick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclick(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 表格双击
	 * ename: doubleclick
	 * 引用方法: 
	 * viewers: 信息修改
	 * messages: 
	 */
    public String doubleclick(Object o){
        
        return "信息修改";
    }
    
	/**
	 * Direction: 跳转录入界面
	 * ename: goinputview
	 * 引用方法: 
	 * viewers: 信息录入
	 * messages: 
	 */
    public String goinputview(Object o){
        
        return "信息录入";
    }
    
	/**
	 * Direction: 跳转修改界面
	 * ename: gomodview
	 * 引用方法: 
	 * viewers: 信息修改
	 * messages: 
	 */
    public String gomodview(Object o){
        
        return "信息修改";
    }
    
	/**
	 * Direction: 返回主页面
	 * ename: gomainview
	 * 引用方法: 
	 * viewers: 银行行名对照参数维护
	 * messages: 
	 */
    public String gomainview(Object o){
        
        return "银行行名对照参数维护";
    }
    
	/**
	 * Direction: 删除操作
	 * ename: del
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String del(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 保存
	 * ename: inputsave
	 * 引用方法: 
	 * viewers: 银行行名对照参数维护
	 * messages: 
	 */
    public String inputsave(Object o){
        
        return "银行行名对照参数维护";
    }
    
	/**
	 * Direction: 修改保存
	 * ename: modsave
	 * 引用方法: 
	 * viewers: 银行行名对照参数维护
	 * messages: 
	 */
    public String modsave(Object o){
        
        return "银行行名对照参数维护";
    }
    
	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: 银行行名对照参数维护
	 * messages: 
	 */
    public String search(Object o){
        
        return "银行行名对照参数维护";
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
        
    public java.util.List getResultlist() {
        return resultlist;
    }

    public void setResultlist(java.util.List _resultlist) {
        resultlist = _resultlist;
    }
    
    public com.cfcc.itfe.persistence.dto.TsConvertbanknameDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TsConvertbanknameDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TsConvertbanknameDto getDetailDto() {
        return detailDto;
    }

    public void setDetailDto(com.cfcc.itfe.persistence.dto.TsConvertbanknameDto _detailDto) {
        detailDto = _detailDto;
    }
}
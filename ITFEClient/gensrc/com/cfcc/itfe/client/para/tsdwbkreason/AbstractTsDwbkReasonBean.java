package com.cfcc.itfe.client.para.tsdwbkreason;

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
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsDwbkReasonDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * 子系统: Para
 * 模块:TsDwbkReason
 * 组件:TsDwbkReason
 */
public abstract class AbstractTsDwbkReasonBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    TsDwbkReasonDto searchdto = null;
    PagingContext pagingcontext = null;
    TsDwbkReasonDto savedto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsdwbkreason_tsdwbkreason_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTsDwbkReasonBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsdwbkreason_tsdwbkreason.properties");
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
			log.warn("为itfe_para_tsdwbkreason_tsdwbkreason读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: 退付原因参数查询结果
	 * messages: 
	 */
    public String search(Object o){
        
        return "退付原因参数查询结果";
    }
    
	/**
	 * Direction: 新增
	 * ename: newInput
	 * 引用方法: 
	 * viewers: 退付原因参数录入界面
	 * messages: 
	 */
    public String newInput(Object o){
        
        return "退付原因参数录入界面";
    }
    
	/**
	 * Direction: 修改跳转
	 * ename: updateDri
	 * 引用方法: 
	 * viewers: 退付原因参数修改界面
	 * messages: 
	 */
    public String updateDri(Object o){
        
        return "退付原因参数修改界面";
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
	 * Direction: 返回
	 * ename: reback
	 * 引用方法: 
	 * viewers: 退付原因参数查询
	 * messages: 
	 */
    public String reback(Object o){
        
        return "退付原因参数查询";
    }
    
	/**
	 * Direction: 保存
	 * ename: save
	 * 引用方法: 
	 * viewers: 退付原因参数查询结果
	 * messages: 
	 */
    public String save(Object o){
        
        return "退付原因参数查询结果";
    }
    
	/**
	 * Direction: 修改
	 * ename: update
	 * 引用方法: 
	 * viewers: 退付原因参数查询结果
	 * messages: 
	 */
    public String update(Object o){
        
        return "退付原因参数查询结果";
    }
    
	/**
	 * Direction: 取消
	 * ename: exit
	 * 引用方法: 
	 * viewers: 退付原因参数查询结果
	 * messages: 
	 */
    public String exit(Object o){
        
        return "退付原因参数查询结果";
    }
    
	/**
	 * Direction: 双击修改
	 * ename: doubleclickToUpdate
	 * 引用方法: 
	 * viewers: 退付原因参数修改界面
	 * messages: 
	 */
    public String doubleclickToUpdate(Object o){
        
        return "退付原因参数修改界面";
    }
    
	/**
	 * Direction: 单击选中数据
	 * ename: singleclickdate
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclickdate(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TsDwbkReasonDto getSearchdto() {
        return searchdto;
    }

    public void setSearchdto(com.cfcc.itfe.persistence.dto.TsDwbkReasonDto _searchdto) {
        searchdto = _searchdto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TsDwbkReasonDto getSavedto() {
        return savedto;
    }

    public void setSavedto(com.cfcc.itfe.persistence.dto.TsDwbkReasonDto _savedto) {
        savedto = _savedto;
    }
}
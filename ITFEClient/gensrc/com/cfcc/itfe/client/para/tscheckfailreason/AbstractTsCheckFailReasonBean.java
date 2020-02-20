package com.cfcc.itfe.client.para.tscheckfailreason;

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
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * 子系统: Para
 * 模块:TsCheckFailReason
 * 组件:TsCheckFailReason
 */
public abstract class AbstractTsCheckFailReasonBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    TsCheckfailreasonDto dto = null;
    PagingContext pagingcontext = null;
    TsCheckfailreasonDto selectedDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tscheckfailreason_tscheckfailreason_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTsCheckFailReasonBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tscheckfailreason_tscheckfailreason.properties");
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
			log.warn("为itfe_para_tscheckfailreason_tscheckfailreason读取messages出错", e);
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
	 * viewers: 常用失败原因维护页
	 * messages: 
	 */
    public String search(Object o){
        
        return "常用失败原因维护页";
    }
    
	/**
	 * Direction: 录入
	 * ename: newInput
	 * 引用方法: 
	 * viewers: 常用失败原因录入页
	 * messages: 
	 */
    public String newInput(Object o){
        
        return "常用失败原因录入页";
    }
    
	/**
	 * Direction: 修改跳转
	 * ename: updateDireck
	 * 引用方法: 
	 * viewers: 常用失败原因修改页
	 * messages: 
	 */
    public String updateDireck(Object o){
        
        return "常用失败原因修改页";
    }
    
	/**
	 * Direction: 保存
	 * ename: save
	 * 引用方法: 
	 * viewers: 常用失败原因维护页
	 * messages: 
	 */
    public String save(Object o){
        
        return "常用失败原因维护页";
    }
    
	/**
	 * Direction: 修改
	 * ename: update
	 * 引用方法: 
	 * viewers: 常用失败原因维护页
	 * messages: 
	 */
    public String update(Object o){
        
        return "常用失败原因维护页";
    }
    
	/**
	 * Direction: 取消
	 * ename: exit
	 * 引用方法: 
	 * viewers: 常用失败原因维护页
	 * messages: 
	 */
    public String exit(Object o){
        
        return "常用失败原因维护页";
    }
    
	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: 常用失败原因维护页
	 * messages: 
	 */
    public String delete(Object o){
        
        return "常用失败原因维护页";
    }
    
	/**
	 * Direction: 双击跳转修改
	 * ename: doubleclickToUpdate
	 * 引用方法: 
	 * viewers: 常用失败原因修改页
	 * messages: 
	 */
    public String doubleclickToUpdate(Object o){
        
        return "常用失败原因修改页";
    }
    
	/**
	 * Direction: 单击事件
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto getSelectedDto() {
        return selectedDto;
    }

    public void setSelectedDto(com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto _selectedDto) {
        selectedDto = _selectedDto;
    }
}
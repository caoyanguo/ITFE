package com.cfcc.itfe.client.recbiz.commercialbankpaybackment;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IFileResolveCommonService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:commercialbankPayBackment
 * 组件:CommercialbankPayBackment
 */
public abstract class AbstractCommercialbankPayBackmentBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IUploadConfirmService uploadConfirmService = (IUploadConfirmService)getService(IUploadConfirmService.class);
	protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService)getService(IFileResolveCommonService.class);
		
	/** 属性列表 */
    List filepath = null;
    List selectedfilelist = null;
    List showfilelist = null;
    List selecteddatalist = null;
    List showdatalist = null;
    TbsTvBnkpayMainDto searchdto = null;
    TvPayreckBankBackDto payreckbackdto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_commercialbankpaybackment_commercialbankpaybackment_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractCommercialbankPayBackmentBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_commercialbankpaybackment_commercialbankpaybackment.properties");
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
			log.warn("为itfe_recbiz_commercialbankpaybackment_commercialbankpaybackment读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 数据加载
	 * ename: dateload
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dateload(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转批量销号查询
	 * ename: topiliangxh
	 * 引用方法: 
	 * viewers: 批量销号
	 * messages: 
	 */
    public String topiliangxh(Object o){
        
        return "批量销号";
    }
    
	/**
	 * Direction: 跳转逐笔销号查询
	 * ename: tozhubixh
	 * 引用方法: 
	 * viewers: 逐笔销号
	 * messages: 
	 */
    public String tozhubixh(Object o){
        
        return "逐笔销号";
    }
    
	/**
	 * Direction: 直接提交
	 * ename: submit
	 * 引用方法: 
	 * viewers: 商行办理支付划款申请退款数据导入
	 * messages: 
	 */
    public String submit(Object o){
        
        return "商行办理支付划款申请退款数据导入";
    }
    
	/**
	 * Direction: 返回默认界面
	 * ename: goback
	 * 引用方法: 
	 * viewers: 商行办理支付划款申请退款数据导入
	 * messages: 
	 */
    public String goback(Object o){
        
        return "商行办理支付划款申请退款数据导入";
    }
    
	/**
	 * Direction: 批量确认
	 * ename: plsubmit
	 * 引用方法: 
	 * viewers: 批量销号
	 * messages: 
	 */
    public String plsubmit(Object o){
        
        return "批量销号";
    }
    
	/**
	 * Direction: 批量删除
	 * ename: pldel
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String pldel(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 逐笔提交
	 * ename: zbsubmit
	 * 引用方法: 
	 * viewers: 逐笔销号
	 * messages: 
	 */
    public String zbsubmit(Object o){
        
        return "逐笔销号";
    }
    
	/**
	 * Direction: 批量销号查询
	 * ename: plsearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String plsearch(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 逐笔销号查询
	 * ename: zbsearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String zbsearch(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 全选
	 * ename: selectall
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectall(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 选中事件
	 * ename: selectEvent
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectEvent(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 双击显示明细
	 * ename: doubleClickEvent
	 * 引用方法: 
	 * viewers: 子明細记录显示界面
	 * messages: 
	 */
    public String doubleClickEvent(Object o){
        
        return "子明細记录显示界面";
    }
    
	/**
	 * Direction: 跳转至直接提交补录信息页面
	 * ename: toDirectBulu
	 * 引用方法: 
	 * viewers: 划款申请退款直接提交补录信息显示
	 * messages: 
	 */
    public String toDirectBulu(Object o){
        
        return "划款申请退款直接提交补录信息显示";
    }
    
	/**
	 * Direction: 跳转至批量提交补录信息页面
	 * ename: toPilBulu
	 * 引用方法: 
	 * viewers: 划款申请退款批量提交补录信息显示
	 * messages: 
	 */
    public String toPilBulu(Object o){
        
        return "划款申请退款批量提交补录信息显示";
    }
    
	/**
	 * Direction: 跳转至逐笔提交补录信息页面
	 * ename: toZhubiBulu
	 * 引用方法: 
	 * viewers: 划款申请退款逐笔提交补录信息显示
	 * messages: 
	 */
    public String toZhubiBulu(Object o){
        
        return "划款申请退款逐笔提交补录信息显示";
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
        
    public java.util.List getFilepath() {
        return filepath;
    }

    public void setFilepath(java.util.List _filepath) {
        filepath = _filepath;
    }
    
    public java.util.List getSelectedfilelist() {
        return selectedfilelist;
    }

    public void setSelectedfilelist(java.util.List _selectedfilelist) {
        selectedfilelist = _selectedfilelist;
    }
    
    public java.util.List getShowfilelist() {
        return showfilelist;
    }

    public void setShowfilelist(java.util.List _showfilelist) {
        showfilelist = _showfilelist;
    }
    
    public java.util.List getSelecteddatalist() {
        return selecteddatalist;
    }

    public void setSelecteddatalist(java.util.List _selecteddatalist) {
        selecteddatalist = _selecteddatalist;
    }
    
    public java.util.List getShowdatalist() {
        return showdatalist;
    }

    public void setShowdatalist(java.util.List _showdatalist) {
        showdatalist = _showdatalist;
    }
    
    public com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto getSearchdto() {
        return searchdto;
    }

    public void setSearchdto(com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto _searchdto) {
        searchdto = _searchdto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto getPayreckbackdto() {
        return payreckbackdto;
    }

    public void setPayreckbackdto(com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto _payreckbackdto) {
        payreckbackdto = _payreckbackdto;
    }
}
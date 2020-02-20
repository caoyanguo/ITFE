package com.cfcc.itfe.client.dataquery.financegrantpayadjustvoucher;

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
import com.cfcc.itfe.service.dataquery.financegrantpayadjustvoucher.IFinanceGrantPayAdjustVoucherService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.HtfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.HtfGrantpayAdjustsubDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * 子系统: DataQuery
 * 模块:financeGrantPayAdjustVoucher
 * 组件:FinanceGrantPayAdjustVoucher
 */
public abstract class AbstractFinanceGrantPayAdjustVoucherBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IFinanceGrantPayAdjustVoucherService financeGrantPayAdjustVoucherService = (IFinanceGrantPayAdjustVoucherService)getService(IFinanceGrantPayAdjustVoucherService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    TfGrantpayAdjustmainDto curSearchDto = null;
    TfGrantpayAdjustsubDto curSubSearchDto = null;
    HtfGrantpayAdjustmainDto hisSearchDto = null;
    HtfGrantpayAdjustsubDto hisSubSearchDto = null;
    PagingContext pagingContext = null;
    List enumList = null;
    String realValue = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_financegrantpayadjustvoucher_financegrantpayadjustvoucher_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractFinanceGrantPayAdjustVoucherBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_financegrantpayadjustvoucher_financegrantpayadjustvoucher.properties");
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
			log.warn("为itfe_dataquery_financegrantpayadjustvoucher_financegrantpayadjustvoucher读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
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
	 * Direction: 双击
	 * ename: doubleClick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 查询
	 * ename: searchToList
	 * 引用方法: 
	 * viewers: 财政授权支付调整凭证当前查询结果
	 * messages: 
	 */
    public String searchToList(Object o){
        
        return "财政授权支付调整凭证当前查询结果";
    }
    
	/**
	 * Direction: 返回
	 * ename: backToSearch
	 * 引用方法: 
	 * viewers: 财政授权支付调整凭证查询条件
	 * messages: 
	 */
    public String backToSearch(Object o){
        
        return "财政授权支付调整凭证查询条件";
    }
    
	/**
	 * Direction: 导出文件
	 * ename: exportFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportFile(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto getCurSearchDto() {
        return curSearchDto;
    }

    public void setCurSearchDto(com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto _curSearchDto) {
        curSearchDto = _curSearchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TfGrantpayAdjustsubDto getCurSubSearchDto() {
        return curSubSearchDto;
    }

    public void setCurSubSearchDto(com.cfcc.itfe.persistence.dto.TfGrantpayAdjustsubDto _curSubSearchDto) {
        curSubSearchDto = _curSubSearchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.HtfGrantpayAdjustmainDto getHisSearchDto() {
        return hisSearchDto;
    }

    public void setHisSearchDto(com.cfcc.itfe.persistence.dto.HtfGrantpayAdjustmainDto _hisSearchDto) {
        hisSearchDto = _hisSearchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.HtfGrantpayAdjustsubDto getHisSubSearchDto() {
        return hisSubSearchDto;
    }

    public void setHisSubSearchDto(com.cfcc.itfe.persistence.dto.HtfGrantpayAdjustsubDto _hisSubSearchDto) {
        hisSubSearchDto = _hisSubSearchDto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingContext() {
        return pagingContext;
    }

    public void setPagingContext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingContext) {
        pagingContext = _pagingContext;
    }
    
    public java.util.List getEnumList() {
        return enumList;
    }

    public void setEnumList(java.util.List _enumList) {
        enumList = _enumList;
    }
    
    public java.lang.String getRealValue() {
        return realValue;
    }

    public void setRealValue(java.lang.String _realValue) {
        realValue = _realValue;
    }
}
package com.cfcc.itfe.client.sendbiz.recvreportreconciliation;

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
import com.cfcc.itfe.service.recbiz.voucherload.IVoucherLoadService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDefrayMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDepositMainDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * 子系统: SendBiz
 * 模块:recvReportReconciliation
 * 组件:RecvReportReconciliation
 */
public abstract class AbstractRecvReportReconciliationBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IVoucherLoadService voucherLoadService = (IVoucherLoadService)getService(IVoucherLoadService.class);
		
	/** 属性列表 */
    TvVoucherinfoDto dto = null;
    PagingContext pagingcontext = null;
    TfReportIncomeMainDto incomeMainDto = null;
    TfReportDefrayMainDto defrayMainDto = null;
    TfReportDepositMainDto depositMainDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_sendbiz_recvreportreconciliation_recvreportreconciliation_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractRecvReportReconciliationBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_sendbiz_recvreportreconciliation_recvreportreconciliation.properties");
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
			log.warn("为itfe_sendbiz_recvreportreconciliation_recvreportreconciliation读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: voucherSearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherSearch(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 全选
	 * ename: selectAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 生成凭证
	 * ename: voucherGenerator
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherGenerator(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 签章
	 * ename: voucherStamp
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherStamp(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 签章撤销
	 * ename: voucherStampCancle
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherStampCancle(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 凭证查看
	 * ename: voucherView
	 * 引用方法: 
	 * viewers: 凭证查看界面
	 * messages: 
	 */
    public String voucherView(Object o){
        
        return "凭证查看界面";
    }
    
	/**
	 * Direction: 发送凭证电子凭证库
	 * ename: voucherSend
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherSend(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 回单还原展示
	 * ename: returnVoucherView
	 * 引用方法: 
	 * viewers: 回单凭证查看界面
	 * messages: 
	 */
    public String returnVoucherView(Object o){
        
        return "回单凭证查看界面";
    }
    
	/**
	 * Direction: 双击查看业务明细
	 * ename: doubleClick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回
	 * ename: backToSearch
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String backToSearch(Object o){
        
        return "维护界面";
    }
    
	/**
	 * Direction: 回单状态查询
	 * ename: queryStatusReturnVoucher
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryStatusReturnVoucher(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TfReportIncomeMainDto getIncomeMainDto() {
        return incomeMainDto;
    }

    public void setIncomeMainDto(com.cfcc.itfe.persistence.dto.TfReportIncomeMainDto _incomeMainDto) {
        incomeMainDto = _incomeMainDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TfReportDefrayMainDto getDefrayMainDto() {
        return defrayMainDto;
    }

    public void setDefrayMainDto(com.cfcc.itfe.persistence.dto.TfReportDefrayMainDto _defrayMainDto) {
        defrayMainDto = _defrayMainDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TfReportDepositMainDto getDepositMainDto() {
        return depositMainDto;
    }

    public void setDepositMainDto(com.cfcc.itfe.persistence.dto.TfReportDepositMainDto _depositMainDto) {
        depositMainDto = _depositMainDto;
    }
}
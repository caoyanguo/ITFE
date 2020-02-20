package com.cfcc.itfe.client.recbiz.backvoucher;

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
import com.cfcc.itfe.service.recbiz.backvoucher.IBackVoucherService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:backVoucher
 * 组件:BackVoucher
 */
public abstract class AbstractBackVoucherBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IVoucherLoadService voucherLoadService = (IVoucherLoadService)getService(IVoucherLoadService.class);
	protected IBackVoucherService backVoucherService = (IBackVoucherService)getService(IBackVoucherService.class);
		
	/** 属性列表 */
    TvVoucherinfoDto dto = null;
    TvVoucherinfoDto leftdto = null;
    PagingContext pagingcontext = null;
    TvPayoutmsgmainDto tvPayoutmsgmainDto = null;
    TvPayoutmsgsubDto tvPayoutmsgsubDto = null;
    TvDwbkDto tvDwbkDto = null;
    TvVoucherinfoDto indexDto = null;
    TfDirectpaymsgmainDto tfDirectpaymsgmainDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_backvoucher_backvoucher_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractBackVoucherBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_backvoucher_backvoucher.properties");
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
			log.warn("为itfe_recbiz_backvoucher_backvoucher读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 凭证生成查询
	 * ename: voucherGSearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherGSearch(Object o){
        
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
	 * viewers: 凭证退回业务查看界面
	 * messages: 
	 */
    public String voucherView(Object o){
        
        return "凭证退回业务查看界面";
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
	 * Direction: 查询退回凭证信息（TCBS下发）
	 * ename: querybackvoucher
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String querybackvoucher(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 生成凭证（TCBS下发）
	 * ename: voucherGeneratorForTCBS
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherGeneratorForTCBS(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 全选
	 * ename: selectAllBack
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAllBack(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 全选
	 * ename: selectAllPayoutBack
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAllPayoutBack(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 生成凭证
	 * ename: voucherGeneratorPayoutBack
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherGeneratorPayoutBack(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
        
        return "";
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
    
	/**
	 * Direction: 删除凭证
	 * ename: delgenvoucher
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delgenvoucher(Object o){
        
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
    
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoDto getLeftdto() {
        return leftdto;
    }

    public void setLeftdto(com.cfcc.itfe.persistence.dto.TvVoucherinfoDto _leftdto) {
        leftdto = _leftdto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto getTvPayoutmsgmainDto() {
        return tvPayoutmsgmainDto;
    }

    public void setTvPayoutmsgmainDto(com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto _tvPayoutmsgmainDto) {
        tvPayoutmsgmainDto = _tvPayoutmsgmainDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto getTvPayoutmsgsubDto() {
        return tvPayoutmsgsubDto;
    }

    public void setTvPayoutmsgsubDto(com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto _tvPayoutmsgsubDto) {
        tvPayoutmsgsubDto = _tvPayoutmsgsubDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvDwbkDto getTvDwbkDto() {
        return tvDwbkDto;
    }

    public void setTvDwbkDto(com.cfcc.itfe.persistence.dto.TvDwbkDto _tvDwbkDto) {
        tvDwbkDto = _tvDwbkDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoDto getIndexDto() {
        return indexDto;
    }

    public void setIndexDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoDto _indexDto) {
        indexDto = _indexDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto getTfDirectpaymsgmainDto() {
        return tfDirectpaymsgmainDto;
    }

    public void setTfDirectpaymsgmainDto(com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto _tfDirectpaymsgmainDto) {
        tfDirectpaymsgmainDto = _tfDirectpaymsgmainDto;
    }
}
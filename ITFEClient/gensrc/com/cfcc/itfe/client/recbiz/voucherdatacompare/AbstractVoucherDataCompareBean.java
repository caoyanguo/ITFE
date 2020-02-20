package com.cfcc.itfe.client.recbiz.voucherdatacompare;

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
import com.cfcc.itfe.service.recbiz.voucherdatacompare.IVoucherDataCompareService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbanklistSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankbackSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankbacklistSxDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubSxDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:voucherDataCompare
 * 组件:VoucherDataCompare
 */
public abstract class AbstractVoucherDataCompareBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IVoucherDataCompareService voucherDataCompareService = (IVoucherDataCompareService)getService(IVoucherDataCompareService.class);
		
	/** 属性列表 */
    TvVoucherinfoSxDto dto = null;
    PagingContext pagingcontext = null;
    TvPayreckbankSxDto tvPayreckbankSxDto = null;
    TvPayreckbanklistSxDto tvPayreckbanklistSxDto = null;
    TvPayreckbankbackSxDto tvPayreckbankbackSxDto = null;
    TvPayreckbankbacklistSxDto tvPayreckbankbacklistSxDto = null;
    TvDirectpaymsgmainSxDto tvDirectpaymsgmainSxDto = null;
    TvDirectpaymsgsubSxDto tvDirectpaymsgsubSxDto = null;
    TvGrantpaymsgmainSxDto tvGrantpaymsgmainSxDto = null;
    TvGrantpaymsgsubSxDto tvGrantpaymsgsubSxDto = null;
    TvPayoutmsgmainSxDto tvPayoutmsgmainSxDto = null;
    TvPayoutmsgsubSxDto tvPayoutmsgsubSxDto = null;
    PagingContext pagingPayreckbanklist = null;
    PagingContext pagingPayreckbankbacklist = null;
    PagingContext pagingDirectpaymsgsub = null;
    PagingContext pagingGrantpaymsgsub = null;
    PagingContext pagingPayoutmsgsub = null;
    PagingContext pagingGrantpaymsgmain = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_voucherdatacompare_voucherdatacompare_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractVoucherDataCompareBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_voucherdatacompare_voucherdatacompare.properties");
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
			log.warn("为itfe_recbiz_voucherdatacompare_voucherdatacompare读取messages出错", e);
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
	 * viewers: * messages: 
	 */
    public String search(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 读取凭证
	 * ename: voucherRead
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherRead(Object o){
        
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
	 * Direction: 数据比对
	 * ename: voucherDataCompare
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherDataCompare(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 发送回单
	 * ename: sendReturnVoucher
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String sendReturnVoucher(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 重新校验
	 * ename: voucherVerify
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherVerify(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 双击记录
	 * ename: doubleClick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回
	 * ename: goBack
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String goBack(Object o){
        
        return "维护界面";
    }
    
	/**
	 * Direction: 生成正式数据
	 * ename: generateData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String generateData(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 同步状态
	 * ename: updateStatus
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String updateStatus(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 主信息单击事件
	 * ename: singleclickMain
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclickMain(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 主信息双击事件
	 * ename: doubleclickMain
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleclickMain(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayreckbankSxDto getTvPayreckbankSxDto() {
        return tvPayreckbankSxDto;
    }

    public void setTvPayreckbankSxDto(com.cfcc.itfe.persistence.dto.TvPayreckbankSxDto _tvPayreckbankSxDto) {
        tvPayreckbankSxDto = _tvPayreckbankSxDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayreckbanklistSxDto getTvPayreckbanklistSxDto() {
        return tvPayreckbanklistSxDto;
    }

    public void setTvPayreckbanklistSxDto(com.cfcc.itfe.persistence.dto.TvPayreckbanklistSxDto _tvPayreckbanklistSxDto) {
        tvPayreckbanklistSxDto = _tvPayreckbanklistSxDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayreckbankbackSxDto getTvPayreckbankbackSxDto() {
        return tvPayreckbankbackSxDto;
    }

    public void setTvPayreckbankbackSxDto(com.cfcc.itfe.persistence.dto.TvPayreckbankbackSxDto _tvPayreckbankbackSxDto) {
        tvPayreckbankbackSxDto = _tvPayreckbankbackSxDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayreckbankbacklistSxDto getTvPayreckbankbacklistSxDto() {
        return tvPayreckbankbacklistSxDto;
    }

    public void setTvPayreckbankbacklistSxDto(com.cfcc.itfe.persistence.dto.TvPayreckbankbacklistSxDto _tvPayreckbankbacklistSxDto) {
        tvPayreckbankbacklistSxDto = _tvPayreckbankbacklistSxDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainSxDto getTvDirectpaymsgmainSxDto() {
        return tvDirectpaymsgmainSxDto;
    }

    public void setTvDirectpaymsgmainSxDto(com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainSxDto _tvDirectpaymsgmainSxDto) {
        tvDirectpaymsgmainSxDto = _tvDirectpaymsgmainSxDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubSxDto getTvDirectpaymsgsubSxDto() {
        return tvDirectpaymsgsubSxDto;
    }

    public void setTvDirectpaymsgsubSxDto(com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubSxDto _tvDirectpaymsgsubSxDto) {
        tvDirectpaymsgsubSxDto = _tvDirectpaymsgsubSxDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainSxDto getTvGrantpaymsgmainSxDto() {
        return tvGrantpaymsgmainSxDto;
    }

    public void setTvGrantpaymsgmainSxDto(com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainSxDto _tvGrantpaymsgmainSxDto) {
        tvGrantpaymsgmainSxDto = _tvGrantpaymsgmainSxDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubSxDto getTvGrantpaymsgsubSxDto() {
        return tvGrantpaymsgsubSxDto;
    }

    public void setTvGrantpaymsgsubSxDto(com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubSxDto _tvGrantpaymsgsubSxDto) {
        tvGrantpaymsgsubSxDto = _tvGrantpaymsgsubSxDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayoutmsgmainSxDto getTvPayoutmsgmainSxDto() {
        return tvPayoutmsgmainSxDto;
    }

    public void setTvPayoutmsgmainSxDto(com.cfcc.itfe.persistence.dto.TvPayoutmsgmainSxDto _tvPayoutmsgmainSxDto) {
        tvPayoutmsgmainSxDto = _tvPayoutmsgmainSxDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayoutmsgsubSxDto getTvPayoutmsgsubSxDto() {
        return tvPayoutmsgsubSxDto;
    }

    public void setTvPayoutmsgsubSxDto(com.cfcc.itfe.persistence.dto.TvPayoutmsgsubSxDto _tvPayoutmsgsubSxDto) {
        tvPayoutmsgsubSxDto = _tvPayoutmsgsubSxDto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingPayreckbanklist() {
        return pagingPayreckbanklist;
    }

    public void setPagingPayreckbanklist(com.cfcc.jaf.rcp.control.table.PagingContext _pagingPayreckbanklist) {
        pagingPayreckbanklist = _pagingPayreckbanklist;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingPayreckbankbacklist() {
        return pagingPayreckbankbacklist;
    }

    public void setPagingPayreckbankbacklist(com.cfcc.jaf.rcp.control.table.PagingContext _pagingPayreckbankbacklist) {
        pagingPayreckbankbacklist = _pagingPayreckbankbacklist;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingDirectpaymsgsub() {
        return pagingDirectpaymsgsub;
    }

    public void setPagingDirectpaymsgsub(com.cfcc.jaf.rcp.control.table.PagingContext _pagingDirectpaymsgsub) {
        pagingDirectpaymsgsub = _pagingDirectpaymsgsub;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingGrantpaymsgsub() {
        return pagingGrantpaymsgsub;
    }

    public void setPagingGrantpaymsgsub(com.cfcc.jaf.rcp.control.table.PagingContext _pagingGrantpaymsgsub) {
        pagingGrantpaymsgsub = _pagingGrantpaymsgsub;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingPayoutmsgsub() {
        return pagingPayoutmsgsub;
    }

    public void setPagingPayoutmsgsub(com.cfcc.jaf.rcp.control.table.PagingContext _pagingPayoutmsgsub) {
        pagingPayoutmsgsub = _pagingPayoutmsgsub;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingGrantpaymsgmain() {
        return pagingGrantpaymsgmain;
    }

    public void setPagingGrantpaymsgmain(com.cfcc.jaf.rcp.control.table.PagingContext _pagingGrantpaymsgmain) {
        pagingGrantpaymsgmain = _pagingGrantpaymsgmain;
    }
}
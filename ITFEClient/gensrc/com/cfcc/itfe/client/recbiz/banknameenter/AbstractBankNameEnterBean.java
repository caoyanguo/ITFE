package com.cfcc.itfe.client.recbiz.banknameenter;

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
import com.cfcc.itfe.service.recbiz.banknameenter.IBankNameEnterService;
import com.cfcc.itfe.service.recbiz.voucherload.IVoucherLoadService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.facade.data.MulitTableDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:bankNameEnter
 * 组件:BankNameEnter
 */
public abstract class AbstractBankNameEnterBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IBankNameEnterService bankNameEnterService = (IBankNameEnterService)getService(IBankNameEnterService.class);
	protected IVoucherLoadService voucherLoadService = (IVoucherLoadService)getService(IVoucherLoadService.class);
		
	/** 属性列表 */
    List selectlist = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_banknameenter_banknameenter_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractBankNameEnterBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_banknameenter_banknameenter.properties");
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
			log.warn("为itfe_recbiz_banknameenter_banknameenter读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 单击信息列表
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 双击银行列表
	 * ename: doubleclickBank
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleclickBank(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 查询银行信息
	 * ename: querybank
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String querybank(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 退出
	 * ename: exit
	 * 引用方法: 
	 * viewers: 需补录支付系统行号信息查询视图
	 * messages: 
	 */
    public String exit(Object o){
        
        return "需补录支付系统行号信息查询视图";
    }
    
	/**
	 * Direction: 补录
	 * ename: addRecord
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String addRecord(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 补复通过
	 * ename: bufusuccess
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String bufusuccess(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: 业务要素补录界面
	 * messages: 
	 */
    public String search(Object o){
        
        return "业务要素补录界面";
    }
    
	/**
	 * Direction: 审核通过
	 * ename: checkSuccess
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String checkSuccess(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 审核失败
	 * ename: checkfault
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String checkfault(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 补复失败
	 * ename: bufufault
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String bufufault(Object o){
        
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
	 * Direction: 双击补录
	 * ename: doubleclickTObulu
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleclickTObulu(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 查询记录
	 * ename: queryRecords
	 * 引用方法: 
	 * viewers: 查询结果视图
	 * messages: 
	 */
    public String queryRecords(Object o){
        
        return "查询结果视图";
    }
    
	/**
	 * Direction: 批量打印
	 * ename: printSelectedVoucher
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String printSelectedVoucher(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 签章
	 * ename: singStamp
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singStamp(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 复核失败
	 * ename: reviewfault
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String reviewfault(Object o){
        
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
        
    public java.util.List getSelectlist() {
        return selectlist;
    }

    public void setSelectlist(java.util.List _selectlist) {
        selectlist = _selectlist;
    }
}
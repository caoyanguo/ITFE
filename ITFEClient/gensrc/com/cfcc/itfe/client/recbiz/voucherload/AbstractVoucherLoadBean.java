package com.cfcc.itfe.client.recbiz.voucherload;

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

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:voucherLoad
 * 组件:VoucherLoad
 */
public abstract class AbstractVoucherLoadBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IVoucherLoadService voucherLoadService = (IVoucherLoadService)getService(IVoucherLoadService.class);
		
	/** 属性列表 */
    TvVoucherinfoDto dto = null;
    PagingContext pagingcontext = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_voucherload_voucherload_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractVoucherLoadBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_voucherload_voucherload.properties");
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
			log.warn("为itfe_recbiz_voucherload_voucherload读取messages出错", e);
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
	 * ename: voucherLoad
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherLoad(Object o){
        
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
	 * Direction: 提交
	 * ename: voucherCommit
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherCommit(Object o){
        
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
	 * Direction: 凭证退回
	 * ename: voucherBack
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherBack(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 凭证打印
	 * ename: voucherPrint
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherPrint(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 凭证签章
	 * ename: voucherStamp
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherStamp(Object o){
        
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
	 * Direction: 签章撤销
	 * ename: stampCancle
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String stampCancle(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 校验
	 * ename: voucherVerify
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherVerify(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 更新成功
	 * ename: voucherUpdateSuccess
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherUpdateSuccess(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出PDF文件
	 * ename: exportPDF
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportPDF(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 大数据明细校验
	 * ename: bigdatavalid
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String bigdatavalid(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回
	 * ename: returnvoucherload
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String returnvoucherload(Object o){
        
        return "维护界面";
    }
    
	/**
	 * Direction: 导出数据
	 * ename: exportData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportData(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 大数据列表双击
	 * ename: bigdatadouble
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String bigdatadouble(Object o){
        
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
}
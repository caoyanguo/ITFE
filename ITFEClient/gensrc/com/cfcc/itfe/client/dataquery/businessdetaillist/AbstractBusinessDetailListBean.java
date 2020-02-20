package com.cfcc.itfe.client.dataquery.businessdetaillist;

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
import com.cfcc.itfe.service.dataquery.businessdetaillist.IBusinessDetailListService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:BusinessDetailList
 * 组件:BusinessDetailList
 */
public abstract class AbstractBusinessDetailListBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IBusinessDetailListService businessDetailListService = (IBusinessDetailListService)getService(IBusinessDetailListService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    TipsParamDto param = null;
    String biztype = null;
    TipsParamDto searchDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_businessdetaillist_businessdetaillist_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractBusinessDetailListBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_businessdetaillist_businessdetaillist.properties");
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
			log.warn("为itfe_dataquery_businessdetaillist_businessdetaillist读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 报表打印
	 * ename: printReport
	 * 引用方法: 
	 * viewers: 报表显示界面
	 * messages: 
	 */
    public String printReport(Object o){
        
        return "报表显示界面";
    }
    
	/**
	 * Direction: 回到查询界面
	 * ename: backToQuery
	 * 引用方法: 
	 * viewers: 无纸化业务凭证打印
	 * messages: 
	 */
    public String backToQuery(Object o){
        
        return "无纸化业务凭证打印";
    }
    
	/**
	 * Direction: 原始凭证打印
	 * ename: printOriVou
	 * 引用方法: 
	 * viewers: 原始凭证打印统计
	 * messages: 
	 */
    public String printOriVou(Object o){
        
        return "原始凭证打印统计";
    }
    
	/**
	 * Direction: 批量打印
	 * ename: batchPrint
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String batchPrint(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 查询打印主单信息
	 * ename: toReport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String toReport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 查询打印明细信息
	 * ename: toReportOfDetail
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String toReportOfDetail(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 代理库打印明细信息
	 * ename: toReportDetailOfDlk
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String toReportDetailOfDlk(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回到清单查询界面
	 * ename: backToSearch
	 * 引用方法: 
	 * viewers: 无纸化业务清单打印
	 * messages: 
	 */
    public String backToSearch(Object o){
        
        return "无纸化业务清单打印";
    }
    
	/**
	 * Direction: 凭证还原
	 * ename: goVoucherViewer
	 * 引用方法: 
	 * viewers: 凭证还原
	 * messages: 
	 */
    public String goVoucherViewer(Object o){
        
        return "凭证还原";
    }
    
	/**
	 * Direction: 返回查询结果
	 * ename: goSearchResultViewer
	 * 引用方法: 
	 * viewers: 原始凭证打印统计
	 * messages: 
	 */
    public String goSearchResultViewer(Object o){
        
        return "原始凭证打印统计";
    }
    
	/**
	 * Direction: 集中支付对账单打印
	 * ename: printcenterpay
	 * 引用方法: 
	 * viewers: 报表显示界面
	 * messages: 
	 */
    public String printcenterpay(Object o){
        
        return "报表显示界面";
    }
    
	/**
	 * Direction: 代理库清单查询打印
	 * ename: agentNatiTrePrintRpt
	 * 引用方法: 
	 * viewers: 报表显示界面
	 * messages: 
	 */
    public String agentNatiTrePrintRpt(Object o){
        
        return "报表显示界面";
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
        
    public com.cfcc.itfe.facade.data.TipsParamDto getParam() {
        return param;
    }

    public void setParam(com.cfcc.itfe.facade.data.TipsParamDto _param) {
        param = _param;
    }
    
    public java.lang.String getBiztype() {
        return biztype;
    }

    public void setBiztype(java.lang.String _biztype) {
        biztype = _biztype;
    }
    
    public com.cfcc.itfe.facade.data.TipsParamDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.facade.data.TipsParamDto _searchDto) {
        searchDto = _searchDto;
    }
}
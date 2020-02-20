package com.cfcc.itfe.client.dataquery.tvfinincomeonlinedetailblend;

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
import com.cfcc.itfe.service.dataquery.tvfinincomeonlinedetailblend.ITvFinIncomeonlineDetailBlendService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvIncomeonlineIncomeondetailBlendDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * 子系统: DataQuery
 * 模块:TvFinIncomeonlineDetailBlend
 * 组件:TvFinIncomeonlineDetailBlend
 */
public abstract class AbstractTvFinIncomeonlineDetailBlendBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected ITvFinIncomeonlineDetailBlendService tvFinIncomeonlineDetailBlendService = (ITvFinIncomeonlineDetailBlendService)getService(ITvFinIncomeonlineDetailBlendService.class);
		
	/** 属性列表 */
    String reportPath = null;
    List reportRs = null;
    Map reportMap = null;
    String reportDetailPath = null;
    List reportDetailRs = null;
    Map reportDetailMap = null;
    String searchArea = null;
    TvIncomeonlineIncomeondetailBlendDto detailDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tvfinincomeonlinedetailblend_tvfinincomeonlinedetailblend_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractTvFinIncomeonlineDetailBlendBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tvfinincomeonlinedetailblend_tvfinincomeonlinedetailblend.properties");
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
			log.warn("为itfe_dataquery_tvfinincomeonlinedetailblend_tvfinincomeonlinedetailblend读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询
	 * ename: searchBlend
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchBlend(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 勾兑
	 * ename: searchIncomeLineDetail
	 * 引用方法: 
	 * viewers: 电子税票勾兑查询结果
	 * messages: 
	 */
    public String searchIncomeLineDetail(Object o){
        
        return "电子税票勾兑查询结果";
    }
    
	/**
	 * Direction: 修改备注
	 * ename: editdemo
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String editdemo(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 删除
	 * ename: deleteadddata
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String deleteadddata(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 修改保存
	 * ename: editdata
	 * 引用方法: 
	 * viewers: 勾兑入库查询
	 * messages: 
	 */
    public String editdata(Object o){
        
        return "勾兑入库查询";
    }
    
	/**
	 * Direction: 勾兑入库
	 * ename: blendStorage
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String blendStorage(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回
	 * ename: returndefault
	 * 引用方法: 
	 * viewers: 勾兑入库查询
	 * messages: 
	 */
    public String returndefault(Object o){
        
        return "勾兑入库查询";
    }
    
	/**
	 * Direction: 修改页面
	 * ename: goedit
	 * 引用方法: 
	 * viewers: 勾兑数据录入界面
	 * messages: 
	 */
    public String goedit(Object o){
        
        return "勾兑数据录入界面";
    }
    
	/**
	 * Direction: 录入界面
	 * ename: goadd
	 * 引用方法: 
	 * viewers: 勾兑数据录入界面
	 * messages: 
	 */
    public String goadd(Object o){
        
        return "勾兑数据录入界面";
    }
    
	/**
	 * Direction: 不符数据查看
	 * ename: notequalsdetail
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String notequalsdetail(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 相符数据查看
	 * ename: equalsdetail
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String equalsdetail(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 全选反选
	 * ename: selectall
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectall(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 勾兑入库汇总报表
	 * ename: toSummaryReport
	 * 引用方法: 
	 * viewers: 勾兑入库汇总报表
	 * messages: 
	 */
    public String toSummaryReport(Object o){
        
        return "勾兑入库汇总报表";
    }
    
	/**
	 * Direction: 勾兑入库明细报表
	 * ename: toDetailReport
	 * 引用方法: 
	 * viewers: 勾兑入库明细报表
	 * messages: 
	 */
    public String toDetailReport(Object o){
        
        return "勾兑入库明细报表";
    }
    
	/**
	 * Direction: 返回
	 * ename: backToSearch
	 * 引用方法: 
	 * viewers: 勾兑入库查询
	 * messages: 
	 */
    public String backToSearch(Object o){
        
        return "勾兑入库查询";
    }
    
	/**
	 * Direction: 单选
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
        
    public java.lang.String getReportPath() {
        return reportPath;
    }

    public void setReportPath(java.lang.String _reportPath) {
        reportPath = _reportPath;
    }
    
    public java.util.List getReportRs() {
        return reportRs;
    }

    public void setReportRs(java.util.List _reportRs) {
        reportRs = _reportRs;
    }
    
    public java.util.Map getReportMap() {
        return reportMap;
    }

    public void setReportMap(java.util.Map _reportMap) {
        reportMap = _reportMap;
    }
    
    public java.lang.String getReportDetailPath() {
        return reportDetailPath;
    }

    public void setReportDetailPath(java.lang.String _reportDetailPath) {
        reportDetailPath = _reportDetailPath;
    }
    
    public java.util.List getReportDetailRs() {
        return reportDetailRs;
    }

    public void setReportDetailRs(java.util.List _reportDetailRs) {
        reportDetailRs = _reportDetailRs;
    }
    
    public java.util.Map getReportDetailMap() {
        return reportDetailMap;
    }

    public void setReportDetailMap(java.util.Map _reportDetailMap) {
        reportDetailMap = _reportDetailMap;
    }
    
    public java.lang.String getSearchArea() {
        return searchArea;
    }

    public void setSearchArea(java.lang.String _searchArea) {
        searchArea = _searchArea;
    }
    
    public com.cfcc.itfe.persistence.dto.TvIncomeonlineIncomeondetailBlendDto getDetailDto() {
        return detailDto;
    }

    public void setDetailDto(com.cfcc.itfe.persistence.dto.TvIncomeonlineIncomeondetailBlendDto _detailDto) {
        detailDto = _detailDto;
    }
}
package com.cfcc.itfe.client.recbiz.fundinto;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IFileResolveCommonService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.recbiz.fundinto.IFundIntoService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.facade.data.BizDataCountDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:fundInto
 * 组件:FundInto
 */
public abstract class AbstractFundIntoBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService)getService(IFileResolveCommonService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IFundIntoService fundIntoService = (IFundIntoService)getService(IFundIntoService.class);
	protected IUploadConfirmService uploadConfirmService = (IUploadConfirmService)getService(IUploadConfirmService.class);
		
	/** 属性列表 */
    List filepath = null;
    BigDecimal sumamt = null;
    List selectedfilelist = null;
    List showfilelist = null;
    List selecteddatalist = null;
    List showdatalist = null;
    FileResultDto filedata = null;
    ShiboDto shibodto = null;
    TbsTvPayoutDto searchdto = null;
    TbsTvPayoutDto paysdto = null;
    List paylist = null;
    String trecode = null;
    List querylist = null;
    String defvou = null;
    String endvou = null;
    String newbudgcode = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_fundinto_fundinto_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractFundIntoBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_fundinto_fundinto.properties");
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
			log.warn("为itfe_recbiz_fundinto_fundinto读取messages出错", e);
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
	 * viewers: * messages: 
	 */
    public String submit(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回默认界面
	 * ename: goback
	 * 引用方法: 
	 * viewers: 实拨资金数据导入
	 * messages: 
	 */
    public String goback(Object o){
        
        return "实拨资金数据导入";
    }
    
	/**
	 * Direction: 批量确认
	 * ename: plsubmit
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String plsubmit(Object o){
        
        return "";
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
	 * viewers: * messages: 
	 */
    public String zbsubmit(Object o){
        
        return "";
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
	 * Direction: 跳转福建实拨
	 * ename: openfjPayout
	 * 引用方法: 
	 * viewers: 逐笔销号fj
	 * messages: 
	 */
    public String openfjPayout(Object o){
        
        return "逐笔销号fj";
    }
    
	/**
	 * Direction: 查询服务
	 * ename: queryService
	 * 引用方法: 
	 * viewers: 查询界面
	 * messages: 
	 */
    public String queryService(Object o){
        
        return "查询界面";
    }
    
	/**
	 * Direction: 返回福建销号
	 * ename: backtofj
	 * 引用方法: 
	 * viewers: 逐笔销号fj
	 * messages: 
	 */
    public String backtofj(Object o){
        
        return "逐笔销号fj";
    }
    
	/**
	 * Direction: 销号失败
	 * ename: setFail
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String setFail(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 单选一条记录
	 * ename: selOneRecord
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selOneRecord(Object o){
        
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
	 * Direction: 明细删除
	 * ename: delDetail
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delDetail(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 销号失败(2)
	 * ename: setFail2
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String setFail2(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 明细删除2
	 * ename: delDetail2
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delDetail2(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 批量删除全选
	 * ename: pldselectall
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String pldselectall(Object o){
        
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
        
    public java.util.List getFilepath() {
        return filepath;
    }

    public void setFilepath(java.util.List _filepath) {
        filepath = _filepath;
    }
    
    public java.math.BigDecimal getSumamt() {
        return sumamt;
    }

    public void setSumamt(java.math.BigDecimal _sumamt) {
        sumamt = _sumamt;
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
    
    public com.cfcc.itfe.persistence.dto.FileResultDto getFiledata() {
        return filedata;
    }

    public void setFiledata(com.cfcc.itfe.persistence.dto.FileResultDto _filedata) {
        filedata = _filedata;
    }
    
    public com.cfcc.itfe.persistence.dto.ShiboDto getShibodto() {
        return shibodto;
    }

    public void setShibodto(com.cfcc.itfe.persistence.dto.ShiboDto _shibodto) {
        shibodto = _shibodto;
    }
    
    public com.cfcc.itfe.persistence.dto.TbsTvPayoutDto getSearchdto() {
        return searchdto;
    }

    public void setSearchdto(com.cfcc.itfe.persistence.dto.TbsTvPayoutDto _searchdto) {
        searchdto = _searchdto;
    }
    
    public com.cfcc.itfe.persistence.dto.TbsTvPayoutDto getPaysdto() {
        return paysdto;
    }

    public void setPaysdto(com.cfcc.itfe.persistence.dto.TbsTvPayoutDto _paysdto) {
        paysdto = _paysdto;
    }
    
    public java.util.List getPaylist() {
        return paylist;
    }

    public void setPaylist(java.util.List _paylist) {
        paylist = _paylist;
    }
    
    public java.lang.String getTrecode() {
        return trecode;
    }

    public void setTrecode(java.lang.String _trecode) {
        trecode = _trecode;
    }
    
    public java.util.List getQuerylist() {
        return querylist;
    }

    public void setQuerylist(java.util.List _querylist) {
        querylist = _querylist;
    }
    
    public java.lang.String getDefvou() {
        return defvou;
    }

    public void setDefvou(java.lang.String _defvou) {
        defvou = _defvou;
    }
    
    public java.lang.String getEndvou() {
        return endvou;
    }

    public void setEndvou(java.lang.String _endvou) {
        endvou = _endvou;
    }
    
    public java.lang.String getNewbudgcode() {
        return newbudgcode;
    }

    public void setNewbudgcode(java.lang.String _newbudgcode) {
        newbudgcode = _newbudgcode;
    }
}
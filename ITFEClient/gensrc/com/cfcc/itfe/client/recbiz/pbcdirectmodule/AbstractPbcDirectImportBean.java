package com.cfcc.itfe.client.recbiz.pbcdirectmodule;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IFileResolveCommonService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.service.recbiz.fundinto.IFundIntoService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.facade.data.BizDataCountDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:pbcDirectModule
 * 组件:PbcDirectImport
 */
public abstract class AbstractPbcDirectImportBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService)getService(IFileResolveCommonService.class);
	protected IUploadConfirmService uploadConfirmService = (IUploadConfirmService)getService(IUploadConfirmService.class);
	protected IFundIntoService fundIntoService = (IFundIntoService)getService(IFundIntoService.class);
		
	/** 属性列表 */
    List filelist = null;
    BigDecimal sumamt = null;
    List selectedfilelist = null;
    List showfilelist = null;
    List selectedDetaillist = null;
    List showDetaillist = null;
    TbsTvPbcpayDto searchdto = null;
    TbsTvPbcpayDto pbcdto = null;
    String trecode = null;
    List pbclist = null;
    String defvou = null;
    String endvou = null;
    String newbudgcode = null;
    List singleResList = null;
    Integer vouCount = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_pbcdirectmodule_pbcdirectimport_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractPbcDirectImportBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_pbcdirectmodule_pbcdirectimport.properties");
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
			log.warn("为itfe_recbiz_pbcdirectmodule_pbcdirectimport读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 数据加载
	 * ename: dataImport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dataImport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转批量销号
	 * ename: goToBatch
	 * 引用方法: 
	 * viewers: 批量销号
	 * messages: 
	 */
    public String goToBatch(Object o){
        
        return "批量销号";
    }
    
	/**
	 * Direction: 跳转逐笔销号
	 * ename: goToSingle
	 * 引用方法: 
	 * viewers: 逐笔销号
	 * messages: 
	 */
    public String goToSingle(Object o){
        
        return "逐笔销号";
    }
    
	/**
	 * Direction: 跳转默认界面
	 * ename: goToDefault
	 * 引用方法: 
	 * viewers: 人行办理直接支付导入
	 * messages: 
	 */
    public String goToDefault(Object o){
        
        return "人行办理直接支付导入";
    }
    
	/**
	 * Direction: 直接提交
	 * ename: directCommit
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String directCommit(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 批量确认
	 * ename: batchCommit
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String batchCommit(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 逐笔确认
	 * ename: singleCommit
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleCommit(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 批量删除
	 * ename: batchDelete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String batchDelete(Object o){
        
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
	 * Direction: 统计信息
	 * ename: countInfo
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String countInfo(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 批量销号查询
	 * ename: batchQuery
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String batchQuery(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 逐笔销号查询
	 * ename: singleQuery
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleQuery(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转福建模式销号
	 * ename: openFJMode
	 * 引用方法: 
	 * viewers: 逐笔销号fj
	 * messages: 
	 */
    public String openFJMode(Object o){
        
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
	 * Direction: 销号失败
	 * ename: thinkFail
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String thinkFail(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 单击选择一条记录
	 * ename: selOneRecord
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selOneRecord(Object o){
        
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
        
    public java.util.List getFilelist() {
        return filelist;
    }

    public void setFilelist(java.util.List _filelist) {
        filelist = _filelist;
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
    
    public java.util.List getSelectedDetaillist() {
        return selectedDetaillist;
    }

    public void setSelectedDetaillist(java.util.List _selectedDetaillist) {
        selectedDetaillist = _selectedDetaillist;
    }
    
    public java.util.List getShowDetaillist() {
        return showDetaillist;
    }

    public void setShowDetaillist(java.util.List _showDetaillist) {
        showDetaillist = _showDetaillist;
    }
    
    public com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto getSearchdto() {
        return searchdto;
    }

    public void setSearchdto(com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto _searchdto) {
        searchdto = _searchdto;
    }
    
    public com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto getPbcdto() {
        return pbcdto;
    }

    public void setPbcdto(com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto _pbcdto) {
        pbcdto = _pbcdto;
    }
    
    public java.lang.String getTrecode() {
        return trecode;
    }

    public void setTrecode(java.lang.String _trecode) {
        trecode = _trecode;
    }
    
    public java.util.List getPbclist() {
        return pbclist;
    }

    public void setPbclist(java.util.List _pbclist) {
        pbclist = _pbclist;
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
    
    public java.util.List getSingleResList() {
        return singleResList;
    }

    public void setSingleResList(java.util.List _singleResList) {
        singleResList = _singleResList;
    }
    
    public java.lang.Integer getVouCount() {
        return vouCount;
    }

    public void setVouCount(java.lang.Integer _vouCount) {
        vouCount = _vouCount;
    }
}
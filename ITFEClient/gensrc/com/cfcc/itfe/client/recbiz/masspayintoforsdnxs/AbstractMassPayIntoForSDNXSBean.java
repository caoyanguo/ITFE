package com.cfcc.itfe.client.recbiz.masspayintoforsdnxs;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * 子系统: RecBiz
 * 模块:MassPayIntoForSDNXS
 * 组件:MassPayIntoForSDNXS
 */
public abstract class AbstractMassPayIntoForSDNXSBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService)getService(IFileResolveCommonService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IUploadConfirmService uploadConfirmService = (IUploadConfirmService)getService(IUploadConfirmService.class);
		
	/** 属性列表 */
    List filepath = null;
    List selectedfilelist = null;
    List showfilelist = null;
    List selecteddatalist = null;
    List showdatalist = null;
    FileResultDto filedata = null;
    List orgcodelist = null;
    String funccode = null;
    String budgetype = null;
    String trecode = null;
    TvPayoutfinanceDto searchdto = null;
    String sbdgorgcode = null;
    String soutorgcode = null;
    List showftpfilelist = null;
    List selectedftpfilelist = null;
    String sdate = null;
    String ftptrecode = null;
    String sstatus = null;
    List selectftpreturnlist = null;
    List showftpreturnlist = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_masspayintoforsdnxs_masspayintoforsdnxs_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractMassPayIntoForSDNXSBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_masspayintoforsdnxs_masspayintoforsdnxs.properties");
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
			log.warn("为itfe_recbiz_masspayintoforsdnxs_masspayintoforsdnxs读取messages出错", e);
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
	 * viewers: 库区移民发放
	 * messages: 
	 */
    public String goback(Object o){
        
        return "库区移民发放";
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
	 * Direction: 批量全选
	 * ename: plselectall
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String plselectall(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ftp文件加载
	 * ename: ftpfileadd
	 * 引用方法: 
	 * viewers: ftp批量文件
	 * messages: 
	 */
    public String ftpfileadd(Object o){
        
        return "ftp批量文件";
    }
    
	/**
	 * Direction: ftp文件全选
	 * ename: ftpfileselectall
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftpfileselectall(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 读取ftp文件
	 * ename: readftp
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String readftp(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转ftp文件列表
	 * ename: goftpfilelist
	 * 引用方法: 
	 * viewers: ftp批量文件
	 * messages: 
	 */
    public String goftpfilelist(Object o){
        
        return "ftp批量文件";
    }
    
	/**
	 * Direction: 查询读取的ftp数据
	 * ename: queryftpdata
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryftpdata(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ftp汇总文件下载
	 * ename: ftpsummarydown
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftpsummarydown(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 跳转ftp回执列表
	 * ename: goftpreturn
	 * 引用方法: 
	 * viewers: ftp文件回执
	 * messages: 
	 */
    public String goftpreturn(Object o){
        
        return "ftp文件回执";
    }
    
	/**
	 * Direction: 查询ftp回执列表
	 * ename: queryftpreturn
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryftpreturn(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ftp文件回执发送
	 * ename: ftpreturnsend
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftpreturnsend(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 全选/反选
	 * ename: selectallreturn
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectallreturn(Object o){
        
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
    
    public java.util.List getOrgcodelist() {
        return orgcodelist;
    }

    public void setOrgcodelist(java.util.List _orgcodelist) {
        orgcodelist = _orgcodelist;
    }
    
    public java.lang.String getFunccode() {
        return funccode;
    }

    public void setFunccode(java.lang.String _funccode) {
        funccode = _funccode;
    }
    
    public java.lang.String getBudgetype() {
        return budgetype;
    }

    public void setBudgetype(java.lang.String _budgetype) {
        budgetype = _budgetype;
    }
    
    public java.lang.String getTrecode() {
        return trecode;
    }

    public void setTrecode(java.lang.String _trecode) {
        trecode = _trecode;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto getSearchdto() {
        return searchdto;
    }

    public void setSearchdto(com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto _searchdto) {
        searchdto = _searchdto;
    }
    
    public java.lang.String getSbdgorgcode() {
        return sbdgorgcode;
    }

    public void setSbdgorgcode(java.lang.String _sbdgorgcode) {
        sbdgorgcode = _sbdgorgcode;
    }
    
    public java.lang.String getSoutorgcode() {
        return soutorgcode;
    }

    public void setSoutorgcode(java.lang.String _soutorgcode) {
        soutorgcode = _soutorgcode;
    }
    
    public java.util.List getShowftpfilelist() {
        return showftpfilelist;
    }

    public void setShowftpfilelist(java.util.List _showftpfilelist) {
        showftpfilelist = _showftpfilelist;
    }
    
    public java.util.List getSelectedftpfilelist() {
        return selectedftpfilelist;
    }

    public void setSelectedftpfilelist(java.util.List _selectedftpfilelist) {
        selectedftpfilelist = _selectedftpfilelist;
    }
    
    public java.lang.String getSdate() {
        return sdate;
    }

    public void setSdate(java.lang.String _sdate) {
        sdate = _sdate;
    }
    
    public java.lang.String getFtptrecode() {
        return ftptrecode;
    }

    public void setFtptrecode(java.lang.String _ftptrecode) {
        ftptrecode = _ftptrecode;
    }
    
    public java.lang.String getSstatus() {
        return sstatus;
    }

    public void setSstatus(java.lang.String _sstatus) {
        sstatus = _sstatus;
    }
    
    public java.util.List getSelectftpreturnlist() {
        return selectftpreturnlist;
    }

    public void setSelectftpreturnlist(java.util.List _selectftpreturnlist) {
        selectftpreturnlist = _selectftpreturnlist;
    }
    
    public java.util.List getShowftpreturnlist() {
        return showftpreturnlist;
    }

    public void setShowftpreturnlist(java.util.List _showftpreturnlist) {
        showftpreturnlist = _showftpreturnlist;
    }
}
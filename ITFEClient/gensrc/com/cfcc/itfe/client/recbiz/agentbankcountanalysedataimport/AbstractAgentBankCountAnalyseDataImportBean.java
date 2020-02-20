package com.cfcc.itfe.client.recbiz.agentbankcountanalysedataimport;

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
import com.cfcc.itfe.service.recbiz.agentbankcountanalysedataimport.IAgentBankCountAnalyseDataImportService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * 子系统: RecBiz
 * 模块:AgentBankCountAnalyseDataImport
 * 组件:AgentBankCountAnalyseDataImport
 */
public abstract class AbstractAgentBankCountAnalyseDataImportBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IAgentBankCountAnalyseDataImportService agentBankCountAnalyseDataImportService = (IAgentBankCountAnalyseDataImportService)getService(IAgentBankCountAnalyseDataImportService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    List filePath = null;
    TrIncomedayrptDto srSearchDto = null;
    TrStockdayrptDto kcSearchDto = null;
    String treCode = null;
    List treCodelist = null;
    String rptDate = null;
    List bizTypeList = null;
    String bizType = null;
    String msg = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_agentbankcountanalysedataimport_agentbankcountanalysedataimport_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractAgentBankCountAnalyseDataImportBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_agentbankcountanalysedataimport_agentbankcountanalysedataimport.properties");
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
			log.warn("为itfe_recbiz_agentbankcountanalysedataimport_agentbankcountanalysedataimport读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 代理库统计分析数据导入
	 * ename: dataImport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dataImport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 发送报表数据
	 * ename: sendDate
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String sendDate(Object o){
        
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
        
    public java.util.List getFilePath() {
        return filePath;
    }

    public void setFilePath(java.util.List _filePath) {
        filePath = _filePath;
    }
    
    public com.cfcc.itfe.persistence.dto.TrIncomedayrptDto getSrSearchDto() {
        return srSearchDto;
    }

    public void setSrSearchDto(com.cfcc.itfe.persistence.dto.TrIncomedayrptDto _srSearchDto) {
        srSearchDto = _srSearchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TrStockdayrptDto getKcSearchDto() {
        return kcSearchDto;
    }

    public void setKcSearchDto(com.cfcc.itfe.persistence.dto.TrStockdayrptDto _kcSearchDto) {
        kcSearchDto = _kcSearchDto;
    }
    
    public java.lang.String getTreCode() {
        return treCode;
    }

    public void setTreCode(java.lang.String _treCode) {
        treCode = _treCode;
    }
    
    public java.util.List getTreCodelist() {
        return treCodelist;
    }

    public void setTreCodelist(java.util.List _treCodelist) {
        treCodelist = _treCodelist;
    }
    
    public java.lang.String getRptDate() {
        return rptDate;
    }

    public void setRptDate(java.lang.String _rptDate) {
        rptDate = _rptDate;
    }
    
    public java.util.List getBizTypeList() {
        return bizTypeList;
    }

    public void setBizTypeList(java.util.List _bizTypeList) {
        bizTypeList = _bizTypeList;
    }
    
    public java.lang.String getBizType() {
        return bizType;
    }

    public void setBizType(java.lang.String _bizType) {
        bizType = _bizType;
    }
    
    public java.lang.String getMsg() {
        return msg;
    }

    public void setMsg(java.lang.String _msg) {
        msg = _msg;
    }
}
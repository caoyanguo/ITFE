package com.cfcc.itfe.client.dataquery.tfrefundnotice;

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
import com.cfcc.itfe.service.dataquery.tfrefundnotice.ITfRefundNoticeService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TfRefundNoticeDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:TfRefundNotice
 * ���:TfRefundNotice
 */
public abstract class AbstractTfRefundNoticeBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITfRefundNoticeService tfRefundNoticeService = (ITfRefundNoticeService)getService(ITfRefundNoticeService.class);
		
	/** �����б� */
    TfRefundNoticeDto searchDto = null;
    PagingContext pagingcontext = null;
    String reportPath = null;
    List rsList = null;
    HashMap paramMap = null;
    TfRefundNoticeDto printDto = null;
    String reportPathDetail = null;
    List rsListDetail = null;
    HashMap paramMapDetail = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tfrefundnotice_tfrefundnotice_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTfRefundNoticeBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tfrefundnotice_tfrefundnotice.properties");
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
			log.warn("Ϊitfe_dataquery_tfrefundnotice_tfrefundnotice��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��ѯ���������
	 * ename: queryResult
	 * ���÷���: 
	 * viewers: ��ѯ�������
	 * messages: 
	 */
    public String queryResult(Object o){
        
        return "��ѯ�������";
    }
    
	/**
	 * Direction: ���ص���ѯ����
	 * ename: backQuery
	 * ���÷���: 
	 * viewers: ��ѯ����
	 * messages: 
	 */
    public String backQuery(Object o){
        
        return "��ѯ����";
    }
    
	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����
	 * ename: toReport
	 * ���÷���: 
	 * viewers: ����
	 * messages: 
	 */
    public String toReport(Object o){
        
        return "����";
    }
    
	/**
	 * Direction: ������ϸ
	 * ename: detail
	 * ���÷���: 
	 * viewers: ������ϸ
	 * messages: 
	 */
    public String detail(Object o){
        
        return "������ϸ";
    }
    
	/**
	 * Direction: ��ӡ��ϸ
	 * ename: printDetail
	 * ���÷���: 
	 * viewers: ��ϸ����
	 * messages: 
	 */
    public String printDetail(Object o){
        
        return "��ϸ����";
    }
    
	/**
	 * Direction: ������ϸ
	 * ename: backToDetail
	 * ���÷���: 
	 * viewers: ������ϸ
	 * messages: 
	 */
    public String backToDetail(Object o){
        
        return "������ϸ";
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
        
    public com.cfcc.itfe.persistence.dto.TfRefundNoticeDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TfRefundNoticeDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public java.lang.String getReportPath() {
        return reportPath;
    }

    public void setReportPath(java.lang.String _reportPath) {
        reportPath = _reportPath;
    }
    
    public java.util.List getRsList() {
        return rsList;
    }

    public void setRsList(java.util.List _rsList) {
        rsList = _rsList;
    }
    
    public java.util.HashMap getParamMap() {
        return paramMap;
    }

    public void setParamMap(java.util.HashMap _paramMap) {
        paramMap = _paramMap;
    }
    
    public com.cfcc.itfe.persistence.dto.TfRefundNoticeDto getPrintDto() {
        return printDto;
    }

    public void setPrintDto(com.cfcc.itfe.persistence.dto.TfRefundNoticeDto _printDto) {
        printDto = _printDto;
    }
    
    public java.lang.String getReportPathDetail() {
        return reportPathDetail;
    }

    public void setReportPathDetail(java.lang.String _reportPathDetail) {
        reportPathDetail = _reportPathDetail;
    }
    
    public java.util.List getRsListDetail() {
        return rsListDetail;
    }

    public void setRsListDetail(java.util.List _rsListDetail) {
        rsListDetail = _rsListDetail;
    }
    
    public java.util.HashMap getParamMapDetail() {
        return paramMapDetail;
    }

    public void setParamMapDetail(java.util.HashMap _paramMapDetail) {
        paramMapDetail = _paramMapDetail;
    }
}
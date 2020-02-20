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
 * ��ϵͳ: DataQuery
 * ģ��:BusinessDetailList
 * ���:BusinessDetailList
 */
public abstract class AbstractBusinessDetailListBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IBusinessDetailListService businessDetailListService = (IBusinessDetailListService)getService(IBusinessDetailListService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    TipsParamDto param = null;
    String biztype = null;
    TipsParamDto searchDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_businessdetaillist_businessdetaillist_";
	
    /** Message */
    
    
    /** ���캯�� */
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
			log.warn("Ϊitfe_dataquery_businessdetaillist_businessdetaillist��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: �����ӡ
	 * ename: printReport
	 * ���÷���: 
	 * viewers: ������ʾ����
	 * messages: 
	 */
    public String printReport(Object o){
        
        return "������ʾ����";
    }
    
	/**
	 * Direction: �ص���ѯ����
	 * ename: backToQuery
	 * ���÷���: 
	 * viewers: ��ֽ��ҵ��ƾ֤��ӡ
	 * messages: 
	 */
    public String backToQuery(Object o){
        
        return "��ֽ��ҵ��ƾ֤��ӡ";
    }
    
	/**
	 * Direction: ԭʼƾ֤��ӡ
	 * ename: printOriVou
	 * ���÷���: 
	 * viewers: ԭʼƾ֤��ӡͳ��
	 * messages: 
	 */
    public String printOriVou(Object o){
        
        return "ԭʼƾ֤��ӡͳ��";
    }
    
	/**
	 * Direction: ������ӡ
	 * ename: batchPrint
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String batchPrint(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ѯ��ӡ������Ϣ
	 * ename: toReport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String toReport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ѯ��ӡ��ϸ��Ϣ
	 * ename: toReportOfDetail
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String toReportOfDetail(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ������ӡ��ϸ��Ϣ
	 * ename: toReportDetailOfDlk
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String toReportDetailOfDlk(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ص��嵥��ѯ����
	 * ename: backToSearch
	 * ���÷���: 
	 * viewers: ��ֽ��ҵ���嵥��ӡ
	 * messages: 
	 */
    public String backToSearch(Object o){
        
        return "��ֽ��ҵ���嵥��ӡ";
    }
    
	/**
	 * Direction: ƾ֤��ԭ
	 * ename: goVoucherViewer
	 * ���÷���: 
	 * viewers: ƾ֤��ԭ
	 * messages: 
	 */
    public String goVoucherViewer(Object o){
        
        return "ƾ֤��ԭ";
    }
    
	/**
	 * Direction: ���ز�ѯ���
	 * ename: goSearchResultViewer
	 * ���÷���: 
	 * viewers: ԭʼƾ֤��ӡͳ��
	 * messages: 
	 */
    public String goSearchResultViewer(Object o){
        
        return "ԭʼƾ֤��ӡͳ��";
    }
    
	/**
	 * Direction: ����֧�����˵���ӡ
	 * ename: printcenterpay
	 * ���÷���: 
	 * viewers: ������ʾ����
	 * messages: 
	 */
    public String printcenterpay(Object o){
        
        return "������ʾ����";
    }
    
	/**
	 * Direction: ������嵥��ѯ��ӡ
	 * ename: agentNatiTrePrintRpt
	 * ���÷���: 
	 * viewers: ������ʾ����
	 * messages: 
	 */
    public String agentNatiTrePrintRpt(Object o){
        
        return "������ʾ����";
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
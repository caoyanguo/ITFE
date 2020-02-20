package com.cfcc.itfe.client.dataquery.taxfreesearch;

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
import com.cfcc.itfe.service.dataquery.taxfreesearch.ITaxFreeSearchService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFreeDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:TaxFreeSearch
 * ���:TaxFreeSearchUI
 */
public abstract class AbstractTaxFreeSearchUIBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITaxFreeSearchService taxFreeSearchService = (ITaxFreeSearchService)getService(ITaxFreeSearchService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    TvFreeDto dto = null;
    PagingContext pagingcontext = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_taxfreesearch_taxfreesearchui_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTaxFreeSearchUIBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_taxfreesearch_taxfreesearchui.properties");
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
			log.warn("Ϊitfe_dataquery_taxfreesearch_taxfreesearchui��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: ��ֵ���Ϣ�б�
	 * messages: 
	 */
    public String search(Object o){
        
        return "��ֵ���Ϣ�б�";
    }
    
	/**
	 * Direction: ����
	 * ename: reback
	 * ���÷���: 
	 * viewers: ��ֵ���ѯ����
	 * messages: 
	 */
    public String reback(Object o){
        
        return "��ֵ���ѯ����";
    }
    
	/**
	 * Direction: ȫѡ
	 * ename: selectAll
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ʧ��
	 * ename: updateFail
	 * ���÷���: 
	 * viewers: ��ֵ���Ϣ�б�
	 * messages: 
	 */
    public String updateFail(Object o){
        
        return "��ֵ���Ϣ�б�";
    }
    
	/**
	 * Direction: ���³ɹ�
	 * ename: updateSuccess
	 * ���÷���: 
	 * viewers: �˿���Ϣ�б�
	 * messages: 
	 */
    public String updateSuccess(Object o){
        
        return "�˿���Ϣ�б�";
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
        
    public com.cfcc.itfe.persistence.dto.TvFreeDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TvFreeDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
}
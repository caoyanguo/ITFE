package com.cfcc.itfe.client.dataquery.tvtaxcancel;

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
import com.cfcc.itfe.service.dataquery.tvtaxcancel.ITvTaxCancelService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TvTaxCancelDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:TvTaxCancel
 * ���:TvTaxCancel
 */
public abstract class AbstractTvTaxCancelBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITvTaxCancelService tvTaxCancelService = (ITvTaxCancelService)getService(ITvTaxCancelService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    TvTaxCancelDto dto = null;
    PagingContext pagingcontext = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tvtaxcancel_tvtaxcancel_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTvTaxCancelBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tvtaxcancel_tvtaxcancel.properties");
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
			log.warn("Ϊitfe_dataquery_tvtaxcancel_tvtaxcancel��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
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
	 * Direction: ��ѯ
	 * ename: taxCancelQuery
	 * ���÷���: 
	 * viewers: ʵʱ������ѯ���
	 * messages: 
	 */
    public String taxCancelQuery(Object o){
        
        return "ʵʱ������ѯ���";
    }
    
	/**
	 * Direction: ����
	 * ename: goBack
	 * ���÷���: 
	 * viewers: ʵʱ������Ϣ��ѯ
	 * messages: 
	 */
    public String goBack(Object o){
        
        return "ʵʱ������Ϣ��ѯ";
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
        
    public com.cfcc.itfe.persistence.dto.TvTaxCancelDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TvTaxCancelDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
}
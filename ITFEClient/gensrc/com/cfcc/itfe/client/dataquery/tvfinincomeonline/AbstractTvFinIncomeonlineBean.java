package com.cfcc.itfe.client.dataquery.tvfinincomeonline;

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
import com.cfcc.itfe.service.dataquery.tvfinincomeonline.ITvFinIncomeonlineService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:TvFinIncomeonline
 * ���:TvFinIncomeonline
 */
public abstract class AbstractTvFinIncomeonlineBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITvFinIncomeonlineService tvFinIncomeonlineService = (ITvFinIncomeonlineService)getService(ITvFinIncomeonlineService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService)getService(IItfeCacheService.class);
		
	/** �����б� */
    TvFinIncomeonlineDto dto = null;
    PagingContext pagingcontext = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_tvfinincomeonline_tvfinincomeonline_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTvFinIncomeonlineBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_tvfinincomeonline_tvfinincomeonline.properties");
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
			log.warn("Ϊitfe_dataquery_tvfinincomeonline_tvfinincomeonline��ȡmessages����", e);
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
	 * ename: query
	 * ���÷���: 
	 * viewers: ����˰Ʊ��Ϣ��ѯ���
	 * messages: 
	 */
    public String query(Object o){
        
        return "����˰Ʊ��Ϣ��ѯ���";
    }
    
	/**
	 * Direction: ����
	 * ename: goBack
	 * ���÷���: 
	 * viewers: ����˰Ʊ��Ϣ��ѯ����
	 * messages: 
	 */
    public String goBack(Object o){
        
        return "����˰Ʊ��Ϣ��ѯ����";
    }
    
	/**
	 * Direction: У����˰��������Ӧ��ϵ
	 * ename: checkTaxPayCodeOrTrecode
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String checkTaxPayCodeOrTrecode(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �ֶ�������ֳ�
	 * ename: makeDivide
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String makeDivide(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ȫѡ/��ѡ
	 * ename: selectAllOrNone
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAllOrNone(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����
	 * ename: exportTable
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportTable(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
}
package com.cfcc.itfe.client.dataquery.bigdatapayreckback;

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
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:bigdataPayreckBack
 * ���:BigdataPayreckBack
 */
public abstract class AbstractBigdataPayreckBackBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    	
	/** �����б� */
    TvPayreckBigdataBackDto searchdto = null;
    TvPayreckBigdataBackDto updatedto = null;
    List selectDataList = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_bigdatapayreckback_bigdatapayreckback_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractBigdataPayreckBackBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_bigdatapayreckback_bigdatapayreckback.properties");
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
			log.warn("Ϊitfe_dataquery_bigdatapayreckback_bigdatapayreckback��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ���ݲ�ѯ
	 * ename: datasearch
	 * ���÷���: 
	 * viewers: �������˿����ݲ�ѯ���
	 * messages: 
	 */
    public String datasearch(Object o){
        
        return "�������˿����ݲ�ѯ���";
    }
    
	/**
	 * Direction: ����
	 * ename: returnSearch
	 * ���÷���: 
	 * viewers: �������˿����ݲ�ѯ����
	 * messages: 
	 */
    public String returnSearch(Object o){
        
        return "�������˿����ݲ�ѯ����";
    }
    
	/**
	 * Direction: ˫���¼�
	 * ename: doubleclick
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleclick(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ȫѡ
	 * ename: checkall
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String checkall(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ѡ
	 * ename: initall
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String initall(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackDto getSearchdto() {
        return searchdto;
    }

    public void setSearchdto(com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackDto _searchdto) {
        searchdto = _searchdto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackDto getUpdatedto() {
        return updatedto;
    }

    public void setUpdatedto(com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackDto _updatedto) {
        updatedto = _updatedto;
    }
    
    public java.util.List getSelectDataList() {
        return selectDataList;
    }

    public void setSelectDataList(java.util.List _selectDataList) {
        selectDataList = _selectDataList;
    }
}
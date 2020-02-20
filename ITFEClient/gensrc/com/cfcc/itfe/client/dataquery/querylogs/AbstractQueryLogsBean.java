package com.cfcc.itfe.client.dataquery.querylogs;

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
import com.cfcc.itfe.service.dataquery.querylogs.IQueryLogsService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:querylogs
 * ���:QueryLogs
 */
public abstract class AbstractQueryLogsBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IQueryLogsService queryLogsService = (IQueryLogsService)getService(IQueryLogsService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_querylogs_querylogs_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractQueryLogsBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_querylogs_querylogs.properties");
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
			log.warn("Ϊitfe_dataquery_querylogs_querylogs��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: �շ���־��ѯ
	 * ename: queryLogs
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryLogs(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��־��ϸ��ѯ
	 * ename: queryLogDetail
	 * ���÷���: 
	 * viewers: ��־��ϸ��Ϣ
	 * messages: 
	 */
    public String queryLogDetail(Object o){
        
        return "��־��ϸ��Ϣ";
    }
    
	/**
	 * Direction: ������־��ѯ
	 * ename: returnQueryLogs
	 * ���÷���: 
	 * viewers: �շ���־��ѯ
	 * messages: 
	 */
    public String returnQueryLogs(Object o){
        
        return "�շ���־��ѯ";
    }
    
	/**
	 * Direction: ������ϸ��ѯ
	 * ename: returnQueryLogDetail
	 * ���÷���: 
	 * viewers: �շ���־��ѯ
	 * messages: 
	 */
    public String returnQueryLogDetail(Object o){
        
        return "�շ���־��ѯ";
    }
    
	/**
	 * Direction: ��ѡһ����¼
	 * ename: selOneRecode
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selOneRecode(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��������
	 * ename: attachDownload
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String attachDownload(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �����յ�����Ϣ
	 * ename: recvDelete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String recvDelete(Object o){
        
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
    }
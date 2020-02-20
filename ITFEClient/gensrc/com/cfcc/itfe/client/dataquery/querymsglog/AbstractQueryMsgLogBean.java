package com.cfcc.itfe.client.dataquery.querymsglog;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
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
 * ģ��:queryMsgLog
 * ���:QueryMsgLog
 */
public abstract class AbstractQueryMsgLogBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IQueryLogsService queryLogsService = (IQueryLogsService)getService(IQueryLogsService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService)getService(IItfeCacheService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_querymsglog_querymsglog_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractQueryMsgLogBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_querymsglog_querymsglog.properties");
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
			log.warn("Ϊitfe_dataquery_querymsglog_querymsglog��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: �����շ���־��ѯ
	 * ename: searchMsgLog
	 * ���÷���: 
	 * viewers: ������־��ѯ���
	 * messages: 
	 */
    public String searchMsgLog(Object o){
        
        return "������־��ѯ���";
    }
    
	/**
	 * Direction: ������־��ѯ����
	 * ename: backSearch
	 * ���÷���: 
	 * viewers: �����շ���־��ѯ
	 * messages: 
	 */
    public String backSearch(Object o){
        
        return "�����շ���־��ѯ";
    }
    
	/**
	 * Direction: ��־�����¼�
	 * ename: singleClickLog
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleClickLog(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ������־����
	 * ename: download
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String download(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �����ط�
	 * ename: msgresend
	 * ���÷���: 
	 * viewers: ������־��ѯ���
	 * messages: 
	 */
    public String msgresend(Object o){
        
        return "������־��ѯ���";
    }
    
	/**
	 * Direction: ����ʧ��
	 * ename: updateFail
	 * ���÷���: 
	 * viewers: ������־��ѯ���
	 * messages: 
	 */
    public String updateFail(Object o){
        
        return "������־��ѯ���";
    }
    
	/**
	 * Direction: ���ձ�����־��������
	 * ename: downloadAll
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String downloadAll(Object o){
        
        return "";
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
	 * Direction: ˢ��
	 * ename: refreshrs
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String refreshrs(Object o){
        
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
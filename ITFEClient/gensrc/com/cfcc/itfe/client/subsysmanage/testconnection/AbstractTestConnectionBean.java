package com.cfcc.itfe.client.subsysmanage.testconnection;

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
import com.cfcc.itfe.service.subsysmanage.testconnection.ITestConnectionService;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * ��ϵͳ: SubSysManage
 * ģ��:testConnection
 * ���:TestConnection
 */
public abstract class AbstractTestConnectionBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITestConnectionService testConnectionService = (ITestConnectionService)getService(ITestConnectionService.class);
		
	/** �����б� */
    String srcvmsgnode = null;
    String smsgdate = null;
    List resultList = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_subsysmanage_testconnection_testconnection_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTestConnectionBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_subsysmanage_testconnection_testconnection.properties");
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
			log.warn("Ϊitfe_subsysmanage_testconnection_testconnection��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: �����Բ���
	 * ename: testConnect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String testConnect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���Խ����ѯ
	 * ename: testResult
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String testResult(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���������Բ���
	 * ename: connect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String connect(Object o){
        
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
        
    public java.lang.String getSrcvmsgnode() {
        return srcvmsgnode;
    }

    public void setSrcvmsgnode(java.lang.String _srcvmsgnode) {
        srcvmsgnode = _srcvmsgnode;
    }
    
    public java.lang.String getSmsgdate() {
        return smsgdate;
    }

    public void setSmsgdate(java.lang.String _smsgdate) {
        smsgdate = _smsgdate;
    }
    
    public java.util.List getResultList() {
        return resultList;
    }

    public void setResultList(java.util.List _resultList) {
        resultList = _resultList;
    }
}
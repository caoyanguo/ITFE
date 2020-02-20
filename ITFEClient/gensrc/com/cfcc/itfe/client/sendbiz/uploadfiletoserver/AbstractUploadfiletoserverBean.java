package com.cfcc.itfe.client.sendbiz.uploadfiletoserver;

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
import com.cfcc.itfe.service.sendbiz.uploadfiletoserver.IUploadfiletoserverService;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * ��ϵͳ: SendBiz
 * ģ��:uploadfiletoserver
 * ���:Uploadfiletoserver
 */
public abstract class AbstractUploadfiletoserverBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IUploadfiletoserverService uploadfiletoserverService = (IUploadfiletoserverService)getService(IUploadfiletoserverService.class);
		
	/** �����б� */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_sendbiz_uploadfiletoserver_uploadfiletoserver_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractUploadfiletoserverBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_sendbiz_uploadfiletoserver_uploadfiletoserver.properties");
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
			log.warn("Ϊitfe_sendbiz_uploadfiletoserver_uploadfiletoserver��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: �ϴ�
	 * ename: uploadfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String uploadfile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: searchfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String searchfile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ɾ��
	 * ename: deletefile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String deletefile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����
	 * ename: downloadfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String downloadfile(Object o){
        
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
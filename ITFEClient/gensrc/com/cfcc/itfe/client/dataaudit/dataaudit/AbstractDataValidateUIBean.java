package com.cfcc.itfe.client.dataaudit.dataaudit;

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

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * ��ϵͳ: DataAudit
 * ģ��:DataAudit
 * ���:DataValidateUI
 */
public abstract class AbstractDataValidateUIBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    	
	/** �����б� */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataaudit_dataaudit_datavalidateui_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractDataValidateUIBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataaudit_dataaudit_datavalidateui.properties");
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
			log.warn("Ϊitfe_dataaudit_dataaudit_datavalidateui��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��ѯУ������
	 * ename: queryValDatas
	 * ���÷���: 
	 * viewers: ���ݲ�ѯ
	 * messages: 
	 */
    public String queryValDatas(Object o){
        
        return "���ݲ�ѯ";
    }
    
	/**
	 * Direction: ��ѯ������ϸ
	 * ename: queryDataDetail
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String queryDataDetail(Object o){
        
        return "��ѯ���";
    }
    
	/**
	 * Direction: ���ز�ѯ����
	 * ename: backQueryDatas
	 * ���÷���: 
	 * viewers: ����У��
	 * messages: 
	 */
    public String backQueryDatas(Object o){
        
        return "����У��";
    }
    
	/**
	 * Direction: ������ϸ��ѯ
	 * ename: backQueryDataDetail
	 * ���÷���: 
	 * viewers: ���ݲ�ѯ
	 * messages: 
	 */
    public String backQueryDataDetail(Object o){
        
        return "���ݲ�ѯ";
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
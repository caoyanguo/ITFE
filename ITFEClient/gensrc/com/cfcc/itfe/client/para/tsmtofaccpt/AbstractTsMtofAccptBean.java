package com.cfcc.itfe.client.para.tsmtofaccpt;

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
 * @time   19-12-08 13:00:40
 * @generated
 * ��ϵͳ: Para
 * ģ��:tsMtofAccpt
 * ���:TsMtofAccpt
 */
public abstract class AbstractTsMtofAccptBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    	
	/** �����б� */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsmtofaccpt_tsmtofaccpt_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTsMtofAccptBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsmtofaccpt_tsmtofaccpt.properties");
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
			log.warn("Ϊitfe_para_tsmtofaccpt_tsmtofaccpt��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��ת¼�����
	 * ename: goInput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String goInput(Object o){
        
        return "¼�����";
    }
    
	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���޸Ľ���
	 * ename: goModify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String goModify(Object o){
        
        return "�޸Ľ���";
    }
    
	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String modifySave(Object o){
        
        return "��ѯ���";
    }
    
	/**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String backMaintenance(Object o){
        
        return "��ѯ���";
    }
    
	/**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String inputSave(Object o){
        
        return "��ѯ���";
    }
    
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
	 * ename: search
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String search(Object o){
        
        return "��ѯ���";
    }
    
	/**
	 * Direction: ����
	 * ename: goBack
	 * ���÷���: 
	 * viewers: ��Ϣ��ѯ
	 * messages: 
	 */
    public String goBack(Object o){
        
        return "��Ϣ��ѯ";
    }
    
	/**
	 * Direction: ���ݵ���
	 * ename: uploadimport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String uploadimport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �����ӡ
	 * ename: reportprint
	 * ���÷���: 
	 * viewers: �����ӡ
	 * messages: 
	 */
    public String reportprint(Object o){
        
        return "�����ӡ";
    }
    
	/**
	 * Direction: �����ݵ������
	 * ename: gouploadimport
	 * ���÷���: 
	 * viewers: ���ݵ���
	 * messages: 
	 */
    public String gouploadimport(Object o){
        
        return "���ݵ���";
    }
    
	/**
	 * Direction: ��¼�����
	 * ename: searchgoinput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String searchgoinput(Object o){
        
        return "¼�����";
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
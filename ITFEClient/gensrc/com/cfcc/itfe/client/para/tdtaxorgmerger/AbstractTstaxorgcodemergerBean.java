package com.cfcc.itfe.client.para.tdtaxorgmerger;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tdtaxorgmerger.ITstaxorgcodemergerService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * ��ϵͳ: Para
 * ģ��:tdtaxorgmerger
 * ���:Tstaxorgcodemerger
 */
public abstract class AbstractTstaxorgcodemergerBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected ITstaxorgcodemergerService tstaxorgcodemergerService = (ITstaxorgcodemergerService)getService(ITstaxorgcodemergerService.class);
		
	/** �����б� */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tdtaxorgmerger_tstaxorgcodemerger_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTstaxorgcodemergerBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tdtaxorgmerger_tstaxorgcodemerger.properties");
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
			log.warn("Ϊitfe_para_tdtaxorgmerger_tstaxorgcodemerger��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ����
	 * ename: add
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String add(Object o){
        
        return "¼�����";
    }
    
	/**
	 * Direction: �޸�
	 * ename: modify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String modify(Object o){
        
        return "�޸Ľ���";
    }
    
	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String delete(Object o){
        
        return "ά������";
    }
    
	/**
	 * Direction: ����ά������
	 * ename: backmain
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String backmain(Object o){
        
        return "ά������";
    }
    
	/**
	 * Direction: ��¼�����
	 * ename: toadd
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String toadd(Object o){
        
        return "¼�����";
    }
    
	/**
	 * Direction: ���޸Ľ���
	 * ename: tomodify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String tomodify(Object o){
        
        return "�޸Ľ���";
    }
    
	/**
	 * Direction: ��ѡ
	 * ename: singleselect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleselect(Object o){
        
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
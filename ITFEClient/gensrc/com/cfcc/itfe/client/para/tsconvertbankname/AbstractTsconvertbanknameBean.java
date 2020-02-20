package com.cfcc.itfe.client.para.tsconvertbankname;

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
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * ��ϵͳ: Para
 * ģ��:tsconvertbankname
 * ���:Tsconvertbankname
 */
public abstract class AbstractTsconvertbanknameBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    	
	/** �����б� */
    List resultlist = null;
    TsConvertbanknameDto searchDto = null;
    TsConvertbanknameDto detailDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsconvertbankname_tsconvertbankname_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTsconvertbanknameBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsconvertbankname_tsconvertbankname.properties");
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
			log.warn("Ϊitfe_para_tsconvertbankname_tsconvertbankname��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��񵥻�
	 * ename: singleclick
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleclick(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���˫��
	 * ename: doubleclick
	 * ���÷���: 
	 * viewers: ��Ϣ�޸�
	 * messages: 
	 */
    public String doubleclick(Object o){
        
        return "��Ϣ�޸�";
    }
    
	/**
	 * Direction: ��ת¼�����
	 * ename: goinputview
	 * ���÷���: 
	 * viewers: ��Ϣ¼��
	 * messages: 
	 */
    public String goinputview(Object o){
        
        return "��Ϣ¼��";
    }
    
	/**
	 * Direction: ��ת�޸Ľ���
	 * ename: gomodview
	 * ���÷���: 
	 * viewers: ��Ϣ�޸�
	 * messages: 
	 */
    public String gomodview(Object o){
        
        return "��Ϣ�޸�";
    }
    
	/**
	 * Direction: ������ҳ��
	 * ename: gomainview
	 * ���÷���: 
	 * viewers: �����������ղ���ά��
	 * messages: 
	 */
    public String gomainview(Object o){
        
        return "�����������ղ���ά��";
    }
    
	/**
	 * Direction: ɾ������
	 * ename: del
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String del(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����
	 * ename: inputsave
	 * ���÷���: 
	 * viewers: �����������ղ���ά��
	 * messages: 
	 */
    public String inputsave(Object o){
        
        return "�����������ղ���ά��";
    }
    
	/**
	 * Direction: �޸ı���
	 * ename: modsave
	 * ���÷���: 
	 * viewers: �����������ղ���ά��
	 * messages: 
	 */
    public String modsave(Object o){
        
        return "�����������ղ���ά��";
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: �����������ղ���ά��
	 * messages: 
	 */
    public String search(Object o){
        
        return "�����������ղ���ά��";
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
        
    public java.util.List getResultlist() {
        return resultlist;
    }

    public void setResultlist(java.util.List _resultlist) {
        resultlist = _resultlist;
    }
    
    public com.cfcc.itfe.persistence.dto.TsConvertbanknameDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TsConvertbanknameDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TsConvertbanknameDto getDetailDto() {
        return detailDto;
    }

    public void setDetailDto(com.cfcc.itfe.persistence.dto.TsConvertbanknameDto _detailDto) {
        detailDto = _detailDto;
    }
}
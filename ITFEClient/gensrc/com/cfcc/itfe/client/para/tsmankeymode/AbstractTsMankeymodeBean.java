package com.cfcc.itfe.client.para.tsmankeymode;

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
import com.cfcc.itfe.service.para.tsmankeymode.ITsMankeymodeService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeymodeDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * ��ϵͳ: Para
 * ģ��:tsmankeymode
 * ���:TsMankeymode
 */
public abstract class AbstractTsMankeymodeBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITsMankeymodeService tsMankeymodeService = (ITsMankeymodeService)getService(ITsMankeymodeService.class);
		
	/** �����б� */
    TsMankeymodeDto dto = null;
    PagingContext pagingContext = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsmankeymode_tsmankeymode_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTsMankeymodeBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsmankeymode_tsmankeymode.properties");
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
			log.warn("Ϊitfe_para_tsmankeymode_tsmankeymode��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ת����Կģʽ�޸�
	 * ename: toKeymodemodify
	 * ���÷���: 
	 * viewers: ��Կģʽ�޸�
	 * messages: 
	 */
    public String toKeymodemodify(Object o){
        
        return "��Կģʽ�޸�";
    }
    
	/**
	 * Direction: ��Կģʽ�޸�
	 * ename: keymodeModify
	 * ���÷���: 
	 * viewers: ��Կģʽ�б�
	 * messages: 
	 */
    public String keymodeModify(Object o){
        
        return "��Կģʽ�б�";
    }
    
	/**
	 * Direction: ת����Կģʽ�б�
	 * ename: toKeylist
	 * ���÷���: 
	 * viewers: ��Կģʽ�б�
	 * messages: 
	 */
    public String toKeylist(Object o){
        
        return "��Կģʽ�б�";
    }
    
	/**
	 * Direction: ����ѡ�ж���
	 * ename: clickSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String clickSelect(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TsMankeymodeDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TsMankeymodeDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingContext() {
        return pagingContext;
    }

    public void setPagingContext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingContext) {
        pagingContext = _pagingContext;
    }
}
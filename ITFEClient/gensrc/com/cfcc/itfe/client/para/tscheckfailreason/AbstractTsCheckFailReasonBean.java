package com.cfcc.itfe.client.para.tscheckfailreason;

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
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * ��ϵͳ: Para
 * ģ��:TsCheckFailReason
 * ���:TsCheckFailReason
 */
public abstract class AbstractTsCheckFailReasonBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    TsCheckfailreasonDto dto = null;
    PagingContext pagingcontext = null;
    TsCheckfailreasonDto selectedDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tscheckfailreason_tscheckfailreason_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTsCheckFailReasonBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tscheckfailreason_tscheckfailreason.properties");
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
			log.warn("Ϊitfe_para_tscheckfailreason_tscheckfailreason��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: ����ʧ��ԭ��ά��ҳ
	 * messages: 
	 */
    public String search(Object o){
        
        return "����ʧ��ԭ��ά��ҳ";
    }
    
	/**
	 * Direction: ¼��
	 * ename: newInput
	 * ���÷���: 
	 * viewers: ����ʧ��ԭ��¼��ҳ
	 * messages: 
	 */
    public String newInput(Object o){
        
        return "����ʧ��ԭ��¼��ҳ";
    }
    
	/**
	 * Direction: �޸���ת
	 * ename: updateDireck
	 * ���÷���: 
	 * viewers: ����ʧ��ԭ���޸�ҳ
	 * messages: 
	 */
    public String updateDireck(Object o){
        
        return "����ʧ��ԭ���޸�ҳ";
    }
    
	/**
	 * Direction: ����
	 * ename: save
	 * ���÷���: 
	 * viewers: ����ʧ��ԭ��ά��ҳ
	 * messages: 
	 */
    public String save(Object o){
        
        return "����ʧ��ԭ��ά��ҳ";
    }
    
	/**
	 * Direction: �޸�
	 * ename: update
	 * ���÷���: 
	 * viewers: ����ʧ��ԭ��ά��ҳ
	 * messages: 
	 */
    public String update(Object o){
        
        return "����ʧ��ԭ��ά��ҳ";
    }
    
	/**
	 * Direction: ȡ��
	 * ename: exit
	 * ���÷���: 
	 * viewers: ����ʧ��ԭ��ά��ҳ
	 * messages: 
	 */
    public String exit(Object o){
        
        return "����ʧ��ԭ��ά��ҳ";
    }
    
	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: ����ʧ��ԭ��ά��ҳ
	 * messages: 
	 */
    public String delete(Object o){
        
        return "����ʧ��ԭ��ά��ҳ";
    }
    
	/**
	 * Direction: ˫����ת�޸�
	 * ename: doubleclickToUpdate
	 * ���÷���: 
	 * viewers: ����ʧ��ԭ���޸�ҳ
	 * messages: 
	 */
    public String doubleclickToUpdate(Object o){
        
        return "����ʧ��ԭ���޸�ҳ";
    }
    
	/**
	 * Direction: �����¼�
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto getSelectedDto() {
        return selectedDto;
    }

    public void setSelectedDto(com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto _selectedDto) {
        selectedDto = _selectedDto;
    }
}
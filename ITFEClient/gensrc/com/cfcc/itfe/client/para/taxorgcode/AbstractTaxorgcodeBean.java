package com.cfcc.itfe.client.para.taxorgcode;

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
import com.cfcc.itfe.service.para.tstaxorg.ITsTaxorgService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * ��ϵͳ: Para
 * ģ��:taxorgcode
 * ���:Taxorgcode
 */
public abstract class AbstractTaxorgcodeBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITsTaxorgService tsTaxorgService = (ITsTaxorgService)getService(ITsTaxorgService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    TsTaxorgDto dto = null;
    PagingContext pagingcontext = null;
    TsTaxorgDto searchdto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_taxorgcode_taxorgcode_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTaxorgcodeBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_taxorgcode_taxorgcode.properties");
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
			log.warn("Ϊitfe_para_taxorgcode_taxorgcode��ȡmessages����", e);
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
	 * viewers: ���ջ�����ڵ��Ӧ��ϵ��ѯ���
	 * messages: 
	 */
    public String search(Object o){
        
        return "���ջ�����ڵ��Ӧ��ϵ��ѯ���";
    }
    
	/**
	 * Direction: ����
	 * ename: adddata
	 * ���÷���: 
	 * viewers: ���ջ�����ڵ��Ӧ��ϵ¼�����
	 * messages: 
	 */
    public String adddata(Object o){
        
        return "���ջ�����ڵ��Ӧ��ϵ¼�����";
    }
    
	/**
	 * Direction: �޸�
	 * ename: updatedata
	 * ���÷���: 
	 * viewers: ���ջ�����ڵ��Ӧ��ϵ�޸Ľ���
	 * messages: 
	 */
    public String updatedata(Object o){
        
        return "���ջ�����ڵ��Ӧ��ϵ�޸Ľ���";
    }
    
	/**
	 * Direction: ɾ��
	 * ename: deletedata
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String deletedata(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����
	 * ename: reback
	 * ���÷���: 
	 * viewers: ���ջ�����ڵ��Ӧ��ϵ��ѯ���
	 * messages: 
	 */
    public String reback(Object o){
        
        return "���ջ�����ڵ��Ӧ��ϵ��ѯ���";
    }
    
	/**
	 * Direction: ����
	 * ename: savedata
	 * ���÷���: 
	 * viewers: ���ջ�����ڵ��Ӧ��ϵ��ѯ���
	 * messages: 
	 */
    public String savedata(Object o){
        
        return "���ջ�����ڵ��Ӧ��ϵ��ѯ���";
    }
    
	/**
	 * Direction: �޸ı���
	 * ename: saveupdae
	 * ���÷���: 
	 * viewers: ���ջ�����ڵ��Ӧ��ϵ��ѯ���
	 * messages: 
	 */
    public String saveupdae(Object o){
        
        return "���ջ�����ڵ��Ӧ��ϵ��ѯ���";
    }
    
	/**
	 * Direction: ����ѡ��
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
        
    public com.cfcc.itfe.persistence.dto.TsTaxorgDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TsTaxorgDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TsTaxorgDto getSearchdto() {
        return searchdto;
    }

    public void setSearchdto(com.cfcc.itfe.persistence.dto.TsTaxorgDto _searchdto) {
        searchdto = _searchdto;
    }
}
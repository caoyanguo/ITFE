package com.cfcc.itfe.client.para.tsconvertrea;

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
import com.cfcc.itfe.service.para.tsconvertrea.ITsconvertreaService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsConvertreaDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * ��ϵͳ: Para
 * ģ��:tsconvertrea
 * ���:Tsconvertrea
 */
public abstract class AbstractTsconvertreaBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected ITsconvertreaService tsconvertreaService = (ITsconvertreaService)getService(ITsconvertreaService.class);
		
	/** �����б� */
    TsConvertreaDto searchDto = null;
    TsConvertreaDto detailDto = null;
    List searchResult = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsconvertrea_tsconvertrea_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTsconvertreaBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsconvertrea_tsconvertrea.properties");
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
			log.warn("Ϊitfe_para_tsconvertrea_tsconvertrea��ȡmessages����", e);
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
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ɾ��
	 * ename: del
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String del(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ת�޸���Ϣҳ��
	 * ename: gomodview
	 * ���÷���: 
	 * viewers: �޸�
	 * messages: 
	 */
    public String gomodview(Object o){
        
        return "�޸�";
    }
    
	/**
	 * Direction: ��ת�����Ϣҳ��
	 * ename: goaddview
	 * ���÷���: 
	 * viewers: ����
	 * messages: 
	 */
    public String goaddview(Object o){
        
        return "����";
    }
    
	/**
	 * Direction: ����Ĭ�Ͻ���
	 * ename: gomainview
	 * ���÷���: 
	 * viewers: ��ѯ
	 * messages: 
	 */
    public String gomainview(Object o){
        
        return "��ѯ";
    }
    
	/**
	 * Direction: �����Ϣ
	 * ename: addInfo
	 * ���÷���: 
	 * viewers: ��ѯ
	 * messages: 
	 */
    public String addInfo(Object o){
        
        return "��ѯ";
    }
    
	/**
	 * Direction: �޸���Ϣ
	 * ename: modInfo
	 * ���÷���: 
	 * viewers: ��ѯ
	 * messages: 
	 */
    public String modInfo(Object o){
        
        return "��ѯ";
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
        
    public com.cfcc.itfe.persistence.dto.TsConvertreaDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TsConvertreaDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TsConvertreaDto getDetailDto() {
        return detailDto;
    }

    public void setDetailDto(com.cfcc.itfe.persistence.dto.TsConvertreaDto _detailDto) {
        detailDto = _detailDto;
    }
    
    public java.util.List getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(java.util.List _searchResult) {
        searchResult = _searchResult;
    }
}
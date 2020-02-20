package com.cfcc.itfe.client.para.tspaybank;

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
import com.cfcc.itfe.service.para.tspaybank.ITsPaybankService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:38
 * @generated
 * ��ϵͳ: Para
 * ģ��:TsPaybank
 * ���:TsPaybank
 */
public abstract class AbstractTsPaybankBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITsPaybankService tsPaybankService = (ITsPaybankService)getService(ITsPaybankService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    TsPaybankDto dto = null;
    PagingContext pagingcontext = null;
    TsPaybankDto searchdto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tspaybank_tspaybank_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTsPaybankBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tspaybank_tspaybank.properties");
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
			log.warn("Ϊitfe_para_tspaybank_tspaybank��ȡmessages����", e);
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
	 * viewers: ��Ϣ¼��
	 * messages: 
	 */
    public String goInput(Object o){
        
        return "��Ϣ¼��";
    }
    
	/**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: ��ѯ���ҳ��
	 * messages: 
	 */
    public String inputSave(Object o){
        
        return "��ѯ���ҳ��";
    }
    
	/**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ��ѯ����
	 * messages: 
	 */
    public String backMaintenance(Object o){
        
        return "��ѯ����";
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
	 * viewers: ��Ϣ�޸�
	 * messages: 
	 */
    public String goModify(Object o){
        
        return "��Ϣ�޸�";
    }
    
	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����
	 * ename: dataExport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String dataExport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ܲ�ѯ
	 * ename: queryResultList
	 * ���÷���: 
	 * viewers: ��ѯ���ҳ��
	 * messages: 
	 */
    public String queryResultList(Object o){
        
        return "��ѯ���ҳ��";
    }
    
	/**
	 * Direction: ���ص���ѯ�������
	 * ename: rebackResultView
	 * ���÷���: 
	 * viewers: ��ѯ���ҳ��
	 * messages: 
	 */
    public String rebackResultView(Object o){
        
        return "��ѯ���ҳ��";
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
        
    public com.cfcc.itfe.persistence.dto.TsPaybankDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TsPaybankDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TsPaybankDto getSearchdto() {
        return searchdto;
    }

    public void setSearchdto(com.cfcc.itfe.persistence.dto.TsPaybankDto _searchdto) {
        searchdto = _searchdto;
    }
}
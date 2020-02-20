package com.cfcc.itfe.client.para.conpaychecksearchui;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IFileResolveCommonService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.service.para.paramtransform.IParamTransformService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * ��ϵͳ: Para
 * ģ��:ConPayCheckSearchUI
 * ���:ConPayCheckBillSearchUI
 */
public abstract class AbstractConPayCheckBillSearchUIBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService)getService(IFileResolveCommonService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService)getService(IItfeCacheService.class);
	protected IParamTransformService paramTransformService = (IParamTransformService)getService(IParamTransformService.class);
		
	/** �����б� */
    List filePath = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_conpaychecksearchui_conpaycheckbillsearchui_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractConPayCheckBillSearchUIBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_conpaychecksearchui_conpaycheckbillsearchui.properties");
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
			log.warn("Ϊitfe_para_conpaychecksearchui_conpaycheckbillsearchui��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��ѯ
	 * ename: queryBudget
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String queryBudget(Object o){
        
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
	 * Direction: ��������
	 * ename: dataimport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String dataimport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ת��������
	 * ename: goImport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String goImport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��������
	 * ename: dataexport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String dataexport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��������
	 * ename: savedate
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String savedate(Object o){
        
        return "��ѯ���";
    }
    
	/**
	 * Direction: ����
	 * ename: returnqueryresult
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String returnqueryresult(Object o){
        
        return "��ѯ���";
    }
    
	/**
	 * Direction: ��ת��ѯ���
	 * ename: goqueryresult
	 * ���÷���: 
	 * viewers: ��Ϣ�޸�
	 * messages: 
	 */
    public String goqueryresult(Object o){
        
        return "��Ϣ�޸�";
    }
    
	/**
	 * Direction: �����ѯ
	 * ename: queryQs
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryQs(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ȶ��˵�ɾ��
	 * ename: eddelete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String eddelete(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ������˵�ɾ��
	 * ename: qsdelete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String qsdelete(Object o){
        
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
        
    public java.util.List getFilePath() {
        return filePath;
    }

    public void setFilePath(java.util.List _filePath) {
        filePath = _filePath;
    }
}
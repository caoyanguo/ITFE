package com.cfcc.itfe.client.para.tsfinmovepaysub;

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
import com.cfcc.itfe.service.para.tsfinmovepaysub.ITsFinmovepaysubService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * ��ϵͳ: Para
 * ģ��:tsfinmovepaysub
 * ���:TsFinmovepaysub
 */
public abstract class AbstractTsFinmovepaysubBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITsFinmovepaysubService tsFinmovepaysubService = (ITsFinmovepaysubService)getService(ITsFinmovepaysubService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    TsFinmovepaysubDto dto = null;
    PagingContext pagingContext = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsfinmovepaysub_tsfinmovepaysub_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTsFinmovepaysubBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsfinmovepaysub_tsfinmovepaysub.properties");
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
			log.warn("Ϊitfe_para_tsfinmovepaysub_tsfinmovepaysub��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ת����������֧��¼��
	 * ename: toFinmovepaysave
	 * ���÷���: 
	 * viewers: ��������֧��¼��
	 * messages: 
	 */
    public String toFinmovepaysave(Object o){
        
        return "��������֧��¼��";
    }
    
	/**
	 * Direction: ��������֧��ɾ��
	 * ename: finmovepayDelete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String finmovepayDelete(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ת����������֧���޸�
	 * ename: toFinmovepaymodify
	 * ���÷���: 
	 * viewers: ��������֧���޸�
	 * messages: 
	 */
    public String toFinmovepaymodify(Object o){
        
        return "��������֧���޸�";
    }
    
	/**
	 * Direction: ��������֧��¼��
	 * ename: finmovepaySave
	 * ���÷���: 
	 * viewers: ��������ָ���б�
	 * messages: 
	 */
    public String finmovepaySave(Object o){
        
        return "��������ָ���б�";
    }
    
	/**
	 * Direction: ת����������ָ���б�
	 * ename: toFinmovepaylist
	 * ���÷���: 
	 * viewers: ��������ָ���б�
	 * messages: 
	 */
    public String toFinmovepaylist(Object o){
        
        return "��������ָ���б�";
    }
    
	/**
	 * Direction: ��������֧���޸�
	 * ename: finmovepayModify
	 * ���÷���: 
	 * viewers: ��������ָ���б�
	 * messages: 
	 */
    public String finmovepayModify(Object o){
        
        return "��������ָ���б�";
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
        
    public com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingContext() {
        return pagingContext;
    }

    public void setPagingContext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingContext) {
        pagingContext = _pagingContext;
    }
}
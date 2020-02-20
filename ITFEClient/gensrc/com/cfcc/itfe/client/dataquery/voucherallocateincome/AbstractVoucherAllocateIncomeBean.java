package com.cfcc.itfe.client.dataquery.voucherallocateincome;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IFileResolveCommonService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.voucherallocateincome.IVoucherAllocateIncomeService;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:VoucherAllocateIncome
 * ���:VoucherAllocateIncome
 */
public abstract class AbstractVoucherAllocateIncomeBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService)getService(IFileResolveCommonService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IVoucherAllocateIncomeService voucherAllocateIncomeService = (IVoucherAllocateIncomeService)getService(IVoucherAllocateIncomeService.class);
		
	/** �����б� */
    TvVoucherinfoAllocateIncomeDto dto = null;
    List filePath = null;
    PagingContext pagingcontext = null;
    TvVoucherinfoAllocateIncomeDto saveDto = null;
    TvVoucherinfoAllocateIncomeDto deleteDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_voucherallocateincome_voucherallocateincome_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractVoucherAllocateIncomeBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_voucherallocateincome_voucherallocateincome.properties");
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
			log.warn("Ϊitfe_dataquery_voucherallocateincome_voucherallocateincome��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��������
	 * ename: upLoad
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String upLoad(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: query
	 * ���÷���: 
	 * viewers: TCBS�ʽ��ĵ����ѯ���
	 * messages: 
	 */
    public String query(Object o){
        
        return "TCBS�ʽ��ĵ����ѯ���";
    }
    
	/**
	 * Direction: ���ز�ѯҳ��
	 * ename: goQuery
	 * ���÷���: 
	 * viewers: TCBS�ʽ��ĵ����ѯ
	 * messages: 
	 */
    public String goQuery(Object o){
        
        return "TCBS�ʽ��ĵ����ѯ";
    }
    
	/**
	 * Direction: ��������
	 * ename: savaDto
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String savaDto(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �޸�
	 * ename: modifyDto
	 * ���÷���: 
	 * viewers: ��������Ʊ���޸�
	 * messages: 
	 */
    public String modifyDto(Object o){
        
        return "��������Ʊ���޸�";
    }
    
	/**
	 * Direction: �޸ı���
	 * ename: saveModifyDto
	 * ���÷���: 
	 * viewers: TCBS�ʽ��ĵ����ѯ���
	 * messages: 
	 */
    public String saveModifyDto(Object o){
        
        return "TCBS�ʽ��ĵ����ѯ���";
    }
    
	/**
	 * Direction: ɾ��
	 * ename: delDto
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delDto(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��������
	 * ename: singleClick
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleClick(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto _dto) {
        dto = _dto;
    }
    
    public java.util.List getFilePath() {
        return filePath;
    }

    public void setFilePath(java.util.List _filePath) {
        filePath = _filePath;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto getSaveDto() {
        return saveDto;
    }

    public void setSaveDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto _saveDto) {
        saveDto = _saveDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto getDeleteDto() {
        return deleteDto;
    }

    public void setDeleteDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto _deleteDto) {
        deleteDto = _deleteDto;
    }
}
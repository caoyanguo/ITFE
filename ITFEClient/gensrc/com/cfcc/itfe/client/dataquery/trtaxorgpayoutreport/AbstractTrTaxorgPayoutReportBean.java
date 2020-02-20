package com.cfcc.itfe.client.dataquery.trtaxorgpayoutreport;

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
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:trTaxorgPayoutReport
 * ���:TrTaxorgPayoutReport
 */
public abstract class AbstractTrTaxorgPayoutReportBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService)getService(IFileResolveCommonService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService)getService(IItfeCacheService.class);
		
	/** �����б� */
    TrTaxorgPayoutReportDto dto = null;
    PagingContext pagingcontext = null;
    List filePath = null;
    TrTaxorgPayoutReportDto inputDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_trtaxorgpayoutreport_trtaxorgpayoutreport_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTrTaxorgPayoutReportBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_trtaxorgpayoutreport_trtaxorgpayoutreport.properties");
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
			log.warn("Ϊitfe_dataquery_trtaxorgpayoutreport_trtaxorgpayoutreport��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��ѯ
	 * ename: query
	 * ���÷���: 
	 * viewers: Ԥ��֧���ձ����ѯ���
	 * messages: 
	 */
    public String query(Object o){
        
        return "Ԥ��֧���ձ����ѯ���";
    }
    
	/**
	 * Direction: ���ز�ѯҳ��
	 * ename: goQuery
	 * ���÷���: 
	 * viewers: Ԥ��֧���ձ����ѯ
	 * messages: 
	 */
    public String goQuery(Object o){
        
        return "Ԥ��֧���ձ����ѯ";
    }
    
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
	 * Direction: ���뵼������ҳ��
	 * ename: goUpload
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String goUpload(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ؼ���֧����ѯҳ��
	 * ename: goConpayQuery
	 * ���÷���: 
	 * viewers: ����֧���ձ����ѯ
	 * messages: 
	 */
    public String goConpayQuery(Object o){
        
        return "����֧���ձ����ѯ";
    }
    
	/**
	 * Direction: ����֧����ѯ
	 * ename: conPayoutQuery
	 * ���÷���: 
	 * viewers: ����֧���ձ����ѯ���
	 * messages: 
	 */
    public String conPayoutQuery(Object o){
        
        return "����֧���ձ����ѯ���";
    }
    
	/**
	 * Direction: �ļ�����
	 * ename: exptofile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exptofile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����֧������
	 * ename: conexpfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String conexpfile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �����±�
	 * ename: exptomonthfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exptomonthfile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����֧�������±�
	 * ename: contexpmonthfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String contexpmonthfile(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public java.util.List getFilePath() {
        return filePath;
    }

    public void setFilePath(java.util.List _filePath) {
        filePath = _filePath;
    }
    
    public com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto getInputDto() {
        return inputDto;
    }

    public void setInputDto(com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto _inputDto) {
        inputDto = _inputDto;
    }
}
package com.cfcc.itfe.client.recbiz.updateinto;

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
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * ��ϵͳ: RecBiz
 * ģ��:updateInto
 * ���:UpdateInto
 */
public abstract class AbstractUpdateIntoBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService)getService(IFileResolveCommonService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IUploadConfirmService uploadConfirmService = (IUploadConfirmService)getService(IUploadConfirmService.class);
		
	/** �����б� */
    List filepath = null;
    List selectedfilelist = null;
    List showfilelist = null;
    List selecteddatalist = null;
    List showdatalist = null;
    FileResultDto filedata = null;
    TbsTvInCorrhandbookDto searchdto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_updateinto_updateinto_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractUpdateIntoBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_updateinto_updateinto.properties");
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
			log.warn("Ϊitfe_recbiz_updateinto_updateinto��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ���ݼ���
	 * ename: dateload
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String dateload(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ת�������Ų�ѯ
	 * ename: topiliangxh
	 * ���÷���: 
	 * viewers: ��������
	 * messages: 
	 */
    public String topiliangxh(Object o){
        
        return "��������";
    }
    
	/**
	 * Direction: ��ת������Ų�ѯ
	 * ename: tozhubixh
	 * ���÷���: 
	 * viewers: �������
	 * messages: 
	 */
    public String tozhubixh(Object o){
        
        return "�������";
    }
    
	/**
	 * Direction: ֱ���ύ
	 * ename: submit
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String submit(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����Ĭ�Ͻ���
	 * ename: goback
	 * ���÷���: 
	 * viewers: ����ƾ֤���ݵ���
	 * messages: 
	 */
    public String goback(Object o){
        
        return "����ƾ֤���ݵ���";
    }
    
	/**
	 * Direction: ����ȷ��
	 * ename: plsubmit
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String plsubmit(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ɾ��
	 * ename: pldel
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String pldel(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ύ
	 * ename: zbsubmit
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String zbsubmit(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �������Ų�ѯ
	 * ename: plsearch
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String plsearch(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ������Ų�ѯ
	 * ename: zbsearch
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String zbsearch(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ȫѡ
	 * ename: selectall
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectall(Object o){
        
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
        
    public java.util.List getFilepath() {
        return filepath;
    }

    public void setFilepath(java.util.List _filepath) {
        filepath = _filepath;
    }
    
    public java.util.List getSelectedfilelist() {
        return selectedfilelist;
    }

    public void setSelectedfilelist(java.util.List _selectedfilelist) {
        selectedfilelist = _selectedfilelist;
    }
    
    public java.util.List getShowfilelist() {
        return showfilelist;
    }

    public void setShowfilelist(java.util.List _showfilelist) {
        showfilelist = _showfilelist;
    }
    
    public java.util.List getSelecteddatalist() {
        return selecteddatalist;
    }

    public void setSelecteddatalist(java.util.List _selecteddatalist) {
        selecteddatalist = _selecteddatalist;
    }
    
    public java.util.List getShowdatalist() {
        return showdatalist;
    }

    public void setShowdatalist(java.util.List _showdatalist) {
        showdatalist = _showdatalist;
    }
    
    public com.cfcc.itfe.persistence.dto.FileResultDto getFiledata() {
        return filedata;
    }

    public void setFiledata(com.cfcc.itfe.persistence.dto.FileResultDto _filedata) {
        filedata = _filedata;
    }
    
    public com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto getSearchdto() {
        return searchdto;
    }

    public void setSearchdto(com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto _searchdto) {
        searchdto = _searchdto;
    }
}
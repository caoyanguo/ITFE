package com.cfcc.itfe.client.recbiz.uploadmoduleforzj;

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
import com.cfcc.itfe.service.recbiz.uploadmoduleforzj.IUploadUIForZjService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ISequenceHelperService;
import com.cfcc.itfe.persistence.dto.FileResultDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * ��ϵͳ: RecBiz
 * ģ��:UploadModuleForZj
 * ���:UploadUIForZj
 */
public abstract class AbstractUploadUIForZjBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IUploadUIForZjService uploadUIForZjService = (IUploadUIForZjService)getService(IUploadUIForZjService.class);
	protected ISequenceHelperService sequenceHelperService = (ISequenceHelperService)getService(ISequenceHelperService.class);
		
	/** �����б� */
    List fileList = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_uploadmoduleforzj_uploaduiforzj_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractUploadUIForZjBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_uploadmoduleforzj_uploaduiforzj.properties");
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
			log.warn("Ϊitfe_recbiz_uploadmoduleforzj_uploaduiforzj��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ���ݼ����ύ
	 * ename: upload
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String upload(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �رմ���
	 * ename: close
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String close(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ת����ȷ���ύ
	 * ename: gotocommitincome
	 * ���÷���: 
	 * viewers: ����ȷ���ύ
	 * messages: 
	 */
    public String gotocommitincome(Object o){
        
        return "����ȷ���ύ";
    }
    
	/**
	 * Direction: ����ȷ���ύ
	 * ename: commitincome
	 * ���÷���: 
	 * viewers: ���ݼ��ؽ���
	 * messages: 
	 */
    public String commitincome(Object o){
        
        return "���ݼ��ؽ���";
    }
    
	/**
	 * Direction: ɾ����������
	 * ename: delErrorData
	 * ���÷���: 
	 * viewers: ���ݼ��ؽ���
	 * messages: 
	 */
    public String delErrorData(Object o){
        
        return "���ݼ��ؽ���";
    }
    
	/**
	 * Direction: ��ѯ�ļ��б�
	 * ename: searchFileListBySrlno
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String searchFileListBySrlno(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ص����ݼ��ش���
	 * ename: goback
	 * ���÷���: 
	 * viewers: ���ݼ��ؽ���
	 * messages: 
	 */
    public String goback(Object o){
        
        return "���ݼ��ؽ���";
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
        
    public java.util.List getFileList() {
        return fileList;
    }

    public void setFileList(java.util.List _fileList) {
        fileList = _fileList;
    }
}
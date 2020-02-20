package com.cfcc.itfe.client.recbiz.certrec;

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
import com.cfcc.itfe.service.recbiz.certrec.IDownloadAllFileService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TvFilesDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * ��ϵͳ: RecBiz
 * ģ��:CertRec
 * ���:ReceiveUI
 */
public abstract class AbstractReceiveUIBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IDownloadAllFileService downloadAllFileService = (IDownloadAllFileService)getService(IDownloadAllFileService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_certrec_receiveui_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractReceiveUIBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_certrec_receiveui.properties");
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
			log.warn("Ϊitfe_recbiz_certrec_receiveui��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ƾ֤��ϸ��ѯ
	 * ename: queryCerDetail
	 * ���÷���: 
	 * viewers: ƾ֤��ϸ
	 * messages: 
	 */
    public String queryCerDetail(Object o){
        
        return "ƾ֤��ϸ";
    }
    
	/**
	 * Direction: ����ƾ֤����
	 * ename: backReceiveCer
	 * ���÷���: 
	 * viewers: ��ƾ֤����
	 * messages: 
	 */
    public String backReceiveCer(Object o){
        
        return "��ƾ֤����";
    }
    
	/**
	 * Direction: ������������
	 * ename: downloadSelectedFiles
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String downloadSelectedFiles(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ������������
	 * ename: downloadOneFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String downloadOneFile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �б�����ѡ��
	 * ename: selectOneRow
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectOneRow(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����·��ѡ��
	 * ename: pathSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String pathSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����·��ѡ��
	 * ename: backupPathSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String backupPathSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ������־��ѯ
	 * ename: queryRecvLog
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryRecvLog(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��������ͳ��
	 * ename: queryRecvLogReport
	 * ���÷���: 
	 * viewers: ��������ͳ��
	 * messages: 
	 */
    public String queryRecvLogReport(Object o){
        
        return "��������ͳ��";
    }
    
	/**
	 * Direction: �����յ�����Ϣ
	 * ename: recvDelete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String recvDelete(Object o){
        
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
    }
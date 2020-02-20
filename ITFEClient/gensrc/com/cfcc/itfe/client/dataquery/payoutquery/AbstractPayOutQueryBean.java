package com.cfcc.itfe.client.dataquery.payoutquery;

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
import com.cfcc.itfe.service.dataquery.payoutquery.IPayoutService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:payOutQuery
 * ���:PayOutQuery
 */
public abstract class AbstractPayOutQueryBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IPayoutService payoutService = (IPayoutService)getService(IPayoutService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    TvPayoutmsgsubDto modifyDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_payoutquery_payoutquery_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractPayOutQueryBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_payoutquery_payoutquery.properties");
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
			log.warn("Ϊitfe_dataquery_payoutquery_payoutquery��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��ѯ�б��¼�
	 * ename: searchList
	 * ���÷���: 
	 * viewers: ʵ���ʽ���Ϣ�б�
	 * messages: 
	 */
    public String searchList(Object o){
        
        return "ʵ���ʽ���Ϣ�б�";
    }
    
	/**
	 * Direction: ���ز�ѯ����
	 * ename: rebackSearch
	 * ���÷���: 
	 * viewers: ʵ���ʽ��ѯ����
	 * messages: 
	 */
    public String rebackSearch(Object o){
        
        return "ʵ���ʽ��ѯ����";
    }
    
	/**
	 * Direction: ����Ϣ�����¼�
	 * ename: singleclickMain
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleclickMain(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����Ϣ˫���¼�
	 * ename: doubleclickMain
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleclickMain(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �޸ı���
	 * ename: modifysave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String modifysave(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �����б��¼�
	 * ename: rebackSearchList
	 * ���÷���: 
	 * viewers: ʵ���ʽ���Ϣ�б�
	 * messages: 
	 */
    public String rebackSearchList(Object o){
        
        return "ʵ���ʽ���Ϣ�б�";
    }
    
	/**
	 * Direction: ת������ҳ���¼�
	 * ename: gosendmsg
	 * ���÷���: 
	 * viewers: ʵ���ʽ�����ͽ���
	 * messages: 
	 */
    public String gosendmsg(Object o){
        
        return "ʵ���ʽ�����ͽ���";
    }
    
	/**
	 * Direction: ���ķ����¼�
	 * ename: sendmsg
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String sendmsg(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ͱ��ĵ����¼�
	 * ename: singleclicksendmsg
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleclicksendmsg(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ʵ���ʽ𸴺�
	 * ename: checkPayout
	 * ���÷���: 
	 * viewers: ʵ���ʽ���Ϣ�б�
	 * messages: 
	 */
    public String checkPayout(Object o){
        
        return "ʵ���ʽ���Ϣ�б�";
    }
    
	/**
	 * Direction: ȥʵ���ʽ𸴺�
	 * ename: goCheckPayout
	 * ���÷���: 
	 * viewers: ʵ���ʽ𸴺˽���
	 * messages: 
	 */
    public String goCheckPayout(Object o){
        
        return "ʵ���ʽ𸴺˽���";
    }
    
	/**
	 * Direction: �˿���˵���ӡ
	 * ename: queryPrint
	 * ���÷���: 
	 * viewers: ʵ���ʽ���˵�
	 * messages: 
	 */
    public String queryPrint(Object o){
        
        return "ʵ���ʽ���˵�";
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
	 * Direction: ���³ɹ�
	 * ename: updateSuccess
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String updateSuccess(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ȫѡ/��ѡ
	 * ename: selectAllOrNone
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAllOrNone(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ʧ��
	 * ename: updateFail
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String updateFail(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ѡ�лص�
	 * ename: exportSelectData
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportSelectData(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ϸ��Ϣ˫���¼�
	 * ename: doubleclickSub
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleclickSub(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �����ļ�csv
	 * ename: exportfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportfile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ת���޸Ľ���
	 * ename: goModify
	 * ���÷���: 
	 * viewers: ʵ���ʽ��޸Ľ���
	 * messages: 
	 */
    public String goModify(Object o){
        
        return "ʵ���ʽ��޸Ľ���";
    }
    
	/**
	 * Direction: �޸ı���
	 * ename: modifyDetailSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String modifyDetailSave(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ص���ϸ�б����
	 * ename: backToList
	 * ���÷���: 
	 * viewers: ʵ���ʽ���Ϣ�б�
	 * messages: 
	 */
    public String backToList(Object o){
        
        return "ʵ���ʽ���Ϣ�б�";
    }
    
	/**
	 * Direction: ��ϸ�޸�
	 * ename: toDetail
	 * ���÷���: 
	 * viewers: ʵ���ʽ���ϸ�޸Ľ���
	 * messages: 
	 */
    public String toDetail(Object o){
        
        return "ʵ���ʽ���ϸ�޸Ľ���";
    }
    
	/**
	 * Direction: ��ѡ
	 * ename: detailSingleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String detailSingleSelect(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto getModifyDto() {
        return modifyDto;
    }

    public void setModifyDto(com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto _modifyDto) {
        modifyDto = _modifyDto;
    }
}
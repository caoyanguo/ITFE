package com.cfcc.itfe.client.recbiz.banknameenter;

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
import com.cfcc.itfe.service.recbiz.banknameenter.IBankNameEnterService;
import com.cfcc.itfe.service.recbiz.voucherload.IVoucherLoadService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.facade.data.MulitTableDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * ��ϵͳ: RecBiz
 * ģ��:bankNameEnter
 * ���:BankNameEnter
 */
public abstract class AbstractBankNameEnterBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IBankNameEnterService bankNameEnterService = (IBankNameEnterService)getService(IBankNameEnterService.class);
	protected IVoucherLoadService voucherLoadService = (IVoucherLoadService)getService(IVoucherLoadService.class);
		
	/** �����б� */
    List selectlist = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_banknameenter_banknameenter_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractBankNameEnterBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_banknameenter_banknameenter.properties");
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
			log.warn("Ϊitfe_recbiz_banknameenter_banknameenter��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ������Ϣ�б�
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ˫�������б�
	 * ename: doubleclickBank
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleclickBank(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ѯ������Ϣ
	 * ename: querybank
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String querybank(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �˳�
	 * ename: exit
	 * ���÷���: 
	 * viewers: �貹¼֧��ϵͳ�к���Ϣ��ѯ��ͼ
	 * messages: 
	 */
    public String exit(Object o){
        
        return "�貹¼֧��ϵͳ�к���Ϣ��ѯ��ͼ";
    }
    
	/**
	 * Direction: ��¼
	 * ename: addRecord
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String addRecord(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ͨ��
	 * ename: bufusuccess
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String bufusuccess(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: ҵ��Ҫ�ز�¼����
	 * messages: 
	 */
    public String search(Object o){
        
        return "ҵ��Ҫ�ز�¼����";
    }
    
	/**
	 * Direction: ���ͨ��
	 * ename: checkSuccess
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String checkSuccess(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ʧ��
	 * ename: checkfault
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String checkfault(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ʧ��
	 * ename: bufufault
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String bufufault(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ȫѡ
	 * ename: selectAll
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ˫����¼
	 * ename: doubleclickTObulu
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleclickTObulu(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ѯ��¼
	 * ename: queryRecords
	 * ���÷���: 
	 * viewers: ��ѯ�����ͼ
	 * messages: 
	 */
    public String queryRecords(Object o){
        
        return "��ѯ�����ͼ";
    }
    
	/**
	 * Direction: ������ӡ
	 * ename: printSelectedVoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String printSelectedVoucher(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ǩ��
	 * ename: singStamp
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singStamp(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ʧ��
	 * ename: reviewfault
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String reviewfault(Object o){
        
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
        
    public java.util.List getSelectlist() {
        return selectlist;
    }

    public void setSelectlist(java.util.List _selectlist) {
        selectlist = _selectlist;
    }
}
package com.cfcc.itfe.client.recbiz.bankvouchervalidate;

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
import com.cfcc.itfe.service.recbiz.bankvouchervalidate.IBankVoucherValidateService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.facade.data.MulitTableDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * ��ϵͳ: RecBiz
 * ģ��:bankVoucherValidate
 * ���:BankVoucherValidate
 */
public abstract class AbstractBankVoucherValidateBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IBankVoucherValidateService bankVoucherValidateService = (IBankVoucherValidateService)getService(IBankVoucherValidateService.class);
		
	/** �����б� */
    PagingContext pagingContext = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_bankvouchervalidate_bankvouchervalidate_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractBankVoucherValidateBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_bankvouchervalidate_bankvouchervalidate.properties");
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
			log.warn("Ϊitfe_recbiz_bankvouchervalidate_bankvouchervalidate��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: �����ļ�
	 * ename: voucherImport
	 * ���÷���: 
	 * viewers: ���ݵ������
	 * messages: 
	 */
    public String voucherImport(Object o){
        
        return "���ݵ������";
    }
    
	/**
	 * Direction: ��ʼ�ȶ�
	 * ename: compare
	 * ���÷���: 
	 * viewers: ���ݱȶԽ���
	 * messages: 
	 */
    public String compare(Object o){
        
        return "���ݱȶԽ���";
    }
    
	/**
	 * Direction: ��ѯ����
	 * ename: queryResult
	 * ���÷���: 
	 * viewers: ���ݱȶԽ���
	 * messages: 
	 */
    public String queryResult(Object o){
        
        return "���ݱȶԽ���";
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
        
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingContext() {
        return pagingContext;
    }

    public void setPagingContext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingContext) {
        pagingContext = _pagingContext;
    }
}
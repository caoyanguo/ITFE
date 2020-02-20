package com.cfcc.itfe.client.sendbiz.recvbusinessreconciliation;

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
import com.cfcc.itfe.service.recbiz.voucherload.IVoucherLoadService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRefundMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaMainDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * ��ϵͳ: SendBiz
 * ģ��:recvBusinessReconciliation
 * ���:RecvBusinessReconciliation
 */
public abstract class AbstractRecvBusinessReconciliationBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IVoucherLoadService voucherLoadService = (IVoucherLoadService)getService(IVoucherLoadService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    TvVoucherinfoDto dto = null;
    PagingContext pagingcontext = null;
    TfReconcilePayinfoMainDto payinfoDto = null;
    TfReconcileRealdialMainDto realdialMainDto = null;
    TfReconcileRefundMainDto refundMainDto = null;
    TfReconcilePayquotaMainDto payquotaMainDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_sendbiz_recvbusinessreconciliation_recvbusinessreconciliation_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractRecvBusinessReconciliationBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_sendbiz_recvbusinessreconciliation_recvbusinessreconciliation.properties");
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
			log.warn("Ϊitfe_sendbiz_recvbusinessreconciliation_recvbusinessreconciliation��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
        
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
	 * Direction: ����ƾ֤
	 * ename: voucherGenerator
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherGenerator(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ǩ��
	 * ename: voucherStamp
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherStamp(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ǩ�³���
	 * ename: voucherStampCancle
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherStampCancle(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ƾ֤�鿴
	 * ename: voucherView
	 * ���÷���: 
	 * viewers: ƾ֤�鿴����
	 * messages: 
	 */
    public String voucherView(Object o){
        
        return "ƾ֤�鿴����";
    }
    
	/**
	 * Direction: ����ƾ֤����ƾ֤��
	 * ename: voucherSend
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherSend(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �ص�״̬��ѯ
	 * ename: queryStatusReturnVoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryStatusReturnVoucher(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �ص���ԭչʾ
	 * ename: returnVoucherView
	 * ���÷���: 
	 * viewers: �ص�ƾ֤�鿴����
	 * messages: 
	 */
    public String returnVoucherView(Object o){
        
        return "�ص�ƾ֤�鿴����";
    }
    
	/**
	 * Direction: ˫���鿴ҵ����ϸ
	 * ename: doubleClick
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����
	 * ename: backToSearch
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String backToSearch(Object o){
        
        return "ά������";
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
        
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto getPayinfoDto() {
        return payinfoDto;
    }

    public void setPayinfoDto(com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto _payinfoDto) {
        payinfoDto = _payinfoDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TfReconcileRealdialMainDto getRealdialMainDto() {
        return realdialMainDto;
    }

    public void setRealdialMainDto(com.cfcc.itfe.persistence.dto.TfReconcileRealdialMainDto _realdialMainDto) {
        realdialMainDto = _realdialMainDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TfReconcileRefundMainDto getRefundMainDto() {
        return refundMainDto;
    }

    public void setRefundMainDto(com.cfcc.itfe.persistence.dto.TfReconcileRefundMainDto _refundMainDto) {
        refundMainDto = _refundMainDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TfReconcilePayquotaMainDto getPayquotaMainDto() {
        return payquotaMainDto;
    }

    public void setPayquotaMainDto(com.cfcc.itfe.persistence.dto.TfReconcilePayquotaMainDto _payquotaMainDto) {
        payquotaMainDto = _payquotaMainDto;
    }
}
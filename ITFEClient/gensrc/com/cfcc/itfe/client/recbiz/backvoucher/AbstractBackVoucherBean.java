package com.cfcc.itfe.client.recbiz.backvoucher;

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
import com.cfcc.itfe.service.recbiz.voucherload.IVoucherLoadService;
import com.cfcc.itfe.service.recbiz.backvoucher.IBackVoucherService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * ��ϵͳ: RecBiz
 * ģ��:backVoucher
 * ���:BackVoucher
 */
public abstract class AbstractBackVoucherBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IVoucherLoadService voucherLoadService = (IVoucherLoadService)getService(IVoucherLoadService.class);
	protected IBackVoucherService backVoucherService = (IBackVoucherService)getService(IBackVoucherService.class);
		
	/** �����б� */
    TvVoucherinfoDto dto = null;
    TvVoucherinfoDto leftdto = null;
    PagingContext pagingcontext = null;
    TvPayoutmsgmainDto tvPayoutmsgmainDto = null;
    TvPayoutmsgsubDto tvPayoutmsgsubDto = null;
    TvDwbkDto tvDwbkDto = null;
    TvVoucherinfoDto indexDto = null;
    TfDirectpaymsgmainDto tfDirectpaymsgmainDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_recbiz_backvoucher_backvoucher_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractBackVoucherBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_recbiz_backvoucher_backvoucher.properties");
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
			log.warn("Ϊitfe_recbiz_backvoucher_backvoucher��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ƾ֤���ɲ�ѯ
	 * ename: voucherGSearch
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherGSearch(Object o){
        
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
	 * Direction: ��ѯ
	 * ename: voucherSearch
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherSearch(Object o){
        
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
	 * viewers: ƾ֤�˻�ҵ��鿴����
	 * messages: 
	 */
    public String voucherView(Object o){
        
        return "ƾ֤�˻�ҵ��鿴����";
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
	 * Direction: ��ѯ�˻�ƾ֤��Ϣ��TCBS�·���
	 * ename: querybackvoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String querybackvoucher(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ƾ֤��TCBS�·���
	 * ename: voucherGeneratorForTCBS
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherGeneratorForTCBS(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ȫѡ
	 * ename: selectAllBack
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAllBack(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ȫѡ
	 * ename: selectAllPayoutBack
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAllPayoutBack(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ƾ֤
	 * ename: voucherGeneratorPayoutBack
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherGeneratorPayoutBack(Object o){
        
        return "";
    }
    
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
	 * Direction: �ص�״̬��ѯ
	 * ename: queryStatusReturnVoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryStatusReturnVoucher(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ɾ��ƾ֤
	 * ename: delgenvoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delgenvoucher(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoDto getLeftdto() {
        return leftdto;
    }

    public void setLeftdto(com.cfcc.itfe.persistence.dto.TvVoucherinfoDto _leftdto) {
        leftdto = _leftdto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto getTvPayoutmsgmainDto() {
        return tvPayoutmsgmainDto;
    }

    public void setTvPayoutmsgmainDto(com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto _tvPayoutmsgmainDto) {
        tvPayoutmsgmainDto = _tvPayoutmsgmainDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto getTvPayoutmsgsubDto() {
        return tvPayoutmsgsubDto;
    }

    public void setTvPayoutmsgsubDto(com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto _tvPayoutmsgsubDto) {
        tvPayoutmsgsubDto = _tvPayoutmsgsubDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvDwbkDto getTvDwbkDto() {
        return tvDwbkDto;
    }

    public void setTvDwbkDto(com.cfcc.itfe.persistence.dto.TvDwbkDto _tvDwbkDto) {
        tvDwbkDto = _tvDwbkDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoDto getIndexDto() {
        return indexDto;
    }

    public void setIndexDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoDto _indexDto) {
        indexDto = _indexDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto getTfDirectpaymsgmainDto() {
        return tfDirectpaymsgmainDto;
    }

    public void setTfDirectpaymsgmainDto(com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto _tfDirectpaymsgmainDto) {
        tfDirectpaymsgmainDto = _tfDirectpaymsgmainDto;
    }
}
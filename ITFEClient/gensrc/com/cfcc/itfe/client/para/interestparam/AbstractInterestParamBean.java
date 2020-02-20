package com.cfcc.itfe.client.para.interestparam;

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
import com.cfcc.itfe.service.para.interestparam.IInterestParamService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TfInterestParamDto;
import com.cfcc.itfe.persistence.dto.TsJxAcctinfoDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * ��ϵͳ: Para
 * ģ��:interestParam
 * ���:InterestParam
 */
public abstract class AbstractInterestParamBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IInterestParamService interestParamService = (IInterestParamService)getService(IInterestParamService.class);
		
	/** �����б� */
    TfInterestParamDto dto = null;
    PagingContext pagingcontext = null;
    List bankTypeList = null;
    String msg = null;
    TsJxAcctinfoDto JXDto = null;
    PagingContext pagingcontextJX = null;
    List bankList = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_interestparam_interestparam_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractInterestParamBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_interestparam_interestparam.properties");
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
			log.warn("Ϊitfe_para_interestparam_interestparam��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ��ת¼�����
	 * ename: goInput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String goInput(Object o){
        
        return "¼�����";
    }
    
	/**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String backMaintenance(Object o){
        
        return "ά������";
    }
    
	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���޸Ľ���
	 * ename: goModify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String goModify(Object o){
        
        return "�޸Ľ���";
    }
    
	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String modifySave(Object o){
        
        return "ά������";
    }
    
	/**
	 * Direction: ��ת��Ϣ�˻�¼�����
	 * ename: goJXInput
	 * ���÷���: 
	 * viewers: ��Ϣ�˻�¼�����
	 * messages: 
	 */
    public String goJXInput(Object o){
        
        return "��Ϣ�˻�¼�����";
    }
    
	/**
	 * Direction: ��Ϣ¼�뱣��
	 * ename: jxInputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String jxInputSave(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ؼ�Ϣ�˻�ά������
	 * ename: backJXMaintenance
	 * ���÷���: 
	 * viewers: ��Ϣ�˻�ά������
	 * messages: 
	 */
    public String backJXMaintenance(Object o){
        
        return "��Ϣ�˻�ά������";
    }
    
	/**
	 * Direction: ��Ϣ��ѡ
	 * ename: jxSingleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String jxSingleSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��Ϣ�˻�ɾ��
	 * ename: jxDelete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String jxDelete(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����Ϣ�˻��޸Ľ���
	 * ename: goJXModify
	 * ���÷���: 
	 * viewers: ��Ϣ�˻��޸Ľ���
	 * messages: 
	 */
    public String goJXModify(Object o){
        
        return "��Ϣ�˻��޸Ľ���";
    }
    
	/**
	 * Direction: ��Ϣ�˻��޸ı���
	 * ename: jxModifySave
	 * ���÷���: 
	 * viewers: ��Ϣ�˻�ά������
	 * messages: 
	 */
    public String jxModifySave(Object o){
        
        return "��Ϣ�˻�ά������";
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
        
    public com.cfcc.itfe.persistence.dto.TfInterestParamDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TfInterestParamDto _dto) {
        dto = _dto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontext() {
        return pagingcontext;
    }

    public void setPagingcontext(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontext) {
        pagingcontext = _pagingcontext;
    }
    
    public java.util.List getBankTypeList() {
        return bankTypeList;
    }

    public void setBankTypeList(java.util.List _bankTypeList) {
        bankTypeList = _bankTypeList;
    }
    
    public java.lang.String getMsg() {
        return msg;
    }

    public void setMsg(java.lang.String _msg) {
        msg = _msg;
    }
    
    public com.cfcc.itfe.persistence.dto.TsJxAcctinfoDto getJXDto() {
        return JXDto;
    }

    public void setJXDto(com.cfcc.itfe.persistence.dto.TsJxAcctinfoDto _jXDto) {
        JXDto = _jXDto;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getPagingcontextJX() {
        return pagingcontextJX;
    }

    public void setPagingcontextJX(com.cfcc.jaf.rcp.control.table.PagingContext _pagingcontextJX) {
        pagingcontextJX = _pagingcontextJX;
    }
    
    public java.util.List getBankList() {
        return bankList;
    }

    public void setBankList(java.util.List _bankList) {
        bankList = _bankList;
    }
}
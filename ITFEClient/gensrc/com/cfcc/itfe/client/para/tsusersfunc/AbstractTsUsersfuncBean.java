package com.cfcc.itfe.client.para.tsusersfunc;

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
import com.cfcc.itfe.service.para.tsusersfunc.ITsUsersfuncService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsSysfuncDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersfuncDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:38
 * @generated
 * ��ϵͳ: Para
 * ģ��:TsUsersfunc
 * ���:TsUsersfunc
 */
public abstract class AbstractTsUsersfuncBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ITsUsersfuncService tsUsersfuncService = (ITsUsersfuncService)getService(ITsUsersfuncService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    TsUsersfuncDto dto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_para_tsusersfunc_tsusersfunc_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractTsUsersfuncBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_para_tsusersfunc_tsusersfunc.properties");
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
			log.warn("Ϊitfe_para_tsusersfunc_tsusersfunc��ȡmessages����", e);
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
	 * viewers: �û�Ȩ��ά��
	 * messages: 
	 */
    public String goInput(Object o){
        
        return "�û�Ȩ��ά��";
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
	 * viewers: * messages: 
	 */
    public String backMaintenance(Object o){
        
        return "";
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
	 * viewers: * messages: 
	 */
    public String goModify(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
        
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
	 * Direction: ��������
	 * ename: repeatPwd
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String repeatPwd(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ѡ
	 * ename: oprSingleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String oprSingleSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ȫѡ1
	 * ename: selectFundAll
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectFundAll(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����1
	 * ename: inputSaveOpr
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSaveOpr(Object o){
        
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
        
    public com.cfcc.itfe.persistence.dto.TsUsersfuncDto getDto() {
        return dto;
    }

    public void setDto(com.cfcc.itfe.persistence.dto.TsUsersfuncDto _dto) {
        dto = _dto;
    }
}
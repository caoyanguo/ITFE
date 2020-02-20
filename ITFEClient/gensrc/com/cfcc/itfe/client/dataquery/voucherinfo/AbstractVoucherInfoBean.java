package com.cfcc.itfe.client.dataquery.voucherinfo;

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
import com.cfcc.itfe.service.dataquery.voucherinfo.IVoucherInfoService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:VoucherInfo
 * ���:VoucherInfo
 */
public abstract class AbstractVoucherInfoBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	protected IVoucherInfoService voucherInfoService = (IVoucherInfoService)getService(IVoucherInfoService.class);
		
	/** �����б� */
    TvVoucherinfoDto searchDto = null;
    TvVoucherinfoDto detailDto = null;
    TvVoucherinfoDto selectedDto = null;
    String dzType = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_voucherinfo_voucherinfo_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractVoucherInfoBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_voucherinfo_voucherinfo.properties");
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
			log.warn("Ϊitfe_dataquery_voucherinfo_voucherinfo��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ����
	 * ename: goback
	 * ���÷���: 
	 * viewers: ���˽����ѯ
	 * messages: 
	 */
    public String goback(Object o){
        
        return "���˽����ѯ";
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: queryInfo
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryInfo(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ˫����ת
	 * ename: doubleTodetail
	 * ���÷���: 
	 * viewers: ��Ϣ��ϸ
	 * messages: 
	 */
    public String doubleTodetail(Object o){
        
        return "��Ϣ��ϸ";
    }
    
	/**
	 * Direction: ����
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ת����ϸ����
	 * ename: goDetail
	 * ���÷���: 
	 * viewers: ��Ϣ��ϸ
	 * messages: 
	 */
    public String goDetail(Object o){
        
        return "��Ϣ��ϸ";
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
        
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoDto getSearchDto() {
        return searchDto;
    }

    public void setSearchDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoDto _searchDto) {
        searchDto = _searchDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoDto getDetailDto() {
        return detailDto;
    }

    public void setDetailDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoDto _detailDto) {
        detailDto = _detailDto;
    }
    
    public com.cfcc.itfe.persistence.dto.TvVoucherinfoDto getSelectedDto() {
        return selectedDto;
    }

    public void setSelectedDto(com.cfcc.itfe.persistence.dto.TvVoucherinfoDto _selectedDto) {
        selectedDto = _selectedDto;
    }
    
    public java.lang.String getDzType() {
        return dzType;
    }

    public void setDzType(java.lang.String _dzType) {
        dzType = _dzType;
    }
}
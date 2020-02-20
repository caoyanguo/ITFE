package com.cfcc.itfe.client.dataquery.interestratemsg;

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
import com.cfcc.itfe.service.dataquery.interestratemsg.IInterestrateMsgService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TfInterestrateMsgDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TfInterestDetailDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:InterestrateMsg
 * ���:InterestrateMsgQuery
 */
public abstract class AbstractInterestrateMsgQueryBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IInterestrateMsgService interestrateMsgService = (IInterestrateMsgService)getService(IInterestrateMsgService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** �����б� */
    List interestRateList = null;
    PagingContext interestVoucherList = null;
    TfInterestrateMsgDto finddto = null;
    TfInterestDetailDto interestDetailDto = null;
    String reportPath = null;
    List reportresult = null;
    Map reportmap = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_interestratemsg_interestratemsgquery_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractInterestrateMsgQueryBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_interestratemsg_interestratemsgquery.properties");
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
			log.warn("Ϊitfe_dataquery_interestratemsg_interestratemsgquery��ȡmessages����", e);
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
	 * viewers: ��Ϣͳ����Ϣ�б�
	 * messages: 
	 */
    public String searchList(Object o){
        
        return "��Ϣͳ����Ϣ�б�";
    }
    
	/**
	 * Direction: ���ز�ѯ����
	 * ename: rebackSearch
	 * ���÷���: 
	 * viewers: ��Ϣͳ����Ϣ��ѯ����
	 * messages: 
	 */
    public String rebackSearch(Object o){
        
        return "��Ϣͳ����Ϣ��ѯ����";
    }
    
	/**
	 * Direction: ��Ϣ
	 * ename: interestRate
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String interestRate(Object o){
        
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
	 * Direction: ��ת��Ϣ����չʾ
	 * ename: goshowreport
	 * ���÷���: 
	 * viewers: ��Ϣ����չʾ
	 * messages: 
	 */
    public String goshowreport(Object o){
        
        return "��Ϣ����չʾ";
    }
    
	/**
	 * Direction: ��Ϣ����չʾ����
	 * ename: goreportback
	 * ���÷���: 
	 * viewers: ��Ϣͳ����Ϣ�б�
	 * messages: 
	 */
    public String goreportback(Object o){
        
        return "��Ϣͳ����Ϣ�б�";
    }
    
	/**
	 * Direction: ���ݵ���
	 * ename: dataExport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String dataExport(Object o){
        
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
        
    public java.util.List getInterestRateList() {
        return interestRateList;
    }

    public void setInterestRateList(java.util.List _interestRateList) {
        interestRateList = _interestRateList;
    }
    
    public com.cfcc.jaf.rcp.control.table.PagingContext getInterestVoucherList() {
        return interestVoucherList;
    }

    public void setInterestVoucherList(com.cfcc.jaf.rcp.control.table.PagingContext _interestVoucherList) {
        interestVoucherList = _interestVoucherList;
    }
    
    public com.cfcc.itfe.persistence.dto.TfInterestrateMsgDto getFinddto() {
        return finddto;
    }

    public void setFinddto(com.cfcc.itfe.persistence.dto.TfInterestrateMsgDto _finddto) {
        finddto = _finddto;
    }
    
    public com.cfcc.itfe.persistence.dto.TfInterestDetailDto getInterestDetailDto() {
        return interestDetailDto;
    }

    public void setInterestDetailDto(com.cfcc.itfe.persistence.dto.TfInterestDetailDto _interestDetailDto) {
        interestDetailDto = _interestDetailDto;
    }
    
    public java.lang.String getReportPath() {
        return reportPath;
    }

    public void setReportPath(java.lang.String _reportPath) {
        reportPath = _reportPath;
    }
    
    public java.util.List getReportresult() {
        return reportresult;
    }

    public void setReportresult(java.util.List _reportresult) {
        reportresult = _reportresult;
    }
    
    public java.util.Map getReportmap() {
        return reportmap;
    }

    public void setReportmap(java.util.Map _reportmap) {
        reportmap = _reportmap;
    }
}
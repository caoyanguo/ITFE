package com.cfcc.itfe.client.dataquery.incomedataquery;

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
import com.cfcc.itfe.service.dataquery.incomedataquery.IIncomeBillService;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:incomeDataQuery
 * ���:IncomeBillQuery
 */
public abstract class AbstractIncomeBillQueryBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IIncomeBillService incomeBillService = (IIncomeBillService)getService(IIncomeBillService.class);
		
	/** �����б� */
    List selectedIncomeList = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_incomedataquery_incomebillquery_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractIncomeBillQueryBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_incomedataquery_incomebillquery.properties");
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
			log.warn("Ϊitfe_dataquery_incomedataquery_incomebillquery��ȡmessages����", e);
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
	 * viewers: ����˰Ʊ�б����
	 * messages: 
	 */
    public String searchList(Object o){
        
        return "����˰Ʊ�б����";
    }
    
	/**
	 * Direction: ����˰Ʊ�¼�
	 * ename: exportBillData
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportBillData(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���������¼�
	 * ename: reSendMsg
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String reSendMsg(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ز�ѯ����
	 * ename: rebackSearch
	 * ���÷���: 
	 * viewers: ����˰Ʊ��ѯ����
	 * messages: 
	 */
    public String rebackSearch(Object o){
        
        return "����˰Ʊ��ѯ����";
    }
    
	/**
	 * Direction: ����˰Ʊ��ϸ�����¼�
	 * ename: singleclickIncome
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleclickIncome(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �����б�
	 * ename: returnlist
	 * ���÷���: 
	 * viewers: ����˰Ʊ�б����
	 * messages: 
	 */
    public String returnlist(Object o){
        
        return "����˰Ʊ�б����";
    }
    
	/**
	 * Direction: ��������
	 * ename: savedata
	 * ���÷���: 
	 * viewers: ����˰Ʊ�б����
	 * messages: 
	 */
    public String savedata(Object o){
        
        return "����˰Ʊ�б����";
    }
    
	/**
	 * Direction: �����б�˫���¼�
	 * ename: doubleclickIncome
	 * ���÷���: 
	 * viewers: ����˰Ʊ�༭ҳ��
	 * messages: 
	 */
    public String doubleclickIncome(Object o){
        
        return "����˰Ʊ�༭ҳ��";
    }
    
	/**
	 * Direction: ȫѡ
	 * ename: selectAllOrNone
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAllOrNone(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ����ѡ��˰Ʊ�¼�
	 * ename: exportSelectedBillData
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportSelectedBillData(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��������˰Ʊ�¼�
	 * ename: exportAllBillData
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportAllBillData(Object o){
        
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
        
    public java.util.List getSelectedIncomeList() {
        return selectedIncomeList;
    }

    public void setSelectedIncomeList(java.util.List _selectedIncomeList) {
        selectedIncomeList = _selectedIncomeList;
    }
}
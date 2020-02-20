package com.cfcc.itfe.client.dataquery.rebatedataquery;

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

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:RebateDataQuery
 * ���:QueryDrawback
 */
public abstract class AbstractQueryDrawbackBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    	
	/** �����б� */
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_rebatedataquery_querydrawback_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractQueryDrawbackBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_rebatedataquery_querydrawback.properties");
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
			log.warn("Ϊitfe_dataquery_rebatedataquery_querydrawback��ȡmessages����", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: ������ѯ
	 * ename: queryDrawback
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryDrawback(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ��ʾ�걨����ϸ
	 * ename: queryReportDatas
	 * ���÷���: 
	 * viewers: ������ʾ
	 * messages: 
	 */
    public String queryReportDatas(Object o){
        
        return "������ʾ";
    }
    
	/**
	 * Direction: ��ԭ�˿�ƾ֤
	 * ename: backdwbkvou
	 * ���÷���: 
	 * viewers: ������ʾ
	 * messages: 
	 */
    public String backdwbkvou(Object o){
        
        return "������ʾ";
    }
    
	/**
	 * Direction: �����˻���
	 * ename: sumdwbkreport
	 * ���÷���: 
	 * viewers: ������ʾ
	 * messages: 
	 */
    public String sumdwbkreport(Object o){
        
        return "������ʾ";
    }
    
	/**
	 * Direction: ��˰��ϸ��ӡ
	 * ename: detaildwbkprint
	 * ���÷���: 
	 * viewers: ������ʾ
	 * messages: 
	 */
    public String detaildwbkprint(Object o){
        
        return "������ʾ";
    }
    
	/**
	 * Direction: ���ͻ�ִ����
	 * ename: senddwbkreport
	 * ���÷���: 
	 * viewers: ��˰��ѯ���ͻ�ִ
	 * messages: 
	 */
    public String senddwbkreport(Object o){
        
        return "��˰��ѯ���ͻ�ִ";
    }
    
	/**
	 * Direction: ȡ��У��
	 * ename: cancelcheck
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String cancelcheck(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ɾ������
	 * ename: deletereport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String deletereport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: �����˿��˻�
	 * ename: makedwbkbackreport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String makedwbkbackreport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: ���ر��Ĳ�ѯ
	 * ename: goback
	 * ���÷���: 
	 * viewers: ͨ�ò�ѯ����
	 * messages: 
	 */
    public String goback(Object o){
        
        return "ͨ�ò�ѯ����";
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
	 * Direction: ���ر��Ĵ������
	 * ename: gobackproc
	 * ���÷���: 
	 * viewers: ��˰��ѯ����
	 * messages: 
	 */
    public String gobackproc(Object o){
        
        return "��˰��ѯ����";
    }
    
	/**
	 * Direction: ����ʧ�ܻ����˻���
	 * ename: sumdwbkfailreport
	 * ���÷���: 
	 * viewers: ������ʾ2
	 * messages: 
	 */
    public String sumdwbkfailreport(Object o){
        
        return "������ʾ2";
    }
    
	/**
	 * Direction: ������ҳ��
	 * ename: goDataview
	 * ���÷���: 
	 * viewers: ��˰���Ĳ�ѯ���
	 * messages: 
	 */
    public String goDataview(Object o){
        
        return "��˰���Ĳ�ѯ���";
    }
    
	/**
	 * Direction: ����ִ����
	 * ename: godwbkbackview
	 * ���÷���: 
	 * viewers: ��˰��ѯ���ͻ�ִ
	 * messages: 
	 */
    public String godwbkbackview(Object o){
        
        return "��˰��ѯ���ͻ�ִ";
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
    }
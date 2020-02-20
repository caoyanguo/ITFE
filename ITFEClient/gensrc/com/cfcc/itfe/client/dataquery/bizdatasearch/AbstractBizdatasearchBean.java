package com.cfcc.itfe.client.dataquery.bizdatasearch;

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
 * @time   19-12-08 13:00:41
 * @generated
 * ��ϵͳ: DataQuery
 * ģ��:bizdatasearch
 * ���:Bizdatasearch
 */
public abstract class AbstractBizdatasearchBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    	
	/** �����б� */
    String trecode = null;
    String biztype = null;
    String starttime = null;
    String endtime = null;
    List trecodelist = null;
    List bizlist = null;
    List reportlist = null;
    String reportpath = null;
    Map reportmap = null;
    List resultlist = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_bizdatasearch_bizdatasearch_";
	
    /** Message */
    
    
    /** ���캯�� */
    public AbstractBizdatasearchBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_bizdatasearch_bizdatasearch.properties");
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
			log.warn("Ϊitfe_dataquery_bizdatasearch_bizdatasearch��ȡmessages����", e);
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
	 * Direction: ��ӡ
	 * ename: print
	 * ���÷���: 
	 * viewers: ��ӡ���
	 * messages: 
	 */
    public String print(Object o){
        
        return "��ӡ���";
    }
    
	/**
	 * Direction: ����
	 * ename: back
	 * ���÷���: 
	 * viewers: ҵ�����ݲ�ѯ����
	 * messages: 
	 */
    public String back(Object o){
        
        return "ҵ�����ݲ�ѯ����";
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
        
    public java.lang.String getTrecode() {
        return trecode;
    }

    public void setTrecode(java.lang.String _trecode) {
        trecode = _trecode;
    }
    
    public java.lang.String getBiztype() {
        return biztype;
    }

    public void setBiztype(java.lang.String _biztype) {
        biztype = _biztype;
    }
    
    public java.lang.String getStarttime() {
        return starttime;
    }

    public void setStarttime(java.lang.String _starttime) {
        starttime = _starttime;
    }
    
    public java.lang.String getEndtime() {
        return endtime;
    }

    public void setEndtime(java.lang.String _endtime) {
        endtime = _endtime;
    }
    
    public java.util.List getTrecodelist() {
        return trecodelist;
    }

    public void setTrecodelist(java.util.List _trecodelist) {
        trecodelist = _trecodelist;
    }
    
    public java.util.List getBizlist() {
        return bizlist;
    }

    public void setBizlist(java.util.List _bizlist) {
        bizlist = _bizlist;
    }
    
    public java.util.List getReportlist() {
        return reportlist;
    }

    public void setReportlist(java.util.List _reportlist) {
        reportlist = _reportlist;
    }
    
    public java.lang.String getReportpath() {
        return reportpath;
    }

    public void setReportpath(java.lang.String _reportpath) {
        reportpath = _reportpath;
    }
    
    public java.util.Map getReportmap() {
        return reportmap;
    }

    public void setReportmap(java.util.Map _reportmap) {
        reportmap = _reportmap;
    }
    
    public java.util.List getResultlist() {
        return resultlist;
    }

    public void setResultlist(java.util.List _resultlist) {
        resultlist = _resultlist;
    }
}
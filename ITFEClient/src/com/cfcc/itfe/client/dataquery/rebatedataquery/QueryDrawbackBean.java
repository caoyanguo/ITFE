package com.cfcc.itfe.client.dataquery.rebatedataquery;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 09-10-21 09:53:01 ��ϵͳ: DataQuery ģ��:RebateDataQuery ���:QueryDrawback
 */
public class QueryDrawbackBean extends AbstractQueryDrawbackBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(QueryDrawbackBean.class);
	private String selQueryKind;
	private String selProcFlag;

	public String getSelQueryKind() {
		return selQueryKind;
	}

	public void setSelQueryKind(String selQueryKind) {
		this.selQueryKind = selQueryKind;
	}

	public String getSelProcFlag() {
		return selProcFlag;
	}

	public void setSelProcFlag(String selProcFlag) {
		this.selProcFlag = selProcFlag;
	}

	public QueryDrawbackBean() {
		super();

	}

	/**
	 * Direction: ��˰���ݲ�ѯ ename: queryDrawback ���÷���: viewers: ��˰��ѯ-Ԥ���� messages:
	 */
	public String queryDrawback(Object o) {
		if (this.getSelQueryKind().equals("1")) {
             if (this.getSelProcFlag()== "1") {
			//	editor.openComposite(toBeOpen);
			} else {

			}
		}

		return "��˰��ѯ-Ԥ����";
	}

	/**
	 * Direction: ��ʾ�걨������ ename: queryReportDatas ���÷���: viewers: �걨���ѯ���
	 * messages:
	 */
	public String queryReportDatas(Object o) {

		return "�걨���ѯ���";
	}
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	
    
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
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	private void init() {
		// ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
		// .getDefault().getLoginInfo();
		// PageRequest pageRequest = new PageRequest();
		// PageResponse pageResponse = retrieve(pageRequest);
		// pagingcontext.setPage(pageResponse);

	}

}
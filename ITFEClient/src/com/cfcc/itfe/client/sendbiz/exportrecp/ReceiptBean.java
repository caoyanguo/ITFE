package com.cfcc.itfe.client.sendbiz.exportrecp;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment: 
 * @author db2admin
 * @time   09-10-20 08:02:09
 * ��ϵͳ: SendBiz
 * ģ��:ExportRecp
 * ���:Receipt
 */
public class ReceiptBean extends AbstractReceiptBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(ReceiptBean.class);
    public ReceiptBean() {
      super();
                  
    }
    
	/**
	 * Direction: ת����
	 * ename: export
	 * ���÷���: 
	 * viewers: ����ҳ��
	 * messages: 
	 */
    public String export(Object o){
          return super.export(o);
    }

	
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

}
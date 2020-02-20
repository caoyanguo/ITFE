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
 * 子系统: SendBiz
 * 模块:ExportRecp
 * 组件:Receipt
 */
public class ReceiptBean extends AbstractReceiptBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(ReceiptBean.class);
    public ReceiptBean() {
      super();
                  
    }
    
	/**
	 * Direction: 转导出
	 * ename: export
	 * 引用方法: 
	 * viewers: 导出页面
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
package com.cfcc.itfe.client.sendbiz.maketbsfile;

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
 * @time   09-10-20 10:35:56
 * 子系统: SendBiz
 * 模块:MakeTbsFile
 * 组件:ExportTBS
 */
public class ExportTBSBean extends AbstractExportTBSBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(ExportTBSBean.class);
    public ExportTBSBean() {
      super();
                  
    }

   // public String stick(Object o){
         // return super.stick(o);
   // }

	
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

}
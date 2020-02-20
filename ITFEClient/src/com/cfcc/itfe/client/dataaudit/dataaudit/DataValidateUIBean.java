package com.cfcc.itfe.client.dataaudit.dataaudit;

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
 * @author Administrator
 * @time   09-10-19 15:35:27
 * 子系统: DataAudit
 * 模块:DataAudit
 * 组件:DataValidateUI
 */
public class DataValidateUIBean extends AbstractDataValidateUIBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(DataValidateUIBean.class);
    public DataValidateUIBean() {
      super();
                  
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
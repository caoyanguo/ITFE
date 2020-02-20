package com.cfcc.itfe.service.subsysmanage.datachangeandcompare;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.subsysmanage.datachangeandcompare.AbstractDatachangeAndCompareService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
/**
 * @author Administrator
 * @time   18-01-03 19:32:34
 * codecomment: 
 */

public class DatachangeAndCompareService extends AbstractDatachangeAndCompareService {
	private static Log log = LogFactory.getLog(DatachangeAndCompareService.class);	


	/**
	 * 更新数据
	 	 
	 * @generated
	 * @param sql
	 * @throws ITFEBizException	 
	 */
    public void runsql(String sql) throws ITFEBizException {
    	try {
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("更新数据出现异常！");
		}
    }

}
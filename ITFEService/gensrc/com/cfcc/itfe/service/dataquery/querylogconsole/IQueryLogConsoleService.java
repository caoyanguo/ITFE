package com.cfcc.itfe.service.dataquery.querylogconsole;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IQueryLogConsoleService extends IService {



	/**
	 * 查询指定日期的接收（发送）日志	 
	 * @generated
	 * @param strType
	 * @param queryDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getLogByDate(String strType, String queryDate) throws ITFEBizException;

}
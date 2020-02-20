package com.cfcc.itfe.service.subsysmanage.dataquery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * @author Administrator
 * @time   19-12-08 13:00:43
 * @generated
 * codecomment: 
 */
public interface IDataQueryService extends IService {



	/**
	 * 查询数据	 
	 * @generated
	 * @param sql
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List find(String sql) throws ITFEBizException;

	/**
	 * 调用存储过程	 
	 * @generated
	 * @param procName
	 * @param procParams
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String callProc(String procName, List procParams) throws ITFEBizException;

}
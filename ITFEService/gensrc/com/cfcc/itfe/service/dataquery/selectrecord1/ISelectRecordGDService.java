package com.cfcc.itfe.service.dataquery.selectrecord1;

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
 * @time   19-12-08 13:00:42
 * @generated
 * codecomment: 
 */
public interface ISelectRecordGDService extends IService {



	/**
	 * 根据条件查询分组结果
	 	 
	 * @generated
	 * @param sql
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List selectGroupResultByCondition(String sql) throws ITFEBizException;

}
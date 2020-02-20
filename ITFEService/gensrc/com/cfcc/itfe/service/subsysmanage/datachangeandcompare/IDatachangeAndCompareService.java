package com.cfcc.itfe.service.subsysmanage.datachangeandcompare;

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
 * @time   19-12-08 13:00:43
 * @generated
 * codecomment: 
 */
public interface IDatachangeAndCompareService extends IService {



	/**
	 * 更新数据
	 	 
	 * @generated
	 * @param sql
	 * @throws ITFEBizException	 
	 */
   public abstract void runsql(String sql) throws ITFEBizException;

}
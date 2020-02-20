package com.cfcc.itfe.service.dataaudit.dataaudit;

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
public interface IDataCheckService extends IService {



	/**
	 * 两表数据校验	 
	 * @generated
	 * @param batchNum
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean datacheck(String batchNum) throws ITFEBizException;

}
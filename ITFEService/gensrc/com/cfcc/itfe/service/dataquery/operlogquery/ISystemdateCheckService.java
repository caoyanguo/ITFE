package com.cfcc.itfe.service.dataquery.operlogquery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface ISystemdateCheckService extends IService {



	/**
	 * 记录操作日志
	 	 
	 * @generated
	 * @param userID
	 * @param sFuncCode
	 * @param menuName
	 * @param serviceName
	 * @param operFlag
	 * @param loginInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void operLog(String userID, String sFuncCode, String menuName, String serviceName, Boolean operFlag, ITFELoginInfo loginInfo) throws ITFEBizException;

}
package com.cfcc.itfe.service.subsysmanage.sysmoniter;

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
public interface ISysmonitorService extends IService {



	/**
	 * ÏµÍ³¼à¿Ø
	 	 
	 * @generated
	 * @param monitorarea
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String sysMonitor(String monitorarea) throws ITFEBizException;

}
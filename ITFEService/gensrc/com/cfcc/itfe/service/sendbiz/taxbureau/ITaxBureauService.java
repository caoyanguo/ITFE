package com.cfcc.itfe.service.sendbiz.taxbureau;

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
 * @time   19-12-08 13:00:37
 * @generated
 * codecomment: 
 */
public interface ITaxBureauService extends IService {



	/**
	 * ·¢ËÍ±¨ÎÄ
	 	 
	 * @generated
	 * @param sendOrgCode
	 * @param reportDate
	 * @param reportArea
	 * @param msgno
	 * @throws ITFEBizException	 
	 */
   public abstract void sendMsg(String sendOrgCode, String reportDate, String reportArea, String msgno) throws ITFEBizException;

}
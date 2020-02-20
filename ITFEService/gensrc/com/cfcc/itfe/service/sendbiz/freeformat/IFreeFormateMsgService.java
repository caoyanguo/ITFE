package com.cfcc.itfe.service.sendbiz.freeformat;

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
public interface IFreeFormateMsgService extends IService {



	/**
	 * ·¢ËÍ
	 	 
	 * @generated
	 * @param sendOrgCode
	 * @param rcvOrgCode
	 * @param content
	 * @throws ITFEBizException	 
	 */
   public abstract void sendFreeFormateMsg(String sendOrgCode, String rcvOrgCode, String content) throws ITFEBizException;

}
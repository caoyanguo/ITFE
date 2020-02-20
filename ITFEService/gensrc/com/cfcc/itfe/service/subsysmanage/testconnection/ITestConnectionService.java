package com.cfcc.itfe.service.subsysmanage.testconnection;

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
public interface ITestConnectionService extends IService {



	/**
	 * 发送测试报文	 
	 * @generated
	 * @param srcvMsgNoade
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String sendTestMsg(String srcvMsgNoade) throws ITFEBizException;

	/**
	 * 查询测试结果	 
	 * @generated
	 * @param sMsgNo
	 * @param sMsgDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List searchTestResult(String sMsgNo, String sMsgDate) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @throws ITFEBizException	 
	 */
   public abstract void testMsg() throws ITFEBizException;

}
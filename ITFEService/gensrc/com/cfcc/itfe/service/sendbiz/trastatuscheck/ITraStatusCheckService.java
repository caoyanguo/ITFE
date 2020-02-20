package com.cfcc.itfe.service.sendbiz.trastatuscheck;

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
public interface ITraStatusCheckService extends IService {



	/**
	 * 查询请求
	 	 
	 * @generated
	 * @param sendOrgCode
	 * @param searchType
	 * @param oriMsgNo
	 * @param oriEntrustDate
	 * @param oriPackNo
	 * @param oriTraNo
	 * @throws ITFEBizException	 
	 */
   public abstract void traStatusCheck(String sendOrgCode, String searchType, String oriMsgNo, String oriEntrustDate, String oriPackNo, String oriTraNo) throws ITFEBizException;

	/**
	 * 查看查询结果
	 	 
	 * @generated
	 * @param sendOrgCode
	 * @param searchType
	 * @param oriMsgNo
	 * @param oriEntrustDate
	 * @param oriPackNo
	 * @param oriTraNo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List viewSelectRusult(String sendOrgCode, String searchType, String oriMsgNo, String oriEntrustDate, String oriPackNo, String oriTraNo) throws ITFEBizException;

}
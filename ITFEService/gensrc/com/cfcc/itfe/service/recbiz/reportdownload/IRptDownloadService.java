package com.cfcc.itfe.service.recbiz.reportdownload;

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
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IRptDownloadService extends IService {



	/**
	 * 报表数据下载
	 	 
	 * @generated
	 * @param strecode
	 * @param rptDate
	 * @param taxprop
	 * @param rpttype
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List downloadRpt(String strecode, String rptDate, String taxprop, String rpttype) throws ITFEBizException;

	/**
	 * 报表申请
	 	 
	 * @generated
	 * @param strecode
	 * @param srptdate
	 * @throws ITFEBizException	 
	 */
   public abstract void requestRpt(String strecode, String srptdate) throws ITFEBizException;

	/**
	 * 发送申请报文
	 	 
	 * @generated
	 * @param acctdate
	 * @param msgid
	 * @param finorg
	 * @param sorgcode
	 * @throws ITFEBizException	 
	 */
   public abstract void sendApplyInfo(String acctdate, String msgid, String finorg, String sorgcode) throws ITFEBizException;

}
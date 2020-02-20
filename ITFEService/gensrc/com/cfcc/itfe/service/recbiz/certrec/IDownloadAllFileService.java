package com.cfcc.itfe.service.recbiz.certrec;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvFilesDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IDownloadAllFileService extends IService {



	/**
	 * 获得指定日期之前的接收日志，如果日期为空，那么查询全部接收日志	 
	 * @generated
	 * @param recvDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getRecvLogBeforeDate(String recvDate) throws ITFEBizException;

	/**
	 * 根据发送流水号获得业务凭证（信息文件）的详细信息，一个发送流水号可以找到一个	 
	 * @generated
	 * @param no
	 * @return com.cfcc.itfe.persistence.dto.TvFilesDto
	 * @throws ITFEBizException	 
	 */
   public abstract TvFilesDto getFileInfoBySendNo(String no) throws ITFEBizException;

	/**
	 * 根据发送流水号查找发送凭证的所有附件，一个发送记录可能会有多个附件	 
	 * @generated
	 * @param recvLog
	 * @param sendNo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getFileListBySendNo(TvRecvLogShowDto recvLog, String sendNo) throws ITFEBizException;

	/**
	 * 修改已经下载的接收日志的处理标志	 
	 * @generated
	 * @param recvLogs
	 * @param status
	 * @throws ITFEBizException	 
	 */
   public abstract void updateStatus(List recvLogs, String status) throws ITFEBizException;

	/**
	 * 统计指定日期各种业务凭证的接收情况	 
	 * @generated
	 * @param recvDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getRecvLogReport(String recvDate) throws ITFEBizException;

	/**
	 * 作废已经接收的凭证	 
	 * @generated
	 * @param recvLog
	 * @throws ITFEBizException	 
	 */
   public abstract void recvDelete(TvRecvLogShowDto recvLog) throws ITFEBizException;

}
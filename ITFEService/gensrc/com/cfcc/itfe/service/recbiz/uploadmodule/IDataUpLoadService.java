package com.cfcc.itfe.service.recbiz.uploadmodule;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IDataUpLoadService extends IService {



	/**
	 * 判断文件是否到导入过	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean checkFileImport(String clientFilePath) throws ITFEBizException;

	/**
	 * 删除服务器上相同的文件	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String deleteSvrFile(String clientFilePath) throws ITFEBizException;

	/**
	 * 根据客户端文件路径得到服务器文件路径	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String genFileName(String clientFilePath) throws ITFEBizException;

	/**
	 * 退库文件处理写数据库	 
	 * @generated
	 * @param msgInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String treDwbkDataProc(Object msgInfo) throws ITFEBizException;

	/**
	 * 退库文件处理写数据库	 
	 * @generated
	 * @param msgInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String requestBillDataProc(Object msgInfo) throws ITFEBizException;

	/**
	 * TBS回执处理写数据库	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String tbsDataProc(String clientFilePath) throws ITFEBizException;

	/**
	 * 更新数据库标志	 
	 * @generated
	 * @param tableName
	 * @param updatesql
	 * @param wheresql
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String updateDataFlag(String tableName, String updatesql, String wheresql) throws ITFEBizException;

	/**
	 * 更新数据库标志	 
	 * @generated
	 * @param dto
	 * @param fileName
	 * @param vouType
	 * @param orgCode
	 * @param sumMoney
	 * @param fileCount
	 * @return java.lang.Void
	 * @throws ITFEBizException	 
	 */
   public abstract Void writeRecvLog(IDto dto, String fileName, String vouType, String orgCode, BigDecimal sumMoney, Integer fileCount) throws ITFEBizException;

}
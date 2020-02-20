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
	 * �ж��ļ��Ƿ񵽵����	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean checkFileImport(String clientFilePath) throws ITFEBizException;

	/**
	 * ɾ������������ͬ���ļ�	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String deleteSvrFile(String clientFilePath) throws ITFEBizException;

	/**
	 * ���ݿͻ����ļ�·���õ��������ļ�·��	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String genFileName(String clientFilePath) throws ITFEBizException;

	/**
	 * �˿��ļ�����д���ݿ�	 
	 * @generated
	 * @param msgInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String treDwbkDataProc(Object msgInfo) throws ITFEBizException;

	/**
	 * �˿��ļ�����д���ݿ�	 
	 * @generated
	 * @param msgInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String requestBillDataProc(Object msgInfo) throws ITFEBizException;

	/**
	 * TBS��ִ����д���ݿ�	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String tbsDataProc(String clientFilePath) throws ITFEBizException;

	/**
	 * �������ݿ��־	 
	 * @generated
	 * @param tableName
	 * @param updatesql
	 * @param wheresql
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String updateDataFlag(String tableName, String updatesql, String wheresql) throws ITFEBizException;

	/**
	 * �������ݿ��־	 
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
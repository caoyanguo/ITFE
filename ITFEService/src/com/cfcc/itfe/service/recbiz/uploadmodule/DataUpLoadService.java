package com.cfcc.itfe.service.recbiz.uploadmodule;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.recbiz.uploadmodule.AbstractDataUpLoadService;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author zhouchuan
 * @time   09-10-27 09:32:38
 * codecomment: 
 */

public class DataUpLoadService extends AbstractDataUpLoadService {
	private static Log log = LogFactory.getLog(DataUpLoadService.class);	


	/**
	 * �ж��ļ��Ƿ񵽵����	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
    public Boolean checkFileImport(String clientFilePath) throws ITFEBizException {
      return null;
    }

	/**
	 * ɾ������������ͬ���ļ�	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String deleteSvrFile(String clientFilePath) throws ITFEBizException {
      return null;
    }

	/**
	 * ���ݿͻ����ļ�·���õ��������ļ�·��	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String genFileName(String clientFilePath) throws ITFEBizException {
      return null;
    }

	/**
	 * �˿��ļ�����д���ݿ�	 
	 * @generated
	 * @param msgInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String treDwbkDataProc(Object msgInfo) throws ITFEBizException {
      return null;
    }

	/**
	 * �˿��ļ�����д���ݿ�	 
	 * @generated
	 * @param msgInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String requestBillDataProc(Object msgInfo) throws ITFEBizException {
      return null;
    }

	/**
	 * TBS��ִ����д���ݿ�	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String tbsDataProc(String clientFilePath) throws ITFEBizException {
      return null;
    }

	/**
	 * �������ݿ��־	 
	 * @generated
	 * @param tableName
	 * @param updatesql
	 * @param wheresql
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String updateDataFlag(String tableName, String updatesql, String wheresql) throws ITFEBizException {
      return null;
    }

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
    public Void writeRecvLog(IDto dto, String fileName, String vouType, String orgCode, BigDecimal sumMoney, Integer fileCount) throws ITFEBizException {
      return null;
    }

}
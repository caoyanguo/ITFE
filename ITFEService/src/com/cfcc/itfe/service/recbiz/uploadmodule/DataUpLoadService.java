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
	 * 判断文件是否到导入过	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
    public Boolean checkFileImport(String clientFilePath) throws ITFEBizException {
      return null;
    }

	/**
	 * 删除服务器上相同的文件	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String deleteSvrFile(String clientFilePath) throws ITFEBizException {
      return null;
    }

	/**
	 * 根据客户端文件路径得到服务器文件路径	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String genFileName(String clientFilePath) throws ITFEBizException {
      return null;
    }

	/**
	 * 退库文件处理写数据库	 
	 * @generated
	 * @param msgInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String treDwbkDataProc(Object msgInfo) throws ITFEBizException {
      return null;
    }

	/**
	 * 退库文件处理写数据库	 
	 * @generated
	 * @param msgInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String requestBillDataProc(Object msgInfo) throws ITFEBizException {
      return null;
    }

	/**
	 * TBS回执处理写数据库	 
	 * @generated
	 * @param clientFilePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String tbsDataProc(String clientFilePath) throws ITFEBizException {
      return null;
    }

	/**
	 * 更新数据库标志	 
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
    public Void writeRecvLog(IDto dto, String fileName, String vouType, String orgCode, BigDecimal sumMoney, Integer fileCount) throws ITFEBizException {
      return null;
    }

}
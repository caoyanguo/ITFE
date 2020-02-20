package com.cfcc.itfe.service.sendbiz.maketbsfile;

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
public interface IExportTBSFileService extends IService {



	/**
	 * TBS明细文件	 
	 * @generated
	 * @param batchNum
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String expTBSBillFile(String batchNum) throws ITFEBizException;

	/**
	 * 生成导出汇总文件文件,每个批次一个文件	 
	 * @generated
	 * @param batchNum
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String expTBSSUMFile(String batchNum) throws ITFEBizException;

	/**
	 * TBS资金文件	 
	 * @generated
	 * @param batchNum
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
   public abstract ArrayList expTBSFundFile(String batchNum) throws ITFEBizException;

	/**
	 * TBS文件	 
	 * @generated
	 * @param batchNum
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
   public abstract ArrayList expTBSFile(ArrayList batchNum) throws ITFEBizException;

	/**
	 * 记导出日志	 
	 * @generated
	 * @param fileName
	 * @param totalCount
	 * @param sumMoney
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
   public abstract ArrayList writeExportLog(String fileName, Integer totalCount, BigDecimal sumMoney) throws ITFEBizException;

	/**
	 * 反填资金文件名	 
	 * @generated
	 * @param vouNum
	 * @param fundFileName
	 * @return java.lang.Void
	 * @throws ITFEBizException	 
	 */
   public abstract Void fillFundFileName(String vouNum, String fundFileName) throws ITFEBizException;

	/**
	 * 取清算行信息	 
	 * @generated
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
   public abstract ArrayList getBankInfo() throws ITFEBizException;

	/**
	 * 取清算行信息	 
	 * @generated
	 * @param fileType
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
   public abstract ArrayList getBatchInfo(String fileType) throws ITFEBizException;

}
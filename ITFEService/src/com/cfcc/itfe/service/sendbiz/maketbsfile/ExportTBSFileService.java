package com.cfcc.itfe.service.sendbiz.maketbsfile;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.sendbiz.maketbsfile.AbstractExportTBSFileService;
import com.cfcc.itfe.exception.ITFEBizException;
/**
 * @author zhouchuan
 * @time   09-10-27 09:32:38
 * codecomment: 
 */

public class ExportTBSFileService extends AbstractExportTBSFileService {
	private static Log log = LogFactory.getLog(ExportTBSFileService.class);	


	/**
	 * TBS��ϸ�ļ�	 
	 * @generated
	 * @param batchNum
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String expTBSBillFile(String batchNum) throws ITFEBizException {
      return null;
    }

	/**
	 * ���ɵ��������ļ��ļ�,ÿ������һ���ļ�	 
	 * @generated
	 * @param batchNum
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String expTBSSUMFile(String batchNum) throws ITFEBizException {
      return null;
    }

	/**
	 * TBS�ʽ��ļ�	 
	 * @generated
	 * @param batchNum
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
    public ArrayList expTBSFundFile(String batchNum) throws ITFEBizException {
      return null;
    }

	/**
	 * TBS�ļ�	 
	 * @generated
	 * @param batchNum
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
    public ArrayList expTBSFile(ArrayList batchNum) throws ITFEBizException {
      return null;
    }

	/**
	 * �ǵ�����־	 
	 * @generated
	 * @param fileName
	 * @param totalCount
	 * @param sumMoney
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
    public ArrayList writeExportLog(String fileName, Integer totalCount, BigDecimal sumMoney) throws ITFEBizException {
      return null;
    }

	/**
	 * �����ʽ��ļ���	 
	 * @generated
	 * @param vouNum
	 * @param fundFileName
	 * @return java.lang.Void
	 * @throws ITFEBizException	 
	 */
    public Void fillFundFileName(String vouNum, String fundFileName) throws ITFEBizException {
      return null;
    }

	/**
	 * ȡ��������Ϣ	 
	 * @generated
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
    public ArrayList getBankInfo() throws ITFEBizException {
      return null;
    }

	/**
	 * ȡ��������Ϣ	 
	 * @generated
	 * @param fileType
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
    public ArrayList getBatchInfo(String fileType) throws ITFEBizException {
      return null;
    }

}
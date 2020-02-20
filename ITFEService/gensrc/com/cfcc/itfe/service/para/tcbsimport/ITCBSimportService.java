package com.cfcc.itfe.service.para.tcbsimport;

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
 * @time   19-12-08 13:00:38
 * @generated
 * codecomment: 
 */
public interface ITCBSimportService extends IService {



	/**
	 * 文件导入	 
	 * @generated
	 * @param filePath
	 * @throws ITFEBizException	 
	 */
   public abstract void fileImport(String filePath) throws ITFEBizException;

	/**
	 * 征收机关国库对应关系数据导入	 
	 * @generated
	 * @param filePath
	 * @throws ITFEBizException	 
	 */
   public abstract void taxFileImport(String filePath) throws ITFEBizException;

	/**
	 * 法人代码维护	 
	 * @generated
	 * @param filePath
	 * @throws ITFEBizException	 
	 */
   public abstract void tdCorpImport(String filePath) throws ITFEBizException;

	/**
	 * 银行账号信息导入	 
	 * @generated
	 * @param filePath
	 * @throws ITFEBizException	 
	 */
   public abstract void banknoImport(String filePath) throws ITFEBizException;

	/**
	 * 银行账号信息导入	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
   public abstract void bankInfoImport(List list) throws ITFEBizException;

	/**
	 * 银行账号信息导入	 
	 * @generated
	 * @param filepath
	 * @throws ITFEBizException	 
	 */
   public abstract void tbsBankImport(String filepath) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param filepath
	 * @param tabcode
	 * @throws ITFEBizException	 
	 */
   public abstract void paramImport(String filepath, String tabcode) throws ITFEBizException;

	/**
	 * 二代行名行号导入	 
	 * @generated
	 * @param filePath
	 * @throws ITFEBizException	 
	 */
   public abstract void bankCodeImport(String filePath) throws ITFEBizException;

}
package com.cfcc.itfe.service.dataquery.tbbillstore;

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
 * @time   19-12-08 13:00:42
 * @generated
 * codecomment: 
 */
public interface ITbbillstoreService extends IService {



	/**
	 * 文件下载
	 	 
	 * @generated
	 * @param paramlist
	 * @throws ITFEBizException	 
	 */
   public abstract void filedownLoad(List paramlist) throws ITFEBizException;

	/**
	 * 文件上传
	 	 
	 * @generated
	 * @param paramList
	 * @throws ITFEBizException	 
	 */
   public abstract void fileupload(List paramList) throws ITFEBizException;

	/**
	 * 查询上传报表信息
	 	 
	 * @generated
	 * @param paramList
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List searchFileInfo(List paramList) throws ITFEBizException;

}
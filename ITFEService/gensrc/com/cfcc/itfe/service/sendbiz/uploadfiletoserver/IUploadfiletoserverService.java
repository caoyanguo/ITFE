package com.cfcc.itfe.service.sendbiz.uploadfiletoserver;

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
public interface IUploadfiletoserverService extends IService {



	/**
	 * 查询列表
	 	 
	 * @generated
	 * @param parammap
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List searchfile(Map parammap) throws ITFEBizException;

	/**
	 * 下载文件
	 	 
	 * @generated
	 * @param parammap
	 * @throws ITFEBizException	 
	 */
   public abstract void downloadfile(Map parammap) throws ITFEBizException;

	/**
	 * 			 
	 * @generated
	 * @param parammap
	 * @throws ITFEBizException	 
	 */
   public abstract void deletefile(Map parammap) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param parammap
	 * @throws ITFEBizException	 
	 */
   public abstract void uploadfile(Map parammap) throws ITFEBizException;

}
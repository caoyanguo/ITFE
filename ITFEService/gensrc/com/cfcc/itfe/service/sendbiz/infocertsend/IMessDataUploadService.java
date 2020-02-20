package com.cfcc.itfe.service.sendbiz.infocertsend;

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
public interface IMessDataUploadService extends IService {



	/**
	 * 信息凭证上传	 
	 * @generated
	 * @param strTitle
	 * @param strContent
	 * @param recvOrgList
	 * @param upLoadFileList
	 * @throws ITFEBizException	 
	 */
   public abstract void upload(String strTitle, String strContent, List recvOrgList, List upLoadFileList) throws ITFEBizException;

	/**
	 * 获得全部连通机构信息	 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getAllConnOrgs() throws ITFEBizException;

	/**
	 * 删除一个已经上载的文件	 
	 * @generated
	 * @param filePath
	 * @throws ITFEBizException	 
	 */
   public abstract void deleteOneFile(String filePath) throws ITFEBizException;

	/**
	 * 记录错误发送日志	 
	 * @generated
	 * @param title
	 * @param errMsg
	 * @throws ITFEBizException	 
	 */
   public abstract void addErrorSendLog(String title, String errMsg) throws ITFEBizException;

}
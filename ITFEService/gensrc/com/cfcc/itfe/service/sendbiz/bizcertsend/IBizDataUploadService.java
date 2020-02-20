package com.cfcc.itfe.service.sendbiz.bizcertsend;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * codecomment: 
 */
public interface IBizDataUploadService extends IService {



	/**
	 * 检查待发送的文件是否已经存在	 
	 * @generated
	 * @param fileName
	 * @return com.cfcc.devplatform.customtype.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract boolean isFileExists(String fileName) throws ITFEBizException;

	/**
	 * 业务凭证上载的处理方法	 
	 * @generated
	 * @param model
	 * @param filePath
	 * @param recvOrgs
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer upload(TsOperationmodelDto model, String filePath, List recvOrgs) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @throws ITFEBizException	 
	 */
   public abstract void showReport() throws ITFEBizException;

	/**
	 * 获得缺省的连通机构信息（国库局）	 
	 * @generated
	 * @return com.cfcc.itfe.persistence.dto.TsOrganDto
	 * @throws ITFEBizException	 
	 */
   public abstract TsOrganDto getDefaultConnOrgs() throws ITFEBizException;

	/**
	 * 获得全部连通机构信息	 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getAllConnOrgs() throws ITFEBizException;

	/**
	 * 获得公共证书的内容	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getCertContent() throws ITFEBizException;

	/**
	 * 记录错误发送日志	 
	 * @generated
	 * @param model
	 * @param title
	 * @param errMsg
	 * @throws ITFEBizException	 
	 */
   public abstract void addErrorSendLog(TsOperationmodelDto model, String title, String errMsg) throws ITFEBizException;

	/**
	 * 获得指定业务凭证的凭证编号（如果存在）	 
	 * @generated
	 * @param vouType
	 * @param fileName
	 * @return com.cfcc.devplatform.customtype.Int
	 * @throws ITFEBizException	 
	 */
   public abstract int getVouNo(String vouType, String fileName) throws ITFEBizException;

	/**
	 * 为指定业务凭证生成凭证编号	 
	 * @generated
	 * @param vouType
	 * @param fileName
	 * @return com.cfcc.devplatform.customtype.Int
	 * @throws ITFEBizException	 
	 */
   public abstract int addVouNo(String vouType, String fileName) throws ITFEBizException;

}
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
	 * �������͵��ļ��Ƿ��Ѿ�����	 
	 * @generated
	 * @param fileName
	 * @return com.cfcc.devplatform.customtype.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract boolean isFileExists(String fileName) throws ITFEBizException;

	/**
	 * ҵ��ƾ֤���صĴ�����	 
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
	 * ���ȱʡ����ͨ������Ϣ������֣�	 
	 * @generated
	 * @return com.cfcc.itfe.persistence.dto.TsOrganDto
	 * @throws ITFEBizException	 
	 */
   public abstract TsOrganDto getDefaultConnOrgs() throws ITFEBizException;

	/**
	 * ���ȫ����ͨ������Ϣ	 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getAllConnOrgs() throws ITFEBizException;

	/**
	 * ��ù���֤�������	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getCertContent() throws ITFEBizException;

	/**
	 * ��¼��������־	 
	 * @generated
	 * @param model
	 * @param title
	 * @param errMsg
	 * @throws ITFEBizException	 
	 */
   public abstract void addErrorSendLog(TsOperationmodelDto model, String title, String errMsg) throws ITFEBizException;

	/**
	 * ���ָ��ҵ��ƾ֤��ƾ֤��ţ�������ڣ�	 
	 * @generated
	 * @param vouType
	 * @param fileName
	 * @return com.cfcc.devplatform.customtype.Int
	 * @throws ITFEBizException	 
	 */
   public abstract int getVouNo(String vouType, String fileName) throws ITFEBizException;

	/**
	 * Ϊָ��ҵ��ƾ֤����ƾ֤���	 
	 * @generated
	 * @param vouType
	 * @param fileName
	 * @return com.cfcc.devplatform.customtype.Int
	 * @throws ITFEBizException	 
	 */
   public abstract int addVouNo(String vouType, String fileName) throws ITFEBizException;

}
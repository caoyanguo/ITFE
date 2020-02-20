package com.cfcc.itfe.service.commonsubsys.commondbaccess;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsOperationtypeDto;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * codecomment: 
 */
public interface IStampDataAccessService extends IService {



	/**
	 * �����ļ�������ҵ��ƾ֤������Ϣ	 
	 * @generated
	 * @param fileType
	 * @return com.cfcc.itfe.persistence.dto.TsOperationtypeDto
	 * @throws ITFEBizException	 
	 */
   public abstract TsOperationtypeDto getOperationTypeByFileType(String fileType) throws ITFEBizException;

	/**
	 * ����ҵ��ƾ֤������Model��Ϣ	 
	 * @generated
	 * @param operationType
	 * @return com.cfcc.itfe.persistence.dto.TsOperationmodelDto
	 * @throws ITFEBizException	 
	 */
   public abstract TsOperationmodelDto getModelByOperationType(String operationType) throws ITFEBizException;

	/**
	 * ����ҵ��ƾ֤��ģ��Id���ҵ��ƾ֤����Ϣ	 
	 * @generated
	 * @param modelId
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getFormByModelId(String modelId) throws ITFEBizException;

	/**
	 * ����ҵ��ƾ֤ģ��Id���ҵ��ƾ֤�����и��º�����λ��	 
	 * @generated
	 * @param modelId
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getPlaceByModelId(String modelId) throws ITFEBizException;

	/**
	 * ����ҵ��ƾ֤ģ����Ϣ���ҵ��ƾ֤������������λ�á�ģ�����ݵ���Ϣ	 
	 * @generated
	 * @param model
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getStampInfoByModel(IDto model) throws ITFEBizException;

	/**
	 * �����ļ������·��������ļ�����	 
	 * @generated
	 * @param path
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getFileByPath(String path) throws ITFEBizException;

	/**
	 * ����û�ָ��ҵ��ƾ֤�ĸ���Ȩ��	 
	 * @generated
	 * @param modelId
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map getUserStampFuction(String modelId) throws ITFEBizException;

}
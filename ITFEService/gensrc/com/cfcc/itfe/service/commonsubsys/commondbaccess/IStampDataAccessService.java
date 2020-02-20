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
	 * 根据文件种类获得业务凭证类型信息	 
	 * @generated
	 * @param fileType
	 * @return com.cfcc.itfe.persistence.dto.TsOperationtypeDto
	 * @throws ITFEBizException	 
	 */
   public abstract TsOperationtypeDto getOperationTypeByFileType(String fileType) throws ITFEBizException;

	/**
	 * 根据业务凭证种类获得Model信息	 
	 * @generated
	 * @param operationType
	 * @return com.cfcc.itfe.persistence.dto.TsOperationmodelDto
	 * @throws ITFEBizException	 
	 */
   public abstract TsOperationmodelDto getModelByOperationType(String operationType) throws ITFEBizException;

	/**
	 * 根据业务凭证的模版Id获得业务凭证联信息	 
	 * @generated
	 * @param modelId
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getFormByModelId(String modelId) throws ITFEBizException;

	/**
	 * 根据业务凭证模版Id获得业务凭证中所有盖章和验章位置	 
	 * @generated
	 * @param modelId
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getPlaceByModelId(String modelId) throws ITFEBizException;

	/**
	 * 根据业务凭证模版信息获得业务凭证盖章联、盖章位置、模版内容等信息	 
	 * @generated
	 * @param model
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getStampInfoByModel(IDto model) throws ITFEBizException;

	/**
	 * 根据文件的相对路径，获得文件内容	 
	 * @generated
	 * @param path
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getFileByPath(String path) throws ITFEBizException;

	/**
	 * 获得用户指定业务凭证的盖章权限	 
	 * @generated
	 * @param modelId
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map getUserStampFuction(String modelId) throws ITFEBizException;

}
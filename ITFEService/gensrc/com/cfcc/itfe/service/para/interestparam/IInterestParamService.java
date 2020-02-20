package com.cfcc.itfe.service.para.interestparam;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IInterestParamService extends IService {



	/**
	 * ����	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
   public abstract IDto addInfo(IDto dtoInfo) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(IDto dtoInfo) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void modInfo(IDto dtoInfo) throws ITFEBizException;

	/**
	 * ���Ӽ�Ϣ�˻�	 
	 * @generated
	 * @param JXDtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
   public abstract IDto addJXInfo(IDto JXDtoInfo) throws ITFEBizException;

	/**
	 * ɾ����Ϣ�˻�	 
	 * @generated
	 * @param JXDtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void delJXInfo(IDto JXDtoInfo) throws ITFEBizException;

	/**
	 * �޸ļ�Ϣ�˻�	 
	 * @generated
	 * @param tsJxAcctinfoDto
	 * @param JXDtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void modJXInfo(IDto tsJxAcctinfoDto, IDto JXDtoInfo) throws ITFEBizException;

}
package com.cfcc.itfe.service.dataquery.trstockdayrpt;

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
public interface ITrStockdayrptService extends IService {



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
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @param finddtolist
	 * @param startDate
	 * @param endDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findTotalBalForExport(IDto dtoInfo, List finddtolist, String startDate, String endDate) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param acctNo
	 * @param startDate
	 * @param endDate
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String findAcctName(String acctNo, String startDate, String endDate) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param idto
	 * @param where
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsByDtoWithWhere(IDto idto, String where) throws ITFEBizException;

}
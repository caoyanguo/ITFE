package com.cfcc.itfe.service.dataquery.tvfinincomeonline;

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
public interface ITvFinIncomeonlineService extends IService {



	/**
	 * 增加	 
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
	 * 校验纳税人与国库对应关系	 
	 * @generated
	 * @param excheckList
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String checkTaxPayCodeOrTrecode(List excheckList) throws ITFEBizException;

	/**
	 * 手动处理共享分成	 
	 * @generated
	 * @param excheckList
	 * @param reportdate
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String makeDivide(List excheckList, String reportdate) throws ITFEBizException;

	/**
	 * 导出数据	 
	 * @generated
	 * @param dto
	 * @param sqlwhere
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List exportTable(IDto dto, String sqlwhere) throws ITFEBizException;

}
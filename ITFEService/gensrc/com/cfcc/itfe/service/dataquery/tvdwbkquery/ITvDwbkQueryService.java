package com.cfcc.itfe.service.dataquery.tvdwbkquery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface ITvDwbkQueryService extends IService {



	/**
	 * 获取查询结果	 
	 * @generated
	 * @param dtoInfo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getSearchList(TvDwbkDto dtoInfo) throws ITFEBizException;

	/**
	 * 批量更新失败	 
	 * @generated
	 * @param dtoInfos
	 * @throws ITFEBizException	 
	 */
   public abstract void updateFail(List dtoInfos) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param selectedtable
	 * @param dtoInfo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List searchForReport(String selectedtable, IDto dtoInfo) throws ITFEBizException;

	/**
	 * 批量更新成功	 
	 * @generated
	 * @param dtoInfos
	 * @throws ITFEBizException	 
	 */
   public abstract void updateSuccess(List dtoInfos) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtolist
	 * @throws ITFEBizException	 
	 */
   public abstract void setback(List dtolist) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param finddto
	 * @param selecttable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportfile(IDto finddto, String selecttable) throws ITFEBizException;

}
package com.cfcc.itfe.service.dataquery.payoutfinancequery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceMainDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IPayOutFinanceQueryService extends IService {



	/**
	 * 分页查询批量拨付主信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPage(TvPayoutfinanceMainDto mainDto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * 分页查询批量拨付子信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPage(TvPayoutfinanceMainDto mainDto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * 分页查询批量拨付主信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPageForHis(HtvPayoutfinanceMainDto mainDto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * 分页查询批量拨付子信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPageForHis(HtvPayoutfinanceMainDto mainDto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param selectTable
	 * @param dtoInfo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List searchForReport(String selectTable, IDto dtoInfo) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param finddto
	 * @param selectedtable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String dataexport(IDto finddto, String selectedtable) throws ITFEBizException;

}
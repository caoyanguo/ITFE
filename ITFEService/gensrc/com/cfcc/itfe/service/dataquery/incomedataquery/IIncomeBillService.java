package com.cfcc.itfe.service.dataquery.incomedataquery;

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
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IIncomeBillService extends IService {



	/**
	 * 分页查询税票收入信息	 
	 * @generated
	 * @param findDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findIncomeByPage(TvInfileDto findDto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * 重发TIPS没有收到的报文	 
	 * @generated
	 * @param scommitdate
	 * @param spackageno
	 * @param sorgcode
	 * @param sfilename
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String reSendMsg(String scommitdate, String spackageno, String sorgcode, String sfilename) throws ITFEBizException;

	/**
	 * 导出收入税票信息（以便修改后重新发送）	 
	 * @generated
	 * @param spackageno
	 * @param exportDto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportIncomeData(String spackageno, TvInfileDto exportDto) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param findDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findIncomeByPage(TvInfileDetailDto findDto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * 导出所选择的收入税票信息	 
	 * @generated
	 * @param exportSelectedList
	 * @param ifdetail
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportSelectedIncomeData(List exportSelectedList, String ifdetail) throws ITFEBizException;

	/**
	 * 导出全部的收入税票信息	 
	 * @generated
	 * @param exportAlldataList
	 * @param ifdetail
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportAllIncomeData(List exportAlldataList, String ifdetail) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param findDto
	 * @param ifdetail
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findIncomeByDto(IDto findDto, String ifdetail) throws ITFEBizException;

}
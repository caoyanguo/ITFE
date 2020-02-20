package com.cfcc.itfe.service.dataquery.pbcpayquery;

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
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.itfe.persistence.dto.HtvPbcpayMainDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IPbcpayService extends IService {



	/**
	 * 人行办理授权支付主体信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPage(TvPbcpayMainDto mainDto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * 人行办理授权支付明细信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPage(TvPbcpayMainDto mainDto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * 人行办理授权支付主体信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPageForHis(HtvPbcpayMainDto mainDto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * 人行办理授权支付明细信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPageForHis(HtvPbcpayMainDto mainDto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * dataExport	 
	 * @generated
	 * @param finddto
	 * @param selectedtable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String dataExport(IDto finddto, String selectedtable) throws ITFEBizException;

	/**
	 * addInfo	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
   public abstract IDto addInfo(IDto dtoInfo) throws ITFEBizException;

	/**
	 * addSubInfo	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void addSubInfo(IDto dtoInfo) throws ITFEBizException;

	/**
	 * updateInfo	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void updateInfo(IDto dtoInfo) throws ITFEBizException;

}
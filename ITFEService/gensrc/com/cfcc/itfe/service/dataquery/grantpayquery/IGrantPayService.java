package com.cfcc.itfe.service.dataquery.grantpayquery;

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
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.itfe.persistence.dto.HtvGrantpaymsgmainDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IGrantPayService extends IService {



	/**
	 * 分页查询授权支付额度主信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPage(TvGrantpaymsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * 分页查询授权支付额度子信息	 
	 * @generated
	 * @param mainDto
	 * @param sstatus
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPage(TvGrantpaymsgmainDto mainDto, String sstatus, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

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
	 * 分页查询授权支付额度主信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPageForHis(HtvGrantpaymsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * 分页查询授权支付额度子信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param sstatus
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPageForHis(HtvGrantpaymsgmainDto mainDto, String sstatus, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * 导出txt	 
	 * @generated
	 * @param mainDto
	 * @param selectedtable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String dataexport(IDto mainDto, String selectedtable) throws ITFEBizException;

	/**
	 * 导出CSV	 
	 * @generated
	 * @param mainDto
	 * @param selectedtable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportFile(IDto mainDto, String selectedtable) throws ITFEBizException;

}
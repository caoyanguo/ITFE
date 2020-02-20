package com.cfcc.itfe.service.dataquery.payoutquery;

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
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IPayoutService extends IService {



	/**
	 * 分页查询实拨资金主信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPage(TvPayoutmsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * 分页查询实拨资金子信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPage(TvPayoutmsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

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
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param updateDto
	 * @throws ITFEBizException	 
	 */
   public abstract void saveInfo(TvPayoutmsgmainDto updateDto) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param sorgcode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List sendfilelist(String sorgcode) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param sorgcode
	 * @param strecode
	 * @param sfilename
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getsendmsg(String sorgcode, String strecode, String sfilename) throws ITFEBizException;

	/**
	 * 分页查询实拨资金主信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPageForHis(HtvPayoutmsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * 分页查询实拨资金子信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPageForHis(HtvPayoutmsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * 查询打印实拨资金信息	 
	 * @generated
	 * @param finddto
	 * @param selectedtable
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findPayOutByPrint(IDto finddto, String selectedtable) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param finddto
	 * @param selectedtable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String dataexport(IDto finddto, String selectedtable) throws ITFEBizException;

	/**
	 * 导出选中回单	 
	 * @generated
	 * @param selectedtable
	 * @param selectedlist
	 * @param filePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportSelectData(String selectedtable, List selectedlist, String filePath) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param maindto
	 * @param selecttable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportfile(IDto maindto, String selecttable) throws ITFEBizException;

}
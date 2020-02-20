package com.cfcc.itfe.service.dataquery.commapplypaybackquery;

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
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface ICommApplyPayBackQueryService extends IService {



	/**
	 * 退回商行办理支付划款申请主体信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPage(TvPayreckBankBackDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * 商行办理支付划款申请退回明细信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPage(TvPayreckBankBackDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param findidto
	 * @param selecttable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportCommApplyPayBack(IDto findidto, String selecttable) throws ITFEBizException;

	/**
	 * 导出CSV	 
	 * @generated
	 * @param findidto
	 * @param selecttable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportFile(IDto findidto, String selecttable) throws ITFEBizException;

}
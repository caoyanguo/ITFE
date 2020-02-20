package com.cfcc.itfe.service.recbiz.backvoucher;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.facade.data.MulitTableDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IBackVoucherService extends IService {



	/**
	 * 生成凭证
	 	 
	 * @generated
	 * @param params
	 * @throws ITFEBizException	 
	 */
   public abstract void voucherGenerate(List params) throws ITFEBizException;

	/**
	 * 获得索引表信息
	 	 
	 * @generated
	 * @param params
	 * @return com.cfcc.itfe.persistence.dto.TvVoucherinfoDto
	 * @throws ITFEBizException	 
	 */
   public abstract TvVoucherinfoDto getTvVoucherinfoDto(List params) throws ITFEBizException;

	/**
	 * voucherGenerate	 
	 * @generated
	 * @param list
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto voucherGenerateForTCBS(List list) throws ITFEBizException;

	/**
	 * voucherGenerateForPayoutBack	 
	 * @generated
	 * @param list
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List voucherGenerateForPayoutBack(List list) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findMainDto(TvVoucherinfoDto dto) throws ITFEBizException;

}
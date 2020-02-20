package com.cfcc.itfe.service.dataquery.voucherallocateincome;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IVoucherAllocateIncomeService extends IService {



	/**
	 * ±£´æ
	 	 
	 * @generated
	 * @param saveDto
	 * @throws ITFEBizException	 
	 */
   public abstract void saveDto(TvVoucherinfoAllocateIncomeDto saveDto) throws ITFEBizException;

	/**
	 * É¾³ý
	 	 
	 * @generated
	 * @param deleteDto
	 * @throws ITFEBizException	 
	 */
   public abstract void deleteDto(TvVoucherinfoAllocateIncomeDto deleteDto) throws ITFEBizException;

	/**
	 * ÐÞ¸Ä±£´æ
	 	 
	 * @generated
	 * @param modifyDto
	 * @throws ITFEBizException	 
	 */
   public abstract void modifySaveDto(TvVoucherinfoAllocateIncomeDto modifyDto) throws ITFEBizException;

}
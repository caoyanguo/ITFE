package com.cfcc.itfe.service.recbiz.bankvouchervalidate;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.page.PageRequest;

/**
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * codecomment: 
 */
public interface IBankVoucherValidateService extends IService {



	/**
	 * 导入并开始校验
	 	 
	 * @generated
	 * @param fileList
	 * @param paramDto
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto voucherImport(List fileList, IDto paramDto) throws ITFEBizException;

	/**
	 * 开始比对数据
	 	 
	 * @generated
	 * @param startDate
	 * @param endDate
	 * @param paramDto
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto voucherCompare(String startDate, String endDate, IDto paramDto) throws ITFEBizException;

	/**
	 * 查询比对结果
	 	 
	 * @generated
	 * @param startDate
	 * @param endDate
	 * @param paramDto
	 * @param request
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findValidateResult(String startDate, String endDate, IDto paramDto, PageRequest request) throws ITFEBizException;

}
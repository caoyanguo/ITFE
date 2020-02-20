package com.cfcc.itfe.service.dataquery.incomeprintproc;

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

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IDealIncomeService extends IService {



	/**
	 * 分页查询国库收入信息	 
	 * @generated
	 * @param finddto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findIncomeByPage(TvInfileDto finddto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * 查询打印国库收入信息	 
	 * @generated
	 * @param printdto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findIncomeByPrint(TvInfileDto printdto) throws ITFEBizException;

}
package com.cfcc.itfe.service.dataquery.businessdetaillist;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IBusinessDetailListService extends IService {



	/**
	 * 生成报表
	 	 
	 * @generated
	 * @param biztype
	 * @param paramdto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeReport(String biztype, TipsParamDto paramdto) throws ITFEBizException;

	/**
	 * 生成报表
	 	 
	 * @generated
	 * @param biztype
	 * @param paramdto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse makestatReport(String biztype, TipsParamDto paramdto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * 生成报表
	 	 
	 * @generated
	 * @param biztype
	 * @param paramdto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeSumCountRecord(String biztype, TipsParamDto paramdto) throws ITFEBizException;

	/**
	 * 主单报表查询
	 	 
	 * @generated
	 * @param biztype
	 * @param paramdto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsForMain(String biztype, TipsParamDto paramdto) throws ITFEBizException;

	/**
	 * 明细报表查询
	 	 
	 * @generated
	 * @param biztype
	 * @param paramdto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsForDetail(String biztype, TipsParamDto paramdto) throws ITFEBizException;

}
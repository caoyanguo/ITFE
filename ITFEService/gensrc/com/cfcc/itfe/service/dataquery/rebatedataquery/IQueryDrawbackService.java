package com.cfcc.itfe.service.dataquery.rebatedataquery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IQueryDrawbackService extends IService {



	/**
	 * 通用查询方法
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List commonQuery(IDto dto) throws ITFEBizException;

	/**
	 * 显示申报表
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List queryReportDatas(IDto dto) throws ITFEBizException;

	/**
	 * 还原退库凭证
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List backdwbkvou(IDto dto) throws ITFEBizException;

	/**
	 * 汇总退还书
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List sumdwbkreport(IDto dto) throws ITFEBizException;

	/**
	 * 退税明细打印
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List detaildwbkprint(IDto dto) throws ITFEBizException;

	/**
	 * 发送回执报文
	 	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
   public abstract void senddwbkreport(List list) throws ITFEBizException;

	/**
	 * 取消校验
	 	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
   public abstract void cancelcheck(List list) throws ITFEBizException;

	/**
	 * 通用查询方法
	 	 
	 * @generated
	 * @param batchnum
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String deletereport(String batchnum) throws ITFEBizException;

	/**
	 * 生成退库退回
	 	 
	 * @generated
	 * @param list
	 * @param fundfilename
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makedwbkbackreport(List list, String fundfilename) throws ITFEBizException;

}
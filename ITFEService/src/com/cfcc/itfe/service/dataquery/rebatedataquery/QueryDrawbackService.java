package com.cfcc.itfe.service.dataquery.rebatedataquery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.dataquery.rebatedataquery.AbstractQueryDrawbackService;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author caoyg
 * @time   09-11-15 12:00:53
 * codecomment: 
 */

public class QueryDrawbackService extends AbstractQueryDrawbackService {
	private static Log log = LogFactory.getLog(QueryDrawbackService.class);	


	/**
	 * 通用查询方法
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List commonQuery(IDto dto) throws ITFEBizException {
      return null;
    }

	/**
	 * 显示申报表
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List queryReportDatas(IDto dto) throws ITFEBizException {
      return null;
    }

	/**
	 * 还原退库凭证
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List backdwbkvou(IDto dto) throws ITFEBizException {
      return null;
    }

	/**
	 * 汇总退还书
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List sumdwbkreport(IDto dto) throws ITFEBizException {
      return null;
    }

	/**
	 * 退税明细打印
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List detaildwbkprint(IDto dto) throws ITFEBizException {
      return null;
    }

	/**
	 * 发送回执报文
	 	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
    public void senddwbkreport(List list) throws ITFEBizException {
      
    }

	/**
	 * 取消校验
	 	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
    public void cancelcheck(List list) throws ITFEBizException {
      
    }

	/**
	 * 通用查询方法
	 	 
	 * @generated
	 * @param batchnum
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String deletereport(String batchnum) throws ITFEBizException {
      return null;
    }

	/**
	 * 生成退库退回
	 	 
	 * @generated
	 * @param list
	 * @param fundfilename
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List makedwbkbackreport(List list, String fundfilename) throws ITFEBizException {
      return null;
    }

}
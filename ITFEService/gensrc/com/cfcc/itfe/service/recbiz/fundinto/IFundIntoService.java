package com.cfcc.itfe.service.recbiz.fundinto;

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
import com.cfcc.itfe.facade.data.BizDataCountDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IFundIntoService extends IService {



	/**
	 * 获取收款人信息
	 	 
	 * @generated
	 * @param taxcode
	 * @param orgcode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getPayerInfo(String taxcode, String orgcode) throws ITFEBizException;

	/**
	 * 得到预算科目代码缓存
	 	 
	 * @generated
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map makeBudgCode() throws ITFEBizException;

	/**
	 * 更新或保存数据
	 	 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException	 
	 */
   public abstract void createOrUpdate(IDto idto) throws ITFEBizException;

	/**
	 * 更新组号数据
	 	 
	 * @generated
	 * @param paramdto
	 * @param allsql
	 * @param sqlwhere
	 * @param idto
	 * @param operType
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String updateGroup(BizDataCountDto paramdto, String allsql, String sqlwhere, IDto idto, String operType) throws ITFEBizException;

	/**
	 * 删除数据
	 	 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException	 
	 */
   public abstract void deleteData(IDto idto) throws ITFEBizException;

}
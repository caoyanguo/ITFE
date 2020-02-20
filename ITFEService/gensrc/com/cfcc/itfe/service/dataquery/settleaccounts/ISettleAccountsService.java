package com.cfcc.itfe.service.dataquery.settleaccounts;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.DetailTsUsers;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface ISettleAccountsService extends IService {



	/**
	 * 查询
	 	 
	 * @generated
	 * @param detailDto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List search(DetailTsUsers detailDto) throws ITFEBizException;

	/**
	 * 根据级次和机构代码查询上下级
	 	 
	 * @generated
	 * @param sorgcode
	 * @param orgleve
	 * @param detailDto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List searchByLeve(String sorgcode, String orgleve, DetailTsUsers detailDto) throws ITFEBizException;

}
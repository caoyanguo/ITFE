package com.cfcc.itfe.service.para.tspayacctinfo;

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
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITspayacctinfoService extends IService {



	/**
	 * 保存
	 	 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException	 
	 */
   public abstract void save(IDto idto) throws ITFEBizException;

	/**
	 * 修改
	 	 
	 * @generated
	 * @param oriDto
	 * @param saveDto
	 * @throws ITFEBizException	 
	 */
   public abstract void mod(IDto oriDto, IDto saveDto) throws ITFEBizException;

	/**
	 * 删除信息
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void del(IDto dto) throws ITFEBizException;

	/**
	 * 校验代理行行号
	 	 
	 * @generated
	 * @param agentBankNo
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean verifyPayeeBankNo(String agentBankNo) throws ITFEBizException;

}
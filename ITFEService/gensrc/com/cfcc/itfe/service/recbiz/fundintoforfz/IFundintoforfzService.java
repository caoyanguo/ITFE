package com.cfcc.itfe.service.recbiz.fundintoforfz;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IFundintoforfzService extends IService {



	/**
	 * 获取查询列表
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getdestorydata(TbsTvPayoutDto dto) throws ITFEBizException;

	/**
	 * 销号
	 	 
	 * @generated
	 * @param tmpdto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer destorydata(TbsTvPayoutDto tmpdto) throws ITFEBizException;

}
package com.cfcc.itfe.service.para.tstaxpayacct;

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
public interface ITsTaxPayacctService extends IService {



	/**
	 * Â¼Èë
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean addInfo(IDto dto) throws ITFEBizException;

	/**
	 * ÐÞ¸Ä
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean modifyInfo(IDto dto) throws ITFEBizException;

	/**
	 * É¾³ý
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean delete(IDto dto) throws ITFEBizException;

}
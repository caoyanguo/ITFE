package com.cfcc.itfe.service.para.tpsharedivide;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TpShareDivideDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITpShareDivideService extends IService {



	/**
	 * Ôö¼Ó	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void addInfo(TpShareDivideDto dtoInfo) throws ITFEBizException;

	/**
	 * É¾³ý	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(TpShareDivideDto dtoInfo) throws ITFEBizException;

	/**
	 * ÐÞ¸Ä	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void modInfo(TpShareDivideDto dtoInfo) throws ITFEBizException;

}
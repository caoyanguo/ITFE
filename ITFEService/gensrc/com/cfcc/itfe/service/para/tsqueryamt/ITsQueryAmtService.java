package com.cfcc.itfe.service.para.tsqueryamt;

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
public interface ITsQueryAmtService extends IService {



	/**
	 * Ôö¼Ó	 
	 * @generated
	 * @param addinfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
   public abstract IDto addInfo(IDto addinfo) throws ITFEBizException;

	/**
	 * É¾³ý	 
	 * @generated
	 * @param delInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(IDto delInfo) throws ITFEBizException;

	/**
	 * ÐÞ¸Ä	 
	 * @generated
	 * @param modifyInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
   public abstract IDto modifyInfo(IDto modifyInfo) throws ITFEBizException;

}
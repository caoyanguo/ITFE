package com.cfcc.itfe.service.para.tsisnopaper;

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
public interface ITsIsnopaperService extends IService {



	/**
	 * Ôö¼Ó
	 	 
	 * @generated
	 * @param IDto
	 * @throws ITFEBizException	 
	 */
   public abstract void addInfo(IDto IDto) throws ITFEBizException;

	/**
	 * É¾³ý
	 	 
	 * @generated
	 * @param IDto
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(IDto IDto) throws ITFEBizException;

	/**
	 * ÐÞ¸Ä
	 	 
	 * @generated
	 * @param IDto
	 * @throws ITFEBizException	 
	 */
   public abstract void modInfo(IDto IDto) throws ITFEBizException;

}
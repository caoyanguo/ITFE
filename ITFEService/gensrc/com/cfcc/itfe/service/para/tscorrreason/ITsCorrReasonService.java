package com.cfcc.itfe.service.para.tscorrreason;

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
public interface ITsCorrReasonService extends IService {



	/**
	 * ����	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
   public abstract IDto addInfo(IDto dtoInfo) throws ITFEBizException;

	/**
	 * ɾ��	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(IDto dtoInfo) throws ITFEBizException;

	/**
	 * �޸�	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void modInfo(IDto dtoInfo) throws ITFEBizException;

}
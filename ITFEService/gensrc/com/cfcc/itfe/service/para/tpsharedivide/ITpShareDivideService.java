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
	 * ����	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void addInfo(TpShareDivideDto dtoInfo) throws ITFEBizException;

	/**
	 * ɾ��	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(TpShareDivideDto dtoInfo) throws ITFEBizException;

	/**
	 * �޸�	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void modInfo(TpShareDivideDto dtoInfo) throws ITFEBizException;

}
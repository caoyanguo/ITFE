package com.cfcc.itfe.service.para.tdbookacctmain;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdBookacctMainDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITdBookacctMainService extends IService {



	/**
	 * ����	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void addInfo(TdBookacctMainDto dtoInfo) throws ITFEBizException;

	/**
	 * ɾ��	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(TdBookacctMainDto dtoInfo) throws ITFEBizException;

	/**
	 * �޸�	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void modInfo(TdBookacctMainDto dtoInfo) throws ITFEBizException;

}
package com.cfcc.itfe.service.para.tsmtofaccpt;

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
public interface ITsMtofAccptService extends IService {



	/**
	 * ɾ��	 
	 * @generated
	 * @param delDto
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(IDto delDto) throws ITFEBizException;

	/**
	 * ����	 
	 * @generated
	 * @param addDto
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
   public abstract IDto addInfo(IDto addDto) throws ITFEBizException;

	/**
	 * �޸�	 
	 * @generated
	 * @param modifyDto
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
   public abstract IDto modifyInfo(IDto modifyDto) throws ITFEBizException;

}
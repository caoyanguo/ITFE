package com.cfcc.itfe.service.dataquery.tvincorrhandbookquery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface ITvInCorrhandbookQueryService extends IService {



	/**
	 * ��������ʧ��	 
	 * @generated
	 * @param dtoInfos
	 * @throws ITFEBizException	 
	 */
   public abstract void updateFail(List dtoInfos) throws ITFEBizException;

	/**
	 * �������³ɹ�	 
	 * @generated
	 * @param dtoInfos
	 * @throws ITFEBizException	 
	 */
   public abstract void updateSuccess(List dtoInfos) throws ITFEBizException;

}
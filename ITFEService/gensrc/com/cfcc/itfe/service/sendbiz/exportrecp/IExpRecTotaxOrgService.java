package com.cfcc.itfe.service.sendbiz.exportrecp;

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
 * @time   19-12-08 13:00:37
 * @generated
 * codecomment: 
 */
public interface IExpRecTotaxOrgService extends IService {



	/**
	 * Éú³É»ØÖ´	 
	 * @generated
	 * @param batchNum
	 * @return java.util.ArrayList
	 * @throws ITFEBizException	 
	 */
   public abstract ArrayList exportRece(ArrayList batchNum) throws ITFEBizException;

}
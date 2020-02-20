package com.cfcc.itfe.service.sendbiz.payoutcheckrequest;

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
public interface IPayoutCheckRequestService extends IService {



	/**
	 * 支出核对包重发请求	 
	 * @generated
	 * @param sendorgcode
	 * @param entrustdate
	 * @param oripackmsgno
	 * @param orichkdate
	 * @param oripackno
	 * @param orgtype
	 * @throws ITFEBizException	 
	 */
   public abstract void payoutCheckRequest(String sendorgcode, String entrustdate, String oripackmsgno, String orichkdate, String oripackno, String orgtype) throws ITFEBizException;

}
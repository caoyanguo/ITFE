package com.cfcc.itfe.service.sendbiz.createvoucherforreport;

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
public interface ICreateVoucherForReportService extends IService {



	/**
	 * 生成凭证并发送
	 	 
	 * @generated
	 * @param createVoucherType
	 * @param createSubVoucherType
	 * @param treCode
	 * @param createVoucherDate
	 * @param orgcode
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer createVoucherAndSend(String createVoucherType, String createSubVoucherType, String treCode, String createVoucherDate, String orgcode) throws ITFEBizException;

}
package com.cfcc.itfe.service.recbiz.tfunitrecordmain;

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
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * codecomment: 
 */
public interface ITfUnitrecordmainService extends IService {



	/**
	 * 导出法人代码信息
	 	 
	 * @generated
	 * @param dtoInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String legalPersonCodeExport(IDto dtoInfo) throws ITFEBizException;

}
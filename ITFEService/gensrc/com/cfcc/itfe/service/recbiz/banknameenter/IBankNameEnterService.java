package com.cfcc.itfe.service.recbiz.banknameenter;

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
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IBankNameEnterService extends IService {



	/**
	 * 报文银行对照信息
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Void
	 * @throws ITFEBizException	 
	 */
   public abstract Void save(IDto dto) throws ITFEBizException;

}
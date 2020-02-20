package com.cfcc.itfe.service.para.tscheckfailreason;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITsCheckFailReasonService extends IService {



	/**
	 * ±£´æ	 
	 * @generated
	 * @param dto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String save(TsCheckfailreasonDto dto) throws ITFEBizException;

	/**
	 * É¾³ý	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void delete(TsCheckfailreasonDto dto) throws ITFEBizException;

}
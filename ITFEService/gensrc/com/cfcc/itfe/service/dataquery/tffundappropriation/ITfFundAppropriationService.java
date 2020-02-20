package com.cfcc.itfe.service.dataquery.tffundappropriation;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.common.page.PageRequest;

/**
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * codecomment: 
 */
public interface ITfFundAppropriationService extends IService {



	/**
	 * ≤È—Ø	 
	 * @generated
	 * @param classInfo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findInfo(Class classInfo) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param idto
	 * @param request
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findInfo(IDto idto, PageRequest request) throws ITFEBizException;

}
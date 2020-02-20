package com.cfcc.itfe.service.para.paramtransform;

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

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface IParamTransformService extends IService {



	/**
	 * 参数导出
	 	 
	 * @generated
	 * @param tabs
	 * @param separator
	 * @param orgcode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List export(List tabs, String separator, String orgcode) throws ITFEBizException;

}
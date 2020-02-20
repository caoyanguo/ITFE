package com.cfcc.itfe.service.dataquery.findatastatdownfortj;

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
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IFinDataStatDownForTJService extends IService {



	/**
	 * 生成报表文件
	 	 
	 * @generated
	 * @param idto
	 * @param billTypeList
	 * @param map
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeRptFile(IDto idto, List billTypeList, HashMap map) throws ITFEBizException;

}
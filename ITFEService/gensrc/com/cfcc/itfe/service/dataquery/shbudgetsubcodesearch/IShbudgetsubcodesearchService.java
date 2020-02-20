package com.cfcc.itfe.service.dataquery.shbudgetsubcodesearch;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.ShanghaiReport;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IShbudgetsubcodesearchService extends IService {



	/**
	 * 获取报表数据
	 	 
	 * @generated
	 * @param searchdto
	 * @param bizkind
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getreportdata(ShanghaiReport searchdto, String bizkind) throws ITFEBizException;

}
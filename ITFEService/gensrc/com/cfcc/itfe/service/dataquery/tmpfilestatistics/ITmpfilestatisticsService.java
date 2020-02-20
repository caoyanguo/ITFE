package com.cfcc.itfe.service.dataquery.tmpfilestatistics;

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
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface ITmpfilestatisticsService extends IService {



	/**
	 * 统计电子税票
	 	 
	 * @generated
	 * @param starttime
	 * @param endtime
	 * @param trecode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getlist(String starttime, String endtime, String trecode) throws ITFEBizException;

}
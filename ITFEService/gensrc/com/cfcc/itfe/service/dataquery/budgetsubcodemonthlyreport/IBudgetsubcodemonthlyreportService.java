package com.cfcc.itfe.service.dataquery.budgetsubcodemonthlyreport;

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
public interface IBudgetsubcodemonthlyreportService extends IService {



	/**
	 * 获取报表内容
	 	 
	 * @generated
	 * @param searchdto
	 * @param reporttype
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getReportData(ShanghaiReport searchdto, String reporttype) throws ITFEBizException;

}
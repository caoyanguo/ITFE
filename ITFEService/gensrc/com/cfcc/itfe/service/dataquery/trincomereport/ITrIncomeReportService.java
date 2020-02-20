package com.cfcc.itfe.service.dataquery.trincomereport;

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
public interface ITrIncomeReportService extends IService {



	/**
	 * 收入类报表文件导入
	 	 
	 * @generated
	 * @param fileList
	 * @param dto
	 * @param reportStyle
	 * @throws ITFEBizException	 
	 */
   public abstract void importFile(List fileList, IDto dto, String reportStyle) throws ITFEBizException;

}
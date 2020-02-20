package com.cfcc.itfe.service.dataquery.tvincomedetailreportcheck;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvIncomeDetailReportCheckDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface ITvIncomeDetailReportCheckService extends IService {



	/**
	 * JMethodImpl-a47fb224-3909-4fc3-8cfd-3c84127979ce
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String incomeDetailReportCheck(TvIncomeDetailReportCheckDto dto) throws ITFEBizException;

}
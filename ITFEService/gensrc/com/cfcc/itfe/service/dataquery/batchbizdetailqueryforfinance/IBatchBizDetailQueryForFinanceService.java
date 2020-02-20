package com.cfcc.itfe.service.dataquery.batchbizdetailqueryforfinance;

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
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IBatchBizDetailQueryForFinanceService extends IService {



	/**
	 * 导出文件
	 	 
	 * @generated
	 * @param dtoInfo
	 * @param flag
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportFile(IDto dtoInfo, String flag) throws ITFEBizException;

}
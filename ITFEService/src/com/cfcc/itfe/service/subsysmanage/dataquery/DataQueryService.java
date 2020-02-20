package com.cfcc.itfe.service.subsysmanage.dataquery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.subsysmanage.dataquery.AbstractDataQueryService;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author admin
 * @time   17-02-20 09:29:52
 * codecomment: 
 */

public class DataQueryService extends AbstractDataQueryService {
	private static Log log = LogFactory.getLog(DataQueryService.class);	


	/**
	 * 查询数据	 
	 * @generated
	 * @param sql
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List find(String sql) throws ITFEBizException {
    	SQLExecutor sqlExe = null;
		SQLResults results = null;

		try {
			
			sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			

			results = sqlExe.runQueryCloseCon(sql);
			if (results == null || results.getRowCount() == 0) {
				return null;
			}

			int rowCount = results.getRowCount();
			int colCount = results.getColumnNames().size();
			//
			List<Object> rows = new ArrayList<Object>(rowCount);
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < colCount; j++) {
					rows.add(results.getObject(i, j));
				}
			}

			List<Object> obj = new ArrayList<Object>(3);
			obj.add(results.getColumnNames());
			obj.add(new Integer(results.getRowCount()));
			obj.add(rows);

			return obj;
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException(e.getMessage());
		}
    }
    /**
     * 
     * 调用存储过程
     * 
     */

	public String callProc(String procName, List procParams)
			throws ITFEBizException {
//		String result ="";
//		
//		try{
//			result = TsasProcedureFacade.callProc(procName, procParams, dbName);	
//		} catch (JAFDatabaseException e) {
//			throw new ITFEBizException(e.getMessage());
//		}
//		return result;
		return "";
		
	}

}
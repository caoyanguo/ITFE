package com.cfcc.itfe.facade;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 调用存储过程通用facade
 * 
 * 
 */
public class DBCallProcedureFacade {
	private static Log logs = LogFactory.getLog(DBCallProcedureFacade.class);

	/**
	 * 存储过程调用接口
	 * 
	 * @param proceName
	 * @param params
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static String callProcedure(String db, String proceName, List params)
			throws JAFDatabaseException {
		SQLExecutor sqlexcutor = null;
		if (db.equalsIgnoreCase("ODB")) {
			sqlexcutor = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
		} else if (db.equalsIgnoreCase("QDB")) {
			sqlexcutor = DatabaseFacade.getQDB().getSqlExecutorFactory()
					.getSQLExecutor();
		} else {
			sqlexcutor = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
		}

		if (params != null && params.size() > 0) {
			for (int i = 0; i < params.size(); i++) {
				sqlexcutor.addParam(params.get(i));
			}
		}

		sqlexcutor.addStoredProcOutParam(Integer.class);
		sqlexcutor.addStoredProcOutParam(String.class);
		SQLResults result = sqlexcutor.runStoredProcExecCloseCon(proceName);
		Integer resultCode = (Integer) result.getParam(1);
		String resultText = (String) result.getParam(2);
		if (resultCode != null && (resultCode.intValue() < 0)) {

			logs.error(resultText);
			return resultText;

		} else {
			return resultText;
		}
	}

	/**
	 * 存储过程调用接口
	 * 
	 * @param proceName
	 * @param params
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static void voidCallProcedure(String db, String proceName,
			List params) throws JAFDatabaseException {
		SQLExecutor sqlexcutor = null;
		if (db.equalsIgnoreCase("ODB")) {
			sqlexcutor = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
		} else if (db.equalsIgnoreCase("QDB")) {
			sqlexcutor = DatabaseFacade.getQDB().getSqlExecutorFactory()
					.getSQLExecutor();
		} else {
			sqlexcutor = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
		}

		if (params != null && params.size() > 0) {
			for (int i = 0; i < params.size(); i++) {
				sqlexcutor.addParam(params.get(i));
			}
		}

		sqlexcutor.runStoredProcExecCloseCon(proceName);

	}
}

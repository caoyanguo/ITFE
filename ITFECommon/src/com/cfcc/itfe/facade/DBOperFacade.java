package com.cfcc.itfe.facade;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public class DBOperFacade {
	
	
	public static List<String> lookTable(){
		return lookTable(ITFECommonConstant.DB_TABLE_SCHEMA,null);
	}
	
	/**
	 * 查询带有大对象的数据表,暂时写死
	 * @return
	 */
	public static List<String> lookLobTable(){
		List<String> list = new ArrayList<String>();
		list.add("SIGNSTAMPINFO");
		return list;
	}
	
	/**
	 * 根据模式名获取数据库表名
	 * 
	 * @param schema模式名
	 * @param db
	 *            （1:ODB,2:RDB,3:QDB）
	 * @return
	 */
	public static List<String> lookTable(String schema, String db) {
		List<String> col = new ArrayList<String>();
		String sql = " select  TABLE_NAME  from SYSIBM.tables where TABLE_SCHEMA=? ORDER BY TABLE_NAME";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = selectDB(db);

			sqlExec.addParam(schema.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
			} else {
				for (int i = 0; i < index; i++) {
					String colname = results.getString(i, 0);
					col.add(colname);
				}
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return col;
	}
	
	public static List<String> lookFatherTable(){
		return lookFatherTable(ITFECommonConstant.DB_TABLE_SCHEMA,null);
	}
	
	public static List<String> lookFatherTable(String schema, String db) {
		List<String> col = new ArrayList<String>();
		String sql = " select  PKTABLE_NAME  from SYSIBM.SQLFOREIGNKEYS  where PKTABLE_SCHEM=FKTABLE_SCHEM and PKTABLE_SCHEM=? ORDER BY pktable_name";

		SQLExecutor sqlExec;
		try {
			sqlExec = selectDB(db);
			sqlExec.addParam(schema.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
			} else {
				for (int i = 0; i < index; i++) {
					String colname = results.getString(i, 0);
					col.add(colname);

				}
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return col;
	}
	
	public static List<String> lookSonTable(){
		return lookSonTable(ITFECommonConstant.DB_TABLE_SCHEMA,null);
	}
	
	public static List<String> lookSonTable(String schema, String db) {
		List<String> col = new ArrayList<String>();
		String sql = " select  FKTABLE_NAME  from SYSIBM.SQLFOREIGNKEYS  where PKTABLE_SCHEM=FKTABLE_SCHEM and FKTABLE_SCHEM=? ORDER BY FKTABLE_NAME";

		SQLExecutor sqlExec;
		try {
			sqlExec = selectDB(db);
			sqlExec.addParam(schema.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
			} else {
				for (int i = 0; i < index; i++) {
					String colname = results.getString(i, 0);
					col.add(colname);

				}
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return col;
	}
	
	
	private static SQLExecutor selectDB(String db) throws JAFDatabaseException {
		SQLExecutor sqlExec = null;

		return sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
				.getSQLExecutor();
	}

}

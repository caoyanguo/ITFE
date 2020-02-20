package com.cfcc.itfe.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;


public class DBTools {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(DBTools.class);

	/**
	 * ���ݱ�����ȡ�ֶ��б�
	 * @param table
	 * @param dbType
	 * @return
	 * @throws ITFEBizException
	 */
	public static List<String> lookColumnByTabName(String table) throws ITFEBizException{
		List<String> colums = new ArrayList<String>();
		String sql = "SELECT  A.COLNAME FROM SYSCAT.COLUMNS A  WHERE A.TABNAME=?  ORDER BY A.COLNO ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();

			sqlExec.addParam(table);
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			for (int i = 0; i < index; i++) {
				String colname = results.getString(i, 0);
				colums.add(colname);
			}
		} catch (JAFDatabaseException e) {
			String error = "���ݱ�����ȡ�ֶ��б������ݿ��쳣!";
			logger.error(error);
			throw new ITFEBizException(error, e);
		}

		return colums;
	}

	/**
	 * ���ݱ����õ�����ֶ�
	 * @param tabName
	 * @return
	 * @throws ITFEBizException 
	 */
	public static Map getColumByTable(String tabName) throws ITFEBizException { 
		Map<String, String> map = new HashMap<String, String>();
		List<String> l = lookColumnByTabName(tabName);
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < l.size(); i++) {
			String col = l.get(i);
			b.append(col);
			if (i != l.size() - 1) {
				b.append(",");
			}

		}
		map.put(tabName, b.toString());
		return map;
	}
	
	/**
	 * ���ݱ����õ�����ֶ�
	 * @param tabName
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String findColumByTable(String tabName) throws ITFEBizException { 
		List<String> l = lookColumnByTabName(tabName);
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < l.size(); i++) {
			String col = l.get(i);
			b.append(col);
			if (i != l.size() - 1) {
				b.append(",");
			}

		}
		return b.toString();
	}
	
	/**
	 * ����ģʽ����ȡ���ݿ���� (��ȥ��ʱ��Ч�ı�)
	 * @param schema
	 * @param dbType
	 * @return
	 * @throws ITFEBizException
	 */
	public static List<String> lookTable(String schema) throws ITFEBizException {
		List<String> col = new ArrayList<String>();
		String sql = " select  TABLE_NAME  from SYSIBM.tables where TABLE_SCHEMA=? ORDER BY TABLE_NAME";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();

			sqlExec.addParam(schema);
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			for (int i = 0; i < index; i++) {
				String colname = results.getString(i, 0);
				
				col.add(colname);
			}
		} catch (JAFDatabaseException e) {
			String error = "���ݱ�����ȡ�ֶ��б������ݿ��쳣!";
			logger.error(error);
			throw new ITFEBizException(error, e);
		}
		return col;
	}
	

	/**
	 * ����ģʽ����ȡ���ݿ���� (��ȥ��ʱ��Ч�ı�)
	 * @param schema
	 * @param dbType
	 * @return
	 * @throws ITFEBizException
	 */
	public static List<String> lookTableWithAll(String schema) throws ITFEBizException {
		List<String> col = new ArrayList<String>();
		String sql = " select  TABLE_NAME  from SYSIBM.tables where TABLE_SCHEMA=? ORDER BY TABLE_NAME";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();

			sqlExec.addParam(schema);
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			for (int i = 0; i < index; i++) {
				String colname = results.getString(i, 0);

				col.add(colname);

			}
		} catch (JAFDatabaseException e) {
			String error = "���ݱ�����ȡ�ֶ��б������ݿ��쳣!";
			logger.error(error);
			throw new ITFEBizException(error, e);
		}
		return col;
	}
	
	/**
	 * ����ģʽ��ȡ��������ϵ�ı���
	 * 
	 * @param schema
	 * @return
	 * @throws ITFEBizException 
	 */
	public static List<String> lookReferenceTable(String schema) throws ITFEBizException {
		List<String> col = new ArrayList<String>();
		String sql = " select  pktable_name,fktable_name,PKTABLE_CAT  from SYSIBM.SQLFOREIGNKEYS where FKTABLE_SCHEM=? ORDER BY pktable_name";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();

			sqlExec.addParam(schema);
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// buf.append(table + "\n");
			} else {
				for (int i = 0; i < index; i++) {
					String father = results.getString(i, 0);
					String son = results.getString(i, 1);

					col.add(father + "|" + son);

				}
			}
		} catch (JAFDatabaseException e) {
			String error = "����ģʽ��ȡ��������ϵ�ı���,�������ݿ��쳣!";
			logger.error(error);
			throw new ITFEBizException(error, e);
		}
		return col;
	}

	public static String tableToDto(String tabName) {
		tabName = tabName.toLowerCase();
		StringBuffer buf = new StringBuffer();
		String tmp = (tabName.substring(0, 1)).toUpperCase();
		buf.append(tmp);
		for (int i = 1; i < tabName.length(); i++) {
			String temp = tabName.substring(i, i + 1);
			if (temp.equalsIgnoreCase("_")) {
				i++;
				temp = tabName.substring(i, i + 1).toUpperCase();

			}
			buf.append(temp);
		}
		buf.append("Dto");
		return buf.toString();
	}

	public static String tableToTAB(String tabName) {
		tabName = tabName.toLowerCase();
		StringBuffer buf = new StringBuffer();
		String tmp = (tabName.substring(0, 1)).toUpperCase();
		buf.append(tmp);
		for (int i = 1; i < tabName.length(); i++) {
			String temp = tabName.substring(i, i + 1);
			if (temp.equalsIgnoreCase("_")) {
				i++;
				temp = tabName.substring(i, i + 1).toUpperCase();

			}
			buf.append(temp);
		}

		return buf.toString();
	}

	/**
	 * ��ѯ�����
	 * 
	 * @param tabName
	 *            ������
	 * @param schema
	 *            ģʽ��
	 * @param keyType
	 *            (P,F) ������
	 * @return
	 * @throws ITFEBizException 
	 */
	public static List<String> lookKeys(String tabName, String schema) throws ITFEBizException {
		List<String> col = new ArrayList<String>();
		String sql = " select  constname  from SYSCAT.TABCONST where (type=? or type=?) and tabschema=? and tabname=?";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();

			sqlExec.addParam("P");
			sqlExec.addParam("F");
			sqlExec.addParam(schema);
			sqlExec.addParam(tabName);
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// buf.append(table + "\n");
			} else {
				for (int i = 0; i < index; i++) {
					String key = results.getString(i, 0);
					col.add(key);

				}
			}
		} catch (JAFDatabaseException e) {
			String error = "��ѯ������������ݿ��쳣!";
			logger.error(error);
			throw new ITFEBizException(error, e);
		}
		return col;

	}

	/**
	 * ���ݱ����õ�����
	 */
	public static String getPKey(String tabName, String colums) throws ITFEBizException {
		List<Object> keyList = new ArrayList<Object>();
		StringBuffer buffer = new StringBuffer();
		String[] strColum = colums.split(",");
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			for (int i = 0; i < strColum.length; i++) {
				String col = strColum[i];
				String sql = "SELECT  B.CONSTNAME  FROM SYSCAT.KEYCOLUSE B JOIN SYSCAT.TABCONST C  ON (B.TABNAME = C.TABNAME AND B.CONSTNAME = C.CONSTNAME AND C.TYPE = ?) WHERE B.TABNAME=?  AND B.COLNAME = ? ";
				sqlExec.addParam("P");
				sqlExec.addParam(tabName);
				sqlExec.addParam(col);
				SQLResults results = sqlExec.runQuery(sql);
				if(results!=null && results.getDtoCollection().size()>0){
					keyList.add(col);
				}

			}
		} catch (JAFDatabaseException e1) {
			String error = "���ݱ����õ������������ݿ��쳣!";
			logger.error(error);
			throw new ITFEBizException(error, e1);
			
		}finally{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}

		for (int i = 0; i < keyList.size(); i++) {
			buffer.append(keyList.get(i));
			if (i != keyList.size() - 1) {
				buffer.append(",");
			}
		}

		return buffer.toString();
	}
	
	/**
	 * ���ݱ����õ�����
	 * @param tabName
	 * @param schema
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String lookPKeys(String tabName) throws ITFEBizException {	
		StringBuffer b = new StringBuffer();
		String sql = "select a.colname from SYSIBM.SYSKEYCOLUSE a,SYSCAT.TABCONST b where a.TBNAME=b.TABNAME  and b.tabname = ? and b.type=? ";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(tabName);
			sqlExec.addParam("P");
			
			SQLResults results = sqlExec.runQueryCloseCon(sql);
			int index = results.getRowCount();
			if (index <= 0) {				
			} else {
				for (int i = 0; i < index; i++) {
					String key = results.getString(i, 0);
					b.append(key).append(",");

				}
			}
		} catch (JAFDatabaseException e) {
			String error = "���ݱ������������������ݿ��쳣!";
			logger.error(error);
			throw new ITFEBizException(error, e);
		}
		String str = b.toString();
		str = str.substring(0, str.length()-1);
		return str;
		
	}

	public static List<String> lookFKeys(String tabName, String schema) throws ITFEBizException {
		;
		List<String> col = new ArrayList<String>();
		String sql = " select  constname  from SYSCAT.TABCONST where type=?  and tabschema=? and tabname=?";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();

			sqlExec.addParam("F");
			sqlExec.addParam(schema);
			sqlExec.addParam(tabName);
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// buf.append(table + "\n");
			} else {
				for (int i = 0; i < index; i++) {
					String key = results.getString(i, 0);

					col.add(key);

				}
			}
		} catch (JAFDatabaseException e) {
			String error = "���ݱ�����������������ݿ��쳣!";
			logger.error(error);
			throw new ITFEBizException(error, e); 
		}
		return col;
	}
}


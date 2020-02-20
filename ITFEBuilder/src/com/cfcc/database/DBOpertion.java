package com.cfcc.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.itfe.facade.DatabaseFacade;



public class DBOpertion {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	/**
	 * 根据表名获取字段列表
	 * 
	 * @param table
	 * @param db
	 *            1：odb 2：rdb 3：qdb
	 * @return
	 */
	public static List<String> lookColumnByTabName(String table, String db) {

		List<String> col = new ArrayList<String>();
		String sql = " select distinct NAME  from SYSIBM.SYSCOLUMNS where tbname=?  order by name";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = selectDB(db);

			sqlExec.addParam(table.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
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

	/**
	 * 根据表名和需要过滤的字段获取字段列表
	 * 
	 * @param table
	 * @param db
	 *            1：odb 2：rdb 3：qdb
	 * @return
	 */
	public static List<String> lookColumnByTabNameWithFilter(String table,
			HashMap<String, String> filterMap) {

		List<String> col = new ArrayList<String>();
		String sql = " select distinct NAME  from SYSIBM.SYSCOLUMNS where tbname=?  order by name";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = selectDB(null);

			sqlExec.addParam(table.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
			} else {
				for (int i = 0; i < index; i++) {
					String colname = results.getString(i, 0);

					if (!filterMap.containsKey(colname)) {
						col.add(colname);
					}
				}
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return col;
	}

	/**
	 * 根据表名获取字段结构
	 * 
	 * @param table
	 * @param db
	 *            1：odb 2：rdb 3：qdb
	 * @return
	 */
	public static List<ColumnData> lookColumnDataByTabName(String table,
			String db) {

		List<ColumnData> listTableData = new ArrayList<ColumnData>();
		String sql = " select distinct NAME,COLTYPE,LENGTH,NULLS,COLNO,REMARKS  from SYSIBM.SYSCOLUMNS where tbname=?  order by COLNO";
		SQLExecutor sqlExec = null;
		try {

			sqlExec = selectDB(db);

			sqlExec.addParam(table.toUpperCase());
			// sqlExec.addParam("db2admin".toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
			} else {
				for (int i = 0; i < index; i++) {
					ColumnData tabdata = new ColumnData();
					String colName = results.getString(i, 0);
					String colType = results.getString(i, 1);
					int colLength = results.getInt(i, 2);
					String isnull = results.getString(i, 3);
					String cname = results.getString(i, 5);
					if (isnull.equalsIgnoreCase("N")) {
						tabdata.setIsnull(false);
					} else {
						tabdata.setIsnull(true);
					}
					tabdata.setColLeng(colLength);
					tabdata.setColid(colName);
					tabdata.setColType(colType);
					tabdata.setColname(cname);
					listTableData.add(tabdata);

				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}

		return listTableData;
	}
	public static List<ColumnData> lookColumnDataByTabNameWithFilter(String table,
			String db,HashMap<String, String> filterMap) {

		List<ColumnData> listTableData = new ArrayList<ColumnData>();
		String sql = " select distinct NAME,COLTYPE,LENGTH,NULLS,COLNO,REMARKS  from SYSIBM.SYSCOLUMNS where tbname=?  order by COLNO";
		SQLExecutor sqlExec = null;
		try {

			sqlExec = selectDB(db);

			sqlExec.addParam(table.toUpperCase());
			// sqlExec.addParam("db2admin".toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
			} else {
				for (int i = 0; i < index; i++) {
					ColumnData tabdata = new ColumnData();
					String colName = results.getString(i, 0);
					String colType = results.getString(i, 1);
					int colLength = results.getInt(i, 2);
					String isnull = results.getString(i, 3);
					String cname = results.getString(i, 5);
					if (isnull.equalsIgnoreCase("N")) {
						tabdata.setIsnull(false);
					} else {
						tabdata.setIsnull(true);
					}
					tabdata.setColLeng(colLength);
					tabdata.setColid(colName);
					tabdata.setColType(colType);
					tabdata.setColname(cname);
					if (!filterMap.containsKey(colName)) {
						listTableData.add(tabdata);
					}
					

				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}

		return listTableData;
	}
	/**
	 * 根据表名获取字段结构并转换成dto的形式
	 * 
	 * 
	 * @param table
	 * @param db
	 *            1：odb 2：rdb 3：qdb
	 * @return
	 */
	public static List<ColumnData> lookColumnDataByTabNameToLow(String table,
			String db) {
		List<ColumnData> list = lookColumnDataByTabName(table, db);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setColid(
					list.get(i).getColid().replaceAll("_", "").toLowerCase());
		}
		return list;

	}

	public static boolean lookColumnIsExist(String table, String db,
			String colName) {

		String sql = " select COUNT(1)  from SYSIBM.SYSCOLUMNS where tbname=?  and TBCREATOR=? and NAME=?";
		SQLExecutor sqlExec = null;
		try {

			sqlExec = selectDB(db);

			sqlExec.addParam(table.toUpperCase());
			sqlExec.addParam("db2admin".toUpperCase());
			sqlExec.addParam(colName.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getInt(0, 0);
			if (index <= 0) {
				return false;
			} else {
				return true;
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 根据表名获取字段结构
	 * 
	 * @param table
	 * @param db
	 *            1：odb 2：rdb 3：qdb
	 * @return
	 */
	public static HashMap<String, TableData> lookColumnDataByTabNameForMap(
			String table, String db) {

		HashMap<String, TableData> listTableData = new HashMap<String, TableData>();
		String sql = " select distinct NAME,COLTYPE,LENGTH,NULLS,COLNO  from SYSIBM.SYSCOLUMNS where tbname=?  and TBCREATOR=? order by COLNO";
		SQLExecutor sqlExec = null;
		try {

			sqlExec = selectDB(db);

			sqlExec.addParam(table.toUpperCase());
			sqlExec.addParam("db2admin".toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
			} else {
				for (int i = 0; i < index; i++) {
					TableData tabdata = new TableData();
					String colName = results.getString(i, 0);
					String colType = results.getString(i, 1);
					int colLength = results.getInt(i, 2);
					String isnull = results.getString(i, 3);
					if (isnull.equalsIgnoreCase("N")) {
						tabdata.setNull(false);
					} else {
						tabdata.setNull(true);
					}
					tabdata.setColLength(colLength);
					tabdata.setColName(colName);
					tabdata.setColType(colType);
					listTableData.put(colName, tabdata);

				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}

		return listTableData;
	}

	public static List<String> lookColumTypeByTabName(String table, String db) {

		List<String> listTableData = new ArrayList<String>();
		String sql = " select distinct COLTYPE from SYSIBM.SYSCOLUMNS where tbname=?   ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = selectDB(db);

			sqlExec.addParam(table.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
			} else {
				for (int i = 0; i < index; i++) {

					String colType = results.getString(i, 0);

					listTableData.add(colType);

				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}

		return listTableData;
	}

	public static HashMap<String, String> lookColumnByTabNameWithHashMap(
			String table, String db) {

		HashMap<String, String> col = new HashMap<String, String>();
		String sql = " select distinct NAME  from SYSIBM.SYSCOLUMNS where tbname=?  order by name";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = selectDB(db);

			sqlExec.addParam(table.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
			} else {
				for (int i = 0; i < index; i++) {
					String colname = results.getString(i, 0);
					col.put(colname, colname);

				}
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return col;
	}

	
	public static HashMap<String, String> lookColumnDataByTabNameWithHashMap(
			String table, String db) {

		HashMap<String, String> col = new HashMap<String, String>();
		String sql = " select distinct NAME ,REMARKS from SYSIBM.SYSCOLUMNS where tbname=?  order by name";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = selectDB(db);

			sqlExec.addParam(table.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
			} else {
				for (int i = 0; i < index; i++) {
					String colname = results.getString(i, 0).toLowerCase().replaceAll("_","");
					String colCname = results.getString(i, 1);
					col.put(colname, colCname);

				}
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return col;
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

	/**
	 * 根据模式获取有依赖关系的表名
	 * 
	 * @param schema
	 * @param db
	 *            1，odb，3，qdb，2 timsdb
	 * @return
	 */
	public static HashMap<String, List<String>> lookReferenceTable(
			String schema, String db) {
		String sql = " select  pktable_name,fktable_name,PKTABLE_SCHEM,FKTABLE_SCHEM  from SYSIBM.SQLFOREIGNKEYS  where PKTABLE_SCHEM=FKTABLE_SCHEM and PKTABLE_SCHEM=? ORDER BY pktable_name";

		HashMap<String, List<String>> referenceMap = new HashMap<String, List<String>>();

		SQLExecutor sqlExec;
		try {
			sqlExec = selectDB(db);

			sqlExec.addParam(schema.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
				// buf.append(table + "\n");
			} else {
				for (int i = 0; i < index; i++) {

					String father = results.getString(i, 0);
					String son = results.getString(i, 1);
					if (referenceMap.containsKey(father)) {
						referenceMap.get(father).add(son);
					} else {
						List<String> sonList = new ArrayList<String>();
						sonList.add(son);
						referenceMap.put(father, sonList);
					}

					// String fatherSchema = results.getString(i, 2);
					// String sonSchema = results.getString(i, 3);
					// col.add(father + "|" + son);
					// System.out.println();

				}
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return referenceMap;
	}

	public static String lookFatherTable(String schema, String db,
			String sonTable) {
		String sql = " select  PKTABLE_NAME  from SYSIBM.SQLFOREIGNKEYS  where FKTABLE_NAME=? and PKTABLE_SCHEM=FKTABLE_SCHEM and PKTABLE_SCHEM=? ORDER BY pktable_name";

		// HashMap<String, List<String>> referenceMap = new HashMap<String,
		// List<String>>();

		SQLExecutor sqlExec;
		try {
			sqlExec = selectDB(db);
			sqlExec.addParam(sonTable.toUpperCase());
			sqlExec.addParam(schema.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
				// buf.append(table + "\n");
			} else {
				// for (int i = 0; i < index; i++) {

				return results.getString(0, 0);

			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "XXXXXXXXXXXXXXXXXXXXXXXX";
	}

	public static void fileSaveZero() {
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File("c:/tmp.tmp"));
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out = null;
			}
		}
	}

	/**
	 * 根据表名转换成DTO
	 * 
	 * @param tabName
	 * @return
	 */
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
	 * 查询主外键
	 * 
	 * @param tabName
	 *            表名称
	 * @param schema
	 *            模式名
	 * @param keyType
	 *            (P,F) 键类型
	 * @return
	 */
	public static List<String> lookKeys(String tabName, String schema, String db) {
		List<String> col = new ArrayList<String>();
		String sql = " select  constname  from SYSCAT.TABCONST where (type=? or type=?) and tabschema=? and tabname=?";
		SQLExecutor sqlExec;
		try {
			sqlExec = selectDB(db);

			sqlExec.addParam("P");
			sqlExec.addParam("F");
			sqlExec.addParam(schema.toUpperCase());
			sqlExec.addParam(tabName.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
				// buf.append(table + "\n");
			} else {
				for (int i = 0; i < index; i++) {
					String key = results.getString(i, 0);

					col.add(key);

				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}
		return col;

	}

	public static List<String> lookPKeys(String tabName, String schema,
			String db) {
		;
		List<String> col = new ArrayList<String>();
		String sql = " select  constname  from SYSCAT.TABCONST where type=?  and tabschema=? and tabname=?";
		SQLExecutor sqlExec;
		try {
			sqlExec = selectDB(db);

			sqlExec.addParam("P");
			sqlExec.addParam(schema.toUpperCase());
			sqlExec.addParam(tabName.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
				// buf.append(table + "\n");
			} else {
				for (int i = 0; i < index; i++) {
					String key = results.getString(i, 0);

					col.add(key);

				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}
		return col;

	}

	/**
	 * 根据表名称查找对应的主键字段
	 * 
	 * @param tabName
	 * @return
	 */
	public static List<String> lookPKToColName(String tabName, String db) {
		List<String> col = new ArrayList<String>();
		String sql = "select a.colname from SYSIBM.SYSKEYCOLUSE a,SYSCAT.TABCONST b where a.TBNAME=b.TABNAME  and b.tabname = ? and b.type=? ";
		SQLExecutor sqlExec;
		try {
			sqlExec = selectDB(db);
			sqlExec.addParam(tabName.toUpperCase());
			sqlExec.addParam("P");

			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {

			} else {
				for (int i = 0; i < index; i++) {
					String key = results.getString(i, 0);

					col.add(key);

				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}
		return col;

	}

	/**
	 * 根据表名称查找对应的主键字段
	 * 
	 * @param tabName
	 * @return
	 */
	public static HashMap<String, String> lookPKToMapName(String tabName,
			String db) {
		HashMap<String, String> col = new HashMap<String, String>();
		String sql = "select a.colname from SYSIBM.SYSKEYCOLUSE a,SYSCAT.TABCONST b where a.TBNAME=b.TABNAME  and b.tabname = ? and b.type=? ";
		SQLExecutor sqlExec;
		try {
			sqlExec = selectDB(db);
			sqlExec.addParam(tabName.toUpperCase());
			sqlExec.addParam("P");

			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {

			} else {
				for (int i = 0; i < index; i++) {
					String key = results.getString(i, 0);

					col.put(key, key);

				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}
		return col;

	}

	/**
	 * @param tabName
	 * @param schema
	 * @return
	 */
	public static List<String> lookFKeys(String tabName, String schema) {
		;
		List<String> col = new ArrayList<String>();
		String sql = " select  constname  from SYSCAT.TABCONST where type=?  and tabschema=? and tabname=?";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			sqlExec.addParam("F");
			sqlExec.addParam(schema.toUpperCase());
			sqlExec.addParam(tabName.toUpperCase());
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
				// buf.append(table + "\n");
			} else {
				for (int i = 0; i < index; i++) {
					String key = results.getString(i, 0);

					col.add(key);

				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}
		return col;

	}

	// public static Hashtable getValuesBySQL(String sql) {
	// Hashtable table = new Hashtable();
	// PreparedStatement pstmt = null;
	// ResultSet rs = null;
	// ResultSetMetaData rsmd = null;
	// Connection cn = null;
	// // final String prefixPackage = "com.guoan.cps.data.";
	//
	// try {
	// cn = DBConnect.getConnect();
	// pstmt = cn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
	// ResultSet.CONCUR_READ_ONLY);
	// rs = pstmt.executeQuery();
	// rsmd = rs.getMetaData();
	// int columNumber = rsmd.getColumnCount();
	// if (rs.next()) {
	// for (int i = 1; i <= columNumber; i++) {
	// int fieldType = rsmd.getColumnType(i);
	// String fieldName = rsmd.getColumnName(i);
	// Object fieldValue = null;
	// boolean isBlob = false;
	// switch (fieldType) {
	// case Types.DATE:
	// fieldValue = rs.getDate(fieldName);
	// break;
	// case Types.INTEGER:
	// case Types.SMALLINT:
	// fieldValue = new Integer(rs.getInt(fieldName));
	// break;
	// case Types.CHAR:
	// case Types.VARCHAR:
	// fieldValue = Tool.stringTrim(rs.getString(fieldName));
	// break;
	// case Types.DECIMAL:
	// // fieldValue = rs.getBigDecimal(fieldName);
	// fieldValue = rs.getBigDecimal(fieldName);
	// // wangwz 2005-1-21
	// if (fieldValue != null) {
	// String tmp = Tool.trimTailZero(fieldValue
	// .toString());
	// fieldValue = new BigDecimal(tmp);
	// }
	// break;
	// case Types.BLOB:
	// fieldValue = rs.getBlob(fieldName);
	// isBlob = true;
	// break;
	// default:
	// // NO DEFINED DATA TYPE
	// throw new Exception("数据字段类型未处理,需要添加处理:" + fieldType);
	// }
	// if (fieldValue != null) {
	// if (isBlob) {
	// Blob blobField = (Blob) fieldValue;
	// fieldValue = blobField.getBytes(1, (int) blobField
	// .length());
	// }
	// table.put(fieldName, fieldValue);
	// }
	// }
	// }
	// } catch (SQLException se) {
	// se.printStackTrace();
	// } catch (ClassNotFoundException ce) {
	// ce.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// // closeConnection(cn, stmt, rs);
	// DBConnect.closeConnect(cn, pstmt, rs);
	// }
	// return table;
	// }

	public static void saveFile(String str, String fileName) {
		File f = new File(fileName);
		OutputStream out = null;
		try {
			out = new FileOutputStream(f);
			out.write(str.getBytes());
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out = null;
		}

	}

	private static SQLExecutor selectDB(String db) throws JAFDatabaseException {
		SQLExecutor sqlExec = null;

		return sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();
	}

	public static List<String> commonSelect(String colName, String tableName,
			String db) {
		String sql = "select " + colName + " from " + tableName;
		List<String> values = new ArrayList<String>();

		SQLExecutor sqlExec;
		try {
			sqlExec = selectDB(db);
			SQLResults results = sqlExec.runQueryCloseCon(sql);

			int index = results.getRowCount();
			if (index <= 0) {
				// System.out.println("没有定义的表="+table);
				// buf.append(table + "\n");
			} else {
				for (int i = 0; i < index; i++) {
					String key = results.getString(i, 0);

					values.add(key);

				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}
		return values;
	}

	/**
	 * 类型转换
	 * 
	 * @param colName
	 * @param typeName
	 * @param length
	 * @return
	 */
	public static String typeConvert(String colName, String typeName, int length) {
		if (colName.equalsIgnoreCase("Ienrolsrlno")) {
			return "_ienrolsrlno";
		} else if (colName.equalsIgnoreCase("s_TraState")) {
			return "\"90\"";
		} else if (colName.equalsIgnoreCase("cProcState")) {
			return "\"90\"";

		} else if (colName.equalsIgnoreCase("Ibatchnum")) {
			return "new Long(_ienrolsrlno).intValue()";

		} else if (typeName.equalsIgnoreCase("CHAR")
				|| typeName.equalsIgnoreCase("VARCHAR")) {

			return strfill(length);
		} else if (typeName.equalsIgnoreCase("DATE")) {
			return "currentDate";
		} else if (typeName.equalsIgnoreCase("DECIMAL")) {
			return "new BigDecimal(\"0.00\")";
		} else {
			return "new Integer(1)";
		}

	}

	private static String strfill(int length) {
		String tmp = "";
		if (length > 20) {
			return "\"A\"";
		}
		for (int i = 0; i < length; i++) {
			tmp = tmp + "A";
		}
		return "\"" + tmp + "\"";
	}

	public static String typeConvertPro(String colName, String typeName,
			int length) {
		if (colName.equalsIgnoreCase("s_TraState")) {
			return "'''90'''";
		} else if (colName.equalsIgnoreCase("c_OperateFlag")) {
			return "'''1'''";

		} else if (colName.equalsIgnoreCase("s_PkgState")) {
			return "'''99'''";
		}

		else if (colName.equalsIgnoreCase("c_ProcState")) {
			return "'''90'''";

		} else if (colName.equalsIgnoreCase("C_TRIMFLAG")) {
			return "'''0'''";
		} else if (typeName.equalsIgnoreCase("CHAR")
				|| typeName.equalsIgnoreCase("VARCHAR")) {

			return "''" + strfillSQL(length) + "''";
		} else if (colName.equalsIgnoreCase("ts_Alter")) {
			return "'CURRENT TIMESTAMP'";
		} else if (colName.equalsIgnoreCase("TS_SYSUPDATE")) {
			return "'CURRENT TIMESTAMP'";
		} else if (typeName.equalsIgnoreCase("DATE")) {
			return "'''2001-12-10'''";
		} else if (typeName.equalsIgnoreCase("time")) {
			return "'current time'";
		} else if (typeName.equalsIgnoreCase("TIMESTAMP")) {
			return "'CURRENT TIMESTAMP'";
		} else if (typeName.equalsIgnoreCase("DECIMAL")) {
			return "'1.00'";
		} else {
			return "'1'";
		}

	}

	/**
	 * 类型转换
	 * 
	 * @param colName
	 * @param typeName
	 * @param length
	 * @return
	 */
	public static String typeConvertSQL(String colName, String typeName,
			int length) {
		if (colName.equalsIgnoreCase("sTraState")) {
			return "'90'";
		} else if (colName.equalsIgnoreCase("cProcState")) {
			return "'90'";

		} else if (typeName.equalsIgnoreCase("CHAR")
				|| typeName.equalsIgnoreCase("VARCHAR")) {

			return strfillSQL(length);
		} else if (colName.equalsIgnoreCase("ts_Alter")) {
			return "CURRENT TIMESTAMP";
		} else if (colName.equalsIgnoreCase("TS_SYSUPDATE")) {
			return "CURRENT TIMESTAMP";
		} else if (typeName.equalsIgnoreCase("DATE")) {
			return "current date";
		} else if (typeName.equalsIgnoreCase("time")) {
			return "current time";
		} else if (typeName.equalsIgnoreCase("TIMESTAMP")) {
			return "CURRENT TIMESTAMP";
		} else if (typeName.equalsIgnoreCase("DECIMAL")) {
			return "0.00";
		} else {
			return "1";
		}

	}

	private static String strfillSQL(int length) {
		String tmp = "";
		if (length > 40) {
			return "'代码长度>40位测试代码测试代码测试代码'";
		}
		for (int i = 0; i < length; i++) {
			tmp = tmp + "A";
		}
		return "'" + tmp + "'";
	}

	public static double logarithm(double value, double base) {
		return Math.log(value) / Math.log(base);
	}

	public static String getColumnsToStr(String tableName, String db) {
		List<ColumnData> columns = lookColumnDataByTabName(tableName, db);
		String columnStr = "";
		for (int i = 0; i < columns.size(); i++) {
			columnStr = columnStr + columns.get(i).getColid() + ",";
		}
		if (columnStr != null && columnStr.trim().length() > 1) {
			return columnStr.substring(0, columnStr.length() - 1);
		} else {
			return "";
		}
	}

	public static HashMap<String, String> filterColumns() {
		HashMap<String, String> filter = new HashMap<String, String>();
		filter.put("C_AUDITSTATE", "C_AUDITSTATE");
		filter.put("S_AUDITRESULT", "S_AUDITRESULT");
		filter.put("TS_SYSUPDATE", "TS_SYSUPDATE");
		return filter;
	}
}

package com.cfcc.itfe.facade;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.persistence.dao.exception.JAFDB2Exception;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.PatternConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.SQLMapping;
import com.cfcc.itfe.facade.data.SQLParam;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.MatcherUtil;

public class DBOperUtil {
	private static Log logs = LogFactory.getLog(DBOperUtil.class);
	
	public static final String sqlLineEndString = ";\n";
	/**
	 * 正确的export sql码
	 */
	public static List<String> rightExportSqlCodeList;
	
	static{
		rightExportSqlCodeList = new ArrayList<String>();
		rightExportSqlCodeList.add("SQL3104N");
		rightExportSqlCodeList.add("SQL3105N");
		rightExportSqlCodeList.add("SQL3100W");
	}


	public static void exec(List<SQLMapping> dbSQLs, SQLExecutor exec,
			List<Object> params) throws JAFDatabaseException {

		// if (dbSQLs != null && dbSQLs.size() > 0) {
		//			
		//			
		// if (operType.equalsIgnoreCase(BatchConstants.INSERT_OPER)) {
		// dbOP(dbSQLs, srcSchema, tarSchema, currentDate, operGroup, db);
		// } else if (operType.equalsIgnoreCase(BatchConstants.DELETE_OPER)) {
		// dbOP(dbSQLs, srcSchema, null, currentDate, operGroup, db);
		// } else if (operType.equalsIgnoreCase(BatchConstants.LOAD_OPER)) {
		// callShellOP(dbSQLs, srcSchema, tarSchema, operGroup,
		// currentDate, db);
		// } else if (operType.equalsIgnoreCase(BatchConstants.IMPORT_OPER)) {
		// callShellOP(dbSQLs, srcSchema, tarSchema, operGroup,
		// currentDate, db);
		// } else if (operType.equalsIgnoreCase(BatchConstants.DETACH_OPER)) {
		// callDetach(dbSQLs, srcSchema, tarSchema, operGroup,
		// currentDate, db);
		// } else if (operType.equalsIgnoreCase(BatchConstants.BACKUP_OPER)) {
		// callShellBackup(dbSQLs, srcSchema, tarSchema, operGroup,
		// currentDate, db);
		// }
		// }
		dbOP(dbSQLs, exec, params);
	}

	/**
	 * 采用调用shell方式进行数据库操作
	 * 
	 * @param loadCursorList
	 * @param srcSchema
	 * @param operGroup
	 * @throws JAFDatabaseException
	 * @throws JAFDatabaseException
	 */
	private static void callShellOP(List<SQLMapping> dbSQLs)
			throws JAFDatabaseException {
		if (dbSQLs != null && dbSQLs.size() > 0) {
			StringBuffer sqlBuf = new StringBuffer();
			// 选择需要连接的数据库和用户名密码
			String connectdb = "";
			String srcSchema = "";
			String password = "";
			sqlBuf.append("connect to " + connectdb + " user " + srcSchema
					+ " using " + password + " ; \n");

			for (int i = 0; i < dbSQLs.size(); i++) {
				sqlBuf.append(dbSQLs.get(i) + ";\n");
			}
			sqlBuf.append("connect reset;");
		}
		// if (dbSQLs != null && dbSQLs.size() > 0) {
		// StringBuffer sqlBuf = new StringBuffer();
		// // 获取操作系统标识
		// boolean flagSys = SystemConfig.getInstance().isWindowsOS();
		//
		// // 选择需要连接的数据库和用户名密码
		// String connectdb = null;
		// String password = null;
		//
		// //
		// sqlBuf.append("connect to " + connectdb + " user " + srcSchema
		// + " using " + password + " ; \n");
		//
		// for (int i = 0; i < dbSQLs.size(); i++) {
		// String operSql = dbSQLs.get(i).getOperSql();
		//
		// sqlBuf.append(operSql);
		// sqlBuf.append(";\n");
		// }
		//
		// sqlBuf.append("connect reset;");
		//
		// // SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
		// // long l = System.currentTimeMillis();
		// // String fileName = sdf.format(new Date(l)) + "." + operGroup +
		// // ".del";
		//
		// logs.debug("批处理文件:" + fileName);
		//
		// // 调用Shell
		// CallShellResutlData shellData = callShell(fileName, flagSys, null);
		// List<SQLResultData> shellResult = shellData.getISQLResultData();
		// String shellStr = shellData.getShellStr();
		//
		// // 更新任务列表(理论上讲调用shell返回的结果和需要调sh的结果数目相等)
		// if (shellResult != null && shellResult.size() > 0
		// && loadCursorList.size() == shellResult.size()) {
		// // 组织需要更新数据库整理任务跟踪信息
		// List<TmDatasettletaskTraceDto> taskTrace = new
		// ArrayList<TmDatasettletaskTraceDto>();
		//
		// StringBuffer buff = new StringBuffer();
		//
		// //
		// for (int i = 0; i < shellResult.size(); i++) {
		// int rejectedNum = shellResult.get(i).getIRejected();
		// String operID = loadCursorList.get(i).getJobId();
		// if (rejectedNum == 0) {
		// taskTrace.add(genDto(operID, operGroup, jobDate));
		// } else {
		// buff.append(operID + ", ");
		// }
		// }
		//
		// if (taskTrace != null && taskTrace.size() > 0) {
		// // 更新数据库整理任务跟踪信息
		// DatabaseFacade
		// .getODB()
		// .update(
		// taskTrace
		// .toArray(new TmDatasettletaskTraceDto[taskTrace
		// .size()]));
		// }
		//
		// if (buff.length() > 0) {
		// buff.insert(0, "存在操作失败的数据,任务ID = " + operGroup
		// + ",操作 ID = ");
		// buff.append(" 操作日期 = " + jobDate);
		// throw new AsynJobException(buff.toString());
		// }
		// } else {
		// logs.error("返回的结果集与调用Shell数量不一致,调用Shell失败,Shell调用结果＝"
		// + shellStr);
		// throw new AsynJobException("调用Shell失败！！！！！");
		// }
		// }

	}

	private static void callShellBackup(List<SQLMapping> loadCursorList,
			String srcSchema, String tarSchema, String operGroup, Date jobDate,
			String db) throws JAFDatabaseException {
		// if (loadCursorList != null && loadCursorList.size() > 0) {
		// StringBuffer sqlBuf = new StringBuffer();
		// // 获取操作系统标识
		// boolean flagSys = SystemConfig.getInstance().isWindowsOS();
		// String tmpSchema = TcbsConfig.getInstance().getSchemaNameA();
		//
		// // 选择需要连接的数据库和用户名密码
		// String password = null;
		// if (db.equalsIgnoreCase(BatchConstants.QDBDB)) {
		// if (srcSchema.equalsIgnoreCase(tmpSchema)) {
		// password = TcbsConfig.getInstance().getQdbUsraPassword();
		// } else {
		// password = TcbsConfig.getInstance().getQdbUsrbPassword();
		// }
		// }
		//
		// // 取当前表空间的备份路径
		// String backupPath = FilePathHelper.getBackupTableSpacePath()
		// + srcSchema + File.separator;
		//
		// // 删除备份的文件
		// FileUtil.getInstance().deleteFiles(backupPath);
		//
		// //
		// sqlBuf.append("connect reset; \n");
		// sqlBuf.append("backup db " + TcbsConfig.getInstance().getQdbAlias()
		// + " user " + srcSchema + " using " + password
		// + " tablespace (");
		//
		// // 取出需要备份的表空间
		// int size = loadCursorList.size();
		// for (int i = 0; i < size; i++) {
		// if (i < size - 1) {
		// sqlBuf.append(loadCursorList.get(i).getOperSql() + ",");
		// } else {
		// sqlBuf.append(loadCursorList.get(i).getOperSql());
		// }
		// }
		// sqlBuf.append(") online to " + backupPath + " ; \n");
		//
		// // SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
		// // long l = System.currentTimeMillis();
		// // String fileName = sdf.format(new Date(l)) + "." + operGroup +
		// // ".del";
		//
		// String fileName = operGroup + ".del";
		//
		// // 保存SQL文件
		// String filePath = FileUtil.getInstance().getRoot()
		// + BatchConstants.SHELL_FILEPATH;
		// // 增加文件路径
		// try {
		// fileName = filePath + fileName;
		// FileUtil.getInstance().writeFile(fileName, sqlBuf.toString());
		//
		// } catch (FileOperateException e) {
		// logs.error("存文件失败" + e);
		// throw new AsynJobException(e.toString());
		// }
		//
		// // 调用Shell
		// String shellStr = callDbBackupShell(fileName, flagSys);
		// if (shellStr == null) {
		// logs.error("返回的结果集与调用Shell数量不一致,调用Shell失败,Shell调用结果＝"
		// + shellStr);
		// throw new AsynJobException("调用Shell失败！！！！！");
		// }
		//
		// String operID = loadCursorList.get(0).getJobId();
		// if (shellStr.indexOf("备份成功") >= 0
		// || shellStr.indexOf("Backup successful") >= 0) {
		// DatabaseFacade.getODB().update(
		// genDto(operID, operGroup, jobDate));
		// } else {
		// logs.error("数据库空间备份失败,任务ID=" + operGroup + "操作ID=" + operID
		// + "操作日期=" + jobDate);
		// throw new AsynJobException("存在操作失败的数据,任务ID=" + operGroup
		// + "操作ID=" + operID + "操作日期=" + jobDate + " 操作结果="
		// + shellStr);
		// }
		// }
	}

	public static String callShell(String command) throws Exception {

		String result = "";
		try {
			String strCommand = null;

			strCommand = ITFECommonConstant.CALL_DB2 + command;
			//	
			logs.debug(" Shell:" + strCommand);
			//	
			Process child = Runtime.getRuntime().exec(strCommand);
			InputStream in = child.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream(8 * 1024);
			byte[] bytes = new byte[64 * 1024];
			int length = 0; // read content
			for (int i = in.read(bytes); i != -1; i = in.read(bytes)) {
				out.write(bytes, 0, i);
				length += i;
			}
			in.close();
			child.waitFor();
			String filename = ITFECommonConstant.FILE_ROOT_PATH+new Date(System.currentTimeMillis())+ "import.log";
			FileUtil.getInstance().writeBytesToFile(
					out.toByteArray(),filename);
			return filename ;
		} catch (Exception e) {
			logs.error("－－－－－－－－－－－－调用shell出错的返回信息＝" + e);
			throw new Exception(e.toString());
		}

	}

	/**
	 * 类型转换方法
	 * 
	 * @param type
	 * @param values
	 * @return
	 */
	private static Object typeConvert(String type, String values) {
		if (type.equalsIgnoreCase("Integer")) {
			return new Integer(values);

		} else if (type.equalsIgnoreCase("BigDecimal")) {
			return new BigDecimal(values);

		} else if (type.equalsIgnoreCase("Date")) {
			return Date.valueOf(values);
		} else {
			return values;
		}
	}

	private static void dbOP(List<SQLMapping> dbSQLs, SQLExecutor sqlExec,
			List<Object> params) throws JAFDatabaseException {
		try{
		boolean sqlExecIsNullflag = false;
		if (sqlExec == null) {
			sqlExecIsNullflag = true;
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
		}

		// 用于更新数据库整理任务跟踪信息
		for (int i = 0; i < dbSQLs.size(); i++) {
			SQLMapping map = (SQLMapping) dbSQLs.get(i);
			String operSql = map.getOperSql();

			if (params != null && params.size() > 0) {
				for (Object o : params) {

					sqlExec.addParam(o);
				}

			}
			sqlExec.runQuery(operSql);
			logs.info("执行的SQL" + operSql);
			sqlExec.clearParams();

		}
//		if (sqlExecIsNullflag) {
//			sqlExec.closeConnection();
//		}
		}catch (JAFDatabaseException e){
			logs.error("关闭数据库失败"+e);
			throw new JAFDB2Exception("关闭数据库失败");
		}finally{
			if(null != sqlExec){
				sqlExec.closeConnection();
			}
		}

	}

	/**
	 * 通过调用SYSPROC.ADMIN_CMD方式
	 * 
	 * @param sql
	 * @param sqlExec
	 * @throws JAFDatabaseException
	 */
	public static void callSYSADMINCMD(String sql, SQLExecutor sqlExec)
			throws JAFDatabaseException {

		long start = System.currentTimeMillis();
		sqlExec.addParam(sql);
		sqlExec.runStoredProc("SYSPROC.ADMIN_CMD", true, null, 0);
		// sqlExec.clearParams();
		// sqlExec.addParam("export to 1.del of del select * from tas_tc_gmj");
		// SQLResults
		// res=sqlExec.runStoredProcExecCloseCon("SYSPROC.ADMIN_CMD");
		// long end = System.currentTimeMillis();
		// logs.info("+++++++++++SPEND+++++++++++++++++" + (end - start));
	}

	// /**
	// * 从数据库（没有成功）和配置文件中查找一致的操作内容
	// *
	// * @param configList
	// * @param notSUCCList
	// * @param confgiID
	// * @param jobDate
	// * @param operType
	// * @return
	// */
	// public static List<SQLMapping> findSameContent(List<SQLMapping>
	// configList,
	// List<TmDatasettletaskTraceDto> notSUCCList, String confgiID,
	// Date jobDate, String operType) {
	//
	// List<SQLMapping> sameContentList = new ArrayList<SQLMapping>();
	//
	// for (int i = 0; i < notSUCCList.size(); i++) {
	// String dbTaskID = notSUCCList.get(i).getStrimoprid();
	// for (int j = 0; j < configList.size(); j++) {
	// SQLMapping map = (SQLMapping) configList.get(j);
	// String configTaskID = map.getJobId();
	// String mapOperType = map.getOperType();
	// if (dbTaskID.equals(configTaskID)) {
	// if (mapOperType.equalsIgnoreCase(operType)) {
	// sameContentList.add(map);
	// }
	// }
	//
	// }
	//
	// }
	// return sameContentList;
	//
	// }
	//
	// private static String dateConvert(String inParams, Date jobDate) {
	// String leng = inParams.substring(0, inParams.length() - 1);
	// String util = inParams.substring(inParams.length() - 1, inParams
	// .length());
	// return "'"
	// + DateUtil.dateBefore(jobDate, new Integer(leng).intValue(),
	// util) + "'";
	// }

	/**
	 * 分析callshell的文件
	 * 
	 * @param sql
	 * @param sqlExec
	 * @throws ITEFBizException 
	 * @throws JAFDatabaseException
	 */
	public static String callShellResultProcess(String resultfilepath) throws ITFEBizException {
		String result = "";
		try {
			result = FileUtil.getInstance().readFile(resultfilepath);
			//FileUtil.getInstance().deleteFile(resultfilepath);
		} catch (FileOperateException e) {
			logs.error("读取导入文件日志失败"+e);
			throw new ITFEBizException("读取导入文件日志失败"+e);}
//		}	 catch (FileNotFoundException e) {
//			logs.info("文件删除失败" + e);
//		}
		// Windows 中会出现 两个回车加一个换行的问题；
		result = result.replaceAll("\r\r\n", "\r\n");
		result = result.trim();
		String proresult = "";
		String[] tmpArray = null;
		tmpArray = result.split("\r\n");
		for (int i = 0; i < tmpArray.length; i++) {
			String tmp = tmpArray[i].toString();
			if (tmp.indexOf("SQLSTATE=08003") >= 0) {
				logs.info("数据库连接异常" + tmp);
				proresult = "数据库连接异常" + tmp;
			} else if (tmp.indexOf("SQL3109N") >= 0) {
				proresult ="";
				proresult = tmp + tmpArray[i + 1].toString() + "\r\n";
				logs.info(proresult);
			} else if (tmp.indexOf("SQL3116W") >= 0) {
				logs.info(tmp+"\r\n");
			} else if (tmp.indexOf("SQL3125W") >= 0) {
				logs.info(tmp+"\r\n");
			} else if (tmp.indexOf("SQL0180N") >= 0) {
				logs.info(tmp+"\r\n");
			} else if (tmp.indexOf("SQL3137W") >= 0) {
				logs.info(tmp+"\r\n");
			} else if (tmp.indexOf("SQL3148W") >= 0) {
				logs.info(tmp+"\r\n");
			} else if (tmp.indexOf("拒绝行数") >= 0) {
				int x = tmp.indexOf("=");
				Long num = new Long(tmp.substring(x + 2));
				if (num > 0) {
					logs.info(proresult + "\r\n拒绝行数" + num.toString());
					proresult = proresult.replace("\n","") + "\r\n数据导入出错,拒绝行数" + num.toString();
					return proresult;
				}
			}
		}
		  if (proresult.indexOf("拒绝行数")<0) {
			   proresult ="导入成功";
		  }
		  return proresult;
		 
		  
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String makeDbConnectionSql() {
		String db = ITFECommonConstant.DB_ALIAS;
		String user = ITFECommonConstant.DB_USERNAME;
		String password = ITFECommonConstant.DB_PASSWORD;
		return makeDbConnectionSql(db, user, password);
	}
	public static String makeDbConnectionSql(String db, String user,
			String password) {
		String dbconnsql = "connect to " + db + " user " + user + " using "
				+ password;
		return dbconnsql;
	}
	
	public static String callShellWithLogFileName(String command,
			String logFilePath) throws Exception {

		try {
			String strCommand = ITFECommonConstant.CALL_DB2 + command;

			logs.debug(" Shell:" + strCommand);

			Process child = Runtime.getRuntime().exec(strCommand);

			InputStream in = child.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream(8 * 1024);
			byte[] bytes = new byte[64 * 1024];
			int length = 0; // read content
			for (int i = in.read(bytes); i != -1; i = in.read(bytes)) {
				out.write(bytes, 0, i);
				length += i;
			}
			in.close();
			child.waitFor();
			File f = new File(logFilePath);
			if (f.exists()) {
				FileUtil.getInstance().deleteFile(logFilePath);
			}
			FileUtil.getInstance().writeBytesToFile(out.toByteArray(),
					logFilePath);
			return logFilePath;
		} catch (Exception e) {
			logs.error("－－－－－－－－－－－－调用shell出错的返回信息＝" + e);
			throw new Exception(e.toString());
		}
	}
	
	/**
	 * 
	 * @param resultfilepath db2sql的完整路径
	 * @return 错误信息
	 * @throws TASBizException
	 */
	public static String analyseDb2ExportShellLog(String resultfilepath) throws ITFEBizException {
		try {
			String db2ShellLogString  = FileUtil.getInstance().readFile(resultfilepath);
			db2ShellLogString = db2ShellLogString.replaceAll("\r\r\n", "\r\n").trim();
			String[] tmpArray = db2ShellLogString.split("\r\n");
			String proMsg = "";
			for (int i = 0; i < tmpArray.length; i++) {
				List<String> list = MatcherUtil.findMatchString(tmpArray[i].toString(), PatternConstant.PATTERN_DB2_SQLCODE);
				for(String s:list){
					if(!rightExportSqlCodeList.contains(s)){
						proMsg+="第"+i+"行有错误码"+s+"\r\n";
					}
				}
			}
			return proMsg;
		} catch (Exception e) {
			logs.error(e);
			throw new ITFEBizException(e);
		}
	}
	
	/**
	 * 生成带大对象lob的表数据导出sql
	 * 
	 * @param tablecode
	 * @param exportFileName
	 * @param exportFileType
	 * @param lobFileDirectory lob文件目录
	 * @param lobFileName lob文件名
	 * @param whereCondition
	 * @return
	 */
	public static String makeLobDataExportSql(String tablecode,
			String exportFileName, String exportFileType, String lobFileDirectory,String lobFileName,String whereCondition) {
		String sql = "export to " + exportFileName + " of " + exportFileType
		+" lobs to " +lobFileDirectory +" lobfile  "+lobFileName + " modified by lobsinfile "
		+ " select * from " + tablecode + " " + whereCondition;
		return sql;
	}
	
	public static String makeDataExportSql(String tablecode,
			String exportFileName, String exportFileType, String whereCondition) {
		String sql = "export to " + exportFileName + " of " + exportFileType
				+ " select * from " + tablecode + " " + whereCondition;
		return sql;
	}
	
	// 生成删除操作的SQL语句
	public static String makeDeleteTableSql(String tablecode,
			String whereCondition) {
		String sql = " delete from " + tablecode + " " + whereCondition;
		return sql;
	}
	
	// 生成恢复时使用的插入语句(含有Lob对象的表)
	public static String makeLobImportTableSql(String tablecode,
			String importFileName, String importFileType,
			String lobFilePath,String modifyedByString, String importMode) {
		// import from importFileName of ixf importMode into tablecode;
		String sql = " import from " + importFileName + " of " + importFileType
				+ " lobs from "+lobFilePath+" " + modifyedByString + " " + importMode + " into "
				+ tablecode;
		return sql;
	}
	// 生成恢复时使用的插入语句
	public static String makeImportTableSql(String tablecode,
			String importFileName, String importFileType,
			String modifyedByString, String importMode) {
		// import from importFileName of ixf importMode into tablecode;
		String sql = " import from " + importFileName + " of " + importFileType
				+ " " + modifyedByString + " " + importMode + " into "
				+ tablecode;
		return sql;
	}
}

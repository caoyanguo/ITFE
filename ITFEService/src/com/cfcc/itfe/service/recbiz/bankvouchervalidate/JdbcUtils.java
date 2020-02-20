package com.cfcc.itfe.service.recbiz.bankvouchervalidate;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.core.loader.ContextFactory;

/**
 * 数据库操作工具类, 主要是方便自己更加灵活的控制事务等操作. (TODO 可以考虑把之前写的相关代码重构一下.)
 * 
 * @author hua
 * 
 */
public final class JdbcUtils {
	private static Log log = LogFactory.getLog(JdbcUtils.class);
	// 数据库连接本地变量
	private static ThreadLocal<Connection> connThreadLocal = new ThreadLocal<Connection>();
	// 数据库连接池
	private static DataSource dataSource = (DataSource) ContextFactory.getApplicationContext().getBean("DataSource.DB.ITFEDB");

	private JdbcUtils() {
	}

	/**
	 * 执行一条更新SQL语句
	 * 
	 * @param conn
	 *            数据库连接(如果传入的是同一个连接, 那么就相当于在同一个事务里面)
	 * @param sql
	 *            要执行的SQL语句
	 * @param params
	 *            SQL语句所需的参数值
	 */
	public static int executeUpdate(Connection conn, String sql, Object[] params) throws SQLException {
		if (conn == null) {
			throw new SQLException("Null connection");
		}

		if (sql == null) {
			throw new SQLException("Null SQL statement");
		}

		PreparedStatement stmt = null;
		int rows = 0;
		try {
			stmt = conn.prepareStatement(sql);
			fillStatement(stmt, params);
			rows = stmt.executeUpdate();

		} catch (SQLException e) {
			rethrow(e, sql, params);

		} finally {
			stmt.close();
			log.debug("======> 执行SQL语句(JdbcUtils) : " + sql + "; " + Arrays.toString(params));
		}
		return rows;
	}

	/**
	 * 执行批量SQL语句
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            要执行的SQL语句
	 * @param params
	 *            SQL语句所需的参数值, 注意每一个子集合都会成填充一遍SQL, 最终形成批量SQL效果
	 * @return
	 * @throws SQLException
	 */
	public int[] executeBatch(Connection conn, String sql, Object[][] params) throws SQLException {
		if (conn == null) {
			throw new SQLException("Null connection");
		}

		if (sql == null) {
			throw new SQLException("Null SQL statement");
		}

		if (params == null) {
			throw new SQLException("Null parameters. If parameters aren't need, pass an empty array.");
		}

		PreparedStatement stmt = null;
		int[] rows = null;
		try {
			stmt = conn.prepareStatement(sql);

			for (int i = 0; i < params.length; i++) {
				fillStatement(stmt, params[i]);
				stmt.addBatch();
			}
			rows = stmt.executeBatch();

		} catch (SQLException e) {
			rethrow(e, sql, (Object[]) params);
		} finally {
			stmt.close();
			log.debug("======> 执行批量SQL语句(JdbcUtils) : " + sql + "; " + Arrays.toString(params));
		}

		return rows;
	}

	/**
	 * 将参数值填充到SQL语句中
	 * 
	 * @param stmt
	 * @param params
	 * @throws SQLException
	 */
	private static void fillStatement(PreparedStatement stmt, Object[] params) throws SQLException {
		ParameterMetaData pmd = null;
		pmd = stmt.getParameterMetaData();
		int stmtCount = pmd.getParameterCount();
		int paramsCount = params == null ? 0 : params.length;

		if (stmtCount != paramsCount) {
			throw new SQLException("Wrong number of parameters: expected " + stmtCount + ", was given " + paramsCount);
		}

		if (params == null) {
			return;
		}

		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				stmt.setObject(i + 1, params[i]);
			} else {
				int sqlType = Types.VARCHAR;
				try {
					sqlType = pmd.getParameterType(i + 1);
				} catch (SQLException e) {
				}
				stmt.setNull(i + 1, sqlType);
			}
		}
	}

	/**
	 * 将异常以更合理的格式重新封装, 主要是便于理解
	 * 
	 * @param cause
	 * @param sql
	 * @param params
	 * @throws SQLException
	 */
	private static void rethrow(SQLException cause, String sql, Object[] params) throws SQLException {
		String causeMessage = cause.getMessage();
		if (causeMessage == null) {
			causeMessage = "";
		}
		StringBuffer msg = new StringBuffer(causeMessage);

		msg.append(" 执行语句: ");
		msg.append(sql);
		msg.append(" 参数: ");

		if (params == null) {
			msg.append("[]");
		} else {
			msg.append(Arrays.deepToString(params));
		}

		SQLException e = new SQLException(msg.toString(), cause.getSQLState(), cause.getErrorCode());
		e.setNextException(cause);

		throw e;
	}

	/**
	 * 获取一个数据库连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		Connection conn = connThreadLocal.get();
		if (conn == null) {
			conn = dataSource.getConnection();
		}
		log.debug("======> 获取数据库连接(JdbcUtils)!");
		return conn;
	}

	/**
	 * 开启事务
	 * 
	 * @throws SQLException
	 */
	public static void startTransaction() throws SQLException {
		Connection conn = connThreadLocal.get();
		if (conn == null) {
			conn = getConnection();
			connThreadLocal.set(conn);
		}
		conn.setAutoCommit(Boolean.FALSE);
		log.debug("======> 开启事务(JdbcUtils)!");
	}

	/**
	 * 提交事务
	 * 
	 * @throws SQLException
	 */
	public static void commit() throws SQLException {
		Connection conn = connThreadLocal.get();
		if (conn != null) {
			conn.commit();
			log.debug("======> 提交事务(JdbcUtils)!");
		}
	}

	/**
	 * 回滚事务
	 * 
	 * @throws SQLException
	 */
	public static void rollback() throws SQLException {
		Connection conn = connThreadLocal.get();
		if (conn != null) {
			conn.rollback();
			log.debug("======> 回滚事务(JdbcUtils)!");
		}
	}

	/**
	 * 释放资源
	 * 
	 * @throws SQLException
	 */
	public static void release() throws SQLException {
		Connection conn = connThreadLocal.get();
		if (conn != null) {
			conn.close();
			connThreadLocal.remove();
		}
		log.debug("======> 释放数据库资源(JdbcUtils)!");
	}

	/**
	 * 释放资源
	 * 
	 * @param conn
	 * @param st
	 * @param rs
	 */
	public static void release(Connection conn, Statement st, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}

			if (st != null) {
				st.close();
			}

			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			log.error("释放资源出现异常", e);
		} finally {
			st = null;
			rs = null;
			conn = null;
		}
	}
}

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
 * ���ݿ����������, ��Ҫ�Ƿ����Լ��������Ŀ�������Ȳ���. (TODO ���Կ��ǰ�֮ǰд����ش����ع�һ��.)
 * 
 * @author hua
 * 
 */
public final class JdbcUtils {
	private static Log log = LogFactory.getLog(JdbcUtils.class);
	// ���ݿ����ӱ��ر���
	private static ThreadLocal<Connection> connThreadLocal = new ThreadLocal<Connection>();
	// ���ݿ����ӳ�
	private static DataSource dataSource = (DataSource) ContextFactory.getApplicationContext().getBean("DataSource.DB.ITFEDB");

	private JdbcUtils() {
	}

	/**
	 * ִ��һ������SQL���
	 * 
	 * @param conn
	 *            ���ݿ�����(����������ͬһ������, ��ô���൱����ͬһ����������)
	 * @param sql
	 *            Ҫִ�е�SQL���
	 * @param params
	 *            SQL�������Ĳ���ֵ
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
			log.debug("======> ִ��SQL���(JdbcUtils) : " + sql + "; " + Arrays.toString(params));
		}
		return rows;
	}

	/**
	 * ִ������SQL���
	 * 
	 * @param conn
	 *            ���ݿ�����
	 * @param sql
	 *            Ҫִ�е�SQL���
	 * @param params
	 *            SQL�������Ĳ���ֵ, ע��ÿһ���Ӽ��϶�������һ��SQL, �����γ�����SQLЧ��
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
			log.debug("======> ִ������SQL���(JdbcUtils) : " + sql + "; " + Arrays.toString(params));
		}

		return rows;
	}

	/**
	 * ������ֵ��䵽SQL�����
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
	 * ���쳣�Ը�����ĸ�ʽ���·�װ, ��Ҫ�Ǳ������
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

		msg.append(" ִ�����: ");
		msg.append(sql);
		msg.append(" ����: ");

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
	 * ��ȡһ�����ݿ�����
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		Connection conn = connThreadLocal.get();
		if (conn == null) {
			conn = dataSource.getConnection();
		}
		log.debug("======> ��ȡ���ݿ�����(JdbcUtils)!");
		return conn;
	}

	/**
	 * ��������
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
		log.debug("======> ��������(JdbcUtils)!");
	}

	/**
	 * �ύ����
	 * 
	 * @throws SQLException
	 */
	public static void commit() throws SQLException {
		Connection conn = connThreadLocal.get();
		if (conn != null) {
			conn.commit();
			log.debug("======> �ύ����(JdbcUtils)!");
		}
	}

	/**
	 * �ع�����
	 * 
	 * @throws SQLException
	 */
	public static void rollback() throws SQLException {
		Connection conn = connThreadLocal.get();
		if (conn != null) {
			conn.rollback();
			log.debug("======> �ع�����(JdbcUtils)!");
		}
	}

	/**
	 * �ͷ���Դ
	 * 
	 * @throws SQLException
	 */
	public static void release() throws SQLException {
		Connection conn = connThreadLocal.get();
		if (conn != null) {
			conn.close();
			connThreadLocal.remove();
		}
		log.debug("======> �ͷ����ݿ���Դ(JdbcUtils)!");
	}

	/**
	 * �ͷ���Դ
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
			log.error("�ͷ���Դ�����쳣", e);
		} finally {
			st = null;
			rs = null;
			conn = null;
		}
	}
}

package com.test;

import java.sql.SQLException;

import javax.sql.XAConnection;

import org.enhydra.jdbc.pool.StandardXAPoolDataSource;

public class MyStandardXAPoolDataSource extends StandardXAPoolDataSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -772207911677636825L;

	/**
	 * getConnection allows to get an object from the pool and returns it
	 * to the user. In this case, we return an PooledConnection
	 */
	public XAConnection getXAConnection() throws SQLException {
		return getXAConnection(getUser(), getPassword());
	}

	/**
	 * getConnection allows to get an object from the pool and returns it
	 * to the user. In this case, we return an PooledConnection
	 */
	public XAConnection getXAConnection(String _user, String _password)
		throws SQLException {
		log.debug("StandardPoolDataSource:getConnection");
//		XAConnection ret = null;
		XAConnection con = null;

		synchronized (this) {
			if (!onOff) {
				log.debug(
					"StandardPoolDataSource:getConnection must configure the pool...");
				pool.start(); // the pool starts now
				onOff = true; // and is initialized
				log.debug(
					"StandardPoolDataSource:getConnection pool config : \n"
						+ pool.toString());
			}
		}

		try {
			try {
				log.debug(
					"StandardPoolDataSource:getConnection Try to give a "
						+ "connection (checkOut)");
				con = (XAConnection) pool.checkOut(_user, _password);
				// get a connection from the pool
				log.debug(
					"StandardPoolDataSource:getConnection checkOut return"
						+ "a new connection");
			} catch (Exception e) {
                                e.printStackTrace();
				log.debug(
					"StandardPoolDataSource:getConnection SQLException in StandardPoolDataSource:getConnection"
						+ e);
				throw new SQLException(
					"SQLException in StandardPoolDataSource:getConnection no connection available "
						+ e);
			}
			log.debug("StandardPoolDataSource:getConnection return a connection");
            return con;
//			ret = con.getConnection();
		} catch (Exception e) {
			log.debug("StandardPoolDataSource:getConnection exception" + e);
                        e.printStackTrace();
			SQLException sqle =
				new SQLException(
					"SQLException in StandardPoolDataSource:getConnection exception: "
						+ e);
			if (e instanceof SQLException)
				sqle.setNextException((SQLException) e);
			if (con != null) {
				pool.checkIn(con);
			}
			throw sqle;
		}
		
//		return ret;
	}


}

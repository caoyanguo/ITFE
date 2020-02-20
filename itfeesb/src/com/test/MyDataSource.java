package com.test;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.enhydra.jdbc.pool.StandardXAPoolDataSource;

public class MyDataSource implements DataSource {
	private StandardXAPoolDataSource standardXAPoolDataSource;
	 
	 
	public StandardXAPoolDataSource getStandardXAPoolDataSource() {
	return standardXAPoolDataSource;
}

public void setStandardXAPoolDataSource(StandardXAPoolDataSource standardXAPoolDataSource) {
	this.standardXAPoolDataSource = standardXAPoolDataSource;
}

	public PrintWriter getLogWriter() throws SQLException {
		return standardXAPoolDataSource.getDataSource().getLogWriter();
		 
	}

	public int getLoginTimeout() throws SQLException {
		
		return standardXAPoolDataSource.getLoginTimeout();
	}

	public Connection getConnection() throws SQLException {
		return standardXAPoolDataSource.getConnection();
		
	}

	public Connection getConnection(String arg0, String arg1) throws SQLException {
		return standardXAPoolDataSource.getConnection(arg0,arg1);			
	}

	public void setLogWriter(PrintWriter arg0) throws SQLException {
		standardXAPoolDataSource.getDataSource().setLogWriter(arg0);
	}

	public void setLoginTimeout(int arg0) throws SQLException {
		standardXAPoolDataSource.setLoginTimeout(arg0);

	}


}

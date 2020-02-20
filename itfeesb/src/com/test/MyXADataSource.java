package com.test;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.enhydra.jdbc.pool.GenerationObject;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;

public class MyXADataSource implements XADataSource {
 private MyStandardXAPoolDataSource standardXAPoolDataSource;
 
 
	public MyStandardXAPoolDataSource getStandardXAPoolDataSource() {
	return standardXAPoolDataSource;
}

public void setStandardXAPoolDataSource(MyStandardXAPoolDataSource standardXAPoolDataSource) {
	this.standardXAPoolDataSource = standardXAPoolDataSource;
}

	public PrintWriter getLogWriter() throws SQLException {
		return standardXAPoolDataSource.getDataSource().getLogWriter();
		 
	}

	public int getLoginTimeout() throws SQLException {
		
		return standardXAPoolDataSource.getLoginTimeout();
	}

	public XAConnection getXAConnection() throws SQLException {
		return standardXAPoolDataSource.getXAConnection();	
	}

	public XAConnection getXAConnection(String arg0, String arg1) throws SQLException {
		return standardXAPoolDataSource.getXAConnection(arg0,arg1);
	}

	public void setLogWriter(PrintWriter arg0) throws SQLException {
		standardXAPoolDataSource.getDataSource().setLogWriter(arg0);
	}

	public void setLoginTimeout(int arg0) throws SQLException {
		standardXAPoolDataSource.setLoginTimeout(arg0);

	}

}

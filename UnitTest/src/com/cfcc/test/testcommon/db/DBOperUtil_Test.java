package com.cfcc.test.testcommon.db;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.itfe.facade.DBOperUtil;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.SQLMapping;

public class DBOperUtil_Test {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

//					
		List paras =new ArrayList();
		paras.add("100000000000");
		List<SQLMapping> tasdelete = (List<SQLMapping>) ContextFactory
    	.getApplicationContext().getBean("TASDelete");
		
		
		
		List<SQLMapping> sqlmove = (List<SQLMapping>) ContextFactory
    	.getApplicationContext().getBean("TASInsert");
		
		
		List<SQLMapping> TBSDelete = (List<SQLMapping>) ContextFactory
    	.getApplicationContext().getBean("TBSDelete");
		
		


		
		try {
			DBOperUtil.exec(tasdelete, null, paras);
			DBOperUtil.exec(sqlmove, null, paras);
			DBOperUtil.exec(TBSDelete, null, paras);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static void testCallCMD(){
		String sql = "import from c:/tas_tc_gmj of del insert into tas_tc_gmj";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
			.getSQLExecutor();
			DBOperUtil.callSYSADMINCMD(sql, sqlExec);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}


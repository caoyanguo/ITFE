package com.cfcc.test.testcommon.db;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.itfe.facade.DBOperUtil;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.SQLMapping;

public class TestDataVMSQL {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	public static void main(String[] arg) {
		String beanid1 = "TASDelete";
		String beanid2 = "TBSDelete";
		String beanid3 = "InsertAuditIndex";

		String beanid4 = "TASparamsMerge";
		List<Object> params = new ArrayList<Object>();
		params.add("020000000000");
		// params.add("020000000000");

		// List<SQLMapping> map1 = (List<SQLMapping>) ContextFactory
		// .getApplicationContext().getBean(beanid1);

		// List<SQLMapping> map2 = (List<SQLMapping>) ContextFactory
		// .getApplicationContext().getBean(beanid2);

		// List<SQLMapping> map3 = (List<SQLMapping>) ContextFactory
		// .getApplicationContext().getBean(beanid3);

		List<Object> map4 = (List<Object>) ContextFactory
				.getApplicationContext().getBean(beanid4);

		SQLExecutor exec;
		try {
			exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			// DBOperUtil.exec(map1, exec, params);
			// System.out.println("ok1");
			// DBOperUtil.exec(map1, exec, params);
			// System.out.println("ok2");
			// DBOperUtil.exec(map2, exec, params);
			// System.out.println("ok3");
			// DBOperUtil.exec(map3, exec, params);
			// System.out.println(map3);

			// for (int i = 0; i < map3.size(); i++) {
			// String sql = map3.get(i).getOperSql() + ";";
			// System.out.println(sql);
			// }

			
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < map4.size(); i++) {
				List<SQLMapping> sing = (List<SQLMapping>) map4.get(i);
				for (int j = 0; j < sing.size(); j++) {
					String w = sing.get(j).getOperSql();
					System.out.println(w);
					buffer.append(w.toString()).append(";\n");
					
				}
//				 DBOperUtil.exec(sing, exec, null);
//				 System.out.println(sing);
			}
//			FileOper.saveFile(buffer.toString(), "c:/1.sql");
			System.out.println("ok4");

		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}

	}
}

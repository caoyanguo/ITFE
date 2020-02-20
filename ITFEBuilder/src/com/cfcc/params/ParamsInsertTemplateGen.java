package com.cfcc.params;

import java.io.StringWriter;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.cfcc.FileOper;
import com.cfcc.database.DBOpertion;
import com.cfcc.jaf.core.loader.ContextFactory;

/**
 * Action template generate
 */

public class ParamsInsertTemplateGen {
	static VelocityContext context = new VelocityContext();
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
		try {
			Velocity.init();
		} catch (Exception e) {
			System.out.println("Problem initializing Velocity : " + e);
			System.exit(0);
		}
	}

	public static void main(String args[]) {
		genIns();
		/* lets make a Context and put data into it */

		/* lets render a template */

	}

	

	/**
	 * 生成插入参数表的代码
	 */
	private static void genIns() {
		// 需要过滤的表
		// // TAS_TC_CMT842_MAIN
		// TAS_TC_CMT842_SUB
		//
		//
		// TAS_TC_CMT841_MAIN
		// TAS_TC_CMT841_GOSORT_SUB
		// TAS_TC_CMT841_ISTTRANS_SUB
		List<String> tables = DBOpertion.lookTable("db2admin", null);
		List<TableName> tabdatas = new ArrayList<TableName>();

		for (int i = 0; i < tables.size(); i++) {
			String tabName = tables.get(i);
			if (tabName.indexOf("TAS_TC") >= 0
					&& !tabName.equals("TAS_TC_CMT842_MAIN")
					&& !tabName.equals("TAS_TC_CMT842_SUB")
					&& !tabName.equals("TAS_TC_CMT841_MAIN")
					&& !tabName.equals("TAS_TC_CMT841_GOSORT_SUB")
					&& !tabName.equals("TAS_TC_CMT841_ISTTRANS_SUB")
					&& !tabName.equals("TAS_TC_CMT842_MAIN")) {
				TableName table = new TableName();
				String strColumns = getColNames(tabName);
				table.setStrColumns(strColumns);
				// table.setTabId("0001_"+tabName);
				table.setSrctable(tabName);
				table.setDestable("TBS_TC"
						+ tabName.substring(6, tabName.length()));
				tabdatas.add(table);
			}
		}
		try {
			context.put("InsTasTableList", tabdatas);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/params/vm/insert.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/DataInsSqlConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}

	private static String getColNames(String table) {
		StringBuffer colStrs = new StringBuffer();
		HashMap<String, String> fileterColMap = new HashMap<String, String>();
		String col1 = "s_AuditResult".toUpperCase();
		String col2 = "c_AuditState".toUpperCase();
		String col3 = "num".toUpperCase();
//		fileterColMap.put(col1, col1);
//		fileterColMap.put(col2, col2);
		fileterColMap.put(col3, col3);
		List<String> cols = DBOpertion.lookColumnByTabNameWithFilter(table,
				fileterColMap);
		for (int i = 0; i < cols.size(); i++) {
			colStrs.append(cols.get(i));
			if (i != cols.size() - 1) {
				colStrs.append(",");
			}

		}
		return colStrs.toString();
	}
}

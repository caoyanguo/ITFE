package com.cfcc.params;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.cfcc.FileOper;
import com.cfcc.database.ColumnData;
import com.cfcc.database.DBOpertion;
import com.cfcc.jaf.core.loader.ContextFactory;

/**
 * Action template generate
 */

public class ParamsMergeTemplateGen {
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
		genMove();
		/* lets make a Context and put data into it */

		/* lets render a template */

	}

	// private static String velocity(TableName table) {
	// String srtTable = table.getSrctable();
	// String desTable = table.getDestable();
	// try {
	// context.put("desTable", desTable);
	// context.put("colname", getColNames(srtTable));
	// context.put("srcTable", srtTable);
	// StringWriter w = new StringWriter();
	// Velocity.mergeTemplate("./src/com/cfcc/params/vm/merge.vm", "GBK",
	// context, w);
	// return w.toString();
	// // saveFile(w.toString(), "c:/test/Action" + actionid + ".java");
	// } catch (Exception e) {
	// System.out.println("Problem merging template : " + e);
	// }
	// return null;
	// }

	/**
	 * 生成数据搬移参数表的代码
	 */
	private static void genMove() {
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
				String strColumns = getColNamesFormat(tabName);
				table.setStrColumns(strColumns);

				String priColumns = getPrimaryKeyNames(tabName);
				table.setPriColumns(priColumns);

				// table.setTabId("0001_"+tabName);
				table.setSrctable(tabName);
				table.setDestable("TBS_TC"
						+ tabName.substring(6, tabName.length()));
				tabdatas.add(table);

			}
		}
		try {
			context.put("MoveTasTableList", tabdatas);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/params/vm/merge.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(),
					"c:/ParamDataCompareMergeSqlConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}

	private static String getPrimaryKeyNames(String table) {
		StringBuffer colStrs = new StringBuffer();
		// HashMap<String, String> fileterColMap = new HashMap<String,
		// String>();
		// String col1 = "s_AuditResult".toUpperCase();
		// String col2 = "c_AuditState".toUpperCase();
		// fileterColMap.put(col1, col1);
		// fileterColMap.put(col2, col2);
		List<String> cols = DBOpertion.lookPKToColName(table, null);
		for (int i = 0; i < cols.size(); i++) {
			if (!cols.get(i).equals("I_PARAMSEQNO")) {
				colStrs.append("a." + cols.get(i) + "=b." + cols.get(i));
				if (i != cols.size() - 1) {
					colStrs.append(" and ");
				}
			}

		}
		return colStrs.toString();
	}

	private static String getColNames(String table) {
		StringBuffer colStrs = new StringBuffer();
		HashMap<String, String> fileterColMap = new HashMap<String, String>();
		String col1 = "s_AuditResult".toUpperCase();
		String col2 = "c_AuditState".toUpperCase();
		// String col3="I_PARAMSEQNO";
		fileterColMap.put(col1, col1);
		fileterColMap.put(col2, col2);

		List<String> cols = DBOpertion.lookColumnByTabNameWithFilter(table,
				fileterColMap);
		for (int i = 0; i < cols.size(); i++) {
			colStrs.append("a." + cols.get(i) + "=b." + cols.get(i));
			if (i != cols.size() - 1) {
				colStrs.append(" and ");
			}
		}
		return colStrs.toString();
	}

	private static String getColNamesFormat(String table) {
		StringBuffer colstrs = new StringBuffer();
		HashMap<String, String> fileterColMap = new HashMap<String, String>();
		String col1 = "s_AuditResult".toUpperCase();
		String col2 = "c_AuditState".toUpperCase();
		String col3 = "i_num".toUpperCase();
		String col4 = "I_PARAMSEQNO";
		String col5 = "TS_SYSUPDATE";
		fileterColMap.put(col1, col1);
		fileterColMap.put(col2, col2);
		fileterColMap.put(col3, col3);
		fileterColMap.put(col4, col4);
		fileterColMap.put(col5, col5);
		
		List<ColumnData> coldatas = DBOpertion
				.lookColumnDataByTabNameWithFilter(table, null, fileterColMap);
		for (int i = 0; i < coldatas.size(); i++) {
			if (coldatas.get(i).isIsnull()) {
				colstrs.append("(( a." + coldatas.get(i).getColid() + "=b."
						+ coldatas.get(i).getColid() + ")or (a."
						+ coldatas.get(i).getColid() + " is null and b."
						+ coldatas.get(i).getColid() + " is null))");
			} else {
				colstrs.append("a." + coldatas.get(i).getColid() + "=b."
						+ coldatas.get(i).getColid());
			}

			if (i != coldatas.size() - 1) {
				colstrs.append(" and ");
			}
		}
		return colstrs.toString();
	}
}

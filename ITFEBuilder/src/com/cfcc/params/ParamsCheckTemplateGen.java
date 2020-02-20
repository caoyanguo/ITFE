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
import com.cfcc.database.TableData;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.config.BizConfigInfo;

/**
 * Action template generate
 */

public class ParamsCheckTemplateGen {
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
		List<String> listT = DBOpertion.lookTable("db2admin", null);
		List<TableData> result = new ArrayList<TableData>();
		for (int i = 0; i < listT.size(); i++) {
			String table = listT.get(i);
			if (table.indexOf("TAS_") >= 0) {
				TableData tabledata = new TableData();
				List<ColumnData> tmpColList = DBOpertion
						.lookColumnDataByTabName(table, null);
				List<ColumnData> colList = new ArrayList<ColumnData>();
				for (int x = 0; x < tmpColList.size(); x++) {
					ColumnData colData = tmpColList.get(x);
					ColumnData newcolData = tmpColList.get(x);
					if (!DBOpertion.filterColumns().containsKey(
							colData.getColid())) {
						String colname = newcolData.getColid().toLowerCase().replaceAll("_","");
						newcolData.setColid(colname);
						colList.add(newcolData);
					}
				}

				tabledata.setColList(colList);
				tabledata.setTabName(BizConfigInfo.TableDesMap().get(table));
				tabledata.setTabID(table);
				result.add(tabledata);
			}

		}
		velocity(result);
	}

	private static String velocity(List<TableData> result) {
		try {
			context.put("TableList", result);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(
					"./src/com/cfcc/params/vm/checkProperties.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/CheckObjectConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
		return null;
	}

	private static String getColNames(String table) {
		StringBuffer colStrs = new StringBuffer();
		HashMap<String, String> fileterColMap = new HashMap<String, String>();
		String col1 = "s_AuditResult".toUpperCase();
		String col2 = "c_AuditState".toUpperCase();
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
		String col3 = "num".toUpperCase();
		fileterColMap.put(col1, col1);
		fileterColMap.put(col2, col2);
		fileterColMap.put(col3, col3);
		List<String> cols = DBOpertion.lookColumnByTabNameWithFilter(table,
				fileterColMap);

		for (int i = 0; i < cols.size(); i++) {
			colstrs.append("a." + cols.get(i) + "=b." + cols.get(i));
			if (i != cols.size() - 1) {
				colstrs.append(" and ");
			}

		}
		return colstrs.toString();
	}

}

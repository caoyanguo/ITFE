package com.cfcc.params;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.cfcc.FileOper;
import com.cfcc.database.DBOpertion;
import com.cfcc.database.TableData;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.facade.DBOperUtil;
import com.cfcc.itfe.util.CommonUtil;

/**
 * Action template generate
 */

public class ParamsDeleteTemplateGen {
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
		genDel();
		genDto();
		/* lets make a Context and put data into it */

		/* lets render a template */

	}

	

	/**
	 * 生成删除参数表的代码
	 */
	private static void genDel() {
		List<String> tables=DBOpertion.lookTable("db2admin",null);
		List<TableData> tastabdatas = new ArrayList<TableData>();
		List<TableData> tbstabdatas = new ArrayList<TableData>();
		for(int i=0;i<tables.size();i++){
			String tabName = tables.get(i);
			if(tabName.indexOf("TAS_")>=0){
				TableData data = new TableData();
				data.setTabID(tabName);
				data.setTabName(tabName);
				tastabdatas.add(data);
			}
			if(tabName.indexOf("TBS_")>=0){
				TableData data = new TableData();
				data.setTabID(tabName);
				data.setTabName(tabName);
				tbstabdatas.add(data);
			}
		}
		try {
			context.put("tasList", tastabdatas);
			context.put("tbsList", tbstabdatas);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/params/vm/delete.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/DataDelSqlConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}
	private static void genDto() {
		List<String> tables=DBOpertion.lookTable("db2admin",null);
		List<TableData> tastabdatas = new ArrayList<TableData>();
		List<TableData> tbstabdatas = new ArrayList<TableData>();
		for(int i=0;i<tables.size();i++){
			String tabName = tables.get(i);
			if(tabName.indexOf("T")>=0){
				TableData data = new TableData();
				data.setTabID(tabName);
				data.setTabName(CommonUtil.tableToDtoName(tabName));
				tastabdatas.add(data);
			}
		}
		try {
			context.put("tasList", tastabdatas);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/params/vm/tabletodto.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/TableNameConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
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
			colStrs.append(cols.get(i));
			if (i != cols.size() - 1) {
				colStrs.append(",");
			}

		}
		return colStrs.toString();
	}
}

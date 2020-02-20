package com.cfcc.model;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.cfcc.FileOper;
import com.cfcc.database.ColumnData;
import com.cfcc.database.DBOpertion;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.model.data.WidgetData;
import com.cfcc.itfe.config.BizConfigInfo;
import com.cfcc.itfe.util.CommonUtil;

public class ParamsUITemplateGen {
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> tables = DBOpertion.lookTable("tcbsusra", null);
		for (int i = 0; i < tables.size(); i++) {
			String tabName = tables.get(i);
			if (tabName.startsWith("TBS_TC_")) {
				String moduleUUID = UUID.randomUUID().toString();
				String eTable = tabName;
				String cTable = BizConfigInfo.TableDesMap().get(tabName);
				String eName = DBOpertion.tableToDto(eTable).replaceAll("Dto",
						"")
						+ "Audit";
				cTable = cTable.replaceAll("（被监督）", "");
				genModule(moduleUUID, eTable, cTable, eName);
				genUIComponent(moduleUUID, eTable, cTable, eName);
				// genExtBean(moduleUUID, eTable, cTable,eName);
			}
		}

		// TODO Auto-generated method stub

	}

	/**
	 * 生成Module文件
	 * 
	 * @param moduleUUID
	 */
	private static void genExtBean(String moduleUUID, String eTable,
			String cTable, String eName) {
		String fileName = "Abstract" + eName + "BeanExt.java";
		try {
			context.put("ename", eName);
			context.put("ename1", eName.toLowerCase());
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/model/vm/ExtBean.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/model/" + fileName);
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}

	/**
	 * 生成Module文件
	 * 
	 * @param moduleUUID
	 */
	private static void genModule(String moduleUUID, String eTable,
			String cTable, String eName) {
		String fileName = "JModule_" + moduleUUID + ".model";
		try {
			context.put("uuid", moduleUUID);
			context.put("eTablename", eName);
			context.put("cTableName", cTable);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/model/vm/module.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/model/" + fileName);
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}

	/**
	 * 生成Module文件
	 * 
	 * @param moduleUUID
	 */
	private static void genUIComponent(String moduleUUID, String eTable,
			String cTable, String eName) {
		List<WidgetData> widgets = new ArrayList<WidgetData>();
		List<ColumnData> tmpcolumnDatas = DBOpertion.lookColumnDataByTabName(
				eTable, null);
		HashMap<String, String> filter = filterColumns();
		for (int i = 0; i < tmpcolumnDatas.size(); i++) {
			if (!filter.containsKey(tmpcolumnDatas.get(i).getColid())) {
				WidgetData data = new WidgetData();
				data.setText(UUID.randomUUID().toString());
				data.setLabel(UUID.randomUUID().toString());
				data.setCompoent(UUID.randomUUID().toString());
				data.setColcname(tmpcolumnDatas.get(i).getColname());
				data.setColename(tmpcolumnDatas.get(i).getColid().replaceAll(
						"_", "").toLowerCase());
				widgets.add(data);
			}
		}

		// for (int i = 0; i < 10; i++) {
		// WidgetData data = new WidgetData();
		// data.setText(UUID.randomUUID().toString());
		// data.setLabel(UUID.randomUUID().toString());
		// data.setCompoent(UUID.randomUUID().toString());
		// data.setColcname("测试" + i);
		// data.setColename("ABC" + i);
		// widgets.add(data);
		// }
		String compuuid = UUID.randomUUID().toString();
		String compcname = cTable + "_参数监督";
		String compename = eName;
		String dtoclz = CommonUtil.tableToDtoName(eTable.replace("TBS", "TAS"));
		String moduleuuid = moduleUUID + ".model#" + moduleUUID;
		String model = UUID.randomUUID().toString();
		String direction1 = UUID.randomUUID().toString();
		String Direction2 = UUID.randomUUID().toString();
		String attribute1 = UUID.randomUUID().toString();
		String attribute2 = UUID.randomUUID().toString();
		String attribute3 = UUID.randomUUID().toString();
		String attribute4 = UUID.randomUUID().toString();
		String area = UUID.randomUUID().toString();
		String node1 = UUID.randomUUID().toString();
		String node2 = UUID.randomUUID().toString();
		String Table = UUID.randomUUID().toString();
		String column1 = UUID.randomUUID().toString();
		String Column2 = UUID.randomUUID().toString();
		String Column3 = UUID.randomUUID().toString();
		String Column4 = UUID.randomUUID().toString();
		String pagenodes = UUID.randomUUID().toString();
		String pages = UUID.randomUUID().toString();
		String views = UUID.randomUUID().toString();
		String cTabname = cTable + "_参数监督";
		String buttonArea = UUID.randomUUID().toString();
		String buttons1 = UUID.randomUUID().toString();
		String buttons2 = UUID.randomUUID().toString();
		try {

			context.put("compuuid", compuuid);
			context.put("compcname", compcname);
			context.put("compename", compename);
			context.put("dtoclz", dtoclz);
			context.put("moduleuuid", moduleuuid);
			context.put("model", model);
			context.put("direction1", direction1);
			context.put("direction2", Direction2);
			context.put("area", area);
			context.put("node1", node1);
			context.put("node2", node2);
			context.put("table", Table);
			context.put("column1", column1);
			context.put("column2", Column2);
			context.put("column3", Column3);
			context.put("column4", Column4);
			context.put("pagenodes", pagenodes);
			context.put("pages", pages);
			context.put("views", views);
			context.put("ctabname", cTabname);
			context.put("buttonarea", buttonArea);
			context.put("buttons1", buttons1);
			context.put("buttons2", buttons2);
			context.put("attribute1", attribute1);
			context.put("attribute2", attribute2);
			context.put("attribute3", attribute3);
			context.put("attribute4", attribute4);

			context.put("widgetList", widgets);

			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(
					"./src/com/cfcc/model/vm/paramsuicomponent.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/model/JUIComponent_" + compuuid
					+ ".model");
			System.out.println("生成文件结束");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}

	private static HashMap<String, String> filterColumns() {
		HashMap<String, String> filter = new HashMap<String, String>();
		filter.put("C_AUDITSTATE", "C_AUDITSTATE");
		filter.put("S_AUDITRESULT", "S_AUDITRESULT");
		filter.put("TS_SYSUPDATE", "TS_SYSUPDATE");
		return filter;
	}
}

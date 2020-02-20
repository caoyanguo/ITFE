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

/**
 * 业务监督模版
 * 
 * @author yiny
 */

public class BizUITemplateGen {

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
		List<String> tables = DBOpertion.lookTable("db2admin", null);
		for (int i = 0; i < tables.size(); i++) {
			String tabName = tables.get(i);
		  if (tabName.startsWith("TAS_TV_")&& !tabName.endsWith("SUB")&& !tabName.endsWith("TEMP")) {
			//if (tabName.equals("TAS_TRL_CMT103COME")) {
				String moduleUUID = UUID.randomUUID().toString();
				String eTable = tabName;// e_name
				String cTable = BizConfigInfo.TableDesMap().get(tabName);
				if (null!= cTable){
				  cTable = cTable.replaceAll("（被监督）", "");// c_name
				}
				String eName = DBOpertion.tableToDto(eTable).replaceAll("Dto",
						"")
						+ "CompareResult";// bean_name
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
			FileOper.saveFile(w.toString(), "c:/bizmodel/" + fileName);
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
		String fileName = "JModule_" + moduleUUID + ".model";// 文件名
		try {
			context.put("uuid", moduleUUID);
			context.put("eTablename", eName);
			context.put("cTableName", cTable);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/model/vm/Testmodule.vm",
					"GBK", context, w);
			FileOper.saveFile(w.toString(), "c:/bizmodel/" + fileName);
		} catch (Exception e) {
			System.out.println("Problem TestModule template : " + e);
		}
	}

	/**
	 * 生成JUIComponent文件
	 * 
	 * @param moduleUUID
	 */
	private static void genUIComponent(String moduleUUID, String eTable,
			String cTable, String eName) {
		List<WidgetData> widgets1 = new ArrayList<WidgetData>();// widgetRefs标签用的
		// List<WidgetData> widgets2 = new ArrayList<WidgetData>();
		List<ColumnData> tmpcolumnDatas = DBOpertion.lookColumnDataByTabName(
				eTable, null);// 字段属性列表
		HashMap<String, String> filter = filterColumns();// 过滤字段
		for (int i = 0; i < tmpcolumnDatas.size(); i++) {
			if (!filter.containsKey(tmpcolumnDatas.get(i).getColid())) {
				WidgetData data1 = new WidgetData();
				data1.setText(UUID.randomUUID().toString());
				data1.setLabel(UUID.randomUUID().toString());
				data1.setCompoent(UUID.randomUUID().toString());
				data1.setColcname(tmpcolumnDatas.get(i).getColname());
				data1.setColename(tmpcolumnDatas.get(i).getColid().replaceAll(
						"_", "").toLowerCase());
				widgets1.add(data1);

				// WidgetData data2 = new WidgetData();
				// data2.setText(UUID.randomUUID().toString());
				// data2.setLabel(UUID.randomUUID().toString());
				// data2.setCompoent(UUID.randomUUID().toString());
				// data2.setColcname(tmpcolumnDatas.get(i).getColname());
				// data2.setColename(tmpcolumnDatas.get(i).getColid().replaceAll(
				// "_", "").toLowerCase());
				// widgets2.add(data2);
			}
		}

		String id1 = UUID.randomUUID().toString();
		String name1 = UUID.randomUUID().toString();
		String labelid1 = UUID.randomUUID().toString();
		String labelname1 = UUID.randomUUID().toString();
		String componentid1 = UUID.randomUUID().toString();
		String componentname1 = UUID.randomUUID().toString();

		String compuuid = UUID.randomUUID().toString();
		String compcname = cTable + "_业务监督";
		String compename = eName;
		String dtoclz = CommonUtil.tableToDtoName(eTable.replace("TBS", "TAS"));
		String moduleuuid = moduleUUID + ".model#" + moduleUUID;
		String model = UUID.randomUUID().toString();
		String direction1 = UUID.randomUUID().toString();
		String direction2 = UUID.randomUUID().toString();
		String attribute1 = UUID.randomUUID().toString();
		String attribute2 = UUID.randomUUID().toString();
		String attribute4 = UUID.randomUUID().toString();
		String attribute3 = UUID.randomUUID().toString();
		String attribute5 = UUID.randomUUID().toString();
		
		String area = UUID.randomUUID().toString();
		String node = UUID.randomUUID().toString();
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
		String cTabname = cTable + "_业务监督";
		String buttonArea = UUID.randomUUID().toString();
		String buttons1 = UUID.randomUUID().toString();
		String buttons2 = UUID.randomUUID().toString();
		try {

			context.put("id1", id1);
			context.put("name1", name1);
			context.put("labelid1", labelid1);
			context.put("labelname1", labelname1);
			context.put("componentid1", componentid1);
			context.put("componentname1", componentname1);

			context.put("compuuid", compuuid);
			context.put("compcname", compcname);
			context.put("compename", compename);
			context.put("dtoclz", dtoclz);
			context.put("moduleuuid", moduleuuid);
			context.put("model", model);
			context.put("direction1", direction1);
			context.put("direction2", direction2);
			context.put("area", area);
			context.put("node", node);
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
			context.put("attribute4", attribute4);
			context.put("attribute3", attribute3);
			context.put("attribute5", attribute5);

			context.put("tasList", widgets1);
			// context.put("tbsList", widgets2);

			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/model/vm/BizJUI.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/bizmodel/JUIComponent_"
					+ compuuid + ".model");
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

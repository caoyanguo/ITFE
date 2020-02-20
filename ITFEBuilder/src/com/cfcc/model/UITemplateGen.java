package com.cfcc.model;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cfcc.FileOper;
import com.cfcc.database.ColumnData;
import com.cfcc.database.DBOpertion;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.model.data.WidgetData;
import com.cfcc.itfe.config.BizConfigInfo;
import com.cfcc.itfe.util.CommonUtil;
/**
 * 适用单表的维护界面组件、服务组件对应的MODEL文件的生成。
 * @author wxy
 *
 */
public class UITemplateGen {
	
	static VelocityContext context = new VelocityContext();
	static {
		BasicConfigurator.configure();
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
//		"/config/Generator.xml");
		ContextFactory.setContextFile("config/ContextLoader_01.xml");
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
		List<String> tables = DBOpertion.lookTable("db2admin", "itfedb");
		for (int i = 0; i < tables.size(); i++) {
			String tabName = tables.get(i);
			if(tabName.startsWith("TS_BANKCODE")){
				String moduleUUID = UUID.randomUUID().toString();
				String serviceModuleUUID = UUID.randomUUID().toString();
				String eTable = tabName;
				//String cTable = BizConfigInfo.TableDesMap().get(tabName);
			    String cTable = "银行账号维护";
				String dtoName = DBOpertion.tableToDto(eTable);
				String eName = dtoName.replaceAll("Dto", "");
				genModule(moduleUUID, eTable, cTable, eName);
				genUIComponent(moduleUUID, eTable, cTable, eName,serviceModuleUUID, "itfedb");
				genService(moduleUUID, eTable, cTable, eName,dtoName,serviceModuleUUID);

			}
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
			Velocity.mergeTemplate("./src/com/cfcc/model/vm/module.vm", "GBK", context, w);
			FileOper.saveFile(w.toString(), "c:/test/" + fileName);
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
			String cTable, String eName,String serviceModuleUUID, String db) {
		List<WidgetData> widgets = new ArrayList<WidgetData>();
		List<ColumnData> tmpcolumnDatas = DBOpertion.lookColumnDataByTabName(eTable,db);
		HashMap<String, String> filter = filterColumns();
		for (int i = 0; i < tmpcolumnDatas.size(); i++) {
			if (!filter.containsKey(tmpcolumnDatas.get(i).getColid())) {
				WidgetData data = new WidgetData();
				data.setText(UUID.randomUUID().toString());
				data.setLabel(UUID.randomUUID().toString());
				data.setCompoent(UUID.randomUUID().toString());
				data.setColcname(tmpcolumnDatas.get(i).getColname());
				data.setColename(tmpcolumnDatas.get(i).getColid().replaceAll("_", "").toLowerCase());
				widgets.add(data);
			}
		}

		String compuuid = UUID.randomUUID().toString();
		String compcname = cTable ;//+ 
		String compename = eName;
		String dtoclz = com.cfcc.itfe.util.CommonUtil.tableToDtoName(eTable);//(eTable.replace("TBS", "TAS"));
		String moduleuuid = moduleUUID + ".model#" + moduleUUID;
		String servicemoduleuuid = "JServiceComponet_"+serviceModuleUUID+ ".model#" + serviceModuleUUID;
		String model = UUID.randomUUID().toString();
		String direction1 = UUID.randomUUID().toString();
//		String direction2 = UUID.randomUUID().toString();
//		String direction3 = UUID.randomUUID().toString();
//		String direction4 = UUID.randomUUID().toString();
		String direction5 = UUID.randomUUID().toString();
		String direction6 = UUID.randomUUID().toString();
		String direction7 = UUID.randomUUID().toString();
		String direction8 = UUID.randomUUID().toString();
		String direction9 = UUID.randomUUID().toString();
//		String direction10 = UUID.randomUUID().toString();
//		String direction11 = UUID.randomUUID().toString();
//		String direction12 = UUID.randomUUID().toString();
//		String direction13 = UUID.randomUUID().toString();
//		String direction14 = UUID.randomUUID().toString();
//		String direction15 = UUID.randomUUID().toString();
//	
		String direction17 = UUID.randomUUID().toString();
		
		String attribute1 = UUID.randomUUID().toString();
		String attribute2 = UUID.randomUUID().toString();
//		String attribute3 = UUID.randomUUID().toString();
//		String attribute4 = UUID.randomUUID().toString();
		
		String area = UUID.randomUUID().toString();//contentAreas id
		String node1 = UUID.randomUUID().toString();//contentareanode id 录入
//		String node2 = UUID.randomUUID().toString();//查询条件
//		String node3 = UUID.randomUUID().toString();//查询结果
//		String node4 = UUID.randomUUID().toString();//复核查询条件
//		String node5 = UUID.randomUUID().toString();//复核查询结果
//		String node6 = UUID.randomUUID().toString();//维护查询条件
		String node7 = UUID.randomUUID().toString();//维护查询结果
		String node8 = UUID.randomUUID().toString();//修改
		
//		String table1 = UUID.randomUUID().toString();//查询结果列表
//		String table2 = UUID.randomUUID().toString();//复核查询结果列表
		String table3 = UUID.randomUUID().toString();//维护查询结果列表

		String pagenodes = UUID.randomUUID().toString();
		//String pages1 = UUID.randomUUID().toString();//录入
		String views1_1 = UUID.randomUUID().toString();
		
//		String pages2 = UUID.randomUUID().toString();//查询
//		String views2_1 = UUID.randomUUID().toString();
//		String views2_2 = UUID.randomUUID().toString();
//		
		String pages3 = UUID.randomUUID().toString();//维护
//		String views3_1 = UUID.randomUUID().toString();
		String views3_2 = UUID.randomUUID().toString();
		String views3_3 = UUID.randomUUID().toString();
//		
//		String pages4 = UUID.randomUUID().toString();//复核
//		String views4_1 = UUID.randomUUID().toString();
//		String views4_2 = UUID.randomUUID().toString();
//		
		
		String cTabname = cTable ;//+ "_参数监督";
		String buttonarea1 = UUID.randomUUID().toString();//录入
		String buttons1 = UUID.randomUUID().toString();
		
//		String buttonarea2_1 = UUID.randomUUID().toString();//查询
//		String buttons2_1 = UUID.randomUUID().toString();
//		String buttonarea2_2 = UUID.randomUUID().toString();
//		String buttons2_2 = UUID.randomUUID().toString();
//		
//		String buttonarea3_1 = UUID.randomUUID().toString();//维护
//		String buttons3_1 = UUID.randomUUID().toString();
		String buttonarea3_2 = UUID.randomUUID().toString();
		String buttons3_2 = UUID.randomUUID().toString();
		String buttons3_3 = UUID.randomUUID().toString();
//		String buttons3_4 = UUID.randomUUID().toString();
//		String buttons3_5 = UUID.randomUUID().toString();
//		String buttons3_6 = UUID.randomUUID().toString();
		String buttons3_9 = UUID.randomUUID().toString();
		String buttonarea3_3 = UUID.randomUUID().toString();
		String buttons3_7 = UUID.randomUUID().toString();
		String buttons3_8 = UUID.randomUUID().toString();
//		
//		String buttonarea4_1 = UUID.randomUUID().toString();//复核
//		String buttons4_1 = UUID.randomUUID().toString();
//		String buttonarea4_2 = UUID.randomUUID().toString();
//		String buttons4_2 = UUID.randomUUID().toString();
//		String buttons4_3 = UUID.randomUUID().toString();
//		String buttons4_4 = UUID.randomUUID().toString();
		
		//String buttons2 = UUID.randomUUID().toString();
		try {

			context.put("compuuid", compuuid);
			context.put("compcname", compcname);
			context.put("compename", compename);
			context.put("dtoclz", dtoclz);
			context.put("moduleuuid", moduleuuid);
			context.put("model", model);
			context.put("direction1", direction1);
//			context.put("direction2", direction2);
//			context.put("direction3", direction3);
//			context.put("direction4", direction4);
			context.put("direction5", direction5);
			context.put("direction6", direction6);
			context.put("direction7", direction7);
			context.put("direction8", direction8);
			context.put("direction9", direction9);
//			context.put("direction10", direction10);
//			context.put("direction11", direction11);
//			context.put("direction12", direction12);
//			context.put("direction13", direction13);
//			context.put("direction14", direction14);
//			context.put("direction15", direction15);
			//context.put("direction16", direction16);
			context.put("direction17", direction17);
			
			context.put("area", area);
			context.put("node1", node1);
//			context.put("node2", node2);
//			context.put("node3", node3);
//			context.put("node4", node4);
//			context.put("node5", node5);
//			context.put("node6", node6);
			context.put("node7", node7);
			context.put("node8", node8);
//			context.put("table1", table1);
//			context.put("table2", table2);
			context.put("table3", table3);
			//context.put("pages1", pages1);
			//context.put("pages2", pages2);
			context.put("pages3", pages3);
			//context.put("pages4", pages4);
			context.put("views1_1", views1_1);
//			context.put("views2_1", views2_1);
//			context.put("views2_2", views2_2);
//			context.put("views3_1", views3_1);
			context.put("views3_2", views3_2);
			context.put("views3_3", views3_3);
//			context.put("views4_1", views4_1);
//			context.put("views4_2", views4_2);

			context.put("pagenodes", pagenodes);
			context.put("ctabname", cTabname);

			context.put("buttonarea1", buttonarea1);
//			context.put("buttonarea2_1", buttonarea2_1);
//			context.put("buttonarea2_2", buttonarea2_2);
//			context.put("buttonarea3_1", buttonarea3_1);
			context.put("buttonarea3_2", buttonarea3_2);
			context.put("buttonarea3_3", buttonarea3_3);
//			context.put("buttonarea4_1", buttonarea4_1);
//			context.put("buttonarea4_2", buttonarea4_2);
			
			context.put("buttons1", buttons1);
//			context.put("buttons2_1", buttons2_1);
//			context.put("buttons2_2", buttons2_2);
//			context.put("buttons3_1", buttons3_1);
			context.put("buttons3_2", buttons3_2);
			context.put("buttons3_3", buttons3_3);
//			context.put("buttons3_4", buttons3_4);
//			context.put("buttons3_5", buttons3_5);
//			context.put("buttons3_6", buttons3_6);
			context.put("buttons3_7", buttons3_7);
			context.put("buttons3_8", buttons3_8);
			context.put("buttons3_9", buttons3_9);
			
//			context.put("buttons4_1", buttons4_1);
//			context.put("buttons4_2", buttons4_2);
//			context.put("buttons4_3", buttons4_3);
//			context.put("buttons4_4", buttons4_4);
			
			context.put("attribute1", attribute1);
			context.put("attribute2", attribute2);
//			context.put("attribute3", attribute3);
//			context.put("attribute4", attribute4);

			context.put("widgetList", widgets);
			context.put("servicemoduleuuid", servicemoduleuuid);

			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(
					"./src/com/cfcc/model/vm/pui.vm", "GBK", context, w);
			FileOper.saveFile(w.toString(), "c:/test/JUIComponent_" + compuuid
					+ ".model");
			System.out.println("生成UI结束" );
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}
	
	/**
	 * 生成Sevice文件
	 * 
	 * @param moduleUUID
	 */
	private static void genService(String moduleUUID, String eTable,
			String cTable, String eName,String dtoName,String serviceModuleUUID) {
		
		String methods1 = UUID.randomUUID().toString();
//		String methods2 = UUID.randomUUID().toString();
//		String methods3 = UUID.randomUUID().toString();
		String methods4 = UUID.randomUUID().toString();
		String methodParams11 = UUID.randomUUID().toString();
//		String methodParams21 = UUID.randomUUID().toString();
//		String methodParams31 = UUID.randomUUID().toString();
//		String methodParams32 = UUID.randomUUID().toString();
		String methodParams41 = UUID.randomUUID().toString();
		String methodParams51 = UUID.randomUUID().toString();
		String methodExceptions1 =UUID.randomUUID().toString();
//		String methodExceptions2 =UUID.randomUUID().toString();
//		String methodExceptions3 =UUID.randomUUID().toString();
		String methodExceptions4 =UUID.randomUUID().toString();
		String compuuid = serviceModuleUUID;
		String service = compuuid;
		String cService = cTable+"服务组件";
		eName = eName.replaceAll("Params", "");
		//String eService = eName+"Service";
		String eService = eName;
		
		
		try {
			context.put("methods1", methods1);
//			context.put("methods2", methods2);
//			context.put("methods3", methods3);
			context.put("methods4", methods4);
			context.put("methodParams11", methodParams11);
//			context.put("methodParams21", methodParams21);
//			context.put("methodParams31", methodParams31);
//			context.put("methodParams32", methodParams32);
			context.put("methodParams41", methodParams41);
			context.put("methodParams51", methodParams51);
			context.put("methodExceptions1", methodExceptions1);
//			context.put("methodExceptions2", methodExceptions2);
//			context.put("methodExceptions3", methodExceptions3);
			context.put("methodExceptions4", methodExceptions4);
			context.put("service", service);
			context.put("cService", cService);
			context.put("eService", eService);
			context.put("dtoName", dtoName);
			//context.put(key, value)
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(
					"./src/com/cfcc/model/vm/service.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/test/JServiceComponet_" + compuuid
					+ ".model");
			System.out.println("生成JService文件结束");
	} catch (Exception e) {
		System.out.println("Problem merging template : " + e);
	}
		
	}
	
	private static HashMap<String, String> filterColumns() {
		HashMap<String, String> filter = new HashMap<String, String>();
		filter.put("S_OPRTFLAG", "S_OPRTFLAG");
//		filter.put("S_AUDITRESULT", "S_AUDITRESULT");
//		filter.put("TS_SYSUPDATE", "TS_SYSUPDATE");
		return filter;
	}
}

package com.cfcc.params;

import java.io.StringWriter;
import java.nio.Buffer;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.cfcc.FileOper;
import com.cfcc.database.DBOpertion;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.loader.ContextFactory;

/**
 * Action template generate
 */

public class ParamsInsertBizGen {
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
		//genTest();
		/* lets make a Context and put data into it */

		/* lets render a template */

	}

//	private static void genTest() {
//		List list1 = new ArrayList();
//		List list2 = new ArrayList();
//		
//		for (int i = 0; i < 10; i++) {
//			list1.add(Integer.valueOf(i).toString());
//			String m=(String) list1.get(i);
//			System.out.println(m);
//		}
//
//		list2.add("m");
//		list2.add("n");
//		
//		list2.addAll(list1);
//		for(int i=0;i<list2.size();i++){
//			System.out.println(list2.get(i));
//		}
//		
//	}

	/**
	 * 生成插入业务表的代码
	 */
	private static void genIns() {

		HashMap<String, String> map1 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Tips_Biz_01_Table");

		HashMap<String, String> map2 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Tips_Biz_02_Table");

		HashMap<String, String> map3 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Payout_Biz_03_Table");

		HashMap<String, String> map4 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Payout_Biz_04_Table");

		HashMap<String, String> map5 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Jzzf_Biz_05_Table");

		HashMap<String, String> map6 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Jzzf_Biz_06_Table");

		HashMap<String, String> map7 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Tips_Biz_07_Table");

		HashMap<String, String> map8 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Income_Biz_08_Table");

		HashMap<String, String> map9 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Income_Biz_09_Table");

		HashMap<String, String> map10 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Income_Biz_10_Table");

		HashMap<String, String> map11 = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("Income_Biz_11_Table");

//		HashMap<String, String> map12 = (HashMap<String, String>) ContextFactory
//				.getApplicationContext().getBean("Jzzf_Biz_11_Table");

		List<TableName> list = new ArrayList<TableName>();
		

		list.addAll(doit1(map1));
		list.addAll(doit1(map2));
		list.addAll(doit1(map3));
		list.addAll(doit1(map4));
		list.addAll(doit1(map5));
		list.addAll(doit1(map6));
		list.addAll(doit1(map7));
		list.addAll(doit1(map8));
		list.addAll(doit1(map9));
		list.addAll(doit1(map10));
		list.addAll(doit1(map11));
		//list.addAll(doit1(map12));

		HashMap<String, TableName> allMap=new HashMap<String, TableName>();
		
		for(TableName table: list){
			allMap.put(table.getIndex(), table);
		}
		
		
		
		
		try {
			context.put("BizTableList", allMap.values());
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/params/vm/bizinsert.vm",
					"GBK", context, w);

			FileOper.saveFile(w.toString(), "c:/BizInsSqlConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}

	}

	private static String getColNames(String table) {
		StringBuffer colStrs = new StringBuffer();
		HashMap<String, String> fileterColMap = new HashMap<String, String>();
		String col1 = "s_AuditResult".toUpperCase();
		String col2 = "c_AuditState".toUpperCase();
		String col3="ts_sysupdate".toUpperCase();
		fileterColMap.put(col1, col1);
		fileterColMap.put(col2, col2);
		fileterColMap.put(col3,col3);
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

	private static List doit1(HashMap<String, String> map) {

		Set<String> keys = map.keySet();
		Iterator<String> iKeys = keys.iterator();
		List<TableName> list = new ArrayList<TableName>();
		while (iKeys.hasNext()) {
			String key = iKeys.next();// key
			String biztype = key.substring(key.length() - 2, key.length());// 业务类型
			String value_temp = (String) map.get(key);// valuetemp表为tbs则取

			TableName table = new TableName();
			if (value_temp.substring(0, 3).equals("TBS")) {
				table.setSrctable(value_temp);// tbs表

				table.setIndex(table.getSrctable() + "_" + biztype); // beanid
				table.setDestable(table.getSrctable().replace("TBS", "TAS"));// tas表

				String strColumns = getColNames(table.getDestable());
				table.setStrColumns(strColumns);// 字段列表

				list.add(table);
			}
		}

		return list;
	}

	// private static List<TableName> addlist(List<TableName> list1,
	// List<TableName> list2) {
	// for (int i = 0; i < list2.size(); i++) {
	// list1.add(list2.get(i));
	// }
	//
	// return list1;
	// }
}

package com.cfcc.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.cfcc.database.DBOpertion;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.config.BizConfigInfo;
import com.cfcc.itfe.util.CommonUtil;

public class PublicModelGen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
		String file = "D:/3CODE/TASApp/ModelProject/model/JUIComponent_4c488a33-fc50-4cf8-b79b-57e72d2ea00e.model";
		//������ͷ��Ϣ
		groupheadModelgen("Biz_9999_Table","�ʽ�",file);
		groupheadModelgen("Biz_9999_Table","ƾ֤",file);
		
		//��ͨ��
		incomebizModelGen("Biz_12_Table",file);
		//���ӱ�
//		futhersubModelGen("Biz_09_Table");
		System.out.print("end");
	}

	
	
	
	public static void groupheadModelgen(String beanid,String flag,String file) {
		//String file = "D:/3CODE/TASApp/ModelProject/model/JUIComponent_4c488a33-fc50-4cf8-b79b-57e72d2ea00e.model";
		IModelGenerator mg = new ModelGeneratorImpl();
		HashMap<String, String> map = BizConfigInfo.BizTable(beanid);
		java.util.Iterator<String> it = map.values().iterator();
		HashMap<String, String> attribute = new HashMap<String, String>();
		while (it.hasNext()) {
			String table = it.next();
			String dtoname = CommonUtil.tableToDtoName(table);
			String bind = table.toLowerCase().replaceAll("_", "") + "Dto";
			String bindlist = table.toLowerCase().replaceAll("_", "") + "List";

			HashMap<String, String> colMap = DBOpertion
					.lookColumnDataByTabNameWithHashMap(table, null);
			
				mg
						.generateTablearea(
								file,
								dtoname, colMap, table, flag +"���¼����", bindlist,
								false, null);
				attribute.put(bindlist, "java.util.ArrayList");
			}
		mg.generateAttribute(file,attribute);
	}
	
	
	public static void incomebizModelGen(String beanid,String file) {
		//String file = "D:/liuliang_view2/TAS/3CODE/TASApp/ModelProject/model/JUIComponent_27ffbd5a-5467-4852-9591-b0f78fa711fc.model";
		IModelGenerator mg = new ModelGeneratorImpl();
		HashMap<String, String> map = BizConfigInfo.BizTable(beanid);

		HashMap<String, String> tmpmap = new HashMap<String, String>();
		java.util.Iterator<String> it1 = map.values().iterator();
		ArrayList<String> list = new ArrayList<String>();
		while (it1.hasNext()) {
			String table = it1.next();
			if(!tmpmap.containsKey(table)){
				tmpmap.put(table, table);
				list.add(table);
			}
			
		}

		HashMap<String, String> attribute = new HashMap<String, String>();
		for(String table:list) {
			String dtoname = CommonUtil.tableToDtoName(table);
			String bind = table.toLowerCase().replaceAll("_", "") + "Dto";
			String bindlist = table.toLowerCase().replaceAll("_", "") + "List";
			HashMap<String, String> colMap = DBOpertion
					.lookColumnDataByTabNameWithHashMap(table, null);
			
			if (table.startsWith("TAS")) {
				mg.generateTextarea(file, dtoname, colMap, table, "�ල��Ϣ¼����_"+BizConfigInfo.TableDesMap()
						.get(table),
						bind, false, null, false);
			} else {
				mg.generateTextarea(file, dtoname, colMap, table,
						"���ල��Ϣ��ʾ��", bind, false, null, true);
			}
			attribute.put(bind, dtoname);

			
//			 mg
//			 .generateTablearea(
//			 file,
//			 dtoname, colMap, table, "���ලƾ֤��Ϣ��ʾ��_��ϸ", bindlist,
//			 false, null);
//			 attribute.put(bindlist, "java.util.ArrayList");

		}
		mg.generateAttribute(file, attribute);

	}
	
	/**
	 * ���ɸ��ӱ�
	 * @param beanid
	 */
	public static void futhersubModelGen(String beanid){
		String file = "D:/liuliang_view2/TAS/3CODE/TASApp/ModelProject/model/JUIComponent_ebe2b303-9c6a-4ea1-9bc6-987d4867fb67.model";
		IModelGenerator mg = new ModelGeneratorImpl();
		HashMap<String, String> map = BizConfigInfo.BizTable(beanid);

		HashMap<String, String> tmpmap = new HashMap<String, String>();
		java.util.Iterator<String> it1 = map.values().iterator();
		ArrayList<String> list = new ArrayList<String>();
		while (it1.hasNext()) {
			String table = it1.next();
			if(!tmpmap.containsKey(table)){
				tmpmap.put(table, table);
				list.add(table);
			}
			
		}

//		java.util.Iterator<String> it = tmpmap.values().iterator();

		

		HashMap<String, String> attribute = new HashMap<String, String>();
		for(String table:list) {
			String dtoname = CommonUtil.tableToDtoName(table);
			String bind = table.toLowerCase().replaceAll("_", "") + "Dto";
			String bindlist = table.toLowerCase().replaceAll("_", "") + "List";
			HashMap<String, String> colMap = DBOpertion
					.lookColumnDataByTabNameWithHashMap(table, null);
			if (table.endsWith("MAIN") ||table.endsWith("DETAIL")){
				if (table.startsWith("TAS")) {
					mg.generateTextarea(file, dtoname, colMap, table, "�ලƾ֤��Ϣ¼����_"+BizConfigInfo.TableDesMap()
							.get(table),
							bind, false, null, false);
				} else {
					mg.generateTextarea(file, dtoname, colMap, table,
							"���ලƾ֤��Ϣ¼����_"+BizConfigInfo.TableDesMap()
							.get(table), bind, false, null, true);
				}
				attribute.put(bind, dtoname);
			}else{
			 mg
			 .generateTablearea(
			 file,
			 dtoname, colMap, table, "���ලƾ֤��Ϣ��ʾ��_��ϸ", bindlist,
			 false, null);
			 attribute.put(bindlist, "java.util.ArrayList");
			}
		}
		mg.generateAttribute(file, attribute);

	}

}

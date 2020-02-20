package com.cfcc.model;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cfcc.database.DBOpertion;
import com.cfcc.devplatform.model.Column;
import com.cfcc.devplatform.model.JAttribute;
import com.cfcc.devplatform.model.JContentArea;
import com.cfcc.devplatform.model.JModel;
import com.cfcc.devplatform.model.JProperty;
import com.cfcc.devplatform.model.JUIComponent;
import com.cfcc.devplatform.model.ModelFactory;
import com.cfcc.devplatform.model.Table;
import com.cfcc.devplatform.model.Text;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.util.NamingConvention;
import com.cfcc.itfe.config.BizConfigInfo;
import com.cfcc.itfe.util.CommonUtil;

public class ModelGeneratorImpl extends AbstractModelGenerator {
	Class<?> dtoclass;

	public void generateTablearea(String uicomponentefile, String dtoclassname,
			Map<String, String> infomap, String contentaraename,
			String contentaraname, String binding, boolean visible,
			List<String> tableareaNotGenerate) {
		initfile(uicomponentefile);
		JUIComponent ui = findUICompinent();
		if (ui != null) {
			try {
				initClazz(dtoclassname);
				Field[] fieds = dtoclass.getDeclaredFields();
				// String databaseTableName = (String) (infoclass
				// .getField(IModelGenerator.TABLENAME_FIELD_NAME)
				// .get(infoInstance));
				removegenerated(ui, IModelGenerator.GENERATE_TYPE_TABLE
						+ dtoclassname);
				JContentArea tablearea = createJContentArea(IModelGenerator.GENERATE_TYPE_TABLE
						+ dtoclassname);
				tablearea.setVisible(Boolean.TRUE);
				generateTableArea(tablearea, fieds, tableareaNotGenerate,
						infomap, contentaraename, contentaraname, binding);
				ui.getContentAreas().getContentareanode().add(tablearea);
				save(ui);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void generateTextarea(String uicomponentefile, String dtoclassname,
			Map<String, String> infomap, String contentaraename,
			String contentaraname, String binding, boolean visible,
			List<String> textareaNotGenerate,boolean isEdit) {
		initfile(uicomponentefile);
		JUIComponent ui = findUICompinent();
		if (ui != null) {
			try {
				initClazz(dtoclassname);
				Field[] fieds = dtoclass.getDeclaredFields();
				// String databaseTableName = (String) (infoclass
				// .getField(IModelGenerator.TABLENAME_FIELD_NAME)
				// .get(infoInstance));
				removegenerated(ui, IModelGenerator.GENERATE_TYPE_TEXT
						+ dtoclassname);
				JContentArea textarea = createJContentArea(IModelGenerator.GENERATE_TYPE_TEXT
						+ dtoclassname);
				textarea.setVisible(Boolean.TRUE);
				if(isEdit){
					generateTextAreaDetail1(textarea, fieds, textareaNotGenerate,
							infomap, contentaraename, contentaraname, binding);
					ui.getContentAreas().getContentareanode().add(textarea);
				}else{
					generateTextAreaDetail2(textarea, fieds, textareaNotGenerate,
							infomap, contentaraename, contentaraname, binding);
					ui.getContentAreas().getContentareanode().add(textarea);
				}
				
				save(ui);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void generateAttribute(String uicomponentefile,
			Map<String, String> attributeMap) {
		try {
			initfile(uicomponentefile);
			JUIComponent ui = findUICompinent();
			JModel jmodel = ui.getModel();
			for (String key : attributeMap.keySet()) {
				String value = attributeMap.get(key);
				JAttribute jAttribute = ModelFactory.eINSTANCE
						.createJAttribute();
				jAttribute.setId(UUID.randomUUID().toString());
				jAttribute.setName(key);
				jAttribute.setType(value);
				addtomodel(jmodel, jAttribute);
			}

			save(ui);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addtomodel(JModel jmodel, JAttribute attribute) {
		boolean has = false;
		for (JAttribute jAttribute : jmodel.getAttributes()) {
			if (jAttribute.getName() != null) {
				if (jAttribute.getName().equals(attribute.getName())) {
					has = true;
				}
			}
		}
		if (!has) {
			jmodel.getAttributes().add(attribute);
		}

	}

	private void initClazz(String dtoclassname) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		dtoclass = Class.forName(dtoclassname);
	}

	private void generateTableArea(JContentArea tablearea, Field[] fieds,
			List<String> tableareaNotGenerate, Map<String, String> infomap,
			String contentaraename, String contentaraname, String binding)
			throws IllegalAccessException, NoSuchFieldException {

		Table table = ModelFactory.eINSTANCE.createTable();
		table.setName(contentaraname);
		table.setCaption(contentaraname);
		table.setBinding(binding);
		table.setHeight(new Integer(450));
		table.setWidth(new Integer(400));
		List<String> needFieldList = getNeedList(fieds, tableareaNotGenerate);
		for (String fieldname : needFieldList) {
			Column column = ModelFactory.eINSTANCE.createColumn();
			column.setName(fieldname);
			column.setCaption(infomap.get(fieldname));
			column.setBinding(fieldname);
			table.getColumnlist().add(column);
		}
		tablearea.setColumnnum(0);
		tablearea.setName(contentaraename);
		tablearea.setGrouptitle(contentaraname);
		tablearea.getWidgetRefs().add(table);

	}

	private void generateTextAreaDetail1(JContentArea textarea, Field[] fieds,
			List<String> notGenerate, Map<String, String> infomap,
			String contentaraename, String contentaraname, String binding)
			throws IllegalAccessException, NoSuchFieldException {

		List<String> needFieldList = getNeedList(fieds, notGenerate);
		for (String fieldname : needFieldList) {
			Text text = ModelFactory.eINSTANCE.createText();
			text.setName(fieldname);
			text.setCaption(infomap.get(fieldname));
			text.setBinding(fieldname);
			text.setEditable(false);
			textarea.getWidgetRefs().add(text);
		}
		textarea.setColumnnum(2);
		textarea.setName(contentaraename);
		textarea.setGrouptitle(contentaraname);
		textarea.setBinding(binding);

	}
	private void generateTextAreaDetail2(JContentArea textarea, Field[] fieds,
			List<String> notGenerate, Map<String, String> infomap,
			String contentaraename, String contentaraname, String binding)
			throws IllegalAccessException, NoSuchFieldException {

		List<String> needFieldList = getNeedList(fieds, notGenerate);
		for (String fieldname : needFieldList) {
			Text text = ModelFactory.eINSTANCE.createText();
			text.setName(fieldname);
			text.setCaption(infomap.get(fieldname));
			text.setBinding(fieldname);
			textarea.getWidgetRefs().add(text);
		}
		textarea.setColumnnum(2);
		textarea.setName(contentaraename);
		textarea.setGrouptitle(contentaraname);
		textarea.setBinding(binding);

	}
	// private String findNameInfo(Map<String, String> infomap, String
	// fieldname,
	// Object infoInstance) throws IllegalArgumentException,
	// IllegalAccessException {
	// // for (Field field : infoclass.getDeclaredFields()) {
	// // if (fieldname.equals(NamingConvention
	// // .getFieldNameFromColumnName(field.getName()))) {
	// // return (String) field.get(infoInstance);
	// // }
	// // }
	// return infomap.get(fieldname);
	// }

	private JContentArea createJContentArea(String type) {
		JContentArea textarea = ModelFactory.eINSTANCE.createJContentArea();
		addGenerateProperty(textarea, type);
		return textarea;
	}

	private List<String> getNeedList(Field[] fields,
			List<String> notGenerateFields) {
		List<String> needlist = new ArrayList<String>();
		for (int i = 0; i < fields.length; i++) {
			if (isGenerateField(fields[i], notGenerateFields)) {
				needlist.add(fields[i].getName());
			}
		}
		return needlist;
	}

	private void addGenerateProperty(JContentArea jContentArea, String type) {
		JProperty jProperty = ModelFactory.eINSTANCE.createJProperty();
		jProperty.setKey(GENERATE_STRING);
		jProperty.setValue(type);
		jContentArea.getJproperties().add(jProperty);
	}

	private void removegenerated(JUIComponent ui, String generateType) {
		// TODO Auto-generated method stub
		List<JContentArea> removed = new ArrayList<JContentArea>();
		for (JContentArea jContentArea : ui.getContentAreas()
				.getContentareanode()) {
			if (isGenerated(jContentArea, generateType)) {
				removed.add(jContentArea);
			}
		}
		for (JContentArea jContentArea : removed) {
			ui.getContentAreas().getContentareanode().remove(jContentArea);
		}
	}

	private boolean isGenerated(JContentArea jContentArea, String type) {
		for (JProperty jProperty : jContentArea.getJproperties()) {
			if (jProperty.getKey().equals(GENERATE_STRING)) {
				if (jProperty.getValue().equals(type)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isGenerateField(Field field, List<String> notGenerateFields) {
		if (notGenerateFields != null) {
			for (String notgeneratefield : notGenerateFields) {
				if (field.getName().equals(notgeneratefield)) {
					return false;
				}
			}
		}
		return true;
	}

	public static void main(String[] args) {
//		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
//
//		IModelGenerator mg = new ModelGeneratorImpl();
//		HashMap<String, String> map = BizConfigInfo
//				.TipsBizTable("Tips_Biz_01_Table");
//
//		Collection<String> c = map.values();
//		Iterator<String> it = c.iterator();
//
//		HashMap<String, String> map1 = new HashMap<String, String>();
//
//		while (it.hasNext()) {
//			String tmp = it.next();
//			String table = tmp.toLowerCase().replaceAll("_", "") + "Dto";
//			map1.put(table, CommonUtil.tableToDtoName(tmp));
//			if (tmp.startsWith("TAS_")) {
//				HashMap<String, String> colMap = DBOpertion
//						.lookColumnDataByTabNameWithHashMap(tmp, null);
//
//				mg
//						.generateTextarea(
//								"D:/TAS_view/TAS/3CODE/TASApp/ModelProject/model/JUIComponent_a939d2bd-b213-4084-80c3-a504d29b6f8b.model",
//								CommonUtil.tableToDtoName(tmp), colMap, tmp,
//								"数据录入区", table,false, null);
//			} else {
//				mg
//						.generateTextarea(
//								"D:/TAS_view/TAS/3CODE/TASApp/ModelProject/model/JUIComponent_a939d2bd-b213-4084-80c3-a504d29b6f8b.model",
//								CommonUtil.tableToDtoName(tmp), DBOpertion
//										.lookColumnDataByTabNameWithHashMap(
//												tmp, null), tmp, "被监督数据显示区",
//								table, false,null);
//
//			}
//
//		}

	}
}

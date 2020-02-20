package com.cfcc.model;

import java.util.List;
import java.util.Map;

public interface IModelGenerator {
	public static String TABLENAME_FIELD_NAME = "TABLENAME";
	public static String GENERATE_STRING = "TAS_GENERATE_KEY";
	public static String GENERATE_TYPE_TABLE = "TAS_GENERATE_TABLE";
	public static String GENERATE_TYPE_TEXT = "TAS_GENERATE_TEXT";

	public void generateTextarea(String uicomponentefile, String dtoclassname,
			Map<String, String> infomap, String contentaraename,String contentaraname,String binding,boolean visible,
			List<String> textareaNotGenerate,boolean isEdit);

	public void generateTablearea(String uicomponentefile, String dtoclassname,
			Map<String, String> infomap,String contentaraename, String contentaraname,String binding,boolean visible,
			List<String> tableareaNotGenerate);

	public void generateAttribute(String uicomponentefile,
			Map<String, String> attributeMap);
}

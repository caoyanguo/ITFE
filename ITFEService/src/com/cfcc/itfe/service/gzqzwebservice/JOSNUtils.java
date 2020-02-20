/**
 * 
 */
package com.cfcc.itfe.service.gzqzwebservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author Administrator
 * 
 */
public class JOSNUtils {

	//国库代码
	public final static String TRECODE = "TRECODE";
	//日期
	public final static String ACCTDATE = "ACCTDATE";
	//调整期标志
	public final static String TRIMFLAG = "TRIMFLAG";
	//预算种类
	public final static String BUDGETTYPE = "BUDGETTYPE";
	//征收机关代码
	public final static String TAXORGCODE = "TAXORGCODE";
	//辖属标志
	public final static String BELONGFLAG = "BELONGFLAG";
	//是否含款合计
	public final static String DIVIDEGROUP = "DIVIDEGROUP";
	//报表类型
	public final static String BILLTYPE = "BILLTYPE";
	

	public static Map<String, String> getJOSNContent(String param){
		if(StringUtils.isBlank(param)){
			return null;
		}
		Map<String, String> resultMap = new HashMap<String, String>();
		String[] strs = param.split(",");
		String[] splits = null;
		for(String str : strs){
			splits = str.split(":");
			resultMap.put(splits[0], splits[1]);
		}
		if(null != resultMap && resultMap.size() > 0){
		return resultMap;
		}else{
			return null;
		}
	}
}

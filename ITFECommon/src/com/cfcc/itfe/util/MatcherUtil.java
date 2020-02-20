package com.cfcc.itfe.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtil {
	
	private static HashMap<String,Pattern>  patternMap = new HashMap<String,Pattern>();
	
	/**
	 * 按照指定模式pattern 从matchString开始查找匹配的字符串，并依次放到返回值list中
	 * @param matchString 被匹配的字符串
	 * @param pattern 模式
	 * @return
	 */
	public static List<String> findMatchString(String matchString,String patternString){
		List<String> list = new ArrayList<String>();
		Pattern p = findPattern(patternString);
		Matcher m = p.matcher(matchString);
		while(m.find()){
			String s = matchString.substring(m.start(),m.end());
			list.add(s);
		}
		return list;
	}
	
	
	/**
	 * 尝试将整个区域与模式匹配
	 */
	public static boolean isCanMatch(String matchString,String patternString){
		Pattern p = findPattern(patternString);
		Matcher m = p.matcher(matchString);
		return m.matches();
	}
	
	
	
	/**
	 * 
	 */
    private synchronized static Pattern findPattern(String patternString){
    	Pattern p;
    	if(!patternMap.containsKey(patternString)){
    		p = Pattern.compile(patternString);
    		patternMap.put(patternString,p);
    	}else{
    		p = patternMap.get(patternString);
    	}
    	return p;
    }
	
}

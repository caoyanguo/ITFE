package com.cfcc.itfe.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtil {
	
	private static HashMap<String,Pattern>  patternMap = new HashMap<String,Pattern>();
	
	/**
	 * ����ָ��ģʽpattern ��matchString��ʼ����ƥ����ַ����������ηŵ�����ֵlist��
	 * @param matchString ��ƥ����ַ���
	 * @param pattern ģʽ
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
	 * ���Խ�����������ģʽƥ��
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

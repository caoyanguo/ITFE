package com.cfcc.itfe.facade;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringPatternCheckFacade {
	/**
	 * �ж��Ƿ�ͬʱ�������֣���ĸ���ַ�
	 * @param s
	 * @return
	 */
	public static boolean isNumCharAndCell(String s){
		boolean flag = false;
		if(isFindMatch(s,"\\d")&&isFindMatch(s,"[a-zA-Z]") && isFindMatch(s,"[^a-zA-Z_0-9]")){
			flag = true;
		}
		
		return flag;
	}
	
	/**
	 * �ж��Ƿ�Ϊ��ĸ�����ֵ���ϣ�����ͬΪ���ֻ���ͬΪ��ĸ
	 */
	
	public static boolean isNumOrChar(String s){
		return isCanMatch(s,"\\w*");
	}
	
	/**
	 * �ж��Ƿ�ȫ������
	 * @param s
	 * @return
	 */
	public static boolean isAllNum(String s){
		return isCanMatch(s,"\\d*");
	}
	
	
	public static boolean isCanMatch(String s,String regex){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}
	
	public static boolean isFindMatch(String s,String regex){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		return matcher.find();
	}
	
	public static boolean  test1(String s){
		if(isNumOrChar(s)&&!isAllNum(s))
			return true;
		return false;
	}
	
	public static void main(String[] args){
		
		System.out.println(test1("1111111111"));
		System.out.println(test1("asdf2adsf2"));
		System.out.println(test1("zs��123123"));
		System.out.println(test1("adsf"));
		System.out.println(test1("����adsfasdf"));
		System.out.println(test1("_adsfasdf"));
	}

}

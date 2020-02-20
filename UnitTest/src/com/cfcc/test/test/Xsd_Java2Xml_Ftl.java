package com.cfcc.test.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;

public class Xsd_Java2Xml_Ftl {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}
	
//	public static String s1 = "Payment1103"; // 需要循环的节点
//	public static String s2 = "TaxTypeList1103"; // 需要循环的节点
//	public static String s3 = "TaxSubjectList1103"; // 需要循环的节点
	
	public static int count = 1 ; // 循环个数
	
	public static void main(String[] argv) throws FileOperateException{
		
		List s1 = new ArrayList<String>(); // 需要循环的节点
		
		printMsgHead();
		
		String filepath = "D:\\国库前置资料文件\\杭州\\报文转化\\9111.xsd"; // schema 路径 
		
		List<String[]> lists = FileUtil.getInstance().readFileWithLine(filepath, "========");
		HashMap<Integer,String> keymap = new HashMap<Integer,String>();
		
		HashMap<String,List<String>> subkeymap = new HashMap<String,List<String>>();
		
		List<Integer> lineno = new ArrayList<Integer>();
		
		// 第一步 先找出KEY
		for(int i = 0 ; i < lists.size(); i++){
			String tmpstr = (lists.get(i))[0];
			
			if(tmpstr.indexOf("maxOccurs") >= 0){
				s1.add(Xsd_Java2Xml_Ftl.getSubKey(tmpstr));
			}
			
			if(tmpstr.indexOf("<xsd:complexType") >= 0){
				keymap.put(Integer.valueOf(i),getKey(tmpstr));
				lineno.add(Integer.valueOf(i));
			}
		}

		Object[] arrays = lineno.toArray();
		for(int k = 0 ; k < arrays.length ; k++){
			
			int itmp_start = ((Integer) arrays[k]).intValue(); 
			int itmp_end = 0;
			
			if(k == arrays.length -1 ){
				itmp_end = lists.size();
			}else{
				itmp_end = ((Integer) arrays[k+1]).intValue();
			}
			
			List<String> tmplist = new ArrayList<String>();
			for(int j = itmp_start ; j < itmp_end ; j++){
				String subtmpStr = (lists.get(j))[0];
				if(subtmpStr.indexOf("<xsd:element name=") >= 0){
					String subkey = getSubKey(subtmpStr);
					tmplist.add(subkey);
				}
			}
			subkeymap.put(keymap.get(arrays[k]), tmplist);
		}
		
		for(int s = 0 ; s < lineno.size(); s++){
			if(keymap.get(lineno.get(s)).equals("MSG") ){
				printlnKey(keymap.get(lineno.get(s)), subkeymap, 1,"cfx",s1);
			}
		}
		
		System.out.println("</CFX>");
	}
	
	private static void printlnKey(String key,HashMap<String,List<String>> subkeymap,int ispace,String var_root,List<String> s1){
		if(s1.contains(key)){
			String tmpvar = "var" + count;
			
			printSpace(ispace);
			System.out.println("<#list " + var_root + "." + key + " as " + tmpvar + ">");
			count ++ ;
			
			var_root = tmpvar;
			
			printSpace(ispace);
			System.out.print("<" + key + ">");
			List<String> list = subkeymap.get(key);
			if(null == list || list.size() == 0){
				System.out.print("${" + var_root  + "}");
				System.out.println("</" + key + ">");
			}else{
				System.out.println("");
				for(int i = 0; i < list.size(); i++){
					printlnKey(list.get(i), subkeymap, ispace + 1,var_root,s1);
				}
				
				printSpace(ispace);
				System.out.println("</" + key + ">");
			}
			
			printSpace(ispace);
			System.out.println("</#list>");
		}else{
			printSpace(ispace);
			System.out.print("<" + key + ">");
			List<String> list = subkeymap.get(key);
			if(null == list || list.size() == 0){
				System.out.print("${" + var_root + "." + key + "}");
				System.out.println("</" + key + ">");
			}else{
				System.out.println("");
				for(int i = 0; i < list.size(); i++){
					printlnKey(list.get(i), subkeymap, ispace + 1,var_root + "." + key,s1);
				}
				
				printSpace(ispace);
				System.out.println("</" + key + ">");
			}
		}
		
	}
	
	private static void printSpace(int ispace){
		for(int i = 0 ; i < ispace ; i++){
			System.out.print("	");
		}
	}
	
	// 取得KEY
	public static String getKey(String value){
		int start = value.indexOf("name=\"");
		int end = value.indexOf("\">");
		
		return value.substring(start + 6, end);
	}
	
	// 取得SUBKey
	public static String getSubKey(String value){
		int start = value.indexOf("name=\"");
		int end = value.indexOf("\" type=");
		
		if(end <= 0){
			end =  value.indexOf("\" m");
		}
		
		if(end <= 0){
			end =  value.indexOf("\">");
		}
		
		return value.substring(start + 6, end);
	}
	
	public static void printMsgHead() {
		System.out.println("<CFX>");
		printSpace(1);
		System.out.println("<HEAD>");
		printSpace(2);
		System.out.println("<VER>${cfx.HEAD.VER}</VER>");
		printSpace(2);
		System.out.println("<SRC>${cfx.HEAD.SRC}</SRC>");
		printSpace(2);
		System.out.println("<DES>${cfx.HEAD.DES}</DES>");
		printSpace(2);
		System.out.println("<APP>${cfx.HEAD.APP}</APP>");
		printSpace(2);
		System.out.println("<MsgNo>${cfx.HEAD.MsgNo}</MsgNo>");
		printSpace(2);
		System.out.println("<MsgID>${cfx.HEAD.MsgID}</MsgID>");
		printSpace(2);
		System.out.println("<MsgRef>${cfx.HEAD.MsgRef}</MsgRef>");
		printSpace(2);
		System.out.println("<WorkDate>${cfx.HEAD.WorkDate}</WorkDate>");
		printSpace(1);
		System.out.println("</HEAD>");
	}
}

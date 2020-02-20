package com.cfcc.test.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;

public class Xsd_Xml2Java_Ftl {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}
	
	
	
	public static void main(String[] argv) throws FileOperateException{
		List s1 = new ArrayList<String>(); // 需要循环的节点
		
		String filepath = "D:\\国库前置资料文件\\杭州\\报文转化\\3131.xsd"; // schema 路径 
		
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
				keymap.put(Integer.valueOf(i),Xsd_Java2Xml_Ftl.getKey(tmpstr));
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
					String subkey = Xsd_Java2Xml_Ftl.getSubKey(subtmpStr);
					tmplist.add(subkey);
				}
			}
			subkeymap.put(keymap.get(arrays[k]), tmplist);
		}
		
		printHead();
		
		String baseStr = "";
		
		for(int s = 0 ; s < lineno.size(); s++){
			Integer count = lineno.get(s);
			String tmpkey = keymap.get(count);
			
			baseStr = getBaseStr(lineno, keymap, subkeymap, tmpkey, "");
			if(s1.contains(tmpkey)){
				// 标志这个节点需要循环
				printBodys(tmpkey,subkeymap,baseStr);
			}else{
				printBody(s1,tmpkey,subkeymap,baseStr);
			}
		}
		
		System.out.println();
		System.out.println("<import file=\"xml2javahead.xml\" />");
		System.out.println("</smooks-resource-list>");
	}
	

	
	// 取得节点的上一层路径
	public static String getBaseStr(List<Integer> lineno,HashMap<Integer,String> keymap,HashMap<String,List<String>> subkeymap,String key,String root){
		for(int i = 0 ; i < lineno.size(); i++){
			String tmpkey = keymap.get(lineno.get(i));
			List<String> list = subkeymap.get(tmpkey);
			if(list.contains(key)){
				if("".equals(root)){
					root = tmpkey + "/" + key;
				}else{
					root = tmpkey + "/" + root;
				}
				
				return  getBaseStr(lineno, keymap, subkeymap, tmpkey, root);
			}
		}
		
		if("".equals(root)){
			return "CFX";
		}
		
		return root;
	}
	
	// 根据上一层的路径取得上一层的节点
	public static String getBaseCode(String baseStr , String key){
		String[] strs = baseStr.split("/");
		for(int i = 0; i < strs.length ; i++){
			if(strs[i].equals(key)){
				return strs[i-1];
			}
		}
		
		return "++++++++++++++++++++";
	}
	
	public static void printHead(){
		System.out.println("<?xml version=\"1.0\"?>");
		System.out.println("<smooks-resource-list xmlns=\"http://www.milyn.org/xsd/smooks-1.1.xsd\" xmlns:jb=\"http://www.milyn.org/xsd/smooks/javabean-1.2.xsd\">");
		System.out.println();
	}
	
	
	public static void printBody(List s1,String key,HashMap<String, List<String>> map,String baseStr){
		if(key.equals("HEAD")){
			return ;
		}
	
		List<String> list = map.get(key);
		if(null != list && list.size() >0 ){
			System.out.println("<jb:bean beanId=\"" + key +"\" class=\"java.util.HashMap\" createOnElement=\"" +key + "\">");
			
			for(int i = 0 ; i < list.size(); i++){
				String tmpkey = list.get(i);
				if(tmpkey.equals("HEAD")){
					System.out.println("	<jb:wiring property=\"HEAD\" beanIdRef=\"HEAD\" />");
				}else{
					if(null != map.get(tmpkey) && map.get(tmpkey).size() > 0){
						String tmpttt = tmpkey;
						if(s1.contains(tmpkey)){
							tmpttt = tmpkey + "s";
						}
						System.out.println("	<jb:wiring property=\"" + tmpkey +"\" beanIdRef=\"" + tmpttt +"\" />");
					}else{
						System.out.println("	<jb:value property=\"" + tmpkey +"\" decoder=\"String\" data=\"" + baseStr + "/" + tmpkey +"\" />");
					}
				}
			}
			
			System.out.println("</jb:bean>");
		}
	}

	public static void printBodys(String key,HashMap<String, List<String>> map,String baseStr){
		if(key.equals("HEAD")){
			return ;
		}
	
		List<String> list = map.get(key);
		if(null != list && list.size() >0 ){
			
			System.out.println("<jb:bean beanId=\"" + key + "s\" class=\"java.util.ArrayList\" createOnElement=\"MSG\">");
			System.out.println("	<jb:wiring beanIdRef=\"" + key + "\" />");
			System.out.println("</jb:bean>");
			
			System.out.println("<jb:bean beanId=\"" + key +"\" class=\"java.util.HashMap\" createOnElement=\"" +key + "\">");
			
			for(int i = 0 ; i < list.size(); i++){
				String tmpkey = list.get(i);
				if(tmpkey.equals("HEAD")){
					System.out.println("	<jb:wiring property=\"HEAD\" beanIdRef=\"HEAD\" />");
				}else{
					if(null != map.get(tmpkey) && map.get(tmpkey).size() > 0){
						System.out.println("	<jb:wiring property=\"" + tmpkey +"\" beanIdRef=\"" + tmpkey +"s\" />");
					}else{
						System.out.println("	<jb:value property=\"" + tmpkey +"\" decoder=\"String\" data=\"" + baseStr + "/" + tmpkey +"\" />");
					}
				}
			}
			
			System.out.println("</jb:bean>");
		}
	}

	
}

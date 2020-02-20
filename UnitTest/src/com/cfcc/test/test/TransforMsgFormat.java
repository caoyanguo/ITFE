package com.cfcc.test.test;

import java.util.List;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;

public class TransforMsgFormat {
	
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}
	
	public static void main(String[] argv) throws FileOperateException{
//		String filepath = "D:\\国库前置资料文件\\报文转化\\测试报文\\9110.xml"; // xml 路径 
//		List<String[]> lists = FileUtil.getInstance().readFileWithLine(filepath, "========");
//		
//		StringBuffer buf = new StringBuffer();
//		
//		for(int i =0; i< lists.size(); i++){
//			buf.append((lists.get(i)[0]).trim());
//		}
//		
//		System.out.println(buf);
		
		System.out.println(System.getProperty("user.home"));
		System.out.println(System.getProperty("file.separator"));
		
	}

}

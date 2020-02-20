package com.cfcc;

import java.util.List;

import com.cfcc.itfe.exception.FileOperateException;

public class FindConfigFileInfo {
/**
 * ���������ļ���Ϣ
 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			findInfo("c:/","test");
		} catch (FileOperateException e) {
			e.printStackTrace();
		}

	}
public static void findInfo(String configPath,String sContent) throws FileOperateException{
	List<String> configList=FileOper.listAbsFile(configPath);
	for(String sCOnfig :configList ){
		if(sContent.equalsIgnoreCase(sCOnfig)){
			String fileContent=FileOper.readFile(sCOnfig);
			if(fileContent!=null && fileContent.equalsIgnoreCase(sContent)){
				System.out.println("������Ϣ���ڵ��ļ�="+sCOnfig);	
			}
			
		}
	}
}

}

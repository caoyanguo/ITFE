package com.cfcc.test.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.util.FileUtil;

public class PassTest {
	public static void main(String[] args) {
		String path = "E:\\ǰ�ò����ļ�\\01�����ļ�\\07��������\\001121004527201202010000000001.txt";
		
		List<String[]> fileContent;
		try {
			fileContent = FileUtil.getInstance().readFileWithLine(path, ",");
			String[] castr = fileContent.get(fileContent.size()-1);
			if("</CA>".equals(castr[0].toUpperCase())) {
				//�������������ļ��й���CA�Ĳ���remove��
				fileContent.remove(fileContent.size()-1);
				fileContent.remove(fileContent.size()-1);
				fileContent.remove(fileContent.size()-1);
			}
			
			Map<String, List> paygroup=new HashMap<String, List>();
			for(String[] paras:fileContent){
				String payacct=paras[4];
				List list=paygroup.get(payacct);
				if(list!=null){
					list.add(paras);
				}else{
					paygroup.put(payacct, new ArrayList());
					List list2=paygroup.get(payacct);
					list2.add(paras);
				}
			}
		} catch (FileOperateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

package com.cfcc.test.testfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.security.DESPlus;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;

public class ExistFile {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	public static void main(String[] args) throws FileOperateException {
		File dir=new File("E:\\����ǰ����Ŀ��ȫ��\\ʵʩ�嵥\\���ֳ���\\PBC.000043100019.BATCH.OUT"); 
		if (dir.isDirectory()) {
			  File[] f = dir.listFiles();
              for(int i = 0;i < f.length;i++){
            	  String strpath=f[i].getPath();
//            	  System.out.println("�ļ�·����"+strpath);
               		String payout = FileUtil.getInstance().readFile(strpath);
//               		System.out.println("�������ݣ�"+payout);
               		if (payout.contains("<FinOrgCode>2226010000</FinOrgCode>")) {
               		 System.out.println("***************"+strpath);
					}
//               		System.out.println("�Ƿ�������֣�"+payout.contains("<FinOrgCode>2226080100</FinOrgCode>"));
              }

		}
//		String strpath = "E:\\����ǰ����Ŀ��ȫ��\\ʵʩ�嵥\\���ֳ���\\PBC.000043100019.BATCH.OUT\\3139-20140707313900002087-20140705073534025-101.xml";
//		String payout = FileUtil.getInstance().readFile(strpath);
//		System.out.println("�������ݣ�"+payout);
//		System.out.println("�Ƿ�������֣�"+payout.contains("<FinOrgCode>2226080100</FinOrgCode>"));

	}
}

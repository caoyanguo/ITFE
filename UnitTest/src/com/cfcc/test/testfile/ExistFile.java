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
		File dir=new File("E:\\国库前置项目（全）\\实施清单\\吉林长春\\PBC.000043100019.BATCH.OUT"); 
		if (dir.isDirectory()) {
			  File[] f = dir.listFiles();
              for(int i = 0;i < f.length;i++){
            	  String strpath=f[i].getPath();
//            	  System.out.println("文件路径："+strpath);
               		String payout = FileUtil.getInstance().readFile(strpath);
//               		System.out.println("报文内容："+payout);
               		if (payout.contains("<FinOrgCode>2226010000</FinOrgCode>")) {
               		 System.out.println("***************"+strpath);
					}
//               		System.out.println("是否包含文字："+payout.contains("<FinOrgCode>2226080100</FinOrgCode>"));
              }

		}
//		String strpath = "E:\\国库前置项目（全）\\实施清单\\吉林长春\\PBC.000043100019.BATCH.OUT\\3139-20140707313900002087-20140705073534025-101.xml";
//		String payout = FileUtil.getInstance().readFile(strpath);
//		System.out.println("报文内容："+payout);
//		System.out.println("是否包含文字："+payout.contains("<FinOrgCode>2226080100</FinOrgCode>"));

	}
}

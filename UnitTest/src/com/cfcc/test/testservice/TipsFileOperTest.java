package com.cfcc.test.testservice;

import java.io.File;
import java.util.List;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;

public class TipsFileOperTest {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			
			// FileUtil.getInstance().readFile("C:\\crebas.sql");
			List<String> list = FileUtil.getInstance().listFileAbspath(
					"E:\\010");
			for (int i = 0; i < list.size(); i++) {
				StringBuffer buf = new StringBuffer("");
				String file = list.get(i);
				File f = new File(file);
				String name = f.getName();
				List<String> liststr = FileUtil.getInstance().readFileWithLine(
						file);
				for (int j = 0; j < liststr.size(); j++) {
					String str = liststr.get(j);
					if (str.indexOf(",404000") > 0) {
						str = str.replaceAll(",404000", ",");
						
					}
					buf.append(str + "\r\n");
				}
				FileUtil.getInstance().writeFile("E:\\0010\\"+name, buf.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

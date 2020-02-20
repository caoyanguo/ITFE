package com.cfcc.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.cfcc.deptone.common.core.exception.FileOperateException;
import com.cfcc.itfe.security.Md5App;

public class OrgCodeEncrypt {

	public static void main(String[] args){
		String key = "itfe@icfcc.com";
		String filename = "orgcode.properties";
		String configpath = "C:/" + filename;
		List<String> list = new ArrayList<String>();
		//有地方横联的省市核算主体前两位开始
		list.add("15" + key);//山东
		list.add("40" + key);//厦门
		list.add("13" + key);//福州
		list.add("02" + key);//天津
		list.add("38" + key);//深圳
		list.add("24" + key);//云南
		list.add("21" + key);//四川
		list.add("19" + key);//广东
		list.add("32" + key);//青岛
		list.add("20" + key);//广西
		list.add("09" + key);//上海
		list.add("24" + key);//宁波
		list.add("26" + key);//陕西
		list.add("04" + key);//山西
		list.add("12" + key);//安徽
		list.add("24" + key);//深圳
		list.add("01" + key);//北京
		list.add("03" + key);//河北
		list.add("11" + key);//浙江
		//有地方横联的省市核算主体前两位结束
		//上线无纸化核算主体开始
		list.add("180000000002" + key);//湖南省本级
		
		System.out.println("--------------");
		list.add("180206000004" + key);//湖南省本级
		list.add("180911000004" + key);//湖南省本级
		list.add("180905000004" + key);//湖南省本级
		
		
		
		
		//上线无纸化核算主体结束
		StringBuffer fileBuffer = new StringBuffer();
		for(String orgcode : list){
			System.out.println(new Md5App().makeMd5(orgcode));
			fileBuffer.append(new Md5App().makeMd5(orgcode)+System.getProperty("line.separator"));
		}
		try {
			writeFile(configpath, fileBuffer.toString());
			System.out.println("保存文件路径："+configpath);
		} catch (FileOperateException e) {
			e.printStackTrace();
		}
	}
	public static void writeFile(String fileName, String fileContent)
	throws FileOperateException {
		File f = new File(fileName);
		File dir = new File(f.getParent());
		FileOutputStream output = null;
		BufferedOutputStream bos = null;
		try {
		
			if (!dir.exists()) {
				dir.mkdirs();
			}
		
			if (!f.exists()) {
				f.createNewFile(); // 重新创建文件
			}
		
			output = new FileOutputStream(fileName, false);
			bos = new BufferedOutputStream(output, 1024);
			
			bos
					.write(fileContent.getBytes("UTF-8"));
			// bos.write(fileContent.getBytes("GBK")) ;
		
		} catch (IOException e) {
			String msg = new String("写文件失败,IO错误." + fileName);
			System.out.println(msg);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异常." + fileName);
			System.out.println(msg);
			throw new FileOperateException(msg, e);
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception ex) {
					System.out.println("关闭文件出错！");
		
				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					System.out.println("关闭文件出错！");
				}
			}
		}
	}
}

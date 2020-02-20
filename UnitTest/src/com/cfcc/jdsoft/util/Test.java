package com.cfcc.jdsoft.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Test {
	public static void main(String[] args) throws Exception {
		Test t = new Test();
		String fileName = "e:/2012061623330.txt";
		//HmacMD5算法
		String str1 = new String(Hex.encode(MD5.encryptHMAC(t.readFile(fileName), "12345678")));
		System.out.println("计算出来的MD5签名 = "+str1);
		
		String str2 = TripleDES.encrypt(t.readFile(fileName)+"\r\n"+"[[["+str1+"]]]", "123456789012345678901234567890123456789012345678", null);
		//3DES加密
//		String str2 = TripleDES.encrypt("9dsoft", "123456789012345678901234567890123456789012345678", null);
		System.out.println("加密后的字符串 = "+str2);
		t.writeFile("e:/test_des.txt", str2);
		
		String str3 = TripleDES.decrypt(t.readFile("e:/test_des.txt"), "123456789012345678901234567890123456789012345678", null);
		System.out.println("解密后的字符串 = "+str3);
		String desFile = t.readFile("e:/test_des.txt");
		
		String ypEndF = str3.substring(0,str3.lastIndexOf("[[[")-2);
		System.out.println("ypEndf ="+ypEndF);
		String md5_str = str3.substring(str3.lastIndexOf("[[["));
		System.out.println("md5_str = "+md5_str);
		if(("[[["+new String(Hex.encode(MD5.encryptHMAC(ypEndF, "12345678")))+"]]]").equals(md5_str)) {
			System.out.println("哥们你成功了");
		}else {
			System.out.println("处理的："+new String(Hex.encode(MD5.encryptHMAC(ypEndF, "12345678"))));
			System.out.println("未处理的："+md5_str);
		}
//		List<String> strlist = t.readFileWithLine("G:/test_des.txt");
//		for(String s : strlist) {
//			
//		}
		
	}
	
	public String readFile(String fileName)  {
		long lBegin = 0;

		FileInputStream input = null;
		String message = null;
		try {
			input = new FileInputStream(fileName);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];

			do {
				int size = input.read(buffer);
				if (size == -1)
					break;
				byteArray.write(buffer, 0, size);
			} while (true);
			byte[] data = byteArray.toByteArray();
			message = new String(data, "GBK");

		} catch (FileNotFoundException e) {
			String msg = new String("读取文件失败,未找到文�?." + fileName);
		} catch (IOException e) {
			String msg = new String("读取文件失败,IO错误." + fileName);
		} catch (RuntimeException e) {
			String msg = new String("读取文件失败，运行时异常." + fileName);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {

				}
			}
		}

		return message;

	}
	
	public void writeFile(String fileName, String fileContent) {
		long lBegin = 0;
		
		File file = new File(fileName);
		File dir = new File(file.getParent());
		
		FileOutputStream output = null;
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			output = new FileOutputStream(fileName, false);
			output.write(fileContent.getBytes("GBK"));
		
		} catch (IOException e) {
			String msg = new String("写文件失�?,IO错误." + fileName);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异�?." + fileName);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
		
				}
			}
		}

	}
	
	public List<String> readFileWithLine(String fileName){
		List<String> listStr = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName)));
			String data = null;
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				listStr.add(data);
			}
			br.close();
			return listStr;
		} catch (Exception e) {
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
				}
		
			}
		}
		return listStr;

}
	
//	/**
//	 * 按照指定格式返回当前系统时间  
//	 * 
//	 * @return
//	 */
//	public static String getCurrentStringTime(String pattern) {
//		return formatDate(new java.util.Date(),pattern);
//	}
	public static Date strToDate(String str) {
		if (str.length() == 10) {
			return Date.valueOf(str);
		} else if (str.length() == 8) {
			str = str.substring(0, 4) + "-" + str.substring(4, 6) + "-"
					+ str.substring(6, 8);
			return Date.valueOf(str);
		}
		return null;
	}
	public void deleteFiles(String filePath) {
		File tmp1 = new File(filePath);
		// 根据绝对路径进行删除操作
		File file = new File(tmp1.getAbsolutePath());
		if (file.isFile()) {
			file.delete();
		} else {
			File[] f = file.listFiles();
			if (f != null && f.length > 0) {
				for (int i = 0; i < f.length; i++) {
					File tmp = f[i];
					if (tmp.isDirectory()) {
						deleteFiles(tmp.getAbsolutePath());

					}
					tmp.delete();
				}

			}

		}
	}
}

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
		//�еط�������ʡ�к�������ǰ��λ��ʼ
		list.add("15" + key);//ɽ��
		list.add("40" + key);//����
		list.add("13" + key);//����
		list.add("02" + key);//���
		list.add("38" + key);//����
		list.add("24" + key);//����
		list.add("21" + key);//�Ĵ�
		list.add("19" + key);//�㶫
		list.add("32" + key);//�ൺ
		list.add("20" + key);//����
		list.add("09" + key);//�Ϻ�
		list.add("24" + key);//����
		list.add("26" + key);//����
		list.add("04" + key);//ɽ��
		list.add("12" + key);//����
		list.add("24" + key);//����
		list.add("01" + key);//����
		list.add("03" + key);//�ӱ�
		list.add("11" + key);//�㽭
		//�еط�������ʡ�к�������ǰ��λ����
		//������ֽ���������忪ʼ
		list.add("180000000002" + key);//����ʡ����
		
		System.out.println("--------------");
		list.add("180206000004" + key);//����ʡ����
		list.add("180911000004" + key);//����ʡ����
		list.add("180905000004" + key);//����ʡ����
		
		
		
		
		//������ֽ�������������
		StringBuffer fileBuffer = new StringBuffer();
		for(String orgcode : list){
			System.out.println(new Md5App().makeMd5(orgcode));
			fileBuffer.append(new Md5App().makeMd5(orgcode)+System.getProperty("line.separator"));
		}
		try {
			writeFile(configpath, fileBuffer.toString());
			System.out.println("�����ļ�·����"+configpath);
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
				f.createNewFile(); // ���´����ļ�
			}
		
			output = new FileOutputStream(fileName, false);
			bos = new BufferedOutputStream(output, 1024);
			
			bos
					.write(fileContent.getBytes("UTF-8"));
			// bos.write(fileContent.getBytes("GBK")) ;
		
		} catch (IOException e) {
			String msg = new String("д�ļ�ʧ��,IO����." + fileName);
			System.out.println(msg);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("д�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);
			System.out.println(msg);
			throw new FileOperateException(msg, e);
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception ex) {
					System.out.println("�ر��ļ�����");
		
				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					System.out.println("�ر��ļ�����");
				}
			}
		}
	}
}

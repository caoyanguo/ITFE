package com.cfcc.itfe.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.output.FileWriterWithEncoding;

import com.cfcc.itfe.util.SinoDetect;


public class SM3Util {
	
	private static final String KEY = "aaa";

	
	
	/**
	 * ��ǩ����
	 * @param fileName
	 * @param key
	 */
	public void addSM3Sign(String fileName, String key){
		BufferedReader input = null;
		StringBuffer encDataTmp = new StringBuffer();
		String encData = null;
		File file = new File(fileName);
		try {
			String encode =getFileEncoding(fileName);
			if (encode.equals("UTF-8")) {
				String str = readFileUtf8(fileName);
			    file.delete();
		        writeFile(fileName, str);
			}
			input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String lineContent = null;
			while((lineContent = input.readLine())!=null){
				if(!"".equals(lineContent)){
					encDataTmp.append(SM3Process.calculateSign(lineContent, key));
				}
			}
			encData = SM3Process.calculateSign(encDataTmp.toString(), key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (input != null) {
                try {
                	input.close();
                } catch (IOException e1) {
                }
            }
        }
		encData = "<CA>" + encData + "</CA>";
		try {
            //��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			FileWriterWithEncoding writer = new FileWriterWithEncoding(fileName, "GBK", true);
            writer.write("\r\n" + encData);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * ��ǩ����
	 * @param fileName
	 * @param key
	 * @return
	 */
	public boolean verifySM3Sign(String fileName, String key) {
		boolean isCorrect = false;
		BufferedReader input = null;
		StringBuffer encDataTmp = new StringBuffer();
		String encData = null;
		String sign = null;
		File file = new File(fileName);
		try {
			input = new BufferedReader(new FileReader(file));
			String lineContent = null;
			while((lineContent = input.readLine())!=null){
				if(!"".equals(lineContent) && !lineContent.startsWith("<CA>")){
					encDataTmp.append(SM3Process.calculateSign(lineContent, key));
				}else if(lineContent.startsWith("<CA>")){
					sign = lineContent.substring(lineContent.indexOf("<CA>") + 4, lineContent.indexOf("</CA>"));
				}
			}
			encData = SM3Process.calculateSign(encDataTmp.toString(), key);
			if(sign != null && encData != null && sign.equals(encData)){
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (input != null) {
                try {
                	input.close();
                } catch (IOException e1) {
                }
            }
        }
		return isCorrect;
	}
	
	
	  /*�ж��Ƿ�ΪUTF-8��ʽ������UTF8���ļ�ʹ��GBK����
	 */
	private static String getFileEncoding(String fileName) throws IOException {
	 
	    SinoDetect detect = new SinoDetect();
		int i=	detect.detectEncoding(new File(fileName));
	    String code =  SinoDetect.nicename[i];
	    if (null==code || !code.equals("UTF-8")) {
	    	code="GBK";
		}
		return code;
	}
	
	/**
	 * @function������д�뵽�ļ�
	 * @param fileName��Ҫд���ļ�·��
	 * @param fileContent��Ҫд���ļ�����
	 * @return
	 */
	private boolean writeFile(String fileName, String fileContent)
	{
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
			System.out.println("д�ļ�ʧ��,IO����." + fileName+e.toString());
			return false;
		} catch (RuntimeException e) {
			System.out.println("д�ļ�ʧ�ܣ�����ʱ�쳣." + fileName+e.toString());
			return false;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					System.out.println("�ر��ļ�����"+ex.toString());
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * ��InputStream�е�UTF8���ݶ�ȡ���ַ�����
	 * 
	 * @param in
	 *            ������
	 * @return ��¼�������ݵ��ַ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String readFileUtf8(InputStream in) throws IOException {
		StringBuffer content = new StringBuffer();
		if (in == null) {
			// �����Ϊ�գ���ô���ؿ��ַ���
			return content.toString();
		}
		char[] buf = new char[4096];
		int ret = 0;
		BufferedReader bin = new BufferedReader(new InputStreamReader(in,
				"utf-8"));
		ret = bin.read(buf);
		while (ret > 0) {
			content.append(new String(buf, 0, ret));
			ret = bin.read(buf);
		}
		bin.close();
		return content.toString();
	}
	/**
	 * ��utf-8�ļ��е����ݶ�ȡ���ַ�����
	 * 
	 * @param fileName
	 *            �ļ��ľ���·��
	 * @return �����ļ����ݵ��ַ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String readFileUtf8(String fileName) throws FileNotFoundException,
			IOException {
		return readFileUtf8(new FileInputStream(fileName));
	}
	
	
	public static void main(String[] args) {
		
		SM3Util t = new SM3Util();
		
		t.addSM3Sign("C:\\201305097301260.txt", "D69C56265D21016971EDF40240DBA15EBCCC9E628B0E3271");
		Boolean b=t.verifySM3Sign("C:\\201305097301260.txt", "D69C56265D21016971EDF40240DBA15EBCCC9E628B0E3271");
		
		System.out.println(b);
	}
		

}

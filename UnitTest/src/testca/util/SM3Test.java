package testca.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cfcc.itfe.exception.FileOperateException;

public class SM3Test {

	/**
	 * ��ȡ�ļ�����
	 * @param filePath
	 * 			�ļ�·��
	 * @return
	 * @throws Exception
	 * 			��ȡ�ļ�ʧ��
	 */
	static String loadData(String filePath) throws Exception {
		FileInputStream fis = new FileInputStream(filePath);
		byte[] dataInput = new byte[fis.available()];
		fis.read(dataInput);
		fis.close();
		String data = new String(dataInput);
		return data;
	}
	
	/**
	 * ��ָ��������д�뵽ָ�����ļ���
	 * 
	 * @param fileName
	 *            �ļ���
	 * @param fileContent
	 *            �ļ�����
	 * @throws FileOperateException
	 *             �����ļ�ʧ��
	 */
	public static void writeFile(String fileName, String fileContent)
			throws FileOperateException {
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
			String msg = new String("д�ļ�ʧ��,IO����." + fileName);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("д�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);
			throw new FileOperateException(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {

				}
			}
		}
	}
	
	static void testSm3() {
		String data = "";
		String key = "1234567890";
		try {
			data = loadData("D:\\itfe(ningbo)\\SM3�����㷨\\��������\\1.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String signData = SM3Process.calculateSign(data,key);
		System.out.println(signData);
		try {
			writeFile("D:\\itfe(ningbo)\\SM3�����㷨\\��������\\1.txt.dm3",signData);
		} catch (FileOperateException e1) {
			e1.printStackTrace();
		}
		
		try {
			data = loadData("D:\\itfe(ningbo)\\SM3�����㷨\\��������\\1.txt.dm3");
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean isCorrect = false;
		isCorrect = SM3Process.verifySM3Sign(data,key);
		System.out.println(isCorrect);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testSm3();
	}

}

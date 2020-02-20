package testca.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cfcc.itfe.exception.FileOperateException;

public class SM3Test {

	/**
	 * 读取文件内容
	 * @param filePath
	 * 			文件路径
	 * @return
	 * @throws Exception
	 * 			读取文件失败
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
	 * 把指定的内容写入到指定的文件中
	 * 
	 * @param fileName
	 *            文件名
	 * @param fileContent
	 *            文件内容
	 * @throws FileOperateException
	 *             操作文件失败
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
			String msg = new String("写文件失败,IO错误." + fileName);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异常." + fileName);
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
			data = loadData("D:\\itfe(ningbo)\\SM3加密算法\\测试数据\\1.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String signData = SM3Process.calculateSign(data,key);
		System.out.println(signData);
		try {
			writeFile("D:\\itfe(ningbo)\\SM3加密算法\\测试数据\\1.txt.dm3",signData);
		} catch (FileOperateException e1) {
			e1.printStackTrace();
		}
		
		try {
			data = loadData("D:\\itfe(ningbo)\\SM3加密算法\\测试数据\\1.txt.dm3");
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

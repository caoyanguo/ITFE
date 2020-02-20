package process6103;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	private static FileUtil instance = new FileUtil();
	
	
	private FileUtil(){
		
	}
	
	public static FileUtil getInstance(){
		return instance;
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
	public void writeFile(String fileName, String fileContent)
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
			String msg = new String("写文件失败,IO错误." + fileName);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异常." + fileName);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {

				}
			}
		}

	}
	
	/**
	 * 从文本文件中读出字符串
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件中的内容
	 */
	public String readFile(String fileName) {

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
			message = new String(data);

		} catch (FileNotFoundException e) {
			String msg = new String("读取文件失败,未找到文件." + fileName);
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
	
	/**
	 * 按照行读取文件，每行根据split进行分割成字符串数组
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String[]> readFileWithLine(String file, String split) {
		List<String[]> listStr = new ArrayList<String[]>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));
			String data = null;
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				if (split.equals(data.substring(data.length() - 1))) {
					data += " ";
				}
				String[] strArray = data.split(split);
				listStr.add(strArray);

			}
			br.close();
			return listStr;
		} catch (Exception e) {
			return null;
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					return null;
				}

			}
		}

	}
	
	/**
	 * 将文件字符串的分隔符，转换为正确的系统分隔符
	 * @param fileString
	 * @return
	 */
	
	public static String transFileSeparatorToSysSeparator(String fileString){
		return fileString.replace("\\",File.separator).replace("/",File.separator);
	}
	
	/**
	 * 获取fileString系统文件分隔符后面的字符串
	 * @param fileString
	 * @return
	 */
	
	public static String getStringBehindLastFileSeparator(String fileString){
		String sysFileString = transFileSeparatorToSysSeparator(fileString);
		int lastSeparatorIndex = sysFileString.lastIndexOf(File.separator);
		return sysFileString.substring(lastSeparatorIndex+1);
	}

	/**
	 * 获取fileString系统文件的目录
	 * @param fileString
	 * @return
	 */
	public static String getFileDirectoryName(String fileString){
		String sysFileString = transFileSeparatorToSysSeparator(fileString);
		int lastSeparatorIndex = sysFileString.lastIndexOf(File.separator);
		return sysFileString.substring(0,lastSeparatorIndex+1);
	}
}

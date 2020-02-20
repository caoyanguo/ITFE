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
	 * ��ָ��������д�뵽ָ�����ļ���
	 * 
	 * @param fileName
	 *            �ļ���
	 * @param fileContent
	 *            �ļ�����
	 * @throws FileOperateException
	 *             �����ļ�ʧ��
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
			String msg = new String("д�ļ�ʧ��,IO����." + fileName);
		} catch (RuntimeException e) {
			String msg = new String("д�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);
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
	 * ���ı��ļ��ж����ַ���
	 * 
	 * @param fileName
	 *            �ļ���
	 * @return �ļ��е�����
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
			String msg = new String("��ȡ�ļ�ʧ��,δ�ҵ��ļ�." + fileName);
		} catch (IOException e) {
			String msg = new String("��ȡ�ļ�ʧ��,IO����." + fileName);
		} catch (RuntimeException e) {
			String msg = new String("��ȡ�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);
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
	 * �����ж�ȡ�ļ���ÿ�и���split���зָ���ַ�������
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
	 * ���ļ��ַ����ķָ�����ת��Ϊ��ȷ��ϵͳ�ָ���
	 * @param fileString
	 * @return
	 */
	
	public static String transFileSeparatorToSysSeparator(String fileString){
		return fileString.replace("\\",File.separator).replace("/",File.separator);
	}
	
	/**
	 * ��ȡfileStringϵͳ�ļ��ָ���������ַ���
	 * @param fileString
	 * @return
	 */
	
	public static String getStringBehindLastFileSeparator(String fileString){
		String sysFileString = transFileSeparatorToSysSeparator(fileString);
		int lastSeparatorIndex = sysFileString.lastIndexOf(File.separator);
		return sysFileString.substring(lastSeparatorIndex+1);
	}

	/**
	 * ��ȡfileStringϵͳ�ļ���Ŀ¼
	 * @param fileString
	 * @return
	 */
	public static String getFileDirectoryName(String fileString){
		String sysFileString = transFileSeparatorToSysSeparator(fileString);
		int lastSeparatorIndex = sysFileString.lastIndexOf(File.separator);
		return sysFileString.substring(0,lastSeparatorIndex+1);
	}
}

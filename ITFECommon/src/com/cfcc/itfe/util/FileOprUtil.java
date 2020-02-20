/*
 * �������� 2005-8-2
 *
 * �����������ļ�ģ��Ϊ
 * ���� > ��ѡ�� > Java > �������� > �����ע��
 */
package com.cfcc.itfe.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.config.ITFEGlobalBeanId;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.jaf.common.statistics.ServiceMethodStatistics;
import com.cfcc.jaf.core.loader.ContextFactory;

/**
 * �ļ���ȡ�Ͷ������л��������л����
 * 
 */
public class FileOprUtil implements IFileOper {

	/**
	 * �Ƿ���Ҫͳ��ִ��ʱ��
	 * 
	 * @return ���� statistics��
	 */
	public boolean isStatistics() {
		return statistics;
	}

	/**
	 * @param statistics
	 *            Ҫ���õ� statistics��
	 */
	public void setStatistics(boolean statistics) {
		this.statistics = statistics;
	}

	private static Log log = LogFactory.getLog(FileOprUtil.class);

	boolean statistics = false;

	/**
	 * ������д���ļ�
	 * 
	 * @param bytes
	 *            ����
	 * @param filePath
	 *            �ļ�·��
	 * @throws FileOperateException
	 */
	public void writeBytesToFile(byte[] bytes, String filePath)
			throws FileOperateException {
		long lBegin = 0;
		if (statistics)
			lBegin = System.currentTimeMillis();

		FileOutputStream output = null;
		BufferedOutputStream bos = null;
		File f = new File(filePath);
		File dir = new File(f.getParent());
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (!dir.exists()) {
				dir.mkdirs();
			}
			output = new FileOutputStream(filePath);
			bos = new BufferedOutputStream(output,
					ITFECommonConstant.FILE_BUF_SIZE);
			bos.write(bytes);
		} catch (IOException e) {
			String msg = new String("д�ļ�ʧ��,IO����." + filePath);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("д�ļ�ʧ�ܣ�����ʱ�쳣." + filePath);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception ex) {
					log.error("�ر��ļ�������", ex);

				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					log.error("�ر��ļ�������", ex);

				}
			}
		}
		if (statistics) {
			long lEnd = System.currentTimeMillis();
			int len = bytes.length / 1024 + 1;
			ServiceMethodStatistics.record("writeBytesToFile", String
					.valueOf(len)
					+ "KB", lEnd - lBegin);
		}
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
			throws FileOperateException {
		long lBegin = 0;
		if (statistics)
			lBegin = System.currentTimeMillis();

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
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("д�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					log.error("�ر��ļ�������", ex);

				}
			}
		}

		if (statistics) {
			long lEnd = System.currentTimeMillis();
			int len = fileContent.length() / 1024 + 1;
			ServiceMethodStatistics.record("WriteFile", String.valueOf(len)
					+ "KB", lEnd - lBegin);
		}
	}
	
	/**
	 * ��ָ��������д�뵽ָ�����ļ���
	 * 
	 * @param fileName
	 *            �ļ���
	 * @param fileContent
	 *            �ļ�����
	 * @param append
	 *            �Ƿ�׷�ӷ�ʽ
	 * @throws FileOperateException
	 *             �����ļ�ʧ��
	 */
	public void writeFile(String fileName, String fileContent,boolean append)
			throws FileOperateException {
		long lBegin = 0;
		if (statistics)
			lBegin = System.currentTimeMillis();

		File file = new File(fileName);
		File dir = new File(file.getParent());

		FileOutputStream output = null;
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			output = new FileOutputStream(fileName, append);
			output.write(fileContent.getBytes("GBK"));

		} catch (IOException e) {
			String msg = new String("д�ļ�ʧ��,IO����." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("д�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					log.error("�ر��ļ�������", ex);

				}
			}
		}

		if (statistics) {
			long lEnd = System.currentTimeMillis();
			int len = fileContent.length() / 1024 + 1;
			ServiceMethodStatistics.record("WriteFile", String.valueOf(len)
					+ "KB", lEnd - lBegin);
		}
	}

	/**
	 * ���ı��ļ��ж����ַ���
	 * 
	 * @param fileName
	 *            �ļ���
	 * @return �ļ��е�����
	 */
	public String readFile(String fileName) throws FileOperateException {
		long lBegin = 0;
		if (statistics)
			lBegin = System.currentTimeMillis();

		FileInputStream input = null;
		String message = null;
		try {
			input = new FileInputStream(fileName);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			byte[] buffer = new byte[ITFECommonConstant.FILE_BUF_SIZE];

			do {
				int size = input.read(buffer);
				if (size == -1)
					break;
				byteArray.write(buffer, 0, size);
			} while (true);
			byte[] data = byteArray.toByteArray();
			message = new String(data, ITFECommonConstant.CHARSET_NAME);

		} catch (FileNotFoundException e) {
			String msg = new String("��ȡ�ļ�ʧ��,δ�ҵ��ļ�." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (IOException e) {
			String msg = new String("��ȡ�ļ�ʧ��,IO����." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("��ȡ�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {
					log.error("�ر��ļ�������" + fileName, ex);

				}
			}
		}

		if (statistics) {
			long lEnd = System.currentTimeMillis();
			int len = message.length() / 1024 + 1;
			ServiceMethodStatistics.record("ReadFile", String.valueOf(len)
					+ "KB", lEnd - lBegin);
		}
		return message;

	}

	/**
	 * ��ָ���Ķ������л���ָ�����ļ�
	 * 
	 * @param obj
	 *            Ҫ���л����ļ�
	 * @param fileName
	 *            �ļ���
	 * @throws FileOperateException
	 */
	public void serialization(Serializable obj, String fileName)
			throws FileOperateException {
		long lBegin = 0;
		if (statistics)
			lBegin = System.currentTimeMillis();

		File f = new File(fileName);
		FileOutputStream out = null;
		try {
			if (!f.exists()) {
				f.createNewFile(); // ���´����ļ�
			}

			out = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(out,
					ITFECommonConstant.FILE_BUF_SIZE);
			SerializationUtils.serialize(obj, bos);
		} catch (IOException e) {
			String msg = new String("���л�ʧ��,IO����." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("���л�ʧ��,����ʱ�쳣." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		}

		if (statistics) {
			long lEnd = System.currentTimeMillis();
			ServiceMethodStatistics.record("Serialization", obj.getClass()
					.getName(), lEnd - lBegin);
		}

	}

	/**
	 * ��ָ�����ļ������л�
	 * 
	 * @param fileName
	 *            Ҫ�����л����ļ�
	 * @return �����л��Ķ���
	 * @throws FileOperateException
	 *             �����ļ���������
	 */
	public Serializable deserialize(String fileName)
			throws FileOperateException {
		long lBegin = 0;
		if (statistics)
			lBegin = System.currentTimeMillis();

		File f = new File(fileName);
		FileInputStream in = null;
		Serializable obj = null;
		try {
			in = new FileInputStream(f);
			BufferedInputStream bio = new BufferedInputStream(in,
					ITFECommonConstant.FILE_BUF_SIZE);
			obj = (Serializable) SerializationUtils.deserialize(bio);
		} catch (FileNotFoundException e) {
			String msg = new String("�����л�ʧ��,�ļ�δ�ҵ�." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (Exception e) {
			String msg = new String("�����л�ʧ��,IO����." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		}

		if (statistics) {
			long lEnd = System.currentTimeMillis();
			ServiceMethodStatistics.record("Deserialize", obj.getClass()
					.getName(), lEnd - lBegin);
		}
		return obj;

	}

	/**
	 * ɾ��ָ�����ļ�
	 * 
	 * @param name
	 *            �ļ���ȫ·��
	 * @throws FileOperateException
	 *             ���ɾ���ļ��������׳����쳣
	 * @throws FileNotFoundException
	 *             ���Ҫɾ�����ļ����������׳����쳣
	 */
	public void deleteFile(String name) throws FileOperateException,
			FileNotFoundException {
		long lBegin = 0;
		if (statistics)
			lBegin = System.currentTimeMillis();

		File f = new File(name);
		if (f.exists()) {
			try {
				f.delete();
			} catch (Exception e) {
				String msg = new String("ɾ���ļ�ʧ��.");
				log.error(msg, e);
				throw new FileOperateException(msg, e);
			}
		} else {
			// ����ļ���������ֱ�ӷ���
//			throw new FileNotFoundException("Ҫɾ�����ļ�������:" + name);
			log.debug("Ҫɾ�����ļ�������:" + name);
		}

		if (statistics) {
			long lEnd = System.currentTimeMillis();
			ServiceMethodStatistics.record("deleteFile", "", lEnd - lBegin);
		}

	}

	/**
	 * �ݹ�ɾ���ļ�
	 * 
	 * @param filePath
	 */
	public void deleteFiles(String filePath) {
		File tmp1 = new File(filePath);
		// ���ݾ���·������ɾ������
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

	/**
	 * ��ȡ·���������ļ��ľ����ļ���
	 * 
	 * @param filePath
	 * @return
	 */
	public List<String> listFileAbspath(String filePath) {
		List<String> listFile = new ArrayList<String>();
		listAbsPath(filePath, listFile);
		return listFile;
	}

	private void listAbsPath(String filePath, List<String> filesStr) {
		File tmp1 = new File(filePath);
		// ���ݾ���·������ɾ������
		String str = tmp1.getAbsolutePath();
		File file = new File(str);
		if (file.isFile()) {
			filesStr.add(str);
		} else {
			File[] f = file.listFiles();
			if (f == null || f.length <= 0) {
				filesStr.add(str);
			}
			for (int i = 0; i < f.length; i++) {
				File tmp = f[i];
				String strF = tmp.getAbsolutePath();
				if (tmp.isDirectory()) {

					listAbsPath(strF, filesStr);

				} else {
					filesStr.add(strF);
				}

			}
		}
	}

	/**
	 * ��ȡ·���������ļ������·���ļ���
	 * 
	 * @param filePath
	 * @return
	 */
	public List<String> listFileRelpath(String filePath) {
		List<String> listFile = new ArrayList<String>();
		listRelPath(filePath, listFile);
		return listFile;
	}

	private void listRelPath(String filePath, List<String> filesStr) {
		File tmp1 = new File(filePath);
		String str = tmp1.getAbsolutePath();
		String str1 = tmp1.getName();
		File file = new File(str);
		if (file.isFile()) {
			filesStr.add(str1);
		} else {
			File[] f = file.listFiles();
			for (int i = 0; i < f.length; i++) {
				File tmp = f[i];
				String strF = tmp.getAbsolutePath();
				String strx = tmp.getName();
				if (tmp.isDirectory()) {

					listRelPath(strF, filesStr);

				}
				filesStr.add(strx);
			}
		}
	}

	/**
	 * �����ж�ȡ�ļ���ÿ�и���split���зָ���ַ�������
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String[]> readFileWithLine(String file, String split)
			throws FileOperateException {
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
				String[] strArray = data.split(split);
				listStr.add(strArray);

			}
			br.close();
			return listStr;
		} catch (Exception e) {
			log.error(e);
			throw new FileOperateException("��ȡ�ļ������쳣", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("��ȡ�ļ������쳣", e);
				}

			}
		}

	}

	/**
	 * ��ѹ��
	 * 
	 * @param sZipFileName
	 * @param outputDirectory
	 * @throws FileOperateException
	 */
	public List<String> unzip(String sZipFileName, String outputDirectory)
			throws FileOperateException {
		BufferedOutputStream bos = null;
		List<String> resList = new ArrayList<String>();
		try {
			JarFile jarf = new JarFile(sZipFileName);
			Enumeration<JarEntry> emu = jarf.entries();
			int i = 0;
			while (emu.hasMoreElements()) {
				JarEntry entry =  emu.nextElement();
				// ���Ŀ¼��Ϊһ��file����һ�Σ�����ֻ����Ŀ¼�Ϳ��ԣ�֮�µ��ļ����ᱻ��������
				if (entry.isDirectory()) {
					new File(outputDirectory + "/" + entry.getName()).mkdirs();
					continue;
				}
				BufferedInputStream bis = new BufferedInputStream(jarf
						.getInputStream(entry));
				String fileName = outputDirectory 
				+ entry.getName().replaceAll(":", "");
				File file = new File(fileName);
				// ���������ԭ����zipfile��ȡ�ļ��������ȡ�ģ������ɿ����ȶ�ȡһ���ļ�
				// ������ļ����ڵ�Ŀ¼��û�г��ֹ�������Ҫ����Ŀ¼����
				resList.add(fileName);
				File parent = file.getParentFile();
				if (parent != null && (!parent.exists())) {
					parent.mkdirs();
				}
				bos = new BufferedOutputStream(new FileOutputStream(file),
						ITFECommonConstant.FILE_BUF_SIZE);
				int count;
				byte data[] = new byte[ITFECommonConstant.FILE_BUF_SIZE];
				while ((count = bis.read(data, 0,
						ITFECommonConstant.FILE_BUF_SIZE)) != -1) {
					bos.write(data, 0, count);
				}
				bos.flush();
				bos.close();
				bis.close();
			}
			jarf.close();
			return resList;
		} catch (Exception e) {
			log.error(e);
			throw new FileOperateException("��ѹ���ļ������쳣", e);
		} finally {
			if (bos != null) {
				try {
					bos.close();
					bos = null;
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("��ѹ���ļ������쳣", e);
				}

			}
		}
	}

	/**
	 * �жϴ����·���Ƿ���ڣ������������ô����Ŀ¼���������κβ���
	 * @param dir Ҫ����Ŀ¼
	 * @return true-Ŀ¼�Ѿ����ڣ�false-Ŀ¼����ʧ��
	 */
	public boolean createDir(String dir){
		File file = new File(dir);
		if (!file.exists()){
			file.mkdirs();
		}
		return true;
	}

	/**
	 * ��InputStream�е�UTF8���ݶ�ȡ���ַ�����
	 * @param in ������
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
		BufferedReader bin = new BufferedReader(new InputStreamReader(in,"utf-8"));
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
	 * @param fileName �ļ��ľ���·��
	 * @return �����ļ����ݵ��ַ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String readFileUtf8(String fileName) throws FileNotFoundException,IOException{
		return readFileUtf8(new FileInputStream(fileName));
	}
	
	/**
	 * �����ж�ȡ�ļ���ÿ�и���split���зָ���ַ�������
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String> readFileWithLine(String fileName)
			throws FileOperateException {
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
			log.error(e);
			throw new FileOperateException("��ȡ�ļ������쳣", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("��ȡ�ļ������쳣", e);
				}

			}
		}

	}

	
	


}
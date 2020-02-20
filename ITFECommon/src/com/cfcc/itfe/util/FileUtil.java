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
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
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
@SuppressWarnings("unchecked")
public class FileUtil implements IFileOper {

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

	private static Log log = LogFactory.getLog(FileUtil.class);

	boolean statistics = false;

	public static FileUtil getInstance() {
		FileUtil util = (FileUtil) ContextFactory.getApplicationContext()
				.getBean(ITFEGlobalBeanId.FILE_UTIL);

		return util;
	}

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
					log.error("�ر��ļ�����", ex);

				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					log.error("�ر��ļ�����", ex);

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
					log.error("�ر��ļ�����", ex);

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
	 * @throws FileOperateException
	 *             �����ļ�ʧ��
	 */
	public void writeFileUtf8(String fileName, String fileContent)
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
			output.write(fileContent.getBytes("utf-8"));

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
					log.error("�ر��ļ�����", ex);

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
	public void writeFile(String fileName, String fileContent, boolean append)
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
					log.error("�ر��ļ�����", ex);

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
					log.error("�ر��ļ�����" + fileName, ex);

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
			throw new FileNotFoundException("Ҫɾ�����ļ�������:" + name);
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

	private void listAbsPath(String filePath, List filesStr) {
		File tmp1 = new File(filePath);
		// ���ݾ���·������ɾ������
		String str = tmp1.getAbsolutePath();
		File file = new File(str);
		if (file.isFile()) 
			filesStr.add(str);
		else {
			File[] f = file.listFiles();
			if (f == null || f.length == 0) 
				return;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
			Map<String,String> map;
			for (int i = 0; i < f.length; i++) {
				File tmp = f[i];
				map = new HashMap<String,String>();
				map.put("filepath", tmp.getAbsolutePath());
				map.put("edittime", df.format(tmp.lastModified()));
				
				if (tmp.isDirectory()) 
					listAbsPath(map.get("filepath"), filesStr);
				 else 
					filesStr.add(map);			
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
			String encode = getFileEncoding(file);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file),encode));

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
			while (emu.hasMoreElements()) {
				JarEntry entry = emu.nextElement();
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
	 * 
	 * @param dir
	 *            Ҫ����Ŀ¼
	 * @return true-Ŀ¼�Ѿ����ڣ�false-Ŀ¼����ʧ��
	 */
	public boolean createDir(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
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

	/**
	 * ȥ��XML��ʽ�����ļ��е�CAǩ��(ȥ��endstr������ַ���)
	 */
	public String removeCAOfXML(String path, String msg, String endstr)
			throws FileOperateException {
		return msg.substring(0, msg.toUpperCase().lastIndexOf(endstr)) + endstr;
	}

	/**
	 * ɾ��ָ��·����days��֮ǰ���ļ�
	 * 
	 * @param dirPath
	 *            Ҫ������·��
	 * @param days
	 *            Ҫɾ������
	 * @author hua
	 * @return
	 */
	public boolean deleteFileWithDays(String dirPath, int days) {
		if (!new File(dirPath).exists()) {
			return true;
		}
		Date pointDate = new Date();
		long timeInterval = pointDate.getTime()
				- convertDaysToMilliseconds(days);
		pointDate.setTime(timeInterval);

		// �����ļ���������
		IOFileFilter timeFileFilter = FileFilterUtils.ageFileFilter(pointDate,
				true);
		IOFileFilter fileFiles = FileFilterUtils.andFileFilter(
				FileFileFilter.FILE, timeFileFilter);

		// ɾ�������������ļ�
		File deleteRootFolder = new File(dirPath);
		Iterator itFile = FileUtils.iterateFiles(deleteRootFolder, fileFiles,
				TrueFileFilter.INSTANCE);
		while (itFile.hasNext()) {
			File file = (File) itFile.next();
			boolean result = file.delete();
			if (!result) {
				return false;
			}
		}

		return true;

	}

	/**
	 * ��������ת��
	 * 
	 * @param days
	 *            ת��������
	 * @author hua
	 * @return
	 */
	private static long convertDaysToMilliseconds(int days) {
		return days * 24L * 3600 * 1000;
	}

	// ��ȡ�ļ��ı����ʽ
	/**
       �ж��Ƿ�ΪUTF-8��ʽ������UTF8���ļ�ʹ��GBK����
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
	 * ���Ƶ����ļ�
	 * @param oldPath    ԭ�ļ�·��
	 * @param newPath    ���ƺ�·��
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				// �ļ�����ʱ
				InputStream inStream = new FileInputStream(oldPath); // ����ԭ�ļ�
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // �ֽ��� �ļ���С
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
			System.out.println("���Ƶ����ļ������ɹ�ִ��");
		} catch (Exception e) {
			System.out.println("���Ƶ����ļ���������");
			e.printStackTrace();
		}
	}
	
	
	public void writeFile(OutputStream out) throws FileOperateException {
		try {
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			String msg = new String("д�ļ�ʧ��!" );
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (IOException e) {
			String msg = new String("д�ļ�ʧ��!" );
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.error("�ر��ļ�����");
				}
				out = null;
			}
		}
	}


	
	/**
	 * ɾ��ָ�����ڵ��ļ�
	 * 
	 * @param name
	 *            �ļ���ȫ·��
	 * @throws FileOperateException
	 *             ���ɾ���ļ��������׳����쳣
	 */
	public void deleteFileForExists(String name) throws FileOperateException,
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
		} 
		if (statistics) {
			long lEnd = System.currentTimeMillis();
			ServiceMethodStatistics.record("deleteFile", "", lEnd - lBegin);
		}

	}
	
	
	/**
	 * �����ļ������ȡ�ļ�������ȡ��ÿ�з����ַ�������
	 * 
	 * @author zhb
	 * @param file
	 * @return List<String>
	 * @throws FileOperateException
	 */
	public List<String> readFileWithLineForEncoding(String fileName)
			throws FileOperateException {
		List<String> listStr = new ArrayList<String>();
		BufferedReader br = null;
		try {
			String encode=getFileEncoding(fileName);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName),encode));
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
	
	/**
	 * ���ļ�ת�����ֽ���
	 * @param fileNmae
	 * @return
	 * @throws FileOperateException
	 */
	public byte[] transformFileToByte(String fileName) throws  FileOperateException{
		byte[] b=new byte[1024];
		FileInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new FileInputStream(fileName);
			out = new ByteArrayOutputStream();
			int n;
			while((n=in.read(b))!=-1){
				out.write(b,0,n);
			}
			return out.toByteArray();
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new FileOperateException("�ͻ����ļ�δ�ϴ���", e);
		}catch (IOException e) {
			log.error(e);
			throw new FileOperateException("��ȡ�ļ������쳣��", e);
		}finally{
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("��ȡ�ļ������쳣��", e);
				}
			if(out!=null)
				try {
					out.close();
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("��ȡ�ļ������쳣��", e);
				}
		}				
	}
	
	/**
	 * ���ļ��ָ�ɶ������ļ��ֽ���
	 * @param fileName
	 * @param cutFileSize �ָ��ļ���С
	 * @return
	 * @throws FileOperateException
	 */
	public List<byte[]> cutFile(String fileName,int cutFileSize) throws FileOperateException {
		byte[] b=new byte[cutFileSize>1024?1024:cutFileSize];
		FileInputStream in = null;
		ByteArrayOutputStream out = null;
		List<byte[]> fileList=new ArrayList<byte[]>();		
		int size = 0;
		try {
			size = new FileInputStream(fileName).available();
		} catch (IOException e) {
			log.error(e);
			throw new FileOperateException("�ͻ����ļ�δ�ϴ���", e);
		}		
		size=size%cutFileSize==0?size/cutFileSize:size/cutFileSize+1;	
		int count=cutFileSize%b.length==0?cutFileSize/b.length:cutFileSize/b.length+1;
		for(int i=0;i<size;i++){
			try{
				in = new FileInputStream(fileName);
				out = new ByteArrayOutputStream();
				in.skip(i*cutFileSize);
				int n;
				for(int j=0;j<count;j++){
					if((n=in.read(b))!=-1)
					out.write(b,0,n);
				}			
				fileList.add(out.toByteArray());
				in.close();
				out.close();
			} catch (IOException e) {
				log.error(e);
				throw new FileOperateException("��ȡ�ļ������쳣��", e);
			}			
		}return fileList;
	}
	/**
	 * ��ȡ�����ֵ��ַ���
	 * @param s �ַ���
	 * @param num  ��ȡ����
	 * @param code  �ַ�����
	 * @return  ��ȡ���ַ���
	 * @throws Exception
	 */
	public static String subStringOfCh(String s,int num,String code) throws Exception{
		int changdu = s.getBytes(code).length;
		if(changdu>num){
			s=s.substring(0, s.length()-1);
			s=subStringOfCh(s, num,code);
		}
		return s;
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
}

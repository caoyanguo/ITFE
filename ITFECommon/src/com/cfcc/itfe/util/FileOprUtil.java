/*
 * 创建日期 2005-8-2
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
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
 * 文件存取和对象序列化、反序列化组件
 * 
 */
public class FileOprUtil implements IFileOper {

	/**
	 * 是否需要统计执行时间
	 * 
	 * @return 返回 statistics。
	 */
	public boolean isStatistics() {
		return statistics;
	}

	/**
	 * @param statistics
	 *            要设置的 statistics。
	 */
	public void setStatistics(boolean statistics) {
		this.statistics = statistics;
	}

	private static Log log = LogFactory.getLog(FileOprUtil.class);

	boolean statistics = false;

	/**
	 * 将数组写入文件
	 * 
	 * @param bytes
	 *            数组
	 * @param filePath
	 *            文件路径
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
			String msg = new String("写文件失败,IO错误." + filePath);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异常." + filePath);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception ex) {
					log.error("关闭文件出错！", ex);

				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					log.error("关闭文件出错！", ex);

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
			String msg = new String("写文件失败,IO错误." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异常." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					log.error("关闭文件出错！", ex);

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
	 * 把指定的内容写入到指定的文件中
	 * 
	 * @param fileName
	 *            文件名
	 * @param fileContent
	 *            文件内容
	 * @param append
	 *            是否追加方式
	 * @throws FileOperateException
	 *             操作文件失败
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
			String msg = new String("写文件失败,IO错误." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异常." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					log.error("关闭文件出错！", ex);

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
	 * 从文本文件中读出字符串
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件中的内容
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
			String msg = new String("读取文件失败,未找到文件." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (IOException e) {
			String msg = new String("读取文件失败,IO错误." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("读取文件失败，运行时异常." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {
					log.error("关闭文件出错！" + fileName, ex);

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
	 * 将指定的对象序列化到指定的文件
	 * 
	 * @param obj
	 *            要序列化的文件
	 * @param fileName
	 *            文件名
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
				f.createNewFile(); // 重新创建文件
			}

			out = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(out,
					ITFECommonConstant.FILE_BUF_SIZE);
			SerializationUtils.serialize(obj, bos);
		} catch (IOException e) {
			String msg = new String("序列化失败,IO错误." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("序列化失败,运行时异常." + fileName);
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
	 * 将指定的文件反序列化
	 * 
	 * @param fileName
	 *            要反序列化的文件
	 * @return 反序列化的对象
	 * @throws FileOperateException
	 *             操作文件发生错误
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
			String msg = new String("反序列化失败,文件未找到." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (Exception e) {
			String msg = new String("反序列化失败,IO错误." + fileName);
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
	 * 删除指定的文件
	 * 
	 * @param name
	 *            文件的全路径
	 * @throws FileOperateException
	 *             如果删除文件出错则抛出该异常
	 * @throws FileNotFoundException
	 *             如果要删除的文件不存在则抛出该异常
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
				String msg = new String("删除文件失败.");
				log.error(msg, e);
				throw new FileOperateException(msg, e);
			}
		} else {
			// 如果文件不存在则直接返回
//			throw new FileNotFoundException("要删除的文件不存在:" + name);
			log.debug("要删除的文件不存在:" + name);
		}

		if (statistics) {
			long lEnd = System.currentTimeMillis();
			ServiceMethodStatistics.record("deleteFile", "", lEnd - lBegin);
		}

	}

	/**
	 * 递归删除文件
	 * 
	 * @param filePath
	 */
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

	/**
	 * 获取路径下所有文件的绝对文件名
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
		// 根据绝对路径进行删除操作
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
	 * 获取路径下所有文件的相对路径文件名
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
	 * 按照行读取文件，每行根据split进行分割成字符串数组
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
			throw new FileOperateException("读取文件出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("读取文件出现异常", e);
				}

			}
		}

	}

	/**
	 * 解压缩
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
				// 会把目录作为一个file读出一次，所以只建立目录就可以，之下的文件还会被迭代到。
				if (entry.isDirectory()) {
					new File(outputDirectory + "/" + entry.getName()).mkdirs();
					continue;
				}
				BufferedInputStream bis = new BufferedInputStream(jarf
						.getInputStream(entry));
				String fileName = outputDirectory 
				+ entry.getName().replaceAll(":", "");
				File file = new File(fileName);
				// 加入这个的原因是zipfile读取文件是随机读取的，这就造成可能先读取一个文件
				// 而这个文件所在的目录还没有出现过，所以要建出目录来。
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
			throw new FileOperateException("解压缩文件出现异常", e);
		} finally {
			if (bos != null) {
				try {
					bos.close();
					bos = null;
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("解压缩文件出现异常", e);
				}

			}
		}
	}

	/**
	 * 判断传入的路径是否存在，如果不存在那么创建目录，否则不做任何操作
	 * @param dir 要检查的目录
	 * @return true-目录已经存在，false-目录创建失败
	 */
	public boolean createDir(String dir){
		File file = new File(dir);
		if (!file.exists()){
			file.mkdirs();
		}
		return true;
	}

	/**
	 * 将InputStream中的UTF8内容读取到字符串中
	 * @param in 读入流
	 * @return 记录流中内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String readFileUtf8(InputStream in) throws IOException {
		StringBuffer content = new StringBuffer();
		if (in == null) {
			// 如果流为空，那么返回空字符串
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
	 * 将utf-8文件中的内容读取到字符串中
	 * @param fileName 文件的绝对路径
	 * @return 保存文件内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String readFileUtf8(String fileName) throws FileNotFoundException,IOException{
		return readFileUtf8(new FileInputStream(fileName));
	}
	
	/**
	 * 按照行读取文件，每行根据split进行分割成字符串数组
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
			throw new FileOperateException("读取文件出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("读取文件出现异常", e);
				}

			}
		}

	}

	
	


}

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
 * 文件存取和对象序列化、反序列化组件
 * 
 */
@SuppressWarnings("unchecked")
public class FileUtil implements IFileOper {

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

	private static Log log = LogFactory.getLog(FileUtil.class);

	boolean statistics = false;

	public static FileUtil getInstance() {
		FileUtil util = (FileUtil) ContextFactory.getApplicationContext()
				.getBean(ITFEGlobalBeanId.FILE_UTIL);

		return util;
	}

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
	 * @throws FileOperateException
	 *             操作文件失败
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
			throw new FileNotFoundException("要删除的文件不存在:" + name);
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

	private void listAbsPath(String filePath, List filesStr) {
		File tmp1 = new File(filePath);
		// 根据绝对路径进行删除操作
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
			while (emu.hasMoreElements()) {
				JarEntry entry = emu.nextElement();
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
	 * 
	 * @param dir
	 *            要检查的目录
	 * @return true-目录已经存在，false-目录创建失败
	 */
	public boolean createDir(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return true;
	}

	/**
	 * 将InputStream中的UTF8内容读取到字符串中
	 * 
	 * @param in
	 *            读入流
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
	 * 将utf-8文件中的内容读取到字符串中
	 * 
	 * @param fileName
	 *            文件的绝对路径
	 * @return 保存文件内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String readFileUtf8(String fileName) throws FileNotFoundException,
			IOException {
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

	/**
	 * 去掉XML格式报文文件中得CA签名(去掉endstr后面的字符串)
	 */
	public String removeCAOfXML(String path, String msg, String endstr)
			throws FileOperateException {
		return msg.substring(0, msg.toUpperCase().lastIndexOf(endstr)) + endstr;
	}

	/**
	 * 删除指定路径下days天之前的文件
	 * 
	 * @param dirPath
	 *            要操作的路径
	 * @param days
	 *            要删除天数
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

		// 设置文件过滤条件
		IOFileFilter timeFileFilter = FileFilterUtils.ageFileFilter(pointDate,
				true);
		IOFileFilter fileFiles = FileFilterUtils.andFileFilter(
				FileFileFilter.FILE, timeFileFilter);

		// 删除符合条件的文件
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
	 * 天与毫秒的转换
	 * 
	 * @param days
	 *            转换的天数
	 * @author hua
	 * @return
	 */
	private static long convertDaysToMilliseconds(int days) {
		return days * 24L * 3600 * 1000;
	}

	// 获取文件的编码格式
	/**
       判断是否为UTF-8格式，不是UTF8的文件使用GBK解析
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
	 * 复制单个文件
	 * @param oldPath    原文件路径
	 * @param newPath    复制后路径
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				// 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
			System.out.println("复制单个文件操作成功执行");
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}
	
	
	public void writeFile(OutputStream out) throws FileOperateException {
		try {
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			String msg = new String("写文件失败!" );
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (IOException e) {
			String msg = new String("写文件失败!" );
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.error("关闭文件出错！");
				}
				out = null;
			}
		}
	}


	
	/**
	 * 删除指定存在的文件
	 * 
	 * @param name
	 *            文件的全路径
	 * @throws FileOperateException
	 *             如果删除文件出错则抛出该异常
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
				String msg = new String("删除文件失败.");
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
	 * 按照文件编码读取文件，将读取的每行放入字符串数组
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
	 * 将文件转换成字节流
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
			throw new FileOperateException("客户端文件未上传！", e);
		}catch (IOException e) {
			log.error(e);
			throw new FileOperateException("读取文件出现异常！", e);
		}finally{
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("读取文件出现异常！", e);
				}
			if(out!=null)
				try {
					out.close();
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("读取文件出现异常！", e);
				}
		}				
	}
	
	/**
	 * 将文件分割成多个多个文件字节流
	 * @param fileName
	 * @param cutFileSize 分割文件大小
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
			throw new FileOperateException("客户端文件未上传！", e);
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
				throw new FileOperateException("读取文件出现异常！", e);
			}			
		}return fileList;
	}
	/**
	 * 截取带汉字的字符串
	 * @param s 字符串
	 * @param num  截取长度
	 * @param code  字符编码
	 * @return  截取后字符串
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
}

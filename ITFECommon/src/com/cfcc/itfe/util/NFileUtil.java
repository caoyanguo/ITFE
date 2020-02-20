package com.cfcc.itfe.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.common.statistics.ServiceMethodStatistics;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.config.ITFEGlobalBeanId;
import com.cfcc.itfe.exception.FileOperateException;

/**
 * 文件存取和对象序列化、反序列化组件
 * 
 * @author 姜洪涛
 */
public class NFileUtil {

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

	private static Log log = LogFactory.getLog(NFileUtil.class);

	boolean statistics = false;

	public static NFileUtil getInstance() {
		NFileUtil util = (NFileUtil) ContextFactory.getApplicationContext()
				.getBean(ITFEGlobalBeanId.FILE_UTIL);

		return util;
	}
	/**
	 * 将数组写入文件
	 * @param bytes   数组
	 * @param filePath 文件路径
	 * @throws FileOperateException
	 */
	public void writeBytesToFile(byte[] bytes,String fileName) throws FileOperateException {
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
			FileChannel fileChannel = output.getChannel();
			ByteBuffer bb = ByteBuffer.wrap(bytes);
			fileChannel.write(bb);			
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
			int len = bytes.length / 1024 + 1;
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
	public void writeFile(String fileName, String fileContent)throws FileOperateException {
		try {
			writeBytesToFile(fileContent.getBytes(ITFECommonConstant.CHARSET_NAME),fileName);
		} catch (UnsupportedEncodingException e) {
			String msg = new String("编码异常" + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
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
			FileChannel fileChannel = input.getChannel();
			int length = (int)fileChannel.size();
			MappedByteBuffer mbBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0,length);
			byte[] bb = new byte[length];
			mbBuffer.get(bb);			
			message = new String(bb,ITFECommonConstant.CHARSET_NAME);			
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
	 * @param filePath
	 */
	public  void deleteFiles(String filePath) {
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
	
}

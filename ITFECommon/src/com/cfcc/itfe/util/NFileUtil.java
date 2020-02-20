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
 * �ļ���ȡ�Ͷ������л��������л����
 * 
 * @author ������
 */
public class NFileUtil {

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

	private static Log log = LogFactory.getLog(NFileUtil.class);

	boolean statistics = false;

	public static NFileUtil getInstance() {
		NFileUtil util = (NFileUtil) ContextFactory.getApplicationContext()
				.getBean(ITFEGlobalBeanId.FILE_UTIL);

		return util;
	}
	/**
	 * ������д���ļ�
	 * @param bytes   ����
	 * @param filePath �ļ�·��
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
			int len = bytes.length / 1024 + 1;
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
	public void writeFile(String fileName, String fileContent)throws FileOperateException {
		try {
			writeBytesToFile(fileContent.getBytes(ITFECommonConstant.CHARSET_NAME),fileName);
		} catch (UnsupportedEncodingException e) {
			String msg = new String("�����쳣" + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
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
			FileChannel fileChannel = input.getChannel();
			int length = (int)fileChannel.size();
			MappedByteBuffer mbBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0,length);
			byte[] bb = new byte[length];
			mbBuffer.get(bb);			
			message = new String(bb,ITFECommonConstant.CHARSET_NAME);			
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
	 * @param filePath
	 */
	public  void deleteFiles(String filePath) {
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
	
}

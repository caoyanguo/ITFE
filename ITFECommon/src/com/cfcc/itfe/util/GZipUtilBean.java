package com.cfcc.itfe.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFEGlobalBeanId;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.jaf.core.loader.ContextFactory;

/**
 * GZIP����
 * 
 * 
 */
public class GZipUtilBean {
	private static Log logger = LogFactory.getLog(GZipUtilBean.class);
	public static final String GIZ_EXT = ".gz";
	public static final String ZIP_EXT = ".zip";
	public static final String RAR_EXT = ".rar";
	private int buffer = 1024;
	
	public static GZipUtilBean getInstance() {
		GZipUtilBean bean = (GZipUtilBean) ContextFactory.getApplicationContext()
				.getBean(ITFEGlobalBeanId.GZIPUTILBEAN_TAS_ID);

		return bean;
	}
	
	

	/**
	 * �ļ�ѹ��,ѹ����ɾ��Դ�ļ�
	 * 
	 * @param srcpath Ҫ��ѹ�����ļ�����Ŀ¼·��
	 * @param desDir ѹ�����ļ��Ĵ洢Ŀ¼,ֻ����Ŀ¼·����������ļ�·��Ҳת����Ŀ¼·��
	 * @throws Exception
	 */
	public void compress(String srcpath,String desDir) throws Exception {
		compress(srcpath,desDir, true,null);
	}

	/**
	 * �ļ�ѹ��
	 * 
	 * @param path
	 * @param delete
	 *            �Ƿ�ɾ��ԭʼ�ļ�
	 * @throws Exception
	 */
	public void compress(String srcpath,String desDir, boolean delete) throws Exception {
		compress(srcpath,desDir,delete,null);
	}
	
	/**
	 * �ļ�ѹ��
	 * 
	 * @param path
	 * @param delete
	 *            �Ƿ�ɾ��ԭʼ�ļ�
	 * @throws Exception
	 */
	public void compress(String srcpath,String desDir, boolean delete,FileFilter fileFilter) throws Exception {
		File srcFile = new File(srcpath);
		if(srcFile.isFile()&&!fileFilter.accept(srcFile)){
			logger.error("Gzip��ѹ���ļ���ʽ����");
			throw new FileOperateException("Gzip��ѹ���ļ���ʽ����"); 
		}
		File desDirFile = new File(desDir);
		compress(srcFile,desDirFile,delete,fileFilter);
	}
	
	
	/**
	 * �ļ�ѹ��
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void compress(File srcFile,File desDirFile) throws Exception {
		compress(srcFile,desDirFile,true,null);
	}

	/**
	 * �ļ�ѹ��
	 * 
	 * @param file
	 * @param delete
	 *            �Ƿ�ɾ��ԭʼ�ļ� true����ɾ�� false��ɾ��
	 * @throws Exception
	 */
	public  void compress(File srcFile,File desDirFile,boolean delete) throws Exception {
		compress(srcFile,desDirFile,delete,null);
	}
	
	/**
	 * �ļ�ѹ��
	 * 
	 * @param file
	 * @param delete
	 *            �Ƿ�ɾ��ԭʼ�ļ�
	 * @throws Exception
	 */
	public  void compress(File srcFile,File desDirFile,boolean delete,FileFilter fileFilter) throws Exception {
		if(!desDirFile.exists()){
			desDirFile.mkdirs();
		}
		
		if(!srcFile.exists()){
			logger.error("Gzip��ѹ���ļ�������");
			throw new FileOperateException("Gzip��ѹ���ļ�������");
		}else if(srcFile.isDirectory()){
			File[] ff = srcFile.listFiles(fileFilter);
			for(File f:ff){
				compress(f,desDirFile,delete,fileFilter);
			}
		}else{
			FileInputStream fis = new FileInputStream(srcFile);
			String desFilePath = desDirFile.getPath()+File.separator+srcFile.getName()+GIZ_EXT;
			FileOutputStream fos = new FileOutputStream(desFilePath);
			compress(fis, fos);
			fis.close();
			fos.flush();
			fos.close();
			if (delete) {
				srcFile.delete();
			}
		}
	}
	
	

	/**
	 * ��ָ�����ļ�����Ŀ¼�µ��ļ�srcPath����ѹ����ָ��desDirĿ¼��
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void decompress(String srcPath,String desDir) throws Exception {
		decompress(srcPath,desDir, true);
	}

	/**
	 * �ļ���ѹ��
	 * 
	 * @param path
	 * @param delete
	 *            �Ƿ�ɾ��ԭʼ�ļ�
	 * @throws Exception
	 */
	public void decompress(String srcPath,String desDir,boolean delete) throws Exception {
		File srcFile = new File(srcPath);
		if(srcFile.isFile()&&!srcPath.endsWith(GIZ_EXT)){
			logger.error(srcFile+"�ļ���ʽ����");
			throw new FileOperateException(srcFile+"�ļ���ʽ����"); 
		}
		
		File desDirFile = new File(desDir);
		decompress(srcFile,desDirFile,delete);
	}
	
	
	/**
	 * �ļ���ѹ��
	 * 
	 * @param file
	 * @throws Exception
	 */
	public  void decompress(File srcFile,File desDirFile) throws Exception {
		decompress(srcFile,desDirFile,true);
	}

	/**
	 * �ļ���ѹ��
	 * 
	 * @param file
	 * @param delete
	 *            �Ƿ�ɾ��ԭʼ�ļ�
	 * @throws Exception
	 */
	public  void decompress(File srcFile,File desDirFile,boolean delete) throws Exception {
		if(!desDirFile.exists()){
			desDirFile.mkdirs();
		}
		
		if(!srcFile.exists()){
			logger.error("Gzip��ѹ���ļ�������");
			throw new FileOperateException("Gzip��ѹ���ļ�����Ŀ¼������");
		}else if(srcFile.isDirectory()){
			File[] files = srcFile.listFiles();
			for(File f:files){
				decompress(f,desDirFile,delete);
			}
		}else{
			FileInputStream fis = new FileInputStream(srcFile);
			String desFileName = desDirFile+File.separator+genDecompressFileName(srcFile.getName());
			FileOutputStream fos = new FileOutputStream(desFileName);
			decompress(fis, fos);
			fis.close();
			fos.flush();
			fos.close();
			if (delete) {
				srcFile.delete();
			}
		}
	}

	
	
	/**
	 * ����ѹ��
	 * 
	 * @param is
	 * @param os
	 * @throws Exception
	 */
	public  void compress(InputStream is, OutputStream os)
			throws Exception {
		GZIPOutputStream gos = new GZIPOutputStream(os);
		int count;
		byte data[] = new byte[buffer];
		while ((count = is.read(data, 0, buffer)) != -1) {
			gos.write(data, 0, count);
		}
		gos.finish();
		gos.flush();
		gos.close();
	}
	/**
	 * ���ݽ�ѹ��
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public  byte[] decompress(byte[] data) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// ��ѹ��
		decompress(bais, baos);
		data = baos.toByteArray();
		baos.flush();
		baos.close();
		bais.close();
		return data;
	}


	/**
	 * ���ݽ�ѹ��
	 * 
	 * @param is
	 * @param os
	 * @throws Exception
	 */
	public void decompress(InputStream is, OutputStream os)
			throws Exception {
		GZIPInputStream gis = new GZIPInputStream(is);
		int count;
		byte data[] = new byte[buffer];
		while ((count = gis.read(data, 0, buffer)) != -1) {
			os.write(data, 0, count);
		}
		gis.close();
	}

	/**
	 * ͨ��ѹ���ļ�����ý�ѹ��֮����ļ���
	 * @param compressFile
	 * @return
	 */
	public static String genDecompressFileName(String compressFileName){
		return compressFileName.replace(GIZ_EXT,"");
	}
	
	/**
	 * ͨ��ѹ���ļ�����ý�ѹ��֮����ļ���
	 * @param compressFile
	 * @return
	 */
	public static String genDecompressFileNameForRar(String compressFileName){
		return compressFileName.replace(RAR_EXT,"");
	}

	
	
	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}


}

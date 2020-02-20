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
 * GZIP工具
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
	 * 文件压缩,压缩后删除源文件
	 * 
	 * @param srcpath 要被压缩的文件或者目录路径
	 * @param desDir 压缩后文件的存储目录,只能是目录路径，如果是文件路径也转化成目录路径
	 * @throws Exception
	 */
	public void compress(String srcpath,String desDir) throws Exception {
		compress(srcpath,desDir, true,null);
	}

	/**
	 * 文件压缩
	 * 
	 * @param path
	 * @param delete
	 *            是否删除原始文件
	 * @throws Exception
	 */
	public void compress(String srcpath,String desDir, boolean delete) throws Exception {
		compress(srcpath,desDir,delete,null);
	}
	
	/**
	 * 文件压缩
	 * 
	 * @param path
	 * @param delete
	 *            是否删除原始文件
	 * @throws Exception
	 */
	public void compress(String srcpath,String desDir, boolean delete,FileFilter fileFilter) throws Exception {
		File srcFile = new File(srcpath);
		if(srcFile.isFile()&&!fileFilter.accept(srcFile)){
			logger.error("Gzip被压缩文件格式不对");
			throw new FileOperateException("Gzip被压缩文件格式不对"); 
		}
		File desDirFile = new File(desDir);
		compress(srcFile,desDirFile,delete,fileFilter);
	}
	
	
	/**
	 * 文件压缩
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void compress(File srcFile,File desDirFile) throws Exception {
		compress(srcFile,desDirFile,true,null);
	}

	/**
	 * 文件压缩
	 * 
	 * @param file
	 * @param delete
	 *            是否删除原始文件 true代表删除 false不删除
	 * @throws Exception
	 */
	public  void compress(File srcFile,File desDirFile,boolean delete) throws Exception {
		compress(srcFile,desDirFile,delete,null);
	}
	
	/**
	 * 文件压缩
	 * 
	 * @param file
	 * @param delete
	 *            是否删除原始文件
	 * @throws Exception
	 */
	public  void compress(File srcFile,File desDirFile,boolean delete,FileFilter fileFilter) throws Exception {
		if(!desDirFile.exists()){
			desDirFile.mkdirs();
		}
		
		if(!srcFile.exists()){
			logger.error("Gzip被压缩文件不存在");
			throw new FileOperateException("Gzip被压缩文件不存在");
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
	 * 将指定的文件或者目录下的文件srcPath，解压缩到指定desDir目录下
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void decompress(String srcPath,String desDir) throws Exception {
		decompress(srcPath,desDir, true);
	}

	/**
	 * 文件解压缩
	 * 
	 * @param path
	 * @param delete
	 *            是否删除原始文件
	 * @throws Exception
	 */
	public void decompress(String srcPath,String desDir,boolean delete) throws Exception {
		File srcFile = new File(srcPath);
		if(srcFile.isFile()&&!srcPath.endsWith(GIZ_EXT)){
			logger.error(srcFile+"文件格式不对");
			throw new FileOperateException(srcFile+"文件格式不对"); 
		}
		
		File desDirFile = new File(desDir);
		decompress(srcFile,desDirFile,delete);
	}
	
	
	/**
	 * 文件解压缩
	 * 
	 * @param file
	 * @throws Exception
	 */
	public  void decompress(File srcFile,File desDirFile) throws Exception {
		decompress(srcFile,desDirFile,true);
	}

	/**
	 * 文件解压缩
	 * 
	 * @param file
	 * @param delete
	 *            是否删除原始文件
	 * @throws Exception
	 */
	public  void decompress(File srcFile,File desDirFile,boolean delete) throws Exception {
		if(!desDirFile.exists()){
			desDirFile.mkdirs();
		}
		
		if(!srcFile.exists()){
			logger.error("Gzip被压缩文件不存在");
			throw new FileOperateException("Gzip解压缩文件或者目录不存在");
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
	 * 数据压缩
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
	 * 数据解压缩
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public  byte[] decompress(byte[] data) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 解压缩
		decompress(bais, baos);
		data = baos.toByteArray();
		baos.flush();
		baos.close();
		bais.close();
		return data;
	}


	/**
	 * 数据解压缩
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
	 * 通过压缩文件名获得解压缩之后的文件名
	 * @param compressFile
	 * @return
	 */
	public static String genDecompressFileName(String compressFileName){
		return compressFileName.replace(GIZ_EXT,"");
	}
	
	/**
	 * 通过压缩文件名获得解压缩之后的文件名
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

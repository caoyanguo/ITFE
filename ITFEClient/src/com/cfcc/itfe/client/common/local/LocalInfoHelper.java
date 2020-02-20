/**
 * 
 */
package com.cfcc.itfe.client.common.local;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * 客户端本地信息获取类
 * 有些信息需要存储到客户端的临时文件中
 * 本类的作用是从客户端临时文件读取信息
 * 此临时文件，类似IE中的cookie
 * @author sjz
 *
 */
public class LocalInfoHelper {
	//临时文件名称
	private static final String TMP_FILE = "localInfo.tmp";
	//临时文件存放目录
	private static String tmpFileSavePath = null;
	//临时文件的绝对路径
	private static String tmpFileFullName = null;
	//文件备份路径
	private String fileBackupPath;
	//文件保存路径
	private String fileSavePath;
	//存储临时文件中信息的变量
	private Properties prop;
	
	public LocalInfoHelper(){
		init();
	}
	
	private void init(){
		//获得临时文件存放目录
		if (tmpFileFullName == null){
			try{
				tmpFileSavePath = System.getProperty("user.dir");
				if ((tmpFileSavePath == null) || (tmpFileSavePath.length() == 0)){
					tmpFileSavePath = System.getProperty("java.io.tmpdir");
					if ((tmpFileSavePath == null) || (tmpFileSavePath.length() == 0)){
						tmpFileSavePath = "c:";
					}
				}
			}catch(Exception e){
				tmpFileSavePath = "c:";
			}
			tmpFileFullName = tmpFileSavePath + "/" + TMP_FILE;
		}
		//载入本地临时文件中的内容
		prop = new Properties();
		try{
			prop.load(new FileInputStream(tmpFileFullName));
			fileBackupPath = prop.getProperty("fileBackupPath","c:/temp");
			fileSavePath = prop.getProperty("fileSavePath","c:/temp");
		}catch(Exception e){
			fileBackupPath = "c:/temp";
			fileSavePath = "c:/temp";
		}
	}
	
	private void save(){
		try{
			prop.store(new FileOutputStream(tmpFileFullName),"tmpinfo");
		}catch(Exception e){
		}
	}
	
	public String getFileBackupPath() {
		return fileBackupPath;
	}

	public void setFileBackupPath(String fileBackupPath) {
		this.fileBackupPath = fileBackupPath;
		prop.setProperty("fileBackupPath", fileBackupPath);
		save();
	}

	public String getFileSavePath() {
		return fileSavePath;
	}

	public void setFileSavePath(String fileSavePath) {
		this.fileSavePath = fileSavePath;
		prop.setProperty("fileSavePath", fileSavePath);
		save();
	}

	public static void main(String args[]){
		LocalInfoHelper helper = new LocalInfoHelper();
		
		helper.setFileBackupPath("c:/temp");
	}
}

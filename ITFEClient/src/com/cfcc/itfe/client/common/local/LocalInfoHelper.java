/**
 * 
 */
package com.cfcc.itfe.client.common.local;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * �ͻ��˱�����Ϣ��ȡ��
 * ��Щ��Ϣ��Ҫ�洢���ͻ��˵���ʱ�ļ���
 * ����������Ǵӿͻ�����ʱ�ļ���ȡ��Ϣ
 * ����ʱ�ļ�������IE�е�cookie
 * @author sjz
 *
 */
public class LocalInfoHelper {
	//��ʱ�ļ�����
	private static final String TMP_FILE = "localInfo.tmp";
	//��ʱ�ļ����Ŀ¼
	private static String tmpFileSavePath = null;
	//��ʱ�ļ��ľ���·��
	private static String tmpFileFullName = null;
	//�ļ�����·��
	private String fileBackupPath;
	//�ļ�����·��
	private String fileSavePath;
	//�洢��ʱ�ļ�����Ϣ�ı���
	private Properties prop;
	
	public LocalInfoHelper(){
		init();
	}
	
	private void init(){
		//�����ʱ�ļ����Ŀ¼
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
		//���뱾����ʱ�ļ��е�����
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

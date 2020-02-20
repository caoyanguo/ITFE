package com.cfcc.itfe.security;

public class ImplGKEncryptKeyJNI {

	public native static long gkEncryptKey(String srcFilename,String decFileName,String key);
	public native static long gkUnEncryptKey(String srcFilename,String decFileName,String key);
	
	static {
		try {
			// 加载动态库文件
			System.loadLibrary("itfe_plkk_encrypt_add");
		} catch (Throwable e) {
		}
	}
	
	public static void main(String[] args) {
		String srcFilename = "d:\\402261000004200903090000100001.txt";
		String decFileName = "d:\\402261000004200903090000100001.pas";
		String key = "1234567890abcdef";
		long value = gkEncryptKey(srcFilename, decFileName,key);
		
		
		String srcFilename1 = "d:\\402261000004200903090000100002.txt";
		
		long value1 = gkUnEncryptKey(decFileName, srcFilename1,key);

	}
}

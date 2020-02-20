package com.cfcc.jdsoft.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 3DES（即Triple DES）是DES向AES过渡的加密算法， 它使�?3�?64位的密钥对数据进行三次加密�??
 * 是DES的一个更安全的变形�?�它以DES为基本模块，通过组合分组方法设计出分组加密算法�??
 * 
 * @author CNJUN
 */
public class TripleDES {
	private static String CHARSET = "UTF-8";
	private final static String CRYPTALGORITHM = "DESede/CBC/PKCS5Padding";
	private final static String KEYALGORITHM = "DESede";
	private final static byte[] DEFAULTIV = {1,2,3,4,5,6,7,8};
	
	static {
		java.security.Security.addProvider(new com.sun.crypto.provider.SunJCE());
	}
	
	/**
	 * 设置字符编码方式
	 */
	public static void setCharset(String charset) {
		CHARSET = charset;
	}

	/**
	 * 3DES加密
	 * 
	 * @param message 要加密的字符�?
	 * @param strKey  密钥
	 * @param byteIV  加密加量
	 * @return        加密后字符串
	 * @throws Exception
	 */
	public static String encrypt(String message, String strKey, byte[] byteIV) throws Exception {
		byte[] input = message.getBytes(CHARSET);
		
		Key key = _KeyGenerator(strKey);
		
		IvParameterSpec ivSpec = (byteIV == null || byteIV.length == 0) ? _IvGenerator(DEFAULTIV) : _IvGenerator(byteIV);
		
		Cipher cipher = Cipher.getInstance(CRYPTALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] output = cipher.doFinal(input);
		
		return new String(Base64.encode(output), CHARSET);
		//return new String(Hex.encode(input));
	}
	
	/**
	 * 3DES解密
	 * 
	 * @param message
	 * @param strKey
	 * @param byteIV
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String message, String strKey, byte[] byteIV) throws Exception {
		byte[] input = Base64.decode(message);
		//byte[] input = Hex.decode(message);
		
		Key key = _KeyGenerator(strKey);
		IvParameterSpec ivSpec = (byteIV == null || byteIV.length == 0) ? _IvGenerator(DEFAULTIV) : _IvGenerator(byteIV);
		
		Cipher cipher = Cipher.getInstance(CRYPTALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		byte[] output = cipher.doFinal(input);
		
		return new String(output, CHARSET);
	}
	
	/**
	 * 生成加解密向�?
	 */
	private static IvParameterSpec _IvGenerator(byte[] b) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(b);
		return iv;
	}
	
	/**
	 * 根据16进制字符串生成密�?
	 */
	private static Key _KeyGenerator(String strKey) throws Exception {
		byte[] input = Hex.decode(strKey);
		
		DESedeKeySpec KeySpec = new DESedeKeySpec(input);
		SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance(KEYALGORITHM);
		
		return KeyFactory.generateSecret(KeySpec);
	}
}

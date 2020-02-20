package com.cfcc.itfe.util.encrypt;

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
 * 3DES����Triple DES����DES��AES���ɵļ����㷨�� ��ʹ��3��64λ����Կ�����ݽ������μ��ܡ�
 * ��DES��һ������ȫ�ı��Ρ�����DESΪ����ģ�飬ͨ����Ϸ��鷽����Ƴ���������㷨��
 * 
 * @author CNJUN
 */
public class TripleDES {
	private static String CHARSET = "GBK";
	private final static String CRYPTALGORITHM = "DESede/CBC/PKCS5Padding";
	private final static String KEYALGORITHM = "DESede";
	private final static byte[] DEFAULTIV = {1,2,3,4,5,6,7,8};
	
	static {
		java.security.Security.addProvider(new com.sun.crypto.provider.SunJCE());
	}
	
	/**
	 * �����ַ����뷽ʽ
	 */
	public static void setCharset(String charset) {
		CHARSET = charset;
	}

	/**
	 * 3DES����
	 * 
	 * @param message Ҫ���ܵ��ַ���
	 * @param strKey  ��Կ
	 * @param byteIV  ���ܼ���
	 * @return        ���ܺ��ַ���
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
	 * 3DES����
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
	 * ���ɼӽ�������
	 */
	private static IvParameterSpec _IvGenerator(byte[] b) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(b);
		return iv;
	}
	
	/**
	 * ����16�����ַ���������Կ
	 */
	private static Key _KeyGenerator(String strKey) throws Exception {
		byte[] input = Hex.decode(strKey);
		
		DESedeKeySpec KeySpec = new DESedeKeySpec(input);
		SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance(KEYALGORITHM);
		
		return KeyFactory.generateSecret(KeySpec);
	}
}

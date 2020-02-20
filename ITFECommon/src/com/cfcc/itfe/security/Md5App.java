package com.cfcc.itfe.security;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * ����Md5����
 * @author sjz
 * 2008-6-26 ����09:10:49
 */
public class Md5App {
	 
	private static Log logger = LogFactory.getLog(Md5App.class);
	
	//����md5����
	private MD5C md5;
	//�Ƿ�������true-�������false-������
	private boolean bXor;
	//����Ҫ��������ʱ�򣬽������ʱʹ�õ�keyֵ
	private String key;
	
	public Md5App(){
		md5 = new MD5C();
		bXor = false;
		key = "";
	}
	
	/**
	 * ����Ҫ���ַ����Ƚ������Ȼ���ټ���Md5ʱ�ĳ�ʼ������
	 * @param bXor �Ƿ���Ҫ�������true-�������false-������
	 * @param key  ����Ҫ�������ʱ�������ʹ�õ�key
	 */
	public Md5App(boolean bXor, String key){
		md5 = new MD5C();
		this.bXor = bXor;
		this.key = key;
	}
	
	/**
	 * ����Ҫ���ַ����Ƚ������Ȼ���ټ���Md5ʱ�ĳ�ʼ������
	 * @param bXor �Ƿ���Ҫ�������true-�������false-������
	 * @param key  ����Ҫ�������ʱ�������ʹ�õ�key
	 */
	public void init(boolean bXor, String key){
		this.bXor = bXor;
		this.key = key;
	}
	
	/**
	 * ����md5�ķ��������ݴ�����ַ�����������ַ�����md5������md5��Ϊ�ַ�������
	 * ���ַ�������������ʱ����Ҫ����md5���ַ����м�û���ַ�����������ֻ�ڽ�β�ĵط����ڣ�������ʱ�����ֱ�Ӷ��ַ�������md5��
	 * �����ַ�����Ҫ��������ʱ����ô����Ľ���п���Ϊ�ַ�����������Ascii��0������ʱҪ�����ǰ���ַ���ת����Byte���飬Ȼ���ٶԴ���������Byte�������md5��
	 * @param src ��Ҫ����md5���ַ���
	 * @return md5ֵ
	 */
	public String makeMd5(String src){
		String strMd5;
		if (bXor){
			//�����Ȼ���ٶ����Ľ������md5����ʱ��Ҫ���ַ���ת����Byte���飬Ȼ���ٽ������Ȼ���ٶԴ���������Byte�������md5
			int iLen = src.getBytes().length;
			byte[] inbuf = xor(src.getBytes(),iLen);
			strMd5 = md5.getMD5byByte(inbuf, iLen);
		}else{
			//�������Ҫ���������ôֱ�Ӽ����ַ�����md5����
			strMd5 = md5.getMD5byStr(src); 
		}
		return strMd5;
	}
	
	/**
	 * �����������õ�key���Դ����Byte���������������
	 * @param src  Ҫ��������byte����
	 * @param iLen ���鳤��
	 * @return ����������byte���飬���п��ܺ����ַ�����������Ascii��0��
	 */
	public byte[] xor(byte[] src, int iLen){
		byte[] result = new byte[iLen];
		int keyLen = key.length();
		int iPos = 0;
		byte[] keys = key.getBytes();
		
		for (int i=0; i<iLen; i++){
//			logger.info("=============> keyLen:" + keyLen);
			iPos = i % keyLen;
			result[i] = (byte)(src[i] ^ keys[iPos]);
		}
		return result;
	}
	
	public static void main(String args[]) {
		String[] src = {
//				"",
//				"a",
//				"abc",
//				"message digest",
//				"abcdefghijklmnopqrstuvwxyz",
//				"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
//				"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
//				"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ����й�������һ�����ԡ�",
//				"����й���",
//				"0123�й�"
//				"{2:001         }{3::0BG:00100:52A:102653005001:58A:011669202015:30A:20080707:0BC:00287015:33G:000000000829280:CC4:102653005001:50C:3100013919200021831:50A:�й��ƶ�ͨ�ż����������޹�˾:50B:����:CC5:011669202015:59C:37100044:59A:������������-���ɿ�˰���˰��:59B:������:CEG:11:72A:�潭�ƶ�˰��/�潭�ƶ�����Ӫҵ˰�𼰸���:B40:00000000:72C:}{2:001         }{3::0BG:00100:52A:102653005001:58A:011669202015:30A:20080707:0BC:00287016:33G:000000000226459:CC4:102653005001:50C:3100013919200021831:50A:�й��ƶ�ͨ�ż����������޹�˾:50B:����:CC5:011669202015:59C:37100044:59A:������������-���ɿ�˰���˰��:59B:������:CEG:11:72A:�潭�ƶ���������˰/�潭�ƶ���������˰:B40:00000000:72C:}"
				"11111111"
		};
		Md5App app = new Md5App();
		System.out.println(app.makeMd5("00000000"));
		Md5App m = new Md5App(true,"0123456789abcdef");
//		MD5 md = new MD5();
		if (Array.getLength(args) == 0) { //���û�в�����ִ�б�׼��Test Suite
			for (String one : src){
			}
		} else{
		}

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(src[0].getBytes());

            final byte[] digest = messageDigest.digest();
            StringBuffer hexString = new StringBuffer();

            synchronized (hexString) {
                for (int i = 0; i < digest.length; i++) {
                    final String plainText = Integer
                        .toHexString(0xFF & digest[i]);

                    if (plainText.length() < 2) {
                        hexString.append("0");
                    }

                    hexString.append(plainText);
                }

            }
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e.getMessage());
        }
 }

}

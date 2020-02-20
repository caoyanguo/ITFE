package com.cfcc.itfe.security;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 生成Md5的类
 * @author sjz
 * 2008-6-26 上午09:10:49
 */
public class Md5App {
	 
	private static Log logger = LogFactory.getLog(Md5App.class);
	
	//计算md5的类
	private MD5C md5;
	//是否进行异或，true-进行异或，false-不进行
	private boolean bXor;
	//在需要进行异或的时候，进行异或时使用的key值
	private String key;
	
	public Md5App(){
		md5 = new MD5C();
		bXor = false;
		key = "";
	}
	
	/**
	 * 当需要对字符串先进行异或然后再计算Md5时的初始化方法
	 * @param bXor 是否需要进行异或，true-进行异或，false-不进行
	 * @param key  当需要进行异或时，异或所使用的key
	 */
	public Md5App(boolean bXor, String key){
		md5 = new MD5C();
		this.bXor = bXor;
		this.key = key;
	}
	
	/**
	 * 当需要对字符串先进行异或然后再计算Md5时的初始化方法
	 * @param bXor 是否需要进行异或，true-进行异或，false-不进行
	 * @param key  当需要进行异或时，异或所使用的key
	 */
	public void init(boolean bXor, String key){
		this.bXor = bXor;
		this.key = key;
	}
	
	/**
	 * 计算md5的方法，根据传入的字符串，计算该字符串的md5，并将md5作为字符串返回
	 * 当字符串不进行异或的时候，需要计算md5的字符串中间没有字符串结束符，只在结尾的地方存在，所以这时候可以直接对字符串计算md5。
	 * 而当字符串需要进行异或的时候，那么异或后的结果有可能为字符串结束符（Ascii码0），此时要在异或前将字符串转换成Byte数组，然后再对存放异或结果的Byte数组计算md5。
	 * @param src 需要计算md5的字符串
	 * @return md5值
	 */
	public String makeMd5(String src){
		String strMd5;
		if (bXor){
			//先异或，然后再对异或的结果计算md5。此时需要将字符串转换成Byte数组，然后再进行异或，然后再对存放异或结果的Byte数组计算md5
			int iLen = src.getBytes().length;
			byte[] inbuf = xor(src.getBytes(),iLen);
			strMd5 = md5.getMD5byByte(inbuf, iLen);
		}else{
			//如果不需要进行异或，那么直接计算字符串的md5即可
			strMd5 = md5.getMD5byStr(src); 
		}
		return strMd5;
	}
	
	/**
	 * 根据事先设置的key，对传入的Byte的数组进行异或操作
	 * @param src  要进行异或的byte数组
	 * @param iLen 数组长度
	 * @return 存放异或结果的byte数组，其中可能含有字符串结束符（Ascii码0）
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
//				"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ你好中国，这是一个测试。",
//				"你好中国，",
//				"0123中国"
//				"{2:001         }{3::0BG:00100:52A:102653005001:58A:011669202015:30A:20080707:0BC:00287015:33G:000000000829280:CC4:102653005001:50C:3100013919200021831:50A:中国移动通信集团重庆有限公司:50B:重庆:CC5:011669202015:59C:37100044:59A:国库待决算款项-待缴库税款（地税）:59B:重庆市:CEG:11:72A:垫江移动税金/垫江移动代扣营业税金及附加:B40:00000000:72C:}{2:001         }{3::0BG:00100:52A:102653005001:58A:011669202015:30A:20080707:0BC:00287016:33G:000000000226459:CC4:102653005001:50C:3100013919200021831:50A:中国移动通信集团重庆有限公司:50B:重庆:CC5:011669202015:59C:37100044:59A:国库待决算款项-待缴库税款（地税）:59B:重庆市:CEG:11:72A:垫江移动个人所得税/垫江移动个人所得税:B40:00000000:72C:}"
				"11111111"
		};
		Md5App app = new Md5App();
		System.out.println(app.makeMd5("00000000"));
		Md5App m = new Md5App(true,"0123456789abcdef");
//		MD5 md = new MD5();
		if (Array.getLength(args) == 0) { //如果没有参数，执行标准的Test Suite
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

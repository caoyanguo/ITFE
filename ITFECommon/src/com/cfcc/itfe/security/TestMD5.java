package com.cfcc.itfe.security;

/**
 * 根据给定的字段获得该字段的MD5校验值
 *
 */
public class TestMD5 {


	/**
	 * @param vouchNo    凭证号码
	 * @param subCode    科目代码
	 * @param rcvAccount 收款账号
	 * @param amt        金额
	 * @param key        密钥
	 * @return
	 */
	public static String getMD5(String vouchNo, String subCode, String rcvAccount,
			Double amt , String key) {
		MD5ForSD md5 = new MD5ForSD();
		StringBuffer buffer = new StringBuffer();
		String value = buffer.append(vouchNo).append(subCode).append(rcvAccount).toString();
		long damt = Math.round(amt*100);               
		return md5.getMD5ofStr(value+damt+key);
	}



	public static void main(String[] args) {
		TestMD5 test = new TestMD5();
		String str = test.getMD5("111", "222", "333", 15.5 ,"yt");
	}

}

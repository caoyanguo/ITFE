package com.cfcc.itfe.security;

/**
 * ���ݸ������ֶλ�ø��ֶε�MD5У��ֵ
 *
 */
public class TestMD5 {


	/**
	 * @param vouchNo    ƾ֤����
	 * @param subCode    ��Ŀ����
	 * @param rcvAccount �տ��˺�
	 * @param amt        ���
	 * @param key        ��Կ
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

package com.cfcc.itfe.util.encrypt;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws Exception {
		//HmacMD5À„∑®
		String str1 = new String(Hex.encode(MD5Sign.encryptHMAC("9dsoft", "123456789012345678901234567890123456789012345678")));
		System.out.println(str1);
		
		//3DESº”√‹
		String str2 = TripleDES.encrypt("9dsoft\n[[["+str1+"]]]", "123456789012345678901234567890123456789012345678", null);
		System.out.println(str2);
		
		String str3 = TripleDES.decrypt(str2, "123456789012345678901234567890123456789012345678", null);
		System.out.println(str3);
	}
}

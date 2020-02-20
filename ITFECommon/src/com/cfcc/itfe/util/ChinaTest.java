/**
 * 
 */
package com.cfcc.itfe.util;

/**
 * @author Administrator
 *
 */
public class ChinaTest {
	// 判断字符是否包含中文字符
	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public static boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		Boolean b = false;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c) == true ) {
				b = true;
				break;
			}
		}
		return b;
	}
	public static void main(String[] args) {
		//传入需要判断的路径，进行中文判断 true有中文  ，false 没有中文
		Boolean b = isChinese("D:\\退库");
	}
}

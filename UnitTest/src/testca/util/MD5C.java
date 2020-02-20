package testca.util;

/**
 * 从C语言转换来的MD5算法
 * 
 * @author sjz 
 * 2008-6-26 下午06:35:54
 */
public class MD5C {
	// 16进制的字符串
	static final String HEX_STR = "0123456789abcdef";

	/*
	 * FF, GG, HH ,II 是4个基本的MD5函数，在原始的MD5的C实现中，由于它们是简单的位运算，
	 * 可能出于效率的考虑把它们实现成了宏，在java中，我们把它们 实现成了private方法，名字保持了原来C中的。 FF, GG, HH, and
	 * II transformations for rounds 1, 2, 3, and 4. Rotation is separate from
	 * addition to prevent recomputation.
	 */

	/**
	 * Add integers, wrapping at 2^32
	 */
	private int add(int x, int y) {
		return ((x & 0x7FFFFFFF) + (y & 0x7FFFFFFF)) ^ (x & 0x80000000)	^ (y & 0x80000000);
	}

	/**
	 * Bitwise rotate a 32-bit number to the left
	 */
	private int rol(int num, int cnt) {
		if (num > 0) {
			return (num << cnt) | (num >> (32 - cnt));
		} else { 
			// 这里要做无符号右移，即补0
			return (num << cnt) | (num >>> (32 - cnt));
		}
	}

	// These functions implement the basic operation for each round of the algorithm
	private int cmn(int q, int a, int b, int x, int s, int t) {
		return add(rol(add(add(a, q), add(x, t)), s), b);
	}

	private int FF(int a, int b, int c, int d, int x, int s, int t) {
		return cmn((b & c) | ((~b) & d), a, b, x, s, t);
	}

	private int GG(int a, int b, int c, int d, int x, int s, int t) {
		return cmn((b & d) | (c & (~d)), a, b, x, s, t);
	}

	private int HH(int a, int b, int c, int d, int x, int s, int t) {
		return cmn(b ^ c ^ d, a, b, x, s, t);
	}

	private int II(int a, int b, int c, int d, int x, int s, int t) {
		return cmn(c ^ (b | (~d)), a, b, x, s, t);
	}

	/**
	 * 计算md5值的主要方法，根据传入的byte数组和数组的长度，计算该byte数组的md5
	 * @param inbuf    要计算md5的byte数组
	 * @param inputLen 数组中字符的长度
	 * @return md5值
	 */
	private String md5Create(byte[] inbuf, int inputLen) {
		int[] x = str2blks_MD5(inbuf, inputLen);
		int xlen = (((inputLen + 8) >> 6) + 1) * 16;
		// /////////////////////////
		int a = 0x67452301;
		int b = 0xEFCDAB89;
		int c = 0x98BADCFE;
		int d = 0x10325476;

		for (int i = 0; i < xlen; i += 16) {
			int olda = a;
			int oldb = b;
			int oldc = c;
			int oldd = d;

			a = FF(a, b, c, d, x[i + 0], 7, 0xD76AA478);
			d = FF(d, a, b, c, x[i + 1], 12, 0xE8C7B756);
			c = FF(c, d, a, b, x[i + 2], 17, 0x242070DB);
			b = FF(b, c, d, a, x[i + 3], 22, 0xC1BDCEEE);
			a = FF(a, b, c, d, x[i + 4], 7, 0xF57C0FAF);
			d = FF(d, a, b, c, x[i + 5], 12, 0x4787C62A);
			c = FF(c, d, a, b, x[i + 6], 17, 0xA8304613);
			b = FF(b, c, d, a, x[i + 7], 22, 0xFD469501);
			a = FF(a, b, c, d, x[i + 8], 7, 0x698098D8);
			d = FF(d, a, b, c, x[i + 9], 12, 0x8B44F7AF);
			c = FF(c, d, a, b, x[i + 10], 17, 0xFFFF5BB1);
			b = FF(b, c, d, a, x[i + 11], 22, 0x895CD7BE);
			a = FF(a, b, c, d, x[i + 12], 7, 0x6B901122);
			d = FF(d, a, b, c, x[i + 13], 12, 0xFD987193);
			c = FF(c, d, a, b, x[i + 14], 17, 0xA679438E);
			b = FF(b, c, d, a, x[i + 15], 22, 0x49B40821);

			a = GG(a, b, c, d, x[i + 1], 5, 0xF61E2562);
			d = GG(d, a, b, c, x[i + 6], 9, 0xC040B340);
			c = GG(c, d, a, b, x[i + 11], 14, 0x265E5A51);
			b = GG(b, c, d, a, x[i + 0], 20, 0xE9B6C7AA);
			a = GG(a, b, c, d, x[i + 5], 5, 0xD62F105D);
			d = GG(d, a, b, c, x[i + 10], 9, 0x02441453);
			c = GG(c, d, a, b, x[i + 15], 14, 0xD8A1E681);
			b = GG(b, c, d, a, x[i + 4], 20, 0xE7D3FBC8);
			a = GG(a, b, c, d, x[i + 9], 5, 0x21E1CDE6);
			d = GG(d, a, b, c, x[i + 14], 9, 0xC33707D6);
			c = GG(c, d, a, b, x[i + 3], 14, 0xF4D50D87);
			b = GG(b, c, d, a, x[i + 8], 20, 0x455A14ED);
			a = GG(a, b, c, d, x[i + 13], 5, 0xA9E3E905);
			d = GG(d, a, b, c, x[i + 2], 9, 0xFCEFA3F8);
			c = GG(c, d, a, b, x[i + 7], 14, 0x676F02D9);
			b = GG(b, c, d, a, x[i + 12], 20, 0x8D2A4C8A);

			a = HH(a, b, c, d, x[i + 5], 4, 0xFFFA3942);
			d = HH(d, a, b, c, x[i + 8], 11, 0x8771F681);
			c = HH(c, d, a, b, x[i + 11], 16, 0x6D9D6122);
			b = HH(b, c, d, a, x[i + 14], 23, 0xFDE5380C);
			a = HH(a, b, c, d, x[i + 1], 4, 0xA4BEEA44);
			d = HH(d, a, b, c, x[i + 4], 11, 0x4BDECFA9);
			c = HH(c, d, a, b, x[i + 7], 16, 0xF6BB4B60);
			b = HH(b, c, d, a, x[i + 10], 23, 0xBEBFBC70);
			a = HH(a, b, c, d, x[i + 13], 4, 0x289B7EC6);
			d = HH(d, a, b, c, x[i + 0], 11, 0xEAA127FA);
			c = HH(c, d, a, b, x[i + 3], 16, 0xD4EF3085);
			b = HH(b, c, d, a, x[i + 6], 23, 0x04881D05);
			a = HH(a, b, c, d, x[i + 9], 4, 0xD9D4D039);
			d = HH(d, a, b, c, x[i + 12], 11, 0xE6DB99E5);
			c = HH(c, d, a, b, x[i + 15], 16, 0x1FA27CF8);
			b = HH(b, c, d, a, x[i + 2], 23, 0xC4AC5665);

			a = II(a, b, c, d, x[i + 0], 6, 0xF4292244);
			d = II(d, a, b, c, x[i + 7], 10, 0x432AFF97);
			c = II(c, d, a, b, x[i + 14], 15, 0xAB9423A7);
			b = II(b, c, d, a, x[i + 5], 21, 0xFC93A039);
			a = II(a, b, c, d, x[i + 12], 6, 0x655B59C3);
			d = II(d, a, b, c, x[i + 3], 10, 0x8F0CCC92);
			c = II(c, d, a, b, x[i + 10], 15, 0xFFEFF47D);
			b = II(b, c, d, a, x[i + 1], 21, 0x85845DD1);
			a = II(a, b, c, d, x[i + 8], 6, 0x6FA87E4F);
			d = II(d, a, b, c, x[i + 15], 10, 0xFE2CE6E0);
			c = II(c, d, a, b, x[i + 6], 15, 0xA3014314);
			b = II(b, c, d, a, x[i + 13], 21, 0x4E0811A1);
			a = II(a, b, c, d, x[i + 4], 6, 0xF7537E82);
			d = II(d, a, b, c, x[i + 11], 10, 0xBD3AF235);
			c = II(c, d, a, b, x[i + 2], 15, 0x2AD7D2BB);
			b = II(b, c, d, a, x[i + 9], 21, 0xEB86D391);

			a = add(a, olda);
			b = add(b, oldb);
			c = add(c, oldc);
			d = add(d, oldd);
		}
		return rhex((int) a) + rhex((int) b) + rhex((int) c) + rhex((int) d);
	}

	/**
	 * Convert a string to a sequence of 16-word blocks, stored as an array.
	 * Append padding bits and the length, as described in the MD5 standard.
	 * @param str 要转换的字符串
	 * @param len 字符串的长度
	 * @return 转换后的字符串
	 */
	private int[] str2blks_MD5(byte[] str, int len) {
		int nblk = ((len + 8) >> 6) + 1;
		int[] blks = new int[nblk * 16];
		int i = 0;

		for (i = 0; i < nblk * 16; i++) {
			blks[i] = 0;
		}
		for (i = 0; i < len; i++) {
			blks[i >> 2] |= str[i] << ((i % 4) * 8);
		}
		blks[i >> 2] |= 0x80 << ((i % 4) * 8);
		blks[nblk * 16 - 2] = len * 8;

		return blks;
	}

	/**
	 * 将整形转换成字符串返回
	 * @param num 整形
	 * @return 整形对应的字符串
	 */
	private String rhex(int num) {
		String str = "";
		//整形所对应的字符串，从16进制的字符串中获得
		for (int j = 0; j <= 3; j++)
			str = str + HEX_STR.charAt((num >> (j * 8 + 4)) & 0x0F)	+ HEX_STR.charAt((num >> (j * 8)) & 0x0F);
		return str;
	}

	/**
	 * getMD5byStr是类MD5最主要的公共方法，入口参数是你想要计算MD5值的字符串
	 * 返回的是传入字符串的MD5。
	 * @param inbuf 要计算md5的字符串
	 * @return md5值
	 */
	public String getMD5byStr(String inbuf) {
		//因为字符串中可能会含有汉字，所以在调用md5Create方法时，长度部分应该取byte数组的长度，而不是字符串的长度。因为对于byte，汉字是2位；而对于字符串，汉字是1位
		return md5Create(inbuf.getBytes(), inbuf.getBytes().length);
	}

	/**
	 * getMD5byByte是类MD5最主要的公共方法，入口参数是你想要计算MD5值的Byte数组
	 * 因为有时传入的字符串中间有字符串结束符，所以将所有的字符串都转换成Byte后再计算Md5
	 * 返回的是传入字符串的MD5。
	 * @param inbuf 要计算md5的byte数组
	 * @param iLen  byte数组的长度
	 * @return md5值
	 */
	public String getMD5byByte(byte[] inbuf, int iLen) {
		return md5Create(inbuf, iLen);
	}

}

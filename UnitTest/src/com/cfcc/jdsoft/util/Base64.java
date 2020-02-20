package com.cfcc.jdsoft.util;

/**
 * Base64编解�?
 * 
 * @author CNJUN
 * @version 1.0
 */
public final class Base64 {
	/**
	 * 编码�?
	 */
	private static final byte[] ENCODINGTABLE =	{
		(byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G',
		(byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N',
		(byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U',
		(byte)'V', (byte)'W', (byte)'X', (byte)'Y', (byte)'Z',
		(byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g',
		(byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n',
		(byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u',
		(byte)'v', (byte)'w', (byte)'x', (byte)'y', (byte)'z',
		(byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', (byte)'6',
		(byte)'7', (byte)'8', (byte)'9',
		(byte)'+', (byte)'/'
	};

	/**
	 * Base64编码
	 */
	public static byte[] encode(byte[] data) {
		byte[] bytes;
		
		int modulus = data.length % 3;
		if (modulus == 0) {
			bytes = new byte[4 * data.length / 3];
		} else {
			bytes = new byte[4 * ((data.length / 3) + 1)];
		}
		
		int dataLength = (data.length - modulus);
		int a1, a2, a3;
		for (int i = 0, j = 0; i < dataLength; i += 3, j += 4) {
			a1 = data[i] & 0xff;
			a2 = data[i + 1] & 0xff;
			a3 = data[i + 2] & 0xff;
			
			bytes[j] = ENCODINGTABLE[(a1 >>> 2) & 0x3f];
			bytes[j + 1] = ENCODINGTABLE[((a1 << 4) | (a2 >>> 4)) & 0x3f];
			bytes[j + 2] = ENCODINGTABLE[((a2 << 2) | (a3 >>> 6)) & 0x3f];
			bytes[j + 3] = ENCODINGTABLE[a3 & 0x3f];
		}
		
		/*
		 * process the tail end.
		 */
		int	b1, b2, b3;
		int	d1, d2;
		
		switch (modulus) {
		case 0: /* nothing left to do */
			break;
		case 1:
			d1 = data[data.length - 1] & 0xff;
			b1 = (d1 >>> 2) & 0x3f;
			b2 = (d1 << 4) & 0x3f;
			
			bytes[bytes.length - 4] = ENCODINGTABLE[b1];
			bytes[bytes.length - 3] = ENCODINGTABLE[b2];
			bytes[bytes.length - 2] = (byte)'=';
			bytes[bytes.length - 1] = (byte)'=';
			break;
		case 2:
			d1 = data[data.length - 2] & 0xff;
			d2 = data[data.length - 1] & 0xff;
			
			b1 = (d1 >>> 2) & 0x3f;
			b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
			b3 = (d2 << 2) & 0x3f;
			
			bytes[bytes.length - 4] = ENCODINGTABLE[b1];
			bytes[bytes.length - 3] = ENCODINGTABLE[b2];
			bytes[bytes.length - 2] = ENCODINGTABLE[b3];
			bytes[bytes.length - 1] = (byte)'=';
			break;
		}
		
		return bytes;
	}
	
	/*
	 * 解码�?
	 */
	private static final byte[] DECODINGTABLE;
	
	static {
		DECODINGTABLE = new byte[128];
		for (int i = 'A'; i <= 'Z'; i++) {
			DECODINGTABLE[i] = (byte)(i - 'A');
		}
		
		for (int i = 'a'; i <= 'z'; i++) {
			DECODINGTABLE[i] = (byte)(i - 'a' + 26);
		}
		
		for (int i = '0'; i <= '9'; i++) {
			DECODINGTABLE[i] = (byte)(i - '0' + 52);
		}
		
		DECODINGTABLE['+'] = 62;
		DECODINGTABLE['/'] = 63;
	}
	
	/**
	 * Base64解码
	 */
	public static byte[] decode(byte[] data) {
		byte[] bytes;
		byte b1, b2, b3, b4;
		
		if (data[data.length - 2] == '=') {
			bytes = new byte[(((data.length / 4) - 1) * 3) + 1];
		} else if (data[data.length - 1] == '=') {
			bytes = new byte[(((data.length / 4) - 1) * 3) + 2];
		} else {
			bytes = new byte[((data.length / 4) * 3)];
		}
		
		for (int i = 0, j = 0; i < data.length - 4; i += 4, j += 3) {
			b1 = DECODINGTABLE[data[i]];
			b2 = DECODINGTABLE[data[i + 1]];
			b3 = DECODINGTABLE[data[i + 2]];
			b4 = DECODINGTABLE[data[i + 3]];
			
			bytes[j] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte)((b2 << 4) | (b3 >> 2));
			bytes[j + 2] = (byte)((b3 << 6) | b4);
		}
		
		if (data[data.length - 2] == '=') {
			b1 = DECODINGTABLE[data[data.length - 4]];
			b2 = DECODINGTABLE[data[data.length - 3]];
			
			bytes[bytes.length - 1] = (byte)((b1 << 2) | (b2 >> 4));
		} else if (data[data.length - 1] == '=') {
			b1 = DECODINGTABLE[data[data.length - 4]];
			b2 = DECODINGTABLE[data[data.length - 3]];
			b3 = DECODINGTABLE[data[data.length - 2]];
			
			bytes[bytes.length - 2] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 1] = (byte)((b2 << 4) | (b3 >> 2));
		} else {
			b1 = DECODINGTABLE[data[data.length - 4]];
			b2 = DECODINGTABLE[data[data.length - 3]];
			b3 = DECODINGTABLE[data[data.length - 2]];
			b4 = DECODINGTABLE[data[data.length - 1]];
			
			bytes[bytes.length - 3] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 2] = (byte)((b2 << 4) | (b3 >> 2));
			bytes[bytes.length - 1] = (byte)((b3 << 6) | b4);
		}
		
		return bytes;
	}
	
	/**
	 * 解码
	 */
	public static byte[] decode(String data) {
		byte[] bytes;
		byte b1, b2, b3, b4;
		
		if (data.charAt(data.length() - 2) == '=') {
			bytes = new byte[(((data.length() / 4) - 1) * 3) + 1];
		} else if (data.charAt(data.length() - 1) == '=') {
			bytes = new byte[(((data.length() / 4) - 1) * 3) + 2];
		} else {
			bytes = new byte[((data.length() / 4) * 3)];
		}
		
		for (int i = 0, j = 0; i < data.length() - 4; i += 4, j += 3) {
			b1 = DECODINGTABLE[data.charAt(i)];
			b2 = DECODINGTABLE[data.charAt(i + 1)];
			b3 = DECODINGTABLE[data.charAt(i + 2)];
			b4 = DECODINGTABLE[data.charAt(i + 3)];
			
			bytes[j] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte)((b2 << 4) | (b3 >> 2));
			bytes[j + 2] = (byte)((b3 << 6) | b4);
		}
		
		if (data.charAt(data.length() - 2) == '=') {
			b1 = DECODINGTABLE[data.charAt(data.length() - 4)];
			b2 = DECODINGTABLE[data.charAt(data.length() - 3)];
			
			bytes[bytes.length - 1] = (byte)((b1 << 2) | (b2 >> 4));
		} else if (data.charAt(data.length() - 1) == '=') {
			b1 = DECODINGTABLE[data.charAt(data.length() - 4)];
			b2 = DECODINGTABLE[data.charAt(data.length() - 3)];
			b3 = DECODINGTABLE[data.charAt(data.length() - 2)];
			
			bytes[bytes.length - 2] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 1] = (byte)((b2 << 4) | (b3 >> 2));
		} else {
			b1 = DECODINGTABLE[data.charAt(data.length() - 4)];
			b2 = DECODINGTABLE[data.charAt(data.length() - 3)];
			b3 = DECODINGTABLE[data.charAt(data.length() - 2)];
			b4 = DECODINGTABLE[data.charAt(data.length() - 1)];
			
			bytes[bytes.length - 3] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 2] = (byte)((b2 << 4) | (b3 >> 2));
			bytes[bytes.length - 1] = (byte)((b3 << 6) | b4);
		}
		
		return bytes;
	}
}

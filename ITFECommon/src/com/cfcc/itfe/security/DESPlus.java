package com.cfcc.itfe.security;

import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;

import com.cfcc.itfe.util.FileUtil;

/**
 * @author aujlure 加密解密
 */
public class DESPlus {
	
	private static String strDefaultKey = "hzitfe"; // 默认密钥

	private Cipher encryptCipher = null;

	private Cipher decryptCipher = null;

	/**
	 * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
	 * hexStr2ByteArr(String strIn) 互为可逆的转换过程
	 * 
	 * @param arrB
	 *            需要转换的byte数组
	 * @return 转换后的字符串
	 * @throws Exception
	 *             本方法不处理任何异常，所有异常全部抛出
	 */
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
	 * 互为可逆的转换过程
	 * 
	 * @param strIn
	 *            需要转换的字符串
	 * @return 转换后的byte数组
	 * @throws Exception
	 *             本方法不处理任何异常，所有异常全部抛出
	 * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>
	 */
	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * 默认构造方法，使用默认密钥
	 * 
	 * @throws Exception
	 */
	public DESPlus() throws Exception {
		this(strDefaultKey);
	}

	/**
	 * 指定密钥构造方法
	 * 
	 * @param strKey
	 *            指定的密钥
	 * @throws Exception
	 */
	public DESPlus(String strKey) throws Exception {
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Key key = getKey(strKey.getBytes());

		encryptCipher = Cipher.getInstance("DES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);

		decryptCipher = Cipher.getInstance("DES");
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
	}

	/**
	 * 加密字节数组
	 * 
	 * @param arrB
	 *            需加密的字节数组
	 * @return 加密后的字节数组
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] arrB) throws Exception {
		return encryptCipher.doFinal(arrB);
	}

	/**
	 * 加密字符串
	 * 
	 * @param strIn
	 *            需加密的字符串
	 * @return 加密后的字符串
	 * @throws Exception
	 */
	public String encrypt(String strIn) throws Exception {
		return byteArr2HexStr(encrypt(strIn.getBytes()));
	}

	/**
	 * 解密字节数组
	 * 
	 * @param arrB
	 *            需解密的字节数组
	 * @return 解密后的字节数组
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] arrB) throws Exception {
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * 解密字符串
	 * 
	 * @param strIn
	 *            需解密的字符串
	 * @return 解密后的字符串
	 * @throws Exception
	 */
	public String decrypt(String strIn) throws Exception {
		return new String(decrypt(hexStr2ByteArr(strIn)));
	}

	/**
	 * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	 * 
	 * @param arrBTmp
	 *            构成该字符串的字节数组
	 * @return 生成的密钥
	 * @throws java.lang.Exception
	 */
	private Key getKey(byte[] arrBTmp) throws Exception {
		// 创建一个空的8位字节数组（默认值为0）
		byte[] arrB = new byte[8];

		// 将原始字节数组转换为8位
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}

		// 生成密钥
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

		return key;
	}

	public static void main(String[] argv){

		String test = "";
		
//		test = FileUtil.getInstance().readFile("D:\\国库前置资料文件\\杭州\\测试数据\\payout\\2009111611170.xml");

		// DESPlus des = new DESPlus();//默认密钥
		DESPlus des;
		Boolean flag = true;
//		flag = false;
		try {
			if(flag){
				//用于解密(通过修改flag标志，解密.enc文件内容)
				test = "cbf825f1395856000b3961a1bd1c09554cfb67a1b01e41e63af349ae73d35975b03553018b36ec075c73f310cb31cd411fd072e184634a6140a5619bbde99628a1b9ce341e7dc8d058938edbec3926f8a0af6bf84b10bef118630521fec8658bac449ee668d565c49908d1def6f48df93571bb6efc0d7bd67396f608fe2ffba309d12f604163338c59f050374045145fdd5de8b1251014d40f88132b8aed91a93194a308febe09b5058ac5d843e1717bfa5461e9e4d7cc63e0cded348589740fafa18e018b79059f4d0b54d3a3652078615ada1b461a3e6e62ea52164ae97049f959270cd29be71009804dd463758589d51b05e171b232bc63873050d2f3f1e294820a57b3e695d0880bc6b785edf36c9f5217f9ec2a8b4c7fa64c6a45f18eafd5d5bab75df3ba449f5217f9ec2a8b4c6e1074b3fba54e3c84397847c08bc789485b8548b169caaf43691e38e4ed3040ce2c959f761517092f26f7100a0ed20d4a3271913db02a980e2f2627dac943c7f51c5f3266abf64eca2716b4da4377b0ea329393a8e108b81d6f3738f7037944b12ff19fa99cd26c54ab96197a64395f80f81ddfe42796fe9c7cc9b66ef4fe25055daff67097cfce3240a1d2a88f22cee5f7ef528b0e9b9cf15349a4a4b94abeea1b4f1dd2ba2f27a207acf1d9e3ed6d1ac92443d1384a765dc7d4fc9e8501989ae998520adb810ebc76b17a0276c8e8673c1d06fac21c0bb6af1491ee3cea260557a0de8e1c4294a09556fe69e8039046a9db910da5cb96b6f8b75cb48c2205485b8548b169caaf4ae755c3bfd80a53bb1c3b025a13284c1337366a83be504277a746ff0a783913c1a65fbd2ebd402f7a574d000eefa8ea8eeb38c62eae2cc1f630e1d8483a70e7e606cd9f48337bf9484466503a278f3dff02577abdaf30a1f68f7cf52b5c1e773488248ab530897c496337c2ffb43e82b5d91b9e8ec9f799af2c050d79e4f8541bdc501dd39f3bccaf7cac487e85e53f5d1c39b553d05cb21fbebd39b6d190c21fbebd39b6d190c21fbebd39b6d190c2371ae5ac441fc8d1c09aaffbeab00a141002adbec6d583f17f01469c1b786514ce287ef64c8df56ad1818d46c58c33217625657c529ca2be21bf7402912ebc560fca704907c3aa4ea0167b44064f8a369c8bfe3c3bedd482722f43ddf235436a99a30509e6df6aac9c8bfe3c3bedd482b53bdd9c43495ee440a8cddf726ee78c907f3222d07bfce2919ffe183bdd76f24d03bb4bcac84c5c5365738df6ec3d5f72a026b3c833b2c44411780bccad99dd39429cbaac3287f1270c63a296477f30b2eed7f2769f15dbd791de5f31f9fc2972a026b3c833b2c44411780bccad99dd39429cbaac3287f1e2caf4b54cd398c0966d42b9663d359c5365738df6ec3d5fede9afd48b59b4720a3b7aab71e2f99684c301c12f7f04f22087a3f2b29393ccabc77be38c530efb96c47d05c0019756664baa1a00c6de929fc160557efc7a201cce6527ca0a7c8fc52490d2e4bebaefc3aa0623fb4b3ef9f168fcb8dfb5368004917eb4cae271e095a082e63884d205c03874169844723af92b04de0bcbf3b4ce17a9e317823493d6e670a3385cdf8120e4ea2dd87dd8f2bb809fe02e1c8784875a4fef69f1d63a90a073f7ec75911253ef5ccefcf30ad5af4b4e8ff49514606a2c5e8a220a87e52d32840d04a90b7d8dbc6acd28b82dc2";
				des = new DESPlus("0123456789abcdef");
				// 自定义密钥
				
	//			String payout = FileUtil.getInstance().readFile("c:\\testpayout.xml");
				String tmp = des.decrypt(test);
				System.out.println(tmp);
			}else{
				//用于加密(通过修改flag标志)
				test = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n"
					+"<CFX><HEAD><VER>1.0</VER><SRC>100000000000</SRC><DES>111111111111</DES><APP>TIPS</APP><MsgNo>7221</MsgNo><MsgID>20170707722200001132</MsgID><MsgRef>88881105271434290754</MsgRef><WorkDate>20170707</WorkDate></HEAD><MSG><BatchHead7221><TaxOrgCode>11101020000</TaxOrgCode><EntrustDate>20170707</EntrustDate><PackNo>01193664</PackNo><DrawBackTreCode>0103000000</DrawBackTreCode><ReckStyle>2</ReckStyle><AllNum>1</AllNum><AllAmt>555.66</AllAmt></BatchHead7221><DrawbackBody7221><DrawbackInfo7221><TraNo>63861861</TraNo><BillDate>20170707</BillDate><VouNo>111117160546136131</VouNo><Amt>555.66</Amt><BdgLevel>0</BdgLevel><BdgKind>1</BdgKind><BdgSbtCode>101010401</BdgSbtCode><ViceSign>00000000500000050000000000000000000</ViceSign><TrimSign>0</TrimSign><DrawBackReasonCode>10</DrawBackReasonCode><TransType>1</TransType><PayeeOrgCode>0103000000</PayeeOrgCode><PayeeName>金凤凰</PayeeName><TaxPayName>金凤凰</TaxPayName><PayeeBankNo>102100000458</PayeeBankNo><PayeeOpBkCode>102100000458</PayeeOpBkCode><PayeeAcct>0200004509201048588</PayeeAcct><ChannelCode>Z11011101020000</ChannelCode><OriTaxList7221><OritaxInfo7221><SeqNo>1</SeqNo><OriTaxVouNo>111016160341927314</OriTaxVouNo></OritaxInfo7221></OriTaxList7221></DrawbackInfo7221></DrawbackBody7221></MSG></CFX>";
				des = new DESPlus("123456789");
				String tmp = des.encrypt(test);
				System.out.println(tmp);
	//			FileUtil.getInstance().writeFile("D:\\国库前置资料文件\\杭州\\测试数据\\payout\\2009111611170_bak.xml", des.encrypt(test));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

package com.cfcc.itfe.security;

import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;

import com.cfcc.itfe.util.FileUtil;

/**
 * @author aujlure ���ܽ���
 */
public class DESPlus {
	
	private static String strDefaultKey = "hzitfe"; // Ĭ����Կ

	private Cipher encryptCipher = null;

	private Cipher decryptCipher = null;

	/**
	 * ��byte����ת��Ϊ��ʾ16����ֵ���ַ����� �磺byte[]{8,18}ת��Ϊ��0813�� ��public static byte[]
	 * hexStr2ByteArr(String strIn) ��Ϊ�����ת������
	 * 
	 * @param arrB
	 *            ��Ҫת����byte����
	 * @return ת������ַ���
	 * @throws Exception
	 *             �������������κ��쳣�������쳣ȫ���׳�
	 */
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// ÿ��byte�������ַ����ܱ�ʾ�������ַ����ĳ��������鳤�ȵ�����
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// �Ѹ���ת��Ϊ����
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// С��0F������Ҫ��ǰ�油0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * ����ʾ16����ֵ���ַ���ת��Ϊbyte���飬 ��public static String byteArr2HexStr(byte[] arrB)
	 * ��Ϊ�����ת������
	 * 
	 * @param strIn
	 *            ��Ҫת�����ַ���
	 * @return ת�����byte����
	 * @throws Exception
	 *             �������������κ��쳣�������쳣ȫ���׳�
	 * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>
	 */
	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		// �����ַ���ʾһ���ֽڣ������ֽ����鳤�����ַ������ȳ���2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * Ĭ�Ϲ��췽����ʹ��Ĭ����Կ
	 * 
	 * @throws Exception
	 */
	public DESPlus() throws Exception {
		this(strDefaultKey);
	}

	/**
	 * ָ����Կ���췽��
	 * 
	 * @param strKey
	 *            ָ������Կ
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
	 * �����ֽ�����
	 * 
	 * @param arrB
	 *            ����ܵ��ֽ�����
	 * @return ���ܺ���ֽ�����
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] arrB) throws Exception {
		return encryptCipher.doFinal(arrB);
	}

	/**
	 * �����ַ���
	 * 
	 * @param strIn
	 *            ����ܵ��ַ���
	 * @return ���ܺ���ַ���
	 * @throws Exception
	 */
	public String encrypt(String strIn) throws Exception {
		return byteArr2HexStr(encrypt(strIn.getBytes()));
	}

	/**
	 * �����ֽ�����
	 * 
	 * @param arrB
	 *            ����ܵ��ֽ�����
	 * @return ���ܺ���ֽ�����
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] arrB) throws Exception {
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * �����ַ���
	 * 
	 * @param strIn
	 *            ����ܵ��ַ���
	 * @return ���ܺ���ַ���
	 * @throws Exception
	 */
	public String decrypt(String strIn) throws Exception {
		return new String(decrypt(hexStr2ByteArr(strIn)));
	}

	/**
	 * ��ָ���ַ���������Կ����Կ������ֽ����鳤��Ϊ8λ ����8λʱ���油0������8λֻȡǰ8λ
	 * 
	 * @param arrBTmp
	 *            ���ɸ��ַ������ֽ�����
	 * @return ���ɵ���Կ
	 * @throws java.lang.Exception
	 */
	private Key getKey(byte[] arrBTmp) throws Exception {
		// ����һ���յ�8λ�ֽ����飨Ĭ��ֵΪ0��
		byte[] arrB = new byte[8];

		// ��ԭʼ�ֽ�����ת��Ϊ8λ
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}

		// ������Կ
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

		return key;
	}

	public static void main(String[] argv){

		String test = "";
		
//		test = FileUtil.getInstance().readFile("D:\\����ǰ�������ļ�\\����\\��������\\payout\\2009111611170.xml");

		// DESPlus des = new DESPlus();//Ĭ����Կ
		DESPlus des;
		Boolean flag = true;
//		flag = false;
		try {
			if(flag){
				//���ڽ���(ͨ���޸�flag��־������.enc�ļ�����)
				test = "cbf825f1395856000b3961a1bd1c09554cfb67a1b01e41e63af349ae73d35975b03553018b36ec075c73f310cb31cd411fd072e184634a6140a5619bbde99628a1b9ce341e7dc8d058938edbec3926f8a0af6bf84b10bef118630521fec8658bac449ee668d565c49908d1def6f48df93571bb6efc0d7bd67396f608fe2ffba309d12f604163338c59f050374045145fdd5de8b1251014d40f88132b8aed91a93194a308febe09b5058ac5d843e1717bfa5461e9e4d7cc63e0cded348589740fafa18e018b79059f4d0b54d3a3652078615ada1b461a3e6e62ea52164ae97049f959270cd29be71009804dd463758589d51b05e171b232bc63873050d2f3f1e294820a57b3e695d0880bc6b785edf36c9f5217f9ec2a8b4c7fa64c6a45f18eafd5d5bab75df3ba449f5217f9ec2a8b4c6e1074b3fba54e3c84397847c08bc789485b8548b169caaf43691e38e4ed3040ce2c959f761517092f26f7100a0ed20d4a3271913db02a980e2f2627dac943c7f51c5f3266abf64eca2716b4da4377b0ea329393a8e108b81d6f3738f7037944b12ff19fa99cd26c54ab96197a64395f80f81ddfe42796fe9c7cc9b66ef4fe25055daff67097cfce3240a1d2a88f22cee5f7ef528b0e9b9cf15349a4a4b94abeea1b4f1dd2ba2f27a207acf1d9e3ed6d1ac92443d1384a765dc7d4fc9e8501989ae998520adb810ebc76b17a0276c8e8673c1d06fac21c0bb6af1491ee3cea260557a0de8e1c4294a09556fe69e8039046a9db910da5cb96b6f8b75cb48c2205485b8548b169caaf4ae755c3bfd80a53bb1c3b025a13284c1337366a83be504277a746ff0a783913c1a65fbd2ebd402f7a574d000eefa8ea8eeb38c62eae2cc1f630e1d8483a70e7e606cd9f48337bf9484466503a278f3dff02577abdaf30a1f68f7cf52b5c1e773488248ab530897c496337c2ffb43e82b5d91b9e8ec9f799af2c050d79e4f8541bdc501dd39f3bccaf7cac487e85e53f5d1c39b553d05cb21fbebd39b6d190c21fbebd39b6d190c21fbebd39b6d190c2371ae5ac441fc8d1c09aaffbeab00a141002adbec6d583f17f01469c1b786514ce287ef64c8df56ad1818d46c58c33217625657c529ca2be21bf7402912ebc560fca704907c3aa4ea0167b44064f8a369c8bfe3c3bedd482722f43ddf235436a99a30509e6df6aac9c8bfe3c3bedd482b53bdd9c43495ee440a8cddf726ee78c907f3222d07bfce2919ffe183bdd76f24d03bb4bcac84c5c5365738df6ec3d5f72a026b3c833b2c44411780bccad99dd39429cbaac3287f1270c63a296477f30b2eed7f2769f15dbd791de5f31f9fc2972a026b3c833b2c44411780bccad99dd39429cbaac3287f1e2caf4b54cd398c0966d42b9663d359c5365738df6ec3d5fede9afd48b59b4720a3b7aab71e2f99684c301c12f7f04f22087a3f2b29393ccabc77be38c530efb96c47d05c0019756664baa1a00c6de929fc160557efc7a201cce6527ca0a7c8fc52490d2e4bebaefc3aa0623fb4b3ef9f168fcb8dfb5368004917eb4cae271e095a082e63884d205c03874169844723af92b04de0bcbf3b4ce17a9e317823493d6e670a3385cdf8120e4ea2dd87dd8f2bb809fe02e1c8784875a4fef69f1d63a90a073f7ec75911253ef5ccefcf30ad5af4b4e8ff49514606a2c5e8a220a87e52d32840d04a90b7d8dbc6acd28b82dc2";
				des = new DESPlus("0123456789abcdef");
				// �Զ�����Կ
				
	//			String payout = FileUtil.getInstance().readFile("c:\\testpayout.xml");
				String tmp = des.decrypt(test);
				System.out.println(tmp);
			}else{
				//���ڼ���(ͨ���޸�flag��־)
				test = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n"
					+"<CFX><HEAD><VER>1.0</VER><SRC>100000000000</SRC><DES>111111111111</DES><APP>TIPS</APP><MsgNo>7221</MsgNo><MsgID>20170707722200001132</MsgID><MsgRef>88881105271434290754</MsgRef><WorkDate>20170707</WorkDate></HEAD><MSG><BatchHead7221><TaxOrgCode>11101020000</TaxOrgCode><EntrustDate>20170707</EntrustDate><PackNo>01193664</PackNo><DrawBackTreCode>0103000000</DrawBackTreCode><ReckStyle>2</ReckStyle><AllNum>1</AllNum><AllAmt>555.66</AllAmt></BatchHead7221><DrawbackBody7221><DrawbackInfo7221><TraNo>63861861</TraNo><BillDate>20170707</BillDate><VouNo>111117160546136131</VouNo><Amt>555.66</Amt><BdgLevel>0</BdgLevel><BdgKind>1</BdgKind><BdgSbtCode>101010401</BdgSbtCode><ViceSign>00000000500000050000000000000000000</ViceSign><TrimSign>0</TrimSign><DrawBackReasonCode>10</DrawBackReasonCode><TransType>1</TransType><PayeeOrgCode>0103000000</PayeeOrgCode><PayeeName>����</PayeeName><TaxPayName>����</TaxPayName><PayeeBankNo>102100000458</PayeeBankNo><PayeeOpBkCode>102100000458</PayeeOpBkCode><PayeeAcct>0200004509201048588</PayeeAcct><ChannelCode>Z11011101020000</ChannelCode><OriTaxList7221><OritaxInfo7221><SeqNo>1</SeqNo><OriTaxVouNo>111016160341927314</OriTaxVouNo></OritaxInfo7221></OriTaxList7221></DrawbackInfo7221></DrawbackBody7221></MSG></CFX>";
				des = new DESPlus("123456789");
				String tmp = des.encrypt(test);
				System.out.println(tmp);
	//			FileUtil.getInstance().writeFile("D:\\����ǰ�������ļ�\\����\\��������\\payout\\2009111611170_bak.xml", des.encrypt(test));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

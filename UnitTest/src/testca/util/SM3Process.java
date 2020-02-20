package testca.util;

/**
 * @author  中国金融电子化公司国库前置项目组
 * @version 创建时间：2012-11-01 上午11:03:41
 * @		修改时间：2012-11-21 
 * 
	Testing data from SM3 Standards
	http://www.oscca.gov.cn/News/201012/News_1199.htm 
	Sample 1
	Input:"abc"  
	Output:66c7f0f4 62eeedd9 d1f2d46b dc10e4e2 4167c487 5cf2f7a2 297da02b 8f4ba8e0
	
	Sample 2 
	Input:"abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd"
	Outpuf:debe9ff9 2275b8a1 38604889 c18e5a4d 6fdb70e5 387e5765 293dcba3 9c0c5732
 *
 */
public class SM3Process {

	private static  String toHexString(byte[] data) {
		byte temp;
		int n;
		String str = "";
		for (int i = 1; i <= data.length; i++) {
			temp = data[i-1];
			n = (int) ((temp & 0xf0) >> 4);
			str += IntToHex(n);
			n = (int) ((temp & 0x0f));
			str += IntToHex(n);
//			str += " ";
			if (i % 16 == 0) {
//				str += "\n";
			}
		}

		return str;
	}
	
	public  void printWithHex(byte[] data) {
		System.out.println(toHexString(data));
	}

	public static  String IntToHex(int n) {
		if (n > 15 || n < 0) {
			return "";
		} else if ((n >= 0) && (n <= 9)) {
			return "" + n;
		} else {
			switch (n) {
			case 10: {
				return "A";
			}
			case 11: {
				return "B";
			}
			case 12: {
				return "C";
			}
			case 13: {
				return "D";
			}
			case 14: {
				return "E";
			}
			case 15: {
				return "F";
			}
			default:
				return "";
			}
		}
	}
	
	public static boolean verifySM3Sign(String Data, String key){
		boolean isCorrect = false;
		
		//数据不合法时直接退出
		if(Data.indexOf("<CA>") < 0 || Data.indexOf("</CA>") < 0)
		{
			return isCorrect;
		}
		try{
			//取签名前的数据
			String inputdata = Data.substring(0,Data.indexOf("<CA>"));
			String verifydata = inputdata + key;
			byte[] byteData = verifydata.getBytes();
			
			//计算电子签名
			SM3Digest digest = new SM3Digest();
			digest.update(byteData, 0, byteData.length);
			
			byte[] out = new byte[32];
			digest.doFinal(out, 0);
			String sign = toHexString(out);
			
			//取业务数据的电子签名
			String dataSign = Data.substring(Data.indexOf("<CA>") + 4, Data.indexOf("</CA>"));
			
			//把计算的签名和业务数据电子签名进行比较，相同时，返回成功，否则返回失败。
			if(sign != null && dataSign != null && sign.equals(dataSign)){
				isCorrect = true;
			}
		}catch(Exception e){
			e.printStackTrace();
			return isCorrect;
		}
		
		return isCorrect;
	}

	/**
	 * 计算SM3签名
	 * @param signData
	 * 			待签名的数据
	 * @param key
	 * 			签名密钥
	 * @return
	 * 			sm3签名
	 */
	public static String calculateSign(String signData, String key){
		try{
			//组合待签名数据和密钥
			String Data = signData + key;
			//计算电子签名
			byte[] byteData = Data.getBytes();
			SM3Digest digest = new SM3Digest();
			digest.update(byteData, 0, byteData.length);
			byte[] out = new byte[32];
			digest.doFinal(out, 0);
			String sign = toHexString(out);
			//根据数据和密钥返回签名信息
			return sign;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}

package testca.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

public class TestCA {

	
	/**
	 * 山东退库和实拨资金校验码
	 * @param vouchNo    凭证号码
	 * @param subCode    科目代码
	 * @param rcvAccount 收款账号
	 * @param amt        金额
	 * @param key        密钥
	 * @return
	 */
	public static String getMD5(String vouchNo, String subCode, String rcvAccount,
			BigDecimal amt , String key) {
		MD5ForSD md5 = new MD5ForSD();
		StringBuffer buffer = new StringBuffer();
		String value = buffer.append(vouchNo).append(subCode).append(rcvAccount).toString();
		long damt = Math.round(amt.doubleValue()*100);               
		return md5.getMD5ofStr(value+damt+key);
	}
	
	public static int calculate_XYM(String filename, int CAByte)
	{
		File file = new File(filename);
		int asc = 0; //asc码
		int cal = 0; //2^n
		int wasc = 0; //权
		int xym = 0;  //验证码

		try {
			//读文件
			FileInputStream fin = new FileInputStream(file);
			//文件长度
			int   size   =   fin.available(); 
			byte[]   temp   =   new   byte[size]; //字节数组

			if (fin.read(temp)   !=   size){ 
				System.out.println( "读不了文件... "); 
			} 
			
			String s2 = ""; //二进制
			String s10 = ""; //十进制

			for (int i = 0; i < temp.length - CAByte; i++) {
				char cchar = (char)temp[i];
				asc = (int)cchar;
				if(asc > 255)
				{
					s2 = Integer.toBinaryString(asc); //二进制
					s10 = Integer.valueOf(s2.substring(s2.length()-8),2).toString(); //单字节10进制
					asc = Integer.parseInt(s10); //单字节二进制asc码
				}
				cal = (i + 1) % 11;
				wasc = (int) Math.pow(2, cal); //算加权
				xym = (xym + asc * wasc);  //校验码计算
			}
			xym = xym % 1000000; //校验码计算
			//校验码< 100000时，xym = xym + 100000
			if (xym < 100000) {
				xym = xym + 100000;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}	
		return xym;
	}
	
	public static boolean verifyCA(String fileName)
	{
		boolean isCorrect = false;
		
		File file = new File(fileName);
		//读文件
		FileInputStream fin;
		try {
			fin = new FileInputStream(file);
		//文件长度
		int size;
			size = fin.available();
		byte[]   temp   =   new   byte[size]; //字节数组
		
		if(fin.read(temp)   !=   size){ 
			System.out.println( "读不了文件... "); 
		} 

		//计算验证码
		int xym = calculate_XYM(fileName, 8);
		
		//取文件验证码
		String CAValue = "";
		for(int i = size - 8 ; i <size - 2; i++)
		{
			char ctemp = (char)temp[i];
			CAValue = CAValue + (char)temp[i];
		}
		
		System.out.println(CAValue);
		
		//文件验证码和计算出的验证码如果相等返回TRUE
		if(Integer.parseInt(CAValue) == xym)
		{
			isCorrect = true;
			System.out.println("签名正确");
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	} 
		
		return isCorrect;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int xym = calculate_XYM("d:\\20120305cz1100000q001.txt ",8);
		System.out.println(xym);
		xym = calculate_XYM("d:\\20090209ds010000000f007.txt ",8);
		System.out.println(xym);
		verifyCA("d:\\20120305cz1100000q001.txt");
		verifyCA("d:\\20090209ds010000000f007.txt ");
	}
}

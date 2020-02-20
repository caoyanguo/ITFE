package testca.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

public class TestCA {

	
	/**
	 * ɽ���˿��ʵ���ʽ�У����
	 * @param vouchNo    ƾ֤����
	 * @param subCode    ��Ŀ����
	 * @param rcvAccount �տ��˺�
	 * @param amt        ���
	 * @param key        ��Կ
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
		int asc = 0; //asc��
		int cal = 0; //2^n
		int wasc = 0; //Ȩ
		int xym = 0;  //��֤��

		try {
			//���ļ�
			FileInputStream fin = new FileInputStream(file);
			//�ļ�����
			int   size   =   fin.available(); 
			byte[]   temp   =   new   byte[size]; //�ֽ�����

			if (fin.read(temp)   !=   size){ 
				System.out.println( "�������ļ�... "); 
			} 
			
			String s2 = ""; //������
			String s10 = ""; //ʮ����

			for (int i = 0; i < temp.length - CAByte; i++) {
				char cchar = (char)temp[i];
				asc = (int)cchar;
				if(asc > 255)
				{
					s2 = Integer.toBinaryString(asc); //������
					s10 = Integer.valueOf(s2.substring(s2.length()-8),2).toString(); //���ֽ�10����
					asc = Integer.parseInt(s10); //���ֽڶ�����asc��
				}
				cal = (i + 1) % 11;
				wasc = (int) Math.pow(2, cal); //���Ȩ
				xym = (xym + asc * wasc);  //У�������
			}
			xym = xym % 1000000; //У�������
			//У����< 100000ʱ��xym = xym + 100000
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
		//���ļ�
		FileInputStream fin;
		try {
			fin = new FileInputStream(file);
		//�ļ�����
		int size;
			size = fin.available();
		byte[]   temp   =   new   byte[size]; //�ֽ�����
		
		if(fin.read(temp)   !=   size){ 
			System.out.println( "�������ļ�... "); 
		} 

		//������֤��
		int xym = calculate_XYM(fileName, 8);
		
		//ȡ�ļ���֤��
		String CAValue = "";
		for(int i = size - 8 ; i <size - 2; i++)
		{
			char ctemp = (char)temp[i];
			CAValue = CAValue + (char)temp[i];
		}
		
		System.out.println(CAValue);
		
		//�ļ���֤��ͼ��������֤�������ȷ���TRUE
		if(Integer.parseInt(CAValue) == xym)
		{
			isCorrect = true;
			System.out.println("ǩ����ȷ");
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

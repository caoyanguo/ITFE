package com.cfcc.test.testcommon.util;

import java.util.List;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.security.SM3Util;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;


public class Test_FileOperUtil {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	public static void main(String[] args) throws FileOperateException {
//		String str="1900000000,190000000002271001,,888,888,3602000909005388993,1,209019901,,,1,100.00,99990001,�㶫ʡ��������ᱣ�ϻ������ר��,102581000089,��ת��β3��26�ջ������ϱ��յ�����";
//		String str="1900000000,190000000002271001,,604,604,190300000003271001,1,2300308,,,2,100.00,40250020,��ͷ�в�����,011586004004,������2013��29��";
//        String str =" 1900000000,�������������֧���ʽ�,44-028801012001516,333333333333,103581002880,�㶫ʡ������,190000000002271001,,,1,100.00,0,,40408101,20130408";
//        String str1="SZ064941,20130408,117,2011502,302,1,100.00";
//		String str ="1900000000,10000525,333333333333,178,1,1,60000.00,0.00";
//		String str1="2081399,,60000.00,0.00";
		String str ="1900000000,�㶫ʡ������,190000000002271001,,,�������й��ݱ���·֧��,7443400127304005601,777777777703,302581044347,1,200.00,0,,99408112,20130408";
		String str1="156,2059999,301,1,200.00";
		//ʵ��
       
        
			for (int i = 34; i < 90; i++) {
				
				String filename = "F:\\�����ļ�\\����������������\\20130410"+i+"270.txt";
				String fileContent =str.replace("99408112", Integer.valueOf(99408112+i)+"").replace("200.00", Integer.valueOf((int) (200.00+i*10))+".00");
				fileContent+="\r\n"+str1.replace("200.00", Integer.valueOf((int) (200.00+i*10))+".00");
	    		FileUtil.getInstance().writeFile(filename, fileContent);
				String key ="D69C56265D21016971EDF40240DBA15EBCCC9E628B0E3271";
				SM3Util sm3 = new SM3Util();
				sm3.addSM3Sign(filename, key);
			}
		
		

	}
}

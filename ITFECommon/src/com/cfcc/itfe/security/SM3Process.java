package com.cfcc.itfe.security;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.SinoDetect;

/**
 * @author  �й����ڵ��ӻ���˾����ǰ����Ŀ��
 * @version ����ʱ�䣺2012-11-01 ����11:03:41
 * @		�޸�ʱ�䣺2012-11-21 
 * 
	Testing data from SM3 Standards(SM3 ��׼�Ĳ�������)
	Sample 1
	Input:"abc"  
	Output:66c7f0f4 62eeedd9 d1f2d46b dc10e4e2 4167c487 5cf2f7a2 297da02b 8f4ba8e0
	
	Sample 2 
	Input:"abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd"
	Outpuf:debe9ff9 2275b8a1 38604889 c18e5a4d 6fdb70e5 387e5765 293dcba3 9c0c5732
 *
 */
public class SM3Process {
	
	private static Log log = LogFactory.getLog(SM3Process.class);

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
	
	/**
	 * ��֤SM3ǩ��
	 * @param Data
	 * 			����SM3ǩ��������
	 * @param key
	 * 			ǩ����Կ
	 * @return
	 */
	public static boolean verifySM3Sign(String Data, String key){
		boolean isCorrect = false;
		
		//���ݲ��Ϸ�ʱֱ���˳�
		if(Data.indexOf("<CA>") < 0 || Data.indexOf("</CA>") < 0)
		{
			return isCorrect;
		}
		try{
			//ȡǩ��ǰ������
			String inputdata = Data.substring(0,Data.indexOf(",<CA>"));
			String verifydata = inputdata + key;
			byte[] byteData = verifydata.getBytes();
			
			//�������ǩ��
			SM3Digest digest = new SM3Digest();
			digest.update(byteData, 0, byteData.length);
			
			byte[] out = new byte[32];
			digest.doFinal(out, 0);
			String sign = toHexString(out);
			
			//ȡҵ�����ݵĵ���ǩ��
			String dataSign = Data.substring(Data.indexOf("<CA>") + 4, Data.indexOf("</CA>"));
			
			//�Ѽ����ǩ����ҵ�����ݵ���ǩ�����бȽϣ���ͬʱ�����سɹ������򷵻�ʧ�ܡ�
			if(sign != null && dataSign != null && sign.equals(dataSign)){
				isCorrect = true;
			}
		}catch(Exception e){
			return isCorrect;
		}
		
		return isCorrect;
	}
	
	/**
	 * SM3��ǩ(�ļ���βʹ��xmlע�ʹ���ǩ���ַ���<!--ǩ���ַ���-->)
	 * @param fileContent  ����SM3ǩ��������
	 * @param key  ǩ����Կ
	 * @throws ITFEBizException
	 */
	public static void checkSM3Sign(String fileContent, String key) throws ITFEBizException {
		if ((key == null) || (key.length() == 0)){
			throw new ITFEBizException("��ԿΪ��");
		}
		if ((fileContent == null) || (fileContent.length() == 0)){
			throw new ITFEBizException("����֤ǩ��������Ϊ��");
		}
		int iBegin, iEnd;
		iBegin = fileContent.lastIndexOf("<!--");
		iEnd = fileContent.lastIndexOf("-->");
		if (iBegin < 0 || iEnd < 0) {
			StringBuffer msg = new StringBuffer();
			msg.append("���ұ���ǩ������, Begin:").append(iBegin);
			msg.append(",End:").append(iEnd);
			log.error(msg.toString());
			throw new ITFEBizException(msg.toString());
		}
		//ȡҵ�����ݵĵ���ǩ��
		String sign = fileContent.substring(iBegin + 4, iEnd);
		iEnd = fileContent.lastIndexOf("</CFX>");
		if (iEnd < 0) {
			StringBuffer msg = new StringBuffer();
			msg.append("���ұ��������,End:").append(iEnd);
			log.error(msg.toString());
			throw new ITFEBizException(msg.toString());
		}
		String data = fileContent.substring(0, iBegin);
		
		String verifydata = data + key;
		byte[] byteData = verifydata.getBytes();
		
		//�������ǩ��
		SM3Digest digest = new SM3Digest();
		digest.update(byteData, 0, byteData.length);
		
		byte[] out = new byte[32];
		digest.doFinal(out, 0);
		String signHex = toHexString(out);
		
		//�Ѽ����ǩ����ҵ�����ݵ���ǩ�����бȽ�
		if(signHex != null && sign != null && signHex.equals(sign)){
			log.info("У��ԭǩ����" + sign +" �ɹ���");
		}else{
			log.error("��ǩʧ�ܣ�ԭ�Ļ򱻴۸ģ�");
			throw new ITFEBizException("��ǩʧ�ܣ�ԭ�Ļ򱻴۸ģ�");
		}
	}

	/**
	 * ����SM3ǩ��
	 * @param signData
	 * 			��ǩ��������
	 * @param key
	 * 			ǩ����Կ
	 * @return
	 * 			sm3ǩ��
	 */
	public static String calculateSign(String signData, String key){
		try{
			//��ϴ�ǩ�����ݺ���Կ
			String Data = signData + key;
			//�������ǩ��
			byte[] byteData = Data.getBytes();
			SM3Digest digest = new SM3Digest();
			digest.update(byteData, 0, byteData.length);
			byte[] out = new byte[32];
			digest.doFinal(out, 0);
			String sign = toHexString(out);
			//�������ݺ���Կ����ǩ����Ϣ
			return sign;
		}catch(Exception e){
			return null;
		}
	}
	/**
	 * ��ǩ����
	 * @param fileName
	 * @param key
	 * @return (succ:0;faild:-1)
	 * @throws IOException 
	 */
	public static int addSM3Sign(String fileName, String key) throws IOException{
		int flag = -1;
		BufferedReader input = null;
		StringBuffer encDataTmp = new StringBuffer();
		String encData = null;
		File file = new File(fileName);
		String encode =getFileEncoding(fileName);
		input = new BufferedReader(new InputStreamReader(new FileInputStream(
				file),encode));
		String lineContent = null;
		while((lineContent = input.readLine())!=null){
			if(!"".equals(lineContent)){
				encDataTmp.append(SM3Process.calculateSign(lineContent, key));
			}
		}
		encData = SM3Process.calculateSign(encDataTmp.toString(), key);
        if (input != null) {
        	input.close();
        }
		encData = "<CA>" + encData + "</CA>";
        //��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
        FileWriter writer = new FileWriter(fileName, true);
        writer.write("\r\n" + encData);
        writer.close();
        flag = 0;
        return flag;
	}
	
	/**
	 * ��ǩ����
	 * @param fileName
	 * @param key
	 * @return
	 * @throws IOException 
	 */
	public static boolean verifySM3SignFile(String fileName, String key) throws IOException {
		boolean isCorrect = false;
		StringBuffer encDataTmp = new StringBuffer();
		String encData = null;
		String sign = null;
		String encode =getFileEncoding(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
				fileName),encode));
		String lineContent = null;
		while((lineContent = br.readLine())!=null){
			if(!"".equals(lineContent) && !lineContent.startsWith("<CA>")){
				encDataTmp.append(SM3Process.calculateSign(lineContent, key));
			}else if(lineContent.startsWith("<CA>")){
				sign = lineContent.substring(lineContent.indexOf("<CA>") + 4, lineContent.indexOf("</CA>"));
			}
		}
		encData = SM3Process.calculateSign(encDataTmp.toString(), key);
		if(sign != null && encData != null && sign.equals(encData)){
			return true;
		}
		br.close();
		return isCorrect;
	}

	
	
	public static void main(String[] args)
	{
		/*SM3Process process = new SM3Process();
//		String ss =process.calculateSign("ab", "c");
//		System.out.println(ss);
		
		int i;
		try {
			SM3Util t = new SM3Util();
			 t.addSM3Sign("C:\\201305097301260.txt", "D69C56265D21016971EDF40240DBA15EBCCC9E628B0E3271");
			Boolean b = process.verifySM3SignFile("C:\\201305097301260.txt", "D69C56265D21016971EDF40240DBA15EBCCC9E628B0E3271");
//			System.out.println(i);
			System.out.println(b);
			t.addSM3Sign("C:\\2012031401130.txt", "D69C56265D21016971EDF40240DBA15EBCCC9E628B0E3271");
			Boolean m =process.verifySM3SignFile("C:\\2012031401130.txt", "D69C56265D21016971EDF40240DBA15EBCCC9E628B0E3271");
//			System.out.println(j);
			System.out.println(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		SM3Process process = new SM3Process();
		String xmlStr = "";
		try {
			xmlStr = process.readFile("C:\\tmp\\2000_e3009e256f1c44fea8f5babf8939b7b8.msg");
		} catch (FileOperateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		String xmlStr = "<?xml version=\"1.0\" encoding=\"GBK\"?><CFX><HEAD><VER>1.0</VER><SRC>102000000000</SRC><DES>111111111111</DES><APP>TCQS</APP><MsgNo>3000</MsgNo><MsgID>92016040500000007409</MsgID><MsgRef>92016040500000007401</MsgRef><WorkDate>20160405</WorkDate></HEAD></CFX>";
//		String xmlStr = "<?xml version=\"1.0\" encoding=\"GBK\"?><CFX><HEAD><VER>1.0</VER><SRC>111111111111</SRC><DES>110000000000</DES><APP>TCQS</APP><MsgNo>1000</MsgNo><MsgID>92017041300000033632</MsgID><MsgRef>92017041300000033632</MsgRef><WorkDate>20170413</WorkDate></HEAD><MSG><BatchHead1000><BillOrg>110606</BillOrg><EntrustDate>20170413</EntrustDate><PackNo>00034032</PackNo><TreCode>0106000000</TreCode><ChangeNo></ChangeNo><AllNum>1</AllNum><AllAmt>823798.09</AllAmt><PayoutVouType>1</PayoutVouType></BatchHead1000><BillSend1000><TraNo>00034032</TraNo><VouNo>170000355</VouNo><VouDate>20170412</VouDate><PayerAcct>0200004509026200178</PayerAcct><PayerName>�����к�����������</PayerName><PayerAddr></PayerAddr><Amt>823798.09</Amt><PayeeBankNo>103100021092</PayeeBankNo><PayeeOpBkNo>103100021092</PayeeOpBkNo><PayeeAcct>11210901040006616</PayeeAcct><PayeeName>�������򷻵�ҽԺ</PayeeName><PayReason></PayReason><BudgetSubjectCode></BudgetSubjectCode><AddWord></AddWord><OfYear>2017</OfYear><Flag>2</Flag></BillSend1000></MSG></CFX>";
//		String sm3Sign = SM3Process.calculateSign(xmlStr, "icfcc");
//		System.out.println(sm3Sign);
//		
//		String xmlsm3 = xmlStr + "<!--" + sm3Sign + "-->";
		try {
			SM3Process.checkSM3Sign(xmlStr, "icfcc");
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if(args!=null&&args.length==2)
//		{
//			process.addSignMain(args);
//		}else if(args!=null&&args.length==3)
//		{
//			process.verifySignMain(args);
//		}
//		else
//		{
//			System.out.println("��ǩ���봫��������������һ������Ϊ��ǩ��Կ���ڶ�������Ϊ��Ҫ��ǩ�ļ���Ŀ¼!");
//			System.out.println("��ǩ���봫��������������һ������Ϊ��ǩ��Կ���ڶ�������Ϊ��Ҫ��ǩ�ļ���Ŀ¼������������Ϊ��ǩ��ʶ����Ϊverifysign");
//		}
	}
	
	/**
	 * ���ı��ļ��ж����ַ���
	 * 
	 * @param fileName
	 *            �ļ���
	 * @return �ļ��е�����
	 */
	public String readFile(String fileName) throws FileOperateException {
		long lBegin = 0;

		FileInputStream input = null;
		String message = null;
		try {
			input = new FileInputStream(fileName);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];

			do {
				int size = input.read(buffer);
				if (size == -1)
					break;
				byteArray.write(buffer, 0, size);
			} while (true);
			byte[] data = byteArray.toByteArray();
			message = new String(data, "GBK");

		} catch (FileNotFoundException e) {
			String msg = new String("��ȡ�ļ�ʧ��,δ�ҵ��ļ�." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (IOException e) {
			String msg = new String("��ȡ�ļ�ʧ��,IO����." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("��ȡ�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {
					log.error("�ر��ļ�����" + fileName, ex);

				}
			}
		}

		return message;

	}
	
	/**
	 * @function ��ǩ������������������飬��һ������Ϊ��ǩ��Կ���ڶ�������Ϊ��Ҫ��ǩ�ļ���Ŀ¼������������Ϊ��ǩ��ʶ����Ϊverifysign
	 * @param args
	 */
	private void verifySignMain(String[]args)
	{
		if(args[0]!=null&&args[1]!=null&&args[2]!=null&&!"".equals(args[0])&&!"".equals(args[1])&&!"".equals(args[2])&&"verifysign".equals(args[2].toLowerCase()))
		{
			File fileDir = null;
			fileDir = new File(args[1]);
			StringBuffer result = new StringBuffer("");
			if(fileDir.isDirectory())
			{
				File[] files = fileDir.listFiles(new FilenameFilter()
				{
					public boolean accept(File file, String fileName)
					{				
						if(fileName.endsWith(".txt"))
						{					
							return true;
						}
						return false;
					}		
				});
				for(int i=0;i<files.length;i++)
						result.append(files[i]+readFileClient(files[i],args[0])+System.getProperty("line.separator"));
			}else if(fileDir.isFile())
			{
				result.append(fileDir+readFileClient(fileDir,args[0])+System.getProperty("line.separator"));
			}else if(!fileDir.exists())
			{
				result.append(args[1]+"������!");
			}
			System.out.println("��ǩ���:"+System.getProperty("line.separator")+result.toString());
		}else
			System.out.println("��ǩʧ��:�����������Ϊ�����Ҷ���Ϊ���ҵ�������������Ϊverifysign");
	}
	/**
	 * @function ��ǩ���������������飬��һ������Ϊ��ǩ��Կ���ڶ�������Ϊ��Ҫ��ǩ�ļ���Ŀ¼
	 * @param args
	 */
	private void addSignMain(String[] args)
	{
		File fileDir = null;
		fileDir = new File(args[1]);
		String getString = null;
		if(fileDir.isDirectory())
		{
			File[] files = fileDir.listFiles(new FilenameFilter()
			{
				public boolean accept(File file, String fileName)
				{				
					if(fileName.endsWith(".txt"))
					{					
						return true;
					}
					return false;
				}		
			});
			getString = fileAddSign(files,args[0],args[1]);
		}else if(fileDir.isFile())
		{
			getString = fileAddSign(fileDir,args[0],fileDir.getParent());
		}else if(!fileDir.exists())
		{
			getString = args[1]+"������!";
		}
		System.out.println("��ǩ���:"+System.getProperty("line.separator")+getString);
	}
	/**
	 * @function ������Ҫ��ǩ���ļ��б�������Կ�������ļ�������Ŀ¼
	 * @param files�ļ��б�
	 * @param signKey��ǩ��Կ
	 * @param filePath�ļ�������Ŀ¼
	 * @return
	 */
	private String fileAddSign(File[] files,String signKey,String filePath)
	{
		StringBuffer getString = new StringBuffer("");
		if(files!=null&&!"".equals(signKey)&&null!=signKey)
		{
			for(int i=0;i<files.length;i++)
			{
				getString.append(fileAddSign(files[i],signKey,filePath));
			}
		}else
		{
			getString.append("��ǩ�ļ�Ŀ¼���ǩ��ԿΪ��!");
		}
		return getString.toString();
	}
	/**
	 * @function ������Ҫ��ǩ���ļ���������Կ�������ļ�������Ŀ¼
	 * @param files��Ҫ��ǩ�ļ�
	 * @param signKey��ǩ��Կ
	 * @param filePath�ļ�������Ŀ¼
	 * @return
	 */
	private String fileAddSign(File file,String signKey,String filePath)
	{
		StringBuffer getString = new StringBuffer("");
		if(file!=null&&null!=signKey&&!"".equals(signKey)&&filePath!=null&&!"".equals(filePath))
		{
			BufferedReader input = null;
			try
			{
				input = new BufferedReader(new FileReader(file)); 
				String lineContent = null;
				StringBuffer fileContent = new StringBuffer("");
				while((lineContent = input.readLine())!=null){
					fileContent.append(lineContent+",<CA>"+calculateSign(lineContent,signKey)+"</CA>"+System.getProperty("line.separator")); 
				} 
				input.close(); 
				if(fileContent!=null&&!"".equals(fileContent.toString()))
				{
					if(writeFile(filePath+System.getProperty("file.separator")+"sign"+System.getProperty("file.separator")+file.getName(),fileContent.toString()));//+"<CA>"+sign+"</CA>"
						getString.append(file+"��ǩ�ɹ�"+System.getProperty("line.separator"));
				}else if("".equals(fileContent))
				{
					getString.append(file+"�ļ�����Ϊ��,��ǩʧ��"+System.getProperty("line.separator"));
				}
			}catch(Exception e)
			{
				if(input!=null)
					try {
						input.close();
					} catch (IOException e1) {
						getString.append(file+"�ر��ļ����쳣,��ǩ�ɹ�"+e.toString()+System.getProperty("line.separator"));
					}
				getString.append(file+"�����ļ��쳣,��ǩʧ��"+e.toString()+System.getProperty("line.separator"));
			}
			
		}else
		{
			getString.append("�ļ����ǩ��Կ���ļ�Ŀ¼Ϊ��!");
		}
		return getString.toString();
	}
	/**
	 * @function��ȡ�ļ���������
	 * @param file��Ҫ��ȡ���ļ�
	 * @return��������String���ͷ���
	 */
	private String readFileClient(File file,String signKey){
		BufferedReader input = null;
		String message = "��ǩ�ɹ�.";
		try {
			input = new BufferedReader(new FileReader(file)); 
			String lineContent = null;
			while((lineContent = input.readLine())!=null){
				if(!verifySM3Sign(lineContent,signKey))
				{
					message = "��ǩʧ��,��ǩ��������"+lineContent;
					break;
				}
			} 
			
		} catch (FileNotFoundException e) {
			System.out.println("��ȡ�ļ�ʧ��,δ�ҵ��ļ�." + file+e.toString());
			return null;
		} catch (IOException e) {
			System.out.println("��ȡ�ļ�ʧ��,IO����." + file+e.toString());
			return null;
		} catch (RuntimeException e) {
			System.out.println("��ȡ�ļ�ʧ�ܣ�����ʱ�쳣." + file+e.toString());
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {
					return null;
				}
			}
		}
		return message;
	}
	/**
	 * @function������д�뵽�ļ�
	 * @param fileName��Ҫд���ļ�·��
	 * @param fileContent��Ҫд���ļ�����
	 * @return
	 */
	private boolean writeFile(String fileName, String fileContent)
	{
		File file = new File(fileName);
		File dir = new File(file.getParent());
		FileOutputStream output = null;
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			output = new FileOutputStream(fileName, false);
			output.write(fileContent.getBytes("GBK"));
		
		} catch (IOException e) {
			System.out.println("д�ļ�ʧ��,IO����." + fileName+e.toString());
			return false;
		} catch (RuntimeException e) {
			System.out.println("д�ļ�ʧ�ܣ�����ʱ�쳣." + fileName+e.toString());
			return false;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					System.out.println("�ر��ļ�����"+ex.toString());
					return false;
				}
			}
		}
		return true;
	}
	
    /*�ж��Ƿ�ΪUTF-8��ʽ������UTF8���ļ�ʹ��GBK����
	 */
	private static String getFileEncoding(String fileName) throws IOException {
	 
	    SinoDetect detect = new SinoDetect();
		int i=	detect.detectEncoding(new File(fileName));
	    String code =  SinoDetect.nicename[i];
	    if (null==code || !code.equals("UTF-8")) {
	    	code="GBK";
		}
		return code;
	}
}

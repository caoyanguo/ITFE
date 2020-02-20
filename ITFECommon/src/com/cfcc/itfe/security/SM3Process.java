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
 * @author  中国金融电子化公司国库前置项目组
 * @version 创建时间：2012-11-01 上午11:03:41
 * @		修改时间：2012-11-21 
 * 
	Testing data from SM3 Standards(SM3 标准的测试数据)
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
	 * 验证SM3签名
	 * @param Data
	 * 			带有SM3签名的数据
	 * @param key
	 * 			签名密钥
	 * @return
	 */
	public static boolean verifySM3Sign(String Data, String key){
		boolean isCorrect = false;
		
		//数据不合法时直接退出
		if(Data.indexOf("<CA>") < 0 || Data.indexOf("</CA>") < 0)
		{
			return isCorrect;
		}
		try{
			//取签名前的数据
			String inputdata = Data.substring(0,Data.indexOf(",<CA>"));
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
			return isCorrect;
		}
		
		return isCorrect;
	}
	
	/**
	 * SM3验签(文件结尾使用xml注释传递签名字符串<!--签名字符串-->)
	 * @param fileContent  带有SM3签名的数据
	 * @param key  签名密钥
	 * @throws ITFEBizException
	 */
	public static void checkSM3Sign(String fileContent, String key) throws ITFEBizException {
		if ((key == null) || (key.length() == 0)){
			throw new ITFEBizException("密钥为空");
		}
		if ((fileContent == null) || (fileContent.length() == 0)){
			throw new ITFEBizException("需验证签名的数据为空");
		}
		int iBegin, iEnd;
		iBegin = fileContent.lastIndexOf("<!--");
		iEnd = fileContent.lastIndexOf("-->");
		if (iBegin < 0 || iEnd < 0) {
			StringBuffer msg = new StringBuffer();
			msg.append("查找报文签名错误, Begin:").append(iBegin);
			msg.append(",End:").append(iEnd);
			log.error(msg.toString());
			throw new ITFEBizException(msg.toString());
		}
		//取业务数据的电子签名
		String sign = fileContent.substring(iBegin + 4, iEnd);
		iEnd = fileContent.lastIndexOf("</CFX>");
		if (iEnd < 0) {
			StringBuffer msg = new StringBuffer();
			msg.append("查找报文体错误,End:").append(iEnd);
			log.error(msg.toString());
			throw new ITFEBizException(msg.toString());
		}
		String data = fileContent.substring(0, iBegin);
		
		String verifydata = data + key;
		byte[] byteData = verifydata.getBytes();
		
		//计算电子签名
		SM3Digest digest = new SM3Digest();
		digest.update(byteData, 0, byteData.length);
		
		byte[] out = new byte[32];
		digest.doFinal(out, 0);
		String signHex = toHexString(out);
		
		//把计算的签名和业务数据电子签名进行比较
		if(signHex != null && sign != null && signHex.equals(sign)){
			log.info("校验原签名：" + sign +" 成功！");
		}else{
			log.error("验签失败，原文或被篡改！");
			throw new ITFEBizException("验签失败，原文或被篡改！");
		}
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
			return null;
		}
	}
	/**
	 * 加签方法
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
        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
        FileWriter writer = new FileWriter(fileName, true);
        writer.write("\r\n" + encData);
        writer.close();
        flag = 0;
        return flag;
	}
	
	/**
	 * 验签方法
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
//		String xmlStr = "<?xml version=\"1.0\" encoding=\"GBK\"?><CFX><HEAD><VER>1.0</VER><SRC>111111111111</SRC><DES>110000000000</DES><APP>TCQS</APP><MsgNo>1000</MsgNo><MsgID>92017041300000033632</MsgID><MsgRef>92017041300000033632</MsgRef><WorkDate>20170413</WorkDate></HEAD><MSG><BatchHead1000><BillOrg>110606</BillOrg><EntrustDate>20170413</EntrustDate><PackNo>00034032</PackNo><TreCode>0106000000</TreCode><ChangeNo></ChangeNo><AllNum>1</AllNum><AllAmt>823798.09</AllAmt><PayoutVouType>1</PayoutVouType></BatchHead1000><BillSend1000><TraNo>00034032</TraNo><VouNo>170000355</VouNo><VouDate>20170412</VouDate><PayerAcct>0200004509026200178</PayerAcct><PayerName>北京市海淀区财政局</PayerName><PayerAddr></PayerAddr><Amt>823798.09</Amt><PayeeBankNo>103100021092</PayeeBankNo><PayeeOpBkNo>103100021092</PayeeOpBkNo><PayeeAcct>11210901040006616</PayeeAcct><PayeeName>北京市羊坊店医院</PayeeName><PayReason></PayReason><BudgetSubjectCode></BudgetSubjectCode><AddWord></AddWord><OfYear>2017</OfYear><Flag>2</Flag></BillSend1000></MSG></CFX>";
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
//			System.out.println("加签必须传入两个参数，第一个参数为加签密钥，第二个参数为需要加签文件主目录!");
//			System.out.println("验签必须传入三个参数，第一个参数为加签密钥，第二个参数为需要验签文件主目录，第三个参数为验签标识必须为verifysign");
//		}
	}
	
	/**
	 * 从文本文件中读出字符串
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件中的内容
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
			String msg = new String("读取文件失败,未找到文件." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (IOException e) {
			String msg = new String("读取文件失败,IO错误." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("读取文件失败，运行时异常." + fileName);
			log.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {
					log.error("关闭文件出错！" + fileName, ex);

				}
			}
		}

		return message;

	}
	
	/**
	 * @function 验签主方法，传入参数数组，第一个参数为加签密钥，第二个参数为需要验签文件主目录，第三个参数为验签标识必须为verifysign
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
				result.append(args[1]+"不存在!");
			}
			System.out.println("验签结果:"+System.getProperty("line.separator")+result.toString());
		}else
			System.out.println("验签失败:传入参数必须为三个且都不为空且第三个参数必须为verifysign");
	}
	/**
	 * @function 加签主方法，参数数组，第一个参数为加签密钥，第二个参数为需要加签文件主目录
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
			getString = args[1]+"不存在!";
		}
		System.out.println("加签结果:"+System.getProperty("line.separator")+getString);
	}
	/**
	 * @function 传入需要加签的文件列表，传入密钥，传入文件所在主目录
	 * @param files文件列表
	 * @param signKey加签密钥
	 * @param filePath文件所在主目录
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
			getString.append("加签文件目录或加签密钥为空!");
		}
		return getString.toString();
	}
	/**
	 * @function 传入需要加签的文件，传入密钥，传入文件所在主目录
	 * @param files需要加签文件
	 * @param signKey加签密钥
	 * @param filePath文件所在主目录
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
						getString.append(file+"加签成功"+System.getProperty("line.separator"));
				}else if("".equals(fileContent))
				{
					getString.append(file+"文件内容为空,加签失败"+System.getProperty("line.separator"));
				}
			}catch(Exception e)
			{
				if(input!=null)
					try {
						input.close();
					} catch (IOException e1) {
						getString.append(file+"关闭文件流异常,加签成功"+e.toString()+System.getProperty("line.separator"));
					}
				getString.append(file+"操作文件异常,加签失败"+e.toString()+System.getProperty("line.separator"));
			}
			
		}else
		{
			getString.append("文件或加签密钥或文件目录为空!");
		}
		return getString.toString();
	}
	/**
	 * @function读取文件里面内容
	 * @param file需要读取的文件
	 * @return返回内容String类型返回
	 */
	private String readFileClient(File file,String signKey){
		BufferedReader input = null;
		String message = "验签成功.";
		try {
			input = new BufferedReader(new FileReader(file)); 
			String lineContent = null;
			while((lineContent = input.readLine())!=null){
				if(!verifySM3Sign(lineContent,signKey))
				{
					message = "验签失败,验签出错数据"+lineContent;
					break;
				}
			} 
			
		} catch (FileNotFoundException e) {
			System.out.println("读取文件失败,未找到文件." + file+e.toString());
			return null;
		} catch (IOException e) {
			System.out.println("读取文件失败,IO错误." + file+e.toString());
			return null;
		} catch (RuntimeException e) {
			System.out.println("读取文件失败，运行时异常." + file+e.toString());
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
	 * @function把内容写入到文件
	 * @param fileName需要写入文件路径
	 * @param fileContent需要写入文件内容
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
			System.out.println("写文件失败,IO错误." + fileName+e.toString());
			return false;
		} catch (RuntimeException e) {
			System.out.println("写文件失败，运行时异常." + fileName+e.toString());
			return false;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					System.out.println("关闭文件出错！"+ex.toString());
					return false;
				}
			}
		}
		return true;
	}
	
    /*判断是否为UTF-8格式，不是UTF8的文件使用GBK解析
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

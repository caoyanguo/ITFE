/**
 * 
 */
package com.cfcc.itfe.util.encrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 调用格尔公司提供的动态链接库进行PKCS7签名、加密的类
 * @author sjz
 */
public class KoalPkcs7Encrypt {
	//配置文件的名称
	private final static String propertyFileName = "RHConfig.xml";//"/conf/config/property/itfeesb.properties";
	private static Logger _log = Logger.getLogger(KoalPkcs7Encrypt.class.getName());
	//最小缓冲区大小
	public final static int MIN_BUF_LEN = 4096;
	//证书缓冲区大小
	public final static int CER_BUF_LEN = 8192;
	//本类的实例
	private static KoalPkcs7Encrypt _instance = null;
	//个人证书存放的绝对路径
	private String strPfxFile;
	//个人证书的口令 
	private String strPfxPwd;
	//公共证书存放的绝对路径
	private String strCerFile;
	//公共证书的内容，支持der/base64格式。
	private String strCerContent;
	//dll方法调用的返回值
	private int retCode;
	//dll方法调用时的错误描述，如果成功则为空
	private String errorMsg;
	
	//初始化动态库方法
	private native void dllInit();
	//签名方法
	private native int dllSign(byte strPfxFile[],byte strPfxPwd[],byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//验证签名方法
	private native int dllVerifySign(byte strSigned[],int iSignedlen,byte strSrc[],int iSrcLen,byte strCert[],int iMaxRetLen);
	//对原文数据进行加密并生成数字信封
	private native int dllEnvelop(byte strCerContent[],int iCerContentLen,byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//对文件进行加密并生成数字信封
	private native int dllEnvelopFile(byte strCerContent[],int iCerContentLen,byte strSrcFile[],byte strRetFile[]);
	//解密标准的PKCS7数字信封
	private native int dllUnenvelop(byte strPfxFile[],byte strPfxPwd[],byte strSecData[],int iSecLen, byte strRet[],int iMaxRetLen);
	//解密标准的PKCS7文件数字信封
	private native int dllUnenvelopFile(byte strPfxFile[],byte strPfxPwd[],byte strSrcFile[],byte strRetFile[]);
	//取证书主题名
	private native int dllParseCertSubject(byte strCertData[],int iCertDataLen,byte strRet[],int iMaxRetLen);
	//取证书有效期
	private native int dllParseCertValidity(byte strCertData[],int iCertDataLen,byte notBefore[],byte notAfter[]);
	//获得最近一次的错误代码
	private native int dllGetLastErrorCode();
	
	
	public KoalPkcs7Encrypt(){
		init();
	}
	
	public static KoalPkcs7Encrypt getInstance(){
		if (_instance == null){
			_instance = new KoalPkcs7Encrypt();
		}
		return _instance;
	}
	
	/**
	 * 使用个人证书对数据进行签名
	 * 因为签名原文中可能会有中文存在，所以在计算长度的时候需要转换成byte数字计算
	 * 签名原文的长度需要转换成byte数组后在计算长度，这样一个中文字符为占两个字节长度，而不象Java中通常的一个字节长度
	 * 签名结果为Base64编码
	 * @param strSrc  签名原文
	 * @return 成功-返回base64编码的签名结果，失败-返回空字符串
	 */
	public String pkcs7sign(String strSrc){
		int iSrcLen,iMaxLen;
		byte byteSigned[];
		String strRet;
		
		//TODO 检查系统设置，是否需要加密（加密和签名设置一个参数即可）
		
		//检查个人证书和证书口令是否设置
		if ((strPfxFile == null) || (strPfxFile.length() == 0) || (strPfxPwd == null) || (strPfxPwd.length() == 0)){
			retCode = -2001;
			return "";
		}
		//记录签名结果的Byte数组
		iSrcLen = strSrc.getBytes().length;
		iMaxLen = iSrcLen * 2 + MIN_BUF_LEN;
		byteSigned = new byte[iMaxLen];
		//签名
		retCode = dllSign(strPfxFile.getBytes(), strPfxPwd.getBytes(), strSrc.getBytes(), iSrcLen, byteSigned, iMaxLen);
		if (retCode >= 0){
			strRet = new String(byteSigned).trim();
		}else{
			strRet = "";
		}
		
		return strRet;
	}
	
	/**
	 * 验证base64编码的签名数据中的签名是否正确，同时验证与原文是否相同
	 * 并返回签名使用的证书内容，证书内容是base64编码的
	 * 因为签名的数据是Base64编码的，其中没有中文，所以不必转换成byte数组在计算长度
	 * 而签名原文中可能会有中文，所以在计算长度的时候，需要转换成Byte数组
	 * 将签名用的证书和原文放在集合中返回，集合的第一个元素为签名证书，集合的第二个元素为签名原文
	 * @param strSigned  base64编码的签名数据
	 * @return 成功-记录签名证书和签名原文的集合； 失败-空字符串
	 */
	public List<String> pkcs7VerifySign(String strSigned){
		//签名数据长度
		int iSignedLen = strSigned.getBytes().length;
		//记录签名证书的byte数字
		byte srcBuf[]  = new byte[iSignedLen];
		byte certBuf[] = new byte[CER_BUF_LEN];
		List<String> ret = new ArrayList<String>();
		
		//TODO 检查系统设置，是否需要加密（加密和签名设置一个参数即可）
		
		//验证签名
		retCode = dllVerifySign(strSigned.getBytes(), iSignedLen, srcBuf, iSignedLen, certBuf, CER_BUF_LEN);
		if (retCode >= 0){
			ret.add(new String(certBuf).trim());
			ret.add(new String(srcBuf).trim());
		}
		
		return ret;
	}
	
	/**
	 * 对原文数据进行加密并生成数字信封
	 * 使用公共证书，对原文数据进行加密并生成数字信封
	 * 数字信封中的内容是base64编码的
	 * 因为加密原文中可能会有中文存在，所以在计算长度的时候需要转换成byte数字计算
	 * 加密原文的长度是转换成byte的长度，这样一个中文字符为占两个字节长度，而不想Java中通常的一个字节长度
	 * @param strSrc  加密原文
	 * @return 成功-返回base64编码的加密字符串；失败-返回空字符串
	 */
	public String pkcs7Envelop(String strSrc){
		int iSrcLen,iMaxRetLen;
		byte byteRet[];
		String strRet;
		
		//TODO 检查系统设置，是否需要加密（加密和签名设置一个参数即可）
		
		//检查公共证书是否设置
		if ((strCerContent == null) || (strCerContent.length() == 0)){
			retCode = -2002;
			return "";
		}
		//记录加密结果的byte数组
		iSrcLen = strSrc.getBytes().length;
		iMaxRetLen = iSrcLen * 2 + MIN_BUF_LEN;
		byteRet = new byte[iMaxRetLen];
		//生成数字信封
		retCode = dllEnvelop(strCerContent.getBytes(), strCerContent.length(), strSrc.getBytes(), iSrcLen, byteRet, iMaxRetLen);
		if (retCode >= 0){
			strRet = new String(byteRet).trim();
		}else{
			strRet = "";
		}
		return strRet;	
	}
	
	/**
	 * 使用公共证书对文件中的内容进行加密，生成数字信封
	 * 并将生成数字信封的结果保存在输出文件中
	 * @param strSrcFile 输入文件，待加密信息
	 * @param strRetFile 输出文件，存放加密信息
	 * @return  > 0	成功返回加密数据的长度；< 0 失败，返回错误码。
	 */
	public int pkcs7EnvelopFile(String strSrcFile, String strRetFile){
		//检查公共证书是否设置
		if ((strCerContent == null) || (strCerContent.length() == 0)){
			retCode = -2002;
			return retCode;
		}
		//检查原文件是否存在
		File file = new File(strSrcFile);
		if (!file.exists()){
			retCode = -2003;
			return retCode;
		}
		//TODO 检查系统设置，是否需要加密（加密和签名设置一个参数即可）
//		FileUtils.copyFile(srcFile, destFile);
		//对文件制作数字信封
		retCode = dllEnvelopFile(strCerContent.getBytes(), strCerContent.getBytes().length, strSrcFile.getBytes(), strRetFile.getBytes());
		return retCode;
	}
	
	/**
	 * 解密标准的PKCS7数字信封
	 * 使用个人证书，对使用公共证书进行加密的数据进行解密
	 * 数字信封中的内容是base64编码的
	 * 直接返回生成数字信封和签名前的原文信息
	 * 这样就可以使用原文和签名后的数组进行签名的验证了
	 * @param strSrc  密文
	 * @param iSrcLen 密文的长度
	 * @return 成功-返回原文字符串；失败-返回空字符串
	 */
	public String pkcs7UnEnvelop(String strSecData){
		int iSecLen = strSecData.getBytes().length;
		//记录原文的byte数组
		byte byteRet[] = new byte[iSecLen];
		String strRet;
		
		//TODO 检查系统设置，是否需要加密（加密和签名设置一个参数即可）
		
		//检查个人证书和证书口令是否设置
		if ((strPfxFile == null) || (strPfxFile.length() == 0) || (strPfxPwd == null) || (strPfxPwd.length() == 0)){
			retCode = -2001;
			return "";
		}
		retCode = dllUnenvelop(strPfxFile.getBytes(), strPfxPwd.getBytes(), strSecData.getBytes(), iSecLen, byteRet, iSecLen);
		if (retCode >= 0){
			strRet = new String(byteRet).trim();
		}else{
			strRet = "";
		}
		return strRet;
	}
	
	/**
	 * 使用个人证书对文件中的密文进行解密，生成原文信息
	 * 并将生成原文保存在输出文件中
	 * @param strSrcFile 输入文件，已加密信息
	 * @param strRetFile 输出文件，存放明文信息
	 * @return  > 0	成功返回解密后数据的长度；< 0 失败，返回错误码。
	 */
	public int pkcs7UnEnvelopFile(String strSrcFile, String strRetFile){
		//检查个人证书和证书口令是否设置
		if ((strPfxFile == null) || (strPfxFile.length() == 0) || (strPfxPwd == null) || (strPfxPwd.length() == 0)){
			retCode = -2001;
			return retCode;
		}
		//检查原文件是否存在
		File file = new File(strSrcFile);
		if (!file.exists()){
			retCode = -2003;
			return retCode;
		}
		//TODO 检查系统设置，是否需要加密（加密和签名设置一个参数即可）
//		FileUtils.copyFile(srcFile, destFile)
		//解数字信封
		retCode = dllUnenvelopFile(strPfxFile.getBytes(), strPfxPwd.getBytes(), strSrcFile.getBytes(), strRetFile.getBytes());
		return retCode;
	}
	
	/**
	 * 根据证书的内容，获得证书的主题
	 * 也就是证书中的用户信息
	 * @param strCertData  base64编码的证书内容
	 * @param iCertDataLen 证书的长度
	 * @return 成功-返回证书主题名（如“C=CN，O=CFCA，CN=TEST”）； 失败-返回空字符串
	 */
	public String parseCertSubject(String strCertData){
		//存放证书主题的byte数组
		byte certBuf[] = new byte[CER_BUF_LEN];
		String strRet;
		
		//解析证书主题
		retCode = dllParseCertSubject(strCertData.getBytes(), strCertData.getBytes().length, certBuf, CER_BUF_LEN);
		if (retCode >= 0){
			strRet = new String(certBuf).trim();
		}else{
			strRet = "";
		}
		return strRet;
	}
	
	/**
	 * 根据证书内容，获得证书的有效期
	 * @param strCertData  base64编码的证书内容
	 * @param iCertDataLen 证书的长度
	 * return 返回证书剩余天数
	 */
	public long parseCertValidity(String strCertData){
		//记录证书开始日期和截止日期的byte数组
		byte lnotBefore[],lnotAfter[];
		String sDateNotAfter;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ss HH:mm:ss");
		long remainDays;
		
		lnotBefore = new byte[MIN_BUF_LEN];
		lnotAfter  = new byte[MIN_BUF_LEN];
		//解析证书有效期
		retCode = dllParseCertValidity(strCertData.getBytes(), strCertData.getBytes().length, lnotBefore, lnotAfter);
		sDateNotAfter = new String(lnotAfter).trim();
		try{
			long test = df.parse(sDateNotAfter).getTime() - new Date().getTime();
			remainDays = (test/1000)/(60*60*24);
		}catch(Exception e){
			remainDays = 0;
		}
		return remainDays;
	}
	
	/**
	 * 返回最近一次执行的返回值
	 */
	public int getLastRetCode(){
		return retCode;
	}
	
	/**
	 * 返回最近一次执行的错误描述
	 */
	public String getLastError(){
		return "(" + retCode + ")" + getErrorMsgByCode(retCode);
	}
	
	/**
	 * 返回个人证书存放路径 
	 */
	public String getStrPfxFile() {
		return strPfxFile;
	}
	
	/**
	 * 设置个人证书存放路径
	 * 同时检查个人证书是否存在
	 */
	public void setStrPfxFile(String strPfxFile) {
		this.strPfxFile = strPfxFile;
		File file = new File(this.strPfxFile);
		if (!file.exists()){
			//如果个人证书文件不存在，那么返回错误
			retCode = -2001;
		}
	}
	
	/**
	 * 返回个人证书的口令
	 */
	public String getStrPfxPwd() {
		return strPfxPwd;
	}
	
	/**
	 * 设置个人证书的口令
	 */
	public void setStrPfxPwd(String strPfxPwd) {
		this.strPfxPwd = strPfxPwd;
	}
	
	/**
	 * 返回公共证书存放路径
	 */
	public String getStrCerFile() {
		return strCerFile;
	}
	
	/**
	 * 返回公共证书的内容
	 */
	public String getStrCerContent() {
		return strCerContent;
	}
	
	/**
	 * 设置公共证书的内容
	 */
	public void setStrCerContent(String strCerContent) {
		this.strCerContent = strCerContent;
	}
	/**
	 * 设置公共证书存放路径
	 * 同时检查公共证书是否存在，并将公共证书的内容读取的字符串中
	 */
	public void setStrCerFile(String strCerFile) {
		this.strCerFile = strCerFile;
		File file = new File(this.strCerFile);
		if (!file.exists()){
			//如果公共证书文件不存在，那么返回错误
			retCode = -2002;
		}else{
			//将公共证书的内容读取到字符串中
			try {
				strCerContent = readFile(file);
			} catch (IOException e) {
				strCerContent = "";
			}
		}
	}
	
	/**
	 * 初始化方法
	 */
	private void init(){
		try{
			String classname = "RHConfig.xml";
			String path = this.getClass().getClassLoader().getResource(classname).getPath();
			path = path.substring(0, path.lastIndexOf(classname) - 1);
			String xmlString;
			// 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
			if (path.endsWith("!")){
				path = path.substring(0, path.lastIndexOf("/"));
				xmlString = readFile(this.getClass().getClassLoader().getResource(classname).openStream());
			}else{
				// 如果路径前面以file开头，那么去掉路径前的file，当该类打包到jar包时，返回的路径前会有file
				if (path.startsWith("file"))
					path = path.substring(path.indexOf("/") + 1);
				// ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径中存在中文和空格时，他会对这些字符进行转换，
				// 这样，得到的往往不是我们想要的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的 中文及空格路径
				try {
					path = URLDecoder.decode(path, "utf-8");
				} catch (UnsupportedEncodingException e) {
					_log.error(e);
				}
				if (path.charAt(0) == '/') {
					// 去掉开始的斜线
					path = path.substring(1);
				}
				xmlString = readFile(path + "/" + propertyFileName);
			}
			setStrPfxFile(getValueByTag(xmlString, "pfxpath"));
			setStrPfxPwd(getValueByTag(xmlString, "pfxpass"));
			setStrCerFile(getValueByTag(xmlString, "remotecertpath"));
		}catch(Exception e){
			setStrPfxFile("c:/cert11/renh.pfx");
			setStrPfxPwd("12345678");
			setStrCerFile("c:/cert11/renh.cer");
		}
		dllInit();
		retCode = dllGetLastErrorCode();
		
	}
	
	/**
	 * 从xml格式的字符串中获得指定tag标签所对应的值
	 * @param xmlString xml格式的字符串
	 * @param tag tag标签
	 * @return tag标签对应的值
	 */
	private String getValueByTag(String xmlString, String tag){
		String value = "";
		//都转换成小写在查找
		String xmlLowerString = xmlString.toLowerCase();
		String tagLower = tag.toLowerCase();
		//在字符串中查找"<"+tag标签，这是xml文件中tag标签的开始
		int beginPos = xmlLowerString.indexOf("<" + tagLower + ">");
		if (beginPos == -1){
			//报文中没有该标签，那么返回一个空字符串
			value = "";
			//如果标签的形式是<tag />样子的，那么需要在此处进行处理
			//只查找"<" + tag标签，看是否可以找到
			beginPos = xmlLowerString.indexOf("<" + tagLower);
			if (beginPos > 0){
				//找到形如"<" + tag标签的字符串，那么需要判断找到的tag是否就是要查找的标签，因为有些标签可能会有包含关系
				//例如：123标签就包含在123ab标签中
				beginPos += tagLower.length() + 1;
				if (xmlLowerString.charAt(beginPos)==30 || xmlLowerString.charAt(beginPos)=='/'){
					//如果标签后面是空格或者‘/’，那么标签正确找到，进行下一步处理
				}
			}
		}else{
			//如果找到开始标签，那么就继续查找结束标签
			beginPos += tagLower.length() + 2;//将字符串位置移动到tag标签的值上
			int endPos = xmlLowerString.indexOf("</" + tagLower + ">", beginPos);
			if (endPos == -1){
				//如果找不到结束标签，表明是一个<tag />类型的标签，那么不判断是否含有value属性，直接置空字符串
				value = "";
			}else{
				//找到结束标签，那么截取标签中的值
				String tmp = xmlString.substring(beginPos, endPos);
				//去掉字符串头部和尾部的空格、回车等不可见字符
				value = tmp.trim();
			}
		}
		return value;
	}
	
	/**
	 * 将InputStream中的内容读取到字符串中
	 * @param in 读入流
	 * @return 记录流中内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(InputStream in) throws IOException {
		StringBuffer content = new StringBuffer();
		if (in == null) {
			// 如果流为空，那么返回空字符串
			return content.toString();
		}
		char[] buf = new char[4096];
		int ret = 0;
		BufferedReader bin = new BufferedReader(new InputStreamReader(in,"gb2312"));
		ret = bin.read(buf);
		while (ret > 0) {
			content.append(new String(buf, 0, ret));
			ret = bin.read(buf);
		}
		bin.close();
		return content.toString();
	}

	/**
	 * 将文件中的内容读取到字符串中
	 * @param file 文件对象
	 * @return 保存文件内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(File file) throws FileNotFoundException,IOException{
		return readFile(new FileInputStream(file));
	}
	
	/**
	 * 将文件中的内容读取到字符串中
	 * @param fileName 文件的绝对路径
	 * @return 保存文件内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws FileNotFoundException,IOException{
		return readFile(new FileInputStream(fileName));
	}

	
	/**
	 * 根据传入的返回值，获得错误描述信息
	 * 如果返回值为成功，那么错误描述为空字符串
	 * @param code 返回值
	 * @return 错误描述，当返回值为成功是，错误描述为空字符串
	 */
	private String getErrorMsgByCode(int code){
		switch (code){
		case -1000:
			errorMsg = "输入参数为空（某个输入参数未赋值）";
			break;
		case -1001:
			errorMsg = "is_err申请内存空间失败";
			break;
		case -1002:
			errorMsg = "Base64编码失败";
			break;
		case -1003:
			errorMsg = "Base46解码失败";
			break;
		case -1004:
			errorMsg = "从buf获得bio指针失败";
			break;
		case -1005:
			errorMsg = "从buf获得bio指针失败";
			break;
		case -1006:
			errorMsg = "生成p7结果失败";
			break;
		case -1007:
			errorMsg = "生成p7bio指针失败";
			break;
		case -1008:
			errorMsg = "获取签名者信息失败";
			break;
		case -1009:
			errorMsg = "取得签名者失败";
			break;
		case -1010:
			errorMsg = "生成memory bio失败";
			break;
		case -1011:
			errorMsg = "I2d失败";
			break;
		case -1012:
			errorMsg = "D2i失败";
			break;
		case -1013:
			errorMsg = "生成ca失败";
			break;
		case -1014:
			errorMsg = "加密失败";
			break;
		case -1015:
			errorMsg = "生成文件bio失败";
			break;
		case -1016:
			errorMsg = "生成pkcs12结构失败";
			break;
		case -1017:
			errorMsg = "Pkcs12解析失败";
			break;
		case -1020:
			errorMsg = "载入证书失败";
			break;
		case -1021:
			errorMsg = "获取BIGNUM失败";
			break;
		case -1022:
			errorMsg = "Bn转换十六进制失败";
			break;
		case -1023:
			errorMsg = "取证书主题名失败";
			break;
		case -1024:
			errorMsg = "文件打不开";
			break;
		case -1025:
			errorMsg = "文件内容为空";
			break;
		case -1026:
			errorMsg = "签名失败";
			break;
		case -1027:
			errorMsg = "取签名原文buf太小";
			break;
		case -1028:
			errorMsg = "取签名原文失败";
			break;
		case -2001:
			errorMsg = "个人证书不存在";
			break;
		case -2002:
			errorMsg = "公共证书不存在";
			break;
		case -2003:
			errorMsg = "源文件不存在";
			break;
		case -3001:
			errorMsg = "动态链接库初始化错误";
			break;
		case -3002:
			errorMsg = "获取动态链接库中的方法错误";
			break;
		case -5005:
			errorMsg = "无法为指针分配内存";
			break;
		default:
			if (code >= 0)
				errorMsg = "";
			else
				errorMsg = "其他错误";
			break;
		}
		return errorMsg;
	}

	static{
		System.loadLibrary("JavaKoalEncrypt");
	}
	
	public static void main(String args[]){
		String src;
		try {
			src = KoalPkcs7Encrypt.readFile("c:/cert11/l100_2009012111.txt");//l100_2009012111.jm");//yuan.txt
		} catch (IOException e) {
			src = "中国，你好，abcdefg1234567，结束了。\n60周年庆祝活动。";
		}//"中国，你好，abcdefg1234567，结束了。\n60周年庆祝活动。";//"abcdefg1234567";
		
		KoalPkcs7Encrypt.getInstance().setStrPfxFile("c:/cert/user1.pfx");
		KoalPkcs7Encrypt.getInstance().setStrPfxPwd("123456");
		KoalPkcs7Encrypt.getInstance().setStrCerFile("c:/cert/user1.cer");
		//签名
//		String signed = KoalPkcs7Encrypt.getInstance().pkcs7sign(src);
//		if (signed == ""){
//		}else{
//		}
		//加密
//		String enc = KoalPkcs7Encrypt.getInstance().pkcs7Envelop(signed);
//		int ret = KoalPkcs7Encrypt.getInstance().pkcs7EnvelopFile("c:/cert11/yuan.txt", "c:/cert11/enc.txt");
//		if (enc == ""){
//		}else{
//		}
		//解密
		String enc = "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";
		String unEnc = KoalPkcs7Encrypt.getInstance().pkcs7UnEnvelop(enc);
//		ret = KoalPkcs7Encrypt.getInstance().pkcs7UnEnvelopFile("c:/cert11/enc.txt", "c:/cert11/unenc.txt");
		if (unEnc == ""){
		}else{
		}
		//验证签名
		List<String> cert = KoalPkcs7Encrypt.getInstance().pkcs7VerifySign(unEnc);
		if (cert.size() > 0){
			String cn = KoalPkcs7Encrypt.getInstance().parseCertSubject(cert.get(0));
			long days = KoalPkcs7Encrypt.getInstance().parseCertValidity(cert.get(0));
		}else{
		}
	}
}


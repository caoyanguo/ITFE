/**
 * 
 */
package com.cfcc.itfe.client.common.encrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 由格尔公司提供，使用Usb-Key在客户端进行加密的类
 * @author sjz
 *
 */
public class KoalClientPkcs7Encrypt {
	private final static String DLL_NAME = "c:\\windows\\system32\\FileOptModule.dll";
	//最小缓冲区大小
	public final static int MIN_BUF_LEN = 4096;
	//证书缓冲区大小
	public final static int CER_BUF_LEN = 8192;
	//本类的实例
	private static KoalClientPkcs7Encrypt _instance = null;
	//个人证书的内容
	private String strPfxFile;
	//公共证书存放的绝对路径
	private String strCerFile;
	//公共证书的内容，支持der/base64格式。
	private String strCerContent;
	//dll方法调用的返回值
	private int retCode;
	//dll方法调用时的错误描述，如果成功则为空
	private String errorMsg;
	
	//初始化key驱动的pkcs11库
	private native int dllInit(byte dllName[]);
	//释放key驱动的pkcs11库
	private native void dllClose();
	//从Usb-Keyz中获得证书内容
	private native int dllGetCertFromKey(byte strCert[], int iMaxCertLen);
	//签名方法
	private native int dllSign(byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//验证签名方法
	private native int dllVerifySign(byte strSigned[],int iSignedlen,byte strSrc[],int iSrcMaxLen,byte strCert[],int iMaxRetLen);
	//对原文数据进行加密并生成数字信封
	private native int dllEnvelop(byte strCerContent[],int iCerContentLen,byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//对文件进行加密并生成数字信封
	private native int dllEnvelopFile(byte strCerContent[],byte strSrcFile[],byte strRetFile[]);
	//对原文数据进行加密并生成数字信封
//	private native int dllEnvelopEx(byte strCerContent[],byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//解密标准的PKCS7数字信封
	private native int dllUnenvelop(byte strSecData[],int iSecLen, byte strRet[],int iMaxRetLen);
	//解密标准的PKCS7文件数字信封
	private native int dllUnenvelopFile(byte strSrcFile[],byte strRetFile[]);
	//Base64编码
	private native int dllBase64Encode(byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//Base64解码
	private native int dllBase64Decode(byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//取证书主题名
	private native int dllParseCertSubject(byte strCertData[],int iCertDataLen,byte strRet[],int iMaxRetLen);
	//取证书有效期
	private native int dllParseCertValidity(byte strCertData[],int iCertDataLen,byte notBefore[],byte notAfter[]);

	public KoalClientPkcs7Encrypt(){
		File file = new File(DLL_NAME);
		if (!file.exists()){
			retCode = -5006;
		}
	}
	
	public static KoalClientPkcs7Encrypt getInstance(){
		if (_instance == null){
			_instance = new KoalClientPkcs7Encrypt();
		}
		return _instance;
	}
	
	/**
	 * 初始化key驱动的pkcs11库
	 * @return =0 成功；小于0，返回错误码。
	 */
	public int init(){
		retCode = dllInit(DLL_NAME.getBytes());
		return retCode;
	}
	
	/**
	 * 释放key驱动的pkcs11库
	 */
	public void close(){
		dllClose();
	}
	
	/**
	 * 获得Usb-key中的证书内容
	 * @return 成功-Usb-key中的证书内容；错误-空字符串
	 */
	public String getCertFromKey(){
		//初始化key驱动
		retCode = init();
		if (retCode < 0){
			return "";
		}
		
		//存放证书内容的缓冲区
		byte certbuf[] = new byte[CER_BUF_LEN];
		
		retCode = dllGetCertFromKey(certbuf, CER_BUF_LEN);
		if (retCode > 0){
			strPfxFile = new String(certbuf).trim();
		}else{
			strPfxFile = "";
		}
		//释放key驱动
		close();
		return strPfxFile;
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
		//初始化key驱动
		retCode = init();
		if (retCode < 0){
			return "";
		}
		
		int iSrcLen,iMaxLen;
		byte byteSigned[];
		String strRet;
		
		//TODO 检查系统设置，是否需要加密（加密和签名设置一个参数即可）
		
		//记录签名结果的Byte数组
		iSrcLen = strSrc.getBytes().length;
		iMaxLen = iSrcLen * 2 + MIN_BUF_LEN;
		byteSigned = new byte[iMaxLen];
		//签名
		retCode = dllSign(strSrc.getBytes(), iSrcLen, byteSigned, iMaxLen);
		if (retCode >= 0){
			strRet = new String(byteSigned).trim();
		}else{
			strRet = "";
		}
		//释放key驱动
		close();
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
		List<String> ret = new ArrayList<String>();
		//初始化key驱动
		retCode = init();
		if (retCode < 0){
			return ret;
		}
		
		//签名数据长度
		int iSignedLen = strSigned.getBytes().length;
		//记录签名证书的byte数字
		byte srcBuf[]  = new byte[iSignedLen];
		byte certBuf[] = new byte[CER_BUF_LEN];
		
		//TODO 检查系统设置，是否需要加密（加密和签名设置一个参数即可）
		
		//验证签名
		retCode = dllVerifySign(strSigned.getBytes(), iSignedLen, srcBuf, iSignedLen, certBuf, CER_BUF_LEN);
		if (retCode >= 0){
			ret.add(new String(certBuf).trim());
			ret.add(new String(srcBuf).trim());
		}
		//释放key驱动
		close();
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
		//初始化key驱动
		retCode = init();
		if (retCode < 0){
			return "";
		}
		
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
		retCode = dllEnvelop(strCerContent.getBytes(), strCerContent.getBytes().length, strSrc.getBytes(), iSrcLen, byteRet, iMaxRetLen);
		if (retCode >= 0){
			strRet = new String(byteRet).trim();
		}else{
			strRet = "";
		}
		//释放key驱动
		close();
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
		//初始化key驱动
		retCode = init();
		if (retCode < 0){
			return retCode;
		}
		
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
		retCode = dllEnvelopFile(strCerContent.getBytes(), strSrcFile.getBytes(), strRetFile.getBytes());
		//释放key驱动
		close();
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
		//初始化key驱动
		retCode = init();
		if (retCode < 0){
			return "";
		}
		
		int iSecLen = strSecData.getBytes().length;
		//记录原文的byte数组
		byte byteRet[] = new byte[iSecLen];
		String strRet;
		
		//TODO 检查系统设置，是否需要加密（加密和签名设置一个参数即可）
		
		retCode = dllUnenvelop(strSecData.getBytes(), iSecLen, byteRet, iSecLen);
		if (retCode >= 0){
			strRet = new String(byteRet).trim();
		}else{
			strRet = "";
		}
		//释放key驱动
		close();
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
		//初始化key驱动
		retCode = init();
		if (retCode < 0){
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
		retCode = dllUnenvelopFile(strSrcFile.getBytes(), strRetFile.getBytes());
		//释放key驱动
		close();
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
		//初始化key驱动
		retCode = init();
		if (retCode < 0){
			return retCode;
		}
		
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
		//释放key驱动
		close();
		return remainDays;
	}
	
	/**
	 * 对字符串进行Base64编码
	 * @param src 源字符串
	 * @return base64编码后的字符串
	 */
	public String base64Encode(String src){
		//原文长度
		int iSrcLen = src.getBytes().length;
		if (iSrcLen == 0){
			return "";
		}
		int iRetLen = iSrcLen * 2;
		String strRet;
		byte retBuf[] = new byte[iRetLen];
		retCode = dllBase64Encode(src.getBytes(), iSrcLen, retBuf, iRetLen);
		if (retCode > 0){
			strRet = new String(retBuf).trim();
		}else{
			strRet = "";
		}
		return strRet;
	}
	
	/**
	 * 对字符串进行Base64解码
	 * @param src base64编码的源字符串
	 * @return 解码后的原字符串
	 */
	public String base64Decode(String src){
		//原文长度
		int iSrcLen = src.getBytes().length;
		if (iSrcLen == 0){
			return "";
		}
		String strRet;
		byte retBuf[] = new byte[iSrcLen];
		retCode = dllBase64Decode(src.getBytes(), iSrcLen, retBuf, iSrcLen);
		if (retCode > 0){
			strRet = new String(retBuf).trim();
		}else{
			strRet = "";
		}
		return strRet;
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
	 * 返回个人证书内容
	 */
	public String getStrPfxFile() {
		return strPfxFile;
	}
	
	/**
	 * 设置个人证书内容
	 */
	public void setStrPfxFile(String strPfxFile) {
		this.strPfxFile = strPfxFile;
	}
	
	/**
	 * 返回公共证书存放路径
	 */
	public String getStrCerFile() {
		return strCerFile;
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
		case -1:
		case -2:
		case -3:
		case -4:
		case -5:
		case -6:
		case -7:
		case -8:
		case -9:
		case -10:
		case -11:
		case -12:
		case -13:
			errorMsg = "初始化key驱动的pkcs11库失败，请检查pkcs11位置及名称";
			break;
		case -1000:
			errorMsg = "文件打不开";
			break;
		case -1001:
			errorMsg = "输入参数错误（某个参数为空）";
			break;
		case -1002:
			errorMsg = "打开证书存储失败，请检查是否具有管理员权限";
			break;
		case -1004:
			errorMsg = "当前usb key中没有证书";
			break;
		case -1005:
			errorMsg = "用户没有选择证书";
			break;
		case -1006:
			errorMsg = "证书不再在有效期内（未生效或已过期，请检查证书有效日期及系统时间）";
			break;
		case -1007:
			errorMsg = "签名失败";
			break;
		case -1008:
			errorMsg = "生成证书上下文失败，数据不是base64格式证书或者长度错误";
			break;
		case -1009:
			errorMsg = "取微软CSP指针错误";
			break;
		case -1010:
			errorMsg = "创建hash失败";
			break;
		case -1011:
			errorMsg = "对数据进行hash运算失败";
			break;
		case -1012:
			errorMsg = "取hash值失败";
			break;
		case -1013:
			errorMsg = "没有找到证书对应的私钥";
			break;
		case -1014:
			errorMsg = "取主题信息错误";
			break;
		case -1015:
			errorMsg = "oid不在（CN,OU,O）之内";
			break;
		case -1031:
			errorMsg = "对数据进行加密失败";
			break;
		case -1032:
			errorMsg = "对数据进行解密失败，请确认是否是base64编码的pkcs7加密数据";
			break;
		case -1041:
			errorMsg = "验证pkcs7 attached签名失败";
			break;
		case -1042:
			errorMsg = "pkcs7 attached签名验证通过，但是签名原文与输入原文不符";
			break;
		case -1043:
			errorMsg = "pkcs7 attached签名验证通过，但是取签名证书失败";
			break;
		case -2000:
			errorMsg = "文件长度为0";
			break;
		case -2001:
			errorMsg = "内存错误，malloc失败";
			break;
		case -2002:
			errorMsg = "公共证书不存在";
			break;
		case -2003:
			errorMsg = "base64解码错误，可能分配空间不足或者不是base64编码数据";
			break;
		case -4001:
			errorMsg = "初始化访问失败";
			break;
		case -4002:
			errorMsg = "取USB槽信息失败";
			break;
		case -4003:
			errorMsg = "当前没有USB设备插入";
			break;
		case -4004:
			errorMsg = "取USB槽信息失败";
			break;
		case -4005:
			errorMsg = "与 USB KEY会话失败";
			break;
		case -4007:
			errorMsg = "查找对象失败";
			break;
		case -4010:
			errorMsg = "取对象属性失败";
			break;
		case -4012:
			errorMsg = "当前系统中检测到多个key";
			break;
		case -5001:
			errorMsg = "加载加密动态链接库错误";
			break;
		case -5002:
			errorMsg = "获取动态链接库中的方法错误";
			break;
		case -5003:
			errorMsg = "初始化加密动态链接库错误";
			break;
		case -5004:
			errorMsg = "无法打开指定文件";
			break;
		case -5005:
			errorMsg = "无法为指针分配内存";
			break;
		case -5006:
			errorMsg = "Usb-Key驱动程序未安装";
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
		System.loadLibrary("JavaKoalClientEncrypt");
	}
	
	public static void main(String args[]){
//		String src;
//		try {
////			String test = "CN=娴嬭瘯璇曠敤";
////			String aa = new String(test.getBytes(""),"UTF-8");
//			src = KoalClientPkcs7Encrypt.readFile("c:/cert11/yuan.txt");
//		} catch (IOException e) {
//			src = "中国，你好，abcdefg1234567，结束了。\n60周年庆祝活动。";
//		}//"中国，你好，abcdefg1234567，结束了。\n60周年庆祝活动。";//"abcdefg1234567";
//		
//		KoalClientPkcs7Encrypt.getInstance().setStrCerFile("c:/cert/user1.cer");
//		//签名
//		String signed = KoalClientPkcs7Encrypt.getInstance().pkcs7sign(src);
//		if (signed == ""){
//		}else{
//		}
//		//加密
//		String enc = KoalClientPkcs7Encrypt.getInstance().pkcs7Envelop(signed);
////		int ret = KoalPkcs7Encrypt.getInstance().pkcs7EnvelopFile("c:/cert11/yuan.txt", "c:/cert11/enc.txt");
//		if (enc == ""){
//		}else{
//		}
//		//解密
//		String unEnc = KoalClientPkcs7Encrypt.getInstance().pkcs7UnEnvelop(enc);
////		ret = KoalPkcs7Encrypt.getInstance().pkcs7UnEnvelopFile("c:/cert11/enc.txt", "c:/cert11/unenc.txt");
//		if (unEnc == ""){
//		}else{
//		}
//		//验证签名
//		List<String> cert = KoalClientPkcs7Encrypt.getInstance().pkcs7VerifySign(unEnc);
//		if (cert.size() > 0){
//			String cn = KoalClientPkcs7Encrypt.getInstance().parseCertSubject(cert.get(0));
//			long days = KoalClientPkcs7Encrypt.getInstance().parseCertValidity(cert.get(0));
//		}else{
//		}
	}
}

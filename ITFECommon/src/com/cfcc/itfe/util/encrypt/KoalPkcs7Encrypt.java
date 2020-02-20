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
 * ���ø����˾�ṩ�Ķ�̬���ӿ����PKCS7ǩ�������ܵ���
 * @author sjz
 */
public class KoalPkcs7Encrypt {
	//�����ļ�������
	private final static String propertyFileName = "RHConfig.xml";//"/conf/config/property/itfeesb.properties";
	private static Logger _log = Logger.getLogger(KoalPkcs7Encrypt.class.getName());
	//��С��������С
	public final static int MIN_BUF_LEN = 4096;
	//֤�黺������С
	public final static int CER_BUF_LEN = 8192;
	//�����ʵ��
	private static KoalPkcs7Encrypt _instance = null;
	//����֤���ŵľ���·��
	private String strPfxFile;
	//����֤��Ŀ��� 
	private String strPfxPwd;
	//����֤���ŵľ���·��
	private String strCerFile;
	//����֤������ݣ�֧��der/base64��ʽ��
	private String strCerContent;
	//dll�������õķ���ֵ
	private int retCode;
	//dll��������ʱ�Ĵ�������������ɹ���Ϊ��
	private String errorMsg;
	
	//��ʼ����̬�ⷽ��
	private native void dllInit();
	//ǩ������
	private native int dllSign(byte strPfxFile[],byte strPfxPwd[],byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//��֤ǩ������
	private native int dllVerifySign(byte strSigned[],int iSignedlen,byte strSrc[],int iSrcLen,byte strCert[],int iMaxRetLen);
	//��ԭ�����ݽ��м��ܲ����������ŷ�
	private native int dllEnvelop(byte strCerContent[],int iCerContentLen,byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//���ļ����м��ܲ����������ŷ�
	private native int dllEnvelopFile(byte strCerContent[],int iCerContentLen,byte strSrcFile[],byte strRetFile[]);
	//���ܱ�׼��PKCS7�����ŷ�
	private native int dllUnenvelop(byte strPfxFile[],byte strPfxPwd[],byte strSecData[],int iSecLen, byte strRet[],int iMaxRetLen);
	//���ܱ�׼��PKCS7�ļ������ŷ�
	private native int dllUnenvelopFile(byte strPfxFile[],byte strPfxPwd[],byte strSrcFile[],byte strRetFile[]);
	//ȡ֤��������
	private native int dllParseCertSubject(byte strCertData[],int iCertDataLen,byte strRet[],int iMaxRetLen);
	//ȡ֤����Ч��
	private native int dllParseCertValidity(byte strCertData[],int iCertDataLen,byte notBefore[],byte notAfter[]);
	//������һ�εĴ������
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
	 * ʹ�ø���֤������ݽ���ǩ��
	 * ��Ϊǩ��ԭ���п��ܻ������Ĵ��ڣ������ڼ��㳤�ȵ�ʱ����Ҫת����byte���ּ���
	 * ǩ��ԭ�ĵĳ�����Ҫת����byte������ڼ��㳤�ȣ�����һ�������ַ�Ϊռ�����ֽڳ��ȣ�������Java��ͨ����һ���ֽڳ���
	 * ǩ�����ΪBase64����
	 * @param strSrc  ǩ��ԭ��
	 * @return �ɹ�-����base64�����ǩ�������ʧ��-���ؿ��ַ���
	 */
	public String pkcs7sign(String strSrc){
		int iSrcLen,iMaxLen;
		byte byteSigned[];
		String strRet;
		
		//TODO ���ϵͳ���ã��Ƿ���Ҫ���ܣ����ܺ�ǩ������һ���������ɣ�
		
		//������֤���֤������Ƿ�����
		if ((strPfxFile == null) || (strPfxFile.length() == 0) || (strPfxPwd == null) || (strPfxPwd.length() == 0)){
			retCode = -2001;
			return "";
		}
		//��¼ǩ�������Byte����
		iSrcLen = strSrc.getBytes().length;
		iMaxLen = iSrcLen * 2 + MIN_BUF_LEN;
		byteSigned = new byte[iMaxLen];
		//ǩ��
		retCode = dllSign(strPfxFile.getBytes(), strPfxPwd.getBytes(), strSrc.getBytes(), iSrcLen, byteSigned, iMaxLen);
		if (retCode >= 0){
			strRet = new String(byteSigned).trim();
		}else{
			strRet = "";
		}
		
		return strRet;
	}
	
	/**
	 * ��֤base64�����ǩ�������е�ǩ���Ƿ���ȷ��ͬʱ��֤��ԭ���Ƿ���ͬ
	 * ������ǩ��ʹ�õ�֤�����ݣ�֤��������base64�����
	 * ��Ϊǩ����������Base64����ģ�����û�����ģ����Բ���ת����byte�����ڼ��㳤��
	 * ��ǩ��ԭ���п��ܻ������ģ������ڼ��㳤�ȵ�ʱ����Ҫת����Byte����
	 * ��ǩ���õ�֤���ԭ�ķ��ڼ����з��أ����ϵĵ�һ��Ԫ��Ϊǩ��֤�飬���ϵĵڶ���Ԫ��Ϊǩ��ԭ��
	 * @param strSigned  base64�����ǩ������
	 * @return �ɹ�-��¼ǩ��֤���ǩ��ԭ�ĵļ��ϣ� ʧ��-���ַ���
	 */
	public List<String> pkcs7VerifySign(String strSigned){
		//ǩ�����ݳ���
		int iSignedLen = strSigned.getBytes().length;
		//��¼ǩ��֤���byte����
		byte srcBuf[]  = new byte[iSignedLen];
		byte certBuf[] = new byte[CER_BUF_LEN];
		List<String> ret = new ArrayList<String>();
		
		//TODO ���ϵͳ���ã��Ƿ���Ҫ���ܣ����ܺ�ǩ������һ���������ɣ�
		
		//��֤ǩ��
		retCode = dllVerifySign(strSigned.getBytes(), iSignedLen, srcBuf, iSignedLen, certBuf, CER_BUF_LEN);
		if (retCode >= 0){
			ret.add(new String(certBuf).trim());
			ret.add(new String(srcBuf).trim());
		}
		
		return ret;
	}
	
	/**
	 * ��ԭ�����ݽ��м��ܲ����������ŷ�
	 * ʹ�ù���֤�飬��ԭ�����ݽ��м��ܲ����������ŷ�
	 * �����ŷ��е�������base64�����
	 * ��Ϊ����ԭ���п��ܻ������Ĵ��ڣ������ڼ��㳤�ȵ�ʱ����Ҫת����byte���ּ���
	 * ����ԭ�ĵĳ�����ת����byte�ĳ��ȣ�����һ�������ַ�Ϊռ�����ֽڳ��ȣ�������Java��ͨ����һ���ֽڳ���
	 * @param strSrc  ����ԭ��
	 * @return �ɹ�-����base64����ļ����ַ�����ʧ��-���ؿ��ַ���
	 */
	public String pkcs7Envelop(String strSrc){
		int iSrcLen,iMaxRetLen;
		byte byteRet[];
		String strRet;
		
		//TODO ���ϵͳ���ã��Ƿ���Ҫ���ܣ����ܺ�ǩ������һ���������ɣ�
		
		//��鹫��֤���Ƿ�����
		if ((strCerContent == null) || (strCerContent.length() == 0)){
			retCode = -2002;
			return "";
		}
		//��¼���ܽ����byte����
		iSrcLen = strSrc.getBytes().length;
		iMaxRetLen = iSrcLen * 2 + MIN_BUF_LEN;
		byteRet = new byte[iMaxRetLen];
		//���������ŷ�
		retCode = dllEnvelop(strCerContent.getBytes(), strCerContent.length(), strSrc.getBytes(), iSrcLen, byteRet, iMaxRetLen);
		if (retCode >= 0){
			strRet = new String(byteRet).trim();
		}else{
			strRet = "";
		}
		return strRet;	
	}
	
	/**
	 * ʹ�ù���֤����ļ��е����ݽ��м��ܣ����������ŷ�
	 * �������������ŷ�Ľ������������ļ���
	 * @param strSrcFile �����ļ�����������Ϣ
	 * @param strRetFile ����ļ�����ż�����Ϣ
	 * @return  > 0	�ɹ����ؼ������ݵĳ��ȣ�< 0 ʧ�ܣ����ش����롣
	 */
	public int pkcs7EnvelopFile(String strSrcFile, String strRetFile){
		//��鹫��֤���Ƿ�����
		if ((strCerContent == null) || (strCerContent.length() == 0)){
			retCode = -2002;
			return retCode;
		}
		//���ԭ�ļ��Ƿ����
		File file = new File(strSrcFile);
		if (!file.exists()){
			retCode = -2003;
			return retCode;
		}
		//TODO ���ϵͳ���ã��Ƿ���Ҫ���ܣ����ܺ�ǩ������һ���������ɣ�
//		FileUtils.copyFile(srcFile, destFile);
		//���ļ����������ŷ�
		retCode = dllEnvelopFile(strCerContent.getBytes(), strCerContent.getBytes().length, strSrcFile.getBytes(), strRetFile.getBytes());
		return retCode;
	}
	
	/**
	 * ���ܱ�׼��PKCS7�����ŷ�
	 * ʹ�ø���֤�飬��ʹ�ù���֤����м��ܵ����ݽ��н���
	 * �����ŷ��е�������base64�����
	 * ֱ�ӷ������������ŷ��ǩ��ǰ��ԭ����Ϣ
	 * �����Ϳ���ʹ��ԭ�ĺ�ǩ������������ǩ������֤��
	 * @param strSrc  ����
	 * @param iSrcLen ���ĵĳ���
	 * @return �ɹ�-����ԭ���ַ�����ʧ��-���ؿ��ַ���
	 */
	public String pkcs7UnEnvelop(String strSecData){
		int iSecLen = strSecData.getBytes().length;
		//��¼ԭ�ĵ�byte����
		byte byteRet[] = new byte[iSecLen];
		String strRet;
		
		//TODO ���ϵͳ���ã��Ƿ���Ҫ���ܣ����ܺ�ǩ������һ���������ɣ�
		
		//������֤���֤������Ƿ�����
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
	 * ʹ�ø���֤����ļ��е����Ľ��н��ܣ�����ԭ����Ϣ
	 * ��������ԭ�ı���������ļ���
	 * @param strSrcFile �����ļ����Ѽ�����Ϣ
	 * @param strRetFile ����ļ������������Ϣ
	 * @return  > 0	�ɹ����ؽ��ܺ����ݵĳ��ȣ�< 0 ʧ�ܣ����ش����롣
	 */
	public int pkcs7UnEnvelopFile(String strSrcFile, String strRetFile){
		//������֤���֤������Ƿ�����
		if ((strPfxFile == null) || (strPfxFile.length() == 0) || (strPfxPwd == null) || (strPfxPwd.length() == 0)){
			retCode = -2001;
			return retCode;
		}
		//���ԭ�ļ��Ƿ����
		File file = new File(strSrcFile);
		if (!file.exists()){
			retCode = -2003;
			return retCode;
		}
		//TODO ���ϵͳ���ã��Ƿ���Ҫ���ܣ����ܺ�ǩ������һ���������ɣ�
//		FileUtils.copyFile(srcFile, destFile)
		//�������ŷ�
		retCode = dllUnenvelopFile(strPfxFile.getBytes(), strPfxPwd.getBytes(), strSrcFile.getBytes(), strRetFile.getBytes());
		return retCode;
	}
	
	/**
	 * ����֤������ݣ����֤�������
	 * Ҳ����֤���е��û���Ϣ
	 * @param strCertData  base64�����֤������
	 * @param iCertDataLen ֤��ĳ���
	 * @return �ɹ�-����֤�����������硰C=CN��O=CFCA��CN=TEST������ ʧ��-���ؿ��ַ���
	 */
	public String parseCertSubject(String strCertData){
		//���֤�������byte����
		byte certBuf[] = new byte[CER_BUF_LEN];
		String strRet;
		
		//����֤������
		retCode = dllParseCertSubject(strCertData.getBytes(), strCertData.getBytes().length, certBuf, CER_BUF_LEN);
		if (retCode >= 0){
			strRet = new String(certBuf).trim();
		}else{
			strRet = "";
		}
		return strRet;
	}
	
	/**
	 * ����֤�����ݣ����֤�����Ч��
	 * @param strCertData  base64�����֤������
	 * @param iCertDataLen ֤��ĳ���
	 * return ����֤��ʣ������
	 */
	public long parseCertValidity(String strCertData){
		//��¼֤�鿪ʼ���ںͽ�ֹ���ڵ�byte����
		byte lnotBefore[],lnotAfter[];
		String sDateNotAfter;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ss HH:mm:ss");
		long remainDays;
		
		lnotBefore = new byte[MIN_BUF_LEN];
		lnotAfter  = new byte[MIN_BUF_LEN];
		//����֤����Ч��
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
	 * �������һ��ִ�еķ���ֵ
	 */
	public int getLastRetCode(){
		return retCode;
	}
	
	/**
	 * �������һ��ִ�еĴ�������
	 */
	public String getLastError(){
		return "(" + retCode + ")" + getErrorMsgByCode(retCode);
	}
	
	/**
	 * ���ظ���֤����·�� 
	 */
	public String getStrPfxFile() {
		return strPfxFile;
	}
	
	/**
	 * ���ø���֤����·��
	 * ͬʱ������֤���Ƿ����
	 */
	public void setStrPfxFile(String strPfxFile) {
		this.strPfxFile = strPfxFile;
		File file = new File(this.strPfxFile);
		if (!file.exists()){
			//�������֤���ļ������ڣ���ô���ش���
			retCode = -2001;
		}
	}
	
	/**
	 * ���ظ���֤��Ŀ���
	 */
	public String getStrPfxPwd() {
		return strPfxPwd;
	}
	
	/**
	 * ���ø���֤��Ŀ���
	 */
	public void setStrPfxPwd(String strPfxPwd) {
		this.strPfxPwd = strPfxPwd;
	}
	
	/**
	 * ���ع���֤����·��
	 */
	public String getStrCerFile() {
		return strCerFile;
	}
	
	/**
	 * ���ع���֤�������
	 */
	public String getStrCerContent() {
		return strCerContent;
	}
	
	/**
	 * ���ù���֤�������
	 */
	public void setStrCerContent(String strCerContent) {
		this.strCerContent = strCerContent;
	}
	/**
	 * ���ù���֤����·��
	 * ͬʱ��鹫��֤���Ƿ���ڣ���������֤������ݶ�ȡ���ַ�����
	 */
	public void setStrCerFile(String strCerFile) {
		this.strCerFile = strCerFile;
		File file = new File(this.strCerFile);
		if (!file.exists()){
			//�������֤���ļ������ڣ���ô���ش���
			retCode = -2002;
		}else{
			//������֤������ݶ�ȡ���ַ�����
			try {
				strCerContent = readFile(file);
			} catch (IOException e) {
				strCerContent = "";
			}
		}
	}
	
	/**
	 * ��ʼ������
	 */
	private void init(){
		try{
			String classname = "RHConfig.xml";
			String path = this.getClass().getClassLoader().getResource(classname).getPath();
			path = path.substring(0, path.lastIndexOf(classname) - 1);
			String xmlString;
			// ������ļ��������JAR���ļ���ʱ��ȥ����Ӧ��JAR�ȴ���ļ���
			if (path.endsWith("!")){
				path = path.substring(0, path.lastIndexOf("/"));
				xmlString = readFile(this.getClass().getClassLoader().getResource(classname).openStream());
			}else{
				// ���·��ǰ����file��ͷ����ôȥ��·��ǰ��file������������jar��ʱ�����ص�·��ǰ����file
				if (path.startsWith("file"))
					path = path.substring(path.indexOf("/") + 1);
				// ClassLoader��getResource����ʹ����utf-8��·����Ϣ�����˱��룬��·���д������ĺͿո�ʱ���������Щ�ַ�����ת����
				// �������õ�����������������Ҫ����ʵ·�����ڴˣ�������URLDecoder��decode�������н��룬�Ա�õ�ԭʼ�� ���ļ��ո�·��
				try {
					path = URLDecoder.decode(path, "utf-8");
				} catch (UnsupportedEncodingException e) {
					_log.error(e);
				}
				if (path.charAt(0) == '/') {
					// ȥ����ʼ��б��
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
	 * ��xml��ʽ���ַ����л��ָ��tag��ǩ����Ӧ��ֵ
	 * @param xmlString xml��ʽ���ַ���
	 * @param tag tag��ǩ
	 * @return tag��ǩ��Ӧ��ֵ
	 */
	private String getValueByTag(String xmlString, String tag){
		String value = "";
		//��ת����Сд�ڲ���
		String xmlLowerString = xmlString.toLowerCase();
		String tagLower = tag.toLowerCase();
		//���ַ����в���"<"+tag��ǩ������xml�ļ���tag��ǩ�Ŀ�ʼ
		int beginPos = xmlLowerString.indexOf("<" + tagLower + ">");
		if (beginPos == -1){
			//������û�иñ�ǩ����ô����һ�����ַ���
			value = "";
			//�����ǩ����ʽ��<tag />���ӵģ���ô��Ҫ�ڴ˴����д���
			//ֻ����"<" + tag��ǩ�����Ƿ�����ҵ�
			beginPos = xmlLowerString.indexOf("<" + tagLower);
			if (beginPos > 0){
				//�ҵ�����"<" + tag��ǩ���ַ�������ô��Ҫ�ж��ҵ���tag�Ƿ����Ҫ���ҵı�ǩ����Ϊ��Щ��ǩ���ܻ��а�����ϵ
				//���磺123��ǩ�Ͱ�����123ab��ǩ��
				beginPos += tagLower.length() + 1;
				if (xmlLowerString.charAt(beginPos)==30 || xmlLowerString.charAt(beginPos)=='/'){
					//�����ǩ�����ǿո���ߡ�/������ô��ǩ��ȷ�ҵ���������һ������
				}
			}
		}else{
			//����ҵ���ʼ��ǩ����ô�ͼ������ҽ�����ǩ
			beginPos += tagLower.length() + 2;//���ַ���λ���ƶ���tag��ǩ��ֵ��
			int endPos = xmlLowerString.indexOf("</" + tagLower + ">", beginPos);
			if (endPos == -1){
				//����Ҳ���������ǩ��������һ��<tag />���͵ı�ǩ����ô���ж��Ƿ���value���ԣ�ֱ���ÿ��ַ���
				value = "";
			}else{
				//�ҵ�������ǩ����ô��ȡ��ǩ�е�ֵ
				String tmp = xmlString.substring(beginPos, endPos);
				//ȥ���ַ���ͷ����β���Ŀո񡢻س��Ȳ��ɼ��ַ�
				value = tmp.trim();
			}
		}
		return value;
	}
	
	/**
	 * ��InputStream�е����ݶ�ȡ���ַ�����
	 * @param in ������
	 * @return ��¼�������ݵ��ַ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(InputStream in) throws IOException {
		StringBuffer content = new StringBuffer();
		if (in == null) {
			// �����Ϊ�գ���ô���ؿ��ַ���
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
	 * ���ļ��е����ݶ�ȡ���ַ�����
	 * @param file �ļ�����
	 * @return �����ļ����ݵ��ַ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(File file) throws FileNotFoundException,IOException{
		return readFile(new FileInputStream(file));
	}
	
	/**
	 * ���ļ��е����ݶ�ȡ���ַ�����
	 * @param fileName �ļ��ľ���·��
	 * @return �����ļ����ݵ��ַ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws FileNotFoundException,IOException{
		return readFile(new FileInputStream(fileName));
	}

	
	/**
	 * ���ݴ���ķ���ֵ����ô���������Ϣ
	 * �������ֵΪ�ɹ�����ô��������Ϊ���ַ���
	 * @param code ����ֵ
	 * @return ����������������ֵΪ�ɹ��ǣ���������Ϊ���ַ���
	 */
	private String getErrorMsgByCode(int code){
		switch (code){
		case -1000:
			errorMsg = "�������Ϊ�գ�ĳ���������δ��ֵ��";
			break;
		case -1001:
			errorMsg = "is_err�����ڴ�ռ�ʧ��";
			break;
		case -1002:
			errorMsg = "Base64����ʧ��";
			break;
		case -1003:
			errorMsg = "Base46����ʧ��";
			break;
		case -1004:
			errorMsg = "��buf���bioָ��ʧ��";
			break;
		case -1005:
			errorMsg = "��buf���bioָ��ʧ��";
			break;
		case -1006:
			errorMsg = "����p7���ʧ��";
			break;
		case -1007:
			errorMsg = "����p7bioָ��ʧ��";
			break;
		case -1008:
			errorMsg = "��ȡǩ������Ϣʧ��";
			break;
		case -1009:
			errorMsg = "ȡ��ǩ����ʧ��";
			break;
		case -1010:
			errorMsg = "����memory bioʧ��";
			break;
		case -1011:
			errorMsg = "I2dʧ��";
			break;
		case -1012:
			errorMsg = "D2iʧ��";
			break;
		case -1013:
			errorMsg = "����caʧ��";
			break;
		case -1014:
			errorMsg = "����ʧ��";
			break;
		case -1015:
			errorMsg = "�����ļ�bioʧ��";
			break;
		case -1016:
			errorMsg = "����pkcs12�ṹʧ��";
			break;
		case -1017:
			errorMsg = "Pkcs12����ʧ��";
			break;
		case -1020:
			errorMsg = "����֤��ʧ��";
			break;
		case -1021:
			errorMsg = "��ȡBIGNUMʧ��";
			break;
		case -1022:
			errorMsg = "Bnת��ʮ������ʧ��";
			break;
		case -1023:
			errorMsg = "ȡ֤��������ʧ��";
			break;
		case -1024:
			errorMsg = "�ļ��򲻿�";
			break;
		case -1025:
			errorMsg = "�ļ�����Ϊ��";
			break;
		case -1026:
			errorMsg = "ǩ��ʧ��";
			break;
		case -1027:
			errorMsg = "ȡǩ��ԭ��buf̫С";
			break;
		case -1028:
			errorMsg = "ȡǩ��ԭ��ʧ��";
			break;
		case -2001:
			errorMsg = "����֤�鲻����";
			break;
		case -2002:
			errorMsg = "����֤�鲻����";
			break;
		case -2003:
			errorMsg = "Դ�ļ�������";
			break;
		case -3001:
			errorMsg = "��̬���ӿ��ʼ������";
			break;
		case -3002:
			errorMsg = "��ȡ��̬���ӿ��еķ�������";
			break;
		case -5005:
			errorMsg = "�޷�Ϊָ������ڴ�";
			break;
		default:
			if (code >= 0)
				errorMsg = "";
			else
				errorMsg = "��������";
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
			src = "�й�����ã�abcdefg1234567�������ˡ�\n60������ף���";
		}//"�й�����ã�abcdefg1234567�������ˡ�\n60������ף���";//"abcdefg1234567";
		
		KoalPkcs7Encrypt.getInstance().setStrPfxFile("c:/cert/user1.pfx");
		KoalPkcs7Encrypt.getInstance().setStrPfxPwd("123456");
		KoalPkcs7Encrypt.getInstance().setStrCerFile("c:/cert/user1.cer");
		//ǩ��
//		String signed = KoalPkcs7Encrypt.getInstance().pkcs7sign(src);
//		if (signed == ""){
//		}else{
//		}
		//����
//		String enc = KoalPkcs7Encrypt.getInstance().pkcs7Envelop(signed);
//		int ret = KoalPkcs7Encrypt.getInstance().pkcs7EnvelopFile("c:/cert11/yuan.txt", "c:/cert11/enc.txt");
//		if (enc == ""){
//		}else{
//		}
		//����
		String enc = "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";
		String unEnc = KoalPkcs7Encrypt.getInstance().pkcs7UnEnvelop(enc);
//		ret = KoalPkcs7Encrypt.getInstance().pkcs7UnEnvelopFile("c:/cert11/enc.txt", "c:/cert11/unenc.txt");
		if (unEnc == ""){
		}else{
		}
		//��֤ǩ��
		List<String> cert = KoalPkcs7Encrypt.getInstance().pkcs7VerifySign(unEnc);
		if (cert.size() > 0){
			String cn = KoalPkcs7Encrypt.getInstance().parseCertSubject(cert.get(0));
			long days = KoalPkcs7Encrypt.getInstance().parseCertValidity(cert.get(0));
		}else{
		}
	}
}


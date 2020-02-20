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
 * �ɸ����˾�ṩ��ʹ��Usb-Key�ڿͻ��˽��м��ܵ���
 * @author sjz
 *
 */
public class KoalClientPkcs7Encrypt {
	private final static String DLL_NAME = "c:\\windows\\system32\\FileOptModule.dll";
	//��С��������С
	public final static int MIN_BUF_LEN = 4096;
	//֤�黺������С
	public final static int CER_BUF_LEN = 8192;
	//�����ʵ��
	private static KoalClientPkcs7Encrypt _instance = null;
	//����֤�������
	private String strPfxFile;
	//����֤���ŵľ���·��
	private String strCerFile;
	//����֤������ݣ�֧��der/base64��ʽ��
	private String strCerContent;
	//dll�������õķ���ֵ
	private int retCode;
	//dll��������ʱ�Ĵ�������������ɹ���Ϊ��
	private String errorMsg;
	
	//��ʼ��key������pkcs11��
	private native int dllInit(byte dllName[]);
	//�ͷ�key������pkcs11��
	private native void dllClose();
	//��Usb-Keyz�л��֤������
	private native int dllGetCertFromKey(byte strCert[], int iMaxCertLen);
	//ǩ������
	private native int dllSign(byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//��֤ǩ������
	private native int dllVerifySign(byte strSigned[],int iSignedlen,byte strSrc[],int iSrcMaxLen,byte strCert[],int iMaxRetLen);
	//��ԭ�����ݽ��м��ܲ����������ŷ�
	private native int dllEnvelop(byte strCerContent[],int iCerContentLen,byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//���ļ����м��ܲ����������ŷ�
	private native int dllEnvelopFile(byte strCerContent[],byte strSrcFile[],byte strRetFile[]);
	//��ԭ�����ݽ��м��ܲ����������ŷ�
//	private native int dllEnvelopEx(byte strCerContent[],byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//���ܱ�׼��PKCS7�����ŷ�
	private native int dllUnenvelop(byte strSecData[],int iSecLen, byte strRet[],int iMaxRetLen);
	//���ܱ�׼��PKCS7�ļ������ŷ�
	private native int dllUnenvelopFile(byte strSrcFile[],byte strRetFile[]);
	//Base64����
	private native int dllBase64Encode(byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//Base64����
	private native int dllBase64Decode(byte strSrc[],int iSrcLen,byte strRet[],int iMaxRetLen);
	//ȡ֤��������
	private native int dllParseCertSubject(byte strCertData[],int iCertDataLen,byte strRet[],int iMaxRetLen);
	//ȡ֤����Ч��
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
	 * ��ʼ��key������pkcs11��
	 * @return =0 �ɹ���С��0�����ش����롣
	 */
	public int init(){
		retCode = dllInit(DLL_NAME.getBytes());
		return retCode;
	}
	
	/**
	 * �ͷ�key������pkcs11��
	 */
	public void close(){
		dllClose();
	}
	
	/**
	 * ���Usb-key�е�֤������
	 * @return �ɹ�-Usb-key�е�֤�����ݣ�����-���ַ���
	 */
	public String getCertFromKey(){
		//��ʼ��key����
		retCode = init();
		if (retCode < 0){
			return "";
		}
		
		//���֤�����ݵĻ�����
		byte certbuf[] = new byte[CER_BUF_LEN];
		
		retCode = dllGetCertFromKey(certbuf, CER_BUF_LEN);
		if (retCode > 0){
			strPfxFile = new String(certbuf).trim();
		}else{
			strPfxFile = "";
		}
		//�ͷ�key����
		close();
		return strPfxFile;
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
		//��ʼ��key����
		retCode = init();
		if (retCode < 0){
			return "";
		}
		
		int iSrcLen,iMaxLen;
		byte byteSigned[];
		String strRet;
		
		//TODO ���ϵͳ���ã��Ƿ���Ҫ���ܣ����ܺ�ǩ������һ���������ɣ�
		
		//��¼ǩ�������Byte����
		iSrcLen = strSrc.getBytes().length;
		iMaxLen = iSrcLen * 2 + MIN_BUF_LEN;
		byteSigned = new byte[iMaxLen];
		//ǩ��
		retCode = dllSign(strSrc.getBytes(), iSrcLen, byteSigned, iMaxLen);
		if (retCode >= 0){
			strRet = new String(byteSigned).trim();
		}else{
			strRet = "";
		}
		//�ͷ�key����
		close();
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
		List<String> ret = new ArrayList<String>();
		//��ʼ��key����
		retCode = init();
		if (retCode < 0){
			return ret;
		}
		
		//ǩ�����ݳ���
		int iSignedLen = strSigned.getBytes().length;
		//��¼ǩ��֤���byte����
		byte srcBuf[]  = new byte[iSignedLen];
		byte certBuf[] = new byte[CER_BUF_LEN];
		
		//TODO ���ϵͳ���ã��Ƿ���Ҫ���ܣ����ܺ�ǩ������һ���������ɣ�
		
		//��֤ǩ��
		retCode = dllVerifySign(strSigned.getBytes(), iSignedLen, srcBuf, iSignedLen, certBuf, CER_BUF_LEN);
		if (retCode >= 0){
			ret.add(new String(certBuf).trim());
			ret.add(new String(srcBuf).trim());
		}
		//�ͷ�key����
		close();
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
		//��ʼ��key����
		retCode = init();
		if (retCode < 0){
			return "";
		}
		
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
		retCode = dllEnvelop(strCerContent.getBytes(), strCerContent.getBytes().length, strSrc.getBytes(), iSrcLen, byteRet, iMaxRetLen);
		if (retCode >= 0){
			strRet = new String(byteRet).trim();
		}else{
			strRet = "";
		}
		//�ͷ�key����
		close();
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
		//��ʼ��key����
		retCode = init();
		if (retCode < 0){
			return retCode;
		}
		
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
		retCode = dllEnvelopFile(strCerContent.getBytes(), strSrcFile.getBytes(), strRetFile.getBytes());
		//�ͷ�key����
		close();
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
		//��ʼ��key����
		retCode = init();
		if (retCode < 0){
			return "";
		}
		
		int iSecLen = strSecData.getBytes().length;
		//��¼ԭ�ĵ�byte����
		byte byteRet[] = new byte[iSecLen];
		String strRet;
		
		//TODO ���ϵͳ���ã��Ƿ���Ҫ���ܣ����ܺ�ǩ������һ���������ɣ�
		
		retCode = dllUnenvelop(strSecData.getBytes(), iSecLen, byteRet, iSecLen);
		if (retCode >= 0){
			strRet = new String(byteRet).trim();
		}else{
			strRet = "";
		}
		//�ͷ�key����
		close();
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
		//��ʼ��key����
		retCode = init();
		if (retCode < 0){
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
		retCode = dllUnenvelopFile(strSrcFile.getBytes(), strRetFile.getBytes());
		//�ͷ�key����
		close();
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
		//��ʼ��key����
		retCode = init();
		if (retCode < 0){
			return retCode;
		}
		
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
		//�ͷ�key����
		close();
		return remainDays;
	}
	
	/**
	 * ���ַ�������Base64����
	 * @param src Դ�ַ���
	 * @return base64�������ַ���
	 */
	public String base64Encode(String src){
		//ԭ�ĳ���
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
	 * ���ַ�������Base64����
	 * @param src base64�����Դ�ַ���
	 * @return ������ԭ�ַ���
	 */
	public String base64Decode(String src){
		//ԭ�ĳ���
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
	 * ���ظ���֤������
	 */
	public String getStrPfxFile() {
		return strPfxFile;
	}
	
	/**
	 * ���ø���֤������
	 */
	public void setStrPfxFile(String strPfxFile) {
		this.strPfxFile = strPfxFile;
	}
	
	/**
	 * ���ع���֤����·��
	 */
	public String getStrCerFile() {
		return strCerFile;
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
			errorMsg = "��ʼ��key������pkcs11��ʧ�ܣ�����pkcs11λ�ü�����";
			break;
		case -1000:
			errorMsg = "�ļ��򲻿�";
			break;
		case -1001:
			errorMsg = "�����������ĳ������Ϊ�գ�";
			break;
		case -1002:
			errorMsg = "��֤��洢ʧ�ܣ������Ƿ���й���ԱȨ��";
			break;
		case -1004:
			errorMsg = "��ǰusb key��û��֤��";
			break;
		case -1005:
			errorMsg = "�û�û��ѡ��֤��";
			break;
		case -1006:
			errorMsg = "֤�鲻������Ч���ڣ�δ��Ч���ѹ��ڣ�����֤����Ч���ڼ�ϵͳʱ�䣩";
			break;
		case -1007:
			errorMsg = "ǩ��ʧ��";
			break;
		case -1008:
			errorMsg = "����֤��������ʧ�ܣ����ݲ���base64��ʽ֤����߳��ȴ���";
			break;
		case -1009:
			errorMsg = "ȡ΢��CSPָ�����";
			break;
		case -1010:
			errorMsg = "����hashʧ��";
			break;
		case -1011:
			errorMsg = "�����ݽ���hash����ʧ��";
			break;
		case -1012:
			errorMsg = "ȡhashֵʧ��";
			break;
		case -1013:
			errorMsg = "û���ҵ�֤���Ӧ��˽Կ";
			break;
		case -1014:
			errorMsg = "ȡ������Ϣ����";
			break;
		case -1015:
			errorMsg = "oid���ڣ�CN,OU,O��֮��";
			break;
		case -1031:
			errorMsg = "�����ݽ��м���ʧ��";
			break;
		case -1032:
			errorMsg = "�����ݽ��н���ʧ�ܣ���ȷ���Ƿ���base64�����pkcs7��������";
			break;
		case -1041:
			errorMsg = "��֤pkcs7 attachedǩ��ʧ��";
			break;
		case -1042:
			errorMsg = "pkcs7 attachedǩ����֤ͨ��������ǩ��ԭ��������ԭ�Ĳ���";
			break;
		case -1043:
			errorMsg = "pkcs7 attachedǩ����֤ͨ��������ȡǩ��֤��ʧ��";
			break;
		case -2000:
			errorMsg = "�ļ�����Ϊ0";
			break;
		case -2001:
			errorMsg = "�ڴ����mallocʧ��";
			break;
		case -2002:
			errorMsg = "����֤�鲻����";
			break;
		case -2003:
			errorMsg = "base64������󣬿��ܷ���ռ䲻����߲���base64��������";
			break;
		case -4001:
			errorMsg = "��ʼ������ʧ��";
			break;
		case -4002:
			errorMsg = "ȡUSB����Ϣʧ��";
			break;
		case -4003:
			errorMsg = "��ǰû��USB�豸����";
			break;
		case -4004:
			errorMsg = "ȡUSB����Ϣʧ��";
			break;
		case -4005:
			errorMsg = "�� USB KEY�Ựʧ��";
			break;
		case -4007:
			errorMsg = "���Ҷ���ʧ��";
			break;
		case -4010:
			errorMsg = "ȡ��������ʧ��";
			break;
		case -4012:
			errorMsg = "��ǰϵͳ�м�⵽���key";
			break;
		case -5001:
			errorMsg = "���ؼ��ܶ�̬���ӿ����";
			break;
		case -5002:
			errorMsg = "��ȡ��̬���ӿ��еķ�������";
			break;
		case -5003:
			errorMsg = "��ʼ�����ܶ�̬���ӿ����";
			break;
		case -5004:
			errorMsg = "�޷���ָ���ļ�";
			break;
		case -5005:
			errorMsg = "�޷�Ϊָ������ڴ�";
			break;
		case -5006:
			errorMsg = "Usb-Key��������δ��װ";
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
		System.loadLibrary("JavaKoalClientEncrypt");
	}
	
	public static void main(String args[]){
//		String src;
//		try {
////			String test = "CN=测试试用";
////			String aa = new String(test.getBytes(""),"UTF-8");
//			src = KoalClientPkcs7Encrypt.readFile("c:/cert11/yuan.txt");
//		} catch (IOException e) {
//			src = "�й�����ã�abcdefg1234567�������ˡ�\n60������ף���";
//		}//"�й�����ã�abcdefg1234567�������ˡ�\n60������ף���";//"abcdefg1234567";
//		
//		KoalClientPkcs7Encrypt.getInstance().setStrCerFile("c:/cert/user1.cer");
//		//ǩ��
//		String signed = KoalClientPkcs7Encrypt.getInstance().pkcs7sign(src);
//		if (signed == ""){
//		}else{
//		}
//		//����
//		String enc = KoalClientPkcs7Encrypt.getInstance().pkcs7Envelop(signed);
////		int ret = KoalPkcs7Encrypt.getInstance().pkcs7EnvelopFile("c:/cert11/yuan.txt", "c:/cert11/enc.txt");
//		if (enc == ""){
//		}else{
//		}
//		//����
//		String unEnc = KoalClientPkcs7Encrypt.getInstance().pkcs7UnEnvelop(enc);
////		ret = KoalPkcs7Encrypt.getInstance().pkcs7UnEnvelopFile("c:/cert11/enc.txt", "c:/cert11/unenc.txt");
//		if (unEnc == ""){
//		}else{
//		}
//		//��֤ǩ��
//		List<String> cert = KoalClientPkcs7Encrypt.getInstance().pkcs7VerifySign(unEnc);
//		if (cert.size() > 0){
//			String cn = KoalClientPkcs7Encrypt.getInstance().parseCertSubject(cert.get(0));
//			long days = KoalClientPkcs7Encrypt.getInstance().parseCertValidity(cert.get(0));
//		}else{
//		}
	}
}

/**
 * 
 */
package com.cfcc.itfe.util.stamp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import com.koalii.bc.util.encoders.Base64;
import com.koalii.eseal.helper.ActiveXHelper;

/**
 * �˴����ø����˾�ṩ����
 * ����ʵ�ֵ���ӡ����У�顢�������ŷ⡢���������ŷ⡢���͵ȹ���
 * @author sjz
 * 2009-3-5 ����04:12:24
 */
public class StampSendHelper {
	private static StampSendHelper _instance = null;
	private static Logger _log = Logger.getLogger(StampSendHelper.class.getName());
	//����ӡ������
	private static ActiveXHelper bjStampHelper = null;
	
	public static StampSendHelper getInstance(){
		if (_instance == null){
			if (init() == 0){
				_instance = new StampSendHelper();
			}
		}
		return _instance;
	}
	
	/**
	 * ���������ŷ�
	 * @param src �����������ŷ���ַ���
	 * @return �ɹ��������������ŷ���Base64�������ݣ�ʧ�ܷ���NULL
	 */
//	public String createEnvelop(String src){
//		//����
//		return bjStampHelper.createEnvelope(src);
//	}
	
	/**
	 * ǩ��
	 * @param src ��ǩ���ַ���
	 * @return �ɹ�����ǩ�����Base64�������ݣ�ʧ�ܷ���NULL
	 */
//	public String createSign(String src){
//		//����ǩ��
//		return bjStampHelper.createSign(src);
//	}
	
	/**
	 * �������ŷ�
	 * @param src �����������ŷ�
	 * @return �ɹ�����ǩ��ԭ�ģ�ʧ�ܷ���NULL
	 */
//	public String parseEnvelop(String src){
//		//����
//		return bjStampHelper.parseEnvelop(src);
//	}
	
	/**
	 * ��֤ǩ��
	 * @param src ����֤ǩ���ַ���
	 * @return �ɹ�����ǩ��ԭ�ģ�ʧ�ܷ���NULL
	 */
//	public String verifySign(String src){
//		//��֤ǩ��
//		return bjStampHelper.verifySign(src);
//	}
	
	/**
	 * ��ָ������λ�õĸ��½�����з������˵���֤
	 * @param data     ����ǩ�����XMLҵ��ƾ֤(��Base64����)
	 * @param model    ��ģ��(��Base64����)
	 * @param sheetId  ƾ֤����ID
	 * @param placeId  Ҫ��֤����λ��ID,����ΪNULL
	 * @param esealId  Ҫ��֤ǩ��ID, ���ΪNULL�����ʾ���ȶ�ǩ��ID
	 * @param compTime �������ʱ�䣬��λ�룬���ΪNULL���ʾ��У��
	 * @return 0�C�ɹ����������󣨶�����ϸ�Ĵ����룩
	 */
	public int verifyFormStamp(String data, String model, String sheetId, String placeId, String esealId, int compTime){
		return bjStampHelper.verifyFormStamp(data, model, sheetId, placeId, compTime);
	}
	
	/**
	 * ��ȡ����ID
	 * @param data     ����ǩ�����XMLҵ��ƾ֤(��Base64����)
	 * @param model    ��ģ��(��Base64����)
	 * @param sheetId  ����ID
	 * @param placeId  Ҫ��֤����λ��ID,����ΪNULL
	 * @return �ɹ�����ǩ��ID(���û�ǩ�¶�Ӧ���е�eseal_id�ֶ�)��ʧ�ܷ���NULL
	 */
	public String getEsealId(String data, String model, String sheetId, String placeId){
		return bjStampHelper.getEsealID(data, model,sheetId, placeId);
	}
	
	/**
	 * ��ȡָ������λ�õĸ���ʱ��
	 * @param data     ����ǩ�����XMLҵ��ƾ֤(��Base64����)
	 * @param model    ��ģ��(��Base64����)
	 * @param sheetId  ����ID
	 * @param placeId  Ҫ��֤����λ��ID,����ΪNULL
	 * @return �ɹ����ظ���ʱ�䣬ʧ�ܷ���NULL
	 */
	public String getStampTime(String data, String model,String sheetId, String placeId){
		return bjStampHelper.getStampTime(data, model, sheetId, placeId);
	}
	
	/**
	 * ���ӡ���Ĵ�����Ϣ
	 * @return
	 */
	public String getLastError(){
		int retCode = bjStampHelper.getErrorCode();
		String errorMsg = "(" + retCode + ")" + bjStampHelper.getErrorMsg(retCode);
		return errorMsg;
	}
	
	/**
	 *  �����ַ�����base64����
	 * @param src ��������ַ���
	 * @return �ַ�����base64����
	 */
	public String base64Encode(String src){
		byte[] out = Base64.encode(src.getBytes());
		return new String(out);
	}
	
	/**
	 * ��base64������ַ���������Դ�ַ���
	 * @param src base64������ַ���
	 * @return Դ�ַ���
	 */
	public String base64Decode(String src){
		byte[] out = Base64.decode(src);
		String ret = null;
		try{
			ret = new String(out,"gb2312");
		}catch(Exception e){
			_log.error(e.getMessage(),e);
		}
		return ret;
	}
	
	/**
	 * ��ʼ������
	 */
	private static int init(){
		int ret = 0;
		String classname = "RHConfig.xml";
		String path = StampSendHelper.class.getClassLoader().getResource(classname).getPath();
		path = path.substring(0, path.lastIndexOf(classname) - 1);
		// ������ļ��������JAR���ļ���ʱ��ȥ����Ӧ��JAR�ȴ���ļ���
		if (path.endsWith("!")){
			path = path.substring(0, path.lastIndexOf("/"));
		}
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
		String configureFile = path + "/" + classname;
		if (_log.isInfoEnabled()){
			_log.info("ʹ�������ļ�" + configureFile + "��ʼ��ӡ������");
		}
		try{
			//��Ҫ��֤ӡ��
			bjStampHelper = new ActiveXHelper();
			bjStampHelper.initConfig(configureFile);
		}catch(Exception e){
			_log.error("���ӡ�������ʼ��ʧ��", e);
			ret = -1;
		}
		return ret;
	}
}

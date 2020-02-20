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
 * 此处调用格尔公司提供的类
 * 用来实现电子印鉴的校验、解数字信封、生成数字信封、发送等功能
 * @author sjz
 * 2009-3-5 下午04:12:24
 */
public class StampSendHelper {
	private static StampSendHelper _instance = null;
	private static Logger _log = Logger.getLogger(StampSendHelper.class.getName());
	//电子印鉴方法
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
	 * 生成数字信封
	 * @param src 待生成数字信封的字符串
	 * @return 成功返回生成数字信封后的Base64编码数据，失败返回NULL
	 */
//	public String createEnvelop(String src){
//		//加密
//		return bjStampHelper.createEnvelope(src);
//	}
	
	/**
	 * 签名
	 * @param src 待签名字符串
	 * @return 成功返回签名后的Base64编码数据，失败返回NULL
	 */
//	public String createSign(String src){
//		//数字签名
//		return bjStampHelper.createSign(src);
//	}
	
	/**
	 * 解数字信封
	 * @param src 待解密数字信封
	 * @return 成功返回签名原文，失败返回NULL
	 */
//	public String parseEnvelop(String src){
//		//解密
//		return bjStampHelper.parseEnvelop(src);
//	}
	
	/**
	 * 验证签名
	 * @param src 待验证签名字符串
	 * @return 成功返回签名原文，失败返回NULL
	 */
//	public String verifySign(String src){
//		//验证签名
//		return bjStampHelper.verifySign(src);
//	}
	
	/**
	 * 对指定盖章位置的盖章结果进行服务器端的验证
	 * @param data     盖章签名后的XML业务凭证(非Base64编码)
	 * @param model    表单模板(非Base64编码)
	 * @param sheetId  凭证表单联ID
	 * @param placeId  要验证盖章位置ID,不能为NULL
	 * @param esealId  要验证签章ID, 如果为NULL，则表示不比对签章ID
	 * @param compTime 允许误差时间，单位秒，如果为NULL则表示不校验
	 * @return 0–成功；其他错误（定义详细的错误码）
	 */
	public int verifyFormStamp(String data, String model, String sheetId, String placeId, String esealId, int compTime){
		return bjStampHelper.verifyFormStamp(data, model, sheetId, placeId, compTime);
	}
	
	/**
	 * 获取盖章ID
	 * @param data     盖章签名后的XML业务凭证(非Base64编码)
	 * @param model    表单模板(非Base64编码)
	 * @param sheetId  表单联ID
	 * @param placeId  要验证盖章位置ID,不能为NULL
	 * @return 成功返回签章ID(即用户签章对应表中的eseal_id字段)，失败返回NULL
	 */
	public String getEsealId(String data, String model, String sheetId, String placeId){
		return bjStampHelper.getEsealID(data, model,sheetId, placeId);
	}
	
	/**
	 * 获取指定盖章位置的盖章时间
	 * @param data     盖章签名后的XML业务凭证(非Base64编码)
	 * @param model    表单模板(非Base64编码)
	 * @param sheetId  表单联ID
	 * @param placeId  要验证盖章位置ID,不能为NULL
	 * @return 成功返回盖章时间，失败返回NULL
	 */
	public String getStampTime(String data, String model,String sheetId, String placeId){
		return bjStampHelper.getStampTime(data, model, sheetId, placeId);
	}
	
	/**
	 * 获得印鉴的错误信息
	 * @return
	 */
	public String getLastError(){
		int retCode = bjStampHelper.getErrorCode();
		String errorMsg = "(" + retCode + ")" + bjStampHelper.getErrorMsg(retCode);
		return errorMsg;
	}
	
	/**
	 *  生成字符串的base64编码
	 * @param src 待编码的字符串
	 * @return 字符串的base64编码
	 */
	public String base64Encode(String src){
		byte[] out = Base64.encode(src.getBytes());
		return new String(out);
	}
	
	/**
	 * 由base64编码的字符串，生成源字符串
	 * @param src base64编码的字符串
	 * @return 源字符串
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
	 * 初始化方法
	 */
	private static int init(){
		int ret = 0;
		String classname = "RHConfig.xml";
		String path = StampSendHelper.class.getClassLoader().getResource(classname).getPath();
		path = path.substring(0, path.lastIndexOf(classname) - 1);
		// 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
		if (path.endsWith("!")){
			path = path.substring(0, path.lastIndexOf("/"));
		}
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
		String configureFile = path + "/" + classname;
		if (_log.isInfoEnabled()){
			_log.info("使用配制文件" + configureFile + "初始化印鉴服务。");
		}
		try{
			//需要验证印鉴
			bjStampHelper = new ActiveXHelper();
			bjStampHelper.initConfig(configureFile);
		}catch(Exception e){
			_log.error("格尔印鉴服务初始化失败", e);
			ret = -1;
		}
		return ret;
	}
}

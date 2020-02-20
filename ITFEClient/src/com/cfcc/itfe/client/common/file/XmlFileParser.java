/**
 * 
 */
package com.cfcc.itfe.client.common.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.cfcc.itfe.client.sendbiz.bizcertsend.model.XmlDzDetail;
import com.cfcc.itfe.client.sendbiz.bizcertsend.model.XmlDzFile;
import com.cfcc.itfe.client.sendbiz.bizcertsend.model.XmlTkDetail;
import com.cfcc.itfe.client.sendbiz.bizcertsend.model.XmlTkFile;
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * 针对本系统xml报文所使用的解析和生成方法
 * 因为本系统中的xml报文，需要解析一部分，而另一部分则不需要解析
 * 所以使用目前的xml解析方法，没有得到太好的解决方法，所以自己针对本系统报文写一个解析方法
 * @author sjz
 * 2009-3-7 下午04:36:06
 */
public class XmlFileParser {
	private final static String HEAD = "head";//Xml报文头的标签集合的Key值
	private final static String BODY = "body";//Xml报文体标签集合的Key值
	private static String detailBeginTag = "<DETAIL";//一条明细记录的开始Tag标签
	private static String detailEndTag = "</DETAIL>";//一条明细记录的结束Tag标签
	
	
	/**
	 * 解析Xml字符串，将Xml的内容存放到对象fileObject中
	 * @param xmlString xml字符串
	 * @param <T> fileObject 存放Xml内容的对象
	 * @return true-成功
	 * @throws Exception
	 */
	public static<T extends XmlFileBaseObject> boolean parseXml(String xmlString, T fileObject) throws Exception{
		Map<String, Map<String,String>> tagMap = getTagMap(fileObject);
		//获取Xml文件的报文头的内容
		Map<String,String> headMap = tagMap.get(HEAD);
		parseXml(headMap, xmlString, fileObject);
		
		//循环获取Xml的报文体的内容
		List<XmlFileBaseObject> details = new ArrayList<XmlFileBaseObject>();
		Map<String,String> bodyMap = tagMap.get(BODY);
		//截取正文中每一个detail标签之间的部分，每一部分就是一条记录
		int iBeginPos = xmlString.indexOf(detailBeginTag);
		int iEndPos = 0;
		while (iBeginPos >= 0){
			//截取一条记录，并解析
			iEndPos = xmlString.indexOf(detailEndTag,iBeginPos);
			//根据Xml文件对象类型，获得Xml明细信息对象
			XmlFileBaseObject one = getDetailObject(fileObject);
			if (one == null){
				throw new Exception("无法获取存放Xml明细信息的对象。");
			}
			String strDetail = xmlString.substring(iBeginPos, iEndPos);
			//解析一笔明细信息
			parseXml(bodyMap,strDetail, one);
			details.add(one);
			iBeginPos = xmlString.indexOf(detailBeginTag, iEndPos);
		}
		fileObject.setDetails(details);
		return true;
	}
	
	/**
	 * 解析传入的xml格式的字符串，并得到报文对象
	 * @param Map<String, String> tagMap 记录xml中标签的Map
	 * @param xmlString xml格式的字符串
	 * @param file xml内容存放的字符串
	 * @return
	 */
	public static void parseXml(Map<String, String> tagMap,String xmlString, XmlFileBaseObject file) throws Exception{
		try{
			//记录xml报文内容的对象，使用Spring的BeanWrapper
			BeanWrapper wrapper = new BeanWrapperImpl(file);
			//获得xml报文中所有的tag标签
			String[] tags = new String[tagMap.keySet().size()];
			tagMap.keySet().toArray(tags);
			for (String tag : tags){
				//获得一个xml报文中一个标签所对应的值
				String value = getValueByTag(xmlString, tag);
				wrapper.setPropertyValue(tagMap.get(tag), value);
			}
		}catch(Exception e){
			throw new ITFEBizException("xml解析时发生错误", e);
		}
	}
	
	/**
	 * 从xml格式的字符串中获得指定tag标签所对应的值
	 * @param xmlString xml格式的字符串
	 * @param tag tag标签
	 * @return tag标签对应的值
	 */
	public static String getValueByTag(String xmlString, String tag){
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
				value = FileOperFacade.trim(tmp);
			}
		}
		return value;
	}
	
	/**
	 * 根据传入的存放XML内容的对象的种类，获得Xml的标签与Java对象成员变量对应关系的集合
	 * head中为Xml中的报文头信息，只获取一次即可
	 * body中为Xml中的报文体信息，需要循环获取，直到最后找不到为止
	 * @param <T> fileObject Xml内容的存放对象
	 * @return Xml内容的标签与Java对象中成员变量对应关系
	 */
	public static<T extends XmlFileBaseObject> Map<String, Map<String,String>> getTagMap(T fileObject){
		//记录整个Xml报文所有标签的Map
		Map<String, Map<String,String>> tagMap = new HashMap<String, Map<String,String>>();
		//记录Xml报文头信息的Map
		Map<String, String> headMap = new HashMap<String, String>();
		//记录Xml报文体信息的Map
		Map<String, String> bodyMap = new HashMap<String, String>();
		
		//根据Xml文件对象的类型来填写Xml报文的标签
		if (fileObject instanceof XmlTkFile){
			//退款文件
			//报文头
			headMap.put("SEND_NAME",  "sendName");
			headMap.put("PAY_CODE",   "payCode");
			headMap.put("PAY_DESC",   "payDesc");
			headMap.put("VOU_DATE",   "vouDate");
			headMap.put("VOU_NUM",    "vouNum");
			headMap.put("PAYEE_CODE", "payeeCode");
			headMap.put("PAYOR_NAME", "payorName");
			headMap.put("PAYOR_ACC",  "payorAcc");
			headMap.put("PAYOR_BANK", "payorBank");
			headMap.put("PAYEE_NAME", "payeeName");
			headMap.put("PAYEE_ACC",  "payeeAcc");
			headMap.put("PAYEE_BANK", "payeeBank");
			headMap.put("SUM_MONEY",  "sumMoney");
			headMap.put("OPER_NAME",  "operName");
			headMap.put("SUMMARY",    "summary");
			headMap.put("REMARK",     "remark");
			headMap.put("IDLE",       "idle");
			//报文体
			bodyMap.put("UN_CODE", "unitCode");
			bodyMap.put("UN_NAME", "unitName");
			bodyMap.put("MONEY",   "money");
			bodyMap.put("FN_CODE", "funcCode");
			bodyMap.put("IN_TYPE", "inType");
			bodyMap.put("EN_CODE", "encCode");
			//明细记录的开始和结束Tag标签
			
		}else if (fileObject instanceof XmlDzFile){
			//对帐文件
			//报文头
			headMap.put("SEND_CODE", "sendCode");
			headMap.put("SEND_NAME", "sendName");
			headMap.put("PAY_CODE",  "payCode");
			headMap.put("PAY_DESC",  "payDesc");
			headMap.put("MONTH",     "month");
			headMap.put("OPER_NAME", "operName");
			headMap.put("IDLE",      "idle");
			//报文体
			bodyMap.put("UN_CODE", "unitCode");
			bodyMap.put("UN_NAME", "unitName");
			bodyMap.put("FN_CODE", "funcCode");
			bodyMap.put("ED_MON",  "edMoney");
			bodyMap.put("TK_MON",  "tkMoney");
			bodyMap.put("QS_MON",  "qsMoney");
			bodyMap.put("RE_MON",  "reMoney");
			bodyMap.put("IN_TYPE", "inType");
		}
		
		tagMap.put(HEAD, headMap);
		tagMap.put(BODY, bodyMap);
		return tagMap;
	}
	
	/**
	 * 根据Xml报文对应的对象的类型，获得存放Xml明细信息的对象
	 * @param <T> fileObject Xml内容的存放对象
	 * @return 存放Xml明细信息对象
	 */
	public static<T extends XmlFileBaseObject> XmlFileBaseObject getDetailObject(T fileObject){
		//根据Xml文件对象的类型来填写Xml报文的标签
		if (fileObject instanceof XmlTkFile){
			//退款文件
			return new XmlTkDetail();
		}else if (fileObject instanceof XmlDzFile){
			//对帐文件
			return new XmlDzDetail();
		}else{
			return null;
		}
	}
	
	public static void main(String[] args){
		try{
			String xmlString = FileOperFacade.readFile("e:/QS-0-095-2-20091106-001.xml");
			XmlTkFile file = new XmlTkFile();
			parseXml(xmlString, file);
			int count = 0;
			for (XmlFileBaseObject one : file.getDetails()){
				count++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

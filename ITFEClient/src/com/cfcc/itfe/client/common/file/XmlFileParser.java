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
 * ��Ա�ϵͳxml������ʹ�õĽ��������ɷ���
 * ��Ϊ��ϵͳ�е�xml���ģ���Ҫ����һ���֣�����һ��������Ҫ����
 * ����ʹ��Ŀǰ��xml����������û�еõ�̫�õĽ�������������Լ���Ա�ϵͳ����дһ����������
 * @author sjz
 * 2009-3-7 ����04:36:06
 */
public class XmlFileParser {
	private final static String HEAD = "head";//Xml����ͷ�ı�ǩ���ϵ�Keyֵ
	private final static String BODY = "body";//Xml�������ǩ���ϵ�Keyֵ
	private static String detailBeginTag = "<DETAIL";//һ����ϸ��¼�Ŀ�ʼTag��ǩ
	private static String detailEndTag = "</DETAIL>";//һ����ϸ��¼�Ľ���Tag��ǩ
	
	
	/**
	 * ����Xml�ַ�������Xml�����ݴ�ŵ�����fileObject��
	 * @param xmlString xml�ַ���
	 * @param <T> fileObject ���Xml���ݵĶ���
	 * @return true-�ɹ�
	 * @throws Exception
	 */
	public static<T extends XmlFileBaseObject> boolean parseXml(String xmlString, T fileObject) throws Exception{
		Map<String, Map<String,String>> tagMap = getTagMap(fileObject);
		//��ȡXml�ļ��ı���ͷ������
		Map<String,String> headMap = tagMap.get(HEAD);
		parseXml(headMap, xmlString, fileObject);
		
		//ѭ����ȡXml�ı����������
		List<XmlFileBaseObject> details = new ArrayList<XmlFileBaseObject>();
		Map<String,String> bodyMap = tagMap.get(BODY);
		//��ȡ������ÿһ��detail��ǩ֮��Ĳ��֣�ÿһ���־���һ����¼
		int iBeginPos = xmlString.indexOf(detailBeginTag);
		int iEndPos = 0;
		while (iBeginPos >= 0){
			//��ȡһ����¼��������
			iEndPos = xmlString.indexOf(detailEndTag,iBeginPos);
			//����Xml�ļ��������ͣ����Xml��ϸ��Ϣ����
			XmlFileBaseObject one = getDetailObject(fileObject);
			if (one == null){
				throw new Exception("�޷���ȡ���Xml��ϸ��Ϣ�Ķ���");
			}
			String strDetail = xmlString.substring(iBeginPos, iEndPos);
			//����һ����ϸ��Ϣ
			parseXml(bodyMap,strDetail, one);
			details.add(one);
			iBeginPos = xmlString.indexOf(detailBeginTag, iEndPos);
		}
		fileObject.setDetails(details);
		return true;
	}
	
	/**
	 * ���������xml��ʽ���ַ��������õ����Ķ���
	 * @param Map<String, String> tagMap ��¼xml�б�ǩ��Map
	 * @param xmlString xml��ʽ���ַ���
	 * @param file xml���ݴ�ŵ��ַ���
	 * @return
	 */
	public static void parseXml(Map<String, String> tagMap,String xmlString, XmlFileBaseObject file) throws Exception{
		try{
			//��¼xml�������ݵĶ���ʹ��Spring��BeanWrapper
			BeanWrapper wrapper = new BeanWrapperImpl(file);
			//���xml���������е�tag��ǩ
			String[] tags = new String[tagMap.keySet().size()];
			tagMap.keySet().toArray(tags);
			for (String tag : tags){
				//���һ��xml������һ����ǩ����Ӧ��ֵ
				String value = getValueByTag(xmlString, tag);
				wrapper.setPropertyValue(tagMap.get(tag), value);
			}
		}catch(Exception e){
			throw new ITFEBizException("xml����ʱ��������", e);
		}
	}
	
	/**
	 * ��xml��ʽ���ַ����л��ָ��tag��ǩ����Ӧ��ֵ
	 * @param xmlString xml��ʽ���ַ���
	 * @param tag tag��ǩ
	 * @return tag��ǩ��Ӧ��ֵ
	 */
	public static String getValueByTag(String xmlString, String tag){
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
				value = FileOperFacade.trim(tmp);
			}
		}
		return value;
	}
	
	/**
	 * ���ݴ���Ĵ��XML���ݵĶ�������࣬���Xml�ı�ǩ��Java�����Ա������Ӧ��ϵ�ļ���
	 * head��ΪXml�еı���ͷ��Ϣ��ֻ��ȡһ�μ���
	 * body��ΪXml�еı�������Ϣ����Ҫѭ����ȡ��ֱ������Ҳ���Ϊֹ
	 * @param <T> fileObject Xml���ݵĴ�Ŷ���
	 * @return Xml���ݵı�ǩ��Java�����г�Ա������Ӧ��ϵ
	 */
	public static<T extends XmlFileBaseObject> Map<String, Map<String,String>> getTagMap(T fileObject){
		//��¼����Xml�������б�ǩ��Map
		Map<String, Map<String,String>> tagMap = new HashMap<String, Map<String,String>>();
		//��¼Xml����ͷ��Ϣ��Map
		Map<String, String> headMap = new HashMap<String, String>();
		//��¼Xml��������Ϣ��Map
		Map<String, String> bodyMap = new HashMap<String, String>();
		
		//����Xml�ļ��������������дXml���ĵı�ǩ
		if (fileObject instanceof XmlTkFile){
			//�˿��ļ�
			//����ͷ
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
			//������
			bodyMap.put("UN_CODE", "unitCode");
			bodyMap.put("UN_NAME", "unitName");
			bodyMap.put("MONEY",   "money");
			bodyMap.put("FN_CODE", "funcCode");
			bodyMap.put("IN_TYPE", "inType");
			bodyMap.put("EN_CODE", "encCode");
			//��ϸ��¼�Ŀ�ʼ�ͽ���Tag��ǩ
			
		}else if (fileObject instanceof XmlDzFile){
			//�����ļ�
			//����ͷ
			headMap.put("SEND_CODE", "sendCode");
			headMap.put("SEND_NAME", "sendName");
			headMap.put("PAY_CODE",  "payCode");
			headMap.put("PAY_DESC",  "payDesc");
			headMap.put("MONTH",     "month");
			headMap.put("OPER_NAME", "operName");
			headMap.put("IDLE",      "idle");
			//������
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
	 * ����Xml���Ķ�Ӧ�Ķ�������ͣ���ô��Xml��ϸ��Ϣ�Ķ���
	 * @param <T> fileObject Xml���ݵĴ�Ŷ���
	 * @return ���Xml��ϸ��Ϣ����
	 */
	public static<T extends XmlFileBaseObject> XmlFileBaseObject getDetailObject(T fileObject){
		//����Xml�ļ��������������дXml���ĵı�ǩ
		if (fileObject instanceof XmlTkFile){
			//�˿��ļ�
			return new XmlTkDetail();
		}else if (fileObject instanceof XmlDzFile){
			//�����ļ�
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

/**
 * 
 */
package com.cfcc.itfe.service.gzqzwebservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author Administrator
 * 
 */
public class JOSNUtils {

	//�������
	public final static String TRECODE = "TRECODE";
	//����
	public final static String ACCTDATE = "ACCTDATE";
	//�����ڱ�־
	public final static String TRIMFLAG = "TRIMFLAG";
	//Ԥ������
	public final static String BUDGETTYPE = "BUDGETTYPE";
	//���ջ��ش���
	public final static String TAXORGCODE = "TAXORGCODE";
	//Ͻ����־
	public final static String BELONGFLAG = "BELONGFLAG";
	//�Ƿ񺬿�ϼ�
	public final static String DIVIDEGROUP = "DIVIDEGROUP";
	//��������
	public final static String BILLTYPE = "BILLTYPE";
	

	public static Map<String, String> getJOSNContent(String param){
		if(StringUtils.isBlank(param)){
			return null;
		}
		Map<String, String> resultMap = new HashMap<String, String>();
		String[] strs = param.split(",");
		String[] splits = null;
		for(String str : strs){
			splits = str.split(":");
			resultMap.put(splits[0], splits[1]);
		}
		if(null != resultMap && resultMap.size() > 0){
		return resultMap;
		}else{
			return null;
		}
	}
}

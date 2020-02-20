/**
 * 
 */
package com.cfcc.itfe.client.common.stamp;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleControlSite;

/**
 * ʹ�õ���ӡ���Ļ�����
 * @author sjz
 * 2009-10-12 19:35:55
 */
public abstract class BaseStampHandler {
	//����ӡ��ActiveX�ؼ�������
	protected String stampLibName;
	//ole�ؼ�����
	protected OleControlSite controlSite;
	//ole���󷽷����ô���
	protected OleAutomation automation;
	
	/**
	 * ����ActiveX�ؼ���ʾ�����ӡ��
	 * @param dataxml �����ļ�����xml��base64����
	 * @param modelxml ģ���ļ�����xml��base64����
	 * @param modelId ��ID
	 * @param percent ��ʾ�ٷֱȣ�100Ϊ������ʾ
	 * @param isTrash �Ƿ���Ч��־��1����Ч��0������
	 * @return �ɹ����ؿգ����򷵻ش�����ʹ���˵��
	 */
	abstract public String showBills(String dataxml,String modelxml, String modelId, String percent);
	/**
	 * ����ActiveX�ؼ���ʾ�����ӡ��
	 * @param dataxml �����ļ�����xml��base64����
	 * @param modelxml ģ���ļ�����xml��base64����
	 * @param modelId ��ID
	 * @param pageNo Ҫ��ʾ��ҳ��
	 * @param percent ��ʾ�ٷֱȣ�100Ϊ������ʾ
	 * @param isTrash �Ƿ���Ч��־��1����Ч��0������
	 * @return �ɹ�������ҳ�������򷵻ش�����ʹ���˵��
	 */
	abstract public String showBill(String dataxml,String modelxml, String modelId, String pageNo,String percent);
	/**
	 * ����ActiveX�ؼ���ӡ
	 * @param dataxml �����ļ�����xml��base64����
	 * @param modelxml ģ���ļ�����xml��base64����
	 * @param modelId ��ID
	 * @param isReal �Ƿ��ӡ��ʵƾ֤�������ƾ֤
	 * @param isTrash �Ƿ���Ч��־��1����Ч��0������
	 * @return �ɹ����ؿգ����򷵻ش�����ʹ���˵��
	 */
	abstract public String printBills(String dataxml,String modelxml, String modelId);
	/**
	 * ����ActiveX�ؼ�����
	 * @param dataxml �����ļ�����xml��base64����
	 * @param modelxml ģ���ļ�����xml��base64����
	 * @param modelid ��ID
	 * @param placeid ģ���ж������Ҫ���µ�λ��
	 * @param label ǩ�±�����key���λ�ñ�ǩ
	 * @param esealid ǩ�µ�ID(SN)�����Ϊ�ձ�ʾ��У��ǩ��ID
	 * @return ǩ�³ɹ������ذ���ǩ�������Base64�����ļ�����ǩ��ʧ�ܷ��ؿ�
	 */
	abstract public String signEseal(String dataxml, String modelxml, String modelid, String placeid,String label, String esealid);
	/**
	 * ����ActiveX�ؼ�����
	 * @param xmlData �����ļ�����xml��base64����
	 * @param xmlModel ģ���ļ�����xml��base64����
	 * @param modelid ��ID
	 * @param placeid ģ���ж������Ҫ���µ�λ��
	 * @param host ǩ�·�������ַ
	 * @param port ��֤����˿�
	 * @return �ɹ����ؿգ����򷵻ش�����ʹ���˵��
	 */
	abstract public String verifyFormStamp(String xmlData, String xmlModel, String modelid, String placeid, String host, String port);
	/**
	 * ����ActiveX�ؼ��������ַ�������base64����
	 * @param srcString ��Ҫ����base64���������
	 * @return �ɹ�����base64�����Ľ����ʧ�ܷ��ؿ��ַ���
	 */
	abstract public String base64Encode(String srcString);
	
	/**
	 * ����ActiveX�ؼ��������ַ�������base64����
	 * @param base64String ��Ҫ����base64���������
	 * @return �ɹ�����base64�����Ľ����ʧ�ܷ��ؿ��ַ���
	 */
	abstract public String base64Decode(String base64String);
	/**
	 * ����ActiveX�ؼ����Key��ָ����ǩ��ӡ��ID
	 * Key�е�ÿ��ӡ������һ��Ψһ�ı�ǩ
	 * @param id ��ǩ
	 * @return ӡ��ID
	 */
	abstract public String getEsealIdFromKey(String id);
	/**
	 * ����ActiveX�ؼ���ȡ���Ѹ���λ�õ��б�
	 * @param xmlData �����ļ�����xml��base64����
	 * @param xmlModel ģ���ļ�����xml��base64����
	 * @return �ɹ�����ҵ��ƾ֤���Ѹ��µ�λ���б�(�ã��ָ�)��ʧ�ܷ���NULL
	 */
	abstract public String getFormStampInfo(String xmlData, String xmlModel);
	/**
	 * ����ActiveX�ؼ���ô��󷵻���Ϣ
	 * @return ����˵��
	 */
	abstract public String getErrorMsg();
	/**
	 * ����ActiveX�ؼ���ô�����
	 * @return ������
	 */
	abstract public int getErrorCode();
	/**
	 * ����ActiveX�ؼ���ô�����ʹ�����Ϣ
	 * @return ����˵��
	 */
	abstract public String getLastError();
	/**
	 * ����ActiveX�ؼ����������Ϣ
	 * @return ����˵��
	 */
	abstract public String getDataXMLBase64();
}

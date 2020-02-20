/**
 * 
 */
package com.cfcc.itfe.client.common.stamp.koal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;

import com.cfcc.itfe.client.common.stamp.BaseStampHandler;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;

/**
 * �����˾����ӡ����Ʒ������
 * @author sjz
 * 2009-10-12 19:45:21
 */
public class KoalStampHandler extends BaseStampHandler {
	public KoalStampHandler(AbstractMetaDataEditorPart editor){
		OleFrame oleframe = new OleFrame(editor.getCurrentComposite(), SWT.BORDER);
		oleframe.setBounds(0, 0, 1, 1);
		oleframe.setVisible(true);
		controlSite = new OleControlSite(oleframe, SWT.NONE | SWT.MAX,	"EsealForm.MainForm.1");//KoalForm.MainWin.1
		//����Ҫ������Ƕ�ؼ��Ĵ�С����Ϊ�����Ѿ������˸��ؼ��Ĵ�С����Ƕ�ؼ��ᰴ�ո��ؼ��Ĵ�С��ʾ
		controlSite.doVerb(OLE.OLEIVERB_PRIMARY);
		controlSite.doVerb(OLE.OLEIVERB_UIACTIVATE);
		controlSite.doVerb(OLE.OLEIVERB_SHOW);
		automation = new OleAutomation(controlSite);
	}
	
	public KoalStampHandler(OleControlSite controlSite){
		this.controlSite = controlSite;
		this.automation = new OleAutomation(controlSite);
	}
	
	/**
	 * ����ActiveX�ؼ���ʾ�����ӡ��
	 * @param dataxml �����ļ�����xml��base64����
	 * @param modelxml ģ���ļ�����xml��base64����
	 * @param modelId ��ID
	 * @param percent ��ʾ�ٷֱȣ�100Ϊ������ʾ
	 * @param isTrash �Ƿ���Ч��־��0����Ч��1������
	 * @return �ɹ����ؿգ����򷵻ش�����ʹ���˵��
	 */
	@Override
	public String showBills(String dataxml, String modelxml, String modelId, String percent) {
		String retString;
		
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_showBills"});//showBills
		int functionId = functionIds[0];
		//�����������
		Variant[] variant = new Variant[5];
		variant[0] = new Variant(dataxml);
		variant[1] = new Variant(modelxml);
		variant[2] = new Variant(modelId);
		variant[3] = new Variant(percent);
		variant[4] = new Variant("0");
		//���÷�������ȡ����ֵ
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) || (ret.getString().charAt(0) != '0')){
			//���ִ���
			retString = "(" + getErrorCode() + ")" + getErrorMsg();
		}else{
			retString = "";
		}
		return retString;
	}

	/**
	 * ����ActiveX�ؼ��������ַ�������base64����
	 * @param srcString ��Ҫ����base64���������
	 * @return �ɹ�����base64�����Ľ����ʧ�ܷ��ؿ��ַ���
	 */
	@Override
	public String base64Encode(String srcString) {
		String retString;
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_base64Encode"});//BJCZ_base64Encode
		int functionId = functionIds[0];
		//�����������
		Variant[] variant = new Variant[1];
		variant[0] = new Variant(srcString);
		//���÷�������ȡ����ֵ
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null) || (ret.getType() == OLE.VT_EMPTY)){
			retString = "";
		}else{
			retString = ret.getString();
		}
		return retString;
	}

	/**
	 * ����ActiveX�ؼ��������ַ�������base64����
	 * @param base64String ��Ҫ����base64���������
	 * @return �ɹ�����base64�����Ľ����ʧ�ܷ��ؿ��ַ���
	 */
	@Override
	public String base64Decode(String base64String) {
		String retString;
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_base64Decode"});//BJCZ_base64Decode
		int functionId = functionIds[0];
		//�����������
		Variant[] variant = new Variant[1];
		variant[0] = new Variant(base64String);
		//���÷�������ȡ����ֵ
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null) || (ret.getType() == OLE.VT_EMPTY)){
			retString = "";
		}else{
			retString = ret.getString();
		}
		return retString;
	}

	/**
	 * ����ActiveX�ؼ���ô�����
	 * @return ������
	 */
	@Override
	public int getErrorCode() {
		int code;
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_getErrorCode"});//BJCZ_getErrorCode
		int functionId = functionIds[0];
		//���÷���
		Variant ret = automation.invoke(functionId);
		if ((ret == null) || (ret.getType() == OLE.VT_EMPTY)){
			code = 9999;
		}else{
			code = ret.getInt();
		}
		return code;
	}

	/**
	 * ����ActiveX�ؼ���ô��󷵻���Ϣ
	 * @return ����˵��
	 */
	@Override
	public String getErrorMsg() {
		String msg;
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_getErrorMsg"});//BJCZ_getErrorMsg
		int functionId = functionIds[0];
		//���÷���
		Variant ret = automation.invoke(functionId);
		if ((ret == null) || (ret.getType() == OLE.VT_EMPTY)){
			msg = "";
		}else{
			msg = ret.getString();
		}
		return msg;
	}

	/**
	 * ����ActiveX�ؼ����Key��ָ����ǩ��ӡ��ID
	 * Key�е�ÿ��ӡ������һ��Ψһ�ı�ǩ
	 * @param id ��ǩ
	 * @return ӡ��ID
	 */
	@Override
	public String getEsealIdFromKey(String id) {
		String esealId;
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_getEsealIDFromKey"});//BJCZ_getEsealIDFromKey_ex
		int functionId = functionIds[0];
		//�����������
		Variant[] variant = new Variant[1];
		variant[0] = new Variant(id);
		//���÷���
		Variant ret = automation.invoke(functionId,variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) || (ret.getString() == "")){
			//���ִ���
			esealId = "";
		}else{
			esealId = ret.getString();
		}
		return esealId;
	}

	/**
	 * ����ActiveX�ؼ���ӡ
	 * @param dataxml �����ļ�����xml��base64����
	 * @param modelxml ģ���ļ�����xml��base64����
	 * @param modelId ��ID
	 * @param isReal �Ƿ��ӡ��ʵƾ֤�������ƾ֤
	 * @param isTrash �Ƿ���Ч��־��0����Ч��1������
	 * @return �ɹ����ؿգ����򷵻ش�����ʹ���˵��
	 */
	@Override
	public String printBills(String dataxml, String modelxml, String modelId) {
		String result;
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_printBills"});//printBills
		int functionId = functionIds[0];
		//׼���������
		Variant[] variant = new Variant[5];
		variant[0] = new Variant(dataxml);
		variant[1] = new Variant(modelxml);
		variant[2] = new Variant(modelId);
		variant[3] = new Variant("1");
		variant[4] = new Variant("0");
		//���÷���
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) ||(ret.getString().charAt(0) != '0')){
			//���ִ���
			result = "(" + getErrorCode() + ")" + getErrorMsg();
		}else{
			result = "";
		}
		return result;
	}

	/**
	 * ����ActiveX�ؼ���ʾ�����ӡ��
	 * @param dataxml �����ļ�����xml��base64����
	 * @param modelxml ģ���ļ�����xml��base64����
	 * @param modelId ��ID
	 * @param pageNo Ҫ��ʾ��ҳ��
	 * @param percent ��ʾ�ٷֱȣ�100Ϊ������ʾ
	 * @param isTrash �Ƿ���Ч��־��0����Ч��1������
	 * @return �ɹ�������ҳ�������򷵻ش�����ʹ���˵��
	 */
	@Override
	public String showBill(String dataxml, String modelxml, String modelId,String pageNo,String percent) {
		String retString;
		
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_showBill"});//showBill
		int functionId = functionIds[0];
		//�����������
		Variant[] variant = new Variant[6];
		variant[0] = new Variant(dataxml);
		variant[1] = new Variant(modelxml);
		variant[2] = new Variant(modelId);
		variant[3] = new Variant("1");
		variant[4] = new Variant(percent);
		variant[5] = new Variant("0");
		//���÷�������ȡ����ֵ
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY)){
			//���ִ���
			retString = "(" + getErrorCode() + ")" + getErrorMsg();
		}else{
			int iRet = Integer.parseInt(ret.getString());
			if (iRet < 0){
				//���ִ���
				retString = "(" + getErrorCode() + ")" + getErrorMsg();
			}else{
				retString = "";
			}
		}
		return retString;
	}

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
	@Override
	public String signEseal(String dataxml, String modelxml, String modelid,
			String placeid, String label, String esealid) {
		String strEseal;
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_signEseal"});//BJCZ_signEseal_ex
		int functionId = functionIds[0];
		//׼���������
		Variant[] variant = new Variant[6];
		variant[0] = new Variant(dataxml);
		variant[1] = new Variant(modelxml);
		variant[2] = new Variant(modelid);
		variant[3] = new Variant(placeid);
		variant[4] = new Variant(label);
		variant[5] = new Variant(esealid);
		//���÷���
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) ||(ret.getString() == null) ||(ret.getString() == "")){
			strEseal = "";
		}else{
			strEseal = ret.getString();
		}
		return strEseal;
	}

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
	@Override
	public String verifyFormStamp(String dataxml, String modelxml,
			String modelid, String placeid, String host, String port) {
		String result;
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_verifyFormStamp"});//verifyFormStamp
		int functionId = functionIds[0];
		//׼���������
		Variant[] variant = new Variant[6];
		variant[0] = new Variant(dataxml);
		variant[1] = new Variant(modelxml);
		variant[2] = new Variant(modelid);
		variant[3] = new Variant(placeid);
		variant[4] = new Variant(host);
		variant[5] = new Variant(port);
		//���÷���
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null) || (ret.getType() == OLE.VT_EMPTY) ||(ret.getString().charAt(0) != '0')){
			//���ִ���
			result = "(" + getErrorCode() + ")" + getErrorMsg();
		}else{
			result = "";
		}
		return result;
	}

	/**
	 * ����ActiveX�ؼ���ô�����ʹ�����Ϣ
	 * @return ����˵��
	 */
	@Override
	public String getLastError() {
		String error = "(" + getErrorCode() + ")" + getErrorMsg();
		return error;
	}

	/**
	 * ����ActiveX�ؼ���ȡ���Ѹ���λ�õ��б�
	 * @param xmlData �����ļ�����xml��base64����
	 * @param xmlModel ģ���ļ�����xml��base64����
	 * @return �ɹ�����ҵ��ƾ֤���Ѹ��µ�λ���б�(�ã��ָ�)��ʧ�ܷ���NULL
	 * @deprecated
	 */
	@Override
	public String getFormStampInfo(String xmlData, String xmlModel) {
		String stampInfo;
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"BJCZ_getFormStampInfo"});
		int functionId = functionIds[0];
		//׼���������
		Variant[] variant = new Variant[2];
		variant[0] = new Variant(xmlData);
		variant[1] = new Variant(xmlModel);
		//���÷���
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) ||(ret.getString() == null) ){
			stampInfo = null;
		}else{
			stampInfo = ret.getString();
		}
		return stampInfo;
	}

	@Override
	public String getDataXMLBase64() {
		String data;
		//��÷���ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"getDataXMLBase64"});
		int functionId = functionIds[0];
		//�����������
		Variant[] variant = new Variant[0];
		//���÷���
		Variant ret = automation.invoke(functionId,variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) || (ret.getString() == "")){
			//���ִ���
			data = "";
		}else{
			data = ret.getString();
		}
		return data;
	}

}

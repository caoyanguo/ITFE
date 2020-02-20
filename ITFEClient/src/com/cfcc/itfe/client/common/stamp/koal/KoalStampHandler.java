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
 * 格尔公司电子印鉴产品调用类
 * @author sjz
 * 2009-10-12 19:45:21
 */
public class KoalStampHandler extends BaseStampHandler {
	public KoalStampHandler(AbstractMetaDataEditorPart editor){
		OleFrame oleframe = new OleFrame(editor.getCurrentComposite(), SWT.BORDER);
		oleframe.setBounds(0, 0, 1, 1);
		oleframe.setVisible(true);
		controlSite = new OleControlSite(oleframe, SWT.NONE | SWT.MAX,	"EsealForm.MainForm.1");//KoalForm.MainWin.1
		//不需要设置内嵌控件的大小，因为上面已经设置了父控件的大小，内嵌控件会按照父控件的大小显示
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
	 * 调用ActiveX控件显示报表和印鉴
	 * @param dataxml 数据文件流，xml的base64编码
	 * @param modelxml 模板文件流，xml的base64编码
	 * @param modelId 联ID
	 * @param percent 显示百分比，100为正常显示
	 * @param isTrash 是否有效标志，0是有效，1是作废
	 * @return 成功返回空，否则返回错误码和错误说明
	 */
	@Override
	public String showBills(String dataxml, String modelxml, String modelId, String percent) {
		String retString;
		
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_showBills"});//showBills
		int functionId = functionIds[0];
		//定义输入参数
		Variant[] variant = new Variant[5];
		variant[0] = new Variant(dataxml);
		variant[1] = new Variant(modelxml);
		variant[2] = new Variant(modelId);
		variant[3] = new Variant(percent);
		variant[4] = new Variant("0");
		//调用方法，获取返回值
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) || (ret.getString().charAt(0) != '0')){
			//出现错误
			retString = "(" + getErrorCode() + ")" + getErrorMsg();
		}else{
			retString = "";
		}
		return retString;
	}

	/**
	 * 调用ActiveX控件对输入字符串进行base64编码
	 * @param srcString 需要进行base64编码的数据
	 * @return 成功返回base64编码后的结果，失败返回空字符串
	 */
	@Override
	public String base64Encode(String srcString) {
		String retString;
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_base64Encode"});//BJCZ_base64Encode
		int functionId = functionIds[0];
		//定义输入参数
		Variant[] variant = new Variant[1];
		variant[0] = new Variant(srcString);
		//调用方法，获取返回值
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null) || (ret.getType() == OLE.VT_EMPTY)){
			retString = "";
		}else{
			retString = ret.getString();
		}
		return retString;
	}

	/**
	 * 调用ActiveX控件对输入字符串进行base64解码
	 * @param base64String 需要进行base64解码的数据
	 * @return 成功返回base64解码后的结果，失败返回空字符串
	 */
	@Override
	public String base64Decode(String base64String) {
		String retString;
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_base64Decode"});//BJCZ_base64Decode
		int functionId = functionIds[0];
		//定义输入参数
		Variant[] variant = new Variant[1];
		variant[0] = new Variant(base64String);
		//调用方法，获取返回值
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null) || (ret.getType() == OLE.VT_EMPTY)){
			retString = "";
		}else{
			retString = ret.getString();
		}
		return retString;
	}

	/**
	 * 调用ActiveX控件获得错误码
	 * @return 错误码
	 */
	@Override
	public int getErrorCode() {
		int code;
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_getErrorCode"});//BJCZ_getErrorCode
		int functionId = functionIds[0];
		//调用方法
		Variant ret = automation.invoke(functionId);
		if ((ret == null) || (ret.getType() == OLE.VT_EMPTY)){
			code = 9999;
		}else{
			code = ret.getInt();
		}
		return code;
	}

	/**
	 * 调用ActiveX控件获得错误返回信息
	 * @return 错误说明
	 */
	@Override
	public String getErrorMsg() {
		String msg;
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_getErrorMsg"});//BJCZ_getErrorMsg
		int functionId = functionIds[0];
		//调用方法
		Variant ret = automation.invoke(functionId);
		if ((ret == null) || (ret.getType() == OLE.VT_EMPTY)){
			msg = "";
		}else{
			msg = ret.getString();
		}
		return msg;
	}

	/**
	 * 调用ActiveX控件获得Key中指定标签的印鉴ID
	 * Key中的每个印鉴都有一个唯一的标签
	 * @param id 标签
	 * @return 印鉴ID
	 */
	@Override
	public String getEsealIdFromKey(String id) {
		String esealId;
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_getEsealIDFromKey"});//BJCZ_getEsealIDFromKey_ex
		int functionId = functionIds[0];
		//定义输入参数
		Variant[] variant = new Variant[1];
		variant[0] = new Variant(id);
		//调用方法
		Variant ret = automation.invoke(functionId,variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) || (ret.getString() == "")){
			//出现错误
			esealId = "";
		}else{
			esealId = ret.getString();
		}
		return esealId;
	}

	/**
	 * 调用ActiveX控件打印
	 * @param dataxml 数据文件流，xml的base64编码
	 * @param modelxml 模板文件流，xml的base64编码
	 * @param modelId 联ID
	 * @param isReal 是否打印真实凭证，或黑章凭证
	 * @param isTrash 是否有效标志，0是有效，1是作废
	 * @return 成功返回空，否则返回错误码和错误说明
	 */
	@Override
	public String printBills(String dataxml, String modelxml, String modelId) {
		String result;
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_printBills"});//printBills
		int functionId = functionIds[0];
		//准备传入参数
		Variant[] variant = new Variant[5];
		variant[0] = new Variant(dataxml);
		variant[1] = new Variant(modelxml);
		variant[2] = new Variant(modelId);
		variant[3] = new Variant("1");
		variant[4] = new Variant("0");
		//调用方法
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) ||(ret.getString().charAt(0) != '0')){
			//出现错误
			result = "(" + getErrorCode() + ")" + getErrorMsg();
		}else{
			result = "";
		}
		return result;
	}

	/**
	 * 调用ActiveX控件显示报表和印鉴
	 * @param dataxml 数据文件流，xml的base64编码
	 * @param modelxml 模板文件流，xml的base64编码
	 * @param modelId 联ID
	 * @param pageNo 要显示的页码
	 * @param percent 显示百分比，100为正常显示
	 * @param isTrash 是否有效标志，0是有效，1是作废
	 * @return 成功返回总页数，否则返回错误码和错误说明
	 */
	@Override
	public String showBill(String dataxml, String modelxml, String modelId,String pageNo,String percent) {
		String retString;
		
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_showBill"});//showBill
		int functionId = functionIds[0];
		//定义输入参数
		Variant[] variant = new Variant[6];
		variant[0] = new Variant(dataxml);
		variant[1] = new Variant(modelxml);
		variant[2] = new Variant(modelId);
		variant[3] = new Variant("1");
		variant[4] = new Variant(percent);
		variant[5] = new Variant("0");
		//调用方法，获取返回值
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY)){
			//出现错误
			retString = "(" + getErrorCode() + ")" + getErrorMsg();
		}else{
			int iRet = Integer.parseInt(ret.getString());
			if (iRet < 0){
				//出现错误
				retString = "(" + getErrorCode() + ")" + getErrorMsg();
			}else{
				retString = "";
			}
		}
		return retString;
	}

	/**
	 * 调用ActiveX控件盖章
	 * @param dataxml 数据文件流，xml的base64编码
	 * @param modelxml 模板文件流，xml的base64编码
	 * @param modelid 联ID
	 * @param placeid 模板中定义的需要盖章的位置
	 * @param label 签章保存在key里的位置标签
	 * @param esealid 签章的ID(SN)，如果为空表示不校验签章ID
	 * @return 签章成功，返回包含签名结果的Base64数据文件流；签章失败返回空
	 */
	@Override
	public String signEseal(String dataxml, String modelxml, String modelid,
			String placeid, String label, String esealid) {
		String strEseal;
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_signEseal"});//BJCZ_signEseal_ex
		int functionId = functionIds[0];
		//准备传入参数
		Variant[] variant = new Variant[6];
		variant[0] = new Variant(dataxml);
		variant[1] = new Variant(modelxml);
		variant[2] = new Variant(modelid);
		variant[3] = new Variant(placeid);
		variant[4] = new Variant(label);
		variant[5] = new Variant(esealid);
		//调用方法
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) ||(ret.getString() == null) ||(ret.getString() == "")){
			strEseal = "";
		}else{
			strEseal = ret.getString();
		}
		return strEseal;
	}

	/**
	 * 调用ActiveX控件验章
	 * @param xmlData 数据文件流，xml的base64编码
	 * @param xmlModel 模板文件流，xml的base64编码
	 * @param modelid 联ID
	 * @param placeid 模板中定义的需要盖章的位置
	 * @param host 签章服务器地址
	 * @param port 验证服务端口
	 * @return 成功返回空，否则返回错误码和错误说明
	 */
	@Override
	public String verifyFormStamp(String dataxml, String modelxml,
			String modelid, String placeid, String host, String port) {
		String result;
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"RHZK_verifyFormStamp"});//verifyFormStamp
		int functionId = functionIds[0];
		//准备传入参数
		Variant[] variant = new Variant[6];
		variant[0] = new Variant(dataxml);
		variant[1] = new Variant(modelxml);
		variant[2] = new Variant(modelid);
		variant[3] = new Variant(placeid);
		variant[4] = new Variant(host);
		variant[5] = new Variant(port);
		//调用方法
		Variant ret = automation.invoke(functionId, variant);
		if ((ret == null) || (ret.getType() == OLE.VT_EMPTY) ||(ret.getString().charAt(0) != '0')){
			//出现错误
			result = "(" + getErrorCode() + ")" + getErrorMsg();
		}else{
			result = "";
		}
		return result;
	}

	/**
	 * 调用ActiveX控件获得错误码和错误信息
	 * @return 错误说明
	 */
	@Override
	public String getLastError() {
		String error = "(" + getErrorCode() + ")" + getErrorMsg();
		return error;
	}

	/**
	 * 调用ActiveX控件获取表单已盖章位置的列表
	 * @param xmlData 数据文件流，xml的base64编码
	 * @param xmlModel 模板文件流，xml的base64编码
	 * @return 成功返回业务凭证中已盖章的位置列表(用；分割)，失败返回NULL
	 * @deprecated
	 */
	@Override
	public String getFormStampInfo(String xmlData, String xmlModel) {
		String stampInfo;
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"BJCZ_getFormStampInfo"});
		int functionId = functionIds[0];
		//准备传入参数
		Variant[] variant = new Variant[2];
		variant[0] = new Variant(xmlData);
		variant[1] = new Variant(xmlModel);
		//调用方法
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
		//获得方法ID
		int[] functionIds = automation.getIDsOfNames(new String[]{"getDataXMLBase64"});
		int functionId = functionIds[0];
		//定义输入参数
		Variant[] variant = new Variant[0];
		//调用方法
		Variant ret = automation.invoke(functionId,variant);
		if ((ret == null)|| (ret.getType() == OLE.VT_EMPTY) || (ret.getString() == "")){
			//出现错误
			data = "";
		}else{
			data = ret.getString();
		}
		return data;
	}

}

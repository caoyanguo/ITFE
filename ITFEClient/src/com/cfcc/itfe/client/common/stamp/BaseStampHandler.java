/**
 * 
 */
package com.cfcc.itfe.client.common.stamp;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleControlSite;

/**
 * 使用电子印鉴的基础类
 * @author sjz
 * 2009-10-12 19:35:55
 */
public abstract class BaseStampHandler {
	//电子印鉴ActiveX控件的名称
	protected String stampLibName;
	//ole控件对象
	protected OleControlSite controlSite;
	//ole对象方法调用代理
	protected OleAutomation automation;
	
	/**
	 * 调用ActiveX控件显示报表和印鉴
	 * @param dataxml 数据文件流，xml的base64编码
	 * @param modelxml 模板文件流，xml的base64编码
	 * @param modelId 联ID
	 * @param percent 显示百分比，100为正常显示
	 * @param isTrash 是否有效标志，1是有效，0是作废
	 * @return 成功返回空，否则返回错误码和错误说明
	 */
	abstract public String showBills(String dataxml,String modelxml, String modelId, String percent);
	/**
	 * 调用ActiveX控件显示报表和印鉴
	 * @param dataxml 数据文件流，xml的base64编码
	 * @param modelxml 模板文件流，xml的base64编码
	 * @param modelId 联ID
	 * @param pageNo 要显示的页码
	 * @param percent 显示百分比，100为正常显示
	 * @param isTrash 是否有效标志，1是有效，0是作废
	 * @return 成功返回总页数，否则返回错误码和错误说明
	 */
	abstract public String showBill(String dataxml,String modelxml, String modelId, String pageNo,String percent);
	/**
	 * 调用ActiveX控件打印
	 * @param dataxml 数据文件流，xml的base64编码
	 * @param modelxml 模板文件流，xml的base64编码
	 * @param modelId 联ID
	 * @param isReal 是否打印真实凭证，或黑章凭证
	 * @param isTrash 是否有效标志，1是有效，0是作废
	 * @return 成功返回空，否则返回错误码和错误说明
	 */
	abstract public String printBills(String dataxml,String modelxml, String modelId);
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
	abstract public String signEseal(String dataxml, String modelxml, String modelid, String placeid,String label, String esealid);
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
	abstract public String verifyFormStamp(String xmlData, String xmlModel, String modelid, String placeid, String host, String port);
	/**
	 * 调用ActiveX控件对输入字符串进行base64编码
	 * @param srcString 需要进行base64编码的数据
	 * @return 成功返回base64编码后的结果，失败返回空字符串
	 */
	abstract public String base64Encode(String srcString);
	
	/**
	 * 调用ActiveX控件对输入字符串进行base64解码
	 * @param base64String 需要进行base64解码的数据
	 * @return 成功返回base64解码后的结果，失败返回空字符串
	 */
	abstract public String base64Decode(String base64String);
	/**
	 * 调用ActiveX控件获得Key中指定标签的印鉴ID
	 * Key中的每个印鉴都有一个唯一的标签
	 * @param id 标签
	 * @return 印鉴ID
	 */
	abstract public String getEsealIdFromKey(String id);
	/**
	 * 调用ActiveX控件获取表单已盖章位置的列表
	 * @param xmlData 数据文件流，xml的base64编码
	 * @param xmlModel 模板文件流，xml的base64编码
	 * @return 成功返回业务凭证中已盖章的位置列表(用；分割)，失败返回NULL
	 */
	abstract public String getFormStampInfo(String xmlData, String xmlModel);
	/**
	 * 调用ActiveX控件获得错误返回信息
	 * @return 错误说明
	 */
	abstract public String getErrorMsg();
	/**
	 * 调用ActiveX控件获得错误码
	 * @return 错误码
	 */
	abstract public int getErrorCode();
	/**
	 * 调用ActiveX控件获得错误码和错误信息
	 * @return 错误说明
	 */
	abstract public String getLastError();
	/**
	 * 调用ActiveX控件获得数据信息
	 * @return 错误说明
	 */
	abstract public String getDataXMLBase64();
}

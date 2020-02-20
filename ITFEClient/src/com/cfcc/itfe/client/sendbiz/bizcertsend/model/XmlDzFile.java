/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.bizcertsend.model;

import com.cfcc.itfe.client.common.file.XmlFileBaseObject;

/**
 * 记录对帐Xml文件内容的Java对象
 * @author sjz
 *
 */
public class XmlDzFile extends XmlFileBaseObject {
	private static final long serialVersionUID = 7463140314196673119L;
	//SEND_CODE	发送方代码
	private String sendCode;
	//SEND_NAME	发送方名称
	private String sendName;
	//PAY_CODE	支付方式代码
	private String payCode;
	//PAY_DESC	支付方式说明
	private String payDesc;
	//MONTH	对帐月度
	private String month;
	//OPER_NAME	操作员名称
	private String operName;
	//IDLE	预留
	private String idle;

	public XmlDzFile(){
		details = null;
	}
	
	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getPayDesc() {
		return payDesc;
	}

	public void setPayDesc(String payDesc) {
		this.payDesc = payDesc;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public String getIdle() {
		return idle;
	}

	public void setIdle(String idle) {
		this.idle = idle;
	}

	/**
	 * 判断两个对象是否相同的方法
	 * 将传入对象与本对象进行比较
	 * @param o 要比较的对象
	 * @return true-两个对象相同，false-两个对象不同
	 */
	public boolean equals(Object o) {
		if (!(o instanceof XmlDzFile))
			return false;
		XmlDzFile other = (XmlDzFile)o;
		if (other.getSendCode().equals(getSendCode())&&
				other.getPayCode().equals(getPayCode())&&
				other.getMonth().equals(getMonth()))
			return true;
		else
			return false;
	}

	/**
	 * 生成HashCode
	 * @return hashCode
	 */
	public int hashCode() {
		return (sendCode + sendName + payCode + payDesc + month).hashCode();
	}

	/**
	 * 生成字符串的方法
	 * @return 返回一个字符串
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer().append(sendName).append("(").append(sendCode).append(")")
			.append(month + "月对账文件");
		return buf.toString();
	}

}

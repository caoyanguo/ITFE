/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.bizcertsend.model;

import com.cfcc.itfe.client.common.file.XmlFileBaseObject;

/**
 * ��¼����Xml�ļ����ݵ�Java����
 * @author sjz
 *
 */
public class XmlDzFile extends XmlFileBaseObject {
	private static final long serialVersionUID = 7463140314196673119L;
	//SEND_CODE	���ͷ�����
	private String sendCode;
	//SEND_NAME	���ͷ�����
	private String sendName;
	//PAY_CODE	֧����ʽ����
	private String payCode;
	//PAY_DESC	֧����ʽ˵��
	private String payDesc;
	//MONTH	�����¶�
	private String month;
	//OPER_NAME	����Ա����
	private String operName;
	//IDLE	Ԥ��
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
	 * �ж����������Ƿ���ͬ�ķ���
	 * ����������뱾������бȽ�
	 * @param o Ҫ�ȽϵĶ���
	 * @return true-����������ͬ��false-��������ͬ
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
	 * ����HashCode
	 * @return hashCode
	 */
	public int hashCode() {
		return (sendCode + sendName + payCode + payDesc + month).hashCode();
	}

	/**
	 * �����ַ����ķ���
	 * @return ����һ���ַ���
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer().append(sendName).append("(").append(sendCode).append(")")
			.append(month + "�¶����ļ�");
		return buf.toString();
	}

}

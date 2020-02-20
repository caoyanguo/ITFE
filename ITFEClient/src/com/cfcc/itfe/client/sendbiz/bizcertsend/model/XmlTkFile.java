/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.bizcertsend.model;

import com.cfcc.itfe.client.common.file.XmlFileBaseObject;

/**
 * ����˿�Xml�ļ����ݵ�Java����
 * @author sjz
 *
 */
public class XmlTkFile extends XmlFileBaseObject{
	private static final long serialVersionUID = 5435091591826342507L;
	//SEND_NAME	���ͷ�����
	private String sendName;
	//PAY_CODE	֧����ʽ����
	private String payCode;
	//PAY_DESC	֧����ʽ˵��
	private String payDesc;
	//VOU_DATE	����
	private String vouDate;
	//VOU_NUM	ƾ֤���
	private String vouNum;
	//PAYEE_CODE	�����˴���
	private String payeeCode;
	//PAYOR_NAME	������ȫ��
	private String payorName;
	//PAYOR_ACC	�������ʺ�
	private String payorAcc;
	//PAYOR_BANK	�����˿�����
	private String payorBank;
	//PAYEE_NAME	�տ���ȫ��
	private String payeeName;
	//PAYEE_ACC	�տ����ʺ�
	private String payeeAcc;
	//PAYEE_BANK	�տ��˿�����
	private String payeeBank;
	//SUM_MONEY	���ܽ��
	private String sumMoney;
	//OPER_NAME	����Ա����
	private String operName;
	//SUMMARY	ժҪ
	private String summary;
	//REMARK	��ע
	private String remark;
	//IDLE	Ԥ��
	private String idle;

	public XmlTkFile(){
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

	public String getVouDate() {
		return vouDate;
	}

	public void setVouDate(String vouDate) {
		this.vouDate = vouDate;
	}

	public String getVouNum() {
		return vouNum;
	}

	public void setVouNum(String vouNum) {
		this.vouNum = vouNum;
	}

	public String getPayeeCode() {
		return payeeCode;
	}

	public void setPayeeCode(String payeeCode) {
		this.payeeCode = payeeCode;
	}

	public String getPayorName() {
		return payorName;
	}

	public void setPayorName(String payorName) {
		this.payorName = payorName;
	}

	public String getPayorAcc() {
		return payorAcc;
	}

	public void setPayorAcc(String payorAcc) {
		this.payorAcc = payorAcc;
	}

	public String getPayorBank() {
		return payorBank;
	}

	public void setPayorBank(String payorBank) {
		this.payorBank = payorBank;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public String getPayeeAcc() {
		return payeeAcc;
	}

	public void setPayeeAcc(String payeeAcc) {
		this.payeeAcc = payeeAcc;
	}

	public String getPayeeBank() {
		return payeeBank;
	}

	public void setPayeeBank(String payeeBank) {
		this.payeeBank = payeeBank;
	}

	public String getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(String sumMoney) {
		this.sumMoney = sumMoney;
	}

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
		if (!(o instanceof XmlTkFile))
			return false;
		XmlTkFile other = (XmlTkFile)o;
		if (other.getPayeeCode().equals(getPayeeCode())&&
				other.getPayCode().equals(getPayCode())&&
				other.getVouDate().equals(getVouDate())&&
				other.getVouNum().equals(getVouNum()))
			return true;
		else
			return false;
	}

	/**
	 * ����HashCode
	 * @return hashCode
	 */
	public int hashCode() {
		return (payeeCode + payCode + vouDate + vouNum).hashCode();
	}

	/**
	 * �����ַ����ķ���
	 * @return ����һ���ַ���
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer().append(sendName).append("(").append(payeeCode).append(")")
			.append("��" + vouNum + "���˿�ƾ֤�����ڣ�").append(vouDate).append("�����").append(sumMoney.toString());
		return buf.toString();
	}

}

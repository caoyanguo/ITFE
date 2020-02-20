/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.bizcertsend.model;

import com.cfcc.itfe.client.common.file.XmlFileBaseObject;

/**
 * 存放退库Xml文件内容的Java对象
 * @author sjz
 *
 */
public class XmlTkFile extends XmlFileBaseObject{
	private static final long serialVersionUID = 5435091591826342507L;
	//SEND_NAME	发送方名称
	private String sendName;
	//PAY_CODE	支付方式代码
	private String payCode;
	//PAY_DESC	支付方式说明
	private String payDesc;
	//VOU_DATE	日期
	private String vouDate;
	//VOU_NUM	凭证编号
	private String vouNum;
	//PAYEE_CODE	付款人代码
	private String payeeCode;
	//PAYOR_NAME	付款人全称
	private String payorName;
	//PAYOR_ACC	付款人帐号
	private String payorAcc;
	//PAYOR_BANK	付款人开户行
	private String payorBank;
	//PAYEE_NAME	收款人全称
	private String payeeName;
	//PAYEE_ACC	收款人帐号
	private String payeeAcc;
	//PAYEE_BANK	收款人开户行
	private String payeeBank;
	//SUM_MONEY	汇总金额
	private String sumMoney;
	//OPER_NAME	操作员名称
	private String operName;
	//SUMMARY	摘要
	private String summary;
	//REMARK	备注
	private String remark;
	//IDLE	预留
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
	 * 判断两个对象是否相同的方法
	 * 将传入对象与本对象进行比较
	 * @param o 要比较的对象
	 * @return true-两个对象相同，false-两个对象不同
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
	 * 生成HashCode
	 * @return hashCode
	 */
	public int hashCode() {
		return (payeeCode + payCode + vouDate + vouNum).hashCode();
	}

	/**
	 * 生成字符串的方法
	 * @return 返回一个字符串
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer().append(sendName).append("(").append(payeeCode).append(")")
			.append("第" + vouNum + "号退款凭证，日期：").append(vouDate).append("，金额").append(sumMoney.toString());
		return buf.toString();
	}

}

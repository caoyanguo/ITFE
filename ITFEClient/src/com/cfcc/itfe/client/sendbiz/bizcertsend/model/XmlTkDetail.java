/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.bizcertsend.model;

import com.cfcc.itfe.client.common.file.XmlFileBaseObject;

/**
 * ��¼�˿���ϸ��Ϣ����
 * @author sjz
 *
 */
public class XmlTkDetail extends XmlFileBaseObject{
	private static final long serialVersionUID = 5333944910771423717L;
	//Ԥ�㵥λ����
	private String unitCode;
	//Ԥ�㵥λ����
	private String unitName;
	//Ԥ���Ŀ����
	private String funcCode;
	//���
	private String money;
	//�ʽ���
	private String inType;
	//���ÿ�Ŀ
	private String encCode;
	
	public String getUnitCode() {
		return unitCode;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getFuncCode() {
		return funcCode;
	}
	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getInType() {
		return inType;
	}
	public void setInType(String inType) {
		this.inType = inType;
	}
	public String getEncCode() {
		return encCode;
	}
	public void setEncCode(String encCode) {
		this.encCode = encCode;
	}

	/**
	 * �ж����������Ƿ���ͬ�ķ���
	 * ����������뱾������бȽ�
	 * @param o Ҫ�ȽϵĶ���
	 * @return true-����������ͬ��false-��������ͬ
	 */
	public boolean equals(Object o) {
		if (!(o instanceof XmlTkDetail))
			return false;
		XmlTkDetail other = (XmlTkDetail)o;
		if (other.getUnitCode().equals(getUnitCode())&&
				other.getUnitName().equals(getUnitName())&&
				other.getFuncCode().equals(getFuncCode())&&
				other.getInType().equals(getInType())&&
				other.getMoney().compareTo(getMoney())==0)
			return true;
		else
			return false;
	}

	/**
	 * ����HashCode
	 * @return hashCode
	 */
	public int hashCode() {
		return (unitCode + unitName + funcCode + inType + money.toString()).hashCode();
	}

	/**
	 * �����ַ����ķ���
	 * @return ����һ���ַ���
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer().append(unitName).append("(").append(unitCode).append(")")
			.append("����Ŀ" + funcCode).append("����֧��������" + inType).append("�����").append(money.toString());
		return buf.toString();
	}
}

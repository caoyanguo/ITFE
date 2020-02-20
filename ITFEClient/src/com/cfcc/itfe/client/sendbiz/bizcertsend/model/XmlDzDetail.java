/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.bizcertsend.model;

import com.cfcc.itfe.client.common.file.XmlFileBaseObject;

/**
 * ��¼����Xml��ϸ��Ϣ��Java����
 * @author sjz
 *
 */
public class XmlDzDetail extends XmlFileBaseObject {
	private static final long serialVersionUID = 7687922442270033103L;
	//Ԥ�㵥λ����
	private String unitCode;
	//Ԥ�㵥λ����
	private String unitName;
	//Ԥ���Ŀ����
	private String funcCode;
	//��Ƚ��
	private String edMoney;
	//�˿���
	private String tkMoney;
	//������
	private String qsMoney;
	//���
	private String reMoney;
	//�ʽ���
	private String inType;
	
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

	public String getEdMoney() {
		return edMoney;
	}

	public void setEdMoney(String edMoney) {
		this.edMoney = edMoney;
	}

	public String getTkMoney() {
		return tkMoney;
	}

	public void setTkMoney(String tkMoney) {
		this.tkMoney = tkMoney;
	}

	public String getQsMoney() {
		return qsMoney;
	}

	public void setQsMoney(String qsMoney) {
		this.qsMoney = qsMoney;
	}

	public String getReMoney() {
		return reMoney;
	}

	public void setReMoney(String reMoney) {
		this.reMoney = reMoney;
	}

	public String getInType() {
		return inType;
	}

	public void setInType(String inType) {
		this.inType = inType;
	}

	/**
	 * �ж����������Ƿ���ͬ�ķ���
	 * ����������뱾������бȽ�
	 * @param o Ҫ�ȽϵĶ���
	 * @return true-����������ͬ��false-��������ͬ
	 */
	public boolean equals(Object o) {
		if (!(o instanceof XmlDzDetail))
			return false;
		XmlDzDetail other = (XmlDzDetail)o;
		if (other.getUnitCode().equals(getUnitCode())&&
				other.getUnitName().equals(getUnitName())&&
				other.getFuncCode().equals(getFuncCode())&&
				other.getInType().equals(getInType()))
			return true;
		else
			return false;
	}

	/**
	 * ����HashCode
	 * @return hashCode
	 */
	public int hashCode() {
		return (unitCode + unitName + funcCode + inType + edMoney.toString() + tkMoney.toString() + qsMoney.toString() + reMoney.toString()).hashCode();
	}

	/**
	 * �����ַ����ķ���
	 * @return ����һ���ַ���
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer().append(unitName).append("(").append(unitCode).append(")")
			.append("����Ŀ" + funcCode).append("����֧��������" + inType).append("����Ƚ��").append(edMoney.toString())
			.append("���˿���").append(tkMoney.toString()).append("��������").append(qsMoney.toString())
			.append("�����").append(reMoney.toString());
		return buf.toString();
	}
}

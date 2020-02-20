/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.bizcertsend.model;

import com.cfcc.itfe.client.common.file.XmlFileBaseObject;

/**
 * 记录对帐Xml明细信息的Java对象
 * @author sjz
 *
 */
public class XmlDzDetail extends XmlFileBaseObject {
	private static final long serialVersionUID = 7687922442270033103L;
	//预算单位代码
	private String unitCode;
	//预算单位名称
	private String unitName;
	//预算科目代码
	private String funcCode;
	//额度金额
	private String edMoney;
	//退款金额
	private String tkMoney;
	//清算金额
	private String qsMoney;
	//余额
	private String reMoney;
	//资金方向
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
	 * 判断两个对象是否相同的方法
	 * 将传入对象与本对象进行比较
	 * @param o 要比较的对象
	 * @return true-两个对象相同，false-两个对象不同
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
	 * 生成HashCode
	 * @return hashCode
	 */
	public int hashCode() {
		return (unitCode + unitName + funcCode + inType + edMoney.toString() + tkMoney.toString() + qsMoney.toString() + reMoney.toString()).hashCode();
	}

	/**
	 * 生成字符串的方法
	 * @return 返回一个字符串
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer().append(unitName).append("(").append(unitCode).append(")")
			.append("，科目" + funcCode).append("，收支管理类型" + inType).append("，额度金额").append(edMoney.toString())
			.append("，退款金额").append(tkMoney.toString()).append("，清算金额").append(qsMoney.toString())
			.append("，余额").append(reMoney.toString());
		return buf.toString();
	}
}

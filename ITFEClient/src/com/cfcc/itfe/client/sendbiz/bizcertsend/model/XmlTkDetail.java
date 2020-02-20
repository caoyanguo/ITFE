/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.bizcertsend.model;

import com.cfcc.itfe.client.common.file.XmlFileBaseObject;

/**
 * 记录退款明细信息的类
 * @author sjz
 *
 */
public class XmlTkDetail extends XmlFileBaseObject{
	private static final long serialVersionUID = 5333944910771423717L;
	//预算单位代码
	private String unitCode;
	//预算单位名称
	private String unitName;
	//预算科目代码
	private String funcCode;
	//金额
	private String money;
	//资金方向
	private String inType;
	//经济科目
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
	 * 判断两个对象是否相同的方法
	 * 将传入对象与本对象进行比较
	 * @param o 要比较的对象
	 * @return true-两个对象相同，false-两个对象不同
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
	 * 生成HashCode
	 * @return hashCode
	 */
	public int hashCode() {
		return (unitCode + unitName + funcCode + inType + money.toString()).hashCode();
	}

	/**
	 * 生成字符串的方法
	 * @return 返回一个字符串
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer().append(unitName).append("(").append(unitCode).append(")")
			.append("，科目" + funcCode).append("，收支管理类型" + inType).append("，金额").append(money.toString());
		return buf.toString();
	}
}

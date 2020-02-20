package com.cfcc.itfe.client.common.file;

import java.io.Serializable;
import java.util.List;

/**
 * xml文件解析成Java对象的父对象
 * 在客户端解析Xml时，会把xml文件中的内容保存到Java对象中，该Java对象必须继承本类
 * @author sjz
 *
 */
public abstract class XmlFileBaseObject implements Serializable {
	private static final long serialVersionUID = -5023986404395412076L;
	//记录明细信息的集合
	protected List<XmlFileBaseObject> details;
	
	/**
	 * 判断两个对象是否相同的方法
	 * 将传入对象与本对象进行比较
	 * @param o 要比较的对象
	 * @return true-两个对象相同，false-两个对象不同
	 */
	public abstract boolean equals(Object o);
	/**
	 * 生成字符串的方法
	 * @return 返回一个字符串
	 */
	public abstract String toString();
	/**
	 * 生成HashCode
	 * @return hashCode
	 */
	public abstract int hashCode();
	
	public List<XmlFileBaseObject> getDetails() {
		return details;
	}
	public void setDetails(List<XmlFileBaseObject> details) {
		this.details = details;
	}

}

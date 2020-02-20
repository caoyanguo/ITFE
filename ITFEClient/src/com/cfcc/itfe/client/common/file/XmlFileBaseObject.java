package com.cfcc.itfe.client.common.file;

import java.io.Serializable;
import java.util.List;

/**
 * xml�ļ�������Java����ĸ�����
 * �ڿͻ��˽���Xmlʱ�����xml�ļ��е����ݱ��浽Java�����У���Java�������̳б���
 * @author sjz
 *
 */
public abstract class XmlFileBaseObject implements Serializable {
	private static final long serialVersionUID = -5023986404395412076L;
	//��¼��ϸ��Ϣ�ļ���
	protected List<XmlFileBaseObject> details;
	
	/**
	 * �ж����������Ƿ���ͬ�ķ���
	 * ����������뱾������бȽ�
	 * @param o Ҫ�ȽϵĶ���
	 * @return true-����������ͬ��false-��������ͬ
	 */
	public abstract boolean equals(Object o);
	/**
	 * �����ַ����ķ���
	 * @return ����һ���ַ���
	 */
	public abstract String toString();
	/**
	 * ����HashCode
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

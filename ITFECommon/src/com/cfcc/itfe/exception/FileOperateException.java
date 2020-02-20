/*
 * �������� 2005-8-3
 *
 * �����������ļ�ģ��Ϊ
 * ���� > ��ѡ�� > Java > �������� > �����ע��
 */
package com.cfcc.itfe.exception;

import com.cfcc.jaf.common.exception.JAFNestedCheckedException;
import com.cfcc.itfe.constant.ClassDescribe;

/**
 * @author ������
 * 
 *         �����ļ������쳣
 */
public class FileOperateException extends JAFNestedCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5372841436982260320L;

	/**
	 * @param message
	 */
	public FileOperateException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FileOperateException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getDescription() {
		return ClassDescribe.FileOperateException_Describe;
	}

	public FileOperateException(Throwable cause) {
		super(ClassDescribe.FileOperateException_Describe, cause);
	}
}

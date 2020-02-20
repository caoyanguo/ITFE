/**
 *
 */
package com.cfcc.itfe.exception;

import java.util.Collection;
import java.util.Iterator;

import com.cfcc.jaf.common.exception.JAFNestedCheckedException;
import com.cfcc.itfe.constant.ClassDescribe;

public class CheckException extends JAFNestedCheckedException {

	/**
	 *
	 */
	private static final long serialVersionUID = -8484507690015203029L;

	/**
	 * @param arg0
	 */
	public CheckException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CheckException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param cause
	 */
	public CheckException(Throwable cause) {
		super(ClassDescribe.CheckException_Describe, cause);
	}

	// ��ȡУ����� Exception
	public static CheckException valueOf(Collection verifyResult) {
		StringBuffer sb = new StringBuffer();
		sb.append("����У�����\n");

		Iterator it = verifyResult.iterator();
		while (it.hasNext()) {
			sb.append("\t" + it.next().toString() + "\n");
		}
		return new CheckException(sb.toString());

	}

	// ��ȡ���˴�����Ϣ���� Exception
	public static CheckException errChkAcctInfo(Collection errResult) {
		StringBuffer sb = new StringBuffer();
		// sb.append("����У�����\n");

		Iterator it = errResult.iterator();
		while (it.hasNext()) {
			sb.append("\t" + it.next().toString() + "\n");
		}
		return new CheckException(sb.toString());

	}

	/*
	 * ���� Javadoc��
	 * 
	 * @see
	 * com.cfcc.jaf.common.exception.JAFNestedCheckedException#getDescription()
	 */
	public String getDescription() {

		return ClassDescribe.CheckException_Describe;
	}

}

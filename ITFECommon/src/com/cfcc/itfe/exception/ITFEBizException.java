/*
 * �������� 2005-8-31
 *
 * �����������ļ�ģ��Ϊ
 * ���� > ��ѡ�� > Java > �������� > �����ע��
 */
package com.cfcc.itfe.exception;

import com.cfcc.itfe.constant.ClassDescribe;
import com.cfcc.jaf.common.exception.JAFNestedCheckedException;

import java.util.*;

/**
 * ҵ���쳣,Service�����׳����쳣
 */
public class ITFEBizException extends JAFNestedCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5132439471270846219L;

	/**
	 * @param arg0
	 */
	public ITFEBizException(String arg0) 
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ITFEBizException(String arg0, Throwable arg1) {
		super(arg0, arg1);	
	}
	/**
	 * @param cause
	 */
	public ITFEBizException(Throwable cause)
	{
		super(ClassDescribe.ITFEBizException_Describe ,cause);
	}
	
	
	// ��ȡУ����� Exception
	public static ITFEBizException valueOf(Collection verifyResult)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("����У�����\n");

		Iterator it = verifyResult.iterator();
		while (it.hasNext()) {
			sb.append("\t" + it.next().toString() + "\n");
		}
		return new ITFEBizException(sb.toString());

	}
	// ��ȡ���˴�����Ϣ���� Exception
	public static ITFEBizException errChkAcctInfo(Collection errResult)
	{
		StringBuffer sb = new StringBuffer();
		//sb.append("����У�����\n");

		Iterator it = errResult.iterator();
		while (it.hasNext()) {
			sb.append("\t" + it.next().toString() + "\n");
		}
		return new ITFEBizException(sb.toString());

	}
	
	/* ���� Javadoc��
	 * @see com.cfcc.jaf.common.exception.JAFNestedCheckedException#getDescription()
	 */
	public String getDescription() {
	
		return ClassDescribe.ITFEBizException_Describe;
	}

}

/*
 * �������� 2005-7-12
 *
 * �����������ļ�ģ��Ϊ
 * ���� > ��ѡ�� > Java > �������� > �����ע��
 */
package com.cfcc.itfe.exception;

import com.cfcc.jaf.common.exception.JAFNestedCheckedException;
import com.cfcc.itfe.constant.ClassDescribe;


/**
 * ��ȡSequence�����׳����쳣
 * @author ������
 */
public class SequenceException extends JAFNestedCheckedException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4648536290191763854L;

	public String getDescription()
	{
		return ClassDescribe.SequenceException_Describe;
	}
	
	public SequenceException(String message)
	{
		super(message);
	}
	
	public SequenceException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public SequenceException(Throwable cause)
	{
		super(ClassDescribe.SequenceException_Describe,cause);
	}
	
}

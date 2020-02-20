/*
 * 创建日期 2005-7-12
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.cfcc.itfe.exception;

import com.cfcc.jaf.common.exception.JAFNestedCheckedException;
import com.cfcc.itfe.constant.ClassDescribe;


/**
 * 获取Sequence出错抛出此异常
 * @author 赵新鹏
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

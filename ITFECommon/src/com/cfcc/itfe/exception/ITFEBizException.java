/*
 * 创建日期 2005-8-31
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.cfcc.itfe.exception;

import com.cfcc.itfe.constant.ClassDescribe;
import com.cfcc.jaf.common.exception.JAFNestedCheckedException;

import java.util.*;

/**
 * 业务异常,Service出错抛出此异常
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
	
	
	// 获取校验错误 Exception
	public static ITFEBizException valueOf(Collection verifyResult)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("数据校验错误：\n");

		Iterator it = verifyResult.iterator();
		while (it.hasNext()) {
			sb.append("\t" + it.next().toString() + "\n");
		}
		return new ITFEBizException(sb.toString());

	}
	// 获取对账错误信息描述 Exception
	public static ITFEBizException errChkAcctInfo(Collection errResult)
	{
		StringBuffer sb = new StringBuffer();
		//sb.append("数据校验错误：\n");

		Iterator it = errResult.iterator();
		while (it.hasNext()) {
			sb.append("\t" + it.next().toString() + "\n");
		}
		return new ITFEBizException(sb.toString());

	}
	
	/* （非 Javadoc）
	 * @see com.cfcc.jaf.common.exception.JAFNestedCheckedException#getDescription()
	 */
	public String getDescription() {
	
		return ClassDescribe.ITFEBizException_Describe;
	}

}

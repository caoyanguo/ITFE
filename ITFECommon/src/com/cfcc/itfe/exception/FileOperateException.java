/*
 * 创建日期 2005-8-3
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.cfcc.itfe.exception;

import com.cfcc.jaf.common.exception.JAFNestedCheckedException;
import com.cfcc.itfe.constant.ClassDescribe;

/**
 * @author 赵新鹏
 * 
 *         操作文件发生异常
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

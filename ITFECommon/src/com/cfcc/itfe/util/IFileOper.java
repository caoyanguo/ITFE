package com.cfcc.itfe.util;

import java.io.FileNotFoundException;
import java.io.Serializable;

import com.cfcc.itfe.exception.FileOperateException;

public interface IFileOper {
	public void deleteFile(String name) throws FileOperateException,
			FileNotFoundException;
	public void deleteFiles(String name) throws FileOperateException,
	FileNotFoundException;
	public Serializable deserialize(String fileName)
	throws FileOperateException ;
}

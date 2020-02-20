package com.cfcc.itfe.service.recbiz.voucherattachservice;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IVoucherAttachServiceService extends IService {



	/**
	 * 查找文件名称(带路径)
	 	 
	 * @generated
	 * @param filePath
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getFileList(String filePath) throws ITFEBizException;

}
package com.cfcc.itfe.service.dataquery.tipsdataexport;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface ITipsDataExportService extends IService {



	/**
	 * 生成导出Tips文件
	 	 
	 * @generated
	 * @param list
	 * @param dto
	 * @param str
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map generateTipsToFile(List list, IDto dto, String str) throws ITFEBizException;

	/**
	 * 删除服务器文件
	 	 
	 * @generated
	 * @param filelist
	 * @throws ITFEBizException	 
	 */
   public abstract void deleteTheFiles(List filelist) throws ITFEBizException;

}
package com.cfcc.itfe.service.recbiz.agentbankcountanalysedataimport;

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
 * @time   19-12-08 13:00:37
 * @generated
 * codecomment: 
 */
public interface IAgentBankCountAnalyseDataImportService extends IService {



	/**
	 * 数据导入
	 	 
	 * @generated
	 * @param dataList
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List dataImport(List dataList) throws ITFEBizException;

	/**
	 * 数据发送
	 	 
	 * @generated
	 * @param senddataInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String sendData(List senddataInfo) throws ITFEBizException;

}
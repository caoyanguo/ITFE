package com.cfcc.itfe.service.dataquery.checkstatusofreportdownload;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.page.PageRequest;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface ICheckStatusOfReportDownloadService extends IService {



	/**
	 * 查询下载报表情况	 
	 * @generated
	 * @param searchdate
	 * @param strecode
	 * @param request
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse searchStatusOfReportDownLoad(Date searchdate, String strecode, PageRequest request) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param exportdate
	 * @throws ITFEBizException	 
	 */
   public abstract void exportToServer(String exportdate) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param exportdate
	 * @param busType
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportBusData(String exportdate, String busType) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param exportdate
	 * @param busType
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map downloadbus(String exportdate, String busType) throws ITFEBizException;

}
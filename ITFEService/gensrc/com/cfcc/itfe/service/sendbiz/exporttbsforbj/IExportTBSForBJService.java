package com.cfcc.itfe.service.sendbiz.exporttbsforbj;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.facade.data.MulitTableDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * codecomment: 
 */
public interface IExportTBSForBJService extends IService {



	/**
	 * 导出TBS格式文件
	 	 
	 * @generated
	 * @param bizType
	 * @param treCode
	 * @param acctDate
	 * @param trimFlag
	 * @param backParam
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List exportTBS(String bizType, String treCode, Date acctDate, String trimFlag, Object backParam) throws ITFEBizException;

	/**
	 * 根据主表信息查找字表
	 	 
	 * @generated
	 * @param mainDto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findSubInfoByMain(IDto mainDto) throws ITFEBizException;

	/**
	 * 导入TBS回执
	 	 
	 * @generated
	 * @param fileList
	 * @param param
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto importTBS(List fileList, Object param) throws ITFEBizException;

	/**
	 * 更新凭证状态
	 	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
   public abstract void updateVouStatus(List list) throws ITFEBizException;

	/**
	 * 删除服务器文件
	 	 
	 * @generated
	 * @param fileName
	 * @throws ITFEBizException	 
	 */
   public abstract void deleteServerFile(String fileName) throws ITFEBizException;

	/**
	 * 记录导入导出TBS文件日志
	 	 
	 * @generated
	 * @param logList
	 * @throws ITFEBizException	 
	 */
   public abstract void writeTbsFileLog(List logList) throws ITFEBizException;

	/**
	 * 定时任务数据获取
	 	 
	 * @generated
	 * @param startDate
	 * @param endDate
	 * @param paramDto
	 * @param backParamString
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map fetchVoucherInfoForClientTimer(String startDate, String endDate, IDto paramDto, String backParamString) throws ITFEBizException;

}
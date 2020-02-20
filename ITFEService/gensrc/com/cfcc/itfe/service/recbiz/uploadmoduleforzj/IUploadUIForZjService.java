package com.cfcc.itfe.service.recbiz.uploadmoduleforzj;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileResultDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IUploadUIForZjService extends IService {



	/**
	 * 加载数据
	 	 
	 * @generated
	 * @param fileResultDto
	 * @throws ITFEBizException	 
	 */
   public abstract void UploadDate(FileResultDto fileResultDto) throws ITFEBizException;

	/**
	 * 查询资金收纳流水信息
	 	 
	 * @generated
	 * @param strasrlno
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List searchByTraSrlNo(String strasrlno) throws ITFEBizException;

	/**
	 * 按照资金收纳流水号删除信息
	 	 
	 * @generated
	 * @param strasrlno
	 * @throws ITFEBizException	 
	 */
   public abstract void delByTraSrlNo(String strasrlno) throws ITFEBizException;

	/**
	 * 按照资金收纳流水号提交信息
	 	 
	 * @generated
	 * @param strasrlno
	 * @param fileList
	 * @throws ITFEBizException	 
	 */
   public abstract void commitByTraSrlNo(String strasrlno, List fileList) throws ITFEBizException;

}
package com.cfcc.itfe.service.commonsubsys.commondbaccess;

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
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * codecomment: 
 */
public interface IUploadConfirmService extends IService {



	/**
	 * batchQuery
	 	 
	 * @generated
	 * @param bizType
	 * @param checkMoney
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List batchQuery(String bizType, BigDecimal checkMoney) throws ITFEBizException;

	/**
	 * batchConfirm
	 	 
	 * @generated
	 * @param bizType
	 * @param idto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer batchConfirm(String bizType, IDto idto) throws ITFEBizException;

	/**
	 * batchDelete
	 	 
	 * @generated
	 * @param bizType
	 * @param idto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer batchDelete(String bizType, IDto idto) throws ITFEBizException;

	/**
	 * eachQuery
	 	 
	 * @generated
	 * @param bizType
	 * @param idto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List eachQuery(String bizType, IDto idto) throws ITFEBizException;

	/**
	 * eachConfirm
	 	 
	 * @generated
	 * @param bizType
	 * @param idto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer eachConfirm(String bizType, IDto idto) throws ITFEBizException;

	/**
	 * directSubmit
	 	 
	 * @generated
	 * @param bizType
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer directSubmit(String bizType) throws ITFEBizException;

	/**
	 * 判断文件名下是否有未销的的记录，如果没有就发送报文
	 	 
	 * @generated
	 * @param idto
	 * @param msgno
	 * @param tablename
	 * @param vousrlno
	 * @throws ITFEBizException	 
	 */
   public abstract void checkAndSendMsg(IDto idto, String msgno, String tablename, Long vousrlno) throws ITFEBizException;

	/**
	 * eachDelete
	 	 
	 * @generated
	 * @param bizType
	 * @param idto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer eachDelete(String bizType, IDto idto) throws ITFEBizException;

	/**
	 * setFail
	 	 
	 * @generated
	 * @param bizType
	 * @param idto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer setFail(String bizType, IDto idto) throws ITFEBizException;

	/**
	 * applyBackDirectSubmit
	 	 
	 * @generated
	 * @param bizType
	 * @param idto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer applyBackDirectSubmit(String bizType, IDto idto) throws ITFEBizException;

	/**
	 * applyBackBatchConfirm
	 	 
	 * @generated
	 * @param bizType
	 * @param idto
	 * @param idto2
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer applyBackBatchConfirm(String bizType, IDto idto, IDto idto2) throws ITFEBizException;

	/**
	 * applyBackEachConfirm
	 	 
	 * @generated
	 * @param bizType
	 * @param idto
	 * @param idto2
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer applyBackEachConfirm(String bizType, IDto idto, IDto idto2) throws ITFEBizException;

	/**
	 * 返回直接支付授权支付文件列表
	 	 
	 * @generated
	 * @param sfilepath
	 * @param sbiztype
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getDirectGrantFileList(String sfilepath, String sbiztype) throws ITFEBizException;

	/**
	 * 加载直接支付授权支付文件
	 	 
	 * @generated
	 * @param filedir
	 * @param filenamelist
	 * @throws ITFEBizException	 
	 */
   public abstract void addDirectGrantFile(String filedir, List filenamelist) throws ITFEBizException;

	/**
	 * 删除错误文件
	 	 
	 * @generated
	 * @param mainfiledir
	 * @param deleteFileList
	 * @throws ITFEBizException	 
	 */
   public abstract void deleteDirectGrantErorFile(String mainfiledir, List deleteFileList) throws ITFEBizException;

	/**
	 * 删除文件
	 	 
	 * @generated
	 * @param delfilelist
	 * @param deletetype
	 * @throws ITFEBizException	 
	 */
   public abstract void delfilelist(List delfilelist, String deletetype) throws ITFEBizException;

	/**
	 * ftp文件加载
	 	 
	 * @generated
	 * @param selectAddList
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List ftpfileadd(List selectAddList) throws ITFEBizException;

	/**
	 * 读取ftp文件
	 	 
	 * @generated
	 * @param sDate
	 * @throws ITFEBizException	 
	 */
   public abstract void readFtpFile(String sDate) throws ITFEBizException;

	/**
	 * 服务器端验签转移文件
	 	 
	 * @generated
	 * @param filenamepath
	 * @param tempfilenamepath
	 * @param privatekey
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map checkSignFileForSd(String filenamepath, String tempfilenamepath, String privatekey) throws ITFEBizException;

	/**
	 * ftp批量加载后更新状态
	 	 
	 * @generated
	 * @param idtoList
	 * @throws ITFEBizException	 
	 */
   public abstract void updateIdtoList(List idtoList) throws ITFEBizException;

	/**
	 * ftp查询可回执的文件
	 	 
	 * @generated
	 * @param queryDto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List queryFtpReturnFileList(TvBatchpayDto queryDto) throws ITFEBizException;

	/**
	 * 发送回执到ftp
	 	 
	 * @generated
	 * @param sendFileList
	 * @throws ITFEBizException	 
	 */
   public abstract void sendReturnToFtp(List sendFileList) throws ITFEBizException;

}
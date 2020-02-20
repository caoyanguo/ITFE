package com.cfcc.itfe.service.dataquery.querylogs;

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
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IQueryLogsService extends IService {



	/**
	 * 查询
	 	 
	 * @generated
	 * @param dto
	 * @param pagerequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse queryLog(IDto dto, PageRequest pagerequest) throws ITFEBizException;

	/**
	 * 结果界面
	 	 
	 * @generated
	 * @throws ITFEBizException	 
	 */
   public abstract void result() throws ITFEBizException;

	/**
	 * 查询附件信息	 
	 * @generated
	 * @param sendNo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List queryAttach(String sendNo) throws ITFEBizException;

	/**
	 * 查询发送凭证的详细内容	 
	 * @generated
	 * @param sendNo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String queryContent(String sendNo) throws ITFEBizException;

	/**
	 * 作废自己发送的报文	 
	 * @generated
	 * @param sendLog
	 * @throws ITFEBizException	 
	 */
   public abstract void cancelSend(TvRecvLogShowDto sendLog) throws ITFEBizException;

	/**
	 * 查询报文收发日志	 
	 * @generated
	 * @param finddto
	 * @param pageRequest
	 * @param startdate
	 * @param enddate
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse queryMsgLog(TvRecvlogDto finddto, PageRequest pageRequest, String startdate, String enddate) throws ITFEBizException;

	/**
	 * 得到文件根路径	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getFileRootPath() throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param ssendno
	 * @throws ITFEBizException	 
	 */
   public abstract void resendMsg(String ssendno) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param sendNo
	 * @param pacackageNo
	 * @param msgno
	 * @param sdate
	 * @throws ITFEBizException	 
	 */
   public abstract void updateFail(String sendNo, String pacackageNo, String msgno, String sdate) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param msgno
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean isBizMsgNo(String msgno) throws ITFEBizException;

}
package com.cfcc.itfe.service.recbiz.certrec;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvFilesDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IDownloadAllFileService extends IService {



	/**
	 * ���ָ������֮ǰ�Ľ�����־���������Ϊ�գ���ô��ѯȫ��������־	 
	 * @generated
	 * @param recvDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getRecvLogBeforeDate(String recvDate) throws ITFEBizException;

	/**
	 * ���ݷ�����ˮ�Ż��ҵ��ƾ֤����Ϣ�ļ�������ϸ��Ϣ��һ��������ˮ�ſ����ҵ�һ��	 
	 * @generated
	 * @param no
	 * @return com.cfcc.itfe.persistence.dto.TvFilesDto
	 * @throws ITFEBizException	 
	 */
   public abstract TvFilesDto getFileInfoBySendNo(String no) throws ITFEBizException;

	/**
	 * ���ݷ�����ˮ�Ų��ҷ���ƾ֤�����и�����һ�����ͼ�¼���ܻ��ж������	 
	 * @generated
	 * @param recvLog
	 * @param sendNo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getFileListBySendNo(TvRecvLogShowDto recvLog, String sendNo) throws ITFEBizException;

	/**
	 * �޸��Ѿ����صĽ�����־�Ĵ����־	 
	 * @generated
	 * @param recvLogs
	 * @param status
	 * @throws ITFEBizException	 
	 */
   public abstract void updateStatus(List recvLogs, String status) throws ITFEBizException;

	/**
	 * ͳ��ָ�����ڸ���ҵ��ƾ֤�Ľ������	 
	 * @generated
	 * @param recvDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getRecvLogReport(String recvDate) throws ITFEBizException;

	/**
	 * �����Ѿ����յ�ƾ֤	 
	 * @generated
	 * @param recvLog
	 * @throws ITFEBizException	 
	 */
   public abstract void recvDelete(TvRecvLogShowDto recvLog) throws ITFEBizException;

}
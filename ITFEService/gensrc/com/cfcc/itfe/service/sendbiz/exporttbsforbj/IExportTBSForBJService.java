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
	 * ����TBS��ʽ�ļ�
	 	 
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
	 * ����������Ϣ�����ֱ�
	 	 
	 * @generated
	 * @param mainDto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findSubInfoByMain(IDto mainDto) throws ITFEBizException;

	/**
	 * ����TBS��ִ
	 	 
	 * @generated
	 * @param fileList
	 * @param param
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto importTBS(List fileList, Object param) throws ITFEBizException;

	/**
	 * ����ƾ֤״̬
	 	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
   public abstract void updateVouStatus(List list) throws ITFEBizException;

	/**
	 * ɾ���������ļ�
	 	 
	 * @generated
	 * @param fileName
	 * @throws ITFEBizException	 
	 */
   public abstract void deleteServerFile(String fileName) throws ITFEBizException;

	/**
	 * ��¼���뵼��TBS�ļ���־
	 	 
	 * @generated
	 * @param logList
	 * @throws ITFEBizException	 
	 */
   public abstract void writeTbsFileLog(List logList) throws ITFEBizException;

	/**
	 * ��ʱ�������ݻ�ȡ
	 	 
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
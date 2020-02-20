package com.cfcc.itfe.service.dataquery.bizdatacollect;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.TipsParamDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IBizDataCountService extends IService {



	/**
	 * ���ɱ���
	 	 
	 * @generated
	 * @param biztype
	 * @param paramdto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeReport(String biztype, TipsParamDto paramdto) throws ITFEBizException;

	/**
	 * ���ɴ����б���
	 	 
	 * @generated
	 * @param paramdto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeBankReport(TipsParamDto paramdto) throws ITFEBizException;

	/**
	 * �����ļ�
	 	 
	 * @generated
	 * @param paramdto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportFile(TipsParamDto paramdto) throws ITFEBizException;

}
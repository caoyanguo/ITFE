package com.cfcc.itfe.service.dataquery.tbbillstore;

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
 * @time   19-12-08 13:00:42
 * @generated
 * codecomment: 
 */
public interface ITbbillstoreService extends IService {



	/**
	 * �ļ�����
	 	 
	 * @generated
	 * @param paramlist
	 * @throws ITFEBizException	 
	 */
   public abstract void filedownLoad(List paramlist) throws ITFEBizException;

	/**
	 * �ļ��ϴ�
	 	 
	 * @generated
	 * @param paramList
	 * @throws ITFEBizException	 
	 */
   public abstract void fileupload(List paramList) throws ITFEBizException;

	/**
	 * ��ѯ�ϴ�������Ϣ
	 	 
	 * @generated
	 * @param paramList
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List searchFileInfo(List paramList) throws ITFEBizException;

}
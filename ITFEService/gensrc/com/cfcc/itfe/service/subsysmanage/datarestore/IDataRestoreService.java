package com.cfcc.itfe.service.subsysmanage.datarestore;

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
 * @time   19-12-08 13:00:43
 * @generated
 * codecomment: 
 */
public interface IDataRestoreService extends IService {



	/**
	 * ���պ����������������ݻָ�	 
	 * @generated
	 * @param sorgcode
	 * @param relPathList
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String datarestore(String sorgcode, List relPathList) throws ITFEBizException;

}
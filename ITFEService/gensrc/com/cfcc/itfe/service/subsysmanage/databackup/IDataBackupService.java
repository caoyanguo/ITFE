package com.cfcc.itfe.service.subsysmanage.databackup;

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
public interface IDataBackupService extends IService {



	/**
	 * ���պ����������������ݱ���	 
	 * @generated
	 * @param sorgcode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List databackup(String sorgcode) throws ITFEBizException;

}
package com.cfcc.itfe.service.para.modifypassword;

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
 * @time   19-12-08 13:00:38
 * @generated
 * codecomment: 
 */
public interface IModifyPasswordService extends IService {



	/**
	 * ÐÞ¸ÄÃÜÂë
	 	 
	 * @generated
	 * @param password
	 * @throws ITFEBizException	 
	 */
   public abstract void modifyPassword(String password) throws ITFEBizException;

}
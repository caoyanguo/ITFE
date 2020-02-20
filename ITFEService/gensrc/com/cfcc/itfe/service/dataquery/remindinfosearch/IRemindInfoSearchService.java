package com.cfcc.itfe.service.dataquery.remindinfosearch;

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
public interface IRemindInfoSearchService extends IService {



	/**
	 * 提醒信息查询
	 	 
	 * @generated
	 * @param params
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List searchInfo(List params) throws ITFEBizException;

}
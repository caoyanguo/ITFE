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

/**
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * codecomment: 
 */
public interface IItfeCacheService extends IService {



	/**
	 * ����ö��ֵ��Ϣ	 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List cacheEnumValue() throws ITFEBizException;

	/**
	 * ���ݴ����ŷ���ö��ֵ��Ϣ	 
	 * @generated
	 * @param typecode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List cacheEnumValueByCode(String typecode) throws ITFEBizException;

	/**
	 * ���ݴ����ŷ���ö��ֵ��Ϣ	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String cacheGetCenterOrg() throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param idto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List cacheGetValueByDto(IDto idto) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param sql
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List cacheEnumValueBySql(String sql) throws ITFEBizException;

}
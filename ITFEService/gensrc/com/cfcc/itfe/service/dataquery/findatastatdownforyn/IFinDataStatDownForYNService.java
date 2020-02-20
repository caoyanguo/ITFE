package com.cfcc.itfe.service.dataquery.findatastatdownforyn;

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
import com.cfcc.itfe.persistence.dto.TvUsersconditionDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IFinDataStatDownForYNService extends IService {



	/**
	 * ���ɱ����ļ�
	 	 
	 * @generated
	 * @param idto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeRptFile(IDto idto) throws ITFEBizException;

	/**
	 * ��ѯ��Ŀ����������Ϣ
	 	 
	 * @generated
	 * @param tvUsersconditionDto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String queryCondition(TvUsersconditionDto tvUsersconditionDto) throws ITFEBizException;

	/**
	 * ���ֲ�ѯ������Ϣ
	 	 
	 * @generated
	 * @param tvUsersconditionDto
	 * @throws ITFEBizException	 
	 */
   public abstract void saveCondition(TvUsersconditionDto tvUsersconditionDto) throws ITFEBizException;

}
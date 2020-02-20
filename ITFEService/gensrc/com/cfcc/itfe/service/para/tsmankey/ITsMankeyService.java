package com.cfcc.itfe.service.para.tsmankey;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.common.page.PageRequest;

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITsMankeyService extends IService {



	/**
	 * ��Կ�����б�
	 	 
	 * @generated
	 * @param dto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse keyList(IDto dto, PageRequest pageRequest) throws ITFEBizException;

	/**
	 * ��Կ����ɾ��
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void keyDelete(IDto dto) throws ITFEBizException;

	/**
	 * ��Կ����¼��
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void keySave(IDto dto) throws ITFEBizException;

	/**
	 * ��Կ�����޸�
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void keyModify(IDto dto) throws ITFEBizException;

	/**
	 * 
	 	 
	 * @generated
	 * @param list
	 * @param daffdate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List updateAndExport(List list, Date daffdate) throws ITFEBizException;

	/**
	 * 
	 	 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List autoGetBillOrg() throws ITFEBizException;

}
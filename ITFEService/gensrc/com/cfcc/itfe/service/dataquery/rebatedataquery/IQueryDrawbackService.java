package com.cfcc.itfe.service.dataquery.rebatedataquery;

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
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IQueryDrawbackService extends IService {



	/**
	 * ͨ�ò�ѯ����
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List commonQuery(IDto dto) throws ITFEBizException;

	/**
	 * ��ʾ�걨��
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List queryReportDatas(IDto dto) throws ITFEBizException;

	/**
	 * ��ԭ�˿�ƾ֤
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List backdwbkvou(IDto dto) throws ITFEBizException;

	/**
	 * �����˻���
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List sumdwbkreport(IDto dto) throws ITFEBizException;

	/**
	 * ��˰��ϸ��ӡ
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List detaildwbkprint(IDto dto) throws ITFEBizException;

	/**
	 * ���ͻ�ִ����
	 	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
   public abstract void senddwbkreport(List list) throws ITFEBizException;

	/**
	 * ȡ��У��
	 	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
   public abstract void cancelcheck(List list) throws ITFEBizException;

	/**
	 * ͨ�ò�ѯ����
	 	 
	 * @generated
	 * @param batchnum
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String deletereport(String batchnum) throws ITFEBizException;

	/**
	 * �����˿��˻�
	 	 
	 * @generated
	 * @param list
	 * @param fundfilename
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makedwbkbackreport(List list, String fundfilename) throws ITFEBizException;

}
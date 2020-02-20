package com.cfcc.itfe.service.recbiz.uploadmodule;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileResultDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IUploadUIService extends IService {



	/**
	 * ��������
	 	 
	 * @generated
	 * @param fileResultDto
	 * @throws ITFEBizException	 
	 */
   public abstract void UploadDate(FileResultDto fileResultDto) throws ITFEBizException;

	/**
	 * ��ѯ�ʽ�������ˮ��Ϣ
	 	 
	 * @generated
	 * @param strasrlno
	 * @param totalmoney
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List searchByTraSrlNo(String strasrlno, BigDecimal totalmoney) throws ITFEBizException;

	/**
	 * �����ʽ�������ˮ��ɾ����Ϣ
	 	 
	 * @generated
	 * @param strasrlno
	 * @throws ITFEBizException	 
	 */
   public abstract void delByTraSrlNo(String strasrlno) throws ITFEBizException;

	/**
	 * �����ʽ�������ˮ���ύ��Ϣ
	 	 
	 * @generated
	 * @param strasrlno
	 * @param fileList
	 * @return com.cfcc.devplatform.customtype.Int
	 * @throws ITFEBizException	 
	 */
   public abstract int commitByTraSrlNo(String strasrlno, List fileList) throws ITFEBizException;

	/**
	 * ����ѡ���б�ɾ��
	 	 
	 * @generated
	 * @param checkList
	 * @throws ITFEBizException	 
	 */
   public abstract void delByCheckFileList(List checkList) throws ITFEBizException;

}
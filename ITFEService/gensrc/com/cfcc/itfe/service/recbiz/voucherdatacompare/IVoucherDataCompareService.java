package com.cfcc.itfe.service.recbiz.voucherdatacompare;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IVoucherDataCompareService extends IService {



	/**
	 * ƾ֤�ȶ�
	 	 
	 * @generated
	 * @param checkList
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto voucherDataCompare(List checkList) throws ITFEBizException;

	/**
	 * ƾ֤�ص�
	 	 
	 * @generated
	 * @param succList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherReturnSuccess(List succList) throws ITFEBizException;

	/**
	 * ƾ֤У��
	 	 
	 * @generated
	 * @param checkList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherVerify(List checkList) throws ITFEBizException;

	/**
	 * ƾ֤��ȡ
	 	 
	 * @generated
	 * @param svtcode
	 * @param sorgcode
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherRead(String svtcode, String sorgcode) throws ITFEBizException;

	/**
	 * ������ʽ����
	 	 
	 * @generated
	 * @param checkList
	 * @throws ITFEBizException	 
	 */
   public abstract void generateData(List checkList) throws ITFEBizException;

	/**
	 * ���ͻص�
	 	 
	 * @generated
	 * @param checkList
	 * @throws ITFEBizException	 
	 */
   public abstract void sendReturnVoucher(List checkList) throws ITFEBizException;

	/**
	 * ����״̬
	 	 
	 * @generated
	 * @param loginSorgcode
	 * @throws ITFEBizException	 
	 */
   public abstract void updateStatus(String loginSorgcode) throws ITFEBizException;

}
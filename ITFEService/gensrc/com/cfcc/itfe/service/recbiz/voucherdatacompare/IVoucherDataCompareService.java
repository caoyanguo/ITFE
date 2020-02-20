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
	 * 凭证比对
	 	 
	 * @generated
	 * @param checkList
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto voucherDataCompare(List checkList) throws ITFEBizException;

	/**
	 * 凭证回单
	 	 
	 * @generated
	 * @param succList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherReturnSuccess(List succList) throws ITFEBizException;

	/**
	 * 凭证校验
	 	 
	 * @generated
	 * @param checkList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherVerify(List checkList) throws ITFEBizException;

	/**
	 * 凭证读取
	 	 
	 * @generated
	 * @param svtcode
	 * @param sorgcode
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherRead(String svtcode, String sorgcode) throws ITFEBizException;

	/**
	 * 生成正式数据
	 	 
	 * @generated
	 * @param checkList
	 * @throws ITFEBizException	 
	 */
   public abstract void generateData(List checkList) throws ITFEBizException;

	/**
	 * 发送回单
	 	 
	 * @generated
	 * @param checkList
	 * @throws ITFEBizException	 
	 */
   public abstract void sendReturnVoucher(List checkList) throws ITFEBizException;

	/**
	 * 更新状态
	 	 
	 * @generated
	 * @param loginSorgcode
	 * @throws ITFEBizException	 
	 */
   public abstract void updateStatus(String loginSorgcode) throws ITFEBizException;

}
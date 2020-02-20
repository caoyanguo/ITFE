package com.cfcc.itfe.service.recbiz.voucherload;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.facade.data.MulitTableDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IVoucherLoadService extends IService {



	/**
	 * 凭证读取
	 	 
	 * @generated
	 * @param svtcode
	 * @param sorgcode
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherLoad(String svtcode, String sorgcode) throws ITFEBizException;

	/**
	 * OCX凭证控件地址
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getOCXServerURL() throws ITFEBizException;

	/**
	 * 凭证回单
	 	 
	 * @generated
	 * @param succList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherReturnSuccess(List succList) throws ITFEBizException;

	/**
	 * 凭证提交
	 	 
	 * @generated
	 * @param checklist
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherCommit(List checklist) throws ITFEBizException;

	/**
	 * 查看已回单凭证状态
	 	 
	 * @generated
	 * @param checkList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer queryStatusReturnVoucher(List checkList) throws ITFEBizException;

	/**
	 * 凭证签章
	 	 
	 * @generated
	 * @param lists
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherStamp(List lists) throws ITFEBizException;

	/**
	 * 凭证退回
	 	 
	 * @generated
	 * @param list
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherReturnBack(List list) throws ITFEBizException;

	/**
	 * 凭证签章撤销
	 	 
	 * @generated
	 * @param lists
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherStampCancle(List lists) throws ITFEBizException;

	/**
	 * 查询打印次数
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String queryVoucherPrintCount(TvVoucherinfoDto dto) throws ITFEBizException;

	/**
	 * 凭证校验
	 	 
	 * @generated
	 * @param checkList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherVerify(List checkList) throws ITFEBizException;

	/**
	 * 凭证生成
	 	 
	 * @generated
	 * @param list
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List voucherGenerate(List list) throws ITFEBizException;

	/**
	 * 获取凭证签章报文
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String voucherStampXml(TvVoucherinfoDto dto) throws ITFEBizException;

	/**
	 * 凭证退回
	 	 
	 * @generated
	 * @param list
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto voucherCheckRetBack(List list) throws ITFEBizException;

	/**
	 * getRotaryStamp
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getRotaryStamp() throws ITFEBizException;

	/**
	 * getOfficialStamp
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getOfficialStamp() throws ITFEBizException;

	/**
	 * 取得是否进行额度控制标志
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getIscheckpayplan() throws ITFEBizException;

	/**
	 * 额度控制校验
	 	 
	 * @generated
	 * @param list
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto amtControlVerify(List list) throws ITFEBizException;

	/**
	 * 取得划款申请是否通过前置提交
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getIsitfecommit() throws ITFEBizException;

	/**
	 * 取得ocx签章服务地址
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getOCXStampServerURL() throws ITFEBizException;

	/**
	 * 发送凭证附件
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void sendData(TvVoucherinfoDto dto) throws ITFEBizException;

	/**
	 * 接收凭证附件
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer getData(TvVoucherinfoDto dto) throws ITFEBizException;

	/**
	 * 校验实拨资金业务中是否为调拨科目
	 	 
	 * @generated
	 * @param voucher
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String verifyPayOutMoveFunSubject(TvVoucherinfoDto voucher) throws ITFEBizException;

	/**
	 * 业务对账凭证库对账方法
	 	 
	 * @generated
	 * @param parammap
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map queryVoucherReturnStatus(Map parammap) throws ITFEBizException;

	/**
	 * 导出PDF文件
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List exportPDF(TvVoucherinfoDto dto) throws ITFEBizException;

	/**
	 * 导出数据
	 	 
	 * @generated
	 * @param voucherList
	 * @return java.lang.StringBuffer
	 * @throws ITFEBizException	 
	 */
   public abstract StringBuffer exportData(List voucherList) throws ITFEBizException;

}
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
	 * ƾ֤��ȡ
	 	 
	 * @generated
	 * @param svtcode
	 * @param sorgcode
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherLoad(String svtcode, String sorgcode) throws ITFEBizException;

	/**
	 * OCXƾ֤�ؼ���ַ
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getOCXServerURL() throws ITFEBizException;

	/**
	 * ƾ֤�ص�
	 	 
	 * @generated
	 * @param succList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherReturnSuccess(List succList) throws ITFEBizException;

	/**
	 * ƾ֤�ύ
	 	 
	 * @generated
	 * @param checklist
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherCommit(List checklist) throws ITFEBizException;

	/**
	 * �鿴�ѻص�ƾ֤״̬
	 	 
	 * @generated
	 * @param checkList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer queryStatusReturnVoucher(List checkList) throws ITFEBizException;

	/**
	 * ƾ֤ǩ��
	 	 
	 * @generated
	 * @param lists
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherStamp(List lists) throws ITFEBizException;

	/**
	 * ƾ֤�˻�
	 	 
	 * @generated
	 * @param list
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherReturnBack(List list) throws ITFEBizException;

	/**
	 * ƾ֤ǩ�³���
	 	 
	 * @generated
	 * @param lists
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherStampCancle(List lists) throws ITFEBizException;

	/**
	 * ��ѯ��ӡ����
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String queryVoucherPrintCount(TvVoucherinfoDto dto) throws ITFEBizException;

	/**
	 * ƾ֤У��
	 	 
	 * @generated
	 * @param checkList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer voucherVerify(List checkList) throws ITFEBizException;

	/**
	 * ƾ֤����
	 	 
	 * @generated
	 * @param list
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List voucherGenerate(List list) throws ITFEBizException;

	/**
	 * ��ȡƾ֤ǩ�±���
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String voucherStampXml(TvVoucherinfoDto dto) throws ITFEBizException;

	/**
	 * ƾ֤�˻�
	 	 
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
	 * ȡ���Ƿ���ж�ȿ��Ʊ�־
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getIscheckpayplan() throws ITFEBizException;

	/**
	 * ��ȿ���У��
	 	 
	 * @generated
	 * @param list
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto amtControlVerify(List list) throws ITFEBizException;

	/**
	 * ȡ�û��������Ƿ�ͨ��ǰ���ύ
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getIsitfecommit() throws ITFEBizException;

	/**
	 * ȡ��ocxǩ�·����ַ
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getOCXStampServerURL() throws ITFEBizException;

	/**
	 * ����ƾ֤����
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void sendData(TvVoucherinfoDto dto) throws ITFEBizException;

	/**
	 * ����ƾ֤����
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
   public abstract Integer getData(TvVoucherinfoDto dto) throws ITFEBizException;

	/**
	 * У��ʵ���ʽ�ҵ�����Ƿ�Ϊ������Ŀ
	 	 
	 * @generated
	 * @param voucher
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String verifyPayOutMoveFunSubject(TvVoucherinfoDto voucher) throws ITFEBizException;

	/**
	 * ҵ�����ƾ֤����˷���
	 	 
	 * @generated
	 * @param parammap
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map queryVoucherReturnStatus(Map parammap) throws ITFEBizException;

	/**
	 * ����PDF�ļ�
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List exportPDF(TvVoucherinfoDto dto) throws ITFEBizException;

	/**
	 * ��������
	 	 
	 * @generated
	 * @param voucherList
	 * @return java.lang.StringBuffer
	 * @throws ITFEBizException	 
	 */
   public abstract StringBuffer exportData(List voucherList) throws ITFEBizException;

}
package com.cfcc.itfe.service.dataaudit.finpaystatisticalreport;

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
public interface IFinpaystatisticalreportService extends IService {



	/**
	 * ���������ձ�	 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param moneyUnit
	 * @param rptDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeIncomeBill(IDto idto, String billCode, String moneyUnit, String rptDate) throws ITFEBizException;

	/**
	 * ����֧���ձ�	 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param moneyUnit
	 * @param rptDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makePayoutBill(IDto idto, String billCode, String moneyUnit, String rptDate) throws ITFEBizException;

	/**
	 * ����ȫ�ھ�˰���ձ�	 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param moneyUnit
	 * @param rptDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeAllTaxBill(IDto idto, String billCode, String moneyUnit, String rptDate) throws ITFEBizException;

	/**
	 * ��֧���м��»���ҵ����˰Ʊ�ձ�	 
	 * @generated
	 * @param idto
	 * @param startdate
	 * @param enddate
	 * @param remake
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeFinincomeOnlineBill(IDto idto, String startdate, String enddate, String remake) throws ITFEBizException;

	/**
	 * ȡ��excle������֧��������	 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param moneyUnit
	 * @param rptDate
	 * @param rptType
	 * @param copyFilename
	 * @param reporttitle
	 * @param unitname
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String makeExcelInOutBillSer(IDto idto, String billCode, String moneyUnit, String rptDate, String rptType, String copyFilename, String reporttitle, String unitname) throws ITFEBizException;

	/**
	 * ȡ��excle������֧��������	 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param moneyUnit
	 * @param rptDate
	 * @param rptType
	 * @param copyFilename
	 * @param reporttitle
	 * @param unitname
	 * @return java.util.HashMap
	 * @throws ITFEBizException	 
	 */
   public abstract HashMap makeExcelInOutBill(IDto idto, String billCode, String moneyUnit, String rptDate, String rptType, String copyFilename, String reporttitle, String unitname) throws ITFEBizException;

	/**
	 * ����Ԥ���������ѯ	 
	 * @generated
	 * @param idto
	 * @param startdate
	 * @param enddate
	 * @param remake
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeNonBudgetary(IDto idto, String startdate, String enddate, String remake) throws ITFEBizException;

	/**
	 * ������Ȩ��֧��ˮͳ�Ʋ�ѯ	 
	 * @generated
	 * @param idto
	 * @param startdate
	 * @param enddate
	 * @param remake
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List FinOutBookSerial(IDto idto, String startdate, String enddate, String remake) throws ITFEBizException;

	/**
	 * EXCEL�������·ּ�Ԥ������ͳ�Ʋ�ѯ	 
	 * @generated
	 * @param idto
	 * @param startdate
	 * @param enddate
	 * @param remake
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List FinExcelIncomeMonthLevel(IDto idto, String startdate, String enddate, String remake) throws ITFEBizException;

}
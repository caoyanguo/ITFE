package com.cfcc.itfe.service.dataquery.finbudgetreccountreport;

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
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.page.PageRequest;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IPayAndIncomeBillService extends IService {



	/**
	 * 财政收入日报	 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 * @param sleTenrptType
	 * @param sleQuarterrptType
	 * @param sleHalfyearrptType
	 * @param sleSubjectType
	 * @param sleSubjectAttribute
	 * @param smoveflag
	 * @param sleMoneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeIncomeBill(IDto idto, String sleBillType, String sleTenrptType, String sleQuarterrptType, String sleHalfyearrptType, String sleSubjectType, String sleSubjectAttribute, String smoveflag, String sleMoneyUnit) throws ITFEBizException;

	/**
	 * 财政支出日报	 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 * @param sleTenrptType
	 * @param sleQuarterrptType
	 * @param sleHalfyearrptType
	 * @param sleSubjectType
	 * @param sleSubjectAttribute
	 * @param smoveflag
	 * @param sleMoneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makePayoutBill(IDto idto, String sleBillType, String sleTenrptType, String sleQuarterrptType, String sleHalfyearrptType, String sleSubjectType, String sleSubjectAttribute, String smoveflag, String sleMoneyUnit) throws ITFEBizException;

	/**
	 * 财政收入日报	 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 * @param sleTenrptType
	 * @param sleQuarterrptType
	 * @param sleHalfyearrptType
	 * @param sleSubjectType
	 * @param sleSubjectAttribute
	 * @param smoveflag
	 * @param sleMoneyUnit
	 * @param request
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse makeIncomeReport(IDto idto, String sleBillType, String sleTenrptType, String sleQuarterrptType, String sleHalfyearrptType, String sleSubjectType, String sleSubjectAttribute, String smoveflag, String sleMoneyUnit, PageRequest request) throws ITFEBizException;

	/**
	 * 财政支出日报	 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 * @param sleTenrptType
	 * @param sleQuarterrptType
	 * @param sleHalfyearrptType
	 * @param sleSubjectType
	 * @param sleSubjectAttribute
	 * @param smoveflag
	 * @param sleMoneyUnit
	 * @param request
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse makePayoutReport(IDto idto, String sleBillType, String sleTenrptType, String sleQuarterrptType, String sleHalfyearrptType, String sleSubjectType, String sleSubjectAttribute, String smoveflag, String sleMoneyUnit, PageRequest request) throws ITFEBizException;

	/**
	 * 预算收入分科目统计报表	 
	 * @generated
	 * @param idto
	 * @param sbudgetsubcode
	 * @param sleBillType
	 * @param sleTenrptType
	 * @param sleQuarterrptType
	 * @param sleHalfyearrptType
	 * @param sleSubjectType
	 * @param smoveflag
	 * @param sleMoneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makeIncomeReportBySubject(IDto idto, String sbudgetsubcode, String sleBillType, String sleTenrptType, String sleQuarterrptType, String sleHalfyearrptType, String sleSubjectType, String smoveflag, String sleMoneyUnit) throws ITFEBizException;

	/**
	 * 预算支出分科目统计报表	 
	 * @generated
	 * @param idto
	 * @param sbudgetsubcode
	 * @param sleBillType
	 * @param sleTenrptType
	 * @param sleQuarterrptType
	 * @param sleHalfyearrptType
	 * @param sleSubjectType
	 * @param smoveflag
	 * @param sleMoneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List makePayoutReportBySubject(IDto idto, String sbudgetsubcode, String sleBillType, String sleTenrptType, String sleQuarterrptType, String sleHalfyearrptType, String sleSubjectType, String smoveflag, String sleMoneyUnit) throws ITFEBizException;

	/**
	 * 库存统计分析	 
	 * @generated
	 * @param sorgcode
	 * @param startyear
	 * @param endyear
	 * @param startdate
	 * @param enddate
	 * @param sleTreCode
	 * @param sleOfFlag
	 * @param sleMoneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List stockCountQueryList(String sorgcode, String startyear, String endyear, String startdate, String enddate, String sleTreCode, String sleOfFlag, String sleMoneyUnit) throws ITFEBizException;

}
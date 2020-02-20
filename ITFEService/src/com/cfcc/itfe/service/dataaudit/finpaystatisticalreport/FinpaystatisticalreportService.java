package com.cfcc.itfe.service.dataaudit.finpaystatisticalreport;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.ExcelReportDto;
import com.cfcc.itfe.persistence.dto.ReportDto;
import com.cfcc.itfe.persistence.dto.TrFinIncomeonlineDayrptDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsReportmainDto;
import com.cfcc.itfe.persistence.dto.TsTaxpaycodeDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvGrantpayplanMainDto;
import com.cfcc.itfe.util.ArithUtil;
import com.cfcc.itfe.util.ReportExcel;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author Administrator
 * @time   15-05-18 15:46:17
 * codecomment: 
 */

public class FinpaystatisticalreportService extends AbstractFinpaystatisticalreportService {
	private static Log log = LogFactory.getLog(FinpaystatisticalreportService.class);	

	/**
	 * 财政收入日报
	 * 
	 * @generated
	 * @param idto
	 * @param sleSumItem--是否含项合计
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List makeIncomeBill(IDto idto, String sleSumItem, String sleSubjectType,
			String rptDate) throws ITFEBizException {
		
		try {

			//收入条件
			String sqlwhere =makeIncomeBillwhere(idto,sleSumItem,sleSubjectType,rptDate,StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
			TrIncomedayrptDto dto = (TrIncomedayrptDto)idto;
			
			StringBuffer sqlbuf = new StringBuffer(
					"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
			// 统计收入报表--收入部分
			//目
			String sql = "select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH,a.N_MONEYQUARTER,a.N_MONEYYEAR "
					+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " union all "
					+ "  select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH,a.N_MONEYQUARTER,a.N_MONEYYEAR "
					+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " union all ";
					
					//是否含项合计:默认为不含项合计
					if(sleSumItem ==null || "".equals(sleSumItem) || StateConstant.REPORTTYPE_0405_YES.equals(sleSumItem)){
					sql+="  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
							+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
							+ sqlwhere
							+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME "
							+ " union all "
							+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
							+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
							+ sqlwhere
							+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME "
							+ " union all ";
					}

					//款
					sql+=
					  "  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
					+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME "
					+ " union all "
					+ "  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
					+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME "
					+ " union all "
					
					//类
					+ "  select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
					+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME "
					+ " union all "
					+ "  select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
					+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME ";
//					+ " UNION ALL ";
			sqlbuf.append(sql);

			/*
			 * 退库部分已经在tcbs里面扎差，这里不做处理
			 */
//			//退库条件
//			String sqlwhereback =makeIncomeBillwhere(idto,sleSumItem,sleSubjectType,rptDate,StateConstant.REPORTTYPE_FLAG_NRDRAWBACKBILL);
//			// 统计收入报表--退库部分
//			//目
//			sql = " select S_BUDGETSUBCODE,S_BUDGETSUBNAME,-N_MONEYDAY,-N_MONEYTENDAY,-N_MONEYMONTH,-N_MONEYQUARTER,-N_MONEYYEAR"
//					+ " from TR_INCOMEDAYRPT a Where 1=1 "
//					+ sqlwhereback
//					+ " union all "
//					+ "  select S_BUDGETSUBCODE,S_BUDGETSUBNAME,-N_MONEYDAY,-N_MONEYTENDAY,-N_MONEYMONTH,-N_MONEYQUARTER,-N_MONEYYEAR"
//					+ " from HTR_INCOMEDAYRPT a Where 1=1 "
//					+ sqlwhereback
//					+ " union all ";
//			
//					//是否含项合计:默认为不含项合计
//					if(sleSumItem ==null || "".equals(sleSumItem) || StateConstant.REPORTTYPE_0405_NO.equals(sleSumItem)){
//						sql+="  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,-sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,-sum(a.N_MONEYMONTH) as N_MONEYMONTH,-sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,-sum(a.N_MONEYYEAR) as N_MONEYYEAR "
//							+ " from TR_INCOMEDAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
//							+ sqlwhereback
//							+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME "
//							+ " union all "
//							+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,-sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,-sum(a.N_MONEYMONTH) as N_MONEYMONTH,-sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,-sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
//							+ " from HTR_INCOMEDAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
//							+ sqlwhereback
//							+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME "
//							+ " union all ";
//					}
//					
//					//款
//					sql+=
//					"  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,-sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,-sum(a.N_MONEYMONTH) as N_MONEYMONTH,-sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,-sum(a.N_MONEYYEAR) as N_MONEYYEAR "
//					+ " from TR_INCOMEDAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
//					+ sqlwhereback
//					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME "
//					+ " union all "
//					+ "  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,-sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,-sum(a.N_MONEYMONTH) as N_MONEYMONTH,-sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,-sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
//					+ " from HTR_INCOMEDAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
//					+ sqlwhereback
//					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME "
//					+ " union all "
//					
//					//类
//					+ "  select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,-sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,-sum(a.N_MONEYMONTH) as N_MONEYMONTH,-sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,-sum(a.N_MONEYYEAR) as N_MONEYYEAR "
//					+ " from TR_INCOMEDAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
//					+ sqlwhereback
//					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME "
//					+ " union all "
//					+ "  select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,-sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,-sum(a.N_MONEYMONTH) as N_MONEYMONTH,-sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,-sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
//					+ " from HTR_INCOMEDAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
//					+ sqlwhereback
//					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME order by S_BUDGETSUBCODE,S_BUDGETSUBNAME ";
//			sqlbuf.append(sql);
			
			
			sqlbuf.append(") select S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			
			//打印sql
			log.debug(sqlbuf.toString());
			
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			List list = (List) exec.runQueryCloseCon(sqlbuf.toString(),
					TrIncomedayrptDto.class, true).getDtoCollection();
			
			// 增加一般预算内、转移性收入等小计
			List listfortype =getSumBySbtType(idto,sleSumItem,sleSubjectType,rptDate);
			list.addAll(listfortype);
			
			//预算级次 0 共享 1 中央 2 省
			if(dto.getSbudgetlevelcode().equals(MsgConstant.BUDGET_LEVEL_SHARE) || dto.getSbudgetlevelcode().equals(MsgConstant.BUDGET_LEVEL_CENTER) 
					 || dto.getSbudgetlevelcode().equals(MsgConstant.BUDGET_LEVEL_PROVINCE)){
				;
			}
			else{
				// 加特殊统计条件
				List listforparm =makeIncomeBillforReportPrmt(idto,sleSumItem,sleSubjectType,rptDate);
				if (null != listforparm)
					list.addAll(listforparm);
			}
			
			return list;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}
	
	/**
	 * 计算收入报表按科目分类进行小计
	 * 
	 * @param idto
	 * @param sleSumItem
	 * @param sleSubjectType
	 * @param rptDate
	 * @return
	 * @throws ITFEBizException 
	 */
	public List getSumBySbtType(IDto idto, String sleSumItem,
			String sleSubjectType, String rptDate) throws ITFEBizException {

		TrIncomedayrptDto sdto = (TrIncomedayrptDto) idto;
		
		String sqlwhere=makeIncomeBillwhere(idto,sleSumItem,sleSubjectType,rptDate,StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
		try {

			// 财政机关代码存储科目类型,征收机关代码存储科目分类
			StringBuffer sqlbuf = new StringBuffer(
					"with tmp(s_FinOrgCode,s_TaxOrgCode,S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
			// 统计收入报表
			String sql = "select b.s_SubjectType as s_FinOrgCode, b.s_ClassFlag as s_TaxOrgCode, S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR"
					+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " union all "
					+ "  select  b.s_SubjectType as s_FinOrgCode, b.s_ClassFlag as s_TaxOrgCode, S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR"
					+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere;
			
					/**
					 * 	退库部分暂时不做
					 */
//					+ " union all "
//					+ " select  b.s_SubjectType as s_FinOrgCode, b.s_ClassFlag as s_TaxOrgCode, S_BUDGETSUBCODE,S_BUDGETSUBNAME,-N_MONEYDAY,-N_MONEYTENDAY,-N_MONEYMONTH,-N_MONEYQUARTER,-N_MONEYYEAR"
//					+ " from TR_INCOMEDAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
//					+ sqlwhere
//					+ " union all "
//					+ "  select  b.s_SubjectType as s_FinOrgCode, b.s_ClassFlag as s_TaxOrgCode, S_BUDGETSUBCODE,S_BUDGETSUBNAME,-N_MONEYDAY,-N_MONEYTENDAY,-N_MONEYMONTH,-N_MONEYQUARTER,-N_MONEYYEAR"
//					+ " from HTR_INCOMEDAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
//					+ sqlwhere;
			
			sqlbuf.append(sql);
			sqlbuf.append(") select s_FinOrgCode,s_TaxOrgCode,S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp group by S_BUDGETSUBCODE,S_BUDGETSUBNAME,s_FinOrgCode,s_TaxOrgCode ");

			SQLExecutor exec;
			exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			//打印sql
			log.debug(sqlbuf.toString());
			
			List<TrIncomedayrptDto> list = (List<TrIncomedayrptDto>) exec
					.runQueryCloseCon(sqlbuf.toString(),
							TrIncomedayrptDto.class, true).getDtoCollection();

			BigDecimal btransday = new BigDecimal(0.00); // 转移性收入日累计
			BigDecimal btranstenday = new BigDecimal(0.00); // 转移性收入旬累计
			BigDecimal btransmonth = new BigDecimal(0.00); // 转移性收入月累计
			BigDecimal btransquarter = new BigDecimal(0.00); // 转移性收入季累计
			BigDecimal btransyear = new BigDecimal(0.00); // 转移性收入年累计

			BigDecimal bcommincday = new BigDecimal(0.00); // 一般预算收入日累计
			BigDecimal bcomminctenday = new BigDecimal(0.00); // 一般预算收入旬累计
			BigDecimal bcommincmonth = new BigDecimal(0.00); // 一般预算收入月累计
			BigDecimal bcommincquarter = new BigDecimal(0.00); // 一般预算收入季累计
			BigDecimal bcommincyear = new BigDecimal(0.00); // 一般预算收入年累计

			BigDecimal bfundday = new BigDecimal(0.00); // 基金收入日累计
			BigDecimal bfundtenday = new BigDecimal(0.00); // 基金收入旬累计
			BigDecimal bfundmonth = new BigDecimal(0.00); // 基金收入月累计
			BigDecimal bfundquarter = new BigDecimal(0.00); // 基金收入季累计
			BigDecimal bfundyear = new BigDecimal(0.00); // 基金收入年累计

			BigDecimal bfundotherday = new BigDecimal(0.00); // 其他基金收入日累计
			BigDecimal bfundothertenday = new BigDecimal(0.00); // 其他基金收入旬累计
			BigDecimal bfundothermonth = new BigDecimal(0.00); // 其他基金收入月累计
			BigDecimal bfundotherquarter = new BigDecimal(0.00); // 其他基金收入季累计
			BigDecimal bfundotheryear = new BigDecimal(0.00); // 其他基金收入年累计

			BigDecimal bdebtday = new BigDecimal(0.00); // 债务收入日累计
			BigDecimal bdebttenday = new BigDecimal(0.00); // 债务收入旬累计
			BigDecimal bdebtmonth = new BigDecimal(0.00); // 债务收入月累计
			BigDecimal bdebtquarter = new BigDecimal(0.00); // 债务收入季累计
			BigDecimal bdebtyear = new BigDecimal(0.00); // 债务收入年累计
			
			BigDecimal allday = new BigDecimal(0.00); // 收入日累计
			BigDecimal alltenday = new BigDecimal(0.00); // 收入旬累计
			BigDecimal allmonth = new BigDecimal(0.00); // 收入月累计
			BigDecimal allquarter = new BigDecimal(0.00); // 收入季累计
			BigDecimal allyear = new BigDecimal(0.00); // 收入年累计
			
			for (TrIncomedayrptDto dto : list) {
				// 科目代码
				String sbtCode = dto.getSbudgetsubcode();
				// 科目类型
				String sbtType = dto.getSfinorgcode().trim();
				// 科目分类
				String sbtClass = dto.getStaxorgcode().trim();
				// 统计各收入类型的发生额
				if (sbtType.equals(StateConstant.SBT_TYPE_BUDGET_IN)
						|| sbtType.equals(StateConstant.SBT_TYPE_BUDGET_OUT)) {
					if (sbtClass.equals(StateConstant.SBT_CLASS_TRANSINCOME)) { // 转移性收入
						btransday =ArithUtil.add(btransday,dto.getNmoneyday(),2);
						btranstenday =ArithUtil.add(btranstenday,dto.getNmoneytenday(),2);
						btransmonth =ArithUtil.add(btransmonth,dto.getNmoneymonth(),2);
						btransquarter =ArithUtil.add(btransquarter,dto.getNmoneyquarter(),2);
						btransyear =ArithUtil.add(btransyear,dto.getNmoneyyear(),2);
					} else { // 一般预算类
						bcommincday =ArithUtil.add(bcommincday,dto.getNmoneyday(),2);
						bcomminctenday =ArithUtil.add(bcomminctenday,dto.getNmoneytenday(),2);
						bcommincmonth =ArithUtil.add(bcommincmonth,dto.getNmoneymonth(),2);
						bcommincquarter =ArithUtil.add(bcommincquarter,dto.getNmoneyquarter(),2);
						bcommincyear =ArithUtil.add(bcommincyear,dto.getNmoneyyear(),2);
					}
				} else if (sbtType.equals(StateConstant.SBT_TYPE_FUND_IN)
						|| sbtType.equals(StateConstant.SBT_TYPE_FUND_OUT)) {
					if (sbtClass.equals(StateConstant.SBT_CLASS_FUNDINCOME)) { // 基金类收入
						bfundday =ArithUtil.add(bfundday,dto.getNmoneyday(),2);
						bfundtenday =ArithUtil.add(bfundtenday,dto.getNmoneytenday(),2);
						bfundmonth =ArithUtil.add(bfundmonth,dto.getNmoneymonth(),2);
						bfundquarter =ArithUtil.add(bfundquarter,dto.getNmoneyquarter(),2);
						bfundyear =ArithUtil.add(bfundyear,dto.getNmoneyyear(),2);
					} else { // 其他基金预算类收入
						bfundotherday =ArithUtil.add(bfundotherday,dto.getNmoneyday(),2);
						bfundothertenday =ArithUtil.add(bfundothertenday,dto.getNmoneytenday(),2);
						bfundothermonth =ArithUtil.add(bfundothermonth,dto.getNmoneymonth(),2);
						bfundotherquarter =ArithUtil.add(bfundotherquarter,dto.getNmoneyquarter(),2);
						bfundotheryear =ArithUtil.add(bfundotheryear,dto.getNmoneyyear(),2);
					}
				} else if (sbtType.equals(StateConstant.SBT_TYPE_DEBT_IN)
						|| sbtType.equals(StateConstant.SBT_TYPE_DEBT_OUT)) { // 债务类收入
					bdebtday =ArithUtil.add(bdebtday,dto.getNmoneyday(),2);
					bdebttenday =ArithUtil.add(bdebttenday,dto.getNmoneytenday(),2);
					bdebtmonth =ArithUtil.add(bdebtmonth,dto.getNmoneymonth(),2);
					bdebtquarter =ArithUtil.add(bdebtquarter,dto.getNmoneyquarter(),2);
					bdebtyear =ArithUtil.add(bdebtyear,dto.getNmoneyyear(),2);
				}
				
				allday = ArithUtil.add(allday,dto.getNmoneyday(),2); // 收入日累计
				alltenday = ArithUtil.add(alltenday,dto.getNmoneytenday(),2); // 收入旬累计
				allmonth = ArithUtil.add(allmonth,dto.getNmoneymonth(),2); // 收入月累计
				allquarter = ArithUtil.add(allquarter,dto.getNmoneyquarter(),2); // 收入季累计
				allyear = ArithUtil.add(allyear,dto.getNmoneyyear(),2); // 收入年累计
				
			}
			List<TrIncomedayrptDto> retlist = new ArrayList<TrIncomedayrptDto>();
			// 增加一般预算收入合计
//			if (bcommincday.compareTo(new BigDecimal(0)) != 0
//					|| bcommincmonth.compareTo(new BigDecimal(0)) != 0
//					|| bcommincyear.compareTo(new BigDecimal(0)) != 0) {
				TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
				tmpdto.setSbudgetsubcode("");
				tmpdto.setSbudgetsubname("一般预算收入合计");
				tmpdto.setNmoneyday(bcommincday);
				tmpdto.setNmoneytenday(bcomminctenday);
				tmpdto.setNmoneymonth(bcommincmonth);
				tmpdto.setNmoneyquarter(bcommincquarter);
				tmpdto.setNmoneyyear(bcommincyear);
				retlist.add(tmpdto);
//			}
//			// 转移性收入合计
//			if (btransday.compareTo(new BigDecimal(0)) != 0
//					|| btransmonth.compareTo(new BigDecimal(0)) != 0
//					|| btransyear.compareTo(new BigDecimal(0)) != 0) {
				TrIncomedayrptDto btranstmpdto = new TrIncomedayrptDto();
				btranstmpdto.setSbudgetsubcode("");
				btranstmpdto.setSbudgetsubname("转移性收入合计");
				btranstmpdto.setNmoneyday(btransday);
				btranstmpdto.setNmoneytenday(btranstenday);
				btranstmpdto.setNmoneymonth(btransmonth);
				btranstmpdto.setNmoneyquarter(btransquarter);
				btranstmpdto.setNmoneyyear(btransyear);
				retlist.add(btranstmpdto);
//			}
//			// 社会保险基金预算收入合计
//			if (bfundday.compareTo(new BigDecimal(0)) != 0
//					|| bfundmonth.compareTo(new BigDecimal(0)) != 0
//					|| bfundyear.compareTo(new BigDecimal(0)) != 0) {
				TrIncomedayrptDto bfundtmpdto = new TrIncomedayrptDto();
				bfundtmpdto.setSbudgetsubcode("");
				bfundtmpdto.setSbudgetsubname("社会保险基金预算收入合计");
				bfundtmpdto.setNmoneyday(bfundday);
				bfundtmpdto.setNmoneytenday(bfundtenday);
				bfundtmpdto.setNmoneymonth(bfundmonth);
				bfundtmpdto.setNmoneyquarter(bfundquarter);
				bfundtmpdto.setNmoneyyear(bfundyear);
				retlist.add(bfundtmpdto);
//			}
//			// 其他基金预算收入合计
//			if (bfundotherday.compareTo(new BigDecimal(0)) != 0
//					|| bfundothermonth.compareTo(new BigDecimal(0)) != 0
//					|| bfundotheryear.compareTo(new BigDecimal(0)) != 0) {
				TrIncomedayrptDto bfundothertmpdto = new TrIncomedayrptDto();
				bfundothertmpdto.setSbudgetsubcode("");
				bfundothertmpdto.setSbudgetsubname("其他基金预算收入合计");
				bfundothertmpdto.setNmoneyday(bfundotherday);
				bfundothertmpdto.setNmoneytenday(bfundothertenday);
				bfundothertmpdto.setNmoneymonth(bfundothermonth);
				bfundothertmpdto.setNmoneyquarter(bfundotherquarter);
				bfundothertmpdto.setNmoneyyear(bfundotheryear);
				retlist.add(bfundothertmpdto);
//			}
//			// 债务类预算收入合计
//			if (bdebtday.compareTo(new BigDecimal(0)) != 0
//					|| bdebtmonth.compareTo(new BigDecimal(0)) != 0
//					|| bdebtyear.compareTo(new BigDecimal(0)) != 0) {
				TrIncomedayrptDto bdebttmpdto = new TrIncomedayrptDto();
				bdebttmpdto.setSbudgetsubcode("");
				bdebttmpdto.setSbudgetsubname("债务类预算收入合计");
				bdebttmpdto.setNmoneyday(bdebtday);
				bdebttmpdto.setNmoneytenday(bdebttenday);
				bdebttmpdto.setNmoneymonth(bdebtmonth);
				bdebttmpdto.setNmoneyquarter(bdebtquarter);
				bdebttmpdto.setNmoneyyear(bdebtyear);
				retlist.add(bdebttmpdto);
//			}
			// 汇总合计	
				TrIncomedayrptDto alltmpdto = new TrIncomedayrptDto();
				alltmpdto.setSbudgetsubcode("");
				alltmpdto.setSbudgetsubname("合计数");
				alltmpdto.setNmoneyday(allday);
				alltmpdto.setNmoneytenday(alltenday);
				alltmpdto.setNmoneymonth(allmonth);
				alltmpdto.setNmoneyquarter(allquarter);
				alltmpdto.setNmoneyyear(allyear);
				retlist.add(alltmpdto);

			return retlist;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}
	
	/**
	 * 按照报表参数给定条件查询财政收入日报
	 * 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param sleSubjectType
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List makeIncomeBillforReportPrmt(IDto idto, String sleSumItem, String sleSubjectType,
			String rptDate) throws ITFEBizException {
		
		
		List<TsReportmainDto> ReportPrmtlist =  getReportPrmt("B_121");
		TrIncomedayrptDto incomedto = (TrIncomedayrptDto) idto;
		
		if(null ==ReportPrmtlist || ReportPrmtlist.size() == 0){
			return null;
		}
		
		String sqlwhere=makeIncomeBillwhere(idto,sleSumItem,sleSubjectType,rptDate,StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
		
		//查询sql
		StringBuffer sqlbuf = new StringBuffer(
		"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
		//拼写预算收入sql
		for(int i=0; i<ReportPrmtlist.size(); i++){
			TsReportmainDto dto = ReportPrmtlist.get(i);
			
			//拼写报表参数给出的查询条件
			String sqllike=" ";
			String[] strarr = dto.getScompute().trim().split("#"); 
			for(int j=0;j<strarr.length;j++){
				if(j==0){
					sqllike += " a.S_BUDGETSUBCODE like '"+strarr[j].trim()+"' ";
				}
				else{
					sqllike += " or a.S_BUDGETSUBCODE like '"+strarr[j].trim()+"' ";
				}
			}
			sqllike = " and ( "+sqllike+" ) ";
			
			if(i==0){
				 String sql = " select '"+dto.getSsubjectcode()+"' as S_BUDGETSUBCODE, '"+dto.getSsubjectname()+"' as S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR "
							+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a ,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE  " 
							+ sqllike
							+ sqlwhere
							+ " union all "
							+ " select '"+dto.getSsubjectcode()+"' as S_BUDGETSUBCODE, '"+dto.getSsubjectname()+"' as S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR "
							+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a ,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE  " 
							+ sqllike
							+ sqlwhere;
				 sqlbuf.append(sql);
			}
			else{
				String sql = " union all select '"+dto.getSsubjectcode()+"' as S_BUDGETSUBCODE, '"+dto.getSsubjectname()+"' as S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE  " 
				+ sqllike
				+ sqlwhere
				+ " union all "
				+ " select '"+dto.getSsubjectcode()+"' as S_BUDGETSUBCODE, '"+dto.getSsubjectname()+"' as S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR "
				+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE  " 
				+ sqllike
				+ sqlwhere;
				sqlbuf.append(sql);
			}
		}
		
//		//拼写退库sql
//		for(int i=0; i<ReportPrmtlist.size(); i++){
//			TsReportmainDto dto = ReportPrmtlist.get(i);
//			
//			//拼写报表参数给出的查询条件
//			String sqllike=" ";
//			String[] strarr = dto.getScompute().trim().split(","); 
//			for(int j=0;j<strarr.length;j++){
//				if(j==0){
//					sqllike += " S_BUDGETSUBCODE like '"+strarr[j].trim()+"' ";
//				}
//				else{
//					sqllike += " or S_BUDGETSUBCODE like '"+strarr[j].trim()+"' ";
//				}
//			}
//			sqllike = " and ( "+sqllike+" ) ";
//			
//			 String sql = " union all select '"+dto.getSsubjectcode()+"' as S_BUDGETSUBCODE, '"+dto.getSsubjectname()+"' as S_BUDGETSUBNAME,-N_MONEYDAY,-N_MONEYTENDAY,-N_MONEYMONTH,-N_MONEYQUARTER,-N_MONEYYEAR "
//						+ " from TR_INCOMEDAYRPT a Where 1=1 " + sqllike
//						+ sqlwhere
//						+ " union all "
//						+ " select '"+dto.getSsubjectcode()+"' as S_BUDGETSUBCODE, '"+dto.getSsubjectname()+"' as S_BUDGETSUBNAME,-N_MONEYDAY,-N_MONEYTENDAY,-N_MONEYMONTH,-N_MONEYQUARTER,-N_MONEYYEAR "
//						+ " from HTR_INCOMEDAYRPT a Where 1=1 " + sqllike
//						+ sqlwhere;
//			 sqlbuf.append(sql);
//		}
		
		sqlbuf.append(") select S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
		
		//打印sql
		log.debug(sqlbuf.toString());
		
		SQLExecutor exec;
		try {
			exec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			
			List<TrIncomedayrptDto> list = (List<TrIncomedayrptDto>) exec.runQueryCloseCon(sqlbuf.toString(),
					TrIncomedayrptDto.class, true).getDtoCollection();
			
			BigDecimal zero= ArithUtil.round(new BigDecimal(0.0000), 2) ;
			//设置显示顺序
			List<TrIncomedayrptDto> newlist = new ArrayList<TrIncomedayrptDto>();
			for(int i=0; i<ReportPrmtlist.size(); i++){
				Boolean flag = false;
				for(int j=0;j<list.size();j++){
					if(ReportPrmtlist.get(i).getSsubjectname().equals(list.get(j).getSbudgetsubname())){
						newlist.add(list.get(j));
						flag = true;
						break;
					}
				}
				if(!flag){
					//如果没有显示数据，就全部设置为0
					TrIncomedayrptDto dto = new TrIncomedayrptDto();
//					S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR
					dto.setSbudgetsubcode(ReportPrmtlist.get(i).getSsubjectcode());
					dto.setSbudgetsubname(ReportPrmtlist.get(i).getSsubjectname());
					dto.setNmoneyday(zero);
					dto.setNmoneytenday(zero);
					dto.setNmoneymonth(zero);
					dto.setNmoneyquarter(zero);
					dto.setNmoneyyear(zero);
					newlist.add(dto);
				}
			}
			return newlist;
		
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}
	
	/**
	 * 取得报表参数信息
	 * 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List<TsReportmainDto> getReportPrmt(String sbillcode) throws ITFEBizException {
		
		//S_BILLCODE, S_SUBJECTCODE, S_SUBJECTNAME, S_COMPUTE, S_SEQ from TS_REPORTMAIN
		String sql =" select * from TS_REPORTMAIN where S_BILLCODE = ? order by S_BILLCODE,S_SEQ ";
		SQLExecutor exec;
		try {
			exec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			
			exec.addParam(sbillcode);
			List<TsReportmainDto> list = (List<TsReportmainDto>) exec.runQueryCloseCon(sql,
					TsReportmainDto.class, true).getDtoCollection();
			
			return list;
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("取得报表参数出错", e);
		}
	}
	
	private String makeIncomeBillwhere(IDto idto, String sleSumItem, String sleSubjectType,
			String rptDate,String sbillkind) throws ITFEBizException{
		
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;
		
		/**
		 * 查询逻辑：
		 * 报表日期：报表生成日期
		 * 收款国库代码：国库代码
		 * 征收机关代码： 具体征收机关 + 000000000000不分征收机关,111111111111国税,222222222222地税,333333333333海关,444444444444财政,555555555555其它
		 * 科目类型：01一般预算内02一般预算外03预算内基金04预算内债务05预算外基金06共用07转移性收入---sleSubjectType
		 * 预算种类： 1预算内,2预算外
		 * 预算级次： 1 中央,2 省,3 市,4 县,5 乡,6地方
		 * 辖属标志： 0全辖,1本级,2全辖非本级	
		 * 含项合计: 1 不含项合计,2含项合计----sleSumItem
		 * 调整期标志：0正常期1调整期
		 */
		// 查询sql 国库代码，辖属标志，征收机关代码，查询日期，预算种类，预算级次，调整期,报表类型
		String sqlwhere = "";
		
		/**收款国库代码和辖属标志、征收机关联合查询
		 * 辖属标志如果本级，那查询条件为当前国库代码+本级标志 --查询某一个征收机关 或查询征收机关大类的时候查询当前性质下所有的征收机关
		 * 辖属标志如果全辖，那查询条件为当前国库代码+全辖标志 --只查询征收机关大类
		 * 辖属标志如果全辖非本级，那查询条件为当前输入国库代码下辖的所有国库（不包括本级国库）+全辖标志 -- 征收机关出大类 查询当前大类下的所有征收机关
		 */
		if (null == dto.getStrecode()
				&& "".equals(dto.getStrecode().trim())) {
			throw new ITFEBizException("查询条件：国库代码不能为空！");
		}
		if (null == dto.getSbelongflag()
				&& "".equals(dto.getSbelongflag().trim())) {
			throw new ITFEBizException("查询条件：辖属标志不能为空！");
		}
		if (null == dto.getStaxorgcode()
				&& "".equals(dto.getStaxorgcode().trim())) {
			throw new ITFEBizException("查询条件：征收机关代码不能为空！");
		}
		
		//全辖--征收机关大类--国库代码
		if(MsgConstant.RULE_SIGN_ALL.equals(dto.getSbelongflag().trim())){
			if(dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_NATION_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_PLACE_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_OTHER_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)
					){
				sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' "+" and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' "+" and a.S_BELONGFLAG ='"+dto.getSbelongflag().trim()+"' ";
			}
			else{
				throw new ITFEBizException("查询条件：辖属标志位全辖的时候，征收机关只允许选择征收大类！");
			}
		}
		
		//本级--征收机关大类或具体征收机关--国库代码
		if(MsgConstant.RULE_SIGN_SELF.equals(dto.getSbelongflag().trim())){
			if(dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_NATION_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_PLACE_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_OTHER_CLASS) ){//征收机关大类
				sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' "
				+" and a.s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"+dto.getStrecode().trim()+"' AND S_TAXPROP='"+dto.getStaxorgcode().trim().subSequence(0, 1)+"' ) "
				+" and a.S_BELONGFLAG ='"+dto.getSbelongflag().trim()+"' ";
			}
			else if(dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)){//不分征收机关
				sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' "
				+" and a.s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) "
				+" and a.S_BELONGFLAG ='"+dto.getSbelongflag().trim()+"' ";
			}
			else{//具体征收机关
				sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' "+" and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' "+" and a.S_BELONGFLAG ='"+dto.getSbelongflag().trim()+"' ";
			}
		}
		
		//全辖非本级--征收机关大类--国库代码：所有辖属国库的全辖数据之和，但不包括本级国库全辖数
		if(MsgConstant.RULE_SIGN_ALLNOTSELF.equals(dto.getSbelongflag().trim())){
			if(dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_NATION_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_PLACE_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_OTHER_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)
					){
				//如果是市库查询市下所有全辖数，如果是县库查询县下所有全辖数
				if(dto.getStrecode().equals("0603000000")){
					sqlwhere += " and a.s_trecode in ( "+" SELECT s_trecode FROM TS_TREASURY WHERE substr(s_trecode,1,4)='"+dto.getStrecode().trim().substring(0, 4)+"' and s_trelevel='4') "+" and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' "+" and a.S_BELONGFLAG ='"+MsgConstant.RULE_SIGN_ALL+"' ";
				}
				else{
					sqlwhere += " and a.s_trecode in ( "+" SELECT s_trecode FROM TS_TREASURY WHERE substr(s_trecode,1,6)='"+dto.getStrecode().trim().substring(0, 6)+"' and s_trelevel='5') "+" and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' "+" and a.S_BELONGFLAG ='"+MsgConstant.RULE_SIGN_ALL+"' ";
				}
//				sqlwhere += " and a.s_trecode in ( "+" SELECT S_TRECODE FROM TS_TREASURY WHERE S_GOVERNTRECODE='"+dto.getStrecode().trim()+"' ) and a.s_trecode <> '"+dto.getStrecode().trim()+"' and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' "+" and a.S_BELONGFLAG ='"+MsgConstant.RULE_SIGN_ALL+"' ";
			}
			else{
				throw new ITFEBizException("查询条件：辖属标志位全辖非本级的时候，征收机关只允许选择征收大类！");
			}
		}
		
		/**
		 * 报表日期
		 */
		if (null != dto.getSrptdate()
				&& !"".equals(dto.getSrptdate().trim())) {
			sqlwhere += " and a.S_RPTDATE ='"+dto.getSrptdate().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：报表日期不能为空！");
		}
		
		/**
		 * 预算种类
		 */
		if (null != dto.getSbudgettype()
				&& !"".equals(dto.getSbudgettype().trim())) {
			sqlwhere += " and a.s_BudgetType ='"+dto.getSbudgettype().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：预算种类不能为空！");
		}
		
		/**预算级次条件判断
		 * 当预算级次为地方时，查询所有级次未非共享 非中央的数据 
		 */
		if (null != dto.getSbudgetlevelcode() && !"".equals(dto.getSbudgetlevelcode().trim())) {
			
			if(MsgConstant.BUDGET_LEVEL_PLACE.equals(dto.getSbudgetlevelcode().trim())){//地方
				//如果是市库查询市下面的，如果是县库查询县下面的
				if(dto.getStrecode().equals("0603000000")){
					sqlwhere += " and a.S_BUDGETLEVELCODE >='"+MsgConstant.BUDGET_LEVEL_DISTRICT+"' ";
				}
				else{
					sqlwhere += " and a.S_BUDGETLEVELCODE >='"+MsgConstant.BUDGET_LEVEL_PREFECTURE+"' ";
				}
			}
			else{//其他
				sqlwhere += " and a.S_BUDGETLEVELCODE ='"+dto.getSbudgetlevelcode().trim()+"' ";
			}
		}
		else{
			throw new ITFEBizException("查询条件：预算级次不能为空！");
		}
		
		/**
		 * 调整期标志
		 */
		if (null != dto.getStrimflag()
				&& !"".equals(dto.getStrimflag().trim())) {
			sqlwhere += " and a.s_TrimFlag ='"+dto.getStrimflag().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：调整期标志不能为空！");
		}
		
		/**
		 * 报表种类
		 */
		sqlwhere += " and a.S_BILLKIND ='"+sbillkind+"' ";
		
		/**
		 * 科目类型 sleSubjectType 由于现在科目类型在报表的最下面按项单独统计，所以这个可以不加入条件中
		 */
		
		/**
		 * 由于各核算主体单独一套预算科目代码，因此查询的时候应加上科目代码的核算主体条件
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_ORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		
		
		return sqlwhere;
	}

	private String makeAllTaxBillwhere(IDto idto, String billCode, String moneyUnit,
			String rptDate) throws ITFEBizException {
		
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;
		
		//and a.s_trecode =? and a.S_RPTDATE = ? and a.S_BELONGFLAG = ? and a.S_TRIMFLAG = ?
		String sqlwhere = "";

		/**
		 * 国库代码
		 */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：国库代码不能为空！");
		}
		
		/**
		 * 报表日期
		 */
		if (null != dto.getSrptdate()
				&& !"".equals(dto.getSrptdate().trim())) {
			sqlwhere += " and a.S_RPTDATE ='"+dto.getSrptdate().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：报表日期不能为空！");
		}
		
		/**
		 * 辖属标志
		 */
		if (null != dto.getSbelongflag()
				&& !"".equals(dto.getSbelongflag().trim())) {
			sqlwhere += " and a.S_BELONGFLAG ='"+dto.getSbelongflag().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：辖属标志不能为空！");
		}
		
		/**
		 * 调整期标志
		 */
		if (null != dto.getStrimflag()
				&& !"".equals(dto.getStrimflag().trim())) {
			sqlwhere += " and a.s_TrimFlag ='"+dto.getStrimflag().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：调整期标志不能为空！");
		}
		
		/**
		 * 由于各核算主体单独一套预算科目代码，因此查询的时候应加上科目代码的核算主体条件
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_BOOKORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		
		return sqlwhere;
	}
	
	/**
	 * 财政支出日报
	 * 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List makePayoutBill(IDto idto, String sleSumItem, String moneyUnit,
			String rptDate) throws ITFEBizException {
//		TrTaxorgPayoutReportDto payoutdto = (TrTaxorgPayoutReportDto) idto;
		String sqlwhere = makePayoutBillwhere(idto,sleSumItem,moneyUnit,rptDate);
		
		StringBuffer sqlbuf = new StringBuffer(
		"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
		
		String sql = 
				//目
				"select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (select * from TR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME union all "
				+ "  select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (select * from HTR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME union all "
				
				//项
				+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (select * from TR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME union all "
				+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
				+ " from (select * from HTR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME union all ";
				
				//款
				//是否含款合计:默认为不含款合计
				if(sleSumItem ==null || "".equals(sleSumItem) || StateConstant.REPORTTYPE_0406_YES.equals(sleSumItem)){
					sql += "  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
					+ " from (select * from TR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME union all "
					+ "  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
					+ " from (select * from HTR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME union all ";
				}
				
				//类
				sql += 
				  "  select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (select * from TR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME union all "
				+ "  select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
				+ " from (select * from HTR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME order by S_BUDGETSUBCODE,S_BUDGETSUBNAME ";
				sqlbuf.append(sql);
				sqlbuf.append(") select S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		
		//合计sql
		StringBuffer sqlsumbuf = new StringBuffer(
		"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
		
		String sqlsum = 
			//目
			"select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
			+ " from (select * from TR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME union all "
			+ "  select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
			+ " from (select * from HTR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME ";
			
		sqlsumbuf.append(sqlsum);
		sqlsumbuf.append(") select '' as S_BUDGETSUBCODE,'合计数' as S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp ");
				
		try {
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			//打印sql
			log.debug(sqlbuf.toString());
			log.debug(sqlsumbuf.toString());
			
			//按科目
			List list = (List)exec.runQueryCloseCon(sqlbuf.toString(), TrTaxorgPayoutReportDto.class,true).getDtoCollection();
			//合计
			
			SQLExecutor exec1 = DatabaseFacade.getODB().getSqlExecutorFactory()
			.getSQLExecutor();
			
			List listall=(List)exec1.runQueryCloseCon(sqlsumbuf.toString(), TrTaxorgPayoutReportDto.class,true).getDtoCollection();
			list.addAll(listall);
			
			return list;
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}
	
	private String makePayoutBillwhere(IDto idto, String sleSumItem, String moneyUnit,
			String rptDate) throws ITFEBizException {
		
		TrTaxorgPayoutReportDto dto = (TrTaxorgPayoutReportDto) idto;
		String sqlwhere = "";

		/**
		 * 国库代码
		 */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：国库代码不能为空！");
		}
		
		/**
		 * 预算单位代码--用S_FINORGCODE这个代替，见DataMoveUtil
		 */
		if (null != dto.getSfinorgcode()
				&& !"".equals(dto.getSfinorgcode().trim())) {
			sqlwhere += " and a.S_FINORGCODE ='"+dto.getSfinorgcode().trim()+"' ";
		}
		
		/**
		 * 报表日期
		 */
		if (null != dto.getSrptdate()
				&& !"".equals(dto.getSrptdate().trim())) {
			sqlwhere += " and a.S_RPTDATE ='"+dto.getSrptdate().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：报表日期不能为空！");
		}
		
		/**
		 * 预算种类
		 */
		if (null != dto.getSbudgettype()
				&& !"".equals(dto.getSbudgettype().trim())) {
			sqlwhere += " and a.s_BudgetType ='"+dto.getSbudgettype().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：预算种类不能为空！");
		}
		
		/**
		 * 预算级次--用预算级次代码当做报表名称类型（1一般预算支出，2人行办理授权支付）在枚举值里面定义0319
		 */
		if (null != dto.getSbudgetlevelcode()
				&& !"".equals(dto.getSbudgetlevelcode().trim())) {
			if(dto.getSbudgetlevelcode().equals(MsgConstant.MSG_RPORT_NAME2)){
				sqlwhere += " and a.S_BUDGETLEVELCODE ='"+dto.getSbudgetlevelcode().trim()+"' ";
			}
			else{
				;
			}
		}
		else{
			throw new ITFEBizException("查询条件：报表名称不能为空！");
		}
		
		/**
		 * 由于各核算主体单独一套预算科目代码，因此查询的时候应加上科目代码的核算主体条件
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_ORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		
		return sqlwhere;
	}
	
	

	public List makeFinincomeOnlineBill(IDto idto, String startdate,String enddate,String remak)
			throws ITFEBizException {
		
		/*TrFinIncomeonlineDayrptDto dto = (TrFinIncomeonlineDayrptDto) idto;
		String sleBillType=dto.getSremark1().trim(); //取得报表类型 月 年
		
		//月报查询基础数据 就是当月发生时间最后的 因为月年金额是累加的，所以取最后一个就是最新的合计
		String sqlmonth=" ( "+
//						" select t.* from TR_FIN_INCOMEONLINE_DAYRPT t, "+
//						" (select S_BDGSBTCODE,S_TRECODE,max(S_ACCT) S_ACCT,S_TAXPAYCODE,S_TAXORGCODE "+ 
//						" from TR_FIN_INCOMEONLINE_DAYRPT where substr(S_ACCT,1,6)='"+rptDate.substring(0, 6)+"' group by S_BDGSBTCODE,S_TRECODE,S_TAXPAYCODE,S_TAXORGCODE) s "+
//						" where t.S_BDGSBTCODE=s.S_BDGSBTCODE and t.S_TRECODE=s.S_TRECODE and t.S_ACCT=s.S_ACCT and t.S_TAXPAYCODE=s.S_TAXPAYCODE and t.S_TAXORGCODE=s.S_TAXORGCODE "+
//						" ) ";
		
		//年报查询基础数据
		String sqlyear=" ( "+
//						" select t.* from TR_FIN_INCOMEONLINE_DAYRPT t, "+
//						" (select S_BDGSBTCODE,S_TRECODE,max(S_ACCT) S_ACCT,S_TAXPAYCODE,S_TAXORGCODE "+ 
//						" from TR_FIN_INCOMEONLINE_DAYRPT where substr(S_ACCT,1,6)='"+rptDate.substring(0, 4)+"' group by S_BDGSBTCODE,S_TRECODE,S_TAXPAYCODE,S_TAXORGCODE) s "+
//						" where t.S_BDGSBTCODE=s.S_BDGSBTCODE and t.S_TRECODE=s.S_TRECODE and t.S_ACCT=s.S_ACCT and t.S_TAXPAYCODE=s.S_TAXPAYCODE and t.S_TAXORGCODE=s.S_TAXORGCODE "+
//						" ) ";
		
		StringBuffer sqlbuf = new StringBuffer(
		"with tmp(S_ACCT,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXORGCODE,S_BDGSBTCODE,S_BDGSBTNAME, "
			+" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER,  "
			+" N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR,S_Remark1 ) as (");
		
		// 修改只查询预算内的数据 20110326 S_BUDGETTYPE='2' MsgConstant.BDG_KIND_IN
		String sql = 
			  " select a.S_ACCT,a.S_TAXPAYCODE,a.S_TAXPAYNAME,a.S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, "
			 +" a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH, a.N_MONEYQUARTER, a.N_MONEYYEAR, a.N_CENTERMONEYDAY, a.N_CENTERMONEYTENDAY, a.N_CENTERMONEYMONTH, a.N_CENTERMONEYQUARTER, "  
			 +" a.N_CENTERMONEYYEAR, a.N_PLACEMONEYDAY, a.N_PLACEMONEYTENDAY, a.N_PLACEMONEYMONTH, a.N_PLACEMONEYQUARTER, a.N_PLACEMONEYYEAR,'' as S_Remark1 " 
			 +" from TR_FIN_INCOMEONLINE_DAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_IN+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
			 +" union all "
			 +" select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, " 
			 +" sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY, " 
			 +" sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY, " 
			 +" sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'小计' as S_Remark1 " 
			 +" from TR_FIN_INCOMEONLINE_DAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_IN+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
			 +" group by a.S_BDGSBTCODE,b.S_SUBJECTNAME "
			 +" union all "  
			 +" select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXORGCODE,'' as S_BDGSBTCODE,'' as S_BDGSBTNAME, " 
			 +" sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY, " 
			 +" sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY, " 
			 +" sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'合计' as S_Remark1 " 
			 +" from TR_FIN_INCOMEONLINE_DAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_IN+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere;
		
//		sql += " union all " + sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", "HTR_FIN_INCOMEONLINE_DAYRPT");
		
		// 区支库下划企业统计日报表查询
		if (StateConstant.REPORT_DAY.equals(sleBillType)) {
			;
		}
		// 区支库下划企业统计旬报表查询
		else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
			throw new ITFEBizException("目前不支持区支库下划企业统计旬报表查询！！");
		}
		// 区支库下划企业统计月报表查询
		else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
			sql = sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", sqlmonth);
		}
		// 区支库下划企业统计季报表查询
		else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
			throw new ITFEBizException("目前不支持区支库下划企业统计季报表查询！！");
		}
		// 区支库下划企业统计年报表查询
		else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
			sql = sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", sqlyear);
		}
		sqlbuf.append(sql);
				
		sqlbuf.append(") select S_ACCT,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXORGCODE,S_BDGSBTCODE,S_BDGSBTNAME, "
			+" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER,  "
			+" N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR,S_Remark1 from tmp order by S_BDGSBTCODE desc,S_BDGSBTNAME desc ");


//暂存
 sqlbuf = new StringBuffer(
					"with tmp(S_ACCT,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXVOUNO,S_TAXORGCODE, S_BDGSBTCODE,S_BDGSBTNAME,S_TRECODE, "
					+" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER,  "
					+" N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR,S_Remark1 ) as ( ");
			
			String sql = 
					" select a.S_ACCT,a.S_TAXPAYCODE,a.S_TAXPAYNAME,a.S_TAXVOUNO,a.S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME,a.S_TRECODE, "
					+" a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH, a.N_MONEYQUARTER, a.N_MONEYYEAR, a.N_CENTERMONEYDAY, a.N_CENTERMONEYTENDAY, a.N_CENTERMONEYMONTH, a.N_CENTERMONEYQUARTER, "   
					+"  a.N_CENTERMONEYYEAR, a.N_PLACEMONEYDAY, a.N_PLACEMONEYTENDAY, a.N_PLACEMONEYMONTH, a.N_PLACEMONEYQUARTER, a.N_PLACEMONEYYEAR,'' as S_Remark1  "
					+" from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere 
					+"  union all "
					+"  select a.S_ACCT,'' as S_TAXPAYCODE, '' as S_TAXPAYNAME ,'' as S_TAXVOUNO,a.S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, '' as S_TRECODE,   "
					+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,"  
					+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
					+"   sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'小计' as S_Remark1  "
					+"   from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
					+"   group by a.S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME,a.S_ACCT "
					+"  union all "
					+"  select a.S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,'' as S_BDGSBTCODE,'' as S_BDGSBTNAME, '' as S_TRECODE,   "
					+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,  "
					+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
					+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'日累计' as S_Remark1  "
					+"  from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
					+"  group by a.S_ACCT "
					+"  union all "
//					+"  select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, '' as S_TRECODE, "
//					+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,  "
//					+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
//					+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'科目小计' as S_Remark1  "
//					+"  from TR_FIN_INCOMEONLINE_DAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
//					+"  group by a.S_BDGSBTCODE,b.S_SUBJECTNAME "
//					+"  union all "   
					+"  select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,'' as S_BDGSBTCODE,'' as S_BDGSBTNAME, '' as S_TRECODE,  "
					+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,"  
					+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
					+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'合计' as S_Remark1  "
					+"  from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere;
			
			sql += " union all " + sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", "HTR_FIN_INCOMEONLINE_DAYRPT");
			
			sqlbuf.append(sql);
					
			sqlbuf.append(") select S_TAXVOUNO,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXORGCODE,S_ACCT,S_BDGSBTCODE,S_BDGSBTNAME,S_TRECODE, "
						+" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER, " 
						+"N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR,S_Remark1 from tmp order by s_acct,S_BDGSBTCODE ,S_TAXORGCODE,s_taxpaycode ");

*/
		StringBuffer sqlbuf = new StringBuffer("");
		
		if(remak.equals("3")){//款汇总
			sqlbuf = makeFinincomeOnlineBillSql(idto, startdate, enddate, remak);
		}else if(remak.equals("4")){//项汇总
			sqlbuf = makeFinincomeOnlineSql(idto, startdate, enddate, remak);
		}else{//目汇总
			String sqlwhere = makeFinincomeOnlineBillwhere(idto,startdate,enddate,remak);
			TrFinIncomeonlineDayrptDto dto = (TrFinIncomeonlineDayrptDto) idto;
			sqlbuf = new StringBuffer(" with tmp(S_TAXPAYCODE,S_TAXPAYNAME, S_BDGSBTCODE,S_BDGSBTNAME,S_TRECODE,"
					+" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER,  "
					+" N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR) as ( ");
			
			sqlbuf.append(" select  S_TAXPAYCODE AS S_TAXPAYCODE,S_TAXPAYNAME AS S_TAXPAYNAME,S_BDGSBTCODE AS S_BDGSBTCODE,S_BDGSBTNAME as S_BDGSBTNAME,S_TRECODE as S_TRECODE,sum(N_MONEYDAY) as N_MONEYDAY," 
					+   " sum(N_MONEYTENDAY) as N_MONEYTENDAY,sum(N_MONEYMONTH) as N_MONEYMONTH,sum(N_MONEYQUARTER) as N_MONEYQUARTER,sum(N_MONEYYEAR) as N_MONEYYEAR,sum(N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY, " 
					+	" sum(N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(N_PLACEMONEYDAY) as N_PLACEMONEYDAY, sum(N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,"
					+	" sum(N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR from (");
			
			String sql = " select  a.S_TAXPAYCODE AS S_TAXPAYCODE,a.S_TAXPAYNAME AS S_TAXPAYNAME,a.S_BDGSBTCODE AS S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME,a.S_TRECODE as S_TRECODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY," 
				+	" sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY, sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,"
				+	" sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY, sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH," 
				+	" sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR from ( " 
				+	" SELECT DISTINCT S_TRECODE,S_BDGSBTCODE,S_BDGSBTNAME,S_ACCT,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXORGCODE,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR,N_CENTERMONEYDAY,N_CENTERMONEYTENDAY,N_CENTERMONEYMONTH,N_CENTERMONEYQUARTER," 
				+	" N_CENTERMONEYYEAR,N_PLACEMONEYDAY,N_PLACEMONEYTENDAY,N_PLACEMONEYMONTH,N_PLACEMONEYQUARTER,N_PLACEMONEYYEAR,S_REMARK1,S_TAXVOUNO FROM TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >='"+startdate+"'  and S_ACCT <='"+enddate+"' and S_TRECODE = '"+dto.getStrecode().trim()+"') a,TS_BUDGETSUBJECT b " 
				+	" Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' AND a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
				+	" group by a.S_TAXPAYCODE,a.S_TAXPAYNAME,a.S_BDGSBTCODE,b.S_SUBJECTNAME,a.S_TRECODE";
			
			
			sql += " union all " + sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", "HTR_FIN_INCOMEONLINE_DAYRPT");
			
			sqlbuf.append(sql);
			
			sqlbuf.append(" ) as c where 1=1 group by c.S_TAXPAYCODE,c.S_TAXPAYNAME,c.S_BDGSBTCODE,c.S_BDGSBTNAME,c.S_TRECODE");
					
			sqlbuf.append(" ) select S_TAXPAYCODE,S_TAXPAYNAME,S_BDGSBTCODE,S_BDGSBTNAME,S_TRECODE,"
						+	" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER, " 
						+	" N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR from tmp ORDER BY S_TAXPAYCODE, S_TAXPAYNAME,N_MONEYDAY DESC,S_BDGSBTCODE,S_BDGSBTNAME ");
		}
		
		try {
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			//打印sql
			log.debug(sqlbuf.toString());
			
			List<TrFinIncomeonlineDayrptDto> list= (List) exec.runQueryCloseCon(sqlbuf.toString(), TrFinIncomeonlineDayrptDto.class,true).getDtoCollection();
//			Collections.reverse(list);
			
			//查全部
			return list;
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}
	
	
	public StringBuffer makeFinincomeOnlineBillSql(IDto idto,String startdate,String enddate,String remak) throws ITFEBizException {
		String sqlwhere = makeFinIncomeOnlineSqlwhere(idto, startdate, enddate, remak);
		TrFinIncomeonlineDayrptDto dto = (TrFinIncomeonlineDayrptDto) idto;
		
		StringBuffer sqlbuf = new StringBuffer(" with tmp(S_TAXPAYCODE,S_TAXPAYNAME,S_BDGSBTCODE,S_BDGSBTNAME, S_TRECODE,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR,N_CENTERMONEYDAY," 
				+	" N_CENTERMONEYTENDAY,N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER,N_CENTERMONEYYEAR,N_PLACEMONEYDAY,  N_PLACEMONEYTENDAY,N_PLACEMONEYMONTH,N_PLACEMONEYQUARTER,N_PLACEMONEYYEAR ) as ( ");
		
		sqlbuf.append(" select  S_TAXPAYCODE AS S_TAXPAYCODE,S_TAXPAYNAME AS S_TAXPAYNAME,S_BDGSBTCODE AS S_BDGSBTCODE,S_BDGSBTNAME as S_BDGSBTNAME,S_TRECODE as S_TRECODE,sum(N_MONEYDAY) as N_MONEYDAY," 
				+   " sum(N_MONEYTENDAY) as N_MONEYTENDAY,sum(N_MONEYMONTH) as N_MONEYMONTH,sum(N_MONEYQUARTER) as N_MONEYQUARTER,sum(N_MONEYYEAR) as N_MONEYYEAR,sum(N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY, " 
				+	" sum(N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(N_PLACEMONEYDAY) as N_PLACEMONEYDAY, sum(N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,"
				+	" sum(N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR from (");
		
		String sql = " select  a.S_TAXPAYCODE AS S_TAXPAYCODE,a.S_TAXPAYNAME AS S_TAXPAYNAME,substr(a.S_BDGSBTCODE ,1,5) AS S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME,a.S_TRECODE as S_TRECODE,sum(a.N_MONEYDAY) as N_MONEYDAY," 
			+   " sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY, " 
			+	" sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY, sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,"
			+	" sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR from ( "
			+	" SELECT DISTINCT S_TRECODE,S_BDGSBTCODE,S_BDGSBTNAME,S_ACCT,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXORGCODE,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR,N_CENTERMONEYDAY,N_CENTERMONEYTENDAY,N_CENTERMONEYMONTH,"
			+	" N_CENTERMONEYQUARTER,N_CENTERMONEYYEAR,N_PLACEMONEYDAY,N_PLACEMONEYTENDAY,N_PLACEMONEYMONTH,N_PLACEMONEYQUARTER,N_PLACEMONEYYEAR,S_REMARK1,S_TAXVOUNO FROM TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >='"+startdate+"'  and S_ACCT <='"+enddate+"' and S_TRECODE = '"+dto.getStrecode().trim()+"') a,TS_BUDGETSUBJECT b " 
			+	" Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' AND substr(a.S_BDGSBTCODE,1,5) = b.S_SUBJECTCODE AND length(a.S_BDGSBTCODE) >= 5 "+sqlwhere
			+	" group by a.S_TAXPAYCODE,a.S_TAXPAYNAME,substr(a.S_BDGSBTCODE ,1,5),b.S_SUBJECTNAME,a.S_TRECODE";
		
		sql += " union all " + sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", "HTR_FIN_INCOMEONLINE_DAYRPT");
		
		sqlbuf.append(sql);
			
		sqlbuf.append(" ) as c where 1=1 group by c.S_TAXPAYCODE,c.S_TAXPAYNAME,c.S_BDGSBTCODE,c.S_BDGSBTNAME,c.S_TRECODE");
		
		sqlbuf.append(" ) SELECT  S_TAXPAYCODE,S_TAXPAYNAME,S_BDGSBTCODE,S_BDGSBTNAME, S_TRECODE,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR,N_CENTERMONEYDAY,N_CENTERMONEYTENDAY,N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER,N_CENTERMONEYYEAR,N_PLACEMONEYDAY, "
				+	" N_PLACEMONEYTENDAY,N_PLACEMONEYMONTH,N_PLACEMONEYQUARTER,N_PLACEMONEYYEAR from tmp  ORDER BY S_TAXPAYCODE, S_TAXPAYNAME,N_MONEYDAY DESC,S_BDGSBTCODE,S_BDGSBTNAME ");
		
		return sqlbuf;
		
	}
	
	
	
	
	public StringBuffer makeFinincomeOnlineSql(IDto idto,String startdate,String enddate,String remak) throws ITFEBizException {
		String sqlwhere = makeFinIncomeOnlineSqlwhere(idto, startdate, enddate, remak);
		TrFinIncomeonlineDayrptDto dto = (TrFinIncomeonlineDayrptDto) idto;
		StringBuffer sqlbuf = new StringBuffer(" with tmp(S_TAXPAYCODE,S_TAXPAYNAME,S_BDGSBTCODE,S_BDGSBTNAME,S_TRECODE, N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR,N_CENTERMONEYDAY," 
				+	" N_CENTERMONEYTENDAY,N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER,N_CENTERMONEYYEAR,N_PLACEMONEYDAY,  N_PLACEMONEYTENDAY,N_PLACEMONEYMONTH,N_PLACEMONEYQUARTER,N_PLACEMONEYYEAR ) as ( ");
		

		sqlbuf.append(" select  S_TAXPAYCODE AS S_TAXPAYCODE,S_TAXPAYNAME AS S_TAXPAYNAME,S_BDGSBTCODE AS S_BDGSBTCODE,S_BDGSBTNAME as S_BDGSBTNAME,S_TRECODE as S_TRECODE,sum(N_MONEYDAY) as N_MONEYDAY," 
				+   " sum(N_MONEYTENDAY) as N_MONEYTENDAY,sum(N_MONEYMONTH) as N_MONEYMONTH,sum(N_MONEYQUARTER) as N_MONEYQUARTER,sum(N_MONEYYEAR) as N_MONEYYEAR,sum(N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY, " 
				+	" sum(N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(N_PLACEMONEYDAY) as N_PLACEMONEYDAY, sum(N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,"
				+	" sum(N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR from (");
		
		String sql = " select  a.S_TAXPAYCODE AS S_TAXPAYCODE,a.S_TAXPAYNAME AS S_TAXPAYNAME,substr(a.S_BDGSBTCODE ,1,7) AS S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME,a.S_TRECODE as S_TRECODE,sum(a.N_MONEYDAY) as N_MONEYDAY," 
			+   " sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY, " 
			+	" sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY, sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,"
			+	" sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR from ( "
			+	" SELECT DISTINCT S_TRECODE,S_BDGSBTCODE,S_BDGSBTNAME,S_ACCT,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXORGCODE,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR,N_CENTERMONEYDAY,N_CENTERMONEYTENDAY,N_CENTERMONEYMONTH,"
			+	" N_CENTERMONEYQUARTER,N_CENTERMONEYYEAR,N_PLACEMONEYDAY,N_PLACEMONEYTENDAY,N_PLACEMONEYMONTH,N_PLACEMONEYQUARTER,N_PLACEMONEYYEAR,S_REMARK1,S_TAXVOUNO FROM TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >='"+startdate+"'  and S_ACCT <='"+enddate+"' and S_TRECODE = '"+dto.getStrecode().trim()+"') a,TS_BUDGETSUBJECT b " 
			+	" Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' AND substr(a.S_BDGSBTCODE,1,7) = b.S_SUBJECTCODE AND length(a.S_BDGSBTCODE) >= 7 "+sqlwhere
			+	" group by a.S_TAXPAYCODE,a.S_TAXPAYNAME,substr(a.S_BDGSBTCODE ,1,7),b.S_SUBJECTNAME,a.S_TRECODE";
		
		sql += " union all " + sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", "HTR_FIN_INCOMEONLINE_DAYRPT");
		
		sqlbuf.append(sql);
		
		sqlbuf.append(" ) as c where 1=1 group by c.S_TAXPAYCODE,c.S_TAXPAYNAME,c.S_BDGSBTCODE,c.S_BDGSBTNAME,c.S_TRECODE");
				
		sqlbuf.append(" ) SELECT  S_TAXPAYCODE,S_TAXPAYNAME,S_BDGSBTCODE,S_BDGSBTNAME, S_TRECODE,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR,N_CENTERMONEYDAY,N_CENTERMONEYTENDAY,N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER,N_CENTERMONEYYEAR,N_PLACEMONEYDAY, "
				+	" N_PLACEMONEYTENDAY,N_PLACEMONEYMONTH,N_PLACEMONEYQUARTER,N_PLACEMONEYYEAR from tmp  ORDER BY S_TAXPAYCODE, S_TAXPAYNAME,N_MONEYDAY DESC,S_BDGSBTCODE,S_BDGSBTNAME ");
		
		return sqlbuf;
		
	}
	
	
	
	/**
	 * 取得纳税人和国库对应关系
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public static List<TsTaxpaycodeDto> getTsTaxpaycodeDtolist(TrFinIncomeonlineDayrptDto dto) throws ITFEBizException {
		String where = " WHERE S_TAXPAYCODE = '" + dto.getStaxpaycode()+"' and S_TRECODE='"+dto.getStrecode()+"' ";
		 try {
			return DatabaseFacade.getDb().find(TsTaxpaycodeDto.class, where);
		} catch (JAFDatabaseException e) {
			log.error("取得纳税人和国库对应关系时错误!", e);
			throw new ITFEBizException("取得纳税人和国库对应关系错误!", e);
		}
	}
	
	private String makeFinincomeOnlineBillwhere(IDto idto,String startdate,String enddate,String remak) throws ITFEBizException {
		
		TrFinIncomeonlineDayrptDto dto = (TrFinIncomeonlineDayrptDto) idto;
		String sqlwhere = "";

		/**
		 * 国库代码
		 */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：国库代码不能为空！");
		} 
		/**
		 * 征收机关代码
		 */
		if (null != dto.getStaxorgcode()
				&& !"".equals(dto.getStaxorgcode().trim())) {
			sqlwhere += " and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' ";
		}
		
		/**
		 * 预算科目代码
		 */
		if (null != dto.getSbdgsbtcode()
				&& !"".equals(dto.getSbdgsbtcode().trim())) {
			sqlwhere += " and a.s_BdgSbtCode ='"+dto.getSbdgsbtcode().trim()+"' ";
		}
		
		/**
		 * 预算科目名称
		 */
		if (null != dto.getSbdgsbtname()
				&& !"".equals(dto.getSbdgsbtname().trim())) {
			sqlwhere += " and a.s_BdgSbtName ='"+dto.getSbdgsbtname().trim()+"' ";
		}
		
		/**
		 * 预算属性
		 */
		sqlwhere += " and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_IN+"' ";
		
		/**
		 * 纳税人代码
		 */
		if (null != dto.getStaxpaycode()
				&& !"".equals(dto.getStaxpaycode().trim())) {
			sqlwhere += " and a.s_TaxPayCode ='"+dto.getStaxpaycode().trim()+"' ";
		}
		
		/**
		 * 纳税人名称
		 */
		if (null != dto.getStaxpayname()
				&& !"".equals(dto.getStaxpayname().trim())) {
			sqlwhere += " and a.s_TaxPayName ='"+dto.getStaxpayname().trim()+"' ";
		}
		
		/**
		 * 账务日期
		 */
//		if (null != dto.getSacct()
//				&& !"".equals(dto.getSacct().trim())) {
//			sqlwhere += " and a.S_ACCT ='"+dto.getSacct().trim()+"' ";
//		}
//		else{
//			throw new ITFEBizException("查询条件：账务日期不能为空！");
//		}
		//修改20110326---用开始日期结束日期查询
		/**
		 * 开始日期
		 */
		if (null != startdate
				&& !"".equals(startdate.trim())) {
			sqlwhere += " and a.S_ACCT >='"+startdate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：开始日期不能为空！");
		}
		
		/**
		 * 结束日期
		 */
		if (null != enddate
				&& !"".equals(enddate.trim())) {
			sqlwhere += " and a.S_ACCT <='"+enddate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：结束日期不能为空！");
		}
		
		/**
		 * 由于各核算主体单独一套预算科目代码，因此查询的时候应加上科目代码的核算主体条件
		 */
//		sqlwhere += " and b.S_ORGCODE = '"+this.getLoginInfo().getSorgcode()+"' ";
		
		//mod 2011-09-06
		sqlwhere += " and b.S_ORGCODE in (SELECT S_BOOKORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		
		/**
		 * 如果查询为重点企业电子税收统计报表，加上这个
		 */
		if (remak.trim().equals("2")) {
			sqlwhere += " and exists (select 1 from TS_TAXPAYCODE o where o.S_TAXPAYCODE=a.S_TAXPAYCODE and o.S_TRECODE=a.S_TRECODE) ";
		}
		
		return sqlwhere;
	}
	
	
	
private String makeFinIncomeOnlineSqlwhere(IDto idto,String startdate,String enddate,String remak) throws ITFEBizException {
		
		TrFinIncomeonlineDayrptDto dto = (TrFinIncomeonlineDayrptDto) idto;
		String sqlwhere = "";

		/**
		 * 国库代码
		 * */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：国库代码不能为空！");
		}
		
		/**
		 * 预算属性
		 */
		sqlwhere += " and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_IN+"' ";
		
		/**
		 * 纳税人代码
		 */
		if (null != dto.getStaxpaycode()
				&& !"".equals(dto.getStaxpaycode().trim())) {
			sqlwhere += " and a.s_TaxPayCode ='"+dto.getStaxpaycode().trim()+"' ";
		}
		
		/**
		 * 纳税人名称
		 */
		if (null != dto.getStaxpayname()
				&& !"".equals(dto.getStaxpayname().trim())) {
			sqlwhere += " and a.s_TaxPayName ='"+dto.getStaxpayname().trim()+"' ";
		}
		
		//修改20110326---用开始日期结束日期查询
		/**
		 * 开始日期
		 */
		if (null != startdate
				&& !"".equals(startdate.trim())) {
			sqlwhere += " and a.S_ACCT >='"+startdate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：开始日期不能为空！");
		}
		
		/**
		 * 结束日期
		 */
		if (null != enddate
				&& !"".equals(enddate.trim())) {
			sqlwhere += " and a.S_ACCT <='"+enddate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：结束日期不能为空！");
		}
		
		
//		sqlwhere += " and b.S_ORGCODE = '"+this.getLoginInfo().getSorgcode()+"' ";
		/**
		 * 由于各核算主体单独一套预算科目代码，因此查询的时候应加上科目代码的核算主体条件
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_BOOKORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		
		/**
		 * 如果查询为重点企业电子税收统计报表，加上这个
		 */
		if (remak.trim().equals("2")) {
			sqlwhere += " and exists (select 1 from TS_TAXPAYCODE o where o.S_TAXPAYCODE=a.S_TAXPAYCODE and o.S_TRECODE=a.S_TRECODE) ";
		}
		
		return sqlwhere;
	}
	
	
	
	public HashMap<String, String[]> makeExcelInOutBill(IDto idto, String sleSumItem, String sleSubjectType, String rptDate, String rptType,
			String copyFilename,String reporttitle,String unitname)
			throws ITFEBizException {
		
		HashMap<String, String[]> datamap=null;
		
		//收入月报
		if(rptType.equals(StateConstant.RPT_TYPE_1)){
//			list=(List<TrIncomedayrptDto>)makeIncomeBill(idto,sleSumItem,sleSubjectType,rptDate);
			//取得基本报表数据
			List<TrIncomedayrptDto> list=(List<TrIncomedayrptDto>)getIncomeBill(idto,sleSumItem,sleSubjectType,rptDate);
			if(list==null || list.size()<=0){
				return null;
			}
			datamap=new HashMap<String, String[]>();
			for(TrIncomedayrptDto dto :list){
				datamap.put(dto.getSbudgetsubcode(), (new String[]{dto.getNmoneymonth().toString()}));
			}
		}
		//支出月报
		if(rptType.equals(StateConstant.RPT_TYPE_2)){
			List<TrTaxorgPayoutReportDto> list=(List<TrTaxorgPayoutReportDto>)getPayoutBill(idto,sleSumItem,sleSubjectType,rptDate);
			if(list==null || list.size()<=0){
				return null;
			}
			datamap=new HashMap<String, String[]>();
			for(TrTaxorgPayoutReportDto dto :list){
				datamap.put(dto.getSbudgetsubcode(), (new String[]{dto.getNmoneymonth().toString()}));
			}
		}
		//收支旬报
		if(rptType.equals(StateConstant.RPT_TYPE_3)){
			//list=null;
		}
		//全口径税收
		if(rptType.equals(StateConstant.RPT_TYPE_4)){
			List<ReportDto> list=(List<ReportDto>)getAllTaxBill(idto,sleSumItem,sleSubjectType,rptDate);
			if(list==null || list.size()<=0){
				return null;
			}
			int i =0;
			datamap=new HashMap<String, String[]>();
			for(i=0;i<list.size(); i++){
				ReportDto dto =list.get(i);
				BigDecimal sum = ArithUtil.add(ArithUtil.add(dto.getNcentralmoney(), dto.getNprovincemoney()),dto.getNcitymoney());
				datamap.put(dto.getSbudgetsubcode(), (new String[]{sum.toString(),dto.getNcentralmoney().toString(),dto.getNprovincemoney().toString(),dto.getNcitymoney().toString()}));
				System.out.println(dto.getSbudgetsubcode());
			}
			
			log.debug(i+datamap.size());
		}
		//年末收支调度表
		if(rptType.equals(StateConstant.RPT_TYPE_5)){
			
			datamap=new HashMap<String, String[]>();
			//取得基本报表数据
			List<TrIncomedayrptDto>list=(List<TrIncomedayrptDto>)makeIncomeBill(idto,sleSumItem,sleSubjectType,rptDate);
			if(list==null || list.size()<=0){
				;
			}
			else{
				for(TrIncomedayrptDto dto :list){
					datamap.put(dto.getSbudgetsubcode(), (new String[]{dto.getNmoneymonth().toString()}));
				}
			}

			TrTaxorgPayoutReportDto payoutdto = changeDto(idto);
			List<TrTaxorgPayoutReportDto> listout=(List<TrTaxorgPayoutReportDto>)makePayoutBill(payoutdto,sleSumItem,sleSubjectType,rptDate);
			if(listout==null || listout.size()<=0){
				;
			}
			else{
				for(TrTaxorgPayoutReportDto dto :listout){
					datamap.put(dto.getSbudgetsubcode(), (new String[]{dto.getNmoneymonth().toString()}));
				}
			}
			
			//地税数据
			List<TrIncomedayrptDto>list2=(List<TrIncomedayrptDto>)getIncomeBill2(idto,sleSumItem,sleSubjectType,rptDate);
			if(list2==null || list2.size()<=0){
				;
			}
			else{
				for(TrIncomedayrptDto dto :list2){
					datamap.put('d'+dto.getSbudgetsubcode(), (new String[]{dto.getNmoneymonth().toString()}));
				}	
			}	
		}
		
		return datamap;
	}
	
	/**
	 * 修改查询dto属性
	 * @param idto
	 */
	private TrTaxorgPayoutReportDto changeDto(IDto idto){
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;
		TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();
		String sqlwhere = "";
		
		/**
		 * 国库代码
		 */
		payoutdto.setStrecode(dto.getStrecode());
		/**
		 * 报表日期
		 */
		payoutdto.setSrptdate(dto.getSrptdate());
		/**
		 * 预算种类
		 */
		payoutdto.setSbudgettype(dto.getSbudgettype());
		/**
		 * 预算级次--用预算级次代码当做报表名称类型（1一般预算支出，2人行办理授权支付）在枚举值里面定义0319
		 */
		payoutdto.setSbudgetlevelcode("1");
		return payoutdto;
	}
	
	/**
	 * 取得地税部门征收税种
	 * 
	 * @generated
	 * @param idto
	 * @param sleSumItem--是否含项合计
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List getIncomeBill2(IDto idto, String sleSumItem, String sleSubjectType,
			String rptDate) throws ITFEBizException {
		
		try {
			
			TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;
			dto.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);

			//收入条件
			String sqlwhere =makeIncomeBillwhere(idto,sleSumItem,sleSubjectType,rptDate,StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
			
			StringBuffer sqlbuf = new StringBuffer(
					"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
			// 统计收入报表--收入部分
			//款
			String sql ="  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
					+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME "
					+ " union all "
					+ "  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
					+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME ";
			sqlbuf.append(sql);
			sqlbuf.append(") select S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			
			//打印sql
			log.debug(sqlbuf.toString());
			
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			List list = (List) exec.runQueryCloseCon(sqlbuf.toString(),
					TrIncomedayrptDto.class, true).getDtoCollection();
			
			return list;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}
	
	/**
	 * 取得财政支出数据
	 * 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List getPayoutBill(IDto idto, String sleSumItem, String moneyUnit,
			String rptDate) throws ITFEBizException {
		TrTaxorgPayoutReportDto payoutdto = changeDto(idto);
		
		String sqlwhere = makePayoutBillwhere(payoutdto,sleSumItem,moneyUnit,rptDate);
		
		StringBuffer sqlbuf = new StringBuffer(
		"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
		
		String sql = 
				//目
				"select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME union all "
				+ "  select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME  union all "
		
				//项
				+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME union all "
				+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
				+ " from (SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME ";
				sqlbuf.append(sql);
				sqlbuf.append(") select S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		try {
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			//打印sql
			log.debug(sqlbuf.toString());
			
			return (List) exec.runQueryCloseCon(sqlbuf.toString(), TrTaxorgPayoutReportDto.class,true)
					.getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}
	
	/**
	 * 全口径税收日报
	 * 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */

	public List makeAllTaxBill(IDto idto, String billCode, String moneyUnit,
			String rptDate) throws ITFEBizException {
		
		if(1==1){
			return getAllTaxBill( idto,  billCode,  moneyUnit,
					 rptDate);
		}
		
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;
		String sbilltype = dto.getSbillkind(); // 报表种类
		List<TsBudgetsubjectDto> subjectList;
		List<TrIncomedayrptDto> reportList;
		
		//取得税收大类
		try {
			String sql1 = "select S_SUBJECTCODE ,s_subjectName from ts_Budgetsubject where length(S_SUBJECTCODE) =5 and s_classflag = '"+StateConstant.SUBJECT_INCOME+"'";
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			subjectList = (List) exec.runQueryCloseCon(sql1,
					TsBudgetsubjectDto.class).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
		
		String sqlwhere = makeAllTaxBillwhere(idto,billCode,moneyUnit,rptDate);
		
		// 税收收入
		String sql = " select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_SHARE_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// 国税部门
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_NATION_CLASS
				+ "' as S_BUDGETSUBCODE,'其中：国税部门' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE ,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
				+ " where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_NATION_CLASS
				+ "' "+sqlwhere+"  GROUP BY  S_BUDGETLEVELCODE union all ";
		// 地税部门
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_PLACE_CLASS
				+ "' as S_BUDGETSUBCODE,'地税部门' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
				+ " where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_PLACE_CLASS
				+ "' "+sqlwhere+" GROUP BY  S_BUDGETLEVELCODE union all ";
		// 海关部分
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_CUSTOM_CLASS
				+ "' as S_BUDGETSUBCODE,'海关部门' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
				+ " where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_CUSTOM_CLASS
				+ "' "+sqlwhere+" GROUP BY  S_BUDGETLEVELCODE union all ";
		// 财政部门
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
				+ "' as S_BUDGETSUBCODE,'财政部门' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
				+ " where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
				+ "' "+sqlwhere+" GROUP BY  S_BUDGETLEVELCODE union all ";
		// 其他部门
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_OTHER_CLASS
				+ "' as S_BUDGETSUBCODE,'其他部门' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
				+ " where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_OTHER_CLASS
				+ "' "+sqlwhere+" GROUP BY  S_BUDGETLEVELCODE union all ";
		// 全辖不分征收机关
		for (TsBudgetsubjectDto tempdto : subjectList) {
			String s_subjectcode = tempdto.getSsubjectcode();
			String s_subjectname = tempdto.getSsubjectname();
			sql += "select '"
					+ s_subjectcode
					+ "' as S_BUDGETSUBCODE,'"
					+ s_subjectname
					+ "' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
					+ " where a.S_TAXORGCODE = '"
					+ MsgConstant.MSG_TAXORG_SHARE_CLASS
					+ "' "+sqlwhere+" and substr(S_BUDGETSUBCODE,1,5) ='" +s_subjectcode +"' group  by  S_BUDGETLEVELCODE    union all ";
		}
		sql += sql.replace("TR_INCOMEDAYRPT", "HTR_INCOMEDAYRPT");
		sql = sql.substring(0, sql.length() - 10) + " with ur";
		try {
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
//			for (int i = 0; i < (6 + subjectList.size())*2; i++) {
//				exec.addParam(dto.getStrecode());
//				exec.addParam(dto.getSrptdate());
//				exec.addParam(dto.getSbelongflag());
//				exec.addParam(dto.getStrimflag());
//			}
			reportList = (List) exec.runQueryCloseCon(sql,
					TrIncomedayrptDto.class).getDtoCollection();
			if (null == reportList|| reportList.size() ==0) {
				throw new ITFEBizException("查询无记录");
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
		HashMap<String, String> subMap = new HashMap<String, String>();
		HashMap<String, BigDecimal> allMap = new HashMap<String, BigDecimal>();
		// 重新组成 月报表要显示的List
		for (TrIncomedayrptDto tmpdto : reportList) {
			if (!subMap.containsKey(tmpdto.getSbudgetsubcode())) {
				subMap.put(tmpdto.getSbudgetsubcode(), tmpdto
						.getSbudgetsubname());
			}
			BigDecimal nmoney;
			if (sbilltype.equals(StateConstant.REPORT_YEAR)) {// 年报
				nmoney = tmpdto.getNmoneyyear();
			} else if (sbilltype.equals(StateConstant.REPORT_QUAR)) { // 季报
				nmoney = tmpdto.getNmoneyquarter();
			} else if (sbilltype.equals(StateConstant.REPORT_MONTH)) { // 月报
				nmoney = tmpdto.getNmoneymonth();
			} else if (sbilltype.equals(StateConstant.REPORT_TEN)) { // 旬报
				nmoney = tmpdto.getNmoneytenday();
			} else { // 日报
				nmoney = tmpdto.getNmoneyday();
			}
			allMap.put(tmpdto.getSbudgetsubcode()
					+ tmpdto.getSbudgetlevelcode(), nmoney);
		}
		List<ReportDto> billList = new ArrayList<ReportDto>();
		//征收机关大类
		for (int i = 0; i < 5; i++) {
			ReportDto tmp = new ReportDto();
			String subcode = null;
			if (i == 1) {
				subcode = MsgConstant.MSG_TAXORG_NATION_CLASS;
				tmp.setSbudgetsubname("其中：国税部门");
			}else if(i ==2){
				subcode = MsgConstant.MSG_TAXORG_PLACE_CLASS;
				tmp.setSbudgetsubname("          地税部门");
			}else if(i ==3){
				subcode = MsgConstant.MSG_TAXORG_CUSTOM_CLASS;
				tmp.setSbudgetsubname("          海关部门");
			}else if(i==4){
				subcode = MsgConstant.MSG_TAXORG_OTHER_CLASS;
				tmp.setSbudgetsubname("          其他部门");
			}else if(i ==0){
				subcode = "101";
				tmp.setSbudgetsubname("税收收入合计");
			}
			tmp.setSbudgetsubcode(subcode);
			BigDecimal central = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_CENTER);
			BigDecimal provice = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_PROVINCE);
			BigDecimal city = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_DISTRICT);
			BigDecimal county = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_PREFECTURE);
			BigDecimal town = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_VILLAGE);
			if (null ==central) {
				central = new BigDecimal(0);
			}
			if (null ==provice) {
				provice = new BigDecimal(0);
			}
			if (null ==city) {
				city = new BigDecimal(0);
			}
			if (null ==county) {
				county = new BigDecimal(0);
			}
			if (null ==town) {
				town = new BigDecimal(0);
			}
			tmp.setNcentralmoney(central);// 中央
			tmp.setNprovincemoney(provice);// 省级
			tmp.setNcitymoney(city);// 市级
			tmp.setNcountymoney(county);// 区县级
			tmp.setNtownmoney(town);// 市级
			billList.add(tmp);
		}
		//各科目大类
		int i = 0;
		for (TsBudgetsubjectDto tmpdto : subjectList) {
			i++;
			ReportDto tmp = new ReportDto();
			String subcode = tmpdto.getSsubjectcode();
			String subname = tmpdto.getSsubjectname();
			tmp.setSbudgetsubcode(subcode);
			tmp.setSbudgetsubname(i+"、"+subname);
			BigDecimal central = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_CENTER);
			BigDecimal provice = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_PROVINCE);
			BigDecimal city = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_DISTRICT);
			BigDecimal county = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_PREFECTURE);
			BigDecimal town = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_VILLAGE);
			if (null ==central) {
				central = new BigDecimal(0);
			}
			if (null ==provice) {
				provice = new BigDecimal(0);
			}
			if (null ==city) {
				city = new BigDecimal(0);
			}
			if (null ==county) {
				county = new BigDecimal(0);
			}
			if (null ==town) {
				town = new BigDecimal(0);
			}
			tmp.setNcentralmoney(central);// 中央
			tmp.setNprovincemoney(provice);// 省级
			tmp.setNcitymoney(city);// 市级
			tmp.setNcountymoney(county);// 区县级
			tmp.setNtownmoney(town);// 市级
			billList.add(tmp);
		}
		
		return billList;
	}
	
	/**
	 *  取得全口径税收数据
	 * 
	 * @generated
	 * @param idto
	 * @param billCode
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */

	public List getAllTaxBill(IDto idto, String billCode, String moneyUnit,
			String rptDate) throws ITFEBizException {
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;
		String sbilltype = dto.getSbillkind(); // 报表种类
		List<TsBudgetsubjectDto> subjectList;
		List<TrIncomedayrptDto> reportList;
		
		//取得税收大类
		try {
			String sql1 = "select S_SUBJECTCODE ,s_subjectName from ts_Budgetsubject where length(S_SUBJECTCODE) =5 and s_classflag = '"+StateConstant.SBT_CLASS_TAXINCOME+"' AND S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and S_ORGCODE in (SELECT S_BOOKORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			subjectList = (List) exec.runQueryCloseCon(sql1,
					TsBudgetsubjectDto.class).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
		
		String sqlwhere = makeAllTaxBillwhere(idto,billCode,moneyUnit,rptDate);
		
		// 税收收入
		String sql = " select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_SHARE_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// 国税部门
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_NATION_CLASS
				+ "' as S_BUDGETSUBCODE,'其中：国税部门' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_NATION_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// 地税部门
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_PLACE_CLASS
				+ "' as S_BUDGETSUBCODE,'地税部门' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_PLACE_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// 海关部分
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_CUSTOM_CLASS
				+ "' as S_BUDGETSUBCODE,'海关部门' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_CUSTOM_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// 财政部门
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
				+ "' as S_BUDGETSUBCODE,'财政部门' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// 其他部门
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
				+ "' as S_BUDGETSUBCODE,'其他部门' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_OTHER_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		
		try {
			
			sql += sql.replace("TR_INCOMEDAYRPT", "HTR_INCOMEDAYRPT");
			sql = sql.substring(0, sql.length() - 10) + " with ur";
			
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			reportList = (List) exec.runQueryCloseCon(sql,TrIncomedayrptDto.class).getDtoCollection();
			if (null == reportList|| reportList.size() ==0) {
				throw new ITFEBizException("查询无记录");
			}
		
			// 全辖不分征收机关
			for (TsBudgetsubjectDto tempdto : subjectList) {
				String s_subjectcode = tempdto.getSsubjectcode();
				String s_subjectname = tempdto.getSsubjectname();
				String sql1 = "select '"
						+ s_subjectcode
						+ "' as S_BUDGETSUBCODE,'"
						+ s_subjectname
						+ "' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
						+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
						+ MsgConstant.MSG_TAXORG_SHARE_CLASS+ "' "
						+ " AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
						+sqlwhere+" and substr(S_BUDGETSUBCODE,1,5) ='" +s_subjectcode +"' group  by  S_BUDGETLEVELCODE    union all ";
				
				sql1 += sql1.replace("TR_INCOMEDAYRPT", "HTR_INCOMEDAYRPT");
				sql1 = sql1.substring(0, sql1.length() - 10) + " with ur";
				
				SQLExecutor exec1 = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				List<TrIncomedayrptDto> reportList1 = (List) exec1.runQueryCloseCon(sql1,TrIncomedayrptDto.class).getDtoCollection();
				if (null == reportList1|| reportList1.size() ==0) {
//					throw new ITFEBizException("查询无记录");
				}
				else{
					reportList.addAll(reportList1);
				}
			}
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
		
		HashMap<String, String> subMap = new HashMap<String, String>();
		HashMap<String, BigDecimal> allMap = new HashMap<String, BigDecimal>();
		// 重新组成 月报表要显示的List
		for (TrIncomedayrptDto tmpdto : reportList) {
			if (!subMap.containsKey(tmpdto.getSbudgetsubcode())) {
				subMap.put(tmpdto.getSbudgetsubcode(), tmpdto
						.getSbudgetsubname());
			}
			BigDecimal nmoney;
			if (sbilltype.equals(StateConstant.REPORT_YEAR)) {// 年报
				nmoney = tmpdto.getNmoneyyear();
			} else if (sbilltype.equals(StateConstant.REPORT_QUAR)) { // 季报
				nmoney = tmpdto.getNmoneyquarter();
			} else if (sbilltype.equals(StateConstant.REPORT_MONTH)) { // 月报
				nmoney = tmpdto.getNmoneymonth();
			} else if (sbilltype.equals(StateConstant.REPORT_TEN)) { // 旬报
				nmoney = tmpdto.getNmoneytenday();
			} else { // 日报
				nmoney = tmpdto.getNmoneyday();
			}
			allMap.put(tmpdto.getSbudgetsubcode()
					+ tmpdto.getSbudgetlevelcode(), nmoney);
		}
		List<ReportDto> billList = new ArrayList<ReportDto>();
		//征收机关大类
		for (int i = 0; i < 6; i++) {
			ReportDto tmp = new ReportDto();
			String subcode = null;
			if (i == 1) {
				subcode = MsgConstant.MSG_TAXORG_NATION_CLASS;
				tmp.setSbudgetsubname("其中: 国税部门");
			}else if(i ==2){
				subcode = MsgConstant.MSG_TAXORG_PLACE_CLASS;
				tmp.setSbudgetsubname("          地税部门");
			}else if(i ==3){
				subcode = MsgConstant.MSG_TAXORG_CUSTOM_CLASS;
				tmp.setSbudgetsubname("          海关部门");
			}else if(i ==4){
				subcode = MsgConstant.MSG_TAXORG_FINANCE_CLASS;
				tmp.setSbudgetsubname("          财政部门");
			}
			else if(i==5){
				subcode = MsgConstant.MSG_TAXORG_OTHER_CLASS;
				tmp.setSbudgetsubname("          其他部门");
			}else if(i ==0){
				subcode = "101";
				tmp.setSbudgetsubname("税收收入合计");
			}
			tmp.setSbudgetsubcode(subcode);
			BigDecimal central = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_CENTER);
			BigDecimal provice = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_PROVINCE);
			BigDecimal city = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_DISTRICT);
			BigDecimal county = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_PREFECTURE);
			BigDecimal town = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_VILLAGE);
			if (null ==central) {
				central = new BigDecimal(0);
			}
			if (null ==provice) {
				provice = new BigDecimal(0);
			}
			if (null ==city) {
				city = new BigDecimal(0);
			}
			if (null ==county) {
				county = new BigDecimal(0);
			}
			if (null ==town) {
				town = new BigDecimal(0);
			}
			tmp.setNcentralmoney(central);// 中央
			tmp.setNprovincemoney(provice);// 省级
			tmp.setNcitymoney(city);// 市级
			tmp.setNcountymoney(county);// 区县级
			tmp.setNtownmoney(town);// 市级
			billList.add(tmp);
		}
		
		int i = 0;
		//各科目大类
		for (TsBudgetsubjectDto tmpdto : subjectList) {
			i++;
			ReportDto tmp = new ReportDto();
			String subcode = tmpdto.getSsubjectcode();
			String subname = tmpdto.getSsubjectname();
			tmp.setSbudgetsubcode(subcode);
			tmp.setSbudgetsubname(i+"、"+subname);
			BigDecimal central = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_CENTER);
			BigDecimal provice = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_PROVINCE);
			BigDecimal city = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_DISTRICT);
			BigDecimal county = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_PREFECTURE);
			BigDecimal town = allMap.get(subcode
					+ MsgConstant.BUDGET_LEVEL_VILLAGE);
			if (null ==central) {
				central = new BigDecimal(0);
			}
			if (null ==provice) {
				provice = new BigDecimal(0);
			}
			if (null ==city) {
				city = new BigDecimal(0);
			}
			if (null ==county) {
				county = new BigDecimal(0);
			}
			if (null ==town) {
				town = new BigDecimal(0);
			}
			tmp.setNcentralmoney(central);// 中央
			tmp.setNprovincemoney(provice);// 省级
			tmp.setNcitymoney(city);// 市级
			tmp.setNcountymoney(county);// 区县级
			tmp.setNtownmoney(town);// 市级
			billList.add(tmp);
		}
		
		return billList;
	}
	
	/**
	 * 取得预算收入原始数据
	 * 
	 * @generated
	 * @param idto
	 * @param sleSumItem--是否含项合计
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List getIncomeBill(IDto idto, String sleSumItem, String sleSubjectType,
			String rptDate) throws ITFEBizException {
		
		try {

			//收入条件
			String sqlwhere =makeIncomeBillwhere(idto,sleSumItem,sleSubjectType,rptDate,StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
			StringBuffer sqlbuf = new StringBuffer(
					"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
			// 统计收入报表--收入部分
			//目
			String sql = "select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH,a.N_MONEYQUARTER,a.N_MONEYYEAR "
					+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " union all "
					+ "  select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH,a.N_MONEYQUARTER,a.N_MONEYYEAR "
					+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " union all ";
			
			//项
			sql+="  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
				+ sqlwhere
				+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME "
				+ " union all "
				+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
				+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
				+ sqlwhere
				+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME "
				+ " union all ";
			
			//款
			sql+=
			  "  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
			+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
			+ sqlwhere
			+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME "
			+ " union all "
			+ "  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
			+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
			+ sqlwhere
			+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME ";
			
			sqlbuf.append(sql);
			sqlbuf.append(") select S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			
			//打印sql
			log.debug(sqlbuf.toString());
			
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			List list = (List) exec.runQueryCloseCon(sqlbuf.toString(),
					TrIncomedayrptDto.class, true).getDtoCollection();
			
			return list;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}
	

	//取得文件路径
	public String getReportRootPath() {
		String dirsep = File.separator; // 取得系统分割符
		return ITFECommonConstant.FILE_ROOT_PATH+"Report"+dirsep;
	}
	
	//取得文件路径
	public String getFileRootPath() {
		return ITFECommonConstant.FILE_ROOT_PATH;
	}

	public String makeExcelInOutBillSer(IDto idto, String sleSumItem, String sleSubjectType, String rptDate, String rptType,
			String copyFilename,String reporttitle,String unitname)
			throws ITFEBizException {
		String msg="";
		HashMap<String, String> datamap=new HashMap<String, String>();;
		List<TrIncomedayrptDto> list = null;
		
		//报表模块文件路径
		ReportExcel.setFilepath(getFileRootPath());
		//文件路径
		ReportExcel.setNewfilepath(getReportRootPath());
		//新建报表名称
		ReportExcel.setCopyFilename(copyFilename);
		//报表标题
		ReportExcel.setReporttitle(reporttitle);
		//出表单位
		ReportExcel.setUnit(unitname);
		
		switch(Integer.parseInt(rptType.trim())){
			case 1 :
			{
				//报表模板名称
				ReportExcel.setFilename(StateConstant.RPT_TYPE_NAME_1+"Model.xls");
				//报表sheet名称
				ReportExcel.setSheetname(StateConstant.RPT_TYPE_NAME_1);
			}
			case 2 :
			{
				//报表模板名称
				ReportExcel.setFilename(StateConstant.RPT_TYPE_NAME_2+"Model.xls");
				//报表sheet名称
				ReportExcel.setSheetname(StateConstant.RPT_TYPE_NAME_2);
			}
			case 3 :
			{
				//报表模板名称
				ReportExcel.setFilename(StateConstant.RPT_TYPE_NAME_3+"Model.xls");
				//报表sheet名称
				ReportExcel.setSheetname(StateConstant.RPT_TYPE_NAME_3+"Model.xls");
			}
			case 4 :
			{
				//报表模板名称
				ReportExcel.setFilename(StateConstant.RPT_TYPE_NAME_4+"Model.xls");
				//报表sheet名称
				ReportExcel.setSheetname(StateConstant.RPT_TYPE_NAME_4);
			}
			case 5 :
			{
				//报表模板名称
				ReportExcel.setFilename(StateConstant.RPT_TYPE_NAME_5+"Model.xls");
				//报表sheet名称
				ReportExcel.setSheetname(StateConstant.RPT_TYPE_NAME_5);
			}
		}
		
		//收入月报
		if(rptType.equals(StateConstant.RPT_TYPE_1)){
			//取得分类款项目报表数据
//			list=(List<TrIncomedayrptDto>)makeIncomeBill(idto,sleSumItem,sleSubjectType,rptDate);
			//取得基本报表数据
			list=(List<TrIncomedayrptDto>)getIncomeBill(idto,sleSumItem,sleSubjectType,rptDate);
			if(list==null || list.size()<=0){
				return null;
			}
			for(TrIncomedayrptDto dto :list){
				datamap.put(dto.getSbudgetsubcode(), dto.getNmoneymonth().toString());
			}
		}
		//支出月报
		if(rptType.equals(StateConstant.RPT_TYPE_2)){
			list=makePayoutBill(idto,sleSumItem,sleSubjectType,rptDate);
		}
		//收支旬报
		if(rptType.equals(StateConstant.RPT_TYPE_3)){
			//list=null;
		}
		//全口径税收
		if(rptType.equals(StateConstant.RPT_TYPE_4)){
			//list=makeAllTaxBill(idto,sleSumItem,sleSubjectType,rptDate);
		}
		//年末收支调度表
		if(rptType.equals(StateConstant.RPT_TYPE_5)){
			//list=makeIncomeBill(idto,sleSumItem,sleSubjectType,rptDate);
		}
		
//		ReportExcel.setDatamap(datamap);
		msg=ReportExcel.getreport();
		msg+=","+getFileRootPath();
		
		return msg;
	}
	
	private String makeNonBudgetarywhere(IDto idto, String startdate, String enddate,
			String remake) throws ITFEBizException {
		
		TrFinIncomeonlineDayrptDto dto = (TrFinIncomeonlineDayrptDto) idto;
		String sqlwhere = "";

		/**
		 * 国库代码
		 */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
//			throw new ITFEBizException("查询条件：核算主体代码不能为空！");
			//修改20110326 用核算主体代码
			sqlwhere += " and a.s_trecode in ( SELECT s_trecode FROM TS_TREASURY WHERE S_BOOKORGCODE= '"+this.getLoginInfo().getSorgcode()+"' ) ";
		}
		
		/**
		 * 征收机关代码
		 */
		if (null != dto.getStaxorgcode()
				&& !"".equals(dto.getStaxorgcode().trim())) {
			sqlwhere += " and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' ";
		}
		
		/**
		 * 预算科目代码
		 */
		if (null != dto.getSbdgsbtcode()
				&& !"".equals(dto.getSbdgsbtcode().trim())) {
			sqlwhere += " and a.s_BdgSbtCode ='"+dto.getSbdgsbtcode().trim()+"' ";
		}
		
		/**
		 * 预算科目名称
		 */
		if (null != dto.getSbdgsbtname()
				&& !"".equals(dto.getSbdgsbtname().trim())) {
			sqlwhere += " and a.s_BdgSbtName ='"+dto.getSbdgsbtname().trim()+"' ";
		}
		
		/**
		 * 纳税人代码
		 */
		if (null != dto.getStaxpaycode()
				&& !"".equals(dto.getStaxpaycode().trim())) {
			sqlwhere += " and a.s_TaxPayCode ='"+dto.getStaxpaycode().trim()+"' ";
		}
		
		/**
		 * 纳税人名称
		 */
		if (null != dto.getStaxpayname()
				&& !"".equals(dto.getStaxpayname().trim())) {
			sqlwhere += " and a.s_TaxPayName ='"+dto.getStaxpayname().trim()+"' ";
		}
		
		/**
		 * 开始日期
		 */
		if (null != startdate
				&& !"".equals(startdate.trim())) {
			sqlwhere += " and a.S_ACCT >='"+startdate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：开始日期不能为空！");
		}
		
		/**
		 * 结束日期
		 */
		if (null != enddate
				&& !"".equals(enddate.trim())) {
			sqlwhere += " and a.S_ACCT <='"+enddate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：结束日期不能为空！");
		}
		
		/**
		 * 预算科目代码--工会经费相关科目
		 */
//		sqlwhere += " and a.S_BDGSBTCODE like '190%' ";
		//修改20110326 为查询预算外的数据
		sqlwhere += " and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_OUT+"' ";
		
		/**
		 * 由于各核算主体单独一套预算科目代码，因此查询的时候应加上科目代码的核算主体条件
		 */
//		sqlwhere += " and b.S_ORGCODE ='"+this.getLoginInfo().getSorgcode()+"' ";
		
		//mod 2011-09-05
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and b.S_ORGCODE in (SELECT S_BOOKORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		}
		else{
			sqlwhere += " and b.S_ORGCODE ='"+this.getLoginInfo().getSorgcode()+"' ";
		}
		
		return sqlwhere;
	}
	
	public List makeNonBudgetary(IDto idto, String startdate, String enddate,
			String remake)throws ITFEBizException {
		
		String sqlwhere = makeNonBudgetarywhere( idto,  startdate,  enddate,remake);
		
		StringBuffer sqlbuf = new StringBuffer(
				"with tmp(S_ACCT,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXVOUNO,S_TAXORGCODE, S_BDGSBTCODE,S_BDGSBTNAME,S_TRECODE, "
				+" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER,  "
				+" N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR,S_Remark1 ) as ( ");
		
		String sql = 
				" select a.S_ACCT,a.S_TAXPAYCODE,a.S_TAXPAYNAME,a.S_TAXVOUNO,a.S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME,a.S_TRECODE, "
				+" a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH, a.N_MONEYQUARTER, a.N_MONEYYEAR, a.N_CENTERMONEYDAY, a.N_CENTERMONEYTENDAY, a.N_CENTERMONEYMONTH, a.N_CENTERMONEYQUARTER, "   
				+"  a.N_CENTERMONEYYEAR, a.N_PLACEMONEYDAY, a.N_PLACEMONEYTENDAY, a.N_PLACEMONEYMONTH, a.N_PLACEMONEYQUARTER, a.N_PLACEMONEYYEAR,'' as S_Remark1  "
				+" from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere 
				+"  union all "
//				+"  select a.S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,a.S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, '' as S_TRECODE,   "
//				+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,"  
//				+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
//				+"   sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'机关小计' as S_Remark1  "
//				+"   from TR_FIN_INCOMEONLINE_DAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
//				+"   group by a.S_TAXORGCODE,a.S_ACCT,a.S_BDGSBTCODE,b.S_SUBJECTNAME "
//				+"  union all "
				+"  select a.S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, '' as S_TRECODE,   "
				+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,  "
				+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
				+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'日累计' as S_Remark1  "
				+"  from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
				+"  group by a.S_ACCT,a.S_BDGSBTCODE,b.S_SUBJECTNAME "
				+"  union all "
				+"  select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, '' as S_TRECODE, "
				+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,  "
				+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
				+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'科目小计' as S_Remark1  "
				+"  from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
				+"  group by a.S_BDGSBTCODE,b.S_SUBJECTNAME "
				+"  union all "   
				+"  select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,'' as S_BDGSBTCODE,'' as S_BDGSBTNAME, '' as S_TRECODE,  "
				+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,"  
				+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
				+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'合计' as S_Remark1  "
				+"  from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere;
		
		sql += " union all " + sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", "HTR_FIN_INCOMEONLINE_DAYRPT");
		
		sqlbuf.append(sql);
				
		sqlbuf.append(") select S_TAXVOUNO,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXORGCODE,S_ACCT,S_BDGSBTCODE,S_BDGSBTNAME,S_TRECODE, "
					+" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER, " 
					+"N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR,S_Remark1 from tmp order by s_acct,S_BDGSBTCODE ,S_TAXORGCODE ,s_taxpaycode ");
		
		try {
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			//打印sql
			log.debug(sqlbuf.toString());
			
			List list= (List) exec.runQueryCloseCon(sqlbuf.toString(), TrFinIncomeonlineDayrptDto.class,true).getDtoCollection();
//			Collections.reverse(list);
			return list;
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}

	public List FinOutBookSerial(IDto idto, String startdate, String enddate,
			String remake) throws ITFEBizException {
		
		String sqlwhere = makeFinOutBookSerialwhere( idto,  startdate,  enddate,remake);
		StringBuffer sqlbuf=null;
		
		sqlbuf = new StringBuffer(
		"with tmp(S_TRECODE,S_DESCRIPTION,D_ACCTDATE,S_VOUNO,F_SUMAMT,S_BILLORG,S_INPUTERID ) as (");
		
		//授权支付额度
		if(remake.equals("1")){
	
			String sql = 
				"select a.S_TRECODE as S_TRECODE ,b.S_SUBJECTNAME as s_description,a.d_acctdate as d_acctdate,a.s_vouno as s_vouno,a.F_AMT as F_SUMAMT,a.S_FUNCSBTCODE as s_billorg,'' as s_inputerid from "
				+"(select * from TV_GRANTPAYPLAN_MAIN m,TV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "
				+"	union all "
				+" select * from HTV_GRANTPAYPLAN_MAIN m,HTV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "		 
				+") as a, "
				+" TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE "+sqlwhere
				+" union all "
				+"select '' as S_TRECODE ,b.S_SUBJECTNAME as s_description,'' as d_acctdate,'' as s_vouno,sum(a.F_AMT) as F_SUMAMT,a.S_FUNCSBTCODE as s_billorg,'小计' as s_inputerid from "
				+"(select * from TV_GRANTPAYPLAN_MAIN m,TV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "
				+"	union all "
				+" select * from HTV_GRANTPAYPLAN_MAIN m,HTV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "		 
				+") as a, "
				+" TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE "+sqlwhere
				+" group by a.S_FUNCSBTCODE,b.S_SUBJECTNAME "
				+" union all "
				+"select '' as S_TRECODE ,'' as s_description,'' as d_acctdate,'' as s_vouno,sum(a.F_AMT) as F_SUMAMT,'' as s_billorg,'合计' as s_inputerid from "
				+"(select * from TV_GRANTPAYPLAN_MAIN m,TV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "
				+"	union all "
				+" select * from HTV_GRANTPAYPLAN_MAIN m,HTV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "		 
				+") as a, "
				+" TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE "+sqlwhere;
			sqlbuf.append(sql);
			
		}
		else{//授权支付
			
			String nuionall=
				" select "
				+"  m.S_SEQ, m.S_TRANO, m.S_ORGCODE, m.S_TRECODE, m.S_BILLORG, m.S_ENTRUSTDATE, m.S_PACKNO, m.S_PAYOUTVOUTYPENO, m.S_PAYMODE, m.S_VOUNO,  "
				+"  m.D_VOUCHER, m.S_PAYERACCT, m.S_PAYERNAME, m.S_PAYERADDR, m.S_PAYEEACCT, m.S_PAYEENAME, m.S_PAYEEADDR, m.S_RCVBNKNO, m.S_PAYEEOPNBNKNO, m.S_ADDWORD "
				+" , m.C_BDGKIND, m.I_OFYEAR, m.S_BDGADMTYPE, m.F_AMT as f_Amt_1, m.S_TRASTATE, m.S_DESCRIPTION, m.D_ACCT, m.C_TRIMFLAG, m.I_DETAILNIO, m.S_STATUS,  "
				+" m.S_BACKFLAG, m.D_ORIENTRUSTDATE, m.S_ORITRANO, m.S_ORIVOUNO, m.D_ORIVOUDATE, m.I_CHGNUM, m.S_INPUTERID, m.TS_SYSUPDATE, "
				+" s.I_SEQNO, s.S_BDGORGCODE, s.S_FUNCSBTCODE, s.S_ECOSBTCODE, s.C_ACCTPROP, s.F_AMT, s.TS_SYSUPDATE "
				+"   from TV_PBCGRANTPAY_MAIN m,TV_PBCGRANTPAY_sub s where m.S_SEQ=s.S_SEQ and m.S_TRASTATE='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "
				+"  union all "
				+"  select  "
				+"   m.S_SEQ, m.S_TRANO, m.S_ORGCODE, m.S_TRECODE, m.S_BILLORG, m.S_ENTRUSTDATE, m.S_PACKNO, m.S_PAYOUTVOUTYPENO, m.S_PAYMODE, m.S_VOUNO,  "
				+"  m.D_VOUCHER, m.S_PAYERACCT, m.S_PAYERNAME, m.S_PAYERADDR, m.S_PAYEEACCT, m.S_PAYEENAME, m.S_PAYEEADDR, m.S_RCVBNKNO, m.S_PAYEEOPNBNKNO, m.S_ADDWORD "
				+"  , m.C_BDGKIND, m.I_OFYEAR, m.S_BDGADMTYPE, m.F_AMT as f_Amt_1, m.S_TRASTATE, m.S_DESCRIPTION, m.D_ACCT, m.C_TRIMFLAG, m.I_DETAILNIO, m.S_STATUS,  "
				+"  m.S_BACKFLAG, m.D_ORIENTRUSTDATE, m.S_ORITRANO, m.S_ORIVOUNO, m.D_ORIVOUDATE, m.I_CHGNUM, m.S_INPUTERID, m.TS_SYSUPDATE, "
				+"  s.I_SEQNO, s.S_BDGORGCODE, s.S_FUNCSBTCODE, s.S_ECOSBTCODE, s.C_ACCTPROP, s.F_AMT, s.TS_SYSUPDATE "
				+"   from HTV_PBCGRANTPAY_MAIN m,HTV_PBCGRANTPAY_sub s where m.S_SEQ=s.S_SEQ and m.S_TRASTATE='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' ";
			
			String sql = 	
				"select a.S_TRECODE as S_TRECODE ,b.S_SUBJECTNAME as s_description,a.d_acct as d_acctdate,a.s_vouno as s_vouno,a.F_AMT as F_SUMAMT,a.S_FUNCSBTCODE as s_billorg,'' as s_inputerid from "
				+"( "+nuionall+" ) as a,  "
				+"TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE  "+sqlwhere
				+"union all  "
				+"select '' as S_TRECODE ,b.S_SUBJECTNAME as s_description,'' as d_acctdate,'' as s_vouno,sum(a.F_AMT) as F_SUMAMT,a.S_FUNCSBTCODE as s_billorg,'小计' as s_inputerid from  "
				+"( "+nuionall+" ) as a,  "
				+" TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE  "+sqlwhere
				+"group by a.S_FUNCSBTCODE,b.S_SUBJECTNAME  "
				+" union all  "
				+"select '' as S_TRECODE ,'' as s_description,'' as d_acctdate,'' as s_vouno,sum(a.F_AMT) as F_SUMAMT,'' as s_billorg,'合计' as s_inputerid from  "
				+" ( "+nuionall+" ) as a,  "
				+"TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE  "+sqlwhere;
			sqlbuf.append(sql);
		}
		sqlbuf.append(") select S_TRECODE,S_DESCRIPTION,D_ACCTDATE,S_VOUNO,F_SUMAMT,S_BILLORG,S_INPUTERID "
				+" from tmp order by S_BILLORG desc,S_DESCRIPTION desc ");
		
		try {
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			//打印sql
			log.debug(sqlbuf.toString());
			
			return (List) exec.runQueryCloseCon(sqlbuf.toString(), TvGrantpayplanMainDto.class,true).getDtoCollection();
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}
	
	private String makeFinOutBookSerialwhere(IDto idto, String startdate, String enddate,
			String remake) throws ITFEBizException {
		
		TvGrantpayplanMainDto dto = (TvGrantpayplanMainDto) idto;
		String sqlwhere = "";

		/**
		 * 国库代码
		 */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：国库代码不能为空！");
		}
		
		/**
		 * 预算单位代码
		 */
		if (null != dto.getSbdgorgcode()
				&& !"".equals(dto.getSbdgorgcode().trim())) {
			sqlwhere += " and a.S_BDGORGCODE ='"+dto.getSbdgorgcode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("查询条件：预算单位代码不能为空！");
		}
		
		if(remake.equals("1")){//授权支付额度
			/**
			 * 开始日期
			 */
			if (null != startdate
					&& !"".equals(startdate.trim())) {
				sqlwhere += " and a.D_ACCTDATE >='"+startdate.trim()+"' ";
			}
			else{
				throw new ITFEBizException("查询条件：开始日期不能为空！");
			}
			
			/**
			 * 结束日期
			 */
			if (null != enddate
					&& !"".equals(enddate.trim())) {
				sqlwhere += " and a.D_ACCTDATE <='"+enddate.trim()+"' ";
			}
			else{
				throw new ITFEBizException("查询条件：结束日期不能为空！");
			}
		}
		else{
			/**
			 * 开始日期
			 */
			if (null != startdate
					&& !"".equals(startdate.trim())) {
				sqlwhere += " and a.D_ACCT >='"+startdate.trim()+"' ";
			}
			else{
				throw new ITFEBizException("查询条件：开始日期不能为空！");
			}
			
			/**
			 * 结束日期
			 */
			if (null != enddate
					&& !"".equals(enddate.trim())) {
				sqlwhere += " and a.D_ACCT <='"+enddate.trim()+"' ";
			}
			else{
				throw new ITFEBizException("查询条件：结束日期不能为空！");
			}
		}
		
		/**
		 * 由于各核算主体单独一套预算科目代码，因此查询的时候应加上科目代码的核算主体条件
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_BOOKORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		
		return sqlwhere;
	}

	public List FinExcelIncomeMonthLevel(IDto idto, String startdate,
			String enddate, String remake) throws ITFEBizException {

		List<TrIncomedayrptDto> reportList;
		List<ExcelReportDto> rusultList= new ArrayList<ExcelReportDto>();
		List<TsTreasuryDto> treasuryList;
		SQLExecutor exec = null;
		
		//取得县区国库代码集
		try {
			String sql1 = "select S_TRECODE,S_GOVERNTREcode from TS_TREASURY where s_trelevel='4' and S_GOVERNTRELEVEL='3' ";
			exec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			treasuryList = (List) exec.runQueryCloseCon(sql1,TsTreasuryDto.class).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
		String strtreasury="";
		for(TsTreasuryDto dto:treasuryList){
			strtreasury+="'"+dto.getStrecode()+"'"+",";
		}
		strtreasury="("+strtreasury.substring(0, strtreasury.length()-1)+")";
		
		//取得市级国库代码
		String strtredistrict=treasuryList.get(0).getSgoverntrecode();
		
		//取得要查询的年月日集
		String montharr[]=TimeFacade.getMontharr(startdate,enddate);
		String strmonth="";
		for(String str:montharr){
			strmonth+="'"+str+"'"+",";
		}
		strmonth="("+strmonth.substring(0, strmonth.length()-1)+")";
		
		//取得征收机关大类集
		String strtaxorgclass="('"+MsgConstant.MSG_TAXORG_NATION_CLASS+"', '"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"','"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"','"+
		MsgConstant.MSG_TAXORG_FINANCE_CLASS+"','"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"','"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"')";
		
		//where共同部分
		String sqlwhere="   where (S_BUDGETSUBCODE  like '103%' or S_BUDGETSUBCODE  like '101%' ) and S_BUDGETSUBCODE  not like '10301%' and s_rptdate in "+strmonth+" and S_TRIMFLAG='0' ";
		
		String sql=
			//区县地方级全辖
			" select S_TRECODE,substr(S_RPTDATE,1,6) as S_RPTDATE,substr(S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,  sum(N_MONEYMONTH) as N_MONEYMONTH "+
			"   from (select * from TR_INCOMEDAYRPT where S_RPTDATE in "+strmonth+") a"+
			sqlwhere+
			"     and s_trecode in "+strtreasury+" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"'  and S_BUDGETLEVELCODE >='"+MsgConstant.BUDGET_LEVEL_PREFECTURE+"' "+
			"  group by S_TRECODE,S_RPTDATE,substr(S_BUDGETSUBCODE,1,3) "+
			" union all "+
			
			//市本级全辖
			" select S_TRECODE,substr(S_RPTDATE,1,6) as S_RPTDATE,substr(S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,  sum(N_MONEYMONTH) as N_MONEYMONTH "+
			"   from (select * from TR_INCOMEDAYRPT where S_RPTDATE in "+strmonth+") a"+
			sqlwhere+
			"     and s_trecode in ('"+strtredistrict+"') and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"'  and S_BUDGETLEVELCODE ='"+MsgConstant.BUDGET_LEVEL_DISTRICT+"' "+
			"  group by S_TRECODE,S_RPTDATE,substr(S_BUDGETSUBCODE,1,3) "+
			" union all "+
			
			//全辖大类
			" select substr(S_TAXORGCODE,1,10) as S_TRECODE,substr(S_RPTDATE,1,6) as S_RPTDATE,substr(S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,  sum(N_MONEYMONTH) as N_MONEYMONTH "+
			"   from (select * from TR_INCOMEDAYRPT where S_RPTDATE in "+strmonth+") a"+
			sqlwhere+
			"     and s_trecode in ('"+strtredistrict+"') and S_TAXORGCODE in "+strtaxorgclass+" and S_BUDGETLEVELCODE >='"+MsgConstant.BUDGET_LEVEL_DISTRICT+"' "+
			"  group by S_TRECODE,S_RPTDATE,substr(S_BUDGETSUBCODE,1,3),S_TAXORGCODE ";
		
		sql += " union all " + sql.replace("TR_INCOMEDAYRPT", "HTR_INCOMEDAYRPT");
		try {
			exec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			reportList = (List) exec.runQueryCloseCon(sql,TrIncomedayrptDto.class,true).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
		
		if(reportList==null || reportList.size()==0){
			throw new ITFEBizException("查询无数据！！！");
		}
		
		HashMap<String , BigDecimal> map=new HashMap<String , BigDecimal>();
		for(TrIncomedayrptDto dto:reportList){
			map.put(dto.getStrecode().trim()+dto.getSrptdate().trim()+dto.getSbudgetsubcode().trim(), dto.getNmoneymonth());
		}
		
		//国库+征收机关大类去两位
		List<String> strecodelist=new ArrayList<String>();
		for(TsTreasuryDto dto:treasuryList){
			strecodelist.add(dto.getStrecode());
		}
		strecodelist.add(strtredistrict);
		strecodelist.add(MsgConstant.MSG_TAXORG_NATION_CLASS.substring(0, 10));
		strecodelist.add(MsgConstant.MSG_TAXORG_PLACE_CLASS.substring(0, 10));
		strecodelist.add(MsgConstant.MSG_TAXORG_CUSTOM_CLASS.substring(0, 10));
		strecodelist.add(MsgConstant.MSG_TAXORG_FINANCE_CLASS.substring(0, 10));
		strecodelist.add(MsgConstant.MSG_TAXORG_OTHER_CLASS.substring(0, 10));
		strecodelist.add(MsgConstant.MSG_TAXORG_SHARE_CLASS.substring(0, 10));
//		strecodelist.add("1000000001"); //县区合计
//		strecodelist.add("1000000002"); //三县小记
//		strecodelist.add("1000000003"); //六区合计
		
		//月份
		List<String> smonthlist=new ArrayList<String>();
		for(String str:montharr){
			smonthlist.add(str.substring(0, 6));
		}
		
		//循环计算
		for(String trecode:strecodelist){
			for(String month:smonthlist){
				if(trecode.equals("1000000001")){//县区合计
					;
				}
				else if(trecode.equals("1000000002")){//三县小记
					;
				}
				else if(trecode.equals("1000000003")){//六区合计
					;
				}
				else{
					ExcelReportDto rusultdto = new ExcelReportDto();
					rusultdto.setStrecode(trecode);
					rusultdto.setMonth(month);
					
					BigDecimal provice = new BigDecimal(0.00);
					if(map.get(trecode.trim()+month.trim()+"101") !=null){
						provice=map.get(trecode.trim()+month.trim()+"101");
					}
					rusultdto.setTaxrevenuemoney(provice);
					
					BigDecimal provice2 = new BigDecimal(0.00);
					if(map.get(trecode.trim()+month.trim()+"103") !=null){
						provice2=map.get(trecode.trim()+month.trim()+"103");
					}
					rusultdto.setIncomemoney(provice.add(provice2));
					
					rusultList.add(rusultdto);
				}
			}
		}
		
		return rusultList;
	}

}
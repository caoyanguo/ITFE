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
	 * ���������ձ�
	 * 
	 * @generated
	 * @param idto
	 * @param sleSumItem--�Ƿ���ϼ�
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List makeIncomeBill(IDto idto, String sleSumItem, String sleSubjectType,
			String rptDate) throws ITFEBizException {
		
		try {

			//��������
			String sqlwhere =makeIncomeBillwhere(idto,sleSumItem,sleSubjectType,rptDate,StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
			TrIncomedayrptDto dto = (TrIncomedayrptDto)idto;
			
			StringBuffer sqlbuf = new StringBuffer(
					"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
			// ͳ�����뱨��--���벿��
			//Ŀ
			String sql = "select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH,a.N_MONEYQUARTER,a.N_MONEYYEAR "
					+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " union all "
					+ "  select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH,a.N_MONEYQUARTER,a.N_MONEYYEAR "
					+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " union all ";
					
					//�Ƿ���ϼ�:Ĭ��Ϊ������ϼ�
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

					//��
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
					
					//��
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
			 * �˿ⲿ���Ѿ���tcbs����������ﲻ������
			 */
//			//�˿�����
//			String sqlwhereback =makeIncomeBillwhere(idto,sleSumItem,sleSubjectType,rptDate,StateConstant.REPORTTYPE_FLAG_NRDRAWBACKBILL);
//			// ͳ�����뱨��--�˿ⲿ��
//			//Ŀ
//			sql = " select S_BUDGETSUBCODE,S_BUDGETSUBNAME,-N_MONEYDAY,-N_MONEYTENDAY,-N_MONEYMONTH,-N_MONEYQUARTER,-N_MONEYYEAR"
//					+ " from TR_INCOMEDAYRPT a Where 1=1 "
//					+ sqlwhereback
//					+ " union all "
//					+ "  select S_BUDGETSUBCODE,S_BUDGETSUBNAME,-N_MONEYDAY,-N_MONEYTENDAY,-N_MONEYMONTH,-N_MONEYQUARTER,-N_MONEYYEAR"
//					+ " from HTR_INCOMEDAYRPT a Where 1=1 "
//					+ sqlwhereback
//					+ " union all ";
//			
//					//�Ƿ���ϼ�:Ĭ��Ϊ������ϼ�
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
//					//��
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
//					//��
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
			
			//��ӡsql
			log.debug(sqlbuf.toString());
			
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			List list = (List) exec.runQueryCloseCon(sqlbuf.toString(),
					TrIncomedayrptDto.class, true).getDtoCollection();
			
			// ����һ��Ԥ���ڡ�ת���������С��
			List listfortype =getSumBySbtType(idto,sleSumItem,sleSubjectType,rptDate);
			list.addAll(listfortype);
			
			//Ԥ�㼶�� 0 ���� 1 ���� 2 ʡ
			if(dto.getSbudgetlevelcode().equals(MsgConstant.BUDGET_LEVEL_SHARE) || dto.getSbudgetlevelcode().equals(MsgConstant.BUDGET_LEVEL_CENTER) 
					 || dto.getSbudgetlevelcode().equals(MsgConstant.BUDGET_LEVEL_PROVINCE)){
				;
			}
			else{
				// ������ͳ������
				List listforparm =makeIncomeBillforReportPrmt(idto,sleSumItem,sleSubjectType,rptDate);
				if (null != listforparm)
					list.addAll(listforparm);
			}
			
			return list;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}
	
	/**
	 * �������뱨����Ŀ�������С��
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

			// �������ش���洢��Ŀ����,���ջ��ش���洢��Ŀ����
			StringBuffer sqlbuf = new StringBuffer(
					"with tmp(s_FinOrgCode,s_TaxOrgCode,S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
			// ͳ�����뱨��
			String sql = "select b.s_SubjectType as s_FinOrgCode, b.s_ClassFlag as s_TaxOrgCode, S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR"
					+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " union all "
					+ "  select  b.s_SubjectType as s_FinOrgCode, b.s_ClassFlag as s_TaxOrgCode, S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR"
					+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere;
			
					/**
					 * 	�˿ⲿ����ʱ����
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

			//��ӡsql
			log.debug(sqlbuf.toString());
			
			List<TrIncomedayrptDto> list = (List<TrIncomedayrptDto>) exec
					.runQueryCloseCon(sqlbuf.toString(),
							TrIncomedayrptDto.class, true).getDtoCollection();

			BigDecimal btransday = new BigDecimal(0.00); // ת�����������ۼ�
			BigDecimal btranstenday = new BigDecimal(0.00); // ת��������Ѯ�ۼ�
			BigDecimal btransmonth = new BigDecimal(0.00); // ת�����������ۼ�
			BigDecimal btransquarter = new BigDecimal(0.00); // ת�������뼾�ۼ�
			BigDecimal btransyear = new BigDecimal(0.00); // ת�����������ۼ�

			BigDecimal bcommincday = new BigDecimal(0.00); // һ��Ԥ���������ۼ�
			BigDecimal bcomminctenday = new BigDecimal(0.00); // һ��Ԥ������Ѯ�ۼ�
			BigDecimal bcommincmonth = new BigDecimal(0.00); // һ��Ԥ���������ۼ�
			BigDecimal bcommincquarter = new BigDecimal(0.00); // һ��Ԥ�����뼾�ۼ�
			BigDecimal bcommincyear = new BigDecimal(0.00); // һ��Ԥ���������ۼ�

			BigDecimal bfundday = new BigDecimal(0.00); // �����������ۼ�
			BigDecimal bfundtenday = new BigDecimal(0.00); // ��������Ѯ�ۼ�
			BigDecimal bfundmonth = new BigDecimal(0.00); // �����������ۼ�
			BigDecimal bfundquarter = new BigDecimal(0.00); // �������뼾�ۼ�
			BigDecimal bfundyear = new BigDecimal(0.00); // �����������ۼ�

			BigDecimal bfundotherday = new BigDecimal(0.00); // ���������������ۼ�
			BigDecimal bfundothertenday = new BigDecimal(0.00); // ������������Ѯ�ۼ�
			BigDecimal bfundothermonth = new BigDecimal(0.00); // ���������������ۼ�
			BigDecimal bfundotherquarter = new BigDecimal(0.00); // �����������뼾�ۼ�
			BigDecimal bfundotheryear = new BigDecimal(0.00); // ���������������ۼ�

			BigDecimal bdebtday = new BigDecimal(0.00); // ծ���������ۼ�
			BigDecimal bdebttenday = new BigDecimal(0.00); // ծ������Ѯ�ۼ�
			BigDecimal bdebtmonth = new BigDecimal(0.00); // ծ���������ۼ�
			BigDecimal bdebtquarter = new BigDecimal(0.00); // ծ�����뼾�ۼ�
			BigDecimal bdebtyear = new BigDecimal(0.00); // ծ���������ۼ�
			
			BigDecimal allday = new BigDecimal(0.00); // �������ۼ�
			BigDecimal alltenday = new BigDecimal(0.00); // ����Ѯ�ۼ�
			BigDecimal allmonth = new BigDecimal(0.00); // �������ۼ�
			BigDecimal allquarter = new BigDecimal(0.00); // ���뼾�ۼ�
			BigDecimal allyear = new BigDecimal(0.00); // �������ۼ�
			
			for (TrIncomedayrptDto dto : list) {
				// ��Ŀ����
				String sbtCode = dto.getSbudgetsubcode();
				// ��Ŀ����
				String sbtType = dto.getSfinorgcode().trim();
				// ��Ŀ����
				String sbtClass = dto.getStaxorgcode().trim();
				// ͳ�Ƹ��������͵ķ�����
				if (sbtType.equals(StateConstant.SBT_TYPE_BUDGET_IN)
						|| sbtType.equals(StateConstant.SBT_TYPE_BUDGET_OUT)) {
					if (sbtClass.equals(StateConstant.SBT_CLASS_TRANSINCOME)) { // ת��������
						btransday =ArithUtil.add(btransday,dto.getNmoneyday(),2);
						btranstenday =ArithUtil.add(btranstenday,dto.getNmoneytenday(),2);
						btransmonth =ArithUtil.add(btransmonth,dto.getNmoneymonth(),2);
						btransquarter =ArithUtil.add(btransquarter,dto.getNmoneyquarter(),2);
						btransyear =ArithUtil.add(btransyear,dto.getNmoneyyear(),2);
					} else { // һ��Ԥ����
						bcommincday =ArithUtil.add(bcommincday,dto.getNmoneyday(),2);
						bcomminctenday =ArithUtil.add(bcomminctenday,dto.getNmoneytenday(),2);
						bcommincmonth =ArithUtil.add(bcommincmonth,dto.getNmoneymonth(),2);
						bcommincquarter =ArithUtil.add(bcommincquarter,dto.getNmoneyquarter(),2);
						bcommincyear =ArithUtil.add(bcommincyear,dto.getNmoneyyear(),2);
					}
				} else if (sbtType.equals(StateConstant.SBT_TYPE_FUND_IN)
						|| sbtType.equals(StateConstant.SBT_TYPE_FUND_OUT)) {
					if (sbtClass.equals(StateConstant.SBT_CLASS_FUNDINCOME)) { // ����������
						bfundday =ArithUtil.add(bfundday,dto.getNmoneyday(),2);
						bfundtenday =ArithUtil.add(bfundtenday,dto.getNmoneytenday(),2);
						bfundmonth =ArithUtil.add(bfundmonth,dto.getNmoneymonth(),2);
						bfundquarter =ArithUtil.add(bfundquarter,dto.getNmoneyquarter(),2);
						bfundyear =ArithUtil.add(bfundyear,dto.getNmoneyyear(),2);
					} else { // ��������Ԥ��������
						bfundotherday =ArithUtil.add(bfundotherday,dto.getNmoneyday(),2);
						bfundothertenday =ArithUtil.add(bfundothertenday,dto.getNmoneytenday(),2);
						bfundothermonth =ArithUtil.add(bfundothermonth,dto.getNmoneymonth(),2);
						bfundotherquarter =ArithUtil.add(bfundotherquarter,dto.getNmoneyquarter(),2);
						bfundotheryear =ArithUtil.add(bfundotheryear,dto.getNmoneyyear(),2);
					}
				} else if (sbtType.equals(StateConstant.SBT_TYPE_DEBT_IN)
						|| sbtType.equals(StateConstant.SBT_TYPE_DEBT_OUT)) { // ծ��������
					bdebtday =ArithUtil.add(bdebtday,dto.getNmoneyday(),2);
					bdebttenday =ArithUtil.add(bdebttenday,dto.getNmoneytenday(),2);
					bdebtmonth =ArithUtil.add(bdebtmonth,dto.getNmoneymonth(),2);
					bdebtquarter =ArithUtil.add(bdebtquarter,dto.getNmoneyquarter(),2);
					bdebtyear =ArithUtil.add(bdebtyear,dto.getNmoneyyear(),2);
				}
				
				allday = ArithUtil.add(allday,dto.getNmoneyday(),2); // �������ۼ�
				alltenday = ArithUtil.add(alltenday,dto.getNmoneytenday(),2); // ����Ѯ�ۼ�
				allmonth = ArithUtil.add(allmonth,dto.getNmoneymonth(),2); // �������ۼ�
				allquarter = ArithUtil.add(allquarter,dto.getNmoneyquarter(),2); // ���뼾�ۼ�
				allyear = ArithUtil.add(allyear,dto.getNmoneyyear(),2); // �������ۼ�
				
			}
			List<TrIncomedayrptDto> retlist = new ArrayList<TrIncomedayrptDto>();
			// ����һ��Ԥ������ϼ�
//			if (bcommincday.compareTo(new BigDecimal(0)) != 0
//					|| bcommincmonth.compareTo(new BigDecimal(0)) != 0
//					|| bcommincyear.compareTo(new BigDecimal(0)) != 0) {
				TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
				tmpdto.setSbudgetsubcode("");
				tmpdto.setSbudgetsubname("һ��Ԥ������ϼ�");
				tmpdto.setNmoneyday(bcommincday);
				tmpdto.setNmoneytenday(bcomminctenday);
				tmpdto.setNmoneymonth(bcommincmonth);
				tmpdto.setNmoneyquarter(bcommincquarter);
				tmpdto.setNmoneyyear(bcommincyear);
				retlist.add(tmpdto);
//			}
//			// ת��������ϼ�
//			if (btransday.compareTo(new BigDecimal(0)) != 0
//					|| btransmonth.compareTo(new BigDecimal(0)) != 0
//					|| btransyear.compareTo(new BigDecimal(0)) != 0) {
				TrIncomedayrptDto btranstmpdto = new TrIncomedayrptDto();
				btranstmpdto.setSbudgetsubcode("");
				btranstmpdto.setSbudgetsubname("ת��������ϼ�");
				btranstmpdto.setNmoneyday(btransday);
				btranstmpdto.setNmoneytenday(btranstenday);
				btranstmpdto.setNmoneymonth(btransmonth);
				btranstmpdto.setNmoneyquarter(btransquarter);
				btranstmpdto.setNmoneyyear(btransyear);
				retlist.add(btranstmpdto);
//			}
//			// ��ᱣ�ջ���Ԥ������ϼ�
//			if (bfundday.compareTo(new BigDecimal(0)) != 0
//					|| bfundmonth.compareTo(new BigDecimal(0)) != 0
//					|| bfundyear.compareTo(new BigDecimal(0)) != 0) {
				TrIncomedayrptDto bfundtmpdto = new TrIncomedayrptDto();
				bfundtmpdto.setSbudgetsubcode("");
				bfundtmpdto.setSbudgetsubname("��ᱣ�ջ���Ԥ������ϼ�");
				bfundtmpdto.setNmoneyday(bfundday);
				bfundtmpdto.setNmoneytenday(bfundtenday);
				bfundtmpdto.setNmoneymonth(bfundmonth);
				bfundtmpdto.setNmoneyquarter(bfundquarter);
				bfundtmpdto.setNmoneyyear(bfundyear);
				retlist.add(bfundtmpdto);
//			}
//			// ��������Ԥ������ϼ�
//			if (bfundotherday.compareTo(new BigDecimal(0)) != 0
//					|| bfundothermonth.compareTo(new BigDecimal(0)) != 0
//					|| bfundotheryear.compareTo(new BigDecimal(0)) != 0) {
				TrIncomedayrptDto bfundothertmpdto = new TrIncomedayrptDto();
				bfundothertmpdto.setSbudgetsubcode("");
				bfundothertmpdto.setSbudgetsubname("��������Ԥ������ϼ�");
				bfundothertmpdto.setNmoneyday(bfundotherday);
				bfundothertmpdto.setNmoneytenday(bfundothertenday);
				bfundothertmpdto.setNmoneymonth(bfundothermonth);
				bfundothertmpdto.setNmoneyquarter(bfundotherquarter);
				bfundothertmpdto.setNmoneyyear(bfundotheryear);
				retlist.add(bfundothertmpdto);
//			}
//			// ծ����Ԥ������ϼ�
//			if (bdebtday.compareTo(new BigDecimal(0)) != 0
//					|| bdebtmonth.compareTo(new BigDecimal(0)) != 0
//					|| bdebtyear.compareTo(new BigDecimal(0)) != 0) {
				TrIncomedayrptDto bdebttmpdto = new TrIncomedayrptDto();
				bdebttmpdto.setSbudgetsubcode("");
				bdebttmpdto.setSbudgetsubname("ծ����Ԥ������ϼ�");
				bdebttmpdto.setNmoneyday(bdebtday);
				bdebttmpdto.setNmoneytenday(bdebttenday);
				bdebttmpdto.setNmoneymonth(bdebtmonth);
				bdebttmpdto.setNmoneyquarter(bdebtquarter);
				bdebttmpdto.setNmoneyyear(bdebtyear);
				retlist.add(bdebttmpdto);
//			}
			// ���ܺϼ�	
				TrIncomedayrptDto alltmpdto = new TrIncomedayrptDto();
				alltmpdto.setSbudgetsubcode("");
				alltmpdto.setSbudgetsubname("�ϼ���");
				alltmpdto.setNmoneyday(allday);
				alltmpdto.setNmoneytenday(alltenday);
				alltmpdto.setNmoneymonth(allmonth);
				alltmpdto.setNmoneyquarter(allquarter);
				alltmpdto.setNmoneyyear(allyear);
				retlist.add(alltmpdto);

			return retlist;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}
	
	/**
	 * ���ձ����������������ѯ���������ձ�
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
		
		//��ѯsql
		StringBuffer sqlbuf = new StringBuffer(
		"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
		//ƴдԤ������sql
		for(int i=0; i<ReportPrmtlist.size(); i++){
			TsReportmainDto dto = ReportPrmtlist.get(i);
			
			//ƴд������������Ĳ�ѯ����
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
		
//		//ƴд�˿�sql
//		for(int i=0; i<ReportPrmtlist.size(); i++){
//			TsReportmainDto dto = ReportPrmtlist.get(i);
//			
//			//ƴд������������Ĳ�ѯ����
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
		
		//��ӡsql
		log.debug(sqlbuf.toString());
		
		SQLExecutor exec;
		try {
			exec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			
			List<TrIncomedayrptDto> list = (List<TrIncomedayrptDto>) exec.runQueryCloseCon(sqlbuf.toString(),
					TrIncomedayrptDto.class, true).getDtoCollection();
			
			BigDecimal zero= ArithUtil.round(new BigDecimal(0.0000), 2) ;
			//������ʾ˳��
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
					//���û����ʾ���ݣ���ȫ������Ϊ0
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
			throw new ITFEBizException("��ѯ����", e);
		}
	}
	
	/**
	 * ȡ�ñ��������Ϣ
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
			throw new ITFEBizException("ȡ�ñ����������", e);
		}
	}
	
	private String makeIncomeBillwhere(IDto idto, String sleSumItem, String sleSubjectType,
			String rptDate,String sbillkind) throws ITFEBizException{
		
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;
		
		/**
		 * ��ѯ�߼���
		 * �������ڣ�������������
		 * �տ������룺�������
		 * ���ջ��ش��룺 �������ջ��� + 000000000000�������ջ���,111111111111��˰,222222222222��˰,333333333333����,444444444444����,555555555555����
		 * ��Ŀ���ͣ�01һ��Ԥ����02һ��Ԥ����03Ԥ���ڻ���04Ԥ����ծ��05Ԥ�������06����07ת��������---sleSubjectType
		 * Ԥ�����ࣺ 1Ԥ����,2Ԥ����
		 * Ԥ�㼶�Σ� 1 ����,2 ʡ,3 ��,4 ��,5 ��,6�ط�
		 * Ͻ����־�� 0ȫϽ,1����,2ȫϽ�Ǳ���	
		 * ����ϼ�: 1 ������ϼ�,2����ϼ�----sleSumItem
		 * �����ڱ�־��0������1������
		 */
		// ��ѯsql ������룬Ͻ����־�����ջ��ش��룬��ѯ���ڣ�Ԥ�����࣬Ԥ�㼶�Σ�������,��������
		String sqlwhere = "";
		
		/**�տ��������Ͻ����־�����ջ������ϲ�ѯ
		 * Ͻ����־����������ǲ�ѯ����Ϊ��ǰ�������+������־ --��ѯĳһ�����ջ��� ���ѯ���ջ��ش����ʱ���ѯ��ǰ���������е����ջ���
		 * Ͻ����־���ȫϽ���ǲ�ѯ����Ϊ��ǰ�������+ȫϽ��־ --ֻ��ѯ���ջ��ش���
		 * Ͻ����־���ȫϽ�Ǳ������ǲ�ѯ����Ϊ��ǰ������������Ͻ�����й��⣨�������������⣩+ȫϽ��־ -- ���ջ��س����� ��ѯ��ǰ�����µ��������ջ���
		 */
		if (null == dto.getStrecode()
				&& "".equals(dto.getStrecode().trim())) {
			throw new ITFEBizException("��ѯ������������벻��Ϊ�գ�");
		}
		if (null == dto.getSbelongflag()
				&& "".equals(dto.getSbelongflag().trim())) {
			throw new ITFEBizException("��ѯ������Ͻ����־����Ϊ�գ�");
		}
		if (null == dto.getStaxorgcode()
				&& "".equals(dto.getStaxorgcode().trim())) {
			throw new ITFEBizException("��ѯ���������ջ��ش��벻��Ϊ�գ�");
		}
		
		//ȫϽ--���ջ��ش���--�������
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
				throw new ITFEBizException("��ѯ������Ͻ����־λȫϽ��ʱ�����ջ���ֻ����ѡ�����մ��࣡");
			}
		}
		
		//����--���ջ��ش����������ջ���--�������
		if(MsgConstant.RULE_SIGN_SELF.equals(dto.getSbelongflag().trim())){
			if(dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_NATION_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_PLACE_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_OTHER_CLASS) ){//���ջ��ش���
				sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' "
				+" and a.s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"+dto.getStrecode().trim()+"' AND S_TAXPROP='"+dto.getStaxorgcode().trim().subSequence(0, 1)+"' ) "
				+" and a.S_BELONGFLAG ='"+dto.getSbelongflag().trim()+"' ";
			}
			else if(dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)){//�������ջ���
				sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' "
				+" and a.s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) "
				+" and a.S_BELONGFLAG ='"+dto.getSbelongflag().trim()+"' ";
			}
			else{//�������ջ���
				sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' "+" and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' "+" and a.S_BELONGFLAG ='"+dto.getSbelongflag().trim()+"' ";
			}
		}
		
		//ȫϽ�Ǳ���--���ջ��ش���--������룺����Ͻ�������ȫϽ����֮�ͣ�����������������ȫϽ��
		if(MsgConstant.RULE_SIGN_ALLNOTSELF.equals(dto.getSbelongflag().trim())){
			if(dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_NATION_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_PLACE_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_OTHER_CLASS) ||
					dto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)
					){
				//������п��ѯ��������ȫϽ����������ؿ��ѯ��������ȫϽ��
				if(dto.getStrecode().equals("0603000000")){
					sqlwhere += " and a.s_trecode in ( "+" SELECT s_trecode FROM TS_TREASURY WHERE substr(s_trecode,1,4)='"+dto.getStrecode().trim().substring(0, 4)+"' and s_trelevel='4') "+" and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' "+" and a.S_BELONGFLAG ='"+MsgConstant.RULE_SIGN_ALL+"' ";
				}
				else{
					sqlwhere += " and a.s_trecode in ( "+" SELECT s_trecode FROM TS_TREASURY WHERE substr(s_trecode,1,6)='"+dto.getStrecode().trim().substring(0, 6)+"' and s_trelevel='5') "+" and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' "+" and a.S_BELONGFLAG ='"+MsgConstant.RULE_SIGN_ALL+"' ";
				}
//				sqlwhere += " and a.s_trecode in ( "+" SELECT S_TRECODE FROM TS_TREASURY WHERE S_GOVERNTRECODE='"+dto.getStrecode().trim()+"' ) and a.s_trecode <> '"+dto.getStrecode().trim()+"' and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' "+" and a.S_BELONGFLAG ='"+MsgConstant.RULE_SIGN_ALL+"' ";
			}
			else{
				throw new ITFEBizException("��ѯ������Ͻ����־λȫϽ�Ǳ�����ʱ�����ջ���ֻ����ѡ�����մ��࣡");
			}
		}
		
		/**
		 * ��������
		 */
		if (null != dto.getSrptdate()
				&& !"".equals(dto.getSrptdate().trim())) {
			sqlwhere += " and a.S_RPTDATE ='"+dto.getSrptdate().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
		}
		
		/**
		 * Ԥ������
		 */
		if (null != dto.getSbudgettype()
				&& !"".equals(dto.getSbudgettype().trim())) {
			sqlwhere += " and a.s_BudgetType ='"+dto.getSbudgettype().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ������Ԥ�����಻��Ϊ�գ�");
		}
		
		/**Ԥ�㼶�������ж�
		 * ��Ԥ�㼶��Ϊ�ط�ʱ����ѯ���м���δ�ǹ��� ����������� 
		 */
		if (null != dto.getSbudgetlevelcode() && !"".equals(dto.getSbudgetlevelcode().trim())) {
			
			if(MsgConstant.BUDGET_LEVEL_PLACE.equals(dto.getSbudgetlevelcode().trim())){//�ط�
				//������п��ѯ������ģ�������ؿ��ѯ�������
				if(dto.getStrecode().equals("0603000000")){
					sqlwhere += " and a.S_BUDGETLEVELCODE >='"+MsgConstant.BUDGET_LEVEL_DISTRICT+"' ";
				}
				else{
					sqlwhere += " and a.S_BUDGETLEVELCODE >='"+MsgConstant.BUDGET_LEVEL_PREFECTURE+"' ";
				}
			}
			else{//����
				sqlwhere += " and a.S_BUDGETLEVELCODE ='"+dto.getSbudgetlevelcode().trim()+"' ";
			}
		}
		else{
			throw new ITFEBizException("��ѯ������Ԥ�㼶�β���Ϊ�գ�");
		}
		
		/**
		 * �����ڱ�־
		 */
		if (null != dto.getStrimflag()
				&& !"".equals(dto.getStrimflag().trim())) {
			sqlwhere += " and a.s_TrimFlag ='"+dto.getStrimflag().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ�����������ڱ�־����Ϊ�գ�");
		}
		
		/**
		 * ��������
		 */
		sqlwhere += " and a.S_BILLKIND ='"+sbillkind+"' ";
		
		/**
		 * ��Ŀ���� sleSubjectType �������ڿ�Ŀ�����ڱ���������水���ͳ�ƣ�����������Բ�����������
		 */
		
		/**
		 * ���ڸ��������嵥��һ��Ԥ���Ŀ���룬��˲�ѯ��ʱ��Ӧ���Ͽ�Ŀ����ĺ�����������
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
		 * �������
		 */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ������������벻��Ϊ�գ�");
		}
		
		/**
		 * ��������
		 */
		if (null != dto.getSrptdate()
				&& !"".equals(dto.getSrptdate().trim())) {
			sqlwhere += " and a.S_RPTDATE ='"+dto.getSrptdate().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
		}
		
		/**
		 * Ͻ����־
		 */
		if (null != dto.getSbelongflag()
				&& !"".equals(dto.getSbelongflag().trim())) {
			sqlwhere += " and a.S_BELONGFLAG ='"+dto.getSbelongflag().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ������Ͻ����־����Ϊ�գ�");
		}
		
		/**
		 * �����ڱ�־
		 */
		if (null != dto.getStrimflag()
				&& !"".equals(dto.getStrimflag().trim())) {
			sqlwhere += " and a.s_TrimFlag ='"+dto.getStrimflag().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ�����������ڱ�־����Ϊ�գ�");
		}
		
		/**
		 * ���ڸ��������嵥��һ��Ԥ���Ŀ���룬��˲�ѯ��ʱ��Ӧ���Ͽ�Ŀ����ĺ�����������
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_BOOKORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		
		return sqlwhere;
	}
	
	/**
	 * ����֧���ձ�
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
				//Ŀ
				"select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (select * from TR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME union all "
				+ "  select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (select * from HTR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME union all "
				
				//��
				+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (select * from TR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME union all "
				+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
				+ " from (select * from HTR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME union all ";
				
				//��
				//�Ƿ񺬿�ϼ�:Ĭ��Ϊ������ϼ�
				if(sleSumItem ==null || "".equals(sleSumItem) || StateConstant.REPORTTYPE_0406_YES.equals(sleSumItem)){
					sql += "  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
					+ " from (select * from TR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME union all "
					+ "  select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
					+ " from (select * from HTR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME union all ";
				}
				
				//��
				sql += 
				  "  select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (select * from TR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME union all "
				+ "  select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
				+ " from (select * from HTR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME order by S_BUDGETSUBCODE,S_BUDGETSUBNAME ";
				sqlbuf.append(sql);
				sqlbuf.append(") select S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		
		//�ϼ�sql
		StringBuffer sqlsumbuf = new StringBuffer(
		"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
		
		String sqlsum = 
			//Ŀ
			"select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
			+ " from (select * from TR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME union all "
			+ "  select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
			+ " from (select * from HTR_TAXORG_PAYOUT_REPORT where S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME ";
			
		sqlsumbuf.append(sqlsum);
		sqlsumbuf.append(") select '' as S_BUDGETSUBCODE,'�ϼ���' as S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp ");
				
		try {
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			//��ӡsql
			log.debug(sqlbuf.toString());
			log.debug(sqlsumbuf.toString());
			
			//����Ŀ
			List list = (List)exec.runQueryCloseCon(sqlbuf.toString(), TrTaxorgPayoutReportDto.class,true).getDtoCollection();
			//�ϼ�
			
			SQLExecutor exec1 = DatabaseFacade.getODB().getSqlExecutorFactory()
			.getSQLExecutor();
			
			List listall=(List)exec1.runQueryCloseCon(sqlsumbuf.toString(), TrTaxorgPayoutReportDto.class,true).getDtoCollection();
			list.addAll(listall);
			
			return list;
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}
	
	private String makePayoutBillwhere(IDto idto, String sleSumItem, String moneyUnit,
			String rptDate) throws ITFEBizException {
		
		TrTaxorgPayoutReportDto dto = (TrTaxorgPayoutReportDto) idto;
		String sqlwhere = "";

		/**
		 * �������
		 */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ������������벻��Ϊ�գ�");
		}
		
		/**
		 * Ԥ�㵥λ����--��S_FINORGCODE������棬��DataMoveUtil
		 */
		if (null != dto.getSfinorgcode()
				&& !"".equals(dto.getSfinorgcode().trim())) {
			sqlwhere += " and a.S_FINORGCODE ='"+dto.getSfinorgcode().trim()+"' ";
		}
		
		/**
		 * ��������
		 */
		if (null != dto.getSrptdate()
				&& !"".equals(dto.getSrptdate().trim())) {
			sqlwhere += " and a.S_RPTDATE ='"+dto.getSrptdate().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
		}
		
		/**
		 * Ԥ������
		 */
		if (null != dto.getSbudgettype()
				&& !"".equals(dto.getSbudgettype().trim())) {
			sqlwhere += " and a.s_BudgetType ='"+dto.getSbudgettype().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ������Ԥ�����಻��Ϊ�գ�");
		}
		
		/**
		 * Ԥ�㼶��--��Ԥ�㼶�δ��뵱�������������ͣ�1һ��Ԥ��֧����2���а�����Ȩ֧������ö��ֵ���涨��0319
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
			throw new ITFEBizException("��ѯ�������������Ʋ���Ϊ�գ�");
		}
		
		/**
		 * ���ڸ��������嵥��һ��Ԥ���Ŀ���룬��˲�ѯ��ʱ��Ӧ���Ͽ�Ŀ����ĺ�����������
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_ORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		
		return sqlwhere;
	}
	
	

	public List makeFinincomeOnlineBill(IDto idto, String startdate,String enddate,String remak)
			throws ITFEBizException {
		
		/*TrFinIncomeonlineDayrptDto dto = (TrFinIncomeonlineDayrptDto) idto;
		String sleBillType=dto.getSremark1().trim(); //ȡ�ñ������� �� ��
		
		//�±���ѯ�������� ���ǵ��·���ʱ������ ��Ϊ���������ۼӵģ�����ȡ���һ���������µĺϼ�
		String sqlmonth=" ( "+
//						" select t.* from TR_FIN_INCOMEONLINE_DAYRPT t, "+
//						" (select S_BDGSBTCODE,S_TRECODE,max(S_ACCT) S_ACCT,S_TAXPAYCODE,S_TAXORGCODE "+ 
//						" from TR_FIN_INCOMEONLINE_DAYRPT where substr(S_ACCT,1,6)='"+rptDate.substring(0, 6)+"' group by S_BDGSBTCODE,S_TRECODE,S_TAXPAYCODE,S_TAXORGCODE) s "+
//						" where t.S_BDGSBTCODE=s.S_BDGSBTCODE and t.S_TRECODE=s.S_TRECODE and t.S_ACCT=s.S_ACCT and t.S_TAXPAYCODE=s.S_TAXPAYCODE and t.S_TAXORGCODE=s.S_TAXORGCODE "+
//						" ) ";
		
		//�걨��ѯ��������
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
		
		// �޸�ֻ��ѯԤ���ڵ����� 20110326 S_BUDGETTYPE='2' MsgConstant.BDG_KIND_IN
		String sql = 
			  " select a.S_ACCT,a.S_TAXPAYCODE,a.S_TAXPAYNAME,a.S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, "
			 +" a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH, a.N_MONEYQUARTER, a.N_MONEYYEAR, a.N_CENTERMONEYDAY, a.N_CENTERMONEYTENDAY, a.N_CENTERMONEYMONTH, a.N_CENTERMONEYQUARTER, "  
			 +" a.N_CENTERMONEYYEAR, a.N_PLACEMONEYDAY, a.N_PLACEMONEYTENDAY, a.N_PLACEMONEYMONTH, a.N_PLACEMONEYQUARTER, a.N_PLACEMONEYYEAR,'' as S_Remark1 " 
			 +" from TR_FIN_INCOMEONLINE_DAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_IN+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
			 +" union all "
			 +" select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, " 
			 +" sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY, " 
			 +" sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY, " 
			 +" sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'С��' as S_Remark1 " 
			 +" from TR_FIN_INCOMEONLINE_DAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_IN+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
			 +" group by a.S_BDGSBTCODE,b.S_SUBJECTNAME "
			 +" union all "  
			 +" select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXORGCODE,'' as S_BDGSBTCODE,'' as S_BDGSBTNAME, " 
			 +" sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY, " 
			 +" sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY, " 
			 +" sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'�ϼ�' as S_Remark1 " 
			 +" from TR_FIN_INCOMEONLINE_DAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_IN+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere;
		
//		sql += " union all " + sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", "HTR_FIN_INCOMEONLINE_DAYRPT");
		
		// ��֧���»���ҵͳ���ձ����ѯ
		if (StateConstant.REPORT_DAY.equals(sleBillType)) {
			;
		}
		// ��֧���»���ҵͳ��Ѯ�����ѯ
		else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
			throw new ITFEBizException("Ŀǰ��֧����֧���»���ҵͳ��Ѯ�����ѯ����");
		}
		// ��֧���»���ҵͳ���±����ѯ
		else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
			sql = sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", sqlmonth);
		}
		// ��֧���»���ҵͳ�Ƽ������ѯ
		else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
			throw new ITFEBizException("Ŀǰ��֧����֧���»���ҵͳ�Ƽ������ѯ����");
		}
		// ��֧���»���ҵͳ���걨���ѯ
		else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
			sql = sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", sqlyear);
		}
		sqlbuf.append(sql);
				
		sqlbuf.append(") select S_ACCT,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXORGCODE,S_BDGSBTCODE,S_BDGSBTNAME, "
			+" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER,  "
			+" N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR,S_Remark1 from tmp order by S_BDGSBTCODE desc,S_BDGSBTNAME desc ");


//�ݴ�
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
					+"   sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'С��' as S_Remark1  "
					+"   from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
					+"   group by a.S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME,a.S_ACCT "
					+"  union all "
					+"  select a.S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,'' as S_BDGSBTCODE,'' as S_BDGSBTNAME, '' as S_TRECODE,   "
					+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,  "
					+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
					+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'���ۼ�' as S_Remark1  "
					+"  from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
					+"  group by a.S_ACCT "
					+"  union all "
//					+"  select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, '' as S_TRECODE, "
//					+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,  "
//					+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
//					+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'��ĿС��' as S_Remark1  "
//					+"  from TR_FIN_INCOMEONLINE_DAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
//					+"  group by a.S_BDGSBTCODE,b.S_SUBJECTNAME "
//					+"  union all "   
					+"  select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,'' as S_BDGSBTCODE,'' as S_BDGSBTNAME, '' as S_TRECODE,  "
					+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,"  
					+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
					+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'�ϼ�' as S_Remark1  "
					+"  from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere;
			
			sql += " union all " + sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", "HTR_FIN_INCOMEONLINE_DAYRPT");
			
			sqlbuf.append(sql);
					
			sqlbuf.append(") select S_TAXVOUNO,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXORGCODE,S_ACCT,S_BDGSBTCODE,S_BDGSBTNAME,S_TRECODE, "
						+" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER, " 
						+"N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR,S_Remark1 from tmp order by s_acct,S_BDGSBTCODE ,S_TAXORGCODE,s_taxpaycode ");

*/
		StringBuffer sqlbuf = new StringBuffer("");
		
		if(remak.equals("3")){//�����
			sqlbuf = makeFinincomeOnlineBillSql(idto, startdate, enddate, remak);
		}else if(remak.equals("4")){//�����
			sqlbuf = makeFinincomeOnlineSql(idto, startdate, enddate, remak);
		}else{//Ŀ����
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
			
			//��ӡsql
			log.debug(sqlbuf.toString());
			
			List<TrFinIncomeonlineDayrptDto> list= (List) exec.runQueryCloseCon(sqlbuf.toString(), TrFinIncomeonlineDayrptDto.class,true).getDtoCollection();
//			Collections.reverse(list);
			
			//��ȫ��
			return list;
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
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
	 * ȡ����˰�˺͹����Ӧ��ϵ
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public static List<TsTaxpaycodeDto> getTsTaxpaycodeDtolist(TrFinIncomeonlineDayrptDto dto) throws ITFEBizException {
		String where = " WHERE S_TAXPAYCODE = '" + dto.getStaxpaycode()+"' and S_TRECODE='"+dto.getStrecode()+"' ";
		 try {
			return DatabaseFacade.getDb().find(TsTaxpaycodeDto.class, where);
		} catch (JAFDatabaseException e) {
			log.error("ȡ����˰�˺͹����Ӧ��ϵʱ����!", e);
			throw new ITFEBizException("ȡ����˰�˺͹����Ӧ��ϵ����!", e);
		}
	}
	
	private String makeFinincomeOnlineBillwhere(IDto idto,String startdate,String enddate,String remak) throws ITFEBizException {
		
		TrFinIncomeonlineDayrptDto dto = (TrFinIncomeonlineDayrptDto) idto;
		String sqlwhere = "";

		/**
		 * �������
		 */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ������������벻��Ϊ�գ�");
		} 
		/**
		 * ���ջ��ش���
		 */
		if (null != dto.getStaxorgcode()
				&& !"".equals(dto.getStaxorgcode().trim())) {
			sqlwhere += " and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' ";
		}
		
		/**
		 * Ԥ���Ŀ����
		 */
		if (null != dto.getSbdgsbtcode()
				&& !"".equals(dto.getSbdgsbtcode().trim())) {
			sqlwhere += " and a.s_BdgSbtCode ='"+dto.getSbdgsbtcode().trim()+"' ";
		}
		
		/**
		 * Ԥ���Ŀ����
		 */
		if (null != dto.getSbdgsbtname()
				&& !"".equals(dto.getSbdgsbtname().trim())) {
			sqlwhere += " and a.s_BdgSbtName ='"+dto.getSbdgsbtname().trim()+"' ";
		}
		
		/**
		 * Ԥ������
		 */
		sqlwhere += " and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_IN+"' ";
		
		/**
		 * ��˰�˴���
		 */
		if (null != dto.getStaxpaycode()
				&& !"".equals(dto.getStaxpaycode().trim())) {
			sqlwhere += " and a.s_TaxPayCode ='"+dto.getStaxpaycode().trim()+"' ";
		}
		
		/**
		 * ��˰������
		 */
		if (null != dto.getStaxpayname()
				&& !"".equals(dto.getStaxpayname().trim())) {
			sqlwhere += " and a.s_TaxPayName ='"+dto.getStaxpayname().trim()+"' ";
		}
		
		/**
		 * ��������
		 */
//		if (null != dto.getSacct()
//				&& !"".equals(dto.getSacct().trim())) {
//			sqlwhere += " and a.S_ACCT ='"+dto.getSacct().trim()+"' ";
//		}
//		else{
//			throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
//		}
		//�޸�20110326---�ÿ�ʼ���ڽ������ڲ�ѯ
		/**
		 * ��ʼ����
		 */
		if (null != startdate
				&& !"".equals(startdate.trim())) {
			sqlwhere += " and a.S_ACCT >='"+startdate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ��������ʼ���ڲ���Ϊ�գ�");
		}
		
		/**
		 * ��������
		 */
		if (null != enddate
				&& !"".equals(enddate.trim())) {
			sqlwhere += " and a.S_ACCT <='"+enddate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
		}
		
		/**
		 * ���ڸ��������嵥��һ��Ԥ���Ŀ���룬��˲�ѯ��ʱ��Ӧ���Ͽ�Ŀ����ĺ�����������
		 */
//		sqlwhere += " and b.S_ORGCODE = '"+this.getLoginInfo().getSorgcode()+"' ";
		
		//mod 2011-09-06
		sqlwhere += " and b.S_ORGCODE in (SELECT S_BOOKORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		
		/**
		 * �����ѯΪ�ص���ҵ����˰��ͳ�Ʊ����������
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
		 * �������
		 * */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ������������벻��Ϊ�գ�");
		}
		
		/**
		 * Ԥ������
		 */
		sqlwhere += " and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_IN+"' ";
		
		/**
		 * ��˰�˴���
		 */
		if (null != dto.getStaxpaycode()
				&& !"".equals(dto.getStaxpaycode().trim())) {
			sqlwhere += " and a.s_TaxPayCode ='"+dto.getStaxpaycode().trim()+"' ";
		}
		
		/**
		 * ��˰������
		 */
		if (null != dto.getStaxpayname()
				&& !"".equals(dto.getStaxpayname().trim())) {
			sqlwhere += " and a.s_TaxPayName ='"+dto.getStaxpayname().trim()+"' ";
		}
		
		//�޸�20110326---�ÿ�ʼ���ڽ������ڲ�ѯ
		/**
		 * ��ʼ����
		 */
		if (null != startdate
				&& !"".equals(startdate.trim())) {
			sqlwhere += " and a.S_ACCT >='"+startdate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ��������ʼ���ڲ���Ϊ�գ�");
		}
		
		/**
		 * ��������
		 */
		if (null != enddate
				&& !"".equals(enddate.trim())) {
			sqlwhere += " and a.S_ACCT <='"+enddate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
		}
		
		
//		sqlwhere += " and b.S_ORGCODE = '"+this.getLoginInfo().getSorgcode()+"' ";
		/**
		 * ���ڸ��������嵥��һ��Ԥ���Ŀ���룬��˲�ѯ��ʱ��Ӧ���Ͽ�Ŀ����ĺ�����������
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_BOOKORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
		
		/**
		 * �����ѯΪ�ص���ҵ����˰��ͳ�Ʊ����������
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
		
		//�����±�
		if(rptType.equals(StateConstant.RPT_TYPE_1)){
//			list=(List<TrIncomedayrptDto>)makeIncomeBill(idto,sleSumItem,sleSubjectType,rptDate);
			//ȡ�û�����������
			List<TrIncomedayrptDto> list=(List<TrIncomedayrptDto>)getIncomeBill(idto,sleSumItem,sleSubjectType,rptDate);
			if(list==null || list.size()<=0){
				return null;
			}
			datamap=new HashMap<String, String[]>();
			for(TrIncomedayrptDto dto :list){
				datamap.put(dto.getSbudgetsubcode(), (new String[]{dto.getNmoneymonth().toString()}));
			}
		}
		//֧���±�
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
		//��֧Ѯ��
		if(rptType.equals(StateConstant.RPT_TYPE_3)){
			//list=null;
		}
		//ȫ�ھ�˰��
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
		//��ĩ��֧���ȱ�
		if(rptType.equals(StateConstant.RPT_TYPE_5)){
			
			datamap=new HashMap<String, String[]>();
			//ȡ�û�����������
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
			
			//��˰����
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
	 * �޸Ĳ�ѯdto����
	 * @param idto
	 */
	private TrTaxorgPayoutReportDto changeDto(IDto idto){
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;
		TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();
		String sqlwhere = "";
		
		/**
		 * �������
		 */
		payoutdto.setStrecode(dto.getStrecode());
		/**
		 * ��������
		 */
		payoutdto.setSrptdate(dto.getSrptdate());
		/**
		 * Ԥ������
		 */
		payoutdto.setSbudgettype(dto.getSbudgettype());
		/**
		 * Ԥ�㼶��--��Ԥ�㼶�δ��뵱�������������ͣ�1һ��Ԥ��֧����2���а�����Ȩ֧������ö��ֵ���涨��0319
		 */
		payoutdto.setSbudgetlevelcode("1");
		return payoutdto;
	}
	
	/**
	 * ȡ�õ�˰��������˰��
	 * 
	 * @generated
	 * @param idto
	 * @param sleSumItem--�Ƿ���ϼ�
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List getIncomeBill2(IDto idto, String sleSumItem, String sleSubjectType,
			String rptDate) throws ITFEBizException {
		
		try {
			
			TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;
			dto.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);

			//��������
			String sqlwhere =makeIncomeBillwhere(idto,sleSumItem,sleSubjectType,rptDate,StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
			
			StringBuffer sqlbuf = new StringBuffer(
					"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
			// ͳ�����뱨��--���벿��
			//��
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
			
			//��ӡsql
			log.debug(sqlbuf.toString());
			
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			List list = (List) exec.runQueryCloseCon(sqlbuf.toString(),
					TrIncomedayrptDto.class, true).getDtoCollection();
			
			return list;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}
	
	/**
	 * ȡ�ò���֧������
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
				//Ŀ
				"select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME union all "
				+ "  select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' AND a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "+sqlwhere+" group by a.S_BUDGETSUBCODE,b.S_SUBJECTNAME  union all "
		
				//��
				+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME union all "
				+ "  select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR  "
				+ " from (SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "+sqlwhere+" and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME ";
				sqlbuf.append(sql);
				sqlbuf.append(") select S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) as N_MONEYDAY,sum(N_MONEYTENDAY) as N_MONEYTENDAY ,sum(N_MONEYMONTH) as N_MONEYMONTH,SUM(N_MONEYQUARTER) as N_MONEYQUARTER,SUM(N_MONEYYEAR) as N_MONEYYEAR from tmp group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		try {
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			//��ӡsql
			log.debug(sqlbuf.toString());
			
			return (List) exec.runQueryCloseCon(sqlbuf.toString(), TrTaxorgPayoutReportDto.class,true)
					.getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}
	
	/**
	 * ȫ�ھ�˰���ձ�
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
		String sbilltype = dto.getSbillkind(); // ��������
		List<TsBudgetsubjectDto> subjectList;
		List<TrIncomedayrptDto> reportList;
		
		//ȡ��˰�մ���
		try {
			String sql1 = "select S_SUBJECTCODE ,s_subjectName from ts_Budgetsubject where length(S_SUBJECTCODE) =5 and s_classflag = '"+StateConstant.SUBJECT_INCOME+"'";
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			subjectList = (List) exec.runQueryCloseCon(sql1,
					TsBudgetsubjectDto.class).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
		
		String sqlwhere = makeAllTaxBillwhere(idto,billCode,moneyUnit,rptDate);
		
		// ˰������
		String sql = " select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_SHARE_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// ��˰����
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_NATION_CLASS
				+ "' as S_BUDGETSUBCODE,'���У���˰����' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE ,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
				+ " where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_NATION_CLASS
				+ "' "+sqlwhere+"  GROUP BY  S_BUDGETLEVELCODE union all ";
		// ��˰����
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_PLACE_CLASS
				+ "' as S_BUDGETSUBCODE,'��˰����' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
				+ " where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_PLACE_CLASS
				+ "' "+sqlwhere+" GROUP BY  S_BUDGETLEVELCODE union all ";
		// ���ز���
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_CUSTOM_CLASS
				+ "' as S_BUDGETSUBCODE,'���ز���' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
				+ " where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_CUSTOM_CLASS
				+ "' "+sqlwhere+" GROUP BY  S_BUDGETLEVELCODE union all ";
		// ��������
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
				+ "' as S_BUDGETSUBCODE,'��������' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
				+ " where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
				+ "' "+sqlwhere+" GROUP BY  S_BUDGETLEVELCODE union all ";
		// ��������
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_OTHER_CLASS
				+ "' as S_BUDGETSUBCODE,'��������' as S_BUDGETSUBNAME, S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a"
				+ " where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_OTHER_CLASS
				+ "' "+sqlwhere+" GROUP BY  S_BUDGETLEVELCODE union all ";
		// ȫϽ�������ջ���
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
				throw new ITFEBizException("��ѯ�޼�¼");
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
		HashMap<String, String> subMap = new HashMap<String, String>();
		HashMap<String, BigDecimal> allMap = new HashMap<String, BigDecimal>();
		// ������� �±���Ҫ��ʾ��List
		for (TrIncomedayrptDto tmpdto : reportList) {
			if (!subMap.containsKey(tmpdto.getSbudgetsubcode())) {
				subMap.put(tmpdto.getSbudgetsubcode(), tmpdto
						.getSbudgetsubname());
			}
			BigDecimal nmoney;
			if (sbilltype.equals(StateConstant.REPORT_YEAR)) {// �걨
				nmoney = tmpdto.getNmoneyyear();
			} else if (sbilltype.equals(StateConstant.REPORT_QUAR)) { // ����
				nmoney = tmpdto.getNmoneyquarter();
			} else if (sbilltype.equals(StateConstant.REPORT_MONTH)) { // �±�
				nmoney = tmpdto.getNmoneymonth();
			} else if (sbilltype.equals(StateConstant.REPORT_TEN)) { // Ѯ��
				nmoney = tmpdto.getNmoneytenday();
			} else { // �ձ�
				nmoney = tmpdto.getNmoneyday();
			}
			allMap.put(tmpdto.getSbudgetsubcode()
					+ tmpdto.getSbudgetlevelcode(), nmoney);
		}
		List<ReportDto> billList = new ArrayList<ReportDto>();
		//���ջ��ش���
		for (int i = 0; i < 5; i++) {
			ReportDto tmp = new ReportDto();
			String subcode = null;
			if (i == 1) {
				subcode = MsgConstant.MSG_TAXORG_NATION_CLASS;
				tmp.setSbudgetsubname("���У���˰����");
			}else if(i ==2){
				subcode = MsgConstant.MSG_TAXORG_PLACE_CLASS;
				tmp.setSbudgetsubname("          ��˰����");
			}else if(i ==3){
				subcode = MsgConstant.MSG_TAXORG_CUSTOM_CLASS;
				tmp.setSbudgetsubname("          ���ز���");
			}else if(i==4){
				subcode = MsgConstant.MSG_TAXORG_OTHER_CLASS;
				tmp.setSbudgetsubname("          ��������");
			}else if(i ==0){
				subcode = "101";
				tmp.setSbudgetsubname("˰������ϼ�");
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
			tmp.setNcentralmoney(central);// ����
			tmp.setNprovincemoney(provice);// ʡ��
			tmp.setNcitymoney(city);// �м�
			tmp.setNcountymoney(county);// ���ؼ�
			tmp.setNtownmoney(town);// �м�
			billList.add(tmp);
		}
		//����Ŀ����
		int i = 0;
		for (TsBudgetsubjectDto tmpdto : subjectList) {
			i++;
			ReportDto tmp = new ReportDto();
			String subcode = tmpdto.getSsubjectcode();
			String subname = tmpdto.getSsubjectname();
			tmp.setSbudgetsubcode(subcode);
			tmp.setSbudgetsubname(i+"��"+subname);
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
			tmp.setNcentralmoney(central);// ����
			tmp.setNprovincemoney(provice);// ʡ��
			tmp.setNcitymoney(city);// �м�
			tmp.setNcountymoney(county);// ���ؼ�
			tmp.setNtownmoney(town);// �м�
			billList.add(tmp);
		}
		
		return billList;
	}
	
	/**
	 *  ȡ��ȫ�ھ�˰������
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
		String sbilltype = dto.getSbillkind(); // ��������
		List<TsBudgetsubjectDto> subjectList;
		List<TrIncomedayrptDto> reportList;
		
		//ȡ��˰�մ���
		try {
			String sql1 = "select S_SUBJECTCODE ,s_subjectName from ts_Budgetsubject where length(S_SUBJECTCODE) =5 and s_classflag = '"+StateConstant.SBT_CLASS_TAXINCOME+"' AND S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and S_ORGCODE in (SELECT S_BOOKORGCODE FROM TS_TREASURY WHERE S_TRECODE='"+dto.getStrecode().trim()+"' ) ";
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			subjectList = (List) exec.runQueryCloseCon(sql1,
					TsBudgetsubjectDto.class).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
		
		String sqlwhere = makeAllTaxBillwhere(idto,billCode,moneyUnit,rptDate);
		
		// ˰������
		String sql = " select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_SHARE_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// ��˰����
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_NATION_CLASS
				+ "' as S_BUDGETSUBCODE,'���У���˰����' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_NATION_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// ��˰����
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_PLACE_CLASS
				+ "' as S_BUDGETSUBCODE,'��˰����' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_PLACE_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// ���ز���
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_CUSTOM_CLASS
				+ "' as S_BUDGETSUBCODE,'���ز���' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_CUSTOM_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// ��������
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
				+ "' as S_BUDGETSUBCODE,'��������' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		// ��������
		sql += "select '"
				+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
				+ "' as S_BUDGETSUBCODE,'��������' as S_BUDGETSUBNAME, a.S_BUDGETLEVELCODE as S_BUDGETLEVELCODE,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR "
				+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a,TS_BUDGETSUBJECT b Where a.S_TAXORGCODE = '"
				+ MsgConstant.MSG_TAXORG_OTHER_CLASS
				+ "' AND b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"'  and b.S_CLASSFLAG= '"+StateConstant.SBT_CLASS_TAXINCOME+"'  and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "+sqlwhere+" group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME,a.S_BUDGETLEVELCODE union all ";
		
		try {
			
			sql += sql.replace("TR_INCOMEDAYRPT", "HTR_INCOMEDAYRPT");
			sql = sql.substring(0, sql.length() - 10) + " with ur";
			
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			reportList = (List) exec.runQueryCloseCon(sql,TrIncomedayrptDto.class).getDtoCollection();
			if (null == reportList|| reportList.size() ==0) {
				throw new ITFEBizException("��ѯ�޼�¼");
			}
		
			// ȫϽ�������ջ���
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
//					throw new ITFEBizException("��ѯ�޼�¼");
				}
				else{
					reportList.addAll(reportList1);
				}
			}
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
		
		HashMap<String, String> subMap = new HashMap<String, String>();
		HashMap<String, BigDecimal> allMap = new HashMap<String, BigDecimal>();
		// ������� �±���Ҫ��ʾ��List
		for (TrIncomedayrptDto tmpdto : reportList) {
			if (!subMap.containsKey(tmpdto.getSbudgetsubcode())) {
				subMap.put(tmpdto.getSbudgetsubcode(), tmpdto
						.getSbudgetsubname());
			}
			BigDecimal nmoney;
			if (sbilltype.equals(StateConstant.REPORT_YEAR)) {// �걨
				nmoney = tmpdto.getNmoneyyear();
			} else if (sbilltype.equals(StateConstant.REPORT_QUAR)) { // ����
				nmoney = tmpdto.getNmoneyquarter();
			} else if (sbilltype.equals(StateConstant.REPORT_MONTH)) { // �±�
				nmoney = tmpdto.getNmoneymonth();
			} else if (sbilltype.equals(StateConstant.REPORT_TEN)) { // Ѯ��
				nmoney = tmpdto.getNmoneytenday();
			} else { // �ձ�
				nmoney = tmpdto.getNmoneyday();
			}
			allMap.put(tmpdto.getSbudgetsubcode()
					+ tmpdto.getSbudgetlevelcode(), nmoney);
		}
		List<ReportDto> billList = new ArrayList<ReportDto>();
		//���ջ��ش���
		for (int i = 0; i < 6; i++) {
			ReportDto tmp = new ReportDto();
			String subcode = null;
			if (i == 1) {
				subcode = MsgConstant.MSG_TAXORG_NATION_CLASS;
				tmp.setSbudgetsubname("����: ��˰����");
			}else if(i ==2){
				subcode = MsgConstant.MSG_TAXORG_PLACE_CLASS;
				tmp.setSbudgetsubname("          ��˰����");
			}else if(i ==3){
				subcode = MsgConstant.MSG_TAXORG_CUSTOM_CLASS;
				tmp.setSbudgetsubname("          ���ز���");
			}else if(i ==4){
				subcode = MsgConstant.MSG_TAXORG_FINANCE_CLASS;
				tmp.setSbudgetsubname("          ��������");
			}
			else if(i==5){
				subcode = MsgConstant.MSG_TAXORG_OTHER_CLASS;
				tmp.setSbudgetsubname("          ��������");
			}else if(i ==0){
				subcode = "101";
				tmp.setSbudgetsubname("˰������ϼ�");
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
			tmp.setNcentralmoney(central);// ����
			tmp.setNprovincemoney(provice);// ʡ��
			tmp.setNcitymoney(city);// �м�
			tmp.setNcountymoney(county);// ���ؼ�
			tmp.setNtownmoney(town);// �м�
			billList.add(tmp);
		}
		
		int i = 0;
		//����Ŀ����
		for (TsBudgetsubjectDto tmpdto : subjectList) {
			i++;
			ReportDto tmp = new ReportDto();
			String subcode = tmpdto.getSsubjectcode();
			String subname = tmpdto.getSsubjectname();
			tmp.setSbudgetsubcode(subcode);
			tmp.setSbudgetsubname(i+"��"+subname);
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
			tmp.setNcentralmoney(central);// ����
			tmp.setNprovincemoney(provice);// ʡ��
			tmp.setNcitymoney(city);// �м�
			tmp.setNcountymoney(county);// ���ؼ�
			tmp.setNtownmoney(town);// �м�
			billList.add(tmp);
		}
		
		return billList;
	}
	
	/**
	 * ȡ��Ԥ������ԭʼ����
	 * 
	 * @generated
	 * @param idto
	 * @param sleSumItem--�Ƿ���ϼ�
	 * @param moneyUnit
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List getIncomeBill(IDto idto, String sleSumItem, String sleSubjectType,
			String rptDate) throws ITFEBizException {
		
		try {

			//��������
			String sqlwhere =makeIncomeBillwhere(idto,sleSumItem,sleSubjectType,rptDate,StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
			StringBuffer sqlbuf = new StringBuffer(
					"with tmp(S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR) as (");
			// ͳ�����뱨��--���벿��
			//Ŀ
			String sql = "select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH,a.N_MONEYQUARTER,a.N_MONEYYEAR "
					+ " from (SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " union all "
					+ "  select a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,a.N_MONEYDAY,a.N_MONEYTENDAY,a.N_MONEYMONTH,a.N_MONEYQUARTER,a.N_MONEYYEAR "
					+ " from (SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"+rptDate+"') a, TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ sqlwhere
					+ " union all ";
			
			//��
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
			
			//��
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
			
			//��ӡsql
			log.debug(sqlbuf.toString());
			
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			List list = (List) exec.runQueryCloseCon(sqlbuf.toString(),
					TrIncomedayrptDto.class, true).getDtoCollection();
			
			return list;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}
	

	//ȡ���ļ�·��
	public String getReportRootPath() {
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		return ITFECommonConstant.FILE_ROOT_PATH+"Report"+dirsep;
	}
	
	//ȡ���ļ�·��
	public String getFileRootPath() {
		return ITFECommonConstant.FILE_ROOT_PATH;
	}

	public String makeExcelInOutBillSer(IDto idto, String sleSumItem, String sleSubjectType, String rptDate, String rptType,
			String copyFilename,String reporttitle,String unitname)
			throws ITFEBizException {
		String msg="";
		HashMap<String, String> datamap=new HashMap<String, String>();;
		List<TrIncomedayrptDto> list = null;
		
		//����ģ���ļ�·��
		ReportExcel.setFilepath(getFileRootPath());
		//�ļ�·��
		ReportExcel.setNewfilepath(getReportRootPath());
		//�½���������
		ReportExcel.setCopyFilename(copyFilename);
		//�������
		ReportExcel.setReporttitle(reporttitle);
		//����λ
		ReportExcel.setUnit(unitname);
		
		switch(Integer.parseInt(rptType.trim())){
			case 1 :
			{
				//����ģ������
				ReportExcel.setFilename(StateConstant.RPT_TYPE_NAME_1+"Model.xls");
				//����sheet����
				ReportExcel.setSheetname(StateConstant.RPT_TYPE_NAME_1);
			}
			case 2 :
			{
				//����ģ������
				ReportExcel.setFilename(StateConstant.RPT_TYPE_NAME_2+"Model.xls");
				//����sheet����
				ReportExcel.setSheetname(StateConstant.RPT_TYPE_NAME_2);
			}
			case 3 :
			{
				//����ģ������
				ReportExcel.setFilename(StateConstant.RPT_TYPE_NAME_3+"Model.xls");
				//����sheet����
				ReportExcel.setSheetname(StateConstant.RPT_TYPE_NAME_3+"Model.xls");
			}
			case 4 :
			{
				//����ģ������
				ReportExcel.setFilename(StateConstant.RPT_TYPE_NAME_4+"Model.xls");
				//����sheet����
				ReportExcel.setSheetname(StateConstant.RPT_TYPE_NAME_4);
			}
			case 5 :
			{
				//����ģ������
				ReportExcel.setFilename(StateConstant.RPT_TYPE_NAME_5+"Model.xls");
				//����sheet����
				ReportExcel.setSheetname(StateConstant.RPT_TYPE_NAME_5);
			}
		}
		
		//�����±�
		if(rptType.equals(StateConstant.RPT_TYPE_1)){
			//ȡ�÷������Ŀ��������
//			list=(List<TrIncomedayrptDto>)makeIncomeBill(idto,sleSumItem,sleSubjectType,rptDate);
			//ȡ�û�����������
			list=(List<TrIncomedayrptDto>)getIncomeBill(idto,sleSumItem,sleSubjectType,rptDate);
			if(list==null || list.size()<=0){
				return null;
			}
			for(TrIncomedayrptDto dto :list){
				datamap.put(dto.getSbudgetsubcode(), dto.getNmoneymonth().toString());
			}
		}
		//֧���±�
		if(rptType.equals(StateConstant.RPT_TYPE_2)){
			list=makePayoutBill(idto,sleSumItem,sleSubjectType,rptDate);
		}
		//��֧Ѯ��
		if(rptType.equals(StateConstant.RPT_TYPE_3)){
			//list=null;
		}
		//ȫ�ھ�˰��
		if(rptType.equals(StateConstant.RPT_TYPE_4)){
			//list=makeAllTaxBill(idto,sleSumItem,sleSubjectType,rptDate);
		}
		//��ĩ��֧���ȱ�
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
		 * �������
		 */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
//			throw new ITFEBizException("��ѯ����������������벻��Ϊ�գ�");
			//�޸�20110326 �ú����������
			sqlwhere += " and a.s_trecode in ( SELECT s_trecode FROM TS_TREASURY WHERE S_BOOKORGCODE= '"+this.getLoginInfo().getSorgcode()+"' ) ";
		}
		
		/**
		 * ���ջ��ش���
		 */
		if (null != dto.getStaxorgcode()
				&& !"".equals(dto.getStaxorgcode().trim())) {
			sqlwhere += " and a.s_TaxOrgCode ='"+dto.getStaxorgcode().trim()+"' ";
		}
		
		/**
		 * Ԥ���Ŀ����
		 */
		if (null != dto.getSbdgsbtcode()
				&& !"".equals(dto.getSbdgsbtcode().trim())) {
			sqlwhere += " and a.s_BdgSbtCode ='"+dto.getSbdgsbtcode().trim()+"' ";
		}
		
		/**
		 * Ԥ���Ŀ����
		 */
		if (null != dto.getSbdgsbtname()
				&& !"".equals(dto.getSbdgsbtname().trim())) {
			sqlwhere += " and a.s_BdgSbtName ='"+dto.getSbdgsbtname().trim()+"' ";
		}
		
		/**
		 * ��˰�˴���
		 */
		if (null != dto.getStaxpaycode()
				&& !"".equals(dto.getStaxpaycode().trim())) {
			sqlwhere += " and a.s_TaxPayCode ='"+dto.getStaxpaycode().trim()+"' ";
		}
		
		/**
		 * ��˰������
		 */
		if (null != dto.getStaxpayname()
				&& !"".equals(dto.getStaxpayname().trim())) {
			sqlwhere += " and a.s_TaxPayName ='"+dto.getStaxpayname().trim()+"' ";
		}
		
		/**
		 * ��ʼ����
		 */
		if (null != startdate
				&& !"".equals(startdate.trim())) {
			sqlwhere += " and a.S_ACCT >='"+startdate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ��������ʼ���ڲ���Ϊ�գ�");
		}
		
		/**
		 * ��������
		 */
		if (null != enddate
				&& !"".equals(enddate.trim())) {
			sqlwhere += " and a.S_ACCT <='"+enddate.trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
		}
		
		/**
		 * Ԥ���Ŀ����--���ᾭ����ؿ�Ŀ
		 */
//		sqlwhere += " and a.S_BDGSBTCODE like '190%' ";
		//�޸�20110326 Ϊ��ѯԤ���������
		sqlwhere += " and b.S_BUDGETTYPE = '"+MsgConstant.BDG_KIND_OUT+"' ";
		
		/**
		 * ���ڸ��������嵥��һ��Ԥ���Ŀ���룬��˲�ѯ��ʱ��Ӧ���Ͽ�Ŀ����ĺ�����������
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
//				+"   sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'����С��' as S_Remark1  "
//				+"   from TR_FIN_INCOMEONLINE_DAYRPT a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
//				+"   group by a.S_TAXORGCODE,a.S_ACCT,a.S_BDGSBTCODE,b.S_SUBJECTNAME "
//				+"  union all "
				+"  select a.S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, '' as S_TRECODE,   "
				+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,  "
				+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
				+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'���ۼ�' as S_Remark1  "
				+"  from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
				+"  group by a.S_ACCT,a.S_BDGSBTCODE,b.S_SUBJECTNAME "
				+"  union all "
				+"  select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,a.S_BDGSBTCODE,b.S_SUBJECTNAME as S_BDGSBTNAME, '' as S_TRECODE, "
				+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,  "
				+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
				+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'��ĿС��' as S_Remark1  "
				+"  from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere
				+"  group by a.S_BDGSBTCODE,b.S_SUBJECTNAME "
				+"  union all "   
				+"  select '' as S_ACCT,'' as S_TAXPAYCODE,'' as S_TAXPAYNAME,'' as S_TAXVOUNO,'' as S_TAXORGCODE,'' as S_BDGSBTCODE,'' as S_BDGSBTNAME, '' as S_TRECODE,  "
				+"  sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYTENDAY) as N_MONEYTENDAY,sum(a.N_MONEYMONTH) as N_MONEYMONTH,sum(a.N_MONEYQUARTER) as N_MONEYQUARTER,sum(a.N_MONEYYEAR) as N_MONEYYEAR,sum(a.N_CENTERMONEYDAY) as N_CENTERMONEYDAY,sum(a.N_CENTERMONEYTENDAY) as N_CENTERMONEYTENDAY,"  
				+"  sum(a.N_CENTERMONEYMONTH) as N_CENTERMONEYMONTH,sum(a.N_CENTERMONEYQUARTER) as N_CENTERMONEYQUARTER,sum(a.N_CENTERMONEYYEAR) as N_CENTERMONEYYEAR,sum(a.N_PLACEMONEYDAY) as N_PLACEMONEYDAY,  "
				+"  sum(a.N_PLACEMONEYTENDAY) as N_PLACEMONEYTENDAY,sum(a.N_PLACEMONEYMONTH) as N_PLACEMONEYMONTH,sum(a.N_PLACEMONEYQUARTER) as N_PLACEMONEYQUARTER,sum(a.N_PLACEMONEYYEAR) as N_PLACEMONEYYEAR,'�ϼ�' as S_Remark1  "
				+"  from (select * from TR_FIN_INCOMEONLINE_DAYRPT where S_ACCT >= '"+startdate+"' and S_ACCT <='"+enddate+"') a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_INCOME+"' and a.S_BDGSBTCODE = b.S_SUBJECTCODE "+sqlwhere;
		
		sql += " union all " + sql.replace("TR_FIN_INCOMEONLINE_DAYRPT", "HTR_FIN_INCOMEONLINE_DAYRPT");
		
		sqlbuf.append(sql);
				
		sqlbuf.append(") select S_TAXVOUNO,S_TAXPAYCODE,S_TAXPAYNAME,S_TAXORGCODE,S_ACCT,S_BDGSBTCODE,S_BDGSBTNAME,S_TRECODE, "
					+" N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH, N_MONEYQUARTER, N_MONEYYEAR, N_CENTERMONEYDAY, N_CENTERMONEYTENDAY, N_CENTERMONEYMONTH, N_CENTERMONEYQUARTER, " 
					+"N_CENTERMONEYYEAR, N_PLACEMONEYDAY, N_PLACEMONEYTENDAY, N_PLACEMONEYMONTH, N_PLACEMONEYQUARTER, N_PLACEMONEYYEAR,S_Remark1 from tmp order by s_acct,S_BDGSBTCODE ,S_TAXORGCODE ,s_taxpaycode ");
		
		try {
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			//��ӡsql
			log.debug(sqlbuf.toString());
			
			List list= (List) exec.runQueryCloseCon(sqlbuf.toString(), TrFinIncomeonlineDayrptDto.class,true).getDtoCollection();
//			Collections.reverse(list);
			return list;
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}

	public List FinOutBookSerial(IDto idto, String startdate, String enddate,
			String remake) throws ITFEBizException {
		
		String sqlwhere = makeFinOutBookSerialwhere( idto,  startdate,  enddate,remake);
		StringBuffer sqlbuf=null;
		
		sqlbuf = new StringBuffer(
		"with tmp(S_TRECODE,S_DESCRIPTION,D_ACCTDATE,S_VOUNO,F_SUMAMT,S_BILLORG,S_INPUTERID ) as (");
		
		//��Ȩ֧�����
		if(remake.equals("1")){
	
			String sql = 
				"select a.S_TRECODE as S_TRECODE ,b.S_SUBJECTNAME as s_description,a.d_acctdate as d_acctdate,a.s_vouno as s_vouno,a.F_AMT as F_SUMAMT,a.S_FUNCSBTCODE as s_billorg,'' as s_inputerid from "
				+"(select * from TV_GRANTPAYPLAN_MAIN m,TV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "
				+"	union all "
				+" select * from HTV_GRANTPAYPLAN_MAIN m,HTV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "		 
				+") as a, "
				+" TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE "+sqlwhere
				+" union all "
				+"select '' as S_TRECODE ,b.S_SUBJECTNAME as s_description,'' as d_acctdate,'' as s_vouno,sum(a.F_AMT) as F_SUMAMT,a.S_FUNCSBTCODE as s_billorg,'С��' as s_inputerid from "
				+"(select * from TV_GRANTPAYPLAN_MAIN m,TV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "
				+"	union all "
				+" select * from HTV_GRANTPAYPLAN_MAIN m,HTV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "		 
				+") as a, "
				+" TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE "+sqlwhere
				+" group by a.S_FUNCSBTCODE,b.S_SUBJECTNAME "
				+" union all "
				+"select '' as S_TRECODE ,'' as s_description,'' as d_acctdate,'' as s_vouno,sum(a.F_AMT) as F_SUMAMT,'' as s_billorg,'�ϼ�' as s_inputerid from "
				+"(select * from TV_GRANTPAYPLAN_MAIN m,TV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "
				+"	union all "
				+" select * from HTV_GRANTPAYPLAN_MAIN m,HTV_GRANTPAYPLAN_sub s where m.S_SEQ=s.S_SEQ and m.S_RESULT='"+DealCodeConstants.DEALCODE_TIPS_SUCCESS+"' "		 
				+") as a, "
				+" TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE "+sqlwhere;
			sqlbuf.append(sql);
			
		}
		else{//��Ȩ֧��
			
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
				+"select '' as S_TRECODE ,b.S_SUBJECTNAME as s_description,'' as d_acctdate,'' as s_vouno,sum(a.F_AMT) as F_SUMAMT,a.S_FUNCSBTCODE as s_billorg,'С��' as s_inputerid from  "
				+"( "+nuionall+" ) as a,  "
				+" TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE  "+sqlwhere
				+"group by a.S_FUNCSBTCODE,b.S_SUBJECTNAME  "
				+" union all  "
				+"select '' as S_TRECODE ,'' as s_description,'' as d_acctdate,'' as s_vouno,sum(a.F_AMT) as F_SUMAMT,'' as s_billorg,'�ϼ�' as s_inputerid from  "
				+" ( "+nuionall+" ) as a,  "
				+"TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"+StateConstant.S_SUBJECTCLASS_PAYOUT2+"' and a.S_FUNCSBTCODE = b.S_SUBJECTCODE  "+sqlwhere;
			sqlbuf.append(sql);
		}
		sqlbuf.append(") select S_TRECODE,S_DESCRIPTION,D_ACCTDATE,S_VOUNO,F_SUMAMT,S_BILLORG,S_INPUTERID "
				+" from tmp order by S_BILLORG desc,S_DESCRIPTION desc ");
		
		try {
			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			
			//��ӡsql
			log.debug(sqlbuf.toString());
			
			return (List) exec.runQueryCloseCon(sqlbuf.toString(), TvGrantpayplanMainDto.class,true).getDtoCollection();
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}
	
	private String makeFinOutBookSerialwhere(IDto idto, String startdate, String enddate,
			String remake) throws ITFEBizException {
		
		TvGrantpayplanMainDto dto = (TvGrantpayplanMainDto) idto;
		String sqlwhere = "";

		/**
		 * �������
		 */
		if (null != dto.getStrecode()
				&& !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='"+dto.getStrecode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ������������벻��Ϊ�գ�");
		}
		
		/**
		 * Ԥ�㵥λ����
		 */
		if (null != dto.getSbdgorgcode()
				&& !"".equals(dto.getSbdgorgcode().trim())) {
			sqlwhere += " and a.S_BDGORGCODE ='"+dto.getSbdgorgcode().trim()+"' ";
		}
		else{
			throw new ITFEBizException("��ѯ������Ԥ�㵥λ���벻��Ϊ�գ�");
		}
		
		if(remake.equals("1")){//��Ȩ֧�����
			/**
			 * ��ʼ����
			 */
			if (null != startdate
					&& !"".equals(startdate.trim())) {
				sqlwhere += " and a.D_ACCTDATE >='"+startdate.trim()+"' ";
			}
			else{
				throw new ITFEBizException("��ѯ��������ʼ���ڲ���Ϊ�գ�");
			}
			
			/**
			 * ��������
			 */
			if (null != enddate
					&& !"".equals(enddate.trim())) {
				sqlwhere += " and a.D_ACCTDATE <='"+enddate.trim()+"' ";
			}
			else{
				throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
			}
		}
		else{
			/**
			 * ��ʼ����
			 */
			if (null != startdate
					&& !"".equals(startdate.trim())) {
				sqlwhere += " and a.D_ACCT >='"+startdate.trim()+"' ";
			}
			else{
				throw new ITFEBizException("��ѯ��������ʼ���ڲ���Ϊ�գ�");
			}
			
			/**
			 * ��������
			 */
			if (null != enddate
					&& !"".equals(enddate.trim())) {
				sqlwhere += " and a.D_ACCT <='"+enddate.trim()+"' ";
			}
			else{
				throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
			}
		}
		
		/**
		 * ���ڸ��������嵥��һ��Ԥ���Ŀ���룬��˲�ѯ��ʱ��Ӧ���Ͽ�Ŀ����ĺ�����������
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
		
		//ȡ������������뼯
		try {
			String sql1 = "select S_TRECODE,S_GOVERNTREcode from TS_TREASURY where s_trelevel='4' and S_GOVERNTRELEVEL='3' ";
			exec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			treasuryList = (List) exec.runQueryCloseCon(sql1,TsTreasuryDto.class).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
		String strtreasury="";
		for(TsTreasuryDto dto:treasuryList){
			strtreasury+="'"+dto.getStrecode()+"'"+",";
		}
		strtreasury="("+strtreasury.substring(0, strtreasury.length()-1)+")";
		
		//ȡ���м��������
		String strtredistrict=treasuryList.get(0).getSgoverntrecode();
		
		//ȡ��Ҫ��ѯ�������ռ�
		String montharr[]=TimeFacade.getMontharr(startdate,enddate);
		String strmonth="";
		for(String str:montharr){
			strmonth+="'"+str+"'"+",";
		}
		strmonth="("+strmonth.substring(0, strmonth.length()-1)+")";
		
		//ȡ�����ջ��ش��༯
		String strtaxorgclass="('"+MsgConstant.MSG_TAXORG_NATION_CLASS+"', '"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"','"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"','"+
		MsgConstant.MSG_TAXORG_FINANCE_CLASS+"','"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"','"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"')";
		
		//where��ͬ����
		String sqlwhere="   where (S_BUDGETSUBCODE  like '103%' or S_BUDGETSUBCODE  like '101%' ) and S_BUDGETSUBCODE  not like '10301%' and s_rptdate in "+strmonth+" and S_TRIMFLAG='0' ";
		
		String sql=
			//���صط���ȫϽ
			" select S_TRECODE,substr(S_RPTDATE,1,6) as S_RPTDATE,substr(S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,  sum(N_MONEYMONTH) as N_MONEYMONTH "+
			"   from (select * from TR_INCOMEDAYRPT where S_RPTDATE in "+strmonth+") a"+
			sqlwhere+
			"     and s_trecode in "+strtreasury+" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"'  and S_BUDGETLEVELCODE >='"+MsgConstant.BUDGET_LEVEL_PREFECTURE+"' "+
			"  group by S_TRECODE,S_RPTDATE,substr(S_BUDGETSUBCODE,1,3) "+
			" union all "+
			
			//�б���ȫϽ
			" select S_TRECODE,substr(S_RPTDATE,1,6) as S_RPTDATE,substr(S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,  sum(N_MONEYMONTH) as N_MONEYMONTH "+
			"   from (select * from TR_INCOMEDAYRPT where S_RPTDATE in "+strmonth+") a"+
			sqlwhere+
			"     and s_trecode in ('"+strtredistrict+"') and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"'  and S_BUDGETLEVELCODE ='"+MsgConstant.BUDGET_LEVEL_DISTRICT+"' "+
			"  group by S_TRECODE,S_RPTDATE,substr(S_BUDGETSUBCODE,1,3) "+
			" union all "+
			
			//ȫϽ����
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
			throw new ITFEBizException("��ѯ����", e);
		}
		
		if(reportList==null || reportList.size()==0){
			throw new ITFEBizException("��ѯ�����ݣ�����");
		}
		
		HashMap<String , BigDecimal> map=new HashMap<String , BigDecimal>();
		for(TrIncomedayrptDto dto:reportList){
			map.put(dto.getStrecode().trim()+dto.getSrptdate().trim()+dto.getSbudgetsubcode().trim(), dto.getNmoneymonth());
		}
		
		//����+���ջ��ش���ȥ��λ
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
//		strecodelist.add("1000000001"); //�����ϼ�
//		strecodelist.add("1000000002"); //����С��
//		strecodelist.add("1000000003"); //�����ϼ�
		
		//�·�
		List<String> smonthlist=new ArrayList<String>();
		for(String str:montharr){
			smonthlist.add(str.substring(0, 6));
		}
		
		//ѭ������
		for(String trecode:strecodelist){
			for(String month:smonthlist){
				if(trecode.equals("1000000001")){//�����ϼ�
					;
				}
				else if(trecode.equals("1000000002")){//����С��
					;
				}
				else if(trecode.equals("1000000003")){//�����ϼ�
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
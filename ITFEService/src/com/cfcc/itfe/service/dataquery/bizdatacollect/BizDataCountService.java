package com.cfcc.itfe.service.dataquery.bizdatacollect;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.BizDataCountDto;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.BankReportDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author hua
 * @time 12-09-12 16:22:05 codecomment:
 */

public class BizDataCountService extends AbstractBizDataCountService {
	private static Log log = LogFactory.getLog(BizDataCountService.class);

	/**
	 * ���ɱ���
	 * 
	 * @generated
	 * @param start
	 * @param end
	 * @param biztype
	 * @throws ITFEBizException
	 */
	public List makeReport(String biztype, TipsParamDto param)
			throws ITFEBizException {
		try {
			String forstart = param.getStartdate().toString().replace("-", "");
			String forend = param.getEnddate().toString().replace("-", "");
			StringBuffer insql = new StringBuffer();
			SQLExecutor sqlExec = null;
			SQLResults rs = null;
			if (StringUtils.isNotBlank(param.getStrecode()))
				return makeReport(biztype, param, true);
			
			if (BizTypeConstant.BIZ_TYPE_INCOME.equals(biztype)) { // ��������ǻ����������ϸ
				List li = generalDataForIncome(biztype, param);
				if (null != li) {
					return li;
				} else {
					return null;
				}

			} else if ("0".equals(biztype)) { // ȫ��ҵ������
				insql
						.append("SELECT (SELECT S_ORGname FROM TS_ORGAN WHERE S_ORGCODE = A.s_orgcode) as S_ORGname,(SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=A.S_OPERATIONTYPECODE)  AS bizname,"
								+ " SUM(I_COUNT) AS totalcount ,"
								+ " SUM(N_MONEY) AS totalfamt "
								+ " FROM TV_FILEPACKAGEREF A WHERE S_OPERATIONTYPECODE IN("
								+ StateConstant.Count_Statics_Type
								+ ") "
								+ " AND S_COMMITDATE>=?"
								+ " AND S_COMMITDATE<=? ");
				sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				sqlExec.addParam("0407");
				sqlExec.addParam(forstart);
				sqlExec.addParam(forend);
				// ����������û����һ���Ϊ����ͳ��ȫ����
				if ((StateConstant.ORG_CENTER_CODE.equals(getLoginInfo()
						.getSorgcode()) || StateConstant.STAT_CENTER_CODE
						.equals(getLoginInfo().getSorgcode()))
						&& (StringUtils.isBlank(param.getSorgcode()) || StateConstant.ORG_CENTER_CODE
								.equals(param.getSorgcode()))) {
					insql = new StringBuffer("");
					insql
							.append("SELECT 'ȫϽ' as S_ORGname,(SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=A.S_OPERATIONTYPECODE)  AS bizname,"
									+ " SUM(I_COUNT) AS totalcount ,"
									+ " SUM(N_MONEY) AS totalfamt "
									+ " FROM TV_FILEPACKAGEREF A WHERE S_OPERATIONTYPECODE IN(SELECT S_VALUE FROM TD_ENUMVALUE WHERE S_TYPECODE IN('0407','0419')) "
									+ " AND S_COMMITDATE>=?"
									+ " AND S_COMMITDATE<=? ");
					insql.append(" GROUP BY A.S_OPERATIONTYPECODE  ");
				} else {
					if (!StateConstant.ORG_CENTER_CODE.equals(getLoginInfo()
							.getSorgcode())) {
						insql.append(" AND S_ORGCODE=?  ");
						sqlExec.addParam(param.getSorgcode());
					}
					insql
							.append(" GROUP BY A.S_ORGCODE,A.S_OPERATIONTYPECODE ORDER BY A.S_ORGCODE ");
				}

				// sqlExec.addParam(getLoginInfo().getSorgcode());
				rs = sqlExec.runQueryCloseCon(insql.toString(),
						BizDataCountDto.class);
				if (null == rs) {
					return null;
				}
				List list = (List) rs.getDtoCollection();
				list.addAll(generalDataForIncome(
						BizTypeConstant.BIZ_TYPE_INCOME, param)); // �����뵥������
				return list;
			} else { // ������ҵ������
				insql.append("SELECT * FROM ");
				insql
						.append(" (SELECT (SELECT S_ORGname FROM TS_ORGAN WHERE S_ORGCODE = A.s_orgcode) AS s_orgname,(SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=?)  AS bizname,");
				insql
						.append(" SUM(I_COUNT) AS totalcount , SUM(N_MONEY) AS totalfamt  FROM TV_FILEPACKAGEREF A ");
				insql
						.append(" WHERE S_OPERATIONTYPECODE =?  AND S_COMMITDATE>=?  AND S_COMMITDATE<=? ");
				sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				sqlExec.addParam("0407");
				sqlExec.addParam(biztype);
				sqlExec.addParam(biztype);
				sqlExec.addParam(forstart);
				sqlExec.addParam(forend);
				if (StateConstant.ORG_CENTER_CODE.equals(getLoginInfo()
						.getSorgcode())
						&& StringUtils.isBlank(param.getSorgcode())) {

				} else {
					insql.append(" AND S_ORGCODE=?  ");
					sqlExec.addParam(param.getSorgcode());
				}
				insql
						.append("GROUP BY A.S_OPERATIONTYPECODE,A.S_ORGCODE ) WHERE totalcount>0");
				rs = sqlExec.runQueryCloseCon(insql.toString(),
						BizDataCountDto.class);
				if (null == rs) {
					return null;
				}
				return (List) rs.getDtoCollection();
			}

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("���ɱ����쳣!\n" + e.getMessage(), e);
		}
	}

	public List generalDataForIncome(String biztype, TipsParamDto param)
			throws JAFDatabaseException {
		String forstart = param.getStartdate().toString().replace("-", "");
		String forend = param.getEnddate().toString().replace("-", "");
		SQLExecutor sqlExec = null;
		SQLResults rs = null;
		StringBuffer insql = new StringBuffer();
		insql
				.append("SELECT * FROM  (SELECT (SELECT S_ORGname FROM TS_ORGAN WHERE S_ORGCODE = A.s_orgcode) as S_ORGname,(SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=?) AS bizname, count(*) AS totalcount ,"
						+ " sum(N_MONEY) AS totalfamt "
						+ " FROM TV_INFILE_DETAIL A WHERE  S_COMMITDATE>=? AND S_COMMITDATE<=? ");
		sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
				.getSQLExecutor();

		if ((StateConstant.ORG_CENTER_CODE.equals(getLoginInfo().getSorgcode()) || StateConstant.STAT_CENTER_CODE
				.equals(getLoginInfo().getSorgcode()))
				&& (StringUtils.isBlank(param.getSorgcode()) || StateConstant.ORG_CENTER_CODE
						.equals(param.getSorgcode()))) {
			insql = new StringBuffer("");
			insql
					.append(" SELECT 'ȫϽ' as S_ORGname,'����˰Ʊ' AS bizname, count(*) AS totalcount ,"
							+ " sum(N_MONEY) AS totalfamt "
							+ " FROM TV_INFILE_DETAIL A WHERE  S_COMMITDATE>=? AND S_COMMITDATE<=? ");
			sqlExec.addParam(forstart);
			sqlExec.addParam(forend);
		} else {
			insql.append(" AND S_ORGCODE=?  ");
			sqlExec.addParam("0407");
			sqlExec.addParam(biztype);
			sqlExec.addParam(forstart);
			sqlExec.addParam(forend);
			sqlExec.addParam(param.getSorgcode());
			insql.append(" GROUP BY A.S_ORGCODE) WHERE totalcount>0");
		}

		// sqlExec.addParam(getLoginInfo().getSorgcode());
		rs = sqlExec.runQueryCloseCon(insql.toString(), BizDataCountDto.class);
		return (List) rs.getDtoCollection();
	}

	/**
	 * ������ֽ��ҵ����ͳ�Ʊ���
	 * 
	 * @author hjr
	 * @param biztype
	 * @param param
	 * @return
	 * @throws ITFEBizException
	 */
	public List generateVoucherDataCountReport(String biztype,
			TipsParamDto param) throws ITFEBizException {
		String succSql = "SELECT s_vtcode AS bizname,count(0) AS succcount , sum(N_MONEY) AS succfamt "
				+ "FROM TV_VOUCHERINFO WHERE S_ORGCODE = '"
				+ param.getSorgcode()
				+ "'  AND "
				+ "S_TRECODE = '"
				+ param.getStrecode()
				+ "' AND S_CREATDATE BETWEEN '"
				+ TimeFacade.formatDate(param.getStartdate(), "yyyyMMdd")
				+ "' AND '"
				+ TimeFacade.formatDate(param.getEnddate(), "yyyyMMdd")
				+ "'"
				+ "AND S_VTCODE IN ('"
				+ (biztype.equals("0") ? "5207','5106','5108','2301','2302','5209"
						: biztype)
				+ "')"
				+ "AND S_STATUS IN ('71','73','80') GROUP BY S_VTCODE";
		String failSql = "SELECT s_vtcode AS bizname,count(0) AS failcount ,sum(N_MONEY) AS failfamt "
				+ "FROM TV_VOUCHERINFO WHERE S_ORGCODE = '"
				+ param.getSorgcode()
				+ "'  AND "
				+ "S_TRECODE = '"
				+ param.getStrecode()
				+ "' AND S_CREATDATE BETWEEN '"
				+ TimeFacade.formatDate(param.getStartdate(), "yyyyMMdd")
				+ "' AND '"
				+ TimeFacade.formatDate(param.getEnddate(), "yyyyMMdd")
				+ "'"
				+ "AND S_VTCODE IN ('"
				+ (biztype.equals("0") ? "5207','5106','5108','2301','2302','5209"
						: biztype)
				+ "')"
				+ "AND S_STATUS IN ('30', '45', '62', '72', '90') GROUP BY S_VTCODE";
		try {
			SQLExecutor sqlExec = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			SQLResults succRs = sqlExec.runQueryCloseCon(succSql,
					BizDataCountDto.class);
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			SQLResults failRs = sqlExec.runQueryCloseCon(failSql,
					BizDataCountDto.class);
			if (succRs != null && failRs != null)
				return combiDto(dealBizname((List) succRs.getDtoCollection()),
						dealBizname((List) failRs.getDtoCollection()));
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
		return null;
	}

	/**
	 * ��ϴ���ɹ�dto�ʹ���ʧ��dto����
	 * 
	 * @param succList
	 * @param failList
	 * @return
	 */
	public List combiDto(List succList, List failList) {
		int temp;
		for (int i = 0; i < succList.size(); i++) {
			temp = 0;
			BizDataCountDto succDto = (BizDataCountDto) succList.get(i);
			for (int j = 0; j < failList.size(); j++) {
				BizDataCountDto failDto = (BizDataCountDto) failList.get(j);
				if (succDto.getBizname().equals(failDto.getBizname())) {
					succDto.setFailcount(failDto.getFailcount());
					succDto.setFailfamt(failDto.getFailfamt());
					temp = 1;
					continue;
				}
			}
			if (temp == 0) {
				succDto.setFailcount("0");
				succDto.setFailfamt(new BigDecimal("0.00"));
			}
		}
		for (int i = 0; i < failList.size(); i++) {
			temp = 0;
			BizDataCountDto failDto = (BizDataCountDto) failList.get(i);
			for (int j = 0; j < succList.size(); j++) {
				BizDataCountDto succDto = (BizDataCountDto) succList.get(j);
				if (succDto.getBizname().equals(failDto.getBizname())) {
					temp = 1;
					continue;
				}
			}
			if (temp == 0) {
				failDto.setSucccount("0");
				failDto.setSuccfamt(new BigDecimal("0.00"));
				succList.add(failDto);
			}
		}
		return succList;
	}

	/**
	 * ��ƾ֤ҵ�����ʹ���ת����ƾ֤ҵ����������
	 * 
	 * @param list
	 * @return
	 */
	public List dealBizname(List list) {
		for (int i = 0; i < list.size(); i++) {
			BizDataCountDto dto = (BizDataCountDto) list.get(i);
			if (dto.getBizname().equals(MsgConstant.VOUCHER_NO_2301))
				dto.setBizname("���л�������");
			else if (dto.getBizname().equals(MsgConstant.VOUCHER_NO_2302))
				dto.setBizname("�����˿�����");
			else if (dto.getBizname().equals(MsgConstant.VOUCHER_NO_5207))
				dto.setBizname("ʵ���ʽ�");
			else if(dto.getBizname().equals(MsgConstant.MSG_NO_3208))
				dto.setBizname("ʵ���ʽ��˻�");
			else if(dto.getBizname().equals(MsgConstant.MSG_NO_51044))
				dto.setBizname("����֧��");
			else if (dto.getBizname().equals(MsgConstant.VOUCHER_NO_5209))
				dto.setBizname("�����˸�");
			else if (dto.getBizname().equals(MsgConstant.VOUCHER_NO_5106))
				dto.setBizname("��Ȩ֧�����");
			else if (dto.getBizname().equals(MsgConstant.VOUCHER_NO_5108))
				dto.setBizname("ֱ��֧�����");
		}
		return list;
	}

	/**
	 * ת��ҵ�����͡������������ڡ��������
	 * 
	 * @param biztype
	 * @return
	 */
	private HashMap transformTableMap(String biztype, TipsParamDto param) {
		HashMap map = new HashMap();
		if(biztype.equals(MsgConstant.VOUCHER_NO_5267))
		{
			map.put("bizname", "ʵ���ʽ�ר��");
			map.put("sdealno", "S_BIZNO");
			map.put("n_money", "N_MONEY");
			map.put("tableName", "TV_PAYOUTMSGMAIN");
			map.put("subtableName", "TV_PAYOUTMSGSUB");
			map.put("creatdate", "S_COMMITDATE");
			map.put("strecode", "S_TRECODE");
			map.put("S_STATUS", "S_STATUS");
			map.put("startdate", param.getStartdate().toString().replace("-",""));
			map.put("enddate", param.getEnddate().toString().replace("-", ""));
//			map.put("sbankcode", "S_PAYEEBANKNO");
			map.put("paymode", "S_PAYTYPECODE");
			map.put("sbudgettype", "S_BUDGETTYPE");
		}else if (biztype.equals(MsgConstant.MSG_NO_5101)) {
			map.put("bizname", "ʵ���ʽ�");
			map.put("sdealno", "S_BIZNO");
			map.put("n_money", "N_MONEY");
			map.put("tableName", "TV_PAYOUTMSGMAIN");
			map.put("subtableName", "TV_PAYOUTMSGSUB");
			map.put("creatdate", "S_COMMITDATE");
			map.put("strecode", "S_TRECODE");
			map.put("S_STATUS", "S_STATUS");
			map.put("startdate", param.getStartdate().toString().replace("-",""));
			map.put("enddate", param.getEnddate().toString().replace("-", ""));
//			map.put("sbankcode", "S_PAYEEBANKNO");
			map.put("paymode", "S_PAYTYPECODE");
			map.put("sbudgettype", "S_BUDGETTYPE");
		} else if (biztype.equals(MsgConstant.MSG_NO_3208)) {//ʵ���ʽ��˻�
			map.put("bizname", "ʵ���ʽ��˻�");
			map.put("sdealno", "S_BIZNO");
			map.put("n_money", "N_MONEY");
			map.put("tableName", "TV_PAYOUTMSGMAIN");
			map.put("subtableName", "TV_PAYOUTMSGSUB");
			map.put("creatdate", "S_COMMITDATE");
			map.put("strecode", "S_TRECODE");
			map.put("S_STATUS", "S_STATUS");
			map.put("startdate", param.getStartdate().toString().replace("-",""));
			map.put("enddate", param.getEnddate().toString().replace("-", ""));
//			map.put("sbankcode", "S_PAYEEBANKNO");
			map.put("paymode", "S_PAYTYPECODE");
			map.put("sbudgettype", "S_BUDGETTYPE");
		}else if (biztype.equals(MsgConstant.MSG_NO_51044)) {//����֧��
			map.put("bizname", "����֧��");
			map.put("sdealno", "S_BIZNO");
			map.put("n_money", "N_MONEY");
			map.put("tableName", "TV_PAYOUTMSGMAIN");
			map.put("subtableName", "TV_PAYOUTMSGSUB");
			map.put("creatdate", "S_COMMITDATE");
			map.put("strecode", "S_TRECODE");
			map.put("S_STATUS", "S_STATUS");
			map.put("startdate", param.getStartdate().toString().replace("-",""));
			map.put("enddate", param.getEnddate().toString().replace("-", ""));
//			map.put("sbankcode", "S_PAYEEBANKNO");
			map.put("paymode", "S_PAYTYPECODE");
			map.put("sbudgettype", "S_BUDGETTYPE");
		}else if (biztype.equals(MsgConstant.MSG_NO_1104)) {
			map.put("bizname", "�����˸�");
			map.put("n_money", "F_AMT");
			map.put("tableName", "Tv_Dwbk");
			map.put("creatdate", "D_accept");
			map.put("strecode", "S_PAYERTRECODE");
			map.put("startdate", param.getStartdate());
			map.put("enddate", param.getEnddate());
			map.put("sbankcode", "S_PAYEEOPNBNKNO");
			//map.put("paymode", "S_PAYTYPECODE");
			map.put("sbudgettype", "C_BDGKIND");
		} else if (biztype.equals(MsgConstant.MSG_NO_5103)) {// ��Ȩ֧�����
			map.put("bizname", "��Ȩ֧�����");
			map.put("sdealno", "I_VOUSRLNO");
			map.put("n_money", "N_MONEY");
			map.put("tableName", "TV_GRANTPAYMSGMAIN");
			map.put("subtableName", "Tv_Grantpaymsgsub");
			map.put("creatdate", "S_COMMITDATE");
			map.put("strecode", "S_TRECODE");
			map.put("S_STATUS", "S_STATUS");
			map.put("startdate", param.getStartdate().toString().replace("-",""));
			map.put("enddate", param.getEnddate().toString().replace("-", ""));
			map.put("sbankcode", "S_PAYBANKNO");
			//map.put("paymode", "S_PAYTYPECODE");
			map.put("sbudgettype", "S_BUDGETTYPE");
		} else if (biztype.equals(MsgConstant.MSG_NO_5102)) {
			map.put("bizname", "ֱ��֧�����");
			map.put("sdealno", "I_VOUSRLNO");
			map.put("n_money", "N_MONEY");
			map.put("tableName", "TV_DIRECTPAYMSGMAIN");
			map.put("subtableName", "Tv_Directpaymsgsub");
			map.put("creatdate", "S_COMMITDATE");
			map.put("strecode", "S_TRECODE");
			map.put("S_STATUS", "S_STATUS");
			map.put("startdate", param.getStartdate().toString().replace("-",""));
			map.put("enddate", param.getEnddate().toString().replace("-", ""));
			map.put("sbankcode", "S_PAYBANKNO");
			//map.put("paymode", "S_PAYTYPECODE");
			map.put("sbudgettype", "S_BUDGETTYPE");
		} else if (biztype.equals(MsgConstant.MSG_NO_2201)) {
			map.put("bizname", "���л�������");
			map.put("sdealno", "I_VOUSRLNO");
			map.put("n_money", "F_AMT");
			map.put("tableName", "TV_PAYRECK_BANK");
			map.put("subtableName", "Tv_Payreck_Bank_List");
			map.put("creatdate", "D_ENTRUSTDATE");
			map.put("strecode", "S_TRECODE");
			map.put("S_STATUS", "S_RESULT");
			map.put("startdate", param.getStartdate());
			map.put("enddate", param.getEnddate());
			map.put("sbankcode", "S_AGENTBNKCODE");
			map.put("paymode", "S_PAYTYPECODE");
			map.put("sbudgettype", "S_BUDGETTYPE");
		} else if (biztype.equals(MsgConstant.MSG_NO_2202)) {
			map.put("bizname", "�����˿�����");
			map.put("sdealno", "I_VOUSRLNO");
			map.put("n_money", "F_AMT");
			map.put("tableName", "TV_PAYRECK_BANK_BACK");
			map.put("subtableName", "Tv_Payreck_Bank_Back_List");
			map.put("creatdate", "D_ENTRUSTDATE");
			map.put("strecode", "S_TRECODE");
			map.put("S_STATUS", "S_STATUS");
			map.put("startdate", param.getStartdate());
			map.put("enddate", param.getEnddate());
			map.put("sbankcode", "S_AGENTBNKCODE");
			map.put("paymode", "S_PAYTYPECODE");
			map.put("sbudgettype", "S_BUDGETTYPE");
		} else if (biztype.equals(MsgConstant.MSG_NO_7211)) {
			map.put("bizname", "����˰Ʊ");
			map.put("n_money", "N_MONEY");
			map.put("tableName", "TV_INFILE");
			map.put("creatdate", "S_COMMITDATE");
			map.put("strecode", "S_RECVTRECODE");
			map.put("startdate", param.getStartdate().toString().replace("-",""));
			map.put("enddate", param.getEnddate().toString().replace("-", ""));
//			map.put("sbankcode", "S_PAYBNKNO");
			//map.put("paymode", "S_PAYTYPECODE");
			map.put("sbudgettype", "S_BUDGETTYPE");
		}
		return map;
	}

	/**
	 * ���ܣ�ҵ����ͳ�Ʋ�ѯ(��)
	 * 
	 * @author �ν���
	 * @time 14-03-06 15:49:44 codecomment:
	 */
	private List makeReport(String biztype, TipsParamDto param, boolean flag)
			throws ITFEBizException {
		List list = new ArrayList();
		if (biztype.equals("0")) {
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_5101,param));
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				list.addAll(generateDataCountReport(MsgConstant.VOUCHER_NO_5267,param));
			}
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_1104,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_5103,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_5102,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_2201,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_2202,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_7211,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_3208,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_51044,param));
		} else
			list.addAll(generateDataCountReport(biztype, param));
		return list;
	}

	/**
	 * ���ɱ���
	 * 
	 * @param biztype
	 * @param param
	 * @return
	 * @throws ITFEBizException
	 */
	private List generateDataCountReport(String biztype, TipsParamDto param)
			throws ITFEBizException {
		try {
			SQLExecutor sqlExec = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			String sql = getSQL(biztype, param,transformTableMap(biztype, param));//************************
			SQLResults rs  = null;
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				if(MsgConstant.MSG_NO_5101.equals(biztype))
				{
					sql = StringUtils.replace(sql, "WHERE a.S_BIZNO = b.S_BIZNO", "WHERE a.S_BIZNO = b.S_BIZNO AND a.S_PAYERACCT = '"+param.getSorgcode()+"27101'");
					sql = StringUtils.replace(sql, "WHERE S_TRECODE", "WHERE S_PAYERACCT = '"+param.getSorgcode()+"27101' AND S_TRECODE");
				}else if(MsgConstant.VOUCHER_NO_5267.equals(biztype))
				{
					sql = StringUtils.replace(sql, "WHERE a.S_BIZNO = b.S_BIZNO", "WHERE a.S_BIZNO = b.S_BIZNO AND a.S_PAYERACCT <> '"+param.getSorgcode()+"27101'");
					sql = StringUtils.replace(sql, "WHERE S_TRECODE", "WHERE S_PAYERACCT <> '"+param.getSorgcode()+"27101' AND S_TRECODE");//WHERE S_TRECODE
				}
			}
			rs = sqlExec.runQueryCloseCon(sql, BizDataCountDto.class);
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	private String getSQL(String biztype, TipsParamDto param, HashMap map) {
		if (biztype.equals(MsgConstant.MSG_NO_1104)
				|| biztype.equals(MsgConstant.MSG_NO_7211))
			return getSQL(param, map);
		return getDetailSQL(biztype, param, map);
	}

	/**
	 * ҵ����ͳ��(����ϸ)��ѯSQL���
	 * 
	 * @param param
	 * @param map
	 * @return
	 */
	private String getDetailSQL(String biztype, TipsParamDto param, HashMap map) {
		return " SELECT  C.bizname,VALUE(SUM(C.succcount),0) AS succcount,VALUE(SUM(C.sudetailcount),0) AS detailsucccount,"
				+ " VALUE(SUM(C.succfamt),0.00) AS succfamt,VALUE(SUM(C.failcount),0) AS failcount,"
				+ " VALUE(SUM(C.faildetailcount),0) AS detailfailcount,VALUE(SUM(C.failfamt),0.00) AS failfamt"
				+ " FROM ( SELECT '"
				+ map.get("bizname")
				+ "' AS bizname,SUM(CASE WHEN "
				+ map.get("S_STATUS")
				+ " = '80000' THEN 1 ELSE 0 END ) AS succcount,"
				+ " 0 AS sudetailcount,SUM(CASE WHEN "
				+ map.get("S_STATUS")
				+ " = '80000' THEN "
				+ map.get("n_money")
				+ " ELSE 0 END ) AS succfamt ,"
				+ " SUM(CASE WHEN "
				+ map.get("S_STATUS")
				+ " = '80001' THEN 1 ELSE 0 END )  AS failcount,0  AS faildetailcount,"
				+ " SUM(CASE WHEN "
				+ map.get("S_STATUS")
				+ " = '80001' THEN "
				+ map.get("n_money")
				+ " ELSE 0 END ) AS failfamt "
				+ " FROM "
				+ map.get("tableName")
				+ " WHERE "
				+ ("0000000000".equals(param.getStrecode()) ? "" : map.get("strecode")+ " = '" + param.getStrecode() + "'"+ " AND ")
				+ (biztype.equals(MsgConstant.MSG_NO_5101)?"S_BACKFLAG='0'  AND  posstr(S_RECBANKNO,'011')<>1 and ":"")
				+ (biztype.equals(MsgConstant.MSG_NO_3208)?"S_BACKFLAG='1' and ":"")
				+ (biztype.equals(MsgConstant.MSG_NO_51044)?"posstr(S_RECBANKNO,'011')=1 and ":"")
			    + ((map.get("sbankcode")==null||"".equals(param.getSbankcode())||"0".equals(param.getSbankcode()) )? "" : map.get("sbankcode")+ "='"+ param.getSbankcode()+ "'"+" AND ")
			    + ((map.get("paymode")==null||"".equals(param.getPaymode())||"0".equals(param.getPaymode())) ? "" : map.get("paymode")+ "='"+ param.getPaymode()+ "'"+" AND ")
			    + ((map.get("paymode")==null||"".equals(param.getSbudgettype())||"0".equals(param.getSbudgettype())) ? "" : map.get("sbudgettype")+ "='"+ param.getSbudgettype()+ "'"+" AND ")
				+ map.get("creatdate")
				+ " BETWEEN '"
				+ map.get("startdate")
				+ "'  AND '"
				+ map.get("enddate")
				+ "' AND "
				+ map.get("S_STATUS")
				+ " IN ('80000','80001') HAVING COUNT(0) > 0 "
				+ " UNION ALL SELECT '"
				+ map.get("bizname")
				+ "' AS bizname,0 AS succcount, SUM(CASE WHEN a."
				+ map.get("S_STATUS")
				+ " = '80000' THEN 1 ELSE 0 END ) AS sudetailcount ,"
				+ " 0 AS succfamt , 0 AS failcount,SUM(CASE WHEN a."
				+ map.get("S_STATUS")
				+ " = '80001' THEN 1 ELSE 0 END ) AS faildetailcount,"
				+ " 0 AS failfamt FROM "
				+ map.get("tableName")
				+ " a ,"
				+ map.get("subtableName")
				+ " b WHERE a."
				+ map.get("sdealno")
				+ " = b."
				+ map.get("sdealno")
				+ (biztype.equals(MsgConstant.MSG_NO_5103) ? " AND a.S_PACKAGETICKETNO=b.S_PACKAGETICKETNO": " ")
				+ ("0000000000".equals(param.getStrecode()) ? "" : " AND "+ map.get("strecode") + " = '" + param.getStrecode()+ "'")+ " AND "
				+ (biztype.equals(MsgConstant.MSG_NO_5101)?"S_BACKFLAG='0'  AND  posstr(S_RECBANKNO,'011')<>1 and ":"")
				+ (biztype.equals(MsgConstant.MSG_NO_3208)?"S_BACKFLAG='1' and ":"")
				+ (biztype.equals(MsgConstant.MSG_NO_51044)?"posstr(S_RECBANKNO,'011')=1 and ":"")
				 + ((map.get("sbankcode")==null||"".equals(param.getSbankcode())||"0".equals(param.getSbankcode()) )? "" : map.get("sbankcode")+ "='"+ param.getSbankcode()+ "'"+" AND ")
				    + ((map.get("paymode")==null||"".equals(param.getPaymode())||"0".equals(param.getPaymode())) ? "" : map.get("paymode")+ "='"+ param.getPaymode()+ "'"+" AND ")
				    + ((map.get("paymode")==null||"".equals(param.getSbudgettype())||"0".equals(param.getSbudgettype())) ? "" : map.get("sbudgettype")+ "='"+ param.getSbudgettype()+ "'"+" AND ")
					+ map.get("creatdate")
				+ " BETWEEN '"
				+ map.get("startdate")
				+ "'  AND '"
				+ map.get("enddate")
				+ "' AND a."
				+ map.get("S_STATUS")
				+ " IN ('80000','80001') HAVING COUNT(0) > 0 "
				+ " UNION ALL SELECT '"
				+ map.get("bizname")
				+ "' AS bizname,SUM(CASE WHEN "
				+ map.get("S_STATUS")
				+ " = '80000' THEN 1 ELSE 0 END ) AS succcount,"
				+ " 0  AS sudetailcount,SUM(CASE WHEN "
				+ map.get("S_STATUS")
				+ " = '80000' THEN "
				+ map.get("n_money")
				+ " ELSE 0 END ) AS succfamt ,"
				+ " SUM(CASE WHEN "
				+ map.get("S_STATUS")
				+ " = '80001' THEN 1 ELSE 0 END ) AS failcount,0 AS faildetailcount,"
				+ " SUM(CASE WHEN "
				+ map.get("S_STATUS")
				+ " = '80001' THEN "
				+ map.get("n_money")
				+ " ELSE 0 END ) AS failfamt "
				+ " FROM H"
				+ map.get("tableName")
				+ " WHERE "
				+ ("0000000000".equals(param.getStrecode()) ? "" : map.get("strecode")+ " = '" + param.getStrecode() + "'" + " AND ")
				+ (biztype.equals(MsgConstant.MSG_NO_5101)?"S_BACKFLAG='0'  AND  posstr(S_RECBANKNO,'011')<>1 and ":"")
				+ (biztype.equals(MsgConstant.MSG_NO_3208)?"S_BACKFLAG='1' and ":"")
				+ (biztype.equals(MsgConstant.MSG_NO_51044)?"posstr(S_RECBANKNO,'011')=1 and ":"")
			    + ((map.get("sbankcode")==null||"".equals(param.getSbankcode())||"0".equals(param.getSbankcode()) )? "" : map.get("sbankcode")+ "='"+ param.getSbankcode()+ "'"+" AND ")
			    + ((map.get("paymode")==null||"".equals(param.getPaymode())||"0".equals(param.getPaymode())) ? "" : map.get("paymode")+ "='"+ param.getPaymode()+ "'"+" AND ")
			    + ((map.get("paymode")==null||"".equals(param.getSbudgettype())||"0".equals(param.getSbudgettype())) ? "" : map.get("sbudgettype")+ "='"+ param.getSbudgettype()+ "'"+" AND ")
				+ map.get("creatdate")
				+ " BETWEEN '"
				+ map.get("startdate")
				+ "'  AND '"
				+ map.get("enddate")
				+ "' AND "
				+ map.get("S_STATUS")
				+ " IN ('80000','80001') HAVING COUNT(0) > 0 "
				+ " UNION ALL SELECT '"
				+ map.get("bizname")
				+ "' AS bizname,0 AS succcount,"
				+ " SUM(CASE WHEN a."
				+ map.get("S_STATUS")
				+ " = '80000' THEN 1 ELSE 0 END ) AS sudetailcount ,0 AS succfamt , 0 AS failcount,"
				+ " SUM(CASE WHEN a."
				+ map.get("S_STATUS")
				+ " = '80001' THEN 1 ELSE 0 END ) AS faildetailcount,0 AS failfamt "
				+ " FROM H"
				+ map.get("tableName")
				+ " a ,H"
				+ map.get("subtableName")
				+ " b WHERE a."
				+ map.get("sdealno")
				+ " = b."
				+ map.get("sdealno")
				+ " "
				+ (biztype.equals(MsgConstant.MSG_NO_5103) ? " AND a.S_PACKAGETICKETNO=b.S_PACKAGETICKETNO": "")
				+ ("0000000000".equals(param.getStrecode()) ? "" : " AND "+ map.get("strecode") + " = '" + param.getStrecode()+ "'")+ " AND "
				+ (biztype.equals(MsgConstant.MSG_NO_5101)?"S_BACKFLAG='0'  AND  posstr(S_RECBANKNO,'011')<>1 and ":"")
				+ (biztype.equals(MsgConstant.MSG_NO_3208)?"S_BACKFLAG='1' and ":"")
				+ (biztype.equals(MsgConstant.MSG_NO_51044)?"posstr(S_RECBANKNO,'011')=1 and ":"")
				 + ((map.get("sbankcode")==null||"".equals(param.getSbankcode())||"0".equals(param.getSbankcode()) )? "" : map.get("sbankcode")+ "='"+ param.getSbankcode()+ "'"+" AND ")
				    + ((map.get("paymode")==null||"".equals(param.getPaymode())||"0".equals(param.getPaymode())) ? "" : map.get("paymode")+ "='"+ param.getPaymode()+ "'"+" AND ")
				    + ((map.get("paymode")==null||"".equals(param.getSbudgettype())||"0".equals(param.getSbudgettype())) ? "" : map.get("sbudgettype")+ "='"+ param.getSbudgettype()+ "'"+" AND ")
					+ map.get("creatdate")
				+ " BETWEEN '"
				+ map.get("startdate")
				+ "'  AND '"
				+ map.get("enddate")
				+ "' AND a."
				+ map.get("S_STATUS")
				+ " IN ('80000','80001') HAVING COUNT(0) > 0 ) C GROUP BY C.bizname ";
	}

	/**
	 * ҵ����ͳ�Ʋ�ѯSQL���
	 * 
	 * @param param
	 * @param map
	 * @return
	 */
	private String getSQL(TipsParamDto param, HashMap map) {
		return "SELECT A.bizname, VALUE(SUM(succcount),0) AS succcount,0 AS detailsucccount,VALUE(SUM(succfamt),0.00) AS succfamt,"
				+ " VALUE(SUM(failcount),0) AS failcount,0 AS detailfailcount,VALUE(SUM(failfamt),0.00) AS failfamt FROM ( "
				+ " SELECT '"
				+ map.get("bizname")
				+ "' AS bizname, SUM(CASE WHEN S_STATUS = '80000' THEN 1 ELSE 0 END ) AS succcount,"
				+ " SUM(CASE WHEN S_STATUS = '80000' THEN "
				+ map.get("n_money")
				+ " ELSE 0 END ) AS succfamt ,"
				+ " SUM(CASE WHEN S_STATUS = '80001' THEN 1 ELSE 0 END ) AS failcount,"
				+ " SUM(CASE WHEN S_STATUS = '80001' THEN "
				+ map.get("n_money")
				+ " ELSE 0 END ) AS failfamt "
				+ " FROM "
				+ map.get("tableName")
				+ " WHERE "
				+ ("0000000000".equals(param.getStrecode()) ? " " : map
						.get("strecode")
						+ " = '" + param.getStrecode() + "' AND ")
			     + ((map.get("sbankcode")==null||"".equals(param.getSbankcode())||"0".equals(param.getSbankcode()) )? "" : map.get("sbankcode")+ "='"+ param.getSbankcode()+ "'"+" AND ")
			    + ((map.get("paymode")==null||"".equals(param.getPaymode())||"0".equals(param.getPaymode())) ? "" : map.get("paymode")+ "='"+ param.getPaymode()+ "'"+" AND ")
			    + ((map.get("paymode")==null||"".equals(param.getSbudgettype())||"0".equals(param.getSbudgettype())) ? "" : map.get("sbudgettype")+ "='"+ param.getSbudgettype()+ "'"+" AND ")
				+ map.get("creatdate")
				+ " BETWEEN '"
				+ map.get("startdate")
				+ "'  AND '"
				+ map.get("enddate")
				+ "' AND S_STATUS IN ('80000','80001') HAVING COUNT(0) > 0"
				+ " UNION ALL SELECT '"
				+ map.get("bizname")
				+ "' AS bizname,SUM(CASE WHEN S_STATUS = '80000' THEN 1 ELSE 0 END ) AS succcount,"
				+ " SUM(CASE WHEN S_STATUS = '80000' THEN "
				+ map.get("n_money")
				+ " ELSE 0 END ) AS succfamt ,"
				+ " SUM(CASE WHEN S_STATUS = '80001' THEN 1 ELSE 0 END ) AS failcount,"
				+ " SUM(CASE WHEN S_STATUS = '80001' THEN "
				+ map.get("n_money")
				+ " ELSE 0 END ) AS failfamt "
				+ " FROM H"
				+ map.get("tableName")
				+ " WHERE "
				+ ("0000000000".equals(param.getStrecode()) ? " " : map
						.get("strecode")
						+ " = '" + param.getStrecode() + "' AND ")
				 + ((map.get("sbankcode")==null||"".equals(param.getSbankcode())||"0".equals(param.getSbankcode()) )? "" : map.get("sbankcode")+ "='"+ param.getSbankcode()+ "'"+" AND ")
			    + ((map.get("paymode")==null||"".equals(param.getPaymode())||"0".equals(param.getPaymode())) ? "" : map.get("paymode")+ "='"+ param.getPaymode()+ "'"+" AND ")
			    + ((map.get("paymode")==null||"".equals(param.getSbudgettype())||"0".equals(param.getSbudgettype())) ? "" : map.get("sbudgettype")+ "='"+ param.getSbudgettype()+ "'"+" AND ")
				+ map.get("creatdate")
				+ " BETWEEN '"
				+ map.get("startdate")
				+ "'  AND '"
				+ map.get("enddate")
				+ "' AND S_STATUS IN ('80000','80001') HAVING COUNT(0) > 0 ) A GROUP BY A.bizname";
	}

	public List makeBankReport(TipsParamDto paramdto) throws ITFEBizException {
		System.out.println("makeBankReport");
		try {
			String forstart = paramdto.getStartdate().toString().replace("-",
					"");
			String forend = paramdto.getEnddate().toString().replace("-", "");
			StringBuffer insql = new StringBuffer();
			SQLExecutor sqlExec = null;
			SQLResults rs = null;
			insql
					.append(" SELECT S_BANKNAME AS SPAYBANKNAME,CASE WHEN DIRECTPAYNUM IS NOT NULL THEN DIRECTPAYNUM ELSE 0 END AS DIRECTPAYNUM, CASE WHEN DIRECTPAYAMT IS NOT NULL THEN DIRECTPAYAMT ELSE 0.00 END AS DIRECTPAYAMT,CASE WHEN PAYOUTNUM IS NOT NULL THEN PAYOUTNUM ELSE 0 END AS PAYOUTNUM ,CASE WHEN PAYOUTAMT IS NOT NULL THEN PAYOUTAMT ELSE 0.00 END AS PAYOUTAMT,CASE WHEN GRANTPAYNUM IS NOT NULL THEN GRANTPAYNUM ELSE 0 END AS GRANTPAYNUM ,CASE WHEN GRANTPAYAMT IS NOT NULL  THEN GRANTPAYAMT ELSE 0.00 END  AS GRANTPAYAMT,CASE WHEN WORKCARDNUM IS NOT NULL THEN WORKCARDNUM ELSE 0 END AS WORKCARDNUM ,CASE WHEN WORKCARDAMT IS NOT NULL THEN WORKCARDAMT ELSE 0.00 END AS WORKCARDAMT, CASE WHEN DIRECTPAYNUM IS NOT NULL THEN DIRECTPAYNUM ELSE 0 END +CASE WHEN PAYOUTNUM IS NOT NULL THEN PAYOUTNUM ELSE 0 END +CASE WHEN GRANTPAYNUM IS NOT NULL THEN GRANTPAYNUM ELSE 0 END +CASE WHEN WORKCARDNUM IS NOT NULL THEN WORKCARDNUM ELSE 0 END  AS TOTALNUM, CASE WHEN DIRECTPAYAMT IS NOT NULL THEN DIRECTPAYAMT ELSE 0.00 END +CASE WHEN PAYOUTAMT IS NOT NULL THEN PAYOUTAMT ELSE 0.00 END +CASE WHEN GRANTPAYAMT IS NOT NULL THEN GRANTPAYAMT ELSE 0.00 END +CASE WHEN WORKCARDAMT IS NOT NULL THEN WORKCARDAMT ELSE 0.00 END AS TOTALAMT FROM ( ");
			insql
					.append(" SELECT * FROM ((SELECT CASE WHEN A.S_PAYBANKCODE IS NOT NULL THEN A.S_PAYBANKCODE ELSE B.S_PAYBANKCODE END AS S_PAYBANKCODE,DIRECTPAYNUM,DIRECTPAYAMT,PAYOUTNUM,PAYOUTAMT,GRANTPAYNUM,GRANTPAYAMT,WORKCARDNUM,WORKCARDAMT FROM ((SELECT CASE WHEN A.S_PAYBANKCODE IS NOT NULL THEN A.S_PAYBANKCODE ELSE B.S_PAYBANKCODE END AS S_PAYBANKCODE,PAYOUTNUM,PAYOUTAMT,GRANTPAYNUM,GRANTPAYAMT,WORKCARDNUM,WORKCARDAMT FROM  ((SELECT CASE WHEN A.S_PAYBANKCODE IS NOT NULL THEN A.S_PAYBANKCODE ELSE B.S_PAYBANKCODE END AS S_PAYBANKCODE,PAYOUTNUM,PAYOUTAMT,GRANTPAYNUM,GRANTPAYAMT FROM ((");
			insql
					.append(" SELECT SUM(PAYOUTNUM) AS PAYOUTNUM,SUM(PAYOUTAMT) AS PAYOUTAMT ,S_INPUTRECBANKNO AS S_PAYBANKCODE FROM (SELECT * FROM((SELECT COUNT(*) AS PAYOUTNUM,SUM(N_MONEY) AS PAYOUTAMT,S_BIZNO AS SBIZNO FROM TV_PAYOUTMSGSUB GROUP BY S_BIZNO ");//Ԥ���ڲ��ʵ���ʽ�ҵ����ͳ��
			insql
					.append(") A INNER  JOIN  (SELECT S_INPUTRECBANKNO,S_BIZNO FROM TV_PAYOUTMSGMAIN WHERE S_BIZNO IN  (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE='5207' AND S_STATUS='80' AND S_TRECODE= ?  AND S_CREATDATE BETWEEN ? AND ? ))B ON  A.SBIZNO=B.S_BIZNO)) GROUP  BY S_INPUTRECBANKNO  ) A FULL JOIN (");
			insql
					.append(" SELECT SUM (GRANTPAYNUM) AS GRANTPAYNUM,SUM(GRANTPAYAMT) AS GRANTPAYAMT ,S_AGENTBNKCODE AS S_PAYBANKCODE FROM (SELECT * FROM ((SELECT I_VOUSRLNO AS IVOUSRLNO,COUNT(*) AS GRANTPAYNUM,SUM(F_AMT) AS GRANTPAYAMT FROM TV_PAYRECK_BANK_LIST GROUP BY I_VOUSRLNO) A INNER  JOIN (SELECT S_AGENTBNKCODE,I_VOUSRLNO FROM TV_PAYRECK_BANK WHERE S_PAYTYPENAME='��Ȩ֧��' AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE='2301' AND S_STATUS='80'  AND S_TRECODE= ?  AND S_CREATDATE BETWEEN ? AND ? )) B ON A.IVOUSRLNO=B.I_VOUSRLNO))GROUP BY S_AGENTBNKCODE  ");//��Ȩ֧��ҵ����ͳ��
			insql
					.append(" ) B ON A.S_PAYBANKCODE=B.S_PAYBANKCODE)) A FULL JOIN ( ");
			insql
					.append(" SELECT SUM (WORKCARDNUM) AS WORKCARDNUM,SUM(WORKCARDAMT) AS WORKCARDAMT ,S_AGENTBNKCODE AS S_PAYBANKCODE FROM (SELECT * FROM ((SELECT I_VOUSRLNO AS IVOUSRLNO,COUNT(*) AS WORKCARDNUM,SUM(F_AMT) AS WORKCARDAMT FROM TV_PAYRECK_BANK_LIST GROUP BY I_VOUSRLNO) A INNER  JOIN (SELECT S_AGENTBNKCODE,I_VOUSRLNO FROM TV_PAYRECK_BANK WHERE S_PAYTYPENAME='��Ȩ֧��' AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE='2301' AND S_STATUS='80'  AND S_TRECODE= ?  AND S_CREATDATE BETWEEN ? AND ? )) B ON A.IVOUSRLNO=B.I_VOUSRLNO))GROUP BY S_AGENTBNKCODE ");//����ҵ����ͳ��
			insql
					.append(" ) B ON A.S_PAYBANKCODE=B.S_PAYBANKCODE )) A FULL JOIN (");
			insql
					.append(" SELECT sum(num) AS directpaynum,sum(amt) AS directpayamt,s_paybankcode FROM ((SELECT count(*) AS NUM,sum(n_payamt) AS amt,I_VOUSRLNO FROM TF_DIRECTPAYMSGsub GROUP  BY  I_VOUSRLNO )a INNER  JOIN (SELECT I_VOUSRLNO,s_paybankcode FROM TF_DIRECTPAYMSGMAIN WHERE s_status='80000' AND s_trecode= ? AND S_XPAYDATE BETWEEN ? AND ?) b ON  a.I_VOUSRLNO=b.I_VOUSRLNO ) GROUP BY s_paybankcode ");//ֱ��֧��ҵ����ͳ�Ʋ�ѯTF_DIRECTPAYMSGMAIN�Լ�TF_DIRECTPAYMSGSUB��
			insql
					.append(" ) B ON A.S_PAYBANKCODE=B.S_PAYBANKCODE)) M INNER  JOIN TS_PAYBANK ON M.S_PAYBANKCODE=TS_PAYBANK.S_BANKNO)) ");
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.addParam(paramdto.getStrecode());
			sqlExec.addParam(forstart);
			sqlExec.addParam(forend);
			sqlExec.addParam(paramdto.getStrecode());
			sqlExec.addParam(forstart);
			sqlExec.addParam(forend);
			sqlExec.addParam(paramdto.getStrecode());
			sqlExec.addParam(forstart);
			sqlExec.addParam(forend);
			sqlExec.addParam(paramdto.getStrecode());
			sqlExec.addParam(forstart);
			sqlExec.addParam(forend);
			rs = sqlExec                                   
					.runQueryCloseCon(insql.toString(), BankReportDto.class);
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("���ɱ����쳣!\n" + e.getMessage(), e);
		}
	}

	public String exportFile(TipsParamDto paramdto) throws ITFEBizException {
		// ȡ�ò���Ա�Ļ�������
		String orgcode = this.getLoginInfo().getSorgcode();
		List list = new ArrayList();
		String strename;
		String filename = "";
		SQLExecutor sqlExec = null;
		SQLResults rs = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			rs = sqlExec
					.runQuery("select s_trename from TS_TREASURY where s_trecode= "
							+ paramdto.getStrecode());
			System.out.println(rs);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ���������쳣!\n" + e.getMessage(), e);
		}finally {
			if (sqlExec != null)
				sqlExec.closeConnection();
		}
		strename = rs.getString(1, "s_trenema");
		System.out.println(strename);
		filename = strename + paramdto.getStartdate() + "��"
				+ paramdto.getEnddate() + "����ҵ���д����м�����֧��ҵ����ͳ�Ʊ�"
				+ DateUtil.date2String3(new Date()) + ".csv";
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String splitSign = ",";// "\t"; // �ļ���¼�ָ�����
		String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
		System.out.println(strdate);
		String fullpath = root + "exportFile" + dirsep + strdate + dirsep
				+ filename;
		int totaldirectpaynum = 0;
		BigDecimal totaldirectpayamt = new BigDecimal(0.00);
		int totalpayoutnum = 0;
		BigDecimal totalpayoutamt = new BigDecimal(0.00);
		int totalgrantpaynum = 0;
		BigDecimal totalgrantpayamt = new BigDecimal(0.00);
		int totalworkcardnum = 0;
		BigDecimal totalworkcardamt = new BigDecimal(0.00);
		int totaltotalnum = 0;
		BigDecimal totaltotalamt = new BigDecimal(0.00);
		try {
			list = makeBankReport(paramdto);
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				// filebuf.append("����ҵ���д����м�����֧��ҵ����ͳ�Ʊ�\r\n");
				// filebuf.append("�������ƣ�,");
				// filebuf.append(strename);
				// filebuf.append(" , , , ,");
				// filebuf.append(paramdto.getStartdate());
				// filebuf.append(",��,");
				// filebuf.append(paramdto.getEnddate());
				// filebuf.append("\r\n");
				// filebuf
				// .append("��λ����, ,�ո�����ֱ��֧��ҵ��, ,Ԥ�㲦��ҵ��, ,�ո�������Ȩ֧��ҵ��, ,����ҵ��, ,�ϼ�\r\n");
				filebuf
						.append(" ��λ����,�ո�����ֱ��֧��ҵ�����,�ո�����ֱ��֧��ҵ���ܽ��,Ԥ�㲦��ҵ�����,Ԥ�㲦��ҵ���ܽ��,�ո�������Ȩ֧��ҵ�����,�ո�������Ȩ֧��ҵ���ܽ��,����ҵ�����,����ҵ���ܽ��,�ϼƱ���,�ϼ��ܽ��\r\n");
				for (int i = 0; i < list.size(); i++) {
					BankReportDto bankReportDto = (BankReportDto) list.get(i);
					filebuf.append(bankReportDto.getSpaybankname());// ��λ����
					filebuf.append(splitSign);
					filebuf.append(bankReportDto.getDirectpaynum());// ֱ��֧������
					filebuf.append(splitSign);
					filebuf.append(bankReportDto.getDirectpayamt());// ֱ��֧�����
					filebuf.append(splitSign);
					filebuf.append(bankReportDto.getPayoutnum());// �������
					filebuf.append(splitSign);
					filebuf.append(bankReportDto.getPayoutamt());// ������
					filebuf.append(splitSign);
					filebuf.append(bankReportDto.getGrantpaynum());// ��Ȩ֧������
					filebuf.append(splitSign);
					filebuf.append(bankReportDto.getGrantpayamt());// ��Ȩ֧�����
					filebuf.append(splitSign);
					filebuf.append(bankReportDto.getWorkcardnum());// ���񿨱���
					filebuf.append(splitSign);
					filebuf.append(bankReportDto.getWorkcardamt());// ���񿨽��
					filebuf.append(splitSign);
					filebuf.append(bankReportDto.getTotalnum());// �ܱ���
					filebuf.append(splitSign);
					filebuf.append(bankReportDto.getTotalamt());// �ܽ��
					filebuf.append("\r\n");
					totaldirectpaynum = totaldirectpaynum
							+ bankReportDto.getDirectpaynum();// ֱ��֧���ϼƱ���
					totaldirectpayamt = totaldirectpayamt.add(bankReportDto
							.getDirectpayamt());// ֱ��֧���ϼƽ��
					totalpayoutnum = totalpayoutnum
							+ bankReportDto.getPayoutnum();// ����ϼƱ���
					totalpayoutamt = totalpayoutamt.add(bankReportDto
							.getPayoutamt());// ����ϼƽ��
					totalgrantpaynum = totalgrantpaynum
							+ bankReportDto.getGrantpaynum();// ��Ȩ֧���ϼƱ���
					totalgrantpayamt = totalgrantpayamt.add(bankReportDto
							.getGrantpayamt());// ��Ȩ֧���ϼƽ��
					totalworkcardnum = totalworkcardnum
							+ bankReportDto.getWorkcardnum();// ���񿨺ϼƱ���
					totalworkcardamt = totalworkcardamt.add(bankReportDto
							.getWorkcardamt());// ���񿨺ϼƽ��
					totaltotalnum = totaltotalnum + bankReportDto.getTotalnum();// �ϼ��ܱ���
					totaltotalamt = totaltotalamt.add(bankReportDto
							.getTotalamt());// �ϼ��ܽ��
				}
				filebuf.append("�ϼ�");
				filebuf.append(splitSign);
				filebuf.append(totaldirectpaynum);
				filebuf.append(splitSign);
				filebuf.append(totaldirectpayamt);
				filebuf.append(splitSign);
				filebuf.append(totalpayoutnum);
				filebuf.append(splitSign);
				filebuf.append(totalpayoutamt);
				filebuf.append(splitSign);
				filebuf.append(totalgrantpaynum);
				filebuf.append(splitSign);
				filebuf.append(totalgrantpayamt);
				filebuf.append(splitSign);
				filebuf.append(totalworkcardnum);
				filebuf.append(splitSign);
				filebuf.append(totalworkcardamt);
				filebuf.append(splitSign);
				filebuf.append(totaltotalnum);
				filebuf.append(splitSign);
				filebuf.append(totaltotalamt);
				// filebuf.append("\r\n");
				// filebuf.append(" , , , , , , , , ,��ӡ����:"
				// + new SimpleDateFormat("yyyy��MM��dd��")
				// .format(new java.util.Date()));
				// filebuf.append("\r\n");
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				return fullpath.replaceAll(root, "");
			} else {
				throw new ITFEBizException("��ѯ������");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("д�ļ�����", e);
		}

	}
}
/*
*//**
 * ���ɱ���222222222222
 * @param biztype
 * @param param
 * @return
 * @throws ITFEBizException
 *//*
	private List generateDataCountReport2(String biztype, String bizbanktype,
			String bizpaytype, String bizbudgettype, TipsParamDto param)
			throws ITFEBizException {
		try {
			SQLExecutor sqlExec = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			String sql = getSQL(biztype, param,transformTableMap(biztype, param));
			SQLResults rs  = null;
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				if(MsgConstant.MSG_NO_5101.equals(biztype))
				{
					sql = StringUtils.replace(sql, "WHERE a.S_BIZNO = b.S_BIZNO", "WHERE a.S_BIZNO = b.S_BIZNO AND a.S_PAYERACCT = '"+param.getSorgcode()+"27101'");
					sql = StringUtils.replace(sql, "WHERE S_TRECODE", "WHERE S_PAYERACCT = '"+param.getSorgcode()+"27101' AND S_TRECODE");
				}else if(MsgConstant.VOUCHER_NO_5267.equals(biztype))
				{
					sql = StringUtils.replace(sql, "WHERE a.S_BIZNO = b.S_BIZNO", "WHERE a.S_BIZNO = b.S_BIZNO AND a.S_PAYERACCT <> '"+param.getSorgcode()+"27101'");
					sql = StringUtils.replace(sql, "WHERE S_TRECODE", "WHERE S_PAYERACCT <> '"+param.getSorgcode()+"27101' AND S_TRECODE");//WHERE S_TRECODE
				}
			}
			rs = sqlExec.runQueryCloseCon(sql, BizDataCountDto.class);
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}
	
	*//**
	 * ���ܣ�ҵ����ͳ�Ʋ�ѯ(2222)
	 * 
	 * @author 
	 * @time 14-03-06 15:49:44 codecomment:
	 *//*
	private List makeReport2(String biztype, String bizbanktype,
			String bizpaytype, String bizbudgettype, TipsParamDto param, boolean flag)
			throws ITFEBizException {
		List list = new ArrayList();
		if (biztype.equals("0")) {
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_5101,param));
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				list.addAll(generateDataCountReport(MsgConstant.VOUCHER_NO_5267,param));
			}
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_1104,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_5103,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_5102,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_2201,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_2202,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_7211,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_3208,param));
			list.addAll(generateDataCountReport(MsgConstant.MSG_NO_51044,param));
		} else
			list.addAll(generateDataCountReport(biztype, param));
		return list;
	}
	
	public List generalDataForIncome2(String biztype, String bizbanktype,
			String bizpaytype, String bizbudgettype, TipsParamDto param)
			throws JAFDatabaseException {
		String forstart = param.getStartdate().toString().replace("-", "");
		String forend = param.getEnddate().toString().replace("-", "");
		SQLExecutor sqlExec = null;
		SQLResults rs = null;
		StringBuffer insql = new StringBuffer();
		insql
				.append("SELECT * FROM  (SELECT (SELECT S_ORGname FROM TS_ORGAN WHERE S_ORGCODE = A.s_orgcode) as S_ORGname,(SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=?) AS bizname, count(*) AS totalcount ,"
						+ " sum(N_MONEY) AS totalfamt "
						+ " FROM TV_INFILE_DETAIL A WHERE  S_COMMITDATE>=? AND S_COMMITDATE<=? ");
		sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
				.getSQLExecutor();
		
		if ((StateConstant.ORG_CENTER_CODE.equals(getLoginInfo().getSorgcode()) || StateConstant.STAT_CENTER_CODE
				.equals(getLoginInfo().getSorgcode()))
				&& (StringUtils.isBlank(param.getSorgcode()) || StateConstant.ORG_CENTER_CODE
						.equals(param.getSorgcode()))) {
			insql = new StringBuffer("");
			insql
					.append(" SELECT 'ȫϽ' as S_ORGname,'����˰Ʊ' AS bizname, count(*) AS totalcount ,"
							+ " sum(N_MONEY) AS totalfamt "
							+ " FROM TV_INFILE_DETAIL A WHERE  S_COMMITDATE>=? AND S_COMMITDATE<=? ");
			sqlExec.addParam(forstart);
			sqlExec.addParam(forend);
		} else {
			insql.append(" AND S_ORGCODE=?  ");
			sqlExec.addParam("0407");
			sqlExec.addParam(biztype);
			sqlExec.addParam(forstart);
			sqlExec.addParam(forend);
			sqlExec.addParam(param.getSorgcode());
			insql.append(" GROUP BY A.S_ORGCODE) WHERE totalcount>0");
		}
	
	// sqlExec.addParam(getLoginInfo().getSorgcode());
	rs = sqlExec.runQueryCloseCon(insql.toString(), BizDataCountDto.class);
	return (List) rs.getDtoCollection();
	}

	public List makeReport(String biztype, String bizbanktype,
			String bizpaytype, String bizbudgettype, TipsParamDto param)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		try {
			String forstart = param.getStartdate().toString().replace("-", "");
			String forend = param.getEnddate().toString().replace("-", "");
			StringBuffer insql = new StringBuffer();
			SQLExecutor sqlExec = null;
			SQLResults rs = null;
			if (StringUtils.isNotBlank(param.getStrecode()))
				return makeReport2(biztype,bizbanktype,bizpaytype,bizbudgettype, param, true);
			;
			if (BizTypeConstant.BIZ_TYPE_INCOME.equals(biztype)) { // ��������ǻ����������ϸ
				List li = generalDataForIncome2(biztype,bizbanktype,bizpaytype,bizbudgettype, param);
				if (null != li) {
					return li;
				} else {
					return null;
				}

			} else if ("0".equals(biztype) && "0".equals(bizbanktype) && "".equals(bizpaytype) && "0".equals(bizbudgettype)) { // ȫ��ҵ������
				insql
						.append("SELECT (SELECT S_ORGname FROM TS_ORGAN WHERE S_ORGCODE = A.s_orgcode) as S_ORGname,(SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=A.S_OPERATIONTYPECODE)  AS bizname,"
								+ "(SELECT S_BANKNAME FROM TS_CONVERTBANKTYPE WHERE S_TRECODE=? AND S_ORGCODE = A.s_orgcode)  AS BANGKname,"
								+ "(SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=A.S_OPERATIONTYPECODE)  AS PAYtype,"
								+ "(SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=A.S_OPERATIONTYPECODE)  AS Budget"
								+ " SUM(I_COUNT) AS totalcount ,"
								+ " SUM(N_MONEY) AS totalfamt "
								+ " FROM TV_FILEPACKAGEREF A WHERE S_OPERATIONTYPECODE IN("
								+ StateConstant.Count_Statics_Type
								+ ") "
								+ " AND S_COMMITDATE>=?"
								+ " AND S_COMMITDATE<=? ");
				sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				sqlExec.addParam("0407");
				sqlExec.addParam("0900000000");
				sqlExec.addParam("0127");
				sqlExec.addParam("0122");
				sqlExec.addParam(forstart);
				sqlExec.addParam(forend);
				// ����������û����һ���Ϊ����ͳ��ȫ����
				if ((StateConstant.ORG_CENTER_CODE.equals(getLoginInfo()
						.getSorgcode()) || StateConstant.STAT_CENTER_CODE
						.equals(getLoginInfo().getSorgcode()))
						&& (StringUtils.isBlank(param.getSorgcode()) || StateConstant.ORG_CENTER_CODE
								.equals(param.getSorgcode()))) {
					insql = new StringBuffer("");
					insql
							.append("SELECT 'ȫϽ' as S_ORGname,(SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=A.S_OPERATIONTYPECODE)  AS bizname,"
									+ " SUM(I_COUNT) AS totalcount ,"
									+ " SUM(N_MONEY) AS totalfamt "
									+ " FROM TV_FILEPACKAGEREF A WHERE S_OPERATIONTYPECODE IN("
									+ StateConstant.Count_Statics_Type
									+ ") "
									+ " AND S_COMMITDATE>=?"
									+ " AND S_COMMITDATE<=? ");
					insql.append(" GROUP BY A.S_OPERATIONTYPECODE  ");
				} else {
					if (!StateConstant.ORG_CENTER_CODE.equals(getLoginInfo()
							.getSorgcode())) {
						insql.append(" AND S_ORGCODE=?  ");
						sqlExec.addParam(param.getSorgcode());
					}
					insql
							.append(" GROUP BY A.S_ORGCODE,A.S_OPERATIONTYPECODE ORDER BY A.S_ORGCODE ");
				}

				// sqlExec.addParam(getLoginInfo().getSorgcode());
				rs = sqlExec.runQueryCloseCon(insql.toString(),
						BizDataCountDto.class);
				if (null == rs) {
					return null;
				}
				List list = (List) rs.getDtoCollection();
				list.addAll(generalDataForIncome(
						BizTypeConstant.BIZ_TYPE_INCOME, param)); // �����뵥������
				return list;
			} else { // ������ҵ������
				insql.append("SELECT * FROM ");
				insql
						.append(" (SELECT (SELECT S_ORGname FROM TS_ORGAN WHERE S_ORGCODE = A.s_orgcode) AS s_orgname,(SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=?)  AS bizname,");
				insql
						.append(" SUM(I_COUNT) AS totalcount , SUM(N_MONEY) AS totalfamt  FROM TV_FILEPACKAGEREF A ");
				insql
						.append(" WHERE S_OPERATIONTYPECODE =?  AND S_COMMITDATE>=?  AND S_COMMITDATE<=? ");
				sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				sqlExec.addParam("0407");
				sqlExec.addParam(biztype);
				sqlExec.addParam(biztype);
				sqlExec.addParam(forstart);
				sqlExec.addParam(forend);
				if (StateConstant.ORG_CENTER_CODE.equals(getLoginInfo()
						.getSorgcode())
						&& StringUtils.isBlank(param.getSorgcode())) {

				} else {
					insql.append(" AND S_ORGCODE=?  ");
					sqlExec.addParam(param.getSorgcode());
				}
				insql
						.append("GROUP BY A.S_OPERATIONTYPECODE,A.S_ORGCODE ) WHERE totalcount>0");
				rs = sqlExec.runQueryCloseCon(insql.toString(),
						BizDataCountDto.class);
				if (null == rs) {
					return null;
				}
				return (List) rs.getDtoCollection();
			}

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("���ɱ����쳣!\n" + e.getMessage(), e);
		}
		
		
	}*/

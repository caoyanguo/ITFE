package com.cfcc.itfe.service.dataquery.finbudgetreccountreport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.IncomeCountReportBySubjectDto;
import com.cfcc.itfe.persistence.dto.IncomeMoneyReportDto;
import com.cfcc.itfe.persistence.dto.StockCountAnalysicsReportDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author caoyg
 * @time 10-06-20 15:26:21 codecomment:
 */

public class PayAndIncomeBillService extends AbstractPayAndIncomeBillService {
	private static Log log = LogFactory.getLog(PayAndIncomeBillService.class);

	/**
	 * ���������ձ�
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --��������
	 * @param sleSubjectType
	 *            --��Ŀ����
	 * @param sleSubjectAttribute
	 *            --��Ŀ����
	 * @param smoveflag
	 *            --������־
	 * @param sleMoneyUnit
	 *            --��λ
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List makeIncomeBill(IDto idto, String sleBillType,
			String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType,
			String sleSubjectAttribute, String smoveflag, String sleMoneyUnit)
			throws ITFEBizException {
		try {
			String sql = makeIncomeReportSQL(idto, sleBillType, sleTenrptType,
					sleQuarterrptType, sleHalfyearrptType, sleSubjectType,
					sleSubjectAttribute, smoveflag, sleMoneyUnit);

			// ��ѯ����SQL
			String querysql = sql.split("#")[0];

			// ��ӡsql
			log.debug(querysql);

			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			List querylist = (List) exec.runQueryCloseCon(querysql.toString(),
					IncomeMoneyReportDto.class, true).getDtoCollection();

			return querylist;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}

	/**
	 * ����֧���ձ�
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --��������
	 * @param sleTenrptType
	 *            --Ѯ������
	 * @param sleQuarterrptType
	 *            --��������
	 * @param sleHalfyearrptType
	 *            --���걨����
	 * @param sleSubjectType
	 *            --��Ŀ����
	 * @param sleSubjectAttribute
	 *            --��Ŀ����
	 * @param smoveflag
	 *            --������־
	 * @param sleMoneyUnit
	 *            --��λ
	 * @param request
	 *            --��ҳ����
	 * @return List
	 * @throws ITFEBizException
	 */
	public List makePayoutBill(IDto idto, String sleBillType,
			String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType,
			String sleSubjectAttribute, String smoveflag, String sleMoneyUnit)
			throws ITFEBizException {
		try {
			String sql = makePayoutReportSQL(idto, sleBillType, sleTenrptType,
					sleQuarterrptType, sleHalfyearrptType, sleSubjectType,
					sleSubjectAttribute, smoveflag, sleMoneyUnit);

			// ��ѯ����SQL
			String querysql = sql.split("#")[0];

			// ��ӡsql
			log.debug(querysql);

			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			List querylist = (List) exec.runQueryCloseCon(querysql.toString(),
					IncomeMoneyReportDto.class, true).getDtoCollection();

			return querylist;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}

	/**
	 * ���������ձ�
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --��������
	 * @param sleTenrptType
	 *            --Ѯ������
	 * @param sleQuarterrptType
	 *            --��������
	 * @param sleHalfyearrptType
	 *            --���걨����
	 * @param sleSubjectType
	 *            --��Ŀ����
	 * @param sleSubjectAttribute
	 *            --��Ŀ����
	 * @param smoveflag
	 *            --������־
	 * @param sleMoneyUnit
	 *            --��λ
	 * @param request
	 *            --��ҳ����
	 * @return PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse makeIncomeReport(IDto idto, String sleBillType,
			String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType,
			String sleSubjectAttribute, String smoveflag, String sleMoneyUnit,
			PageRequest request) throws ITFEBizException {
		try {
			String sql = makeIncomeReportSQL(idto, sleBillType, sleTenrptType,
					sleQuarterrptType, sleHalfyearrptType, sleSubjectType,
					sleSubjectAttribute, smoveflag, sleMoneyUnit);

			// ��ѯ����SQL
			String querysql = sql.split("#")[0];

			// ��װ����SQL
			String innersql = sql.split("#")[1];

			// ��ӡsql
			log.debug(querysql);

			PageResponse response = new PageResponse(request);
			// ��ҳ��ѯ
			SQLExecutor sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			if (response.getTotalCount() == 0) {
				SQLExecutor sqlExe1 = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				// ��ҳ����
				String countsql = " select count(*)  from ( " + innersql
						+ " ) with ur ";

				int count = sqlExe1.runQueryCloseCon(countsql).getInt(0, 0);
				response.setTotalCount(count);
			}

			SQLResults res = sqlExe.runQueryCloseCon(querysql,
					IncomeMoneyReportDto.class, request.getStartPosition(),
					request.getPageSize(), false);
			List responselist = new ArrayList();
			responselist.addAll(res.getDtoCollection());

			response.getData().clear();
			response.setData(responselist);
			return response;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}

	/**
	 * ���������ַ�������ȡ������С����
	 * 
	 * @param rptdate
	 * @return
	 * @throws ITFEBizException
	 */
	public String getRptDate(String rptdate) throws ITFEBizException {
		String rptDate = "";
		String rptDateBeforeyear = "";
		SQLExecutor execute1 = null;
		try {
			String monthsql = "SELECT max(S_RPTDATE) FROM TR_INCOMEDAYRPT WHERE substr(S_RPTDATE ,1,6) = '"
					+ rptdate + "'";
			if (rptdate.length() == 4) {
				monthsql = "SELECT min(S_RPTDATE) FROM TR_INCOMEDAYRPT WHERE substr(S_RPTDATE ,1,4) = '"
						+ rptdate + "'";
			}

			// ����ȷ������ѯ�����ڵ�ǰ��������ʷ��
			execute1 = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();

			String trdate = execute1.runQuery(monthsql).getString(0, 0);

			if (null != trdate && !trdate.equals("")) {
				rptDate = trdate;// ��������
				rptDateBeforeyear = ((Integer.parseInt(trdate) - 10000) + "")
						.trim();// ��һ�걨������
			} else {
				String htrdate = execute1
						.runQuery(
								monthsql.replace("TR_INCOMEDAYRPT",
										"HTR_INCOMEDAYRPT")).getString(0, 0);
				if (null != htrdate && !htrdate.equals("")) {
					rptDate = htrdate;// ��������
					rptDateBeforeyear = ((Integer.parseInt(htrdate) - 10000) + "")
							.trim();// ��һ�걨������
				}
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�������ڳ���", e);
		}finally{
			if(null!=execute1){
				execute1.closeConnection();
			}
		}
		return rptDate + "=" + rptDateBeforeyear;
	}

	/**
	 * �������뱨���ѯ����
	 * 
	 * @param idto
	 * @param sleSubjectType
	 * @param smoveflag
	 * @param sbillkind
	 * @param srptdate
	 * @return
	 * @throws ITFEBizException
	 */
	private String makeIncomeReportwhere(IDto idto, String sleSubjectType,
			String smoveflag, String sbillkind, String srptdate)
			throws ITFEBizException {

		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;

		/**
		 * �������ڣ������������� �տ������룺�������
		 * ��Ŀ���ͣ�01һ��Ԥ����02һ��Ԥ����03Ԥ���ڻ���04Ԥ����ծ��05Ԥ�������06����07ת��������---sleSubjectType
		 * Ԥ�����ࣺ 1Ԥ����,2Ԥ���� Ԥ�㼶�Σ� 1 ����,2 ʡ,3 ��,4 ��,5 ��,6�ط� Ͻ����־�� 0 ȫϽ,1����,2ȫϽ�Ǳ���
		 * ������־: 0 �ǵ���,1 ����, 2 ������ �����ڱ�־��0������1������
		 */
		// ��ѯsql ������룬Ͻ����־�����ջ��ش��룬��ѯ���ڣ�Ԥ�����࣬Ԥ�㼶�Σ�������, ��������, ��Ŀ���� ,������־
		String sqlwhere = "";

		if (null == dto.getStrecode() || "".equals(dto.getStrecode().trim())) {
			throw new ITFEBizException("��ѯ������������벻��Ϊ�գ�");
		}
		if (null == dto.getSbelongflag()
				|| "".equals(dto.getSbelongflag().trim())) {
			throw new ITFEBizException("��ѯ������Ͻ����־����Ϊ�գ�");
		}

		/**
		 * Ͻ����־+����
		 */
		sqlwhere += " and a.s_trecode ='" + dto.getStrecode().trim() + "' "
				+ " and a.S_BELONGFLAG ='" + dto.getSbelongflag().trim() + "' ";

		/**
		 * ��������
		 */
		if (null != dto.getSrptdate() && !"".equals(dto.getSrptdate().trim())) {
			sqlwhere += " and a.S_RPTDATE ='" + srptdate + "' ";
		} else {
			throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
		}

		/**
		 * Ԥ������
		 */
		if (null != dto.getSbudgettype()
				&& !"".equals(dto.getSbudgettype().trim())) {
			sqlwhere += " and a.s_BudgetType ='" + dto.getSbudgettype().trim()
					+ "' ";
		} else {
			throw new ITFEBizException("��ѯ������Ԥ�����಻��Ϊ�գ�");
		}

		/**
		 * Ԥ�㼶�������ж� ��Ԥ�㼶��Ϊ���ط���ʱ����ѯ���м�+������������ ��Ԥ�㼶��Ϊ�������֡�ʱ����ѯ�����뼶+�м�+������������
		 */
		if (null != dto.getSbudgetlevelcode()
				&& !"".equals(dto.getSbudgetlevelcode().trim())) {

			if (StateConstant.SUBJECTLEVEL_LOCAL.equals(dto
					.getSbudgetlevelcode().trim())) {// �ط�
				sqlwhere += " and a.S_BUDGETLEVELCODE in ('"
						+ StateConstant.SUBJECTLEVEL_CITY + "' ,'"
						+ StateConstant.SUBJECTLEVEL_AREA + "') ";
			} else if (StateConstant.SUBJECTLEVEL_ANY.equals(dto
					.getSbudgetlevelcode().trim())) {// �����ּ���
				sqlwhere += " and a.S_BUDGETLEVELCODE in ('"
						+ StateConstant.SUBJECTLEVEL_CENTER + "' ,'"
						+ StateConstant.SUBJECTLEVEL_CITY + "' ,'"
						+ StateConstant.SUBJECTLEVEL_AREA + "') ";
			} else {// ����
				sqlwhere += " and a.S_BUDGETLEVELCODE ='"
						+ dto.getSbudgetlevelcode().trim() + "' ";
			}
		} else {
			throw new ITFEBizException("��ѯ������Ԥ�㼶�β���Ϊ�գ�");
		}

		/**
		 * �����ڱ�־
		 */
		if (null != dto.getStrimflag() && !"".equals(dto.getStrimflag().trim())) {
			sqlwhere += " and a.s_TrimFlag ='" + dto.getStrimflag().trim()
					+ "' ";
			// ������
			if (dto.getStrimflag().equals(MsgConstant.TIME_FLAG_TRIM)) {
				sbillkind = StateConstant.REPORTTYPE_FLAG_TRBUDGETBILL;
			}
		} else {
			throw new ITFEBizException("��ѯ�����������ڱ�־����Ϊ�գ�");
		}

		/**
		 * ��������
		 */
		if (null != sbillkind && !"".equals(sbillkind.trim())) {
			sqlwhere += " and a.S_BILLKIND ='" + sbillkind + "' ";
		}

		/**
		 * ��Ŀ���� sleSubjectType
		 */
		if (null != sleSubjectType && !"".equals(sleSubjectType.trim())) {
			sqlwhere += " and b.S_SUBJECTTYPE ='" + sleSubjectType + "' ";
		}

		/**
		 * ������־ S_MOVEFLAG
		 */
		if (null != smoveflag && !"".equals(smoveflag.trim())) {
			sqlwhere += " and b.S_MOVEFLAG ='" + smoveflag + "' ";
		}

		/**
		 * ���ڸ��������嵥��һ��Ԥ���Ŀ���룬��˲�ѯ��ʱ��Ӧ���Ͽ�Ŀ����ĺ�����������
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_ORGCODE FROM TS_TREASURY WHERE S_TRECODE='"
				+ dto.getStrecode().trim() + "' ) ";

		return sqlwhere;
	}

	/**
	 * ����֧���ձ�
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --��������
	 * @param sleTenrptType
	 *            --Ѯ������
	 * @param sleQuarterrptType
	 *            --��������
	 * @param sleHalfyearrptType
	 *            --���걨����
	 * @param sleSubjectType
	 *            --��Ŀ����
	 * @param sleSubjectAttribute
	 *            --��Ŀ����
	 * @param smoveflag
	 *            --������־
	 * @param sleMoneyUnit
	 *            --��λ
	 * @param request
	 *            --��ҳ����
	 * @return PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse makePayoutReport(IDto idto, String sleBillType,
			String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType,
			String sleSubjectAttribute, String smoveflag, String sleMoneyUnit,
			PageRequest request) throws ITFEBizException {
		try {
			String sql = makePayoutReportSQL(idto, sleBillType, sleTenrptType,
					sleQuarterrptType, sleHalfyearrptType, sleSubjectType,
					sleSubjectAttribute, smoveflag, sleMoneyUnit);

			// ��ѯ����SQL
			String querysql = sql.split("#")[0];
			// ��װ����SQl
			String innersql = sql.split("#")[1];

			// ��ӡsql
			log.debug(querysql);

			PageResponse response = new PageResponse(request);
			// ��ҳ��ѯ
			SQLExecutor sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			if (response.getTotalCount() == 0) {
				SQLExecutor sqlExe1 = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				// ��ҳ����
				String countsql = " select count(*)  from ( " + innersql
						+ " ) with ur ";

				int count = sqlExe1.runQueryCloseCon(countsql).getInt(0, 0);
				response.setTotalCount(count);
			}

			SQLResults res = sqlExe.runQueryCloseCon(querysql,
					IncomeMoneyReportDto.class, request.getStartPosition(),
					request.getPageSize(), false);
			List responselist = new ArrayList();
			responselist.addAll(res.getDtoCollection());

			response.getData().clear();
			response.setData(responselist);
			return response;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}

	}

	/**
	 * ����֧�������ѯ����
	 * 
	 * @param idto
	 * @param sleSubjectType
	 * @param smoveflag
	 * @param rptDate
	 * @return
	 * @throws ITFEBizException
	 */
	private String makePayoutReportwhere(IDto idto, String sleSubjectType,
			String smoveflag, String rptDate) throws ITFEBizException {

		TrTaxorgPayoutReportDto dto = (TrTaxorgPayoutReportDto) idto;
		String sqlwhere = "";

		/**
		 * �������
		 */
		if (null != dto.getStrecode() && !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='" + dto.getStrecode().trim() + "' ";
		} else {
			throw new ITFEBizException("��ѯ������������벻��Ϊ�գ�");
		}

		/**
		 * ��������
		 */
		if (null != dto.getSrptdate() && !"".equals(dto.getSrptdate().trim())) {
			sqlwhere += " and a.S_RPTDATE ='" + rptDate.trim() + "' ";
		} else {
			throw new ITFEBizException("��ѯ�������������ڲ���Ϊ�գ�");
		}

		/**
		 * Ԥ������
		 */
		if (null != dto.getSbudgettype()
				&& !"".equals(dto.getSbudgettype().trim())) {
			sqlwhere += " and a.s_BudgetType ='" + dto.getSbudgettype().trim()
					+ "' ";
		} else {
			throw new ITFEBizException("��ѯ������Ԥ�����಻��Ϊ�գ�");
		}

		/**
		 * Ԥ�㼶�������ж� ��Ԥ�㼶��Ϊ���ط���ʱ����ѯ���м�+������������ ��Ԥ�㼶��Ϊ�������֡�ʱ����ѯ�����뼶+�м�+������������
		 */
		if (null != dto.getSbudgetlevelcode()
				&& !"".equals(dto.getSbudgetlevelcode().trim())) {

			if (StateConstant.SUBJECTLEVEL_LOCAL.equals(dto
					.getSbudgetlevelcode().trim())) {// �ط�
				sqlwhere += " and a.S_BUDGETLEVELCODE in ('"
						+ StateConstant.SUBJECTLEVEL_CITY + "' ,'"
						+ StateConstant.SUBJECTLEVEL_AREA + "') ";
			} else if (StateConstant.SUBJECTLEVEL_ANY.equals(dto
					.getSbudgetlevelcode().trim())) {// �����ּ���
				sqlwhere += " and a.S_BUDGETLEVELCODE in ('"
						+ StateConstant.SUBJECTLEVEL_CENTER + "' ,'"
						+ StateConstant.SUBJECTLEVEL_CITY + "' ,'"
						+ StateConstant.SUBJECTLEVEL_AREA + "') ";
			} else {// ����
				sqlwhere += " and a.S_BUDGETLEVELCODE ='"
						+ dto.getSbudgetlevelcode().trim() + "' ";
			}
		} else {
			throw new ITFEBizException("��ѯ������Ԥ�㼶�β���Ϊ�գ�");
		}

		/**
		 * ��Ŀ���� sleSubjectType
		 */
		if (null != sleSubjectType && !"".equals(sleSubjectType.trim())) {
			sqlwhere += " and b.S_SUBJECTTYPE ='" + sleSubjectType + "' ";
		}

		/**
		 * ������־ S_MOVEFLAG
		 */
		if (null != smoveflag && !"".equals(smoveflag.trim())) {
			sqlwhere += " and b.S_MOVEFLAG ='" + smoveflag + "' ";
		}

		/**
		 * ���ڸ��������嵥��һ��Ԥ���Ŀ���룬��˲�ѯ��ʱ��Ӧ���Ͽ�Ŀ����ĺ�����������
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_ORGCODE FROM TS_TREASURY WHERE S_TRECODE='"
				+ dto.getStrecode().trim() + "' ) ";

		return sqlwhere;
	}

	/**
	 * ���������ձ�
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --��������
	 * @param sleTenrptType
	 *            --Ѯ������
	 * @param sleQuarterrptType
	 *            --��������
	 * @param sleHalfyearrptType
	 *            --���걨����
	 * @param sleSubjectType
	 *            --��Ŀ����
	 * @param sleSubjectAttribute
	 *            --��Ŀ����
	 * @param smoveflag
	 *            --������־
	 * @param sleMoneyUnit
	 *            --��λ
	 * @return String
	 * @throws ITFEBizException
	 */
	public String makeIncomeReportSQL(IDto idto, String sleBillType,
			String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType,
			String sleSubjectAttribute, String smoveflag, String sleMoneyUnit)
			throws ITFEBizException {
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;

		String temprptDate = dto.getSrptdate();// ��������
		String temprptDateBeforeyear = ((Integer.parseInt(dto.getSrptdate()) - 10000) + "")
				.trim();// ��һ�걨������

		String rptDate = "";// �����ı�������
		String rptDateBeforeyear = "";// ��������һ�걨������

		String prorptDate = "";// �����ı�������(Ѯ���������걨)
		if (sleBillType.equals(StateConstant.REPORT_DAY)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// �ձ��걨
			rptDate = temprptDate;// ��������
			rptDateBeforeyear = temprptDateBeforeyear;// ��һ�걨������
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��

			if (sleTenrptType.equals(StateConstant.TENRPT_TOP)) {// ��Ѯ
				rptDate = temprptDate.substring(0, 6) + "10";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();
			} else if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)) {// ��Ѯ
				rptDate = temprptDate.substring(0, 6) + "20";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();

				prorptDate = temprptDate.substring(0, 6) + "10";

			} else if (sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ
				String allrptdate = getRptDate(temprptDate.substring(0, 6));
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				prorptDate = temprptDate.substring(0, 6) + "20";
			}
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {// �±�

			String allrptdate = getRptDate(temprptDate.substring(0, 6));
			if (allrptdate.equals("=")) {
				throw new ITFEBizException("����ѯ�������ڣ�");
			}
			rptDate = allrptdate.split("=")[0];
			rptDateBeforeyear = allrptdate.split("=")[1];
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FIRST)) {// ��һ����
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "03");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];
			} else if (sleQuarterrptType
					.equals(StateConstant.QUARTERRPT_SECOND)) {// �ڶ�����
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "03");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)) {// ��������
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "09");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// ���ļ���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "09");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_TOP)) {// �ϰ���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

			} else if (sleHalfyearrptType
					.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			}
		}

		String crrentsql = ""; // ��ǰ�����������뱨��SQL
		String crrentprosql = ""; // ��ǰ�����������뱨��SQL(����Ѯ���������걨)
		String beforesql = ""; // ��һ��ͬ�ڱ����������뱨��SQL
		String beforeprosql = ""; // ��һ��ͬ�ڱ����������뱨��SQL(����Ѯ���������걨)
		String trincomesql = " SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDate + "'";// ���ݱ������ڲ�ѯ��ǰ��
		String htrincomesql = " SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDate + "'";// ���ݱ������ڲ�ѯ��ʷ��
		String incomesql = trincomesql + " union all " + htrincomesql;// �����ձ���ѯSQL
		String htrincomesqlbeforeyear = " SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDateBeforeyear + "'";// ������һ�걨�����ڲ�ѯ��ʷ��
		String incomeprosql = "";

		String currentprosqlwhere = "";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��

			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ��Ѯ
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ����������������
				currentprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, prorptDate);
			}
		}

		if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// �ڶ����ȵ������ȵ��ļ���
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ����������������
				currentprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, prorptDate);
			}
		}
		if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ����������������
				currentprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, prorptDate);
			}
		}

		// ������ѯSQL
		StringBuffer sqlbuf = new StringBuffer();

		// ��ǰ����������������
		String currentsqlwhere = makeIncomeReportwhere(idto, sleSubjectType,
				smoveflag, StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, rptDate);
		// ��һ�걨��������������
		String lastyearsqlwhere = makeIncomeReportwhere(idto, sleSubjectType,
				smoveflag, StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL,
				rptDateBeforeyear);

		// ��Ԫ
		String yiyuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT)/100000000 as nmoneycurrent,SUM(N_MONEYYEAR)/100000000 as nmoneyyear ,sum(N_MONEYINCREMENTAL)/100000000 as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END)  ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";
		// ��Ԫ
		String wanyuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT)/10000 as nmoneycurrent,SUM(N_MONEYYEAR)/10000 as nmoneyyear ,sum(N_MONEYINCREMENTAL)/10000 as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END) ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";
		// Ԫ
		String yuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT) as nmoneycurrent,SUM(N_MONEYYEAR) as nmoneyyear ,sum(N_MONEYINCREMENTAL) as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END) ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";

		// �Բ�ͬ�Ľ�λ�����жϣ�Ȼ���ͳ�ƽ����������
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// ��Ԫ
			sqlbuf.append(yiyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// ��Ԫ
			sqlbuf.append(wanyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// Ԫ
			sqlbuf.append(yuansql);
		}

		// �ڲ���ѯSQL
		StringBuffer innersqlbuf = new StringBuffer();

		innersqlbuf
				.append(" select S_BUDGETSUBCODE AS S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYDAY) AS N_MONEYCURRENT, sum(N_MONEYALLYEAR) AS N_MONEYYEAR, sum(N_MONEYINCREMENTAL) AS N_MONEYINCREMENTAL,sum(D_MONEYGROWTHRATE) AS D_MONEYGROWTHRATE from (");

		if (null == sleSubjectAttribute
				|| "".equals(sleSubjectAttribute.trim())) {
			throw new ITFEBizException("��ѯ��������Ŀ���Բ���Ϊ�գ�");
		}

		// ���ݿ�Ŀ���ԣ��ࡢ��Ŀ��ƴװ��ͬ��SQL
		if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_CLASS)) {// ��

			// ��ǰ�����������뱨��SQL
			crrentsql += "select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR  ,sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomesql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
					+ currentsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME "
					+ " union all ";

			crrentprosql += "select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,0 as N_MONEYALLYEAR  ,0 as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomeprosql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
					+ currentprosqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME ";

			// ��һ��ͬ�ڱ����������뱨��SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_PAGE)) {// ��

			// ��ǰ�����������뱨��SQL
			crrentsql += "select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR  ,sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomesql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ currentsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME "
					+ " union all ";

			crrentprosql += "select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,0 as N_MONEYALLYEAR  ,0 as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomeprosql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ currentprosqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME ";

			// ��һ��ͬ�ڱ����������뱨��SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute
				.equals(StateConstant.SUBJECTATTR_PROJECT)) {// ��

			// ��ǰ�����������뱨��SQL
			crrentsql += "select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR  ,sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomesql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
					+ currentsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME "
					+ " union all ";

			crrentprosql += "select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,0 as N_MONEYALLYEAR  ,0 as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomeprosql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
					+ currentprosqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME ";

			// ��һ��ͬ�ڱ����������뱨��SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_ITEM)) {// Ŀ

			// ��ǰ�����������뱨��SQL
			crrentsql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR  ,sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomesql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ currentsqlwhere
					+ " group by S_BUDGETSUBCODE,S_SUBJECTNAME "
					+ " union all ";

			crrentprosql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,0 as N_MONEYALLYEAR  ,0 as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomeprosql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ currentprosqlwhere
					+ " group by S_BUDGETSUBCODE,S_SUBJECTNAME ";

			// ��һ��ͬ�ڱ����������뱨��SQL
			beforesql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " group by S_BUDGETSUBCODE,S_SUBJECTNAME ";

		}

		String innerprosql = " select S_BUDGETSUBCODE AS S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYDAY) AS N_MONEYCURRENT, sum(N_MONEYALLYEAR) AS N_MONEYALLYEAR, sum(N_MONEYINCREMENTAL) AS N_MONEYINCREMENTAL,sum(D_MONEYGROWTHRATE) AS D_MONEYGROWTHRATE from (";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ��Ѯ
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYMONTH");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// ����ǰ��������SQL����һ��ͬ�ڵ�SQLƴ��һ��������ͳ��
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// �ڶ����ȵ������ȵ��ļ���
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYYEAR");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// ����ǰ��������SQL����һ��ͬ�ڵ�SQLƴ��һ��������ͳ��
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYYEAR");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// ����ǰ��������SQL����һ��ͬ�ڵ�SQLƴ��һ��������ͳ��
			}
		} else {
			innersqlbuf.append(crrentsql).append(beforesql);// ����ǰ��������SQL����һ��ͬ�ڵ�SQLƴ��һ��������ͳ��
		}

		// �ڲ�SQL�������
		innersqlbuf.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String innersql = innersqlbuf.toString();

		// ����SQL�������
		sqlbuf.append(innersql).append(
				" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String sql = sqlbuf.toString();

		String querysql = "";// �Բ�ͬ�ı����������ò�ͬ�Ĳ�ѯSQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// �ձ�
			querysql = sql;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// Ѯ�����±�
			innersql = innersql.replace("N_MONEYDAY", "N_MONEYMONTH");
			querysql = sql.replace("N_MONEYDAY", "N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// ���������걨���걨
			innersql = innersql.replace("N_MONEYDAY", "N_MONEYYEAR");
			querysql = sql.replace("N_MONEYDAY", "N_MONEYYEAR");
		}

		return querysql + "#" + innersql;

	}

	/**
	 * ����֧���ձ���ѯ����SQL
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --��������
	 * @param sleTenrptType
	 *            --Ѯ������
	 * @param sleQuarterrptType
	 *            --��������
	 * @param sleHalfyearrptType
	 *            --���걨����
	 * @param sleSubjectType
	 *            --��Ŀ����
	 * @param sleSubjectAttribute
	 *            --��Ŀ����
	 * @param smoveflag
	 *            --������־
	 * @param sleMoneyUnit
	 *            --��λ
	 * @return String
	 * @throws ITFEBizException
	 */
	public String makePayoutReportSQL(IDto idto, String sleBillType,
			String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType,
			String sleSubjectAttribute, String smoveflag, String sleMoneyUnit)
			throws ITFEBizException {

		TrTaxorgPayoutReportDto dto = (TrTaxorgPayoutReportDto) idto;
		String temprptDate = dto.getSrptdate();// ��������
		String temprptDateBeforeyear = ((Integer.parseInt(dto.getSrptdate()) - 10000) + "")
				.trim();// ��һ�걨������

		String rptDate = "";// �����ı�������
		String rptDateBeforeyear = "";// ��������һ�걨������

		String prorptDate = "";// �����ı�������(Ѯ���������걨)
		if (sleBillType.equals(StateConstant.REPORT_DAY)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// �ձ��걨
			rptDate = temprptDate;// ��������
			rptDateBeforeyear = temprptDateBeforeyear;// ��һ�걨������
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��

			if (sleTenrptType.equals(StateConstant.TENRPT_TOP)) {// ��Ѯ
				rptDate = temprptDate.substring(0, 6) + "10";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();
			} else if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)) {// ��Ѯ
				rptDate = temprptDate.substring(0, 6) + "20";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();

				prorptDate = temprptDate.substring(0, 6) + "10";

			} else if (sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ
				String allrptdate = getRptDate(temprptDate.substring(0, 6));
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				prorptDate = temprptDate.substring(0, 6) + "20";
			}
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {// �±�

			String allrptdate = getRptDate(temprptDate.substring(0, 6));
			if (allrptdate.equals("=")) {
				throw new ITFEBizException("����ѯ�������ڣ�");
			}
			rptDate = allrptdate.split("=")[0];
			rptDateBeforeyear = allrptdate.split("=")[1];
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FIRST)) {// ��һ����
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "03");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];
			} else if (sleQuarterrptType
					.equals(StateConstant.QUARTERRPT_SECOND)) {// �ڶ�����
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "03");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)) {// ��������
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "09");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// ���ļ���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "09");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_TOP)) {// �ϰ���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

			} else if (sleHalfyearrptType
					.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			}
		}

		String crrentsql = ""; // ��ǰ�������ڱ���SQL
		String crrentprosql = ""; // ��ǰ�������ڱ���SQL(����Ѯ���������걨)
		String beforesql = ""; // ��һ��ͬ�ڱ������ڱ���SQL
		String beforeprosql = ""; // ��һ��ͬ�ڱ������ڱ���SQL(����Ѯ���������걨)
		String trincomesql = " SELECT * FROM TR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDate + "'";// ���ݱ������ڲ�ѯ��ǰ��
		String htrincomesql = " SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDate + "'";// ���ݱ������ڲ�ѯ��ʷ��
		String incomesql = trincomesql + " union all " + htrincomesql;// ֧���ձ���ѯSQL
		String htrincomesqlbeforeyear = " SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDateBeforeyear + "'";// ������һ�걨�����ڲ�ѯ��ʷ��
		String incomeprosql = "";

		String currentprosqlwhere = "";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��

			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ��Ѯ
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ��������֧������
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);
			}
		}

		if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// �ڶ����ȵ������ȵ��ļ���
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ��������֧������
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);
			}
		}
		if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ��������֧������
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);
			}
		}

		// ������ѯSQL
		StringBuffer sqlbuf = new StringBuffer();

		// ��ǰ��������֧������
		String currentsqlwhere = makePayoutReportwhere(idto, sleSubjectType,
				smoveflag, rptDate);
		// ��һ�걨������֧������
		String lastyearsqlwhere = makePayoutReportwhere(idto, sleSubjectType,
				smoveflag, rptDateBeforeyear);

		// ��Ԫ
		String yiyuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT)/100000000 as nmoneycurrent,SUM(N_MONEYYEAR)/100000000 as nmoneyyear ,sum(N_MONEYINCREMENTAL)/100000000 as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END) ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";
		// ��Ԫ
		String wanyuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT)/10000 as nmoneycurrent,SUM(N_MONEYYEAR)/10000 as nmoneyyear ,sum(N_MONEYINCREMENTAL)/10000 as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END) ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";
		// Ԫ
		String yuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT) as nmoneycurrent,SUM(N_MONEYYEAR) as nmoneyyear ,sum(N_MONEYINCREMENTAL) as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END) ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";

		// �Բ�ͬ�Ľ�λ�����жϣ�Ȼ���ͳ�ƽ����������
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// ��Ԫ
			sqlbuf.append(yiyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// ��Ԫ
			sqlbuf.append(wanyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// Ԫ
			sqlbuf.append(yuansql);
		}

		// �ڲ���ѯSQL
		StringBuffer innersqlbuf = new StringBuffer();

		innersqlbuf
				.append(" select S_BUDGETSUBCODE AS S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYDAY) AS N_MONEYCURRENT, sum(N_MONEYALLYEAR) AS N_MONEYYEAR, sum(N_MONEYINCREMENTAL) AS N_MONEYINCREMENTAL,sum(D_MONEYGROWTHRATE) AS D_MONEYGROWTHRATE from (");

		if (null == sleSubjectAttribute
				|| "".equals(sleSubjectAttribute.trim())) {
			throw new ITFEBizException("��ѯ��������Ŀ���Բ���Ϊ�գ�");
		}

		// ���ݿ�Ŀ���ԣ��ࡢ��Ŀ��ƴװ��ͬ��SQL
		if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_CLASS)) {// ��

			// ��ǰ��������֧������SQL
			crrentsql += "select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR  ,sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomesql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
					+ currentsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME "
					+ " union all ";

			crrentprosql += "select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,0 as N_MONEYALLYEAR  ,0 as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomeprosql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
					+ currentprosqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME ";

			// ��һ��ͬ�ڱ�������֧������SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_PAGE)) {// ��

			// ��ǰ��������֧������SQL
			crrentsql += "select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR  ,sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomesql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ currentsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME "
					+ " union all ";

			crrentprosql += "select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,0 as N_MONEYALLYEAR  ,0 as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomeprosql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ currentprosqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME ";

			// ��һ��ͬ�ڱ�������֧������SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute
				.equals(StateConstant.SUBJECTATTR_PROJECT)) {// ��

			// ��ǰ��������֧������SQL
			crrentsql += "select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR  ,sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomesql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
					+ currentsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME "
					+ " union all ";

			crrentprosql += "select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,0 as N_MONEYALLYEAR  ,0 as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomeprosql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
					+ currentprosqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME ";

			// ��һ��ͬ�ڱ�������֧������SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_ITEM)) {// Ŀ

			// ��ǰ��������֧������SQL
			crrentsql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYDAY,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR  ,sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomesql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ currentsqlwhere 
					+ " group by S_BUDGETSUBCODE,S_SUBJECTNAME "
					+ " union all ";

			crrentprosql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYDAY,0 as N_MONEYALLYEAR  ,0 as N_MONEYINCREMENTAL ,0 as D_MONEYGROWTHRATE from ("
					+ incomeprosql
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ currentprosqlwhere
					+ " group by S_BUDGETSUBCODE,S_SUBJECTNAME ";

			// ��һ��ͬ�ڱ�������֧������SQL
			beforesql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " group by S_BUDGETSUBCODE,S_SUBJECTNAME ";

		}

		String innerprosql = " select S_BUDGETSUBCODE AS S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYDAY) AS N_MONEYCURRENT, sum(N_MONEYALLYEAR) AS N_MONEYALLYEAR, sum(N_MONEYINCREMENTAL) AS N_MONEYINCREMENTAL,sum(D_MONEYGROWTHRATE) AS D_MONEYGROWTHRATE from (";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ��Ѯ
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYMONTH");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// ����ǰ��������SQL����һ��ͬ�ڵ�SQLƴ��һ��������ͳ��
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// �ڶ����ȵ������ȵ��ļ���
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYYEAR");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// ����ǰ��������SQL����һ��ͬ�ڵ�SQLƴ��һ��������ͳ��
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYYEAR");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// ����ǰ��������SQL����һ��ͬ�ڵ�SQLƴ��һ��������ͳ��
			}
		} else {
			innersqlbuf.append(crrentsql).append(beforesql);// ����ǰ��������SQL����һ��ͬ�ڵ�SQLƴ��һ��������ͳ��
		}

		// �ڲ�SQL�������
		innersqlbuf.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String innersql = innersqlbuf.toString();

		// ����SQL�������
		sqlbuf.append(innersql).append(
				" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String sql = sqlbuf.toString();

		String querysql = "";// �Բ�ͬ�ı����������ò�ͬ�Ĳ�ѯSQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// �ձ�
			querysql = sql;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// Ѯ�����±�
			innersql = innersql.replace("N_MONEYDAY", "N_MONEYMONTH");
			querysql = sql.replace("N_MONEYDAY", "N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// ���������걨���걨
			innersql = innersql.replace("N_MONEYDAY", "N_MONEYYEAR");
			querysql = sql.replace("N_MONEYDAY", "N_MONEYYEAR");
		}

		return querysql + "#" + innersql;

	}

	/**
	 * ���������ձ�
	 * 
	 * @generated
	 * @param idto
	 * @param sbudgetsubcode
	 *            --Ԥ���Ŀ����(����Զ��ŷָ�)
	 * @param sleBillType
	 *            --��������
	 * @param sleTenrptType
	 *            --Ѯ������
	 * @param sleQuarterrptType
	 *            --��������
	 * @param sleHalfyearrptType
	 *            --���걨����
	 * @param sleSubjectType
	 *            --��Ŀ����
	 * @param smoveflag
	 *            --������־
	 * @param sleMoneyUnit
	 *            --��λ
	 * @return String
	 * @throws ITFEBizExceptionn
	 */
	public List makeIncomeReportBySubject(IDto idto, String sbudgetsubcode,
			String sleBillType, String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType, String smoveflag,
			String sleMoneyUnit) throws ITFEBizException {
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;

		String temprptDate = dto.getSrptdate();// ��������
		String temprptDateBeforeyear = ((Integer.parseInt(dto.getSrptdate()) - 10000) + "")
				.trim();// ��һ�걨������

		String rptDate = "";// �����ı�������
		String rptDateBeforeyear = "";// ��������һ�걨������

		String prorptDate = "";// �����ı�������(Ѯ���������걨)
		String prorptDateBeforeyear = "";// ��������һ�걨������(Ѯ���������걨)
		if (sleBillType.equals(StateConstant.REPORT_DAY)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// �ձ��걨
			rptDate = temprptDate;// ��������
			rptDateBeforeyear = temprptDateBeforeyear;// ��һ�걨������
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��

			if (sleTenrptType.equals(StateConstant.TENRPT_TOP)) {// ��Ѯ
				rptDate = temprptDate.substring(0, 6) + "10";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();
			} else if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)) {// ��Ѯ
				rptDate = temprptDate.substring(0, 6) + "20";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();

				prorptDate = temprptDate.substring(0, 6) + "10";
				prorptDateBeforeyear = temprptDateBeforeyear.substring(0, 6)
						+ "10";

			} else if (sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ
				String allrptdate = getRptDate(temprptDate.substring(0, 6));
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				prorptDate = temprptDate.substring(0, 6) + "20";
				prorptDateBeforeyear = temprptDateBeforeyear.substring(0, 6)
						+ "20";
			}
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {// �±�

			String allrptdate = getRptDate(temprptDate.substring(0, 6));
			if (allrptdate.equals("=")) {
				throw new ITFEBizException("����ѯ�������ڣ�");
			}
			rptDate = allrptdate.split("=")[0];
			rptDateBeforeyear = allrptdate.split("=")[1];
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FIRST)) {// ��һ����
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "03");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];
			} else if (sleQuarterrptType
					.equals(StateConstant.QUARTERRPT_SECOND)) {// �ڶ�����
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "03");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)) {// ��������
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "09");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// ���ļ���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "09");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_TOP)) {// �ϰ���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

			} else if (sleHalfyearrptType
					.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			}
		}

		String crrentsql = ""; // ��ǰ�����������뱨��SQL
		String crrentprosql = ""; // ��ǰ�����������뱨��SQL(����Ѯ���������걨)
		String beforesql = ""; // ��һ��ͬ�ڱ����������뱨��SQL
		String beforeprosql = ""; // ��һ��ͬ�ڱ����������뱨��SQL(����Ѯ���������걨)

		String crrentquerysql = ""; // ��ǰ�����������뱨��SQL
		String procrrentquerysql = ""; // ��ǰ�����������뱨��SQL(����Ѯ���������걨)
		String beforequerysql = ""; // ��һ��ͬ�ڱ����������뱨��SQL
		String probeforequerysql = ""; // ��һ��ͬ�ڱ����������뱨��SQL(����Ѯ���������걨)

		String trincomesql = " SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDate + "'";// ���ݱ������ڲ�ѯ��ǰ��
		String htrincomesql = " SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDate + "'";// ���ݱ������ڲ�ѯ��ʷ��
		String incomesql = trincomesql + " union all " + htrincomesql;// �����ձ���ѯSQL
		String htrincomesqlbeforeyear = " SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDateBeforeyear + "'";// ������һ�걨�����ڲ�ѯ��ʷ��
		String incomeprosql = "";
		String htrincomeprosqlbeforeyear = "";

		String currentprosqlwhere = "";
		String lastyearprosqlwhere = "";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��

			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ��Ѯ
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ����������������
				currentprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, prorptDate);

				htrincomeprosqlbeforeyear = htrincomesqlbeforeyear.replace(
						rptDateBeforeyear, prorptDateBeforeyear);
				lastyearprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL,
						prorptDateBeforeyear);

			}
		}

		if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// �ڶ����ȵ������ȵ��ļ���
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ����������������
				currentprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, prorptDate);

				htrincomeprosqlbeforeyear = htrincomesqlbeforeyear.replace(
						rptDateBeforeyear, prorptDateBeforeyear);
				lastyearprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL,
						prorptDateBeforeyear);

			}
		}
		if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ����������������
				currentprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, prorptDate);

				htrincomeprosqlbeforeyear = htrincomesqlbeforeyear.replace(
						rptDateBeforeyear, prorptDateBeforeyear);
				lastyearprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL,
						prorptDateBeforeyear);

			}
		}

		// ������ѯSQL
		StringBuffer sqlbuf = new StringBuffer();

		// ��ǰ����������������
		String currentsqlwhere = makeIncomeReportwhere(idto, sleSubjectType,
				smoveflag, StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, rptDate);
		// ��һ�걨��������������
		String lastyearsqlwhere = makeIncomeReportwhere(idto, sleSubjectType,
				smoveflag, StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL,
				rptDateBeforeyear);

		// ��Ԫ
		String yiyuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5)/100000000 as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5)/100000000 as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";
		// ��Ԫ
		String wanyuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5)/10000 as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5)/10000 as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";
		// Ԫ
		String yuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5) as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5) as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";

		// �Բ�ͬ�Ľ�λ�����жϣ�Ȼ���ͳ�ƽ����������
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// ��Ԫ
			sqlbuf.append(yiyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// ��Ԫ
			sqlbuf.append(wanyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// Ԫ
			sqlbuf.append(yuansql);
		}

		// �Զ��Ԥ���Ŀ�������SQLƴ��
		String subjectcodes = "";
		String[] str = sbudgetsubcode.split(",");
		for (int i = 0; i < str.length; i++) {
			if (i == str.length - 1) {
				subjectcodes += "'" + str[i] + "'";
				break;
			}
			subjectcodes += "'" + str[i] + "',";
		}

		// �ڲ���ѯSQL
		StringBuffer innersqlbuf = new StringBuffer();
		// �Զ��Ԥ���Ŀ���л���ͳ��
		StringBuffer currentbysubjectsbf = new StringBuffer();
		// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
		StringBuffer lastyearbysubjectsbf = new StringBuffer();

		String prooutersql = "select S_BUDGETSUBCODE as S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYCURRENT3) as N_MONEYCURRENT5,sum(N_MONEYCURRENT4) as N_MONEYCURRENT6,sum(D_MONEYGROWTH1) as D_MONEYGROWTH2,sum(D_MONEYCONTRIBUTION3) as D_MONEYCONTRIBUTION5,sum(D_MONEYCONTRIBUTION4) as D_MONEYCONTRIBUTION6,sum(D_MONEYRATIO3) as D_MONEYRATIO5,sum(D_MONEYRATIO4) as D_MONEYRATIO6,sum(N_MONEYALLYEAR3) as N_MONEYALLYEAR5  ,sum(N_MONEYALLYEAR4) as N_MONEYALLYEAR6  ,sum(D_MONEYYEARGROWTH1) as D_MONEYYEARGROWTH2 ,sum(D_MONEYYEARCONTRIBUTION3) as D_MONEYYEARCONTRIBUTION5,sum(D_MONEYYEARCONTRIBUTION4) as D_MONEYYEARCONTRIBUTION6,sum(D_MONEYYEARRATIO3) as D_MONEYYEARRATIO5,sum(D_MONEYYEARRATIO4) as D_MONEYYEARRATIO6 from (";

		String proinnersql = "select S_BUDGETSUBCODE as S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYCURRENT1) as N_MONEYCURRENT3,sum(N_MONEYCURRENT2) as N_MONEYCURRENT4,sum(D_MONEYGROWTH) as D_MONEYGROWTH1,sum(D_MONEYCONTRIBUTION1) as D_MONEYCONTRIBUTION3,sum(D_MONEYCONTRIBUTION2) as D_MONEYCONTRIBUTION4,sum(D_MONEYRATIO1) as D_MONEYRATIO3,sum(D_MONEYRATIO2) as D_MONEYRATIO4,sum(N_MONEYALLYEAR1) as N_MONEYALLYEAR3  ,sum(N_MONEYALLYEAR2) as N_MONEYALLYEAR4  ,sum(D_MONEYYEARGROWTH) as D_MONEYYEARGROWTH1 ,sum(D_MONEYYEARCONTRIBUTION1) as D_MONEYYEARCONTRIBUTION3,sum(D_MONEYYEARCONTRIBUTION2) as D_MONEYYEARCONTRIBUTION4,sum(D_MONEYYEARRATIO1) as D_MONEYYEARRATIO3,sum(D_MONEYYEARRATIO2) as D_MONEYYEARRATIO4 from (";

		// ��ǰ�����������뱨��SQL(ռ��Ԥ���Ŀ)
		crrentquerysql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,0.00 as D_MONEYGROWTH,0.00 as D_MONEYCONTRIBUTION1,sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,sum(a.N_MONEYDAY) as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,sum(a.N_MONEYYEAR) as D_MONEYYEARCONTRIBUTION2, 0.00 as D_MONEYYEARRATIO1,sum(a.N_MONEYYEAR) as D_MONEYYEARRATIO2 from ("
				+ incomesql
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_INCOME
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ currentsqlwhere
				+ " and a.S_BUDGETSUBCODE = '"
				+ dto.getSbudgetsubcode()
				+ "' group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME"
				+ " union all ";

		procrrentquerysql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,0.00 as D_MONEYGROWTH,0.00 as D_MONEYCONTRIBUTION1,-sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,-sum(a.N_MONEYDAY) as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2, 0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ("
				+ incomeprosql
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_INCOME
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ currentprosqlwhere
				+ " and a.S_BUDGETSUBCODE = '"
				+ dto.getSbudgetsubcode()
				+ "' group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		// ��һ��ͬ�ڱ����������뱨��SQL(ռ��Ԥ���Ŀ)
		beforequerysql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,0.00 as D_MONEYGROWTH,0.00 as D_MONEYCONTRIBUTION1,-sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,-sum(a.N_MONEYYEAR) as D_MONEYYEARCONTRIBUTION2,0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ( "
				+ htrincomesqlbeforeyear
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_INCOME
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ lastyearsqlwhere
				+ " and a.S_BUDGETSUBCODE = '"
				+ dto.getSbudgetsubcode()
				+ "' group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		probeforequerysql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,0.00 as D_MONEYGROWTH,0.00 as D_MONEYCONTRIBUTION1,sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2,0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ( "
				+ htrincomeprosqlbeforeyear
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_INCOME
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ lastyearprosqlwhere
				+ " and a.S_BUDGETSUBCODE = '"
				+ dto.getSbudgetsubcode()
				+ "' group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ��Ѯ
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(procrrentquerysql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(proinnersql)
						.append(beforequerysql)
						.append(" union all ")
						.append(probeforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			} else {
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// �ڶ����ȵ������ȵ��ļ���
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(procrrentquerysql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(proinnersql)
						.append(beforequerysql)
						.append(" union all ")
						.append(probeforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			} else {
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(procrrentquerysql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(proinnersql)
						.append(beforequerysql)
						.append(" union all ")
						.append(probeforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			} else {
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else {
			// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
			lastyearbysubjectsbf
					.append(prooutersql)
					.append(proinnersql)
					.append(crrentquerysql)
					.append(beforequerysql)
					.append(
							" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
		}

		String basesubjectsbf = lastyearbysubjectsbf.toString();

		String basesubjectquerysql = "";// �Բ�ͬ�ı����������ò�ͬ�Ĳ�ѯSQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// �ձ�
			basesubjectquerysql = basesubjectsbf;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// Ѯ�����±�
			basesubjectquerysql = basesubjectsbf.replace("N_MONEYDAY",
					"N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// ���������걨���걨
			basesubjectquerysql = basesubjectsbf.replace("N_MONEYDAY",
					"N_MONEYYEAR");
		}

		SQLExecutor execute = null;
		SQLResults res = null;
		try {
			execute = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			res = execute.runQueryCloseCon(basesubjectquerysql.toString());

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}

		String innerprosql = "";
		if (null != res && res.getRowCount() > 0) {
			innerprosql = "select S_BUDGETSUBCODE as S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYCURRENT1)+"
					+ res.getBigDecimal(0, 2)
					+ " as N_MONEYCURRENT3,sum(N_MONEYCURRENT2)+"
					+ res.getBigDecimal(0, 3)
					+ " as N_MONEYCURRENT4,sum(D_MONEYGROWTH)+"
					+ res.getBigDecimal(0, 4)
					+ " as D_MONEYGROWTH1,sum(D_MONEYCONTRIBUTION1)+"
					+ res.getBigDecimal(0, 5)
					+ " as D_MONEYCONTRIBUTION3,sum(D_MONEYCONTRIBUTION2)+"
					+ res.getBigDecimal(0, 6)
					+ " as D_MONEYCONTRIBUTION4,sum(D_MONEYRATIO1)+"
					+ res.getBigDecimal(0, 7)
					+ " as D_MONEYRATIO3,sum(D_MONEYRATIO2)+"
					+ res.getBigDecimal(0, 8)
					+ " as D_MONEYRATIO4,sum(N_MONEYALLYEAR1)+"
					+ res.getBigDecimal(0, 9)
					+ " as N_MONEYALLYEAR3  ,sum(N_MONEYALLYEAR2)+"
					+ res.getBigDecimal(0, 10)
					+ " as N_MONEYALLYEAR4  ,sum(D_MONEYYEARGROWTH)+"
					+ res.getBigDecimal(0, 11)
					+ " as D_MONEYYEARGROWTH1 ,sum(D_MONEYYEARCONTRIBUTION1)+"
					+ res.getBigDecimal(0, 12)
					+ " as D_MONEYYEARCONTRIBUTION3,sum(D_MONEYYEARCONTRIBUTION2)+"
					+ res.getBigDecimal(0, 13)
					+ " as D_MONEYYEARCONTRIBUTION4,sum(D_MONEYYEARRATIO1)+"
					+ res.getBigDecimal(0, 14)
					+ " as D_MONEYYEARRATIO3,sum(D_MONEYYEARRATIO2)+"
					+ res.getBigDecimal(0, 15) + " as D_MONEYYEARRATIO4 from (";
		} else {
			throw new ITFEBizException("����ѯ��ռ��Ԥ���Ŀ�������ڣ�");
		}

		// ��ǰ�����������뱨��SQL
		crrentsql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,sum(a.N_MONEYDAY) as D_MONEYGROWTH,sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION1,0.00 as D_MONEYCONTRIBUTION2,sum(a.N_MONEYDAY) as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,sum(a.N_MONEYYEAR) as D_MONEYYEARGROWTH ,sum(a.N_MONEYYEAR) as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2,sum(a.N_MONEYYEAR) as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ("
				+ incomesql
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_INCOME
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ currentsqlwhere
				+ " and a.S_BUDGETSUBCODE in ("
				+ subjectcodes
				+ " ) group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME"
				+ " union all ";

		crrentprosql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,-sum(a.N_MONEYDAY) as D_MONEYGROWTH,-sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION1,0.00 as D_MONEYCONTRIBUTION2,-sum(a.N_MONEYDAY) as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2,0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ("
				+ incomeprosql
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_INCOME
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ currentprosqlwhere
				+ " and a.S_BUDGETSUBCODE in ("
				+ subjectcodes
				+ " ) group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		// ��һ��ͬ�ڱ����������뱨��SQL
		beforesql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,sum(a.N_MONEYDAY) as N_MONEYCURRENT2,-sum(a.N_MONEYDAY) as D_MONEYGROWTH,-sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION1,0.00 as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR2,-sum(a.N_MONEYYEAR) as D_MONEYYEARGROWTH ,-sum(a.N_MONEYYEAR) as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2,0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ( "
				+ htrincomesqlbeforeyear
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_INCOME
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ lastyearsqlwhere
				+ " and a.S_BUDGETSUBCODE in ("
				+ subjectcodes
				+ " ) group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		beforeprosql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,-sum(a.N_MONEYDAY) as N_MONEYCURRENT2,sum(a.N_MONEYDAY) as D_MONEYGROWTH,sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION1,0.00 as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2,0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ( "
				+ htrincomeprosqlbeforeyear
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_INCOME
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ lastyearprosqlwhere
				+ " and a.S_BUDGETSUBCODE in ("
				+ subjectcodes
				+ " ) group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ��Ѯ

				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(crrentprosql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(innerprosql)
						.append(beforesql)
						.append(" union all ")
						.append(beforeprosql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);

			} else {
				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// �ڶ����ȵ������ȵ��ļ���
				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(crrentprosql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(innerprosql)
						.append(beforesql)
						.append(" union all ")
						.append(beforeprosql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);
			} else {
				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(crrentprosql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(innerprosql)
						.append(beforesql)
						.append(" union all ")
						.append(beforeprosql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);
			} else {
				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else {
			// �Զ��Ԥ���Ŀ���л���ͳ��
			currentbysubjectsbf
					.append(prooutersql)
					.append(innerprosql)
					.append(crrentsql)
					.append(beforesql)
					.append(
							" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

			// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
			innersqlbuf.append(currentbysubjectsbf);
		}

		String innersql = innersqlbuf.toString();

		// ����SQL�������
		sqlbuf.append(innersql).append(
				" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String sql = sqlbuf.toString();

		String querysql = "";// �Բ�ͬ�ı����������ò�ͬ�Ĳ�ѯSQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// �ձ�
			querysql = sql;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// Ѯ�����±�
			querysql = sql.replace("N_MONEYDAY", "N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// ���������걨���걨
			querysql = sql.replace("N_MONEYDAY", "N_MONEYYEAR");
		}

		// ��ӡsql
		log.debug(querysql);

		SQLExecutor exec;
		try {
			exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			List querylist = (List) exec.runQueryCloseCon(querysql.toString(),
					IncomeCountReportBySubjectDto.class, true)
					.getDtoCollection();

			return querylist;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}

	}

	/**
	 * ����֧���ձ�
	 * 
	 * @generated
	 * @param idto
	 * @param sbudgetsubcode
	 *            --Ԥ���Ŀ����(����Զ��ŷָ�)
	 * @param sleBillType
	 *            --��������
	 * @param sleTenrptType
	 *            --Ѯ������
	 * @param sleQuarterrptType
	 *            --��������
	 * @param sleHalfyearrptType
	 *            --���걨����
	 * @param sleSubjectType
	 *            --��Ŀ����
	 * @param smoveflag
	 *            --������־
	 * @param sleMoneyUnit
	 *            --��λ
	 * @return String
	 * @throws ITFEBizExceptionn
	 */
	public List makePayoutReportBySubject(IDto idto, String sbudgetsubcode,
			String sleBillType, String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType, String smoveflag,
			String sleMoneyUnit) throws ITFEBizException {
		TrTaxorgPayoutReportDto dto = (TrTaxorgPayoutReportDto) idto;

		String temprptDate = dto.getSrptdate();// ��������
		String temprptDateBeforeyear = ((Integer.parseInt(dto.getSrptdate()) - 10000) + "")
				.trim();// ��һ�걨������

		String rptDate = "";// �����ı�������
		String rptDateBeforeyear = "";// ��������һ�걨������

		String prorptDate = "";// �����ı�������(Ѯ���������걨)
		String prorptDateBeforeyear = "";// ��������һ�걨������(Ѯ���������걨)
		if (sleBillType.equals(StateConstant.REPORT_DAY)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// �ձ��걨
			rptDate = temprptDate;// ��������
			rptDateBeforeyear = temprptDateBeforeyear;// ��һ�걨������
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��

			if (sleTenrptType.equals(StateConstant.TENRPT_TOP)) {// ��Ѯ
				rptDate = temprptDate.substring(0, 6) + "10";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();
			} else if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)) {// ��Ѯ
				rptDate = temprptDate.substring(0, 6) + "20";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();

				prorptDate = temprptDate.substring(0, 6) + "10";
				prorptDateBeforeyear = temprptDateBeforeyear.substring(0, 6)
						+ "10";

			} else if (sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ
				String allrptdate = getRptDate(temprptDate.substring(0, 6));
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				prorptDate = temprptDate.substring(0, 6) + "20";
				prorptDateBeforeyear = temprptDateBeforeyear.substring(0, 6)
						+ "20";
			}
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {// �±�

			String allrptdate = getRptDate(temprptDate.substring(0, 6));
			if (allrptdate.equals("=")) {
				throw new ITFEBizException("����ѯ�������ڣ�");
			}
			rptDate = allrptdate.split("=")[0];
			rptDateBeforeyear = allrptdate.split("=")[1];
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FIRST)) {// ��һ����
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "03");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];
			} else if (sleQuarterrptType
					.equals(StateConstant.QUARTERRPT_SECOND)) {// �ڶ�����
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "03");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)) {// ��������
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "09");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// ���ļ���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "09");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_TOP)) {// �ϰ���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

			} else if (sleHalfyearrptType
					.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("����ѯ�������ڣ�");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			}
		}

		String crrentsql = ""; // ��ǰ��������֧������SQL
		String crrentprosql = ""; // ��ǰ��������֧������SQL(����Ѯ���������걨)
		String beforesql = ""; // ��һ��ͬ�ڱ�������֧������SQL
		String beforeprosql = ""; // ��һ��ͬ�ڱ�������֧������SQL(����Ѯ���������걨)

		String crrentquerysql = ""; // ��ǰ��������֧������SQL
		String procrrentquerysql = ""; // ��ǰ��������֧������SQL(����Ѯ���������걨)
		String beforequerysql = ""; // ��һ��ͬ�ڱ�������֧������SQL
		String probeforequerysql = ""; // ��һ��ͬ�ڱ�������֧������SQL(����Ѯ���������걨)

		String trincomesql = " SELECT * FROM TR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDate + "'";// ���ݱ������ڲ�ѯ��ǰ��
		String htrincomesql = " SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDate + "'";// ���ݱ������ڲ�ѯ��ʷ��
		String incomesql = trincomesql + " union all " + htrincomesql;// ֧���ձ���ѯSQL
		String htrincomesqlbeforeyear = " SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDateBeforeyear + "'";// ������һ�걨�����ڲ�ѯ��ʷ��
		String incomeprosql = "";
		String htrincomeprosqlbeforeyear = "";

		String currentprosqlwhere = "";
		String lastyearprosqlwhere = "";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��

			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ��Ѯ
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ��������֧������
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);

				htrincomeprosqlbeforeyear = htrincomesqlbeforeyear.replace(
						rptDateBeforeyear, prorptDateBeforeyear);
				lastyearprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDateBeforeyear);

			}
		}

		if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// �ڶ����ȵ������ȵ��ļ���
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ��������֧������
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);

				htrincomeprosqlbeforeyear = htrincomesqlbeforeyear.replace(
						rptDateBeforeyear, prorptDateBeforeyear);
				lastyearprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDateBeforeyear);

			}
		}
		if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// ��ǰ��������֧������
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);

				htrincomeprosqlbeforeyear = htrincomesqlbeforeyear.replace(
						rptDateBeforeyear, prorptDateBeforeyear);
				lastyearprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDateBeforeyear);

			}
		}

		// ������ѯSQL
		StringBuffer sqlbuf = new StringBuffer();

		// ��ǰ��������֧������
		String currentsqlwhere = makePayoutReportwhere(idto, sleSubjectType,
				smoveflag, rptDate);
		// ��һ�걨������֧������
		String lastyearsqlwhere = makePayoutReportwhere(idto, sleSubjectType,
				smoveflag, rptDateBeforeyear);

		// ��Ԫ
		String yiyuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5)/100000000 as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5)/100000000 as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";
		// ��Ԫ
		String wanyuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5)/10000 as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5)/10000 as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";
		// Ԫ
		String yuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5) as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5) as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";

		// �Բ�ͬ�Ľ�λ�����жϣ�Ȼ���ͳ�ƽ����������
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// ��Ԫ
			sqlbuf.append(yiyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// ��Ԫ
			sqlbuf.append(wanyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// Ԫ
			sqlbuf.append(yuansql);
		}

		// �Զ��Ԥ���Ŀ�������SQLƴ��
		String subjectcodes = "";
		String[] str = sbudgetsubcode.split(",");
		for (int i = 0; i < str.length; i++) {
			if (i == str.length - 1) {
				subjectcodes += "'" + str[i] + "'";
				break;
			}
			subjectcodes += "'" + str[i] + "',";
		}

		// �ڲ���ѯSQL
		StringBuffer innersqlbuf = new StringBuffer();
		// �Զ��Ԥ���Ŀ���л���ͳ��
		StringBuffer currentbysubjectsbf = new StringBuffer();
		// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
		StringBuffer lastyearbysubjectsbf = new StringBuffer();

		String prooutersql = "select S_BUDGETSUBCODE as S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYCURRENT3) as N_MONEYCURRENT5,sum(N_MONEYCURRENT4) as N_MONEYCURRENT6,sum(D_MONEYGROWTH1) as D_MONEYGROWTH2,sum(D_MONEYCONTRIBUTION3) as D_MONEYCONTRIBUTION5,sum(D_MONEYCONTRIBUTION4) as D_MONEYCONTRIBUTION6,sum(D_MONEYRATIO3) as D_MONEYRATIO5,sum(D_MONEYRATIO4) as D_MONEYRATIO6,sum(N_MONEYALLYEAR3) as N_MONEYALLYEAR5  ,sum(N_MONEYALLYEAR4) as N_MONEYALLYEAR6  ,sum(D_MONEYYEARGROWTH1) as D_MONEYYEARGROWTH2 ,sum(D_MONEYYEARCONTRIBUTION3) as D_MONEYYEARCONTRIBUTION5,sum(D_MONEYYEARCONTRIBUTION4) as D_MONEYYEARCONTRIBUTION6,sum(D_MONEYYEARRATIO3) as D_MONEYYEARRATIO5,sum(D_MONEYYEARRATIO4) as D_MONEYYEARRATIO6 from (";

		String proinnersql = "select S_BUDGETSUBCODE as S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYCURRENT1) as N_MONEYCURRENT3,sum(N_MONEYCURRENT2) as N_MONEYCURRENT4,sum(D_MONEYGROWTH) as D_MONEYGROWTH1,sum(D_MONEYCONTRIBUTION1) as D_MONEYCONTRIBUTION3,sum(D_MONEYCONTRIBUTION2) as D_MONEYCONTRIBUTION4,sum(D_MONEYRATIO1) as D_MONEYRATIO3,sum(D_MONEYRATIO2) as D_MONEYRATIO4,sum(N_MONEYALLYEAR1) as N_MONEYALLYEAR3  ,sum(N_MONEYALLYEAR2) as N_MONEYALLYEAR4  ,sum(D_MONEYYEARGROWTH) as D_MONEYYEARGROWTH1 ,sum(D_MONEYYEARCONTRIBUTION1) as D_MONEYYEARCONTRIBUTION3,sum(D_MONEYYEARCONTRIBUTION2) as D_MONEYYEARCONTRIBUTION4,sum(D_MONEYYEARRATIO1) as D_MONEYYEARRATIO3,sum(D_MONEYYEARRATIO2) as D_MONEYYEARRATIO4 from (";

		// ��ǰ�����������뱨��SQL(ռ��Ԥ���Ŀ)
		crrentquerysql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,0.00 as D_MONEYGROWTH,0.00 as D_MONEYCONTRIBUTION1,sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,sum(a.N_MONEYDAY) as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,sum(a.N_MONEYYEAR) as D_MONEYYEARCONTRIBUTION2, 0.00 as D_MONEYYEARRATIO1,sum(a.N_MONEYYEAR) as D_MONEYYEARRATIO2 from ("
				+ incomesql
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_PAYOUT2
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ currentsqlwhere
				+ " and a.S_BUDGETSUBCODE = '"
				+ dto.getSbudgetsubcode()
				+ "' group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME"
				+ " union all ";

		procrrentquerysql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,0.00 as D_MONEYGROWTH,0.00 as D_MONEYCONTRIBUTION1,-sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,-sum(a.N_MONEYDAY) as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2, 0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ("
				+ incomeprosql
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_PAYOUT2
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ currentprosqlwhere
				+ " and a.S_BUDGETSUBCODE = '"
				+ dto.getSbudgetsubcode()
				+ "' group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		// ��һ��ͬ�ڱ����������뱨��SQL(ռ��Ԥ���Ŀ)
		beforequerysql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,0.00 as D_MONEYGROWTH,0.00 as D_MONEYCONTRIBUTION1,-sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,-sum(a.N_MONEYYEAR) as D_MONEYYEARCONTRIBUTION2,0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ( "
				+ htrincomesqlbeforeyear
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_PAYOUT2
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ lastyearsqlwhere
				+ " and a.S_BUDGETSUBCODE = '"
				+ dto.getSbudgetsubcode()
				+ "' group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		probeforequerysql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,0.00 as D_MONEYGROWTH,0.00 as D_MONEYCONTRIBUTION1,sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2,0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ( "
				+ htrincomeprosqlbeforeyear
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_PAYOUT2
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ lastyearprosqlwhere
				+ " and a.S_BUDGETSUBCODE = '"
				+ dto.getSbudgetsubcode()
				+ "' group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ��Ѯ
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(procrrentquerysql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(proinnersql)
						.append(beforequerysql)
						.append(" union all ")
						.append(probeforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			} else {
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// �ڶ����ȵ������ȵ��ļ���
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(procrrentquerysql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(proinnersql)
						.append(beforequerysql)
						.append(" union all ")
						.append(probeforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			} else {
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(procrrentquerysql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(proinnersql)
						.append(beforequerysql)
						.append(" union all ")
						.append(probeforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			} else {
				// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else {
			// �Ե���ռ��Ԥ���Ŀ������л���ͳ��
			lastyearbysubjectsbf
					.append(prooutersql)
					.append(proinnersql)
					.append(crrentquerysql)
					.append(beforequerysql)
					.append(
							" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
		}

		String basesubjectsbf = lastyearbysubjectsbf.toString();

		String basesubjectquerysql = "";// �Բ�ͬ�ı����������ò�ͬ�Ĳ�ѯSQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// �ձ�
			basesubjectquerysql = basesubjectsbf;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// Ѯ�����±�
			basesubjectquerysql = basesubjectsbf.replace("N_MONEYDAY",
					"N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// ���������걨���걨
			basesubjectquerysql = basesubjectsbf.replace("N_MONEYDAY",
					"N_MONEYYEAR");
		}

		SQLExecutor execute = null;
		SQLResults res = null;
		try {
			execute = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			res = execute.runQueryCloseCon(basesubjectquerysql.toString());

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}

		String innerprosql = "";

		if (null != res && res.getRowCount() > 0) {
			innerprosql = "select S_BUDGETSUBCODE as S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYCURRENT1)+"
					+ res.getBigDecimal(0, 2)
					+ " as N_MONEYCURRENT3,sum(N_MONEYCURRENT2)+"
					+ res.getBigDecimal(0, 3)
					+ " as N_MONEYCURRENT4,sum(D_MONEYGROWTH)+"
					+ res.getBigDecimal(0, 4)
					+ " as D_MONEYGROWTH1,sum(D_MONEYCONTRIBUTION1)+"
					+ res.getBigDecimal(0, 5)
					+ " as D_MONEYCONTRIBUTION3,sum(D_MONEYCONTRIBUTION2)+"
					+ res.getBigDecimal(0, 6)
					+ " as D_MONEYCONTRIBUTION4,sum(D_MONEYRATIO1)+"
					+ res.getBigDecimal(0, 7)
					+ " as D_MONEYRATIO3,sum(D_MONEYRATIO2)+"
					+ res.getBigDecimal(0, 8)
					+ " as D_MONEYRATIO4,sum(N_MONEYALLYEAR1)+"
					+ res.getBigDecimal(0, 9)
					+ " as N_MONEYALLYEAR3  ,sum(N_MONEYALLYEAR2)+"
					+ res.getBigDecimal(0, 10)
					+ " as N_MONEYALLYEAR4  ,sum(D_MONEYYEARGROWTH)+"
					+ res.getBigDecimal(0, 11)
					+ " as D_MONEYYEARGROWTH1 ,sum(D_MONEYYEARCONTRIBUTION1)+"
					+ res.getBigDecimal(0, 12)
					+ " as D_MONEYYEARCONTRIBUTION3,sum(D_MONEYYEARCONTRIBUTION2)+"
					+ res.getBigDecimal(0, 13)
					+ " as D_MONEYYEARCONTRIBUTION4,sum(D_MONEYYEARRATIO1)+"
					+ res.getBigDecimal(0, 14)
					+ " as D_MONEYYEARRATIO3,sum(D_MONEYYEARRATIO2)+"
					+ res.getBigDecimal(0, 15) + " as D_MONEYYEARRATIO4 from (";
		} else {
			throw new ITFEBizException("����ѯ��ռ��Ԥ���Ŀ�������ڣ�");
		}

		// ��ǰ�����������뱨��SQL
		crrentsql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,sum(a.N_MONEYDAY) as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,sum(a.N_MONEYDAY) as D_MONEYGROWTH,sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION1,0.00 as D_MONEYCONTRIBUTION2,sum(a.N_MONEYDAY) as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,sum(a.N_MONEYYEAR) as D_MONEYYEARGROWTH ,sum(a.N_MONEYYEAR) as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2,sum(a.N_MONEYYEAR) as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ("
				+ incomesql
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_PAYOUT2
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ currentsqlwhere
				+ " and a.S_BUDGETSUBCODE in ("
				+ subjectcodes
				+ " ) group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME"
				+ " union all ";

		crrentprosql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,-sum(a.N_MONEYDAY) as N_MONEYCURRENT1,0.00 as N_MONEYCURRENT2,-sum(a.N_MONEYDAY) as D_MONEYGROWTH,-sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION1,0.00 as D_MONEYCONTRIBUTION2,-sum(a.N_MONEYDAY) as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2,0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ("
				+ incomeprosql
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_PAYOUT2
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ currentprosqlwhere
				+ " and a.S_BUDGETSUBCODE in ("
				+ subjectcodes
				+ " ) group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		// ��һ��ͬ�ڱ����������뱨��SQL
		beforesql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,sum(a.N_MONEYDAY) as N_MONEYCURRENT2,-sum(a.N_MONEYDAY) as D_MONEYGROWTH,-sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION1,0.00 as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,sum(a.N_MONEYYEAR) as N_MONEYALLYEAR2,-sum(a.N_MONEYYEAR) as D_MONEYYEARGROWTH ,-sum(a.N_MONEYYEAR) as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2,0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ( "
				+ htrincomesqlbeforeyear
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_PAYOUT2
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ lastyearsqlwhere
				+ " and a.S_BUDGETSUBCODE in ("
				+ subjectcodes
				+ " ) group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		beforeprosql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0.00 as N_MONEYCURRENT1,-sum(a.N_MONEYDAY) as N_MONEYCURRENT2,sum(a.N_MONEYDAY) as D_MONEYGROWTH,sum(a.N_MONEYDAY) as D_MONEYCONTRIBUTION1,0.00 as D_MONEYCONTRIBUTION2,0.00 as D_MONEYRATIO1,0.00 as D_MONEYRATIO2,0.00 as N_MONEYALLYEAR1 ,0.00 as N_MONEYALLYEAR2,0.00 as D_MONEYYEARGROWTH ,0.00 as D_MONEYYEARCONTRIBUTION1,0.00 as D_MONEYYEARCONTRIBUTION2,0.00 as D_MONEYYEARRATIO1,0.00 as D_MONEYYEARRATIO2 from ( "
				+ htrincomeprosqlbeforeyear
				+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
				+ StateConstant.S_SUBJECTCLASS_PAYOUT2
				+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
				+ lastyearprosqlwhere
				+ " and a.S_BUDGETSUBCODE in ("
				+ subjectcodes
				+ " ) group by a.S_BUDGETSUBCODE ,b.S_SUBJECTNAME";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// Ѯ��
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// ��Ѯ��Ѯ

				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(crrentprosql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(innerprosql)
						.append(beforesql)
						.append(" union all ")
						.append(beforeprosql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);

			} else {
				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// ����
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// �ڶ����ȵ������ȵ��ļ���
				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(crrentprosql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(innerprosql)
						.append(beforesql)
						.append(" union all ")
						.append(beforeprosql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);
			} else {
				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// ���걨
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// �°���
				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(crrentprosql)
						.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ")
						.append(" union all ")
						.append(innerprosql)
						.append(beforesql)
						.append(" union all ")
						.append(beforeprosql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);
			} else {
				// �Զ��Ԥ���Ŀ���л���ͳ��
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else {
			// �Զ��Ԥ���Ŀ���л���ͳ��
			currentbysubjectsbf
					.append(prooutersql)
					.append(innerprosql)
					.append(crrentsql)
					.append(beforesql)
					.append(
							" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

			// �Զ��Ԥ���Ŀ�͵���ռ��Ԥ���Ŀ������л���ͳ��
			innersqlbuf.append(currentbysubjectsbf);
		}

		String innersql = innersqlbuf.toString();

		// ����SQL�������
		sqlbuf.append(innersql).append(
				" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String sql = sqlbuf.toString();

		String querysql = "";// �Բ�ͬ�ı����������ò�ͬ�Ĳ�ѯSQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// �ձ�
			querysql = sql;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// Ѯ�����±�
			querysql = sql.replace("N_MONEYDAY", "N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// ���������걨���걨
			querysql = sql.replace("N_MONEYDAY", "N_MONEYYEAR");
		}

		// ��ӡsql
		log.debug(querysql);

		SQLExecutor exec;
		try {
			exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			List querylist = (List) exec.runQueryCloseCon(querysql.toString(),
					IncomeCountReportBySubjectDto.class, true)
					.getDtoCollection();

			return querylist;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}

	}

	/**
	 * ����֧���ձ�
	 * 
	 * @generated
	 * @param startqueryyear
	 *            --��ʼ���
	 * @param endqueryyear
	 *            --�������
	 * @param startdate
	 *            --��ʼ����
	 * @param enddate
	 *            --��������
	 * @param sleTreCode
	 *            --�������
	 * @param sleOfFlag
	 *            --Ͻ����־
	 * @param sleMoneyUnit
	 *            --��λ
	 * @return List
	 * @throws ITFEBizExceptionn
	 */
	public List stockCountQueryList(String sorgcode, String startyear,
			String endyear, String startdate, String enddate,
			String sleTreCode, String sleOfFlag, String sleMoneyUnit)
			throws ITFEBizException {
		// ��ʼ��ȵ���ʼ����
		String startyearstartdate = startyear.trim() + startdate.trim();

		// ��ʼ��ȵĽ�������
		String startyearenddate = startyear.trim() + enddate.trim();

		// ������ȵ���ʼ����
		String endyearstartdate = endyear.trim() + startdate.trim();

		// ������ȵĽ�������
		String endyearenddate = endyear.trim() + enddate.trim();

		// ��ʼ��Ȳ�ѯ����Sql
		String startyearsqlwhere = "where 1=1 ";

		// ���������Ȳ�ѯ����Sql
		String endyearsqlwhere = "where 1=1 ";
		// �ж�Ͻ����־
		if (sleOfFlag.equals(MsgConstant.RULE_SIGN_ALL)) {// ȫϽ

			startyearsqlwhere += " and (S_TRECODE = '"
					+ sleTreCode
					+ "' or S_TRECODE in ( select S_TRECODE from TS_TREASURY where S_GOVERNTRECODE = '"
					+ sleTreCode + "' and S_ORGCODE = '" + sorgcode
					+ "') ) and S_RPTDATE between '" + startyearstartdate
					+ "' and '" + startyearenddate + "'";

			endyearsqlwhere += " and (S_TRECODE = '"
					+ sleTreCode
					+ "' or S_TRECODE in ( select S_TRECODE from TS_TREASURY where S_GOVERNTRECODE = '"
					+ sleTreCode + "' and S_ORGCODE = '" + sorgcode
					+ "') ) and S_RPTDATE between '" + endyearstartdate
					+ "' and '" + endyearenddate + "'";
		} else if (sleOfFlag.equals(MsgConstant.RULE_SIGN_SELF)) {// ����

			startyearsqlwhere += " and S_ORGCODE = '" + sorgcode
					+ "' and S_TRECODE = '" + sleTreCode
					+ "'  and S_RPTDATE between '" + startyearstartdate
					+ "' and '" + startyearenddate + "'";

			endyearsqlwhere += " and S_ORGCODE = '" + sorgcode
					+ "' and S_TRECODE = '" + sleTreCode
					+ "'  and S_RPTDATE between '" + endyearstartdate
					+ "' and '" + endyearenddate + "'";
		}
		// ������ʼ��ȵĲ�ѯSQL
		StringBuffer startyearsbf = new StringBuffer();
		// ����������ȵĲ�ѯSQL
		StringBuffer endyearsbf = new StringBuffer();

		// ��ʼ��Ȳ�ѯ��ǰ�����ʷ���SQL
		String startyearallsql = "select * from TR_STOCKDAYRPT "
				+ startyearsqlwhere
				+ " union all select * from HTR_STOCKDAYRPT "
				+ startyearsqlwhere;
		// ������Ȳ�ѯ��ǰ�����ʷ���SQL
		String endyearallsql = "select * from TR_STOCKDAYRPT "
				+ endyearsqlwhere + " union all select * from HTR_STOCKDAYRPT "
				+ endyearsqlwhere;

		// ��ʼ��ȵĲ�ѯSQL
		String startyearquerysql = "SELECT avg(N_MONEYTODAY),stddev(N_MONEYTODAY) FROM ("
				+ startyearallsql + ") ";

		// ������ȵĲ�ѯSQL
		String endyearquerysql = "SELECT avg(N_MONEYTODAY),stddev(N_MONEYTODAY) FROM ("
				+ endyearallsql + ") ";

		// �Բ�ͬ�Ľ�λ�����жϣ�Ȼ���ͳ�ƽ����������
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// ��Ԫ
			startyearquerysql = startyearquerysql.replace("N_MONEYTODAY",
					"N_MONEYTODAY/100000000");

			endyearquerysql = endyearquerysql.replace("N_MONEYTODAY",
					"N_MONEYTODAY/100000000");

		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// ��Ԫ
			startyearquerysql = startyearquerysql.replace("N_MONEYTODAY",
					"N_MONEYTODAY/10000");

			endyearquerysql = endyearquerysql.replace("N_MONEYTODAY",
					"N_MONEYTODAY/10000");

		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// Ԫ
		}

		SQLExecutor execute = null;
		SQLResults res1 = null;
		SQLResults res2 = null;
		try {
			execute = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			// ��ʼ��Ȳ�ѯ�����
			res1 = execute.runQuery(startyearquerysql.toString());
			// �ж��Ƿ�Ϊͬһ���
			if (!startyear.equals(endyear)) {
				// ������Ȳ�ѯ�����
				res2 = execute.runQueryCloseCon(endyearquerysql.toString());
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}

		String exceptionMessgae = "";

		if (null != res1 && res1.getRowCount() > 0
				&& null != res1.getBigDecimal(0, 0)
				&& null != res1.getBigDecimal(0, 1)) {
			startyearsbf
					.append("select concat('"
							+ startyear
							+ "','���') as syear ,"
							+ res1.getBigDecimal(0, 0)
							+ " as naveragevalue ,max(N_MONEYTODAY) as nmaximumvalue ,min(N_MONEYTODAY) as nminimumvalue ,"
							+ res1.getDouble(0, 1)
							+ " as nstandarddeviation , sum(power(N_MONEYTODAY-"
							+ res1.getBigDecimal(0, 0) + ",3))/power("
							+ res1.getDouble(0, 1)
							+ ",3) as nskewness ,sum(power(N_MONEYTODAY-"
							+ res1.getBigDecimal(0, 0) + ",4))/power("
							+ res1.getDouble(0, 1)
							+ ",4)-3 as nkurtosis FROM (" + startyearallsql
							+ ") ");
		} else {
			exceptionMessgae += "����ѯ��" + startyear + "��ȿ��ͳ�Ʒ����������ڣ�\r\n";
		}

		// �ж��Ƿ�Ϊͬһ���
		if (!startyear.equals(endyear)) {
			if (null != res2 && res2.getRowCount() > 0
					&& null != res2.getBigDecimal(0, 0)
					&& null != res2.getBigDecimal(0, 1)) {
				endyearsbf
						.append("select concat('"
								+ endyear
								+ "','���') as syear ,"
								+ res2.getBigDecimal(0, 0)
								+ " as naveragevalue ,max(N_MONEYTODAY) as nmaximumvalue ,min(N_MONEYTODAY) as nminimumvalue ,"
								+ res2.getDouble(0, 1)
								+ " as nstandarddeviation , sum(power(N_MONEYTODAY-"
								+ res2.getBigDecimal(0, 0) + ",3))/power("
								+ res2.getDouble(0, 1)
								+ ",3) as nskewness ,sum(power(N_MONEYTODAY-"
								+ res2.getBigDecimal(0, 0) + ",4))/power("
								+ res2.getDouble(0, 1)
								+ ",4)-3 as nkurtosis FROM (" + endyearallsql
								+ ") ");
			} else {
				exceptionMessgae += "����ѯ��" + endyear + "��ȿ��ͳ�Ʒ����������ڣ�";
			}
		}

		if (!exceptionMessgae.equals("")) {
			throw new ITFEBizException(exceptionMessgae);
		}

		// ���ղ�ѯSQL
		StringBuffer querysqlsbf = new StringBuffer("");

		querysqlsbf.append(startyearsbf);

		// �ж��Ƿ�Ϊͬһ���
		if (!startyear.equals(endyear)) {
			querysqlsbf.append(" union all ").append(endyearsbf);
		}

		String querysql = querysqlsbf.toString();

		// �Բ�ͬ�Ľ�λ�����жϣ�Ȼ���ͳ�ƽ����������
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// ��Ԫ
			querysql = querysql.replace("N_MONEYTODAY",
					"N_MONEYTODAY/100000000");
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// ��Ԫ
			querysql = querysql.replace("N_MONEYTODAY", "N_MONEYTODAY/10000");
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// Ԫ
		}

		// ��ӡsql
		log.debug(querysql);

		SQLExecutor exec = null;
		try {
			exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			List querylist = (List) exec.runQueryCloseCon(querysql,
					StockCountAnalysicsReportDto.class, true)
					.getDtoCollection();

			return querylist;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}finally{
			if(null!=execute){
				execute.closeConnection();
			}
			if(null!=exec){
				exec.closeConnection();
			}
		}
	}

}
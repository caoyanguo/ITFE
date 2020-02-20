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
	 * 财政收入日报
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --报表种类
	 * @param sleSubjectType
	 *            --科目类型
	 * @param sleSubjectAttribute
	 *            --科目属性
	 * @param smoveflag
	 *            --调拨标志
	 * @param sleMoneyUnit
	 *            --金额单位
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

			// 查询报表SQL
			String querysql = sql.split("#")[0];

			// 打印sql
			log.debug(querysql);

			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			List querylist = (List) exec.runQueryCloseCon(querysql.toString(),
					IncomeMoneyReportDto.class, true).getDtoCollection();

			return querylist;

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}

	/**
	 * 财政支出日报
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --报表种类
	 * @param sleTenrptType
	 *            --旬报类型
	 * @param sleQuarterrptType
	 *            --季报类型
	 * @param sleHalfyearrptType
	 *            --半年报类型
	 * @param sleSubjectType
	 *            --科目类型
	 * @param sleSubjectAttribute
	 *            --科目属性
	 * @param smoveflag
	 *            --调拨标志
	 * @param sleMoneyUnit
	 *            --金额单位
	 * @param request
	 *            --分页请求
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

			// 查询报表SQL
			String querysql = sql.split("#")[0];

			// 打印sql
			log.debug(querysql);

			SQLExecutor exec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			List querylist = (List) exec.runQueryCloseCon(querysql.toString(),
					IncomeMoneyReportDto.class, true).getDtoCollection();

			return querylist;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}
	}

	/**
	 * 财政收入日报
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --报表种类
	 * @param sleTenrptType
	 *            --旬报类型
	 * @param sleQuarterrptType
	 *            --季报类型
	 * @param sleHalfyearrptType
	 *            --半年报类型
	 * @param sleSubjectType
	 *            --科目类型
	 * @param sleSubjectAttribute
	 *            --科目属性
	 * @param smoveflag
	 *            --调拨标志
	 * @param sleMoneyUnit
	 *            --金额单位
	 * @param request
	 *            --分页请求
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

			// 查询报表SQL
			String querysql = sql.split("#")[0];

			// 组装计数SQL
			String innersql = sql.split("#")[1];

			// 打印sql
			log.debug(querysql);

			PageResponse response = new PageResponse(request);
			// 分页查询
			SQLExecutor sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			if (response.getTotalCount() == 0) {
				SQLExecutor sqlExe1 = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				// 分页计数
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
			throw new ITFEBizException("查询出错", e);
		}
	}

	/**
	 * 根据日期字符串儿获取最大和最小日期
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

			// 首先确定所查询报表在当前表还是在历史表
			execute1 = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();

			String trdate = execute1.runQuery(monthsql).getString(0, 0);

			if (null != trdate && !trdate.equals("")) {
				rptDate = trdate;// 报表日期
				rptDateBeforeyear = ((Integer.parseInt(trdate) - 10000) + "")
						.trim();// 上一年报表日期
			} else {
				String htrdate = execute1
						.runQuery(
								monthsql.replace("TR_INCOMEDAYRPT",
										"HTR_INCOMEDAYRPT")).getString(0, 0);
				if (null != htrdate && !htrdate.equals("")) {
					rptDate = htrdate;// 报表日期
					rptDateBeforeyear = ((Integer.parseInt(htrdate) - 10000) + "")
							.trim();// 上一年报表日期
				}
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询报表日期出错", e);
		}finally{
			if(null!=execute1){
				execute1.closeConnection();
			}
		}
		return rptDate + "=" + rptDateBeforeyear;
	}

	/**
	 * 制作收入报表查询条件
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
		 * 报表日期：报表生成日期 收款国库代码：国库代码
		 * 科目类型：01一般预算内02一般预算外03预算内基金04预算内债务05预算外基金06共用07转移性收入---sleSubjectType
		 * 预算种类： 1预算内,2预算外 预算级次： 1 中央,2 省,3 市,4 县,5 乡,6地方 辖属标志： 0 全辖,1本级,2全辖非本级
		 * 调拨标志: 0 非调拨,1 调拨, 2 不区分 调整期标志：0正常期1调整期
		 */
		// 查询sql 国库代码，辖属标志，征收机关代码，查询日期，预算种类，预算级次，调整期, 报表类型, 科目类型 ,调拨标志
		String sqlwhere = "";

		if (null == dto.getStrecode() || "".equals(dto.getStrecode().trim())) {
			throw new ITFEBizException("查询条件：国库代码不能为空！");
		}
		if (null == dto.getSbelongflag()
				|| "".equals(dto.getSbelongflag().trim())) {
			throw new ITFEBizException("查询条件：辖属标志不能为空！");
		}

		/**
		 * 辖属标志+国库
		 */
		sqlwhere += " and a.s_trecode ='" + dto.getStrecode().trim() + "' "
				+ " and a.S_BELONGFLAG ='" + dto.getSbelongflag().trim() + "' ";

		/**
		 * 报表日期
		 */
		if (null != dto.getSrptdate() && !"".equals(dto.getSrptdate().trim())) {
			sqlwhere += " and a.S_RPTDATE ='" + srptdate + "' ";
		} else {
			throw new ITFEBizException("查询条件：报表日期不能为空！");
		}

		/**
		 * 预算种类
		 */
		if (null != dto.getSbudgettype()
				&& !"".equals(dto.getSbudgettype().trim())) {
			sqlwhere += " and a.s_BudgetType ='" + dto.getSbudgettype().trim()
					+ "' ";
		} else {
			throw new ITFEBizException("查询条件：预算种类不能为空！");
		}

		/**
		 * 预算级次条件判断 当预算级次为【地方】时，查询【市级+区级】的数据 当预算级次为【不区分】时，查询【中央级+市级+区级】的数据
		 */
		if (null != dto.getSbudgetlevelcode()
				&& !"".equals(dto.getSbudgetlevelcode().trim())) {

			if (StateConstant.SUBJECTLEVEL_LOCAL.equals(dto
					.getSbudgetlevelcode().trim())) {// 地方
				sqlwhere += " and a.S_BUDGETLEVELCODE in ('"
						+ StateConstant.SUBJECTLEVEL_CITY + "' ,'"
						+ StateConstant.SUBJECTLEVEL_AREA + "') ";
			} else if (StateConstant.SUBJECTLEVEL_ANY.equals(dto
					.getSbudgetlevelcode().trim())) {// 不区分级次
				sqlwhere += " and a.S_BUDGETLEVELCODE in ('"
						+ StateConstant.SUBJECTLEVEL_CENTER + "' ,'"
						+ StateConstant.SUBJECTLEVEL_CITY + "' ,'"
						+ StateConstant.SUBJECTLEVEL_AREA + "') ";
			} else {// 其他
				sqlwhere += " and a.S_BUDGETLEVELCODE ='"
						+ dto.getSbudgetlevelcode().trim() + "' ";
			}
		} else {
			throw new ITFEBizException("查询条件：预算级次不能为空！");
		}

		/**
		 * 调整期标志
		 */
		if (null != dto.getStrimflag() && !"".equals(dto.getStrimflag().trim())) {
			sqlwhere += " and a.s_TrimFlag ='" + dto.getStrimflag().trim()
					+ "' ";
			// 调整期
			if (dto.getStrimflag().equals(MsgConstant.TIME_FLAG_TRIM)) {
				sbillkind = StateConstant.REPORTTYPE_FLAG_TRBUDGETBILL;
			}
		} else {
			throw new ITFEBizException("查询条件：调整期标志不能为空！");
		}

		/**
		 * 报表种类
		 */
		if (null != sbillkind && !"".equals(sbillkind.trim())) {
			sqlwhere += " and a.S_BILLKIND ='" + sbillkind + "' ";
		}

		/**
		 * 科目类型 sleSubjectType
		 */
		if (null != sleSubjectType && !"".equals(sleSubjectType.trim())) {
			sqlwhere += " and b.S_SUBJECTTYPE ='" + sleSubjectType + "' ";
		}

		/**
		 * 调拨标志 S_MOVEFLAG
		 */
		if (null != smoveflag && !"".equals(smoveflag.trim())) {
			sqlwhere += " and b.S_MOVEFLAG ='" + smoveflag + "' ";
		}

		/**
		 * 由于各核算主体单独一套预算科目代码，因此查询的时候应加上科目代码的核算主体条件
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_ORGCODE FROM TS_TREASURY WHERE S_TRECODE='"
				+ dto.getStrecode().trim() + "' ) ";

		return sqlwhere;
	}

	/**
	 * 财政支出日报
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --报表种类
	 * @param sleTenrptType
	 *            --旬报类型
	 * @param sleQuarterrptType
	 *            --季报类型
	 * @param sleHalfyearrptType
	 *            --半年报类型
	 * @param sleSubjectType
	 *            --科目类型
	 * @param sleSubjectAttribute
	 *            --科目属性
	 * @param smoveflag
	 *            --调拨标志
	 * @param sleMoneyUnit
	 *            --金额单位
	 * @param request
	 *            --分页请求
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

			// 查询报表SQL
			String querysql = sql.split("#")[0];
			// 组装计数SQl
			String innersql = sql.split("#")[1];

			// 打印sql
			log.debug(querysql);

			PageResponse response = new PageResponse(request);
			// 分页查询
			SQLExecutor sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			if (response.getTotalCount() == 0) {
				SQLExecutor sqlExe1 = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				// 分页计数
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
			throw new ITFEBizException("查询出错", e);
		}

	}

	/**
	 * 制作支出报表查询条件
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
		 * 国库代码
		 */
		if (null != dto.getStrecode() && !"".equals(dto.getStrecode().trim())) {
			sqlwhere += " and a.s_trecode ='" + dto.getStrecode().trim() + "' ";
		} else {
			throw new ITFEBizException("查询条件：国库代码不能为空！");
		}

		/**
		 * 报表日期
		 */
		if (null != dto.getSrptdate() && !"".equals(dto.getSrptdate().trim())) {
			sqlwhere += " and a.S_RPTDATE ='" + rptDate.trim() + "' ";
		} else {
			throw new ITFEBizException("查询条件：报表日期不能为空！");
		}

		/**
		 * 预算种类
		 */
		if (null != dto.getSbudgettype()
				&& !"".equals(dto.getSbudgettype().trim())) {
			sqlwhere += " and a.s_BudgetType ='" + dto.getSbudgettype().trim()
					+ "' ";
		} else {
			throw new ITFEBizException("查询条件：预算种类不能为空！");
		}

		/**
		 * 预算级次条件判断 当预算级次为【地方】时，查询【市级+区级】的数据 当预算级次为【不区分】时，查询【中央级+市级+区级】的数据
		 */
		if (null != dto.getSbudgetlevelcode()
				&& !"".equals(dto.getSbudgetlevelcode().trim())) {

			if (StateConstant.SUBJECTLEVEL_LOCAL.equals(dto
					.getSbudgetlevelcode().trim())) {// 地方
				sqlwhere += " and a.S_BUDGETLEVELCODE in ('"
						+ StateConstant.SUBJECTLEVEL_CITY + "' ,'"
						+ StateConstant.SUBJECTLEVEL_AREA + "') ";
			} else if (StateConstant.SUBJECTLEVEL_ANY.equals(dto
					.getSbudgetlevelcode().trim())) {// 不区分级次
				sqlwhere += " and a.S_BUDGETLEVELCODE in ('"
						+ StateConstant.SUBJECTLEVEL_CENTER + "' ,'"
						+ StateConstant.SUBJECTLEVEL_CITY + "' ,'"
						+ StateConstant.SUBJECTLEVEL_AREA + "') ";
			} else {// 其他
				sqlwhere += " and a.S_BUDGETLEVELCODE ='"
						+ dto.getSbudgetlevelcode().trim() + "' ";
			}
		} else {
			throw new ITFEBizException("查询条件：预算级次不能为空！");
		}

		/**
		 * 科目类型 sleSubjectType
		 */
		if (null != sleSubjectType && !"".equals(sleSubjectType.trim())) {
			sqlwhere += " and b.S_SUBJECTTYPE ='" + sleSubjectType + "' ";
		}

		/**
		 * 调拨标志 S_MOVEFLAG
		 */
		if (null != smoveflag && !"".equals(smoveflag.trim())) {
			sqlwhere += " and b.S_MOVEFLAG ='" + smoveflag + "' ";
		}

		/**
		 * 由于各核算主体单独一套预算科目代码，因此查询的时候应加上科目代码的核算主体条件
		 */
		sqlwhere += " and b.S_ORGCODE in (SELECT S_ORGCODE FROM TS_TREASURY WHERE S_TRECODE='"
				+ dto.getStrecode().trim() + "' ) ";

		return sqlwhere;
	}

	/**
	 * 财政收入日报
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --报表种类
	 * @param sleTenrptType
	 *            --旬报类型
	 * @param sleQuarterrptType
	 *            --季报类型
	 * @param sleHalfyearrptType
	 *            --半年报类型
	 * @param sleSubjectType
	 *            --科目类型
	 * @param sleSubjectAttribute
	 *            --科目属性
	 * @param smoveflag
	 *            --调拨标志
	 * @param sleMoneyUnit
	 *            --金额单位
	 * @return String
	 * @throws ITFEBizException
	 */
	public String makeIncomeReportSQL(IDto idto, String sleBillType,
			String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType,
			String sleSubjectAttribute, String smoveflag, String sleMoneyUnit)
			throws ITFEBizException {
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;

		String temprptDate = dto.getSrptdate();// 报表日期
		String temprptDateBeforeyear = ((Integer.parseInt(dto.getSrptdate()) - 10000) + "")
				.trim();// 上一年报表日期

		String rptDate = "";// 处理后的报表日期
		String rptDateBeforeyear = "";// 处理后的上一年报表日期

		String prorptDate = "";// 处理后的报表日期(旬、季、半年报)
		if (sleBillType.equals(StateConstant.REPORT_DAY)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// 日报年报
			rptDate = temprptDate;// 报表日期
			rptDateBeforeyear = temprptDateBeforeyear;// 上一年报表日期
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报

			if (sleTenrptType.equals(StateConstant.TENRPT_TOP)) {// 上旬
				rptDate = temprptDate.substring(0, 6) + "10";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();
			} else if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)) {// 中旬
				rptDate = temprptDate.substring(0, 6) + "20";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();

				prorptDate = temprptDate.substring(0, 6) + "10";

			} else if (sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 下旬
				String allrptdate = getRptDate(temprptDate.substring(0, 6));
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				prorptDate = temprptDate.substring(0, 6) + "20";
			}
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {// 月报

			String allrptdate = getRptDate(temprptDate.substring(0, 6));
			if (allrptdate.equals("=")) {
				throw new ITFEBizException("所查询报表不存在！");
			}
			rptDate = allrptdate.split("=")[0];
			rptDateBeforeyear = allrptdate.split("=")[1];
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FIRST)) {// 第一季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "03");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];
			} else if (sleQuarterrptType
					.equals(StateConstant.QUARTERRPT_SECOND)) {// 第二季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "03");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)) {// 第三季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "09");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第四季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "09");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_TOP)) {// 上半年
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

			} else if (sleHalfyearrptType
					.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
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

		String crrentsql = ""; // 当前报表日期收入报表SQL
		String crrentprosql = ""; // 当前报表日期收入报表SQL(处理旬、季、半年报)
		String beforesql = ""; // 上一年同期报表日期收入报表SQL
		String beforeprosql = ""; // 上一年同期报表日期收入报表SQL(处理旬、季、半年报)
		String trincomesql = " SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDate + "'";// 根据报表日期查询当前表
		String htrincomesql = " SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDate + "'";// 根据报表日期查询历史表
		String incomesql = trincomesql + " union all " + htrincomesql;// 收入日报查询SQL
		String htrincomesqlbeforeyear = " SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDateBeforeyear + "'";// 根据上一年报表日期查询历史表
		String incomeprosql = "";

		String currentprosqlwhere = "";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报

			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 中旬下旬
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期收入条件
				currentprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, prorptDate);
			}
		}

		if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第二季度第三季度第四季度
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期收入条件
				currentprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, prorptDate);
			}
		}
		if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期收入条件
				currentprosqlwhere = makeIncomeReportwhere(idto,
						sleSubjectType, smoveflag,
						StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, prorptDate);
			}
		}

		// 创建查询SQL
		StringBuffer sqlbuf = new StringBuffer();

		// 当前报表日期收入条件
		String currentsqlwhere = makeIncomeReportwhere(idto, sleSubjectType,
				smoveflag, StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, rptDate);
		// 上一年报表日期收入条件
		String lastyearsqlwhere = makeIncomeReportwhere(idto, sleSubjectType,
				smoveflag, StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL,
				rptDateBeforeyear);

		// 亿元
		String yiyuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT)/100000000 as nmoneycurrent,SUM(N_MONEYYEAR)/100000000 as nmoneyyear ,sum(N_MONEYINCREMENTAL)/100000000 as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END)  ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";
		// 万元
		String wanyuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT)/10000 as nmoneycurrent,SUM(N_MONEYYEAR)/10000 as nmoneyyear ,sum(N_MONEYINCREMENTAL)/10000 as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END) ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";
		// 元
		String yuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT) as nmoneycurrent,SUM(N_MONEYYEAR) as nmoneyyear ,sum(N_MONEYINCREMENTAL) as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END) ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";

		// 对不同的金额单位进行判断，然后对统计结果进行整理
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// 亿元
			sqlbuf.append(yiyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// 万元
			sqlbuf.append(wanyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// 元
			sqlbuf.append(yuansql);
		}

		// 内部查询SQL
		StringBuffer innersqlbuf = new StringBuffer();

		innersqlbuf
				.append(" select S_BUDGETSUBCODE AS S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYDAY) AS N_MONEYCURRENT, sum(N_MONEYALLYEAR) AS N_MONEYYEAR, sum(N_MONEYINCREMENTAL) AS N_MONEYINCREMENTAL,sum(D_MONEYGROWTHRATE) AS D_MONEYGROWTHRATE from (");

		if (null == sleSubjectAttribute
				|| "".equals(sleSubjectAttribute.trim())) {
			throw new ITFEBizException("查询条件：科目属性不能为空！");
		}

		// 根据科目属性（类、款、项、目）拼装不同的SQL
		if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_CLASS)) {// 类

			// 当前报表日期收入报表SQL
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

			// 上一年同期报表日期收入报表SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_PAGE)) {// 款

			// 当前报表日期收入报表SQL
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

			// 上一年同期报表日期收入报表SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute
				.equals(StateConstant.SUBJECTATTR_PROJECT)) {// 项

			// 当前报表日期收入报表SQL
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

			// 上一年同期报表日期收入报表SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_ITEM)) {// 目

			// 当前报表日期收入报表SQL
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

			// 上一年同期报表日期收入报表SQL
			beforesql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_INCOME
					+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " group by S_BUDGETSUBCODE,S_SUBJECTNAME ";

		}

		String innerprosql = " select S_BUDGETSUBCODE AS S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYDAY) AS N_MONEYCURRENT, sum(N_MONEYALLYEAR) AS N_MONEYALLYEAR, sum(N_MONEYINCREMENTAL) AS N_MONEYINCREMENTAL,sum(D_MONEYGROWTHRATE) AS D_MONEYGROWTHRATE from (";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 中旬下旬
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYMONTH");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// 将当前报表日期SQL与上一年同期的SQL拼在一起进行组合统计
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第二季度第三季度第四季度
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYYEAR");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// 将当前报表日期SQL与上一年同期的SQL拼在一起进行组合统计
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYYEAR");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// 将当前报表日期SQL与上一年同期的SQL拼在一起进行组合统计
			}
		} else {
			innersqlbuf.append(crrentsql).append(beforesql);// 将当前报表日期SQL与上一年同期的SQL拼在一起进行组合统计
		}

		// 内部SQL组合排序
		innersqlbuf.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String innersql = innersqlbuf.toString();

		// 最终SQL组合排序
		sqlbuf.append(innersql).append(
				" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String sql = sqlbuf.toString();

		String querysql = "";// 对不同的报表类型设置不同的查询SQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// 日报
			querysql = sql;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// 旬报、月报
			innersql = innersql.replace("N_MONEYDAY", "N_MONEYMONTH");
			querysql = sql.replace("N_MONEYDAY", "N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// 季报、半年报、年报
			innersql = innersql.replace("N_MONEYDAY", "N_MONEYYEAR");
			querysql = sql.replace("N_MONEYDAY", "N_MONEYYEAR");
		}

		return querysql + "#" + innersql;

	}

	/**
	 * 财政支出日报查询条件SQL
	 * 
	 * @generated
	 * @param idto
	 * @param sleBillType
	 *            --报表种类
	 * @param sleTenrptType
	 *            --旬报类型
	 * @param sleQuarterrptType
	 *            --季报类型
	 * @param sleHalfyearrptType
	 *            --半年报类型
	 * @param sleSubjectType
	 *            --科目类型
	 * @param sleSubjectAttribute
	 *            --科目属性
	 * @param smoveflag
	 *            --调拨标志
	 * @param sleMoneyUnit
	 *            --金额单位
	 * @return String
	 * @throws ITFEBizException
	 */
	public String makePayoutReportSQL(IDto idto, String sleBillType,
			String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType,
			String sleSubjectAttribute, String smoveflag, String sleMoneyUnit)
			throws ITFEBizException {

		TrTaxorgPayoutReportDto dto = (TrTaxorgPayoutReportDto) idto;
		String temprptDate = dto.getSrptdate();// 报表日期
		String temprptDateBeforeyear = ((Integer.parseInt(dto.getSrptdate()) - 10000) + "")
				.trim();// 上一年报表日期

		String rptDate = "";// 处理后的报表日期
		String rptDateBeforeyear = "";// 处理后的上一年报表日期

		String prorptDate = "";// 处理后的报表日期(旬、季、半年报)
		if (sleBillType.equals(StateConstant.REPORT_DAY)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// 日报年报
			rptDate = temprptDate;// 报表日期
			rptDateBeforeyear = temprptDateBeforeyear;// 上一年报表日期
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报

			if (sleTenrptType.equals(StateConstant.TENRPT_TOP)) {// 上旬
				rptDate = temprptDate.substring(0, 6) + "10";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();
			} else if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)) {// 中旬
				rptDate = temprptDate.substring(0, 6) + "20";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();

				prorptDate = temprptDate.substring(0, 6) + "10";

			} else if (sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 下旬
				String allrptdate = getRptDate(temprptDate.substring(0, 6));
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				prorptDate = temprptDate.substring(0, 6) + "20";
			}
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {// 月报

			String allrptdate = getRptDate(temprptDate.substring(0, 6));
			if (allrptdate.equals("=")) {
				throw new ITFEBizException("所查询报表不存在！");
			}
			rptDate = allrptdate.split("=")[0];
			rptDateBeforeyear = allrptdate.split("=")[1];
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FIRST)) {// 第一季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "03");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];
			} else if (sleQuarterrptType
					.equals(StateConstant.QUARTERRPT_SECOND)) {// 第二季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "03");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)) {// 第三季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "09");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第四季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "09");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_TOP)) {// 上半年
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

			} else if (sleHalfyearrptType
					.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
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

		String crrentsql = ""; // 当前报表日期报表SQL
		String crrentprosql = ""; // 当前报表日期报表SQL(处理旬、季、半年报)
		String beforesql = ""; // 上一年同期报表日期报表SQL
		String beforeprosql = ""; // 上一年同期报表日期报表SQL(处理旬、季、半年报)
		String trincomesql = " SELECT * FROM TR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDate + "'";// 根据报表日期查询当前表
		String htrincomesql = " SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDate + "'";// 根据报表日期查询历史表
		String incomesql = trincomesql + " union all " + htrincomesql;// 支出日报查询SQL
		String htrincomesqlbeforeyear = " SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDateBeforeyear + "'";// 根据上一年报表日期查询历史表
		String incomeprosql = "";

		String currentprosqlwhere = "";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报

			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 中旬下旬
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期支出条件
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);
			}
		}

		if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第二季度第三季度第四季度
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期支出条件
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);
			}
		}
		if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期支出条件
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);
			}
		}

		// 创建查询SQL
		StringBuffer sqlbuf = new StringBuffer();

		// 当前报表日期支出条件
		String currentsqlwhere = makePayoutReportwhere(idto, sleSubjectType,
				smoveflag, rptDate);
		// 上一年报表日期支出条件
		String lastyearsqlwhere = makePayoutReportwhere(idto, sleSubjectType,
				smoveflag, rptDateBeforeyear);

		// 亿元
		String yiyuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT)/100000000 as nmoneycurrent,SUM(N_MONEYYEAR)/100000000 as nmoneyyear ,sum(N_MONEYINCREMENTAL)/100000000 as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END) ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";
		// 万元
		String wanyuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT)/10000 as nmoneycurrent,SUM(N_MONEYYEAR)/10000 as nmoneyyear ,sum(N_MONEYINCREMENTAL)/10000 as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END) ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";
		// 元
		String yuansql = " select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT) as nmoneycurrent,SUM(N_MONEYYEAR) as nmoneyyear ,sum(N_MONEYINCREMENTAL) as nmoneyincremental, sum(N_MONEYINCREMENTAL)*100/CASE WHEN SUM(D_MONEYGROWTHRATE) =0 THEN (CASE WHEN sum(N_MONEYINCREMENTAL)=0 THEN 1 ELSE sum(N_MONEYINCREMENTAL) END) ELSE SUM(D_MONEYGROWTHRATE) END  as dmoneygrowthrate  from ( ";

		// 对不同的金额单位进行判断，然后对统计结果进行整理
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// 亿元
			sqlbuf.append(yiyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// 万元
			sqlbuf.append(wanyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// 元
			sqlbuf.append(yuansql);
		}

		// 内部查询SQL
		StringBuffer innersqlbuf = new StringBuffer();

		innersqlbuf
				.append(" select S_BUDGETSUBCODE AS S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYDAY) AS N_MONEYCURRENT, sum(N_MONEYALLYEAR) AS N_MONEYYEAR, sum(N_MONEYINCREMENTAL) AS N_MONEYINCREMENTAL,sum(D_MONEYGROWTHRATE) AS D_MONEYGROWTHRATE from (");

		if (null == sleSubjectAttribute
				|| "".equals(sleSubjectAttribute.trim())) {
			throw new ITFEBizException("查询条件：科目属性不能为空！");
		}

		// 根据科目属性（类、款、项、目）拼装不同的SQL
		if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_CLASS)) {// 类

			// 当前报表日期支出报表SQL
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

			// 上一年同期报表日期支出报表SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,3) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,3) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 3  group by substr(a.S_BUDGETSUBCODE,1,3),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_PAGE)) {// 款

			// 当前报表日期支出报表SQL
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

			// 上一年同期报表日期支出报表SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,5) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,5) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 5  group by substr(a.S_BUDGETSUBCODE,1,5),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute
				.equals(StateConstant.SUBJECTATTR_PROJECT)) {// 项

			// 当前报表日期支出报表SQL
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

			// 上一年同期报表日期支出报表SQL
			beforesql += "select substr(a.S_BUDGETSUBCODE,1,7) as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and substr(a.S_BUDGETSUBCODE,1,7) = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " and length(a.S_BUDGETSUBCODE) > 7  group by substr(a.S_BUDGETSUBCODE,1,7),b.S_SUBJECTNAME ";

		} else if (sleSubjectAttribute.equals(StateConstant.SUBJECTATTR_ITEM)) {// 目

			// 当前报表日期支出报表SQL
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

			// 上一年同期报表日期支出报表SQL
			beforesql += "select a.S_BUDGETSUBCODE as S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME,0 as N_MONEYDAY,0 as N_MONEYALLYEAR  ,-sum(a.N_MONEYYEAR) as N_MONEYINCREMENTAL ,sum(a.N_MONEYYEAR) as D_MONEYGROWTHRATE from ("
					+ htrincomesqlbeforeyear
					+ ") a,TS_BUDGETSUBJECT b Where b.S_SUBJECTCLASS = '"
					+ StateConstant.S_SUBJECTCLASS_PAYOUT2
					+ "' and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE "
					+ lastyearsqlwhere
					+ " group by S_BUDGETSUBCODE,S_SUBJECTNAME ";

		}

		String innerprosql = " select S_BUDGETSUBCODE AS S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYDAY) AS N_MONEYCURRENT, sum(N_MONEYALLYEAR) AS N_MONEYALLYEAR, sum(N_MONEYINCREMENTAL) AS N_MONEYINCREMENTAL,sum(D_MONEYGROWTHRATE) AS D_MONEYGROWTHRATE from (";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 中旬下旬
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYMONTH");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// 将当前报表日期SQL与上一年同期的SQL拼在一起进行组合统计
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第二季度第三季度第四季度
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYYEAR");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// 将当前报表日期SQL与上一年同期的SQL拼在一起进行组合统计
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				innerprosql = innerprosql.replace("N_MONEYCURRENT",
						"N_MONEYYEAR");
				innersqlbuf.append(innerprosql).append(crrentsql).append(
						crrentprosql).append(
						" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ").append(
						" union all ").append(beforesql);
			} else {
				innersqlbuf.append(crrentsql).append(beforesql);// 将当前报表日期SQL与上一年同期的SQL拼在一起进行组合统计
			}
		} else {
			innersqlbuf.append(crrentsql).append(beforesql);// 将当前报表日期SQL与上一年同期的SQL拼在一起进行组合统计
		}

		// 内部SQL组合排序
		innersqlbuf.append(" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String innersql = innersqlbuf.toString();

		// 最终SQL组合排序
		sqlbuf.append(innersql).append(
				" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String sql = sqlbuf.toString();

		String querysql = "";// 对不同的报表类型设置不同的查询SQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// 日报
			querysql = sql;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// 旬报、月报
			innersql = innersql.replace("N_MONEYDAY", "N_MONEYMONTH");
			querysql = sql.replace("N_MONEYDAY", "N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// 季报、半年报、年报
			innersql = innersql.replace("N_MONEYDAY", "N_MONEYYEAR");
			querysql = sql.replace("N_MONEYDAY", "N_MONEYYEAR");
		}

		return querysql + "#" + innersql;

	}

	/**
	 * 财政收入日报
	 * 
	 * @generated
	 * @param idto
	 * @param sbudgetsubcode
	 *            --预算科目代码(多个以逗号分割)
	 * @param sleBillType
	 *            --报表种类
	 * @param sleTenrptType
	 *            --旬报类型
	 * @param sleQuarterrptType
	 *            --季报类型
	 * @param sleHalfyearrptType
	 *            --半年报类型
	 * @param sleSubjectType
	 *            --科目类型
	 * @param smoveflag
	 *            --调拨标志
	 * @param sleMoneyUnit
	 *            --金额单位
	 * @return String
	 * @throws ITFEBizExceptionn
	 */
	public List makeIncomeReportBySubject(IDto idto, String sbudgetsubcode,
			String sleBillType, String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType, String smoveflag,
			String sleMoneyUnit) throws ITFEBizException {
		TrIncomedayrptDto dto = (TrIncomedayrptDto) idto;

		String temprptDate = dto.getSrptdate();// 报表日期
		String temprptDateBeforeyear = ((Integer.parseInt(dto.getSrptdate()) - 10000) + "")
				.trim();// 上一年报表日期

		String rptDate = "";// 处理后的报表日期
		String rptDateBeforeyear = "";// 处理后的上一年报表日期

		String prorptDate = "";// 处理后的报表日期(旬、季、半年报)
		String prorptDateBeforeyear = "";// 处理后的上一年报表日期(旬、季、半年报)
		if (sleBillType.equals(StateConstant.REPORT_DAY)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// 日报年报
			rptDate = temprptDate;// 报表日期
			rptDateBeforeyear = temprptDateBeforeyear;// 上一年报表日期
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报

			if (sleTenrptType.equals(StateConstant.TENRPT_TOP)) {// 上旬
				rptDate = temprptDate.substring(0, 6) + "10";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();
			} else if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)) {// 中旬
				rptDate = temprptDate.substring(0, 6) + "20";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();

				prorptDate = temprptDate.substring(0, 6) + "10";
				prorptDateBeforeyear = temprptDateBeforeyear.substring(0, 6)
						+ "10";

			} else if (sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 下旬
				String allrptdate = getRptDate(temprptDate.substring(0, 6));
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				prorptDate = temprptDate.substring(0, 6) + "20";
				prorptDateBeforeyear = temprptDateBeforeyear.substring(0, 6)
						+ "20";
			}
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {// 月报

			String allrptdate = getRptDate(temprptDate.substring(0, 6));
			if (allrptdate.equals("=")) {
				throw new ITFEBizException("所查询报表不存在！");
			}
			rptDate = allrptdate.split("=")[0];
			rptDateBeforeyear = allrptdate.split("=")[1];
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FIRST)) {// 第一季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "03");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];
			} else if (sleQuarterrptType
					.equals(StateConstant.QUARTERRPT_SECOND)) {// 第二季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "03");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)) {// 第三季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "09");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第四季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
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
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_TOP)) {// 上半年
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

			} else if (sleHalfyearrptType
					.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
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

		String crrentsql = ""; // 当前报表日期收入报表SQL
		String crrentprosql = ""; // 当前报表日期收入报表SQL(处理旬、季、半年报)
		String beforesql = ""; // 上一年同期报表日期收入报表SQL
		String beforeprosql = ""; // 上一年同期报表日期收入报表SQL(处理旬、季、半年报)

		String crrentquerysql = ""; // 当前报表日期收入报表SQL
		String procrrentquerysql = ""; // 当前报表日期收入报表SQL(处理旬、季、半年报)
		String beforequerysql = ""; // 上一年同期报表日期收入报表SQL
		String probeforequerysql = ""; // 上一年同期报表日期收入报表SQL(处理旬、季、半年报)

		String trincomesql = " SELECT * FROM TR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDate + "'";// 根据报表日期查询当前表
		String htrincomesql = " SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDate + "'";// 根据报表日期查询历史表
		String incomesql = trincomesql + " union all " + htrincomesql;// 收入日报查询SQL
		String htrincomesqlbeforeyear = " SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE = '"
				+ rptDateBeforeyear + "'";// 根据上一年报表日期查询历史表
		String incomeprosql = "";
		String htrincomeprosqlbeforeyear = "";

		String currentprosqlwhere = "";
		String lastyearprosqlwhere = "";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报

			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 中旬下旬
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期收入条件
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

		if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第二季度第三季度第四季度
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期收入条件
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
		if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期收入条件
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

		// 创建查询SQL
		StringBuffer sqlbuf = new StringBuffer();

		// 当前报表日期收入条件
		String currentsqlwhere = makeIncomeReportwhere(idto, sleSubjectType,
				smoveflag, StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL, rptDate);
		// 上一年报表日期收入条件
		String lastyearsqlwhere = makeIncomeReportwhere(idto, sleSubjectType,
				smoveflag, StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL,
				rptDateBeforeyear);

		// 亿元
		String yiyuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5)/100000000 as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5)/100000000 as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";
		// 万元
		String wanyuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5)/10000 as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5)/10000 as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";
		// 元
		String yuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5) as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5) as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";

		// 对不同的金额单位进行判断，然后对统计结果进行整理
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// 亿元
			sqlbuf.append(yiyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// 万元
			sqlbuf.append(wanyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// 元
			sqlbuf.append(yuansql);
		}

		// 对多个预算科目代码进行SQL拼接
		String subjectcodes = "";
		String[] str = sbudgetsubcode.split(",");
		for (int i = 0; i < str.length; i++) {
			if (i == str.length - 1) {
				subjectcodes += "'" + str[i] + "'";
				break;
			}
			subjectcodes += "'" + str[i] + "',";
		}

		// 内部查询SQL
		StringBuffer innersqlbuf = new StringBuffer();
		// 对多个预算科目进行汇总统计
		StringBuffer currentbysubjectsbf = new StringBuffer();
		// 对单个占比预算科目代码进行汇总统计
		StringBuffer lastyearbysubjectsbf = new StringBuffer();

		String prooutersql = "select S_BUDGETSUBCODE as S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYCURRENT3) as N_MONEYCURRENT5,sum(N_MONEYCURRENT4) as N_MONEYCURRENT6,sum(D_MONEYGROWTH1) as D_MONEYGROWTH2,sum(D_MONEYCONTRIBUTION3) as D_MONEYCONTRIBUTION5,sum(D_MONEYCONTRIBUTION4) as D_MONEYCONTRIBUTION6,sum(D_MONEYRATIO3) as D_MONEYRATIO5,sum(D_MONEYRATIO4) as D_MONEYRATIO6,sum(N_MONEYALLYEAR3) as N_MONEYALLYEAR5  ,sum(N_MONEYALLYEAR4) as N_MONEYALLYEAR6  ,sum(D_MONEYYEARGROWTH1) as D_MONEYYEARGROWTH2 ,sum(D_MONEYYEARCONTRIBUTION3) as D_MONEYYEARCONTRIBUTION5,sum(D_MONEYYEARCONTRIBUTION4) as D_MONEYYEARCONTRIBUTION6,sum(D_MONEYYEARRATIO3) as D_MONEYYEARRATIO5,sum(D_MONEYYEARRATIO4) as D_MONEYYEARRATIO6 from (";

		String proinnersql = "select S_BUDGETSUBCODE as S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYCURRENT1) as N_MONEYCURRENT3,sum(N_MONEYCURRENT2) as N_MONEYCURRENT4,sum(D_MONEYGROWTH) as D_MONEYGROWTH1,sum(D_MONEYCONTRIBUTION1) as D_MONEYCONTRIBUTION3,sum(D_MONEYCONTRIBUTION2) as D_MONEYCONTRIBUTION4,sum(D_MONEYRATIO1) as D_MONEYRATIO3,sum(D_MONEYRATIO2) as D_MONEYRATIO4,sum(N_MONEYALLYEAR1) as N_MONEYALLYEAR3  ,sum(N_MONEYALLYEAR2) as N_MONEYALLYEAR4  ,sum(D_MONEYYEARGROWTH) as D_MONEYYEARGROWTH1 ,sum(D_MONEYYEARCONTRIBUTION1) as D_MONEYYEARCONTRIBUTION3,sum(D_MONEYYEARCONTRIBUTION2) as D_MONEYYEARCONTRIBUTION4,sum(D_MONEYYEARRATIO1) as D_MONEYYEARRATIO3,sum(D_MONEYYEARRATIO2) as D_MONEYYEARRATIO4 from (";

		// 当前报表日期收入报表SQL(占比预算科目)
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

		// 上一年同期报表日期收入报表SQL(占比预算科目)
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

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 中旬下旬
				// 对单个占比预算科目代码进行汇总统计
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
				// 对单个占比预算科目代码进行汇总统计
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第二季度第三季度第四季度
				// 对单个占比预算科目代码进行汇总统计
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
				// 对单个占比预算科目代码进行汇总统计
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				// 对单个占比预算科目代码进行汇总统计
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
				// 对单个占比预算科目代码进行汇总统计
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else {
			// 对单个占比预算科目代码进行汇总统计
			lastyearbysubjectsbf
					.append(prooutersql)
					.append(proinnersql)
					.append(crrentquerysql)
					.append(beforequerysql)
					.append(
							" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
		}

		String basesubjectsbf = lastyearbysubjectsbf.toString();

		String basesubjectquerysql = "";// 对不同的报表类型设置不同的查询SQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// 日报
			basesubjectquerysql = basesubjectsbf;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// 旬报、月报
			basesubjectquerysql = basesubjectsbf.replace("N_MONEYDAY",
					"N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// 季报、半年报、年报
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
			throw new ITFEBizException("查询出错", e);
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
			throw new ITFEBizException("所查询的占比预算科目报表不存在！");
		}

		// 当前报表日期收入报表SQL
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

		// 上一年同期报表日期收入报表SQL
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

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 中旬下旬

				// 对多个预算科目进行汇总统计
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

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);

			} else {
				// 对多个预算科目进行汇总统计
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第二季度第三季度第四季度
				// 对多个预算科目进行汇总统计
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

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);
			} else {
				// 对多个预算科目进行汇总统计
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				// 对多个预算科目进行汇总统计
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

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);
			} else {
				// 对多个预算科目进行汇总统计
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else {
			// 对多个预算科目进行汇总统计
			currentbysubjectsbf
					.append(prooutersql)
					.append(innerprosql)
					.append(crrentsql)
					.append(beforesql)
					.append(
							" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

			// 对多个预算科目和单个占比预算科目代码进行汇总统计
			innersqlbuf.append(currentbysubjectsbf);
		}

		String innersql = innersqlbuf.toString();

		// 最终SQL组合排序
		sqlbuf.append(innersql).append(
				" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String sql = sqlbuf.toString();

		String querysql = "";// 对不同的报表类型设置不同的查询SQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// 日报
			querysql = sql;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// 旬报、月报
			querysql = sql.replace("N_MONEYDAY", "N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// 季报、半年报、年报
			querysql = sql.replace("N_MONEYDAY", "N_MONEYYEAR");
		}

		// 打印sql
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
			throw new ITFEBizException("查询出错", e);
		}

	}

	/**
	 * 财政支出日报
	 * 
	 * @generated
	 * @param idto
	 * @param sbudgetsubcode
	 *            --预算科目代码(多个以逗号分割)
	 * @param sleBillType
	 *            --报表种类
	 * @param sleTenrptType
	 *            --旬报类型
	 * @param sleQuarterrptType
	 *            --季报类型
	 * @param sleHalfyearrptType
	 *            --半年报类型
	 * @param sleSubjectType
	 *            --科目类型
	 * @param smoveflag
	 *            --调拨标志
	 * @param sleMoneyUnit
	 *            --金额单位
	 * @return String
	 * @throws ITFEBizExceptionn
	 */
	public List makePayoutReportBySubject(IDto idto, String sbudgetsubcode,
			String sleBillType, String sleTenrptType, String sleQuarterrptType,
			String sleHalfyearrptType, String sleSubjectType, String smoveflag,
			String sleMoneyUnit) throws ITFEBizException {
		TrTaxorgPayoutReportDto dto = (TrTaxorgPayoutReportDto) idto;

		String temprptDate = dto.getSrptdate();// 报表日期
		String temprptDateBeforeyear = ((Integer.parseInt(dto.getSrptdate()) - 10000) + "")
				.trim();// 上一年报表日期

		String rptDate = "";// 处理后的报表日期
		String rptDateBeforeyear = "";// 处理后的上一年报表日期

		String prorptDate = "";// 处理后的报表日期(旬、季、半年报)
		String prorptDateBeforeyear = "";// 处理后的上一年报表日期(旬、季、半年报)
		if (sleBillType.equals(StateConstant.REPORT_DAY)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// 日报年报
			rptDate = temprptDate;// 报表日期
			rptDateBeforeyear = temprptDateBeforeyear;// 上一年报表日期
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报

			if (sleTenrptType.equals(StateConstant.TENRPT_TOP)) {// 上旬
				rptDate = temprptDate.substring(0, 6) + "10";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();
			} else if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)) {// 中旬
				rptDate = temprptDate.substring(0, 6) + "20";
				rptDateBeforeyear = ((Integer.parseInt(rptDate) - 10000) + "")
						.trim();

				prorptDate = temprptDate.substring(0, 6) + "10";
				prorptDateBeforeyear = temprptDateBeforeyear.substring(0, 6)
						+ "10";

			} else if (sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 下旬
				String allrptdate = getRptDate(temprptDate.substring(0, 6));
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				prorptDate = temprptDate.substring(0, 6) + "20";
				prorptDateBeforeyear = temprptDateBeforeyear.substring(0, 6)
						+ "20";
			}
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {// 月报

			String allrptdate = getRptDate(temprptDate.substring(0, 6));
			if (allrptdate.equals("=")) {
				throw new ITFEBizException("所查询报表不存在！");
			}
			rptDate = allrptdate.split("=")[0];
			rptDateBeforeyear = allrptdate.split("=")[1];
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FIRST)) {// 第一季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "03");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];
			} else if (sleQuarterrptType
					.equals(StateConstant.QUARTERRPT_SECOND)) {// 第二季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "03");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)) {// 第三季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "09");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

				String rptdate = getRptDate(temprptDate.substring(0, 4) + "06");
				if (rptdate.equals("=")) {
					rptdate = getRptDate(temprptDate.substring(0, 4));
				}
				prorptDate = rptdate.split("=")[0];
				prorptDateBeforeyear = rptdate.split("=")[1];
			} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第四季度
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
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
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_TOP)) {// 上半年
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "06");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
				}
				rptDate = allrptdate.split("=")[0];
				rptDateBeforeyear = allrptdate.split("=")[1];

			} else if (sleHalfyearrptType
					.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				String allrptdate = getRptDate(temprptDate.substring(0, 4)
						+ "12");
				if (allrptdate.equals("=")) {
					throw new ITFEBizException("所查询报表不存在！");
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

		String crrentsql = ""; // 当前报表日期支出报表SQL
		String crrentprosql = ""; // 当前报表日期支出报表SQL(处理旬、季、半年报)
		String beforesql = ""; // 上一年同期报表日期支出报表SQL
		String beforeprosql = ""; // 上一年同期报表日期支出报表SQL(处理旬、季、半年报)

		String crrentquerysql = ""; // 当前报表日期支出报表SQL
		String procrrentquerysql = ""; // 当前报表日期支出报表SQL(处理旬、季、半年报)
		String beforequerysql = ""; // 上一年同期报表日期支出报表SQL
		String probeforequerysql = ""; // 上一年同期报表日期支出报表SQL(处理旬、季、半年报)

		String trincomesql = " SELECT * FROM TR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDate + "'";// 根据报表日期查询当前表
		String htrincomesql = " SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDate + "'";// 根据报表日期查询历史表
		String incomesql = trincomesql + " union all " + htrincomesql;// 支出日报查询SQL
		String htrincomesqlbeforeyear = " SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE = '"
				+ rptDateBeforeyear + "'";// 根据上一年报表日期查询历史表
		String incomeprosql = "";
		String htrincomeprosqlbeforeyear = "";

		String currentprosqlwhere = "";
		String lastyearprosqlwhere = "";

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报

			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 中旬下旬
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期支出条件
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);

				htrincomeprosqlbeforeyear = htrincomesqlbeforeyear.replace(
						rptDateBeforeyear, prorptDateBeforeyear);
				lastyearprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDateBeforeyear);

			}
		}

		if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报

			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第二季度第三季度第四季度
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期支出条件
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);

				htrincomeprosqlbeforeyear = htrincomesqlbeforeyear.replace(
						rptDateBeforeyear, prorptDateBeforeyear);
				lastyearprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDateBeforeyear);

			}
		}
		if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报

			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				incomeprosql = incomesql.replace(rptDate, prorptDate);
				// 当前报表日期支出条件
				currentprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDate);

				htrincomeprosqlbeforeyear = htrincomesqlbeforeyear.replace(
						rptDateBeforeyear, prorptDateBeforeyear);
				lastyearprosqlwhere = makePayoutReportwhere(idto,
						sleSubjectType, smoveflag, prorptDateBeforeyear);

			}
		}

		// 创建查询SQL
		StringBuffer sqlbuf = new StringBuffer();

		// 当前报表日期支出条件
		String currentsqlwhere = makePayoutReportwhere(idto, sleSubjectType,
				smoveflag, rptDate);
		// 上一年报表日期支出条件
		String lastyearsqlwhere = makePayoutReportwhere(idto, sleSubjectType,
				smoveflag, rptDateBeforeyear);

		// 亿元
		String yiyuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5)/100000000 as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5)/100000000 as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";
		// 万元
		String wanyuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5)/10000 as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5)/10000 as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";
		// 元
		String yuansql = "select S_BUDGETSUBCODE as sbudgetsubcode,S_BUDGETSUBNAME as sbudgetsubname,SUM(N_MONEYCURRENT5) as nmoneycurrent,sum(D_MONEYGROWTH2)*100/CASE WHEN SUM(N_MONEYCURRENT6)=0 THEN 1 ELSE SUM(N_MONEYCURRENT6) END  as dmoneygrowth,sum(D_MONEYCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYCONTRIBUTION6) END  as dmoneycontribution,sum(D_MONEYRATIO5)*100/CASE WHEN sum(D_MONEYRATIO6)=0 THEN 1 ELSE sum(D_MONEYRATIO6) END  as dmoneyratio,sum(N_MONEYALLYEAR5) as nmoneyyear ,sum(D_MONEYYEARGROWTH2)*100/CASE WHEN sum(N_MONEYALLYEAR6)=0 THEN 1 ELSE sum(N_MONEYALLYEAR6) END  as dmoneyyeargrowth ,sum(D_MONEYYEARCONTRIBUTION5)*100/CASE WHEN sum(D_MONEYYEARCONTRIBUTION6)=0 THEN 1 ELSE sum(D_MONEYYEARCONTRIBUTION6) END  as dmoneyyearcontribution,sum(D_MONEYYEARRATIO5)*100/CASE WHEN sum(D_MONEYYEARRATIO6)=0 THEN 1 ELSE sum(D_MONEYYEARRATIO6) END  as dmoneyyearratio from (";

		// 对不同的金额单位进行判断，然后对统计结果进行整理
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// 亿元
			sqlbuf.append(yiyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// 万元
			sqlbuf.append(wanyuansql);
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// 元
			sqlbuf.append(yuansql);
		}

		// 对多个预算科目代码进行SQL拼接
		String subjectcodes = "";
		String[] str = sbudgetsubcode.split(",");
		for (int i = 0; i < str.length; i++) {
			if (i == str.length - 1) {
				subjectcodes += "'" + str[i] + "'";
				break;
			}
			subjectcodes += "'" + str[i] + "',";
		}

		// 内部查询SQL
		StringBuffer innersqlbuf = new StringBuffer();
		// 对多个预算科目进行汇总统计
		StringBuffer currentbysubjectsbf = new StringBuffer();
		// 对单个占比预算科目代码进行汇总统计
		StringBuffer lastyearbysubjectsbf = new StringBuffer();

		String prooutersql = "select S_BUDGETSUBCODE as S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYCURRENT3) as N_MONEYCURRENT5,sum(N_MONEYCURRENT4) as N_MONEYCURRENT6,sum(D_MONEYGROWTH1) as D_MONEYGROWTH2,sum(D_MONEYCONTRIBUTION3) as D_MONEYCONTRIBUTION5,sum(D_MONEYCONTRIBUTION4) as D_MONEYCONTRIBUTION6,sum(D_MONEYRATIO3) as D_MONEYRATIO5,sum(D_MONEYRATIO4) as D_MONEYRATIO6,sum(N_MONEYALLYEAR3) as N_MONEYALLYEAR5  ,sum(N_MONEYALLYEAR4) as N_MONEYALLYEAR6  ,sum(D_MONEYYEARGROWTH1) as D_MONEYYEARGROWTH2 ,sum(D_MONEYYEARCONTRIBUTION3) as D_MONEYYEARCONTRIBUTION5,sum(D_MONEYYEARCONTRIBUTION4) as D_MONEYYEARCONTRIBUTION6,sum(D_MONEYYEARRATIO3) as D_MONEYYEARRATIO5,sum(D_MONEYYEARRATIO4) as D_MONEYYEARRATIO6 from (";

		String proinnersql = "select S_BUDGETSUBCODE as S_BUDGETSUBCODE,S_BUDGETSUBNAME as S_BUDGETSUBNAME,sum(N_MONEYCURRENT1) as N_MONEYCURRENT3,sum(N_MONEYCURRENT2) as N_MONEYCURRENT4,sum(D_MONEYGROWTH) as D_MONEYGROWTH1,sum(D_MONEYCONTRIBUTION1) as D_MONEYCONTRIBUTION3,sum(D_MONEYCONTRIBUTION2) as D_MONEYCONTRIBUTION4,sum(D_MONEYRATIO1) as D_MONEYRATIO3,sum(D_MONEYRATIO2) as D_MONEYRATIO4,sum(N_MONEYALLYEAR1) as N_MONEYALLYEAR3  ,sum(N_MONEYALLYEAR2) as N_MONEYALLYEAR4  ,sum(D_MONEYYEARGROWTH) as D_MONEYYEARGROWTH1 ,sum(D_MONEYYEARCONTRIBUTION1) as D_MONEYYEARCONTRIBUTION3,sum(D_MONEYYEARCONTRIBUTION2) as D_MONEYYEARCONTRIBUTION4,sum(D_MONEYYEARRATIO1) as D_MONEYYEARRATIO3,sum(D_MONEYYEARRATIO2) as D_MONEYYEARRATIO4 from (";

		// 当前报表日期收入报表SQL(占比预算科目)
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

		// 上一年同期报表日期收入报表SQL(占比预算科目)
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

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 中旬下旬
				// 对单个占比预算科目代码进行汇总统计
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
				// 对单个占比预算科目代码进行汇总统计
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第二季度第三季度第四季度
				// 对单个占比预算科目代码进行汇总统计
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
				// 对单个占比预算科目代码进行汇总统计
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				// 对单个占比预算科目代码进行汇总统计
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
				// 对单个占比预算科目代码进行汇总统计
				lastyearbysubjectsbf
						.append(prooutersql)
						.append(proinnersql)
						.append(crrentquerysql)
						.append(beforequerysql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
			}
		} else {
			// 对单个占比预算科目代码进行汇总统计
			lastyearbysubjectsbf
					.append(prooutersql)
					.append(proinnersql)
					.append(crrentquerysql)
					.append(beforequerysql)
					.append(
							" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");
		}

		String basesubjectsbf = lastyearbysubjectsbf.toString();

		String basesubjectquerysql = "";// 对不同的报表类型设置不同的查询SQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// 日报
			basesubjectquerysql = basesubjectsbf;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// 旬报、月报
			basesubjectquerysql = basesubjectsbf.replace("N_MONEYDAY",
					"N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// 季报、半年报、年报
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
			throw new ITFEBizException("查询出错", e);
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
			throw new ITFEBizException("所查询的占比预算科目报表不存在！");
		}

		// 当前报表日期收入报表SQL
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

		// 上一年同期报表日期收入报表SQL
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

		if (sleBillType.equals(StateConstant.REPORT_TEN)) {// 旬报
			if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)
					|| sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {// 中旬下旬

				// 对多个预算科目进行汇总统计
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

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);

			} else {
				// 对多个预算科目进行汇总统计
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {// 季报
			if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)
					|| sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {// 第二季度第三季度第四季度
				// 对多个预算科目进行汇总统计
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

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);
			} else {
				// 对多个预算科目进行汇总统计
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {// 半年报
			if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {// 下半年
				// 对多个预算科目进行汇总统计
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

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);
			} else {
				// 对多个预算科目进行汇总统计
				currentbysubjectsbf
						.append(prooutersql)
						.append(innerprosql)
						.append(crrentsql)
						.append(beforesql)
						.append(
								" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

				// 对多个预算科目和单个占比预算科目代码进行汇总统计
				innersqlbuf.append(currentbysubjectsbf);
			}
		} else {
			// 对多个预算科目进行汇总统计
			currentbysubjectsbf
					.append(prooutersql)
					.append(innerprosql)
					.append(crrentsql)
					.append(beforesql)
					.append(
							" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME  ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

			// 对多个预算科目和单个占比预算科目代码进行汇总统计
			innersqlbuf.append(currentbysubjectsbf);
		}

		String innersql = innersqlbuf.toString();

		// 最终SQL组合排序
		sqlbuf.append(innersql).append(
				" ) group by S_BUDGETSUBCODE,S_BUDGETSUBNAME ");

		String sql = sqlbuf.toString();

		String querysql = "";// 对不同的报表类型设置不同的查询SQL
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {// 日报
			querysql = sql;
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)
				|| sleBillType.equals(StateConstant.REPORT_MONTH)) {// 旬报、月报
			querysql = sql.replace("N_MONEYDAY", "N_MONEYMONTH");
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)
				|| sleBillType.equals(StateConstant.REPORT_HALFYEAR)
				|| sleBillType.equals(StateConstant.REPORT_YEAR)) {// 季报、半年报、年报
			querysql = sql.replace("N_MONEYDAY", "N_MONEYYEAR");
		}

		// 打印sql
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
			throw new ITFEBizException("查询出错", e);
		}

	}

	/**
	 * 财政支出日报
	 * 
	 * @generated
	 * @param startqueryyear
	 *            --开始年度
	 * @param endqueryyear
	 *            --结束年度
	 * @param startdate
	 *            --开始日期
	 * @param enddate
	 *            --结束日期
	 * @param sleTreCode
	 *            --国库代码
	 * @param sleOfFlag
	 *            --辖属标志
	 * @param sleMoneyUnit
	 *            --金额单位
	 * @return List
	 * @throws ITFEBizExceptionn
	 */
	public List stockCountQueryList(String sorgcode, String startyear,
			String endyear, String startdate, String enddate,
			String sleTreCode, String sleOfFlag, String sleMoneyUnit)
			throws ITFEBizException {
		// 起始年度的起始日期
		String startyearstartdate = startyear.trim() + startdate.trim();

		// 起始年度的结束日期
		String startyearenddate = startyear.trim() + enddate.trim();

		// 结束年度的起始日期
		String endyearstartdate = endyear.trim() + startdate.trim();

		// 结束年度的结束日期
		String endyearenddate = endyear.trim() + enddate.trim();

		// 起始年度查询条件Sql
		String startyearsqlwhere = "where 1=1 ";

		// 结束年度年度查询条件Sql
		String endyearsqlwhere = "where 1=1 ";
		// 判断辖属标志
		if (sleOfFlag.equals(MsgConstant.RULE_SIGN_ALL)) {// 全辖

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
		} else if (sleOfFlag.equals(MsgConstant.RULE_SIGN_SELF)) {// 本级

			startyearsqlwhere += " and S_ORGCODE = '" + sorgcode
					+ "' and S_TRECODE = '" + sleTreCode
					+ "'  and S_RPTDATE between '" + startyearstartdate
					+ "' and '" + startyearenddate + "'";

			endyearsqlwhere += " and S_ORGCODE = '" + sorgcode
					+ "' and S_TRECODE = '" + sleTreCode
					+ "'  and S_RPTDATE between '" + endyearstartdate
					+ "' and '" + endyearenddate + "'";
		}
		// 构建起始年度的查询SQL
		StringBuffer startyearsbf = new StringBuffer();
		// 构建结束年度的查询SQL
		StringBuffer endyearsbf = new StringBuffer();

		// 起始年度查询当前表和历史表的SQL
		String startyearallsql = "select * from TR_STOCKDAYRPT "
				+ startyearsqlwhere
				+ " union all select * from HTR_STOCKDAYRPT "
				+ startyearsqlwhere;
		// 结束年度查询当前表和历史表的SQL
		String endyearallsql = "select * from TR_STOCKDAYRPT "
				+ endyearsqlwhere + " union all select * from HTR_STOCKDAYRPT "
				+ endyearsqlwhere;

		// 起始年度的查询SQL
		String startyearquerysql = "SELECT avg(N_MONEYTODAY),stddev(N_MONEYTODAY) FROM ("
				+ startyearallsql + ") ";

		// 结束年度的查询SQL
		String endyearquerysql = "SELECT avg(N_MONEYTODAY),stddev(N_MONEYTODAY) FROM ("
				+ endyearallsql + ") ";

		// 对不同的金额单位进行判断，然后对统计结果进行整理
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// 亿元
			startyearquerysql = startyearquerysql.replace("N_MONEYTODAY",
					"N_MONEYTODAY/100000000");

			endyearquerysql = endyearquerysql.replace("N_MONEYTODAY",
					"N_MONEYTODAY/100000000");

		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// 万元
			startyearquerysql = startyearquerysql.replace("N_MONEYTODAY",
					"N_MONEYTODAY/10000");

			endyearquerysql = endyearquerysql.replace("N_MONEYTODAY",
					"N_MONEYTODAY/10000");

		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// 元
		}

		SQLExecutor execute = null;
		SQLResults res1 = null;
		SQLResults res2 = null;
		try {
			execute = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			// 起始年度查询结果集
			res1 = execute.runQuery(startyearquerysql.toString());
			// 判断是否为同一年度
			if (!startyear.equals(endyear)) {
				// 结束年度查询结果集
				res2 = execute.runQueryCloseCon(endyearquerysql.toString());
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错", e);
		}

		String exceptionMessgae = "";

		if (null != res1 && res1.getRowCount() > 0
				&& null != res1.getBigDecimal(0, 0)
				&& null != res1.getBigDecimal(0, 1)) {
			startyearsbf
					.append("select concat('"
							+ startyear
							+ "','年度') as syear ,"
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
			exceptionMessgae += "所查询的" + startyear + "年度库存统计分析报表不存在！\r\n";
		}

		// 判断是否为同一年度
		if (!startyear.equals(endyear)) {
			if (null != res2 && res2.getRowCount() > 0
					&& null != res2.getBigDecimal(0, 0)
					&& null != res2.getBigDecimal(0, 1)) {
				endyearsbf
						.append("select concat('"
								+ endyear
								+ "','年度') as syear ,"
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
				exceptionMessgae += "所查询的" + endyear + "年度库存统计分析报表不存在！";
			}
		}

		if (!exceptionMessgae.equals("")) {
			throw new ITFEBizException(exceptionMessgae);
		}

		// 最终查询SQL
		StringBuffer querysqlsbf = new StringBuffer("");

		querysqlsbf.append(startyearsbf);

		// 判断是否为同一年度
		if (!startyear.equals(endyear)) {
			querysqlsbf.append(" union all ").append(endyearsbf);
		}

		String querysql = querysqlsbf.toString();

		// 对不同的金额单位进行判断，然后对统计结果进行整理
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			// 亿元
			querysql = querysql.replace("N_MONEYTODAY",
					"N_MONEYTODAY/100000000");
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			// 万元
			querysql = querysql.replace("N_MONEYTODAY", "N_MONEYTODAY/10000");
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			// 元
		}

		// 打印sql
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
			throw new ITFEBizException("查询出错", e);
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
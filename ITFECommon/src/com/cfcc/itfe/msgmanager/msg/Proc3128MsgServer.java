/**
 * 收入日报表信息
 */
package com.cfcc.itfe.msgmanager.msg;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.webservice.FinReportService;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 
 * @author wangtuo
 * 
 */
public class Proc3128MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3128MsgServer.class);

	/**
	 * 报文信息处理
	 * 
	 * @param eventContext
	 * @throws ITFEBizException
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MuleMessage muleMessage = eventContext.getMessage();
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		/**
		 * 解析报文头 headMap
		 */
		String orgcode = (String) headMap.get("DES");// 接收机构代码
		String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号
		String msgref = (String) headMap.get("MsgRef"); // 报文id号
		/**
		 * 解析信息头 取得财政代码与报表日期 msgMap --> BillHead3128
		 */
		HashMap billheadMap = (HashMap) msgMap.get("BillHead3128");
		String finOrgCode = (String) billheadMap.get("FinOrgCode"); // 财政机关代码
		String reportDate = (String) billheadMap.get("ReportDate"); // 报表所属日期
		String trecode = ""; // 国库代码
		boolean iskucun = false;
		boolean isshouru = false;
		String sbookorgcode = (String) headMap.get("DES");

		// 查询报表是否接收过
		String sql = "select count(1) from TR_INCOMEDAYRPT where s_FinOrgCode = ? and s_RptDate = ? fetch first 1 rows only with ur";
		String sql1 = "select count(1) from HTR_INCOMEDAYRPT where s_FinOrgCode = ? and s_RptDate = ? fetch first 1 rows only with ur";
		SQLResults sr;
		SQLExecutor sqlExec;
		SQLResults sr1;
		SQLExecutor sqlExec1;
		try {
			//判断是否是是国库维护的财政机构代码，防止维护了两个财政，造成入库流水重复。
			// 财政机构代码缓存key为财政代码
			HashMap<String, TsConvertfinorgDto> mapFincInfoByFinc = SrvCacheFacade.cacheFincInfoByFinc(null);
			if (!mapFincInfoByFinc.containsKey(finOrgCode)) {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
			// 根据财政代码查询核算主体
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null)
					.get(finOrgCode);
			if (null != _dto) {
				sbookorgcode = _dto.getSorgcode();
			}
			// 判断当前表 是否有财政当前当天的数据
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			sqlExec.addParam(finOrgCode);
			sqlExec.addParam(reportDate);
			sr = sqlExec.runQueryCloseCon(sql);
//			// 判断历史表 是否有财政当天的的数据
//			sqlExec1 = DatabaseFacade.getDb().getSqlExecutorFactory()
//					.getSQLExecutor();
//
//			sqlExec1.addParam(finOrgCode);
//			sqlExec1.addParam(reportDate);
//			sr1 = sqlExec1.runQueryCloseCon(sql1);
			//不再判断历史表，即使历史表中已经存在当日的数据，也允许重复申请
			if (sr.getLong(0, 0) == 0 ) { // 说明已经没有接收过，程序重新解析

				/**
				 * 解析正常期预算收入报表体 msgMap --> NrBudget3128R
				 */
				HashMap nrbudget3128 = (HashMap) msgMap.get("NrBudget3128");
				if (null != nrbudget3128) {
					List nrbudgetbill3128 = (List) nrbudget3128
							.get("NrBudgetBill3128");
					if (null == nrbudgetbill3128
							|| nrbudgetbill3128.size() == 0) {
						// 没有正常期收入报表数据
					} else {
						int incomecount = nrbudgetbill3128.size();
						// 存库用LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {
							// CFX->MSG->NrBudget3128->NrBudgetBill3128
							HashMap billmap = (HashMap) nrbudgetbill3128.get(i);

							// 财政报表-预算收入日报表(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // 征收机关代码
							trecode = (String) billmap.get("TreCode");
							tmpdto.setStrecode((String) billmap.get("TreCode")); // 国库主体代码
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // 预算种类
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // 预算级次代码
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // 预算科目
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // 预算科目名称
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // 日累计金额
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // 旬累计金额
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // 月累计金额
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // 季累计金额
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // 年累计金额

							tmpdto.setSrptdate(reportDate); // 报表日期
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // 辖属标志
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志
							tmpdto.setSfinorgcode(finOrgCode);// 财政机关代码
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);// 正常期预算收入报表体
							tmpdto.setSdividegroup("0");// 分成组标志

							incomelist.add(tmpdto);

							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("保存正常期收入报表时出现数据库异常!", e);
									throw new ITFEBizException(
											"保存正常期收入报表时出现数据库异常!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * 解析正常期退库报表体 msgMap --> NrDrawBack3128
				 */
				HashMap nrdrawback3128 = (HashMap) msgMap.get("NrDrawBack3128");
				if (null != nrdrawback3128) {
					// MSG-> NrDrawBack3128-> NrDrawBackBill3128
					List nrdrawbackbill3128 = (List) nrdrawback3128
							.get("NrDrawBackBill3128");
					if (null == nrdrawbackbill3128
							|| nrdrawbackbill3128.size() == 0) {
						// 没有正常期退库报表数据
					} else {

						int incomecount = nrdrawbackbill3128.size();
						// 存库用LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->NrBudget3128->NrBudgetBill3128
							HashMap billmap = (HashMap) nrdrawbackbill3128
									.get(i);

							// 财政报表-预算收入日报表(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // 征收机关代码
							tmpdto.setStrecode((String) billmap.get("TreCode")); // 国库主体代码
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // 预算种类
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // 预算级次代码
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // 预算科目
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // 预算科目名称
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // 日累计金额
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // 旬累计金额
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // 月累计金额
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // 季累计金额
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // 年累计金额

							tmpdto.setSrptdate(reportDate); // 报表日期
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // 辖属标志
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志
							tmpdto.setSfinorgcode(finOrgCode);// 财政机关代码
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_NRDRAWBACKBILL);// 正常期退库报表
							tmpdto.setSdividegroup("0");// 分成组标志

							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("保存正常期退库报表时出现数据库异常!", e);
									throw new ITFEBizException(
											"保存正常期退库报表时出现数据库异常!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * 解析正常期调拨收入报表体 msgMap --> NrRemove3128
				 */
				HashMap nrremove3128 = (HashMap) msgMap.get("NrRemove3128");
				if (null != nrremove3128) {
					List nrremovebill3128 = (List) nrremove3128
							.get("NrRemoveBill3128");
					if (null == nrremovebill3128
							|| nrremovebill3128.size() == 0) {
						// 没有正常期调拨收入报表数据
						// <NrRemoveBill3128>
					} else {

						int incomecount = nrremovebill3128.size();
						// 存库用LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->NrRemove3128->NrRemoveBill3128
							HashMap billmap = (HashMap) nrremovebill3128.get(i);
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							// 财政报表-预算收入日报表(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();

							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // 征收机关代码
							tmpdto.setStrecode((String) billmap.get("TreCode")); // 国库主体代码
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // 预算种类
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // 预算级次代码
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // 预算科目
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // 预算科目名称
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // 日累计金额
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // 旬累计金额
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // 月累计金额
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // 季累计金额
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // 年累计金额

							tmpdto.setSrptdate(reportDate); // 报表日期
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // 辖属标志
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志
							tmpdto.setSfinorgcode(finOrgCode);// 财政机关代码
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_NRREMOVEBILL);// 正常期调拨收入报表
							tmpdto.setSdividegroup("0");// 分成组标志
							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("保存正常期调拨收入报表时出现数据库异常!", e);
									throw new ITFEBizException(
											"保存正常期调拨收入报表时出现数据库异常!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * 解析正常期总额分成报表体 msgMap --> Amount3128
				 */
				HashMap nramountmap = (HashMap) msgMap.get("Amount3128");
				if (null != nramountmap) {
					List amountbill3128 = (List) nramountmap
							.get("AmountBill3128");
					if (null == amountbill3128 || amountbill3128.size() == 0) {
						// 没有正常期总额分成报表数据
						// <AmountBill3128>
					} else {

						int incomecount = amountbill3128.size();
						// 存库用LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {
							// CFX->MSG->Amount3128->AmountBill3128
							HashMap billmap = (HashMap) amountbill3128.get(i);

							// 财政报表-预算收入日报表(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode(sendorgcode); // 征收机关代码
							tmpdto.setStrecode((String) billmap.get("TreCode")); // 国库主体代码
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // 预算种类
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // 预算级次代码
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // 预算科目
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // 预算科目名称
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // 日累计金额
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // 旬累计金额
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // 月累计金额
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // 季累计金额
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // 年累计金额

							tmpdto.setSrptdate(reportDate); // 报表日期
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // 辖属标志
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志
							tmpdto.setSfinorgcode(finOrgCode);// 财政机关代码
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_AMOUNTBILL);// 正常期总额分成报表
							tmpdto.setSdividegroup("0");// 分成组标志

							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("保存正常期总额分成报表时出现数据库异常!", e);
									throw new ITFEBizException(
											"保存正常期总额分成报表时出现数据库异常!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * 解析正常期共享分成报表体 msgMap --> NrShare3128
				 */
				HashMap nrsharemap = (HashMap) msgMap.get("NrShare3128");
				if (null != nrsharemap) {
					List nrsharebill3128 = (List) nrsharemap
							.get("NrShareBill3128");
					if (null == nrsharebill3128 || nrsharebill3128.size() == 0) {
						// 没有正常期共享分成报表数据
						// <NrShareBill3128>
					} else {

						int incomecount = nrsharebill3128.size();
						// 存库用LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->NrShare3128->NrShareBill3128
							HashMap billmap = (HashMap) nrsharebill3128.get(i);

							// 财政报表-预算收入日报表(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // 征收机关代码
							tmpdto.setStrecode((String) billmap.get("TreCode")); // 国库主体代码
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // 预算种类
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // 预算级次代码
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // 预算科目
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // 预算科目名称
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // 日累计金额
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // 旬累计金额
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // 月累计金额
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // 季累计金额
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // 年累计金额

							tmpdto.setSrptdate(reportDate); // 报表日期
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // 辖属标志
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志
							tmpdto.setSfinorgcode(finOrgCode);// 财政机关代码
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_NRSHAREBILL);// 正常期共享分成报表
							tmpdto.setSdividegroup((String) billmap
									.get("DivideGroup"));// 分成组标志
							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("保存正常期共享分成报表时出现数据库异常!", e);
									throw new ITFEBizException(
											"保存正常期共享分成报表时出现数据库异常!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * 解析调整期预算收入报表体 msgMap --> TrBudget3128
				 */
				HashMap trincomemap = (HashMap) msgMap.get("TrBudget3128");
				if (null != trincomemap) {
					List trim_incomelist = (List) trincomemap
							.get("TrBudgetBill3128");
					if (null == trim_incomelist || trim_incomelist.size() == 0) {
						// 没有调整期预算收入报表数据
					} else {

						int incomecount = trim_incomelist.size();
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {
							HashMap billmap = (HashMap) trim_incomelist.get(i);
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // 征收机关代码
							tmpdto.setStrecode((String) billmap.get("TreCode")); // 国库主体代码
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // 预算种类
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // 预算级次代码
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // 预算科目
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // 预算科目名称
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // 日累计金额
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // 旬累计金额
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // 月累计金额
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // 季累计金额
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // 年累计金额

							tmpdto.setSrptdate(reportDate); // 报表日期
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // 辖属标志
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_TRIM); // 调整期标志
							tmpdto.setSfinorgcode(finOrgCode);// 财政机关代码
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_TRBUDGETBILL);// 调整期预算收入报表
							tmpdto.setSdividegroup("0");// 分成组标志

							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("保存调整期收入报表时出现数据库异常!", e);
									throw new ITFEBizException(
											"保存调整期收入报表时出现数据库异常!", e);
								}
								incomelist.clear();
							}
						}
					}
				}
				/**
				 * 解析调整期退库报表体 msgMap --> TrDrawBack3128
				 */
				HashMap trdwbkmap = (HashMap) msgMap.get("TrDrawBack3128");
				if (null != trdwbkmap) {
					List trdrawbackbill3128 = (List) trdwbkmap
							.get("TrDrawBackBill3128");
					if (null == trdrawbackbill3128
							|| trdrawbackbill3128.size() == 0) {
						// 没有调整期退库报表数据
						// <TrDrawBackBill3128>
					} else {

						int incomecount = trdrawbackbill3128.size();
						// 存库用LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->TrDrawBack3128->TrDrawBackBill3128
							HashMap billmap = (HashMap) trdrawbackbill3128
									.get(i);

							// 财政报表-预算收入日报表(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // 征收机关代码
							tmpdto.setStrecode((String) billmap.get("TreCode")); // 国库主体代码
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // 预算种类
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // 预算级次代码
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // 预算科目
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // 预算科目名称
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // 日累计金额
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // 旬累计金额
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // 月累计金额
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // 季累计金额
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // 年累计金额

							tmpdto.setSrptdate(reportDate); // 报表日期
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // 辖属标志
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_TRIM); // 调整期标志
							tmpdto.setSfinorgcode(finOrgCode);// 财政机关代码
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_TRDRAWBACKBILL);// 调整期退库报表
							tmpdto.setSdividegroup("0");// 分成组标志

							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("保存调整期退库报表时出现数据库异常!", e);
									throw new ITFEBizException(
											"保存调整期退库报表时出现数据库异常!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * 解析调整期调拨收入报表体 msgMap --> TrRemove3128
				 */
				HashMap trremove3128 = (HashMap) msgMap.get("TrRemove3128");
				if (null != trremove3128) {
					List trremovebill3128 = (List) trremove3128
							.get("TrRemoveBill3128");
					if (null == trremovebill3128
							|| trremovebill3128.size() == 0) {
						// 没有调整期调拨收入报表数据
						// <TrRemoveBill3128>
					} else {

						int incomecount = trremovebill3128.size();
						// 存库用LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->TrRemove3128->TrRemoveBill3128
							HashMap billmap = (HashMap) trremovebill3128.get(i);

							// 财政报表-预算收入日报表(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // 征收机关代码
							tmpdto.setStrecode((String) billmap.get("TreCode")); // 国库主体代码
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // 预算种类
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // 预算级次代码
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // 预算科目
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // 预算科目名称
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // 日累计金额
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // 旬累计金额
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // 月累计金额
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // 季累计金额
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // 年累计金额

							tmpdto.setSrptdate(reportDate); // 报表日期
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // 辖属标志
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_TRIM); // 调整期标志
							tmpdto.setSfinorgcode(finOrgCode);// 财政机关代码
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_TRREMOVEBILL);// 调整期调拨收入报表
							tmpdto.setSdividegroup("0");// 分成组标志

							incomelist.add(tmpdto);

							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("保存调整期调拨收入报表时出现数据库异常!", e);
									throw new ITFEBizException(
											"保存调整期调拨收入报表时出现数据库异常!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * 解析调整期共享分成报表体 msgMap --> TrShare3128
				 */
				HashMap trshare3128 = (HashMap) msgMap.get("TrShare3128");
				if (null != trshare3128) {
					List trsharebill3128 = (List) trshare3128
							.get("TrShareBill3128");
					if (null == trsharebill3128 || trsharebill3128.size() == 0) {
						// 没有调整期共享分成报表数据
						// <TrShareBill3128>
					} else {

						int incomecount = trsharebill3128.size();
						// 存库用LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->TrShare3128->TrShareBill3128
							HashMap billmap = (HashMap) trsharebill3128.get(i);

							// 财政报表-预算收入日报表(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // 征收机关代码
							tmpdto.setStrecode((String) billmap.get("TreCode")); // 国库主体代码
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // 预算种类
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // 预算级次代码
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // 预算科目
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // 预算科目名称
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // 日累计金额
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // 旬累计金额
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // 月累计金额
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // 季累计金额
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // 年累计金额

							tmpdto.setSrptdate(reportDate); // 报表日期
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // 辖属标志
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_TRIM); // 调整期标志
							tmpdto.setSfinorgcode(finOrgCode);// 财政机关代码
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_TRSHAREBILL);// 调整期共享分成报表
							tmpdto.setSdividegroup((String) billmap
									.get("DivideGroup"));// 分成组标志

							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("保存调整期共享分成报表时出现数据库异常!", e);
									throw new ITFEBizException(
											"保存调整期共享分成报表时出现数据库异常!", e);
								}
								incomelist.clear();
							}

						}
					}
				}
				/**
				 * 解析库存日报报表体 msgMap --> Stock3128
				 */
				HashMap stockmap = (HashMap) msgMap.get("Stock3128");
				if (null != stockmap) {
					List stockbill3128 = (List) stockmap.get("StockBill3128");
					if (null == stockbill3128 || stockbill3128.size() == 0) {
						// 没有库存日报表数据
						// <StockBill3128>
					} else {
						String sql2 = "delete from TR_STOCKDAYRPT where s_OrgCode = ? and s_RptDate = ? ";
						SQLExecutor sqlExecs = DatabaseFacade.getDb()
								.getSqlExecutorFactory().getSQLExecutor();
						sqlExecs.addParam(finOrgCode);
						sqlExecs.addParam(reportDate);
						sqlExecs.runQueryCloseCon(sql2);
						int stockcount = stockbill3128.size();
						List<IDto> stocklist = new ArrayList<IDto>();
						for (int i = 0; i < stockcount; i++) {
							HashMap billmap = (HashMap) stockbill3128.get(i);
							// 财政报表-库存日报表(3128)
							TrStockdayrptDto tmpdto = new TrStockdayrptDto();
							trecode = (String)billmap.get("TreCode");
							iskucun = true;
							tmpdto.setSorgcode(finOrgCode);// 财政机构代码
							tmpdto.setStrecode((String) billmap.get("TreCode")); // 国库主体代码
							tmpdto.setSrptdate(reportDate); // 报表日期
							tmpdto.setSaccno((String) billmap.get("AcctCode")); // 帐户代码
							tmpdto
									.setSaccname((String) billmap
											.get("AcctName")); // 帐户名称
							tmpdto
									.setSaccdate((String) billmap
											.get("AcctDate")); // 帐户日期
							tmpdto.setNmoneyyesterday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("YesterdayBalance"))); // 上日余额
							tmpdto.setNmoneyin(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TodayReceipt"))); // 本日收入
							tmpdto.setNmoneyout(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TodayPay"))); // 本日支出
							tmpdto.setNmoneytoday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TodayBalance"))); // 本日余额

							stocklist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == stockcount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(stocklist));
								} catch (JAFDatabaseException e) {
									logger.error("保存库存库存日报表时出现数据库异常!", e);
									throw new ITFEBizException(
											"保存库存日报报表时出现数据库异常!", e);
								}
								stocklist.clear();
							}
						}
					}
				}

			}
			/*
			 * 接收/发送日志
			 */
			String recvseqno;// 接收日志流水号
			String sendseqno;// 发送日志流水号
			if(iskucun)
				saveDownloadReportCheck(reportDate,trecode,true);
			if(isshouru)
				saveDownloadReportCheck(reportDate,trecode,false);
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			String path = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_FILE_NAME);
			// 记接收日志
			MsgLogFacade.writeRcvLog(recvseqno, recvseqno, sbookorgcode,
					reportDate, (String) headMap.get("MsgNo"), (String) headMap
							.get("SRC"), path, 0, new BigDecimal(0), null,
							trecode, null, finOrgCode, null,
					(String) headMap.get("MsgID"),
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null, null);

			//用于判断所接财政数据是否已经完整，如果完整则调用WebService通知财政取数    --20131230 by hua
			if("030010011118".equals((String) headMap.get("DES"))) {//目前只支持北京
				//1、首先看是否已经给财政发过通知，如果没有发过则继续
				boolean boo = BusinessFacade.checkAndSaveRecvlog(finOrgCode, reportDate, MsgConstant.MSG_NO_3128, StateConstant.COMMON_NO);
				if(!boo) {
					//2、校验数据完整性并得到msgid的集合
					String[] msgidArray = BusinessFacade.checkIfComplete(finOrgCode, reportDate, MsgConstant.MSG_NO_3128);
					//3、调用通知接口
					if(null != msgidArray && msgidArray.length > 0) {
						FinReportService finReportService = new FinReportService();
						try {
							finReportService.readReportNotice(finOrgCode, reportDate, MsgConstant.MSG_NO_3128,"1",msgidArray);
							BusinessFacade.checkAndSaveRecvlog(finOrgCode, reportDate, MsgConstant.MSG_NO_3128, StateConstant.COMMON_YES);
						} catch (UnsupportedEncodingException e) {
							String error = "向财政发送报文时出现异常！";
							logger.error(error, e);
							throw new ITFEBizException(error, e);
						}
						
					}
				}
			}
			
			eventContext.setStopFurtherProcessing(true);
			return;

		} catch (JAFDatabaseException e1) {
			logger.error("解析保存3128报文出现异常!", e1);
			throw new ITFEBizException("解析保存3128报文出现异常!", e1);

		} catch (SequenceException e) {
			logger.error("解析保存3128报文出现异常!", e);
			throw new ITFEBizException("解析保存3128报文出现异常!", e);
		}
	}

	/**
	 * 根据征收机关代码判断辖属标志是全辖或本级
	 * 
	 * @param String
	 *            staxorgcode 征收机关代码
	 * @return
	 */
	private String getRuleSignByTaxOrgCode(String staxorgcode) {
		if (MsgConstant.MSG_TAXORG_SHARE_CLASS.equals(staxorgcode.trim())
				|| MsgConstant.MSG_TAXORG_NATION_CLASS.equals(staxorgcode
						.trim())
				|| MsgConstant.MSG_TAXORG_PLACE_CLASS
						.equals(staxorgcode.trim())
				|| MsgConstant.MSG_TAXORG_CUSTOM_CLASS.equals(staxorgcode
						.trim())
				|| MsgConstant.MSG_TAXORG_FINANCE_CLASS.equals(staxorgcode
						.trim())
				|| MsgConstant.MSG_TAXORG_OTHER_CLASS
						.equals(staxorgcode.trim())) {
			return MsgConstant.RULE_SIGN_ALL;
		}

		return MsgConstant.RULE_SIGN_SELF;
	}
	private void saveDownloadReportCheck(String date,String trecode,boolean iskucun)
	{
		if(date==null||trecode==null||"".equals(date)||"".equals(trecode))
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(date);
		finddto.setStrecode(trecode);
		try {
			TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
			if(dto==null)
			{
				if(iskucun)
					finddto.setSkucun("1");
				else
					finddto.setSribao("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if(iskucun)
				{
					if("0".equals(dto.getSkucun())||null==dto.getSkucun())
					{
						dto.setSkucun("1");
						DatabaseFacade.getODB().update(dto);
					}
				}
				else
				{
					if("0".equals(dto.getSribao())||null==dto.getSribao())
					{
						dto.setSribao("1");
						DatabaseFacade.getODB().update(dto);
					}
				}
				
			}
		} catch (JAFDatabaseException e) {
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}catch(Exception e)
		{
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}
	}
}

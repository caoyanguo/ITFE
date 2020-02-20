package com.cfcc.itfe.client.dataquery.budgetincomequerysubjectreport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.BudgetSubCodeDialog;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.service.dataquery.finbudgetreccountreport.IPayAndIncomeBillService;
import com.cfcc.itfe.util.ReportExcel;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-09-03 18:59:27 子系统: DataQuery 模块:BudgetIncomeQuerySubjectReport
 *       组件:BudgetIncomeQuerySubjectReport
 */
public class BudgetIncomeQuerySubjectReportBean extends
		AbstractBudgetIncomeQuerySubjectReportBean implements IPageDataProvider {

	private static Log log = LogFactory
			.getLog(BudgetIncomeQuerySubjectReportBean.class);

	/** service */
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	protected IPayAndIncomeBillService payAndIncomeBillService = (IPayAndIncomeBillService) getService(IPayAndIncomeBillService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService) getService(IItfeCacheService.class);

	ITFELoginInfo loginfo;
	// 报表路径
	private String reportPath;
	// 报表list
	private List reportList;
	// 报表map
	private Map<String, String> reportMap;
	// 预算科目代码
	private String sbudgetsubcode;
	// 占比预算科目代码
	private String sbasebudgetsubcode;
	// 占比预算科目名称
	private String sbasebudgetsubcodename;
	// 国库代码列表
	private List<TsTreasuryDto> treList;
	// 国库代码
	private String sleTreCode;
	// 国库名称
	private String sleTrename;
	// 辖属标志
	private String sleOfFlag;
	// 报表类型
	private String sleBillType;
	// 调整期标志
	private String sleTrimFlag;
	// 查询日期
	private String sdate;
	// 预算种类
	private String sleBudKind;
	// 预算级次
	private String sleBudLevel;
	// 科目类型
	private String sleSubjectType;
	// 金额单位
	private String sleMoneyUnit;
	// 金额单位名称
	private String moneyUnitName;
	// 报表辖属标志
	private String sleOfFlagName = null;
	// 报表预算级次标志
	private String sbudgetlevelname = null;
	// 报表类型标志
	private String slebilltypename = null;
	// 报表预算种类标志
	private String sbudgettypename = null;

	private Boolean flag = true;
	// 查询用list
	private List list = null;
	// 报表标题
	private String reporttitle = null;
	private String filenamecmt = null;
	// 调拨标志
	private String smoveflag = null;
	// 调拨标志名称
	private String smoveflagname = null;
	// 收入参数DTO
	TrIncomedayrptDto incomedto = new TrIncomedayrptDto();
	// 支出参数DTO
	TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();
	// 旬报类型
	private String sleTenrptType;
	// 季报类型
	private String sleQuarterrptType;
	// 半年报类型
	private String sleHalfyearrptType;
	// 旬报类型名称
	private String sleTenrptTypename;
	// 季报类型名称
	private String sleQuarterrptTypename;
	// 半年报类型名称
	private String sleHalfyearrptTypename;
	// 报表标题
	String rptTitle = "";
	String datename = "";
	// 报表类型（支出还是收入）
	private String reporttype;
	// 报表类型列表
	private List<Mapper> reporttypelist;
	// 报表类型(旬报，季报，半年报)
	private String rptType;
	// 报表类型类型(旬报，季报，半年报)
	private List<Mapper> rptTypeList;

	public BudgetIncomeQuerySubjectReportBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		reportPath = "/com/cfcc/itfe/client/ireport/BudgetIncomeQuerySubjectReport.jasper";
		reportList = new ArrayList();
		reportMap = new HashMap<String, String>();
		rptType = null;
		rptTypeList = new ArrayList<Mapper>();
		init();

	}

	// 页面查询条件初始化
	private void init() {
		// 查询国库代码用dto
		TsTreasuryDto tredto = new TsTreasuryDto();
		// 中心机构代码
		String centerorg = null;

		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
			// 中心机构，取得所有国库列表
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// 非中心机构，取得登录机构对应国库
			else {
				tredto = new TsTreasuryDto();
				tredto.setSorgcode(loginfo.getSorgcode());
				treList = commonDataAccessService.findRsByDto(tredto);
			}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		// 初始化国库代码默认值
		if (treList.size() > 0) {
			sleTreCode = treList.get(0).getStrecode();
		}

		// 初始化国库名称
		for (TsTreasuryDto tmp : treList) {
			if (tmp.getStrecode().equals(sleTreCode)) {
				sleTrename = tmp.getStrename();
			}
		}

		// 报表类型
		sleBillType = StateConstant.REPORT_DAY;
		// 报表类型名称
		slebilltypename = "日报";
		// 调整期标志
		sleTrimFlag = StateConstant.TRIMSIGN_FLAG_NORMAL;
		// 金额单位
		sleMoneyUnit = StateConstant.MONEY_UNIT_3;
		// 金额单位名称
		moneyUnitName = "单位 ：亿元";
		// 辖属标志
		sleOfFlag = MsgConstant.RULE_SIGN_ALL;
		// 辖属标志名称
		sleOfFlagName = "全辖";
		// 预算种类
		sleBudKind = MsgConstant.BDG_KIND_IN;
		// 预算种类名称
		sbudgettypename = "预算内";
		// 预算级次
		sleBudLevel = MsgConstant.BUDGET_LEVEL_CENTER;
		// 预算级次名称
		sbudgetlevelname = "中央级";
		// 查询日期
		sdate = TimeFacade.getCurrentStringTime();
		// 报表类型下拉框赋值
		reporttypelist = new ArrayList<Mapper>();
		Mapper mapper1 = new Mapper();
		reporttype = "1";
		mapper1.setDisplayValue("收入报表");
		mapper1.setUnderlyValue("1");
		reporttypelist.add(mapper1);
		Mapper mapper2 = new Mapper();
		mapper2.setDisplayValue("支出报表");
		mapper2.setUnderlyValue("2");
		reporttypelist.add(mapper2);

	}

	/**
	 * Direction: 查询 ename: query 引用方法: viewers: 预算收入分科目统计分析查询结果显示界面 messages:
	 */
	public String query(Object o) {

		/**
		 * 查询条件校验
		 */
		if (!querycheck()) {
			return null;
		}

		// 编制时间
		String date = TimeFacade.getCurrentStringTime();

		/**
		 * 取得报表数据
		 */
		if (reporttype.equals("1")) {
			// 占比预算科目代码
			incomedto.setSbudgetsubcode(sbasebudgetsubcode);
			// 国库主体代码
			incomedto.setStrecode(sleTreCode);
			// 预算种类
			incomedto.setSbudgettype(sleBudKind);
			// 预算级次代码
			incomedto.setSbudgetlevelcode(sleBudLevel);
			// 辖属标志
			incomedto.setSbelongflag(sleOfFlag);
			// 报表类型
			incomedto.setSbillkind(sleBillType);
			// 调整期标志
			incomedto.setStrimflag(sleTrimFlag);
			// 日期
			incomedto.setSrptdate(sdate);

			// 报表标题
			reporttitle = sleTrename + sbudgetlevelname + "预算收入分科目统计分析"
					+ slebilltypename + "表";
		} else if (reporttype.equals("2")) {
			// 占比预算科目代码
			payoutdto.setSbudgetsubcode(sbasebudgetsubcode);
			// 国库主体代码
			payoutdto.setStrecode(sleTreCode);
			// 预算种类
			payoutdto.setSbudgettype(sleBudKind);
			// 预算级次代码
			payoutdto.setSbudgetlevelcode(sleBudLevel);
			// // 辖属标志
			// payoutdto.setSbelongflag(sleOfFlag);
			// // 报表类型
			// payoutdto.setSbillkind(sleBillType);
			// // 调整期标志
			// payoutdto.setStrimflag(sleTrimFlag);
			// 日期
			payoutdto.setSrptdate(sdate);

			// 报表标题
			reporttitle = sleTrename + sbudgetlevelname + "预算支出分科目统计分析"
					+ slebilltypename + "表";
		}

		/**
		 * 具体报表信息展示
		 */
		// 日报表查询
		if (StateConstant.REPORT_DAY.equals(sleBillType)) {
			datename = "【" + sdate.substring(0, 4) + "年"
					+ sdate.substring(4, 6) + "月" + sdate.substring(6, 8)
					+ "日<日报>】";
		}
		// 旬报表查询
		else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
			reporttitle += "(" + sleTenrptTypename + ")";
			datename = "【" + sdate.substring(0, 4) + "年"
					+ sdate.substring(4, 6) + "月" + "<" + sleTenrptTypename
					+ "报>】";
		}
		// 月报表查询
		else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
			datename = "【" + sdate.substring(0, 4) + "年"
					+ sdate.substring(4, 6) + "月" + "<月报>】";
		}
		// 季报表查询
		else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
			reporttitle += "(" + sleQuarterrptTypename + ")";
			datename = "【" + sdate.substring(0, 4) + "年" + "<"
					+ sleQuarterrptTypename + "报>】";
		}
		// 半年报表查询
		else if (StateConstant.REPORT_HALFYEAR.equals(sleBillType)) {
			reporttitle += "(" + sleHalfyearrptTypename + ")";
			datename = "【" + sdate.substring(0, 4) + "年" + "<"
					+ sleHalfyearrptTypename + "报>】";
		}
		// 年报表查询
		else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
			datename = "【" + sdate.substring(0, 4) + "年" + "<年报>】";
		}

		try {
			if (reporttype.equals("1")) {
				rptTitle = sleTrename + "(" + sleOfFlagName + ")"
						+ "预算收入分科目统计分析" + slebilltypename + "表" + datename;

				// 编制年月日
				date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
						+ date.substring(6, 8) + "日";

				reportList = budgetIncomeQueryList(incomedto);
			} else if (reporttype.equals("2")) {
				rptTitle = sleTrename + "(" + sleOfFlagName + ")"
						+ "预算支出分科目统计分析" + slebilltypename + "表" + datename;
				// 编制年月日
				date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
						+ date.substring(6, 8) + "日";

				reportList = budgetPayoutQueryList(payoutdto);
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}

		if (null == reportList || reportList.size() == 0) {
			MessageDialog.openMessageDialog(null, "查询无数据!");
			return "";
		}
		// 报表标题
		reportMap.put("reportTitle", rptTitle.toString());
		// 基准科目代码
		reportMap.put("sbasebudgetsubcode", sbasebudgetsubcodename.toString());
		// 预算级次
		reportMap.put("sleBudLevel", sbudgetlevelname.toString());
		// 金额单位
		reportMap.put("sleMoneyUnit", moneyUnitName.toString());
		// 编制单位
		reportMap.put("makeUnit", sleTrename.toString());
		// 编制人
		reportMap.put("makeUser", loginfo.getSuserName().toString());
		// 编制时间
		reportMap.put("makeDate", date.toString());

		return super.query(o);
	}

	/**
	 * 财政预算收入查询 return list
	 * 
	 * @throws ITFEBizException
	 */
	public List budgetIncomeQueryList(TrIncomedayrptDto incomedto)
			throws ITFEBizException {

		try {
			// 判断报表类型
			if (null != rptType && rptType.contains("x")) {
				sleTenrptType = rptType;
			} else if (null != rptType && rptType.contains("j")) {
				sleQuarterrptType = rptType;
			} else if (null != rptType && rptType.contains("n")) {
				sleHalfyearrptType = rptType;
			}
			// sbudgetsubcode--预算科目代码(多个以逗号分割) sleBillType--报表种类
			// sleTenrptType--日报表类型sleQuarterrptType--季报表类型sleHalfyearrptType--半年报类型
			// sleSubjectType--科目类型 smoveflag--调拨标志 sleMoneyUnit--金额单位
			list = payAndIncomeBillService.makeIncomeReportBySubject(incomedto,
					sbudgetsubcode, sleBillType, sleTenrptType,
					sleQuarterrptType, sleHalfyearrptType, sleSubjectType,
					smoveflag, sleMoneyUnit);
		} catch (ITFEBizException e) {
			log.error(e);
			throw e;
		}
		return list;
	}

	/**
	 * 财政预算支出查询 return list
	 * 
	 * @throws ITFEBizException
	 */
	public List budgetPayoutQueryList(TrTaxorgPayoutReportDto payoutdto)
			throws ITFEBizException {

		try {
			// 判断报表类型
			if (null != rptType && rptType.contains("x")) {
				sleTenrptType = rptType;
			} else if (null != rptType && rptType.contains("j")) {
				sleQuarterrptType = rptType;
			} else if (null != rptType && rptType.contains("n")) {
				sleHalfyearrptType = rptType;
			}
			// sbudgetsubcode--预算科目代码(多个以逗号分割) sleBillType--报表种类
			// sleTenrptType--日报表类型sleQuarterrptType--季报表类型sleHalfyearrptType--半年报类型
			// sleSubjectType--科目类型 smoveflag--调拨标志 sleMoneyUnit--金额单位
			list = payAndIncomeBillService.makePayoutReportBySubject(payoutdto,
					sbudgetsubcode, sleBillType, sleTenrptType,
					sleQuarterrptType, sleHalfyearrptType, sleSubjectType,
					smoveflag, sleMoneyUnit);
		} catch (ITFEBizException e) {
			log.error(e);
			throw e;
		}
		return list;
	}

	/**
	 * 查询条件校验
	 */
	public boolean querycheck() {

		if (null == sbudgetsubcode || "".equals(sbudgetsubcode)) {
			MessageDialog.openMessageDialog(null, "请选取预算科目代码");
			return false;
		} else if (null == sbasebudgetsubcode || "".equals(sbasebudgetsubcode)) {
			MessageDialog.openMessageDialog(null, "请输入占比预算科目代码");
			return false;
		} else if (null == sleTreCode || "".equals(sleTreCode)) {
			MessageDialog.openMessageDialog(null, "请选择国库代码");
			return false;
		} else if (null == sdate || "".equals(sdate)) {
			MessageDialog.openMessageDialog(null, "请输入查询日期");
			return false;
		} else if (null == sleBillType || "".equals(sleBillType)) {
			MessageDialog.openMessageDialog(null, "请选择报表类型");
			return false;
		} else if (null == sleOfFlag || "".equals(sleOfFlag)) {
			MessageDialog.openMessageDialog(null, "请选择辖属标志");
			return false;
		} else if (null == sleBudKind || "".equals(sleBudKind)) {
			MessageDialog.openMessageDialog(null, "请选择预算种类");
			return false;
		} else if (null == sleBudLevel || "".equals(sleBudLevel)) {
			MessageDialog.openMessageDialog(null, "请选择预算级次");
			return false;
		} else if (null == sleTrimFlag || "".equals(sleTrimFlag)) {
			MessageDialog.openMessageDialog(null, "请选择调整期标志");
			return false;
		}
		return true;
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 预算收入分科目统计分析查询结果显示界面 messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}

	/**
	 * Direction: 导出标准Excel ename: exportExcelFile 引用方法: viewers: * messages:
	 */
	public String exportExcelFile(Object o) {

		// 编制时间
		String date = TimeFacade.getCurrentStringTime();
		// 编制年月日
		date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
				+ date.substring(6, 8) + "日";

		// 辖属标志 sleOfFlagName 预算种类 sbudgettypename
		filenamecmt = "_" + sleOfFlagName + "_" + sbudgettypename;

		String dirsep = File.separator; // 取得系统分割符
		// 报表模块文件路径
		String filerootpath = "C:\\" + "Report" + dirsep
				+ TimeFacade.getCurrentStringTime() + dirsep;
		String copyFilename = this.sleTreCode
				+ "_"
				+ sdate
				+ "_"
				+ reporttitle
				+ filenamecmt
				+ "_"
				+ (new SimpleDateFormat("ddHHmmsss").format(System
						.currentTimeMillis()) + "R.xls");

		ReportExcel.init();

		ReportExcel.setFilepath(filerootpath);

		ReportExcel.setNewfilepath(filerootpath);
		// 新建报表名称
		ReportExcel.setCopyFilename(copyFilename);

		ReportExcel.setReporttitle(rptTitle);
		// 编制年月日
		ReportExcel.setDate(date);

		// 报表模板名称
		ReportExcel.setFilename("Income2.xls");
		ReportExcel.setInputstream(BudgetIncomeQuerySubjectReportBean.class
				.getResourceAsStream("Income2.xls"));
		// 报表sheet名称
		ReportExcel.setSheetname(datename);

		try {
			ReportExcel.getIncomeReportBySubject(list, sleTrename,
					sbasebudgetsubcodename + "(" + sbasebudgetsubcode + ")",
					sbudgetlevelname, moneyUnitName, loginfo.getSuserName());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}

		MessageDialog.openMessageDialog(null, "导出文件成功！已保存到：" + filerootpath
				+ copyFilename);
		return super.exportExcelFile(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
	
	public List<TsBudgetsubjectDto> openDialog(String editorid){
		List<TsBudgetsubjectDto> subcodelist=new ArrayList<TsBudgetsubjectDto>();
		final BudgetSubCodeDialog dialog = new BudgetSubCodeDialog(null, commonDataAccessService, editorid);
		int dialogflag = dialog.open();
		if (dialogflag == 0) {
			subcodelist= dialog.getCheckSubList();
		}
		return subcodelist;
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		BudgetIncomeQuerySubjectReportBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public ICommonDataAccessService getCommonDataAccessService() {
		return commonDataAccessService;
	}

	public void setCommonDataAccessService(
			ICommonDataAccessService commonDataAccessService) {
		this.commonDataAccessService = commonDataAccessService;
	}

	public IPayAndIncomeBillService getPayAndIncomeBillService() {
		return payAndIncomeBillService;
	}

	public void setPayAndIncomeBillService(
			IPayAndIncomeBillService payAndIncomeBillService) {
		this.payAndIncomeBillService = payAndIncomeBillService;
	}

	public IItfeCacheService getItfeCacheService() {
		return itfeCacheService;
	}

	public void setItfeCacheService(IItfeCacheService itfeCacheService) {
		this.itfeCacheService = itfeCacheService;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public List getReportList() {
		return reportList;
	}

	public void setReportList(List reportList) {
		this.reportList = reportList;
	}

	public Map<String, String> getReportMap() {
		return reportMap;
	}

	public void setReportMap(Map<String, String> reportMap) {
		this.reportMap = reportMap;
	}

	public String getSbudgetsubcode() {
		return sbudgetsubcode;
	}

	public void setSbudgetsubcode(String sbudgetsubcode) {
		this.sbudgetsubcode = sbudgetsubcode;
	}

	public String getSbasebudgetsubcode() {
		return sbasebudgetsubcode;
	}

	public void setSbasebudgetsubcode(String sbasebudgetsubcode) {
		this.sbasebudgetsubcode = sbasebudgetsubcode;
		TsBudgetsubjectDto subjectdto = new TsBudgetsubjectDto();
		subjectdto.setSorgcode(loginfo.getSorgcode());
		subjectdto.setSsubjectcode(sbasebudgetsubcode);
		try {
			List<TsBudgetsubjectDto> list = commonDataAccessService
					.findRsByDto(subjectdto);
			if (null != list && list.size() > 0) {
				sbasebudgetsubcodename = list.get(0).getSsubjectname();
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
		}
		this.editor.fireModelChanged();
	}

	public TrIncomedayrptDto getIncomedto() {
		return incomedto;
	}

	public void setIncomedto(TrIncomedayrptDto incomedto) {
		this.incomedto = incomedto;
	}

	public List<TsTreasuryDto> getTreList() {
		return treList;
	}

	public void setTreList(List<TsTreasuryDto> treList) {
		this.treList = treList;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public String getSleTreCode() {
		return sleTreCode;
	}

	public void setSleTreCode(String sleTreCode) {
		for (TsTreasuryDto tmp : treList) {
			if (tmp.getStrecode().equals(sleTreCode)) {
				sleTrename = tmp.getStrename();
			}
		}
		this.sleTreCode = sleTreCode;
		if (flag)
			editor.fireModelChanged();
	}

	public String getSleTrename() {
		return sleTrename;
	}

	public void setSleTrename(String sleTrename) {
		this.sleTrename = sleTrename;
	}

	public String getSleOfFlag() {
		return sleOfFlag;
	}

	public void setSleOfFlag(String sleOfFlag) {
		if (sleOfFlag.equals(MsgConstant.RULE_SIGN_ALL)) {
			sleOfFlagName = "全辖";
		} else if (sleOfFlag.equals(MsgConstant.RULE_SIGN_SELF)) {
			sleOfFlagName = "本级";
		}
		this.sleOfFlag = sleOfFlag;
		this.editor.fireModelChanged();
	}

	public String getSleBillType() {
		return sleBillType;
	}

	public void setSleBillType(String sleBillType) {
		rptTypeList.clear();
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {
			slebilltypename = "日报";
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {
			Mapper mapper1 = new Mapper();
			setRptType(StateConstant.TENRPT_TOP);
			mapper1.setDisplayValue("上旬");
			mapper1.setUnderlyValue(StateConstant.TENRPT_TOP);
			rptTypeList.add(mapper1);
			Mapper mapper2 = new Mapper();
			mapper2.setDisplayValue("中旬");
			mapper2.setUnderlyValue(StateConstant.TENRPT_MIDDLE);
			rptTypeList.add(mapper2);
			Mapper mapper3 = new Mapper();
			mapper3.setDisplayValue("下旬");
			mapper3.setUnderlyValue(StateConstant.TENRPT_BOTTOM);
			rptTypeList.add(mapper3);
			slebilltypename = "旬报";
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {
			slebilltypename = "月报";
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {
			Mapper mapper1 = new Mapper();
			setRptType(StateConstant.QUARTERRPT_FIRST);
			mapper1.setDisplayValue("第一季度");
			mapper1.setUnderlyValue(StateConstant.QUARTERRPT_FIRST);
			rptTypeList.add(mapper1);
			Mapper mapper2 = new Mapper();
			mapper2.setDisplayValue("第二季度");
			mapper2.setUnderlyValue(StateConstant.QUARTERRPT_SECOND);
			rptTypeList.add(mapper2);
			Mapper mapper3 = new Mapper();
			mapper3.setDisplayValue("第三季度");
			mapper3.setUnderlyValue(StateConstant.QUARTERRPT_THREE);
			rptTypeList.add(mapper3);
			Mapper mapper4 = new Mapper();
			mapper4.setDisplayValue("第四季度");
			mapper4.setUnderlyValue(StateConstant.QUARTERRPT_FOUR);
			rptTypeList.add(mapper4);
			slebilltypename = "季报";
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {
			Mapper mapper1 = new Mapper();
			setRptType(StateConstant.HALFYEARRPT_TOP);
			mapper1.setDisplayValue("上半年");
			mapper1.setUnderlyValue(StateConstant.HALFYEARRPT_TOP);
			rptTypeList.add(mapper1);
			Mapper mapper2 = new Mapper();
			mapper2.setDisplayValue("下半年");
			mapper2.setUnderlyValue(StateConstant.HALFYEARRPT_BOTTOM);
			rptTypeList.add(mapper2);
			slebilltypename = "半年报";
		} else if (sleBillType.equals(StateConstant.REPORT_YEAR)) {
			slebilltypename = "年报";
		}
		this.sleBillType = sleBillType;
		this.editor.fireModelChanged();
	}

	public String getSleMoneyUnit() {
		return sleMoneyUnit;
	}

	public void setSleMoneyUnit(String sleMoneyUnit) {
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			moneyUnitName = "单位： 元";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			moneyUnitName = "单位： 万元";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			moneyUnitName = "单位：亿元 ";
		}
		this.sleMoneyUnit = sleMoneyUnit;
		this.editor.fireModelChanged();
	}

	public String getSleTrimFlag() {
		return sleTrimFlag;
	}

	public void setSleTrimFlag(String sleTrimFlag) {
		this.sleTrimFlag = sleTrimFlag;
	}

	public String getSdate() {
		return sdate;
	}

	public void setSdate(String sdate) {
		this.sdate = sdate;
	}

	public String getSleBudLevel() {
		return sleBudLevel;
	}

	public void setSleBudLevel(String sleBudLevel) {
		if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_CENTER)) {
			sbudgetlevelname = "中央级";
		} else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_CITY)) {
			sbudgetlevelname = "市级";
		} else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_AREA)) {
			sbudgetlevelname = "区级";
		} else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_LOCAL)) {
			sbudgetlevelname = "地方级";
		} else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_ANY)) {
			sbudgetlevelname = "不分级次";
		}
		this.sleBudLevel = sleBudLevel;
		this.editor.fireModelChanged();
	}

	public String getMoneyUnitName() {
		return moneyUnitName;
	}

	public void setMoneyUnitName(String moneyUnitName) {
		this.moneyUnitName = moneyUnitName;
	}

	public String getSleBudKind() {
		return sleBudKind;
	}

	public void setSleBudKind(String sleBudKind) {
		if (sleBudKind.equals(MsgConstant.BUDGET_TYPE_IN_BDG)) {
			sbudgettypename = "预算内";
		} else if (sleBudKind.equals(MsgConstant.BUDGET_TYPE_OUT_BDG)) {
			sbudgettypename = "预算外";
		}
		this.sleBudKind = sleBudKind;
		this.editor.fireModelChanged();
	}

	public String getSleOfFlagName() {
		return sleOfFlagName;
	}

	public void setSleOfFlagName(String sleOfFlagName) {
		this.sleOfFlagName = sleOfFlagName;
	}

	public String getSbudgetlevelname() {
		return sbudgetlevelname;
	}

	public void setSbudgetlevelname(String sbudgetlevelname) {
		this.sbudgetlevelname = sbudgetlevelname;
	}

	public String getSlebilltypename() {
		return slebilltypename;
	}

	public void setSlebilltypename(String slebilltypename) {
		this.slebilltypename = slebilltypename;
	}

	public String getSbudgettypename() {
		return sbudgettypename;
	}

	public void setSbudgettypename(String sbudgettypename) {
		this.sbudgettypename = sbudgettypename;
	}

	public String getSleSubjectType() {
		return sleSubjectType;
	}

	public void setSleSubjectType(String sleSubjectType) {
		this.sleSubjectType = sleSubjectType;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public String getReporttitle() {
		return reporttitle;
	}

	public void setReporttitle(String reporttitle) {
		this.reporttitle = reporttitle;
	}

	public String getFilenamecmt() {
		return filenamecmt;
	}

	public void setFilenamecmt(String filenamecmt) {
		this.filenamecmt = filenamecmt;
	}

	public String getSmoveflag() {
		return smoveflag;
	}

	public void setSmoveflag(String smoveflag) {
		if (smoveflag.equals(StateConstant.MOVEFLAG_NOMOVE)) {
			smoveflagname = "非调拨";
		} else if (smoveflag.equals(StateConstant.MOVEFLAG_MOVE)) {
			smoveflagname = "调拨";
		} else if (smoveflag.equals(StateConstant.MOVEFLAG_NORMAL)) {
			smoveflagname = "不区分";
		}
		this.smoveflag = smoveflag;
		this.editor.fireModelChanged();
	}

	public String getSleTenrptTypename() {
		return sleTenrptTypename;
	}

	public void setSleTenrptTypename(String sleTenrptTypename) {
		this.sleTenrptTypename = sleTenrptTypename;
	}

	public String getSleQuarterrptTypename() {
		return sleQuarterrptTypename;
	}

	public void setSleQuarterrptTypename(String sleQuarterrptTypename) {
		this.sleQuarterrptTypename = sleQuarterrptTypename;
	}

	public String getSleHalfyearrptTypename() {
		return sleHalfyearrptTypename;
	}

	public void setSleHalfyearrptTypename(String sleHalfyearrptTypename) {
		this.sleHalfyearrptTypename = sleHalfyearrptTypename;
	}

	public String getSmoveflagname() {
		return smoveflagname;
	}

	public void setSmoveflagname(String smoveflagname) {
		this.smoveflagname = smoveflagname;
	}

	public String getSbasebudgetsubcodename() {
		return sbasebudgetsubcodename;
	}

	public void setSbasebudgetsubcodename(String sbasebudgetsubcodename) {
		this.sbasebudgetsubcodename = sbasebudgetsubcodename;
	}

	public String getReporttype() {
		return reporttype;
	}

	public void setReporttype(String reporttype) {
		this.reporttype = reporttype;
	}

	public List getReporttypelist() {
		return reporttypelist;
	}

	public void setReporttypelist(List reporttypelist) {
		this.reporttypelist = reporttypelist;
	}

	public String getRptType() {
		return rptType;
	}

	public void setRptType(String rptType) {
		if (rptType.equals(StateConstant.TENRPT_TOP)) {
			sleTenrptTypename = "上旬";
		} else if (rptType.equals(StateConstant.TENRPT_MIDDLE)) {
			sleTenrptTypename = "中旬";
		} else if (rptType.equals(StateConstant.TENRPT_BOTTOM)) {
			sleTenrptTypename = "下旬";
		} else if (rptType.equals(StateConstant.QUARTERRPT_FIRST)) {
			sleQuarterrptTypename = "第一季度";
		} else if (rptType.equals(StateConstant.QUARTERRPT_SECOND)) {
			sleQuarterrptTypename = "第二季度";
		} else if (rptType.equals(StateConstant.QUARTERRPT_THREE)) {
			sleQuarterrptTypename = "第三季度";
		} else if (rptType.equals(StateConstant.QUARTERRPT_FOUR)) {
			sleQuarterrptTypename = "第四季度";
		} else if (rptType.equals(StateConstant.HALFYEARRPT_TOP)) {
			sleHalfyearrptTypename = "上半年";
		} else if (rptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {
			sleHalfyearrptTypename = "下半年";
		}
		this.rptType = rptType;
		this.editor.fireModelChanged();
	}

	public List<Mapper> getRptTypeList() {
		return rptTypeList;
	}

	public void setRptTypeList(List<Mapper> rptTypeList) {
		this.rptTypeList = rptTypeList;
	}

}
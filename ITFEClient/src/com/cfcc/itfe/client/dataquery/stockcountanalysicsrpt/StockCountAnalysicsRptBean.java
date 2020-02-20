package com.cfcc.itfe.client.dataquery.stockcountanalysicsrpt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dataquery.budgetincomequerysubjectreport.BudgetIncomeQuerySubjectReportBean;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
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

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-09-16 16:20:21 子系统: DataQuery 模块:StockCountAnalysicsRpt
 *       组件:StockCountAnalysicsRpt
 */
public class StockCountAnalysicsRptBean extends
		AbstractStockCountAnalysicsRptBean implements IPageDataProvider {

	private static Log log = LogFactory
			.getLog(StockCountAnalysicsRptBean.class);

	/** service */
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	protected IPayAndIncomeBillService payAndIncomeBillService = (IPayAndIncomeBillService) getService(IPayAndIncomeBillService.class);
	protected IItfeCacheService itfeCacheService = (IItfeCacheService) getService(IItfeCacheService.class);

	ITFELoginInfo loginfo;
	// 国库名称
	private String sleTrename;
	// 辖属标志
	private String sleOfFlag;
	// 调整期标志
	private String sleTrimFlag;
	// 预算种类
	private String sleBudKind;
	// 金额单位
	private String sleMoneyUnit;
	// 金额单位名称
	private String moneyUnitName;
	// 报表辖属标志
	private String sleOfFlagName = null;
	// 报表预算种类标志
	private String sbudgettypename = null;

	// 报表标题
	private String reporttitle = null;
	private String filenamecmt = null;

	// 报表路径
	private String reportPath;
	// 报表list
	private List reportList;
	// 报表map
	private Map<String, String> reportMap;

	// 报表标题
	String rptTitle = "";
	String datename = "";
	// 起止日期
	String startenddate = "";

	public StockCountAnalysicsRptBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		String date = TimeFacade.getCurrentStringTime();
		String year = date.substring(0, 4);
		startqueryyear = year;
		endqueryyear = year;
		startdate = date.substring(4, 8);
		enddate = date.substring(4, 8);
		treList = new ArrayList<TsTreasuryDto>();
		reportPath = "/com/cfcc/itfe/client/ireport/StockCountAnalysicsRpt.jasper";
		reportList = new ArrayList();
		reportMap = new HashMap<String, String>();
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
			sleTreCode = ((TsTreasuryDto) treList.get(0)).getStrecode();
		}

		// 初始化国库名称
		for (int i = 0; i < treList.size(); i++) {
			TsTreasuryDto tmp = (TsTreasuryDto) treList.get(i);
			if (tmp.getStrecode().equals(sleTreCode)) {
				sleTrename = tmp.getStrename();
			}
		}

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

	}

	/**
	 * Direction: 查询 ename: queryReport 引用方法: viewers: 库存统计分析查询结果界面 messages:
	 */
	public String queryReport(Object o) {

		/**
		 * 查询条件校验
		 */
		if (!querycheck()) {
			return null;
		}

		// 报表标题
		reporttitle = sleTrename + "(" + sleOfFlagName + ")" + startqueryyear
				+ "-" + endqueryyear + sbudgettypename + "库存余额统计分析表";

		// 编制时间
		String date = TimeFacade.getCurrentStringTime();

		/**
		 * 具体报表信息展示
		 */
		try {
			rptTitle = reporttitle;

			// 编制年月日
			date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
					+ date.substring(6, 8) + "日";

			// 开始年度-startqueryyear 结束年度-endqueryyear 开始日期-startdate 结束日期-enddate
			// 国库代码-sleTreCode 辖属标志-sleOfFlag 金额单位-sleMoneyUnit
			reportList = payAndIncomeBillService.stockCountQueryList(loginfo
					.getSorgcode(), startqueryyear, endqueryyear, startdate,
					enddate, sleTreCode, sleOfFlag, sleMoneyUnit);
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
		// 起止日期
		startenddate = startdate.substring(0, 2) + " 月 "
				+ startdate.substring(2, 4) + " 日 " + "-"
				+ enddate.substring(0, 2) + " 月 " + enddate.substring(2, 4)
				+ " 日 ";
		reportMap.put("startenddate", startenddate.toString());
		// 金额单位
		reportMap.put("sleMoneyUnit", moneyUnitName.toString());
		// 编制单位
		reportMap.put("makeUnit", sleTrename.toString());
		// 编制人
		reportMap.put("makeUser", loginfo.getSuserName().toString());
		// 编制时间
		reportMap.put("makeDate", date.toString());

		return super.queryReport(o);
	}

	/**
	 * 查询条件校验
	 */
	public boolean querycheck() {

		if (null == startqueryyear || "".equals(startqueryyear)) {
			MessageDialog.openMessageDialog(null, "请选择开始年度");
			return false;
		} else if (null == endqueryyear || "".equals(endqueryyear)) {
			MessageDialog.openMessageDialog(null, "请选择结束年度");
			return false;
		} else if (Integer.parseInt(startqueryyear) > Integer
				.parseInt(endqueryyear)) {
			MessageDialog.openMessageDialog(null, "开始年度应小于结束年度");
			return false;
		} else if (null == startdate || "".equals(startdate)) {
			MessageDialog.openMessageDialog(null, "请输入开始日期");
			return false;
		} else if (Integer.parseInt(startdate) < 101
				|| Integer.parseInt(startdate) > 1231
				|| startdate.substring(0, 2).equals("00")
				|| startdate.substring(2, 4).equals("00")) {
			MessageDialog.openMessageDialog(null, "输入的开始日期不正确");
			return false;
		} else if (null == enddate || "".equals(enddate)) {
			MessageDialog.openMessageDialog(null, "请选择结束日期");
			return false;
		} else if (Integer.parseInt(enddate) < 101
				|| Integer.parseInt(enddate) > 1231
				|| enddate.substring(0, 2).equals("00")
				|| enddate.substring(2, 4).equals("00")) {
			MessageDialog.openMessageDialog(null, "输入的结束日期不正确");
			return false;
		} else if (Integer.parseInt(startdate) > Integer.parseInt(enddate)) {
			MessageDialog.openMessageDialog(null, "开始日期应小于结束日期");
			return false;
		} else if (null == sleTreCode || "".equals(sleTreCode)) {
			MessageDialog.openMessageDialog(null, "请选择国库代码");
			return false;
		} else if (null == sleOfFlag || "".equals(sleOfFlag)) {
			MessageDialog.openMessageDialog(null, "请选择辖属标志");
			return false;
		} else if (null == sleBudKind || "".equals(sleBudKind)) {
			MessageDialog.openMessageDialog(null, "请选择预算种类");
			return false;
		} else if (null == sleTrimFlag || "".equals(sleTrimFlag)) {
			MessageDialog.openMessageDialog(null, "请选择调整期标志");
			return false;
		}
		return true;
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 库存统计分析查询结果界面 messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}

	/**
	 * Direction: 导出Excel文件 ename: exportExcelFile 引用方法: viewers: * messages:
	 */
	public String exportExcelFile(Object o) {

		// 编制时间
		String date = TimeFacade.getCurrentStringTime();
		// 编制年月日
		date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
				+ date.substring(6, 8) + "日";

		String dirsep = File.separator; // 取得系统分割符
		// 报表模块文件路径
		String filerootpath = "C:\\" + "Report" + dirsep
				+ TimeFacade.getCurrentStringTime() + dirsep;
		String copyFilename = this.sleTreCode
				+ "_"
				+ TimeFacade.getCurrentStringTime()
				+ "_"
				+ reporttitle
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
		ReportExcel.setFilename("Stock.xls");
		ReportExcel.setInputstream(StockCountAnalysicsRptBean.class
				.getResourceAsStream("Stock.xls"));
		// 报表sheet名称
		ReportExcel.setSheetname(datename);

		try {
			ReportExcel.getStockCountAnalysisReport(reportList, startenddate,
					moneyUnitName, sleTrename, loginfo.getSuserName());
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

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		StockCountAnalysicsRptBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public String getSleTreCode() {
		return sleTreCode;
	}

	public void setSleTreCode(String sleTreCode) {
		for (int i = 0; i < treList.size(); i++) {
			TsTreasuryDto tmp = (TsTreasuryDto) treList.get(i);
			if (tmp.getStrecode().equals(sleTreCode)) {
				sleTrename = tmp.getStrename();
			}
		}
		this.sleTreCode = sleTreCode;
		this.editor.fireModelChanged();
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

	public String getSleTrimFlag() {
		return sleTrimFlag;
	}

	public void setSleTrimFlag(String sleTrimFlag) {
		this.sleTrimFlag = sleTrimFlag;
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

	public String getMoneyUnitName() {
		return moneyUnitName;
	}

	public void setMoneyUnitName(String moneyUnitName) {
		this.moneyUnitName = moneyUnitName;
	}

	public String getSleOfFlagName() {
		return sleOfFlagName;
	}

	public void setSleOfFlagName(String sleOfFlagName) {
		this.sleOfFlagName = sleOfFlagName;
	}

	public String getSbudgettypename() {
		return sbudgettypename;
	}

	public void setSbudgettypename(String sbudgettypename) {
		this.sbudgettypename = sbudgettypename;
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

}
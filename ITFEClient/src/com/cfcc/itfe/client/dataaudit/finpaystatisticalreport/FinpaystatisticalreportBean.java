package com.cfcc.itfe.client.dataaudit.finpaystatisticalreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.report.ReportGen;
import com.cfcc.itfe.client.dataquery.finbudgetreccountreport.FinBudgetRecCountReportBean;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsBatchcrtreportmsgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ReportExcel;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 15-05-18 15:46:17 子系统: DataAudit 模块:finpaystatisticalreport
 *       组件:Finpaystatisticalreport
 */
public class FinpaystatisticalreportBean extends
		AbstractFinpaystatisticalreportBean implements IPageDataProvider {

	private static Log log = LogFactory
			.getLog(FinpaystatisticalreportBean.class);
	private PagingContext pagingcontext = new PagingContext(this);

	ITFELoginInfo loginfo;
	// 国库代码列表
	private List<TsTreasuryDto> treList;
	// 征收机关代码列表
	private List<TsTaxorgDto> taxorgList;
	// 征收机关代码
	private String sleTaxOrgCode;
	// 征收机关名称
	private String sleTaxOrgName;
	// 国库代码
	private String sleTreCode;
	// 国库名称
	private String sleTrename;
	// 查询日期
	private String rptdate;
	// 预算种类
	private String sleBudKind;
	// 预算级次
	private String sleBudLevel;
	// 报表类型
	private String sleBillType;
	// 报表栏式类型（二栏式、三栏式、四栏式）
	private String sleBillKind;

	// 辖属标志
	private String sleOfFlag;
	// 调整期标志
	private String sleTrimFlag;
	// 查询月份
	private String month;
	// 科目代码
	private String sbudgetsubcode;

	private String sleMoneyUnit;
	private String moneyUnitName;

	// 报表辖属标志
	private String offlag = null;
	// 报表预算级次标志
	private String sbudgetlevelname = null;
	// 报表类型标志
	private String slebilltypename = null;
	// 报表预算种类标志
	private String sbudgettypename = null;

	private String sleSumItem = null;

	// 含款合计list
	private List<TdEnumvalueDto> sumitemlist;

	private String slereportname = null;
	private String slereportnamecmt = null;
	// 报表名称list
	private List<TdEnumvalueDto> slereportnamelist;

	// 查询用list
	private List list = null;

	//
	private String reporttitle = null;

	//
	private String filenamecmt = null;

	//
	private String popath = null;

	private Boolean flag = true;

	// 预算单位代码
	private String scorpcode = null;

	// 预算单位名称
	private String scorpname = null;

	// 支出日报表二栏式显示
	private List payoutList = null;
	private Map payoutMap = new HashMap();
	private String payoutPath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportTwo.jasper";

	// 支出日报表三栏式显示
	private List payoutThreeList = null;
	private Map payoutThreeMap = new HashMap();
	private String payoutThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportThree.jasper";

	// 支出日报表四栏式显示
	private List payoutFourList = null;
	private Map payoutFourMap = new HashMap();
	private String payoutFourPath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportFour.jasper";

	// 支出旬报表二栏式显示
	private List payoutTenRptList = null;
	private Map payoutTenRptMap = new HashMap();
	private String payoutTenRptPath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportTwo.jasper";

	// 支出旬报表三栏式显示
	private List payoutTenRptThreeList = null;
	private Map payoutTenRptThreeMap = new HashMap();
	private String payoutTenRptThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportThree.jasper";

	// 支出旬报表四栏式显示
	private List payoutTenRptFourList = null;
	private Map payoutTenRptFourMap = new HashMap();
	private String payoutTenRptFourPath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportFour.jasper";

	// 支出月报表二栏式显示
	private List payoutMonthList = null;
	private Map payoutMonthMap = new HashMap();
	private String payoutMonthPath = "com/cfcc/itfe/client/ireport/FinBudgetRecMonthReportTwo.jasper";

	// 支出月报表三栏式显示
	private List payoutMonthThreeList = null;
	private Map payoutMonthThreeMap = new HashMap();
	private String payoutMonthThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecMonthReportThree.jasper";

	// 支出季报表二栏式显示
	private List payoutQuarList = null;
	private Map payoutQuarMap = new HashMap();
	private String payoutQuarPath = "com/cfcc/itfe/client/ireport/FinBudgetRecQuarReportTwo.jasper";

	// 支出季报表三栏式显示
	private List payoutQuarThreeList = null;
	private Map payoutQuarThreeMap = new HashMap();
	private String payoutQuarThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecQuarReportThree.jasper";

	// 支出年报表二栏式显示
	private List payoutYearList = null;
	private Map payoutYearMap = new HashMap();
	private String payoutYearPath = "com/cfcc/itfe/client/ireport/FinBudgetRecYearReportTwo.jasper";

	private List poList = null;
	private Map poMap = new HashMap();

	public FinpaystatisticalreportBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();

		init();
	}

	private void initstrastatelist() {

		// 含款合计
		TdEnumvalueDto finddto = new TdEnumvalueDto();
		finddto.setStypecode("0406");
		try {
			sumitemlist = commonDataAccessService.findRsByDtoUR(finddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}

		// 支出报表名称
		TdEnumvalueDto reportnamedto = new TdEnumvalueDto();
		reportnamedto.setStypecode("0319");
		try {
			slereportnamelist = commonDataAccessService
					.findRsByDtoUR(reportnamedto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
	}

	/**
	 * 设置批量打印参数
	 * 
	 * @param idto
	 */
	public void setBathPrar(TsBatchcrtreportmsgDto idto) {

		this.setFlag(false);
		this.setSleTreCode(idto.getSletrecode());
		this.setRptdate(idto.getRptdate());
		this.setSleBudKind(idto.getSlebudkind());
		this.setSleSumItem(idto.getSlesumitem());// 含款合计
		this.setSleBillType(idto.getSlebilltype());
		this.setSleBillKind(idto.getSlebillkind());
		this.setSleTrimFlag(idto.getSletrimflag());
		this.setSleOfFlag(idto.getSleofflag());
		this.setScorpcode(idto.getScorpcode());
		this.setSlereportname(idto.getSlereportname());
	}

	/**
	 * Direction: 查询 ename: query 引用方法: viewers: 报表显示界面 messages:
	 */
	public String query(Object o) {

		// 查询条件校验
		if (!querycheck()) {
			return null;
		}

		/**
		 * 报表传参查询
		 */
		if (!reportparamquery()) {
			return null;
		}

		/**
		 * 取得报表数据
		 */
		try {
			poList = budgetPayoutQueryList();
			if (null == poList || poList.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询无记录");
				return null;
			}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}

		/**
		 * 编制报表共通信息
		 */
		// 编制日期
		poMap
				.put("makedate", rptdate.substring(0, 4) + "年"
						+ rptdate.substring(4, 6) + "月"
						+ rptdate.substring(6, 8) + "日");
		// 辖属标志
		poMap.put("offlag", "【" + offlag + "】");
		// 预算种类
		poMap.put("sbudgettype", "【" + sbudgettypename + "】");
		// 制表人
		poMap.put("username", loginfo.getSuserName());
		// 预算单位代码
		poMap.put("taxorgname", this.scorpname);
		poMap.put("printtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(System.currentTimeMillis()));

		// 报表标题
		reporttitle = sleTrename + slereportnamecmt + slebilltypename + "表";
		poMap.put("billtitle", reporttitle);

		filenamecmt = "_" + offlag + "_" + sbudgettypename;

		/**
		 * 具体报表信息展示
		 */
		// 财政预算支出日报表查询
		if (StateConstant.REPORT_DAY.equals(sleBillType)) {
			// 日报表查询
			dayReportQuery();
		}
		// 财政预算支出旬报表查询
		else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
			// 旬报表查询
			tenReportQuery();
		}
		// 财政预算支出月报表查询
		else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
			// 月报表查询
			monthReportQuery();
		}
		// 财政预算支出季报表查询
		else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
			// 季报表查询
			quarReportQuery();
		}
		// 财政预算支出年报表查询
		else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
			// 年报表查询
			yearReportQuery();
		}
		return super.query(o);
	}

	/**
	 * 财政预算支出日报表查询
	 */
	public void dayReportQuery() {
		// 财政预算支出日报表二栏式
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {

			filenamecmt += "_二栏式";
			popath = payoutPath;

			this.setPayoutList(poList);
			this.setPayoutMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出日报表二栏式", true);
		}
		// 财政预算支出日报表三栏式
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {

			filenamecmt += "_三栏式";
			popath = payoutThreePath;

			this.setPayoutThreeList(poList);
			this.setPayoutThreeMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出日报表三栏式", true);
		}
		// 财政预算支出日报表四栏式
		else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {

			filenamecmt += "_四栏式";
			popath = payoutFourPath;

			this.setPayoutFourList(poList);
			this.setPayoutFourMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出日报表四栏式", true);
		}
	}

	/**
	 * 财政预算支出旬报表查询
	 */
	public void tenReportQuery() {
		// 财政预算支出旬报表二栏式
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {

			filenamecmt += "_二栏式";
			popath = payoutTenRptPath;

			this.setPayoutTenRptList(poList);
			this.setPayoutTenRptMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出旬报表二栏式", true);
		}
		// 财政预算支出旬报表三栏式
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {

			filenamecmt += "_三栏式";
			popath = payoutTenRptThreePath;

			this.setPayoutTenRptThreeList(poList);
			this.setPayoutTenRptThreeMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出旬报表三栏式", true);
		}
		// 财政预算支出旬报表四栏式
		else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {

			filenamecmt += "_四栏式";
			popath = payoutTenRptFourPath;

			this.setPayoutTenRptFourList(poList);
			this.setPayoutTenRptFourMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出旬报表四栏式", true);
		}
	}

	/**
	 * 财政预算支出月报表查询
	 */
	public void monthReportQuery() {
		// 财政预算支出月报表二栏式
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {

			filenamecmt += "_二栏式";
			popath = payoutMonthPath;

			this.setPayoutMonthList(poList);
			this.setPayoutMonthMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出月报表二栏式", true);
		}
		// 财政预算支出月报表三栏式
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {

			filenamecmt += "_三栏式";
			popath = payoutMonthThreePath;

			this.setPayoutMonthThreeList(poList);
			this.setPayoutMonthThreeMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出月报表三栏式", true);
		} else {
			MessageDialog.openMessageDialog(null, "财政预算支出统计月报表只有二、三栏式");
			return;
		}
	}

	/**
	 * 财政预算支出季报表查询
	 */
	public void quarReportQuery() {
		// 财政预算支出季报表二栏式
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {

			filenamecmt += "_二栏式";
			popath = payoutQuarPath;

			this.setPayoutQuarList(poList);
			this.setPayoutQuarMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出季报表二栏式", true);
		}
		// 财政预算支出季报表三栏式
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {

			filenamecmt += "_三栏式";
			popath = payoutQuarThreePath;

			this.setPayoutQuarThreeList(poList);
			this.setPayoutQuarThreeMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出季报表三栏式", true);
		} else {
			MessageDialog.openMessageDialog(null, "财政预算支出统计季报表只有二、三栏式");
			return;
		}
	}

	/**
	 * 财政预算支出年报表查询
	 */
	public void yearReportQuery() {
		// 财政预算支出年报表二栏式
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {

			filenamecmt += "_二栏式";
			popath = payoutYearPath;

			this.setPayoutYearList(poList);
			this.setPayoutYearMap(poMap);
			if (flag)
				editor.openComposite("财政预算支出年报表二栏式", true);
		} else {
			MessageDialog.openMessageDialog(null, "财政预算支出统计年报表只有二栏式");
			return;
		}
	}

	/**
	 * 财政预算支出查询 return list
	 * 
	 * @throws ITFEBizException
	 */
	public List budgetPayoutQueryList() throws ITFEBizException {

		TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();
		// 国库主体代码
		payoutdto.setStrecode(sleTreCode);
		// 预算种类
		payoutdto.setSbudgettype(sleBudKind);
		// 日期
		payoutdto.setSrptdate(rptdate);

		// 预算级次代码--用预算级次代码当做报表名称类型（1一般预算支出，2人行办理授权支付）
		payoutdto.setSbudgetlevelcode(slereportname);

		// 预算单位代码
		payoutdto.setSfinorgcode(scorpcode);

		// // 报表类型
		// payoutdto.setSbillkind(sleBillType);
		// 辖属标志
		// payoutdto.setSbelongflag(sleOfFlag);
		// // 调整期标志
		// payoutdto.setStrimflag(sleTrimFlag);

		try {
			list = finpaystatisticalreportService.makePayoutBill(payoutdto,
					sleSumItem, sleMoneyUnit, rptdate); // --第2项传入是否含项合计
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

		if (null == sleTreCode || "".equals(sleTreCode)) {
			MessageDialog.openMessageDialog(null, "请选择国库代码");
			return false;
		}
		// if (null == sleTaxOrgCode || "".equals(sleTaxOrgCode)) {
		// MessageDialog.openMessageDialog(null, "请选择征收机关代码");
		// return false;
		// }
		if (null == rptdate || "".equals(rptdate)) {
			MessageDialog.openMessageDialog(null, "请输入查询日期");
			return false;
		}
		if (null == sleBillKind || "".equals(sleBillKind)) {
			MessageDialog.openMessageDialog(null, "请选择报表栏式类型");
			return false;
		}
		if (null == sleBillType || "".equals(sleBillType)) {
			MessageDialog.openMessageDialog(null, "请选择报表类型");
			return false;
		}
		if (null == sleBudKind || "".equals(sleBudKind)) {
			MessageDialog.openMessageDialog(null, "请选择预算种类");
			return false;
		}
		// if (null == sleBudLevel || "".equals(sleBudLevel)) {
		// MessageDialog.openMessageDialog(null, "请选择预算级次");
		// return false;
		// }
		return true;
	}

	/**
	 * 报表传参查询
	 */
	public boolean reportparamquery() {
		List<TdEnumvalueDto> enumList = new ArrayList<TdEnumvalueDto>();

		try {
			TdEnumvalueDto EnumvalueDto = new TdEnumvalueDto();
			// 报表辖属标志
			EnumvalueDto.setStypecode("0302");
			enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			for (TdEnumvalueDto valueDto : enumList) {
				if (valueDto.getSvalue().equals(sleOfFlag)) {
					offlag = valueDto.getSvaluecmt();
					break;
				}
			}
			enumList.clear();
			// // 报表预算级次标志
			// EnumvalueDto.setStypecode("0121");
			// enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			// for (TdEnumvalueDto valueDto : enumList) {
			// if (valueDto.getSvalue().equals(sleBudLevel)) {
			// sbudgetlevelname = valueDto.getSvaluecmt();
			// }
			// }
			// enumList.clear();
			// 报表类型标志
			EnumvalueDto.setStypecode("0314");
			enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			for (TdEnumvalueDto valueDto : enumList) {
				if (valueDto.getSvalue().equals(sleBillType)) {
					slebilltypename = valueDto.getSvaluecmt();
					break;
				}
			}
			enumList.clear();
			// 报表预算种类标志
			EnumvalueDto.setStypecode("0122");
			enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			for (TdEnumvalueDto valueDto : enumList) {
				if (valueDto.getSvalue().equals(sleBudKind)) {
					sbudgettypename = valueDto.getSvaluecmt();
					break;
				}
			}
			enumList.clear();

			for (TdEnumvalueDto valueDto : slereportnamelist) {
				if (valueDto.getSvalue().equals(slereportname)) {
					slereportnamecmt = valueDto.getSvaluecmt();
					break;
				}
			}
			return true;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return false;
		}
	}

	/**
	 * Direction: 导出文件 ename: exportFile 引用方法: viewers: * messages:
	 */
	public String exportFile(Object o) {

		String dirsep = File.separator; // 取得系统分割符
		String filepath = "C:\\" + "Report" + dirsep
				+ TimeFacade.getCurrentStringTime() + dirsep;
		if (scorpname == null) {
			scorpname = "";
		}
		String copyFilename = this.sleTreCode
				+ "_"
				+ rptdate
				+ "_"
				+ reporttitle
				+ filenamecmt
				+ "_"
				+ scorpname
				+ "_"
				+ (new SimpleDateFormat("ddHHmmsss").format(System
						.currentTimeMillis()) + "R.xls");
		OutputStream out = null;
		try {

			File file = new File(filepath + copyFilename);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}

			out = new FileOutputStream(file);
			ReportGen.exportReportToXLS(poMap, poList, popath, out);
			FileUtil.getInstance().writeFile(out);

		} catch (JRException e) {
			MessageDialog.openErrorDialog(null, e);
			return "";
		} catch (FileNotFoundException e) {
			MessageDialog.openErrorDialog(null, e);
			return "";
		} catch (FileOperateException e) {
			MessageDialog.openErrorDialog(null, e);
			return "";
		}

		MessageDialog.openMessageDialog(null, "导出文件成功！已保存到：" + filepath
				+ copyFilename);

		return super.exportFile(o);
	}

	/**
	 * Direction: 导出Excel文件 ename: exportExcelFile 引用方法: viewers: * messages:
	 */
	public String exportExcelFile(Object o) {
		String dirsep = File.separator; // 取得系统分割符
		String filerootpath = "C:\\" + "Report" + dirsep
				+ TimeFacade.getCurrentStringTime() + dirsep;
		if (scorpname == null) {
			scorpname = "";
		}
		String copyFilename = this.sleTreCode
				+ "_"
				+ rptdate
				+ "_"
				+ reporttitle
				+ filenamecmt
				+ "_"
				+ scorpname
				+ "_"
				+ (new SimpleDateFormat("ddHHmmsss").format(System
						.currentTimeMillis()) + "R.xls");

		String datename = rptdate.substring(0, 4) + "年"
				+ rptdate.substring(4, 6) + "月" + rptdate.substring(6, 8) + "日";
		ReportExcel.init();

		ReportExcel.setFilepath(filerootpath);

		ReportExcel.setNewfilepath(filerootpath);
		// 新建报表名称
		ReportExcel.setCopyFilename(copyFilename);

		ReportExcel.setReporttitle(sleTrename + slereportnamecmt
				+ slebilltypename + "表");
		// 编制年月日
		String date = TimeFacade.getCurrentStringTime();
		date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
				+ date.substring(6, 8) + "日";
		ReportExcel.setDate(date);

		// 报表模板名称
		ReportExcel.setFilename("model4.xls");
		ReportExcel.setInputstream(FinpaystatisticalreportBean.class
				.getResourceAsStream("model4.xls"));
		// 报表sheet名称
		ReportExcel.setSheetname(datename);

		try {
			ReportExcel.getreportbyOutrpt(list, offlag, sbudgettypename,
					sleBillType, sleBillKind, loginfo.getSuserName());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}

		MessageDialog.openMessageDialog(null, "导出文件成功！已保存到：" + filerootpath
				+ copyFilename);
		return super.exportExcelFile(o);
	}

	/**
	 * Direction: 导出pdf文件 ename: exportPdfFile 引用方法: viewers: * messages:
	 */
	public String exportPdfFile(Object o) {

		String dirsep = File.separator; // 取得系统分割符
		String filepath = "C:\\" + "Report" + dirsep
				+ TimeFacade.getCurrentStringTime() + dirsep;
		if (scorpname == null) {
			scorpname = "";
		}
		String copyFilename = this.sleTreCode
				+ "_"
				+ rptdate
				+ "_"
				+ reporttitle
				+ filenamecmt
				+ "_"
				+ scorpname
				+ "_"
				+ (new SimpleDateFormat("ddHHmmsss").format(System
						.currentTimeMillis()) + "R.pdf");
		OutputStream out = null;
		try {

			File file = new File(filepath + copyFilename);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}

			out = new FileOutputStream(file);
			ReportGen.exportReportToPDF(poMap, poList, popath, out);
			FileUtil.getInstance().writeFile(out);

		} catch (JRException e) {
			MessageDialog.openErrorDialog(null, e);
			return "";
		} catch (FileNotFoundException e) {
			MessageDialog.openErrorDialog(null, e);
			return "";
		} catch (FileOperateException e) {
			MessageDialog.openErrorDialog(null, e);
			return "";
		}

		MessageDialog.openMessageDialog(null, "导出文件成功！已保存到：" + filepath
				+ copyFilename);
		return super.exportPdfFile(o);
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 财政收支月报查询条件 messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
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

	/**
	 * 页面查询条件初始化
	 */
	private void init() {
		// 查询国库代码用dto
		TsTreasuryDto tredto = new TsTreasuryDto();
		// 中心机构代码
		String centerorg = null;

		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
			// 中心机构，取得所有国库列表
			// mod -20110906 加市级查全辖条件
			// if (loginfo.getSorgcode().equals(centerorg) ||
			// loginfo.getSorgcode().equals("060300000003")) {
			// treList = commonDataAccessService.findRsByDto(tredto);
			// }
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// 非中心机构，取得登录机构对应国库
			else {
				tredto = new TsTreasuryDto();
				// tredto.setSbookorgcode(loginfo.getSorgcode());
				String trecodesql = this.getTrecodesql();

				treList = commonDataAccessService.findRsByDtoWithWhere(tredto,
						trecodesql);
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
				break;
			}
		}

		slereportnamelist = new ArrayList<TdEnumvalueDto>();
		// 初始化含款合计and报表名称
		initstrastatelist();

		// 初始化征收机关代码
		querytaxoeglist(this.getSleTreCode());
		// 报表类型
		sleBillType = StateConstant.REPORT_DAY;
		// 栏式类型
		sleBillKind = StateConstant.REPORT_TWO;
		// 调整期标志
		sleTrimFlag = StateConstant.TRIMSIGN_FLAG_NORMAL;
		sleMoneyUnit = StateConstant.MONEY_UNIT_5;
		moneyUnitName = "单位 ：元";
		// 辖属标志
		sleOfFlag = MsgConstant.RULE_SIGN_SELF;
		// 预算种类
		sleBudKind = MsgConstant.BDG_KIND_IN;
		// 预算级次
		sleBudLevel = MsgConstant.BUDGET_LEVEL_CENTER;
		// 查询日期
		rptdate = TimeFacade.getCurrentStringTime();
		// 报表名称
		slereportname = this.getSlereportnamelist().get(0).getSvalue();
		// 含款合计
		sleSumItem = this.getSumitemlist().get(0).getSvalue();
	}

	public String getTrecodesql() {
		// 获得用户所挂接的国库主体，并作为查询条件
		List trecodes = loginfo.getTreasuryCodes();
		StringBuffer sbf = new StringBuffer();
		String trecodesql = "";
		if (null != trecodes && trecodes.size() > 0) {
			for (int i = 0; i < trecodes.size(); i++) {
				sbf.append("'").append(trecodes.get(i)).append("',");
			}
			trecodesql = sbf.toString().substring(0,
					sbf.toString().lastIndexOf(","));
			trecodesql = "  WHERE  S_TRECODE IN (" + trecodesql + ") ";
		}
		return trecodesql;
	}

	/**
	 * 根据国库代码查询征收机关代码 国库与征收机关对应关系表
	 */
	private List querytaxoeglist(String sleTreCode) {

		// 查询国库对应征收机关代码用dto
		TsTaxorgDto taxorgdto = new TsTaxorgDto();
		taxorgdto.setStrecode(sleTreCode);// 国库代码
		try {
			taxorgList = commonDataAccessService.findRsByDto(taxorgdto);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		// 初始化征收机关代码默认值
		if (taxorgList.size() > 0) {
			sleTaxOrgCode = taxorgList.get(0).getStaxorgcode();
		}
		return taxorgList;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getSbudgetsubcode() {
		return sbudgetsubcode;
	}

	public void setSbudgetsubcode(String sbudgetsubcode) {
		this.sbudgetsubcode = sbudgetsubcode;
	}

	public List getPayoutList() {
		return payoutList;
	}

	public void setPayoutList(List payoutList) {
		this.payoutList = payoutList;
	}

	public Map getPayoutMap() {
		return payoutMap;
	}

	public void setPayoutMap(Map payoutMap) {
		this.payoutMap = payoutMap;
	}

	public String getPayoutPath() {
		return payoutPath;
	}

	public void setPayoutPath(String payoutPath) {
		this.payoutPath = payoutPath;
	}

	public List<TsTreasuryDto> getTreList() {
		return treList;
	}

	public void setTreList(List<TsTreasuryDto> treList) {
		this.treList = treList;
	}

	public String getSleBillKind() {
		return sleBillKind;
	}

	public void setSleBillKind(String sleBillKind) {
		this.sleBillKind = sleBillKind;
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

		// List list = querytaxoeglist(sleTreCode);
		// if (list != null && list.size() > 0) {
		// taxorgList = list;
		// } else {
		// taxorgList.clear();
		// }
		if (flag)
			editor.fireModelChanged();
	}

	public String getSleOfFlag() {
		return sleOfFlag;
	}

	public void setSleOfFlag(String sleOfFlag) {
		this.sleOfFlag = sleOfFlag;

		if (flag) {
			if (sleOfFlag.equals(MsgConstant.RULE_SIGN_ALL)) {
				sleBudLevel = MsgConstant.BUDGET_LEVEL_ALL;
			} else {
				sleBudLevel = MsgConstant.BUDGET_LEVEL_CENTER;
			}
			editor.fireModelChanged();
		}
	}

	public String getSleBillType() {
		return sleBillType;
	}

	public void setSleBillType(String sleBillType) {
		this.sleBillType = sleBillType;
	}

	public String getSleMoneyUnit() {
		return sleMoneyUnit;
	}

	public void setSleMoneyUnit(String sleMoneyUnit) {
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_5)) {
			moneyUnitName = "单位： 元";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_4)) {
			moneyUnitName = "单位： 万元";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			moneyUnitName = "单位：白万 ";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			moneyUnitName = "单位： 十万";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			moneyUnitName = "单位： 千万";
		}
		this.sleMoneyUnit = sleMoneyUnit;
	}

	public String getRptdate() {
		return rptdate;
	}

	public void setRptdate(String rptdate) {
		this.rptdate = rptdate;
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

	public String getPopath() {
		return popath;
	}

	public void setPopath(String popath) {
		this.popath = popath;
	}

	/**
	 * @return the sleTrimFlag
	 */
	public String getSleTrimFlag() {
		return sleTrimFlag;
	}

	/**
	 * @param sleTrimFlag
	 *            the sleTrimFlag to set
	 */
	public void setSleTrimFlag(String sleTrimFlag) {
		this.sleTrimFlag = sleTrimFlag;
	}

	public String getSleBudLevel() {
		return sleBudLevel;
	}

	public void setSleBudLevel(String sleBudLevel) {
		this.sleBudLevel = sleBudLevel;
	}

	public String getSleTrename() {
		return sleTrename;
	}

	public void setSleTrename(String sleTrename) {
		this.sleTrename = sleTrename;
	}

	public String getMoneyUnitName() {
		return moneyUnitName;
	}

	public void setMoneyUnitName(String moneyUnitName) {
		this.moneyUnitName = moneyUnitName;
	}

	public List<TsTaxorgDto> getTaxorgList() {
		return taxorgList;
	}

	public void setTaxorgList(List<TsTaxorgDto> taxorgList) {
		this.taxorgList = taxorgList;
	}

	public String getSleTaxOrgCode() {
		return sleTaxOrgCode;
	}

	public void setSleTaxOrgCode(String sleTaxOrgCode) {
		for (TsTaxorgDto tmp : taxorgList) {
			if (tmp.getStaxorgcode().equals(sleTaxOrgCode)) {
				sleTaxOrgName = tmp.getStaxorgname();
			}
		}
		this.sleTaxOrgCode = sleTaxOrgCode;
	}

	public String getSleTaxOrgName() {
		return sleTaxOrgName;
	}

	public void setSleTaxOrgName(String sleTaxOrgName) {
		this.sleTaxOrgName = sleTaxOrgName;
	}

	public String getSleBudKind() {
		return sleBudKind;
	}

	public void setSleBudKind(String sleBudKind) {
		this.sleBudKind = sleBudKind;
	}

	public List getPayoutThreeList() {
		return payoutThreeList;
	}

	public void setPayoutThreeList(List payoutThreeList) {
		this.payoutThreeList = payoutThreeList;
	}

	public Map getPayoutThreeMap() {
		return payoutThreeMap;
	}

	public void setPayoutThreeMap(Map payoutThreeMap) {
		this.payoutThreeMap = payoutThreeMap;
	}

	public String getPayoutThreePath() {
		return payoutThreePath;
	}

	public void setPayoutThreePath(String payoutThreePath) {
		this.payoutThreePath = payoutThreePath;
	}

	public List getPayoutFourList() {
		return payoutFourList;
	}

	public void setPayoutFourList(List payoutFourList) {
		this.payoutFourList = payoutFourList;
	}

	public Map getPayoutFourMap() {
		return payoutFourMap;
	}

	public void setPayoutFourMap(Map payoutFourMap) {
		this.payoutFourMap = payoutFourMap;
	}

	public String getPayoutFourPath() {
		return payoutFourPath;
	}

	public void setPayoutFourPath(String payoutFourPath) {
		this.payoutFourPath = payoutFourPath;
	}

	public String getOfflag() {
		return offlag;
	}

	public void setOfflag(String offlag) {
		this.offlag = offlag;
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

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public List getPayoutTenRptList() {
		return payoutTenRptList;
	}

	public void setPayoutTenRptList(List payoutTenRptList) {
		this.payoutTenRptList = payoutTenRptList;
	}

	public Map getPayoutTenRptMap() {
		return payoutTenRptMap;
	}

	public void setPayoutTenRptMap(Map payoutTenRptMap) {
		this.payoutTenRptMap = payoutTenRptMap;
	}

	public String getPayoutTenRptPath() {
		return payoutTenRptPath;
	}

	public void setPayoutTenRptPath(String payoutTenRptPath) {
		this.payoutTenRptPath = payoutTenRptPath;
	}

	public List getPayoutTenRptThreeList() {
		return payoutTenRptThreeList;
	}

	public void setPayoutTenRptThreeList(List payoutTenRptThreeList) {
		this.payoutTenRptThreeList = payoutTenRptThreeList;
	}

	public Map getPayoutTenRptThreeMap() {
		return payoutTenRptThreeMap;
	}

	public void setPayoutTenRptThreeMap(Map payoutTenRptThreeMap) {
		this.payoutTenRptThreeMap = payoutTenRptThreeMap;
	}

	public String getPayoutTenRptThreePath() {
		return payoutTenRptThreePath;
	}

	public void setPayoutTenRptThreePath(String payoutTenRptThreePath) {
		this.payoutTenRptThreePath = payoutTenRptThreePath;
	}

	public List getPayoutTenRptFourList() {
		return payoutTenRptFourList;
	}

	public void setPayoutTenRptFourList(List payoutTenRptFourList) {
		this.payoutTenRptFourList = payoutTenRptFourList;
	}

	public Map getPayoutTenRptFourMap() {
		return payoutTenRptFourMap;
	}

	public void setPayoutTenRptFourMap(Map payoutTenRptFourMap) {
		this.payoutTenRptFourMap = payoutTenRptFourMap;
	}

	public String getPayoutTenRptFourPath() {
		return payoutTenRptFourPath;
	}

	public void setPayoutTenRptFourPath(String payoutTenRptFourPath) {
		this.payoutTenRptFourPath = payoutTenRptFourPath;
	}

	public List getPayoutMonthList() {
		return payoutMonthList;
	}

	public void setPayoutMonthList(List payoutMonthList) {
		this.payoutMonthList = payoutMonthList;
	}

	public Map getPayoutMonthMap() {
		return payoutMonthMap;
	}

	public void setPayoutMonthMap(Map payoutMonthMap) {
		this.payoutMonthMap = payoutMonthMap;
	}

	public String getPayoutMonthPath() {
		return payoutMonthPath;
	}

	public void setPayoutMonthPath(String payoutMonthPath) {
		this.payoutMonthPath = payoutMonthPath;
	}

	public List getPayoutMonthThreeList() {
		return payoutMonthThreeList;
	}

	public void setPayoutMonthThreeList(List payoutMonthThreeList) {
		this.payoutMonthThreeList = payoutMonthThreeList;
	}

	public Map getPayoutMonthThreeMap() {
		return payoutMonthThreeMap;
	}

	public void setPayoutMonthThreeMap(Map payoutMonthThreeMap) {
		this.payoutMonthThreeMap = payoutMonthThreeMap;
	}

	public String getPayoutMonthThreePath() {
		return payoutMonthThreePath;
	}

	public void setPayoutMonthThreePath(String payoutMonthThreePath) {
		this.payoutMonthThreePath = payoutMonthThreePath;
	}

	public List getPayoutQuarList() {
		return payoutQuarList;
	}

	public void setPayoutQuarList(List payoutQuarList) {
		this.payoutQuarList = payoutQuarList;
	}

	public Map getPayoutQuarMap() {
		return payoutQuarMap;
	}

	public void setPayoutQuarMap(Map payoutQuarMap) {
		this.payoutQuarMap = payoutQuarMap;
	}

	public String getPayoutQuarPath() {
		return payoutQuarPath;
	}

	public void setPayoutQuarPath(String payoutQuarPath) {
		this.payoutQuarPath = payoutQuarPath;
	}

	public List getPayoutQuarThreeList() {
		return payoutQuarThreeList;
	}

	public void setPayoutQuarThreeList(List payoutQuarThreeList) {
		this.payoutQuarThreeList = payoutQuarThreeList;
	}

	public Map getPayoutQuarThreeMap() {
		return payoutQuarThreeMap;
	}

	public void setPayoutQuarThreeMap(Map payoutQuarThreeMap) {
		this.payoutQuarThreeMap = payoutQuarThreeMap;
	}

	public String getPayoutQuarThreePath() {
		return payoutQuarThreePath;
	}

	public void setPayoutQuarThreePath(String payoutQuarThreePath) {
		this.payoutQuarThreePath = payoutQuarThreePath;
	}

	public List getPayoutYearList() {
		return payoutYearList;
	}

	public void setPayoutYearList(List payoutYearList) {
		this.payoutYearList = payoutYearList;
	}

	public Map getPayoutYearMap() {
		return payoutYearMap;
	}

	public void setPayoutYearMap(Map payoutYearMap) {
		this.payoutYearMap = payoutYearMap;
	}

	public String getPayoutYearPath() {
		return payoutYearPath;
	}

	public void setPayoutYearPath(String payoutYearPath) {
		this.payoutYearPath = payoutYearPath;
	}

	public List getPoList() {
		return poList;
	}

	public void setPoList(List poList) {
		this.poList = poList;
	}

	public Map getPoMap() {
		return poMap;
	}

	public void setPoMap(Map poMap) {
		this.poMap = poMap;
	}

	public String getSleSumItem() {
		return sleSumItem;
	}

	public void setSleSumItem(String sleSumItem) {
		this.sleSumItem = sleSumItem;
	}

	public List<TdEnumvalueDto> getSumitemlist() {
		return sumitemlist;
	}

	public void setSumitemlist(List<TdEnumvalueDto> sumitemlist) {
		this.sumitemlist = sumitemlist;
	}

	public String getSlereportname() {
		return slereportname;
	}

	public void setSlereportname(String slereportname) {
		this.slereportname = slereportname;
	}

	public List<TdEnumvalueDto> getSlereportnamelist() {
		return slereportnamelist;
	}

	public void setSlereportnamelist(List<TdEnumvalueDto> slereportnamelist) {
		this.slereportnamelist = slereportnamelist;
	}

	public String getSlereportnamecmt() {
		return slereportnamecmt;
	}

	public void setSlereportnamecmt(String slereportnamecmt) {
		this.slereportnamecmt = slereportnamecmt;
	}

	public String getScorpcode() {
		return scorpcode;
	}

	public void setScorpcode(String scorpcode) {

		if (scorpcode != null && !"".equals(scorpcode)) {
			try {
				TdCorpDto tdcorpdto = new TdCorpDto();
				tdcorpdto.setSbookorgcode(loginfo.getSorgcode());
				tdcorpdto.setStrecode(this.getSleTreCode());
				tdcorpdto.setCtrimflag(this.getSleTrimFlag());
				tdcorpdto.setScorpcode(scorpcode);
				List<TdCorpDto> list = commonDataAccessService
						.findRsByDto(tdcorpdto);

				if (list != null && list.size() > 0) {
					this.scorpcode = scorpcode;
					setScorpname(list.get(0).getScorpname());
				} else {
					MessageDialog.openMessageDialog(null, "预算单位代码不存在或者与国库不对应！");
					setScorpname("");
					return;
				}

			} catch (ITFEBizException e) {
				MessageDialog.openErrorDialog(null, e);
				return;
			}
		} else {
			setScorpname("");
		}
		if (flag)
			editor.fireModelChanged();
	}

	public String getScorpname() {
		return scorpname;
	}

	public void setScorpname(String scorpname) {
		this.scorpname = scorpname;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

}
package com.cfcc.itfe.client.dataquery.budgetsubcodemonthlyreport;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.BudgetSubCodeDialog;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.ShanghaiReport;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.pk.TsTreasuryPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.util.ReportExcel;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 13-08-29 15:20:11 子系统: DataQuery 模块:budgetsubcodemonthlyreport
 *       组件:Budgetsubcodemonthlyreport
 */
public class BudgetsubcodemonthlyreportBean extends
		AbstractBudgetsubcodemonthlyreportBean implements IPageDataProvider {

	private static Log log = LogFactory
			.getLog(BudgetsubcodemonthlyreportBean.class);

	private ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	private ShanghaiReport searchdto = null;
	private String subcode;
	private List trelist;
	private String reporttype;
	private List reporttypelist;

	public BudgetsubcodemonthlyreportBean() {
		super();
		searchdto = new ShanghaiReport();
		TsTreasuryDto tredto = new TsTreasuryDto();
		if (!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
			tredto.setSorgcode(loginfo.getSorgcode());
		} else {
			tredto.setStreattrib(StateConstant.COMMON_YES);
		}
		try {
			trelist = commonDataAccessService.getSubTreCode(loginfo
					.getSorgcode());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		// 调整期标志（默认正常期）、国库（默认分库）、辖属标志（本级或全辖，默认全辖）、预算级次预算级次（中央级、市级、区级、地方级、不分级次）、年度起止（默认本年度）、月度起止、金额单位（元、万元、亿元，默认亿元）
		searchdto.setTrecode("0900000000");
		searchdto.setStrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL);
		searchdto.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
		searchdto.setSlevelcode(StateConstant.SUBJECTLEVEL_ANY);
		searchdto.setMoneyunit(StateConstant.MONEY_UNIT_3);
		searchdto.setStartdate(new Date(System.currentTimeMillis()));
		searchdto.setEnddate(new Date(System.currentTimeMillis()));
		reporttypelist = new ArrayList();
		Mapper mapper2 = new Mapper();
		reporttype = "TR_INCOMEDAYRPT";
		mapper2.setDisplayValue("收入报表");
		mapper2.setUnderlyValue("TR_INCOMEDAYRPT");
		reporttypelist.add(mapper2);
		Mapper mapper1 = new Mapper();
		mapper1.setDisplayValue("支出报表");
		mapper1.setUnderlyValue("TR_TAXORG_PAYOUT_REPORT");
		reporttypelist.add(mapper1);
		
	}

	/**
	 * Direction: 导出 ename: export 引用方法: viewers: * messages:
	 */
	public String export(Object o) {

		if ("TR_TAXORG_PAYOUT_REPORT".equals(reporttype)) {
			if (StringUtils.isNotBlank(searchdto.getStrimflag())) {
				// 调整期直接返回null 数据库没有调整期标志
				if (StateConstant.TRIMSIGN_FLAG_TRIM.equals(searchdto
						.getStrimflag())) {
					MessageDialog.openMessageDialog(null, "没有符合条件的数据！");
					return null;
				}
			}
			if (StringUtils.isNotBlank(searchdto.getSbelongflag())) {
				// 没有本级报表 直接返回
				if (MsgConstant.RULE_SIGN_SELF.equals(searchdto
						.getSbelongflag())) {
					MessageDialog.openMessageDialog(null, "没有符合条件的数据！");
					return null;
				}
			}
		}
		if (StringUtils.isNotBlank(subcode)) {
			String[] subcodes = subcode.replaceAll("，", ",").split(",");
			if (null != subcodes && subcodes.length != 0) {
				StringBuffer code = new StringBuffer();
				code.append("'" + subcodes[0] + "'");
				for (int i = 1; i < subcodes.length; i++) {
					code.append(",'" + subcodes[i] + "'");
				}
				searchdto.setSubcode(code.toString());
			}
		}

		try {
			List list = budgetsubcodemonthlyreportService.getReportData(
					searchdto, reporttype);
			if (null == list || list.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有符合条件的数据！");
				return null;
			}
			String msg = genReportFile(list);
			MessageDialog.openMessageDialog(null, "文件已生成！已保存到：" + msg);
		} catch (ITFEBizException e) {
			log.error("获取报表内容失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.export(o);
	}

	private String genReportFile(List result) throws ITFEBizException {
		try {
			String dirsep = File.separator; // 取得系统分割符
			TsTreasuryPK treasuryPK = new TsTreasuryPK();
			treasuryPK.setSorgcode(loginfo.getSorgcode());
			treasuryPK.setStrecode(searchdto.getTrecode());
			TsTreasuryDto tsTreasuryDto = (TsTreasuryDto) commonDataAccessService
					.find(treasuryPK);
			String datename = "20110101";
			ReportExcel.init();
			// 国家金库上海市闵行支库（代理）
			String copyFilename = tsTreasuryDto.getStrename().replaceAll(
					"（代理）", "")
					+ "("
					+ (MsgConstant.RULE_SIGN_ALL.equals(searchdto
							.getSbelongflag()) ? "全辖" : "本级")
					+ ")预算"
					+ ("TR_TAXORG_PAYOUT_REPORT".equals(reporttype) ? "支出"
							: "收入")
					+ "分科目"
					+ DateUtil.date2String2(searchdto.getStartdate())
							.substring(0, 4)
					+ "-"
					+ DateUtil.date2String2(searchdto.getEnddate()).substring(
							0, 4) 
					+ "年月度汇总表"
					+ "_"
					+ (new SimpleDateFormat("ddHHmmsss").format(System
							.currentTimeMillis()) + "R.xls");
			// 报表模块文件路径
			String filerootpath = "C:\\" + "Report" + dirsep;
			String rportpath = TimeFacade.getCurrentStringTime() + dirsep;
			ReportExcel.setNewfilepath(filerootpath + rportpath);
			// 新建报表名称
			ReportExcel.setCopyFilename(copyFilename);
			// 报表标题
			ReportExcel.setReporttitle(tsTreasuryDto.getStrename().replaceAll(
					"（代理）", "")
					+ "("
					+ (MsgConstant.RULE_SIGN_ALL.equals(searchdto
							.getSbelongflag()) ? "全辖" : "本级")
					+ ")预算"
					+ ("TR_TAXORG_PAYOUT_REPORT".equals(reporttype) ? "支出"
							: "收入")
					+ "分科目"
					+ DateUtil.date2String2(searchdto.getStartdate())
							.substring(0, 4)
					+ "-"
					+ DateUtil.date2String2(searchdto.getEnddate()).substring(
							0, 4) + "年月度汇总表");
			ReportExcel.setFilepath(filerootpath);
			ReportExcel.setUnit("");
			HashMap<String, Object> datamap = new HashMap<String, Object>();
			datamap.put("infoIds", result);
			datamap.put("dto", searchdto);
			ReportExcel.setDatamap(datamap);
			// 编制年月日
			String date = TimeFacade.getCurrentStringTime();
			date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
					+ date.substring(6, 8) + "日";
			ReportExcel.setDate(date);
			// 报表模板名称
			ReportExcel.setFilename("model.xls");
			ReportExcel.setInputstream(BudgetsubcodemonthlyreportBean.class
					.getResourceAsStream("model.xls"));
			// 报表sheet名称
			ReportExcel.setSheetname("sheet1");
			ReportExcel.bzunit = tsTreasuryDto.getStrename().replaceAll("（代理）",
					"");
			ReportExcel.bzuser = loginfo.getSuserName();

			String msg = ReportExcel.genBudgetsubcodemonthReport();
			return msg;
		} catch (ITFEBizException e) {
			log.error("生成报表失败！", e);
			throw new ITFEBizException("生成报表失败！", e);
		}
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

	public ShanghaiReport getSearchdto() {
		return searchdto;
	}

	public void setSearchdto(ShanghaiReport searchdto) {
		this.searchdto = searchdto;
	}

	public List getTrelist() {
		return trelist;
	}

	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}

	public String getSubcode() {
		return subcode;
	}

	public void setSubcode(String subcode) {
		this.subcode = subcode;
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

}
package com.cfcc.itfe.client.dataquery.shbudgetsubcodesearch;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.BudgetSubCodeDialog;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.ShanghaiReport;
import com.cfcc.itfe.persistence.dto.ShanghaiReport2;
import com.cfcc.itfe.persistence.dto.ShanghaiReport2Data;
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
 * @time 13-09-06 10:00:14 子系统: DataQuery 模块:shbudgetsubcodesearch
 *       组件:Shbudgetsubcodesearch
 */
public class ShbudgetsubcodesearchBean extends
		AbstractShbudgetsubcodesearchBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(ShbudgetsubcodesearchBean.class);
	private ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	private List trelist;
	private String reporttype;
	private String subcode;
	private List reportkind;
	private List reporttypelist;
	private ShanghaiReport searchdto = null;
	private String bizkind;
	private Map<String, List> reportkindmap;
	private Date startdate;

	public ShbudgetsubcodesearchBean() {
		super();
		searchdto = new ShanghaiReport();
		trelist = new ArrayList();
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
		init();

	}

	private void init() {
		// 调整期标志（默认正常期）、国库（默认分库）、辖属标志（本级或全辖，默认全辖）、预算级次预算级次（中央级、市级、区级、地方级、不分级次）、年度起止（默认本年度）、月度起止、金额单位（元、万元、亿元，默认亿元）
		searchdto.setTrecode("0900000000");
		searchdto.setStrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL);
		searchdto.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
		searchdto.setSlevelcode(StateConstant.SUBJECTLEVEL_ANY);
		searchdto.setMoneyunit(StateConstant.MONEY_UNIT_3);
		startdate = new Date(System.currentTimeMillis());
		reporttypelist = new ArrayList();
		Mapper bizkind2 = new Mapper();
		bizkind = "TR_INCOMEDAYRPT";
		bizkind2.setDisplayValue("收入报表");
		bizkind2.setUnderlyValue("TR_INCOMEDAYRPT");
		Mapper bizkind1 = new Mapper();
		bizkind1.setDisplayValue("支出报表");
		bizkind1.setUnderlyValue("TR_TAXORG_PAYOUT_REPORT");
		reporttypelist.add(bizkind2);
		reporttypelist.add(bizkind1);
		reporttype = StateConstant.REPORT_DAY;

		reportkindmap = new HashMap<String, List>();
		List tenrptlist = new ArrayList();
		Mapper mapper1 = new Mapper();
		mapper1.setDisplayValue("上旬");
		mapper1.setUnderlyValue(StateConstant.TENRPT_TOP);
		Mapper mapper2 = new Mapper();
		mapper2.setDisplayValue("中旬");
		mapper2.setUnderlyValue(StateConstant.TENRPT_MIDDLE);
		Mapper mapper3 = new Mapper();
		mapper3.setDisplayValue("下旬");
		mapper3.setUnderlyValue(StateConstant.TENRPT_BOTTOM);
		tenrptlist.add(mapper1);
		tenrptlist.add(mapper2);
		tenrptlist.add(mapper3);
		reportkindmap.put(StateConstant.REPORT_TEN, tenrptlist);
		List quar = new ArrayList();
		Mapper quar1 = new Mapper();
		quar1.setDisplayValue("第一季度");
		quar1.setUnderlyValue(StateConstant.QUARTERRPT_FIRST);
		Mapper quar2 = new Mapper();
		quar2.setDisplayValue("第二季度");
		quar2.setUnderlyValue(StateConstant.QUARTERRPT_SECOND);
		Mapper quar3 = new Mapper();
		quar3.setDisplayValue("第三季度");
		quar3.setUnderlyValue(StateConstant.QUARTERRPT_THREE);
		Mapper quar4 = new Mapper();
		quar4.setDisplayValue("第四季度");
		quar4.setUnderlyValue(StateConstant.QUARTERRPT_FOUR);
		quar.add(quar1);
		quar.add(quar2);
		quar.add(quar3);
		quar.add(quar4);
		reportkindmap.put(StateConstant.REPORT_QUAR, quar);
		List year = new ArrayList();
		Mapper year1 = new Mapper();
		year1.setDisplayValue("上半年");
		year1.setUnderlyValue(StateConstant.HALFYEARRPT_TOP);
		Mapper year2 = new Mapper();
		year2.setDisplayValue("下半年");
		year2.setUnderlyValue(StateConstant.HALFYEARRPT_BOTTOM);
		year.add(year1);
		year.add(year2);
		reportkindmap.put(StateConstant.REPORT_HALFYEAR, year);
	}

	/**
	 * Direction: 导出 ename: export 引用方法: viewers: * messages:
	 */
	public String export(Object o) {
		if ("TR_TAXORG_PAYOUT_REPORT".equals(bizkind)) {
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
		if (StateConstant.REPORT_DAY.equals(reporttype)) {
			searchdto.setEnddate(null);
		}
		this.editor.fireModelChanged();
		searchdto.setStartdate(startdate);
		if ((StateConstant.REPORT_HALFYEAR.equals(reporttype)
				|| StateConstant.REPORT_QUAR.equals(reporttype) || StateConstant.REPORT_TEN
				.equals(reporttype))
				&& StringUtils.isBlank(searchdto.getReportkind())) {
			MessageDialog.openMessageDialog(null, "报表类型为半年报、季报和旬报时，报表种类不能为空！");
			return null;
		}
		searchdto.setReporttype(reporttype);
		searchdto.setSubcode(null);
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
			List list = shbudgetsubcodesearchService.getreportdata(searchdto,
					bizkind);
			if (null == list || list.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有符合条件的数据！");
				return null;
			}
			String msg = genreport(list);
			MessageDialog.openMessageDialog(null, "文件已生成！已保存到：" + msg);
		} catch (ITFEBizException e) {
			log.error("获取报表内容失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}

		return super.export(o);
	}

	private String genreport(List<ShanghaiReport2> list)
			throws ITFEBizException {
		HashMap<String, List<ShanghaiReport2Data>> groupdatedata = new HashMap<String, List<ShanghaiReport2Data>>(); // 按日期把记录放到list
		// 存储所有的科目代码
		List<String> subgetcodes = new ArrayList<String>();
		// 记录 科目的上次记录
		HashMap<String, BigDecimal> lasttimeamt = new HashMap<String, BigDecimal>();
		// 记录所有的日期
		List<String> dates = new ArrayList<String>();
		ShanghaiReport2Data data;
		BigDecimal multiplyunit = new BigDecimal(100);
		for (ShanghaiReport2 tmp : list) {
			if (!dates.contains(tmp.getSrptdate())) {
				dates.add(tmp.getSrptdate());
			}
			// 存储科目信息
			if (!subgetcodes.contains(tmp.getSbudgetsubcode()
					+ tmp.getSbudgetsubname())) {
				subgetcodes.add(tmp.getSbudgetsubcode()
						+ tmp.getSbudgetsubname());
			}
			data = new ShanghaiReport2Data();
			if (null != lasttimeamt.get(tmp.getSbudgetsubcode())) {
				data.setSbudgetsubcode(tmp.getSbudgetsubcode()
						+ tmp.getSbudgetsubname());
				data.setSrptdate(tmp.getSrptdate());
				data.setSrptdate(tmp.getSrptdate());
				data.setNum1(tmp.getNmoneyday());
				data.setNum2(tmp.getNmoneyday().subtract(
						lasttimeamt.get(tmp.getSbudgetsubcode())));
				if (new BigDecimal(0).compareTo(lasttimeamt.get(tmp
						.getSbudgetsubcode())) == 0) {
					if (new BigDecimal(0).compareTo(data.getNum1()) == 0) {
						data.setNum3(new BigDecimal(0));
					} else {
						data.setNum3(new BigDecimal(100));
					}
				} else {
					data.setNum3(new BigDecimal((tmp.getNmoneyday().subtract(
							lasttimeamt.get(tmp.getSbudgetsubcode()))
							.doubleValue() / lasttimeamt.get(
							tmp.getSbudgetsubcode()).doubleValue()) * 100));
				}
				lasttimeamt.put(tmp.getSbudgetsubcode(), tmp.getNmoneyday());
			} else {
				lasttimeamt.put(tmp.getSbudgetsubcode(), tmp.getNmoneyday());
				data.setNum1(tmp.getNmoneyday());
				data.setNum2(null);
				data.setNum3(null);
				data.setSbudgetsubcode(tmp.getSbudgetsubcode()
						+ tmp.getSbudgetsubname());
				data.setSrptdate(tmp.getSrptdate());
			}
			// 按日期分解
			if (null != groupdatedata.get(tmp.getSrptdate())) {
				groupdatedata.get(tmp.getSrptdate()).add(data);
			} else {
				List<ShanghaiReport2Data> tmplist = new ArrayList<ShanghaiReport2Data>();
				tmplist.add(data);
				groupdatedata.put(tmp.getSrptdate(), tmplist);
			}
		}
		try {
			ReportExcel.init();
			String dirsep = File.separator; // 取得系统分割符
			TsTreasuryPK treasuryPK = new TsTreasuryPK();
			treasuryPK.setSorgcode(loginfo.getSorgcode());
			treasuryPK.setStrecode(searchdto.getTrecode());
			TsTreasuryDto tsTreasuryDto;

			tsTreasuryDto = (TsTreasuryDto) commonDataAccessService
					.find(treasuryPK);

			String datename = "20110101";
			// 国家金库上海市闵行支库（代理）
			String copyFilename = tsTreasuryDto.getStrename().replaceAll(
					"（代理）", "")
					+ "("
					+ (MsgConstant.RULE_SIGN_ALL.equals(searchdto
							.getSbelongflag()) ? "全辖" : "本级")
					+ ")预算"
					+ ("TR_TAXORG_PAYOUT_REPORT".equals(bizkind) ? "支出"
							: "收入") + "分科目序时统计分析表"
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
					+ ("TR_TAXORG_PAYOUT_REPORT".equals(bizkind) ? "支出"
							: "收入") + "分科目序时统计分析表");
			ReportExcel.setFilepath(filerootpath);
			ReportExcel.setUnit("");
			HashMap<String, Object> datamap = new HashMap<String, Object>();

			// 处理报表类型
			String bbmc = "";
			if (StateConstant.REPORT_DAY.equals(reporttype)) {
				datamap.put("reporttype", "("
						+ com.cfcc.deptone.common.util.DateUtil
								.date2String(searchdto.getStartdate()) + "<日报>)");
			} else if (StateConstant.REPORT_TEN.equals(reporttype)) {
				if(StateConstant.TENRPT_TOP.equals(searchdto.getReportkind())){
					bbmc = "上旬";
				}else if(StateConstant.TENRPT_MIDDLE.equals(searchdto.getReportkind())){
					bbmc = "中旬";
				}else if(StateConstant.TENRPT_BOTTOM.equals(searchdto.getReportkind())){
					bbmc = "下旬";
				}
				datamap.put("reporttype", "("
						+ com.cfcc.deptone.common.util.DateUtil.date2String(
								searchdto.getStartdate()).substring(0, 8)
						+ bbmc + "<旬报>)");
			} else if (StateConstant.REPORT_MONTH.equals(reporttype)) {
				datamap.put("reporttype", "("
						+ com.cfcc.deptone.common.util.DateUtil.date2String(
								searchdto.getStartdate()).substring(0, 8)
						+ "--"
						+ com.cfcc.deptone.common.util.DateUtil.date2String(
								searchdto.getEnddate()).substring(0, 8)
						+ "<月报>)");
			} else if (StateConstant.REPORT_QUAR.equals(reporttype)) {
				if(StateConstant.QUARTERRPT_FIRST.equals(searchdto.getReportkind())){
					bbmc = "第一季";
				}else if(StateConstant.QUARTERRPT_SECOND.equals(searchdto.getReportkind())){
					bbmc = "第二季";
				}else if(StateConstant.QUARTERRPT_THREE.equals(searchdto.getReportkind())){
					bbmc = "第三季";
				}else if(StateConstant.QUARTERRPT_FOUR.equals(searchdto.getReportkind())){
					bbmc = "第四季";
				}
				datamap.put("reporttype", "("
						+ com.cfcc.deptone.common.util.DateUtil.date2String(
								searchdto.getStartdate()).substring(0, 5)
						+ bbmc + "----" + com.cfcc.deptone.common.util.DateUtil.date2String(
								searchdto.getEnddate()).substring(0, 5) + bbmc + "<季报>)");
			} else if (StateConstant.REPORT_HALFYEAR.equals(reporttype)) {
				if(StateConstant.HALFYEARRPT_TOP.equals(searchdto.getReportkind())){
					bbmc = "上半年";
				}else if(StateConstant.HALFYEARRPT_BOTTOM.equals(searchdto.getReportkind())){
					bbmc = "下半年";
				}
				datamap.put("reporttype", "("
						+ com.cfcc.deptone.common.util.DateUtil.date2String(
								searchdto.getStartdate()).substring(0, 5) + bbmc + "----" +  com.cfcc.deptone.common.util.DateUtil.date2String(
										searchdto.getEnddate()).substring(0, 5)	+ bbmc +  "<半年报>)");
			} else {
				datamap.put("reporttype", "("
						+ com.cfcc.deptone.common.util.DateUtil.date2String(
								searchdto.getStartdate()).substring(0, 5)
						+ "--"
						+ com.cfcc.deptone.common.util.DateUtil.date2String(
								searchdto.getEnddate()).substring(0, 5)
						+ "<年报>)");
			}

			datamap.put("subcodes", subgetcodes);
			datamap.put("infoIds", groupdatedata);
			datamap.put("dates", dates);
			datamap.put("dto", searchdto);
			ReportExcel.setDatamap(datamap);
			// 编制年月日
			String date = TimeFacade.getCurrentStringTime();
			date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
					+ date.substring(6, 8) + "日";
			ReportExcel.setDate(date);
			// 报表模板名称
			ReportExcel.setFilename("model.xls");
			ReportExcel.setInputstream(ShbudgetsubcodesearchBean.class
					.getResourceAsStream("model.xls"));
			// 报表sheet名称
			ReportExcel.setSheetname("sheet1");
			ReportExcel.bzunit = tsTreasuryDto.getStrename().replaceAll("（代理）",
					"");
			ReportExcel.bzuser = loginfo.getSuserName();
			return ReportExcel.genShbudgetsubcodesearch(searchdto);
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

	public List getTrelist() {
		return trelist;
	}

	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}

	public String getReporttype() {
		return reporttype;
	}

	public void setReporttype(String reporttype) {
		this.reporttype = reporttype;
		if (StateConstant.REPORT_DAY.equals(reporttype)) {
			reportkind = new ArrayList();
			searchdto.setReportkind("");
			searchdto.setEnddate(null);
		} else if (StateConstant.REPORT_TEN.equals(reporttype)) {
			reportkind = reportkindmap.get(reporttype);
			searchdto.setReportkind(StateConstant.TENRPT_TOP);
			searchdto.setEnddate(null);
		} else if (StateConstant.REPORT_QUAR.equals(reporttype)) {
			reportkind = reportkindmap.get(reporttype);
			searchdto.setReportkind(StateConstant.QUARTERRPT_FIRST);
			if (null == searchdto.getEnddate()) {
				searchdto.setEnddate(new Date(System.currentTimeMillis()));
			}
		} else if (StateConstant.REPORT_HALFYEAR.equals(reporttype)) {
			reportkind = reportkindmap.get(reporttype);
			searchdto.setReportkind(StateConstant.HALFYEARRPT_TOP);
			if (null == searchdto.getEnddate()) {
				searchdto.setEnddate(new Date(System.currentTimeMillis()));
			}
		} else if (StateConstant.REPORT_YEAR.equals(reporttype)
				|| StateConstant.REPORT_MONTH.equals(reporttype)) {
			reportkind = new ArrayList();
			searchdto.setReportkind("");
			if (null == searchdto.getEnddate()) {
				searchdto.setEnddate(new Date(System.currentTimeMillis()));
			}
		}
		this.editor.fireModelChanged();
	}

	public String getSubcode() {
		return subcode;
	}

	public void setSubcode(String subcode) {
		this.subcode = subcode;
	}

	public List getReportkind() {
		return reportkind;
	}

	public void setReportkind(List reportkind) {
		this.reportkind = reportkind;
	}

	public List getReporttypelist() {
		return reporttypelist;
	}

	public void setReporttypelist(List reporttypelist) {
		this.reporttypelist = reporttypelist;
	}

	public ShanghaiReport getSearchdto() {
		return searchdto;
	}

	public void setSearchdto(ShanghaiReport searchdto) {
		this.searchdto = searchdto;
	}

	public String getBizkind() {
		return bizkind;
	}

	public void setBizkind(String bizkind) {
		this.bizkind = bizkind;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

}
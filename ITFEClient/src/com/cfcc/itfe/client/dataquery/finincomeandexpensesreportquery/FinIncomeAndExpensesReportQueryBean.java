/**
 * 财政预算支出统计报表
 */
package com.cfcc.itfe.client.dataquery.finincomeandexpensesreportquery;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.ReportExcel;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:财政预算支出统计报表
 * 
 * @author db2admin
 * @time 10-06-12 16:13:44 子系统: DataQuery 模块:finIncomeAndExpensesReportQuery
 *       组件:FinIncomeAndExpensesReportQuery
 */
@SuppressWarnings("unchecked")
public class FinIncomeAndExpensesReportQueryBean extends AbstractFinIncomeAndExpensesReportQueryBean implements
		IPageDataProvider {

	private static Log log = LogFactory
			.getLog(FinIncomeAndExpensesReportQueryBean.class);

	private PagingContext pagingcontext = new PagingContext(this);

	ITFELoginInfo loginfo;
	// 国库代码列表
	private List<TsTreasuryDto> treList;
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
	//金额单位
	private String sleMoneyUnit;
	//金额单位名称
	private String moneyUnitName;
	// 报表预算级次标志
	private String sbudgetlevelname = null;
	// 报表类型标志
	private String slebilltypename = null;
	// 报表预算种类标志
	private String sbudgettypename = null;
	//科目类型
	private String sleSubjectType;
	//科目属性
	private String sleSubjectAttribute=null;
	// 查询用list
	private List list = null;
	//报表标题
	private String reporttitle = null;
	private String filenamecmt=null;
	//调拨标志
	private String smoveflag=null;
	//调拨标志名称
	private String smoveflagname=null;
	//旬报类型
	private String sleTenrptType;
	//季报类型
	private String sleQuarterrptType;
	//半年报类型
	private String sleHalfyearrptType;
	//旬报类型名称
	private String sleTenrptTypename;
	//季报类型名称
	private String sleQuarterrptTypename;
	//半年报类型名称
	private String sleHalfyearrptTypename;
	
	private Boolean flag=true; 
	// 报表查询用poList
	private List poList = null;
	
	TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();

	public FinIncomeAndExpensesReportQueryBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();

		init();
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
				break;
			}
		}

		// 报表类型
		sleBillType = StateConstant.REPORT_DAY;
		// 报表类型名称
		slebilltypename = "日报";
		//金额单位
		sleMoneyUnit = StateConstant.MONEY_UNIT_3;
		//金额单位名称
		moneyUnitName = "单位 ：亿元";
		// 预算种类
		sleBudKind = MsgConstant.BDG_KIND_IN;
		// 预算种类名称
		sbudgettypename = "预算内";
		// 预算级次
		sleBudLevel = MsgConstant.BUDGET_LEVEL_CENTER;
		// 预算级次名称
		sbudgetlevelname = "中央级";
		// 查询日期
		rptdate = TimeFacade.getCurrentStringTime();
		//科目属性
		sleSubjectAttribute=StateConstant.SUBJECTATTR_CLASS;
	}

	/**
	 * Direction: 查询 ename: query 引用方法: viewers: 报表显示界面 messages:
	 */
	public String query(Object o) {

		// 查询条件校验
		if (!querycheck()) {
			return null;
		}

		// 国库主体代码
		payoutdto.setStrecode(sleTreCode);
		// 预算种类
		payoutdto.setSbudgettype(sleBudKind);
		// 日期
		payoutdto.setSrptdate(rptdate);
		// 预算级次代码
		payoutdto.setSbudgetlevelcode(sleBudLevel);
		
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " 查询无记录！");
			return "";
		}
		return super.query(o);
	}


	/**
	 * 财政预算支出查询 return list
	 * @throws ITFEBizException 
	 */
	public List budgetPayoutQueryList() throws ITFEBizException {

		try {
			// sleBillType--报表种类	sleTenrptType--日报表类型sleQuarterrptType--季报表类型sleHalfyearrptType--半年报类型 sleSubjectType--科目类型 sleSubjectAttribute--科目属性  smoveflag--调拨标志   sleMoneyUnit--金额单位
			list = payAndIncomeBillService.makePayoutBill(payoutdto, sleBillType,sleTenrptType,sleQuarterrptType,sleHalfyearrptType, sleSubjectType, sleSubjectAttribute, smoveflag, sleMoneyUnit); 
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
		if (null == rptdate || "".equals(rptdate)) {
			MessageDialog.openMessageDialog(null, "请输入查询日期");
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
		if (null == sleBudLevel || "".equals(sleBudLevel)) {
			MessageDialog.openMessageDialog(null, "请选择预算级次");
			return false;
		}
		return true;
	}

	
    
    
    /**
	 * Direction: 导出Excel文件
	 * ename: exportExcelFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
	public String exportExcelFile(Object o) {
		
		
		// 报表标题
		reporttitle = sleTrename + sbudgetlevelname + "预算支出"+ slebilltypename + "表";
		
		// 辖属标志 sleOfFlagName 预算种类 sbudgettypename 
		filenamecmt="_"+"全辖"+"_"+sbudgettypename;

		
		String dirsep = File.separator; // 取得系统分割符
		// 报表模块文件路径
    	String filerootpath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
    	String copyFilename = this.sleTreCode+"_"+rptdate+"_"+reporttitle+filenamecmt+"_"+(new SimpleDateFormat("ddHHmmsss").format(System.currentTimeMillis())+"R.xls");
		
		String datename="";
		ReportExcel.init();
		
		ReportExcel.setFilepath(filerootpath);
	
		ReportExcel.setNewfilepath(filerootpath );
		// 新建报表名称
		ReportExcel.setCopyFilename(copyFilename);
		
		/**
		 * 具体报表信息展示
		 */
		// 财政预算收入日报表查询
		if (StateConstant.REPORT_DAY.equals(sleBillType)) {
			datename="【"+rptdate.substring(0, 4) + "年"+rptdate.substring(4, 6) + "月"+ rptdate.substring(6, 8) + "日<日报>】";
		}
		// 财政预算收入旬报表查询
		else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
			reporttitle+="("+sleTenrptTypename+")";
			datename="【"+rptdate.substring(0, 4) + "年"+rptdate.substring(4, 6) + "月"+ "<"+sleTenrptTypename+"报>】";
		}
		// 财政预算收入月报表查询
		else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
			datename="【"+rptdate.substring(0, 4) + "年"+rptdate.substring(4, 6) + "月"+ "<月报>】";
		}
		// 财政预算收入季报表查询
		else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
			reporttitle+="("+sleQuarterrptTypename+")";
			datename="【"+rptdate.substring(0, 4) + "年"+ "<"+sleQuarterrptTypename+"报>】";
		}
		// 财政预算收入半年报表查询
		else if (StateConstant.REPORT_HALFYEAR.equals(sleBillType)) {
			reporttitle+="("+sleHalfyearrptTypename+")";
			datename="【"+rptdate.substring(0, 4) + "年"+ "<"+sleHalfyearrptTypename+"报>】";
		}
		// 财政预算收入年报表查询
		else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
			datename="【"+rptdate.substring(0, 4) + "年"+ "<年报>】";
		}
		
		ReportExcel.setReporttitle(sleTrename + "(全辖)"+ "预算支出"+ slebilltypename + "表"+datename);
		// 编制年月日
		String date = TimeFacade.getCurrentStringTime();
		date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"	+ date.substring(6, 8) + "日";
		ReportExcel.setDate(date);

		// 报表模板名称
		ReportExcel.setFilename("Income1.xls");
		ReportExcel.setInputstream(FinIncomeAndExpensesReportQueryBean.class
				.getResourceAsStream("Income1.xls"));
		// 报表sheet名称
		ReportExcel.setSheetname(datename);

		try {
			poList = budgetPayoutQueryList();
			if (null == poList || poList.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询无记录");
				return null;
			}
			//与收入公用一个getreportbyIncomerpt()方法
			ReportExcel.getreportbyIncomerpt(poList,sleTrename, smoveflagname,sbudgetlevelname,moneyUnitName,loginfo.getSuserName());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}
		
		MessageDialog.openMessageDialog(null, "导出文件成功！已保存到：" + filerootpath+copyFilename);
		return super.exportExcelFile(o);
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
	public PageResponse retrieve(PageRequest request) {
		try {
			// sleBillType--报表种类	sleTenrptType--日报表类型sleQuarterrptType--季报表类型sleHalfyearrptType--半年报类型 sleSubjectType--科目类型 sleSubjectAttribute--科目属性  smoveflag--调拨标志   sleMoneyUnit--金额单位
			return payAndIncomeBillService.makePayoutReport(payoutdto, sleBillType,sleTenrptType,sleQuarterrptType,sleHalfyearrptType, sleSubjectType, sleSubjectAttribute, smoveflag, sleMoneyUnit, request);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
		}
		return super.retrieve(request);
	}



	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		FinIncomeAndExpensesReportQueryBean.log = log;
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

	public List<TsTreasuryDto> getTreList() {
		return treList;
	}

	public void setTreList(List<TsTreasuryDto> treList) {
		this.treList = treList;
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
		if(flag) editor.fireModelChanged();
	}


	public String getSleBillType() {
		return sleBillType;
	}

	public void setSleBillType(String sleBillType) {
		if (sleBillType.equals(StateConstant.REPORT_DAY)) {
			setSleTenrptType("");
			setSleQuarterrptType("");
			setSleHalfyearrptType("");
			slebilltypename = "日报";
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {
			setSleTenrptType(StateConstant.TENRPT_TOP);
			setSleQuarterrptType("");
			setSleHalfyearrptType("");
			slebilltypename = "旬报";
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {
			setSleTenrptType("");
			setSleQuarterrptType("");
			setSleHalfyearrptType("");
			slebilltypename = "月报";
		}else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {
			setSleTenrptType("");
			setSleQuarterrptType(StateConstant.QUARTERRPT_FIRST);
			setSleHalfyearrptType("");
			slebilltypename = "季报";
		}else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {
			setSleTenrptType("");
			setSleQuarterrptType("");
			setSleHalfyearrptType(StateConstant.HALFYEARRPT_TOP);
			slebilltypename = "半年报";
		}else if (sleBillType.equals(StateConstant.REPORT_YEAR)) {
			setSleTenrptType("");
			setSleQuarterrptType("");
			setSleHalfyearrptType("");
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
		}else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_LOCAL)) {
			sbudgetlevelname = "地方级";
		}else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_ANY)) {
			sbudgetlevelname = "不分级次";
		}
		this.sleBudLevel = sleBudLevel;
		this.editor.fireModelChanged();
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

	public List getPoList() {
		return poList;
	}

	public void setPoList(List poList) {
		this.poList = poList;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}


	public String getSleSubjectType() {
		return sleSubjectType;
	}


	public void setSleSubjectType(String sleSubjectType) {
		this.sleSubjectType = sleSubjectType;
	}


	public String getSleSubjectAttribute() {
		return sleSubjectAttribute;
	}


	public void setSleSubjectAttribute(String sleSubjectAttribute) {
		this.sleSubjectAttribute = sleSubjectAttribute;
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

	public String getSleTenrptType() {
		return sleTenrptType;
	}

	public void setSleTenrptType(String sleTenrptType) {
		if (sleTenrptType.equals(StateConstant.TENRPT_TOP)) {
			sleTenrptTypename = "上旬";
		} else if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)) {
			sleTenrptTypename = "中旬";
		} else if (sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {
			sleTenrptTypename = "下旬";
		}
		this.sleTenrptType = sleTenrptType;
		this.editor.fireModelChanged();
	}

	public String getSleQuarterrptType() {
		return sleQuarterrptType;
	}

	public void setSleQuarterrptType(String sleQuarterrptType) {
		if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FIRST)) {
			sleQuarterrptTypename = "第一季度";
		} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)) {
			sleQuarterrptTypename = "第二季度";
		} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)) {
			sleQuarterrptTypename = "第三季度";
		}else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {
			sleQuarterrptTypename = "第四季度";
		}
		this.sleQuarterrptType = sleQuarterrptType;
		this.editor.fireModelChanged();
	}

	public String getSleHalfyearrptType() {
		return sleHalfyearrptType;
	}

	public void setSleHalfyearrptType(String sleHalfyearrptType) {
		if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_TOP)) {
			sleHalfyearrptTypename = "上半年";
		} else if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {
			sleHalfyearrptTypename = "下半年";
		}
		this.sleHalfyearrptType = sleHalfyearrptType;
		this.editor.fireModelChanged();
	}


	public String getSmoveflagname() {
		return smoveflagname;
	}


	public void setSmoveflagname(String smoveflagname) {
		this.smoveflagname = smoveflagname;
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
	
	
}
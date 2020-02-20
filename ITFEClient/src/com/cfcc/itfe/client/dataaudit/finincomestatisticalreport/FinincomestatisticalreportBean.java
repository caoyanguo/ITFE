package com.cfcc.itfe.client.dataaudit.finincomestatisticalreport;

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
import com.cfcc.itfe.client.dataaudit.finpaystatisticalreport.FinpaystatisticalreportBean;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsBatchcrtreportmsgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.ArithUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ReportExcel;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
/**
 * codecomment: 
 * @author Administrator
 * @time   15-05-18 15:46:17
 * 子系统: DataAudit
 * 模块:finincomestatisticalreport
 * 组件:Finincomestatisticalreport
 */
public class FinincomestatisticalreportBean extends AbstractFinincomestatisticalreportBean implements IPageDataProvider {

	
	
	
    private static Log log = LogFactory.getLog(FinincomestatisticalreportBean.class);
    
    ITFELoginInfo loginfo;
	// 国库代码列表
	private List<TsTreasuryDto> treList;
	// 征收机关代码列表
	private List<TsTaxorgDto> taxorgList;
	// 征收机关代码
	private String sleTaxOrgCode;
	// 征收机关名称
	private String sletaxorgname;
	// 国库代码
	private String sleTreCode;
	// 国库名称
	private String sleTrename;

	// 报表栏式类型（二栏式、三栏式、四栏式）
	private String sleBillKind;
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
	// 查询月份
	private String month;
	// 科目代码
	private String sbudgetsubcode;
	//科目类型
	private String sleSubjectType;
	
	//含项合计list
	private List<TdEnumvalueDto> sumitemlist;
	
	private String sleSumItem=null;

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
	
	private Boolean flag=true; 

	// 查询用list
	private List list = null;
	
	//
	private String reporttitle = null;
	
	//
	private String filenamecmt=null;
	
	//
	private String ipath=null;

	// 收入日报表二栏式显示
	private List incomeList = null;
	private Map incomeMap = new HashMap();
	private String incomePath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportTwo.jasper";

	// 收入日报表三栏式显示
	private List incomeThreeList = null;
	private Map incomeThreeMap = new HashMap();
	private String incomeThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportThree.jasper";

	// 收入日报表四栏式显示
	private List incomeFourList = null;
	private Map incomeFourMap = new HashMap();
	private String incomeFourPath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportFour.jasper";

	// 收入旬报表二栏式显示
	private List incomeTenRptList = null;
	private Map incomeTenRptMap = new HashMap();
	private String incomeTenRptPath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportTwo.jasper";

	// 收入旬报表三栏式显示
	private List incomeTenRptThreeList = null;
	private Map incomeTenRptThreeMap = new HashMap();
	private String incomeTenRptThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportThree.jasper";

	// 收入旬报表四栏式显示
	private List incomeTenRptFourList = null;
	private Map incomeTenRptFourMap = new HashMap();
	private String incomeTenRptFourPath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportFour.jasper";

	// 收入月报表二栏式显示
	private List incomeMonthList = null;
	private Map incomeMonthMap = new HashMap();
	private String incomeMonthPath = "com/cfcc/itfe/client/ireport/FinBudgetRecMonthReportTwo.jasper";

	// 收入月报表三栏式显示
	private List incomeMonthThreeList = null;
	private Map incomeMonthThreeMap = new HashMap();
	private String incomeMonthThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecMonthReportThree.jasper";

	// 收入季报表二栏式显示
	private List incomeQuarList = null;
	private Map incomeQuarMap = new HashMap();
	private String incomeQuarPath = "com/cfcc/itfe/client/ireport/FinBudgetRecQuarReportTwo.jasper";

	// 收入季报表三栏式显示
	private List incomeQuarThreeList = null;
	private Map incomeQuarThreeMap = new HashMap();
	private String incomeQuarThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecQuarReportThree.jasper";

	// 收入年报表二栏式显示
	private List incomeYearList = null;
	private Map incomeYearMap = new HashMap();
	private String incomeYearPath = "com/cfcc/itfe/client/ireport/FinBudgetRecYearReportTwo.jasper";
	
	// 报表栏式显示共通
	private List iList = null;
	private Map iMap = new HashMap();
	
    public FinincomestatisticalreportBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
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
			// mod -20110906 加市级查全辖条件
//			if (loginfo.getSorgcode().equals(centerorg) || loginfo.getSorgcode().equals("060300000003")) {
//				treList = commonDataAccessService.findRsByDto(tredto);
//			}
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// 非中心机构，取得登录机构对应国库
			else {
				tredto = new TsTreasuryDto();
//				tredto.setSbookorgcode(loginfo.getSorgcode());
				String trecodesql=this.getTrecodesql();
				treList = commonDataAccessService.findRsByDtoWithWhere(tredto, trecodesql);
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
		sdate = TimeFacade.getCurrentStringTime();
		//含项合计
		initstrastatelist();
		sleSumItem=this.getSumitemlist().get(0).getSvalue();

	}
	
	public String getTrecodesql(){
		//获得用户所挂接的国库主体，并作为查询条件
		List trecodes=loginfo.getTreasuryCodes();
		StringBuffer sbf=new StringBuffer();
		String trecodesql="";
		if(null!=trecodes&&trecodes.size()>0){
			for(int i=0;i<trecodes.size();i++){
				sbf.append("'").append(trecodes.get(i)).append("',");
			}
		    trecodesql=sbf.toString().substring(0, sbf.toString().lastIndexOf(","));
			trecodesql="  WHERE  S_TRECODE IN ("+trecodesql+") ";
		}
		return trecodesql;
	}

	/**
	 * 根据国库代码查询征收机关代码 国库与征收机关对应关系表
	 */
	private List querytaxoeglist(String sleTreCode) {
		// 查询国库对应征收机关代码用dto
		TsTaxorgDto taxorgdto = new TsTaxorgDto();
		List<TsTaxorgDto> taxorglist=new ArrayList<TsTaxorgDto>();
		try {
			TsTaxorgDto orgdto0 = new TsTaxorgDto();
			orgdto0.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto0.setStrecode(sleTreCode);// 国库代码
			orgdto0.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			orgdto0.setStaxorgname("不分征收机关");
			orgdto0.setStaxprop(MsgConstant.MSG_TAXORG_SHARE_PROP);
			taxorglist.add(orgdto0);
			
			TsTaxorgDto orgdto1 = new TsTaxorgDto();
			orgdto1.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto1.setStrecode(sleTreCode);// 国库代码
			orgdto1.setStaxorgcode(MsgConstant.MSG_TAXORG_NATION_CLASS);
			orgdto1.setStaxorgname("国税");
			orgdto1.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
			taxorglist.add(orgdto1);

			TsTaxorgDto orgdto2 = new TsTaxorgDto();
			orgdto2.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto2.setStrecode(sleTreCode);// 国库代码
			orgdto2.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			orgdto2.setStaxorgname("地税");
			orgdto2.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
			taxorglist.add(orgdto2);

			TsTaxorgDto orgdto3 = new TsTaxorgDto();
			orgdto3.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto3.setStrecode(sleTreCode);// 国库代码
			orgdto3.setStaxorgcode(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			orgdto3.setStaxorgname("海关");
			orgdto3.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
			taxorglist.add(orgdto3);

			TsTaxorgDto orgdto4 = new TsTaxorgDto();
			orgdto4.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto4.setStrecode(sleTreCode);// 国库代码
			orgdto4.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			orgdto4.setStaxorgname("财政");
			orgdto4.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
			taxorglist.add(orgdto4);

			TsTaxorgDto orgdto5 = new TsTaxorgDto();
			orgdto5.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto5.setStrecode(sleTreCode);// 国库代码
			orgdto5.setStaxorgcode(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			orgdto5.setStaxorgname("其他");
			orgdto5.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
			taxorglist.add(orgdto5);
			
			taxorgdto.setStrecode(sleTreCode);// 国库代码
			taxorglist.addAll(commonDataAccessService.findRsByDto(taxorgdto));
			setTaxorgList(taxorglist);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		// 初始化征收机关代码默认值
		if (taxorgList.size() > 0) {
			taxorgdto.setStrecode(sleTreCode);// 国库代码
			setSleTaxOrgCode(taxorgList.get(0).getStaxorgcode());
		}
		return taxorgList;
	}
	
	/**
	 * 设置批量打印参数
	 * @param idto
	 */
	public void setBathPrar(TsBatchcrtreportmsgDto idto){
		
		this.setFlag(false);
		this.setSleTreCode(idto.getSletrecode());
		this.setSleTaxOrgCode(idto.getSletaxorgcode());
		this.setSdate(idto.getRptdate());
		this.setSleBillKind(idto.getSlebillkind());
		this.setSleBillType(idto.getSlebilltype());
		this.setSleBudKind(idto.getSlebudkind());
		this.setSleBudLevel(idto.getSlebudlevel());
		this.setSleTrimFlag(idto.getSletrimflag());
		this.setSleOfFlag(idto.getSleofflag());
		
		this.setSleSumItem(idto.getSlesumitem());//含项合计
		this.setSleSubjectType(idto.getSlesubjecttype());//科目类型
	
	}
	
	/**
	 * Direction: 查询 ename: query 引用方法: viewers: * messages:
	 */
	public String query(Object o) {

		/**
		 * 查询条件校验
		 */
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
			TrIncomedayrptDto incomedto = new TrIncomedayrptDto();
			// 国库主体代码
			incomedto.setStrecode(sleTreCode);
			// 征收机关代码
			incomedto.setStaxorgcode(sleTaxOrgCode);
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
			
			iList = budgetIncomeQueryList(incomedto);
			if (null == iList || iList.size() == 0) {
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
		iMap.put("makedate", sdate.substring(0, 4) + "年"+ sdate.substring(4, 6) + "月"+ sdate.substring(6, 8) + "日");
		// 辖属标志
		iMap.put("offlag", "【" + offlag + "】");
		// 预算种类
		iMap.put("sbudgettype", "【" + sbudgettypename + "】");
		// 制表人
		iMap.put("username", loginfo.getSuserName());
		// 征收机关名称
		iMap.put("taxorgname", sletaxorgname);
		// 报表标题
		reporttitle = sleTrename + sbudgetlevelname + "级" + "预算收入"+ slebilltypename + "表";
		iMap.put("billtitle", reporttitle);
		iMap.put("printtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
		
		filenamecmt="_"+offlag+"_"+sbudgettypename+"_"+sletaxorgname;

		/**
		 * 具体报表信息展示
		 */
		// 财政预算收入日报表查询
		if (StateConstant.REPORT_DAY.equals(sleBillType)) {
			// 日报表查询
			dayReportQuery();
		}
		// 财政预算收入旬报表查询
		else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
			// 旬报表查询
			tenReportQuery();
		}
		// 财政预算收入月报表查询
		else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
			// 月报表查询
			monthReportQuery();
		}
		// 财政预算收入季报表查询
		else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
			// 季报表查询
			quarReportQuery();
		}
		// 财政预算收入年报表查询
		else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
			// 年报表查询
			yearReportQuery();
		}
		
		return super.query(o);
	}

	/**
	 * 财政预算收入日报表查询
	 */
	public void dayReportQuery() {
		
		// 财政预算收入日报表二栏式
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
			
			filenamecmt+="_二栏式";
			ipath=incomePath;
			
			this.setIncomeList(iList);
			this.setIncomeMap(iMap);
			
			if(flag) editor.openComposite("财政预算收入日报表二栏式", true);
			
		}
		// 财政预算收入日报表三栏式
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
			
			filenamecmt+="_三栏式";
			ipath=incomeThreePath;
			
			this.setIncomeThreeList(iList);
			this.setIncomeThreeMap(iMap);
			if(flag) editor.openComposite("财政预算收入日报表三栏式", true);
		}
		// 财政预算收入日报表四栏式
		else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {
			
			filenamecmt+="_四栏式";
			ipath=incomeFourPath;
			
			this.setIncomeFourList(iList);
			this.setIncomeFourMap(iMap);
			if(flag) editor.openComposite("财政预算收入日报表四栏式", true);
		}
	}

	/**
	 * 财政预算收入旬报表查询
	 */
	public void tenReportQuery() {
		// 财政预算收入旬报表二栏式
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
			
			filenamecmt+="_二栏式";
			ipath=incomeTenRptPath;
			
			this.setIncomeTenRptList(iList);
			this.setIncomeTenRptMap(iMap);
			if(flag) editor.openComposite("财政预算收入旬报表二栏式", true);
		}
		// 财政预算收入旬报表三栏式
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
			
			filenamecmt+="_三栏式";
			ipath=incomeTenRptThreePath;
			
			this.setIncomeTenRptThreeList(iList);
			this.setIncomeTenRptThreeMap(iMap);
			if(flag) editor.openComposite("财政预算收入旬报表三栏式", true);
		}
		// 财政预算收入旬报表四栏式
		else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {
			
			filenamecmt+="_四栏式";
			ipath=incomeTenRptFourPath;
			
			this.setIncomeTenRptFourList(iList);
			this.setIncomeTenRptFourMap(iMap);
			if(flag) editor.openComposite("财政预算收入旬报表四栏式", true);
		}
	}

	/**
	 * 财政预算收入月报表查询
	 */
	public void monthReportQuery() {
		
		// 财政预算收入月报表二栏式
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
			
			filenamecmt+="_二栏式";
			ipath=incomeMonthPath;
			
			this.setIncomeMonthList(iList);
			this.setIncomeMonthMap(iMap);
			if(flag) editor.openComposite("财政预算收入月报表二栏式", true);
		}
		// 财政预算收入月报表三栏式
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
			
			filenamecmt+="_三栏式";
			ipath=incomeMonthThreePath;
			
			this.setIncomeMonthThreeList(iList);
			this.setIncomeMonthThreeMap(iMap);
			if(flag) editor.openComposite("财政预算收入月报表三栏式", true);
		} else {
			MessageDialog.openMessageDialog(null, "财政预算收入月报表只有二、三栏式");
			return;
		}
	}

	/**
	 * 财政预算收入季报表查询
	 */
	public void quarReportQuery() {
		
		// 财政预算收入季报表二栏式
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
			
			filenamecmt+="_二栏式";
			ipath=incomeQuarPath;
			
			this.setIncomeQuarList(iList);
			this.setIncomeQuarMap(iMap);
			if(flag) editor.openComposite("财政预算收入季报表二栏式", true);
		}
		// 财政预算收入季报表三栏式
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
			
			filenamecmt+="_三栏式";
			ipath=incomeQuarThreePath;
			
			this.setIncomeQuarThreeList(iList);
			this.setIncomeQuarThreeMap(iMap);
			if(flag) editor.openComposite("财政预算收入季报表三栏式", true);
		} else {
			MessageDialog.openMessageDialog(null, "财政预算收入季报表只有二、三栏式");
			return;
		}
	}

	/**
	 * 财政预算收入年报表查询
	 */
	public void yearReportQuery() {
		
		// 财政预算收入年报表二栏式
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
			
			filenamecmt+="_二栏式";
			ipath=incomeYearPath;
			
			this.setIncomeYearList(iList);
			this.setIncomeYearMap(iMap);
			if(flag) editor.openComposite("财政预算收入年报表二栏式", true);
		} else {
			MessageDialog.openMessageDialog(null, "财政预算收入年报表只有二栏式");
			return;
		}
	}

	/**
	 * 财政预算收入查询 return list
	 * @throws ITFEBizException 
	 */
	public List budgetIncomeQueryList(TrIncomedayrptDto incomedto) throws ITFEBizException {

		
		try {
		
			//如果是本级查询征收机关大类，先查询全辖数，再查询全辖非本级数，两者进行轧差
			if(MsgConstant.RULE_SIGN_SELF.equals(incomedto.getSbelongflag().trim())){
				if(incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_NATION_CLASS) ||
						incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_PLACE_CLASS) ||
						incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS) ||
						incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS) ||
						incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_OTHER_CLASS) ||
						incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS) ){//征收机关大类
					list = makemsglist(incomedto,sleSumItem, sleSubjectType, sdate); //--第2项传入是否含项合计 第3项传入科目类型
				}
				else{
					list = finpaystatisticalreportService.makeIncomeBill(incomedto,sleSumItem, sleSubjectType, sdate); //--第2项传入是否含项合计 第3项传入科目类型
				}
			}
			else{
				list = finpaystatisticalreportService.makeIncomeBill(incomedto,sleSumItem, sleSubjectType, sdate); //--第2项传入是否含项合计 第3项传入科目类型
			}
		
		} catch (ITFEBizException e) {
			log.error(e);
			throw e;
		}
		return list;
	}
	
	private List makemsglist(TrIncomedayrptDto idto, String sleSumItem, String sleSubjectType,
			String rptDate) throws ITFEBizException{
		
		idto.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
		//全辖查询
		List<TrIncomedayrptDto> signalllist = finpaystatisticalreportService.makeIncomeBill(idto,sleSumItem, sleSubjectType, rptDate); //--第2项传入是否含项合计 第3项传入科目类型
		
		idto.setSbelongflag(MsgConstant.RULE_SIGN_ALLNOTSELF);
		//全辖非本级查询
		List<TrIncomedayrptDto> signallnotselflist = finpaystatisticalreportService.makeIncomeBill(idto,sleSumItem, sleSubjectType, rptDate); //--第2项传入是否含项合计 第3项传入科目类型
		
		if((null == signalllist || signalllist.size() == 0) || (null == signallnotselflist || signallnotselflist.size() == 0)){
//			MessageDialog.openMessageDialog(null, "没有查询到对应的报表数据！");
			return null;
		}
		
		
		
		
		List<TrIncomedayrptDto> newlist = new ArrayList<TrIncomedayrptDto>();
		//以S_BUDGETSUBCODE,S_BUDGETSUBNAME比较
		for(TrIncomedayrptDto alldto : signalllist){
			TrIncomedayrptDto newdto = new TrIncomedayrptDto();
			boolean flag = false;
			for(TrIncomedayrptDto ndto : signallnotselflist){
				if(alldto.getSbudgetsubcode()==null || alldto.getSbudgetsubcode().trim().equals("")){
					if(alldto.getSbudgetsubname().trim().equals(ndto.getSbudgetsubname().trim())){
						//N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR 金额比较
						newdto.setSbudgetsubcode("");
						newdto.setSbudgetsubname(alldto.getSbudgetsubname());
						newdto.setNmoneyday(ArithUtil.sub(alldto.getNmoneyday(), ndto.getNmoneyday(), 2));
						newdto.setNmoneytenday(ArithUtil.sub(alldto.getNmoneytenday(), ndto.getNmoneytenday(), 2));
						newdto.setNmoneymonth(ArithUtil.sub(alldto.getNmoneymonth(), ndto.getNmoneymonth(), 2));
						newdto.setNmoneyquarter(ArithUtil.sub(alldto.getNmoneyquarter(), ndto.getNmoneyquarter(), 2));
						newdto.setNmoneyyear(ArithUtil.sub(alldto.getNmoneyyear(), ndto.getNmoneyyear(), 2));
						newlist.add(newdto);
						flag=true;
						break;
					}
				}
				else{
					if(alldto.getSbudgetsubcode().trim().equals(ndto.getSbudgetsubcode().trim())){
						//N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR 金额比较
						newdto.setSbudgetsubcode(alldto.getSbudgetsubcode());
						newdto.setSbudgetsubname(alldto.getSbudgetsubname());
						newdto.setNmoneyday(ArithUtil.sub(alldto.getNmoneyday(), ndto.getNmoneyday(), 2));
						newdto.setNmoneytenday(ArithUtil.sub(alldto.getNmoneytenday(), ndto.getNmoneytenday(), 2));
						newdto.setNmoneymonth(ArithUtil.sub(alldto.getNmoneymonth(), ndto.getNmoneymonth(), 2));
						newdto.setNmoneyquarter(ArithUtil.sub(alldto.getNmoneyquarter(), ndto.getNmoneyquarter(), 2));
						newdto.setNmoneyyear(ArithUtil.sub(alldto.getNmoneyyear(), ndto.getNmoneyyear(), 2));
						newlist.add(newdto);
						flag=true;
						break;
					}	
				}
			}
			if(!flag){
				newlist.add(alldto);
			}
		}
		
		return newlist;
	}
	
	private void initstrastatelist() {
		
		TdEnumvalueDto finddto=new TdEnumvalueDto();
		finddto.setStypecode("0405");
		try {
			sumitemlist = commonDataAccessService.findRsByDtoUR(finddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 财政预算收入统计报表查询条件 messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}
	
	/**
	 * Direction: 导出文件
	 * ename: exportFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportFile(Object o) {

    	String dirsep = File.separator; // 取得系统分割符
    	String filepath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
    	String copyFilename = this.sleTreCode+"_"+sdate+"_"+reporttitle+filenamecmt+"_"+(new SimpleDateFormat("ddHHmmsss").format(System.currentTimeMillis())+"R.xls");
    	OutputStream out =null;
		try {
			
			File file = new File(filepath+copyFilename);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			out = new FileOutputStream(file);
			ReportGen.exportReportToXLS(iMap, iList,ipath,out);
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
		
		MessageDialog.openMessageDialog(null, "导出文件成功！已保存到：" + filepath+copyFilename);
		
		return super.exportFile(o);
	}
    
    
    /**
	 * Direction: 导出Excel文件
	 * ename: exportExcelFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
	public String exportExcelFile(Object o) {
		
		String dirsep = File.separator; // 取得系统分割符
		// 报表模块文件路径
    	String filerootpath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
    	String copyFilename = this.sleTreCode+"_"+sdate+"_"+reporttitle+filenamecmt+"_"+(new SimpleDateFormat("ddHHmmsss").format(System.currentTimeMillis())+"R.xls");
		
		String datename=sdate.substring(0, 4) + "年"+sdate.substring(4, 6) + "月"+ sdate.substring(6, 8) + "日";
		ReportExcel.init();
		
		ReportExcel.setFilepath(filerootpath);
	
		ReportExcel.setNewfilepath(filerootpath );
		// 新建报表名称
		ReportExcel.setCopyFilename(copyFilename);
		
		ReportExcel.setReporttitle(sleTrename + sbudgetlevelname + "级" + "预算收入"+ slebilltypename + "表"+"【" + sletaxorgname + "】");
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
			ReportExcel.getreportbyIncomerpt(list,offlag, sbudgettypename,sleBillType,sleBillKind,loginfo.getSuserName());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}
		
		MessageDialog.openMessageDialog(null, "导出文件成功！已保存到：" + filerootpath+copyFilename);
		
		return super.exportExcelFile(o);
	}
    
    
    
	/**
	 * Direction: 导出pdf文件
	 * ename: exportPdfFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportPdfFile(Object o){
        
    	String dirsep = File.separator; // 取得系统分割符
    	String filepath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
    	String copyFilename = this.sleTreCode+"_"+sdate+"_"+reporttitle+filenamecmt+"_"+(new SimpleDateFormat("ddHHmmsss").format(System.currentTimeMillis())+"R.pdf");
//    	String wordFilename=this.sleTreCode+"_"+sdate+"_"+reporttitle+filenamecmt+"_"+(new SimpleDateFormat("ddHHmmsss").format(System.currentTimeMillis())+"R.pdf");
    	
    	OutputStream out =null;
		try {
			
			File file = new File(filepath+copyFilename);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
//			File file1 = new File(filepath+wordFilename);
//			File dir1 = new File(file1.getParent());
//			if (!dir1.exists()) {
//				dir1.mkdirs();
//			}
//			out = new FileOutputStream(file1);
//			ReportGen.exportReportToRTF(iMap, iList,ipath,out);
//			FileUtil.getInstance().writeFile(out);

			out = new FileOutputStream(file);
			ReportGen.exportReportToPDF(iMap, iList,ipath,out);
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
		
		MessageDialog.openMessageDialog(null, "导出文件成功！已保存到：" + filepath+copyFilename);
		
		return super.exportPdfFile(o);
    }

	/**
	 * 查询条件校验
	 */
	public boolean querycheck() {

		if (null == sleTreCode || "".equals(sleTreCode)) {
			MessageDialog.openMessageDialog(null, "请选择国库代码");
			return false;
		} else if (null == sleTaxOrgCode || "".equals(sleTaxOrgCode)) {
			MessageDialog.openMessageDialog(null, "请选择征收机关代码");
			return false;
		} else if (null == sdate || "".equals(sdate)) {
			MessageDialog.openMessageDialog(null, "请输入查询日期");
			return false;
		} else if (null == sleBillKind || "".equals(sleBillKind)) {
			MessageDialog.openMessageDialog(null, "请选择报表栏式类型");
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
				}
			}
			enumList.clear();
			// 报表预算级次标志
			EnumvalueDto.setStypecode("0121");
			enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			for (TdEnumvalueDto valueDto : enumList) {
				if (valueDto.getSvalue().equals(sleBudLevel)) {
					sbudgetlevelname = valueDto.getSvaluecmt();
				}
			}
			enumList.clear();
			// 报表类型标志
			EnumvalueDto.setStypecode("0314");
			enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			for (TdEnumvalueDto valueDto : enumList) {
				if (valueDto.getSvalue().equals(sleBillType)) {
					slebilltypename = valueDto.getSvaluecmt();
				}
			}
			enumList.clear();
			// 报表预算种类标志
			EnumvalueDto.setStypecode("0122");
			enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			for (TdEnumvalueDto valueDto : enumList) {
				if (valueDto.getSvalue().equals(sleBudKind)) {
					sbudgettypename = valueDto.getSvaluecmt();
				}
			}
			enumList.clear();
			
			return true;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return false;
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
				sletaxorgname = tmp.getStaxorgname();
			}
		}
		this.sleTaxOrgCode = sleTaxOrgCode;
	}

	public String getSletaxorgname() {
		return sletaxorgname;
	}

	public void setSletaxorgname(String sletaxorgname) {
		this.sletaxorgname = sletaxorgname;
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

		List list = querytaxoeglist(sleTreCode);
		if (list != null && list.size() > 0) {
			taxorgList = list;
		} else {
			taxorgList.clear();
		}
		if(flag) editor.fireModelChanged();
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
		this.sleOfFlag = sleOfFlag;
		
		if(flag){
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
		this.sleBudLevel = sleBudLevel;
	}

	public String getMoneyUnitName() {
		return moneyUnitName;
	}

	public void setMoneyUnitName(String moneyUnitName) {
		this.moneyUnitName = moneyUnitName;
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

	public String getSleBillKind() {
		return sleBillKind;
	}

	public void setSleBillKind(String sleBillKind) {
		this.sleBillKind = sleBillKind;
	}

	public String getSleBudKind() {
		return sleBudKind;
	}

	public void setSleBudKind(String sleBudKind) {
		this.sleBudKind = sleBudKind;
	}

	public List getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(List incomeList) {
		this.incomeList = incomeList;
	}

	public Map getIncomeMap() {
		return incomeMap;
	}

	public void setIncomeMap(Map incomeMap) {
		this.incomeMap = incomeMap;
	}

	public String getIncomePath() {
		return incomePath;
	}

	public void setIncomePath(String incomePath) {
		this.incomePath = incomePath;
	}

	public List getIncomeThreeList() {
		return incomeThreeList;
	}

	public void setIncomeThreeList(List incomeThreeList) {
		this.incomeThreeList = incomeThreeList;
	}

	public Map getIncomeThreeMap() {
		return incomeThreeMap;
	}

	public void setIncomeThreeMap(Map incomeThreeMap) {
		this.incomeThreeMap = incomeThreeMap;
	}

	public String getIncomeThreePath() {
		return incomeThreePath;
	}

	public void setIncomeThreePath(String incomeThreePath) {
		this.incomeThreePath = incomeThreePath;
	}

	public List getIncomeFourList() {
		return incomeFourList;
	}

	public void setIncomeFourList(List incomeFourList) {
		this.incomeFourList = incomeFourList;
	}

	public Map getIncomeFourMap() {
		return incomeFourMap;
	}

	public void setIncomeFourMap(Map incomeFourMap) {
		this.incomeFourMap = incomeFourMap;
	}

	public String getIncomeFourPath() {
		return incomeFourPath;
	}

	public void setIncomeFourPath(String incomeFourPath) {
		this.incomeFourPath = incomeFourPath;
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

	public List getIncomeTenRptList() {
		return incomeTenRptList;
	}

	public void setIncomeTenRptList(List incomeTenRptList) {
		this.incomeTenRptList = incomeTenRptList;
	}

	public Map getIncomeTenRptMap() {
		return incomeTenRptMap;
	}

	public void setIncomeTenRptMap(Map incomeTenRptMap) {
		this.incomeTenRptMap = incomeTenRptMap;
	}

	public String getIncomeTenRptPath() {
		return incomeTenRptPath;
	}

	public void setIncomeTenRptPath(String incomeTenRptPath) {
		this.incomeTenRptPath = incomeTenRptPath;
	}

	public List getIncomeTenRptThreeList() {
		return incomeTenRptThreeList;
	}

	public void setIncomeTenRptThreeList(List incomeTenRptThreeList) {
		this.incomeTenRptThreeList = incomeTenRptThreeList;
	}

	public Map getIncomeTenRptThreeMap() {
		return incomeTenRptThreeMap;
	}

	public void setIncomeTenRptThreeMap(Map incomeTenRptThreeMap) {
		this.incomeTenRptThreeMap = incomeTenRptThreeMap;
	}

	public String getIncomeTenRptThreePath() {
		return incomeTenRptThreePath;
	}

	public void setIncomeTenRptThreePath(String incomeTenRptThreePath) {
		this.incomeTenRptThreePath = incomeTenRptThreePath;
	}

	public List getIncomeTenRptFourList() {
		return incomeTenRptFourList;
	}

	public void setIncomeTenRptFourList(List incomeTenRptFourList) {
		this.incomeTenRptFourList = incomeTenRptFourList;
	}

	public Map getIncomeTenRptFourMap() {
		return incomeTenRptFourMap;
	}

	public void setIncomeTenRptFourMap(Map incomeTenRptFourMap) {
		this.incomeTenRptFourMap = incomeTenRptFourMap;
	}

	public String getIncomeTenRptFourPath() {
		return incomeTenRptFourPath;
	}

	public void setIncomeTenRptFourPath(String incomeTenRptFourPath) {
		this.incomeTenRptFourPath = incomeTenRptFourPath;
	}

	public List getIncomeMonthList() {
		return incomeMonthList;
	}

	public void setIncomeMonthList(List incomeMonthList) {
		this.incomeMonthList = incomeMonthList;
	}

	public Map getIncomeMonthMap() {
		return incomeMonthMap;
	}

	public void setIncomeMonthMap(Map incomeMonthMap) {
		this.incomeMonthMap = incomeMonthMap;
	}

	public String getIncomeMonthPath() {
		return incomeMonthPath;
	}

	public void setIncomeMonthPath(String incomeMonthPath) {
		this.incomeMonthPath = incomeMonthPath;
	}

	public List getIncomeMonthThreeList() {
		return incomeMonthThreeList;
	}

	public void setIncomeMonthThreeList(List incomeMonthThreeList) {
		this.incomeMonthThreeList = incomeMonthThreeList;
	}

	public Map getIncomeMonthThreeMap() {
		return incomeMonthThreeMap;
	}

	public void setIncomeMonthThreeMap(Map incomeMonthThreeMap) {
		this.incomeMonthThreeMap = incomeMonthThreeMap;
	}

	public String getIncomeMonthThreePath() {
		return incomeMonthThreePath;
	}

	public void setIncomeMonthThreePath(String incomeMonthThreePath) {
		this.incomeMonthThreePath = incomeMonthThreePath;
	}

	public List getIncomeQuarList() {
		return incomeQuarList;
	}

	public void setIncomeQuarList(List incomeQuarList) {
		this.incomeQuarList = incomeQuarList;
	}

	public Map getIncomeQuarMap() {
		return incomeQuarMap;
	}

	public void setIncomeQuarMap(Map incomeQuarMap) {
		this.incomeQuarMap = incomeQuarMap;
	}

	public String getIncomeQuarPath() {
		return incomeQuarPath;
	}

	public void setIncomeQuarPath(String incomeQuarPath) {
		this.incomeQuarPath = incomeQuarPath;
	}

	public List getIncomeQuarThreeList() {
		return incomeQuarThreeList;
	}

	public void setIncomeQuarThreeList(List incomeQuarThreeList) {
		this.incomeQuarThreeList = incomeQuarThreeList;
	}

	public Map getIncomeQuarThreeMap() {
		return incomeQuarThreeMap;
	}

	public void setIncomeQuarThreeMap(Map incomeQuarThreeMap) {
		this.incomeQuarThreeMap = incomeQuarThreeMap;
	}

	public String getIncomeQuarThreePath() {
		return incomeQuarThreePath;
	}

	public void setIncomeQuarThreePath(String incomeQuarThreePath) {
		this.incomeQuarThreePath = incomeQuarThreePath;
	}

	public List getIncomeYearList() {
		return incomeYearList;
	}

	public void setIncomeYearList(List incomeYearList) {
		this.incomeYearList = incomeYearList;
	}

	public Map getIncomeYearMap() {
		return incomeYearMap;
	}

	public void setIncomeYearMap(Map incomeYearMap) {
		this.incomeYearMap = incomeYearMap;
	}

	public String getIncomeYearPath() {
		return incomeYearPath;
	}

	public void setIncomeYearPath(String incomeYearPath) {
		this.incomeYearPath = incomeYearPath;
	}

	public List<TdEnumvalueDto> getSumitemlist() {
		return sumitemlist;
	}

	public void setSumitemlist(List<TdEnumvalueDto> sumitemlist) {
		this.sumitemlist = sumitemlist;
	}

	public String getSleSumItem() {
		return sleSumItem;
	}

	public void setSleSumItem(String sleSumItem) {
		this.sleSumItem = sleSumItem;
	}

	public String getSleSubjectType() {
		return sleSubjectType;
	}

	public void setSleSubjectType(String sleSubjectType) {
		this.sleSubjectType = sleSubjectType;
	}

	public List getIList() {
		return iList;
	}

	public void setIList(List list) {
		iList = list;
	}

	public Map getIMap() {
		return iMap;
	}

	public void setIMap(Map map) {
		iMap = map;
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

	public String getIpath() {
		return ipath;
	}

	public void setIpath(String ipath) {
		this.ipath = ipath;
	}
	
}
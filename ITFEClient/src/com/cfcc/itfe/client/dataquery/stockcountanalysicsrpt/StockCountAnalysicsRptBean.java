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
 * @time 13-09-16 16:20:21 ��ϵͳ: DataQuery ģ��:StockCountAnalysicsRpt
 *       ���:StockCountAnalysicsRpt
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
	// ��������
	private String sleTrename;
	// Ͻ����־
	private String sleOfFlag;
	// �����ڱ�־
	private String sleTrimFlag;
	// Ԥ������
	private String sleBudKind;
	// ��λ
	private String sleMoneyUnit;
	// ��λ����
	private String moneyUnitName;
	// ����Ͻ����־
	private String sleOfFlagName = null;
	// ����Ԥ�������־
	private String sbudgettypename = null;

	// �������
	private String reporttitle = null;
	private String filenamecmt = null;

	// ����·��
	private String reportPath;
	// ����list
	private List reportList;
	// ����map
	private Map<String, String> reportMap;

	// �������
	String rptTitle = "";
	String datename = "";
	// ��ֹ����
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

	// ҳ���ѯ������ʼ��
	private void init() {
		// ��ѯ���������dto
		TsTreasuryDto tredto = new TsTreasuryDto();
		// ���Ļ�������
		String centerorg = null;

		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
			// ���Ļ�����ȡ�����й����б�
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// �����Ļ�����ȡ�õ�¼������Ӧ����
			else {
				tredto = new TsTreasuryDto();
				tredto.setSorgcode(loginfo.getSorgcode());
				treList = commonDataAccessService.findRsByDto(tredto);
			}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		// ��ʼ���������Ĭ��ֵ
		if (treList.size() > 0) {
			sleTreCode = ((TsTreasuryDto) treList.get(0)).getStrecode();
		}

		// ��ʼ����������
		for (int i = 0; i < treList.size(); i++) {
			TsTreasuryDto tmp = (TsTreasuryDto) treList.get(i);
			if (tmp.getStrecode().equals(sleTreCode)) {
				sleTrename = tmp.getStrename();
			}
		}

		// �����ڱ�־
		sleTrimFlag = StateConstant.TRIMSIGN_FLAG_NORMAL;
		// ��λ
		sleMoneyUnit = StateConstant.MONEY_UNIT_3;
		// ��λ����
		moneyUnitName = "��λ ����Ԫ";
		// Ͻ����־
		sleOfFlag = MsgConstant.RULE_SIGN_ALL;
		// Ͻ����־����
		sleOfFlagName = "ȫϽ";
		// Ԥ������
		sleBudKind = MsgConstant.BDG_KIND_IN;
		// Ԥ����������
		sbudgettypename = "Ԥ����";

	}

	/**
	 * Direction: ��ѯ ename: queryReport ���÷���: viewers: ���ͳ�Ʒ�����ѯ������� messages:
	 */
	public String queryReport(Object o) {

		/**
		 * ��ѯ����У��
		 */
		if (!querycheck()) {
			return null;
		}

		// �������
		reporttitle = sleTrename + "(" + sleOfFlagName + ")" + startqueryyear
				+ "-" + endqueryyear + sbudgettypename + "������ͳ�Ʒ�����";

		// ����ʱ��
		String date = TimeFacade.getCurrentStringTime();

		/**
		 * ���屨����Ϣչʾ
		 */
		try {
			rptTitle = reporttitle;

			// ����������
			date = date.substring(0, 4) + "��" + date.substring(4, 6) + "��"
					+ date.substring(6, 8) + "��";

			// ��ʼ���-startqueryyear �������-endqueryyear ��ʼ����-startdate ��������-enddate
			// �������-sleTreCode Ͻ����־-sleOfFlag ��λ-sleMoneyUnit
			reportList = payAndIncomeBillService.stockCountQueryList(loginfo
					.getSorgcode(), startqueryyear, endqueryyear, startdate,
					enddate, sleTreCode, sleOfFlag, sleMoneyUnit);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}

		if (null == reportList || reportList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѯ������!");
			return "";
		}
		// �������
		reportMap.put("reportTitle", rptTitle.toString());
		// ��ֹ����
		startenddate = startdate.substring(0, 2) + " �� "
				+ startdate.substring(2, 4) + " �� " + "-"
				+ enddate.substring(0, 2) + " �� " + enddate.substring(2, 4)
				+ " �� ";
		reportMap.put("startenddate", startenddate.toString());
		// ��λ
		reportMap.put("sleMoneyUnit", moneyUnitName.toString());
		// ���Ƶ�λ
		reportMap.put("makeUnit", sleTrename.toString());
		// ������
		reportMap.put("makeUser", loginfo.getSuserName().toString());
		// ����ʱ��
		reportMap.put("makeDate", date.toString());

		return super.queryReport(o);
	}

	/**
	 * ��ѯ����У��
	 */
	public boolean querycheck() {

		if (null == startqueryyear || "".equals(startqueryyear)) {
			MessageDialog.openMessageDialog(null, "��ѡ��ʼ���");
			return false;
		} else if (null == endqueryyear || "".equals(endqueryyear)) {
			MessageDialog.openMessageDialog(null, "��ѡ��������");
			return false;
		} else if (Integer.parseInt(startqueryyear) > Integer
				.parseInt(endqueryyear)) {
			MessageDialog.openMessageDialog(null, "��ʼ���ӦС�ڽ������");
			return false;
		} else if (null == startdate || "".equals(startdate)) {
			MessageDialog.openMessageDialog(null, "�����뿪ʼ����");
			return false;
		} else if (Integer.parseInt(startdate) < 101
				|| Integer.parseInt(startdate) > 1231
				|| startdate.substring(0, 2).equals("00")
				|| startdate.substring(2, 4).equals("00")) {
			MessageDialog.openMessageDialog(null, "����Ŀ�ʼ���ڲ���ȷ");
			return false;
		} else if (null == enddate || "".equals(enddate)) {
			MessageDialog.openMessageDialog(null, "��ѡ���������");
			return false;
		} else if (Integer.parseInt(enddate) < 101
				|| Integer.parseInt(enddate) > 1231
				|| enddate.substring(0, 2).equals("00")
				|| enddate.substring(2, 4).equals("00")) {
			MessageDialog.openMessageDialog(null, "����Ľ������ڲ���ȷ");
			return false;
		} else if (Integer.parseInt(startdate) > Integer.parseInt(enddate)) {
			MessageDialog.openMessageDialog(null, "��ʼ����ӦС�ڽ�������");
			return false;
		} else if (null == sleTreCode || "".equals(sleTreCode)) {
			MessageDialog.openMessageDialog(null, "��ѡ��������");
			return false;
		} else if (null == sleOfFlag || "".equals(sleOfFlag)) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ͻ����־");
			return false;
		} else if (null == sleBudKind || "".equals(sleBudKind)) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ԥ������");
			return false;
		} else if (null == sleTrimFlag || "".equals(sleTrimFlag)) {
			MessageDialog.openMessageDialog(null, "��ѡ������ڱ�־");
			return false;
		}
		return true;
	}

	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: ���ͳ�Ʒ�����ѯ������� messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}

	/**
	 * Direction: ����Excel�ļ� ename: exportExcelFile ���÷���: viewers: * messages:
	 */
	public String exportExcelFile(Object o) {

		// ����ʱ��
		String date = TimeFacade.getCurrentStringTime();
		// ����������
		date = date.substring(0, 4) + "��" + date.substring(4, 6) + "��"
				+ date.substring(6, 8) + "��";

		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		// ����ģ���ļ�·��
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
		// �½���������
		ReportExcel.setCopyFilename(copyFilename);

		ReportExcel.setReporttitle(rptTitle);
		// ����������
		ReportExcel.setDate(date);

		// ����ģ������
		ReportExcel.setFilename("Stock.xls");
		ReportExcel.setInputstream(StockCountAnalysicsRptBean.class
				.getResourceAsStream("Stock.xls"));
		// ����sheet����
		ReportExcel.setSheetname(datename);

		try {
			ReportExcel.getStockCountAnalysisReport(reportList, startenddate,
					moneyUnitName, sleTrename, loginfo.getSuserName());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}

		MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ѱ��浽��" + filerootpath
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
			sleOfFlagName = "ȫϽ";
		} else if (sleOfFlag.equals(MsgConstant.RULE_SIGN_SELF)) {
			sleOfFlagName = "����";
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
			sbudgettypename = "Ԥ����";
		} else if (sleBudKind.equals(MsgConstant.BUDGET_TYPE_OUT_BDG)) {
			sbudgettypename = "Ԥ����";
		}
		this.sleBudKind = sleBudKind;
		this.editor.fireModelChanged();
	}

	public String getSleMoneyUnit() {
		return sleMoneyUnit;
	}

	public void setSleMoneyUnit(String sleMoneyUnit) {
		if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			moneyUnitName = "��λ�� Ԫ";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			moneyUnitName = "��λ�� ��Ԫ";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			moneyUnitName = "��λ����Ԫ ";
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
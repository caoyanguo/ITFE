/**
 * ����Ԥ������ͳ�Ʊ���
 */
package com.cfcc.itfe.client.dataquery.finbudgetreccountreport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.ReportExcel;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:����Ԥ������ͳ�Ʊ���
 * 
 * @author db2admin
 * @time 10-08-25 13:33:20 ��ϵͳ: DataQuery ģ��:FinBudgetRecCountReport
 *       ���:FinBudgetRecCountReport
 */
@SuppressWarnings("unchecked")
public class FinBudgetRecCountReportBean extends
		AbstractFinBudgetRecCountReportBean implements IPageDataProvider {

	private static Log log = LogFactory
			.getLog(FinBudgetRecCountReportBean.class);
	ITFELoginInfo loginfo;
	// ��������б�
	private List<TsTreasuryDto> treList;
	// �������
	private String sleTreCode;
	// ��������
	private String sleTrename;
	// Ͻ����־
	private String sleOfFlag;
	// ��������
	private String sleBillType;
	// �����ڱ�־
	private String sleTrimFlag;
	// ��ѯ����
	private String sdate;
	// Ԥ������
	private String sleBudKind;
	// Ԥ�㼶��
	private String sleBudLevel;
	// ��Ŀ����
	private String sleSubjectType;
	// ��Ŀ����
	private String sleSubjectAttribute = null;
	// ��λ
	private String sleMoneyUnit;
	// ��λ����
	private String moneyUnitName;
	// ����Ͻ����־
	private String sleOfFlagName = null;
	// ����Ԥ�㼶�α�־
	private String sbudgetlevelname = null;
	// �������ͱ�־
	private String slebilltypename = null;
	// ����Ԥ�������־
	private String sbudgettypename = null;

	private Boolean flag = true;
	// ��ѯ��list
	private List list = null;
	// ��������
	private String reporttitle = null;
	private String filenamecmt = null;
	// ������־
	private String smoveflag = null;
	// ������־����
	private String smoveflagname = null;
	// ����DTO
	TrIncomedayrptDto incomedto = new TrIncomedayrptDto();
	// ֧������DTO
	TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();
	// Ѯ������
	private String sleTenrptType;
	// ��������
	private String sleQuarterrptType;
	// ���걨����
	private String sleHalfyearrptType;
	// Ѯ����������
	private String sleTenrptTypename;
	// ������������
	private String sleQuarterrptTypename;
	// ���걨��������
	private String sleHalfyearrptTypename;
	// �������ͣ�֧���������룩
	private String reporttype;
	// ���������б�
	private List<Mapper> reporttypelist;
	// ��������(Ѯ�������������걨)
	private String rptType;
	// ������������(Ѯ�������������걨)
	private List<Mapper> rptTypeList;
	// ����·��
	private String reportPath;
	// ����list
	private List reportList;
	// ����map
	private Map<String, String> reportMap;
	// ��������
	String rptTitle = "";
	String datename = "";

	// ������ѯ�б�
	private List iList = null;

	public FinBudgetRecCountReportBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		pagingcontext = new PagingContext(this);
		reportPath = "/com/cfcc/itfe/client/ireport/FinBudgetRecCountReport.jasper";
		reportList = new ArrayList();
		reportMap = new HashMap<String, String>();
		rptType = null;
		rptTypeList = new ArrayList<Mapper>();
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
			sleTreCode = treList.get(0).getStrecode();
		}

		// ��ʼ����������
		for (TsTreasuryDto tmp : treList) {
			if (tmp.getStrecode().equals(sleTreCode)) {
				sleTrename = tmp.getStrename();
			}
		}

		// ��������
		sleBillType = StateConstant.REPORT_DAY;
		// ������������
		slebilltypename = "�ձ�";
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
		// Ԥ�㼶��
		sleBudLevel = MsgConstant.BUDGET_LEVEL_CENTER;
		// Ԥ�㼶������
		sbudgetlevelname = "���뼶";
		// ��ѯ����
		sdate = TimeFacade.getCurrentStringTime();
		// ��Ŀ����
		sleSubjectAttribute = StateConstant.SUBJECTATTR_CLASS;
		// ������־
		smoveflag = StateConstant.MOVEFLAG_NOMOVE;
		// ������־����
		smoveflagname = "�ǵ���";
		// ��������������ֵ
		reporttypelist = new ArrayList<Mapper>();
		Mapper mapper1 = new Mapper();
		reporttype = "1";
		mapper1.setDisplayValue("���뱨��");
		mapper1.setUnderlyValue("1");
		reporttypelist.add(mapper1);
		Mapper mapper2 = new Mapper();
		mapper2.setDisplayValue("֧������");
		mapper2.setUnderlyValue("2");
		reporttypelist.add(mapper2);

	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: * messages:
	 */
	public String query(Object o) {

		/**
		 * ��ѯ����У��
		 */
		if (!querycheck()) {
			return null;
		}

		// ����ʱ��
		String date = TimeFacade.getCurrentStringTime();

		/**
		 * ȡ�ñ�������
		 */
		if (reporttype.equals("1")) {
			// �����������
			incomedto.setStrecode(sleTreCode);
			// Ԥ������
			incomedto.setSbudgettype(sleBudKind);
			// Ԥ�㼶�δ���
			incomedto.setSbudgetlevelcode(sleBudLevel);
			// Ͻ����־
			incomedto.setSbelongflag(sleOfFlag);
			// ��������
			incomedto.setSbillkind(sleBillType);
			// �����ڱ�־
			incomedto.setStrimflag(sleTrimFlag);
			// ����
			incomedto.setSrptdate(sdate);

			// ��������
			reporttitle = sleTrename + sbudgetlevelname + "Ԥ������"
					+ slebilltypename + "��";
		} else if (reporttype.equals("2")) {
			// �����������
			payoutdto.setStrecode(sleTreCode);
			// Ԥ������
			payoutdto.setSbudgettype(sleBudKind);
			// Ԥ�㼶�δ���
			payoutdto.setSbudgetlevelcode(sleBudLevel);
			// // Ͻ����־
			// payoutdto.setSbelongflag(sleOfFlag);
			// // ��������
			// payoutdto.setSbillkind(sleBillType);
			// // �����ڱ�־
			// payoutdto.setStrimflag(sleTrimFlag);
			// ����
			payoutdto.setSrptdate(sdate);

			// ��������
			reporttitle = sleTrename + sbudgetlevelname + "Ԥ��֧��"
					+ slebilltypename + "��";
		}

		/**
		 * ���屨����Ϣչʾ
		 */
		// �ձ�����ѯ
		if (StateConstant.REPORT_DAY.equals(sleBillType)) {
			datename = "��" + sdate.substring(0, 4) + "��"
					+ sdate.substring(4, 6) + "��" + sdate.substring(6, 8)
					+ "��<�ձ�>��";
		}
		// Ѯ������ѯ
		else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
			reporttitle += "(" + sleTenrptTypename + ")";
			datename = "��" + sdate.substring(0, 4) + "��"
					+ sdate.substring(4, 6) + "��" + "<" + sleTenrptTypename
					+ "��>��";
		}
		// �±�����ѯ
		else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
			datename = "��" + sdate.substring(0, 4) + "��"
					+ sdate.substring(4, 6) + "��" + "<�±�>��";
		}
		// ��������ѯ
		else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
			reporttitle += "(" + sleQuarterrptTypename + ")";
			datename = "��" + sdate.substring(0, 4) + "��" + "<"
					+ sleQuarterrptTypename + "��>��";
		}
		// ���걨����ѯ
		else if (StateConstant.REPORT_HALFYEAR.equals(sleBillType)) {
			reporttitle += "(" + sleHalfyearrptTypename + ")";
			datename = "��" + sdate.substring(0, 4) + "��" + "<"
					+ sleHalfyearrptTypename + "��>��";
		}
		// �걨����ѯ
		else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
			datename = "��" + sdate.substring(0, 4) + "��" + "<�걨>��";
		}

		try {
			if (reporttype.equals("1")) {
				rptTitle = sleTrename + "(" + sleOfFlagName + ")" + "Ԥ������"
						+ slebilltypename + "��" + datename;

				// ����������
				date = date.substring(0, 4) + "��" + date.substring(4, 6) + "��"
						+ date.substring(6, 8) + "��";

				reportList = budgetIncomeQueryList(incomedto);
			} else if (reporttype.equals("2")) {
				rptTitle = sleTrename + "(" + sleOfFlagName + ")" + "Ԥ��֧��"
						+ slebilltypename + "��" + datename;
				// ����������
				date = date.substring(0, 4) + "��" + date.substring(4, 6) + "��"
						+ date.substring(6, 8) + "��";

				reportList = budgetPayoutQueryList(payoutdto);
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}

		if (null == reportList || reportList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѯ������!");
			return "";
		}
		// ��������
		reportMap.put("reportTitle", rptTitle.toString());
		// ������־
		reportMap.put("smoveflag", smoveflagname.toString());
		// Ԥ�㼶��
		reportMap.put("sleBudLevel", sbudgetlevelname.toString());
		// ��λ
		reportMap.put("sleMoneyUnit", moneyUnitName.toString());
		// ���Ƶ�λ
		reportMap.put("makeUnit", sleTrename.toString());
		// ������
		reportMap.put("makeUser", loginfo.getSuserName().toString());
		// ����ʱ��
		reportMap.put("makeDate", date.toString());

		return super.query(o);
	}

	/**
	 * ����Ԥ�������ѯ return list
	 * 
	 * @throws ITFEBizException
	 */
	public List budgetIncomeQueryList(TrIncomedayrptDto incomedto)
			throws ITFEBizException {

		try {
			// �жϱ�������
			if (null != rptType && rptType.contains("x")) {
				sleTenrptType = rptType;
			} else if (null != rptType && rptType.contains("j")) {
				sleQuarterrptType = rptType;
			} else if (null != rptType && rptType.contains("n")) {
				sleHalfyearrptType = rptType;
			}
			// sleBillType--��������
			// sleTenrptType--�ձ�������sleQuarterrptType--����������sleHalfyearrptType--���걨����
			// sleSubjectType--��Ŀ���� sleSubjectAttribute--��Ŀ���� smoveflag--������־
			// sleMoneyUnit--��λ
			list = payAndIncomeBillService.makeIncomeBill(incomedto,
					sleBillType, sleTenrptType, sleQuarterrptType,
					sleHalfyearrptType, sleSubjectType, sleSubjectAttribute,
					smoveflag, sleMoneyUnit);
		} catch (ITFEBizException e) {
			log.error(e);
			throw e;
		}
		return list;
	}

	/**
	 * ����Ԥ��֧����ѯ return list
	 * 
	 * @throws ITFEBizException
	 */
	public List budgetPayoutQueryList(TrTaxorgPayoutReportDto trpayoutdto)
			throws ITFEBizException {

		try {
			// �жϱ�������
			if (null != rptType && rptType.contains("x")) {
				sleTenrptType = rptType;
			} else if (null != rptType && rptType.contains("j")) {
				sleQuarterrptType = rptType;
			} else if (null != rptType && rptType.contains("n")) {
				sleHalfyearrptType = rptType;
			}
			// sleBillType--��������
			// sleTenrptType--�ձ�������sleQuarterrptType--����������sleHalfyearrptType--���걨����
			// sleSubjectType--��Ŀ���� sleSubjectAttribute--��Ŀ���� smoveflag--������־
			// sleMoneyUnit--��λ
			list = payAndIncomeBillService.makePayoutBill(payoutdto,
					sleBillType, sleTenrptType, sleQuarterrptType,
					sleHalfyearrptType, sleSubjectType, sleSubjectAttribute,
					smoveflag, sleMoneyUnit);
		} catch (ITFEBizException e) {
			log.error(e);
			throw e;
		}
		return list;
	}

	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: ����Ԥ������ͳ�Ʊ�����ѯ���� messages:
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

		// Ͻ����־ sleOfFlagName Ԥ������ sbudgettypename
		filenamecmt = "_" + sleOfFlagName + "_" + sbudgettypename;

		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		// ����ģ���ļ�·��
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

		String datename = "";
		ReportExcel.init();

		ReportExcel.setFilepath(filerootpath);

		ReportExcel.setNewfilepath(filerootpath);
		// �½���������
		ReportExcel.setCopyFilename(copyFilename);

		ReportExcel.setReporttitle(rptTitle);
		// ����������
		ReportExcel.setDate(date);

		// ����ģ������
		ReportExcel.setFilename("Income1.xls");
		ReportExcel.setInputstream(FinBudgetRecCountReportBean.class
				.getResourceAsStream("Income1.xls"));
		// ����sheet����
		ReportExcel.setSheetname(datename);

		try {
			ReportExcel.getreportbyIncomerpt(reportList, sleTrename,
					smoveflagname, sbudgetlevelname, moneyUnitName, loginfo
							.getSuserName());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}

		MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ѱ��浽��" + filerootpath
				+ copyFilename);

		return super.exportExcelFile(o);
	}

	/**
	 * ��ѯ����У��
	 */
	public boolean querycheck() {

		if (null == sleTreCode || "".equals(sleTreCode)) {
			MessageDialog.openMessageDialog(null, "��ѡ��������");
			return false;
		} else if (null == sdate || "".equals(sdate)) {
			MessageDialog.openMessageDialog(null, "�������ѯ����");
			return false;
		} else if (null == sleBillType || "".equals(sleBillType)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񱨱�����");
			return false;
		} else if (null == sleOfFlag || "".equals(sleOfFlag)) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ͻ����־");
			return false;
		} else if (null == sleBudKind || "".equals(sleBudKind)) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ԥ������");
			return false;
		} else if (null == sleBudLevel || "".equals(sleBudLevel)) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ԥ�㼶��");
			return false;
		} else if (null == sleTrimFlag || "".equals(sleTrimFlag)) {
			MessageDialog.openMessageDialog(null, "��ѡ������ڱ�־");
			return false;
		}
		return true;
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
			// sleBillType--��������
			// sleTenrptType--�ձ�������sleQuarterrptType--����������sleHalfyearrptType--���걨����
			// sleSubjectType--��Ŀ���� sleSubjectAttribute--��Ŀ���� smoveflag--������־
			// sleMoneyUnit--��λ
			return payAndIncomeBillService.makeIncomeReport(incomedto,
					sleBillType, sleTenrptType, sleQuarterrptType,
					sleHalfyearrptType, sleSubjectType, sleSubjectAttribute,
					smoveflag, sleMoneyUnit, request);
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
		FinBudgetRecCountReportBean.log = log;
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
			sleOfFlagName = "ȫϽ";
		} else if (sleOfFlag.equals(MsgConstant.RULE_SIGN_SELF)) {
			sleOfFlagName = "����";
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
			slebilltypename = "�ձ�";
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {
			Mapper mapper1 = new Mapper();
			setRptType(StateConstant.TENRPT_TOP);
			mapper1.setDisplayValue("��Ѯ");
			mapper1.setUnderlyValue(StateConstant.TENRPT_TOP);
			rptTypeList.add(mapper1);
			Mapper mapper2 = new Mapper();
			mapper2.setDisplayValue("��Ѯ");
			mapper2.setUnderlyValue(StateConstant.TENRPT_MIDDLE);
			rptTypeList.add(mapper2);
			Mapper mapper3 = new Mapper();
			mapper3.setDisplayValue("��Ѯ");
			mapper3.setUnderlyValue(StateConstant.TENRPT_BOTTOM);
			rptTypeList.add(mapper3);
			slebilltypename = "Ѯ��";
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {
			slebilltypename = "�±�";
		} else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {
			Mapper mapper1 = new Mapper();
			setRptType(StateConstant.QUARTERRPT_FIRST);
			mapper1.setDisplayValue("��һ����");
			mapper1.setUnderlyValue(StateConstant.QUARTERRPT_FIRST);
			rptTypeList.add(mapper1);
			Mapper mapper2 = new Mapper();
			mapper2.setDisplayValue("�ڶ�����");
			mapper2.setUnderlyValue(StateConstant.QUARTERRPT_SECOND);
			rptTypeList.add(mapper2);
			Mapper mapper3 = new Mapper();
			mapper3.setDisplayValue("��������");
			mapper3.setUnderlyValue(StateConstant.QUARTERRPT_THREE);
			rptTypeList.add(mapper3);
			Mapper mapper4 = new Mapper();
			mapper4.setDisplayValue("���ļ���");
			mapper4.setUnderlyValue(StateConstant.QUARTERRPT_FOUR);
			rptTypeList.add(mapper4);
			slebilltypename = "����";
		} else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {
			Mapper mapper1 = new Mapper();
			setRptType(StateConstant.HALFYEARRPT_TOP);
			mapper1.setDisplayValue("�ϰ���");
			mapper1.setUnderlyValue(StateConstant.HALFYEARRPT_TOP);
			rptTypeList.add(mapper1);
			Mapper mapper2 = new Mapper();
			mapper2.setDisplayValue("�°���");
			mapper2.setUnderlyValue(StateConstant.HALFYEARRPT_BOTTOM);
			rptTypeList.add(mapper2);
			slebilltypename = "���걨";
		} else if (sleBillType.equals(StateConstant.REPORT_YEAR)) {
			slebilltypename = "�걨";
		}
		this.sleBillType = sleBillType;
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
			sbudgetlevelname = "���뼶";
		} else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_CITY)) {
			sbudgetlevelname = "�м�";
		} else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_AREA)) {
			sbudgetlevelname = "����";
		} else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_LOCAL)) {
			sbudgetlevelname = "�ط���";
		} else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_ANY)) {
			sbudgetlevelname = "���ּ���";
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
			sbudgettypename = "Ԥ����";
		} else if (sleBudKind.equals(MsgConstant.BUDGET_TYPE_OUT_BDG)) {
			sbudgettypename = "Ԥ����";
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

	public String getSleSubjectAttribute() {
		return sleSubjectAttribute;
	}

	public void setSleSubjectAttribute(String sleSubjectAttribute) {
		this.sleSubjectAttribute = sleSubjectAttribute;
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
			smoveflagname = "�ǵ���";
		} else if (smoveflag.equals(StateConstant.MOVEFLAG_MOVE)) {
			smoveflagname = "����";
		} else if (smoveflag.equals(StateConstant.MOVEFLAG_NORMAL)) {
			smoveflagname = "������";
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

	public String getReporttype() {
		return reporttype;
	}

	public void setReporttype(String reporttype) {
		this.reporttype = reporttype;
	}

	public List<Mapper> getReporttypelist() {
		return reporttypelist;
	}

	public void setReporttypelist(List<Mapper> reporttypelist) {
		this.reporttypelist = reporttypelist;
	}

	public String getRptType() {
		return rptType;
	}

	public void setRptType(String rptType) {
		if (rptType.equals(StateConstant.TENRPT_TOP)) {
			sleTenrptTypename = "��Ѯ";
		} else if (rptType.equals(StateConstant.TENRPT_MIDDLE)) {
			sleTenrptTypename = "��Ѯ";
		} else if (rptType.equals(StateConstant.TENRPT_BOTTOM)) {
			sleTenrptTypename = "��Ѯ";
		} else if (rptType.equals(StateConstant.QUARTERRPT_FIRST)) {
			sleQuarterrptTypename = "��һ����";
		} else if (rptType.equals(StateConstant.QUARTERRPT_SECOND)) {
			sleQuarterrptTypename = "�ڶ�����";
		} else if (rptType.equals(StateConstant.QUARTERRPT_THREE)) {
			sleQuarterrptTypename = "��������";
		} else if (rptType.equals(StateConstant.QUARTERRPT_FOUR)) {
			sleQuarterrptTypename = "���ļ���";
		} else if (rptType.equals(StateConstant.HALFYEARRPT_TOP)) {
			sleHalfyearrptTypename = "�ϰ���";
		} else if (rptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {
			sleHalfyearrptTypename = "�°���";
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
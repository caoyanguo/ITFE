/**
 * ����Ԥ��֧��ͳ�Ʊ���
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
 * codecomment:����Ԥ��֧��ͳ�Ʊ���
 * 
 * @author db2admin
 * @time 10-06-12 16:13:44 ��ϵͳ: DataQuery ģ��:finIncomeAndExpensesReportQuery
 *       ���:FinIncomeAndExpensesReportQuery
 */
@SuppressWarnings("unchecked")
public class FinIncomeAndExpensesReportQueryBean extends AbstractFinIncomeAndExpensesReportQueryBean implements
		IPageDataProvider {

	private static Log log = LogFactory
			.getLog(FinIncomeAndExpensesReportQueryBean.class);

	private PagingContext pagingcontext = new PagingContext(this);

	ITFELoginInfo loginfo;
	// ��������б�
	private List<TsTreasuryDto> treList;
	// �������
	private String sleTreCode;
	// ��������
	private String sleTrename;
	// ��ѯ����
	private String rptdate;
	// Ԥ������
	private String sleBudKind;
	// Ԥ�㼶��
	private String sleBudLevel;
	// ��������
	private String sleBillType;
	//��λ
	private String sleMoneyUnit;
	//��λ����
	private String moneyUnitName;
	// ����Ԥ�㼶�α�־
	private String sbudgetlevelname = null;
	// �������ͱ�־
	private String slebilltypename = null;
	// ����Ԥ�������־
	private String sbudgettypename = null;
	//��Ŀ����
	private String sleSubjectType;
	//��Ŀ����
	private String sleSubjectAttribute=null;
	// ��ѯ��list
	private List list = null;
	//�������
	private String reporttitle = null;
	private String filenamecmt=null;
	//������־
	private String smoveflag=null;
	//������־����
	private String smoveflagname=null;
	//Ѯ������
	private String sleTenrptType;
	//��������
	private String sleQuarterrptType;
	//���걨����
	private String sleHalfyearrptType;
	//Ѯ����������
	private String sleTenrptTypename;
	//������������
	private String sleQuarterrptTypename;
	//���걨��������
	private String sleHalfyearrptTypename;
	
	private Boolean flag=true; 
	// �����ѯ��poList
	private List poList = null;
	
	TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();

	public FinIncomeAndExpensesReportQueryBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();

		init();
	}
	
	
	/**
	 * ҳ���ѯ������ʼ��
	 */
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
				break;
			}
		}

		// ��������
		sleBillType = StateConstant.REPORT_DAY;
		// ������������
		slebilltypename = "�ձ�";
		//��λ
		sleMoneyUnit = StateConstant.MONEY_UNIT_3;
		//��λ����
		moneyUnitName = "��λ ����Ԫ";
		// Ԥ������
		sleBudKind = MsgConstant.BDG_KIND_IN;
		// Ԥ����������
		sbudgettypename = "Ԥ����";
		// Ԥ�㼶��
		sleBudLevel = MsgConstant.BUDGET_LEVEL_CENTER;
		// Ԥ�㼶������
		sbudgetlevelname = "���뼶";
		// ��ѯ����
		rptdate = TimeFacade.getCurrentStringTime();
		//��Ŀ����
		sleSubjectAttribute=StateConstant.SUBJECTATTR_CLASS;
	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: ������ʾ���� messages:
	 */
	public String query(Object o) {

		// ��ѯ����У��
		if (!querycheck()) {
			return null;
		}

		// �����������
		payoutdto.setStrecode(sleTreCode);
		// Ԥ������
		payoutdto.setSbudgettype(sleBudKind);
		// ����
		payoutdto.setSrptdate(rptdate);
		// Ԥ�㼶�δ���
		payoutdto.setSbudgetlevelcode(sleBudLevel);
		
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѯ�޼�¼��");
			return "";
		}
		return super.query(o);
	}


	/**
	 * ����Ԥ��֧����ѯ return list
	 * @throws ITFEBizException 
	 */
	public List budgetPayoutQueryList() throws ITFEBizException {

		try {
			// sleBillType--��������	sleTenrptType--�ձ�������sleQuarterrptType--����������sleHalfyearrptType--���걨���� sleSubjectType--��Ŀ���� sleSubjectAttribute--��Ŀ����  smoveflag--������־   sleMoneyUnit--��λ
			list = payAndIncomeBillService.makePayoutBill(payoutdto, sleBillType,sleTenrptType,sleQuarterrptType,sleHalfyearrptType, sleSubjectType, sleSubjectAttribute, smoveflag, sleMoneyUnit); 
		} catch (ITFEBizException e) {
			log.error(e);
			throw e;
		}
		return list;
	}

	/**
	 * ��ѯ����У��
	 */
	public boolean querycheck() {

		if (null == sleTreCode || "".equals(sleTreCode)) {
			MessageDialog.openMessageDialog(null, "��ѡ��������");
			return false;
		}
		if (null == rptdate || "".equals(rptdate)) {
			MessageDialog.openMessageDialog(null, "�������ѯ����");
			return false;
		}
		if (null == sleBillType || "".equals(sleBillType)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񱨱�����");
			return false;
		}
		if (null == sleBudKind || "".equals(sleBudKind)) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ԥ������");
			return false;
		}
		if (null == sleBudLevel || "".equals(sleBudLevel)) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ԥ�㼶��");
			return false;
		}
		return true;
	}

	
    
    
    /**
	 * Direction: ����Excel�ļ�
	 * ename: exportExcelFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
	public String exportExcelFile(Object o) {
		
		
		// �������
		reporttitle = sleTrename + sbudgetlevelname + "Ԥ��֧��"+ slebilltypename + "��";
		
		// Ͻ����־ sleOfFlagName Ԥ������ sbudgettypename 
		filenamecmt="_"+"ȫϽ"+"_"+sbudgettypename;

		
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		// ����ģ���ļ�·��
    	String filerootpath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
    	String copyFilename = this.sleTreCode+"_"+rptdate+"_"+reporttitle+filenamecmt+"_"+(new SimpleDateFormat("ddHHmmsss").format(System.currentTimeMillis())+"R.xls");
		
		String datename="";
		ReportExcel.init();
		
		ReportExcel.setFilepath(filerootpath);
	
		ReportExcel.setNewfilepath(filerootpath );
		// �½���������
		ReportExcel.setCopyFilename(copyFilename);
		
		/**
		 * ���屨����Ϣչʾ
		 */
		// ����Ԥ�������ձ����ѯ
		if (StateConstant.REPORT_DAY.equals(sleBillType)) {
			datename="��"+rptdate.substring(0, 4) + "��"+rptdate.substring(4, 6) + "��"+ rptdate.substring(6, 8) + "��<�ձ�>��";
		}
		// ����Ԥ������Ѯ�����ѯ
		else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
			reporttitle+="("+sleTenrptTypename+")";
			datename="��"+rptdate.substring(0, 4) + "��"+rptdate.substring(4, 6) + "��"+ "<"+sleTenrptTypename+"��>��";
		}
		// ����Ԥ�������±����ѯ
		else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
			datename="��"+rptdate.substring(0, 4) + "��"+rptdate.substring(4, 6) + "��"+ "<�±�>��";
		}
		// ����Ԥ�����뼾�����ѯ
		else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
			reporttitle+="("+sleQuarterrptTypename+")";
			datename="��"+rptdate.substring(0, 4) + "��"+ "<"+sleQuarterrptTypename+"��>��";
		}
		// ����Ԥ��������걨���ѯ
		else if (StateConstant.REPORT_HALFYEAR.equals(sleBillType)) {
			reporttitle+="("+sleHalfyearrptTypename+")";
			datename="��"+rptdate.substring(0, 4) + "��"+ "<"+sleHalfyearrptTypename+"��>��";
		}
		// ����Ԥ�������걨���ѯ
		else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
			datename="��"+rptdate.substring(0, 4) + "��"+ "<�걨>��";
		}
		
		ReportExcel.setReporttitle(sleTrename + "(ȫϽ)"+ "Ԥ��֧��"+ slebilltypename + "��"+datename);
		// ����������
		String date = TimeFacade.getCurrentStringTime();
		date = date.substring(0, 4) + "��" + date.substring(4, 6) + "��"	+ date.substring(6, 8) + "��";
		ReportExcel.setDate(date);

		// ����ģ������
		ReportExcel.setFilename("Income1.xls");
		ReportExcel.setInputstream(FinIncomeAndExpensesReportQueryBean.class
				.getResourceAsStream("Income1.xls"));
		// ����sheet����
		ReportExcel.setSheetname(datename);

		try {
			poList = budgetPayoutQueryList();
			if (null == poList || poList.size() == 0) {
				MessageDialog.openMessageDialog(null, "��ѯ�޼�¼");
				return null;
			}
			//�����빫��һ��getreportbyIncomerpt()����
			ReportExcel.getreportbyIncomerpt(poList,sleTrename, smoveflagname,sbudgetlevelname,moneyUnitName,loginfo.getSuserName());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}
		
		MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ѱ��浽��" + filerootpath+copyFilename);
		return super.exportExcelFile(o);
	}
    

	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: ������֧�±���ѯ���� messages:
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
			// sleBillType--��������	sleTenrptType--�ձ�������sleQuarterrptType--����������sleHalfyearrptType--���걨���� sleSubjectType--��Ŀ���� sleSubjectAttribute--��Ŀ����  smoveflag--������־   sleMoneyUnit--��λ
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
			slebilltypename = "�ձ�";
		} else if (sleBillType.equals(StateConstant.REPORT_TEN)) {
			setSleTenrptType(StateConstant.TENRPT_TOP);
			setSleQuarterrptType("");
			setSleHalfyearrptType("");
			slebilltypename = "Ѯ��";
		} else if (sleBillType.equals(StateConstant.REPORT_MONTH)) {
			setSleTenrptType("");
			setSleQuarterrptType("");
			setSleHalfyearrptType("");
			slebilltypename = "�±�";
		}else if (sleBillType.equals(StateConstant.REPORT_QUAR)) {
			setSleTenrptType("");
			setSleQuarterrptType(StateConstant.QUARTERRPT_FIRST);
			setSleHalfyearrptType("");
			slebilltypename = "����";
		}else if (sleBillType.equals(StateConstant.REPORT_HALFYEAR)) {
			setSleTenrptType("");
			setSleQuarterrptType("");
			setSleHalfyearrptType(StateConstant.HALFYEARRPT_TOP);
			slebilltypename = "���걨";
		}else if (sleBillType.equals(StateConstant.REPORT_YEAR)) {
			setSleTenrptType("");
			setSleQuarterrptType("");
			setSleHalfyearrptType("");
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
			sbudgetlevelname = "���뼶";
		} else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_CITY)) {
			sbudgetlevelname = "�м�";
		} else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_AREA)) {
			sbudgetlevelname = "����";
		}else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_LOCAL)) {
			sbudgetlevelname = "�ط���";
		}else if (sleBudLevel.equals(StateConstant.SUBJECTLEVEL_ANY)) {
			sbudgetlevelname = "���ּ���";
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
			sbudgettypename = "Ԥ����";
		} else if (sleBudKind.equals(MsgConstant.BUDGET_TYPE_OUT_BDG)) {
			sbudgettypename = "Ԥ����";
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
			smoveflagname = "�ǵ���";
		} else if (smoveflag.equals(StateConstant.MOVEFLAG_MOVE)) {
			smoveflagname = "����";
		} else if (smoveflag.equals(StateConstant.MOVEFLAG_NORMAL)) {
			smoveflagname = "������";
		} 
		this.smoveflag = smoveflag;
		this.editor.fireModelChanged();
	}

	public String getSleTenrptType() {
		return sleTenrptType;
	}

	public void setSleTenrptType(String sleTenrptType) {
		if (sleTenrptType.equals(StateConstant.TENRPT_TOP)) {
			sleTenrptTypename = "��Ѯ";
		} else if (sleTenrptType.equals(StateConstant.TENRPT_MIDDLE)) {
			sleTenrptTypename = "��Ѯ";
		} else if (sleTenrptType.equals(StateConstant.TENRPT_BOTTOM)) {
			sleTenrptTypename = "��Ѯ";
		}
		this.sleTenrptType = sleTenrptType;
		this.editor.fireModelChanged();
	}

	public String getSleQuarterrptType() {
		return sleQuarterrptType;
	}

	public void setSleQuarterrptType(String sleQuarterrptType) {
		if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FIRST)) {
			sleQuarterrptTypename = "��һ����";
		} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_SECOND)) {
			sleQuarterrptTypename = "�ڶ�����";
		} else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_THREE)) {
			sleQuarterrptTypename = "��������";
		}else if (sleQuarterrptType.equals(StateConstant.QUARTERRPT_FOUR)) {
			sleQuarterrptTypename = "���ļ���";
		}
		this.sleQuarterrptType = sleQuarterrptType;
		this.editor.fireModelChanged();
	}

	public String getSleHalfyearrptType() {
		return sleHalfyearrptType;
	}

	public void setSleHalfyearrptType(String sleHalfyearrptType) {
		if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_TOP)) {
			sleHalfyearrptTypename = "�ϰ���";
		} else if (sleHalfyearrptType.equals(StateConstant.HALFYEARRPT_BOTTOM)) {
			sleHalfyearrptTypename = "�°���";
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
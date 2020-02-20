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
 * @time 15-05-18 15:46:17 ��ϵͳ: DataAudit ģ��:finpaystatisticalreport
 *       ���:Finpaystatisticalreport
 */
public class FinpaystatisticalreportBean extends
		AbstractFinpaystatisticalreportBean implements IPageDataProvider {

	private static Log log = LogFactory
			.getLog(FinpaystatisticalreportBean.class);
	private PagingContext pagingcontext = new PagingContext(this);

	ITFELoginInfo loginfo;
	// ��������б�
	private List<TsTreasuryDto> treList;
	// ���ջ��ش����б�
	private List<TsTaxorgDto> taxorgList;
	// ���ջ��ش���
	private String sleTaxOrgCode;
	// ���ջ�������
	private String sleTaxOrgName;
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
	// ������ʽ���ͣ�����ʽ������ʽ������ʽ��
	private String sleBillKind;

	// Ͻ����־
	private String sleOfFlag;
	// �����ڱ�־
	private String sleTrimFlag;
	// ��ѯ�·�
	private String month;
	// ��Ŀ����
	private String sbudgetsubcode;

	private String sleMoneyUnit;
	private String moneyUnitName;

	// ����Ͻ����־
	private String offlag = null;
	// ����Ԥ�㼶�α�־
	private String sbudgetlevelname = null;
	// �������ͱ�־
	private String slebilltypename = null;
	// ����Ԥ�������־
	private String sbudgettypename = null;

	private String sleSumItem = null;

	// ����ϼ�list
	private List<TdEnumvalueDto> sumitemlist;

	private String slereportname = null;
	private String slereportnamecmt = null;
	// ��������list
	private List<TdEnumvalueDto> slereportnamelist;

	// ��ѯ��list
	private List list = null;

	//
	private String reporttitle = null;

	//
	private String filenamecmt = null;

	//
	private String popath = null;

	private Boolean flag = true;

	// Ԥ�㵥λ����
	private String scorpcode = null;

	// Ԥ�㵥λ����
	private String scorpname = null;

	// ֧���ձ������ʽ��ʾ
	private List payoutList = null;
	private Map payoutMap = new HashMap();
	private String payoutPath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportTwo.jasper";

	// ֧���ձ�������ʽ��ʾ
	private List payoutThreeList = null;
	private Map payoutThreeMap = new HashMap();
	private String payoutThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportThree.jasper";

	// ֧���ձ�������ʽ��ʾ
	private List payoutFourList = null;
	private Map payoutFourMap = new HashMap();
	private String payoutFourPath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportFour.jasper";

	// ֧��Ѯ�������ʽ��ʾ
	private List payoutTenRptList = null;
	private Map payoutTenRptMap = new HashMap();
	private String payoutTenRptPath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportTwo.jasper";

	// ֧��Ѯ��������ʽ��ʾ
	private List payoutTenRptThreeList = null;
	private Map payoutTenRptThreeMap = new HashMap();
	private String payoutTenRptThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportThree.jasper";

	// ֧��Ѯ��������ʽ��ʾ
	private List payoutTenRptFourList = null;
	private Map payoutTenRptFourMap = new HashMap();
	private String payoutTenRptFourPath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportFour.jasper";

	// ֧���±������ʽ��ʾ
	private List payoutMonthList = null;
	private Map payoutMonthMap = new HashMap();
	private String payoutMonthPath = "com/cfcc/itfe/client/ireport/FinBudgetRecMonthReportTwo.jasper";

	// ֧���±�������ʽ��ʾ
	private List payoutMonthThreeList = null;
	private Map payoutMonthThreeMap = new HashMap();
	private String payoutMonthThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecMonthReportThree.jasper";

	// ֧�����������ʽ��ʾ
	private List payoutQuarList = null;
	private Map payoutQuarMap = new HashMap();
	private String payoutQuarPath = "com/cfcc/itfe/client/ireport/FinBudgetRecQuarReportTwo.jasper";

	// ֧������������ʽ��ʾ
	private List payoutQuarThreeList = null;
	private Map payoutQuarThreeMap = new HashMap();
	private String payoutQuarThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecQuarReportThree.jasper";

	// ֧���걨�����ʽ��ʾ
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

		// ����ϼ�
		TdEnumvalueDto finddto = new TdEnumvalueDto();
		finddto.setStypecode("0406");
		try {
			sumitemlist = commonDataAccessService.findRsByDtoUR(finddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}

		// ֧����������
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
	 * ����������ӡ����
	 * 
	 * @param idto
	 */
	public void setBathPrar(TsBatchcrtreportmsgDto idto) {

		this.setFlag(false);
		this.setSleTreCode(idto.getSletrecode());
		this.setRptdate(idto.getRptdate());
		this.setSleBudKind(idto.getSlebudkind());
		this.setSleSumItem(idto.getSlesumitem());// ����ϼ�
		this.setSleBillType(idto.getSlebilltype());
		this.setSleBillKind(idto.getSlebillkind());
		this.setSleTrimFlag(idto.getSletrimflag());
		this.setSleOfFlag(idto.getSleofflag());
		this.setScorpcode(idto.getScorpcode());
		this.setSlereportname(idto.getSlereportname());
	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: ������ʾ���� messages:
	 */
	public String query(Object o) {

		// ��ѯ����У��
		if (!querycheck()) {
			return null;
		}

		/**
		 * �����β�ѯ
		 */
		if (!reportparamquery()) {
			return null;
		}

		/**
		 * ȡ�ñ�������
		 */
		try {
			poList = budgetPayoutQueryList();
			if (null == poList || poList.size() == 0) {
				MessageDialog.openMessageDialog(null, "��ѯ�޼�¼");
				return null;
			}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}

		/**
		 * ���Ʊ���ͨ��Ϣ
		 */
		// ��������
		poMap
				.put("makedate", rptdate.substring(0, 4) + "��"
						+ rptdate.substring(4, 6) + "��"
						+ rptdate.substring(6, 8) + "��");
		// Ͻ����־
		poMap.put("offlag", "��" + offlag + "��");
		// Ԥ������
		poMap.put("sbudgettype", "��" + sbudgettypename + "��");
		// �Ʊ���
		poMap.put("username", loginfo.getSuserName());
		// Ԥ�㵥λ����
		poMap.put("taxorgname", this.scorpname);
		poMap.put("printtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(System.currentTimeMillis()));

		// �������
		reporttitle = sleTrename + slereportnamecmt + slebilltypename + "��";
		poMap.put("billtitle", reporttitle);

		filenamecmt = "_" + offlag + "_" + sbudgettypename;

		/**
		 * ���屨����Ϣչʾ
		 */
		// ����Ԥ��֧���ձ����ѯ
		if (StateConstant.REPORT_DAY.equals(sleBillType)) {
			// �ձ����ѯ
			dayReportQuery();
		}
		// ����Ԥ��֧��Ѯ�����ѯ
		else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
			// Ѯ�����ѯ
			tenReportQuery();
		}
		// ����Ԥ��֧���±����ѯ
		else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
			// �±����ѯ
			monthReportQuery();
		}
		// ����Ԥ��֧���������ѯ
		else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
			// �������ѯ
			quarReportQuery();
		}
		// ����Ԥ��֧���걨���ѯ
		else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
			// �걨���ѯ
			yearReportQuery();
		}
		return super.query(o);
	}

	/**
	 * ����Ԥ��֧���ձ����ѯ
	 */
	public void dayReportQuery() {
		// ����Ԥ��֧���ձ������ʽ
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutPath;

			this.setPayoutList(poList);
			this.setPayoutMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧���ձ������ʽ", true);
		}
		// ����Ԥ��֧���ձ�������ʽ
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutThreePath;

			this.setPayoutThreeList(poList);
			this.setPayoutThreeMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧���ձ�������ʽ", true);
		}
		// ����Ԥ��֧���ձ�������ʽ
		else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutFourPath;

			this.setPayoutFourList(poList);
			this.setPayoutFourMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧���ձ�������ʽ", true);
		}
	}

	/**
	 * ����Ԥ��֧��Ѯ�����ѯ
	 */
	public void tenReportQuery() {
		// ����Ԥ��֧��Ѯ�������ʽ
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutTenRptPath;

			this.setPayoutTenRptList(poList);
			this.setPayoutTenRptMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧��Ѯ�������ʽ", true);
		}
		// ����Ԥ��֧��Ѯ��������ʽ
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutTenRptThreePath;

			this.setPayoutTenRptThreeList(poList);
			this.setPayoutTenRptThreeMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧��Ѯ��������ʽ", true);
		}
		// ����Ԥ��֧��Ѯ��������ʽ
		else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutTenRptFourPath;

			this.setPayoutTenRptFourList(poList);
			this.setPayoutTenRptFourMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧��Ѯ��������ʽ", true);
		}
	}

	/**
	 * ����Ԥ��֧���±����ѯ
	 */
	public void monthReportQuery() {
		// ����Ԥ��֧���±������ʽ
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutMonthPath;

			this.setPayoutMonthList(poList);
			this.setPayoutMonthMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧���±������ʽ", true);
		}
		// ����Ԥ��֧���±�������ʽ
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutMonthThreePath;

			this.setPayoutMonthThreeList(poList);
			this.setPayoutMonthThreeMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧���±�������ʽ", true);
		} else {
			MessageDialog.openMessageDialog(null, "����Ԥ��֧��ͳ���±���ֻ�ж�������ʽ");
			return;
		}
	}

	/**
	 * ����Ԥ��֧���������ѯ
	 */
	public void quarReportQuery() {
		// ����Ԥ��֧�����������ʽ
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutQuarPath;

			this.setPayoutQuarList(poList);
			this.setPayoutQuarMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧�����������ʽ", true);
		}
		// ����Ԥ��֧������������ʽ
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutQuarThreePath;

			this.setPayoutQuarThreeList(poList);
			this.setPayoutQuarThreeMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧������������ʽ", true);
		} else {
			MessageDialog.openMessageDialog(null, "����Ԥ��֧��ͳ�Ƽ�����ֻ�ж�������ʽ");
			return;
		}
	}

	/**
	 * ����Ԥ��֧���걨���ѯ
	 */
	public void yearReportQuery() {
		// ����Ԥ��֧���걨�����ʽ
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {

			filenamecmt += "_����ʽ";
			popath = payoutYearPath;

			this.setPayoutYearList(poList);
			this.setPayoutYearMap(poMap);
			if (flag)
				editor.openComposite("����Ԥ��֧���걨�����ʽ", true);
		} else {
			MessageDialog.openMessageDialog(null, "����Ԥ��֧��ͳ���걨��ֻ�ж���ʽ");
			return;
		}
	}

	/**
	 * ����Ԥ��֧����ѯ return list
	 * 
	 * @throws ITFEBizException
	 */
	public List budgetPayoutQueryList() throws ITFEBizException {

		TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();
		// �����������
		payoutdto.setStrecode(sleTreCode);
		// Ԥ������
		payoutdto.setSbudgettype(sleBudKind);
		// ����
		payoutdto.setSrptdate(rptdate);

		// Ԥ�㼶�δ���--��Ԥ�㼶�δ��뵱�������������ͣ�1һ��Ԥ��֧����2���а�����Ȩ֧����
		payoutdto.setSbudgetlevelcode(slereportname);

		// Ԥ�㵥λ����
		payoutdto.setSfinorgcode(scorpcode);

		// // ��������
		// payoutdto.setSbillkind(sleBillType);
		// Ͻ����־
		// payoutdto.setSbelongflag(sleOfFlag);
		// // �����ڱ�־
		// payoutdto.setStrimflag(sleTrimFlag);

		try {
			list = finpaystatisticalreportService.makePayoutBill(payoutdto,
					sleSumItem, sleMoneyUnit, rptdate); // --��2����Ƿ���ϼ�
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
		// if (null == sleTaxOrgCode || "".equals(sleTaxOrgCode)) {
		// MessageDialog.openMessageDialog(null, "��ѡ�����ջ��ش���");
		// return false;
		// }
		if (null == rptdate || "".equals(rptdate)) {
			MessageDialog.openMessageDialog(null, "�������ѯ����");
			return false;
		}
		if (null == sleBillKind || "".equals(sleBillKind)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񱨱���ʽ����");
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
		// if (null == sleBudLevel || "".equals(sleBudLevel)) {
		// MessageDialog.openMessageDialog(null, "��ѡ��Ԥ�㼶��");
		// return false;
		// }
		return true;
	}

	/**
	 * �����β�ѯ
	 */
	public boolean reportparamquery() {
		List<TdEnumvalueDto> enumList = new ArrayList<TdEnumvalueDto>();

		try {
			TdEnumvalueDto EnumvalueDto = new TdEnumvalueDto();
			// ����Ͻ����־
			EnumvalueDto.setStypecode("0302");
			enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			for (TdEnumvalueDto valueDto : enumList) {
				if (valueDto.getSvalue().equals(sleOfFlag)) {
					offlag = valueDto.getSvaluecmt();
					break;
				}
			}
			enumList.clear();
			// // ����Ԥ�㼶�α�־
			// EnumvalueDto.setStypecode("0121");
			// enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			// for (TdEnumvalueDto valueDto : enumList) {
			// if (valueDto.getSvalue().equals(sleBudLevel)) {
			// sbudgetlevelname = valueDto.getSvaluecmt();
			// }
			// }
			// enumList.clear();
			// �������ͱ�־
			EnumvalueDto.setStypecode("0314");
			enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			for (TdEnumvalueDto valueDto : enumList) {
				if (valueDto.getSvalue().equals(sleBillType)) {
					slebilltypename = valueDto.getSvaluecmt();
					break;
				}
			}
			enumList.clear();
			// ����Ԥ�������־
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
	 * Direction: �����ļ� ename: exportFile ���÷���: viewers: * messages:
	 */
	public String exportFile(Object o) {

		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
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

		MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ѱ��浽��" + filepath
				+ copyFilename);

		return super.exportFile(o);
	}

	/**
	 * Direction: ����Excel�ļ� ename: exportExcelFile ���÷���: viewers: * messages:
	 */
	public String exportExcelFile(Object o) {
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
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

		String datename = rptdate.substring(0, 4) + "��"
				+ rptdate.substring(4, 6) + "��" + rptdate.substring(6, 8) + "��";
		ReportExcel.init();

		ReportExcel.setFilepath(filerootpath);

		ReportExcel.setNewfilepath(filerootpath);
		// �½���������
		ReportExcel.setCopyFilename(copyFilename);

		ReportExcel.setReporttitle(sleTrename + slereportnamecmt
				+ slebilltypename + "��");
		// ����������
		String date = TimeFacade.getCurrentStringTime();
		date = date.substring(0, 4) + "��" + date.substring(4, 6) + "��"
				+ date.substring(6, 8) + "��";
		ReportExcel.setDate(date);

		// ����ģ������
		ReportExcel.setFilename("model4.xls");
		ReportExcel.setInputstream(FinpaystatisticalreportBean.class
				.getResourceAsStream("model4.xls"));
		// ����sheet����
		ReportExcel.setSheetname(datename);

		try {
			ReportExcel.getreportbyOutrpt(list, offlag, sbudgettypename,
					sleBillType, sleBillKind, loginfo.getSuserName());
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
	 * Direction: ����pdf�ļ� ename: exportPdfFile ���÷���: viewers: * messages:
	 */
	public String exportPdfFile(Object o) {

		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
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

		MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ѱ��浽��" + filepath
				+ copyFilename);
		return super.exportPdfFile(o);
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
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
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
			// mod -20110906 ���м���ȫϽ����
			// if (loginfo.getSorgcode().equals(centerorg) ||
			// loginfo.getSorgcode().equals("060300000003")) {
			// treList = commonDataAccessService.findRsByDto(tredto);
			// }
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// �����Ļ�����ȡ�õ�¼������Ӧ����
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

		slereportnamelist = new ArrayList<TdEnumvalueDto>();
		// ��ʼ������ϼ�and��������
		initstrastatelist();

		// ��ʼ�����ջ��ش���
		querytaxoeglist(this.getSleTreCode());
		// ��������
		sleBillType = StateConstant.REPORT_DAY;
		// ��ʽ����
		sleBillKind = StateConstant.REPORT_TWO;
		// �����ڱ�־
		sleTrimFlag = StateConstant.TRIMSIGN_FLAG_NORMAL;
		sleMoneyUnit = StateConstant.MONEY_UNIT_5;
		moneyUnitName = "��λ ��Ԫ";
		// Ͻ����־
		sleOfFlag = MsgConstant.RULE_SIGN_SELF;
		// Ԥ������
		sleBudKind = MsgConstant.BDG_KIND_IN;
		// Ԥ�㼶��
		sleBudLevel = MsgConstant.BUDGET_LEVEL_CENTER;
		// ��ѯ����
		rptdate = TimeFacade.getCurrentStringTime();
		// ��������
		slereportname = this.getSlereportnamelist().get(0).getSvalue();
		// ����ϼ�
		sleSumItem = this.getSumitemlist().get(0).getSvalue();
	}

	public String getTrecodesql() {
		// ����û����ҽӵĹ������壬����Ϊ��ѯ����
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
	 * ���ݹ�������ѯ���ջ��ش��� ���������ջ��ض�Ӧ��ϵ��
	 */
	private List querytaxoeglist(String sleTreCode) {

		// ��ѯ�����Ӧ���ջ��ش�����dto
		TsTaxorgDto taxorgdto = new TsTaxorgDto();
		taxorgdto.setStrecode(sleTreCode);// �������
		try {
			taxorgList = commonDataAccessService.findRsByDto(taxorgdto);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		// ��ʼ�����ջ��ش���Ĭ��ֵ
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
			moneyUnitName = "��λ�� Ԫ";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_4)) {
			moneyUnitName = "��λ�� ��Ԫ";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_3)) {
			moneyUnitName = "��λ������ ";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_2)) {
			moneyUnitName = "��λ�� ʮ��";
		} else if (sleMoneyUnit.equals(StateConstant.MONEY_UNIT_1)) {
			moneyUnitName = "��λ�� ǧ��";
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
					MessageDialog.openMessageDialog(null, "Ԥ�㵥λ���벻���ڻ�������ⲻ��Ӧ��");
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
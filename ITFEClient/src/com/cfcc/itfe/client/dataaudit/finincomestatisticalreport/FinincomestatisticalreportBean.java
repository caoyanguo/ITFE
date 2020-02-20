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
 * ��ϵͳ: DataAudit
 * ģ��:finincomestatisticalreport
 * ���:Finincomestatisticalreport
 */
public class FinincomestatisticalreportBean extends AbstractFinincomestatisticalreportBean implements IPageDataProvider {

	
	
	
    private static Log log = LogFactory.getLog(FinincomestatisticalreportBean.class);
    
    ITFELoginInfo loginfo;
	// ��������б�
	private List<TsTreasuryDto> treList;
	// ���ջ��ش����б�
	private List<TsTaxorgDto> taxorgList;
	// ���ջ��ش���
	private String sleTaxOrgCode;
	// ���ջ�������
	private String sletaxorgname;
	// �������
	private String sleTreCode;
	// ��������
	private String sleTrename;

	// ������ʽ���ͣ�����ʽ������ʽ������ʽ��
	private String sleBillKind;
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
	// ��ѯ�·�
	private String month;
	// ��Ŀ����
	private String sbudgetsubcode;
	//��Ŀ����
	private String sleSubjectType;
	
	//����ϼ�list
	private List<TdEnumvalueDto> sumitemlist;
	
	private String sleSumItem=null;

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
	
	private Boolean flag=true; 

	// ��ѯ��list
	private List list = null;
	
	//
	private String reporttitle = null;
	
	//
	private String filenamecmt=null;
	
	//
	private String ipath=null;

	// �����ձ������ʽ��ʾ
	private List incomeList = null;
	private Map incomeMap = new HashMap();
	private String incomePath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportTwo.jasper";

	// �����ձ�������ʽ��ʾ
	private List incomeThreeList = null;
	private Map incomeThreeMap = new HashMap();
	private String incomeThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportThree.jasper";

	// �����ձ�������ʽ��ʾ
	private List incomeFourList = null;
	private Map incomeFourMap = new HashMap();
	private String incomeFourPath = "com/cfcc/itfe/client/ireport/FinBudgetRecCountReportFour.jasper";

	// ����Ѯ�������ʽ��ʾ
	private List incomeTenRptList = null;
	private Map incomeTenRptMap = new HashMap();
	private String incomeTenRptPath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportTwo.jasper";

	// ����Ѯ��������ʽ��ʾ
	private List incomeTenRptThreeList = null;
	private Map incomeTenRptThreeMap = new HashMap();
	private String incomeTenRptThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportThree.jasper";

	// ����Ѯ��������ʽ��ʾ
	private List incomeTenRptFourList = null;
	private Map incomeTenRptFourMap = new HashMap();
	private String incomeTenRptFourPath = "com/cfcc/itfe/client/ireport/FinBudgetRecTenReportFour.jasper";

	// �����±������ʽ��ʾ
	private List incomeMonthList = null;
	private Map incomeMonthMap = new HashMap();
	private String incomeMonthPath = "com/cfcc/itfe/client/ireport/FinBudgetRecMonthReportTwo.jasper";

	// �����±�������ʽ��ʾ
	private List incomeMonthThreeList = null;
	private Map incomeMonthThreeMap = new HashMap();
	private String incomeMonthThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecMonthReportThree.jasper";

	// ���뼾�������ʽ��ʾ
	private List incomeQuarList = null;
	private Map incomeQuarMap = new HashMap();
	private String incomeQuarPath = "com/cfcc/itfe/client/ireport/FinBudgetRecQuarReportTwo.jasper";

	// ���뼾��������ʽ��ʾ
	private List incomeQuarThreeList = null;
	private Map incomeQuarThreeMap = new HashMap();
	private String incomeQuarThreePath = "com/cfcc/itfe/client/ireport/FinBudgetRecQuarReportThree.jasper";

	// �����걨�����ʽ��ʾ
	private List incomeYearList = null;
	private Map incomeYearMap = new HashMap();
	private String incomeYearPath = "com/cfcc/itfe/client/ireport/FinBudgetRecYearReportTwo.jasper";
	
	// ������ʽ��ʾ��ͨ
	private List iList = null;
	private Map iMap = new HashMap();
	
    public FinincomestatisticalreportBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
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
			// mod -20110906 ���м���ȫϽ����
//			if (loginfo.getSorgcode().equals(centerorg) || loginfo.getSorgcode().equals("060300000003")) {
//				treList = commonDataAccessService.findRsByDto(tredto);
//			}
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// �����Ļ�����ȡ�õ�¼������Ӧ����
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
		sdate = TimeFacade.getCurrentStringTime();
		//����ϼ�
		initstrastatelist();
		sleSumItem=this.getSumitemlist().get(0).getSvalue();

	}
	
	public String getTrecodesql(){
		//����û����ҽӵĹ������壬����Ϊ��ѯ����
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
	 * ���ݹ�������ѯ���ջ��ش��� ���������ջ��ض�Ӧ��ϵ��
	 */
	private List querytaxoeglist(String sleTreCode) {
		// ��ѯ�����Ӧ���ջ��ش�����dto
		TsTaxorgDto taxorgdto = new TsTaxorgDto();
		List<TsTaxorgDto> taxorglist=new ArrayList<TsTaxorgDto>();
		try {
			TsTaxorgDto orgdto0 = new TsTaxorgDto();
			orgdto0.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto0.setStrecode(sleTreCode);// �������
			orgdto0.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			orgdto0.setStaxorgname("�������ջ���");
			orgdto0.setStaxprop(MsgConstant.MSG_TAXORG_SHARE_PROP);
			taxorglist.add(orgdto0);
			
			TsTaxorgDto orgdto1 = new TsTaxorgDto();
			orgdto1.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto1.setStrecode(sleTreCode);// �������
			orgdto1.setStaxorgcode(MsgConstant.MSG_TAXORG_NATION_CLASS);
			orgdto1.setStaxorgname("��˰");
			orgdto1.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
			taxorglist.add(orgdto1);

			TsTaxorgDto orgdto2 = new TsTaxorgDto();
			orgdto2.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto2.setStrecode(sleTreCode);// �������
			orgdto2.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			orgdto2.setStaxorgname("��˰");
			orgdto2.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
			taxorglist.add(orgdto2);

			TsTaxorgDto orgdto3 = new TsTaxorgDto();
			orgdto3.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto3.setStrecode(sleTreCode);// �������
			orgdto3.setStaxorgcode(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			orgdto3.setStaxorgname("����");
			orgdto3.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
			taxorglist.add(orgdto3);

			TsTaxorgDto orgdto4 = new TsTaxorgDto();
			orgdto4.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto4.setStrecode(sleTreCode);// �������
			orgdto4.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			orgdto4.setStaxorgname("����");
			orgdto4.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
			taxorglist.add(orgdto4);

			TsTaxorgDto orgdto5 = new TsTaxorgDto();
			orgdto5.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto5.setStrecode(sleTreCode);// �������
			orgdto5.setStaxorgcode(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			orgdto5.setStaxorgname("����");
			orgdto5.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
			taxorglist.add(orgdto5);
			
			taxorgdto.setStrecode(sleTreCode);// �������
			taxorglist.addAll(commonDataAccessService.findRsByDto(taxorgdto));
			setTaxorgList(taxorglist);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		// ��ʼ�����ջ��ش���Ĭ��ֵ
		if (taxorgList.size() > 0) {
			taxorgdto.setStrecode(sleTreCode);// �������
			setSleTaxOrgCode(taxorgList.get(0).getStaxorgcode());
		}
		return taxorgList;
	}
	
	/**
	 * ����������ӡ����
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
		
		this.setSleSumItem(idto.getSlesumitem());//����ϼ�
		this.setSleSubjectType(idto.getSlesubjecttype());//��Ŀ����
	
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
			TrIncomedayrptDto incomedto = new TrIncomedayrptDto();
			// �����������
			incomedto.setStrecode(sleTreCode);
			// ���ջ��ش���
			incomedto.setStaxorgcode(sleTaxOrgCode);
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
			
			iList = budgetIncomeQueryList(incomedto);
			if (null == iList || iList.size() == 0) {
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
		iMap.put("makedate", sdate.substring(0, 4) + "��"+ sdate.substring(4, 6) + "��"+ sdate.substring(6, 8) + "��");
		// Ͻ����־
		iMap.put("offlag", "��" + offlag + "��");
		// Ԥ������
		iMap.put("sbudgettype", "��" + sbudgettypename + "��");
		// �Ʊ���
		iMap.put("username", loginfo.getSuserName());
		// ���ջ�������
		iMap.put("taxorgname", sletaxorgname);
		// �������
		reporttitle = sleTrename + sbudgetlevelname + "��" + "Ԥ������"+ slebilltypename + "��";
		iMap.put("billtitle", reporttitle);
		iMap.put("printtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
		
		filenamecmt="_"+offlag+"_"+sbudgettypename+"_"+sletaxorgname;

		/**
		 * ���屨����Ϣչʾ
		 */
		// ����Ԥ�������ձ����ѯ
		if (StateConstant.REPORT_DAY.equals(sleBillType)) {
			// �ձ����ѯ
			dayReportQuery();
		}
		// ����Ԥ������Ѯ�����ѯ
		else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
			// Ѯ�����ѯ
			tenReportQuery();
		}
		// ����Ԥ�������±����ѯ
		else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
			// �±����ѯ
			monthReportQuery();
		}
		// ����Ԥ�����뼾�����ѯ
		else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
			// �������ѯ
			quarReportQuery();
		}
		// ����Ԥ�������걨���ѯ
		else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
			// �걨���ѯ
			yearReportQuery();
		}
		
		return super.query(o);
	}

	/**
	 * ����Ԥ�������ձ����ѯ
	 */
	public void dayReportQuery() {
		
		// ����Ԥ�������ձ������ʽ
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomePath;
			
			this.setIncomeList(iList);
			this.setIncomeMap(iMap);
			
			if(flag) editor.openComposite("����Ԥ�������ձ������ʽ", true);
			
		}
		// ����Ԥ�������ձ�������ʽ
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomeThreePath;
			
			this.setIncomeThreeList(iList);
			this.setIncomeThreeMap(iMap);
			if(flag) editor.openComposite("����Ԥ�������ձ�������ʽ", true);
		}
		// ����Ԥ�������ձ�������ʽ
		else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomeFourPath;
			
			this.setIncomeFourList(iList);
			this.setIncomeFourMap(iMap);
			if(flag) editor.openComposite("����Ԥ�������ձ�������ʽ", true);
		}
	}

	/**
	 * ����Ԥ������Ѯ�����ѯ
	 */
	public void tenReportQuery() {
		// ����Ԥ������Ѯ�������ʽ
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomeTenRptPath;
			
			this.setIncomeTenRptList(iList);
			this.setIncomeTenRptMap(iMap);
			if(flag) editor.openComposite("����Ԥ������Ѯ�������ʽ", true);
		}
		// ����Ԥ������Ѯ��������ʽ
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomeTenRptThreePath;
			
			this.setIncomeTenRptThreeList(iList);
			this.setIncomeTenRptThreeMap(iMap);
			if(flag) editor.openComposite("����Ԥ������Ѯ��������ʽ", true);
		}
		// ����Ԥ������Ѯ��������ʽ
		else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomeTenRptFourPath;
			
			this.setIncomeTenRptFourList(iList);
			this.setIncomeTenRptFourMap(iMap);
			if(flag) editor.openComposite("����Ԥ������Ѯ��������ʽ", true);
		}
	}

	/**
	 * ����Ԥ�������±����ѯ
	 */
	public void monthReportQuery() {
		
		// ����Ԥ�������±������ʽ
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomeMonthPath;
			
			this.setIncomeMonthList(iList);
			this.setIncomeMonthMap(iMap);
			if(flag) editor.openComposite("����Ԥ�������±������ʽ", true);
		}
		// ����Ԥ�������±�������ʽ
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomeMonthThreePath;
			
			this.setIncomeMonthThreeList(iList);
			this.setIncomeMonthThreeMap(iMap);
			if(flag) editor.openComposite("����Ԥ�������±�������ʽ", true);
		} else {
			MessageDialog.openMessageDialog(null, "����Ԥ�������±���ֻ�ж�������ʽ");
			return;
		}
	}

	/**
	 * ����Ԥ�����뼾�����ѯ
	 */
	public void quarReportQuery() {
		
		// ����Ԥ�����뼾�������ʽ
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomeQuarPath;
			
			this.setIncomeQuarList(iList);
			this.setIncomeQuarMap(iMap);
			if(flag) editor.openComposite("����Ԥ�����뼾�������ʽ", true);
		}
		// ����Ԥ�����뼾��������ʽ
		else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomeQuarThreePath;
			
			this.setIncomeQuarThreeList(iList);
			this.setIncomeQuarThreeMap(iMap);
			if(flag) editor.openComposite("����Ԥ�����뼾��������ʽ", true);
		} else {
			MessageDialog.openMessageDialog(null, "����Ԥ�����뼾����ֻ�ж�������ʽ");
			return;
		}
	}

	/**
	 * ����Ԥ�������걨���ѯ
	 */
	public void yearReportQuery() {
		
		// ����Ԥ�������걨�����ʽ
		if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
			
			filenamecmt+="_����ʽ";
			ipath=incomeYearPath;
			
			this.setIncomeYearList(iList);
			this.setIncomeYearMap(iMap);
			if(flag) editor.openComposite("����Ԥ�������걨�����ʽ", true);
		} else {
			MessageDialog.openMessageDialog(null, "����Ԥ�������걨��ֻ�ж���ʽ");
			return;
		}
	}

	/**
	 * ����Ԥ�������ѯ return list
	 * @throws ITFEBizException 
	 */
	public List budgetIncomeQueryList(TrIncomedayrptDto incomedto) throws ITFEBizException {

		
		try {
		
			//����Ǳ�����ѯ���ջ��ش��࣬�Ȳ�ѯȫϽ�����ٲ�ѯȫϽ�Ǳ����������߽�������
			if(MsgConstant.RULE_SIGN_SELF.equals(incomedto.getSbelongflag().trim())){
				if(incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_NATION_CLASS) ||
						incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_PLACE_CLASS) ||
						incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS) ||
						incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS) ||
						incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_OTHER_CLASS) ||
						incomedto.getStaxorgcode().trim().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS) ){//���ջ��ش���
					list = makemsglist(incomedto,sleSumItem, sleSubjectType, sdate); //--��2����Ƿ���ϼ� ��3����Ŀ����
				}
				else{
					list = finpaystatisticalreportService.makeIncomeBill(incomedto,sleSumItem, sleSubjectType, sdate); //--��2����Ƿ���ϼ� ��3����Ŀ����
				}
			}
			else{
				list = finpaystatisticalreportService.makeIncomeBill(incomedto,sleSumItem, sleSubjectType, sdate); //--��2����Ƿ���ϼ� ��3����Ŀ����
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
		//ȫϽ��ѯ
		List<TrIncomedayrptDto> signalllist = finpaystatisticalreportService.makeIncomeBill(idto,sleSumItem, sleSubjectType, rptDate); //--��2����Ƿ���ϼ� ��3����Ŀ����
		
		idto.setSbelongflag(MsgConstant.RULE_SIGN_ALLNOTSELF);
		//ȫϽ�Ǳ�����ѯ
		List<TrIncomedayrptDto> signallnotselflist = finpaystatisticalreportService.makeIncomeBill(idto,sleSumItem, sleSubjectType, rptDate); //--��2����Ƿ���ϼ� ��3����Ŀ����
		
		if((null == signalllist || signalllist.size() == 0) || (null == signallnotselflist || signallnotselflist.size() == 0)){
//			MessageDialog.openMessageDialog(null, "û�в�ѯ����Ӧ�ı������ݣ�");
			return null;
		}
		
		
		
		
		List<TrIncomedayrptDto> newlist = new ArrayList<TrIncomedayrptDto>();
		//��S_BUDGETSUBCODE,S_BUDGETSUBNAME�Ƚ�
		for(TrIncomedayrptDto alldto : signalllist){
			TrIncomedayrptDto newdto = new TrIncomedayrptDto();
			boolean flag = false;
			for(TrIncomedayrptDto ndto : signallnotselflist){
				if(alldto.getSbudgetsubcode()==null || alldto.getSbudgetsubcode().trim().equals("")){
					if(alldto.getSbudgetsubname().trim().equals(ndto.getSbudgetsubname().trim())){
						//N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR ���Ƚ�
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
						//N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH,N_MONEYQUARTER,N_MONEYYEAR ���Ƚ�
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
	 * Direction: ���� ename: goBack ���÷���: viewers: ����Ԥ������ͳ�Ʊ����ѯ���� messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}
	
	/**
	 * Direction: �����ļ�
	 * ename: exportFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportFile(Object o) {

    	String dirsep = File.separator; // ȡ��ϵͳ�ָ��
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
		
		MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ѱ��浽��" + filepath+copyFilename);
		
		return super.exportFile(o);
	}
    
    
    /**
	 * Direction: ����Excel�ļ�
	 * ename: exportExcelFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
	public String exportExcelFile(Object o) {
		
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		// ����ģ���ļ�·��
    	String filerootpath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
    	String copyFilename = this.sleTreCode+"_"+sdate+"_"+reporttitle+filenamecmt+"_"+(new SimpleDateFormat("ddHHmmsss").format(System.currentTimeMillis())+"R.xls");
		
		String datename=sdate.substring(0, 4) + "��"+sdate.substring(4, 6) + "��"+ sdate.substring(6, 8) + "��";
		ReportExcel.init();
		
		ReportExcel.setFilepath(filerootpath);
	
		ReportExcel.setNewfilepath(filerootpath );
		// �½���������
		ReportExcel.setCopyFilename(copyFilename);
		
		ReportExcel.setReporttitle(sleTrename + sbudgetlevelname + "��" + "Ԥ������"+ slebilltypename + "��"+"��" + sletaxorgname + "��");
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
			ReportExcel.getreportbyIncomerpt(list,offlag, sbudgettypename,sleBillType,sleBillKind,loginfo.getSuserName());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}
		
		MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ѱ��浽��" + filerootpath+copyFilename);
		
		return super.exportExcelFile(o);
	}
    
    
    
	/**
	 * Direction: ����pdf�ļ�
	 * ename: exportPdfFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportPdfFile(Object o){
        
    	String dirsep = File.separator; // ȡ��ϵͳ�ָ��
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
		
		MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ѱ��浽��" + filepath+copyFilename);
		
		return super.exportPdfFile(o);
    }

	/**
	 * ��ѯ����У��
	 */
	public boolean querycheck() {

		if (null == sleTreCode || "".equals(sleTreCode)) {
			MessageDialog.openMessageDialog(null, "��ѡ��������");
			return false;
		} else if (null == sleTaxOrgCode || "".equals(sleTaxOrgCode)) {
			MessageDialog.openMessageDialog(null, "��ѡ�����ջ��ش���");
			return false;
		} else if (null == sdate || "".equals(sdate)) {
			MessageDialog.openMessageDialog(null, "�������ѯ����");
			return false;
		} else if (null == sleBillKind || "".equals(sleBillKind)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񱨱���ʽ����");
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
				}
			}
			enumList.clear();
			// ����Ԥ�㼶�α�־
			EnumvalueDto.setStypecode("0121");
			enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			for (TdEnumvalueDto valueDto : enumList) {
				if (valueDto.getSvalue().equals(sleBudLevel)) {
					sbudgetlevelname = valueDto.getSvaluecmt();
				}
			}
			enumList.clear();
			// �������ͱ�־
			EnumvalueDto.setStypecode("0314");
			enumList = commonDataAccessService.findRsByDto(EnumvalueDto);
			for (TdEnumvalueDto valueDto : enumList) {
				if (valueDto.getSvalue().equals(sleBillType)) {
					slebilltypename = valueDto.getSvaluecmt();
				}
			}
			enumList.clear();
			// ����Ԥ�������־
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
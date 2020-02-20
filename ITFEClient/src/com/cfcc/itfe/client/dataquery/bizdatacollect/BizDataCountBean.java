package com.cfcc.itfe.client.dataquery.bizdatacollect;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.BankReportDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
/**
 * codecomment:
 * 
 * @author hua
 * @time 12-09-12 15:41:25 ��ϵͳ: DataQuery ģ��:bizDataCollect ���:BizDataCount
 */
public class BizDataCountBean extends AbstractBizDataCountBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(BizDataCountBean.class);
	private String reportPath;
	private List reportRs;
	private Map reportmap;
	private String bankReportPath;
	private List bankReportRs;
	private Map bankReportmap;
	private BigDecimal totalFamt;
	private ITFELoginInfo loginInfo;
	private List treCodeList = null;
	private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	List bizbanklist = null;


	public BizDataCountBean() {
		super();
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		param = new TipsParamDto();
		param.setStartdate(TimeFacade.getCurrentDateTime());
		param.setEnddate(TimeFacade.getCurrentDateTime());

		bizlist = new ArrayList();
		bizbanklist = new ArrayList();
		reportPath = "/com/cfcc/itfe/client/ireport/voucherDataCountReport.jasper";
		reportRs = new ArrayList();
		reportmap = new HashMap();
		paramdto = new TipsParamDto();
		paramdto.setStartdate(TimeFacade.getCurrentDateTime());
		paramdto.setEnddate(TimeFacade.getCurrentDateTime());
		bankReportPath = "/com/cfcc/itfe/client/ireport/bankCountReport.jasper";
		bankReportRs = new ArrayList();
		bankReportmap = new HashMap();
		biztype = "0";	
		param.setSbankcode("0");
		param.setPaymode("");
		param.setSbudgettype("0");
		bizlist = getEnums("0417");
		bizbanklist = getEnums("correspondentBank");//��������
		biztypelist = getEnums("0127");
		bizbudgetlist = getEnums("0122");
		setDefaultStrecode();
	}

	/**
	 * Direction: �����ӡ ename: printReport ���÷���: viewers: * messages:
	 */
	public String printReport(Object o) {
		if (StringUtils.isBlank(param.getStrecode())) {
			if ("000000000000".equals(loginInfo.getSorgcode()))
				param.setStrecode("0000000000");
			else {
				MessageDialog.openMessageDialog(null, "����������ѡ��!");
				return "";
			}
	
		}

		if (param.getStartdate() == null) {
			MessageDialog.openMessageDialog(null, "��ʼ���ڲ���Ϊ��!");
			return "";
		}
		if (param.getEnddate() == null) {
			MessageDialog.openMessageDialog(null, "�������ڲ���Ϊ��!");
			return "";
		}
		if (Integer.parseInt(TimeFacade.formatDate(param.getStartdate(),
				"yyyyMMdd")) > Integer.parseInt(TimeFacade.formatDate(param
				.getEnddate(), "yyyyMMdd"))) {
			MessageDialog.openMessageDialog(null, "��ʼ���ڲ��ܴ��ڽ������ڣ�");
			return "";
		}
		param.setSorgcode(loginInfo.getSorgcode());
		try {
			reportRs = bizDataCountService.makeReport(StringUtils
					.isBlank(biztype) ? "0" : biztype, param);
		
			if (null == reportRs || reportRs.size() == 0) {
				MessageDialog.openMessageDialog(null, "��ѯ������!");
				return "";
			}
			if ("0000000000".equals(param.getStrecode())
					&& "000000000000".equals(loginInfo.getSorgcode()))
				reportmap.put("strecode", "���й���");
			else
				reportmap.put("strecode", getStrenameByStrecode(param
						.getStrecode(), loginInfo.getSorgcode()));
			reportmap.put("printDate", new SimpleDateFormat("yyyy��MM��dd��")
					.format(new java.util.Date()));
			reportmap.put("starttime", param.getStartdate().toString());
			reportmap.put("endtime", param.getEnddate().toString());
			//reportmap.put("sbankcode", param.getSbankcode());
			//reportmap.put("paymode", param.getPaymode());
			//reportmap.put("sbudgettype", param.getSbudgettype());
			if ("0000000000".equals(param.getStrecode()))
				param.setStrecode(null);
			return super.printReport(o);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"��ѯƾ֤ҵ�������������쳣��", e));
		}
		return null;
		
	}

	/**
	 * ����Ĭ�Ϲ������
	 */
	public void setDefaultStrecode() {
		TsTreasuryDto dto = new TsTreasuryDto();
		if (!"000000000000".equals(loginInfo.getSorgcode()))
			dto.setSorgcode(loginInfo.getSorgcode());
		try {
			treCodeList=commonDataAccessService.getSubTreCode(loginInfo.getSorgcode());
			if (treCodeList == null || treCodeList.size() == 0
					&& !"000000000000".equals(loginInfo.getSorgcode())) {
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), new Exception(
						"��ǰ������������Ӧ�Ĺ����������δά����"));
				return;
			}
			if (!"000000000000".equals(loginInfo.getSorgcode()))
				param.setStrecode(((TsTreasuryDto) treCodeList.get(0)).getStrecode());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		}
	}

	/**
	 * ���ݹ��������ҹ�������
	 * 
	 * @param strecode
	 * @param sorgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public String getStrenameByStrecode(String strecode, String sorgcode)
			throws ITFEBizException {
		TsTreasuryDto dto = new TsTreasuryDto();
		dto.setStrecode(strecode);
//		dto.setSorgcode(sorgcode);
		List list = commonDataAccessService.findRsByDto(dto);
		if (list == null || list.size() == 0)
			throw new ITFEBizException("�����������" + strecode + "�ڹ���������Ϣ�����в����ڣ�");
		return ((TsTreasuryDto) list.get(0)).getStrename();
	}
	/**
	 * ����bizlist����dto��Ӧ�ı������
	 * @param o
	 * @return
	 */
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		IItfeCacheService cacheService = (IItfeCacheService) ServiceFactory
				.getService(IItfeCacheService.class);
		if(o.equals("correspondentBank")){
			List<TsConvertbanktypeDto> enumList2 = new ArrayList<TsConvertbanktypeDto>();
			try {
				TsConvertbanktypeDto dto = new TsConvertbanktypeDto();
				dto.setSorgcode(loginInfo.getSorgcode());
				enumList2 = commonDataAccessService.findRsByDto(dto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
			maplist.add(new Mapper("0", "ȫ������"));//
			for (TsConvertbanktypeDto emuDto : enumList2) {
				Mapper map2 = new Mapper(emuDto.getSbanktype(), emuDto.getSbankname());
				maplist.add(map2);
			}
		}else{
			List<TdEnumvalueDto> enumList = new ArrayList<TdEnumvalueDto>();
			try {
				enumList = cacheService.cacheEnumValueByCode(o.toString());
				
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
			if(o.equals("0417")){
			maplist.add(new Mapper("0", "ȫ��ҵ������"));
			for (TdEnumvalueDto emuDto : enumList) {
				Mapper map = new Mapper(emuDto.getSvalue(), emuDto.getSvaluecmt());
				maplist.add(map);
			}
			}else if(o.equals("0127")){
				maplist.add(new Mapper("", "ȫ��֧����ʽ"));//
				for (TdEnumvalueDto emuDto : enumList) {
					Mapper map = new Mapper(emuDto.getSvalue(), emuDto.getSvaluecmt());
					maplist.add(map);
			}
			}else if(o.equals("0122")){
				maplist.add(new Mapper("0", "ȫ��Ԥ������"));//
				for (TdEnumvalueDto emuDto : enumList) {
					Mapper map = new Mapper(emuDto.getSvalue(), emuDto.getSvaluecmt());
					maplist.add(map);
			}
			}
		}
		
		return maplist;
	}
	
	/******************************************************************************/
	public static List<Mapper> getOrgLists(String orgcode,
			ICommonDataAccessService common) {
		// ��������� ��ʾ���еĻ���
		try {
			List<Mapper> list = new ArrayList<Mapper>();
			TsOrganDto organDto = new TsOrganDto();
			if (!StateConstant.ORG_CENTER_CODE.equals(orgcode.trim())
					&& !StateConstant.STAT_CENTER_CODE.equals(orgcode.trim())) {
				organDto.setSorgcode(orgcode);
			}
			List<TsOrganDto> organList;
			organList = common.findRsByDto(organDto);
			Mapper mapper = null;
			for (TsOrganDto tmp : organList) {
				if (StateConstant.ORG_CENTER_CODE.equals(tmp.getSorgcode())
						|| StateConstant.STAT_CENTER_CODE.equals(tmp
								.getSorgcode())) {
					list.add(new Mapper("000000000000", "ȫϽ"));
					continue;
				}
				mapper = new Mapper();
				mapper.setDisplayValue(tmp.getSorgname());
				mapper.setUnderlyValue(tmp.getSorgcode());
				list.add(mapper);
			}
			return list;
		} catch (Throwable e) {
			log.error(e);
			if (e.toString().contains("HttpInvokeServletException")
					|| e.toString().contains("HttpInvokerException")) {
				MessageDialog.openMessageDialog(null,
						"�����涨ʱ��δ����ҵ��,�Ự��ʧ��\r\n�����µ�¼��");
			} else {
				MessageDialog.openMessageDialog(null, "��ȡ���������������ʧ�ܣ�");
			}
			return null;
		}
	}

	@Override
	public String printBankReport(Object o) {
		if (StringUtils.isBlank(paramdto.getStrecode())) {
			if ("000000000000".equals(loginInfo.getSorgcode()))
				paramdto.setStrecode("0000000000");
			else {
				MessageDialog.openMessageDialog(null, "����������ѡ��!");
				return "";
			}
		}
		if (paramdto.getStartdate() == null) {
			MessageDialog.openMessageDialog(null, "��ʼ���ڲ���Ϊ��!");
			return "";
		}
		if (paramdto.getEnddate() == null) {
			MessageDialog.openMessageDialog(null, "�������ڲ���Ϊ��!");
			return "";
		}
		if (Integer.parseInt(TimeFacade.formatDate(paramdto.getStartdate(),
				"yyyyMMdd")) > Integer.parseInt(TimeFacade.formatDate(param
				.getEnddate(), "yyyyMMdd"))) {
			MessageDialog.openMessageDialog(null, "��ʼ���ڲ��ܴ��ڽ������ڣ�");
			return "";
		}
		paramdto.setSorgcode(loginInfo.getSorgcode());
		try {
			bankReportRs = bizDataCountService.makeBankReport(paramdto);
			if (null == bankReportRs || bankReportRs.size() == 0) {
				MessageDialog.openMessageDialog(null, "��ѯ������!");
				return "";
			} else {
				bankReportmap.put("strecode", getStrenameByStrecode(paramdto.getStrecode(), null));
				bankReportmap.put("printDate", new SimpleDateFormat(
						"yyyy��MM��dd��").format(new java.util.Date()));
				bankReportmap.put("starttime", paramdto.getStartdate().toString());
				bankReportmap.put("endtime", paramdto.getEnddate().toString());
				bankReportmap.put("total", "�ϼ�");
				int totaldirectpaynum=0;
				BigDecimal totaldirectpayamt=new BigDecimal(0.00);
				int totalpayoutnum=0;
				BigDecimal totalpayoutamt=new BigDecimal(0.00);
				int totalgrantpaynum=0;
				BigDecimal totalgrantpayamt=new BigDecimal(0.00);
				int totalworkcardnum=0;
				BigDecimal totalworkcardamt=new BigDecimal(0.00);
				int totaltotalnum=0;
				BigDecimal totaltotalamt=new BigDecimal(0.00);
				for (int i = 0; i < bankReportRs.size(); i++) {
					 BankReportDto bankReportDto=(BankReportDto) bankReportRs.get(i);
					 totaldirectpaynum=totaldirectpaynum+bankReportDto.getDirectpaynum();
					 totaldirectpayamt=totaldirectpayamt.add(bankReportDto.getDirectpayamt());
					 totalpayoutnum=totalpayoutnum+bankReportDto.getPayoutnum();
					 totalpayoutamt=totalpayoutamt.add(bankReportDto.getPayoutamt());
					 totalgrantpaynum=totalgrantpaynum+bankReportDto.getGrantpaynum();
					 totalgrantpayamt=totalgrantpayamt.add(bankReportDto.getGrantpayamt());
					 totalworkcardnum=totalworkcardnum+bankReportDto.getWorkcardnum();
					 totalworkcardamt=totalworkcardamt.add(bankReportDto.getWorkcardamt());
					 totaltotalnum=totaltotalnum+bankReportDto.getTotalnum();
					 totaltotalamt=totaltotalamt.add(bankReportDto.getTotalamt());
				}
				bankReportmap.put("totaldirectpaynum",totaldirectpaynum);
				bankReportmap.put("totaldirectpayamt", totaldirectpayamt);
				bankReportmap.put("totalpayoutnum", totalpayoutnum);
				bankReportmap.put("totalpayoutamt", totalpayoutamt);
				bankReportmap.put("totalgrantpaynum", totalgrantpaynum);
				bankReportmap.put("totalgrantpayamt", totalgrantpayamt);
				bankReportmap.put("totalworkcardnum", totalworkcardnum);
				bankReportmap.put("totalworkcardamt", totalworkcardamt);
				bankReportmap.put("totaltotalnum", totaltotalnum);
				bankReportmap.put("totaltotalamt", totaltotalamt);
			}
			return super.printBankReport(o);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"��ѯƾ֤ҵ�������������쳣��", e));
		}

		return null;
	}
@Override
public String exportBankReport(Object o) {
	DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
			.getActiveShell());
	String filePath = path.open();
	List<String> filelist = new ArrayList<String>();
	if ((null == filePath) || (filePath.length() == 0)) {
		MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
		return "";
	}
	String clientFilePath="";//�ͻ����ļ�����·��
	String dirsep = File.separator;
	String serverFilePath;//������ļ�·��
	try {
		serverFilePath = bizDataCountService.exportFile(paramdto).replace("\\","/");
		int j = serverFilePath.lastIndexOf("/");
		//��ȡ�����ļ���
		String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(0, j + 1), "");
		clientFilePath = filePath + dirsep + partfilepath;
		File f = new File(clientFilePath);
		File dir = new File(f.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
		MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n"	+ clientFilePath);
	} catch (Exception e) {
		MessageDialog.openErrorDialog(null, e);
	}
    return "";
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

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public List getReportRs() {
		return reportRs;
	}

	public void setReportRs(List reportRs) {
		this.reportRs = reportRs;
	}

	public Map getReportmap() {
		return reportmap;
	}

	public void setReportmap(Map reportmap) {
		this.reportmap = reportmap;
	}

	public String getBankReportPath() {
		return bankReportPath;
	}

	public void setBankReportPath(String bankReportPath) {
		this.bankReportPath = bankReportPath;
	}

	public List getBankReportRs() {
		return bankReportRs;
	}

	public void setBankReportRs(List bankReportRs) {
		this.bankReportRs = bankReportRs;
	}

	public Map getBankReportmap() {
		return bankReportmap;
	}

	public void setBankReportmap(Map bankReportmap) {
		this.bankReportmap = bankReportmap;
	}

	public List getBizbanklist() {
		return bizbanklist;
	}

	public void setBizbanklist(List bizbanklist) {
		this.bizbanklist = bizbanklist;
	}

	public List getTreCodeList() {
		return treCodeList;
	}

	public void setTreCodeList(List treCodeList) {
		this.treCodeList = treCodeList;
	}

}
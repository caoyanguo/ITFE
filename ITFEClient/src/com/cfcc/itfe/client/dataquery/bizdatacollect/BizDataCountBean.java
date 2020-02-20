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
 * @time 12-09-12 15:41:25 子系统: DataQuery 模块:bizDataCollect 组件:BizDataCount
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
		bizbanklist = getEnums("correspondentBank");//代理银行
		biztypelist = getEnums("0127");
		bizbudgetlist = getEnums("0122");
		setDefaultStrecode();
	}

	/**
	 * Direction: 报表打印 ename: printReport 引用方法: viewers: * messages:
	 */
	public String printReport(Object o) {
		if (StringUtils.isBlank(param.getStrecode())) {
			if ("000000000000".equals(loginInfo.getSorgcode()))
				param.setStrecode("0000000000");
			else {
				MessageDialog.openMessageDialog(null, "国库代码必须选择!");
				return "";
			}
	
		}

		if (param.getStartdate() == null) {
			MessageDialog.openMessageDialog(null, "开始日期不能为空!");
			return "";
		}
		if (param.getEnddate() == null) {
			MessageDialog.openMessageDialog(null, "结束日期不能为空!");
			return "";
		}
		if (Integer.parseInt(TimeFacade.formatDate(param.getStartdate(),
				"yyyyMMdd")) > Integer.parseInt(TimeFacade.formatDate(param
				.getEnddate(), "yyyyMMdd"))) {
			MessageDialog.openMessageDialog(null, "开始日期不能大于结束日期！");
			return "";
		}
		param.setSorgcode(loginInfo.getSorgcode());
		try {
			reportRs = bizDataCountService.makeReport(StringUtils
					.isBlank(biztype) ? "0" : biztype, param);
		
			if (null == reportRs || reportRs.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询无数据!");
				return "";
			}
			if ("0000000000".equals(param.getStrecode())
					&& "000000000000".equals(loginInfo.getSorgcode()))
				reportmap.put("strecode", "所有国库");
			else
				reportmap.put("strecode", getStrenameByStrecode(param
						.getStrecode(), loginInfo.getSorgcode()));
			reportmap.put("printDate", new SimpleDateFormat("yyyy年MM月dd日")
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
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"查询凭证业务量操作出现异常！", e));
		}
		return null;
		
	}

	/**
	 * 设置默认国库代码
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
						"当前核算主体代码对应的国库主体代码未维护！"));
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
	 * 根据国库代码查找国库名称
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
			throw new ITFEBizException("国库主体代码" + strecode + "在国库主体信息参数中不存在！");
		return ((TsTreasuryDto) list.get(0)).getStrename();
	}
	/**
	 * 根据bizlist查找dto对应的表的数据
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
			maplist.add(new Mapper("0", "全部银行"));//
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
			maplist.add(new Mapper("0", "全部业务类型"));
			for (TdEnumvalueDto emuDto : enumList) {
				Mapper map = new Mapper(emuDto.getSvalue(), emuDto.getSvaluecmt());
				maplist.add(map);
			}
			}else if(o.equals("0127")){
				maplist.add(new Mapper("", "全部支付方式"));//
				for (TdEnumvalueDto emuDto : enumList) {
					Mapper map = new Mapper(emuDto.getSvalue(), emuDto.getSvaluecmt());
					maplist.add(map);
			}
			}else if(o.equals("0122")){
				maplist.add(new Mapper("0", "全部预算种类"));//
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
		// 如果是中心 显示所有的机构
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
					list.add(new Mapper("000000000000", "全辖"));
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
						"超过规定时间未操作业务,会话已失败\r\n请重新登录！");
			} else {
				MessageDialog.openMessageDialog(null, "获取核算主体代码数据失败！");
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
				MessageDialog.openMessageDialog(null, "国库代码必须选择!");
				return "";
			}
		}
		if (paramdto.getStartdate() == null) {
			MessageDialog.openMessageDialog(null, "开始日期不能为空!");
			return "";
		}
		if (paramdto.getEnddate() == null) {
			MessageDialog.openMessageDialog(null, "结束日期不能为空!");
			return "";
		}
		if (Integer.parseInt(TimeFacade.formatDate(paramdto.getStartdate(),
				"yyyyMMdd")) > Integer.parseInt(TimeFacade.formatDate(param
				.getEnddate(), "yyyyMMdd"))) {
			MessageDialog.openMessageDialog(null, "开始日期不能大于结束日期！");
			return "";
		}
		paramdto.setSorgcode(loginInfo.getSorgcode());
		try {
			bankReportRs = bizDataCountService.makeBankReport(paramdto);
			if (null == bankReportRs || bankReportRs.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询无数据!");
				return "";
			} else {
				bankReportmap.put("strecode", getStrenameByStrecode(paramdto.getStrecode(), null));
				bankReportmap.put("printDate", new SimpleDateFormat(
						"yyyy年MM月dd日").format(new java.util.Date()));
				bankReportmap.put("starttime", paramdto.getStartdate().toString());
				bankReportmap.put("endtime", paramdto.getEnddate().toString());
				bankReportmap.put("total", "合计");
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
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"查询凭证业务量操作出现异常！", e));
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
		MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
		return "";
	}
	String clientFilePath="";//客户端文件保存路径
	String dirsep = File.separator;
	String serverFilePath;//服务端文件路径
	try {
		serverFilePath = bizDataCountService.exportFile(paramdto).replace("\\","/");
		int j = serverFilePath.lastIndexOf("/");
		//获取服务文件名
		String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(0, j + 1), "");
		clientFilePath = filePath + dirsep + partfilepath;
		File f = new File(clientFilePath);
		File dir = new File(f.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
		MessageDialog.openMessageDialog(null, "导出文件成功！文件保存在：\n"	+ clientFilePath);
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
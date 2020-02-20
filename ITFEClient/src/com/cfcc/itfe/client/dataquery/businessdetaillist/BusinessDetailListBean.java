package com.cfcc.itfe.client.dataquery.businessdetaillist;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.recbiz.banknameenter.ShowCompositeVoucherOcx;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositevoucherCheckAccountQuery;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositevoucherVoucherViewer;
import com.cfcc.itfe.client.sendbiz.bizcertsend.AsspOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FundGrantCustomDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.service.recbiz.voucherload.IVoucherLoadService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author renqingbin
 * @time 14-03-31 14:48:39 子系统: DataQuery 模块:BusinessDetailList
 *       组件:BusinessDetailList
 */
public class BusinessDetailListBean extends AbstractBusinessDetailListBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(BusinessDetailListBean.class);
	private IItfeCacheService cacheService = (IItfeCacheService)ServiceFactory.getService(IItfeCacheService.class);
	protected IVoucherLoadService voucherLoadService = (IVoucherLoadService)getService(IVoucherLoadService.class);
	
	
	private List voucherViewersList;
	private String reportPath;
	private List reportRs;
	private Map reportmap;
	private BigDecimal totalFamt;
	private ITFELoginInfo loginInfo;
	// 国库代码列表
	private List<TsTreasuryDto> treList = new ArrayList<TsTreasuryDto>();
	private Boolean directPlan;
	private Boolean grantPlan;
	private Boolean payOut;
	private Boolean directPayMoney;
	private Boolean directBckMoney;
	private Boolean grantPayMoney;
	private Boolean grantBckMoney;
	private Boolean dwbk;
	private Boolean directPlan1;
	private Boolean grantPlan1;
	private Boolean payOut1;
	private Boolean payOutBack;
	private Boolean directPayMoney1;
	private Boolean directBckMoney1;
	private Boolean grantPayMoney1;
	private Boolean grantBckMoney1;
	private Boolean dwbk1;
	private Boolean dwbkBack;
	PagingContext pagingcontext;
	List<TvVoucherinfoDto> list;
	private String tmpPath1;
	private String tmpPath11;
	private String tmpPath2;
	private String tmpPath3;
	private String tmpPath4;
	private String tmpPath5;
	private String tmpPath6;
	private String tmpPath66;
	private String tmpPath2Detail;
	private String tmpPath3Detail;
	private String tmpPath22Detail;
	private String tmpPath33Detail;
	private String tmpPath4Detail;
	private String tmpPath44Detail;
	private String tmpPath5Detail;
	private String tmpPath55Detail;
	private List<Mapper> bankCodeList;
	private List<Mapper> bankCodeList1;
	private String month = null;
    private List monthlist = null;
    private List datelist = null;
    private String datatype = null;
//	Mapper map3 = new Mapper(MsgConstant.VOUCHER_NO_3510, "清算额度对账3510");
//	subVoucherList.add(map);
	public BusinessDetailListBean() {
		super();
		voucherViewersList = new ArrayList();
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		param = new TipsParamDto();
		searchDto = new TipsParamDto();
		param.setStartdate(TimeFacade.getCurrentDateTime());
		param.setEnddate(TimeFacade.getCurrentDateTime());
		searchDto.setStartdate(TimeFacade.getCurrentDateTime());
		searchDto.setEnddate(TimeFacade.getCurrentDateTime());
		reportPath = "/com/cfcc/itfe/client/ireport/businessDetailList.jasper";
		reportRs = new ArrayList();
		reportmap = new HashMap();
		pagingcontext = new PagingContext(this);
		biztype = "0";
		directPlan = Boolean.TRUE;
		grantPlan = Boolean.TRUE;
		payOut = Boolean.TRUE;
		directPayMoney = Boolean.TRUE;
		directBckMoney = Boolean.TRUE;
		grantPayMoney = Boolean.TRUE;
		grantBckMoney = Boolean.TRUE;
		dwbk = Boolean.TRUE;
		directPlan1 = Boolean.FALSE;
		grantPlan1 = Boolean.FALSE;
		payOut1 = Boolean.TRUE;
		payOutBack = Boolean.FALSE;
		directPayMoney1 = Boolean.FALSE;
		directBckMoney1 = Boolean.FALSE;
		grantPayMoney1 = Boolean.FALSE;
		grantBckMoney1 = Boolean.FALSE;
		dwbk1 = Boolean.FALSE;
		dwbkBack = Boolean.FALSE;
		tmpPath1 = "/com/cfcc/itfe/client/ireport/fundGrant.jasper";
		tmpPath11 = "/com/cfcc/itfe/client/ireport/fundGrant1.jasper";
		tmpPath2 = "/com/cfcc/itfe/client/ireport/directPayOut.jasper";
		tmpPath3 = "/com/cfcc/itfe/client/ireport/grantPayOut.jasper";
		tmpPath4 = "/com/cfcc/itfe/client/ireport/comcialBnkTransferMoney.jasper";
		tmpPath5 = "/com/cfcc/itfe/client/ireport/comcialBnkDrawback.jasper";
		tmpPath2Detail = "/com/cfcc/itfe/client/ireport/directPayOutDetail.jasper";
		tmpPath3Detail = "/com/cfcc/itfe/client/ireport/grantPayOutDetail.jasper";
		tmpPath4Detail = "/com/cfcc/itfe/client/ireport/comcialBnkTransferMoneyDetail.jasper";
		tmpPath5Detail = "/com/cfcc/itfe/client/ireport/comcialBnkDrawbackDetail.jasper";
		tmpPath22Detail = "/com/cfcc/itfe/client/ireport/directPayOutDetail1.jasper";
		tmpPath33Detail = "/com/cfcc/itfe/client/ireport/grantPayOutDetail1.jasper";
		tmpPath44Detail = "/com/cfcc/itfe/client/ireport/comcialBnkTransferMoneyDetail1.jasper";
		tmpPath55Detail = "/com/cfcc/itfe/client/ireport/comcialBnkDrawbackDetail1.jasper";
		tmpPath6 = "/com/cfcc/itfe/client/ireport/drawbackIncome.jasper";
		tmpPath66 = "/com/cfcc/itfe/client/ireport/drawbackIncome1.jasper";
		param.setDatatype("1");
		searchDto.setDatatype("1");
		init();
	}

	private void init() {
		try {
			// 查询国库代码用dto
			TsTreasuryDto tredto = new TsTreasuryDto();
			tredto.setSorgcode(loginInfo.getSorgcode());
			treList = commonDataAccessService.findRsByDto(tredto);
			// 初始化国库代码默认值
			if (treList.size() > 0) {
				param.setStrecode(treList.get(0).getStrecode());
				searchDto.setStrecode(treList.get(0).getStrecode());
			}
			if(bankCodeList==null||bankCodeList.size()<=0||bankCodeList1==null||bankCodeList1.size()<=0)
			{
				TsConvertbanktypeDto finddto = new TsConvertbanktypeDto();
				finddto.setSorgcode(loginInfo.getSorgcode());
					List<TsConvertbanktypeDto> resultList = commonDataAccessService.findRsByDto(finddto);
					if(resultList!=null&&resultList.size()>0)
					{
						Mapper map = null;
						bankCodeList = new ArrayList<Mapper>();
						bankCodeList1 = new ArrayList<Mapper>();
						for(TsConvertbanktypeDto temp:resultList)
						{
							 map = new Mapper(temp.getSbankcode(), temp.getSbankname());
							 bankCodeList.add(map);
							 bankCodeList1.add(map);
						}
					}
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		searchDto.setSbeflag("80000");
	}

	/**
	 * Direction: 报表打印 ename: printReport 引用方法: viewers: 报表显示界面 messages:
	 */
	public String printReport(Object o) {
		editor.fireModelChanged();
		reportRs.clear();
		reportmap.clear();
		reportPath = "/com/cfcc/itfe/client/ireport/businessDetailList.jasper";
		if (selQueryWhere(param) == 1) {
			return null;
		}
		try {
			reportRs = businessDetailListService.makeReport(judSleBiztype(),
					param);
			if (null == reportRs || reportRs.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询无数据!");
				return "";
			}
			reportmap.put("strecode", getStrenameByStrecode(
					param.getStrecode(), loginInfo.getSorgcode()));
			reportmap.put("printDate", new SimpleDateFormat("yyyy年MM月dd日")
					.format(new java.util.Date()));
			reportmap.put("starttime", param.getStartdate().toString());
			reportmap.put("endtime", param.getEnddate().toString());
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
		return super.printReport(o);
	}
	
	/**
	 * Direction: 代理库清单查询打印
	 * ename: agentNatiTrePrintRpt
	 * 引用方法: 
	 * viewers: 报表显示界面
	 * messages: 
	 */
    public String agentNatiTrePrintRpt(Object o){
    	editor.fireModelChanged();
		reportRs.clear();
		reportmap.clear();
		reportPath = "/com/cfcc/itfe/client/ireport/agetTreListRtp.jasper";
		if (selQueryWhere(param) == 1) {
			return null;
		}
		try {
			reportRs = businessDetailListService.makeReport(judSleBiztype(),param);
			if (null == reportRs || reportRs.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询无数据!");
				return "";
			}
			reportmap.put("strecode", getStrenameByStrecode(param.getStrecode(), loginInfo.getSorgcode()));
			reportmap.put("printDate", new SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date()));
			reportmap.put("starttime", param.getStartdate().toString());
			reportmap.put("endtime", param.getEnddate().toString());
			return super.printReport(o);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), new Exception("查询凭证业务量操作出现异常！", e));
		}
        return super.agentNatiTrePrintRpt(o);
    }
    
	/**
	 * Direction: 集中支付对账单打印
	 * ename: printcenterpay
	 * 引用方法: 
	 * viewers: 报表显示界面
	 * messages: 
	 */
    public String printcenterpay(Object o){
    	editor.fireModelChanged();
		reportRs.clear();
		reportmap.clear();
		reportPath = "/com/cfcc/itfe/client/ireport/businessDetailCount.jasper";
		if (StringUtils.isBlank(param.getStrecode())) {
			MessageDialog.openMessageDialog(null, "国库代码必须选择!");
			return null;
		}
		if (param.getStartdate() == null || param.getEnddate() == null) {
			MessageDialog.openMessageDialog(null, "开始日期和结束日期不能为空!");
			return null;
		}
		if (Integer.parseInt(TimeFacade.formatDate(param.getStartdate(),
				"yyyyMMdd")) > Integer.parseInt(TimeFacade.formatDate(param
				.getEnddate(), "yyyyMMdd"))) {
			MessageDialog.openMessageDialog(null, "开始日期不能大于结束日期！");
			return null;
		}
		if(param.getSbankcode()==null||"".equals(param.getSbankcode()))
		{
			MessageDialog.openMessageDialog(null, "请选择代理银行！");
			return null;
		}
		param.setSorgcode(loginInfo.getSorgcode());
		param.setExptype("17999");
		String bankname = "";
		if(bankCodeList!=null&&bankCodeList.size()>0)
		{
			for(Mapper mapper : bankCodeList)
			{
				if(mapper.getUnderlyValue().equals(param.getSbankcode()))
				{
					bankname = String.valueOf(mapper.getDisplayValue());
					break;
				}
			}
		}
		try {
			reportRs = businessDetailListService.makeReport(judSleBiztype(),
					param);
			if (null == reportRs || reportRs.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询无数据!");
				return "";
			}
			String yearmonth = TimeFacade.formatDate(param.getEnddate(),"yyyyMMdd");
			reportmap.put("strecode", getStrenameByStrecode(
					param.getStrecode(), loginInfo.getSorgcode()));
			reportmap.put("printDate", new SimpleDateFormat("yyyy-MM-dd")
					.format(new java.util.Date()));
			reportmap.put("starttime", param.getStartdate().toString());
			reportmap.put("endtime", param.getEnddate().toString());
			reportmap.put("bankname", bankname);
			reportmap.put("yearmonth", yearmonth.substring(0,4)+"年"+yearmonth.substring(4,6)+"月");
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
        return super.printcenterpay(o);
    }
	/**
	 * Direction: 回到查询界面 ename: backToQuery 引用方法: viewers: 无纸化业务清单打印 messages:
	 */
	public String backToQuery(Object o) {
		return super.backToQuery(o);
	}

	/**
	 * 
	 * 引用方法: viewers: 无纸化业务清单打印 messages:
	 */
	public String jug(Object o) {
		return super.backToQuery(o);
	}

	/**
	 * Direction: 原始凭证打印 ename: printOriVou 引用方法: viewers: 原始凭证打印统计 messages:
	 */
	public String printOriVou(Object o) {
		editor.fireModelChanged();
		if (selQueryWhere(param) == 1) {
			return null;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " 查询无记录！");
			return null;
		}
		// 获取分类记录情况
		list = pageResponse.getData();
		return super.printOriVou(o);
	}

	/**
	 * Direction: 批量打印 ename: batchPrint 引用方法: viewers: * messages:
	 */
	public String batchPrint(Object o) {

		AsspOcx ocx = new AsspOcx(new Shell());
		HashMap<String, String> map = new HashMap<String, String>();
		//判断是否有状态为不成功的的记录
		for (TvVoucherinfoDto _dto : list) {
			String status = _dto.getSstatus();
			//有处理失败的给出提示用户是否打印
			if (DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(status) || DealCodeConstants.VOUCHER_CHECKFAULT.equals(status)|| DealCodeConstants.VOUCHER_FAIL_TIPS.equals(status) || DealCodeConstants.VOUCHER_FAIL_TCBS.equals(status)) {
				org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
						editor.getContainer().getShell(), "打印提示", null, "选择的记录中有【校验失败】或【处理失败】的记录，是否继续打印?",
						org.eclipse.jface.dialogs.MessageDialog.QUESTION,
						new String[] { "是", "否" }, 0);
				if(msg.open()==org.eclipse.jface.dialogs.MessageDialog.CANCEL){
					return null;
				}
			}
		}
		
		try {
			List <TvVoucherinfoDto> tmplist  = businessDetailListService.makeSumCountRecord(judSleBiztype(), param);
			//判断打印次数
			
             //			
			List <TvVoucherinfoDto> tlist = new  ArrayList<TvVoucherinfoDto>();
				// 分按业务类型分组打印
				for (TvVoucherinfoDto tmp : tmplist) {
					String vtcode = tmp.getSvtcode();
					String svouno = tmp.getSvoucherno();
				    if (map.containsKey(vtcode)) {
				    	map.put(vtcode,map.get(vtcode)+","+svouno);
					}else{
						map.put(vtcode,svouno);
						tlist.add(tmp);
					}
				}
			//按业务类型分别打印
				for (TvVoucherinfoDto tdto : tlist) {
					String vtcode = tdto.getSvtcode();
					int year = Integer.valueOf(tdto.getSstyear());
					String admDivCode = tdto.getSadmdivcode();
					String voucherNos = map.get(vtcode);
					ocx.printVoucher("001", admDivCode, year, vtcode,
							voucherNos);
				}
		        
		     
		} catch (ITFEBizException e) {
			log.error("批量打印出错！",e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        MessageDialog.openMessageDialog(null, "打印完毕！");
		return "";
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
		dto.setSorgcode(sorgcode);
		List list = commonDataAccessService.findRsByDto(dto);
		if (list == null || list.size() == 0)
			throw new ITFEBizException("国库主体代码" + strecode + "在国库主体信息参数中不存在！");
		return ((TsTreasuryDto) list.get(0)).getStrename();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {
		try {
			if (selQueryWhere(param) == 1) {
				return null;
			}
			return businessDetailListService.makestatReport(judSleBiztype(),
					param, arg0);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return null;

	}

	/**
	 * 判断选择的业务类型组成where 语句
	 * 
	 * @return
	 */
	private String judSleBiztype() {
		String sqland = "('";
		if (directPlan) {
			sqland += MsgConstant.VOUCHER_NO_5108 + "','";
		}
		;
		if (grantPlan) {
			sqland += MsgConstant.VOUCHER_NO_5106 + "','";
		}
		;
		if (payOut) {
			sqland += MsgConstant.VOUCHER_NO_5207 + "','";
		}
		;
		if (directPayMoney||grantPayMoney) {
			sqland += MsgConstant.VOUCHER_NO_2301 + "','";
			if(directPayMoney&&grantPayMoney)
			{
				param.setPaymode(null);
			}else
			{
				if(directPayMoney)
					param.setPaymode("0");//直接支付
				else
					param.setPaymode("1");//授权支付
			}
		}
		;
		if (directBckMoney||grantBckMoney) {
			sqland += MsgConstant.VOUCHER_NO_2302 + "','";
			if(directBckMoney&&grantBckMoney)
			{
				param.setPaymode(null);
			}else
			{
				if(directBckMoney)
					param.setPaymode("0");//直接支付
				else
					param.setPaymode("1");//授权支付
			}
		}
		;
		if (dwbk) {
			sqland += MsgConstant.VOUCHER_NO_5209 + "','";
		}
		;
		sqland += "N')";
		return sqland;

	}
	
	/**
	 * 判断选择的业务类型组成where 语句
	 * 
	 * @return
	 */
	private String chooseBiztype() {
		String sqland = "('";
		if (directPlan1) {
			sqland += MsgConstant.VOUCHER_NO_5108 + "','";
		}
		if (grantPlan1) {
			sqland += MsgConstant.VOUCHER_NO_5106 + "','";
		}
		if (payOut1) {
			sqland += MsgConstant.VOUCHER_NO_5207 + "','";
		}
		if(payOutBack)
		{
			sqland += MsgConstant.VOUCHER_NO_3208 + "','";
		}
		if (directPayMoney1||grantPayMoney1) {
			sqland += MsgConstant.VOUCHER_NO_2301 + "','";
			if (directPayMoney1&&grantPayMoney1)
			{
				searchDto.setPaymode(null);
			}else
			{
				if(directPayMoney1)
					searchDto.setPaymode("0");//直接支付
				else
					searchDto.setPaymode("1");//授权支付
			}
		}
		if (directBckMoney1||grantBckMoney1) {
			sqland += MsgConstant.VOUCHER_NO_2302 + "','";
			if (directBckMoney1&&grantBckMoney1)
			{
				searchDto.setPaymode(null);
			}else
			{
				if(directBckMoney1)
					searchDto.setPaymode("0");//直接支付
				else
					searchDto.setPaymode("1");//授权支付
			}
		}
		if (dwbk1) {
			sqland += MsgConstant.VOUCHER_NO_5209 + "','";
		}
		if(dwbkBack)
		{
			sqland += MsgConstant.VOUCHER_NO_3210 + "','";
		}
		sqland += "N')";
		return sqland;

	}

	/**
	 * 查询条件判断
	 * 
	 * @return
	 */
	private int selQueryWhere(TipsParamDto param) {
		if (StringUtils.isBlank(param.getStrecode())) {
			MessageDialog.openMessageDialog(null, "国库代码必须选择!");
			return 1;
		}
		if (param.getStartdate() == null || param.getEnddate() == null) {
			MessageDialog.openMessageDialog(null, "开始日期和结束日期不能为空!");
			return 1;
		}
		if (Integer.parseInt(TimeFacade.formatDate(param.getStartdate(),
				"yyyyMMdd")) > Integer.parseInt(TimeFacade.formatDate(param
				.getEnddate(), "yyyyMMdd"))) {
			MessageDialog.openMessageDialog(null, "开始日期不能大于结束日期！");
			return 1;
		}
		param.setSorgcode(loginInfo.getSorgcode());
		String name = this.editor.getCurrentPage().name;
		if("无纸化业务凭证打印".equals(name)){
			if (judSleBiztype().length() < 5) {
				MessageDialog.openMessageDialog(null, "请选择要打印的业务类型！");
				return 1;
			}
		}else if("无纸化业务清单打印".equals(name)){
			if (chooseBiztype().length() <= 5) {
				MessageDialog.openMessageDialog(null, "请选择要打印的业务类型！");
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * Direction: 查询打印主单信息
	 * ename: toReport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String toReport(Object o){
    	if (selQueryWhere(searchDto) == 1) {
			return null;
		}
    	if (chooseBiztype().length() > 12) {
			MessageDialog.openMessageDialog(null, "只能选择一种要打印的业务类型！");
			return null;
		}
    	reportRs.clear();
    	reportmap.clear();
		try {
			reportRs = businessDetailListService.findRsForMain(chooseBiztype(),searchDto);
			if (null == reportRs || reportRs.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询无数据!");
				return null;
			}
			String typename = "";
			if(directBckMoney1||directPayMoney1)
				typename = "直接";
			else if(grantBckMoney1||grantPayMoney1)
				typename = "授权";
			reportmap.put("strecode", getStrenameByStrecode(searchDto.getStrecode(), loginInfo.getSorgcode()));
			reportmap.put("printDate", new SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date()));
			reportmap.put("starttime", searchDto.getStartdate().toString());
			reportmap.put("endtime", searchDto.getEnddate().toString());
			reportmap.put("acctDate",loginInfo.getCurrentDate().toString());
			reportmap.put("typename",typename);
			for(int i=0;i<reportRs.size();i++){
				FundGrantCustomDto dto = (FundGrantCustomDto)reportRs.get(i);
				List<TdEnumvalueDto> list = cacheService.cacheEnumValueByCode("0300");
				for(int j=0;j<list.size();j++){
					if(list.get(j).getSvalue().equals(dto.getSstatus())){
						dto.setSstatus(list.get(j).getSvaluecmt());
					}
				}
			}
			if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5108)){
				reportPath = tmpPath2;
				return "直接支付额度报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5106)){
				reportPath = tmpPath3;
				return "授权支付额度报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5207)){
				reportPath = tmpPath1;
				reportmap.put("reporttitle", "实拨拨款");
				return "实拨资金业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_3208)){
				reportPath = tmpPath1;
				reportmap.put("reporttitle", "实拨退款");
				return "实拨资金业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5209)){
				reportPath = tmpPath6;
				return "收入退付业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_3210)){
				reportPath = tmpPath6;
				return "收入退付退款业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_2301)){
				reportPath = tmpPath4;
				return "商行支付划款报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_2302)){
				reportPath = tmpPath5;
				return "商行支付退款报表";
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return "";
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			return "";
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"查询凭证业务量操作出现异常！", e));
			return "";
		}
        return "实拨资金业务报表";
    }
    /**
	 * Direction: 查询打印明细信息
	 * ename: toReport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String toReportOfDetail(Object o){
    	if (selQueryWhere(searchDto) == 1) {
			return null;
		}
    	if (chooseBiztype().length() > 12) {
			MessageDialog.openMessageDialog(null, "只能选择一种要打印的业务类型！");
			return null;
		}
    	reportRs.clear();
    	reportmap.clear();
		try {
			reportRs = businessDetailListService.findRsForDetail(chooseBiztype(),searchDto);
			if (null == reportRs || reportRs.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询无数据!");
				return null;
			}
			reportmap.put("strecode", getStrenameByStrecode(searchDto.getStrecode(), loginInfo.getSorgcode()));
			reportmap.put("printDate", new SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date()));
			reportmap.put("starttime", searchDto.getStartdate().toString());
			reportmap.put("endtime", searchDto.getEnddate().toString());
			reportmap.put("acctDate",loginInfo.getCurrentDate().toString());
			BigDecimal allamt = new BigDecimal(0);
			String vouno = "";
			for(int i=0;i<reportRs.size();i++){
				FundGrantCustomDto dto = (FundGrantCustomDto)reportRs.get(i);
				List<TdEnumvalueDto> list = cacheService.cacheEnumValueByCode("0300");
				for(int j=0;j<list.size();j++){
					if(list.get(j).getSvalue().equals(dto.getSstatus())){
						dto.setSstatus(list.get(j).getSvaluecmt());
					}
				}
				if(!vouno.equals(dto.getVouno()))
					allamt = allamt.add(dto.getNmoney());
				vouno = dto.getVouno();
			}
			reportmap.put("allamt", allamt);
			if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5108)){
				reportPath = tmpPath2Detail;
				return "直接支付额度明细报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5106)){
				reportPath = tmpPath3Detail;
				return "授权支付额度明细报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5207)){
				reportPath = tmpPath1;
				reportmap.put("reporttitle", "实拨拨款");
				return "实拨资金业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_3208)){
				reportPath = tmpPath1;
				reportmap.put("reporttitle", "实拨退款");
				return "实拨资金业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5209)){
				reportPath = tmpPath6;
				return "收入退付业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5209)){
				reportPath = tmpPath6;
				return "收入退付退款业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_2301)){
				reportPath = tmpPath4Detail;
				return "商行支付划款明细报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_2302)){
				reportPath = tmpPath5Detail;
				return "商行支付退款明细报表";
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"查询凭证业务量操作出现异常！", e));
		}
        return "实拨资金业务报表";
    }
    /**
	 * Direction: 代理库打印明细信息
	 * ename: toReportDetailOfDlk
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String toReportDetailOfDlk(Object o){
    	if (selQueryWhere(searchDto) == 1) {
			return null;
		}
    	if (chooseBiztype().length() > 12) {
			MessageDialog.openMessageDialog(null, "只能选择一种要打印的业务类型！");
			return null;
		}
    	reportRs.clear();
    	reportmap.clear();
		try {
			reportRs = businessDetailListService.findRsForDetail(chooseBiztype(),searchDto);
			if (null == reportRs || reportRs.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询无数据!");
				return null;
			}
			reportmap.put("strecode", getStrenameByStrecode(searchDto.getStrecode(), loginInfo.getSorgcode()));
			reportmap.put("printDate", new SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date()));
			reportmap.put("starttime", searchDto.getStartdate().toString());
			reportmap.put("endtime", searchDto.getEnddate().toString());
			reportmap.put("acctDate",loginInfo.getCurrentDate().toString());
			BigDecimal allamt = new BigDecimal(0);
			String vouno = "";
			for(int i=0;i<reportRs.size();i++){
				FundGrantCustomDto dto = (FundGrantCustomDto)reportRs.get(i);
				List<TdEnumvalueDto> list = cacheService.cacheEnumValueByCode("0300");
				for(int j=0;j<list.size();j++){
					if(list.get(j).getSvalue().equals(dto.getSstatus())){
						dto.setSstatus(list.get(j).getSvaluecmt());
					}
				}
				if(!vouno.equals(dto.getVouno()))
					allamt = allamt.add(dto.getNmoney());
				vouno = dto.getVouno();
			}
			reportmap.put("allamt", allamt);
			if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5108)){
				reportPath = tmpPath22Detail;
				return "直接支付额度明细报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5106)){
				reportPath = tmpPath33Detail;
				return "授权支付额度明细报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5207)){
				reportPath = tmpPath11;
				reportmap.put("reporttitle", "实拨拨款");
				return "实拨资金业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_3208)){
				reportPath = tmpPath11;
				reportmap.put("reporttitle", "实拨退款");
				return "实拨资金业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5209)){
				reportPath = tmpPath66;
				return "收入退付业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_5209)){
				reportPath = tmpPath66;
				return "收入退付退款业务报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_2301)){
				reportPath = tmpPath44Detail;
				return "商行支付划款明细报表";
			}else if(chooseBiztype().contains(MsgConstant.VOUCHER_NO_2302)){
				reportPath = tmpPath55Detail;
				return "商行支付退款明细报表";
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"查询凭证业务量操作出现异常！", e));
		}
        return "实拨资金业务报表";
    }
    /**
	 * Direction: 凭证还原
	 * ename: goVoucherViewer
	 * 引用方法: 
	 * viewers: 凭证还原
	 * messages: 
	 */
    public String goVoucherViewer(Object o){
    	try {
			voucherViewersList = businessDetailListService.makeSumCountRecord(judSleBiztype(), param);
			ActiveXCompositevoucherVoucherViewer.init(0);
		} catch (ITFEBizException e) {
			e.printStackTrace();
		}
        return super.goVoucherViewer(o);
    }
    
    /**
	 * Direction: 查询凭证联数
	 * ename: queryVoucherPrintCount
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public int queryVoucherJOintCount(TvVoucherinfoDto vDto){
    	int count=0;
    	TsVouchercommitautoDto tDto=new TsVouchercommitautoDto();
    	tDto.setSorgcode(vDto.getSorgcode());
    	tDto.setStrecode(vDto.getStrecode());
    	tDto.setSvtcode(vDto.getSvtcode());
    	try {
			List<TsVouchercommitautoDto> list= (List) commonDataAccessService.findRsByDto(tDto);
			if(list==null||list.size()==0)
				return -1;
			tDto=list.get(0);
			if(tDto.getSjointcount()==null){
				return -1;
			}			
		} catch (ITFEBizException e) {
			log.error(e);
			return -2;
		}catch(Exception e){
			log.error(e);
			return -1;
		}
    	return tDto.getSjointcount();
    }
    
    /**
	 * Direction: 查询凭证打印次数
	 * ename: queryVoucherPrintCount
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryVoucherPrintCount(TvVoucherinfoDto vDto){
    	String err=null;
    	try {
			err=voucherLoadService.queryVoucherPrintCount(vDto);
			
		} catch (ITFEBizException e) {			
			log.error(e);
			return "查询异常";
		}
    	return err;
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

	public BigDecimal getTotalFamt() {
		return totalFamt;
	}

	public void setTotalFamt(BigDecimal totalFamt) {
		this.totalFamt = totalFamt;
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public Boolean getDirectPlan() {
		return directPlan;
	}

	public void setDirectPlan(Boolean directPlan) {
		this.directPlan = directPlan;
	}

	public Boolean getGrantPlan() {
		return grantPlan;
	}

	public void setGrantPlan(Boolean grantPlan) {
		this.grantPlan = grantPlan;
	}

	public Boolean getPayOut() {
		return payOut;
	}

	public void setPayOut(Boolean payOut) {
		this.payOut = payOut;
	}

	public Boolean getDwbk() {
		return dwbk;
	}

	public void setDwbk(Boolean dwbk) {
		this.dwbk = dwbk;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public String getTmpPath1() {
		return tmpPath1;
	}

	public void setTmpPath1(String tmpPath1) {
		this.tmpPath1 = tmpPath1;
	}

	public String getTmpPath2() {
		return tmpPath2;
	}

	public void setTmpPath2(String tmpPath2) {
		this.tmpPath2 = tmpPath2;
	}

	public String getTmpPath3() {
		return tmpPath3;
	}

	public void setTmpPath3(String tmpPath3) {
		this.tmpPath3 = tmpPath3;
	}

	public String getTmpPath4() {
		return tmpPath4;
	}

	public void setTmpPath4(String tmpPath4) {
		this.tmpPath4 = tmpPath4;
	}

	public String getTmpPath5() {
		return tmpPath5;
	}

	public void setTmpPath5(String tmpPath5) {
		this.tmpPath5 = tmpPath5;
	}

	public String getTmpPath6() {
		return tmpPath6;
	}

	public void setTmpPath6(String tmpPath6) {
		this.tmpPath6 = tmpPath6;
	}

	public Boolean getDirectPlan1() {
		return directPlan1;
	}

	public void setDirectPlan1(Boolean directPlan1) {
		this.directPlan1 = directPlan1;
	}

	public Boolean getGrantPlan1() {
		return grantPlan1;
	}

	public void setGrantPlan1(Boolean grantPlan1) {
		this.grantPlan1 = grantPlan1;
	}

	public Boolean getPayOut1() {
		return payOut1;
	}

	public void setPayOut1(Boolean payOut1) {
		this.payOut1 = payOut1;
	}
	public Boolean getDwbk1() {
		return dwbk1;
	}

	public void setDwbk1(Boolean dwbk1) {
		this.dwbk1 = dwbk1;
	}

	public Boolean getDirectPayMoney() {
		return directPayMoney;
	}

	public void setDirectPayMoney(Boolean directPayMoney) {
		this.directPayMoney = directPayMoney;
	}

	public Boolean getDirectBckMoney() {
		return directBckMoney;
	}

	public void setDirectBckMoney(Boolean directBckMoney) {
		this.directBckMoney = directBckMoney;
	}

	public Boolean getGrantPayMoney() {
		return grantPayMoney;
	}

	public void setGrantPayMoney(Boolean grantPayMoney) {
		this.grantPayMoney = grantPayMoney;
	}

	public Boolean getGrantBckMoney() {
		return grantBckMoney;
	}

	public void setGrantBckMoney(Boolean grantBckMoney) {
		this.grantBckMoney = grantBckMoney;
	}

	public Boolean getDirectPayMoney1() {
		return directPayMoney1;
	}

	public void setDirectPayMoney1(Boolean directPayMoney1) {
		this.directPayMoney1 = directPayMoney1;
	}

	
	public List getVoucherViewersList() {
		return voucherViewersList;
	}

	public void setVoucherViewersList(List voucherViewersList) {
		this.voucherViewersList = voucherViewersList;
	}

	public Boolean getDirectBckMoney1() {
		return directBckMoney1;
	}

	public void setDirectBckMoney1(Boolean directBckMoney1) {
		this.directBckMoney1 = directBckMoney1;
	}

	public Boolean getGrantPayMoney1() {
		return grantPayMoney1;
	}

	public void setGrantPayMoney1(Boolean grantPayMoney1) {
		this.grantPayMoney1 = grantPayMoney1;
	}

	public Boolean getGrantBckMoney1() {
		return grantBckMoney1;
	}

	public void setGrantBckMoney1(Boolean grantBckMoney1) {
		this.grantBckMoney1 = grantBckMoney1;
	}
	public List<Mapper> getBankCodeList() {
		return bankCodeList;
	}

	public void setBankCodeList(List<Mapper> bankCodeList) {
		this.bankCodeList = bankCodeList;
	}

	public List<Mapper> getBankCodeList1() {
		return bankCodeList1;
	}

	public void setBankCodeList1(List<Mapper> bankCodeList1) {
		this.bankCodeList1 = bankCodeList1;
	}

	public Boolean getPayOutBack() {
		return payOutBack;
	}

	public void setPayOutBack(Boolean payOutBack) {
		this.payOutBack = payOutBack;
	}

	public Boolean getDwbkBack() {
		return dwbkBack;
	}

	public void setDwbkBack(Boolean dwbkBack) {
		this.dwbkBack = dwbkBack;
	}
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		if(month!=null&&!"".equals(month)&&!"0".equals(month))
		{
			String stringdate = TimeFacade.getCurrentStringTime().substring(0,4)+month+"01";
			param.setStartdate(new java.sql.Date(TimeFacade.parseDate(stringdate).getTime()));
			param.setEnddate(new java.sql.Date(TimeFacade.parseDate(TimeFacade.getEndDateOfMonth(stringdate)).getTime()));
		}else
		{
			param.setStartdate(TimeFacade.getCurrentDateTime());
			param.setEnddate(TimeFacade.getCurrentDateTime());
		}
		this.month = month;
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public List getMonthlist() {
		if(monthlist==null)
		{
			monthlist = new ArrayList();
			Mapper map = new Mapper("0", "");
			monthlist.add(map);
			for(int i=1;i<=12;i++)
			{
				if(i<10)
					map = new Mapper("0"+i,i+"月");
				else
					map = new Mapper(String.valueOf(i),i+"月");
				monthlist.add(map);
			}
		}
		return monthlist;
	}

	public List getDatelist() {
		if(datelist==null)
		{
			datelist = new ArrayList();
			Mapper map = new Mapper("0","接收日期");
			datelist.add(map);
			map = new Mapper("1","处理日期");
			datelist.add(map);
		}
		return datelist;
	}

	public void setDatelist(List datelist) {
		this.datelist = datelist;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
		searchDto.setDatatype(datatype);
	}

	public String getTmpPath11() {
		return tmpPath11;
	}

	public void setTmpPath11(String tmpPath11) {
		this.tmpPath11 = tmpPath11;
	}

	public String getTmpPath66() {
		return tmpPath66;
	}

	public void setTmpPath66(String tmpPath66) {
		this.tmpPath66 = tmpPath66;
	}

	public String getTmpPath22Detail() {
		return tmpPath22Detail;
	}

	public void setTmpPath22Detail(String tmpPath22Detail) {
		this.tmpPath22Detail = tmpPath22Detail;
	}

	public String getTmpPath33Detail() {
		return tmpPath33Detail;
	}

	public void setTmpPath33Detail(String tmpPath33Detail) {
		this.tmpPath33Detail = tmpPath33Detail;
	}

	public String getTmpPath44Detail() {
		return tmpPath44Detail;
	}

	public void setTmpPath44Detail(String tmpPath44Detail) {
		this.tmpPath44Detail = tmpPath44Detail;
	}

	public String getTmpPath55Detail() {
		return tmpPath55Detail;
	}

	public void setTmpPath55Detail(String tmpPath55Detail) {
		this.tmpPath55Detail = tmpPath55Detail;
	}
}
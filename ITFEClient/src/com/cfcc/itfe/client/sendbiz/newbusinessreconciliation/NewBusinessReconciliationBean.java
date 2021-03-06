package com.cfcc.itfe.client.sendbiz.newbusinessreconciliation;

import itferesourcepackage.ReportBussReadReturnEnumFactory;
import itferesourcepackage.VoucherRegularCheckAccountSvtcodeEnumFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeNewSendBussOcx;
import com.cfcc.itfe.client.sendbiz.sendbusinessreconciliation.ReconcilePayinfoSubBean;
import com.cfcc.itfe.client.sendbiz.sendbusinessreconciliation.ReconcilePayquotaSubBean;
import com.cfcc.itfe.client.sendbiz.sendbusinessreconciliation.ReconcileRealdialSubBean;
import com.cfcc.itfe.client.sendbiz.sendbusinessreconciliation.ReconcileRefundSubBean;
import com.cfcc.itfe.client.sendbiz.sendbusinessreconciliation.VoucherReconciliationSubBean;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoSubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaSubDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialSubDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRefundMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRefundSubDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.metadata.ColumnMetaData;
import com.cfcc.jaf.ui.metadata.ComboMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TableMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;
import com.cfcc.jaf.ui.util.Mapper;
/**
 * codecomment: 
 * @author zhangliang
 * @time   17-06-26 14:05:39
 * 子系统: SendBiz
 * 模块:newBusinessReconciliation
 * 组件:NewBusinessReconciliation
 */
@SuppressWarnings("unchecked")
public class NewBusinessReconciliationBean extends AbstractNewBusinessReconciliationBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(NewBusinessReconciliationBean.class);
	private ReconcilePayinfoSubBean payinfoSubBean;
    private ReconcileRealdialSubBean realdialSubBean;
    private ReconcileRefundSubBean refundSubBean;
    private ReconcilePayquotaSubBean payquotaSubBean;
    private VoucherReconciliationSubBean reconciliationPaging;
    private List<Mapper> bankCodeList;  
    private List<Mapper> fundtypelist;
    private boolean isfirst = false;
    private List acctList = null;
	List<TvVoucherinfoDto> checkList = null;
	List<TvVoucherinfoDto> returnList = null;
	List subVoucherList = null;
	// 用户登录信息
	private ITFELoginInfo loginInfo;

	// 对账起始日期
	private String startDate;
	private String endDate;
	// 签章类型
	private String stamp;
	// 签章列表
	private List<TsStamppositionDto> stampList = null;
	// 凭证类型
	private String voucherType = MsgConstant.VOUCHER_NO_3587;
	private List recvDeptList = new ArrayList();
    private String recvDept = null;
    private String strecode = null;
    private String month = null;
    private List monthlist = null;
    public NewBusinessReconciliationBean() {
    	super();
      	payinfoSubBean = new ReconcilePayinfoSubBean();
		realdialSubBean = new ReconcileRealdialSubBean();
		refundSubBean = new ReconcileRefundSubBean();
		payquotaSubBean = new ReconcilePayquotaSubBean();
		payinfoDto = new TfReconcilePayinfoMainDto();
	    realdialMainDto = new TfReconcileRealdialMainDto();
	    refundMainDto = new TfReconcileRefundMainDto();
	    payquotaMainDto = new TfReconcilePayquotaMainDto();
	    reconciliationPaging = new VoucherReconciliationSubBean();
		dto = new TvVoucherinfoDto();
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		checkList = new ArrayList<TvVoucherinfoDto>();
		dto.setSorgcode(loginInfo.getSorgcode());
		dto.setSvtcode(MsgConstant.VOUCHER_NO_3587);
		pagingcontext = new PagingContext(this);
		startDate = TimeFacade.getCurrentStringTime();
		endDate = TimeFacade.getCurrentStringTime();
		TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
		tsTreasuryDto.setSorgcode(loginInfo.getSorgcode());
		TsStamppositionDto tsDto = new TsStamppositionDto();
		tsDto.setSorgcode(dto.getSorgcode());
		tsDto.setSvtcode(dto.getSvtcode());
		tsDto.setStrecode(dto.getStrecode());
		Set<String> set = new HashSet<String>();
		TsStamppositionDto sDto = new TsStamppositionDto();
		List<TsStamppositionDto> tList = null;
		stampList = new ArrayList<TsStamppositionDto>();
		List<TsStamppositionDto> tsList = new ArrayList<TsStamppositionDto>();
		try {
			tList = commonDataAccessService.findRsByDto(tsDto);
			if (tList.size() > 0) {
				for (TsStamppositionDto sdto : tList) {
					set.add(sdto.getSstamptype());
				}
				for (String stamptype : (Set<String>) set) {
					sDto = new TsStamppositionDto();
					sDto.setSorgcode(dto.getSorgcode());
					sDto.setSvtcode(dto.getSvtcode());
					sDto.setSstamptype(stamptype);
					sDto = (TsStamppositionDto) commonDataAccessService
							.findRsByDto(sDto).get(0);
					tsList.add(sDto);
				}
				stampList.addAll(tsList);
				if (stampList.size() == 1) {
					stamp = ((TsStamppositionDto) stampList.get(0))
							.getSstamptype();
				}
			}
			TsTreasuryDto stsTreasuryDto = new TsTreasuryDto();
	  	  	tsTreasuryDto.setSorgcode(loginInfo.getSorgcode());
			List<TsTreasuryDto> slist = null;
		  	  try {
		  		slist = (List<TsTreasuryDto>)commonDataAccessService.findRsByDto(stsTreasuryDto);
		  		acctList = new ArrayList();
		  		TsInfoconnorgaccDto qd = new TsInfoconnorgaccDto();
		  		qd.setSorgcode(loginInfo.getSorgcode());
		  		List relist =commonDataAccessService.findRsByDto(qd);
		  		for(int i=0;i<relist.size();i++)
		  		{
		  			qd = (TsInfoconnorgaccDto)relist.get(i);
		  			Mapper map = new Mapper(qd.getSpayeraccount(), qd.getSpayername());
		  			acctList.add(map);
		  		}
		  	  } catch (ITFEBizException e){
		  		  log.error(e);
		  	  }
		  	  setFundType();
		  	  if(slist!=null && slist.size() > 0){
		  		  dto.setStrecode(slist.get(0).getStrecode());//国库代码
		  		  setStrecode(dto.getStrecode());
		  		  isfirst = true;
		  	  }
//			if(bankCodeList==null||bankCodeList.size()<=0)
//			{
//				TsConvertbanktypeDto finddto = new TsConvertbanktypeDto();
//				finddto.setSorgcode(loginInfo.getSorgcode());
//				finddto.setStrecode(dto.getStrecode());
//				List<TsConvertbanktypeDto> resultList = commonDataAccessService.findRsByDto(finddto);
//				if(resultList!=null&&resultList.size()>0)
//				{
//					Mapper map = null;
//					bankCodeList = new ArrayList<Mapper>();
//					for(TsConvertbanktypeDto temp:resultList)
//					{
//						map = new Mapper(temp.getSbankcode(), temp.getSbankname()+(temp.getSbankcode().length()>3?temp.getSbankcode().substring(0,3):""));
//						bankCodeList.add(map);
//					}
//				}
//			}
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("凭证刷新操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
                  
    }
    /**
	 * Direction: 生成凭证 ename: voucherGenerator 引用方法: viewers: * messages:
	 */
	public String voucherGenerator(Object o) {
		if (startDate == null || startDate.equals("")) {
			MessageDialog.openMessageDialog(null, "请输入对账起始日期！");
			return "";
		}
		if (endDate == null || endDate.equals("")) {
			MessageDialog.openMessageDialog(null, "请输入对账终止日期！");
			return "";
		}
		if (Integer.parseInt(startDate) > Integer.parseInt(TimeFacade
				.getCurrentStringTime())) {
			MessageDialog.openMessageDialog(null, "对账起始日期不能大于当前系统日期！");
			return "";
		}
		if (Integer.parseInt(endDate) > Integer.parseInt(TimeFacade
				.getCurrentStringTime())) {
			MessageDialog.openMessageDialog(null, "对账终止日期不能大于当前系统日期！");
			return "";
		}
		if (Integer.parseInt(startDate) > Integer.parseInt(endDate)) {
			MessageDialog.openMessageDialog(null, "对账起始日期不能大于终止日期！");
			return "";
		}
		if(loginInfo.getPublicparam().contains(",datelength=2,"))
		{
			if (Integer.parseInt(endDate.substring(4, 6))
					- Integer.parseInt(startDate.substring(4, 6)) > 1) {
				MessageDialog.openMessageDialog(null, "时间段不能大于两个月！");
				return "";
			}
		}
		if (dto.getStrecode() == null || "".equals(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "国库代码不可为空！");
			return "";
		}
		if (dto.getSvtcode() == null || "".equals(dto.getSvtcode())) {
			MessageDialog.openMessageDialog(null, "凭证类型不可为空！");
			return "";
		}
		int count = 0;
		try {
			StringBuffer sbuf = new StringBuffer();
			TvVoucherinfoDto voucherDto = new TvVoucherinfoDto();
			voucherDto.setSpaybankcode(dto.getSpaybankcode());
			voucherDto.setSext1(dto.getSext1());
			voucherDto.setSext2(dto.getSext2());
			voucherDto.setSext3(dto.getSext3());
			voucherDto.setSext4(dto.getSext4());
			voucherDto.setSext5(dto.getSext5());
			if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
				List voucherMapperList = new VoucherRegularCheckAccountSvtcodeEnumFactory()
						.getEnums(null);
				for (Mapper mapper : (List<Mapper>) voucherMapperList) {
					voucherDto.setSvtcode(mapper.getUnderlyValue() + "");
					// 判断是否已经生成凭证
					sbuf.append(voucherIsRepeat(voucherDto));
				}
			} else {
				if(dto.getSvtcode().indexOf("TCBS")>0)
					dto.setSvtcode(MsgConstant.VOUCHER_NO_3580);
				voucherDto.setSvtcode(dto.getSvtcode());
				// 判断是否已经生成凭证
				sbuf.append(voucherIsRepeat(voucherDto));
			}
			if (sbuf.toString() != null && !sbuf.toString().equals("")) {
				if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null,
						"信息提示", sbuf.toString() + "\r\n确定继续生成吗？")) {
					return "";
				}
			}
			if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
				List voucherMapperList = new VoucherRegularCheckAccountSvtcodeEnumFactory()
						.getEnums(null);
				for (Mapper mapper : (List<Mapper>) voucherMapperList) {
					voucherDto.setSvtcode(mapper.getUnderlyValue() + "");
					String result = voucherGererate(voucherDto);
					if (result == null || result.equals("")) {
						continue;
					}
					count += Integer.parseInt(result);
				}
			} else {
				voucherDto.setSvtcode(dto.getSvtcode());
				if(loginInfo.getPublicparam().contains(",send3588=more,")&&(MsgConstant.VOUCHER_NO_3588.equals(voucherDto.getSvtcode())||MsgConstant.VOUCHER_NO_3558.equals(voucherDto.getSvtcode())))
				{
					if(recvDept!=null&&!"".equals(recvDept)&&recvDept.length()==3)
					{
						voucherDto.setSext3(recvDept);
					}
				}else if(MsgConstant.VOUCHER_NO_3582.equals(dto.getSvtcode()))
				{
					if(dto.getSpaybankcode()==null||"".equals(dto.getSpaybankcode()))
					{
						MessageDialog.openMessageDialog(null, "代理银行不可为空！");
						return "";
					}
					if(dto.getSext1()==null||"".equals(dto.getSext1())||"-1".equals(dto.getSext1()))
					{
						MessageDialog.openMessageDialog(null, "支付方式只能为直接或授权！");
						return "";
					}
					if(dto.getSext3()==null||"".equals(dto.getSext3()))
					{
						MessageDialog.openMessageDialog(null, "辖属标志不可为空！");
						return "";
					}
					if(dto.getSext2()==null||"".equals(dto.getSext2()))
					{
						MessageDialog.openMessageDialog(null, "预算种类不可为空！");
						return "";
					}
					dto.setSext4(month);//月份
					dto.setSext5("35122");//类型标示
				}
				String result = voucherGererate(voucherDto);
				if (result != null && !result.equals("")) {
					count += Integer.parseInt(result);
				}
			}
			if (count == 0) {
				MessageDialog.openMessageDialog(null, "没有该日期的凭证生成！");
				return "";
			}
			MessageDialog.openMessageDialog(null, "对账凭证生成操作成功，成功条数为：" + count
					+ " ！");
			refreshTable();
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
			Exception e1 = new Exception("生成凭证操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.voucherGenerator(o);
	}

	/**
	 * 获取OCX控件url
	 * 
	 * @return
	 */
	public String getOcxVoucherServerURL() {
		String ls_URL = "";
		try {
			ls_URL = voucherLoadService.getOCXServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception("获取OCX控件URL地址操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return ls_URL;
	}

	/**
	 * 获取签章服务地址
	 * 
	 * @return
	 */
	public String getOCXStampServerURL() {
		String ls_URL = "";
		try {
			ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception("获取OCX控件签章服务URL地址操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return ls_URL;
	}

	/**
	 * Direction: 查询 ename: search 引用方法: viewers: * messages:
	 */
	public String search(Object o) {
		refreshTable();
		return super.search(o);
	}

	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = new PageResponse();
		pageResponse = retrieve(pageRequest);
//		if (pageResponse.getTotalCount() == 0
//				&& (StringUtils.isBlank(dto.getSstatus()) || dto.getSstatus()
//						.equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)))
//			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录，请先生成凭证！");
//		else if (pageResponse.getTotalCount() == 0
//				&& (dto.getSstatus().equals(DealCodeConstants.VOUCHER_STAMP)))
//			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录，请先签章！");
//		else if (pageResponse.getTotalCount() == 0&& dto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS))
		if (pageResponse.getTotalCount() == 0)
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录！");
		pagingcontext.setPage(pageResponse);
	}
	/**
	 * Direction: 删除凭证
	 * ename: deletegenvoucher
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delgenvoucher(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要删除电子凭证数据记录！");
    		return "";
    	}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行删除操作吗？")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
			if(!DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus().trim())){
				MessageDialog.openMessageDialog(null, "只有状态为处理成功的数据可以删除！");
        		return "";
			}
    	}
    	try {
	    	for(TvVoucherinfoDto infoDto:checkList)
	    	{
	    		commonDataAccessService.delete(infoDto);
	    	}
	    	refreshTable();
    	} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("删除数据失败！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}
        return "";
    }
	/**
	 * Direction: 全选 ename: selectAll 引用方法: viewers: * messages:
	 */
	public String selectAll(Object o) {
		if (checkList == null || checkList.size() == 0) {
			checkList = new ArrayList<TvVoucherinfoDto>();
			checkList.addAll(pagingcontext.getPage().getData());
		} else
			checkList.clear();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.selectAll(o);
	}

	/**
	 * Direction: 发送电子凭证 ename: sendReturnVoucher 引用方法: viewers: * messages:
	 */
	public String voucherSend(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要发送电子凭证的记录！");
			return "";
		}
		int count = 0;
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示",
				"你确定要对选中的记录执行发送电子凭证操作吗？")) {
			return "";
		}
		// 对选中的列表进行操作时，重新查询数据库取得最新数据状态
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		for (TvVoucherinfoDto vDto : this.checkList) {
			TvVoucherinfoDto newvDto = new TvVoucherinfoDto();
			try {
				newvDto = getDto(vDto);
			} catch (ITFEBizException e) {
				Exception e1 = new Exception("重新查询数据出现错误！", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e1);
			}
			checkList.add(newvDto);
		}

		for (TvVoucherinfoDto infoDto : checkList) {
			if(loginInfo.getPublicparam().contains(",send3588=more,")&&(MsgConstant.VOUCHER_NO_3588.equals(infoDto.getSvtcode())||MsgConstant.VOUCHER_NO_3558.equals(infoDto.getSvtcode())))
			{
				if(recvDept==null||"".equals(recvDept)||recvDept.length()!=3)
				{
					MessageDialog.openMessageDialog(null, "请选择发送凭证的接收方！");
	        		return "";
				}else
				{
					infoDto.setSext5(recvDept);
				}
			}
			if (loginInfo.getPublicparam().indexOf(",stampmode=sign") >= 0) {
				if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
						.getSstatus().trim()))) {
					MessageDialog.openMessageDialog(null,
							"请选择凭证状态为 \"处理成功\" 的记录！");
					return "";
				}
			} else {
				if (!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto
						.getSstatus().trim()))) {
					MessageDialog.openMessageDialog(null,
							"请选择凭证状态为 \"签章成功\" 的记录！");
					return "";
				}
			}
		}

		try {
			count = voucherLoadService.voucherReturnSuccess(checkList);
			MessageDialog.openMessageDialog(null, "发送电子凭证   "
					+ checkList.size() + " 条，成功条数为：" + count + " ！");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return "";
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("发送电子凭证库操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);

			return "";
		}
		return super.voucherSend(o);

	}
	/**
	 * Direction: 凭证查看 ename: voucherView 引用方法: viewers: * messages:
	 */
	public String voucherView(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要查看的记录！");
			return "";
		}
		try {
			ActiveXCompositeNewSendBussOcx.init(0);

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("凭证查看异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.voucherView(o);
	}

	/**
	 * Direction: 回单还原展示 ename: returnVoucherView 引用方法: viewers: * messages:
	 */
	public String returnVoucherView(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要查看的记录！");
			return "";
		}
		try {
			if (returnList == null)
				returnList = new ArrayList<TvVoucherinfoDto>();
			TvVoucherinfoDto fdto = new TvVoucherinfoDto();
			List tempList = null;
			for (TvVoucherinfoDto vdto : checkList) {
				fdto.setSext2(vdto.getSvoucherno());
				if (MsgConstant.VOUCHER_NO_3507.equals(vdto.getSvtcode())) {
					fdto.setSvtcode(null);
					tempList = commonDataAccessService.findRsByDtoWithWhere(
							fdto, " and (S_VTCODE='"
									+ MsgConstant.VOUCHER_NO_2507
									+ "' or S_VTCODE='"
									+ MsgConstant.VOUCHER_NO_5507 + "') ");
					if (tempList != null && tempList.size() > 0)
						returnList.addAll(tempList);
				} else if (MsgConstant.VOUCHER_NO_3508
						.equals(vdto.getSvtcode())) {
					fdto.setSvtcode(MsgConstant.VOUCHER_NO_5508);
					tempList = commonDataAccessService.findRsByDto(fdto);
					if (tempList != null && tempList.size() > 0)
						returnList.addAll(tempList);
				} else if (MsgConstant.VOUCHER_NO_3509
						.equals(vdto.getSvtcode())) {
					fdto.setSvtcode(MsgConstant.VOUCHER_NO_5509);
					tempList = commonDataAccessService.findRsByDto(fdto);
					if (tempList != null && tempList.size() > 0)
						returnList.addAll(tempList);
				} else if (MsgConstant.VOUCHER_NO_3510
						.equals(vdto.getSvtcode())) {
					fdto.setSvtcode(MsgConstant.VOUCHER_NO_5510);
					tempList = commonDataAccessService.findRsByDto(fdto);
					if (tempList != null && tempList.size() > 0)
						returnList.addAll(tempList);
				}
				else if (MsgConstant.VOUCHER_NO_3511
						.equals(vdto.getSvtcode())) {
					fdto.setSvtcode(MsgConstant.VOUCHER_NO_5511);
					tempList = commonDataAccessService.findRsByDto(fdto);
					if (tempList != null && tempList.size() > 0)
						returnList.addAll(tempList);
				}
				else if (MsgConstant.VOUCHER_NO_3512
						.equals(vdto.getSvtcode())) {
					fdto.setSvtcode(MsgConstant.VOUCHER_NO_5512);
					tempList = commonDataAccessService.findRsByDto(fdto);
					if (tempList != null && tempList.size() > 0)
						returnList.addAll(tempList);
				}
				else if (MsgConstant.VOUCHER_NO_3513
						.equals(vdto.getSvtcode())) {
					fdto.setSvtcode(MsgConstant.VOUCHER_NO_5513);
					tempList = commonDataAccessService.findRsByDto(fdto);
					if (tempList != null && tempList.size() > 0)
						returnList.addAll(tempList);
				}else{
					tempList = commonDataAccessService.findRsByDto(fdto);
					if (tempList != null && tempList.size() > 0)
						returnList.addAll(tempList);
				}
			}
			if (returnList == null || returnList.size() == 0) {
				MessageDialog.openMessageDialog(null, "选择的记录没有收到对方的回单！");
				return "";
			}

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("凭证查看异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.returnVoucherView(o);
	}

	/**
	 * Direction: 查询凭证打印次数 ename: queryVoucherPrintCount 引用方法: viewers: *
	 * messages:
	 */
	public String queryVoucherPrintCount(TvVoucherinfoDto vDto) {
		String err = null;
		try {
			err = voucherLoadService.queryVoucherPrintCount(vDto);

		} catch (ITFEBizException e) {
			log.error(e);
			return "查询异常";
		}
		return err;
	}

	/**
	 * Direction: 查询凭证联数 ename: queryVoucherPrintCount 引用方法: viewers: *
	 * messages:
	 */
	public int queryVoucherJOintCount(TvVoucherinfoDto vDto) {
		TsVouchercommitautoDto tDto = new TsVouchercommitautoDto();
		tDto.setSorgcode(vDto.getSorgcode());
		tDto.setStrecode(vDto.getStrecode());
		tDto.setSvtcode(vDto.getSvtcode());
		try {
			List<TsVouchercommitautoDto> list = (List) commonDataAccessService
					.findRsByDto(tDto);
			if (list == null || list.size() == 0)
				return -1;
			tDto = list.get(0);
			if (tDto.getSjointcount() == null) {
				return -1;
			}
		} catch (ITFEBizException e) {
			log.error(e);
			return -2;
		} catch (Exception e) {
			log.error(e);
			return -1;
		}
		return tDto.getSjointcount();
	}

	public List<Mapper> getStampEnums(TvVoucherinfoDto vDto) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		List<TsStamppositionDto> enumList = new ArrayList<TsStamppositionDto>();
		TsStamppositionDto tDto = new TsStamppositionDto();
		tDto.setSorgcode(vDto.getSorgcode());
		tDto.setStrecode(vDto.getStrecode());
		tDto.setSvtcode(vDto.getSvtcode());
		try {
			enumList = commonDataAccessService.findRsByDto(tDto);
			if (enumList != null && enumList.size() > 0) {
				for (TsStamppositionDto emuDto : enumList) {
					Mapper map = new Mapper(emuDto.getSstamptype(), emuDto
							.getSstampname());
					maplist.add(map);
				}
			}

		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception("获取凭证签章列表出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}

		return maplist;
	}

	public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException {
		return voucherLoadService.voucherStampXml(vDto);
	}

	public void refreshTable() {
		init();
		checkList.clear();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
	}

	public String voucherGererate(TvVoucherinfoDto voucherDto)
			throws ITFEBizException {
		List<TvVoucherinfoDto> list = getVoucherList(voucherDto);
		if (list == null || list.size() == 0) {
			return "";
		}

		List resultList = voucherLoadService.voucherGenerate(list);
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0) + "";
		}
		return "";
	}

	/**
	 * 对账凭证凭证判重
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	private String voucherIsRepeat(TvVoucherinfoDto voucherDto)
			throws ITFEBizException {
		List<TvVoucherinfoDto> list = getVoucherList(voucherDto);
		if (list == null || list.size() == 0) {
			return "";
		}
		String vtcodeType = voucherDto.getSvtcode();
		// 存放具体国库下的重复凭证
		List<TvVoucherinfoDto> lists = new ArrayList<TvVoucherinfoDto>();
		// 遍历凭证列表
		for (TvVoucherinfoDto tvVoucherinfoDto : list) {
			TvVoucherinfoDto idto = new TvVoucherinfoDto();
			idto.setSorgcode(tvVoucherinfoDto.getSorgcode());
			idto.setStrecode(tvVoucherinfoDto.getStrecode());
			idto.setSvtcode(tvVoucherinfoDto.getSvtcode());
			// idto.setScreatdate(TimeFacade.getCurrentStringTime());
			idto.setShold3(startDate);
			idto.setShold4(endDate);
			if (vtcodeType != null && !vtcodeType.equals("")) {
				if (vtcodeType.equals(MsgConstant.VOUCHER_NO_3504)||vtcodeType.equals(MsgConstant.VOUCHER_NO_3507)) {
					idto.setSpaybankcode(tvVoucherinfoDto.getSpaybankcode());
				} else if (vtcodeType.equals(MsgConstant.VOUCHER_NO_3551)) {
					idto.setSpaybankcode(tvVoucherinfoDto.getSpaybankcode());
					idto.setShold2(tvVoucherinfoDto.getShold2());
				}
			}
			List<TvVoucherinfoDto> voucherList = commonDataAccessService
					.findRsByDto(idto);
			if (voucherList != null && voucherList.size() > 0) {
				lists.add(tvVoucherinfoDto);
			}
		}
		StringBuffer sbf = new StringBuffer();
		if (lists != null && lists.size() > 0) {
			for (TvVoucherinfoDto tvVoucherinfoDto : lists) {
				sbf.append("国库为：").append(tvVoucherinfoDto.getStrecode())
						.append(", 业务类型为：")
						.append(getVtcodeTypeCmt(vtcodeType)).append(", 在 ")
						.append(tvVoucherinfoDto.getShold3()).append(" 到 ")
						.append(tvVoucherinfoDto.getShold4()).append(
								" 期间内的对账凭证已生成,").append("\r\n");
			}
		}
		return sbf.toString();

	}

	public String getVtcodeTypeCmt(String vtcode) {
		String cmt = "";
		if (vtcode.equals(MsgConstant.VOUCHER_NO_3551)) {
			cmt = "国库与财政集中支付额度余额对账单3551";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3587)) {
			cmt = "清算信息对账3587";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3550)) {
			cmt = "集中支付对账3550";
		}else if (vtcode.equals(MsgConstant.VOUCHER_NO_3588)) {
			cmt = "实拨信息对账3588";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3558)) {
			cmt = "实拨信息对账3558";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3509)) {
			cmt = "收入退付对账3509";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3580)) {
			cmt = "清算额度对账3580";
		}else if(vtcode.equals(MsgConstant.VOUCHER_NO_3583))
		{
			cmt = "库款账户对账3583";
		}else if(vtcode.equals(MsgConstant.VOUCHER_NO_3582))
		{
			cmt = "清算信息对账3582";
		}
		return cmt;

	}

	public List<TvVoucherinfoDto> getVoucherList(TvVoucherinfoDto voucherDto)
			throws ITFEBizException {
		List<TvVoucherinfoDto> list = new ArrayList<TvVoucherinfoDto>();
		List<TsTreasuryDto> tList = new ArrayList<TsTreasuryDto>();
		TsTreasuryDto tDto = new TsTreasuryDto();
		tDto.setSorgcode(loginInfo.getSorgcode());
		if (dto.getStrecode() == null || dto.getStrecode().equals("")) {
			tList = commonDataAccessService.findRsByDto(tDto);
		} else {
			tDto.setStrecode(dto.getStrecode());
			tList.add(tDto);
		}
		if (tList == null || tList.size() == 0)
			throw new ITFEBizException("国库参数未维护！", new Exception(""));
		TvVoucherinfoDto vDto = null;
		for (TsTreasuryDto tsDto : tList) {
			vDto = (TvVoucherinfoDto) dto.clone();
			vDto.setStrecode(tsDto.getStrecode());
			vDto.setScreatdate(TimeFacade.getCurrentStringTime());
			vDto.setShold3(startDate);
			vDto.setShold4(endDate);
			vDto.setSverifyusercode(loginInfo.getSuserName());
			list.add(vDto);
		}
		return list;
	}
	
	/**
	 * Direction: 双击查看业务明细
	 * ename: doubleClick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
        TvVoucherinfoDto voucherinfoDto = (TvVoucherinfoDto)o;
        if(MsgConstant.VOUCHER_NO_3587.equals(voucherinfoDto.getSvtcode())||MsgConstant.VOUCHER_NO_3550.equals(voucherinfoDto.getSvtcode())){
        	payinfoDto = new TfReconcilePayinfoMainDto();
        	payinfoDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReconcilePayinfoMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(payinfoDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "查询清算对账信息业务数据出错！");
			}
			payinfoDto = list.get(0);
			TfReconcilePayinfoSubDto payinfoSubDto = new TfReconcilePayinfoSubDto();
			payinfoSubDto.setIvousrlno(payinfoDto.getIvousrlno());
			String tmp = payinfoSubBean.searchDtoList(payinfoSubDto);
			if(tmp==null){
    			PagingContext p = payinfoSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "清算信息对账";
        }else if(MsgConstant.VOUCHER_NO_3588.equals(voucherinfoDto.getSvtcode())||MsgConstant.VOUCHER_NO_3558.equals(voucherinfoDto.getSvtcode())){
        	realdialMainDto = new TfReconcileRealdialMainDto();
        	realdialMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReconcileRealdialMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(realdialMainDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "查询实拨信息对帐业务数据出错！");
			}
			realdialMainDto = list.get(0);
			TfReconcileRealdialSubDto realdialSubDto = new TfReconcileRealdialSubDto();
			realdialSubDto.setIvousrlno(realdialMainDto.getIvousrlno());
			String tmp = realdialSubBean.searchDtoList(realdialSubDto);
			if(tmp==null){
    			PagingContext p = realdialSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "实拨信息对账";
        }else if(MsgConstant.VOUCHER_NO_3583.equals(voucherinfoDto.getSvtcode())){
        	refundMainDto = new TfReconcileRefundMainDto();
        	refundMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReconcileRefundMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(refundMainDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "查询实拨信息对帐业务数据出错！");
			}
			refundMainDto = list.get(0);
			TfReconcileRefundSubDto refundSubDto = new TfReconcileRefundSubDto();
			refundSubDto.setIvousrlno(refundMainDto.getIvousrlno());
			String tmp = refundSubBean.searchDtoList(refundSubDto);
			if(tmp==null){
    			PagingContext p = refundSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "收入退付对账";
        }else if(MsgConstant.VOUCHER_NO_3580.equals(voucherinfoDto.getSvtcode())){
        	payquotaMainDto = new TfReconcilePayquotaMainDto();
        	payquotaMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReconcilePayquotaMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(payquotaMainDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "查询实拨信息对帐业务数据出错！");
			}
			payquotaMainDto = list.get(0);
			TfReconcilePayquotaSubDto payquotaSubDto = new TfReconcilePayquotaSubDto();
			payquotaSubDto.setIvousrlno(payquotaMainDto.getIvousrlno());
			String tmp = payquotaSubBean.searchDtoList(payquotaSubDto);
			if(tmp==null){
    			PagingContext p = payquotaSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "清算额度对账";
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
	public PageResponse retrieve(PageRequest pageRequest) {
		pageRequest.setPageSize(50);
		PageResponse page = null;
		StringBuffer wheresql = new StringBuffer();
		wheresql.append(" 1=1 ");
		if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
			List voucherList = new VoucherRegularCheckAccountSvtcodeEnumFactory()
					.getEnums(null);
			wheresql.append("AND ( ");
			for (int i = 0; i < voucherList.size(); i++) {
				Mapper mapper = (Mapper) voucherList.get(i);
				if (i == voucherList.size() - 1) {
					wheresql.append(" S_VTCODE = '" + mapper.getUnderlyValue()
							+ "' )");
					break;
				}
				wheresql.append(" S_VTCODE = '" + mapper.getUnderlyValue()
						+ "' OR ");
			}
		}
		if(voucherType!=null&&voucherType.equals(MsgConstant.VOUCHER_NO_3580))
		{
			wheresql.append(" and (s_ext4 IS NULL or s_ext4='') ");
		}
		if(voucherType!=null&&voucherType.equals(MsgConstant.VOUCHER_NO_3580+"TCBS"))
		{
			dto.setSvtcode(MsgConstant.VOUCHER_NO_3580);
			
		}
		if(voucherType!=null&&voucherType.equals(MsgConstant.VOUCHER_NO_3582))
			dto.setSext4(month);//月份
		else if(voucherType!=null&&voucherType.equals(MsgConstant.VOUCHER_NO_3580+"TCBS"))
			dto.setSext4("TCBS");//月份;
		else
			dto.setSext4(null);//月份
		dto.setShold3(startDate);
		dto.setShold4(endDate);
		try {
			if(loginInfo.getPublicparam().contains(",send3510=more,")||loginInfo.getPublicparam().contains(",send3508=more,"))
				dto.setSext5(recvDept);
			else
				dto.setSext5(null);
			if("-1".equals(dto.getSext1()))
				dto.setSext1("");
			if(loginInfo.getPublicparam().contains((",sendbussearchhold=5,")))
			{
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(TimeFacade.getCurrentDate());
				calendar.add(Calendar.DATE, -5);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
				String getdate = dateFormat.format(calendar.getTime());
				page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,wheresql.toString()+" AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+dto.getSorgcode()+(dto.getStrecode()==null?"":"' and S_TRECODE='"+dto.getStrecode())+(dto.getSext4()==null?"":"' and s_ext4='"+dto.getSext4())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+dto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
			}else if(loginInfo.getPublicparam().contains((",sendbussearchhold=15,")))
			{
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(TimeFacade.getCurrentDate());
				calendar.add(Calendar.DATE, -15);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
				String getdate = dateFormat.format(calendar.getTime());
				page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,wheresql.toString()+" AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+dto.getSorgcode()+(dto.getStrecode()==null?"":"' and S_TRECODE='"+dto.getStrecode())+(dto.getSext4()==null?"":"' and s_ext4='"+dto.getSext4())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+dto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
			}else
			{
				page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,wheresql.toString(), " TS_SYSUPDATE desc");
			}
			return page;
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Throwable e) {
			log.error(e);
			Exception e1 = new Exception("凭证查询出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.retrieve(pageRequest);
	}

	/**
	 * Direction: 签章 ename: voucherStamp 引用方法: viewers: * messages:
	 */
	public String voucherStamp(Object o) {
		boolean ocxflag = false;
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		String stamp = null;
		TvVoucherinfoDto dto = new TvVoucherinfoDto();
		if (o instanceof List) {
			List ocxStampList = (List) o;
			String stampname = (String) ocxStampList.get(0);
			dto = (TvVoucherinfoDto) ocxStampList.get(1);
			TsStamppositionDto stampPostionDto = new TsStamppositionDto();
			stampPostionDto.setSorgcode(dto.getSorgcode());
			stampPostionDto.setStrecode(dto.getStrecode());
			stampPostionDto.setSvtcode(dto.getSvtcode());
			stampPostionDto.setSstampname(stampname);
			try {
				stampPostionDto = (TsStamppositionDto) commonDataAccessService
						.findRsByDto(stampPostionDto).get(0);
			} catch (ITFEBizException e) {
				log.error(e);
				Exception e1 = new Exception("签章出现异常！", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e1);
				return "";
			}
			stamp = stampPostionDto.getSstamptype();
			this.stamp = stampPostionDto.getSstamptype();
			checkList.add(dto);
			ocxflag = true;
		}
		if (!ocxflag) {
			stamp = this.stamp;
			checkList = this.checkList;
			dto = this.dto;
		}
		int count = 0;
		if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择签章类型！");
			return null;
		}

		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要签章的记录！");
			return "";
		}

		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行签章操作吗？")) {
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
					.getSstatus().trim()))) {
				MessageDialog.openMessageDialog(null, "请选择凭证状态为处理成功的记录！");
				return "";
			}
		}
		try {
			if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
					dto.getSvtcode())) {
				MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");
				return "";
			}
			TsUsersDto uDto = new TsUsersDto();
			uDto.setSorgcode(loginInfo.getSorgcode());
			uDto.setSusercode(loginInfo.getSuserCode());
			uDto = (TsUsersDto) commonDataAccessService.findRsByDto(uDto)
					.get(0);
			TsStamppositionDto stampDto = new TsStamppositionDto();
			stampDto.setSvtcode(dto.getSvtcode());
			stampDto.setSorgcode(loginInfo.getSorgcode());
			stampDto.setSstamptype(stamp);
			stampDto = (TsStamppositionDto) commonDataAccessService
					.findRsByDto(stampDto).get(0);
			String permission = uDto.getSstamppermission();
			boolean flag = true;
			if (permission == null || permission.equals("")) {
				flag = false;
			} else {
				if (permission.indexOf(",") < 0) {
					if (!permission.equals(stamp)) {
						flag = false;
					}
				} else {
					flag = false;
					String[] permissions = permission.split(",");
					for (int i = 0; i < permissions.length; i++) {
						if (permissions[i].equals(stamp)) {
							flag = true;
							break;
						}
					}
				}
			}
			if (flag == false) {
				MessageDialog.openMessageDialog(null, "当前用户无  \""
						+ stampDto.getSstampname() + "\"  签章权限！");
				return "";
			}
			TsTreasuryDto tDto = new TsTreasuryDto();
			TsStamppositionDto sDto = new TsStamppositionDto();
			Map map = new HashMap();
			String usercode = uDto.getSusercode();
			String stampuser = "";
			String stampid = "";
			for (TvVoucherinfoDto vDto : checkList) {
				map.put(vDto.getStrecode(), "");
				stampid = vDto.getSstampid();
				if (stampid != null && !stampid.equals("")) {
					String[] stampids = stampid.split(",");
					for (int i = 0; i < stampids.length; i++) {
						if (stamp.equals(stampids[i])) {
							MessageDialog.openMessageDialog(null, "凭证编号："
									+ vDto.getSvoucherno() + " 已签  \""
									+ stampDto.getSstampname()
									+ "\" ，同一凭证不能重复签章！");
							return "";
						}
					}
				}
				if (!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)
						&& !stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)&&!stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)) {
					stampuser = vDto.getSstampuser();
					if (stampuser != null && !stampuser.equals("")) {
						String[] stampusers = stampuser.split(",");
						for (int i = 0; i < stampusers.length; i++) {
							if (usercode.equals(stampusers[i])) {
								TsStamppositionDto tstampDto = new TsStamppositionDto();
								tstampDto.setSorgcode(loginInfo.getSorgcode());
								tstampDto.setSvtcode(dto.getSvtcode());
								String[] stampids = vDto.getSstampid().split(
										",");
								for (int j = 0; j < stampids.length; j++) {
									if (!stampids[i]
											.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
										tstampDto.setSstamptype(stampids[i]);
										break;
									}
								}
								tstampDto = (TsStamppositionDto) commonDataAccessService
										.findRsByDto(tstampDto).get(0);
								MessageDialog.openMessageDialog(null, "凭证编号："
										+ vDto.getSvoucherno() + " 当前用户已签  \""
										+ tstampDto.getSstampname()
										+ "\" ，同一用户只能签一次私章，请选择其他用户！");
								return "";
							}
						}

					}
				}
			}
			Iterator it = map.keySet().iterator();
			List lists = new ArrayList();
			List list = null;
			List sinList = null;
			List<TsStamppositionDto> sList = null;
			List vList = null;
			String strecode = "";
			while (it.hasNext()) {
				strecode = it.next() + "";
				vList = new ArrayList();
				tDto = new TsTreasuryDto();
				sDto = new TsStamppositionDto();
				sList = new ArrayList<TsStamppositionDto>();
				list = new ArrayList();
				try {
					tDto.setSorgcode(loginInfo.getSorgcode());
					tDto.setStrecode(strecode);
					tDto = (TsTreasuryDto) commonDataAccessService.findRsByDto(
							tDto).get(0);
				} catch (Exception e) {
					log.error(e);
					MessageDialog.openMessageDialog(null, "国库主体代码" + strecode
							+ "在国库主体信息参数中不存在！");

					return "";
				}
				try {
					sDto.setSorgcode(loginInfo.getSorgcode());
					sDto.setStrecode(strecode);
					sDto.setSvtcode(dto.getSvtcode());
					sList = (List<TsStamppositionDto>) commonDataAccessService
							.findRsByDto(sDto);
					sDto.setSstamptype(stamp);
					sDto = (TsStamppositionDto) commonDataAccessService
							.findRsByDto(sDto).get(0);

				} catch (Exception e) {

					log.error(e);
					MessageDialog.openMessageDialog(null, "国库主体代码：" + strecode
							+ " " + stampDto.getSstampname() + " 参数未维护！ ");

					return "";
				}
				if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
					if (tDto.getSrotarycertid() == null
							|| tDto.getSrotarycertid().equals("")) {
						MessageDialog.openMessageDialog(null, "国库主体代码"
								+ strecode + "在国库主体信息参数中 "
								+ stampDto.getSstampname() + "证书ID 参数未维护！ ");

						return "";
					} else if (tDto.getSrotaryid() == null
							|| tDto.getSrotaryid().equals("")) {
						MessageDialog.openMessageDialog(null, "国库主体代码"
								+ strecode + "在国库主体信息参数中 "
								+ stampDto.getSstampname() + "印章ID 参数未维护！ ");

						return "";
					}
				} else if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {

					if (tDto.getScertid() == null
							|| tDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "国库主体代码"
								+ strecode + "在国库主体信息参数中 "
								+ stampDto.getSstampname() + "证书ID 参数未维护！ ");

						return "";
					} else if (tDto.getSstampid() == null
							|| tDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "国库主体代码"
								+ strecode + "在国库主体信息参数中 "
								+ stampDto.getSstampname() + "印章ID 参数未维护！ ");

						return "";
					}

				}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
				{
    				if(tDto.getSattachcertid()==null||tDto.getSattachcertid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"证书ID 参数未维护！ ");
        				
        				return "";
        			}else if(tDto.getSattachid()==null||tDto.getSattachid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"印章ID 参数未维护！ ");
        				
        				return "";
        			}
				} else {
					if (uDto.getScertid() == null
							|| uDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "当前用户  "
								+ stampDto.getSstampname() + "  证书ID参数未维护！ ");
						return "";
					} else if (uDto.getSstampid() == null
							|| uDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "当前用户   "
								+ stampDto.getSstampname() + "  印章ID参数未维护！ ");
						return "";
					}
				}
				for (TvVoucherinfoDto vDto : checkList) {
					if (vDto.getStrecode().equals(strecode)) {
						sinList = new ArrayList();
						sinList.add(vDto);
						stampid = vDto.getSstampid();
						String seq = sDto.getSstampsequence();
						if (seq != null && !seq.equals("")) {
							List<String> seqList = new ArrayList<String>();
							for (int i = 0; i < sList.size(); i++) {
								TsStamppositionDto tsDto = (TsStamppositionDto) sList
										.get(i);
								if (tsDto.getSstampsequence() != null
										&& !tsDto.getSstampsequence()
												.equals("")) {
									seqList.add(tsDto.getSstampsequence());
								}
							}
							if (seqList != null && seqList.size() > 0) {
								String[] seqs = seqList
										.toArray(new String[seqList.size()]);
								ActiveXCompositeNewSendBussOcx
										.sortStringArray(seqs);
								String temp = "";
								for (int i = seqs.length - 1; i > -1; i--) {
									if (Integer.parseInt(seqs[i]) < Integer
											.parseInt(seq)) {
										temp = seqs[i];
										break;
									}
								}
								if (!temp.equals("")) {
									List<TsStamppositionDto> tsList = new ArrayList<TsStamppositionDto>();
									TsStamppositionDto tsDto = new TsStamppositionDto();
									tsDto.setSorgcode(loginInfo.getSorgcode());
									tsDto.setStrecode(strecode);
									tsDto.setSvtcode(vDto.getSvtcode());
									tsDto.setSstampsequence(temp);
									tsList = (List<TsStamppositionDto>) commonDataAccessService
											.findRsByDto(tsDto);
									String err = "";
									for (TsStamppositionDto tstampDto : tsList) {
										err = tstampDto.getSstampname() + " , "
												+ err;
									}
									err = err
											.substring(0, err.lastIndexOf(","));
									if (stampid == null || stampid.equals("")) {
										MessageDialog
												.openMessageDialog(
														null,
														"国库代码："
																+ vDto
																		.getStrecode()
																+ " 凭证类型: "
																+ vDto
																		.getSvtcode()
																+ vDto
																		.getSvoucherno()
																+ " \""
																+ stampDto
																		.getSstampname()
																+ "\"签章前请先 \""
																+ err + "\"签章！");

										return "";

									} else {
										err = "";
										String[] stampids = stampid.split(",");
										List<TsStamppositionDto> tsList1 = new ArrayList<TsStamppositionDto>();
										for (int j = 0; j < tsList.size(); j++) {
											for (int i = 0; i < stampids.length; i++) {
												if (stampids[i]
														.equals(tsList
																.get(j)
																.getSstamptype())) {
													tsList1.add(tsList.get(j));
												}
											}
										}
										tsList.removeAll(tsList1);
										if (tsList.size() > 0) {
											for (TsStamppositionDto tstampDto : tsList) {
												err = tstampDto.getSstampname()
														+ " , " + err;
											}
											err = err.substring(0, err
													.lastIndexOf(","));
											MessageDialog
													.openMessageDialog(
															null,
															"国库代码："
																	+ vDto
																			.getStrecode()
																	+ " 凭证类型: "
																	+ vDto
																			.getSvtcode()
																	+ " \""
																	+ stampDto
																			.getSstampname()
																	+ "\"签章前请先 \""
																	+ err
																	+ "\"签章！");

											return "";
										}
									}

								}
							}
						}
						if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
							if (!voucherLoadService.getOfficialStamp().equals(
									MsgConstant.VOUCHER_OFFICIALSTAMP)) {
								sinList.add(ActiveXCompositeNewSendBussOcx
										.getVoucherStamp(vDto, tDto
												.getScertid(), sDto
												.getSstampposition(), tDto
												.getSstampid()));
							}
						} else if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
							if (!voucherLoadService.getOfficialStamp().equals(
									MsgConstant.VOUCHER_ROTARYSTAMP)) {
								sinList.add(ActiveXCompositeNewSendBussOcx
										.getVoucherStamp(vDto, tDto
												.getSrotarycertid(), sDto
												.getSstampposition(), tDto
												.getSrotaryid()));
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
						{
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP))
								sinList.add(ActiveXCompositeNewSendBussOcx.getVoucherStamp(vDto, tDto.getSattachcertid(), sDto.getSstampposition(), tDto.getSattachid()));
						} else {
							if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
							{
								sinList.add(ActiveXCompositeNewSendBussOcx
										.getVoucherStamp(vDto, uDto.getScertid(),
												sDto.getSstampposition(), uDto
														.getSstampid()));
							}
						}
						vList.add(sinList);
					}
				}
				list.add(uDto);
				list.add(tDto);
				list.add(sDto);
				list.add(sList.size());
				list.add(vList);
				lists.add(list);
			}
			count = voucherLoadService.voucherStamp(lists);
			if (ocxflag) {

				return count + "";
			}
			MessageDialog.openMessageDialog(null, "凭证签章   " + checkList.size()
					+ " 条，成功条数为：" + count + " ！");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return "";

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("签章操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
			return "";

		}
		return super.voucherStamp(o);
	}

	/**
	 * Direction:签章撤销 ename: voucherStampCancle 引用方法: viewers: * messages:
	 */
	public String voucherStampCancle(Object o) {
		int count = 0;
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要签章撤销的记录！");
			return "";
		}
		if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择签章类型！");
			return null;
		}
		if (!org.eclipse.jface.dialogs.MessageDialog
				.openConfirm(this.editor.getCurrentComposite().getShell(),
						"提示", "你确定要对选中的记录执行签章撤销操作吗？")) {
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
					.getSstatus()))
					&& !(DealCodeConstants.VOUCHER_STAMP.equals(infoDto
							.getSstatus()))) {
				MessageDialog.openMessageDialog(null, "请选择凭证状态为处理成功、签章成功的记录！");
				return "";
			}
		}

		try {
			if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
					dto.getSvtcode())) {
				MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");
				return "";

			}

			TsUsersDto uDto = new TsUsersDto();
			uDto.setSorgcode(loginInfo.getSorgcode());
			uDto.setSusercode(loginInfo.getSuserCode());
			uDto = (TsUsersDto) commonDataAccessService.findRsByDto(uDto)
					.get(0);
			TsStamppositionDto stampDto = new TsStamppositionDto();
			stampDto.setSvtcode(dto.getSvtcode());
			stampDto.setSorgcode(loginInfo.getSorgcode());
			stampDto.setSstamptype(stamp);
			stampDto = (TsStamppositionDto) commonDataAccessService
					.findRsByDto(stampDto).get(0);
			String permission = uDto.getSstamppermission();
			boolean flag = true;
			if (permission == null || permission.equals("")) {
				flag = false;
			} else {
				if (permission.indexOf(",") < 0) {
					if (!permission.equals(stamp)) {
						flag = false;
					}

				} else {
					flag = false;
					String[] permissions = permission.split(",");
					for (int i = 0; i < permissions.length; i++) {
						if (permissions[i].equals(stamp)) {
							flag = true;
							break;
						}
					}

				}
			}
			boolean managerPermission = false;
			if (flag == false) {
				MessageDialog.openMessageDialog(null, "当前用户无  \""
						+ stampDto.getSstampname() + "\"  签章权限！，可通过主管授权撤销签章");
				String msg = "需要主管授权才能才能撤销签章！";
				if (!AdminConfirmDialogFacade.open("B_247", "业务对账凭证", "授权用户"
						+ loginInfo.getSuserName() + "撤销签章", msg)) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					return null;
				} else {
					managerPermission = true;
				}

			}
			TsTreasuryDto tDto = new TsTreasuryDto();
			TsStamppositionDto sDto = new TsStamppositionDto();
			Map map = new HashMap();
			String usercode = uDto.getSusercode();
			String stampid = "";
			for (TvVoucherinfoDto vDto : checkList) {
				usercode = uDto.getSusercode();
				map.put(vDto.getStrecode(), "");
				stampid = vDto.getSstampid();
				int j = -1;
				if (stampid == null || stampid.equals("")) {
					flag = false;
				} else {
					flag = false;
					String[] stampids = stampid.split(",");
					for (int i = 0; i < stampids.length; i++) {
						if (stamp.equals(stampids[i])) {
							flag = true;
							j = i;
							break;
						}
					}

				}
				if (flag == false) {
					MessageDialog.openMessageDialog(null, "凭证编号："
							+ vDto.getSvoucherno() + " 未签  \""
							+ stampDto.getSstampname() + "\" ！");
					return "";
				}
				if (managerPermission == false) {
					if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
						usercode = usercode + "!";
					} else if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
						usercode = usercode + "#";
					}
					String stampuserboolean = vDto.getSstampuser().split(",")[j];
					if (!stampuserboolean.equals(usercode)) {
						MessageDialog.openMessageDialog(null, "凭证编号："
								+ vDto.getSvoucherno() + "   \""
								+ stampDto.getSstampname()
								+ "\" 不是当前用户所签！可通过主管授权撤销签章");
						String msg = "需要主管授权才能才能撤销签章！";
						if (!AdminConfirmDialogFacade
								.open("B_247", "收入报表凭证", "授权用户"
										+ loginInfo.getSuserName() + "撤销签章",
										msg)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							return null;
						} else {
							managerPermission = true;
						}
					}
				}
			}

			Iterator it = map.keySet().iterator();

			List lists = new ArrayList();
			List list = null;
			List sinList = null;
			List<TsStamppositionDto> sList = null;
			List vList = null;

			String strecode = "";
			while (it.hasNext()) {
				strecode = it.next() + "";
				vList = new ArrayList<TvVoucherinfoDto>();
				tDto = new TsTreasuryDto();
				sDto = new TsStamppositionDto();
				sList = new ArrayList<TsStamppositionDto>();
				list = new ArrayList();
				try {
					tDto.setSorgcode(loginInfo.getSorgcode());
					tDto.setStrecode(strecode);
					tDto = (TsTreasuryDto) commonDataAccessService.findRsByDto(
							tDto).get(0);
				} catch (Exception e) {
					log.error(e);
					MessageDialog.openMessageDialog(null, "国库主体代码" + strecode
							+ "在国库主体信息参数中不存在！");

					return "";
				}

				try {
					sDto.setSorgcode(loginInfo.getSorgcode());
					sDto.setStrecode(strecode);
					sDto.setSvtcode(dto.getSvtcode());
					sList = (List<TsStamppositionDto>) commonDataAccessService
							.findRsByDto(sDto);
					sDto.setSstamptype(stamp);
					sDto = (TsStamppositionDto) commonDataAccessService
							.findRsByDto(sDto).get(0);

				} catch (Exception e) {
					log.error(e);
					MessageDialog.openMessageDialog(null, "国库主体代码：" + strecode
							+ " " + stampDto.getSstampname() + " 参数未维护！ ");
					return "";
				}

				if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
					if (tDto.getSrotarycertid() == null
							|| tDto.getSrotarycertid().equals("")) {
						MessageDialog.openMessageDialog(null, "国库主体代码"
								+ strecode + "在国库主体信息参数中 "
								+ stampDto.getSstampname() + "证书ID 参数未维护！ ");

						return "";
					} else if (tDto.getSrotaryid() == null
							|| tDto.getSrotaryid().equals("")) {
						MessageDialog.openMessageDialog(null, "国库主体代码"
								+ strecode + "在国库主体信息参数中 "
								+ stampDto.getSstampname() + "印章ID 参数未维护！ ");
						return "";
					}
				} else if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {

					if (tDto.getScertid() == null
							|| tDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "国库主体代码"
								+ strecode + "在国库主体信息参数中 "
								+ stampDto.getSstampname() + "证书ID 参数未维护！ ");
						return "";
					} else if (tDto.getSstampid() == null
							|| tDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "国库主体代码"
								+ strecode + "在国库主体信息参数中 "
								+ stampDto.getSstampname() + "印章ID 参数未维护！ ");
						return "";
					}

				} else {
					if (uDto.getScertid() == null
							|| uDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "当前用户  "
								+ stampDto.getSstampname() + "  证书ID参数未维护！ ");
						return "";
					} else if (uDto.getSstampid() == null
							|| uDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "当前用户   "
								+ stampDto.getSstampname() + "  印章ID参数未维护！ ");
						return "";
					}

				}

				for (TvVoucherinfoDto vDto : checkList) {
					if (vDto.getStrecode().equals(strecode)) {

						sinList = new ArrayList();
						sinList.add(vDto);
						stampid = vDto.getSstampid();
						String seq = sDto.getSstampsequence();
						if (seq != null && !seq.equals("")) {
							List<String> seqList = new ArrayList<String>();
							for (int i = 0; i < sList.size(); i++) {
								TsStamppositionDto tsDto = (TsStamppositionDto) sList
										.get(i);
								if (tsDto.getSstampsequence() != null
										&& !tsDto.getSstampsequence()
												.equals("")) {
									seqList.add(tsDto.getSstampsequence());
								}
							}
							if (seqList != null && seqList.size() > 0) {
								String[] seqs = seqList
										.toArray(new String[seqList.size()]);
								ActiveXCompositeNewSendBussOcx
										.sortStringArray(seqs);

								String temp = "";
								for (int i = 0; i < seqs.length; i++) {
									if (Integer.parseInt(seqs[i]) > Integer
											.parseInt(seq)) {
										temp = seqs[i];
										break;
									}
								}
								if (!temp.equals("")) {
									List<TsStamppositionDto> tsList = new ArrayList<TsStamppositionDto>();
									TsStamppositionDto tsDto = new TsStamppositionDto();
									tsDto.setSorgcode(loginInfo.getSorgcode());
									tsDto.setStrecode(strecode);
									tsDto.setSvtcode(vDto.getSvtcode());
									tsDto.setSstampsequence(temp);
									tsList = (List<TsStamppositionDto>) commonDataAccessService
											.findRsByDto(tsDto);
									String err = "";

									String[] stampids = stampid.split(",");
									for (int j = 0; j < tsList.size(); j++) {
										for (int i = 0; i < stampids.length; i++) {
											if (stampids[i].equals(tsList
													.get(j).getSstamptype())) {
												err = tsList.get(j)
														.getSstampname()
														+ " " + err;
											}
										}
									}
									if (!err.trim().equals("")) {
										for (TsStamppositionDto tstampDto : tsList) {
											err = tstampDto.getSstampname()
													+ " , " + err;
										}
										err = err.substring(0, err
												.lastIndexOf(","));
										MessageDialog
												.openMessageDialog(
														null,
														"国库代码："
																+ vDto
																		.getStrecode()
																+ " 凭证类型: "
																+ vDto
																		.getSvtcode()
																+ " \""
																+ stampDto
																		.getSstampname()
																+ "\"撤销签章前请先撤销 \""
																+ err + "\"签章！");

										return "";
									}
								}
							}
						}
						int j = -1;
						String[] stampids = stampid.split(",");
						for (int i = 0; i < stampids.length; i++) {
							if (stamp.equals(stampids[i])) {
								j = i;
								break;

							}
						}
						TsUsersDto userDto = new TsUsersDto();
						userDto.setSorgcode(loginInfo.getSorgcode());
						String user = vDto.getSstampuser().split(",")[j];

						userDto
								.setSusercode(stamp
										.equals(MsgConstant.VOUCHERSAMP_ROTARY) ? user
										.substring(0, (user.length() - 1))
										: (stamp
												.equals(MsgConstant.VOUCHERSAMP_OFFICIAL) ? user
												.substring(0,
														(user.length() - 1))
												: user));
						userDto = (TsUsersDto) commonDataAccessService
								.findRsByDto(userDto).get(0);
						sinList.add(userDto);
						vList.add(sinList);
					}
				}

				list.add(tDto);
				list.add(sDto);
				list.add(vList);
				lists.add(list);
			}
			count = voucherLoadService.voucherStampCancle(lists);
			MessageDialog.openMessageDialog(null, "凭证撤销签章   "
					+ checkList.size() + " 条，成功条数为：" + count + " ！");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return "";

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("签章操作撤销出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
			return "";

		}
		return super.voucherStampCancle(o);
	}

	/**
	 * 刷新签章类型
	 * 
	 */
	public void refreshStampType(String type) {
		TsStamppositionDto tsDto = new TsStamppositionDto();
		tsDto.setSorgcode(dto.getSorgcode());
		tsDto.setSvtcode(dto.getSvtcode());
		tsDto.setStrecode(dto.getStrecode());
		Set<String> set = new HashSet<String>();
		TsStamppositionDto sDto = new TsStamppositionDto();
		List<TsStamppositionDto> tList = null;
		stampList = new ArrayList<TsStamppositionDto>();
		List<TsStamppositionDto> tsList = new ArrayList<TsStamppositionDto>();
		try {
			tList = commonDataAccessService.findRsByDto(tsDto);
			if (tList.size() > 0) {
				for (TsStamppositionDto sdto : tList) {
					set.add(sdto.getSstamptype());
				}
				for (String stamptype : (Set<String>) set) {
					sDto = new TsStamppositionDto();
					sDto.setSorgcode(dto.getSorgcode());
					sDto.setSvtcode(dto.getSvtcode());
					sDto.setSstamptype(stamptype);
					sDto = (TsStamppositionDto) commonDataAccessService
							.findRsByDto(sDto).get(0);
					tsList.add(sDto);
				}
				stampList.addAll(tsList);
				if (stampList.size() == 1) {
					stamp = ((TsStamppositionDto) stampList.get(0))
							.getSstamptype();
				}
			}
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("凭证刷新操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		if (type == null) {
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}
	}

	public TvVoucherinfoDto getDto(TvVoucherinfoDto dto)
			throws ITFEBizException {
		TvVoucherinfoPK pk = new TvVoucherinfoPK();
		pk.setSdealno(dto.getSdealno());
		return (TvVoucherinfoDto) commonDataAccessService.find(pk);
	}

	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TvVoucherinfoDto> checkList) {
		this.checkList = checkList;
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		NewBusinessReconciliationBean.log = log;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public List<TsStamppositionDto> getStampList() {
		return stampList;
	}

	public void setStampList(List<TsStamppositionDto> stampList) {
		this.stampList = stampList;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		if(voucherType!=null&&voucherType.equals(MsgConstant.VOUCHER_NO_3580+"TCBS"))//厦门个性化需要从tcbs导入数据生成额度对帐
		{
			this.dto.setSext4("TCBS");
			this.dto.setSvtcode(MsgConstant.VOUCHER_NO_3580);
		}else
		{
			this.dto.setSext4(null);
			this.dto.setSvtcode(voucherType);
		}
		// 根据不同的凭证显示不同的控件
		setContentVisible();
		pagingcontext.setPage(new PageResponse());
		refreshStampType(null);
	}

	public void setContentVisible() {
		List<String> visContentAreaName = new ArrayList<String>();
		List<String> displaylist = new ArrayList<String>();
		if(voucherType.equals(MsgConstant.VOUCHER_NO_3582))
		{
			String mon = TimeFacade.getCurrentStringTime().substring(4,6);
			int i = Integer.parseInt(mon)-1;
			if(i==0)
				i=12;
			setMonth(i<10?"0"+i:String.valueOf(i));
			dto.setSext1("0");//支付方式	
			dto.setSext2("1");//预算种类
			dto.setSext3("0");//辖属标志
			dto.setSext4(month);//月份
			dto.setSext5("35122");//类型标示
			visContentAreaName.add("对账条件选择");
			visContentAreaName.add("凭证查询结果");
			displaylist.add("信息查询");
			displaylist.add("凭证查询一览表");
			displaylist.add("库款账户对账生成条件");
			displaylist.add("库款账户对账查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, visContentAreaName, true);
			MVCUtils.setContentAreaVisible(editor, displaylist, false);
			MVCUtils.reopenCurrentComposite(editor);
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			return;
		}else if(voucherType.equals(MsgConstant.VOUCHER_NO_3583))
		{
			displaylist.add("信息查询");
			displaylist.add("凭证查询一览表");
			displaylist.add("对账条件选择");
			displaylist.add("凭证查询结果");
			visContentAreaName.add("库款账户对账生成条件");
			visContentAreaName.add("库款账户对账查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, displaylist, false);
			MVCUtils.setContentAreaVisible(editor, visContentAreaName, true);
		}else
		{
			dto.setSext1(null);
			dto.setSext2(null);
			dto.setSext3(null);
			dto.setSext4(null);
			dto.setSext5(null);
			if(voucherType!=null&&voucherType.equals(MsgConstant.VOUCHER_NO_3580+"TCBS"))//厦门个性化需要从tcbs导入数据生成额度对帐
			{
				this.dto.setSext4("TCBS");
				this.dto.setSvtcode(MsgConstant.VOUCHER_NO_3580);
			}
			visContentAreaName.add("信息查询");
			visContentAreaName.add("凭证查询一览表");
			displaylist.add("库款账户对账生成条件");
			displaylist.add("库款账户对账查询结果一览表");
			displaylist.add("对账条件选择");
			displaylist.add("凭证查询结果");
			MVCUtils.setContentAreaVisible(editor, visContentAreaName, true);
			MVCUtils.setContentAreaVisible(editor, displaylist, false);
		}
		if(voucherType.equals(MsgConstant.VOUCHER_NO_3588)||voucherType.equals(MsgConstant.VOUCHER_NO_3558))
		{
			dto.setSpaybankcode(null);
			dto.setSext1(null);
			recvDeptList = new ReportBussReadReturnEnumFactory().getEnums("0470");
			if(recvDeptList==null||recvDeptList.size()<=0)
			{
				recvDeptList = new ArrayList();
				Mapper map = new Mapper("011", "财政");
				Mapper map1 = new Mapper("012", "地税");
				recvDeptList.add(map);
				recvDeptList.add(map1);
			}
		}else if(voucherType.equals(MsgConstant.VOUCHER_NO_3580)||voucherType.equals(MsgConstant.VOUCHER_NO_3580+"TCBS"))
		{
			recvDeptList = new ReportBussReadReturnEnumFactory().getEnums("0480");
			if(recvDeptList==null||recvDeptList.size()<=0)
			{
				recvDeptList = new ArrayList();
				Mapper map = new Mapper("011", "财政");
				recvDeptList.add(map);
				Mapper map1 = null;
				TsConvertbanktypeDto querydto = new TsConvertbanktypeDto();
				querydto.setSorgcode(loginInfo.getSorgcode());
				try {
					List queryList = commonDataAccessService.findRsByDto(querydto);
					if(queryList!=null&&queryList.size()>0)
					{
						for(int i=0;i<queryList.size();i++)
						{
							querydto = (TsConvertbanktypeDto)queryList.get(i);
							map1 = new Mapper(querydto.getSbanktype(),querydto.getSbankname());
							recvDeptList.add(map1);
						}
					}
				} catch (ITFEBizException e) {
					
				}	
			}
		}else
		{
			recvDeptList = new ArrayList();
		}
		List<ContainerMetaData> containerMetaData = MVCUtils
				.setContentAreasToVisible(editor, visContentAreaName);
		for (ContainerMetaData metadata : containerMetaData) {
			List controls = metadata.controlMetadatas;
			if ("信息查询".equals(metadata.name)) {
				for (int i = 0; i < controls.size(); i++) {
					if (voucherType.equals(MsgConstant.VOUCHER_NO_3588)
							|| voucherType.equals(MsgConstant.VOUCHER_NO_3509)|| voucherType.equals(MsgConstant.VOUCHER_NO_3558)) {
						if (controls.get(i) instanceof TextMetaData) {
							TextMetaData textmetadata = (TextMetaData) controls
									.get(i);
							if (textmetadata.caption.equals("银行代码")) {
								textmetadata.visible = false;
							}
						}else if (controls.get(i) instanceof ComboMetaData) {
							ComboMetaData combometadata = (ComboMetaData) controls.get(i);
							if (combometadata.caption.equals("支付方式")||combometadata.caption.equals("代理银行")) {
								combometadata.visible = false;
							}
							if(combometadata.caption.equals("接收方"))
							{
								if(loginInfo.getPublicparam().contains(",send3508=more,")&&MsgConstant.VOUCHER_NO_3508.equals(voucherType))
									combometadata.visible=true;
								else
									combometadata.visible=false;
							}
						}
					} else if (voucherType.equals(MsgConstant.VOUCHER_NO_3580+"TCBS")||voucherType.equals(MsgConstant.VOUCHER_NO_3580)
							|| voucherType.equals(MsgConstant.VOUCHER_NO_3587)) {
						if (controls.get(i) instanceof TextMetaData) {
							TextMetaData textmetadata = (TextMetaData) controls
									.get(i);
							if (textmetadata.caption.equals("银行代码")) {
								textmetadata.visible = true;
							}
						}else if (controls.get(i) instanceof ComboMetaData) {
							ComboMetaData combo = (ComboMetaData)controls.get(i);
							if (combo.caption.equals("支付方式")||combo.caption.equals("代理银行")) {
								combo.visible=true;
							}
							if(combo.caption.equals("接收方"))
							{
								if(loginInfo.getPublicparam().contains(",send3580=more,")&&(MsgConstant.VOUCHER_NO_3580.equals(voucherType)||voucherType.equals(MsgConstant.VOUCHER_NO_3580+"TCBS")))
									combo.visible=true;
								else
									combo.visible=false;
							}
						 }
					}else if(voucherType.equals(MsgConstant.VOUCHER_NO_3550)){
						if (controls.get(i) instanceof ComboMetaData) {
							ComboMetaData combo = (ComboMetaData)controls.get(i);
							if (combo.caption.equals("支付方式")||combo.caption.equals("资金性质")) {
								combo.visible=true;
							}
							if(loginInfo.getPublicparam().contains(",send3508=more,")&&MsgConstant.VOUCHER_NO_3508.equals(voucherType))
								combo.visible=true;
							else if(loginInfo.getPublicparam().contains(",send3510=more,")&&(MsgConstant.VOUCHER_NO_3510.equals(voucherType)||voucherType.equals(MsgConstant.VOUCHER_NO_3510+"TCBS")))
								combo.visible=true;
							else
								combo.visible=false;
						}
					}
				}
			} else if ("凭证查询一览表".equals(metadata.name)) {
				TableMetaData tablemd = (TableMetaData) controls.get(0);
				for (int i = 0; i < tablemd.columnList.size(); i++) {
					if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
						ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList
								.get(i);
						if (voucherType.equals(MsgConstant.VOUCHER_NO_3588)
								|| voucherType.equals(MsgConstant.VOUCHER_NO_3509)|| voucherType.equals(MsgConstant.VOUCHER_NO_3558)) {
							if (coletadata.title.equals("银行代码")||coletadata.title.equals("支付方式")) {
								coletadata.visible = false;
							}
						} else if (voucherType.equals(MsgConstant.VOUCHER_NO_3580+"TCBS")||voucherType
								.equals(MsgConstant.VOUCHER_NO_3580)
								|| voucherType
										.equals(MsgConstant.VOUCHER_NO_3587)) {
							if (coletadata.title.equals("银行代码")||coletadata.title.equals("支付方式")) {
								coletadata.visible = true;
							}// else if(coletadata.title.equals("支付方式")){
							// coletadata.visible = false;
							// }
						}else if (voucherType
								.equals(MsgConstant.VOUCHER_NO_3550)) {
							if (coletadata.title.equals("支付方式")) 
								coletadata.visible = true;
						}
						if(coletadata.title.equals("接收方"))
						{
							if((loginInfo.getPublicparam().contains(",send3508=more,")&&MsgConstant.VOUCHER_NO_3508.equals(voucherType))||(loginInfo.getPublicparam().contains(",send3510=more,")&&MsgConstant.VOUCHER_NO_3510.equals(voucherType)))
								coletadata.visible=true;
							else
								coletadata.visible=false;
						}
					}
				}
			}
		}
		MVCUtils.reopenCurrentComposite(editor);
	}

	public List getSubVoucherList() {
		subVoucherList = new ReportBussReadReturnEnumFactory().getEnums("1460");
		if (subVoucherList == null || subVoucherList.size() <= 0) {
			subVoucherList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_3580, "清算额度对账3580");
			Mapper map3510tcbs = new Mapper(MsgConstant.VOUCHER_NO_3580+"TCBS", "清算额度对账3580TCBS");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3582, "清算信息对账3582");
			Mapper map2 = new Mapper(MsgConstant.VOUCHER_NO_3583, "库款账户对账3583");
			Mapper map3 = new Mapper(MsgConstant.VOUCHER_NO_3587, "清算信息对账3587");
			Mapper map4 = new Mapper(MsgConstant.VOUCHER_NO_3588, "实拨信息对账3588");
			subVoucherList.add(map);
			subVoucherList.add(map3510tcbs);
			subVoucherList.add(map1);
			subVoucherList.add(map2);
			subVoucherList.add(map3);
			subVoucherList.add(map4);
		}
		return subVoucherList;
	}

	public void setSubVoucherList(List subVoucherList) {
		this.subVoucherList = subVoucherList;
	}

	public List<TvVoucherinfoDto> getReturnList() {
		return returnList;
	}

	public void setReturnList(List<TvVoucherinfoDto> returnList) {
		this.returnList = returnList;
	}

	public ReconcilePayinfoSubBean getPayinfoSubBean() {
		return payinfoSubBean;
	}

	public void setPayinfoSubBean(ReconcilePayinfoSubBean payinfoSubBean) {
		this.payinfoSubBean = payinfoSubBean;
	}

	public ReconcileRealdialSubBean getRealdialSubBean() {
		return realdialSubBean;
	}

	public void setRealdialSubBean(ReconcileRealdialSubBean realdialSubBean) {
		this.realdialSubBean = realdialSubBean;
	}

	public ReconcileRefundSubBean getRefundSubBean() {
		return refundSubBean;
	}

	public void setRefundSubBean(ReconcileRefundSubBean refundSubBean) {
		this.refundSubBean = refundSubBean;
	}

	public ReconcilePayquotaSubBean getPayquotaSubBean() {
		return payquotaSubBean;
	}

	public void setPayquotaSubBean(ReconcilePayquotaSubBean payquotaSubBean) {
		this.payquotaSubBean = payquotaSubBean;
	}

	public List<Mapper> getBankCodeList() {
		return bankCodeList;
	}

	public void setBankCodeList(List<Mapper> bankCodeList) {
		this.bankCodeList = bankCodeList;
	}
	/**
	 * Direction: 回单状态查询 ename: queryStatusReturnVoucher 引用方法: viewers: *
	 * messages:
	 */
	public String queryStatusReturnVoucher(Object o) {
		int count = 0;
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要查看回单状态的记录！");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"选择的记录中凭证状态与数据库不一致,请重新查询后再进行操作！");
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_SUCCESS.equals(infoDto.getSstatus().trim())||DealCodeConstants.VOUCHER_SUCCESS_BACK.equals(infoDto.getSstatus()))) {
				MessageDialog.openMessageDialog(null, "请选择凭证状态为回单成功或者退票成功的记录！");
				return "";
			}
		}
		try {
			count = voucherLoadService.queryStatusReturnVoucher(checkList);
			MessageDialog.openMessageDialog(null, "凭证查看回单状态" + checkList.size()
					+ " 条，成功条数为：" + count + " , 请查看！");
			refreshTable();
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
			Exception e1 = new Exception("凭证查看回单状态操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return "";
	}
	// 判断用户选择的记录是否与数据库中状态一致，如果不一致，则提示用户重新查询后，再进行操作
	// 按照用户选择的条件重新查询数据库，逐条比对用户状态
	public int checkdbstatus() {
		if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
			dto.setSvtcode(checkList.get(0).getSvtcode());
		}
		int i = 0;
		try {
			List<TvVoucherinfoDto> queryList = commonDataAccessService
					.findRsByDto(dto);
			if (queryList.size() > 0) {
				HashMap<String, String> map = new HashMap<String, String>();
				for (TvVoucherinfoDto tmp : queryList) {
					if (!map.containsKey(tmp.getSdealno())) {
						map.put(tmp.getSdealno(), tmp.getSstatus());
					}
				}
				for (TvVoucherinfoDto tmp : checkList) {
					if (map.containsKey(tmp.getSdealno())
							&& tmp.getSstatus().equals(
									map.get(tmp.getSdealno()))) {
						continue;
					} else {
						i++;
						break;
					}
				}
			} else {
				return 1;
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return 1;
		}
		return i;
	}
	/**
	 * Direction: 凭证库对账
	 * ename: voucherReconciliation
	 * 引用方法: 
	 * viewers: 凭证库对账结果
	 * messages: 
	 */
    public String voucherReconciliation(Object o){
    	TvVoucherinfoDto pdto = null;
//    	if(checkList!=null&&checkList.size()>1)
//    	{
//    		return "";
//    	}
//    	else if(checkList!=null&&checkList.size()==1)
//    	{
//    		pdto = (TvVoucherinfoDto)checkList.get(0).clone();
//    		if(MsgConstant.VOUCHER_NO_3507.equals(pdto.getSverifyusercode()))
//    		{
//    			
//    		}else if(MsgConstant.VOUCHER_NO_3508.equals(pdto.getSverifyusercode()))
//    		{
//    			
//    		}else if(MsgConstant.VOUCHER_NO_3509.equals(pdto.getSverifyusercode()))
//    		{
//    			
//    		}else if(MsgConstant.VOUCHER_NO_3510.equals(pdto.getSverifyusercode()))
//    		{
//    			
//    		}
//    	}
//    	else
//    	{
    		pdto = (TvVoucherinfoDto)dto.clone();
    		pdto.setShold3(startDate);
    		pdto.setShold4(endDate);
//    	}
    	String tmp = reconciliationPaging.searchDtoList(pdto);
    	if(tmp==null)
    	{
    		reconciliationPaging.getPagingContext().setPage(new PageResponse());
    		MessageDialog.openMessageDialog(null, "查询不到对账数据!");
    		return "";
    	}
        return super.voucherReconciliation(o);
    }
	public List getRecvDeptList() {
		return recvDeptList;
	}

	public void setRecvDeptList(List recvDeptList) {
		this.recvDeptList = recvDeptList;
	}

	public String getRecvDept() {
		return recvDept;
	}

	public void setRecvDept(String recvDept) {
		this.recvDept = recvDept;
	}

	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
		dto.setStrecode(strecode);
		TsConvertbanktypeDto finddto = new TsConvertbanktypeDto();
		finddto.setSorgcode(loginInfo.getSorgcode());
		finddto.setStrecode(dto.getStrecode());
		List<TsConvertbanktypeDto> resultList = null;
		try {
			resultList = commonDataAccessService.findRsByDto(finddto);
		} catch (ITFEBizException e) {
		}
		Mapper map = null;
		bankCodeList = new ArrayList<Mapper>();
		if(resultList!=null&&resultList.size()>0)
		{
			for(TsConvertbanktypeDto temp:resultList)
			{
				map = new Mapper(temp.getSbankcode(), temp.getSbankname()+(temp.getSbankcode().length()>3?temp.getSbankcode().substring(0,3):""));
				bankCodeList.add(map);
			}
		}
		setBankCodeList(bankCodeList);
		if(isfirst)
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		if(month!=null&&!"".equals(month)&&!"0".equals(month))
		{
			startDate = TimeFacade.getCurrentStringTime().substring(0,4)+month+"01";
			endDate = TimeFacade.getEndDateOfMonth(startDate);
		}else
		{
			startDate = TimeFacade.getCurrentStringTime();
			endDate = TimeFacade.getCurrentStringTime();
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

	public void setMonthlist(List monthlist) {
		this.monthlist = monthlist;
	}

	public VoucherReconciliationSubBean getReconciliationPaging() {
		return reconciliationPaging;
	}

	public void setReconciliationPaging(
			VoucherReconciliationSubBean reconciliationPaging) {
		this.reconciliationPaging = reconciliationPaging;
	}

	public List<Mapper> getFundtypelist() {
		return fundtypelist;
	}

	public void setFundtypelist(List<Mapper> fundtypelist) {
		this.fundtypelist = fundtypelist;
	}
	private void setFundType(){
		if(fundtypelist==null)
			fundtypelist = new ArrayList();
		Mapper map = new Mapper();
		map.setDisplayValue("预算管理资金");
		map.setUnderlyValue("1");
		fundtypelist.add(map);
		Mapper map1 = new Mapper();
		map1.setDisplayValue("财政专户管理资金");
		map1.setUnderlyValue("2");
		fundtypelist.add(map1);
		Mapper map2 = new Mapper();
		map2.setDisplayValue("其他资金");
		map2.setUnderlyValue("9");
		fundtypelist.add(map2);
		
	}
	public List getAcctList() {
		return acctList;
	}
	public void setAcctList(List acctList) {
		this.acctList = acctList;
	}
}
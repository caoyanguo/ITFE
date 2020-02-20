package com.cfcc.itfe.client.recbiz.voucherload;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.CheckFaultMsgDialogFacade;
import com.cfcc.itfe.client.dialog.CheckVoucherStatusMsgDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsQueryAmtDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataListDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TsOrganPK;
import com.cfcc.itfe.persistence.pk.TsTreasuryPK;
import com.cfcc.itfe.persistence.pk.TvPayoutmsgmainPK;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.metadata.ColumnMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TableMetaData;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author hjr
 * @time 13-07-05 00:26:06 子系统: RecBiz 模块:voucherLoad 组件:VoucherLoad
 */
@SuppressWarnings("unchecked")
public class VoucherLoadBean extends AbstractVoucherLoadBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(VoucherLoadBean.class);
	List<TvVoucherinfoDto> checkList = null;

	// 用户登录信息
	private ITFELoginInfo loginInfo;
	private String stamp;
	private List stampList = null;
	private String voucherType = null;
	private List voucherTypeList = null;
	private List bizTypeMapperList = null;
	private TvPayreckBigdataDto bigdatadto = null;
	private TvPayreckBigdataBackDto bigdatabackdto = null;
	private PagingContext bigdatapage;
	private PagingContext bigdatabackpage;
	private Map<String, TsQueryAmtDto> amtMap;
	private String searchtype = "";
	private List mainlist;
	public VoucherLoadBean() {
		super();
		dto = new TvVoucherinfoDto();
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		checkList = new ArrayList();
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		dto.setSorgcode(loginInfo.getSorgcode());
		bigdatadto = new TvPayreckBigdataDto();
		bigdatabackdto = new TvPayreckBigdataBackDto();
		mainlist = new ArrayList();
		pagingcontext = new PagingContext(this);
		bigdatapage = new PagingContext(this);
		bigdatabackpage = new PagingContext(this);
		voucherTypeList = new itferesourcepackage.SVoucherTypeEnumFactory()
				.getEnums(null);

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
	 * Direction: 导出数据
	 * ename: exportData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportData(Object o){
    	try {
    		if(checkList==null||checkList.size()<=0)
    		{
    			MessageDialog.openMessageDialog(null, "请选择需要导出的数据！");
				return "";
    		}
			// 选择保存路径
			DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
			String filePath = path.open();
			if ((null == filePath) || (filePath.length() == 0)) {
				MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
				return "";
			}
			StringBuffer vouchercontext = voucherLoadService.exportData(checkList); 
			String fileName = filePath+ File.separator+ checkList.get(0).getSvtcode() +checkList.get(0).getScreatdate() + ".xml";
			try {
				FileUtil.getInstance().writeFile(fileName, vouchercontext.toString());
				MessageDialog.openMessageDialog(null, "导出成功！");
			} catch (Exception e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
			
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null,e.toString());
		}
        return super.exportData(o);
    }
	/**
	 * Direction: 查询 ename: search 引用方法: viewers: * messages:
	 */
	public String search(Object o) {
		if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
			MessageDialog.openMessageDialog(null, "请先选择凭证类型！");
			return "";
		}
		if(loginInfo.getPublicparam().contains(",voucherload=trecode,"))
		{
			if(dto.getStrecode() == null|| dto.getStrecode().equals(""))
			{
				List<TsTreasuryDto> findTreCodeList = null;
				TsTreasuryDto findto = new TsTreasuryDto();
				findto.setSorgcode(loginInfo.getSorgcode());
				try {
					findTreCodeList = commonDataAccessService.findRsByDto(findto);
					dto.setStrecode(findTreCodeList.get(0).getStrecode());
				} catch (ITFEBizException e) {
				}
			}
		}
		refreshTable();
		return super.search(o);
	}

	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = new PageResponse();
		pageResponse = retrieve(pageRequest);
		if (pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录！");
		}
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: 读取凭证 ename: voucherLoad 引用方法: viewers: * messages:
	 */
	public String voucherLoad(Object o) {
		int count = 0;
		try {
			count = voucherLoadService.voucherLoad(dto.getSvtcode(), loginInfo
					.getSorgcode());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "调用服务出错，请确认已连接到服务器！");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}
		if (count == -1) {
			MessageDialog.openMessageDialog(null, "凭证库服务器连接超时，请重试！");
		} else {
			MessageDialog.openMessageDialog(null, "成功读取了" + count + "条记录，请查询！");
		}
		refreshTable();
		return super.voucherLoad(o);
	}

	/**
	 * Direction: 全选 ename: selectAll 引用方法: viewers: * messages:
	 */
	public String selectAll(Object o) {
		if (checkList == null || checkList.size() == 0) {
			checkList = new ArrayList();
			checkList.addAll(pagingcontext.getPage().getData());
		} else
			checkList.clear();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.selectAll(o);
	}

	/**
	 * Direction: 校验 ename: voucherVerify 引用方法: viewers: * messages:
	 */
	public String voucherVerify(Object o) {
		int count = 0;

		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要校验的记录！");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"选择的记录中凭证状态与数据库不一致,请重新查询后再进行操作！");
			return "";
		}
		for (int i = 0; i < checkList.size(); i++) {
			String ls_Status = checkList.get(i).getSstatus().trim();
			if (!ls_Status.equals(DealCodeConstants.VOUCHER_RECEIVE_SUCCESS)
					&& !ls_Status
							.equals(DealCodeConstants.VOUCHER_VALIDAT_FAIL)
					&& !ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT)) {
				MessageDialog
						.openMessageDialog(null,
								"只有状态为\"签收成功\" , \"校验失败\" "
										+ (loginInfo.getPublicparam().indexOf(
												",sh,") >= 0 ? ",\"校验中\"" : "")
										+ " 的凭证可执行此操作!");
				return "";
			}
			if (ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_FAIL)
					&& checkList.get(i).getSdemo().startsWith("业务要素勾兑失败")) {
				MessageDialog.openMessageDialog(null, "业务要素勾兑失败的凭证不能重新校验!");
				return "";
			}
		}

		try {
			count = voucherLoadService.voucherVerify(checkList);
			MessageDialog.openMessageDialog(null, "凭证校验" + checkList.size()
					+ "条，成功条数为：" + count);
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
			Exception e1 = new Exception("凭证校验操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		refreshTable();
		return super.voucherCommit(o);
	}

	/**
	 * Direction: 
成功 ename: voucherUpdateSuccess 引用方法: viewers: * messages:
	 */
	public String voucherUpdateSuccess(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要更改状态的记录！");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"选择的记录中凭证状态与数据库不一致,请重新查询后再进行操作！");
			return "";
		}
		if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
				dto.getSvtcode())) {
			MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");
			return "";
		}
		boolean flag = false;
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		for (TvVoucherinfoDto vDto : this.checkList) {
			TvVoucherinfoDto voucherDto = null;
			try {
				voucherDto = getDto(vDto);
			} catch (ITFEBizException e) {
				log.error("设置状态过程中出现错误", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e);
				return "";
			} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null,
						"超过规定时间未操作业务,会话已失效\r\n请重新登录！");
				return "";
			} catch (Exception e) {
				log.error(e);
				Exception e1 = new Exception("设置状态过程中出现异常！", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e1);
				return "";
			}
    		String ls_Status =  voucherDto.getSstatus();
			if((ls_Status.equals(DealCodeConstants.VOUCHER_SUCCESS) &&(vDto.getSstampid()!=null &&
					vDto.getSstampid().getBytes().length>0))){
				MessageDialog.openMessageDialog(null, "凭证"+vDto.getSvoucherno()+"回单成功不能更改状态！");    			
        		return "";			
    		}else if((ls_Status.equals(DealCodeConstants.VOUCHER_FAIL) &&(vDto.getSstampid()!=null &&
					vDto.getSstampid().getBytes().length>0))){
				MessageDialog.openMessageDialog(null, "凭证"+vDto.getSvoucherno()+"退回成功不能更改状态！");    			
        		return "";			
    		}else
				try {
					if(!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TCBS)||ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS))&&
							!((loginInfo.getPublicparam().contains(",updatestatus=all,"))&&(DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(ls_Status)||ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)||ls_Status.equals(DealCodeConstants.VOUCHER_CHECKSUCCESS)||ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS)||ls_Status.equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS)))
							&&
							!(ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)&&
							((StateConstant.COMMON_NO.equals(voucherLoadService.getIsitfecommit())&&(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)))||
							vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)||
							vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207) || vDto.getSvtcode().equals(
									MsgConstant.VOUCHER_NO_2252) ||
							(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)&&vDto.getShold2().equals(StateConstant.PAYOUT_PAY_CODE))))&&
							!(ls_Status.equals(DealCodeConstants.VOUCHER_RECIPE))&&
							!(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5407))&&
							!(ls_Status.equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK))&&
							!(ls_Status.equals(DealCodeConstants.VOUCHER_REGULATORY_FAULT))&&
							!(ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))){
						MessageDialog.openMessageDialog(null, "只有状态为\"TCBS处理失败\" ,  \"已发送\" , \"已收妥\" ,\t\n或状态为 \"校验成功\" 凭证类型为 2301 、2302 " +
								(loginInfo.getPublicparam().indexOf(",sh,")>=0?"、8207、 5253、 5351":"")+
								(loginInfo.getPublicparam().indexOf(",sh,")>=0?"\t\n或状态为 \"校验成功\"、支付方式为实拨、凭证类型为 2252":"")+
								",\t\n或状态为  \"处理成功\" 未签章 \t\n的凭证可执行此操作！");    			
						return "";
					}else if(!voucherCompare(voucherDto, checkList)){
						return "";
					}
				} catch (ITFEBizException e) {
					MessageDialog.openMessageDialog(null, "凭证更改状态失败！"+e.toString());    			
	        		return "";
				}
			checkList.add(voucherDto);
    		if(loginInfo.getPublicparam().contains(",display=true,"))
    		{
    			if((!"display".equals(voucherDto.getSext4()))&&(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)|| voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)))
    			{
    				MessageDialog.openMessageDialog(null, "凭证"+voucherDto.getSvoucherno()+"必须凭证还原展示后才可继续操作！");    			
            		return "";
    			}
    		}
    		if(loginInfo.getPublicparam().contains(",display=all,"))
    		{
    			if((!"display".equals(voucherDto.getSext4()))&&(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)))
    			{
    				MessageDialog.openMessageDialog(null, "凭证"+voucherDto.getSvoucherno()+"必须凭证还原展示后才可继续操作！");    			
            		return "";
    			}
    		}
    	}
		String msg = "需要主管授权才能更改凭证状态！";
		try {
			int i = 0;
			int j = 0;
			int k = 0;
			if (!flag) {
				if(!loginInfo.getPublicparam().contains(",adminconfrim=false,"))
				{
					if (!AdminConfirmDialogFacade.open(msg)) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						return null;
					}
				}
				TsCheckfailreasonDto checkDto = new TsCheckfailreasonDto();
				checkDto.setSorgcode(loginInfo.getSorgcode());
				List<TsCheckfailreasonDto> checkfail = commonDataAccessService
						.findRsByDto(checkDto);
				if (CheckVoucherStatusMsgDialogFacade.open(checkList,
						checkfail, commonDataAccessService)) {
					for (TvVoucherinfoDto vDto : checkList) {
						updateMainDto(vDto);
						commonDataAccessService.updateData(vDto);
						// if (vDto.getSvtcode().equals(
						// MsgConstant.VOUCHER_NO_2252)) {
						// vDto.setSvtcode(MsgConstant.VOUCHER_NO_2302);
						// updateMainDto(vDto);
						// }
						if (vDto.getSstatus().equals(
								DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)) {
							i++;
						} else if (vDto.getSstatus().equals(
								DealCodeConstants.VOUCHER_SUCCESS_BACK)){
							k++;
						}else {
							j++;
						}
					}
				}
			} else {
				for (TvVoucherinfoDto vDto : checkList) {
					vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					vDto.setSdemo("处理成功");
					updateMainDto(vDto);
					commonDataAccessService.updateData(vDto);
					i++;
				}
			}
			if (i > 0 || j > 0 || k > 0) {
				String updateMsg = "更改状态操作执行成功！\t\n";
				if (i > 0) {
					updateMsg = updateMsg + "成功设置凭证状态为 \"处理成功\" 条数：" + i
							+ "\t\n";
				}
				if (j > 0) {
					updateMsg = updateMsg + "成功设置凭证状态为 \"TCBS处理失败\" 条数：" + j;
				}
				if (k > 0) {
					updateMsg = updateMsg + "成功设置凭证状态为 \"退票成功\" 条数：" + k
							+ "\t\n";
				}
				MessageDialog.openMessageDialog(null, updateMsg);
				refreshTable();
				AdminConfirmDialogFacade.createSysLog("B_018", "业务凭证处理", "授权用户"
						+ loginInfo.getSuserName() + "更改凭证状态为"
						+ (i > 0 ? "处理成功" : "TCBS处理失败"));
			}
		} catch (ITFEBizException e) {
			log.error("设置状态过程中出现错误", e);
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
							"设置状态过程中出现异常！", e));
		}
		return super.voucherUpdateSuccess(o);
	}

	/**
	 * Direction: 提交 ename: voucherCommit 引用方法: viewers: * messages:
	 */
	public String voucherCommit(Object o) {
		int count = 0;

		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要提交的记录！");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"选择的记录中凭证状态与数据库不一致,请重新查询后再进行操作！");
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行提交操作吗？")) {
			return "";
		}
		if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
				dto.getSvtcode())) {
			MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");
			return "";
		}
		TsTreasuryPK trecodeDto = new TsTreasuryPK();
		if(dto.getStrecode()==null||"".equals(dto.getStrecode()))
			trecodeDto.setStrecode(checkList.get(0).getStrecode());
		else
			trecodeDto.setStrecode(dto.getStrecode());
		trecodeDto.setSorgcode(loginInfo.getSorgcode());
		TsTreasuryDto tredto = null;
		try {
			tredto = (TsTreasuryDto)commonDataAccessService.find(trecodeDto);
			if(tredto.getStreattrib().equals("2"))
			{
				
				for(int i=0;i<checkList.size();i++)
				{
					if(!DealCodeConstants.VOUCHER_RECIPE.equals(checkList.get(i).getSstatus()) && !DealCodeConstants.VOUCHER_SUCCESS.equals(checkList.get(i).getSstatus()))
					{
						MessageDialog.openMessageDialog(null, "代理库数据状态只有为已收妥、已回单才可重新提交TBS!");
						return "";
					}
				}
				if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
						.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行重新提交TBS操作吗？")) {
					return "";
				}else
				{
					for(int i=0;i<checkList.size();i++)
					{
						//如果是已经回单过的记录，打个标记，以便后续不再回单
						if(DealCodeConstants.VOUCHER_SUCCESS.equals(checkList.get(i).getSstatus())){
							checkList.get(i).setShold4(StateConstant.COMMON_NO);
						}
						if(MsgConstant.VOUCHER_NO_5207.equals(checkList.get(i).getSvtcode()))
						{
							checkList.get(i).setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
							checkList.get(i).setSext1("");
						}else
						{
							checkList.get(i).setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
							checkList.get(i).setSext1("");
						}
						
					}
					commonDataAccessService.updateDtos(checkList);
				}
				MessageDialog.openMessageDialog(null, "数据重新提交TBS成功!");
				return "";
			}
		} catch (ITFEBizException e2) {
		}
			
		
		// 陕西：只有校验成功并且比对成功的数据才可执行提交操作 -- start
		if ("260000000002".equals(loginInfo.getSorgcode())) {
			for (int i = 0; i < checkList.size(); i++) {
				try {
					String svtcode = getDto(checkList.get(i)).getSvtcode();
					String status = getDto(checkList.get(i)).getSstatus();
					String sdemo = getDto(checkList.get(i)).getSdemo();

					if (MsgConstant.VOUCHER_NO_2301.equals(svtcode)
							|| MsgConstant.VOUCHER_NO_2302.equals(svtcode)
							|| MsgConstant.VOUCHER_NO_5106.equals(svtcode)
							|| MsgConstant.VOUCHER_NO_5108.equals(svtcode)) {
						if (DealCodeConstants.VOUCHER_VALIDAT_SUCCESS
								.equals(status)
								&& !"数据比对成功".equals(sdemo)) {
							String msg = "部分数据尚未比对成功，是否继续";
							boolean flag = org.eclipse.jface.dialogs.MessageDialog
									.openConfirm(null, "提示", msg);
							if (flag == false) {
								return null;
							} else {
								if (AdminConfirmDialogFacade
										.open("需要主管授权才能继续执行操作")) {
									DisplayCursor.setCursor(SWT.CURSOR_ARROW);
								}
							}
						}

					}
				} catch (ITFEBizException e) {
					log.error("数据查询异常：" + e);
					MessageDialog.openMessageDialog(null, "数据查询异常！");
					return "";
				}
			}
		}
		// 陕西：只有校验成功并且比对成功的数据才可执行提交操作 -- end

		try {
			if (StateConstant.COMMON_YES.equals(voucherLoadService
					.getIscheckpayplan())) {// 河北通过前置进行额度控制，不提交TIPS，进行额度校验
				for (int i = 0; i < checkList.size(); i++) {
					String ls_Status = getDto(checkList.get(i)).getSstatus();
					if (!(ls_Status
							.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS))
							&& !(ls_Status
									.equals(DealCodeConstants.VOUCHER_FAIL_TCBS))) {
						MessageDialog.openMessageDialog(null,
								"只有状态为\"校验成功\" , \"TCBS处理失败\"的凭证可执行提交操作!");
						return "";
					}
				}
				MulitTableDto _dto = voucherLoadService
						.amtControlVerify(checkList);
				count = _dto.getTotalCount();
				List<TvVoucherinfoDto> chkerrList = _dto.getErrCheckList();
				String errText = "";
				for (TvVoucherinfoDto tmp : chkerrList) {
					errText = "凭证编号：" + tmp.getSvoucherno() + tmp.getSdemo()
							+ "\n";
					if (errText.length() > 100) {
						break;
					}
				}
				String info = "凭证提交   " + checkList.size() + " 条，成功条数为："
						+ count + "!";
				if (errText.length() > 0) {
					info += " 失败条数：" + (checkList.size() - count)
							+ "  错误信息如下：\n" + errText;
				}
				MessageDialog.openMessageDialog(null, info);
				refreshTable();

        	}else{//提交TIPS走此逻辑
	    		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||
	    				dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)){	
	        		if(voucherLoadService.getIsitfecommit().equals(StateConstant.COMMON_YES)){
	        			for(int i=0;i<checkList.size();i++){
			    			String ls_Status =  getDto(checkList.get(i)).getSstatus();
			    			if((loginInfo.getPublicparam().contains(",jgtr"+dto.getStrecode()+",")||loginInfo.getPublicparam().contains(",jgall,")||loginInfo.getPublicparam().contains(","+dto.getSvtcode()+"jg"+dto.getStrecode()+","))&&!ls_Status.equals(DealCodeConstants.VOUCHER_REGULATORY_SUCCESS)&&!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS) )&&!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
			    			{
			    				MessageDialog.openMessageDialog(null, "只有状态为 \"监管成功\"的凭证可执行此操作!");
			            		return "";
			    			}else if(!(ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS) )&&!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS) )&&!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED)){
			        			MessageDialog.openMessageDialog(null, "只有状态为\"校验成功\" , \"TIPS处理失败\"的凭证可执行此操作!");
			            		return "";
			        		}
			        		if(ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
	        				{
	        					Calendar calendar = Calendar.getInstance();
	        					calendar.setTime(TimeFacade.getCurrentDate());
	        					calendar.add(Calendar.DATE, -15);
	        					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
	        					String getdate = dateFormat.format(calendar.getTime());
	        					TvSendlogDto sdto = new TvSendlogDto();
	        					sdto.setSpackno(checkList.get(i).getSpackno());
	        					sdto.setStrecode(checkList.get(i).getStrecode());
	        					sdto.setSsendorgcode(checkList.get(i).getSorgcode());
	        					List<TvSendlogDto> slist = commonDataAccessService.findRsByDtoWithWhere(sdto, " and S_DATE>'"+getdate+"'");
	        					if(slist!=null&&slist.size()==1)
	        					{
	        						sdto = slist.get(0);
	        						if(!(sdto.getStitle()==null||"".equals(sdto.getStitle())))
	        						{
	        							MessageDialog.openMessageDialog(null, (loginInfo.getPublicparam().indexOf(",sh,")>=0?"单笔业务的直接支付业务、单笔业务的":"")+
			        					"实拨资金业务、收入退付业务只有状态为\"复核成功\" , \"TIPS处理失败\"的凭证可执行此操作!");
	        							return "";
	        						}
	        					}
	        				}
			        	}
	        		}else{
	        			MessageDialog.openMessageDialog(null, "集中支付清算凭证不能提交TIPS!");
	        			return "";
	        		}
	        	}else if((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207))
	        			||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)
	        			|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)){
		    		for(int i=0;i<checkList.size();i++){
		    			TvVoucherinfoDto tempdto= getDto(checkList.get(i));
		        		String ls_Status = tempdto.getSstatus();
		        		if(((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267))&&loginInfo.getPublicparam().indexOf(",sh,")<0)
			        			||((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)&&loginInfo.getPublicparam().indexOf(",sh,")>=0)
			        					&&!tempdto.getShold4().equals(StateConstant.BIZTYPE_CODE_BATCH))
			        			||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)){
		        			if((loginInfo.getPublicparam().contains(",jgtr"+dto.getStrecode()+",")||loginInfo.getPublicparam().contains(",jgall,")||loginInfo.getPublicparam().contains(","+dto.getSvtcode()+"jg"+dto.getStrecode()+","))&&!ls_Status.equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS)&&!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS) )&&!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
			    			{
			    				MessageDialog.openMessageDialog(null, "只有状态为 \"复核成功\"的凭证可执行此操作!");
			            		return "";
			    			}else if(!(ls_Status.equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS) )&&!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS) )&&!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED)){
		        				MessageDialog.openMessageDialog(null, (loginInfo.getPublicparam().indexOf(",sh,")>=0?"单笔业务的直接支付业务、单笔业务的":"")+
				        					"实拨资金业务、收入退付业务只有状态为\"复核成功\" , \"TIPS处理失败\"的凭证可执行此操作!");
				            		return "";
			        		}else
			        		{
			        			if(ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
		        				{
		        					Calendar calendar = Calendar.getInstance();
		        					calendar.setTime(TimeFacade.getCurrentDate());
		        					calendar.add(Calendar.DATE, -15);
		        					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
		        					String getdate = dateFormat.format(calendar.getTime());
		        					TvSendlogDto sdto = new TvSendlogDto();
		        					sdto.setSpackno(tempdto.getSpackno());
		        					sdto.setStrecode(tempdto.getStrecode());
		        					sdto.setSsendorgcode(tempdto.getSorgcode());
		        					List<TvSendlogDto> slist = commonDataAccessService.findRsByDtoWithWhere(sdto, " and S_DATE>'"+getdate+"'");
		        					if(slist!=null&&slist.size()==1)
		        					{
		        						sdto = slist.get(0);
		        						if(!(sdto.getStitle()==null||"".equals(sdto.getStitle())))
		        						{
		        							MessageDialog.openMessageDialog(null, (loginInfo.getPublicparam().indexOf(",sh,")>=0?"单笔业务的直接支付业务、单笔业务的":"")+
				        					"实拨资金业务、收入退付业务只有状态为\"复核成功\" , \"TIPS处理失败\"的凭证可执行此操作!");
		        							return "";
		        						}
		        					}
		        				}
			        		}
		        			/**
		        			 * 做调拨业务时，如果调拨凭证是第一次发过来，接收行行号未填写，并且之前从未补录过，
		        			 * 那么在第一次校验时，是校验不出来该凭证是否是调拨科目，如果没有校验出来，
		        			 * 那么发送到TCBS就会无法处理，所以在此处再次判断
		        			 * @author zhanghuibin
		        			 */
		        			String verifyInfo = voucherLoadService.verifyPayOutMoveFunSubject(tempdto);
		        			if(StringUtils.isNotBlank(verifyInfo)){
		        				MessageDialog.openMessageDialog(null, "凭证编号为["+tempdto.getSvoucherno()+"]的"+verifyInfo);
		        				return "";
		        			}
		        		}		        		
		        	}
		    	}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)||
		    			dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)){
		    		MessageDialog.openMessageDialog(null, "财政批量业务支付明细凭证、直接支付调整凭证和授权支付调整凭证不能提交TIPS!");
        			return "";
		    	}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8202))
		    	{
		    		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8202))
		    		{
		    			MessageDialog.openMessageDialog(null, "财政授权支付凭证8202不能提交TIPS!");
		    		}else if(!loginInfo.getPublicparam().contains(",5201=pbcpay,"))
		    		{
		    			MessageDialog.openMessageDialog(null, "财政直接支付凭证5201不能提交TIPS!");
		    		}
		    	}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2252)){
		    		for(int i=0;i<checkList.size();i++){
		    			String payTypeCode = checkList.get(i).getShold2().trim();//支付方式编码
		        		if(StringUtils.isNotBlank(payTypeCode)&&!payTypeCode.equals(MsgConstant.directPay)){
		        			MessageDialog.openMessageDialog(null, "只有支付方式为【直接支付】的收款银行退款通知凭证可提交TIPS!");
		        			return "";
		        		}
		        	}
		    	}else{
		    		for(int i=0;i<checkList.size();i++){
		    			String ls_Status =  getDto(checkList.get(i)).getSstatus();
		    			if((loginInfo.getPublicparam().contains(",jgtr"+dto.getStrecode()+",")||loginInfo.getPublicparam().contains(",jgall,")||loginInfo.getPublicparam().contains(","+dto.getSvtcode()+"jg"+dto.getStrecode()+","))&&!ls_Status.equals(DealCodeConstants.VOUCHER_REGULATORY_SUCCESS)&&!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS) )&&!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
		    			{
		    				MessageDialog.openMessageDialog(null, "只有状态为 \"监管成功\"的凭证可执行此操作!");
		            		return "";
		    			}else if((!ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS))&&(!ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS))&&(!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))){
		        			MessageDialog.openMessageDialog(null, "只有状态为\"校验成功\" , \"TIPS处理失败\"的凭证可执行此操作!");
		            		return "";
		        		}
		        		if(ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
        				{
        					Calendar calendar = Calendar.getInstance();
        					calendar.setTime(TimeFacade.getCurrentDate());
        					calendar.add(Calendar.DATE, -15);
        					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
        					String getdate = dateFormat.format(calendar.getTime());
        					TvSendlogDto sdto = new TvSendlogDto();
        					sdto.setSpackno(checkList.get(i).getSpackno());
        					sdto.setStrecode(checkList.get(i).getStrecode());
        					sdto.setSsendorgcode(checkList.get(i).getSorgcode());
        					List<TvSendlogDto> slist = commonDataAccessService.findRsByDtoWithWhere(sdto, " and S_DATE>'"+getdate+"'");
        					if(slist!=null&&slist.size()==1)
        					{
        						sdto = slist.get(0);
        						if(!(sdto.getStitle()==null||"".equals(sdto.getStitle())))
        						{
        							MessageDialog.openMessageDialog(null, (loginInfo.getPublicparam().indexOf(",sh,")>=0?"单笔业务的直接支付业务、单笔业务的":"")+
		        					"实拨资金业务、收入退付业务只有状态为\"复核成功\" , \"TIPS处理失败\"的凭证可执行此操作!");
        							return "";
        						}
        					}
        				}
		        	}
		    	}
	    		if(loginInfo.getPublicparam().contains(",display=true,"))
	    		{
	    			TvVoucherinfoDto voucherDto = null;
	    			for(int i=0;i<checkList.size();i++)
	    			{
	    				voucherDto = getDto(checkList.get(i));
		    			if((!"display".equals(voucherDto.getSext4()))&&(voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)|| voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)))
		    			{
		    				MessageDialog.openMessageDialog(null, "凭证"+voucherDto.getSvoucherno()+"必须凭证还原展示后才可继续操作！");    			
		            		return "";
		    			}
		    			if(loginInfo.getPublicparam().contains(",display=all,"))
		        		{
		        			if((!"display".equals(voucherDto.getSext4()))&&(voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)))
		        			{
		        				MessageDialog.openMessageDialog(null, "凭证"+voucherDto.getSvoucherno()+"必须凭证还原展示后才可继续操作！");    			
		                		return "";
		        			}
		        		}
		    			if(loginInfo.getPublicparam().contains("bigamt=true,")&&(voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)))
			    		{
			    			TsQueryAmtDto queryAmtDto =  getAmtMap().get(voucherDto.getSorgcode()+voucherDto.getStrecode()+voucherDto.getSvtcode());
							if (null!=queryAmtDto)
							{
								BigDecimal moneyfilter = queryAmtDto.getFendamt();
								if (moneyfilter.compareTo(voucherDto.getNmoney())<=0&&(!"1".equals(voucherDto.getSext2()))) 
								{
									if (!AdminConfirmDialogFacade.open("大额资金需要会计主管授权！")) {
										DisplayCursor.setCursor(SWT.CURSOR_ARROW);
										return null;
									}
								}
							}else
							{
								BigDecimal moneyfilter = new BigDecimal(100000000.00);
								if (moneyfilter.compareTo(voucherDto.getNmoney())<=0&&(!"1".equals(voucherDto.getSext2()))) 
								{
									if (!AdminConfirmDialogFacade.open("大额资金需要会计主管授权！")) {
										DisplayCursor.setCursor(SWT.CURSOR_ARROW);
										return null;
									}
								}
							}
			    		}
	    			}
	    		}
	    		if(loginInfo.getPublicparam().contains(",5207verifykc=true,"))
        		{
	    			BigDecimal kced = new BigDecimal(0);
	        		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267))
		    		{
	        			TvVoucherinfoDto findto = new TvVoucherinfoDto();
	        			findto.setSvtcode(dto.getSvtcode());
	        			findto.setScreatdate(dto.getScreatdate());
	        			findto.setStrecode(dto.getStrecode());
	        			findto.setSorgcode(dto.getSorgcode());
	        			List findlist = commonDataAccessService.findRsByDtoWithWhere(findto, " and S_STATUS in('"+DealCodeConstants.VOUCHER_SENDED+"','"
	        					+DealCodeConstants.VOUCHER_RECIPE+"','"
	        					+DealCodeConstants.VOUCHER_SUCCESS_NO_BACK+"','"
	        					+DealCodeConstants.VOUCHER_STAMP+"','"
	        					+DealCodeConstants.VOUCHER_SUCCESS+"')");
	        			if(findlist!=null&&findlist.size()>0)
	        			{
	        				for(int i=0;i<findlist.size();i++)
	        				{
	        					findto = (TvVoucherinfoDto)findlist.get(i);
	        					kced.add(findto.getNmoney());
	        				}
	        			}
		    		}
	        		TvVoucherinfoDto voucherDto = null;
	        		TrStockdayrptDto kc = new TrStockdayrptDto();
	        		TrStockdayrptDto kcdto = new TrStockdayrptDto();
	        		String getdate = null;
	        		Calendar calendar = null;
	        		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
	        		List datalist = null;
	        		for(int i=1;i<15;i++)
	        		{
	        			calendar = Calendar.getInstance();
	        			calendar.setTime(TimeFacade.getCurrentDate());
	        			calendar.add(Calendar.DATE, -i);
	        			getdate = dateFormat.format(calendar.getTime());
	        			kcdto.setSaccdate(getdate);
	        			kcdto.setSrptdate(getdate);
	        			datalist = commonDataAccessService.findRsByDto(kcdto);
	        			if(datalist!=null&&datalist.size()>0)
	        				break;
	        		}
	        		kc.setSaccdate(getdate);
	        		kc.setSrptdate(getdate);
	        		TvPayoutmsgmainPK pk = new TvPayoutmsgmainPK();
	        		TvPayoutmsgmainDto maindto = new TvPayoutmsgmainDto();
	        		List kcList = null;
	        		for(int i=0;i<checkList.size();i++)
	    			{
	    				voucherDto = checkList.get(i);
	    				pk.setSbizno(voucherDto.getSdealno());
	    				maindto = (TvPayoutmsgmainDto) commonDataAccessService.find(pk);
	    				if(voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267))
	    				{
	    					kced.add(voucherDto.getNmoney());
	    					kc.setStrecode(voucherDto.getStrecode());
	    					kc.setSaccno(maindto.getSpayeracct());
	    					kcList = commonDataAccessService.findRsByDto(kc);
	    					if(kcList!=null&&kcList.size()>0)
	    						kcdto = (TrStockdayrptDto)kcList.get(0);
	    					if(kcdto==null)
	    					{
	    						if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
	    								.getCurrentComposite().getShell(), "提示", "账户"+kc.getSaccno()+"库存报表不存在,确定不校验余额提交吗?")) {
	    							return "";
	    						}
	    					}else if(kced.compareTo(kcdto.getNmoneytoday()==null?new BigDecimal(0):kcdto.getNmoneytoday())>0)
	    					{
	    						if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
	    								.getCurrentComposite().getShell(), "提示", "账户"+kcdto.getSaccno()+"余额不足请查询TCBS余额,确定继续提交吗?")) {
	    							return "";
	    						}
	    					}
	    				}
	    			}
        		}
	    		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408)){
	    			Integer fcount = new Integer(0);
		    		for(int i=0;i<checkList.size();i++){
		    			if(checkList.get(i).getIcount()!=null)
		    			{
		    				fcount+=checkList.get(i).getIcount();
		    			}
		        		if(fcount.intValue()>1000){
		        			MessageDialog.openMessageDialog(null, "选中的记录明细笔数太多,Tips规定明细笔数不能超1000笔!");
		        			return "";
		        		}
		        	}
	    		}
				count=voucherLoadService.voucherCommit(checkList);
				MessageDialog.openMessageDialog(null, "凭证提交"+checkList.size()+"条，成功条数为："+count);
				refreshTable();
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return null;
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			return null;
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("凭证提交操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
			return null;
		}
		return super.voucherCommit(o);
	}

	/**
	 * Direction: 发送回单 ename: sendReturnVoucher 引用方法: viewers: * messages:
	 */
	public String sendReturnVoucher(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要回单的记录！");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"选择的记录中凭证状态与数据库不一致,请重新查询后再进行操作！");
			return "";
		}
		int count = 0;
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行回单操作吗？")) {
			return "";
		}
		boolean flag = true;
		// 对选中的列表进行操作时，重新查询数据库取得最新数据状态
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		for (TvVoucherinfoDto vDto : this.checkList) {
			TvVoucherinfoDto newvDto = new TvVoucherinfoDto();
			//上海2252直接支付退款不需要回单操作
			if(MsgConstant.VOUCHER_NO_2252.equals(vDto.getSvtcode().trim())){
				MessageDialog.openMessageDialog(null, "收款银行退款通知2252不需要做回单操作！");
				return "";
			}
			
			try {
				newvDto = getDto(vDto);
			} catch (ITFEBizException e) {
				Exception e1 = new Exception("重新查询数据出现错误！", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e1);
			}
			checkList.add(newvDto);
		}
		// 校验可发送电子凭证库的凭证状态
		for (TvVoucherinfoDto vDto : checkList) {
//			if(vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)
//			{
//				MessageDialog.openMessageDialog(null, "凭证"	+ vDto.getSvoucherno() + "已签退票章不能回单只能退回！");
//				return "";
//			}
			if (!vDto.getSstatus().equals(DealCodeConstants.VOUCHER_STAMP)
					&& !vDto.getSstatus().equals(
							DealCodeConstants.VOUCHER_SUCCESS))
				flag = false;
			else if (vDto.getSstatus()
					.equals(DealCodeConstants.VOUCHER_SUCCESS)) {
				String repeatVoucherFlag = getRepeatVoucherFlag(vDto);// 获取重新发送凭证辅助标志
				if (!repeatVoucherFlag.equals("0")) {
					vDto.setSreturnerrmsg(vDto.getSreturnerrmsg() + "$"
							+ repeatVoucherFlag);
				} else
					flag = false;
			}
			if(loginInfo.getPublicparam().contains(","+dto.getSvtcode()+"qm=true,")&&(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_RECIPE)||vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)))
			{
				flag = true;
			}
			if (!flag)
				break;
			if (loginInfo.getPublicparam().indexOf(",display=true,") >= 0) {
				if ((!"display".equals(vDto.getSext4()))
						&& (vDto.getSvtcode().equals(
								MsgConstant.VOUCHER_NO_2301)
								|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)|| vDto
								.getSvtcode().equals(
										MsgConstant.VOUCHER_NO_5207))) {
					MessageDialog.openMessageDialog(null, "凭证"
							+ vDto.getSvoucherno() + "必须凭证还原展示后才可继续操作！");
					return "";
				}
			}
			if(loginInfo.getPublicparam().contains(",display=all,"))
    		{
    			if((!"display".equals(vDto.getSext4()))&&(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)))
    			{
    				MessageDialog.openMessageDialog(null, "凭证"+vDto.getSvoucherno()+"必须凭证还原展示后才可继续操作！");    			
            		return "";
    			}
    		}
		}
		if (!flag) {
			MessageDialog
					.openMessageDialog(
							null,
							"请选择凭证状态为\"签章成功\" ，或凭证状态为\"已回单\""
									+ "详细信息为  \"回单状态:对方未接收\" ，\"回单状态:对方接收失败\"，\"回单状态:对方签收失败\"，"
									+ "\"回单状态:本方未发送 \"，\"回单状态:对方已退回\"的记录！");
			return "";
		}
		try {
			count = voucherLoadService.voucherReturnSuccess(checkList);
			MessageDialog.openMessageDialog(null, "凭证发送回单   "
					+ checkList.size() + " 条，成功条数为：" + count + " ！");
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
			Exception e1 = new Exception("凭证发送回单操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.sendReturnVoucher(o);
	}

	/**
	 * 
	 *重新发送凭证辅助标志： 0 不重发 1 回单状态为失败等状态的凭证 2 回单状态为已退回的凭证 3 [财政机构]回单状态为失败等状态的凭证 4
	 * [代理银行]回单状态为失败等状态的凭证 5 [财政机构]和[代理银行]回单状态均为失败等状态的凭证
	 * 
	 * @param dto
	 * @return
	 */
	public String getRepeatVoucherFlag(TvVoucherinfoDto dto) {
		String repeatVoucherFlag = "0";
		String[] errArr = { "回单状态:本方未发送","回单状态:对方未接收", "回单状态:对方接收失败", "回单状态:对方签收失败",
				 };// 回单状态：接收失败等状态
		for (int i = 0; i < errArr.length; i++) {
			if (dto.getSdemo().indexOf(errArr[i]) > -1)
				repeatVoucherFlag = "1";
			if (dto.getSdemo().indexOf("[财政机构]" + errArr[i]) > -1)
				repeatVoucherFlag = "3";
			if (dto.getSdemo().indexOf("[代理银行]" + errArr[i]) > -1)
				repeatVoucherFlag = "4";
			if (dto.getSdemo().indexOf("[财政机构]" + errArr[i]) > -1
					&& dto.getSdemo().indexOf("[代理银行]" + errArr[i]) > -1)
				repeatVoucherFlag = "5";
			if (!repeatVoucherFlag.equals("0"))
				break;
		}
		if (dto.getSdemo().indexOf("回单状态:对方已退回") > -1)
			repeatVoucherFlag = "2";
		return repeatVoucherFlag;
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
			ActiveXCompositeVoucherOcx.init(0,true);

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("凭证查看操作出现异常！"+e.toString(), e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		} catch (Error e) {
			log.error(e);
			Exception e1 = new Exception("凭证查看操作OCX调用出现系统错误，请联系开发人员！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.voucherView(o);
	}

	/**
	 * Direction: 凭证退回 ename: voucherBack 引用方法: viewers: * messages:
	 */
	public String voucherBack(Object o) {
		int count = 0;
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要退回的记录！");
			return "";
		}else
		{
			for(TvVoucherinfoDto temdto:checkList)
			{
				if(!(temdto.getSstampid()==null||"".equals(temdto.getSstampid())))
				{
					MessageDialog.openMessageDialog(null, "选中的记录中存在已签章的数据，请撤消签章后再退回！");
					return "";
				}
			}
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"选择的记录中凭证状态与数据库不一致,请重新查询后再进行操作！");
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要退回选中的记录吗？")) {
			return "";
		}
		if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
				dto.getSvtcode())) {
			MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");

			return "";

		}
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		checkList.addAll(this.checkList);
//<<<<<<< .mine
//		try {
//	  		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)
//    				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)){
//				for (int i = 0; i < checkList.size(); i++) {
//					String ls_Status = getDto(checkList.get(i)).getSstatus();
//					if (!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS
//							.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_RECEIVE_SUCCESS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_FAIL_TIPS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_VALIDAT_FAIL
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_FAIL_TCBS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_CHECKFAULT
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_CHECKSUCCESS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_CHECK_SUCCESS
//									.equals(ls_Status)
//							&& (loginInfo.getPublicparam().indexOf(",sh,") >= 0 ? !DealCodeConstants.VOUCHER_VALIDAT
//									.equals(ls_Status)
//									: true)) {
//						MessageDialog
//								.openMessageDialog(
//										null,
//										(loginInfo.getPublicparam().indexOf(
//												",sh,") >= 0 ? "直接支付业务、" : "")
//												+ "实拨资金业务和收入退付业务只有状态为  \"签收成功\" ,\"校验成功\" , "
//												+ (loginInfo.getPublicparam()
//														.indexOf(",sh,") >= 0 ? ",\"校验中\" , "
//														: "")
//												+ "\"校验失败\",\"审核失败\" ,\"审核成功\",\"复核成功\" \"TIPS处理失败\" , \"TCBS处理失败\" 的凭证可执行此操作!");
//						refreshTable();
//						return "";
//					}
//				}
//			} else {
//				for (int i = 0; i < checkList.size(); i++) {
//					String ls_Status = getDto(checkList.get(i)).getSstatus();
//					if (!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS
//							.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_RECEIVE_SUCCESS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_FAIL_TIPS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_VALIDAT_FAIL
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_FAIL_TCBS
//									.equals(ls_Status)
//							&& (loginInfo.getPublicparam().indexOf(",sh,") >= 0 ? !DealCodeConstants.VOUCHER_VALIDAT
//									.equals(ls_Status)
//									: true)) {
//						MessageDialog
//								.openMessageDialog(
//										null,
//										"请选择凭证状态为  \"签收成功\" ,\"校验成功\" , "
//												+ (loginInfo.getPublicparam()
//														.indexOf(",sh,") >= 0 ? ",\"校验中\" , "
//														: "")
//												+ "\"校验失败\" \"TIPS处理失败\" , \"TCBS处理失败\"  的记录！ ");
//						refreshTable();
//						return "";
//					}
//				}
//			}
//			boolean flag = false;
//			String status = checkList.get(0).getSstatus();
//			if (status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)
//					|| status.equals(DealCodeConstants.VOUCHER_RECEIVE_SUCCESS)
//					|| status.equals(DealCodeConstants.VOUCHER_CHECKSUCCESS)
//					|| status.equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS)
//					|| status.equals(DealCodeConstants.VOUCHER_VALIDAT)) {
//				flag = true;
//			}
//			if (flag) {
//				// 打开主管授权界面，手动输入退回失败原因
//				TsCheckfailreasonDto checkfailDto = new TsCheckfailreasonDto();
//=======
    	try {
    		if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0)
    		{
    			MessageDialog.openMessageDialog(null, "业务上要求不能退回,只能签退票章发送回单！");
            			refreshTable();
                		return "";
    		}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)
    				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)){
        		for(int i=0;i<checkList.size();i++){
            		String ls_Status =  getDto(checkList.get(i)).getSstatus();
            		if(!DealCodeConstants.VOUCHER_ACCEPT.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_FAIL.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_FAIL_TIPS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_FAIL_TCBS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_CHECKFAULT.equals(ls_Status)&&
            				!(ls_Status.equals(DealCodeConstants.VOUCHER_REGULATORY_FAULT))&&
            				!DealCodeConstants.VOUCHER_CHECKSUCCESS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_CHECK_SUCCESS.equals(ls_Status)&&
            				(loginInfo.getPublicparam().indexOf(",sh,")>=0?!DealCodeConstants.VOUCHER_VALIDAT.equals(ls_Status):true)&&
            				(!loginInfo.getPublicparam().contains(",backtype=wh,"))){
            			MessageDialog.openMessageDialog(null, (loginInfo.getPublicparam().indexOf(",sh,")>=0?"直接支付业务、":"")+
            					"实拨资金业务和收入退付业务只有状态为  \"签收成功\" ,\"校验成功\" , " +
            					(loginInfo.getPublicparam().indexOf(",sh,")>=0?",\"校验中\" , ":"")+
            					"\"校验失败\",\"审核失败\" ,\"审核成功\",\"复核成功\" \"TIPS处理失败\" , \"TCBS处理失败\" 的凭证可执行此操作!");
            			refreshTable();
                		return "";
            		}
            	}
        	}else{
        		for(int i=0;i<checkList.size();i++){
        			String ls_Status =  getDto(checkList.get(i)).getSstatus();
            		if((!(DealCodeConstants.VOUCHER_VALIDAT.equals(ls_Status)&&MsgConstant.VOUCHER_NO_5408.equals(checkList.get(i).getSvtcode())))&&!DealCodeConstants.VOUCHER_ACCEPT.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_FAIL.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_FAIL_TIPS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_FAIL_TCBS.equals(ls_Status)&&!(ls_Status.equals(DealCodeConstants.VOUCHER_REGULATORY_FAULT))&&
            				(loginInfo.getPublicparam().indexOf(",sh,")>=0?!DealCodeConstants.VOUCHER_VALIDAT.equals(ls_Status):true)){
            			MessageDialog.openMessageDialog(null, "请选择凭证状态为  \"签收成功\" ,\"校验成功\" , " +
            					(loginInfo.getPublicparam().indexOf(",sh,")>=0?",\"校验中\" , ":"")+
            					"\"校验失败\" \"TIPS处理失败\" , \"TCBS处理失败\"  的记录！ ");
            			refreshTable();
                		return "";
            		}
            	}
        	}    		
    		boolean flag=false;
			String status=checkList.get(0).getSstatus();
			if(status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)||status.equals(DealCodeConstants.VOUCHER_RECEIVE_SUCCESS)
					||status.equals(DealCodeConstants.VOUCHER_CHECKSUCCESS)||status.equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS)
					||status.equals(DealCodeConstants.VOUCHER_VALIDAT)){
				if(!loginInfo.getPublicparam().contains(",backtype=wh,")&&!dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207))
					flag=true;
				else if(!loginInfo.getPublicparam().contains(",backtype=wh,"))
					flag=true;
			}
			if(DealCodeConstants.VOUCHER_ACCEPT.equals(status)||DealCodeConstants.VOUCHER_RECEIVE_FAIL.equals(status))
			{
				if(loginInfo.getPublicparam().contains(",backverify,"))
				{
					if (!AdminConfirmDialogFacade.open("已读取或签收失败数据退回需要会计主管授权！")) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						return null;
					}else
					{
						flag=true;
					}
				}
			}
			if(flag){
				//打开主管授权界面，手动输入退回失败原因
				TsCheckfailreasonDto checkfailDto=new TsCheckfailreasonDto();
				checkfailDto.setSorgcode(dto.getSorgcode());
				List<TsCheckfailreasonDto> checkfail = commonDataAccessService
						.findRsByDto(checkfailDto);
				List<TvVoucherinfoDto> returnBackList = new ArrayList<TvVoucherinfoDto>();
				for (TvVoucherinfoDto vdto : checkList) {
					if (!CheckFaultMsgDialogFacade.open(vdto, checkfail,
							commonDataAccessService)) {
						returnBackList.add(vdto);
					}
				}
				checkList.removeAll(returnBackList);
			}
			String errText = "";
			if (checkList.size() > 0) {
				MulitTableDto _dto = voucherLoadService
						.voucherCheckRetBack(checkList);
				count = _dto.getTotalCount();
				List<TvVoucherinfoDto> chkerrList = _dto.getErrCheckList();
				for (TvVoucherinfoDto tmp : chkerrList) {
					errText = "凭证编号：" + tmp.getSvoucherno() + tmp.getSdemo()
							+ "\n";
					if (errText.length() > 100) {
						break;
					}
				}
			} else {
				MessageDialog.openMessageDialog(null, "请选择要退回的记录！");
				return "";
			}
			String info = "凭证退回   " + checkList.size() + " 条，成功条数为：" + count
					+ "!";
			if (errText.length() > 0) {
				info += " 失败条数：" + (checkList.size() - count) + "  错误信息如下：\n"
						+ errText;
			}
			MessageDialog.openMessageDialog(null, info);
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return null;
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			return "";
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("凭证退回操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}

		return "";

	}

	/**
	 * Direction: 签章 ename: transferCompletedStamp 引用方法: viewers: * messages:
	 */
	public String voucherStamp(Object o) {
		boolean ocxflag = false;
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		String stamp = null;
		TvVoucherinfoDto dto = new TvVoucherinfoDto();
		/*
		 * 凭证还原展示页面凭证签章
		 */
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
		/*
		 * 业务处理页面凭证签章
		 */
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
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"选择的记录中凭证状态与数据库不一致,请重新查询后再进行操作！");
			return "";
		}

		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行签章操作吗？")) {
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if((loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0)&&stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
			{
				if (!(DealCodeConstants.VOUCHER_FAIL_TCBS.equals(infoDto.getSstatus().trim()))) {
					MessageDialog.openMessageDialog(null, "请选择凭证状态为处理失败的记录！");
					return "";
				}
			}else
			{
				if(loginInfo.getPublicparam().contains(","+dto.getSvtcode()+"qm=true,"))
				{
					MessageDialog.openMessageDialog(null, dto.getSvtcode()+"业务不能签章只能签名请直接发送回单！");
					return "";
				}
				if(infoDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408))
				{
					if(!(DealCodeConstants.VOUCHER_RECIPE.equals(infoDto
							.getSstatus().trim()))) {
							MessageDialog.openMessageDialog(null, "请选择凭证状态为已收妥的记录！");
							return "";
						}
				}else
				{
					if(!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
						.getSstatus().trim()))) {
						MessageDialog.openMessageDialog(null, "请选择凭证状态为处理成功的记录！");
						return "";
					}
				}
			}
			
		}
		try {
			if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
					dto.getSvtcode())) {
				MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");
				return "";
			}
			// 查询当前签章用户信息
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
			/*
			 * 校验用户是否有当前选定印章签章权限
			 */
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
				/*
				 * 校验凭证是否已签当前选定印章，同一凭证不能重复签章
				 */
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
				/*
				 * 校验用户是否已签私章，同一用户只能签一次私章
				 */
				if (!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)
						&& !stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)
						&& !stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)
						&& !stamp.equals(MsgConstant.VOUCHERSAMP_BUSS)) {
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
				// 查询国库主体代码参数
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
				// 查询签章位置信息
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

				// 校验印章对应的证书ID、印章ID是否已经维护
				Boolean b = checkStampIDAndCertID(tDto, uDto, strecode,
						stampDto);
				if (!b) {
					return "";
				}
				for (TvVoucherinfoDto vDto : checkList) {
					if (vDto.getStrecode().equals(strecode)) {
						sinList = new ArrayList();
						sinList.add(vDto);
						stampid = vDto.getSstampid();
						String seq = sDto.getSstampsequence();
						/*
						 * 凭证按签章顺序签章：签章顺序号小的优先签章
						 */
						if (seq != null && !seq.equals("")) {
							Boolean bo = checkSignStampSeq(vDto, seq, sList,
									strecode, stampDto, stampid);
							if (!bo) {
								return "";
							}
						}
						/**
						 * 判断签章类型，进行签章
						 */
						SignStamp(vDto, seq, sList, tDto, sinList, sDto, uDto);
						vList.add(sinList);
					}
				}
				list.add(uDto);
				list.add(tDto);
				list.add(sDto);
				boolean tpz = false;
				for(TsStamppositionDto tedto:sList)
				{
					if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&MsgConstant.VOUCHERSAMP_ATTACH.equals(tedto.getSstamptype()))
						tpz = true;
				}
				if(tpz)
					list.add((sList.size()-1));
				else
					list.add(sList.size());
				list.add(vList);
				lists.add(list);
			}
			// 凭证签章
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
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("签章操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.voucherStamp(o);
	}

	/**
	 * Direction:签章撤销 ename: transferCompletedStampCancle 引用方法: viewers: *
	 * messages:
	 */
	public String stampCancle(Object o) {
		int count = 0;
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
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要签章撤销的记录！");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"选择的记录中凭证状态与数据库不一致,请重新查询后再进行操作！");
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
			if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus()))
				&&!((DealCodeConstants.VOUCHER_CHECKSUCCESS.equals(infoDto.getSstatus())||DealCodeConstants.VOUCHER_CHECKFAULT.equals(infoDto.getSstatus())||DealCodeConstants.VOUCHER_CHECK_SUCCESS.equals(infoDto.getSstatus())))
				&&!(infoDto.getSreturnerrmsg()!=null&&infoDto.getSreturnerrmsg().contains("签章位置已签章"))
				&&!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus()))
				&&!(DealCodeConstants.VOUCHER_FAIL_TCBS.equals(infoDto.getSstatus()))
				&&!(DealCodeConstants.VOUCHER_FAIL_TIPS.equals(infoDto.getSstatus()))
				&&(DealCodeConstants.VOUCHER_SUCCESS.equals(infoDto.getSstatus())||infoDto.getSstampuser()==null||"".equals(infoDto.getSstampuser())||infoDto.getSstampid()==null||"".equals(infoDto.getSstampid()))) {
				MessageDialog.openMessageDialog(null, "请选择凭证状态为TCBS处理失败、处理成功、签章成功的记录！");

				return "";
			}
		}

		try {
			if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
					dto.getSvtcode())) {
				MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");

				return "";

			}
			// 查询当前撤销签章用户信息
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
			/*
			 * 校验当前用户是否有当前选定印章签章权限
			 */
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
			/*
			 * 若当前用户无当前选定印章签章权限，可通过主管授权撤销签章
			 */
			boolean managerPermission = false;
			if (flag == false) {
				MessageDialog.openMessageDialog(null, "当前用户无  \""
						+ stampDto.getSstampname() + "\"  签章权限！，可通过主管授权撤销签章");
				String msg = "需要主管授权才能才能撤销签章！";
				if (!AdminConfirmDialogFacade.open("B_018", "业务凭证处理", "授权用户"
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
				/*
				 * 校验凭证是否已签当前用户所撤销的签章
				 */
				if((vDto.getSreturnerrmsg()!=null&&vDto.getSreturnerrmsg().contains("签章位置已签章")))
				{
					stampid = stamp+",";
					vDto.setSstampuser(loginInfo.getSuserCode());
					vDto.setSstampid(stamp+",");
				}else
				{
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
					} else if (stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)) {
						usercode = usercode + "^";
					}else if (stamp.equals(MsgConstant.VOUCHERSAMP_BUSS)) {
						usercode = usercode + "&";
					}
					/*
					 * 1、撤销签章按照谁签章谁撤销的原则 2 、若当前选定印章不是当前用户所签，可通过主管授权撤销签章
					 */
					String stampuserboolean = vDto.getSstampuser().split(",")[j];
					if (!stampuserboolean.equals(usercode)) {
						MessageDialog.openMessageDialog(null, "凭证编号："
								+ vDto.getSvoucherno() + "   \""
								+ stampDto.getSstampname()
								+ "\" 不是当前用户所签！可通过主管授权撤销签章");
						String msg = "需要主管授权才能才能撤销签章！";
						if (!AdminConfirmDialogFacade
								.open("B_018", "业务凭证处理", "授权用户"
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
				// 查询国库主体代码参数
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
				// 查询签章位置信息
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
				// 校验印章对应的证书ID、印章ID是否已经维护
				Boolean b = checkStampIDAndCertID(tDto, uDto, strecode,
						stampDto);
				if (!b) {
					return "";
				}

				for (TvVoucherinfoDto vDto : checkList) {
					if (vDto.getStrecode().equals(strecode)) {

						sinList = new ArrayList();
						sinList.add(vDto);
						stampid = vDto.getSstampid();
						String seq = sDto.getSstampsequence();
						/*
						 * 按照签章顺序依次撤销签章
						 */
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
								ActiveXCompositeVoucherOcx
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
						// 查询凭证签章痕迹、用户信息痕迹
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
												: (stamp
														.equals(MsgConstant.VOUCHERSAMP_ATTACH) ? user
														.substring(0, (user
																.length() - 1))
														: (stamp
																.equals(MsgConstant.VOUCHERSAMP_BUSS) ? user
																		.substring(0, (user
																				.length() - 1))
																		: user))));
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
			// 凭证撤销签章
			count = voucherLoadService.voucherStampCancle(lists);
			MessageDialog.openMessageDialog(null, "凭证撤销签章   "
					+ checkList.size() + " 条，成功条数为：" + count + " ！");
			if(checkList.size()!=count&&loginInfo.getPublicparam().contains(",canclestamp=verify,"))
			{
				if(!loginInfo.getPublicparam().contains(",canclestamp=notverify,"))
				{
					if (!AdminConfirmDialogFacade.open("选中的条数和成功条数不一致，是否会计主管强制撤消签章痕迹！")) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						return null;
					}
				}
				List upList = new ArrayList();
				for (TvVoucherinfoDto vDto : checkList) {
					vDto = getDto(vDto);
					int j = -1;
					String[] stampids = vDto.getSstampid().split(",");
					for (int i = 0; i < stampids.length; i++) {
						if (stamp.equals(stampids[i])) {
							j = i;
							break;
						}
					}
					if(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_STAMP)){
						vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					}
					// 查询凭证签章痕迹、用户信息痕迹
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
											: (stamp
													.equals(MsgConstant.VOUCHERSAMP_ATTACH) ? user
													.substring(0, (user
															.length() - 1))
													: (stamp
															.equals(MsgConstant.VOUCHERSAMP_BUSS) ? user
																	.substring(0, (user
																			.length() - 1))
																	: user))));
					userDto = (TsUsersDto) commonDataAccessService
							.findRsByDto(userDto).get(0);
					// 撤销签章成功更新印章痕迹
					vDto.setSstampid(vDto.getSstampid().substring(
							0,
							vDto.getSstampid().indexOf(
									sDto.getSstamptype() + ","))
							+ vDto.getSstampid().substring(
									vDto.getSstampid().indexOf(
											sDto.getSstamptype() + ",")
											+ (sDto.getSstamptype() + ",")
													.length(),
									vDto.getSstampid().length()));
					// 撤销签章成功更新签章用户痕迹
					if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_OFFICIAL))
						vDto.setSstampuser(vDto.getSstampuser().substring(
								0,
								vDto.getSstampuser().indexOf(
										userDto.getSusercode() + "#,"))
								+ vDto.getSstampuser().substring(
										vDto.getSstampuser().indexOf(
												userDto.getSusercode() + "#,")
												+ (userDto.getSusercode() + "#,")
														.length(),
										vDto.getSstampuser().length()));
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_ROTARY))
						vDto.setSstampuser(vDto.getSstampuser().substring(
								0,
								vDto.getSstampuser().indexOf(
										userDto.getSusercode() + "!,"))
								+ vDto.getSstampuser().substring(
										vDto.getSstampuser().indexOf(
												userDto.getSusercode() + "!,")
												+ (userDto.getSusercode() + "!,")
														.length(),
										vDto.getSstampuser().length()));
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_ATTACH))
						vDto.setSstampuser(vDto.getSstampuser().substring(
								0,
								vDto.getSstampuser().indexOf(
										userDto.getSusercode() + "^,"))
								+ vDto.getSstampuser().substring(
										vDto.getSstampuser().indexOf(
												userDto.getSusercode() + "^,")
												+ (userDto.getSusercode() + "^,")
														.length(),
										vDto.getSstampuser().length()));
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_BUSS))
						vDto.setSstampuser(vDto.getSstampuser().substring(
								0,
								vDto.getSstampuser().indexOf(
										userDto.getSusercode() + "&,"))
								+ vDto.getSstampuser().substring(
										vDto.getSstampuser().indexOf(
												userDto.getSusercode() + "&,")
												+ (userDto.getSusercode() + "&,")
														.length(),
										vDto.getSstampuser().length()));
					else
						vDto.setSstampuser(vDto.getSstampuser().substring(
								0,
								vDto.getSstampuser().indexOf(
										userDto.getSusercode() + ","))
								+ vDto.getSstampuser().substring(
										vDto.getSstampuser().indexOf(
												userDto.getSusercode() + ",")
												+ (userDto.getSusercode() + ",")
														.length(),
										vDto.getSstampuser().length()));
					upList.add(vDto);
				}
				commonDataAccessService.updateDtos(upList);
			}
			refreshTable();
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("凭证撤销签章操作出现异常！", e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), e1);
		}
		return super.stampCancle(o);
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
			if (!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus().trim())||DealCodeConstants.VOUCHER_SUCCESS.equals(infoDto.getSstatus().trim())||DealCodeConstants.VOUCHER_FAIL.equals(infoDto.getSstatus().trim())||DealCodeConstants.VOUCHER_SUCCESS_BACK.equals(infoDto.getSstatus()))) {
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
		return super.queryStatusReturnVoucher(o);
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

	public void refreshTable() {
		init();
		checkList.clear();
		refreshStampType();
	}

	public void updateMainDto(TvVoucherinfoDto vDto) throws ITFEBizException {
		String status = "";
		String xpaydate = "";
		if (vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)) {
			status = DealCodeConstants.DEALCODE_ITFE_SUCCESS;
			xpaydate = StringUtils.isBlank(vDto.getSext5()) ? TimeFacade
					.getCurrentStringTime() : vDto.getSext5();
		} else {
			status = DealCodeConstants.DEALCODE_ITFE_FAIL;
		}
		if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)) {
			TvPayoutmsgmainDto mainDto = new TvPayoutmsgmainDto();
			mainDto.setSbizno(vDto.getSdealno());
			try {
				mainDto = (TvPayoutmsgmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			mainDto.setSstatus(status);
			if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//厦门处理失败不更新清算金额清算日期
			{
				mainDto.setSxpayamt(new BigDecimal(0));
				mainDto.setSxpaydate("");
			}else
			{
				mainDto.setSxpayamt(vDto.getNmoney());
				mainDto.setSxpaydate(xpaydate);
			}
			//对拨款清单的处理2.0时代的实拨资金方式
			//根据国库代码判断
			TsTreasuryDto d = new TsTreasuryDto();
			IItfeCacheService cacheServer = (IItfeCacheService) ServiceFactory.getService(IItfeCacheService.class);
	        d.setStrecode(vDto.getStrecode());
	        List  list = cacheServer.cacheGetValueByDto(d);
			if (null!=list && list.size()>0) {
				TsTreasuryDto resdto = (TsTreasuryDto) list.get(0);
				if (StateConstant.COMMON_YES.equals(resdto.getSpayunitname())){
					mainDto.setSxagentbusinessno("0");
					String saddword;
					if (vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)) {
						saddword = DealCodeConstants.PROCESS_SUCCESS;
						status = DealCodeConstants.DEALCODE_ITFE_SUCCESS;
						xpaydate = StringUtils.isBlank(vDto.getSext5()) ? TimeFacade
								.getCurrentStringTime() : vDto.getSext5();
					} else {
						saddword = DealCodeConstants.PROCESS_FAIL;
						status = DealCodeConstants.DEALCODE_ITFE_FAIL;
					}
					mainDto.setSaddword(saddword);
					//更新明细
					TvPayoutmsgsubDto sub = new TvPayoutmsgsubDto();
					sub.setSbizno(vDto.getSdealno());
					List <TvPayoutmsgsubDto> sublist = commonDataAccessService.findRsByDto(sub);
					List updateDtos = new ArrayList();
					for (TvPayoutmsgsubDto t : sublist ) {
						t.setSxaddword(saddword);
						t.setSxagentbusinessno(t.getStrasno());
						t.setSxpaydate(xpaydate);
						t.setSstatus(status);
						if (vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)) {
							  t.setSxpayamt(t.getNmoney());
						}else{
							  t.setSxpayamt(BigDecimal.ZERO);
						}
						updateDtos.add(t);
					}
					if (updateDtos.size()>0) {
						commonDataAccessService.updateDtos(updateDtos);
					}
				
				}
			}
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)) {
			TvDwbkDto mainDto = new TvDwbkDto();
			mainDto.setSbizno(vDto.getSdealno());
			try {
				mainDto = (TvDwbkDto) commonDataAccessService.findRsByDto(
						mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			mainDto.setSstatus(status);
			if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//厦门处理失败不更新清算金额清算日期
			{
				mainDto.setXpayamt(new BigDecimal(0));
			}else
			{
				mainDto.setXpayamt(vDto.getNmoney());
			}
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
			TvGrantpaymsgmainDto mainDto = new TvGrantpaymsgmainDto();
			mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
			TvGrantpaymsgsubDto subDto = new TvGrantpaymsgsubDto();
			subDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
			List<TvGrantpaymsgmainDto> mainDtoList = commonDataAccessService
					.findRsByDto(mainDto);
			if (mainDtoList == null || mainDtoList.size() == 0)
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！");
			List<TvGrantpaymsgsubDto> subDtoList = commonDataAccessService
					.findRsByDto(subDto);
			if (subDtoList == null || subDtoList.size() == 0)
				throw new ITFEBizException("查询子表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的子表记录不存在！");
			for (TvGrantpaymsgmainDto maindto : mainDtoList) {
				if(!(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0))//厦门处理失败不更新清算金额清算日期
				{
					maindto.setSxacctdate(xpaydate);
				}
				maindto.setSstatus(status);
				commonDataAccessService.updateData(maindto);
			}
			for (TvGrantpaymsgsubDto subdto : subDtoList) {
				subdto.setSstatus(status);
				commonDataAccessService.updateData(subdto);
			}
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {
			TvDirectpaymsgmainDto mainDto = new TvDirectpaymsgmainDto();
			mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
			try {
				mainDto = (TvDirectpaymsgmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			if(!(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0))//厦门处理失败不更新清算金额清算日期
			{
				mainDto.setSxacctdate(xpaydate);
			}
			mainDto.setSstatus(status);
			commonDataAccessService.updateData(mainDto);
		}else if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5671)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408)){
			TvNontaxmainDto maindto = new TvNontaxmainDto();
			maindto.setSdealno(vDto.getSdealno());
			try {
				maindto = (TvNontaxmainDto) commonDataAccessService.findRsByDto(maindto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			maindto.setSstatus(status);
			if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//厦门处理失败不更新清算金额清算日期
			{
				maindto.setNxmoney(new BigDecimal(0));
				maindto.setSxtradate("");
			}else
			{
				maindto.setNxmoney(vDto.getNmoney());
				maindto.setSxtradate(xpaydate);
			}
			commonDataAccessService.updateData(maindto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)) {
			TvPayreckBankDto mainDto = new TvPayreckBankDto();
			mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
			try {
				mainDto = (TvPayreckBankDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			/**
			 * TS_CONVERTBANKTYPE “XPaySndBnkNo支付发起行行号” 填写3144中填写代理银行行号
			 * 手工更新没有对应的值 “XAddWord附言” 填写 原交易流水号
			 */

			if (mainDto.getSbookorgcode().indexOf("09") >= 0
					&& mainDto.getSpaymode().equals("1")) {
				mainDto.setSxpaysndbnkno(mainDto.getSagentbnkcode());
				mainDto.setSaddword("011无法获取");
			}
			if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//厦门处理失败不更新清算金额清算日期
			{
				mainDto.setSxpayamt(new BigDecimal(0));
			}else
			{
				mainDto.setSxcleardate(CommonUtil.strToDate(xpaydate));
				mainDto.setSxpayamt(vDto.getNmoney());
			}
			mainDto.setSresult(status);
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)) {
			// if (loginInfo.getPublicparam().indexOf("sh,") > 0
			// && "91".equals(vDto.getShold2()) && null != checkList
			// && checkList.size() > 0) {
			// List<TvVoucherinfoDto> voucher2252 = new
			// ArrayList<TvVoucherinfoDto>();
			// TvVoucherinfoDto tmp = ((TvVoucherinfoDto) vDto.clone());
			// tmp.setSvtcode(MsgConstant.VOUCHER_NO_2252);
			// voucher2252.add(tmp);
			// voucherLoadService.voucherCommit(voucher2252);
			// }
			TvPayreckBankBackDto mainDto = new TvPayreckBankBackDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (TvPayreckBankBackDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			/**
			 * 核算主体以09开头的默认为上海 “XPaySndBnkNo支付发起行行号” 填写3144中填写代理银行行号
			 * “XAddWord附言” 填写 原交易流水号
			 */
			if (mainDto.getSbookorgcode().indexOf("09") >= 0
					&& mainDto.getSpaymode().equals("1")) {
				mainDto.setSxpaysndbnkno(mainDto.getSpaysndbnkno());
				mainDto.setSaddword("011无法获取");
			}
			if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//厦门处理失败不更新清算金额清算日期
			{
				mainDto.setSxpayamt(new BigDecimal(0));
			}else
			{
				mainDto.setSxcleardate(CommonUtil.strToDate(xpaydate));
				mainDto.setSxpayamt(vDto.getNmoney().negate());
			}
			mainDto.setSstatus(status);
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)) {
			TfGrantpayAdjustmainDto mainDto = new TfGrantpayAdjustmainDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (TfGrantpayAdjustmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			mainDto.setSxaccdate(xpaydate);
			mainDto.setSstatus(status);
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)) {
			TfDirectpayAdjustmainDto mainDto = new TfDirectpayAdjustmainDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (TfDirectpayAdjustmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			mainDto.setSxpaydate(xpaydate);
			mainDto.setSstatus(status);
			mainDto.setNxpayamt(mainDto.getNxpayamt());
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {
			TfDirectpaymsgmainDto mainDto = new TfDirectpaymsgmainDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (TfDirectpaymsgmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			mainDto.setSxpaydate(xpaydate);
			mainDto.setSstatus(status);
			mainDto.setNxpayamt(mainDto.getNpayamt());
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)) {
			TfPaymentDetailsmainDto mainDto = new TfPaymentDetailsmainDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (TfPaymentDetailsmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			mainDto.setSxpaydate(xpaydate);
			mainDto.setSstatus(status);
			mainDto.setNxsumamt(mainDto.getNsumamt());
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5407)) {
			TvInCorrhandbookDto mainDto = new TvInCorrhandbookDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (commonDataAccessService.findRsByDto(mainDto)==null||commonDataAccessService.findRsByDto(mainDto).size()<=0)?null:(TvInCorrhandbookDto) commonDataAccessService.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("查询主表信息异常：与凭证："
						+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
			}
			if(mainDto==null)
				return;
			mainDto.setSstatus(status);
			mainDto.setDxacctdate(xpaydate);
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2252)) {
			if (loginInfo.getPublicparam().indexOf("sh,") > 0
					&& "91".equals(vDto.getShold2()) && null != checkList
					&& checkList.size() > 0) {
				//如果更新失败不调用后台复制表
				if (DealCodeConstants.DEALCODE_ITFE_FAIL.equals(vDto.getSstatus().trim()) || DealCodeConstants.VOUCHER_FAIL_TCBS.equals(vDto.getSstatus().trim())) {
					TfPaybankRefundmainDto mainDto = new TfPaybankRefundmainDto();
					mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
					try {
						mainDto = (TfPaybankRefundmainDto) commonDataAccessService
								.findRsByDto(mainDto).get(0);
					} catch (java.lang.IndexOutOfBoundsException e) {
						log.error(e);
						throw new ITFEBizException("查询主表信息异常：与凭证："
								+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
					}
					mainDto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
					mainDto.setNbackmoney(BigDecimal.ZERO);
					commonDataAccessService.updateData(mainDto);
				} else {
					List<TvVoucherinfoDto> voucher2252 = new ArrayList<TvVoucherinfoDto>();
					TvVoucherinfoDto tmp = ((TvVoucherinfoDto) vDto.clone());
					tmp.setSvtcode(MsgConstant.VOUCHER_NO_2252);
					voucher2252.add(tmp);
					voucherLoadService.voucherCommit(voucher2252);
				}
			} else {
				TvPayreckBankBackDto mainDto = new TvPayreckBankBackDto();
				mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
				try {
					mainDto = (TvPayreckBankBackDto) commonDataAccessService
							.findRsByDto(mainDto).get(0);
				} catch (java.lang.IndexOutOfBoundsException e) {
					log.error(e);
					throw new ITFEBizException("查询主表信息异常：与凭证："
							+ vDto.getSvoucherno() + "对应的主表记录不存在！", e);
				}
				/**
				 * 核算主体以09开头的默认为上海 “XPaySndBnkNo支付发起行行号” 填写3144中填写代理银行行号
				 * “XAddWord附言” 填写 原交易流水号
				 */
				if (mainDto.getSbookorgcode().indexOf("09") >= 0
						&& mainDto.getSpaymode().equals("1")) {
					mainDto.setSxpaysndbnkno(mainDto.getSpaysndbnkno());
					mainDto.setSaddword("011无法获取");
				}
				if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//厦门处理失败不更新清算金额清算日期
				{
					mainDto.setSxpayamt(new BigDecimal(0));
				}else
				{
					mainDto.setSxcleardate(CommonUtil.strToDate(xpaydate));
					mainDto.setSxpayamt(vDto.getNmoney().negate());
				}
				mainDto.setSstatus(status);
				commonDataAccessService.updateData(mainDto);
			}
		}
	}

	public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException {
		return voucherLoadService.voucherStampXml(vDto);
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
		try {
			if(searchtype!=null&&!"".equals(searchtype))
			{
				if(searchtype!=null&&MsgConstant.VOUCHER_NO_2301.equals(searchtype))
				{
					TvPayreckBigdataListDto bigdatalistdto = new TvPayreckBigdataListDto();
					bigdatalistdto.setIvousrlno(bigdatadto.getIvousrlno());
					page = commonDataAccessService.findRsByDtoPaging(bigdatalistdto, pageRequest,null,null);
				}else
				{
					TvPayreckBigdataBackListDto bigdatabacklistdto = new TvPayreckBigdataBackListDto();
					bigdatabacklistdto.setIvousrlno(bigdatabackdto.getIvousrlno());
					page = commonDataAccessService.findRsByDtoPaging(bigdatabacklistdto, pageRequest,null,null);
				}
			}else
			{
				if(loginInfo.getPublicparam().contains((",vouchersearchhold=5,")))
				{
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(TimeFacade.getCurrentDate());
					calendar.add(Calendar.DATE, -5);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
					String getdate = dateFormat.format(calendar.getTime());
					page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,
							"1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+dto.getSorgcode()+(dto.getStrecode()==null?"":"' and S_TRECODE='"+dto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+dto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_SUCCESS_BACK+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_SUCCESS+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_FAIL+"')", "S_RECVTIME DESC");
				}else if(loginInfo.getPublicparam().contains((",vouchersearchhold=15,")))
				{
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(TimeFacade.getCurrentDate());
					calendar.add(Calendar.DATE, -15);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
					String getdate = dateFormat.format(calendar.getTime());
					page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,
							"1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+dto.getSorgcode()+(dto.getStrecode()==null?"":"' and S_TRECODE='"+dto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+dto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_SUCCESS_BACK+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_SUCCESS+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_FAIL+"')", "S_RECVTIME DESC");
				}else
				{
					page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,
							"1=1 AND S_PAYBANKCODE IS NOT NULL ", "S_RECVTIME DESC");
				}
			}
			return page;

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Throwable e) {
			log.error(e);
			Exception e1 = new Exception("查询数据异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.retrieve(pageRequest);
	}

	public TvVoucherinfoDto getDto(TvVoucherinfoDto dto)
			throws ITFEBizException {
		TvVoucherinfoPK pk = new TvVoucherinfoPK();
		pk.setSdealno(dto.getSdealno());
		return (TvVoucherinfoDto) commonDataAccessService.find(pk);
	}

	/**
	 * 刷新签章类型
	 * 
	 */
	public void refreshStampType() {
		TsStamppositionDto tsDto = new TsStamppositionDto();
		tsDto.setSorgcode(dto.getSorgcode());
		tsDto.setSvtcode(dto.getSvtcode());
		tsDto.setStrecode(dto.getStrecode());
		Set set = new HashSet();
		TsStamppositionDto sDto = new TsStamppositionDto();
		List<TsStamppositionDto> tList = null;
		stampList = new ArrayList();
		stamp = null;
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
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List checkList) {
		this.checkList = checkList;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public List getStampList() {
		return stampList;
	}

	public void setStampList(List stampList) {
		this.stampList = stampList;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		// 商行划款和商行退款区分是直接支付还是授权
		if (voucherType.equals("230101") || voucherType.equals("230201")) {
			this.dto.setSext1("0");// 支付方式为直接支付
			this.dto.setSvtcode(voucherType.substring(0, 4));
		} else if (voucherType.equals("230102") || voucherType.equals("230202")) {
			this.dto.setSvtcode(voucherType.substring(0, 4));
			this.dto.setSext1("1");// 支付方式为授权支付
		} else {
			this.dto.setSvtcode(voucherType);
		}
		refreshStampType();
		List contreaNamesVisibleFalse = new ArrayList();//关闭显示凭证一览表
		List contreaNamesVisibbleTrue = new ArrayList();//显示凭证一览表
		//动态显示
		if (((MsgConstant.VOUCHER_NO_5207.equals(voucherType)|| MsgConstant.VOUCHER_NO_5267.equals(voucherType)
				||MsgConstant.VOUCHER_NO_5209.equals(voucherType)||MsgConstant.VOUCHER_NO_5407.equals(voucherType)) 
				&&loginInfo.getPublicparam().indexOf(",sh,")<0)) {
			contreaNamesVisibleFalse.add("凭证查询一览表代理行");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表非税收入");
			contreaNamesVisibbleTrue.add("凭证查询一览表出票单位");
			List<ContainerMetaData> containerMetaData = MVCUtils.setContentAreasToVisible(editor, contreaNamesVisibbleTrue);
			for (ContainerMetaData metadata : containerMetaData) {
				if ("凭证查询一览表出票单位".equals(metadata.name))
				{
					List controls = metadata.controlMetadatas;
					TableMetaData tablemd = (TableMetaData) controls.get(0);
					for (int i = 0; i < tablemd.columnList.size(); i++) {
						if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
							ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
							if(coletadata.title.equals("收款人账号")||coletadata.title.equals("收款人名称")||coletadata.title.equals("收款银行名称")||coletadata.title.equals("付款人账号")||coletadata.title.equals("付款人名称"))
							{
								if(loginInfo.getPublicparam().contains(",voucherdisplaymain=true,"))
									coletadata.visible = true;
								else
									coletadata.visible = false;
							}
							if(coletadata.title.equals("打印次数"))
							{
								if(loginInfo.getPublicparam().contains(",printcount=true,"))
									coletadata.visible = true;
								else
									coletadata.visible = false;
							}
						}
					}
				}
			}
		} else if (((MsgConstant.VOUCHER_NO_2301.equals(voucherType)
				|| "230101".equals(voucherType) || "230102".equals(voucherType)) && loginInfo
				.getPublicparam().indexOf(",sh,") < 0)
				|| MsgConstant.VOUCHER_NO_2302.equals(voucherType)
				|| "230201".equals(voucherType)
				|| "230202".equals(voucherType)
				|| (MsgConstant.VOUCHER_NO_5106.equals(voucherType) && loginInfo
						.getPublicparam().indexOf(",sh,") < 0)
				|| (MsgConstant.VOUCHER_NO_5108.equals(voucherType) && loginInfo
						.getPublicparam().indexOf(",sh,") < 0)
				|| MsgConstant.VOUCHER_NO_2252.equals(voucherType)) {
			contreaNamesVisibleFalse.add("凭证查询一览表出票单位");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表非税收入");
			contreaNamesVisibbleTrue.add("凭证查询一览表代理行");
			List<ContainerMetaData> containerMetaData = MVCUtils.setContentAreasToVisible(editor, contreaNamesVisibbleTrue);
			for (ContainerMetaData metadata : containerMetaData) {
				if ("凭证查询一览表代理行".equals(metadata.name))
				{
					List controls = metadata.controlMetadatas;
					TableMetaData tablemd = (TableMetaData) controls.get(0);
					for (int i = 0; i < tablemd.columnList.size(); i++) {
						if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
							ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
							if(coletadata.title.equals("打印次数"))
							{
								if(loginInfo.getPublicparam().contains(",printcount=true,"))
									coletadata.visible = true;
								else
									coletadata.visible = false;
							}
							if(coletadata.title.equals("预算种类"))
							{
								if(loginInfo.getPublicparam().contains(",quotabudgettype,"))
									coletadata.visible = true;
								else
									coletadata.visible = false;
							}
							if(coletadata.title.equals("匹配凭证编号"))
							{
								if((MsgConstant.VOUCHER_NO_5106.equals(voucherType)||MsgConstant.VOUCHER_NO_5108.equals(voucherType)))
								{
									TsOrganPK ipk = new TsOrganPK();
									ipk.setSorgcode(loginInfo.getSorgcode());
									TsOrganDto dto=null;
									try {
										dto = (TsOrganDto)commonDataAccessService.find(ipk);
									} catch (ITFEBizException e) {
									}
									if(dto!=null&&dto.getSofprovorgcode()!=null&&"hold1".contains(dto.getSofprovorgcode()))
										coletadata.visible = true;
									else
										coletadata.visible = false;
								}else
									coletadata.visible = false;
							}
						}
					}
				}
			}
		} else if (((MsgConstant.VOUCHER_NO_2301.equals(voucherType)
				|| "230101".equals(voucherType) || "230102".equals(voucherType)) && loginInfo
				.getPublicparam().indexOf(",sh,") >= 0)
				|| MsgConstant.VOUCHER_NO_5253.equals(voucherType)
				|| MsgConstant.VOUCHER_NO_5351.equals(voucherType)) {
			contreaNamesVisibleFalse.add("凭证查询一览表出票单位");
			contreaNamesVisibleFalse.add("凭证查询一览表代理行");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表非税收入");
			contreaNamesVisibbleTrue.add("凭证查询一览表勾兑凭证类型");
		} else if ((MsgConstant.VOUCHER_NO_5106.equals(voucherType) && loginInfo
				.getPublicparam().indexOf(",sh,") >= 0)
				|| (MsgConstant.VOUCHER_NO_5108.equals(voucherType) && loginInfo
						.getPublicparam().indexOf(",sh,") >= 0)) {
			contreaNamesVisibleFalse.add("凭证查询一览表出票单位");
			contreaNamesVisibleFalse.add("凭证查询一览表代理行");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表非税收入");
			contreaNamesVisibbleTrue.add("凭证查询一览表勾兑凭证类型勾兑编号");
		} else if (MsgConstant.VOUCHER_NO_8207.equals(voucherType)) {
			contreaNamesVisibleFalse.add("凭证查询一览表出票单位");
			contreaNamesVisibleFalse.add("凭证查询一览表代理行");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表非税收入");
			contreaNamesVisibbleTrue.add("凭证查询一览表勾兑凭证类型勾兑编号业务类型编码");
		} else if ((MsgConstant.VOUCHER_NO_5207.equals(voucherType) && loginInfo
				.getPublicparam().indexOf(",sh,") >= 0)
				|| MsgConstant.VOUCHER_NO_5201.equals(voucherType)) {
			contreaNamesVisibleFalse.add("凭证查询一览表出票单位");
			contreaNamesVisibleFalse.add("凭证查询一览表代理行");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号");
			contreaNamesVisibleFalse.add("凭证查询一览表非税收入");
			contreaNamesVisibbleTrue.add("凭证查询一览表勾兑凭证类型业务类型编码");
		}else if (MsgConstant.VOUCHER_NO_5671.equals(voucherType)||MsgConstant.VOUCHER_NO_5408.equals(voucherType))
		{
			contreaNamesVisibleFalse.add("凭证查询一览表出票单位");
			contreaNamesVisibleFalse.add("凭证查询一览表代理行");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型业务类型编码");
			contreaNamesVisibbleTrue.add("凭证查询一览表非税收入");
		}else
		{
			contreaNamesVisibleFalse.add("凭证查询一览表代理行");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型勾兑编号业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表勾兑凭证类型业务类型编码");
			contreaNamesVisibleFalse.add("凭证查询一览表非税收入");
			contreaNamesVisibbleTrue.add("凭证查询一览表出票单位");
			List<ContainerMetaData> containerMetaData = MVCUtils.setContentAreasToVisible(editor, contreaNamesVisibbleTrue);
			for (ContainerMetaData metadata : containerMetaData) {
				if ("凭证查询一览表出票单位".equals(metadata.name))
				{
					List controls = metadata.controlMetadatas;
					TableMetaData tablemd = (TableMetaData) controls.get(0);
					for (int i = 0; i < tablemd.columnList.size(); i++) {
						if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
							ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
							if(coletadata.title.equals("打印次数"))
							{
								if(loginInfo.getPublicparam().contains(",printcount=true,"))
									coletadata.visible = true;
								else
									coletadata.visible = false;
								break;
							}
						}
					}
					break;
				}
			}
		}
		MVCUtils.setContentAreaVisible(editor, contreaNamesVisibleFalse, false);
		MVCUtils.setContentAreaVisible(editor, contreaNamesVisibbleTrue, true);
		pagingcontext.setPage(new PageResponse());
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged();

	}

	public boolean voucherCompare(TvVoucherinfoDto dto, List checkList) {
		if (loginInfo.getPublicparam().indexOf(",sh,") < 0)
			return true;
		if (!(((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201) || dto
				.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)) && dto
				.getShold4().equals(StateConstant.BIZTYPE_CODE_BATCH)) || dto
				.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)))
			return true;
		TvVoucherinfoDto tempdto = new TvVoucherinfoDto();
		tempdto.setStrecode(dto.getStrecode());
		tempdto.setSvtcode(dto.getScheckvouchertype());
		tempdto.setShold4(StateConstant.BIZTYPE_CODE_BATCH);
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
			tempdto.setSvoucherno(dto.getShold3());
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)) {
			tempdto.setShold3(dto.getSvoucherno());
		}
		List list = null;
		try {
			list = commonDataAccessService.findRsByDto(tempdto,
					" ORDER BY S_STATUS DESC");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"查询凭证信息异常!", e));
			return false;
		}
		if (list == null || list.size() == 0) {
			MessageDialog.openMessageDialog(null, "对应的"
					+ dto.getScheckvouchertype() + "凭证不存在。");
			return false;
		}
		tempdto = (TvVoucherinfoDto) list.get(0);
		if (tempdto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)) {
			if (tempdto.getSstatus().equals(
					DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)
					|| tempdto.getSstatus().equals(
							DealCodeConstants.VOUCHER_FAIL_TCBS)
					|| (tempdto.getSstatus().equals(
							DealCodeConstants.VOUCHER_SUCCESS_NO_BACK) && StringUtils
							.isBlank(tempdto.getSstampid()))) {
				checkList.add(tempdto);
				return true;
			}
		} else if (tempdto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {
			if (tempdto.getSstatus().equals(
					DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)) {
				MessageDialog.openMessageDialog(null, "对应的"
						+ dto.getScheckvouchertype()
						+ "凭证状态为\"校验成功\"，未提交TIPS，不能更新状态!");
				return false;
			}
			if (tempdto.getSstatus()
					.equals(DealCodeConstants.VOUCHER_FAIL_TCBS)
					|| (tempdto.getSstatus().equals(
							DealCodeConstants.VOUCHER_SUCCESS_NO_BACK) && StringUtils
							.isBlank(tempdto.getSstampid()))
					|| tempdto.getSstatus().equals(
							DealCodeConstants.VOUCHER_RECIPE)
					|| tempdto.getSstatus().equals(
							DealCodeConstants.VOUCHER_SENDED)) {
				checkList.add(tempdto);
				return true;
			}
		}
		return true;

	}

	public List getVoucherTypeList() {
		return voucherTypeList;
	}

	public void setVoucherTypeList(List voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
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
	 * 判断印章对应的证书是否已经维护
	 * 
	 * @return
	 */
	public Boolean checkStampIDAndCertID(TsTreasuryDto tDto, TsUsersDto uDto,
			String strecode, TsStamppositionDto stampDto) {
		/*
		 * 校验国库主体代码转讫章证书ID、印章ID参数是否已维护
		 */
		if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
			if (tDto.getSrotarycertid() == null
					|| tDto.getSrotarycertid().equals("")) {
				MessageDialog.openMessageDialog(null, "国库主体代码" + strecode
						+ "在国库主体信息参数中 " + stampDto.getSstampname()
						+ "证书ID 参数未维护！ ");
				return Boolean.FALSE;
			} else if (tDto.getSrotaryid() == null
					|| tDto.getSrotaryid().equals("")) {
				MessageDialog.openMessageDialog(null, "国库主体代码" + strecode
						+ "在国库主体信息参数中 " + stampDto.getSstampname()
						+ "印章ID 参数未维护！ ");
				return Boolean.FALSE;
			}
		}
		/*
		 * 校验国库主体代码公章证书ID、印章ID参数是否已维护
		 */
		else if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {

			if (tDto.getScertid() == null || tDto.getScertid().equals("")) {
				MessageDialog.openMessageDialog(null, "国库主体代码" + strecode
						+ "在国库主体信息参数中 " + stampDto.getSstampname()
						+ "证书ID 参数未维护！ ");

				return Boolean.FALSE;
			} else if (tDto.getSstampid() == null
					|| tDto.getSstampid().equals("")) {
				MessageDialog.openMessageDialog(null, "国库主体代码" + strecode
						+ "在国库主体信息参数中 " + stampDto.getSstampname()
						+ "印章ID 参数未维护！ ");

				return Boolean.FALSE;
			}
		}
		/*
		 * 校验国库主体代码公章证书ID、印章ID参数是否已维护
		 */
		else if (stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)) {

			if (tDto.getSattachid() == null
					|| tDto.getSattachid().equals("")) {
				MessageDialog.openMessageDialog(null, "国库主体代码" + strecode
						+ "在国库主体信息参数中 " + stampDto.getSstampname()
						+ "印章ID 参数未维护！ ");

				return Boolean.FALSE;
			}
		}
		else if (stamp.equals(MsgConstant.VOUCHERSAMP_BUSS)) {

			if (tDto.getSattachcertid() == null
					|| tDto.getSattachcertid().equals("")) {
				MessageDialog.openMessageDialog(null, "国库主体代码" + strecode
						+ "在国库主体信息参数中 " + stampDto.getSstampname()
						+ "印章ID 参数未维护！ ");

				return Boolean.FALSE;
			}
		}
		/*
		 * 校验当前用户私章证书ID、印章ID参数是否已维护
		 */
		else {
			if (uDto.getScertid() == null || uDto.getScertid().equals("")) {
				MessageDialog.openMessageDialog(null, "当前用户  "
						+ stampDto.getSstampname() + "  证书ID参数未维护！ ");
				return Boolean.FALSE;
			} else if (uDto.getSstampid() == null
					|| uDto.getSstampid().equals("")) {
				MessageDialog.openMessageDialog(null, "当前用户   "
						+ stampDto.getSstampname() + "  印章ID参数未维护！ ");
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * 校验签章顺序，按照签章顺序签章
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public Boolean checkSignStampSeq(TvVoucherinfoDto vDto, String seq,
			List sList, String strecode, TsStamppositionDto stampDto,
			String stampid) throws ITFEBizException {

		List<String> seqList = new ArrayList<String>();
		for (int i = 0; i < sList.size(); i++) {
			TsStamppositionDto tsDto = (TsStamppositionDto) sList.get(i);
			if (tsDto.getSstampsequence() != null
					&& !tsDto.getSstampsequence().equals("")) {
				seqList.add(tsDto.getSstampsequence());
			}
		}
		if (seqList != null && seqList.size() > 0) {
			String[] seqs = seqList.toArray(new String[seqList.size()]);
			ActiveXCompositeVoucherOcx.sortStringArray(seqs);

			String temp = "";
			for (int i = seqs.length - 1; i > -1; i--) {
				if (Integer.parseInt(seqs[i]) < Integer.parseInt(seq)) {
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
					err = tstampDto.getSstampname() + " , " + err;
				}
				err = err.substring(0, err.lastIndexOf(","));
				if (stampid == null || stampid.equals("")) {
					MessageDialog.openMessageDialog(null, "国库代码："
							+ vDto.getStrecode() + " 凭证类型: "
							+ vDto.getSvtcode() + vDto.getSvoucherno() + " \""
							+ stampDto.getSstampname() + "\"签章前请先 \"" + err
							+ "\"签章！");
					return Boolean.FALSE;
				} else {
					err = "";
					String[] stampids = stampid.split(",");
					List<TsStamppositionDto> tsList1 = new ArrayList<TsStamppositionDto>();
					for (int j = 0; j < tsList.size(); j++) {
						for (int i = 0; i < stampids.length; i++) {
							if (stampids[i].equals(tsList.get(j)
									.getSstamptype())) {
								tsList1.add(tsList.get(j));
							}
						}
					}
					tsList.removeAll(tsList1);
					if (tsList.size() > 0) {
						for (TsStamppositionDto tstampDto : tsList) {
							err = tstampDto.getSstampname() + " , " + err;
						}
						err = err.substring(0, err.lastIndexOf(","));
						MessageDialog.openMessageDialog(null, "国库代码："
								+ vDto.getStrecode() + " 凭证类型: "
								+ vDto.getSvtcode() + " \""
								+ stampDto.getSstampname() + "\"签章前请先 \"" + err
								+ "\"签章！");
						return Boolean.FALSE;
					}
				}

			}
		}
		return Boolean.TRUE;
	}

	/**
	 * 根据不同的印章类型进行盖章，在盖章过程中区分客户端盖章和服务器端盖章
	 * 
	 * @throws ITFEBizException
	 * 
	 */
	private void SignStamp(TvVoucherinfoDto vDto, String seq, List sList,
			TsTreasuryDto tDto, List sinList, TsStamppositionDto sDto,
			TsUsersDto uDto) throws ITFEBizException {
		if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
			/*
			 * 公章签章方式 1-客户端签章 ：客户端签章
			 */
			if (!voucherLoadService.getOfficialStamp().equals(
					MsgConstant.VOUCHER_OFFICIALSTAMP)) {
				sinList.add(ActiveXCompositeVoucherOcx.getVoucherStamp(vDto,
						tDto.getScertid(), sDto.getSstampposition(), tDto
								.getSstampid()));
			}
		} else if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
			/*
			 * 转讫章签章方式 1-客户端签章 ：客户端签章
			 */
			if (!voucherLoadService.getRotaryStamp().equals(
					MsgConstant.VOUCHER_ROTARYSTAMP)) {
				sinList.add(ActiveXCompositeVoucherOcx.getVoucherStamp(vDto,
						tDto.getSrotarycertid(), sDto.getSstampposition(), tDto
								.getSrotaryid()));
			}
		} else if (stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)) {
			/*
			 * 附件章签章方式 1-客户端签章 ：客户端签章
			 */
			if (!voucherLoadService.getRotaryStamp().equals(
					MsgConstant.VOUCHER_ATTACHSTAMP)) {
				sinList.add(ActiveXCompositeVoucherOcx.getVoucherStamp(vDto,
						vDto.getStrecode(), sDto.getSstampposition(), tDto
								.getSattachid()));
			}
		} else if (stamp.equals(MsgConstant.VOUCHERSAMP_BUSS)) {
			/*
			 * 附件章签章方式 1-客户端签章 ：客户端签章
			 */
			if (!voucherLoadService.getRotaryStamp().equals(
					MsgConstant.VOUCHER_ATTACHSTAMP)) {
				sinList.add(ActiveXCompositeVoucherOcx.getVoucherStamp(vDto,
						vDto.getStrecode(), sDto.getSstampposition(), tDto
								.getSattachcertid()));
			}
		} else {
			/*
			 * 私章签章方式：客户端仅签名
			 */
			if (stamp.endsWith(MsgConstant.VOUCHERSIGN_PRIVATE)) {
				sinList.add(ActiveXCompositeVoucherOcx.getVoucherSign(vDto,
						uDto.getScertid(), sDto.getSstampposition()));
			} else {// 客户端签私人印章
				if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
				{
					sinList.add(ActiveXCompositeVoucherOcx.getVoucherStamp(vDto,
							uDto.getScertid(), sDto.getSstampposition(), uDto
									.getSstampid()));
				}
			}

		}
	}

	/**
	 * Direction: 导出PDF文件
	 * ename: exportPDF
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportPDF(Object o){
    	try {
    		// 选择保存路径
    		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
    				.getActiveShell());
    		String filePath = path.open();
    		if ((null == filePath) || (filePath.length() == 0)) {
    			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
    			return "";
    		}
	    	for(TvVoucherinfoDto dto : checkList){
	    		List<byte[]> list = voucherLoadService.exportPDF(dto);
	    		byte[] pdfbyte = list.get(0);
				InputStream pdfStream = new java.io.ByteArrayInputStream(pdfbyte);
				java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath+"\\"+dto.getSvoucherno()+".pdf");
				byte[] bytes = new byte[1024];
				int len =-1;
				while((len=pdfStream.read(bytes))!=-1){
					fos.write(bytes, 0, len);
				}
				fos.flush();
				fos.close();
	    	}
    	} catch (Exception e) {
    		log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		}
        return "";
    }
	
//	public List getBizTypeMapperList() {
//		bizTypeMapperList = new ArrayList<Mapper>();
//		Mapper mapper0 = new Mapper();
//		mapper0.setDisplayValue("直接支付");
//		mapper0.setUnderlyValue("0");
//		Mapper mapper91 = new Mapper();
//		mapper91.setDisplayValue("实拨退款");
//		mapper91.setUnderlyValue("91");
//		bizTypeMapperList.add(mapper0);
//		bizTypeMapperList.add(mapper91);
//		return bizTypeMapperList;
//	}

	public void setBizTypeMapperList(List bizTypeMapperList) {
		this.bizTypeMapperList = bizTypeMapperList;
	}


	/**
	 * Direction: 大数据明细校验
	 * ename: bigdatavalid
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String bigdatavalid(Object o){
    	TvVoucherinfoDto selectdto = null;
    	if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要校验的记录！");
			return "";
		}else
		{
			selectdto = checkList.get(0);
		}
    	if(MsgConstant.VOUCHER_NO_2301.equals(selectdto.getSvtcode()))
    	{
    		TvPayreckBigdataDto seardto = new TvPayreckBigdataDto();
    		seardto.setSid(selectdto.getSvoucherno());
    		try {
    			mainlist = commonDataAccessService.findRsByDto(seardto);
				if(mainlist==null||mainlist.size()>0)
				{
					bigdatadto = (TvPayreckBigdataDto)mainlist.get(mainlist.size()-1);
					PageRequest pageRequest = new PageRequest();
					searchtype = MsgConstant.VOUCHER_NO_2301;
					PageResponse pageResponse = retrieve(pageRequest);
					bigdatapage.setPage(pageResponse);
					editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}else
				{
					MessageDialog.openMessageDialog(null, "查询不到匹配的数据！");
					return "";
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "查询异常！");
				return "";
			}
    		return "大数据2305数据查看";
    	}
    	else
    	{
    		TvPayreckBigdataBackDto seardto = new TvPayreckBigdataBackDto();
    		seardto.setSid(selectdto.getSvoucherno());
    		try {
    			mainlist = commonDataAccessService.findRsByDto(seardto);
				if(mainlist==null||mainlist.size()>0)
				{
					bigdatabackdto = (TvPayreckBigdataBackDto)mainlist.get(0);
					PageRequest pageRequest = new PageRequest();
					searchtype = MsgConstant.VOUCHER_NO_2302;
					PageResponse pageResponse = retrieve(pageRequest);
					bigdatabackpage.setPage(pageResponse);
					editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}else
				{
					MessageDialog.openMessageDialog(null, "查询不到匹配的数据！");
					return "";
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "查询异常！");
				return "";
			}
    		return "大数据2306数据查看";
    	}
    }
    /**
	 * Direction: 大数据列表双击
	 * ename: bigdatadouble
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String bigdatadouble(Object o){
    	PageRequest pageRequest = new PageRequest();
    	if(o instanceof TvPayreckBigdataBackDto)
    	{
    		bigdatabackdto = (TvPayreckBigdataBackDto)o;
    		searchtype = MsgConstant.VOUCHER_NO_2302;
    		PageResponse pageResponse = retrieve(pageRequest);
			bigdatabackpage.setPage(pageResponse);
    	}else if(o instanceof TvPayreckBigdataDto)
    	{
    		bigdatadto = (TvPayreckBigdataDto)o;
    		searchtype = MsgConstant.VOUCHER_NO_2301;
    		PageResponse pageResponse = retrieve(pageRequest);
			bigdatapage.setPage(pageResponse);
    	}
    	editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.bigdatadouble(o);
    }
    /**
	 * Direction: 返回
	 * ename: returnvoucherload
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String returnvoucherload(Object o){
        searchtype="";
        return super.returnvoucherload(o);
    }
	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public List getBizTypeMapperList() {
		return bizTypeMapperList;
	}
	public Map<String, TsQueryAmtDto> getAmtMap() throws ITFEBizException{
		if(amtMap==null)
		{
			amtMap = new HashMap<String, TsQueryAmtDto>();
			List<TsQueryAmtDto> queryAmtDtoList =  commonDataAccessService.findRsByDto(new TsQueryAmtDto());
			if(queryAmtDtoList!=null&&queryAmtDtoList.size()>0)
			{
				for(TsQueryAmtDto tempdto:queryAmtDtoList)
					amtMap.put(tempdto.getSorgcode()+tempdto.getStrecode()+tempdto.getSbiztype(), tempdto);
				
			}			
		}
		return amtMap;
	}

	public void setAmtMap(Map<String, TsQueryAmtDto> amtMap) {
		this.amtMap = amtMap;
	}
	public TvPayreckBigdataDto getBigdatadto() {
		return bigdatadto;
	}

	public void setBigdatadto(TvPayreckBigdataDto bigdatadto) {
		this.bigdatadto = bigdatadto;
	}

	public TvPayreckBigdataBackDto getBigdatabackdto() {
		return bigdatabackdto;
	}

	public void setBigdatabackdto(TvPayreckBigdataBackDto bigdatabackdto) {
		this.bigdatabackdto = bigdatabackdto;
	}

	public PagingContext getBigdatapage() {
		return bigdatapage;
	}

	public void setBigdatapage(PagingContext bigdatapage) {
		this.bigdatapage = bigdatapage;
	}

	public PagingContext getBigdatabackpage() {
		return bigdatabackpage;
	}

	public void setBigdatabackpage(PagingContext bigdatabackpage) {
		this.bigdatabackpage = bigdatabackpage;
	}

	public String getSearchtype() {
		return searchtype;
	}

	public void setSearchtype(String searchtype) {
		this.searchtype = searchtype;
	}

	public List getMainlist() {
		return mainlist;
	}

	public void setMainlist(List mainlist) {
		this.mainlist = mainlist;
	}
}
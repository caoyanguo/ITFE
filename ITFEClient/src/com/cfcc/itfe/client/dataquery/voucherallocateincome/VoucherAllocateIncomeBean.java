package com.cfcc.itfe.client.dataquery.voucherallocateincome;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * TCBS资金报文导入
 * 
 * @author zhanghuibin
 * @time 14-04-01 14:26:36 子系统: DataQuery 模块:VoucherAllocateIncome
 *       组件:VoucherAllocateIncome
 */
public class VoucherAllocateIncomeBean extends
		AbstractVoucherAllocateIncomeBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(VoucherAllocateIncomeBean.class);

	private ITFELoginInfo loginInfo;
	// 资金业务类型列表
	private List<Mapper> vtcodeKindList;
	// 业务类型
	private String vtcodeKind;
	// 资金报文类型
	private String fundtype;

	// 设置报文类型
	private String vtcode;

	private boolean isJLFlag = false;

	public VoucherAllocateIncomeBean() {
		super();
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		isJLFlag = loginInfo.getPublicparam().contains(",jilin,");
		if (isJLFlag) {
			vtcode = MsgConstant.MSG_NO_3403;
		} else {
			vtcode = MsgConstant.MSG_NO_3403;
		}
		filePath = new ArrayList<File>();
		dto = new TvVoucherinfoAllocateIncomeDto();
		saveDto = new TvVoucherinfoAllocateIncomeDto();
		deleteDto = new TvVoucherinfoAllocateIncomeDto();
		dto.setSorgcode(loginInfo.getSorgcode());
		dto.setScommitdate(TimeFacade.getCurrentStringTime());
		pagingcontext = new PagingContext(this);
		setDefualtStrecode();
		vtcodeKind = vtcode;
		initvtcodeKindList();// 初始化资金业务类型列表，并且给其赋值
		fundtype = StateConstant.CMT100;
	}

	public void initvtcodeKindList() {
		vtcodeKindList = new ArrayList<Mapper>();
		// 给资金业务类型赋值[1-中央及省市往来票据 2-核算主体间调拨收入]
		Mapper mapper1 = new Mapper();
		mapper1.setDisplayValue(MsgConstant.FUND_BIZ_TYPE_CENTREAREADATA_CMT);
		mapper1.setUnderlyValue(vtcode);
		vtcodeKindList.add(mapper1);
		Mapper mapper2 = new Mapper();
		mapper2.setDisplayValue("实拨资金退款");
		mapper2.setUnderlyValue(MsgConstant.VOUCHER_NO_3208);
		vtcodeKindList.add(mapper2);
		if (loginInfo.getPublicparam().indexOf(",collectpayment=1,") >= 0) {
			Mapper map3268 = new Mapper(MsgConstant.VOUCHER_NO_3268, "实拨资金专户退款");
			vtcodeKindList.add(map3268);
		}
		Mapper mapper3 = new Mapper();
		mapper3.setDisplayValue("收入退付退款");
		if (isJLFlag) {
			mapper3.setUnderlyValue(MsgConstant.VOUCHER_NO_3251);
		} else {
			mapper3.setUnderlyValue(MsgConstant.VOUCHER_NO_3210);
		}
		vtcodeKindList.add(mapper3);
		Mapper mapper4 = new Mapper();
		mapper4.setDisplayValue("直接支付不确定退款");
		mapper4.setUnderlyValue(MsgConstant.VOUCHER_NO_2203);
		vtcodeKindList.add(mapper4);
	}

	/**
	 * Direction: 导入数据 ename: upLoad 引用方法: viewers: * messages:
	 */
	@SuppressWarnings("unchecked")
	public String upLoad(Object o) {
		if (StringUtils.isBlank(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "请选择国库代码！");
			return null;
		}
		if (StringUtils.isBlank(fundtype)) {
			MessageDialog.openMessageDialog(null, "请资金报文类型！");
			return null;
		} else {
			dto.setSreportkind(fundtype);
		}
		if (StringUtils.isBlank(vtcodeKind)) {
			MessageDialog.openMessageDialog(null, "请选择业务类型！");
			return null;
		} else {
			dto.setSvtcode(vtcodeKind);
		}
		if (fundtype.equals(StateConstant.CMT108)) {
			if (!vtcodeKind.equals(MsgConstant.VOUCHER_NO_3208)
					&& !vtcodeKind.equals(MsgConstant.VOUCHER_NO_3210)) {
				MessageDialog.openMessageDialog(null,
						"资金报文类型为 [CMT108] 时,资金业务类型必须为 [实拨资金退款] 或 [收入退付退款]！");
				return null;
			}
		}
		if (fundtype.equals(StateConstant.PKG007)) {
			if (!vtcodeKind.equals(MsgConstant.VOUCHER_NO_3208)
					&& !vtcodeKind.equals(MsgConstant.VOUCHER_NO_3210)) {
				MessageDialog.openMessageDialog(null,
						"资金报文类型为  [PKG007] 时,资金业务类型必须为 [实拨资金退款] 或 [收入退付退款]！");
				return null;
			}
		}
		if (fundtype.equals(StateConstant.TYPE999)) {
			if (!vtcodeKind.equals(vtcode)) {
				MessageDialog.openMessageDialog(null,
						"资金报文类型为 [核算主体间调拨收入] 时,资金业务类型必须为 [国库往来票据]！");
				return null;
			}
		}
		if (findAdmDivCodeByStrecode())
			return null;
		List<String> serverpathlist = new ArrayList<String>();
		if (null == filePath || filePath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			return null;
		}
		if (filePath.size() > 1000) {
			MessageDialog.openMessageDialog(null, "所选加载文件不能大于1000个！");
			return null;
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "导入提示",
				" 请确认导入文件与[资金报文][业务类型]是否一致，确定需要导入吗？")) {
			return "";
		}
		List<File> fileList = new ArrayList<File>();
		try {
			for (File tmpfile : (List<File>) filePath) {
				if (null == tmpfile || null == tmpfile.getName()
						|| null == tmpfile.getAbsolutePath()) {
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return null;
				}
				if (!tmpfile.getName().trim().toLowerCase().endsWith(".csv")) {
					MessageDialog.openMessageDialog(null,
							" 请选择正确的文件格式,只支持csv格式文件！");
					return null;
				}
				// 删除服务器的同名文件，防止覆盖写入
				List<String> pathlist = new ArrayList<String>();
				String path = File.separator + "ITFEDATA" + File.separator
						+ loginInfo.getSorgcode() + File.separator
						+ TimeFacade.getCurrentStringTime() + File.separator
						+ tmpfile.getName();
				pathlist.add(path);
				DeleteServerFileUtil.delFile(commonDataAccessService, pathlist);
				// 将导入文件加载到服务器
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			int sumFile = 0; // 统计所有导入的txt文件
			int wrongFileSum = 0; // 统计错误文件的个数，如果超出20个，不再追加错误信息
			int errCount = 0;
			String errName = "";
			StringBuffer problemStr = new StringBuffer();
			StringBuffer errorStr = new StringBuffer();
			MulitTableDto bizDto = fileResolveCommonService.loadFile(fileList,
					vtcodeKind, vtcode, dto);
			errCount = bizDto.getErrorCount() + wrongFileSum;
			if (null != bizDto.getErrorList()
					&& bizDto.getErrorList().size() > 0) {
				for (int m = 0; m < bizDto.getErrorList().size(); m++) {
					wrongFileSum++;
					if (wrongFileSum < 15) {
						problemStr.append(bizDto.getErrorList().get(m)
								.substring(6)
								+ "\r\n");
						errorStr.append(bizDto.getErrorList().get(m) + "\r\n");
					} else {
						errorStr.append(bizDto.getErrorList().get(m) + "\r\n");
					}
				}
				errName = StateConstant.Import_Errinfo_DIR
						+ "TCBS资金报文导入("
						+ new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒")
								.format(new java.util.Date()) + ").txt";
				if (!"".equals(errorStr.toString())) {
					FileUtil.getInstance().writeFile(errName,
							errorStr.toString());
				}
				FileUtil
						.getInstance()
						.deleteFileWithDays(
								StateConstant.Import_Errinfo_DIR,
								Integer
										.parseInt(StateConstant.Import_Errinfo_BackDays));
			}
			commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
					.wipeFileOut(serverpathlist, bizDto.getErrNameList()),
					vtcode);
			if (problemStr.toString().trim().length() > 0) {
				String noteInfo = "";
				if (fileList == null || fileList.size() == 0) {
					noteInfo = "共加载了" + sumFile + "个文件，其中有" + wrongFileSum
							+ "个错误文件，信息如下：\r\n";
				} else {
					noteInfo = "共加载了" + sumFile + "个文件，其中有" + errCount
							+ "个错误文件，部分信息如下【详细错误信息请查看" + errName + "】：\r\n";
				}
				MessageDialog.openMessageDialog(null, noteInfo
						+ problemStr.toString());
			} else {
				MessageDialog.openMessageDialog(null, "文件加载成功,本次共加载成功 "
						+ fileList.size() + " 个文件！");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			filePath = new ArrayList();
			this.editor.fireModelChanged();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,
						serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("删除服务器文件失败！", e1);
				MessageDialog.openErrorDialog(null, e);
			}
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		}
		return super.upLoad(o);
	}

	/**
	 * Direction: 查询 ename: query 引用方法: viewers: 中央及省市往来票据查询结果 messages:
	 */
	public String query(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = new PageResponse();
		pageResponse = retrieve(pageRequest);
		if (pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录！");
			return null;
		}
		pagingcontext.setPage(pageResponse);
		return super.query(o);
	}

	/**
	 * Direction: 返回查询页面 ename: goQuery 引用方法: viewers: 中央及省市往来票据查询 messages:
	 */
	public String goQuery(Object o) {
		return super.goQuery(o);
	}

	/**
	 * Direction: 新增保存 ename: savaDto 引用方法: viewers: * messages:
	 */
	public String savaDto(Object o) {
		if (saveDto.getStrecode() == null || saveDto.getStrecode().equals("")) {
			MessageDialog.openMessageDialog(null, "请选择对应的国库！");
			return null;
		}
		if (saveDto.getSvtcode() == null
				|| saveDto.getSvtcode().trim().equals("")) {
			MessageDialog.openMessageDialog(null, "业务类型不能为空！");
			return null;
		}
		if (saveDto.getSpayeeacctno() == null
				|| saveDto.getSpayeeacctno().equals("")) {
			MessageDialog.openMessageDialog(null, "收款人账号不能为空！");
			return null;
		}
		if (saveDto.getSpayeeacctname() == null
				|| saveDto.getSpayeeacctname().equals("")) {
			MessageDialog.openMessageDialog(null, "收款人名称不能为空！");
			return null;
		}
		if (saveDto.getSpayacctno() == null
				|| saveDto.getSpayacctno().equals("")) {
			MessageDialog.openMessageDialog(null, "付款人账号不能为空！");
			return null;
		}
		if (saveDto.getSpayacctname() == null
				|| saveDto.getSpayacctname().equals("")) {
			MessageDialog.openMessageDialog(null, "付款人名称不能为空！");
			return null;
		}
		if (saveDto.getSpaydealno() == null
				|| saveDto.getSpaydealno().trim().equals("")) {
			MessageDialog.openMessageDialog(null, "支付交易序号不能为空！");
			return null;
		}
		if(!loginInfo.getSorgcode().startsWith("09"))
			saveDto.setSvtcode(vtcode); // 凭证类型为国库往来票据

		try {
			if (saveDto.getSdealno() != null) {
				voucherAllocateIncomeService.modifySaveDto(saveDto);
				super.modifyDto(o);
			} else {
				saveDto.setSorgcode(loginInfo.getSorgcode());
				saveDto.setScommitdate(TimeFacade.getCurrentStringTime());
				saveDto.setSvtcode(vtcode);
				saveDto.setTssysupdate(new Timestamp(new java.util.Date()
						.getTime()));// 更新时间
				voucherAllocateIncomeService.saveDto(saveDto);
			}
		} catch (ITFEBizException e) {
			log.debug(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		saveDto = new TvVoucherinfoAllocateIncomeDto();
		editor.fireModelChanged();
		return super.savaDto(o);
	}

	/**
	 * Direction: 修改 ename: modifyDto 引用方法: viewers: TCBS资金报文录入 messages:
	 */
	public String modifyDto(Object o) {
		if (!loginInfo.getSorgcode().startsWith("09")) {
			if (!vtcode.equals(vtcodeKind)) {
				MessageDialog.openMessageDialog(null, "只能对业务类型为国库往来票据进行修改！");
				return "";
			}
		}else{
			if (!AdminConfirmDialogFacade.open("需要主管授权才能修改凭证！")) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				return null;
			}
		}
		if (deleteDto == null || deleteDto.getSdealno() == null
				|| deleteDto.getSdealno().equals("")) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		saveDto = (TvVoucherinfoAllocateIncomeDto) deleteDto.clone();
		return super.modifyDto(o);
	}

	/**
	 * Direction: 修改保存 ename: saveModifyDto 引用方法: viewers: TCBS资金报文导入查询结果
	 * messages:
	 */
	public String saveModifyDto(Object o) {
		savaDto(o);
		pagingcontext.setPage(retrieve(new PageRequest()));
		editor.fireModelChanged();
		deleteDto = new TvVoucherinfoAllocateIncomeDto();
		return super.saveModifyDto(o);
	}

	/**
	 * Direction: 删除 ename: delDto 引用方法: viewers: * messages:
	 */
	public String delDto(Object o) {
		if (deleteDto == null || deleteDto.getSdealno() == null
				|| deleteDto.getSdealno().equals("")) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定删除选中的记录吗？")) {
			return "";
		}
		try {
			//机构代码是上海   查找是否生成凭证
			if(loginInfo.getSorgcode().startsWith("09")){
				TvVoucherinfoDto tvVoucherinfoDto = new TvVoucherinfoDto();
				tvVoucherinfoDto.setStrecode(deleteDto.getStrecode());
				tvVoucherinfoDto.setSvtcode(deleteDto.getSvtcode());
				tvVoucherinfoDto.setScreatdate(deleteDto.getScommitdate());
				tvVoucherinfoDto.setNmoney(deleteDto.getNmoney());
				List<TvVoucherinfoDto> resultList = commonDataAccessService.findRsByDto(tvVoucherinfoDto);
				if(null != resultList && resultList.size() > 0){
					tvVoucherinfoDto = resultList.get(0);
					if(Integer.valueOf(tvVoucherinfoDto.getSstatus()) <= Integer.valueOf(DealCodeConstants.VOUCHER_STAMP)){
						commonDataAccessService.delete(tvVoucherinfoDto);
					}
				}
			}
			commonDataAccessService.delete(deleteDto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delDto(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		pagingcontext.setPage(retrieve(new PageRequest()));
		editor.fireModelChanged();
		return super.delDto(o);
	}

	/**
	 * Direction: 单击方法 ename: singleClick 引用方法: viewers: * messages:
	 */
	public String singleClick(Object o) {
		deleteDto = (TvVoucherinfoAllocateIncomeDto) o;
		return super.singleClick(o);
	}

	/**
	 * 根据组织机构代码、国库代码查找相应的区划代码
	 * 
	 * @return
	 */
	private Boolean findAdmDivCodeByStrecode() {
		TsConvertfinorgDto finorgDto = new TsConvertfinorgDto();
		finorgDto.setSorgcode(dto.getSorgcode());
		finorgDto.setStrecode(dto.getStrecode());
		try {
			List list = commonDataAccessService.findRsByDto(finorgDto);
			if (list == null || list.size() == 0) {
				MessageDialog.openMessageDialog(null, "国库代码"
						+ dto.getStrecode() + "对应的财政代码参数未维护！");
				return true;
			}
			finorgDto = (TsConvertfinorgDto) list.get(0);
			if (StringUtils.isBlank(finorgDto.getSadmdivcode()))
				MessageDialog.openMessageDialog(null, "财政代码"
						+ finorgDto.getSfinorgcode() + "对应的区划代码参数未维护！");
			else {
				dto.setSadmdivcode(finorgDto.getSadmdivcode());
				return false;
			}
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "查询财政代码参数异常！");
		}
		return true;
	}

	/**
	 * 设置默认国库代码
	 */
	public void setDefualtStrecode() {
		TsTreasuryDto tmpdto = new TsTreasuryDto();
		tmpdto.setSorgcode(loginInfo.getSorgcode());
		try {
			if (StringUtils.isBlank(dto.getStrecode())) {
				List list = commonDataAccessService.findRsByDto(tmpdto);
				if (list != null || list.size() > 0)
					dto
							.setStrecode(((TsTreasuryDto) list.get(0))
									.getStrecode());
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		pageRequest.setPageSize(17);
		dto.setSreportkind(fundtype);
		dto.setSvtcode(vtcodeKind);
		try {
			return commonDataAccessService.findRsByDtoPaging(dto, pageRequest,
					"1=1", "TS_SYSUPDATE DESC");
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"查询数据异常！", e));
		}
		return super.retrieve(pageRequest);
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		VoucherAllocateIncomeBean.log = log;
	}

	public List<Mapper> getVtcodeKindList() {
		return vtcodeKindList;
	}

	public void setVtcodeKindList(List<Mapper> vtcodeKindList) {
		this.vtcodeKindList = vtcodeKindList;
	}

	public String getVtcodeKind() {
		return vtcodeKind;
	}

	public void setVtcodeKind(String vtcodeKind) {
		this.vtcodeKind = vtcodeKind;
	}

	public String getFundtype() {
		return fundtype;
	}

	public void setFundtype(String fundtype) {
		this.fundtype = fundtype;
	}

}
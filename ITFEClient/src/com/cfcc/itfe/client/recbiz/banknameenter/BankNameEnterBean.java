package com.cfcc.itfe.client.recbiz.banknameenter;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.BuLUBankNoDialogFacade;
import com.cfcc.itfe.client.dialog.CheckFaultMsgDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsDwbkReasonDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsQueryAmtDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 13-05-22 13:07:12 子系统: RecBiz 模块:bankNameEnter 组件:BankNameEnter
 */
public class BankNameEnterBean extends AbstractBankNameEnterBean implements
		IPageDataProvider {


	private static Log log = LogFactory.getLog(BankNameEnterBean.class);
	private List<TdEnumvalueDto> biztypelist;// 业务类型列表
	private List<TsPaybankDto> banklist;// 银行代码列表
	private List querypayoutlist; // 实拨资金信息列表
	private List dwbklist;// 收入退付
	private List directlist;// 直接支付信息列表
	private List directpayoutlist; // 直接支付清算信息列表
	private List directpayoutbacklist; // 直接支付清算退回信息列表
	private List directplanlist;// 直接额度信息列表
	private String biztype; // 业务类型
	private String dacceptdate;// 受理日期
	private String sifmatch;// 是否补录
	private String scheckstatus;// 补录复核状态
	private String bankname; // 银行名称
	private String operatype;// 操作类型 0-补录，1-补复
	
	private String admDivCode;
	
	private String startfamt;
	private String endfamt;
	private TvPayoutmsgmainDto payoutdto = new TvPayoutmsgmainDto();
	private TvDwbkDto dwbkdto = new TvDwbkDto();
	private TfDirectpaymsgmainDto directdto = new TfDirectpaymsgmainDto();
	private TsPaybankDto bankdto = new TsPaybankDto();
	private ITFELoginInfo loginfo;
	private List pagingList = new ArrayList();
	private List pagingList2 = new ArrayList();
	private List pagingListDirect = new ArrayList();
	private PagingContext tablePagingContext = null;
	private PagingContext tableDwbkPagingContext = null;
	private PagingContext tableDirectPagingContext = null;
	private List checklist = null;
	private List checkBizList = new ArrayList();
	private IDto selDto;	
	private String moneyUnit;
	private String samtFlag;
	private HashMap<String, TsQueryAmtDto> amtMap;
	private List<TsQueryAmtDto> listMoney;
	private HashMap<String, String> admDivCodeMap;
	private String strecode;
	private String stamp; //签章类型
	private boolean breakMark;
	private boolean singStampflag; //设定是否需要在审核复核时进行签章,默认不签章
	private TsUsersDto uDto;
	public BankNameEnterBean() {
		super();
		singStampflag =false;
		banklist = new ArrayList<TsPaybankDto>();
		biztypelist = new ArrayList();
		selectlist = new ArrayList();
//		biztype = null;
		sifmatch = "";
		scheckstatus = "";
		moneyUnit = StateConstant.MoneyUnit_BW;
		tablePagingContext = new PagingContext(this);
		tableDwbkPagingContext = new PagingContext(this);
		tableDirectPagingContext = new PagingContext(this);
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		init();
	}

	public void init() {
		if (loginfo.getPublicparam().indexOf(",singStampflag,")>=0) {
			singStampflag=true;
		}
		TdEnumvalueDto value1 = new TdEnumvalueDto();
		value1.setStypecode(BizTypeConstant.VOR_TYPE_PAYOUT);
		value1.setSvaluecmt("实拨资金5207");
		biztypelist.add(value1);
		if(loginfo.getPublicparam().indexOf(",collectpayment=1,")>=0)
		{
			TdEnumvalueDto value5267 = new TdEnumvalueDto();
			value5267.setStypecode(MsgConstant.VOUCHER_NO_5267);
			value5267.setSvaluecmt("实拨资金专户5267");
			biztypelist.add(value5267);
		}
		TdEnumvalueDto value2 = new TdEnumvalueDto();
		value2.setStypecode(BizTypeConstant.VOR_TYPE_DWBK);
		value2.setSvaluecmt("收入退付5209");
		biztypelist.add(value2);
		if(loginfo.getPublicparam().indexOf(",sh,")>=0){
			TdEnumvalueDto value3 = new TdEnumvalueDto();
			value3.setStypecode(MsgConstant.VOUCHER_NO_5201);
			value3.setSvaluecmt("直接支付5201");
			biztypelist.add(value3);
		}
		dacceptdate = TimeFacade.getCurrentStringTime();
		try {
			TsConvertfinorgDto tmpdto = new TsConvertfinorgDto();
			tmpdto.setSorgcode(loginfo.getSorgcode());
			List<TsConvertfinorgDto> list = commonDataAccessService.findRsByDto(tmpdto);
			if (list.size() > 0) {
				admDivCode = list.get(0).getSadmdivcode();
				admDivCodeMap = new HashMap<String, String>();
				for (TsConvertfinorgDto _dto: list) {
					admDivCodeMap.put(_dto.getStrecode(), _dto.getSadmdivcode());
				}
			}
			TsQueryAmtDto tmp = new TsQueryAmtDto();
			tmp.setSorgcode(loginfo.getSorgcode());
			listMoney = commonDataAccessService.findRsByDto(tmp);
			if (listMoney.size() > 0) {
				amtMap = new HashMap<String, TsQueryAmtDto>();
				for (TsQueryAmtDto t : listMoney) {
					amtMap.put(t.getSamtflag(), t);
				}
			}
		} catch (ITFEBizException e) {
			log.error("查询参数出现异常!!!");
			MessageDialog.openErrorDialog(null, e);
		}
		
	}

	/**
	 * Direction: 单击信息列表 ename: singleSelect 引用方法: viewers: * messages:
	 */
	@SuppressWarnings("unchecked")
	public String singleSelect(Object o) {
		try {
			if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
				payoutdto = (TvPayoutmsgmainDto) o;
				TsPaybankDto bankdto = new TsPaybankDto();
				bankdto.setSstate(StateConstant.COMMON_YES);
				setBankname(payoutdto.getSrecbankname());
				banklist = commonDataAccessService.findRsByDto(bankdto,
						" AND S_BANKNAME LIKE '%" + payoutdto.getSrecbankname()
								+ "%' ");
			} else if (BizTypeConstant.VOR_TYPE_DWBK.equals(biztype)) {
				dwbkdto = (TvDwbkDto) o;
				TsPaybankDto bankdto = new TsPaybankDto();
				bankdto.setSstate(StateConstant.COMMON_YES);
				setBankname(dwbkdto.getSrecbankname());
				banklist = commonDataAccessService.findRsByDto(bankdto,
						" AND S_BANKNAME LIKE '%" + dwbkdto.getSrecbankname()
								+ "%' ");
			}else if (MsgConstant.VOUCHER_NO_5201.equals(biztype)) {
				directdto = (TfDirectpaymsgmainDto) o;
				TsPaybankDto bankdto = new TsPaybankDto();
				bankdto.setSstate(StateConstant.COMMON_YES);
				setBankname(directdto.getSinputrecbankname());
				banklist = commonDataAccessService.findRsByDto(bankdto,
						" AND S_BANKNAME LIKE '%" + directdto.getSinputrecbankname()
								+ "%' ");
			}
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("单击信息列表错误", e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.singleSelect(o);
	}

	/**
	 * Direction: 双击银行列表 ename: doubleclick 引用方法: viewers: * messages:
	 */
	public String doubleclickBank(Object o) {
		TsConvertbanknameDto convertbankdto = new TsConvertbanknameDto();
		String msg = "";
		bankdto = (TsPaybankDto) o;
		if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {

			if (payoutdto.getSorgcode() == null) {
				return null;
			}
			if (dacceptdate.equals("0")) {
				msg = "银行行名【" + payoutdto.getSrecbankname() + "】对应支付系统行名【"
						+ bankdto.getSbankname() + "】,是否确认补录?";
			} else {
				msg = "银行行名【" + payoutdto.getSrecbankname() + "】对应支付系统行名【"
						+ bankdto.getSbankname() + "】,是否确认补复?";
			}
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
					null, "提示", msg);
			if (flag) {
				try {
					if(bankdto.getSbankno()==null||"".equals(bankdto.getSbankno())||bankdto.getSbankname()==null||"".equals(bankdto.getSbankname()))
					{
						MessageDialog.openMessageDialog(null, "收款开户行行号或行名不可为空！");
						return null;
					}
					// 补录
					payoutdto.setSrecbankno(bankdto.getSbankno());// 收款开户行行号
					payoutdto.setSinputrecbankname(bankdto.getSbankname());// 补录的收款开户行行名
					payoutdto.setSinputusercode(loginfo.getSuserCode());// 补录人
					payoutdto.setScheckstatus(StateConstant.CHECKSTATUS_1);// 已补录
					// 更新行号
					commonDataAccessService.updateData(payoutdto);
					MessageDialog.openMessageDialog(null, "补录成功");
					editor.fireModelChanged();
					return super.doubleclickBank(o);
				} catch (ITFEBizException e) {
					log.error(e);
					MessageDialog.openMessageDialog(null, "补录失败");
				}
			} else {
				return null;
			}
		} else if (BizTypeConstant.VOR_TYPE_DWBK.equals(biztype)) {
			if (dwbkdto.getSbookorgcode() == null) {
				return null;
			}
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
					null, "提示", "银行行名【" + dwbkdto.getSrecbankname()
							+ "】对应支付系统行名【" + bankdto.getSbankname()
							+ "】,是否确认补录?");
			if (flag) {
				try {
					// 更新行号
					dwbkdto.setSpayeeopnbnkno(bankdto.getSbankno());// 收款开户行行号
					dwbkdto.setSinputrecbankname(bankdto.getSbankname());// 补录的收款开户行行名
					dwbkdto.setSinputusercode(loginfo.getSuserCode());// 补录人
					dwbkdto.setScheckstatus(StateConstant.CHECKSTATUS_1);// 已补录
					commonDataAccessService.updateData(dwbkdto);
					editor.fireModelChanged();
					return super.doubleclickBank(o);
				} catch (ITFEBizException e) {
					log.error("更新行号错误", e);
					MessageDialog.openErrorDialog(null, e);
				}
			} else {
				return null;
			}
		} else if (MsgConstant.VOUCHER_NO_5201.equals(biztype)) {
			if (directdto.getSorgcode() == null) {
				return null;
			}
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
					null, "提示", "银行行名【" + directdto.getSinputrecbankname()
							+ "】对应支付系统行名【" + bankdto.getSbankname()
							+ "】,是否确认补录?");
			if (flag) {
				try {
					// 更新行号
					directdto.setSpayeeacctbankno(bankdto.getSbankno());//收款人银行行号
					directdto.setSinputrecbankname(bankdto.getSbankname());//支付系统行名
					directdto.setSinputusercode(loginfo.getSuserCode());// 补录人
					directdto.setScheckstatus(StateConstant.CHECKSTATUS_1);//已补录
					commonDataAccessService.updateData(directdto);
					editor.fireModelChanged();
					return super.doubleclickBank(o);
				} catch (ITFEBizException e) {
					log.error("更新行号错误", e);
					MessageDialog.openErrorDialog(null, e);
				}
			} else {
				return null;
			}
		}
		return super.doubleclickBank(o);
	}

	/**
	 * Direction: 双击补录 ename: doubleclickTObulu 引用方法: viewers: * messages:
	 */
	public String doubleclickTObulu(Object o) {
		try {
			if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
				payoutdto = (TvPayoutmsgmainDto) o;
				if (!payoutdto.getSifmatch().equals(
						StateConstant.IF_MATCHBNKNAME_YES)) {
					MessageDialog.openMessageDialog(null, "不需要补录行号，请审核、复核！");
					return "";
				} else {
					if (payoutdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_4)) {
						MessageDialog.openMessageDialog(null,
								"支付系统行号已复核，请选择其它操作！");
						return "";
					}
					if (payoutdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_1)) {
						MessageDialog.openMessageDialog(null, "支付系统行号已补录，请审核！");
						return "";
					}
				}
				TsPaybankDto bankdto = new TsPaybankDto();
				bankdto.setSstate(StateConstant.COMMON_YES);
				banklist = commonDataAccessService.findRsByDto(bankdto,
						" AND S_BANKNAME LIKE '%" + payoutdto.getSrecbankname()
								+ "%' ");
				payoutdto = (TvPayoutmsgmainDto) BuLUBankNoDialogFacade
						.openDialogProc(payoutdto, banklist, payoutdto
								.getSrecbankname(), biztype,
								commonDataAccessService, null);
				editor.fireModelChanged();
			} else if (BizTypeConstant.VOR_TYPE_DWBK.equals(biztype)) {
				dwbkdto = (TvDwbkDto) o;
				if (dwbkdto.getScheckstatus().equals(
						StateConstant.CHECKSTATUS_1)) {
					MessageDialog.openMessageDialog(null, "支付系统行号已补录，请选择其它操作！");
					return "";
				}
				if (dwbkdto.getScheckstatus().equals(
						StateConstant.CHECKSTATUS_4)) {
					MessageDialog.openMessageDialog(null, "支付系统行号已复核，请选择其它操作！");
					return "";
				}
				TsPaybankDto bankdto = new TsPaybankDto();
				bankdto.setSstate(StateConstant.COMMON_YES);
				setBankname(dwbkdto.getSrecbankname());
				banklist = commonDataAccessService.findRsByDto(bankdto,
						" AND S_BANKNAME LIKE '%" + dwbkdto.getSrecbankname()
								+ "%' ");
				TsDwbkReasonDto drdto = new TsDwbkReasonDto();
				drdto.setSorgcode(loginfo.getSorgcode());
				List<TsDwbkReasonDto> listdto = commonDataAccessService
						.findRsByDto(drdto);
				dwbkdto = (TvDwbkDto) BuLUBankNoDialogFacade.openDialogProc(
						dwbkdto, banklist, null, biztype,
						commonDataAccessService, listdto);
				editor.fireModelChanged();
			} else if (MsgConstant.VOUCHER_NO_5201.equals(biztype)) {
				directdto = (TfDirectpaymsgmainDto) o;
				if (!directdto.getSifmatch().equals(
						StateConstant.IF_MATCHBNKNAME_YES)) {
					MessageDialog.openMessageDialog(null, "不需要补录行号，请审核、复核！");
					return "";
				} else {
					if (directdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_4)) {
						MessageDialog.openMessageDialog(null,
								"支付系统行号已复核，请选择其它操作！");
						return "";
					}
					if (directdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_1)) {
						MessageDialog.openMessageDialog(null, "支付系统行号已补录，请审核！");
						return "";
					}
				}
				TsPaybankDto bankdto = new TsPaybankDto();
				bankdto.setSstate(StateConstant.COMMON_YES);
				banklist = commonDataAccessService.findRsByDto(bankdto,
						" AND S_BANKNAME LIKE '%" + directdto.getSinputrecbankname()
								+ "%' ");
				directdto = (TfDirectpaymsgmainDto) BuLUBankNoDialogFacade
						.openDialogProc(directdto, banklist, directdto
								.getSinputrecbankname(), biztype,
								commonDataAccessService, null);
				editor.fireModelChanged();
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.doubleclickTObulu(o);
	}

	/**
	 * Direction: 查询银行信息 ename: querybank 引用方法: viewers: * messages:
	 */
	@SuppressWarnings("unchecked")
	public String querybank(Object o) {
		TsPaybankDto bankdto = new TsPaybankDto();
		bankdto.setSstate(StateConstant.COMMON_YES);
		try {
			banklist = commonDataAccessService.findRsByDto(bankdto,
					" AND S_BANKNAME LIKE '%" + bankname + "%' ");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.querybank(o);
	}

	/**
	 * Direction: 退出 ename: exit 引用方法: viewers: * messages:
	 */
	public String exit(Object o) {
		selectlist = new ArrayList();
		// setBiztype(biztype);
		editor.openComposite("需补录支付系统行号信息查询视图");
		return null;

	}

	/**
	 * Direction: 全选 ename: selectAll 引用方法: viewers: * messages:
	 */
	public String selectAll(Object o) {
		if (selectlist == null || selectlist.size() == 0) {
			selectlist = new ArrayList();
			if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
				selectlist.addAll(querypayoutlist);
			} else if (BizTypeConstant.VOR_TYPE_DWBK.equals(biztype)) {
				selectlist.addAll(dwbklist);
			} else if (MsgConstant.VOUCHER_NO_5201.equals(biztype)) {
				selectlist.addAll(directlist);
			}

		} else
			selectlist.clear();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.selectAll(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {
		StringBuffer sql = new StringBuffer();
		if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
			TvPayoutmsgmainDto dto = new TvPayoutmsgmainDto();
			dto.setSorgcode(loginfo.getSorgcode());
			if (dacceptdate != null && !dacceptdate.equals("")) {
				dto.setScommitdate(dacceptdate);
			}
			// 是否补录
			if (sifmatch != null && !sifmatch.equals("")) {
				dto.setSifmatch(sifmatch);
			}
			// 补录复核状态
			if (scheckstatus != null && !scheckstatus.equals("")) {
				dto.setScheckstatus(scheckstatus);
			}
			//国库代码
			if (strecode != null && !strecode.equals("")) {
				dto.setStrecode(strecode);
			}
			String wheresql = null;
			
			wheresql = " S_BIZNO in (select S_DEALNO from TV_VOUCHERINFO WHERE S_VTCODE='"
				+ biztype
				+ "' AND (S_STATUS='"
				+ DealCodeConstants.VOUCHER_VALIDAT_SUCCESS
				+ "' OR S_STATUS='"
				+ DealCodeConstants.VOUCHER_CHECKSUCCESS
				+ "' OR S_STATUS='"
				+ DealCodeConstants.VOUCHER_CHECKFAULT
				+ "' OR S_STATUS='"
				+ DealCodeConstants.VOUCHER_REGULATORY_SUCCESS
				+ "' OR S_STATUS='"
				+ DealCodeConstants.VOUCHER_CHECK_SUCCESS + "'))";
			String moneyExtent = getExtentSQl(startfamt, endfamt, moneyUnit);
			if (null == moneyExtent) {
				return null;
			} else {
				wheresql += moneyExtent;
			}
			try {
				return commonDataAccessService.findRsByDtoWithWherePaging(dto,
						arg0, wheresql);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}

		} else if (BizTypeConstant.VOR_TYPE_DWBK.equals(biztype)) {
			TvDwbkDto dto = new TvDwbkDto();
			dto.setSbookorgcode(loginfo.getSorgcode());
			if (dacceptdate != null && !dacceptdate.equals("")) {
				dto.setDaccept(CommonUtil.strToDate(dacceptdate));
			}
			// 是否补录
			if (sifmatch != null && !sifmatch.equals("")) {
				dto.setSifmatch(sifmatch);
			}
			// 补录复核状态
			if (scheckstatus != null && !scheckstatus.equals("")) {
				dto.setScheckstatus(scheckstatus);
			}
			//国库代码
			if (strecode != null && !strecode.equals("")) {
				dto.setSpayertrecode(strecode);
			}
			String wheresql = " S_BIZNO in (select S_DEALNO from TV_VOUCHERINFO WHERE S_VTCODE='"
					+ BizTypeConstant.VOR_TYPE_DWBK
					+ "' AND (S_STATUS='"
					+ DealCodeConstants.VOUCHER_VALIDAT_SUCCESS
					+ "' OR S_STATUS='"
					+ DealCodeConstants.VOUCHER_CHECKSUCCESS
					+ "' OR S_STATUS='"
					+ DealCodeConstants.VOUCHER_CHECKFAULT
					+ "' OR S_STATUS='"
					+ DealCodeConstants.VOUCHER_REGULATORY_SUCCESS
					+ "' OR S_STATUS='"
					+ DealCodeConstants.VOUCHER_CHECK_SUCCESS + "'))";
			String moneyExtent = getExtentSQl(startfamt, endfamt, moneyUnit);
			if (null == moneyExtent) {
				return null;
			} else {
				wheresql += moneyExtent;
			}
			try {
				return commonDataAccessService.findRsByDtoWithWherePaging(dto,
						arg0, wheresql);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}

		} else if (MsgConstant.VOUCHER_NO_5201.equals(biztype)) {
			TfDirectpaymsgmainDto dto = new TfDirectpaymsgmainDto();
			dto.setSorgcode(loginfo.getSorgcode());
			if (dacceptdate != null && !dacceptdate.equals("")) {
				dto.setScommitdate(dacceptdate);
			}
			// 是否补录
			if (sifmatch != null && !sifmatch.equals("")) {
				dto.setSifmatch(sifmatch);
			}
			// 补录复核状态
			if (scheckstatus != null && !scheckstatus.equals("")) {
				dto.setScheckstatus(scheckstatus);
			}
			//国库代码
			if (strecode != null && !strecode.equals("")) {
				dto.setStrecode(strecode);
			}
			String wheresql = " I_VOUSRLNO in (select S_DEALNO from TV_VOUCHERINFO WHERE S_VTCODE='"
					+ MsgConstant.VOUCHER_NO_5201
					+ "' AND (S_STATUS='"
					+ DealCodeConstants.VOUCHER_VALIDAT_SUCCESS
					+ "' OR S_STATUS='"
					+ DealCodeConstants.VOUCHER_CHECKSUCCESS
					+ "' OR S_STATUS='"
					+ DealCodeConstants.VOUCHER_CHECKFAULT
					+ "' OR S_STATUS='"
					+ DealCodeConstants.VOUCHER_REGULATORY_SUCCESS
					+ "' OR S_STATUS='"
					+ DealCodeConstants.VOUCHER_CHECK_SUCCESS
					+ "')"
					+ "	AND (S_HOLD4 = '"
					+ StateConstant.BIZTYPE_CODE_SINGLE
					+ "' OR S_HOLD4 = '"
					+ StateConstant.BIZTYPE_CODE_SALARY
					+ "' ))";
			String moneyExtent = getExtentSQl(startfamt, endfamt, moneyUnit);
			if (null == moneyExtent) {
				return null;
			} else {
				wheresql += moneyExtent;
			}
			try {
				return commonDataAccessService.findRsByDtoWithWherePaging(dto,
						arg0, wheresql);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}

		}
		return null;
	}

	/**
	 * Direction: 查询 ename: search 引用方法: viewers: * messages:
	 */
	public String search(Object o) {
		selectlist = new ArrayList();
		if (StringUtils.isBlank(biztype)) {
			MessageDialog.openMessageDialog(null, "业务类型不能为空！");
			return null;
		}
		 queryTablelist(biztype);
		 if (this.breakMark) {
			return null;
		}
		if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
			ShowCompositeVoucherOcx.composite_table.setVisible(true);
			ShowCompositeVoucherOcx.composite_dwbk.setVisible(false);
			ShowCompositeVoucherOcx.composite_direct.setVisible(false);
			if (tablePagingContext.getPage().getData().isEmpty()) {
				MessageDialog.openMessageDialog(null, "没有查询到数据！");
				return null;
			}
			new Thread() {
				public void run() {
			    ShowCompositeVoucherOcx.init(0, (IDto) tablePagingContext.getPage()
					.getData().get(0));
				}}.start();
		} else if (MsgConstant.VOUCHER_NO_5201.equals(biztype)) {
			ShowCompositeVoucherOcx.composite_table.setVisible(false);
			ShowCompositeVoucherOcx.composite_dwbk.setVisible(false);
			ShowCompositeVoucherOcx.composite_direct.setVisible(true);
			if (tableDirectPagingContext.getPage().getData().isEmpty()) {
				MessageDialog.openMessageDialog(null, "没有查询到数据！");
				return null;
			}
			new Thread() {
				public void run() {
			ShowCompositeVoucherOcx.init(0, (IDto) tableDirectPagingContext.getPage()
					.getData().get(0));
				}}.start();
		}else {
			ShowCompositeVoucherOcx.composite_table.setVisible(false);
			ShowCompositeVoucherOcx.composite_dwbk.setVisible(true);
			ShowCompositeVoucherOcx.composite_direct.setVisible(false);
			if (tableDwbkPagingContext.getPage().getData().isEmpty()) {
				MessageDialog.openMessageDialog(null, "没有查询到数据！");
				return null;
			}
			new Thread() {
				public void run() {
			ShowCompositeVoucherOcx.init(0, (IDto) tableDwbkPagingContext
					.getPage().getData().get(0));		
				}}.start();
		}

		return super.search(o);
	}

	/**
	 * 根据业务类型查询需补录、复核的数据列表
	 * 
	 * @param biztype
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void queryTablelist(String biztype) {
		try {
			PageResponse response = retrieve(new PageRequest());
			if (null==response) {
				response =new PageResponse();
				setBreakMark(true);
				return;
			}else {
				setBreakMark(false);
			}
			if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
				if (!tablePagingContext.getPage().getData().isEmpty()) {
					tablePagingContext.getPage().getData().clear();
				}
				tablePagingContext.setPage(response);
				tablePagingContext.getPage().setData(response.getData());
				tablePagingContext.getPage().setTotalCount(
						response.getTotalCount());
			}else if(MsgConstant.VOUCHER_NO_5201.equals(biztype)) {
				if (!tableDirectPagingContext.getPage().getData().isEmpty()) {
					tableDirectPagingContext.getPage().getData().clear();
				}
				tableDirectPagingContext.setPage(response);
				tableDirectPagingContext.getPage().setData(response.getData());
				tableDirectPagingContext.getPage().setTotalCount(
						response.getTotalCount());
			}  else {
				if (!tablePagingContext.getPage().getData().isEmpty()) {
					tableDwbkPagingContext.getPage().getData().clear();
				}
				tableDwbkPagingContext.setPage(response);
				tableDwbkPagingContext.getPage().setData(response.getData());
				tableDwbkPagingContext.getPage().setTotalCount(
						response.getTotalCount());
			}

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
	}

	public List getBiztypelist() {
		return biztypelist;
	}

	public void setBiztypelist(List biztypelist) {
		this.biztypelist = biztypelist;
	}

	public List getQuerypayoutlist() {
		return querypayoutlist;
	}

	public void setQuerypayoutlist(List querypayoutlist) {
		this.querypayoutlist = querypayoutlist;
	}

	public String getBiztype() {
		return biztype;
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

	public void setBiztype(String biztype) {
		this.biztype = biztype;
		editor = MVCUtils.getEditor("c5dc725e-2b29-42bf-9e9f-77f1b06143a4");
		List<String> disvisContentAreaName = new ArrayList<String>();// 不可见area
		List<String> visContentAreaName = new ArrayList<String>();// 可见area
		if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
//			queryTablelist(biztype);
//			isVisiable(true);
			
			// 控制显示列表
			visContentAreaName.add("实拨资金信息列表");
			disvisContentAreaName.add("收入退付");
			disvisContentAreaName.add("直接支付");
		} else if (BizTypeConstant.VOR_TYPE_DWBK.equals(biztype)) {
//			queryTablelist(biztype);
//			isVisiable(false);
			// 控制显示列表
			visContentAreaName.add("收入退付");
			disvisContentAreaName.add("实拨资金信息列表");
			disvisContentAreaName.add("直接支付");
		} else if (MsgConstant.VOUCHER_NO_5201.equals(biztype)) {
//			queryTablelist(biztype);
//			isVisiable(false);
			// 控制显示列表
			visContentAreaName.add("直接支付");
			disvisContentAreaName.add("实拨资金信息列表");
			disvisContentAreaName.add("收入退付");
		}
		MVCUtils.setContentAreasToVisible(editor, visContentAreaName);
		MVCUtils.setContentAreaVisible(editor, disvisContentAreaName, false);
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged();
	}
	/**
	 * 是否显示金额范围控件 true 显示false 不显示
	 * @param flag
	 */
	private void isVisiable(boolean flag) {
		List currentAreas=editor.getCurrentVisibleAreas();
		ContainerMetaData cmetadata = (ContainerMetaData) currentAreas
		.get(0);
		ArrayList  controlMetadatas=(ArrayList) cmetadata.getControlMetadatas();
		for (int i = 0; i < controlMetadatas.size(); i++) {
			Object element=controlMetadatas.get(i);
			if (element instanceof TextMetaData) {
				if(((TextMetaData) element).id.equalsIgnoreCase("eb63d63f-ede9-4264-83e9-2efb21a702d4")
						||((TextMetaData) element).id.equalsIgnoreCase("f81c69d8-26db-4c56-943d-6623981496cc"))
				{
						((TextMetaData) element).visible=flag;
					
				}
			}
		}
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
     * @return
     */
    public String getOCXStampServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("获取OCX控件签章服务URL地址操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }

	/**
	 * Direction: 补录 ename: bulu 引用方法: viewers: 支付系统行号补录视图 messages:
	 */
	public String addRecord(Object o) {
		if (null == selectlist||selectlist.size()==0) {
			MessageDialog.openMessageDialog(null, "请选择要补录要素的数据记录！");
			return null;
		}else if (selectlist.size() > 1) {
			MessageDialog.openMessageDialog(null, "每次只能补录一条数据，请选择要补录的数据！");
			return null;
		}else{
			selDto = (IDto) selectlist.get(0);
		}
		try {
			if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
				payoutdto = (TvPayoutmsgmainDto) selDto;
				if (!payoutdto.getSifmatch().equals(
						StateConstant.IF_MATCHBNKNAME_YES)) {
					MessageDialog.openMessageDialog(null, "不需要补录行号，请审核、复核！");
					return "";
				} else {
					if (payoutdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_4)) {
						MessageDialog.openMessageDialog(null,
								"支付系统行号已复核，请选择其它操作！");
						return "";
					}
					if (payoutdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_1)) {
						MessageDialog.openMessageDialog(null, "支付系统行号已补录，请审核！");
						return "";
					}
				}
				payoutdto = (TvPayoutmsgmainDto) BuLUBankNoDialogFacade
						.openDialogProc(payoutdto, banklist, null, biztype,
								commonDataAccessService, null);
			} else if (BizTypeConstant.VOR_TYPE_DWBK.equals(biztype)) {
				dwbkdto = (TvDwbkDto) selDto;
				if (dwbkdto.getScheckstatus().equals(
						StateConstant.CHECKSTATUS_1)) {
					MessageDialog.openMessageDialog(null, "支付系统行号已补录，请选择其它操作！");
					return "";
				}
				if (dwbkdto.getScheckstatus().equals(
						StateConstant.CHECKSTATUS_4)) {
					MessageDialog.openMessageDialog(null, "支付系统行号已复核，请选择其它操作！");
					return "";
				}
				TsDwbkReasonDto drdto = new TsDwbkReasonDto();
				drdto.setSorgcode(loginfo.getSorgcode());
				List<TsDwbkReasonDto> listdto = commonDataAccessService
						.findRsByDto(drdto);
				dwbkdto = (TvDwbkDto) BuLUBankNoDialogFacade.openDialogProc(
						dwbkdto, banklist, null, biztype,
						commonDataAccessService, listdto);
			} else if (MsgConstant.VOUCHER_NO_5201.equals(biztype)) {
				directdto = (TfDirectpaymsgmainDto) selDto;
				if (!directdto.getSifmatch().equals(
						StateConstant.IF_MATCHBNKNAME_YES)) {
					MessageDialog.openMessageDialog(null, "不需要补录行号，请审核、复核！");
					return "";
				} else {
					if (directdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_4)) {
						MessageDialog.openMessageDialog(null,
								"支付系统行号已复核，请选择其它操作！");
						return "";
					}
					if (directdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_1)) {
						MessageDialog.openMessageDialog(null, "支付系统行号已补录，请审核！");
						return "";
					}
				}
				directdto = (TfDirectpaymsgmainDto) BuLUBankNoDialogFacade
						.openDialogProc(directdto, banklist, null, biztype,
								commonDataAccessService, null);
			}
		} catch (ITFEBizException e) {
			log.error("补录支付系统行号信息时错误!", e);
			MessageDialog.openErrorDialog(null, e);
		}
		editor.fireModelChanged();
		return super.addRecord(o);
	}

	/**
	 * Direction: 补复成功 ename: bufusuccess 引用方法: viewers: 支付系统行号补复视图 messages:
	 */
	public String bufusuccess(Object o) {
		
		// 校验只能复核审核成功的数据
		if (selectlist != null && selectlist.size() > 0) {
			int bufsuccess = 0 ;
			if (singStampflag) {
				
					//判断用户是否是复核人员并具有签复核章的权限
					stamp=null; //初始化私章
					TsUsersPK userpk = new TsUsersPK();
					userpk.setSorgcode(loginfo.getSorgcode());
					userpk.setSusercode(loginfo.getSuserCode());
					try {
						uDto=(TsUsersDto) commonDataAccessService.find(userpk);
					} catch (ITFEBizException e2) {
						MessageDialog.openMessageDialog(null, "查询异常: "+e2.getMessage());
						e2.printStackTrace();
						return "";
					}
					if (StringUtils.isBlank(uDto.getSstamppermission())) {
						MessageDialog.openMessageDialog(null, "当前用户无签核章权限，请选择其他用户进行复核 ！");
						return "";
					}
			}
			StringBuffer error = new StringBuffer("");
			TsConvertbanknameDto convertbankdto = new TsConvertbanknameDto();
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "提示", "是否要将选中数据复核成功吗？");
			if (flag) {
				if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
					for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
						if (!singStampflag) {
							if (!(paydto.getScheckstatus().equals(
									StateConstant.CHECKSTATUS_3)||(paydto.getSifmatch().equals(
											StateConstant.IF_MATCHBNKNAME_NO)&&!paydto.getScheckstatus().equals(
													StateConstant.CHECKSTATUS_4)))) {
								MessageDialog.openMessageDialog(null,"只能复核审核成功或不需要补录的数据！");
								if(paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4))
								{
									try {
										TvVoucherinfoPK pk = new TvVoucherinfoPK();
										pk.setSdealno(paydto.getSbizno());
										TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
										if(DealCodeConstants.VOUCHER_CHECKSUCCESS.equals(vDto.getSstatus()))
										{
											vDto.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
											vDto.setSdemo("复核成功");
											commonDataAccessService.updateData(vDto);
										}
									} catch (ITFEBizException e) {
										log.error(e);
										MessageDialog.openErrorDialog(null, e);
									}
								}
								return "";
							}
						}else if(!(paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_3))){
							MessageDialog.openMessageDialog(null,"只能复核审核成功的数据！");
							return "";
						}
						if (paydto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)&&paydto.getSinputusercode().equals(loginfo.getSuserCode())) {
							MessageDialog.openMessageDialog(null,"补录、复核不能是同一用户！");
							return "";
						}
						// 更新行号
						try {
							paydto.setScheckstatus(StateConstant.CHECKSTATUS_4);// 复核状态为已复核
							paydto.setSchecksercode(loginfo.getSuserCode());// 复核人
							//数据库更新在最后代码中进行{code}
							// 更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(paydto.getSbizno());
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							
							if(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_CHECKSUCCESS))
							{
								vDto.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
								vDto.setSdemo("复核成功");
							}
							if (paydto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)) {
								// 把新行名行号插入行名对照表
								convertbankdto.setSorgcode(loginfo.getSorgcode());
								convertbankdto.setSbankname(paydto.getSrecbankname());
								convertbankdto.setStcbankname(paydto.getSinputrecbankname());
								convertbankdto.setSbankcode(paydto.getSrecbankno());
								bankNameEnterService.save(convertbankdto);
							}
								if (singStampflag) {
									TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//设置签章信息
									List lists = validStampfactor(uDto, vDto, stampDto);
									if (null==lists) {
										return "";
									}
						    		Integer count = voucherLoadService.voucherStamp(lists);
									if (count==1) {
										vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
										commonDataAccessService.updateData(paydto);
										commonDataAccessService.updateData(vDto);
										bufsuccess++;
									}else
									{
										vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
										error.append(vDto.getSvoucherno()+":"+vDto.getSreturnerrmsg());
										commonDataAccessService.updateData(paydto);
										commonDataAccessService.updateData(vDto);
										bufsuccess++;
									}
								}
								else{
									commonDataAccessService.updateData(paydto);
									commonDataAccessService.updateData(vDto);
									bufsuccess++;
								}
						} catch (ITFEBizException e) {
							log.error(e);
							MessageDialog.openErrorDialog(null, e);
						}
					}
				} else if (biztype.equals(BizTypeConstant.VOR_TYPE_DWBK)) {
					for (TvDwbkDto dwdkdto : (ArrayList<TvDwbkDto>) selectlist) {
						if (!singStampflag) {
							if (!(dwdkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_3)
									||(dwdkdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_NO)
									&&!dwdkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)))) {
								MessageDialog.openMessageDialog(null,"只能复核审核成功或不需要补录的数据！");
								return "";
							}
						}else if(!(dwdkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_3))){
								MessageDialog.openMessageDialog(null,"只能复核审核成功的数据！");
								return "";
							
						}
						if (dwdkdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)&&dwdkdto.getSinputusercode().equals(loginfo.getSuserCode())) {
							MessageDialog.openMessageDialog(null,
									"补录、复核不能是同一用户！");
							return "";
						}
						// 更新行号
						try {
							dwdkdto.setScheckstatus(StateConstant.CHECKSTATUS_4);// 复核状态为已复核
							dwdkdto.setScheckusercode(loginfo.getSuserCode());// 复核人
							// 更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(dwdkdto.getSbizno());
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							vDto.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
							vDto.setSdemo("复核成功");
							if (dwdkdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)) {
								// 把新行名行号插入行名对照表
								convertbankdto.setSorgcode(loginfo.getSorgcode());
								convertbankdto.setSbankname(dwdkdto.getSrecbankname());
								convertbankdto.setStcbankname(dwdkdto.getSinputrecbankname());
								convertbankdto.setSbankcode(dwdkdto.getSpayeeopnbnkno());
								bankNameEnterService.save(convertbankdto);
							}
							if (singStampflag) {
								TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//设置签章信息
								List lists = validStampfactor(uDto, vDto, stampDto);
								if (null==lists) {
									return "";
								}
					    		Integer count = voucherLoadService.voucherStamp(lists);
								if (count==1) {
									vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
									commonDataAccessService.updateData(dwdkdto);
									commonDataAccessService.updateData(vDto);
									++bufsuccess;
								}else
								{
									vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
									error.append(vDto.getSvoucherno()+":"+vDto.getSreturnerrmsg());
									commonDataAccessService.updateData(dwdkdto);
									commonDataAccessService.updateData(vDto);
									bufsuccess++;
								}
							}else{
								commonDataAccessService.updateData(dwdkdto);
								commonDataAccessService.updateData(vDto);
								++bufsuccess;
							}
						} catch (ITFEBizException e) {
							log.error(e);
							MessageDialog.openMessageDialog(null, "复核数据出错，请重新复核！");
						}
					}
				}else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
					for (TfDirectpaymsgmainDto directdto : (ArrayList<TfDirectpaymsgmainDto>) selectlist) {
						if (!singStampflag) {
							if (!(directdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_3)||
									(directdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_NO)
									&&!directdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)))) {
								MessageDialog.openMessageDialog(null,"只能复核审核成功或不需要补录的数据！");
								return "";
							}
						}else if(!(directdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_3))){
							MessageDialog.openMessageDialog(null,"只能复核审核成功的数据！");
							return "";
						}
						if (directdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)&&directdto.getSinputusercode().equals(loginfo.getSuserCode())) {
							MessageDialog.openMessageDialog(null,"补录、复核不能是同一用户！");
							return "";
						}
						// 更新行号
						try {
							directdto.setScheckstatus(StateConstant.CHECKSTATUS_4);// 复核状态为已复核
							directdto.setSchecksercode(loginfo.getSuserCode());// 复核人
							// 更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(directdto.getIvousrlno()+"");
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							vDto.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
							vDto.setSdemo("复核成功");
							if (directdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)) {
								// 把新行名行号插入行名对照表
								convertbankdto.setSorgcode(loginfo.getSorgcode());
								convertbankdto.setSbankname(directdto.getSinputrecbankname());
								convertbankdto.setStcbankname(directdto.getSinputrecbankname());
								convertbankdto.setSbankcode(directdto.getSpayeeacctbankno());
								bankNameEnterService.save(convertbankdto);
							}
							if (singStampflag) {
								TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//设置签章信息
								List lists = validStampfactor(uDto, vDto, stampDto);
								if (null==lists) {
									return "";
								}
					    		Integer count = voucherLoadService.voucherStamp(lists);
								if (count==1) {
									vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
									commonDataAccessService.updateData(directdto);
									commonDataAccessService.updateData(vDto);
									++bufsuccess;
								}
							}else{
								commonDataAccessService.updateData(directdto);
								commonDataAccessService.updateData(vDto);
								++bufsuccess;
							}
						} catch (ITFEBizException e) {
							log.error(e);
							MessageDialog.openErrorDialog(null, e);
						}
					}
				}
				if (singStampflag) {
					MessageDialog.openMessageDialog(null, "复核结果：成功[ "+bufsuccess+" ]笔，失败[ "+(selectlist.size()-bufsuccess)+" ]笔!!!"+error.toString());
				}else
				{
					MessageDialog.openMessageDialog(null, "复核结果：成功[ "+bufsuccess+" ]笔，失败[ "+(selectlist.size()-bufsuccess)+" ]笔!!!");
				}
			}
			editor.fireModelChanged();
		} else {
			MessageDialog.openMessageDialog(null, "请选择要复核的数据！");
		}
		return super.bufusuccess(o);
	}

	/**
	 * 签章 
	 * 
	 */
	@Override
	public String singStamp(Object o) {
		return super.singStamp(o);
	}

	 /**
     * 刷新签章类型
     * 获取用户唯一的私章
     */
    public TsStamppositionDto refreshStampType(TvVoucherinfoDto dto,TsUsersDto currentUser){
    	TsStamppositionDto returnDto = null;
    	TsStamppositionDto tsDto=new TsStamppositionDto();
        tsDto.setSorgcode(dto.getSorgcode());
      	tsDto.setSvtcode(dto.getSvtcode());
      	tsDto.setStrecode(dto.getStrecode());
      	tsDto.setSadmdivcode(dto.getSadmdivcode());
      	Set set=new HashSet();
      	TsStamppositionDto sDto=new TsStamppositionDto();
      	List<TsStamppositionDto> tList=null;
      	ArrayList stampList = new ArrayList();
      	List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
      	try{
      		//获取用户的签章权限
          	tList= commonDataAccessService.findRsByDto(tsDto);
          	if(tList.size()>0){
          		//判处公章、转讫章、附件章等信息
          		for(TsStamppositionDto sdto:tList){ 
          			String tmpstamp= sdto.getSstamptype();
          			if (!tmpstamp.endsWith(MsgConstant.VOUCHERSAMP_OFFICIAL)
          					&&!tmpstamp.endsWith(MsgConstant.VOUCHERSAMP_ROTARY)
          					&&!tmpstamp.endsWith(MsgConstant.VOUCHERSAMP_ATTACH)
          					&&!tmpstamp.endsWith(MsgConstant.VOUCHERSAMP_BUSS)
          					&& currentUser.getSstamppermission().contains(tmpstamp) 
              						) 
          			{
          				set.add(tmpstamp);
          				stamp = tmpstamp;
          				returnDto = sdto;
          				break;
					}
          		}
          		if (null==stamp || stamp.trim().length()<=0) {
          			MessageDialog.openMessageDialog(null, "该用户无此类型私章信息，请检查用户签章权限信息和凭证签章位置信息！");
              		return null;
				}

          	}else {
          		MessageDialog.openMessageDialog(null, "无该类型签章权限，请获取签章位置信息！");
          		return null;
			}      		
      	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
      		log.error(e);
      		Exception e1=new Exception("凭证刷新操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);			   			
      	}
		return returnDto;
    }
	/**
	 * 判断印章对应的证书是否已经维护
	 * @return
	 */
	public Boolean checkStampIDAndCertID(TsTreasuryDto tDto,TsUsersDto uDto,String strecode,TsStamppositionDto stampDto){
		/*
		 * 校验当前用户私章证书ID、印章ID参数是否已维护
		 */
			if(uDto.getScertid()==null||uDto.getScertid().equals("")){
				MessageDialog.openMessageDialog(null, "当前用户  "+stampDto.getSstampname()+"  证书ID参数未维护！ ");        				
				return Boolean.FALSE;
			}else if(uDto.getSstampid()==null||uDto.getSstampid().equals("")){
				MessageDialog.openMessageDialog(null, "当前用户   "+stampDto.getSstampname()+"  印章ID参数未维护！ ");        				
				return Boolean.FALSE;
			}    		    			
		return Boolean.TRUE;
	}
	/**
	 * 根据不同的印章类型进行盖章，在盖章过程中区分客户端盖章和服务器端盖章
	 * 
	 * @throws ITFEBizException 
	 */
	private void SignStamp(TvVoucherinfoDto vDto,String seq,List sList,TsTreasuryDto tDto,List sinList,TsStamppositionDto sDto,TsUsersDto uDto) throws ITFEBizException{
		//客户端签私人印章
		if(!loginfo.getPublicparam().contains(",jbrstamp=server,"))
		{
			sinList.add(ShowCompositeVoucherOcx.getVoucherStamp(vDto, uDto.getScertid(), sDto.getSstampposition(),uDto.getSstampid()));
		}
	}

	/**
	 * 校验签章顺序，按照签章顺序签章
	 * @return
	 * @throws ITFEBizException 
	 */
	public Boolean checkSignStampSeq(TvVoucherinfoDto vDto,String seq,List sList,String strecode,TsStamppositionDto stampDto,String stampid) throws ITFEBizException{

	List<String> seqList=new ArrayList<String>();
	for(int i=0;i<sList.size();i++){
		TsStamppositionDto tsDto=(TsStamppositionDto) sList.get(i);
		if(tsDto.getSstampsequence()!=null&&!tsDto.getSstampsequence().equals("")){
			seqList.add(tsDto.getSstampsequence());
		}
	}
	if(seqList!=null&&seqList.size()>0){
		String[] seqs=seqList.toArray(new String[seqList.size()]);
		ShowCompositeVoucherOcx.sortStringArray(seqs);
		
		String temp="";
		for(int i=seqs.length-1;i>-1;i--){
			if(Integer.parseInt(seqs[i])<Integer.parseInt(seq)){
				temp=seqs[i];
				break;
			}
		}
		if(!temp.equals("")){ 
			List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
			TsStamppositionDto tsDto=new TsStamppositionDto();
			tsDto.setSorgcode(loginfo.getSorgcode());
			tsDto.setStrecode(strecode);
			tsDto.setSvtcode(vDto.getSvtcode());
			tsDto.setSstampsequence(temp);
			tsList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(tsDto);
			String err="";
			for(TsStamppositionDto tstampDto:tsList){
				err=tstampDto.getSstampname()+" , "+err;
			}
			err=err.substring(0,err.lastIndexOf(","));
			if(stampid==null||stampid.equals("")){
				MessageDialog.openMessageDialog(null, "国库代码："+vDto.getStrecode()+" 凭证类型: "+vDto.getSvtcode()+vDto.getSvoucherno()+" \""+stampDto.getSstampname()+"\"签章前请先 \""+err+"\"签章！");
    			return Boolean.FALSE;
			}else{
				err="";
				String[] stampids=stampid.split(",");
				List<TsStamppositionDto> tsList1=new ArrayList<TsStamppositionDto>();
				for(int j=0;j<tsList.size();j++){
					for(int i=0;i<stampids.length;i++){
						if(stampids[i].equals(tsList.get(j).getSstamptype())){
							tsList1.add(tsList.get(j));
						}
					}
				}
				tsList.removeAll(tsList1);
				if(tsList.size()>0){
					for(TsStamppositionDto tstampDto:tsList){
        				err=tstampDto.getSstampname()+" , "+err;
        			}
					err=err.substring(0,err.lastIndexOf(","));
					MessageDialog.openMessageDialog(null, "国库代码："+vDto.getStrecode()+" 凭证类型: "+vDto.getSvtcode()+" \""+stampDto.getSstampname()+"\"签章前请先 \""+err+"\"签章！");
	    			return Boolean.FALSE;
				}
			}
		
		}
	}
	return Boolean.TRUE;
	}
    public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException{
    	
    	return voucherLoadService.voucherStampXml(vDto);
    }
	/**
	 * Direction: 审核通过 ename: checkSuccess 引用方法: viewers: * messages:
	 */
	public String checkSuccess(Object o) {
		
		if (selectlist != null && selectlist.size() > 0) {
			int cksuccess=0;
			if (singStampflag) {
				//判断用户是否是审核人员并具有私章的权限
				stamp=null;//初始化私章
				TsUsersPK userpk = new TsUsersPK();
				userpk.setSorgcode(loginfo.getSorgcode());
				userpk.setSusercode(loginfo.getSuserCode());
				try {
					uDto=(TsUsersDto) commonDataAccessService.find(userpk);
				} catch (ITFEBizException e2) {
					MessageDialog.openMessageDialog(null, "查询异常: "+e2.getMessage());
					e2.printStackTrace();
					return "";
				}
				if (StringUtils.isBlank(uDto.getSstamppermission())) {
					MessageDialog.openMessageDialog(null, "当前用户无签私章权限，请选择其他用户进行审核 ！");
					return "";
				}
			}
			StringBuffer error = new StringBuffer("");
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
					null, "提示", "是否要将选中数据审核成功吗？");
			if (flag) {
				if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
					for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
						if (!singStampflag) {
							if(paydto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_NO)){
								MessageDialog.openMessageDialog(null,
								"财政已带支付行号的实拨信息只需复核！");
								return "";
							}
						}
						if (!(paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_1)||paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_2))) {
							MessageDialog.openMessageDialog(null,
									"只能审核已补录或审核失败的数据，请重新选择数据！");
							return "";
						}else if(paydto.getSinputrecbankno()==null||"".equals(paydto.getSinputrecbankno()))
						{
							MessageDialog.openMessageDialog(null,"补录的行号"+paydto.getSinputrecbankno()+"有误，请审核失败重新补录行号！");
							return "";
						}
						//陕西：只有校验成功并且比对成功的数据才可执行审核通过操作 -- start
						if("260000000002".equals(loginfo.getSorgcode()))
						{
							TvVoucherinfoDto qdo = new TvVoucherinfoDto();
							qdo.setSdealno(paydto.getSbizno());
							try 
							{
								List<TvVoucherinfoDto> list = (List<TvVoucherinfoDto>)commonDataAccessService.findRsByDto(qdo);
								if(list != null && list.size()>0)
								{
									TvVoucherinfoDto stus = list.get(0);
									
									if(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(stus.getSstatus()) && !"数据比对成功".equals(stus.getSdemo()))
									{
										String msg = "部分数据尚未比对成功，是否继续";
						    	    	boolean isContinue = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "提示", msg);
						    	    	if (isContinue == false)
						    	    	{
						    				return null;
						    			}else
						    			{
						    				if(AdminConfirmDialogFacade.open("需要主管授权才能继续执行操作"))
						    	    		{
						    					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						    				}
						    			}
									}
								}else 
								{
									MessageDialog.openMessageDialog(null, "查询凭证索引表无数据！");
				        			return "";
								}
							} catch (ITFEBizException e) 
							{
								log.error("查询凭证索引表异常："+e);
								MessageDialog.openMessageDialog(null, "查询凭证索引表异常！");
			        			return "";
							}
						}
						//陕西：只有校验成功并且比对成功的数据才可执行审核通过操作 -- end

						// 更新状态
						try {
						
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(paydto.getSbizno());
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							
							//签章开始
								if (singStampflag) {
									TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//设置签章信息
									List lists = validStampfactor(uDto, vDto, stampDto);
									if (null==lists) {
										return "";
									}
						    		Integer count = voucherLoadService.voucherStamp(lists);
						    		
					    		//签章结束
						    		if (count==1) {
						    			vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
						    			paydto.setScheckstatus(StateConstant.CHECKSTATUS_3);// 复核状态为审核通过
										paydto.setSinputusercode(loginfo.getSuserCode());
										commonDataAccessService.updateData(paydto);
										// 更新凭证库信息
										vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
										vDto.setSdemo("审核成功");
										//同步更新凭证审核信息
										commonDataAccessService.updateData(vDto);
										++cksuccess;
									}else
									{
										vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
										error.append(vDto.getSvoucherno()+":"+vDto.getSreturnerrmsg());
						    			paydto.setScheckstatus(StateConstant.CHECKSTATUS_3);// 复核状态为审核通过
										paydto.setSinputusercode(loginfo.getSuserCode());
										commonDataAccessService.updateData(paydto);
										// 更新凭证库信息
										vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
										vDto.setSdemo("审核成功");
										//同步更新凭证审核信息
										commonDataAccessService.updateData(vDto);
										++cksuccess;
									}
								}else{
									paydto.setScheckstatus(StateConstant.CHECKSTATUS_3);// 复核状态为审核通过
									paydto.setSinputusercode(loginfo.getSuserCode());
									commonDataAccessService.updateData(paydto);
									// 更新凭证库信息
									vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
									vDto.setSdemo("审核成功");
									//同步更新凭证审核信息
									commonDataAccessService.updateData(vDto);
									++cksuccess;
							}
							
						} catch (ITFEBizException e) {
							log.error(e);
							MessageDialog.openErrorDialog(null, e);
						}
					}
				} else if (biztype.equals(BizTypeConstant.VOR_TYPE_DWBK)) {
					for (TvDwbkDto dwbkdto : (ArrayList<TvDwbkDto>) selectlist) {
						if (!singStampflag) {
							if(dwbkdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_NO)){
								MessageDialog.openMessageDialog(null,
								"财政已带支付行号的收入退付信息只需复核！");
								return "";
							}
						}
						if (!(dwbkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_1)||dwbkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_2))) {
							MessageDialog.openMessageDialog(null,
									"只能审核已补录或审核失败的数据，请重新选择数据！");
							return "";
						}if(dwbkdto.getSinputrecbankno()==null||"".equals(dwbkdto.getSinputrecbankno()))
						{
							MessageDialog.openMessageDialog(null,"补录的行号"+dwbkdto.getSinputrecbankno()+"有误，请审核失败重新补录行号！");
							return "";
						}
						// 更新状态
						try {
							//更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(dwbkdto.getSbizno());
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							
							if (singStampflag) { //签章开始
								TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//设置签章信息
								List lists = validStampfactor(uDto, vDto, stampDto);
								if (null==lists) {
									return "";
								}
					    		Integer count = voucherLoadService.voucherStamp(lists);
								//签章结束
					    		if (count==1) {
						    		vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
									dwbkdto.setScheckstatus(StateConstant.CHECKSTATUS_3);// 复核状态为审核通过
									dwbkdto.setSinputusercode(loginfo.getSuserCode());
									commonDataAccessService.updateData(dwbkdto);
									vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
									vDto.setSdemo("审核成功");
									commonDataAccessService.updateData(vDto);
									++cksuccess;
								}else
								{
									vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
									error.append(vDto.getSvoucherno()+":"+vDto.getSreturnerrmsg());
									dwbkdto.setScheckstatus(StateConstant.CHECKSTATUS_3);// 复核状态为审核通过
									dwbkdto.setSinputusercode(loginfo.getSuserCode());
									commonDataAccessService.updateData(dwbkdto);
									vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
									vDto.setSdemo("审核成功");
									commonDataAccessService.updateData(vDto);
									++cksuccess;
								}

							}else{
								dwbkdto.setScheckstatus(StateConstant.CHECKSTATUS_3);// 复核状态为审核通过
								dwbkdto.setSinputusercode(loginfo.getSuserCode());
								commonDataAccessService.updateData(dwbkdto);
								vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
								vDto.setSdemo("审核成功");
								commonDataAccessService.updateData(vDto);
								++cksuccess;
								}
							
						} catch (ITFEBizException e) {
							log.error(e);
							MessageDialog.openErrorDialog(null, e);
						}
					}
				} else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
					for (TfDirectpaymsgmainDto directdto : (ArrayList<TfDirectpaymsgmainDto>) selectlist) {
						if (!singStampflag) {
							if(directdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_NO)){
								MessageDialog.openMessageDialog(null,
								"财政已带支付行号的直接支付信息只需复核！");
								return "";
							}
						}
						if (!(directdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_1)||directdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_2))) {
							MessageDialog.openMessageDialog(null,
									"只能审核已补录或审核失败的数据，请重新选择数据！");
							return "";
						}if(directdto.getSpayeeacctbankno()==null||"".equals(directdto.getSpayeeacctbankno()))
						{
							MessageDialog.openMessageDialog(null,"补录的行号"+directdto.getSpayeeacctbankno()+"有误，请审核失败重新补录行号！");
							return "";
						}
						// 更新状态
						try {
							// 更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(directdto.getIvousrlno()+"");
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							if (singStampflag) {
								TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//设置签章信息
								List lists = validStampfactor(uDto, vDto, stampDto);
								if (null==lists) {
									return "";
								}
					    		//凭证签章
					    		Integer count = voucherLoadService.voucherStamp(lists);
					    		if (count==1) {
					    			vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
									directdto.setScheckstatus(StateConstant.CHECKSTATUS_3);// 复核状态为审核通过
									directdto.setSinputusercode(loginfo.getSuserCode());
									commonDataAccessService.updateData(directdto);
							
									vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
									vDto.setSdemo("审核成功");
									commonDataAccessService.updateData(vDto);
									++cksuccess;
								}
							}else{
							
								directdto.setScheckstatus(StateConstant.CHECKSTATUS_3);// 复核状态为审核通过
								directdto.setSinputusercode(loginfo.getSuserCode());
								commonDataAccessService.updateData(directdto);
								vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
								vDto.setSdemo("审核成功");
								commonDataAccessService.updateData(vDto);
								++cksuccess;
							}
						} catch (ITFEBizException e) {
							log.error(e);
							MessageDialog.openErrorDialog(null, e);
						}
					}
				}
				if(singStampflag)
				{
					MessageDialog.openMessageDialog(null, "审核结果 ：成功[ "+cksuccess+" ]笔,失败[ "+ (selectlist.size()-cksuccess)+" ]笔"+error.toString());
				}else
				{
					MessageDialog.openMessageDialog(null, "审核结果 ：成功[ "+cksuccess+" ]笔,失败[ "+ (selectlist.size()-cksuccess)+" ]笔");
				}
			}
			editor.fireModelChanged();
		} else {
			MessageDialog.openMessageDialog(null, "请选择要审核的数据！");
		}
		return super.checkSuccess(o);
	}

	/**
	 * 判断签章条件
	 * @param uDto
	 * @param vDto
	 * @param stampDto
	 * @return
	 * @throws ITFEBizException
	 */
	private List validStampfactor(TsUsersDto uDto, TvVoucherinfoDto vDto,
			TsStamppositionDto stampDto) throws ITFEBizException {
		TsTreasuryDto tDto=new TsTreasuryDto();
		TsStamppositionDto sDto=new TsStamppositionDto();
		Map map=new HashMap();
		String usercode=uDto.getSusercode();
		String stampuser="";
		String stampid="";
//		for(TvVoucherinfoDto vDto:checkList){
			map.put(vDto.getStrecode(), "");
			stampid=vDto.getSstampid();
			/*
			 * 校验凭证是否已签当前选定印章，同一凭证不能重复签章
			 */
			if(stampid!=null&&!stampid.equals("")){
				String[] stampids=stampid.split(",");
				for(int i=0;i<stampids.length;i++){
					if(stamp.equals(stampids[i])){
						MessageDialog.openMessageDialog(null, "凭证编号："+vDto.getSvoucherno()+" 已签 私章 \""+stampDto.getSstampname()+"\" ，同一凭证不能重复签章！");			    			
		    			return null;
					}
				}
			}
			/*
			 * 校验用户是否已签私章，同一用户只能签一次私章
			 */
			if(!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)&&!stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)&& !stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)&& !stamp.equals(MsgConstant.VOUCHERSAMP_BUSS) ){
				stampuser=vDto.getSstampuser();
				if(stampuser!=null&&!stampuser.equals("")){
					String[] stampusers=stampuser.split(",");
					for(int i=0;i<stampusers.length;i++){							
						if(usercode.equals(stampusers[i])){
							boolean flag2=false;
							String[]  stampids=vDto.getSstampid().split(",");
							for(int j=0;j<stampids.length;j++){
								if(stampids[i].equalsIgnoreCase(stamp)){
									flag2 = true;
									break;
								}
							}
							if (flag2) {
								MessageDialog.openMessageDialog(null, "凭证编号："+vDto.getSvoucherno()+" 当前用户已签  \""+stampDto.getSstampname()+"\" ，同一用户只能签一次私章，请选择其他用户！");    			    			
				    			return null;
							}
							
						}
					}
				
				}
			}
//		}    		
		Iterator it=map.keySet().iterator();    		
		List lists=new ArrayList();
		List list=null;
		List sinList=null;
		List<TsStamppositionDto> sList=null;
		List vList=null;
		String strecode="";
		String xml="";
		while(it.hasNext()){
			 strecode=it.next()+"";
			 vList=new ArrayList();
			 tDto=new TsTreasuryDto();
			 sDto=new TsStamppositionDto();
			 sList=new ArrayList<TsStamppositionDto>();
			 list=new ArrayList();
			 //查询国库主体代码参数
			try{
				tDto.setSorgcode(loginfo.getSorgcode());
				tDto.setStrecode(strecode);
				tDto=(TsTreasuryDto) commonDataAccessService.findRsByDto(tDto).get(0);
			}catch(Exception e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中不存在！");
				
				return null;
			}
			//查询签章位置信息
			try{
				sDto.setSorgcode(loginfo.getSorgcode());
				sDto.setStrecode(strecode);
				sDto.setSvtcode(biztype);
				sList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(sDto);
				sDto.setSstamptype(stamp);
				sDto= (TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
				
			}catch(Exception e){    			
				log.error(e);
				MessageDialog.openMessageDialog(null, "国库主体代码："+strecode+" "+stampDto.getSstampname()+" 参数未维护！ ");    				
				return null;
			}
 
			//校验印章对应的证书ID、印章ID是否已经维护
			Boolean b = checkStampIDAndCertID(tDto, uDto, strecode, stampDto);
			if (!b){
				return null;
			}
//			for(TvVoucherinfoDto vDto:checkList){
				if(vDto.getStrecode().equals(strecode)){    					
					sinList=new ArrayList();
					sinList.add(vDto);
					stampid=vDto.getSstampid();	
					String seq=sDto.getSstampsequence();
					/*
					 * 凭证按签章顺序签章：签章顺序号小的优先签章
					 * 
					 */
					if(seq!=null&&!seq.equals("")){
						Boolean bo = checkSignStampSeq(vDto, seq, sList, strecode, stampDto, stampid);
						if (!bo) {
							return null;
						}
					}
					/**
					 * 判断签章类型，进行签章
					 */
					SignStamp(vDto, seq, sList, tDto, sinList, sDto, uDto);
					vList.add(sinList);
				}
//			}
			list.add(uDto);
			list.add(tDto);
			list.add(sDto);
			list.add(sList.size());
			list.add(vList);    			
			lists.add(list);
		}
		return lists;
	}

	/**
	 * Direction: 审核失败 ename: checkfault 引用方法: viewers: * messages:
	 */
	public String checkfault(Object o) {
		try {
			if (selectlist == null || selectlist.size() == 0) {
				MessageDialog.openMessageDialog(null, "请选择要审核的数据！");
			} else if (selectlist.size() > 1) {
				MessageDialog.openMessageDialog(null, "每次只能对一条数据进行审核失败操作！");
			} else {				
				boolean flag = org.eclipse.jface.dialogs.MessageDialog
						.openConfirm(null, "提示", "是否要将选中数据审核失败吗？");
				if (flag) {
					if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
						for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
							if(paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)){
								MessageDialog.openMessageDialog(null, "已复核成功数据不能审核失败！");
								return "";
							}
						}
					}else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
						for (TfDirectpaymsgmainDto paydto : (ArrayList<TfDirectpaymsgmainDto>) selectlist) {
							if(paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)){
								MessageDialog.openMessageDialog(null, "已复核成功数据不能审核失败！");
								return "";
							}
						}
					}else{
						for (TvDwbkDto dwbkdto : (ArrayList<TvDwbkDto>) selectlist) {
							if(dwbkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)){
								MessageDialog.openMessageDialog(null, "已复核成功数据不能审核失败！");
								return "";
							}
						}
					}
					TsCheckfailreasonDto dto = new TsCheckfailreasonDto();
					dto.setSorgcode(loginfo.getSorgcode());
					List<TsCheckfailreasonDto> checkfail = commonDataAccessService.findRsByDto(dto);
					if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
						for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
							
							// 更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(paydto.getSbizno());
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,commonDataAccessService)) {
								// 更新状态
								paydto.setScheckstatus(StateConstant.CHECKSTATUS_2);// 复核状态为审核失败
								paydto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);

								commonDataAccessService.updateData(paydto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "审核失败！");
							} else {
								MessageDialog.openMessageDialog(null, "请重新审核！");
							}
						}
					} else if (biztype.equals(BizTypeConstant.VOR_TYPE_DWBK)) {
						for (TvDwbkDto dwbkdto : (ArrayList<TvDwbkDto>) selectlist) {
							// 更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(dwbkdto.getSbizno());
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService
									.find(pk);
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,
									commonDataAccessService)) {
								// 更新状态
								dwbkdto
										.setScheckstatus(StateConstant.CHECKSTATUS_2);// 复核状态为审核失败
								dwbkdto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);
						
								commonDataAccessService.updateData(dwbkdto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "审核失败！");
							} else {
								MessageDialog.openMessageDialog(null, "请重新审核！");
							}
						}
					} else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
						for (TfDirectpaymsgmainDto directdto : (ArrayList<TfDirectpaymsgmainDto>) selectlist) {
							
							// 更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(directdto.getIvousrlno()+"");
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService
									.find(pk);
							
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,
									commonDataAccessService)) {
								// 更新状态
								directdto.setScheckstatus(StateConstant.CHECKSTATUS_2);// 复核状态为审核失败
								directdto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);

								commonDataAccessService.updateData(directdto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "审核失败！");
							} else {
								MessageDialog.openMessageDialog(null, "请重新审核！");
							}
						}
					} 
				}
				editor.fireModelChanged();
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.checkfault(o);
	}
	/**
	 * Direction: 复核失败 ename: checkfault 引用方法: viewers: * messages:
	 */
	public String reviewfault(Object o) {
		try {
			if (selectlist == null || selectlist.size() == 0) {
				MessageDialog.openMessageDialog(null, "请选择要复核的数据！");
			} else if (selectlist.size() > 1) {
				MessageDialog.openMessageDialog(null, "每次只能对一条数据进行复核失败操作！");
			} else {				
				boolean flag = org.eclipse.jface.dialogs.MessageDialog
						.openConfirm(null, "提示", "是否要将选中数据复核失败吗？");
				if (flag) {
					if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
						for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
							if(!paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)){
								MessageDialog.openMessageDialog(null, "只有复核成功的数据才可复核失败！");
								return "";
							}
						}
					}else{
						for (TvDwbkDto dwbkdto : (ArrayList<TvDwbkDto>) selectlist) {
							if(!dwbkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)){
								MessageDialog.openMessageDialog(null, "只有复核成功的数据才可复核失败！");
								return "";
							}
						}
					}
					TsCheckfailreasonDto dto = new TsCheckfailreasonDto();
					dto.setSorgcode(loginfo.getSorgcode());
					List<TsCheckfailreasonDto> checkfail = commonDataAccessService.findRsByDto(dto);
					if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
						for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
							
							// 更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(paydto.getSbizno());
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,commonDataAccessService)) {
								// 更新状态
								paydto.setScheckstatus(StateConstant.CHECKSTATUS_2);// 复核状态为审核失败
								paydto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);

								commonDataAccessService.updateData(paydto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "复核失败！");
							} else {
								MessageDialog.openMessageDialog(null, "请重新审核！");
							}
						}
					} else if (biztype.equals(BizTypeConstant.VOR_TYPE_DWBK)) {
						for (TvDwbkDto dwbkdto : (ArrayList<TvDwbkDto>) selectlist) {
							// 更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(dwbkdto.getSbizno());
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService
									.find(pk);
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,
									commonDataAccessService)) {
								// 更新状态
								dwbkdto
										.setScheckstatus(StateConstant.CHECKSTATUS_2);// 复核状态为审核失败
								dwbkdto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);
						
								commonDataAccessService.updateData(dwbkdto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "复核失败！");
							} else {
								MessageDialog.openMessageDialog(null, "请重新审核！");
							}
						}
					} else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
						for (TfDirectpaymsgmainDto directdto : (ArrayList<TfDirectpaymsgmainDto>) selectlist) {
							
							// 更新凭证库信息
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(directdto.getIvousrlno()+"");
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService
									.find(pk);
							
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,
									commonDataAccessService)) {
								// 更新状态
								directdto.setScheckstatus(StateConstant.CHECKSTATUS_2);// 复核状态为审核失败
								directdto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);

								commonDataAccessService.updateData(directdto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "复核失败！");
							} else {
								MessageDialog.openMessageDialog(null, "请重新审核！");
							}
						}
					} 
				}
				editor.fireModelChanged();
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.checkfault(o);
	}
	/**
	 * Direction 批量打印凭证 （选择的数据）
	 */
	public String printSelectedVoucher(Object o) {
		if (null==selectlist || selectlist.size() < 0) {
			MessageDialog.openMessageDialog(null, "请选择需要打印的凭证  !!!");
		}
		return super.printSelectedVoucher(o);
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
		int count = 0;
		TsVouchercommitautoDto tDto = new TsVouchercommitautoDto();
		tDto.setSorgcode(vDto.getSorgcode());
		tDto.setStrecode(vDto.getStrecode());
		tDto.setSvtcode(vDto.getSvtcode());
		try {
			tDto = (TsVouchercommitautoDto) commonDataAccessService
					.findRsByDto(tDto).get(0);
			if (tDto.getSjointcount() == 0 || tDto.getSjointcount() == null) {
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

	/**
	 * Direction: 根据凭证表查询凭证索引信息 ename: queryVoucherIndexInfo 引用方法: viewers: *
	 * messages:
	 */
	public List queryVoucherIndexInfo(IDto idto) {
		int count = 0;
		// 包含实拨资金和退库业务
		TvVoucherinfoDto queryDto = null;
		if (idto instanceof TvPayoutmsgmainDto) {
			TvPayoutmsgmainDto _dto = (TvPayoutmsgmainDto) idto;
			queryDto = new TvVoucherinfoDto();
			queryDto.setSdealno(_dto.getSbizno());
		} else if (idto instanceof TvDwbkDto) {
			TvDwbkDto _dto = (TvDwbkDto) idto;
			queryDto = new TvVoucherinfoDto();
			queryDto.setSdealno(_dto.getSbizno() + "");
		} else if (idto instanceof TfDirectpaymsgmainDto) {
			TfDirectpaymsgmainDto _dto = (TfDirectpaymsgmainDto) idto;
			queryDto = new TvVoucherinfoDto();
			queryDto.setSdealno(_dto.getIvousrlno() + "");
		}
		try {
			checklist = commonDataAccessService.findRsByDto(queryDto);
			return checklist;
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null,e);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null,e);
		}
		return null;
	}

	/**
	 * Direction: 查询记录 ename: queryRecords 引用方法: viewers: 查询结果视图 messages:
	 */
	public String queryRecords(Object o) {
		selectlist = new ArrayList();
		setBiztype(biztype);
		return "查询结果视图";
	}
	
	public List<Mapper> getMapOfDwbkR(){
		List<Mapper> list = new ArrayList<Mapper>();
		TsDwbkReasonDto idto = new TsDwbkReasonDto();
		idto.setSorgcode(loginfo.getSorgcode());
		try {
			List<TsDwbkReasonDto> dwlist = commonDataAccessService.findRsByDto(idto);
			if(dwlist!=null && dwlist.size()>0){
				for(TsDwbkReasonDto dto : dwlist){
					Mapper map = new Mapper(dto.getSdrawbackreacode(), dto.getSdrawbackreacmt());
					list.add(map);
				}
			}
		} catch (ITFEBizException e) {
			log.error(e);
		}
		return list;
		
	}
	
	/**
	 * Direction: 补复失败 ename: bufufault 引用方法: viewers: * messages:
	 */
	public String bufufault(Object o) {

		return super.bufufault(o);
	}
	
	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public TvPayoutmsgmainDto getPayoutdto() {
		return payoutdto;
	}

	public void setPayoutdto(TvPayoutmsgmainDto payoutdto) {
		this.payoutdto = payoutdto;
	}

	public TsPaybankDto getBankdto() {
		return bankdto;
	}

	public void setBankdto(TsPaybankDto bankdto) {
		this.bankdto = bankdto;
	}

	public void setBanklist(List<TsPaybankDto> banklist) {
		this.banklist = banklist;
	}

	public List<TsPaybankDto> getBanklist() {
		return banklist;
	}

	public List getDirectpayoutlist() {
		return directpayoutlist;
	}

	public void setDirectpayoutlist(List directpayoutlist) {
		this.directpayoutlist = directpayoutlist;
	}

	public List getDirectpayoutbacklist() {
		return directpayoutbacklist;
	}

	public void setDirectpayoutbacklist(List directpayoutbacklist) {
		this.directpayoutbacklist = directpayoutbacklist;
	}

	public List getDirectplanlist() {
		return directplanlist;
	}

	public void setDirectplanlist(List directplanlist) {
		this.directplanlist = directplanlist;
	}

	public String getDacceptdate() {
		return dacceptdate;
	}

	public void setDacceptdate(String dacceptdate) {
		this.dacceptdate = dacceptdate;
	}

	public String getSifmatch() {
		return sifmatch;
	}

	public void setSifmatch(String sifmatch) {
		this.sifmatch = sifmatch;
	}

	public String getScheckstatus() {
		return scheckstatus;
	}

	public void setScheckstatus(String scheckstatus) {
		this.scheckstatus = scheckstatus;
	}

	public List getPagingList() {
		return pagingList;
	}

	public void setPagingList(List pagingList) {
		this.pagingList = pagingList;
	}

	public PagingContext getTablePagingContext() {
		return tablePagingContext;
	}

	public void setTablePagingContext(PagingContext tablePagingContext) {
		this.tablePagingContext = tablePagingContext;
	}

	public List getChecklist() {
		return checklist;
	}

	public void setChecklist(List checklist) {
		this.checklist = checklist;
	}

	public List getCheckBizList() {
		return checkBizList;
	}

	public void setCheckBizList(List checkBizList) {
		this.checkBizList = checkBizList;
	}

	public List getDwbklist() {
		return dwbklist;
	}

	public void setDwbklist(List dwbklist) {
		this.dwbklist = dwbklist;
	}

	public TvDwbkDto getDwbkdto() {
		return dwbkdto;
	}

	public void setDwbkdto(TvDwbkDto dwbkdto) {
		this.dwbkdto = dwbkdto;
	}

	public IDto getSelDto() {
		return selDto;
	}

	public void setSelDto(IDto selDto) {
		this.selDto = selDto;
	}

	public List getPagingList2() {
		return pagingList2;
	}

	public void setPagingList2(List pagingList2) {
		this.pagingList2 = pagingList2;
	}

	public PagingContext getTableDwbkPagingContext() {
		return tableDwbkPagingContext;
	}

	public void setTableDwbkPagingContext(PagingContext tableDwbkPagingContext) {
		this.tableDwbkPagingContext = tableDwbkPagingContext;
	}

	public List getDirectlist() {
		return directlist;
	}

	public void setDirectlist(List directlist) {
		this.directlist = directlist;
	}

	public TfDirectpaymsgmainDto getDirectdto() {
		return directdto;
	}

	public void setDirectdto(TfDirectpaymsgmainDto directdto) {
		this.directdto = directdto;
	}

	public PagingContext getTableDirectPagingContext() {
		return tableDirectPagingContext;
	}

	public void setTableDirectPagingContext(PagingContext tableDirectPagingContext) {
		this.tableDirectPagingContext = tableDirectPagingContext;
	}

	public List getPagingListDirect() {
		return pagingListDirect;
	}

	public void setPagingListDirect(List pagingListDirect) {
		this.pagingListDirect = pagingListDirect;
	}

	public void setStartfamt(String startfamt) {
		this.startfamt = startfamt;
	}

	public String getStartfamt() {
		return startfamt;
	}

	public void setEndfamt(String endfamt) {
		this.endfamt = endfamt;
	}

	public String getEndfamt() {
		return endfamt;
	}

	public void setAdmDivCode(String admDivCode) {
		this.admDivCode = admDivCode;
	}

	public String getAdmDivCode() {
		return admDivCode;
	}
	public String getStamp() {
		return stamp;
	}
	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public void setBreakMark(boolean breakMark) {
		this.breakMark = breakMark;
	}

	public boolean isBreakMark() {
		return breakMark;
	}
	public List<TsQueryAmtDto> getListMoney() {
		return listMoney;
	}

	public void setListMoney(List<TsQueryAmtDto> listMoney) {
		this.listMoney = listMoney;
	}

	public String getMoneyUnit() {
		return moneyUnit;
	}

	public void setMoneyUnit(String moneyUnit) {
		this.setSamtFlag("");
		this.setStartfamt("");
		this.setEndfamt("");
		this.moneyUnit = moneyUnit;
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public String getSamtFlag() {
		return samtFlag;
	}

	public void setSamtFlag(String samtFlag) {
		if (null != samtFlag && !"".equals(samtFlag)) {
			BigDecimal samt = amtMap.get(samtFlag).getFstatamt();
			BigDecimal eamt = amtMap.get(samtFlag).getFendamt();
			if (samt.compareTo(new BigDecimal(Math.pow(10, 8))) >= 0) {
				moneyUnit = StateConstant.MoneyUnit_YI;
				startfamt = samt.divide(new BigDecimal(Math.pow(10, 8)))
						.toString();
				endfamt = eamt.divide(new BigDecimal(Math.pow(10, 8)))
						.toString();
			} else if (samt.compareTo(new BigDecimal(Math.pow(10, 7))) >= 0) {
				moneyUnit = StateConstant.MoneyUnit_QW;
				startfamt = samt.divide(new BigDecimal(Math.pow(10, 7)))
						.toString();
				endfamt = eamt.divide(new BigDecimal(Math.pow(10, 7)))
						.toString();
			} else if (samt.compareTo(new BigDecimal(Math.pow(10, 6))) >= 0) {
				moneyUnit = StateConstant.MoneyUnit_BW;
				startfamt = samt.divide(new BigDecimal(Math.pow(10, 6)))
						.toString();
				endfamt = eamt.divide(new BigDecimal(Math.pow(10, 6)))
						.toString();
			} else if (samt.compareTo(new BigDecimal(Math.pow(10, 5))) >= 0) {
				moneyUnit = StateConstant.MoneyUnit_WAN;
				startfamt = samt.divide(new BigDecimal(Math.pow(10, 4)))
						.toString();
				endfamt = eamt.divide(new BigDecimal(Math.pow(10, 4)))
						.toString();
			} else {
				moneyUnit = StateConstant.MoneyUnit_YUAN;
				startfamt = samt.toString();
				endfamt = eamt.toString();
			}
			this.samtFlag = samtFlag;

			
		}else{
			this.samtFlag = samtFlag;
		}
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	/**
	 * 获取被乘数
	 * 
	 * @param moneyUnit
	 * @return
	 */
	private BigDecimal getMultiply(String moneyUnit) {
		BigDecimal multiplicand = new BigDecimal(1);// 被乘数
		if (StateConstant.MoneyUnit_WAN.equals(moneyUnit)) {
			multiplicand = new BigDecimal(Math.pow(10, 4));
		} else if (StateConstant.MoneyUnit_BW.equals(moneyUnit)) {
			multiplicand = new BigDecimal(Math.pow(10, 6));
		} else if (StateConstant.MoneyUnit_QW.equals(moneyUnit)) {
			multiplicand = new BigDecimal(Math.pow(10, 8));
		} else if (StateConstant.MoneyUnit_YI.equals(moneyUnit)) {
			multiplicand = new BigDecimal(Math.pow(10, 9));
		}
		return multiplicand;
	}

	/**
	 * 获取查询金额的扩展SQL
	 * 
	 * @param startfamt
	 * @param ndfamt
	 * @return
	 */
	private String getExtentSQl(String startfamt, String endfamt,
			String moneyUnit) {
		StringBuffer moneyExtent = new StringBuffer();
		BigDecimal multiplicand = getMultiply(moneyUnit);// 被乘数
		// 查询金额范围
		BigDecimal statFamt = new BigDecimal(0);
		BigDecimal endFamt = new BigDecimal(0);
		if (null != startfamt && startfamt.trim().length() > 0) {
			statFamt = (new BigDecimal(startfamt.trim())).multiply(multiplicand);
		}
		if (null != endfamt && endfamt.trim().length() > 0) {
			endFamt = (new BigDecimal(endfamt.trim())).multiply(multiplicand);
		}
		if ( !StringUtils.isBlank(startfamt)&& !StringUtils.isBlank(endfamt)&& statFamt.compareTo(endFamt) > 0) {
			MessageDialog.openMessageDialog(null, "查询金额下限应该小于查询金额上限范围   !!!");
			return null;
		}
		//默认金额字段是 N_MONEY
		String money = "N_MONEY";
		if (biztype.equalsIgnoreCase(BizTypeConstant.VOR_TYPE_DWBK)) {
			money ="F_AMT";
		}else if (biztype.equalsIgnoreCase(BizTypeConstant.VOR_TYPE_DIRECTPAY)) {
			money ="N_PAYAMT";
		}
		
		if (StringUtils.isBlank(startfamt)&& !StringUtils.isBlank(endfamt)) { //a==null b!=null
			moneyExtent.append(" AND (").append(money).append(" <= ").append(endFamt).append(" )");
			return moneyExtent.toString();
		}else if (!StringUtils.isBlank(startfamt)&& StringUtils.isBlank(endfamt)) {//a!=null b==null
			moneyExtent.append(" AND (").append(money).append(" >= ").append(statFamt).append(" )");
			return moneyExtent.toString();
		}else if (statFamt.compareTo(new BigDecimal(0)) > 0 || endFamt.compareTo(new BigDecimal(0)) > 0) {//a!=null b!=null
			moneyExtent.append(" AND (").append(money).append("  BETWEEN  ").append(statFamt).append(" AND ").append(endFamt).append(")");
			return moneyExtent.toString();
		} else {//a==null b==null
			return "";
		}

	}

	public HashMap<String, String> getAdmDivCodeMap() {
		return admDivCodeMap;
	}

	public void setAdmDivCodeMap(HashMap<String, String> admDivCodeMap) {
		this.admDivCodeMap = admDivCodeMap;
	}

	public void setSingStampflag(boolean singStampflag) {
		this.singStampflag = singStampflag;
	}

	public boolean isSingStampflag() {
		return singStampflag;
	}
	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
	}
	
	public TsUsersDto getUDto() {
		return uDto;
	}

	public void setUDto(TsUsersDto dto) {
		uDto = dto;
	}

}
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
 * @time 13-05-22 13:07:12 ��ϵͳ: RecBiz ģ��:bankNameEnter ���:BankNameEnter
 */
public class BankNameEnterBean extends AbstractBankNameEnterBean implements
		IPageDataProvider {


	private static Log log = LogFactory.getLog(BankNameEnterBean.class);
	private List<TdEnumvalueDto> biztypelist;// ҵ�������б�
	private List<TsPaybankDto> banklist;// ���д����б�
	private List querypayoutlist; // ʵ���ʽ���Ϣ�б�
	private List dwbklist;// �����˸�
	private List directlist;// ֱ��֧����Ϣ�б�
	private List directpayoutlist; // ֱ��֧��������Ϣ�б�
	private List directpayoutbacklist; // ֱ��֧�������˻���Ϣ�б�
	private List directplanlist;// ֱ�Ӷ����Ϣ�б�
	private String biztype; // ҵ������
	private String dacceptdate;// ��������
	private String sifmatch;// �Ƿ�¼
	private String scheckstatus;// ��¼����״̬
	private String bankname; // ��������
	private String operatype;// �������� 0-��¼��1-����
	
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
	private String stamp; //ǩ������
	private boolean breakMark;
	private boolean singStampflag; //�趨�Ƿ���Ҫ����˸���ʱ����ǩ��,Ĭ�ϲ�ǩ��
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
		value1.setSvaluecmt("ʵ���ʽ�5207");
		biztypelist.add(value1);
		if(loginfo.getPublicparam().indexOf(",collectpayment=1,")>=0)
		{
			TdEnumvalueDto value5267 = new TdEnumvalueDto();
			value5267.setStypecode(MsgConstant.VOUCHER_NO_5267);
			value5267.setSvaluecmt("ʵ���ʽ�ר��5267");
			biztypelist.add(value5267);
		}
		TdEnumvalueDto value2 = new TdEnumvalueDto();
		value2.setStypecode(BizTypeConstant.VOR_TYPE_DWBK);
		value2.setSvaluecmt("�����˸�5209");
		biztypelist.add(value2);
		if(loginfo.getPublicparam().indexOf(",sh,")>=0){
			TdEnumvalueDto value3 = new TdEnumvalueDto();
			value3.setStypecode(MsgConstant.VOUCHER_NO_5201);
			value3.setSvaluecmt("ֱ��֧��5201");
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
			log.error("��ѯ���������쳣!!!");
			MessageDialog.openErrorDialog(null, e);
		}
		
	}

	/**
	 * Direction: ������Ϣ�б� ename: singleSelect ���÷���: viewers: * messages:
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
			log.error("������Ϣ�б����", e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.singleSelect(o);
	}

	/**
	 * Direction: ˫�������б� ename: doubleclick ���÷���: viewers: * messages:
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
				msg = "����������" + payoutdto.getSrecbankname() + "����Ӧ֧��ϵͳ������"
						+ bankdto.getSbankname() + "��,�Ƿ�ȷ�ϲ�¼?";
			} else {
				msg = "����������" + payoutdto.getSrecbankname() + "����Ӧ֧��ϵͳ������"
						+ bankdto.getSbankname() + "��,�Ƿ�ȷ�ϲ���?";
			}
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
					null, "��ʾ", msg);
			if (flag) {
				try {
					if(bankdto.getSbankno()==null||"".equals(bankdto.getSbankno())||bankdto.getSbankname()==null||"".equals(bankdto.getSbankname()))
					{
						MessageDialog.openMessageDialog(null, "�տ�����кŻ���������Ϊ�գ�");
						return null;
					}
					// ��¼
					payoutdto.setSrecbankno(bankdto.getSbankno());// �տ�����к�
					payoutdto.setSinputrecbankname(bankdto.getSbankname());// ��¼���տ��������
					payoutdto.setSinputusercode(loginfo.getSuserCode());// ��¼��
					payoutdto.setScheckstatus(StateConstant.CHECKSTATUS_1);// �Ѳ�¼
					// �����к�
					commonDataAccessService.updateData(payoutdto);
					MessageDialog.openMessageDialog(null, "��¼�ɹ�");
					editor.fireModelChanged();
					return super.doubleclickBank(o);
				} catch (ITFEBizException e) {
					log.error(e);
					MessageDialog.openMessageDialog(null, "��¼ʧ��");
				}
			} else {
				return null;
			}
		} else if (BizTypeConstant.VOR_TYPE_DWBK.equals(biztype)) {
			if (dwbkdto.getSbookorgcode() == null) {
				return null;
			}
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
					null, "��ʾ", "����������" + dwbkdto.getSrecbankname()
							+ "����Ӧ֧��ϵͳ������" + bankdto.getSbankname()
							+ "��,�Ƿ�ȷ�ϲ�¼?");
			if (flag) {
				try {
					// �����к�
					dwbkdto.setSpayeeopnbnkno(bankdto.getSbankno());// �տ�����к�
					dwbkdto.setSinputrecbankname(bankdto.getSbankname());// ��¼���տ��������
					dwbkdto.setSinputusercode(loginfo.getSuserCode());// ��¼��
					dwbkdto.setScheckstatus(StateConstant.CHECKSTATUS_1);// �Ѳ�¼
					commonDataAccessService.updateData(dwbkdto);
					editor.fireModelChanged();
					return super.doubleclickBank(o);
				} catch (ITFEBizException e) {
					log.error("�����кŴ���", e);
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
					null, "��ʾ", "����������" + directdto.getSinputrecbankname()
							+ "����Ӧ֧��ϵͳ������" + bankdto.getSbankname()
							+ "��,�Ƿ�ȷ�ϲ�¼?");
			if (flag) {
				try {
					// �����к�
					directdto.setSpayeeacctbankno(bankdto.getSbankno());//�տ��������к�
					directdto.setSinputrecbankname(bankdto.getSbankname());//֧��ϵͳ����
					directdto.setSinputusercode(loginfo.getSuserCode());// ��¼��
					directdto.setScheckstatus(StateConstant.CHECKSTATUS_1);//�Ѳ�¼
					commonDataAccessService.updateData(directdto);
					editor.fireModelChanged();
					return super.doubleclickBank(o);
				} catch (ITFEBizException e) {
					log.error("�����кŴ���", e);
					MessageDialog.openErrorDialog(null, e);
				}
			} else {
				return null;
			}
		}
		return super.doubleclickBank(o);
	}

	/**
	 * Direction: ˫����¼ ename: doubleclickTObulu ���÷���: viewers: * messages:
	 */
	public String doubleclickTObulu(Object o) {
		try {
			if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
				payoutdto = (TvPayoutmsgmainDto) o;
				if (!payoutdto.getSifmatch().equals(
						StateConstant.IF_MATCHBNKNAME_YES)) {
					MessageDialog.openMessageDialog(null, "����Ҫ��¼�кţ�����ˡ����ˣ�");
					return "";
				} else {
					if (payoutdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_4)) {
						MessageDialog.openMessageDialog(null,
								"֧��ϵͳ�к��Ѹ��ˣ���ѡ������������");
						return "";
					}
					if (payoutdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_1)) {
						MessageDialog.openMessageDialog(null, "֧��ϵͳ�к��Ѳ�¼������ˣ�");
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
					MessageDialog.openMessageDialog(null, "֧��ϵͳ�к��Ѳ�¼����ѡ������������");
					return "";
				}
				if (dwbkdto.getScheckstatus().equals(
						StateConstant.CHECKSTATUS_4)) {
					MessageDialog.openMessageDialog(null, "֧��ϵͳ�к��Ѹ��ˣ���ѡ������������");
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
					MessageDialog.openMessageDialog(null, "����Ҫ��¼�кţ�����ˡ����ˣ�");
					return "";
				} else {
					if (directdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_4)) {
						MessageDialog.openMessageDialog(null,
								"֧��ϵͳ�к��Ѹ��ˣ���ѡ������������");
						return "";
					}
					if (directdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_1)) {
						MessageDialog.openMessageDialog(null, "֧��ϵͳ�к��Ѳ�¼������ˣ�");
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
	 * Direction: ��ѯ������Ϣ ename: querybank ���÷���: viewers: * messages:
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
	 * Direction: �˳� ename: exit ���÷���: viewers: * messages:
	 */
	public String exit(Object o) {
		selectlist = new ArrayList();
		// setBiztype(biztype);
		editor.openComposite("�貹¼֧��ϵͳ�к���Ϣ��ѯ��ͼ");
		return null;

	}

	/**
	 * Direction: ȫѡ ename: selectAll ���÷���: viewers: * messages:
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
			// �Ƿ�¼
			if (sifmatch != null && !sifmatch.equals("")) {
				dto.setSifmatch(sifmatch);
			}
			// ��¼����״̬
			if (scheckstatus != null && !scheckstatus.equals("")) {
				dto.setScheckstatus(scheckstatus);
			}
			//�������
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
			// �Ƿ�¼
			if (sifmatch != null && !sifmatch.equals("")) {
				dto.setSifmatch(sifmatch);
			}
			// ��¼����״̬
			if (scheckstatus != null && !scheckstatus.equals("")) {
				dto.setScheckstatus(scheckstatus);
			}
			//�������
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
			// �Ƿ�¼
			if (sifmatch != null && !sifmatch.equals("")) {
				dto.setSifmatch(sifmatch);
			}
			// ��¼����״̬
			if (scheckstatus != null && !scheckstatus.equals("")) {
				dto.setScheckstatus(scheckstatus);
			}
			//�������
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
	 * Direction: ��ѯ ename: search ���÷���: viewers: * messages:
	 */
	public String search(Object o) {
		selectlist = new ArrayList();
		if (StringUtils.isBlank(biztype)) {
			MessageDialog.openMessageDialog(null, "ҵ�����Ͳ���Ϊ�գ�");
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
				MessageDialog.openMessageDialog(null, "û�в�ѯ�����ݣ�");
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
				MessageDialog.openMessageDialog(null, "û�в�ѯ�����ݣ�");
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
				MessageDialog.openMessageDialog(null, "û�в�ѯ�����ݣ�");
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
	 * ����ҵ�����Ͳ�ѯ�貹¼�����˵������б�
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
			Exception e1 = new Exception("��ȡƾ֤ǩ���б�����쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}

		return maplist;
	}

	public void setBiztype(String biztype) {
		this.biztype = biztype;
		editor = MVCUtils.getEditor("c5dc725e-2b29-42bf-9e9f-77f1b06143a4");
		List<String> disvisContentAreaName = new ArrayList<String>();// ���ɼ�area
		List<String> visContentAreaName = new ArrayList<String>();// �ɼ�area
		if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
//			queryTablelist(biztype);
//			isVisiable(true);
			
			// ������ʾ�б�
			visContentAreaName.add("ʵ���ʽ���Ϣ�б�");
			disvisContentAreaName.add("�����˸�");
			disvisContentAreaName.add("ֱ��֧��");
		} else if (BizTypeConstant.VOR_TYPE_DWBK.equals(biztype)) {
//			queryTablelist(biztype);
//			isVisiable(false);
			// ������ʾ�б�
			visContentAreaName.add("�����˸�");
			disvisContentAreaName.add("ʵ���ʽ���Ϣ�б�");
			disvisContentAreaName.add("ֱ��֧��");
		} else if (MsgConstant.VOUCHER_NO_5201.equals(biztype)) {
//			queryTablelist(biztype);
//			isVisiable(false);
			// ������ʾ�б�
			visContentAreaName.add("ֱ��֧��");
			disvisContentAreaName.add("ʵ���ʽ���Ϣ�б�");
			disvisContentAreaName.add("�����˸�");
		}
		MVCUtils.setContentAreasToVisible(editor, visContentAreaName);
		MVCUtils.setContentAreaVisible(editor, disvisContentAreaName, false);
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged();
	}
	/**
	 * �Ƿ���ʾ��Χ�ؼ� true ��ʾfalse ����ʾ
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
	 * ��ȡOCX�ؼ�url
	 * 
	 * @return
	 */
	public String getOcxVoucherServerURL() {
		String ls_URL = "";
		try {
			ls_URL = voucherLoadService.getOCXServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception("��ȡOCX�ؼ�URL��ַ���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return ls_URL;
	}
	
    /**
     * ��ȡǩ�·����ַ
     * @return
     */
    public String getOCXStampServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("��ȡOCX�ؼ�ǩ�·���URL��ַ���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }

	/**
	 * Direction: ��¼ ename: bulu ���÷���: viewers: ֧��ϵͳ�кŲ�¼��ͼ messages:
	 */
	public String addRecord(Object o) {
		if (null == selectlist||selectlist.size()==0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ��¼Ҫ�ص����ݼ�¼��");
			return null;
		}else if (selectlist.size() > 1) {
			MessageDialog.openMessageDialog(null, "ÿ��ֻ�ܲ�¼һ�����ݣ���ѡ��Ҫ��¼�����ݣ�");
			return null;
		}else{
			selDto = (IDto) selectlist.get(0);
		}
		try {
			if (BizTypeConstant.VOR_TYPE_PAYOUT.equals(biztype)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
				payoutdto = (TvPayoutmsgmainDto) selDto;
				if (!payoutdto.getSifmatch().equals(
						StateConstant.IF_MATCHBNKNAME_YES)) {
					MessageDialog.openMessageDialog(null, "����Ҫ��¼�кţ�����ˡ����ˣ�");
					return "";
				} else {
					if (payoutdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_4)) {
						MessageDialog.openMessageDialog(null,
								"֧��ϵͳ�к��Ѹ��ˣ���ѡ������������");
						return "";
					}
					if (payoutdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_1)) {
						MessageDialog.openMessageDialog(null, "֧��ϵͳ�к��Ѳ�¼������ˣ�");
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
					MessageDialog.openMessageDialog(null, "֧��ϵͳ�к��Ѳ�¼����ѡ������������");
					return "";
				}
				if (dwbkdto.getScheckstatus().equals(
						StateConstant.CHECKSTATUS_4)) {
					MessageDialog.openMessageDialog(null, "֧��ϵͳ�к��Ѹ��ˣ���ѡ������������");
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
					MessageDialog.openMessageDialog(null, "����Ҫ��¼�кţ�����ˡ����ˣ�");
					return "";
				} else {
					if (directdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_4)) {
						MessageDialog.openMessageDialog(null,
								"֧��ϵͳ�к��Ѹ��ˣ���ѡ������������");
						return "";
					}
					if (directdto.getScheckstatus().equals(
							StateConstant.CHECKSTATUS_1)) {
						MessageDialog.openMessageDialog(null, "֧��ϵͳ�к��Ѳ�¼������ˣ�");
						return "";
					}
				}
				directdto = (TfDirectpaymsgmainDto) BuLUBankNoDialogFacade
						.openDialogProc(directdto, banklist, null, biztype,
								commonDataAccessService, null);
			}
		} catch (ITFEBizException e) {
			log.error("��¼֧��ϵͳ�к���Ϣʱ����!", e);
			MessageDialog.openErrorDialog(null, e);
		}
		editor.fireModelChanged();
		return super.addRecord(o);
	}

	/**
	 * Direction: �����ɹ� ename: bufusuccess ���÷���: viewers: ֧��ϵͳ�кŲ�����ͼ messages:
	 */
	public String bufusuccess(Object o) {
		
		// У��ֻ�ܸ�����˳ɹ�������
		if (selectlist != null && selectlist.size() > 0) {
			int bufsuccess = 0 ;
			if (singStampflag) {
				
					//�ж��û��Ƿ��Ǹ�����Ա������ǩ�����µ�Ȩ��
					stamp=null; //��ʼ��˽��
					TsUsersPK userpk = new TsUsersPK();
					userpk.setSorgcode(loginfo.getSorgcode());
					userpk.setSusercode(loginfo.getSuserCode());
					try {
						uDto=(TsUsersDto) commonDataAccessService.find(userpk);
					} catch (ITFEBizException e2) {
						MessageDialog.openMessageDialog(null, "��ѯ�쳣: "+e2.getMessage());
						e2.printStackTrace();
						return "";
					}
					if (StringUtils.isBlank(uDto.getSstamppermission())) {
						MessageDialog.openMessageDialog(null, "��ǰ�û���ǩ����Ȩ�ޣ���ѡ�������û����и��� ��");
						return "";
					}
			}
			StringBuffer error = new StringBuffer("");
			TsConvertbanknameDto convertbankdto = new TsConvertbanknameDto();
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "��ʾ", "�Ƿ�Ҫ��ѡ�����ݸ��˳ɹ���");
			if (flag) {
				if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
					for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
						if (!singStampflag) {
							if (!(paydto.getScheckstatus().equals(
									StateConstant.CHECKSTATUS_3)||(paydto.getSifmatch().equals(
											StateConstant.IF_MATCHBNKNAME_NO)&&!paydto.getScheckstatus().equals(
													StateConstant.CHECKSTATUS_4)))) {
								MessageDialog.openMessageDialog(null,"ֻ�ܸ�����˳ɹ�����Ҫ��¼�����ݣ�");
								if(paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4))
								{
									try {
										TvVoucherinfoPK pk = new TvVoucherinfoPK();
										pk.setSdealno(paydto.getSbizno());
										TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
										if(DealCodeConstants.VOUCHER_CHECKSUCCESS.equals(vDto.getSstatus()))
										{
											vDto.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
											vDto.setSdemo("���˳ɹ�");
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
							MessageDialog.openMessageDialog(null,"ֻ�ܸ�����˳ɹ������ݣ�");
							return "";
						}
						if (paydto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)&&paydto.getSinputusercode().equals(loginfo.getSuserCode())) {
							MessageDialog.openMessageDialog(null,"��¼�����˲�����ͬһ�û���");
							return "";
						}
						// �����к�
						try {
							paydto.setScheckstatus(StateConstant.CHECKSTATUS_4);// ����״̬Ϊ�Ѹ���
							paydto.setSchecksercode(loginfo.getSuserCode());// ������
							//���ݿ�������������н���{code}
							// ����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(paydto.getSbizno());
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							
							if(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_CHECKSUCCESS))
							{
								vDto.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
								vDto.setSdemo("���˳ɹ�");
							}
							if (paydto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)) {
								// ���������кŲ����������ձ�
								convertbankdto.setSorgcode(loginfo.getSorgcode());
								convertbankdto.setSbankname(paydto.getSrecbankname());
								convertbankdto.setStcbankname(paydto.getSinputrecbankname());
								convertbankdto.setSbankcode(paydto.getSrecbankno());
								bankNameEnterService.save(convertbankdto);
							}
								if (singStampflag) {
									TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//����ǩ����Ϣ
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
								MessageDialog.openMessageDialog(null,"ֻ�ܸ�����˳ɹ�����Ҫ��¼�����ݣ�");
								return "";
							}
						}else if(!(dwdkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_3))){
								MessageDialog.openMessageDialog(null,"ֻ�ܸ�����˳ɹ������ݣ�");
								return "";
							
						}
						if (dwdkdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)&&dwdkdto.getSinputusercode().equals(loginfo.getSuserCode())) {
							MessageDialog.openMessageDialog(null,
									"��¼�����˲�����ͬһ�û���");
							return "";
						}
						// �����к�
						try {
							dwdkdto.setScheckstatus(StateConstant.CHECKSTATUS_4);// ����״̬Ϊ�Ѹ���
							dwdkdto.setScheckusercode(loginfo.getSuserCode());// ������
							// ����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(dwdkdto.getSbizno());
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							vDto.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
							vDto.setSdemo("���˳ɹ�");
							if (dwdkdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)) {
								// ���������кŲ����������ձ�
								convertbankdto.setSorgcode(loginfo.getSorgcode());
								convertbankdto.setSbankname(dwdkdto.getSrecbankname());
								convertbankdto.setStcbankname(dwdkdto.getSinputrecbankname());
								convertbankdto.setSbankcode(dwdkdto.getSpayeeopnbnkno());
								bankNameEnterService.save(convertbankdto);
							}
							if (singStampflag) {
								TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//����ǩ����Ϣ
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
							MessageDialog.openMessageDialog(null, "�������ݳ��������¸��ˣ�");
						}
					}
				}else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
					for (TfDirectpaymsgmainDto directdto : (ArrayList<TfDirectpaymsgmainDto>) selectlist) {
						if (!singStampflag) {
							if (!(directdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_3)||
									(directdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_NO)
									&&!directdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)))) {
								MessageDialog.openMessageDialog(null,"ֻ�ܸ�����˳ɹ�����Ҫ��¼�����ݣ�");
								return "";
							}
						}else if(!(directdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_3))){
							MessageDialog.openMessageDialog(null,"ֻ�ܸ�����˳ɹ������ݣ�");
							return "";
						}
						if (directdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)&&directdto.getSinputusercode().equals(loginfo.getSuserCode())) {
							MessageDialog.openMessageDialog(null,"��¼�����˲�����ͬһ�û���");
							return "";
						}
						// �����к�
						try {
							directdto.setScheckstatus(StateConstant.CHECKSTATUS_4);// ����״̬Ϊ�Ѹ���
							directdto.setSchecksercode(loginfo.getSuserCode());// ������
							// ����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(directdto.getIvousrlno()+"");
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							vDto.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
							vDto.setSdemo("���˳ɹ�");
							if (directdto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_YES)) {
								// ���������кŲ����������ձ�
								convertbankdto.setSorgcode(loginfo.getSorgcode());
								convertbankdto.setSbankname(directdto.getSinputrecbankname());
								convertbankdto.setStcbankname(directdto.getSinputrecbankname());
								convertbankdto.setSbankcode(directdto.getSpayeeacctbankno());
								bankNameEnterService.save(convertbankdto);
							}
							if (singStampflag) {
								TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//����ǩ����Ϣ
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
					MessageDialog.openMessageDialog(null, "���˽�����ɹ�[ "+bufsuccess+" ]�ʣ�ʧ��[ "+(selectlist.size()-bufsuccess)+" ]��!!!"+error.toString());
				}else
				{
					MessageDialog.openMessageDialog(null, "���˽�����ɹ�[ "+bufsuccess+" ]�ʣ�ʧ��[ "+(selectlist.size()-bufsuccess)+" ]��!!!");
				}
			}
			editor.fireModelChanged();
		} else {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���˵����ݣ�");
		}
		return super.bufusuccess(o);
	}

	/**
	 * ǩ�� 
	 * 
	 */
	@Override
	public String singStamp(Object o) {
		return super.singStamp(o);
	}

	 /**
     * ˢ��ǩ������
     * ��ȡ�û�Ψһ��˽��
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
      		//��ȡ�û���ǩ��Ȩ��
          	tList= commonDataAccessService.findRsByDto(tsDto);
          	if(tList.size()>0){
          		//�д����¡�ת���¡������µ���Ϣ
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
          			MessageDialog.openMessageDialog(null, "���û��޴�����˽����Ϣ�������û�ǩ��Ȩ����Ϣ��ƾ֤ǩ��λ����Ϣ��");
              		return null;
				}

          	}else {
          		MessageDialog.openMessageDialog(null, "�޸�����ǩ��Ȩ�ޣ����ȡǩ��λ����Ϣ��");
          		return null;
			}      		
      	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
      		log.error(e);
      		Exception e1=new Exception("ƾ֤ˢ�²��������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);			   			
      	}
		return returnDto;
    }
	/**
	 * �ж�ӡ�¶�Ӧ��֤���Ƿ��Ѿ�ά��
	 * @return
	 */
	public Boolean checkStampIDAndCertID(TsTreasuryDto tDto,TsUsersDto uDto,String strecode,TsStamppositionDto stampDto){
		/*
		 * У�鵱ǰ�û�˽��֤��ID��ӡ��ID�����Ƿ���ά��
		 */
			if(uDto.getScertid()==null||uDto.getScertid().equals("")){
				MessageDialog.openMessageDialog(null, "��ǰ�û�  "+stampDto.getSstampname()+"  ֤��ID����δά���� ");        				
				return Boolean.FALSE;
			}else if(uDto.getSstampid()==null||uDto.getSstampid().equals("")){
				MessageDialog.openMessageDialog(null, "��ǰ�û�   "+stampDto.getSstampname()+"  ӡ��ID����δά���� ");        				
				return Boolean.FALSE;
			}    		    			
		return Boolean.TRUE;
	}
	/**
	 * ���ݲ�ͬ��ӡ�����ͽ��и��£��ڸ��¹��������ֿͻ��˸��ºͷ������˸���
	 * 
	 * @throws ITFEBizException 
	 */
	private void SignStamp(TvVoucherinfoDto vDto,String seq,List sList,TsTreasuryDto tDto,List sinList,TsStamppositionDto sDto,TsUsersDto uDto) throws ITFEBizException{
		//�ͻ���ǩ˽��ӡ��
		if(!loginfo.getPublicparam().contains(",jbrstamp=server,"))
		{
			sinList.add(ShowCompositeVoucherOcx.getVoucherStamp(vDto, uDto.getScertid(), sDto.getSstampposition(),uDto.getSstampid()));
		}
	}

	/**
	 * У��ǩ��˳�򣬰���ǩ��˳��ǩ��
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
				MessageDialog.openMessageDialog(null, "������룺"+vDto.getStrecode()+" ƾ֤����: "+vDto.getSvtcode()+vDto.getSvoucherno()+" \""+stampDto.getSstampname()+"\"ǩ��ǰ���� \""+err+"\"ǩ�£�");
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
					MessageDialog.openMessageDialog(null, "������룺"+vDto.getStrecode()+" ƾ֤����: "+vDto.getSvtcode()+" \""+stampDto.getSstampname()+"\"ǩ��ǰ���� \""+err+"\"ǩ�£�");
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
	 * Direction: ���ͨ�� ename: checkSuccess ���÷���: viewers: * messages:
	 */
	public String checkSuccess(Object o) {
		
		if (selectlist != null && selectlist.size() > 0) {
			int cksuccess=0;
			if (singStampflag) {
				//�ж��û��Ƿ��������Ա������˽�µ�Ȩ��
				stamp=null;//��ʼ��˽��
				TsUsersPK userpk = new TsUsersPK();
				userpk.setSorgcode(loginfo.getSorgcode());
				userpk.setSusercode(loginfo.getSuserCode());
				try {
					uDto=(TsUsersDto) commonDataAccessService.find(userpk);
				} catch (ITFEBizException e2) {
					MessageDialog.openMessageDialog(null, "��ѯ�쳣: "+e2.getMessage());
					e2.printStackTrace();
					return "";
				}
				if (StringUtils.isBlank(uDto.getSstamppermission())) {
					MessageDialog.openMessageDialog(null, "��ǰ�û���ǩ˽��Ȩ�ޣ���ѡ�������û�������� ��");
					return "";
				}
			}
			StringBuffer error = new StringBuffer("");
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
					null, "��ʾ", "�Ƿ�Ҫ��ѡ��������˳ɹ���");
			if (flag) {
				if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
					for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
						if (!singStampflag) {
							if(paydto.getSifmatch().equals(StateConstant.IF_MATCHBNKNAME_NO)){
								MessageDialog.openMessageDialog(null,
								"�����Ѵ�֧���кŵ�ʵ����Ϣֻ�踴�ˣ�");
								return "";
							}
						}
						if (!(paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_1)||paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_2))) {
							MessageDialog.openMessageDialog(null,
									"ֻ������Ѳ�¼�����ʧ�ܵ����ݣ�������ѡ�����ݣ�");
							return "";
						}else if(paydto.getSinputrecbankno()==null||"".equals(paydto.getSinputrecbankno()))
						{
							MessageDialog.openMessageDialog(null,"��¼���к�"+paydto.getSinputrecbankno()+"���������ʧ�����²�¼�кţ�");
							return "";
						}
						//������ֻ��У��ɹ����ұȶԳɹ������ݲſ�ִ�����ͨ������ -- start
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
									
									if(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(stus.getSstatus()) && !"���ݱȶԳɹ�".equals(stus.getSdemo()))
									{
										String msg = "����������δ�ȶԳɹ����Ƿ����";
						    	    	boolean isContinue = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "��ʾ", msg);
						    	    	if (isContinue == false)
						    	    	{
						    				return null;
						    			}else
						    			{
						    				if(AdminConfirmDialogFacade.open("��Ҫ������Ȩ���ܼ���ִ�в���"))
						    	    		{
						    					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						    				}
						    			}
									}
								}else 
								{
									MessageDialog.openMessageDialog(null, "��ѯƾ֤�����������ݣ�");
				        			return "";
								}
							} catch (ITFEBizException e) 
							{
								log.error("��ѯƾ֤�������쳣��"+e);
								MessageDialog.openMessageDialog(null, "��ѯƾ֤�������쳣��");
			        			return "";
							}
						}
						//������ֻ��У��ɹ����ұȶԳɹ������ݲſ�ִ�����ͨ������ -- end

						// ����״̬
						try {
						
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(paydto.getSbizno());
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							
							//ǩ�¿�ʼ
								if (singStampflag) {
									TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//����ǩ����Ϣ
									List lists = validStampfactor(uDto, vDto, stampDto);
									if (null==lists) {
										return "";
									}
						    		Integer count = voucherLoadService.voucherStamp(lists);
						    		
					    		//ǩ�½���
						    		if (count==1) {
						    			vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
						    			paydto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
										paydto.setSinputusercode(loginfo.getSuserCode());
										commonDataAccessService.updateData(paydto);
										// ����ƾ֤����Ϣ
										vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
										vDto.setSdemo("��˳ɹ�");
										//ͬ������ƾ֤�����Ϣ
										commonDataAccessService.updateData(vDto);
										++cksuccess;
									}else
									{
										vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
										error.append(vDto.getSvoucherno()+":"+vDto.getSreturnerrmsg());
						    			paydto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
										paydto.setSinputusercode(loginfo.getSuserCode());
										commonDataAccessService.updateData(paydto);
										// ����ƾ֤����Ϣ
										vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
										vDto.setSdemo("��˳ɹ�");
										//ͬ������ƾ֤�����Ϣ
										commonDataAccessService.updateData(vDto);
										++cksuccess;
									}
								}else{
									paydto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
									paydto.setSinputusercode(loginfo.getSuserCode());
									commonDataAccessService.updateData(paydto);
									// ����ƾ֤����Ϣ
									vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
									vDto.setSdemo("��˳ɹ�");
									//ͬ������ƾ֤�����Ϣ
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
								"�����Ѵ�֧���кŵ������˸���Ϣֻ�踴�ˣ�");
								return "";
							}
						}
						if (!(dwbkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_1)||dwbkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_2))) {
							MessageDialog.openMessageDialog(null,
									"ֻ������Ѳ�¼�����ʧ�ܵ����ݣ�������ѡ�����ݣ�");
							return "";
						}if(dwbkdto.getSinputrecbankno()==null||"".equals(dwbkdto.getSinputrecbankno()))
						{
							MessageDialog.openMessageDialog(null,"��¼���к�"+dwbkdto.getSinputrecbankno()+"���������ʧ�����²�¼�кţ�");
							return "";
						}
						// ����״̬
						try {
							//����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(dwbkdto.getSbizno());
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							
							if (singStampflag) { //ǩ�¿�ʼ
								TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//����ǩ����Ϣ
								List lists = validStampfactor(uDto, vDto, stampDto);
								if (null==lists) {
									return "";
								}
					    		Integer count = voucherLoadService.voucherStamp(lists);
								//ǩ�½���
					    		if (count==1) {
						    		vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
									dwbkdto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
									dwbkdto.setSinputusercode(loginfo.getSuserCode());
									commonDataAccessService.updateData(dwbkdto);
									vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
									vDto.setSdemo("��˳ɹ�");
									commonDataAccessService.updateData(vDto);
									++cksuccess;
								}else
								{
									vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
									error.append(vDto.getSvoucherno()+":"+vDto.getSreturnerrmsg());
									dwbkdto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
									dwbkdto.setSinputusercode(loginfo.getSuserCode());
									commonDataAccessService.updateData(dwbkdto);
									vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
									vDto.setSdemo("��˳ɹ�");
									commonDataAccessService.updateData(vDto);
									++cksuccess;
								}

							}else{
								dwbkdto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
								dwbkdto.setSinputusercode(loginfo.getSuserCode());
								commonDataAccessService.updateData(dwbkdto);
								vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
								vDto.setSdemo("��˳ɹ�");
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
								"�����Ѵ�֧���кŵ�ֱ��֧����Ϣֻ�踴�ˣ�");
								return "";
							}
						}
						if (!(directdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_1)||directdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_2))) {
							MessageDialog.openMessageDialog(null,
									"ֻ������Ѳ�¼�����ʧ�ܵ����ݣ�������ѡ�����ݣ�");
							return "";
						}if(directdto.getSpayeeacctbankno()==null||"".equals(directdto.getSpayeeacctbankno()))
						{
							MessageDialog.openMessageDialog(null,"��¼���к�"+directdto.getSpayeeacctbankno()+"���������ʧ�����²�¼�кţ�");
							return "";
						}
						// ����״̬
						try {
							// ����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(directdto.getIvousrlno()+"");
							TvVoucherinfoDto vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							if (singStampflag) {
								TsStamppositionDto stampDto= refreshStampType(vDto, uDto);//����ǩ����Ϣ
								List lists = validStampfactor(uDto, vDto, stampDto);
								if (null==lists) {
									return "";
								}
					    		//ƾ֤ǩ��
					    		Integer count = voucherLoadService.voucherStamp(lists);
					    		if (count==1) {
					    			vDto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
									directdto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
									directdto.setSinputusercode(loginfo.getSuserCode());
									commonDataAccessService.updateData(directdto);
							
									vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
									vDto.setSdemo("��˳ɹ�");
									commonDataAccessService.updateData(vDto);
									++cksuccess;
								}
							}else{
							
								directdto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
								directdto.setSinputusercode(loginfo.getSuserCode());
								commonDataAccessService.updateData(directdto);
								vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
								vDto.setSdemo("��˳ɹ�");
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
					MessageDialog.openMessageDialog(null, "��˽�� ���ɹ�[ "+cksuccess+" ]��,ʧ��[ "+ (selectlist.size()-cksuccess)+" ]��"+error.toString());
				}else
				{
					MessageDialog.openMessageDialog(null, "��˽�� ���ɹ�[ "+cksuccess+" ]��,ʧ��[ "+ (selectlist.size()-cksuccess)+" ]��");
				}
			}
			editor.fireModelChanged();
		} else {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ��˵����ݣ�");
		}
		return super.checkSuccess(o);
	}

	/**
	 * �ж�ǩ������
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
			 * У��ƾ֤�Ƿ���ǩ��ǰѡ��ӡ�£�ͬһƾ֤�����ظ�ǩ��
			 */
			if(stampid!=null&&!stampid.equals("")){
				String[] stampids=stampid.split(",");
				for(int i=0;i<stampids.length;i++){
					if(stamp.equals(stampids[i])){
						MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"+vDto.getSvoucherno()+" ��ǩ ˽�� \""+stampDto.getSstampname()+"\" ��ͬһƾ֤�����ظ�ǩ�£�");			    			
		    			return null;
					}
				}
			}
			/*
			 * У���û��Ƿ���ǩ˽�£�ͬһ�û�ֻ��ǩһ��˽��
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
								MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"+vDto.getSvoucherno()+" ��ǰ�û���ǩ  \""+stampDto.getSstampname()+"\" ��ͬһ�û�ֻ��ǩһ��˽�£���ѡ�������û���");    			    			
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
			 //��ѯ��������������
			try{
				tDto.setSorgcode(loginfo.getSorgcode());
				tDto.setStrecode(strecode);
				tDto=(TsTreasuryDto) commonDataAccessService.findRsByDto(tDto).get(0);
			}catch(Exception e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ�����в����ڣ�");
				
				return null;
			}
			//��ѯǩ��λ����Ϣ
			try{
				sDto.setSorgcode(loginfo.getSorgcode());
				sDto.setStrecode(strecode);
				sDto.setSvtcode(biztype);
				sList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(sDto);
				sDto.setSstamptype(stamp);
				sDto= (TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
				
			}catch(Exception e){    			
				log.error(e);
				MessageDialog.openMessageDialog(null, "����������룺"+strecode+" "+stampDto.getSstampname()+" ����δά���� ");    				
				return null;
			}
 
			//У��ӡ�¶�Ӧ��֤��ID��ӡ��ID�Ƿ��Ѿ�ά��
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
					 * ƾ֤��ǩ��˳��ǩ�£�ǩ��˳���С������ǩ��
					 * 
					 */
					if(seq!=null&&!seq.equals("")){
						Boolean bo = checkSignStampSeq(vDto, seq, sList, strecode, stampDto, stampid);
						if (!bo) {
							return null;
						}
					}
					/**
					 * �ж�ǩ�����ͣ�����ǩ��
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
	 * Direction: ���ʧ�� ename: checkfault ���÷���: viewers: * messages:
	 */
	public String checkfault(Object o) {
		try {
			if (selectlist == null || selectlist.size() == 0) {
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ��˵����ݣ�");
			} else if (selectlist.size() > 1) {
				MessageDialog.openMessageDialog(null, "ÿ��ֻ�ܶ�һ�����ݽ������ʧ�ܲ�����");
			} else {				
				boolean flag = org.eclipse.jface.dialogs.MessageDialog
						.openConfirm(null, "��ʾ", "�Ƿ�Ҫ��ѡ���������ʧ����");
				if (flag) {
					if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
						for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
							if(paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)){
								MessageDialog.openMessageDialog(null, "�Ѹ��˳ɹ����ݲ������ʧ�ܣ�");
								return "";
							}
						}
					}else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
						for (TfDirectpaymsgmainDto paydto : (ArrayList<TfDirectpaymsgmainDto>) selectlist) {
							if(paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)){
								MessageDialog.openMessageDialog(null, "�Ѹ��˳ɹ����ݲ������ʧ�ܣ�");
								return "";
							}
						}
					}else{
						for (TvDwbkDto dwbkdto : (ArrayList<TvDwbkDto>) selectlist) {
							if(dwbkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)){
								MessageDialog.openMessageDialog(null, "�Ѹ��˳ɹ����ݲ������ʧ�ܣ�");
								return "";
							}
						}
					}
					TsCheckfailreasonDto dto = new TsCheckfailreasonDto();
					dto.setSorgcode(loginfo.getSorgcode());
					List<TsCheckfailreasonDto> checkfail = commonDataAccessService.findRsByDto(dto);
					if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
						for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
							
							// ����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(paydto.getSbizno());
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,commonDataAccessService)) {
								// ����״̬
								paydto.setScheckstatus(StateConstant.CHECKSTATUS_2);// ����״̬Ϊ���ʧ��
								paydto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);

								commonDataAccessService.updateData(paydto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "���ʧ�ܣ�");
							} else {
								MessageDialog.openMessageDialog(null, "��������ˣ�");
							}
						}
					} else if (biztype.equals(BizTypeConstant.VOR_TYPE_DWBK)) {
						for (TvDwbkDto dwbkdto : (ArrayList<TvDwbkDto>) selectlist) {
							// ����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(dwbkdto.getSbizno());
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService
									.find(pk);
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,
									commonDataAccessService)) {
								// ����״̬
								dwbkdto
										.setScheckstatus(StateConstant.CHECKSTATUS_2);// ����״̬Ϊ���ʧ��
								dwbkdto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);
						
								commonDataAccessService.updateData(dwbkdto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "���ʧ�ܣ�");
							} else {
								MessageDialog.openMessageDialog(null, "��������ˣ�");
							}
						}
					} else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
						for (TfDirectpaymsgmainDto directdto : (ArrayList<TfDirectpaymsgmainDto>) selectlist) {
							
							// ����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(directdto.getIvousrlno()+"");
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService
									.find(pk);
							
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,
									commonDataAccessService)) {
								// ����״̬
								directdto.setScheckstatus(StateConstant.CHECKSTATUS_2);// ����״̬Ϊ���ʧ��
								directdto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);

								commonDataAccessService.updateData(directdto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "���ʧ�ܣ�");
							} else {
								MessageDialog.openMessageDialog(null, "��������ˣ�");
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
	 * Direction: ����ʧ�� ename: checkfault ���÷���: viewers: * messages:
	 */
	public String reviewfault(Object o) {
		try {
			if (selectlist == null || selectlist.size() == 0) {
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���˵����ݣ�");
			} else if (selectlist.size() > 1) {
				MessageDialog.openMessageDialog(null, "ÿ��ֻ�ܶ�һ�����ݽ��и���ʧ�ܲ�����");
			} else {				
				boolean flag = org.eclipse.jface.dialogs.MessageDialog
						.openConfirm(null, "��ʾ", "�Ƿ�Ҫ��ѡ�����ݸ���ʧ����");
				if (flag) {
					if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
						for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
							if(!paydto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)){
								MessageDialog.openMessageDialog(null, "ֻ�и��˳ɹ������ݲſɸ���ʧ�ܣ�");
								return "";
							}
						}
					}else{
						for (TvDwbkDto dwbkdto : (ArrayList<TvDwbkDto>) selectlist) {
							if(!dwbkdto.getScheckstatus().equals(StateConstant.CHECKSTATUS_4)){
								MessageDialog.openMessageDialog(null, "ֻ�и��˳ɹ������ݲſɸ���ʧ�ܣ�");
								return "";
							}
						}
					}
					TsCheckfailreasonDto dto = new TsCheckfailreasonDto();
					dto.setSorgcode(loginfo.getSorgcode());
					List<TsCheckfailreasonDto> checkfail = commonDataAccessService.findRsByDto(dto);
					if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)||MsgConstant.VOUCHER_NO_5267.equals(biztype)) {
						for (TvPayoutmsgmainDto paydto : (ArrayList<TvPayoutmsgmainDto>) selectlist) {
							
							// ����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(paydto.getSbizno());
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService.find(pk);
							
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,commonDataAccessService)) {
								// ����״̬
								paydto.setScheckstatus(StateConstant.CHECKSTATUS_2);// ����״̬Ϊ���ʧ��
								paydto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);

								commonDataAccessService.updateData(paydto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "����ʧ�ܣ�");
							} else {
								MessageDialog.openMessageDialog(null, "��������ˣ�");
							}
						}
					} else if (biztype.equals(BizTypeConstant.VOR_TYPE_DWBK)) {
						for (TvDwbkDto dwbkdto : (ArrayList<TvDwbkDto>) selectlist) {
							// ����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(dwbkdto.getSbizno());
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService
									.find(pk);
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,
									commonDataAccessService)) {
								// ����״̬
								dwbkdto
										.setScheckstatus(StateConstant.CHECKSTATUS_2);// ����״̬Ϊ���ʧ��
								dwbkdto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);
						
								commonDataAccessService.updateData(dwbkdto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "����ʧ�ܣ�");
							} else {
								MessageDialog.openMessageDialog(null, "��������ˣ�");
							}
						}
					} else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
						for (TfDirectpaymsgmainDto directdto : (ArrayList<TfDirectpaymsgmainDto>) selectlist) {
							
							// ����ƾ֤����Ϣ
							TvVoucherinfoPK pk = new TvVoucherinfoPK();
							pk.setSdealno(directdto.getIvousrlno()+"");
							TvVoucherinfoDto vdto = (TvVoucherinfoDto) commonDataAccessService
									.find(pk);
							
							if (CheckFaultMsgDialogFacade.open(vdto, checkfail,
									commonDataAccessService)) {
								// ����״̬
								directdto.setScheckstatus(StateConstant.CHECKSTATUS_2);// ����״̬Ϊ���ʧ��
								directdto.setSinputusercode(loginfo.getSuserCode());
								
								vdto.setSstatus(DealCodeConstants.VOUCHER_CHECKFAULT);

								commonDataAccessService.updateData(directdto);
								commonDataAccessService.updateData(vdto);
								MessageDialog.openMessageDialog(null, "����ʧ�ܣ�");
							} else {
								MessageDialog.openMessageDialog(null, "��������ˣ�");
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
	 * Direction ������ӡƾ֤ ��ѡ������ݣ�
	 */
	public String printSelectedVoucher(Object o) {
		if (null==selectlist || selectlist.size() < 0) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ��ӡ��ƾ֤  !!!");
		}
		return super.printSelectedVoucher(o);
		}
	/**
	 * Direction: ��ѯƾ֤��ӡ���� ename: queryVoucherPrintCount ���÷���: viewers: *
	 * messages:
	 */
	public String queryVoucherPrintCount(TvVoucherinfoDto vDto) {
		String err = null;
		try {
			err = voucherLoadService.queryVoucherPrintCount(vDto);
		} catch (ITFEBizException e) {
			log.error(e);
			return "��ѯ�쳣";
		}
		return err;
	}

	/**
	 * Direction: ��ѯƾ֤���� ename: queryVoucherPrintCount ���÷���: viewers: *
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
	 * Direction: ����ƾ֤���ѯƾ֤������Ϣ ename: queryVoucherIndexInfo ���÷���: viewers: *
	 * messages:
	 */
	public List queryVoucherIndexInfo(IDto idto) {
		int count = 0;
		// ����ʵ���ʽ���˿�ҵ��
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
	 * Direction: ��ѯ��¼ ename: queryRecords ���÷���: viewers: ��ѯ�����ͼ messages:
	 */
	public String queryRecords(Object o) {
		selectlist = new ArrayList();
		setBiztype(biztype);
		return "��ѯ�����ͼ";
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
	 * Direction: ����ʧ�� ename: bufufault ���÷���: viewers: * messages:
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
	 * ��ȡ������
	 * 
	 * @param moneyUnit
	 * @return
	 */
	private BigDecimal getMultiply(String moneyUnit) {
		BigDecimal multiplicand = new BigDecimal(1);// ������
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
	 * ��ȡ��ѯ������չSQL
	 * 
	 * @param startfamt
	 * @param ndfamt
	 * @return
	 */
	private String getExtentSQl(String startfamt, String endfamt,
			String moneyUnit) {
		StringBuffer moneyExtent = new StringBuffer();
		BigDecimal multiplicand = getMultiply(moneyUnit);// ������
		// ��ѯ��Χ
		BigDecimal statFamt = new BigDecimal(0);
		BigDecimal endFamt = new BigDecimal(0);
		if (null != startfamt && startfamt.trim().length() > 0) {
			statFamt = (new BigDecimal(startfamt.trim())).multiply(multiplicand);
		}
		if (null != endfamt && endfamt.trim().length() > 0) {
			endFamt = (new BigDecimal(endfamt.trim())).multiply(multiplicand);
		}
		if ( !StringUtils.isBlank(startfamt)&& !StringUtils.isBlank(endfamt)&& statFamt.compareTo(endFamt) > 0) {
			MessageDialog.openMessageDialog(null, "��ѯ�������Ӧ��С�ڲ�ѯ������޷�Χ   !!!");
			return null;
		}
		//Ĭ�Ͻ���ֶ��� N_MONEY
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
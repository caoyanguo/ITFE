package com.cfcc.itfe.client.sendbiz.recvbusinessreconciliation;

import itferesourcepackage.ReportBussReadReturnEnumFactory;
import itferesourcepackage.VoucherRegularCheckAccountSvtcodeEnumFactory;

import java.util.ArrayList;
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
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeRecvBussOcx;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeRecvBussReturnOcx;
import com.cfcc.itfe.client.sendbiz.sendbusinessreconciliation.ReconcilePayinfoSubBean;
import com.cfcc.itfe.client.sendbiz.sendbusinessreconciliation.ReconcilePayquotaSubBean;
import com.cfcc.itfe.client.sendbiz.sendbusinessreconciliation.ReconcileRealdialSubBean;
import com.cfcc.itfe.client.sendbiz.sendbusinessreconciliation.ReconcileRefundSubBean;
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
 * @time   15-01-16 10:28:33
 * ��ϵͳ: SendBiz
 * ģ��:recvBusinessReconciliation
 * ���:RecvBusinessReconciliation
 */
@SuppressWarnings("unchecked")
public class RecvBusinessReconciliationBean extends AbstractRecvBusinessReconciliationBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(RecvBusinessReconciliationBean.class);
    private ReconcilePayinfoSubBean payinfoSubBean;
    private ReconcileRealdialSubBean realdialSubBean;
    private ReconcileRefundSubBean refundSubBean;
    private ReconcilePayquotaSubBean payquotaSubBean;
    private List<Mapper> bankCodeList;    
	List<TvVoucherinfoDto> checkList = null;
	List<TvVoucherinfoDto> returnList = null;
	List subVoucherList = null;
	// �û���¼��Ϣ
	private ITFELoginInfo loginInfo;

	// ������ʼ����
	private String startDate;
	private String endDate;
	// ǩ������
	private String stamp;
	// ǩ���б�
	private List<TsStamppositionDto> stampList = null;
	// ƾ֤����
	private String voucherType = MsgConstant.VOUCHER_NO_3507;

	public RecvBusinessReconciliationBean() {
		super();
		payinfoSubBean = new ReconcilePayinfoSubBean();
		realdialSubBean = new ReconcileRealdialSubBean();
		refundSubBean = new ReconcileRefundSubBean();
		payquotaSubBean = new ReconcilePayquotaSubBean();
		payinfoDto = new TfReconcilePayinfoMainDto();
	    realdialMainDto = new TfReconcileRealdialMainDto();
	    refundMainDto = new TfReconcileRefundMainDto();
	    payquotaMainDto = new TfReconcilePayquotaMainDto();
		
		dto = new TvVoucherinfoDto();
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		checkList = new ArrayList<TvVoucherinfoDto>();
		dto.setSorgcode(loginInfo.getSorgcode());
		dto.setSvtcode(MsgConstant.VOUCHER_NO_3507);
		pagingcontext = new PagingContext(this);
		startDate = TimeFacade.getCurrentStringTime();
		endDate = TimeFacade.getCurrentStringTime();
		TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
		tsTreasuryDto.setSorgcode(loginInfo.getSorgcode());
		List<TsTreasuryDto> list = null;
		try {
			list = (List<TsTreasuryDto>) commonDataAccessService
					.findRsByDto(tsTreasuryDto);
		} catch (ITFEBizException e) {
			log.error(e);
		}
		if (list != null && list.size() > 0) {
			dto.setStrecode(list.get(0).getStrecode());// �������
		}
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
			if(bankCodeList==null||bankCodeList.size()<=0)
			{
				TsConvertbanktypeDto finddto = new TsConvertbanktypeDto();
				finddto.setSorgcode(loginInfo.getSorgcode());
					List<TsConvertbanktypeDto> resultList = commonDataAccessService.findRsByDto(finddto);
					if(resultList!=null&&resultList.size()>0)
					{
						Mapper map = null;
						bankCodeList = new ArrayList<Mapper>();
						for(TsConvertbanktypeDto temp:resultList)
						{
							map = new Mapper(temp.getSbankcode(), temp.getSbankname()+(temp.getSbankcode().length()>3?temp.getSbankcode().substring(0,3):""));
							 bankCodeList.add(map);
						}
					}
			}
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤ˢ�²��������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
	}

	/**
	 * Direction: ����ƾ֤ ename: voucherGenerator ���÷���: viewers: * messages:
	 */
	public String voucherGenerator(Object o) {
		try {
			if(checkList!=null&&checkList.size()>0)
			{
				TvVoucherinfoDto vDto = null;
				boolean isgen = false;
				for(int i=0;i<checkList.size();i++)
				{
					vDto = (TvVoucherinfoDto)checkList.get(i);
					if(!DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(vDto.getSstatus())&&!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(vDto.getSstatus()))
					{
						isgen = true;
						break;
					}
				}
				if(isgen)
				{
					MessageDialog.openMessageDialog(null, "ѡ�е������а����������ɻص������ݣ�");
					return null;
				}
				for(int i=0;i<checkList.size();i++){
					vDto = (TvVoucherinfoDto)checkList.get(i);
					if(MsgConstant.VOUCHER_NO_3507.equals(vDto.getSvtcode()))
					{
						if(vDto.getSext1()!=null&&"2".equals(vDto.getSext1()))
							vDto.setSvtcode(MsgConstant.VOUCHER_NO_5507);
						else
							vDto.setSvtcode(MsgConstant.VOUCHER_NO_2507);
					}else if(MsgConstant.VOUCHER_NO_3508.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5508);
					}else if(MsgConstant.VOUCHER_NO_3509.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5509);
					}else if(MsgConstant.VOUCHER_NO_3510.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5510);
					}else if(MsgConstant.VOUCHER_NO_3511.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5511);
					}else if(MsgConstant.VOUCHER_NO_3512.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5512);
					}else if(MsgConstant.VOUCHER_NO_3513.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5513);
					}
				}
			}else
			{
				MessageDialog.openMessageDialog(null, "��ѡ����Ҫ���ɻص���ƾ֤��");
				return null;
			}
			List resultList = voucherLoadService.voucherGenerate(checkList);
			if (resultList != null && resultList.size() > 0) {
				MessageDialog.openMessageDialog(null, "�ص����ɲ����ɹ����ɹ�����Ϊ��" + resultList.get(0)
						+ "��");
			}
			
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception(e.getMessage(), e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("����ƾ֤���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.voucherGenerator(o);
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
	 * 
	 * @return
	 */
	public String getOCXStampServerURL() {
		String ls_URL = "";
		try {
			ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception("��ȡOCX�ؼ�ǩ�·���URL��ַ���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return ls_URL;
	}

	/**
	 * Direction: ��ѯ ename: search ���÷���: viewers: * messages:
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
//			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼����������ƾ֤��");
//		else if (pageResponse.getTotalCount() == 0
//				&& (dto.getSstatus().equals(DealCodeConstants.VOUCHER_STAMP)))
//			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼������ǩ�£�");
//		else if (pageResponse.getTotalCount() == 0&& dto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS))
		if (pageResponse.getTotalCount() == 0)
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: ȫѡ ename: selectAll ���÷���: viewers: * messages:
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
	 * Direction: ���͵���ƾ֤ ename: sendReturnVoucher ���÷���: viewers: * messages:
	 */
	public String voucherSend(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���͵���ƾ֤�ļ�¼��");
			return "";
		}
		int count = 0;
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ",
				"��ȷ��Ҫ��ѡ�еļ�¼ִ�з��͵���ƾ֤������")) {
			return "";
		}
		// ��ѡ�е��б���в���ʱ�����²�ѯ���ݿ�ȡ����������״̬
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		for (TvVoucherinfoDto vDto : this.checkList) {
			TvVoucherinfoDto newvDto = new TvVoucherinfoDto();
			try {
				newvDto = getDto(vDto);
			} catch (ITFEBizException e) {
				Exception e1 = new Exception("���²�ѯ���ݳ��ִ���", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e1);
			}
			checkList.add(newvDto);
		}
		
		
		
		
		
		
		
		

		try {
			List<TvVoucherinfoDto> returnVoucherList = new ArrayList<TvVoucherinfoDto>();
			//���һص�ƾ֤��Ϣ
			for(TvVoucherinfoDto vdto : this.checkList){
				returnVoucherList.add(searchReturnVoucher(vdto));
			}
			for (TvVoucherinfoDto infoDto : returnVoucherList) {
				if (loginInfo.getPublicparam().indexOf(",stampmode=sign") >= 0) {
					if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
							.getSstatus().trim()))) {
						MessageDialog.openMessageDialog(null,
								"��ѡ��ƾ֤״̬Ϊ \"����ɹ�\" �ļ�¼��");
						return "";
					}
				} else {
					if (!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto
							.getSstatus().trim()))) {
						MessageDialog.openMessageDialog(null,
								"��ѡ��ƾ֤״̬Ϊ \"ǩ�³ɹ�\" �ļ�¼��");
						return "";
					}
				}
			}
			count = voucherLoadService.voucherReturnSuccess(returnVoucherList);
			MessageDialog.openMessageDialog(null, "���͵���ƾ֤   "
					+ checkList.size() + " �����ɹ�����Ϊ��" + count + " ��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return "";
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("���͵���ƾ֤����������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);

			return "";
		}
		return super.voucherSend(o);

	}

	/**
	 * Direction: ƾ֤�鿴 ename: voucherView ���÷���: viewers: * messages:
	 */
	public String voucherView(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ļ�¼��");
			return "";
		}
		try {
			ActiveXCompositeRecvBussOcx.init(0);

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤�鿴�쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.voucherView(o);
	}

	/**
	 * Direction: �ص���ԭչʾ ename: returnVoucherView ���÷���: viewers: * messages:
	 */
	public String returnVoucherView(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ļ�¼��");
			return "";
		}
		try {
			if (returnList == null)
				returnList = new ArrayList<TvVoucherinfoDto>();
			TvVoucherinfoDto fdto = new TvVoucherinfoDto();
			List tempList = null;
			for (TvVoucherinfoDto vdto : checkList) {
				fdto.setSext1(vdto.getSdealno());
				if (MsgConstant.VOUCHER_NO_3507.equals(vdto.getSvtcode())) {
					fdto.setSvtcode(null);
					tempList = commonDataAccessService.findRsByDtoWithWhere(
							fdto, " and (S_VTCODE='"
									+ MsgConstant.VOUCHER_NO_2507
									+ "' or S_VTCODE=')"
									+ MsgConstant.VOUCHER_NO_5507 + "' ");
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
			}
			if (returnList == null || returnList.size() == 0) {
				MessageDialog.openMessageDialog(null, "ѡ��ļ�¼û���յ��Է��Ļص���");
				return "";
			}
			ActiveXCompositeRecvBussReturnOcx.init(0);

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤�鿴�쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.returnVoucherView(o);
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
			Exception e1 = new Exception("��ȡƾ֤ǩ���б�����쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}

		return maplist;
	}
	
	/**
	 * ���Ҷ�Ӧ�Ļص���Ϣ
	 * @return
	 * @throws ITFEBizException 
	 */
	public TvVoucherinfoDto searchReturnVoucher(TvVoucherinfoDto vdto) throws ITFEBizException{
		TvVoucherinfoDto searchDto = new TvVoucherinfoDto();
		if("2".equals(vdto.getSext1())){	//���������
			if(MsgConstant.VOUCHER_NO_3507.equals(vdto.getSvtcode())){	//����3507��5507
				searchDto.setSvtcode(MsgConstant.VOUCHER_NO_5507);
			}else if(MsgConstant.VOUCHER_NO_3508.equals(vdto.getSvtcode())){	//����3508��5508
				searchDto.setSvtcode(MsgConstant.VOUCHER_NO_5508);
			}else if(MsgConstant.VOUCHER_NO_3510.equals(vdto.getSvtcode())){	//����3510��5510
				searchDto.setSvtcode(MsgConstant.VOUCHER_NO_5510);
			}else{
				throw new ITFEBizException("���Ҷ�Ӧ�ص���Ϣʧ�ܣ�");
			}
		}else if("3".equals(vdto.getSext1())){	//���з���	
			if(MsgConstant.VOUCHER_NO_3507.equals(vdto.getSvtcode())){	//����3507��2507
				searchDto.setSvtcode(MsgConstant.VOUCHER_NO_2507);
			}else{
				throw new ITFEBizException("���Ҷ�Ӧ�ص���Ϣʧ�ܣ�");
			}
		}else{
			throw new ITFEBizException("���Ҷ�Ӧ�ص���Ϣʧ�ܣ�");
		}
		searchDto.setSext1(vdto.getSdealno());
		List list = commonDataAccessService.findRsByDto(searchDto);
		if(null == list || list.size() == 0){
			throw new ITFEBizException("���Ҷ�Ӧ�ص���Ϣʧ�ܣ�");
		}
		return (TvVoucherinfoDto) list.get(0);
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


	public String getVtcodeTypeCmt(String vtcode) {
		String cmt = "";
		if (vtcode.equals(MsgConstant.VOUCHER_NO_3503)) {
			cmt = "�����Ϣ����3503";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3504)) {
			cmt = "������Ϣ����3504";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3505)) {
			cmt = "ʵ����Ϣ����3505";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3506)) {
			cmt = "�����˸�����3506";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3551)) {
			cmt = "�������������֧����������˵�3551";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3507)) {
			cmt = "������Ϣ����3507";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3508)) {
			cmt = "ʵ����Ϣ����3508";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3509)) {
			cmt = "�����˸�����3509";
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_3510)) {
			cmt = "�����ȶ���3510";
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
			throw new ITFEBizException("�������δά����", new Exception(""));
		TvVoucherinfoDto vDto = null;
		for (TsTreasuryDto tsDto : tList) {
			vDto = (TvVoucherinfoDto) dto.clone();
			vDto.setStrecode(tsDto.getStrecode());
			vDto.setScreatdate(TimeFacade.getCurrentStringTime());
			vDto.setShold3(startDate);
			vDto.setShold4(endDate);
			list.add(vDto);
		}
		return list;
	}
	
	/**
	 * Direction: ˫���鿴ҵ����ϸ
	 * ename: doubleClick
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
        TvVoucherinfoDto voucherinfoDto = (TvVoucherinfoDto)o;
        if(MsgConstant.VOUCHER_NO_3507.equals(voucherinfoDto.getSvtcode())){
        	payinfoDto = new TfReconcilePayinfoMainDto();
        	payinfoDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReconcilePayinfoMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(payinfoDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���������Ϣҵ�����ݳ���");
			}
			payinfoDto = list.get(0);
			TfReconcilePayinfoSubDto payinfoSubDto = new TfReconcilePayinfoSubDto();
			payinfoSubDto.setIvousrlno(payinfoDto.getIvousrlno());
			String tmp = payinfoSubBean.searchDtoList(payinfoSubDto);
			if(tmp==null){
    			PagingContext p = payinfoSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "������Ϣ����";
        }else if(MsgConstant.VOUCHER_NO_3508.equals(voucherinfoDto.getSvtcode())){
        	realdialMainDto = new TfReconcileRealdialMainDto();
        	realdialMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReconcileRealdialMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(realdialMainDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯʵ����Ϣ����ҵ�����ݳ���");
			}
			realdialMainDto = list.get(0);
			TfReconcileRealdialSubDto realdialSubDto = new TfReconcileRealdialSubDto();
			realdialSubDto.setIvousrlno(realdialMainDto.getIvousrlno());
			String tmp = realdialSubBean.searchDtoList(realdialSubDto);
			if(tmp==null){
    			PagingContext p = realdialSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "ʵ����Ϣ����";
        }else if(MsgConstant.VOUCHER_NO_3509.equals(voucherinfoDto.getSvtcode())){
        	refundMainDto = new TfReconcileRefundMainDto();
        	refundMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReconcileRefundMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(refundMainDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯʵ����Ϣ����ҵ�����ݳ���");
			}
			refundMainDto = list.get(0);
			TfReconcileRefundSubDto refundSubDto = new TfReconcileRefundSubDto();
			refundSubDto.setIvousrlno(refundMainDto.getIvousrlno());
			String tmp = refundSubBean.searchDtoList(refundSubDto);
			if(tmp==null){
    			PagingContext p = refundSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "�����˸�����";
        }else if(MsgConstant.VOUCHER_NO_3510.equals(voucherinfoDto.getSvtcode())){
        	payquotaMainDto = new TfReconcilePayquotaMainDto();
        	payquotaMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReconcilePayquotaMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(payquotaMainDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯʵ����Ϣ����ҵ�����ݳ���");
			}
			payquotaMainDto = list.get(0);
			TfReconcilePayquotaSubDto payquotaSubDto = new TfReconcilePayquotaSubDto();
			payquotaSubDto.setIvousrlno(payquotaMainDto.getIvousrlno());
			String tmp = payquotaSubBean.searchDtoList(payquotaSubDto);
			if(tmp==null){
    			PagingContext p = payquotaSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "�����ȶ���";
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
		wheresql.append(" (S_EXT1='2' or S_EXT1='3')");
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
		dto.setShold3(startDate);
		dto.setShold4(endDate);
		try {
			page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,
					wheresql.toString(), " TS_SYSUPDATE desc");
			return page;
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Throwable e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤��ѯ�����쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.retrieve(pageRequest);
	}

	/**
	 * Direction: ǩ�� ename: voucherStamp ���÷���: viewers: * messages:
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
				Exception e1 = new Exception("ǩ�³����쳣��", e);
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
			MessageDialog.openMessageDialog(null, "��ѡ��ǩ�����ͣ�");
			return null;
		}

		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫǩ�µļ�¼��");
			return "";
		}

		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ǩ�²�����")) {
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
					.getSstatus().trim()))) {
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ����ɹ��ļ�¼��");
				return "";
			}
		}
		try {
			if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
					dto.getSvtcode())) {
				MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");
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
				MessageDialog.openMessageDialog(null, "��ǰ�û���  \""
						+ stampDto.getSstampname() + "\"  ǩ��Ȩ�ޣ�");
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
							MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"
									+ vDto.getSvoucherno() + " ��ǩ  \""
									+ stampDto.getSstampname()
									+ "\" ��ͬһƾ֤�����ظ�ǩ�£�");
							return "";
						}
					}
				}
				if (!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)
						&& !stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
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
								MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"
										+ vDto.getSvoucherno() + " ��ǰ�û���ǩ  \""
										+ tstampDto.getSstampname()
										+ "\" ��ͬһ�û�ֻ��ǩһ��˽�£���ѡ�������û���");
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
					MessageDialog.openMessageDialog(null, "�����������" + strecode
							+ "�ڹ���������Ϣ�����в����ڣ�");

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
					MessageDialog.openMessageDialog(null, "����������룺" + strecode
							+ " " + stampDto.getSstampname() + " ����δά���� ");

					return "";
				}
				if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
					if (tDto.getSrotarycertid() == null
							|| tDto.getSrotarycertid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "֤��ID ����δά���� ");

						return "";
					} else if (tDto.getSrotaryid() == null
							|| tDto.getSrotaryid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "ӡ��ID ����δά���� ");

						return "";
					}
				} else if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {

					if (tDto.getScertid() == null
							|| tDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "֤��ID ����δά���� ");

						return "";
					} else if (tDto.getSstampid() == null
							|| tDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "ӡ��ID ����δά���� ");

						return "";
					}

				} else {
					if (uDto.getScertid() == null
							|| uDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "��ǰ�û�  "
								+ stampDto.getSstampname() + "  ֤��ID����δά���� ");
						return "";
					} else if (uDto.getSstampid() == null
							|| uDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "��ǰ�û�   "
								+ stampDto.getSstampname() + "  ӡ��ID����δά���� ");
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
								ActiveXCompositeRecvBussReturnOcx
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
														"������룺"
																+ vDto
																		.getStrecode()
																+ " ƾ֤����: "
																+ vDto
																		.getSvtcode()
																+ vDto
																		.getSvoucherno()
																+ " \""
																+ stampDto
																		.getSstampname()
																+ "\"ǩ��ǰ���� \""
																+ err + "\"ǩ�£�");

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
															"������룺"
																	+ vDto
																			.getStrecode()
																	+ " ƾ֤����: "
																	+ vDto
																			.getSvtcode()
																	+ " \""
																	+ stampDto
																			.getSstampname()
																	+ "\"ǩ��ǰ���� \""
																	+ err
																	+ "\"ǩ�£�");

											return "";
										}
									}

								}
							}
						}
						if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
							if (!voucherLoadService.getOfficialStamp().equals(
									MsgConstant.VOUCHER_OFFICIALSTAMP)) {
								sinList.add(ActiveXCompositeRecvBussReturnOcx
										.getVoucherStamp(vDto, tDto
												.getScertid(), sDto
												.getSstampposition(), tDto
												.getSstampid()));
							}
						} else if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
							if (!voucherLoadService.getOfficialStamp().equals(
									MsgConstant.VOUCHER_ROTARYSTAMP)) {
								sinList.add(ActiveXCompositeRecvBussReturnOcx
										.getVoucherStamp(vDto, tDto
												.getSrotarycertid(), sDto
												.getSstampposition(), tDto
												.getSrotaryid()));
							}
						} else {
							if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
							{
								sinList.add(ActiveXCompositeRecvBussReturnOcx
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
			MessageDialog.openMessageDialog(null, "ƾ֤ǩ��   " + checkList.size()
					+ " �����ɹ�����Ϊ��" + count + " ��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return "";

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ǩ�²��������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
			return "";

		}
		return super.voucherStamp(o);
	}

	/**
	 * Direction:ǩ�³��� ename: voucherStampCancle ���÷���: viewers: * messages:
	 */
	public String voucherStampCancle(Object o) {
		int count = 0;
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫǩ�³����ļ�¼��");
			return "";
		}
		if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ��ǩ�����ͣ�");
			return null;
		}
		if (!org.eclipse.jface.dialogs.MessageDialog
				.openConfirm(this.editor.getCurrentComposite().getShell(),
						"��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ǩ�³���������")) {
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
					.getSstatus()))
					&& !(DealCodeConstants.VOUCHER_STAMP.equals(infoDto
							.getSstatus()))) {
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ����ɹ���ǩ�³ɹ��ļ�¼��");
				return "";
			}
		}

		try {
			if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
					dto.getSvtcode())) {
				MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");
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
				MessageDialog.openMessageDialog(null, "��ǰ�û���  \""
						+ stampDto.getSstampname() + "\"  ǩ��Ȩ�ޣ�����ͨ��������Ȩ����ǩ��");
				String msg = "��Ҫ������Ȩ���ܲ��ܳ���ǩ�£�";
				if (!AdminConfirmDialogFacade.open("B_247", "ҵ�����ƾ֤", "��Ȩ�û�"
						+ loginInfo.getSuserName() + "����ǩ��", msg)) {
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
					MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"
							+ vDto.getSvoucherno() + " δǩ  \""
							+ stampDto.getSstampname() + "\" ��");
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
						MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"
								+ vDto.getSvoucherno() + "   \""
								+ stampDto.getSstampname()
								+ "\" ���ǵ�ǰ�û���ǩ����ͨ��������Ȩ����ǩ��");
						String msg = "��Ҫ������Ȩ���ܲ��ܳ���ǩ�£�";
						if (!AdminConfirmDialogFacade
								.open("B_247", "���뱨��ƾ֤", "��Ȩ�û�"
										+ loginInfo.getSuserName() + "����ǩ��",
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
					MessageDialog.openMessageDialog(null, "�����������" + strecode
							+ "�ڹ���������Ϣ�����в����ڣ�");

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
					MessageDialog.openMessageDialog(null, "����������룺" + strecode
							+ " " + stampDto.getSstampname() + " ����δά���� ");
					return "";
				}

				if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
					if (tDto.getSrotarycertid() == null
							|| tDto.getSrotarycertid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "֤��ID ����δά���� ");

						return "";
					} else if (tDto.getSrotaryid() == null
							|| tDto.getSrotaryid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "ӡ��ID ����δά���� ");
						return "";
					}
				} else if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {

					if (tDto.getScertid() == null
							|| tDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "֤��ID ����δά���� ");
						return "";
					} else if (tDto.getSstampid() == null
							|| tDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "ӡ��ID ����δά���� ");
						return "";
					}

				} else {
					if (uDto.getScertid() == null
							|| uDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "��ǰ�û�  "
								+ stampDto.getSstampname() + "  ֤��ID����δά���� ");
						return "";
					} else if (uDto.getSstampid() == null
							|| uDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "��ǰ�û�   "
								+ stampDto.getSstampname() + "  ӡ��ID����δά���� ");
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
								ActiveXCompositeRecvBussReturnOcx
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
														"������룺"
																+ vDto
																		.getStrecode()
																+ " ƾ֤����: "
																+ vDto
																		.getSvtcode()
																+ " \""
																+ stampDto
																		.getSstampname()
																+ "\"����ǩ��ǰ���ȳ��� \""
																+ err + "\"ǩ�£�");

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
			MessageDialog.openMessageDialog(null, "ƾ֤����ǩ��   "
					+ checkList.size() + " �����ɹ�����Ϊ��" + count + " ��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return "";

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ǩ�²������������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
			return "";

		}
		return super.voucherStampCancle(o);
	}

	/**
	 * ˢ��ǩ������
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
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤ˢ�²��������쳣��", e);
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
		this.dto.setSvtcode(voucherType);
		// ���ݲ�ͬ��ƾ֤��ʾ��ͬ�Ŀؼ�
		setContentVisible();
		pagingcontext.setPage(new PageResponse());
		refreshStampType(null);
	}

	public void setContentVisible() {
		List<String> visContentAreaName = new ArrayList<String>();
		visContentAreaName.add("��Ϣ��ѯ");
		visContentAreaName.add("ƾ֤��ѯһ����");
		List<ContainerMetaData> containerMetaData = MVCUtils
				.setContentAreasToVisible(editor, visContentAreaName);
		for (ContainerMetaData metadata : containerMetaData) {
			List controls = metadata.controlMetadatas;
			if ("��Ϣ��ѯ".equals(metadata.name)) {
				for (int i = 0; i < controls.size(); i++) {
					if (voucherType.equals(MsgConstant.VOUCHER_NO_3508)
							|| voucherType.equals(MsgConstant.VOUCHER_NO_3509)) {
						if (controls.get(i) instanceof ComboMetaData) {
							ComboMetaData combometadata = (ComboMetaData) controls.get(i);
							if (combometadata.caption.equals("֧����ʽ")||combometadata.caption.equals("��������")) {
								combometadata.visible = false;
							}
						}
					} else if (voucherType.equals(MsgConstant.VOUCHER_NO_3510)
							|| voucherType.equals(MsgConstant.VOUCHER_NO_3507)) {
//						if (controls.get(i) instanceof TextMetaData) {
//							TextMetaData textmetadata = (TextMetaData) controls
//									.get(i);
//							if (textmetadata.caption.equals("���д���")) {
//								textmetadata.visible = true;
//							}
//						}
						if (controls.get(i) instanceof ComboMetaData) {
							ComboMetaData combometadata = (ComboMetaData) controls.get(i);
							if (combometadata.caption.equals("֧����ʽ")||combometadata.caption.equals("��������")) {
								combometadata.visible = false;
							}
						}
					}
				}
			} else if ("ƾ֤��ѯһ����".equals(metadata.name)) {
				TableMetaData tablemd = (TableMetaData) controls.get(0);
				for (int i = 0; i < tablemd.columnList.size(); i++) {
					if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
						ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList
								.get(i);
						if (voucherType.equals(MsgConstant.VOUCHER_NO_3508)
								|| voucherType
										.equals(MsgConstant.VOUCHER_NO_3509)) {
							if (coletadata.title.equals("���д���")) {
								coletadata.visible = false;
							}
						} else if (voucherType
								.equals(MsgConstant.VOUCHER_NO_3510)
								|| voucherType
										.equals(MsgConstant.VOUCHER_NO_3507)) {
							if (coletadata.title.equals("���д���")) {
								coletadata.visible = true;
							}// else if(coletadata.title.equals("֧����ʽ")){
							// coletadata.visible = false;
							// }
						}
					}
				}
			}
		}
		MVCUtils.reopenCurrentComposite(editor);
	}

	public List getSubVoucherList() {
		subVoucherList = new ReportBussReadReturnEnumFactory().getEnums("0460");
		if (subVoucherList == null || subVoucherList.size() <= 0) {
			subVoucherList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_3507, "������Ϣ����3507");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3508, "ʵ����Ϣ����3508");
			// Mapper map2 = new Mapper(MsgConstant.VOUCHER_NO_3509,
			// "�����˸�����3509");
			Mapper map3 = new Mapper(MsgConstant.VOUCHER_NO_3510, "�����ȶ���3510");
			subVoucherList.add(map);
			subVoucherList.add(map1);
			// subVoucherList.add(map2);
			subVoucherList.add(map3);
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
	 * Direction: �ص�״̬��ѯ ename: queryStatusReturnVoucher ���÷���: viewers: *
	 * messages:
	 */
	public String queryStatusReturnVoucher(Object o) {
		int count = 0;
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ص�״̬�ļ�¼��");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"ѡ��ļ�¼��ƾ֤״̬�����ݿⲻһ��,�����²�ѯ���ٽ��в�����");
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_SUCCESS.equals(infoDto.getSstatus().trim())||DealCodeConstants.VOUCHER_SUCCESS_BACK.equals(infoDto.getSstatus()))) {
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ�ص��ɹ�������Ʊ�ɹ��ļ�¼��");
				return "";
			}
		}
		try {
			count = voucherLoadService.queryStatusReturnVoucher(checkList);
			MessageDialog.openMessageDialog(null, "ƾ֤�鿴�ص�״̬" + checkList.size()
					+ " �����ɹ�����Ϊ��" + count + " , ��鿴��");
			refreshTable();
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
			Exception e1 = new Exception("ƾ֤�鿴�ص�״̬���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return "";
	}
	// �ж��û�ѡ��ļ�¼�Ƿ������ݿ���״̬һ�£������һ�£�����ʾ�û����²�ѯ���ٽ��в���
	// �����û�ѡ����������²�ѯ���ݿ⣬�����ȶ��û�״̬
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
}
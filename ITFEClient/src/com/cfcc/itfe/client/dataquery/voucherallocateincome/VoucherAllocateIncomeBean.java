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
 * TCBS�ʽ��ĵ���
 * 
 * @author zhanghuibin
 * @time 14-04-01 14:26:36 ��ϵͳ: DataQuery ģ��:VoucherAllocateIncome
 *       ���:VoucherAllocateIncome
 */
public class VoucherAllocateIncomeBean extends
		AbstractVoucherAllocateIncomeBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(VoucherAllocateIncomeBean.class);

	private ITFELoginInfo loginInfo;
	// �ʽ�ҵ�������б�
	private List<Mapper> vtcodeKindList;
	// ҵ������
	private String vtcodeKind;
	// �ʽ�������
	private String fundtype;

	// ���ñ�������
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
		initvtcodeKindList();// ��ʼ���ʽ�ҵ�������б����Ҹ��丳ֵ
		fundtype = StateConstant.CMT100;
	}

	public void initvtcodeKindList() {
		vtcodeKindList = new ArrayList<Mapper>();
		// ���ʽ�ҵ�����͸�ֵ[1-���뼰ʡ������Ʊ�� 2-����������������]
		Mapper mapper1 = new Mapper();
		mapper1.setDisplayValue(MsgConstant.FUND_BIZ_TYPE_CENTREAREADATA_CMT);
		mapper1.setUnderlyValue(vtcode);
		vtcodeKindList.add(mapper1);
		Mapper mapper2 = new Mapper();
		mapper2.setDisplayValue("ʵ���ʽ��˿�");
		mapper2.setUnderlyValue(MsgConstant.VOUCHER_NO_3208);
		vtcodeKindList.add(mapper2);
		if (loginInfo.getPublicparam().indexOf(",collectpayment=1,") >= 0) {
			Mapper map3268 = new Mapper(MsgConstant.VOUCHER_NO_3268, "ʵ���ʽ�ר���˿�");
			vtcodeKindList.add(map3268);
		}
		Mapper mapper3 = new Mapper();
		mapper3.setDisplayValue("�����˸��˿�");
		if (isJLFlag) {
			mapper3.setUnderlyValue(MsgConstant.VOUCHER_NO_3251);
		} else {
			mapper3.setUnderlyValue(MsgConstant.VOUCHER_NO_3210);
		}
		vtcodeKindList.add(mapper3);
		Mapper mapper4 = new Mapper();
		mapper4.setDisplayValue("ֱ��֧����ȷ���˿�");
		mapper4.setUnderlyValue(MsgConstant.VOUCHER_NO_2203);
		vtcodeKindList.add(mapper4);
	}

	/**
	 * Direction: �������� ename: upLoad ���÷���: viewers: * messages:
	 */
	@SuppressWarnings("unchecked")
	public String upLoad(Object o) {
		if (StringUtils.isBlank(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "��ѡ�������룡");
			return null;
		}
		if (StringUtils.isBlank(fundtype)) {
			MessageDialog.openMessageDialog(null, "���ʽ������ͣ�");
			return null;
		} else {
			dto.setSreportkind(fundtype);
		}
		if (StringUtils.isBlank(vtcodeKind)) {
			MessageDialog.openMessageDialog(null, "��ѡ��ҵ�����ͣ�");
			return null;
		} else {
			dto.setSvtcode(vtcodeKind);
		}
		if (fundtype.equals(StateConstant.CMT108)) {
			if (!vtcodeKind.equals(MsgConstant.VOUCHER_NO_3208)
					&& !vtcodeKind.equals(MsgConstant.VOUCHER_NO_3210)) {
				MessageDialog.openMessageDialog(null,
						"�ʽ�������Ϊ [CMT108] ʱ,�ʽ�ҵ�����ͱ���Ϊ [ʵ���ʽ��˿�] �� [�����˸��˿�]��");
				return null;
			}
		}
		if (fundtype.equals(StateConstant.PKG007)) {
			if (!vtcodeKind.equals(MsgConstant.VOUCHER_NO_3208)
					&& !vtcodeKind.equals(MsgConstant.VOUCHER_NO_3210)) {
				MessageDialog.openMessageDialog(null,
						"�ʽ�������Ϊ  [PKG007] ʱ,�ʽ�ҵ�����ͱ���Ϊ [ʵ���ʽ��˿�] �� [�����˸��˿�]��");
				return null;
			}
		}
		if (fundtype.equals(StateConstant.TYPE999)) {
			if (!vtcodeKind.equals(vtcode)) {
				MessageDialog.openMessageDialog(null,
						"�ʽ�������Ϊ [����������������] ʱ,�ʽ�ҵ�����ͱ���Ϊ [��������Ʊ��]��");
				return null;
			}
		}
		if (findAdmDivCodeByStrecode())
			return null;
		List<String> serverpathlist = new ArrayList<String>();
		if (null == filePath || filePath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return null;
		}
		if (filePath.size() > 1000) {
			MessageDialog.openMessageDialog(null, "��ѡ�����ļ����ܴ���1000����");
			return null;
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "������ʾ",
				" ��ȷ�ϵ����ļ���[�ʽ���][ҵ������]�Ƿ�һ�£�ȷ����Ҫ������")) {
			return "";
		}
		List<File> fileList = new ArrayList<File>();
		try {
			for (File tmpfile : (List<File>) filePath) {
				if (null == tmpfile || null == tmpfile.getName()
						|| null == tmpfile.getAbsolutePath()) {
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}
				if (!tmpfile.getName().trim().toLowerCase().endsWith(".csv")) {
					MessageDialog.openMessageDialog(null,
							" ��ѡ����ȷ���ļ���ʽ,ֻ֧��csv��ʽ�ļ���");
					return null;
				}
				// ɾ����������ͬ���ļ�����ֹ����д��
				List<String> pathlist = new ArrayList<String>();
				String path = File.separator + "ITFEDATA" + File.separator
						+ loginInfo.getSorgcode() + File.separator
						+ TimeFacade.getCurrentStringTime() + File.separator
						+ tmpfile.getName();
				pathlist.add(path);
				DeleteServerFileUtil.delFile(commonDataAccessService, pathlist);
				// �������ļ����ص�������
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			int sumFile = 0; // ͳ�����е����txt�ļ�
			int wrongFileSum = 0; // ͳ�ƴ����ļ��ĸ������������20��������׷�Ӵ�����Ϣ
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
						+ "TCBS�ʽ��ĵ���("
						+ new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��")
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
					noteInfo = "��������" + sumFile + "���ļ���������" + wrongFileSum
							+ "�������ļ�����Ϣ���£�\r\n";
				} else {
					noteInfo = "��������" + sumFile + "���ļ���������" + errCount
							+ "�������ļ���������Ϣ���¡���ϸ������Ϣ��鿴" + errName + "����\r\n";
				}
				MessageDialog.openMessageDialog(null, noteInfo
						+ problemStr.toString());
			} else {
				MessageDialog.openMessageDialog(null, "�ļ����سɹ�,���ι����سɹ� "
						+ fileList.size() + " ���ļ���");
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
				log.error("ɾ���������ļ�ʧ�ܣ�", e1);
				MessageDialog.openErrorDialog(null, e);
			}
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		}
		return super.upLoad(o);
	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: ���뼰ʡ������Ʊ�ݲ�ѯ��� messages:
	 */
	public String query(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = new PageResponse();
		pageResponse = retrieve(pageRequest);
		if (pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
			return null;
		}
		pagingcontext.setPage(pageResponse);
		return super.query(o);
	}

	/**
	 * Direction: ���ز�ѯҳ�� ename: goQuery ���÷���: viewers: ���뼰ʡ������Ʊ�ݲ�ѯ messages:
	 */
	public String goQuery(Object o) {
		return super.goQuery(o);
	}

	/**
	 * Direction: �������� ename: savaDto ���÷���: viewers: * messages:
	 */
	public String savaDto(Object o) {
		if (saveDto.getStrecode() == null || saveDto.getStrecode().equals("")) {
			MessageDialog.openMessageDialog(null, "��ѡ���Ӧ�Ĺ��⣡");
			return null;
		}
		if (saveDto.getSvtcode() == null
				|| saveDto.getSvtcode().trim().equals("")) {
			MessageDialog.openMessageDialog(null, "ҵ�����Ͳ���Ϊ�գ�");
			return null;
		}
		if (saveDto.getSpayeeacctno() == null
				|| saveDto.getSpayeeacctno().equals("")) {
			MessageDialog.openMessageDialog(null, "�տ����˺Ų���Ϊ�գ�");
			return null;
		}
		if (saveDto.getSpayeeacctname() == null
				|| saveDto.getSpayeeacctname().equals("")) {
			MessageDialog.openMessageDialog(null, "�տ������Ʋ���Ϊ�գ�");
			return null;
		}
		if (saveDto.getSpayacctno() == null
				|| saveDto.getSpayacctno().equals("")) {
			MessageDialog.openMessageDialog(null, "�������˺Ų���Ϊ�գ�");
			return null;
		}
		if (saveDto.getSpayacctname() == null
				|| saveDto.getSpayacctname().equals("")) {
			MessageDialog.openMessageDialog(null, "���������Ʋ���Ϊ�գ�");
			return null;
		}
		if (saveDto.getSpaydealno() == null
				|| saveDto.getSpaydealno().trim().equals("")) {
			MessageDialog.openMessageDialog(null, "֧��������Ų���Ϊ�գ�");
			return null;
		}
		if(!loginInfo.getSorgcode().startsWith("09"))
			saveDto.setSvtcode(vtcode); // ƾ֤����Ϊ��������Ʊ��

		try {
			if (saveDto.getSdealno() != null) {
				voucherAllocateIncomeService.modifySaveDto(saveDto);
				super.modifyDto(o);
			} else {
				saveDto.setSorgcode(loginInfo.getSorgcode());
				saveDto.setScommitdate(TimeFacade.getCurrentStringTime());
				saveDto.setSvtcode(vtcode);
				saveDto.setTssysupdate(new Timestamp(new java.util.Date()
						.getTime()));// ����ʱ��
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
	 * Direction: �޸� ename: modifyDto ���÷���: viewers: TCBS�ʽ���¼�� messages:
	 */
	public String modifyDto(Object o) {
		if (!loginInfo.getSorgcode().startsWith("09")) {
			if (!vtcode.equals(vtcodeKind)) {
				MessageDialog.openMessageDialog(null, "ֻ�ܶ�ҵ������Ϊ��������Ʊ�ݽ����޸ģ�");
				return "";
			}
		}else{
			if (!AdminConfirmDialogFacade.open("��Ҫ������Ȩ�����޸�ƾ֤��")) {
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
	 * Direction: �޸ı��� ename: saveModifyDto ���÷���: viewers: TCBS�ʽ��ĵ����ѯ���
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
	 * Direction: ɾ�� ename: delDto ���÷���: viewers: * messages:
	 */
	public String delDto(Object o) {
		if (deleteDto == null || deleteDto.getSdealno() == null
				|| deleteDto.getSdealno().equals("")) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��ɾ��ѡ�еļ�¼��")) {
			return "";
		}
		try {
			//�����������Ϻ�   �����Ƿ�����ƾ֤
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
	 * Direction: �������� ename: singleClick ���÷���: viewers: * messages:
	 */
	public String singleClick(Object o) {
		deleteDto = (TvVoucherinfoAllocateIncomeDto) o;
		return super.singleClick(o);
	}

	/**
	 * ������֯�������롢������������Ӧ����������
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
				MessageDialog.openMessageDialog(null, "�������"
						+ dto.getStrecode() + "��Ӧ�Ĳ����������δά����");
				return true;
			}
			finorgDto = (TsConvertfinorgDto) list.get(0);
			if (StringUtils.isBlank(finorgDto.getSadmdivcode()))
				MessageDialog.openMessageDialog(null, "��������"
						+ finorgDto.getSfinorgcode() + "��Ӧ�������������δά����");
			else {
				dto.setSadmdivcode(finorgDto.getSadmdivcode());
				return false;
			}
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "��ѯ������������쳣��");
		}
		return true;
	}

	/**
	 * ����Ĭ�Ϲ������
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
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"��ѯ�����쳣��", e));
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
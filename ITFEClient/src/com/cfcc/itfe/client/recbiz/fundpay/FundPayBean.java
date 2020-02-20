package com.cfcc.itfe.client.recbiz.fundpay;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.ChinaTest;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.common.file.ErrorNoteDialog;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.dialog.MessageInfoDialog;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.BizDataCountDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TsCheckbalanceDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.constants.MetaDataConstants;
import com.cfcc.jaf.ui.metadata.ViewMetaData;

/**
 * codecomment:
 * 
 * @author hua
 * @time 12-09-27 09:19:17 ��ϵͳ: RecBiz ģ��:fundpay ���:FundPay
 */
public class FundPayBean extends AbstractFundPayBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(FundPayBean.class);

	private ITFELoginInfo loginfo;
	private BigDecimal sumamt;
	// ƾ֤�ܱ���
	private Integer vouCount;
	private Date adjustdate;
	private Date systemDate;
	private BigDecimal totalmoney = new BigDecimal(0.00);
	private int totalnum = 0;
	private TbsTvPayoutDto _dto;;
	private String area;
	private GroupQueryBean groupbean = null;
	private TbsTvPayoutDto groupSelect = new TbsTvPayoutDto();
	//���Ŵ���ѡ��list
	List selectedwaitlist = null;

	public FundPayBean() {
		super();
		filepath = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		selectedwaitlist = new ArrayList();
		filedata = new FileResultDto();
		searchdto = new TbsTvPayoutDto();
		paysdto = new TbsTvPayoutDto();
		paylist = new ArrayList<ResultDesDto>();
		trecode = "";
		searchdto.setDaccept(DateUtil.currentDate());
		defvou = "";
		endvou = "";
		newbudgcode = "";
		grpno = "";
		totalcount = null;
		setGroupbean(new GroupQueryBean(commonDataAccessService,new TbsTvPayoutDto()));
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		area = AreaSpecUtil.getInstance().getArea();

	}

	/**
	 * Direction: ���ݼ��� ename: dateload ���÷���: viewers: * messages:
	 */
	public String dateload(Object o) {
		Map tmpMap = new HashMap();
		String interfacetype = ""; // �ӿ�����
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer errorPartInfo = new StringBuffer(""); // ������Ŵ�����Ϣ
		StringBuffer errorTotalInfo = new StringBuffer(""); // ������д�����Ϣ
		int errorFileCount = 0;
		// �ж��Ƿ�ѡ���ļ�
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			filepath = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			return null;
		}

		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			boolean bool = false;
			Set<String> sets = new HashSet<String>();
			for (int i = 0; i < filepath.size(); i++) {
				String tmpname = ((File) filepath.get(i)).getName();
				String str = tmpname.toLowerCase().substring(
						tmpname.lastIndexOf("."));
				// �ж��Ƿ������ļ����С�txt��ʽ��pas��ʽͬʱ����
				if (".txt".equals(str) || ".pas".equals(str)) {
					sets.add(str);
					if (sets.size() == 2) {
						break;
					}
				} else {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "ѡ����ļ���ʽ����ȷ��");
					return null;
				}
			}
			if (sets.size() == 1) {
				Object[] strs = (Object[]) sets.toArray();
				if (".pas".equals(strs[0])) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "ѡ����ļ���ʽ����ȷ��");
					return null;
				}
			} else if (sets.size() == 2) {
				bool = true;
				// ��ȡ�ܳ�
				tmpMap = commonDataAccessService
						.getMiYao(loginfo.getSorgcode());
			}

			adjustdate = commonDataAccessService.getAdjustDate();
			String tmpdate = commonDataAccessService.getSysDBDate();
			systemDate = Date.valueOf(tmpdate.substring(0, 4) + "-"
					+ tmpdate.substring(4, 6) + "-" + tmpdate.substring(6, 8));
			// ���ݼ���
			boolean financeflag = true; // ��¼�Ƿ��ǲ����ӿ�
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				String tmpfilename = tmpfile.getName().toLowerCase(); // �ļ�������
				String tmpfilepath = tmpfile.getAbsolutePath(); // ��ȡ�ļ���·��

				// �ж��ļ��Ƿ�ѡ��
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}
				// �����ļ����ֻ�ȡ�ӿ�����
				interfacetype = getFileObjByFileNameBiz(
						BizTypeConstant.BIZ_TYPE_PAY_OUT, tmpfilename, bool);
				// �ж��ļ��Ƿ��Ѿ�����
				if (commonDataAccessService.verifyImportRepeat(tmpfilename)
						&& bool) {
					errorFileCount++;
					if (errorFileCount < 15) {
						errorPartInfo.append("ʵ���ʽ��ļ�[" + tmpfilename
								+ "]У��Ϊ�ظ�����!\r\n");
						errorTotalInfo.append("ʵ���ʽ��ļ�[" + tmpfilename
								+ "]У��Ϊ�ظ�����!\r\n");
					} else {
						errorTotalInfo.append("ʵ���ʽ��ļ�[" + tmpfilename
								+ "]У��Ϊ�ظ�����!\r\n");
					}
					continue;
				}
				// �����ӿڴ��� ����ǲ����ӿڣ������ж���˰�����Ƿ���ͬ�������ֹͬͣ����
				if (MsgConstant.SHIBO_FUJIAN_DAORU.equals(interfacetype)) {
					if (financeflag) { // ���financeflagΪtrue����ʼ�ж��ļ��б��Ƿ���ͬһ��˰���أ�financeflagΪfalse���Ѿ��жϲ����ļ��б�ʱͬһ��˰����
						if (judgeOrgcode(filepath)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null,
									"ѡ���ļ�����ͬһ����˰������������ѡ��");
							// ɾ�����������ϴ��ļ�
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
						financeflag = false;
					}
					// ��ǩ
					if (!TipsFileDecrypt.verifyCA(tmpfilepath)) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "��֤�ļ�����");
						// ɾ�����������ϴ��ļ�
						DeleteServerFileUtil.delFile(commonDataAccessService,
								serverpathlist);
						return null;
					}
					// �ϴ����Ҽ�¼��־
					String serverpath = ClientFileTransferUtil
							.uploadFile(tmpfile.getAbsolutePath());
					fileList.add(new File(serverpath));
					serverpathlist.add(serverpath);
					continue;
				}
				// ����Ϊ�����ӿڴ���

				// ������ �ļ������ŵ�pas�ļ� ��txt�ļ���ͬ����
				if (bool) {
					if (tmpfilename.endsWith(".pas")) {
						if (ChinaTest.isChinese(tmpfilepath)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null,
									"�ļ�·���к��֣������·����");
							// ɾ�����������ϴ��ļ�
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
						String miyao = (String) tmpMap.get(tmpfilename
								.substring(tmpfilename.length() - 16,
										tmpfilename.lastIndexOf(".")));
						if (miyao != null && miyao.length() > 0) {
							// ��Կ����
							miyao = commonDataAccessService.decrypt(miyao);
							if ("-1".equals(TipsFileDecrypt.decryptPassFile(
									tmpfilepath, tmpfilepath.replaceAll(".pas",
											".tmp"), miyao))) {
								DisplayCursor.setCursor(SWT.CURSOR_ARROW);
								MessageDialog.openMessageDialog(null,
										tmpfilename + "�ļ�����ʧ�ܣ�");
								// ɾ�����������ϴ��ļ�
								DeleteServerFileUtil
										.delFile(commonDataAccessService,
												serverpathlist);
								return null;
							}
							// �ϴ�δ�����ļ�����¼��־
							String serverpath = ClientFileTransferUtil
									.uploadFile(tmpfile.getAbsolutePath());
							// �ϴ������ɹ��ļ�
							fileList.add(new File(ClientFileTransferUtil
									.uploadFile(tmpfile.getAbsolutePath()
											.replaceAll(".pas", ".tmp"))));
							serverpathlist.add(serverpath);
							// ɾ����ʱ�ļ�
							FileUtil.getInstance().deleteFile(
									tmpfilepath.replaceAll(".pas", ".tmp"));
							continue;
						} else {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null,
									"��Կû��ά��������ά����Կ��");
							// ɾ�����������ϴ��ļ�
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
					}
				}
				// ���Žӿڴ���
				// �ϴ���txt�ļ�
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			MulitTableDto bizDto = null;
			String errName = "";
			// ����ӿ��Ǹ����ģ��򵯳��Ի���ѡ�񸶿�����Ϣ
			if (fileList != null && fileList.size() > 0) {
				if (MsgConstant.SHIBO_FUJIAN_DAORU.equals(interfacetype)) {
					String str = ((File) filepath.get(0)).getName();
					List<ShiboDto> shiboList = fundIntoService.getPayerInfo(str
							.substring(str.indexOf("q") - 9, str.indexOf("q")),
							loginfo.getSorgcode());
					// �����������Ϣ��һ�� �򲻵����Ի���
					if (null == shiboList || shiboList.size() == 0) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						// ���û�ж�Ӧ���տ�����Ϣ������ʾ�û�
						MessageDialog
								.openMessageDialog(null, "û�ж�Ӧ�ĸ�������Ϣ�����ʵ��");
						// ɾ�����������ϴ��ļ�
						DeleteServerFileUtil.delFile(commonDataAccessService,
								serverpathlist);
						return null;
					} else {
						// ��������Ϣ���ڶ����򵯳��Ի���
						if (shiboList.size() > 1) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							ErrorNoteDialog dialog = new ErrorNoteDialog(null,
									shiboList);
							if (IDialogConstants.OK_ID == dialog.open()) {
								this.shibodto = dialog.getShibo();
								DisplayCursor.setCursor(SWT.CURSOR_WAIT);
							}
						} else {
							this.shibodto = shiboList.get(0);
						}
					}
					bizDto = fileResolveCommonService.loadFile(fileList,
							BizTypeConstant.BIZ_TYPE_PAY_OUT, interfacetype,
							shibodto);
				} else {
					// �����������ϴ��ļ�
					bizDto = fileResolveCommonService.loadFile(fileList,
							BizTypeConstant.BIZ_TYPE_PAY_OUT, interfacetype,
							null);
				}
				int tmp_errcount = errorFileCount;
				if (null != bizDto.getErrorList()
						&& bizDto.getErrorList().size() > 0) {
					for (int m = 0; m < bizDto.getErrorList().size(); m++) {
						tmp_errcount++;
						if (tmp_errcount < 15) {
							errorPartInfo.append(bizDto.getErrorList().get(m)
									.substring(4)
									+ "\r\n");
							errorTotalInfo.append(bizDto.getErrorList().get(m)
									+ "\r\n");
						} else {
							errorTotalInfo.append(bizDto.getErrorList().get(m)
									+ "\r\n");
						}
					}
				}
				errName = StateConstant.Import_Errinfo_DIR
						+ "ʵ���ʽ��ļ����������Ϣ("
						+ new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��")
								.format(new java.util.Date()) + ").txt";
				if (!"".equals(errorTotalInfo.toString())) {
					FileUtil.getInstance().writeFile(errName,
							errorTotalInfo.toString());
				}
				FileUtil
						.getInstance()
						.deleteFileWithDays(
								StateConstant.Import_Errinfo_DIR,
								Integer
										.parseInt(StateConstant.Import_Errinfo_BackDays));
				// ��¼��־
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverpathlist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_PAY_OUT);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if (errorPartInfo.toString().trim().length() > 0) {
					String noteInfo = "��������" + filepath.size() + "���ļ���������"
							+ (errorFileCount + bizDto.getErrorCount())
							+ "�������ļ���������Ϣ���¡���ϸ������Ϣ��鿴" + errName + "����\r\n";
					MessageInfoDialog.openMessageDialog(null, noteInfo
							+ errorPartInfo.toString());
				} else {
					MessageDialog.openMessageDialog(null, "�ļ����سɹ�,���ι����سɹ� "
							+ fileList.size() + " ���ļ���");
				}
			} else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "��������" + filepath.size()
						+ "���ļ���������" + errorFileCount + "�������ļ�����Ϣ���£�\r\n"
						+ errorPartInfo.toString());
			}

		} catch (Throwable e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			// ɾ�����������ϴ��ļ�
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,
						serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("ɾ���������ļ�ʧ�ܣ�", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			return null;
		}
		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		filepath = new ArrayList();
		this.editor.fireModelChanged();
		return super.dateload(o);
	}

	/**
	 * Direction: ��ת�������Ų�ѯ ename: topiliangxh ���÷���: viewers: �������� messages:
	 */
	public String topiliangxh(Object o) {
		return super.topiliangxh(o);
	}

	/**
	 * Direction: ��ת������Ų�ѯ ename: tozhubixh ���÷���: viewers: ������� messages:
	 */
	public String tozhubixh(Object o) {
//		if ("FUZHOU".equals(area)) { // ��ȡ����ģʽ
			return super.openfjPayout(o);
//		}
//		return super.tozhubixh(o);
	}

	/**
	 * Direction: ֱ���ύ ename: submit ���÷���: viewers: * messages:
	 */
	public String submit(Object o) {
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_PAY_OUT, loginfo.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_PAY_OUT);
				if (StateConstant.SUBMITSTATE_SUCCESS == result) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "�����ɹ���");
				} else if (StateConstant.SUBMITSTATE_DONE == result) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "�뵼�����ݣ�");
				} else {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "����ʧ�ܣ�");
				}
			} else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "û��ֱ���ύȨ�ޣ��������Ա��ϵ��");
				return null;
			}
		} catch (ITFEBizException e) {
			log.error("ֱ���ύʧ�ܣ�", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.submit(o);
	}

	/**
	 * Direction: ����Ĭ�Ͻ��� ename: goback ���÷���: viewers: ֱ��֧����ȵ��� messages:
	 */
	public String goback(Object o) {
		paysdto.setSpayeename(null);
		paysdto.setFamt(null);
		editor.fireModelChanged();
		return super.goback(o);
	}

	/**
	 * Direction: ����ȷ�� ename: plsubmit ���÷���: viewers: * messages:
	 */
	public String plsubmit(Object o) {
		try {
			if(true) {
				MessageDialog.openMessageDialog(null, "�ù�����δ����!");
				return super.plsubmit(o);
			}			
			if (commonDataAccessService.judgeBatchConfirm(
					BizTypeConstant.BIZ_TYPE_PAY_OUT, loginfo.getSorgcode())) {
				if (null == selectedfilelist || selectedfilelist.size() == 0) {
					MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
					return null;
				}
				// ѡ���ļ����ܽ��
				BigDecimal selSumamt = new BigDecimal(0.00);
				// ѡ���ļ���ƾ֤�ܱ���
				Integer selvouCount = 0;
				if (sumamt == null) {
					MessageDialog.openMessageDialog(null, "����д�ܽ�");
					return null;
				}
				if (vouCount == null) {
					MessageDialog.openMessageDialog(null, "����дƾ֤�ܱ�����");
					return null;
				}
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					selSumamt = selSumamt.add(dto.getNmoney());
					selvouCount = selvouCount + dto.getIcount();
				}
				if (selSumamt.compareTo(sumamt) != 0
						|| selvouCount.compareTo(vouCount) != 0) {
					MessageDialog.openMessageDialog(null, "������ܽ�� ["
							+ sumamt.toString() + "]��ƾ֤�ܱ���["
							+ vouCount.toString() + "]�������ļ����ܽ�ƾ֤�ܱ�������,���֤!");
					return null;
				} else {
					DisplayCursor.setCursor(SWT.CURSOR_WAIT);
					massdispose();
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				}
			} else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "û����������Ȩ�ޣ��������Ա��ϵ��");
				return null;
			}

		} catch (ITFEBizException e) {
			log.error("��������ʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.plsubmit(o);
	}

	/**
	 * Direction: ����ɾ�� ename: pldel ���÷���: viewers: * messages:
	 */
	public String pldel(Object o) {
		// ȷ���Ѿ���ѡ��ļ�¼
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
			return null;
		}
		// ��ʾ�û�ȷ��ɾ��
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ��?")) {
			try {
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					int result = uploadConfirmService.batchDelete(
							BizTypeConstant.BIZ_TYPE_PAY_OUT, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showfilelist.remove(dto);

				}
				selectedfilelist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "�����ɹ���");
			} catch (ITFEBizException e) {
				log.error("����ɾ��ʧ�ܣ�", e);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
			return null;
		}

		return super.pldel(o);
	}

	/**
	 * Direction: ����ύ ename: zbsubmit ���÷���: viewers: * messages:
	 */
	public String zbsubmit(Object o) {
		// ȷ���Ѿ���ѡ��ļ�¼
		if (null == selecteddatalist || selecteddatalist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
			return null;
		}
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			try {
				for (int i = 0; i < selecteddatalist.size(); i++) {
					TbsTvPayoutDto dto = (TbsTvPayoutDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_PAY_OUT, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showdatalist.remove(dto);

				}
				selecteddatalist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "�����ɹ���");
			} catch (ITFEBizException e) {
				log.error("����ύʧ�ܣ�", e);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
		}
		return super.zbsubmit(o);
	}

	// /**
	// * Direction: �������Ų�ѯ ename: plsearch ���÷���: viewers: * messages:
	// */
	// public String plsearch(Object o) {
	// // ���������ӿڻ�ȡshowfilelist�����showfilelist��������¼����ѡ�У�����ֱ��ѡ��
	// try {
	// showfilelist = new ArrayList();
	// selectedfilelist = new ArrayList();
	// showfilelist = uploadConfirmService.batchQuery(
	// BizTypeConstant.BIZ_TYPE_PAY_OUT, sumamt);
	// if (null != showfilelist) {
	// if (1 == showfilelist.size()) {
	// selectedfilelist.addAll(showfilelist);
	// }
	// }
	// this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	// } catch (ITFEBizException e) {
	// log.error("�������Ų�ѯ����", e);
	// MessageDialog.openErrorDialog(null, e);
	// return null;
	// }
	//
	// return super.plsearch(o);
	// }

	/**
	 * Direction: ������Ų�ѯ ename: zbsearch ���÷���: viewers: * messages:
	 */
	public String zbsearch(Object o) {
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			// �����������
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// δ����
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_PAY_OUT, searchdto);
			// ͳ�Ʊ������
			List<TbsTvPayoutDto> mlist = (List<TbsTvPayoutDto>) showdatalist;
			totalmoney = new BigDecimal(0.00);
			totalnum = 0;
			for (TbsTvPayoutDto mdto : mlist) {
				totalmoney = totalmoney.add(mdto.getFamt());
				totalnum++;
			}
			if (showdatalist.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "û�в�ѯ���������������ݣ�");
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				return null;
			}

			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("������Ų�ѯʧ�ܣ�", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		return super.zbsearch(o);
	}

	/**
	 * Direction: ��ѯ���� ename: queryService ���÷���: viewers: ��ѯ���� messages:
	 */
	public String queryService(Object o) {
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		// �����������
		paysdto.setSbookorgcode(loginfo.getSorgcode());
		// δ����
		paysdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
		if (null == defvou) {
			defvou = "";
		}
		if (null == endvou) {
			endvou = "";
		}
		paysdto.setStrecode(trecode);
		paysdto.setSvouno(defvou.trim() + endvou.trim());
		paysdto.setSgroupid(null);
		try {
			querylist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_PAY_OUT+",grpnull", paysdto);
			if (null == querylist || querylist.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "û�в�ѯ��δ���ŵļ�¼!");
				return null;
			}
		} catch (ITFEBizException e) {
			log.error("������Ų�ѯ�쳣��", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.queryService(o);
	}

	/**
	 * Direction: ȫѡ ename: selectall ���÷���: viewers: * messages:
	 */
	public String selectall(Object o) {
		if (null == selecteddatalist || selecteddatalist.size() == 0) {
			selecteddatalist = new ArrayList();
			selecteddatalist.addAll(showdatalist);
		} else {
			selecteddatalist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.selectall(o);
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

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	private String getFileObjByFileNameBiz(String biztype, String filename,
			boolean bool) throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();

		if (tmpfilename_new.toLowerCase().indexOf(".txt") > 0) {
			tmpfilename_new = tmpfilename_new.toLowerCase();
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			// ʮ��λ˵�����п����ǵط�������tbs boolΪtrue ��Ϊtbs�ӿڷ���Ϊ�ط�����
			if (tmpfilename.length() == 13 || tmpfilename.length() == 15) {
				// 8λ���ڣ�2λ���κţ�2λҵ�����ͣ�1λ�����ڱ�־���
				fileobj.setSdate(tmpfilename.substring(0, 8)); // ����
				if (tmpfilename.length() == 13) {
					fileobj.setSbatch(tmpfilename.substring(8, 10)); // ���κ�
					fileobj.setSbiztype(tmpfilename.substring(10, 12)); // ҵ������
					fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // �����ڱ�־
				} else {
					fileobj.setSbatch(tmpfilename.substring(8, 12)); // ���κ�
					fileobj.setSbiztype(tmpfilename.substring(12, 14)); // ҵ������
					fileobj.setCtrimflag(tmpfilename.substring(14, 15)); // ҵ������
				}
				if (bool) {
					interficetype = MsgConstant.SHIBO_XIAMEN_DAORU;// tbs�ӿ�
				} else {
					interficetype = MsgConstant.SHIBO_SHANDONG_DAORU; // �����ӿ�����
					// ������û����
				}
				if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(fileobj
						.getSbiztype())
						|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(fileobj
								.getSbiztype())) {
				} else {
					throw new ITFEBizException("ҵ�����Ͳ�����!");
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					throw new ITFEBizException("�����ڱ�־�����ϣ�����Ϊ��0�������� �� ��1��������!");
				} else {
					if (MsgConstant.TIME_FLAG_TRIM.equals(fileobj
							.getCtrimflag())) {
						if (DateUtil.after(systemDate, adjustdate)) {
							throw new ITFEBizException("������"
									+ com.cfcc.deptone.common.util.DateUtil
											.date2String(adjustdate)
									+ "�ѹ������ܴ��������ҵ��");
						}
					}
				}

			} else if (tmpfilename.length() == 23) {
				fileobj.setSdate(tmpfilename.substring(0, 8)); // ����
				fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_PAY_OUT); // ҵ������
				interficetype = MsgConstant.SHIBO_FUJIAN_DAORU;// �����ӿ�
				if ("q".equals(tmpfilename.substring(19, 20))) {
				} else {
					throw new ITFEBizException("�ļ�����ʽ����!");
				}
			} else {
				throw new ITFEBizException("�ļ�����ʽ����!");
			}
		} else if (tmpfilename_new.indexOf(".pas") > 0) {
			// �����������кţ�12λ��������(8λ)�������ļ���ţ�10λ������������(12λ)��.pas
			String tmpfilename = tmpfilename_new.replace(".pas", "");
			if (tmpfilename.length() == 42) {
				if (!commonDataAccessService.auditBankCode(tmpfilename
						.substring(0, 12)))
					throw new ITFEBizException("�����кŲ�����!");
				interficetype = MsgConstant.SHIBO_XIAMEN_DAORU;// TBS�ӿ�����
			} else {
				throw new ITFEBizException("�ļ�����ʽ����!");
			}
		} else {
			throw new ITFEBizException("�ļ�����ʽ����!");
		}
		return interficetype;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	/**
	 * 
	 * �ж�ѡ����ļ��Ƿ���һ����˰����
	 */
	private boolean judgeOrgcode(List<File> filelist) {
		if (filelist.size() == 1) {
			return false;
		} else {
			String tmpname = ((File) filepath.get(0)).getName();
			String taxcode = tmpname.substring(tmpname.indexOf("z") + 1,
					tmpname.lastIndexOf("q"));
			for (int i = 1; i < filepath.size(); i++) {
				String str = ((File) filepath.get(i)).getName();
				if (!taxcode.equals(str.substring(str.indexOf("z") + 1, str
						.lastIndexOf("q")))) {
					return true;
				}
			}
			return false;
		}
	}

	public BigDecimal getSumamt() {
		return sumamt;
	}

	public void setSumamt(BigDecimal sumamt) {
		if(null == vouCount || null == sumamt){
			MessageDialog.openMessageDialog(null, "ƾ֤�������ܽ���Ϊ�գ�");
			return ;
		}
		this.sumamt = sumamt;
		if (sumamt == null || sumamt.compareTo(new BigDecimal(0)) == 0) {
			// MessageDialog.openMessageDialog(null, "����Ϊ�ջ�0��");
		} else {
			try {
				showfilelist = new ArrayList();
				selectedfilelist = new ArrayList();
				showfilelist = uploadConfirmService.batchQuery(
						BizTypeConstant.BIZ_TYPE_PAY_OUT, sumamt);
				if (null != showfilelist) {
					if (1 == showfilelist.size()) {
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
						// massdispose();
					}
					this.editor
							.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}
				if (showfilelist == null || showfilelist.size() <= 0) {
					MessageDialog.openMessageDialog(null, "û�з������������ݣ�");
				}
			} catch (ITFEBizException e) {
				log.error("����ʧ�ܣ�", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
	}

	// �����ύ����
	private void massdispose() throws ITFEBizException {
		String warnInfo = "";
		if (selectedfilelist.size() == 0) {

		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.batchConfirm(
						BizTypeConstant.BIZ_TYPE_PAY_OUT, dto);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result)
					showfilelist.remove(dto);

			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, "����ɹ���");
		}
	}
	
	/**
	 * Direction: ���Ŵ���ȫѡ
	 * ename: selectallwait
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectallwait(Object o){
		if (null == selectedwaitlist || selectedwaitlist.size() == 0) {
			selectedwaitlist = new ArrayList();
			selectedwaitlist.addAll(querylist);
		} else {
			selectedwaitlist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.selectallwait(o);
    }

	/**
	 * Direction: ����ʧ�� ename: setFail ���÷���: viewers: * messages:
	 */
	public String setFail(Object o) {
		if (selectedwaitlist == null || selectedwaitlist.size()==0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�������Ŵ����ļ�¼��");
		} else {
			String msg="";
			for(int i=0; i < selectedwaitlist.size(); i++){
				TbsTvPayoutDto dto = (TbsTvPayoutDto) selectedwaitlist.get(i);
				if (!StateConstant.CONFIRMSTATE_YES.equals(dto.getSstatus())) {
					DisplayCursor.setCursor(SWT.CURSOR_WAIT);
					try {
						// �ж��ļ����Ƿ�ȫ�����ţ����ȫ�����ţ����÷��ͱ���
						uploadConfirmService.checkAndSendMsg(dto,
								MsgConstant.MSG_NO_5101,
								TbsTvPayoutDto.tableName(), dto.getIvousrlno());
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						dto = null;
						editor.fireModelChanged();
					} catch (ITFEBizException e) {
						log.error("��ѯ��ʱ��δ���ż�¼����", e);
						MessageDialog.openErrorDialog(null, e);
					}
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				} else {
					msg += "��ƾ֤"+ dto.getSvouno() +"�����ţ��������ó����Ŵ�����\n";
				}
			}
			selectedwaitlist.clear();
			if("".equals(msg)){
				try {
					querylist = uploadConfirmService.eachQuery(BizTypeConstant.BIZ_TYPE_PAY_OUT, paysdto);
				} catch (ITFEBizException e) {
					log.error("��ѯ��ʱ��δ���ż�¼����", e);
					MessageDialog.openErrorDialog(null, e);
				}
				if (null == querylist || querylist.size() == 0) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "��ѯ�޼�¼!");
					return null;
				}
				MessageDialog.openMessageDialog(null, "�������Ŵ����ɹ���");
			}else{
				MessageDialog.openMessageDialog(null, msg);
			}
		}
		editor.fireModelChanged();
		return super.setFail(o);
	}

	/**
	 * Direction: ��ѡһ����¼ ename: selOneRecord ���÷���: viewers: * messages:
	 */
	public String selOneRecord(Object o) {
		_dto = (TbsTvPayoutDto) o;
		return super.selOneRecord(o);
	}

	/**
	 * Direction: ��ϸɾ�� ename: delDetail ���÷���: viewers: * messages:
	 */
	public String delDetail(Object o) {
		if (selectedwaitlist ==null || selectedwaitlist.size()==0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫɾ���ļ�¼��");
		} else {
			if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
					.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ��ѡ�е�����?")) {
			for(int i=0; i < selectedwaitlist.size(); i++){
				TbsTvPayoutDto _dto = (TbsTvPayoutDto) selectedwaitlist.get(i);
				if (!StateConstant.CONFIRMSTATE_YES.equals(_dto.getSstatus())) {
					DisplayCursor.setCursor(SWT.CURSOR_WAIT);
					try {
						int result = uploadConfirmService.eachDelete(
								BizTypeConstant.BIZ_TYPE_PAY_OUT, _dto);
						if (StateConstant.SUBMITSTATE_DONE == result
								|| StateConstant.SUBMITSTATE_SUCCESS == result)
							showdatalist.remove(_dto);
						// �ж��ļ����Ƿ�ȫ�����ţ����ȫ�����ţ����÷��ͱ���
						uploadConfirmService.checkAndSendMsg(_dto,
								MsgConstant.MSG_NO_5101,
								TbsTvPayoutDto.tableName(), null);
						querylist.remove(_dto);
						_dto = null;
						editor.fireModelChanged();
					} catch (ITFEBizException e) {
						log.error("��ѯ��ʱ��δ���ż�¼����", e);
						MessageDialog.openErrorDialog(null, e);
					}
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				}
			}
			selectedwaitlist.clear();
			MessageDialog.openMessageDialog(null, "��ϸɾ���ɹ���");
			}
		}
		return super.delDetail(o);

	}

	/**
	 * Direction: ����ƽ�� ename: tryBalance ���÷���: viewers: * messages:
	 */
    public String tryBalance(Object o){
    	if(null == grpno || "".equals(grpno)) {
    		MessageDialog.openMessageDialog(null, "���������!");
    		return "";
    	}
    	/**
    	 * У��ƽ��֮ǰ����ǰ�ܱ������ܽ����½����ݿ�
    	 */
    	try {
    		/**
	    	 * �ҵ���ǰ�����Ϣ
	    	 */
	    	TsCheckbalanceDto check = getcheckDto();        
	    	List l = commonDataAccessService.findRsByDtoUR(check);
			if(null == l || l.size() == 0) {
				MessageDialog.openMessageDialog(null, "����Ŷ�Ӧ��Ϣ�����ڻ����ύ��");
				return "";
			}
	    	TsCheckbalanceDto updateDto = getcheckDto();
	    	StringBuffer sb = new StringBuffer("");
	    	if(null == totalcount || totalcount == 0) {
				sb.append("�ܱ����������0\n");
			}
			if(null == totalfamt || totalfamt.compareTo(new BigDecimal("0.00"))==0) {
				sb.append("�ܽ���Ϊ��\n");
			}
			if(!"".equals(sb.toString())) {
				MessageDialog.openMessageDialog(null, sb.toString());
				return "";
			}
			/**
			 * ���û��޸�֮����ܽ��ܱ������µ����ݿ���
			 */
	    	updateDto.setItotalnum(this.getTotalcount());
	    	updateDto.setNtotalmoney(this.getTotalfamt());
	    	updateDto.setIcheckednum(((TsCheckbalanceDto)l.get(0)).getIcheckednum());
	    	updateDto.setNcheckmoney(((TsCheckbalanceDto)l.get(0)).getNcheckmoney());
	    	updateDto.setSisbalance(((TsCheckbalanceDto)l.get(0)).getSisbalance());
	    	commonDataAccessService.updateData(updateDto);
	    	
			TsCheckbalanceDto resdto = (TsCheckbalanceDto)l.get(0);
			Integer checknum = resdto.getIcheckednum();
			Integer totalunm = resdto.getItotalnum();
			BigDecimal checkfamt = resdto.getNcheckmoney();
			BigDecimal totalfamt = resdto.getNtotalmoney();
			
			if(null == checknum || checknum.intValue() == 0) {
				checknum = 0;
			}
			if(null == totalunm || totalunm.intValue() == 0) {
				totalunm = 0;
			}
			if(null == checkfamt) {
				checkfamt = new BigDecimal("0.00");
			}
			if(null == totalfamt) {
				totalfamt = new BigDecimal("0.00");
			}
			if(totalunm.intValue() == checknum.intValue()) {
				if(this.getTotalfamt().compareTo(checkfamt) == 0) { //ƽ��
					if(!"1".equals(resdto.getSisbalance())) {
						resdto.setSisbalance("1"); //1--ƽ��
						commonDataAccessService.updateData(resdto);
					}
					MessageDialog.openMessageDialog(null, "��У��ƽ��,�ɽ����ύ������\n" +
							"["+grpno+"] �����ű���:"+checknum+" �����Ž��:"+checkfamt);
					return "";
				}else { //��ƽ��
					if(!"0".equals(resdto.getSisbalance())) {
						resdto.setSisbalance("0"); //1--��ƽ��
						commonDataAccessService.updateData(resdto);
					}
					MessageDialog.openMessageDialog(null, "��У�鲻ƽ��,�������ű����ﵽ�ܱ���,��ϸ���£�\n" +
							"["+grpno+"] �����ű���:"+checknum+" �����Ž��:"+checkfamt);
					return "";
				}
			}else {
				MessageDialog.openMessageDialog(null, "��У�鲻ƽ�⣡\n" +
						"["+grpno+"] �����ű���:"+checknum+" �����Ž��:"+checkfamt);
				return "";
			}
			
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
        return super.tryBalance(o);
    }
	
    public TsCheckbalanceDto getcheckDto() {
    	TsCheckbalanceDto check = new TsCheckbalanceDto();
        check.setSorgcode(loginfo.getSorgcode());
        check.setSgroupid(getGrpno());
        check.setSusercode(loginfo.getSuserCode());
        return check;
    }
    /**
	 * Direction: ��ѯ���� ename: queryTheGroup ���÷���: viewers: ����Ϣ��ʾ messages:
	 */
    public String queryTheGroup(Object o){
    	if(null == grpno || "".equals(grpno)) {
    		MessageDialog.openMessageDialog(null, "���������!");
    		return "";
    	}
        String s = queryGroup();
        if("".equals(s)) {
        	return super.queryTheGroup(o) ;
        }else if(null == s) {
        	MessageDialog.openMessageDialog(null, "���������������Ϣ!");
        	return "";
        }
        return super.queryTheGroup(o) ;       
    }
    
    public String queryGroup() {    	
    	TbsTvPayoutDto paydto = new TbsTvPayoutDto();
        paydto.setSusercode(loginfo.getSuserCode());
        paydto.setSbookorgcode(loginfo.getSorgcode());
        paydto.setSgroupid(grpno);
        paydto.setDaccept(TimeFacade.getCurrentDateTime());
//        paydto.setSstatus("0");
        groupbean.setPayout(paydto);
        
        PageRequest grpRequest = new PageRequest();
        PageResponse resp = groupbean.retrieve(grpRequest);
        if(null == resp) {
        	return null;
        }
        editor.fireModelChanged();
        return "";
    }
    
	/**
	 * Direction: �����ύ ename: groupSubmit ���÷���: viewers: * messages:
	 */
    public String groupSubmit(Object o){
    	if(null == grpno || "".equals(grpno)) {
    		MessageDialog.openMessageDialog(null, "���������!");
    		return "";
    	}
    	TsCheckbalanceDto check = getcheckDto(); 
    	try {
			List l = commonDataAccessService.findRsByDtoUR(check);
			if(null == l || l.size() == 0) {
				MessageDialog.openMessageDialog(null, "����Ŷ�Ӧ��Ϣ�����ڻ����ύ!");
				return "";
			}
			TsCheckbalanceDto resdto = (TsCheckbalanceDto)l.get(0);
			
			if(!"1".equals(resdto.getSisbalance())) {
				MessageDialog.openMessageDialog(null, "������δƽ�⣬����У��ƽ��!");
				return "";
			}		
			boolean boo = confirmGroup();
			if(boo) {
				/**
				 * 1��ɾ��ʹ���������Ϣ
				 */
				TsCheckbalanceDto cedto = new TsCheckbalanceDto();
				cedto.setSgroupid(grpno);
				cedto.setSorgcode(loginfo.getSorgcode());
				cedto.setSusercode(loginfo.getSuserCode());
				fundIntoService.deleteData(cedto);
				/**
				 * 2���ÿս����ϵ�Ҫ��
				 *    ƽ��֮���ÿ���š��ܱ������ܽ��
				 */				
				setGrpno("");
				setTotalcount(null); //�ܱ���
				setTotalfamt(null);
				paysdto.setFamt(null);
				paysdto.setSpayeename(null);
				setPaylist(new ArrayList());
				setEndvou("");
				setSpayeecode("");
				setNewbudgcode(null);
				MessageDialog.openMessageDialog(null, "���������ύ�ɹ�!");
				editor.fireModelChanged();
				/**
				 * 3��ˢ�½��棬�������Ľ��㶨λ�ڡ�����������롯�����ϣ��������Ž���
				 */
				MVCUtils.setContentAreaFocus(editor, "�������");
				editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
						.getData(MetaDataConstants.META_DATA_KEY)).id, true,true);
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}    	
        return super.groupSubmit(o);
    }
    
    /**
	 * Direction: �Ƴ������� ename: wipeOutFromGroup ���÷���: viewers: * messages:
	 */
	public String wipeOutFromGroup(Object o) {
		if (null == groupSelect 
				|| "null".equals(groupSelect)
				|| null == groupSelect.getSbookorgcode()
				|| "".equals(groupSelect.getSbookorgcode())) {
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼!");
			return "";
		}
		if("1".equals(groupSelect.getSstatus())){
			MessageDialog.openMessageDialog(null, "�ü�¼�Ѿ��ύ�����Ƴ�!");
			return "";
		}
		groupSelect.setSgroupid(null);
		groupSelect.setSusercode(null);
		try {
			BizDataCountDto datacount = new BizDataCountDto();
			datacount.setBizname(grpno);
			datacount.setTotalfamt(groupSelect.getFamt());
			/**
			 * //���Ƚ����ݸ��³�ԭʼ���ݲ�������ȥ������
			 */
			String grpresult = fundIntoService.updateGroup(datacount, "", "", groupSelect,"wipe");
			/**
			 * ����ȡ���ݣ��൱��ˢ��
			 */
			queryGroup();
			groupSelect = null;
			editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.wipeOutFromGroup(o);
	}

	/**
	 * Direction: ѡ��һ������Ϣ ename: chooseOneGroup ���÷���: viewers: * messages:
	 */
	public String chooseOneGroup(Object o) {
		groupSelect = (TbsTvPayoutDto)o;
		return super.chooseOneGroup(o);
	}
    
	public String selectEvent(Object o) {
		List<TbsTvPayoutDto> mlist = (List<TbsTvPayoutDto>) selecteddatalist;
		totalmoney = new BigDecimal(0.00);
		totalnum = 0;
		for (TbsTvPayoutDto mdto : mlist) {
			totalmoney = totalmoney.add(mdto.getFamt());
			totalnum++;
		}
		this.editor.fireModelChanged();
		return super.selectEvent(o);
	}
	
	/**
	 * ������� ֱ���ύ���顤
	 */
	public boolean confirmGroup() throws ITFEBizException {
		TbsTvPayoutDto paramdto = new TbsTvPayoutDto();
		paramdto.setSbookorgcode(loginfo.getSorgcode()); //��������
		paramdto.setSgroupid(grpno); //���
		paramdto.setSusercode(loginfo.getSuserCode()); //�û���
		paramdto.setSstatus(StateConstant.CONFIRMSTATE_NO); //δ����
		List l = commonDataAccessService.findRsByDtoUR(paramdto);
		if(null == l || l.size() == 0) {
			return Boolean.FALSE;
		}else {
			for(Object obj : l) {
				IDto idto = (IDto)obj; 
				int result = uploadConfirmService.eachConfirm(BizTypeConstant.BIZ_TYPE_PAY_OUT, idto);
				if (StateConstant.SUBMITSTATE_DONE != result && StateConstant.SUBMITSTATE_SUCCESS != result) {
					return Boolean.FALSE;			
				}
			}
		}
		return Boolean.TRUE;		
	}

	public BigDecimal getTotalmoney() {
		return totalmoney;
	}

	public void setTotalmoney(BigDecimal totalmoney) {
		this.totalmoney = totalmoney;
	}

	public int getTotalnum() {
		return totalnum;
	}

	public void setTotalnum(int totalnum) {
		this.totalnum = totalnum;
	}

	public Integer getVouCount() {
		return vouCount;
	}

	public void setVouCount(Integer vouCount) {
		this.vouCount = vouCount;
	}

	public GroupQueryBean getGroupbean() {
		return groupbean;
	}

	public void setGroupbean(GroupQueryBean groupbean) {
		this.groupbean = groupbean;
	}

	public TbsTvPayoutDto getGroupSelect() {
		return groupSelect;
	}

	public void setGroupSelect(TbsTvPayoutDto groupSelect) {
		this.groupSelect = groupSelect;
	}

	public List getSelectedwaitlist() {
		return selectedwaitlist;
	}

	public void setSelectedwaitlist(List selectedwaitlist) {
		this.selectedwaitlist = selectedwaitlist;
	}

}
package com.cfcc.itfe.client.recbiz.fundinto;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.cfcc.itfe.client.common.encrypt.DecryptClientUtil;
import com.cfcc.itfe.client.common.file.ErrorNoteDialog;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.dialog.MessageInfoDialog;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
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
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 12-02-21 15:15:51 ��ϵͳ: RecBiz ģ��:fundInto ���:FundInto
 */
public class FundIntoBean extends AbstractFundIntoBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(FundIntoBean.class);
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
	private String sfilename;
	private List filelist;
	private int filecount=0;
	private int okcount=0;
	private int nocount=0;
	public FundIntoBean() {
		super();
		filepath = new ArrayList();
		filelist = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		filedata = new FileResultDto();
		searchdto = new TbsTvPayoutDto();
		paysdto = new TbsTvPayoutDto();
		paylist = new ArrayList<ResultDesDto>();
		trecode = "";
		searchdto.setDaccept(DateUtil.currentDate());
		defvou = "";
		endvou = "";
		newbudgcode = "";
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		area = loginfo.getArea();
		sfilename="";
		init();
	}
	private void init()
	{
		// �����������
		paysdto.setSbookorgcode(loginfo.getSorgcode());
		// δ����
		paysdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(TimeFacade.getCurrentDate());
			calendar.add(Calendar.DATE, -10);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// ʱ���ʽ
			String getdate = dateFormat.format(calendar.getTime());
			List qlist = commonDataAccessService.findRsByDtoWithWhere(paysdto, " and D_VOUCHER >='"+getdate+"'");
			if(qlist!=null&&qlist.size()>0)
			{
				Set<String> tmpTreSet = new HashSet<String>();
				TbsTvPayoutDto tmp = null;
				filelist = new ArrayList();
				TsTreasuryDto filemap = null;
				for(int i=0;i<qlist.size();i++)
				{
					tmp = (TbsTvPayoutDto)qlist.get(i);
					if(tmpTreSet.add(tmp.getStrecode()+tmp.getSfilename()))
					{
						filemap = new TsTreasuryDto();
						filemap.setStrecode(tmp.getStrecode()+"-"+tmp.getSfilename());
						filemap.setStrename(tmp.getStrecode()+"-"+tmp.getSfilename());
						filelist.add(filemap);
					}
				}
				
			}
		} catch (ITFEBizException e) {
			log.error("������Ų�ѯ�쳣��", e);
			MessageDialog.openErrorDialog(null, e);
		}
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
					// MessageDialog.openMessageDialog(null, noteInfo +
					// errorPartInfo.toString());
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
		// ��ȡ���¼�뷽ʽ����ģʽ
		if (StateConstant.SPECIAL_AREA_FUZHOU.equals(area)
				|| StateConstant.SPECIAL_AREA_GUANGDONG.equals(area)) {
			return super.openfjPayout(o);
		}
		return super.tozhubixh(o);
	}

	/**
	 * Direction: ֱ���ύ ename: submit ���÷���: viewers: * messages:
	 */
	public String submit(Object o) {
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_PAY_OUT, loginfo.getSorgcode())) {
				
				if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(area)) {
					List<TbsTvPayoutDto> retList = needGrantList(null, null);
					if (null != retList && retList.size() > 0) {// ������Ȩ
						String msg = "ϵͳ�ڵ���֧�������˻����˻����в�����,��Ҫ������Ȩ:\n";
						for (TbsTvPayoutDto tmp : retList) {
							msg = msg + tmp.getSpayeeacct() + "\n";
						}
						if (!AdminConfirmDialogFacade.open(msg)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							return null;
						}
					}
				}
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
		return super.goback(o);
	}

	/**
	 * Direction: ����ȷ�� ename: plsubmit ���÷���: viewers: * messages:
	 */
	public String plsubmit(Object o) {
		try {
			if (StateConstant.IfStopPayoutSubmit_true
					.equals(commonDataAccessService
							.getCacheInfo("IFSTOPPAYOUTSUBMIT"))) {
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
					if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(area)) {
						List<TbsTvPayoutDto> retList = needGrantList(dto
								.getSfilename(), null);
						if (null != retList && retList.size() > 0) {// ������Ȩ
							String msg = "ϵͳ�ڵ���֧�������˻����˻����в�����,��Ҫ������Ȩ:\n";
							for (TbsTvPayoutDto tmp : retList) {
								msg = msg + tmp.getSpayeeacct() + "\n";
							}
							if (!AdminConfirmDialogFacade.open(msg)) {
								DisplayCursor.setCursor(SWT.CURSOR_ARROW);
								return null;
							}
						}
					}
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
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫɾ���ļ�¼��");
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
					if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(area)) {
						List<TbsTvPayoutDto> retList = needGrantList(null, dto.getIvousrlno());
						if (null != retList && retList.size() > 0) {// ������Ȩ
							String msg = "ϵͳ�ڵ���֧�������˻����˻������в���,��Ҫ������Ȩ:\n";
							for (TbsTvPayoutDto tmp : retList) {
								msg = msg + tmp.getSpayeeacct() + "\n";
							}
							if (!AdminConfirmDialogFacade.open(msg)) {
								DisplayCursor.setCursor(SWT.CURSOR_ARROW);
								return null;
							}
						}
					}
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
		try {
			querylist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_PAY_OUT, paysdto);
			if (null == querylist || querylist.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "��ѯ�޼�¼!");
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

	public String delDetail2(Object o) {
		if (null == selecteddatalist || selecteddatalist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
			return null;
		} else {
			if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), "��ʾ",
					"�Ƿ�ȷ��ɾ����")) {

				try {
					for (int i = 0; i < selecteddatalist.size(); i++) {
						TbsTvPayoutDto dto = (TbsTvPayoutDto) selecteddatalist
								.get(i);
						if (!StateConstant.CONFIRMSTATE_YES.equals(dto
								.getSstatus())) {
							int result = uploadConfirmService.eachDelete(
									BizTypeConstant.BIZ_TYPE_PAY_OUT, dto);
							if (StateConstant.SUBMITSTATE_DONE == result
									|| StateConstant.SUBMITSTATE_SUCCESS == result)
								showdatalist.remove(dto);
							// �ж��ļ����Ƿ�ȫ�����ţ����ȫ�����ţ����÷��ͱ���
							uploadConfirmService.checkAndSendMsg(dto,
									MsgConstant.MSG_NO_5101, TbsTvPayoutDto
											.tableName(), null);
						}
					}
					totalmoney = new BigDecimal(0.00);
					totalnum = 0;
					for (int i = 0; i < showdatalist.size(); i++) {
						TbsTvPayoutDto dto = (TbsTvPayoutDto) showdatalist
								.get(i);
						totalmoney = totalmoney.add(dto.getFamt());
						totalnum++;
					}
					selecteddatalist = new ArrayList();
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "��ϸɾ���ɹ���");
					editor.fireModelChanged();
				} catch (ITFEBizException e) {
					log.error("��ѯ��ʱ��δ���ż�¼����", e);
					MessageDialog.openErrorDialog(null, e);
				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			}
		}
		return super.delDetail2(o);
	}

	public String setFail2(Object o) {
		if (null == selecteddatalist || selecteddatalist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
			return null;
		} else {
			if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), "��ʾ",
					"�Ƿ�ȷ�����Ŵ�����")) {
				try {
					// �ж��ļ����Ƿ�ȫ�����ţ����ȫ�����ţ����÷��ͱ���
					for (int i = 0; i < selecteddatalist.size(); i++) {
						TbsTvPayoutDto dto = (TbsTvPayoutDto) selecteddatalist
								.get(i);
						if (!StateConstant.CONFIRMSTATE_YES.equals(dto
								.getSstatus())) {

							uploadConfirmService.setFail(
									MsgConstant.MSG_NO_5101, dto);

							uploadConfirmService.checkAndSendMsg(dto,
									MsgConstant.MSG_NO_5101, TbsTvPayoutDto
											.tableName(), null);
						} else {
							MessageDialog.openMessageDialog(null,
									"��ƾ֤�����ţ��������ó����Ŵ�����");
						}
					}
					selecteddatalist = new ArrayList();
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "�������Ŵ����ɹ���");
					this.zbsearch(o);
					editor.fireModelChanged();
				} catch (ITFEBizException e) {
					log.error("��ѯ��ʱ��δ���ż�¼����", e);
					MessageDialog.openErrorDialog(null, e);
				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);

			}
		}
		editor.fireModelChanged();
		return super.setFail2(o);
	}

	/**
	 * Direction: ����ʧ�� ename: setFail ���÷���: viewers: * messages:
	 */
	public String setFail(Object o) {
		if (null == _dto || null == _dto.getIvousrlno()) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�������Ŵ����ļ�¼��");
		} else {
			if (!StateConstant.CONFIRMSTATE_YES.equals(_dto.getSstatus())) {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				try {
					// �趨���Ŵ���״̬
					uploadConfirmService.setFail(MsgConstant.MSG_NO_5101, _dto);
					// �ж��ļ����Ƿ�ȫ�����ţ����ȫ�����ţ����÷��ͱ���
					uploadConfirmService.checkAndSendMsg(_dto,
							MsgConstant.MSG_NO_5101,
							TbsTvPayoutDto.tableName(), _dto.getIvousrlno());
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "�������Ŵ����ɹ���");
					_dto = null;
					querylist = uploadConfirmService.eachQuery(
							BizTypeConstant.BIZ_TYPE_PAY_OUT, paysdto);
					if (null == querylist || querylist.size() == 0) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "��ѯ�޼�¼!");
						return null;
					}
					editor.fireModelChanged();
				} catch (ITFEBizException e) {
					log.error("��ѯ��ʱ��δ���ż�¼����", e);
					MessageDialog.openErrorDialog(null, e);
				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			} else {
				MessageDialog.openMessageDialog(null, "��ƾ֤�����ţ��������ó����Ŵ�����");
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
		if (null == _dto || null == _dto.getIvousrlno()) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫɾ���ļ�¼��");
		} else {

			if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), "��ʾ",
					"�Ƿ�ȷ��ɾ���ü�¼��")) {
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
								MsgConstant.MSG_NO_5101, TbsTvPayoutDto
										.tableName(), null);
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "��ϸɾ���ɹ���");
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
		}
		return super.delDetail(o);

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

	/**
	 * ������Ҫ������Ȩ�ļ�¼���б�
	 * 
	 * @throws ITFEBizException
	 */
	private List<TbsTvPayoutDto> needGrantList(String filename, Long ivousrlno)
			throws ITFEBizException {
		TbsTvPayoutDto _dto = new TbsTvPayoutDto();
		_dto.setSbookorgcode(loginfo.getSorgcode());
		if (null == filename && null == ivousrlno) {// ֱ���ύ
			_dto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			_dto.setSgroupid(StateConstant.COMMON_YES);
		} else if (null == filename) {// �������
			_dto.setIvousrlno(ivousrlno);
			_dto.setSgroupid(StateConstant.COMMON_YES);
		} else if (null == ivousrlno) {// ��������
			_dto.setSfilename(filename);
			_dto.setSgroupid(StateConstant.COMMON_YES);
		}
		return commonDataAccessService.findRsByDto(_dto);

	}
	public List getFilelist() {
		return filelist;
	}

	public void setFilelist(List filelist) {
		this.filelist = filelist;
	}

	public int getFilecount() {
		return filecount;
	}

	public void setFilecount(int filecount) {
		this.filecount = filecount;
	}

	public int getOkcount() {
		return okcount;
	}

	public void setOkcount(int okcount) {
		this.okcount = okcount;
	}

	public int getNocount() {
		return nocount;
	}

	public void setNocount(int nocount) {
		this.nocount = nocount;
	}
	public String getSfilename() {
		return sfilename;
	}
	public void setSfilename(String sfilename) {
		this.sfilename = sfilename;
		if(sfilename!=null&&sfilename.contains("-"))
		{
			String trecode = sfilename.split("-")[0];
			String filename = sfilename.split("-")[1];
			TvFilepackagerefDto findcount = new TvFilepackagerefDto();
			findcount.setStrecode(trecode);
			findcount.setSfilename(filename);
			try {
				List<TvFilepackagerefDto> findlist = commonDataAccessService.findRsByDto(findcount);
				if(findlist!=null&&findlist.size()>0)
				{
					filecount =0;
					for(TvFilepackagerefDto tempdto:findlist)
						filecount+=tempdto.getIcount();
					TbsTvPayoutDto pdto = new TbsTvPayoutDto();
					pdto.setStrecode(trecode);
					pdto.setSfilename(filename);
					pdto.setSbookorgcode(loginfo.getSorgcode());
					pdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					List<TbsTvPayoutDto> relist = commonDataAccessService.findRsByDto(pdto);
					nocount = relist!=null?relist.size():0;
					okcount = filecount - nocount;
				}
			} catch (ITFEBizException e) {
			}
			editor.fireModelChanged();
		}
	}
	/**
	 * Direction: ȫѡ ename: selectall ���÷���: viewers: * messages:
	 */
	public String pldselectall(Object o) {
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			selectedfilelist = new ArrayList();
			selectedfilelist.addAll(showfilelist);
		} else {
			selectedfilelist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.pldselectall(o);
	}
}
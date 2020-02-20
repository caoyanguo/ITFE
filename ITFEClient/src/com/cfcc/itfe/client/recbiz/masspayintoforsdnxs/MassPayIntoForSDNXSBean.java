package com.cfcc.itfe.client.recbiz.masspayintoforsdnxs;

import java.io.File;
import java.math.BigDecimal;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.ChinaTest;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.BursarAffirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.PiLiangDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 12-02-21 15:15:51 ��ϵͳ: RecBiz ģ��:massPayInto ���:MassPayInto
 */
@SuppressWarnings("unchecked")
public class MassPayIntoForSDNXSBean extends AbstractMassPayIntoForSDNXSBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(MassPayIntoForSDNXSBean.class);
	private IUploadConfirmService uploadConfirmService = (IUploadConfirmService) getService(IUploadConfirmService.class);
	private ITFELoginInfo loginfo;
	private BigDecimal sumamt;
	// ƾ֤�ܱ���
	private Integer vouCount;
	// ��������б�
	private List<TsTreasuryDto> treList;
	//����������һ��������������ɽ���������񷢷�
	private String type;
	
	public MassPayIntoForSDNXSBean() {
		super();
		filepath = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		selectedftpfilelist = new ArrayList();
		selectftpreturnlist = new ArrayList();
		filedata = new FileResultDto();
		orgcodelist = new ArrayList();
		searchdto = new TvPayoutfinanceDto();
		budgetype = StateConstant.BudgetType_IN;
		sdate = TimeFacade.getCurrentStringTime();
		type = "SDNXS";//ɽ���������񷢷�(ɽ��ũ����)
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		init();

	}

	private void init() {
		// ��ѯ���������dto
		TsTreasuryDto tredto = new TsTreasuryDto();
		// ���Ļ�������
		String centerorg = null;

		try {
			centerorg = StateConstant.ORG_CENTER_CODE;
			// ���Ļ�����ȡ�����й����б�
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// �����Ļ�����ȡ�õ�¼������Ӧ����
			else {
				tredto = new TsTreasuryDto();
				tredto.setSorgcode(loginfo.getSorgcode());
				treList = commonDataAccessService.findRsByDto(tredto);
			}

			// ��ʼ���������Ĭ��ֵ
			if (treList.size() > 0) {
				trecode = treList.get(0).getStrecode();
				ftptrecode = treList.get(0).getStrecode();
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
	}

	/**
	 * Direction: ���ݼ��� ename: dateload ���÷���: viewers: * messages:
	 */
	public String dateload(Object o) {
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		Map tmpMap = new HashMap();
		String interfacetype = ""; // �ӿ�����
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		TsBudgetsubjectDto tsBudgetsubject = new TsBudgetsubjectDto();
		tsBudgetsubject.setSorgcode(loginfo.getSorgcode());
		tsBudgetsubject.setSsubjectcode(funccode);
		try {
			List list = commonDataAccessService.findRsByDto(tsBudgetsubject);
			if (null == list || list.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "�������Ŀ����¼���������ʵ��");
				return null;
			}
			// �ж��Ƿ�ѡ���ļ�
			if (null == filepath || filepath.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
				return null;
			}
			Set<String> sets = new HashSet<String>();
			for (int i = 0; i < filepath.size(); i++) {
				String tmpname = ((File) filepath.get(i)).getName();
				String str = tmpname.substring(tmpname.lastIndexOf("."));
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
			// ��ȡ�ܳ�
			tmpMap = commonDataAccessService.getMiYao(loginfo.getSorgcode());
			if (null == tmpMap || tmpMap.keySet().size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "��ȡ��Կʧ�ܣ�");
				return null;
			}
			// ��¼txt�ļ���֤��
			Map<String, String> yzmMap = new HashMap<String, String>();
			List<File> yzmExistFileList = new ArrayList<File>(); // ��¼��֤���Ѿ����ڵ��ļ�
			String yzmExistPromptMsg = "";// ��¼��֤�������ʾ��Ϣ

			// ���ݼ���
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				String tmpfilename = tmpfile.getName().toLowerCase(); // �ļ�������
				String tmpfilepath = tmpfile.getAbsolutePath(); // ��ȡ�ļ���·��

				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}
				// �����ļ�����
				interfacetype = getFileObjByFileNameBiz(
						BizTypeConstant.BIZ_TYPE_BATCH_OUT, tmpfilename);
				// �ж��ļ��Ƿ��Ѿ�����
				if (commonDataAccessService.verifyImportRepeat(tmpfilename)) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, tmpfilename
							+ "�Ѿ����룬��ȷ�ϣ�");
					// ɾ���������ϴ�ʧ���ļ�
					DeleteServerFileUtil.delFile(commonDataAccessService,
							serverpathlist);
					return null;
				}
				// �ļ���Ϊpas�ļ���txt�ļ�
				if (tmpfilename.endsWith(".pas")) {
					if (ChinaTest.isChinese(tmpfilepath)) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "�ļ�·���к��֣������·����");
						// ɾ���������ϴ�ʧ���ļ�
						DeleteServerFileUtil.delFile(commonDataAccessService,
								serverpathlist);
						return null;
					}
					String miyao = (String) tmpMap.get(tmpfilename.substring(
							tmpfilename.length() - 16, tmpfilename
									.lastIndexOf(".")));
					if (null != miyao && miyao.length() > 0) {
						// ��Կ����
						miyao = commonDataAccessService.decrypt(miyao);
						if ("-1".equals(TipsFileDecrypt.decryptPassFile(
								tmpfilepath, tmpfilepath
										.replace(".pas", ".tmp"), miyao))) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null, tmpfilename
									+ "�ļ�����ʧ�ܣ�");
							// ɾ���������ϴ�ʧ���ļ�
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
						// �ϴ�ԭ�ļ�����¼��־
						String serverpath = ClientFileTransferUtil
								.uploadFile(tmpfile.getAbsolutePath());
						// �ϴ������ɹ��ļ�
						fileList.add(new File(serverpath));
						serverpathlist.add(serverpath);
						// ɾ����ʱ�ļ�
						FileUtil.getInstance().deleteFile(
								tmpfilepath.replace(".pas", ".tmp"));
						continue;
					} else {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "��Կû��ά��������ά����Կ��");
						// ɾ���������ϴ�ʧ���ļ�
						DeleteServerFileUtil.delFile(commonDataAccessService,
								serverpathlist);
						return null;
					}
				} else {
					String outorg = "";
					if (soutorgcode != null && !"".equals(soutorgcode)) {
						outorg = soutorgcode;
					} else {
						outorg = "aaaaaaaaaaaa";
					}
					String miyao = (String) tmpMap.get(outorg);
					if (null != miyao && miyao.length() > 0) {
						// ��Կ����
						miyao = commonDataAccessService.decrypt(miyao);
						String yzm = TipsFileDecrypt.checkSignFile(tmpfilepath,
								tmpfilepath.replaceAll(".txt", ".tmp"), miyao);
						if ("-1".equals(yzm)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null, tmpfilename
									+ "�ļ�����ʧ�ܣ�");
							// ɾ���������ϴ�ʧ���ļ�
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
						TvRecvlogDto recvlogDto = new TvRecvlogDto();
						recvlogDto.setSbatch(yzm);
						List<TvRecvlogDto> list2 = commonDataAccessService
								.findRsByDto(recvlogDto);
						if (list2.size() > 0) {
							// ��֤����ڣ��������ϴ��Ȳ�����������Ҫ��¼�Ѿ����ڵ���֤��
							yzmExistFileList.add(tmpfile);
							yzmExistPromptMsg += tmpfilename + "\r\n";
						}
						// �ϴ�ԭ�ļ�����¼��־
						String serverpath = ClientFileTransferUtil
								.uploadFile(tmpfile.getAbsolutePath());
						// �ϴ������ɹ��ļ�
						fileList.add(new File(serverpath));
						serverpathlist.add(serverpath);
						yzmMap.put(yzm, serverpath);
						// ɾ����ʱ�ļ�
						FileUtil.getInstance().deleteFile(
								tmpfilepath.replace(".txt", ".tmp"));
						continue;
					} else {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "��Կû��ά��������ά����Կ��");
						// ɾ���������ϴ�ʧ���ļ�
						DeleteServerFileUtil.delFile(commonDataAccessService,
								serverpathlist);
						return null;
					}

				}

			}
			// �����֤���Ѿ����ڣ�����ʾ�û��Ƿ���
			if (yzmExistFileList.size() > 0) {

				// ����Ȩ����
				String msg = "���������������ļ���ͬ����֤�룬��Ȩ����ܵ���\r\n" + yzmExistPromptMsg;

				if (!BursarAffirmDialogFacade.open(msg)) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "��Ȩ���ɹ������ܵ��룡");
					// ɾ���������ϴ�ʧ���ļ�
					DeleteServerFileUtil.delFile(commonDataAccessService,
							serverpathlist);
					return null;
				}
				;
			}

			// �����������ļ�
			fileResolveCommonService.loadFile(fileList,
					BizTypeConstant.BIZ_TYPE_BATCH_OUT, interfacetype,
					new PiLiangDto(trecode, budgetype, funccode, sbdgorgcode, type));
			Iterator<String> iterators = sets.iterator();
			if (".txt".equals(iterators.next())) {
				commonDataAccessService.saveMassTvrecvlog(yzmMap,
						BizTypeConstant.BIZ_TYPE_BATCH_OUT);
			} else {
				commonDataAccessService.saveTvrecvlog(serverpathlist,
						BizTypeConstant.BIZ_TYPE_BATCH_OUT);
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, "�ļ����سɹ�,���ι����سɹ� "
					+ fileList.size() + " ���ļ���");
		} catch (Exception e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			// ɾ���������ϴ�ʧ���ļ�
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,
						serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("ɾ���������ļ�ʧ�ܣ�", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			return null;
		}
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
		return super.tozhubixh(o);
	}

	/**
	 * Direction: ֱ���ύ ename: submit ���÷���: viewers: * messages:
	 */
	public String submit(Object o) {
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_BATCH_OUT, loginfo.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_BATCH_OUT);
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
				MessageDialog.openMessageDialog(null, "����ֱ���ύ���������Ա��ϵ��");
				return null;
			}
		} catch (ITFEBizException e) {
			log.error("ֱ���ύʧ�ܣ�", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
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
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
			return null;
		}
		try {
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
						+ sumamt.toString() + "]��ƾ֤�ܱ���[" + vouCount.toString()
						+ "]�������ļ����ܽ��["+selSumamt+"]��ƾ֤�ܱ���["+selvouCount+"]����,���֤!");
				return null;
			} else {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				massdispose();
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			}
		} catch (ITFEBizException e) {
			log.error("�����ύʧ�ܣ�", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
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
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ��?")) {
			try {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					int result = uploadConfirmService.batchDelete(
							BizTypeConstant.BIZ_TYPE_BATCH_OUT, dto);
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
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			try {
				for (int i = 0; i < selecteddatalist.size(); i++) {
					TvPayoutfinanceDto dto = (TvPayoutfinanceDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_BATCH_OUT, dto);
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
				log.error("�����ύʧ�ܣ�", e);
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
	// BizTypeConstant.BIZ_TYPE_BATCH_OUT, sumamt);
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
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		try {
			// �����������
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// δ����
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_BATCH_OUT, searchdto);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("������Ų�ѯʧ�ܣ�", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.zbsearch(o);
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

	/**
	 * Direction: ����ȫѡ ename: plselectall ���÷���: viewers: * messages:
	 */
	public String plselectall(Object o) {
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			selectedfilelist = new ArrayList();
			selectedfilelist.addAll(showfilelist);
		} else {
			selectedfilelist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.plselectall(o);
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
	private String getFileObjByFileNameBiz(String biztype, String filename)
			throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		if (tmpfilename_new.indexOf(".pas") > 0) {
			// �����������кţ�12λ��������(8λ)�������ļ���ţ�10λ������������(12λ)��.pas
			String tmpfilename = tmpfilename_new.replace(".pas", "");
			if (tmpfilename.length() == 42) {
				if (!commonDataAccessService.auditBankCode(tmpfilename
						.substring(0, 12)))
					throw new ITFEBizException("�������кŲ�����!");
				interficetype = MsgConstant.PILIANG_DAORU;// TBS�ӿ�����
			} else {
				throw new ITFEBizException("�ļ�����ʽ����!");
			}
		} else if (tmpfilename_new.indexOf(".txt") > 0) {
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			if (tmpfilename.length() == 30) {
				if (!commonDataAccessService.auditBankCode(tmpfilename
						.substring(0, 12)))
					throw new ITFEBizException("�������кŲ�����!");
				interficetype = MsgConstant.PILIANG_DAORU;// TBS�ӿ�����
			} else {
				throw new ITFEBizException("�ļ�����ʽ����!");
			}
		} else {
			throw new ITFEBizException("�ļ�����ʽ����!");
		}
		return interficetype;
	}

	public List<TsTreasuryDto> getTreList() {
		return treList;
	}

	public void setTreList(List<TsTreasuryDto> treList) {
		this.treList = treList;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public BigDecimal getSumamt() {
		return sumamt;
	}

	public void setSumamt(BigDecimal sumamt) {
//		DisplayCursor.setCursor(SWT.);
		if(null == vouCount || null == sumamt){
			MessageDialog.openMessageDialog(null, "ƾ֤�������ܽ���Ϊ�գ�");
			return ;
		}
		this.sumamt = sumamt;
		if (sumamt == null || sumamt.compareTo(new BigDecimal(0)) == 0) {
		} else {
			try {
				TvFilepackagerefDto refdto = new TvFilepackagerefDto();
				BigDecimal totalfamt = new BigDecimal("0.00");
				int totalnum = 0;
				showfilelist = new ArrayList();
				selectedfilelist = new ArrayList();
				showfilelist = uploadConfirmService.batchQuery(
						BizTypeConstant.BIZ_TYPE_BATCH_OUT, sumamt);
				if (null != showfilelist) {
					for(int i = 0; i <  showfilelist.size(); i++){
						refdto = (TvFilepackagerefDto) showfilelist.get(i);
						totalfamt= totalfamt.add(refdto.getNmoney());
						totalnum=totalnum+refdto.getIcount();
					}
					if(totalfamt.compareTo(sumamt) != 0 || vouCount != totalnum){
						MessageDialog.openMessageDialog(null, "������ܱ��� ["+vouCount+"]���ܽ��["
								+ sumamt + "]��δ����ƾ֤���ܱ������ܽ�����ݲ�һ��\n"
								+ "��ϸ��ʾ:����δ����ƾ֤�ܱ���Ϊ["+totalnum+"]�ܽ��Ϊ[" + totalfamt
								+ "],���� [" + showfilelist.size() + "]���ļ�");
					}
					selectedfilelist.addAll(showfilelist);
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}else if (showfilelist == null || showfilelist.size() <= 0) {
					MessageDialog.openMessageDialog(null, "û�з������������ݣ�");
				}
			} catch (Throwable e) {
				log.error("����ʧ�ܣ�", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
	}
	/**
	 * Direction: ��ȡftp�ļ�
	 * ename: readftp
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String readftp(Object o){
    	if(sdate==null||"".equals(sdate))
    	{
    		MessageDialog.openMessageDialog(null, "�������ڲ���Ϊ�գ�");
    		return null;
    	}
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "ȷ�϶�ȡFtp������������Ϊ"+sdate+"�����ݣ�")) {
	    	try{
	    		 uploadConfirmService.readFtpFile(sdate);
	    		 if(ftptrecode!=null&&!"".equals(ftptrecode))
	    	     {
		    		 TvBatchpayDto queryDto = new TvBatchpayDto();
		    		 queryDto.setSorgcode(loginfo.getSorgcode());
		    	     queryDto.setSdate(sdate);
		    	     if(sstatus==null||"".equals(sstatus))
			   	    	 sstatus = StateConstant.FTPFILESTATE_DOWNLOAD;
			   	     queryDto.setSstatus(sstatus);
			   	     queryDto.setStrecode(ftptrecode);
		    	     this.showftpfilelist = commonDataAccessService.findRsByDto(queryDto);
	    	     }
	    	     MessageDialog.openMessageDialog(null, "��ȡFtp������������ɡ�");
	    	     this.selectedftpfilelist = new ArrayList();
	    	     this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	    	} catch (ITFEBizException e) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if(e.getCause().getMessage().indexOf("�����Ѿ���ȡ")>=0)
					MessageDialog.openMessageDialog(null, "Ftp�����������Ѿ���ȡ���������ݡ�");
				else
					MessageDialog.openMessageDialog(null, "Ftp��������û�����ݡ�");
				return null;
			}
    	}else {
			return null;
		}
        return super.readftp(o);
    }
    /**
	 * Direction: ftp�����ļ�����
	 * ename: ftpsummarydown
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String ftpsummarydown(Object o){
    	if(selectedftpfilelist!=null&&selectedftpfilelist.size()>0)
    	{
    		TvBatchpayDto dto = null;
    		for (int i = 0; i < selectedftpfilelist.size(); i++) {
				dto = (TvBatchpayDto)selectedftpfilelist.get(i);
				if(!dto.getSstatus().equals(StateConstant.FTPFILESTATE_SUMMARY))
				{
					MessageDialog.openMessageDialog(null, "ѡ�е��ļ��б���״̬����Ϊ�����ļ���");
					return null;
				}
    		}
    		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
    		String strPath = path.open();
    		if ((null == strPath) || (strPath.length() == 0)) {
    			MessageDialog.openMessageDialog(null, "��ѡ�������ļ�����·����");
    			return "";
    		}
    		// �������и���
    		try {
    			uploadConfirmService.ftpfileadd(selectedftpfilelist);
        		for (int i = 0; i < selectedftpfilelist.size(); i++) {
    				dto = (TvBatchpayDto)selectedftpfilelist.get(i);
    				// ���ļ����ص�����Ŀ¼��
    				ClientFileTransferUtil.downloadFile("ITFEDATA"+"/"+loginfo.getSorgcode()+"/"+TimeFacade.getCurrentStringTime()+"/"+dto.getSfilename(), strPath + "/"
    						+ dto.getSfilename());
    			}
    			MessageDialog.openMessageDialog(null, "�����Ѿ����浽·��" + strPath + "�С�");
    		} catch (Throwable e) {
    			MessageDialog.openErrorDialog(null, e);
    		}
    	}else {
    		MessageDialog.openMessageDialog(null, "��ѡ����Ҫ���صĻ����ļ���");
    		return null;
		}
        return super.ftpsummarydown(o);
    }
    /**
	 * Direction: ��תftp�ļ��б�
	 * ename: goftpfilelist
	 * ���÷���: 
	 * viewers: ftp�����ļ�
	 * messages: 
	 */
    public String goftpfilelist(Object o){
    	sstatus = StateConstant.FTPFILESTATE_DOWNLOAD;
        return super.goftpfilelist(o);
    }
    /**
	 * Direction: ��ѯ��ȡ��ftp����
	 * ename: queryftpdata
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryftpdata(Object o){
    	if(sdate==null||"".equals(sdate))
    	{
    		MessageDialog.openMessageDialog(null, "�������ڲ���Ϊ�գ�");
    		return null;
    	}
    	if(ftptrecode==null||"".equals(ftptrecode))
    	{
    		MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
    		return null;
    	}
    	if(sstatus==null||"".equals(sstatus))
    	{
    		MessageDialog.openMessageDialog(null, "״̬����Ϊ�գ�");
    		return null;
    	}
    	try{
	   		 TvBatchpayDto queryDto = new TvBatchpayDto();
	   		 queryDto.setSorgcode(loginfo.getSorgcode());
	   	     queryDto.setSdate(sdate);
	   	     queryDto.setStrecode(ftptrecode);
	   	     if(sstatus==null||"".equals(sstatus))
	   	    	 sstatus = StateConstant.FTPFILESTATE_DOWNLOAD;
	   	     queryDto.setSstatus(sstatus);
	   	     this.showftpfilelist = commonDataAccessService.findRsByDto(queryDto);
	   	     if(showftpfilelist==null||showftpfilelist.size()<=0)
	   	    	MessageDialog.openMessageDialog(null, "��ѯ���ݽ��Ϊ�գ�");
	   	  selectedftpfilelist = new ArrayList();
	   	  this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    	} catch (ITFEBizException e) {
			 DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			 MessageDialog.openErrorDialog(null, e);
			
			return null;
		}
    	return super.queryftpdata(o);
    }
    /**
	 * Direction: ftp�ļ�����
	 * ename: ftpfileadd
	 * ���÷���: 
	 * viewers: ftp�����ļ�
	 * messages: 
	 */
    public String ftpfileadd(Object o){
    	if(selectedftpfilelist!=null&&selectedftpfilelist.size()>0)
    	{
    		TvBatchpayDto dto = null;
    		Set<String> sets = new HashSet<String>();
    		for (int i = 0; i < selectedftpfilelist.size(); i++) {
				dto = (TvBatchpayDto)selectedftpfilelist.get(i);
				if(!dto.getSstatus().equals(StateConstant.FTPFILESTATE_DOWNLOAD))
				{
					MessageDialog.openMessageDialog(null, "ѡ�е��ļ��б���״̬����Ϊ�Ѷ�ȡ��");
					return null;
				}
				String tmpname = dto.getSfilename();
				String str = tmpname.substring(tmpname.lastIndexOf("."));
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
    		if(!dto.getStrecode().equals(trecode))
			{
				if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
						.getCurrentComposite().getShell(), "��ʾ", "�ļ����еĹ���Ͳ�¼��ѡ���ⲻһ�£�ȷ���ύ��")) {
					
				}else
					return null;
			}
    		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
    		Map tmpMap = new HashMap();
    		String interfacetype = ""; // �ӿ�����
    		List<File> fileList = new ArrayList<File>();
    		List<String> serverpathlist = new ArrayList<String>();
    		TsBudgetsubjectDto tsBudgetsubject = new TsBudgetsubjectDto();
    		tsBudgetsubject.setSorgcode(loginfo.getSorgcode());
    		tsBudgetsubject.setSsubjectcode(funccode);
    		try {
    			List list = commonDataAccessService.findRsByDto(tsBudgetsubject);
    			if (null == list || list.size() == 0) {
    				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    				MessageDialog.openMessageDialog(null, "�������Ŀ����¼���������ʵ��");
    				return null;
    			}
    			// �ж��Ƿ�ѡ���ļ�
    			if (null == selectedftpfilelist || selectedftpfilelist.size() == 0) {
    				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
    				return null;
    			}
    			// ��ȡ�ܳ�
    			tmpMap = commonDataAccessService.getMiYao(loginfo.getSorgcode());
    			if (null == tmpMap || tmpMap.keySet().size() == 0) {
    				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    				MessageDialog.openMessageDialog(null, "��ȡ��Կʧ�ܣ�");
    				return null;
    			}
    			// ��¼txt�ļ���֤��
    			Map<String, String> yzmMap = new HashMap<String, String>();
    			List<File> yzmExistFileList = new ArrayList<File>(); // ��¼��֤���Ѿ����ڵ��ļ�
    			String yzmExistPromptMsg = "";// ��¼��֤�������ʾ��Ϣ
    			Map<String,TvBatchpayDto> idtoMap = new HashMap<String,TvBatchpayDto>();
    			// ���ݼ���
    			File tempFile = null;
    			for (int i = 0; i < selectedftpfilelist.size(); i++) {
    				dto = (TvBatchpayDto)selectedftpfilelist.get(i);
    				String tmpfilename = dto.getSfilename(); // �ļ�������
    				String tmpfilepath = dto.getSfilepath(); // ��ȡ�ļ���·��
    				// �����ļ�����
    				interfacetype = getFileObjByFileNameBiz(
    						BizTypeConstant.BIZ_TYPE_BATCH_OUT, tmpfilename);
    				// �ж��ļ��Ƿ��Ѿ�����
    				if (commonDataAccessService.verifyImportRepeat(tmpfilename)) {
    					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    					MessageDialog.openMessageDialog(null, tmpfilename
    							+ "�Ѿ����룬��ȷ�ϣ�");
    					// ɾ���������ϴ�ʧ���ļ�
    					DeleteServerFileUtil.delFile(commonDataAccessService,
    							serverpathlist);
    					return null;
    				}
    				// �ļ���Ϊpas�ļ���txt�ļ�
    				if (tmpfilename.endsWith(".txt")) {
    					String outorg = "";
    					if (soutorgcode != null && !"".equals(soutorgcode)) {
    						outorg = soutorgcode;
    					} else {
    						outorg = "aaaaaaaaaaaa";
    					}
    					String miyao = (String) tmpMap.get(outorg);
    					if (null != miyao && miyao.length() > 0) {
    						// ��Կ����
    						miyao = commonDataAccessService.decrypt(miyao);
    						String yzm =null;
    						dto.setSkey(miyao);//��ǩ��Կ��ִ��
    						Map getMap =uploadConfirmService.checkSignFileForSd(tmpfilepath+tmpfilename, tmpfilepath+tmpfilename.replaceAll(".txt", ".tmp"), miyao);
    						yzm = String.valueOf(getMap.get("yzm"));
    						if(getMap.get("newfilename")!=null)
    						{
    							serverpathlist.add(String.valueOf(getMap.get("newfilename")));
    							tempFile = new File(String.valueOf(getMap.get("newfilename")));
    							fileList.add(tempFile);
    							idtoMap.put(tempFile.getName(), dto);
    						}
    						if ("-1".equals(yzm)) {
    							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    							MessageDialog.openMessageDialog(null, tmpfilename+ "�ļ�����ʧ�ܣ�");
    							// ɾ���������ϴ�ʧ���ļ�
    							DeleteServerFileUtil.delFile(commonDataAccessService, serverpathlist);
    							return null;
    						}
    						TvRecvlogDto recvlogDto = new TvRecvlogDto();
    						recvlogDto.setSbatch(yzm);
    						List<TvRecvlogDto> list2 = commonDataAccessService.findRsByDto(recvlogDto);
    						if (list2.size() > 0) {
    							// ��֤����ڣ��������ϴ��Ȳ�����������Ҫ��¼�Ѿ����ڵ���֤��
    							yzmExistFileList.add(new File(tmpfilename));
    							yzmExistPromptMsg += tmpfilename + "\r\n";
    						}
    						// �ϴ�ԭ�ļ�����¼��־
    						if(serverpathlist!=null&&serverpathlist.size()==1)
    							yzmMap.put(yzm, serverpathlist.get(0));
    						else
    							yzmMap.put(yzm, serverpathlist.get(serverpathlist.size()-1));
    						continue;
    					} else {
    						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    						MessageDialog.openMessageDialog(null, "��Կû��ά��������ά����Կ��");
    						// ɾ���������ϴ�ʧ���ļ�
    						DeleteServerFileUtil.delFile(commonDataAccessService,
    								serverpathlist);
    						return null;
    					}
    				}
    			}
    			// �����֤���Ѿ����ڣ�����ʾ�û��Ƿ���
    			if (yzmExistFileList.size() > 0) {

    				// ����Ȩ����
    				String msg = "���������������ļ���ͬ����֤�룬��Ȩ����ܵ���\r\n" + yzmExistPromptMsg;

    				if (!BursarAffirmDialogFacade.open(msg)) {
    					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    					MessageDialog.openMessageDialog(null, "��Ȩ���ɹ������ܵ��룡");
    					// ɾ���������ϴ�ʧ���ļ�
    					DeleteServerFileUtil.delFile(commonDataAccessService,
    							serverpathlist);
    					return null;
    				}
    				;
    			}
    			// �����������ļ�
    			fileResolveCommonService.loadFile(fileList,
    					BizTypeConstant.BIZ_TYPE_BATCH_OUT, interfacetype,
    					new PiLiangDto(trecode, budgetype, funccode, sbdgorgcode, "ftppljz",idtoMap));
    			Iterator<String> iterators = sets.iterator();
    			if (".txt".equals(iterators.next())) {
    				commonDataAccessService.saveMassTvrecvlog(yzmMap,
    						BizTypeConstant.BIZ_TYPE_BATCH_OUT);
    			} else {
    				commonDataAccessService.saveTvrecvlog(serverpathlist,
    						BizTypeConstant.BIZ_TYPE_BATCH_OUT);
    			}
    			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    			MessageDialog.openMessageDialog(null, "�ļ����سɹ�,���ι����سɹ� "
    					+ fileList.size() + " ���ļ���");
    		} catch (Exception e) {
    			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    			MessageDialog.openErrorDialog(null, e);
    			// ɾ���������ϴ�ʧ���ļ�
    			try {
    				DeleteServerFileUtil.delFile(commonDataAccessService,
    						serverpathlist);
    			} catch (ITFEBizException e1) {
    				log.error("ɾ���������ļ�ʧ�ܣ�", e1);
    				MessageDialog.openErrorDialog(null, e);
    			}
    			queryftpdata(o);
        		this.editor.fireModelChanged();
    			return null;
    		}
    		filepath = new ArrayList();
    		queryftpdata(o);
    		this.editor.fireModelChanged();
    	}else {
    		MessageDialog.openMessageDialog(null, "��ѡ����Ҫ���ص��ļ���");
    		return null;
		}
        return super.ftpfileadd(o);
    }
    
	/**
	 * Direction: ftp�ļ�ȫѡ
	 * ename: ftpfileselectall
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String ftpfileselectall(Object o){
    	if (null == selectedftpfilelist || selectedftpfilelist.size() == 0) {
			selectedftpfilelist = new ArrayList();
			selectedftpfilelist.addAll(showftpfilelist);
		} else {
			selectedftpfilelist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.ftpfileselectall(o);
    }
    /**
	 * Direction: ȫѡ/��ѡ
	 * ename: selectallreturn
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectallreturn(Object o){
        if(null==this.selectftpreturnlist||this.selectftpreturnlist.size()==0)
        {
        	this.selectftpreturnlist = new ArrayList();
        	this.selectftpreturnlist.addAll(this.showftpreturnlist);
        }
        else
        	this.selectftpreturnlist.clear();
        this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.selectallreturn(o);
    }
    /**
	 * Direction: ��תftp��ִ�б�
	 * ename: goftpreturn
	 * ���÷���: 
	 * viewers: ftp�ļ���ִ
	 * messages: 
	 */
    public String goftpreturn(Object o){
//    	sdate = TimeFacade.getCurrentStringTime();
//    	queryftpreturn(o);
        return super.goftpreturn(o);
    }
    
	/**
	 * Direction: ��ѯftp��ִ�б�
	 * ename: queryftpreturn
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryftpreturn(Object o){
    	TvBatchpayDto querydto = new TvBatchpayDto();
    	if(sdate==null||"".equals(sdate))
    		sdate = TimeFacade.getCurrentStringTime();
    	querydto.setSdate(sdate);
    	if(ftptrecode!=null&&!"".equals(ftptrecode))
    		querydto.setStrecode(ftptrecode);
    	else
    	{
    		MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
    		return null;
    	}
    	try {
			this.showftpreturnlist = uploadConfirmService.queryFtpReturnFileList(querydto);
			if(this.showftpreturnlist==null||this.showftpreturnlist.size()<=0)
				MessageDialog.openMessageDialog(null, "��ѯ���ݽ��Ϊ�գ�");
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "��ѯ�쳣��ԭ��"+e.toString());
		}
    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.queryftpreturn(o);
    }
    
	/**
	 * Direction: ftp�ļ���ִ����
	 * ename: ftpreturnsend
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String ftpreturnsend(Object o){
    	if(this.selectftpreturnlist==null||this.selectftpreturnlist.size()<=0)
    	{
    		MessageDialog.openMessageDialog(null, "��ѡ����Ҫ��ִ���ļ��б�");
    		return null;
    	}
    	try {
			uploadConfirmService.sendReturnToFtp(this.selectftpreturnlist);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "��ִ�쳣��ԭ��"+e.toString());
		}
		MessageDialog.openMessageDialog(null, "��ִ�ɹ���");
    	queryftpreturn(o);
    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.ftpreturnsend(o);
    }
	private void massdispose() throws ITFEBizException {

		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.batchConfirm(
						BizTypeConstant.BIZ_TYPE_BATCH_OUT, dto);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result)
					showfilelist.remove(dto);

			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "����ɹ���");
		}
	}

	public Integer getVouCount() {
		return vouCount;
	}

	public void setVouCount(Integer vouCount) {
		this.vouCount = vouCount;
	}

	
}
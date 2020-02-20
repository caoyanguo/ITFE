package com.cfcc.itfe.client.recbiz.returnkuinto;

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
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.ChinaTest;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 12-02-21 15:15:51 ��ϵͳ: RecBiz ģ��:returnKuInto ���:ReturnKuInto
 */
public class ReturnKuIntoBean extends AbstractReturnKuIntoBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(ReturnKuIntoBean.class);
	private ITFELoginInfo loginfo;
	private BigDecimal sumamt;
	// ƾ֤�ܱ���
	private Integer vouCount;
	private Date adjustdate;
	private Date systemDate;
	private TbsTvDwbkDto _dto;;
	private BigDecimal totalmoney = new BigDecimal(0.00);
	private int totalnum = 0;
	private String area;

	public ReturnKuIntoBean() {
		super();
		filepath = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		filedata = new FileResultDto();
		searchdto = new TbsTvDwbkDto();
		searchdto.setDaccept(DateUtil.currentDate());
		dwbklist = new ArrayList<ResultDesDto>();
		dwbkdto = new TbsTvDwbkDto();
		defvou = "";
		endvou = "";
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		area = loginfo.getArea();
	}

	/**
	 * Direction: ���ݼ��� ename: dateload ���÷���: viewers: * messages:
	 */
	public String dateload(Object o) {
		Map tmpMap = new HashMap();
		String interfacetype = ""; // �ӿ�����
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer errorPartInfo = new StringBuffer("");
		StringBuffer errorTotalInfo = new StringBuffer("");
		int errorFileCount = 0;
		// �ж��Ƿ�ѡ���ļ�
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return null;
		}
		try {
			// �ж�ѡ���ļ��Ƿ���pas��txt�ļ��������pas�ļ�������txt�ļ�����txt�ļ���һ����pas�ļ�
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			boolean bool = false;
			Set<String> sets = new HashSet<String>();
			for (int i = 0; i < filepath.size(); i++) {
				String tmpname = ((File) filepath.get(i)).getName();
				String str = tmpname.toLowerCase().substring(
						tmpname.lastIndexOf("."));
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
			String str = commonDataAccessService.getSysDBDate();
			systemDate = Date.valueOf(str.substring(0, 4) + "-"
					+ str.substring(4, 6) + "-" + str.substring(6, 8));
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
						BizTypeConstant.BIZ_TYPE_RET_TREASURY, tmpfilename,
						bool);
				// �ж��ļ��Ƿ��Ѿ�����
				if (commonDataAccessService.verifyImportRepeat(tmpfilename)
						&& bool) {
					errorFileCount++;
					if (errorFileCount < 15) {
						errorPartInfo.append("�˿��ļ�[" + tmpfilename
								+ "]У��Ϊ�ظ�����!\r\n");
						errorTotalInfo.append("�˿��ļ�[" + tmpfilename
								+ "]У��Ϊ�ظ�����!\r\n");
					} else {
						errorTotalInfo.append("�˿��ļ�[" + tmpfilename
								+ "]У��Ϊ�ظ�����!\r\n");
					}
					continue;
				}
				// ����ǲ����ӿ�
				if (MsgConstant.TUIKU_FUJIAN_DAORU.equals(interfacetype)) {
					// ��ǩ
					if (!TipsFileDecrypt.verifyCA(tmpfilepath)) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "��֤�ļ�����");
						// ɾ���������ϴ�ʧ���ļ�
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

				// ����ļ���pas�ļ�
				if (bool) {
					if (tmpfilename.endsWith(".pas")) {
						if (ChinaTest.isChinese(tmpfilepath)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null,
									"�ļ�·���к��֣������·����");
							// ɾ���������ϴ�ʧ���ļ�
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
						String miyao = (String) tmpMap.get(tmpfilename
								.substring(tmpfilename.length() - 16,
										tmpfilename.lastIndexOf(".")));
						if (null != miyao && miyao.length() > 0) {
							// ��Կ����
							miyao = commonDataAccessService.decrypt(miyao);
							if ("-1".equals(TipsFileDecrypt.decryptPassFile(
									tmpfilepath, tmpfilepath.replaceAll(".pas",
											".tmp"), miyao))) {
								DisplayCursor.setCursor(SWT.CURSOR_ARROW);
								MessageDialog.openMessageDialog(null,
										tmpfilename + "�ļ�����ʧ�ܣ�");
								// ɾ���������ϴ�ʧ���ļ�
								DeleteServerFileUtil
										.delFile(commonDataAccessService,
												serverpathlist);
								return null;
							}
							// �ϴ�ԭ�ļ�
							String serverpath = ClientFileTransferUtil
									.uploadFile(tmpfile.getAbsolutePath());
							// �ϴ������ļ�
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
							// ɾ���������ϴ�ʧ���ļ�
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
					}
				}
				// �ļ��ϴ�
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			// �����������ļ�
			String errName = "";
			if (null != fileList && fileList.size() > 0) {
				MulitTableDto bizDto = fileResolveCommonService.loadFile(
						fileList, BizTypeConstant.BIZ_TYPE_RET_TREASURY,
						interfacetype, null);
				int tmp_errcount = errorFileCount;
				if (null != bizDto.getErrorList()
						&& bizDto.getErrorList().size() > 0) {
					for (int m = 0; m < bizDto.getErrorList().size(); m++) {
						tmp_errcount++;
						if (tmp_errcount < 15) {
							errorPartInfo.append(bizDto.getErrorList().get(m)
									.substring(2)
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
						+ "�˿��ļ����������Ϣ("
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
						BizTypeConstant.BIZ_TYPE_RET_TREASURY);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if (errorPartInfo.toString().trim().length() > 0) {
					String noteInfo = "��������" + filepath.size() + "���ļ���������"
							+ (errorFileCount + bizDto.getErrorCount())
							+ "�������ļ���������Ϣ���¡���ϸ������Ϣ��鿴" + errName + "����\r\n";
					MessageDialog.openMessageDialog(null, noteInfo
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
		if (StateConstant.SPECIAL_AREA_FUZHOU.equals(area)
				|| StateConstant.SPECIAL_AREA_GUANGDONG.equals(area)) { // ��ȡ����ģʽ
			return super.opendwbkfj(o);
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
					BizTypeConstant.BIZ_TYPE_RET_TREASURY, loginfo
							.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_RET_TREASURY);
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
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		} catch (ITFEBizException e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			log.error("ֱ���ύʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.submit(o);
	}

	/**
	 * Direction: ����Ĭ�Ͻ��� ename: goback ���÷���: viewers: �˿⵼�� messages:
	 */
	public String goback(Object o) {
		return super.goback(o);
	}

	/**
	 * Direction: ����ȷ�� ename: plsubmit ���÷���: viewers: * messages:
	 */
	public String plsubmit(Object o) {

		// ��������
		try {
			if (commonDataAccessService.judgeBatchConfirm(
					BizTypeConstant.BIZ_TYPE_RET_TREASURY, loginfo
							.getSorgcode())) {
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
			}

		} catch (ITFEBizException e) {
			log.error("�����ύʧ�ܣ�", e);
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
							BizTypeConstant.BIZ_TYPE_RET_TREASURY, dto);
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
					TbsTvDwbkDto dto = (TbsTvDwbkDto) selecteddatalist.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_RET_TREASURY, dto);
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
	// BizTypeConstant.BIZ_TYPE_RET_TREASURY, sumamt);
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
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		try {
			// �����������
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// δ����
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_RET_TREASURY, searchdto);
			// ͳ�Ʊ������
			List<TbsTvDwbkDto> mlist = (List<TbsTvDwbkDto>) showdatalist;
			totalmoney = new BigDecimal(0.00);
			totalnum = 0;
			for (TbsTvDwbkDto mdto : mlist) {
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

	/**
	 * Direction: ��ѯ���� ename: queryService ���÷���: viewers: ��ѯ���� messages:
	 */
	public String queryService(Object o) {
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			// �����������
			dwbkdto.setSbookorgcode(loginfo.getSorgcode());
			// δ����
			dwbkdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			if (null == defvou) {
				defvou = "";
			}
			if (null == endvou) {
				endvou = "";
			}
			dwbkdto.setSpayertrecode(trecode);
			dwbkdto.setSdwbkvoucode(defvou.trim() + endvou.trim());
			querylist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_RET_TREASURY, dwbkdto);
			if (null == querylist || querylist.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "��ѯ�޼�¼!");
				return null;
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("������Ų�ѯʧ�ܣ�", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.queryService(o);
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

	// �����ļ�����
	// /////////////////////////////////////////////////////////////////////////////////////////////
	private String getFileObjByFileNameBiz(String biztype, String filename,
			boolean bool) throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();
		// �����˿� ���������ţ�����ļ���
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
					interficetype = MsgConstant.TUIKU_XIAMEN_DAORU;// tbs�ӿ�
				} else {
					interficetype = MsgConstant.TUIKU_SHANDONG_DAORU; // �����ӿ�����
				}
				if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(fileobj
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
				if ("f".equals(tmpfilename.substring(19, 20))) {
					interficetype = MsgConstant.TUIKU_FUJIAN_DAORU;
				} else {
					throw new ITFEBizException("�ļ�����ʽ����!");
				}
			} else {
				throw new ITFEBizException("�ļ�����ʽ����!");
			}
		} else if (tmpfilename_new.indexOf(".pas") > 0) {
			String tmpfilename = tmpfilename_new.replace(".pas", "");
			if (tmpfilename.length() == 42) {
				// TBS�ӿڣ������������кţ�12λ��������(8λ)�������ļ���ţ�10λ������������(12λ,���ݸ���Ϣȡ��Կ)��.pas��
				if (!commonDataAccessService.auditBankCode(tmpfilename
						.substring(0, 12)))
					throw new ITFEBizException("�����кŲ�����!");
				interficetype = MsgConstant.TUIKU_XIAMEN_DAORU; // �˿⵼������
			} else {
				throw new ITFEBizException("�ļ�����ʽ����!");
			}
		} else {
			throw new ITFEBizException("�ļ�����ʽ����!");
		}
		return interficetype;

		// ҵ��������Ҫ�Ķ�
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
						BizTypeConstant.BIZ_TYPE_RET_TREASURY, sumamt);
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

	private void massdispose() throws ITFEBizException {
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.batchConfirm(
						BizTypeConstant.BIZ_TYPE_RET_TREASURY, dto);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result)
					showfilelist.remove(dto);

			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "����ɹ���");
		}
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
					uploadConfirmService.setFail(MsgConstant.MSG_NO_1104, _dto);
					// �ж��ļ����Ƿ�ȫ�����ţ����ȫ�����ţ����÷��ͱ���
					uploadConfirmService.checkAndSendMsg(_dto,
							MsgConstant.MSG_NO_1104, TbsTvDwbkDto.tableName(),
							_dto.getIvousrlno());
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "�������Ŵ����ɹ���");
					_dto = null;
					querylist = uploadConfirmService.eachQuery(
							BizTypeConstant.BIZ_TYPE_RET_TREASURY, searchdto);
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

	public String delDetail(Object o) {
		if (null == selecteddatalist || selecteddatalist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
			return null;
		} else {
			if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), "��ʾ",
					"�Ƿ�ȷ��ɾ����")) {

				try {
					for (int i = 0; i < selecteddatalist.size(); i++) {
						TbsTvDwbkDto dto = (TbsTvDwbkDto) selecteddatalist
								.get(i);
						if (!StateConstant.CONFIRMSTATE_YES.equals(dto
								.getSstatus())) {
							int result = uploadConfirmService.eachDelete(
									BizTypeConstant.BIZ_TYPE_RET_TREASURY, dto);
							if (StateConstant.SUBMITSTATE_DONE == result
									|| StateConstant.SUBMITSTATE_SUCCESS == result)
								showdatalist.remove(dto);
							// �ж��ļ����Ƿ�ȫ�����ţ����ȫ�����ţ����÷��ͱ���
							uploadConfirmService.checkAndSendMsg(dto,
									MsgConstant.MSG_NO_1104, TbsTvDwbkDto
											.tableName(), null);
						}
					}
					totalmoney = new BigDecimal(0.00);
					totalnum = 0;
					for (int i = 0; i < showdatalist.size(); i++) {
						TbsTvDwbkDto dto = (TbsTvDwbkDto) showdatalist.get(i);
						totalmoney = totalmoney.add(dto.getFamt());
						totalnum++;
					}
					selecteddatalist = new ArrayList();
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "��ϸɾ���ɹ���");
					editor.fireModelChanged();
					return tozhubixh(o);
				} catch (ITFEBizException e) {
					log.error("��ѯ��ʱ��δ���ż�¼����", e);
					MessageDialog.openErrorDialog(null, e);
				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			}
		}
		return super.delDetail(o);
	}

	/**
	 * Direction: ��ϸɾ�� ename: delDetail ���÷���: viewers: * messages:
	 */
	public String delDetail2(Object o) {
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
								BizTypeConstant.BIZ_TYPE_RET_TREASURY, _dto);
						if (StateConstant.SUBMITSTATE_DONE == result
								|| StateConstant.SUBMITSTATE_SUCCESS == result)
							showdatalist.remove(_dto);

						// �ж��ļ����Ƿ�ȫ�����ţ����ȫ�����ţ����÷��ͱ���
						uploadConfirmService.checkAndSendMsg(_dto,
								MsgConstant.MSG_NO_1104, TbsTvDwbkDto
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
						TbsTvDwbkDto dto = (TbsTvDwbkDto) selecteddatalist
								.get(i);
						if (!StateConstant.CONFIRMSTATE_YES.equals(dto
								.getSstatus())) {

							uploadConfirmService.setFail(
									MsgConstant.MSG_NO_1104, dto);

							uploadConfirmService.checkAndSendMsg(dto,
									MsgConstant.MSG_NO_1104, TbsTvDwbkDto
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

	public String selectEvent(Object o) {
		List<TbsTvDwbkDto> mlist = (List<TbsTvDwbkDto>) selecteddatalist;
		totalmoney = new BigDecimal(0.00);
		totalnum = 0;
		for (TbsTvDwbkDto mdto : mlist) {
			totalmoney = totalmoney.add(mdto.getFamt());
			totalnum++;
		}
		this.editor.fireModelChanged();
		return super.selectEvent(o);
	}

	/**
	 * Direction: ��ѡһ����¼ ename: selOneRecord ���÷���: viewers: * messages:
	 */
	public String selOneRecord(Object o) {
		_dto = (TbsTvDwbkDto) o;
		selecteddatalist = new ArrayList();
		selecteddatalist.add(_dto);
		return super.selOneRecord(o);
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
}

package com.cfcc.itfe.client.recbiz.fundintoforfz;

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
import com.cfcc.itfe.client.common.encrypt.DecryptClientUtil;
import com.cfcc.itfe.client.common.file.ErrorNoteDialog;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IFileResolveCommonService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.service.recbiz.fundinto.IFundIntoService;
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
 * @time 13-09-13 15:34:59 ��ϵͳ: RecBiz ģ��:fundintoforfz ���:Fundintoforfz
 */
public class FundintoforfzBean extends AbstractFundintoforfzBean implements
		IPageDataProvider {
	private static Log log = LogFactory.getLog(FundintoforfzBean.class);
	protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService) getService(IFileResolveCommonService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	protected IFundIntoService fundIntoService = (IFundIntoService) getService(IFundIntoService.class);
	protected IUploadConfirmService uploadConfirmService = (IUploadConfirmService) getService(IUploadConfirmService.class);
	private Date adjustdate;
	private Date systemDate;
	private List salarydetaillist;
	private BigDecimal salarysumamt;
	private TbsTvPayoutDto salarysearch;
	private ITFELoginInfo loginfo;
	private List filepath;
	private ShiboDto shibodto;

	public FundintoforfzBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		salarydetaillist = new ArrayList();
		salarysumamt = new BigDecimal(0);
		salarysearch = new TbsTvPayoutDto();
		filepath = new ArrayList();

	}

	/**
	 * Direction: �����ļ����� ename: salarydataload ���÷���: viewers: * messages:
	 */
	public String salarydataload(Object o) {
		// �ж��Ƿ�ѡ���ļ�
		// if (!dataload(filepath, "Salary", serverpathlist)) {
		// return null;
		// }
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
				if (commonDataAccessService.verifyImportRepeat(tmpfilename)) {
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
							BizTypeConstant.BIZ_TYPE_PAY_OUT, interfacetype
									+ "Salary", shibodto);
				} else {
					// �����������ϴ��ļ�
					bizDto = fileResolveCommonService.loadFile(fileList,
							BizTypeConstant.BIZ_TYPE_PAY_OUT, interfacetype
									+ "Salary", null);
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
					MessageDialog.openMessageDialog(null, noteInfo
							+ errorPartInfo.toString());
				} else {
					MessageDialog.openMessageDialog(null, "�ļ����سɹ���");
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
		return super.salarydataload(o);
	}

	/**
	 * Direction: ��ת�����ļ������б� ename: gosalarydestoryview ���÷���: viewers: �����ļ�����
	 * messages:
	 */
	public String gosalarydestoryview(Object o) {
		return super.gosalarydestoryview(o);
	}

	/**
	 * Direction: ���ع��ʼ���ҳ ename: backsalaryloadview ���÷���: viewers: �����ļ�����
	 * messages:
	 */
	public String backsalaryloadview(Object o) {
		return super.backsalaryloadview(o);
	}

	/**
	 * Direction: �����ļ����Ų��� ename: salarydestory ���÷���: viewers: * messages:
	 */
	public String salarydestory(Object o) {
		try {
			List list = fundintoforfzService.getdestorydata(salarysearch);
			if (null == list || list.size() == 0) {
				MessageDialog.openMessageDialog(null, "û����Ҫ���ŵ����ݣ�");
				return null;
			}
			TbsTvPayoutDto updatedto = (TbsTvPayoutDto) salarysearch.clone();
			updatedto.setSbookorgcode(loginfo.getSorgcode());
			updatedto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			int result = fundintoforfzService.destorydata(updatedto);
			if (StateConstant.SUBMITSTATE_DONE == result
					|| StateConstant.SUBMITSTATE_SUCCESS == result) {
				salarydetaillist.clear();
				salarysumamt = new BigDecimal(0);
				this.editor.fireModelChanged();
				MessageDialog.openMessageDialog(null, "�����ɹ���");
				return null;
			}

		} catch (ITFEBizException e) {
			log.error("�����ļ�����ʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}

		return super.salarydestory(o);
	}

	/**
	 * Direction: �����ļ���ѯ ename: salarysearch ���÷���: viewers: * messages:
	 */
	public String salarysearch(Object o) {
		try {
			salarydetaillist.clear();
			salarydetaillist = fundintoforfzService
					.getdestorydata(salarysearch);
			if (null == salarydetaillist || salarydetaillist.size() == 0) {
				this.editor.fireModelChanged();
				MessageDialog.openMessageDialog(null, "û�з������������ݣ�");
				return null;
			}
			this.editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error("��ѯ���빤���ļ���ϸ��Ϣʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.salarysearch(o);
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

	public List getSalarydetaillist() {
		return salarydetaillist;
	}

	public void setSalarydetaillist(List salarydetaillist) {
		this.salarydetaillist = salarydetaillist;
	}

	public BigDecimal getSalarysumamt() {
		return salarysumamt;
	}

	public void setSalarysumamt(BigDecimal salarysumamt) {
		this.salarysumamt = salarysumamt;
	}

	public TbsTvPayoutDto getSalarysearch() {
		return salarysearch;
	}

	public void setSalarysearch(TbsTvPayoutDto salarysearch) {
		this.salarysearch = salarysearch;
	}

	public Date getAdjustdate() {
		return adjustdate;
	}

	public void setAdjustdate(Date adjustdate) {
		this.adjustdate = adjustdate;
	}

	public Date getSystemDate() {
		return systemDate;
	}

	public void setSystemDate(Date systemDate) {
		this.systemDate = systemDate;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List getFilepath() {
		return filepath;
	}

	public void setFilepath(List filepath) {
		this.filepath = filepath;
	}

	public ShiboDto getShibodto() {
		return shibodto;
	}

	public void setShibodto(ShiboDto shibodto) {
		this.shibodto = shibodto;
	}

}
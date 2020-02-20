package com.cfcc.itfe.client.recbiz.pbcdirectmodule;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.service.ITFELoginInfo;
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
 * @author hua
 * @time 12-06-13 09:30:11 ��ϵͳ: RecBiz ģ��:pbcDirectModule ���:PbcDirectImport
 */
public class PbcDirectImportBean extends AbstractPbcDirectImportBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(PbcDirectImportBean.class);
	private Date adjustdate;
	private Date systemDate;
	private int totalnum;
	private BigDecimal totalmoney = new BigDecimal("0.00");
	private ITFELoginInfo loginfo;
	private TbsTvPbcpayDto _dto;

	public PbcDirectImportBean() {
		super();
		filelist = new ArrayList();
		// sumamt = new BigDecimal("");
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selectedDetaillist = new ArrayList();
		showDetaillist = new ArrayList();
		searchdto = new TbsTvPbcpayDto();
		pbcdto = new TbsTvPbcpayDto();
		pbclist = new ArrayList<ResultDesDto>();
		trecode = "";
		defvou = "";
		endvou = "";
		newbudgcode = "";
		singleResList = new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
	}

	/**
	 * Direction: ���ݼ��� ename: dataImport ���÷���: viewers: * messages:
	 */
	public String dataImport(Object o) {
		// �ж��Ƿ�ѡ���ļ�
		if (null == filelist || filelist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ�!");
			return null;
		}
		// ��ʼ�������ļ�����,��txt�ļ�
		int trueSum = 0;
		// ������Ŵ�����Ϣ
		StringBuffer errorPartInfo = new StringBuffer("");
		// ������д�����Ϣ
		StringBuffer errorTotalInfo = new StringBuffer("");
		// �����ļ�����
		int errorFileCount = 0;
		// ���ص�������֮����ļ�·���б�
		List<String> serverFilelist = new ArrayList<String>();
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			// �ֱ��õ��������ں����ݿ�����
			adjustdate = commonDataAccessService.getAdjustDate();
			String dbstr = commonDataAccessService.getSysDBDate();
			systemDate = Date.valueOf(dbstr.substring(0, 4) + "-"
					+ dbstr.substring(4, 6) + "-" + dbstr.substring(6, 8));
			// ��ʼ�����ļ�
			for (int i = 0; i < filelist.size(); i++) {
				File tmpFile = (File) filelist.get(i);
				// �õ��ļ�����
				String fileName = tmpFile.getName();
				// ��ȡ�ļ���·��
				String filepath = tmpFile.getAbsolutePath();
				// ȥ����txt�ļ�
				if (!fileName.trim().toLowerCase().endsWith(".txt")) {
					continue;
				}
				trueSum++;
				if (null == tmpFile || null == fileName || null == filepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}
				// У���ļ����а�������Ϣ(���ȡ�ҵ�����͡�������)
				String valiReturn = validateFileByName(
						BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, fileName);
				// �ļ���У�鷵�ز�Ϊ�գ���Ϊ�����ļ�
				if (!"".equals(valiReturn)) {
					// ��ʾ��ϸ��Ϣ������20
					errorFileCount++;
					if (errorFileCount < 15) {
						errorPartInfo.append(valiReturn);
						errorTotalInfo.append(valiReturn);
					} else {
						errorTotalInfo.append(valiReturn);
					}
					continue;
				}
				// ��У����ȷ���ļ��ϴ�����������
				serverFilelist.add(ClientFileTransferUtil.uploadFile(tmpFile
						.getAbsolutePath()));
			}

			// ���÷��������ز������ļ�
			if (serverFilelist != null && serverFilelist.size() > 0) {
				// �������˼����ļ���������Ϣ
				MulitTableDto bizDto = fileResolveCommonService.loadFile(
						serverFilelist,
						BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY,
						MsgConstant.PBC_DIRECT_IMPORT, null);
				int errCount = errorFileCount + bizDto.getErrorCount(); // �ܴ������
				if (null != bizDto.getErrorList()
						&& bizDto.getErrorList().size() > 0) {
					for (String error : bizDto.getErrorList()) {
						if (errorFileCount < 15) {
							errorFileCount++;
							errorPartInfo.append(error.substring(8) + "\r\n");
							errorTotalInfo.append(error + "\r\n");
						} else {
							errorTotalInfo.append(error + "\r\n");
						}
					}
				}
				String errName = StateConstant.Import_Errinfo_DIR
						+ "���а���ֱ��֧���ļ����������Ϣ("
						+ new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��")
								.format(new java.util.Date()) + ").txt";
				if(!"".equals(errorTotalInfo.toString())) {
					FileUtil.getInstance().writeFile(errName,
							errorTotalInfo.toString());
				}
				
				FileUtil
						.getInstance()
						.deleteFileWithDays(
								StateConstant.Import_Errinfo_DIR,
								Integer
										.parseInt(StateConstant.Import_Errinfo_BackDays));
				// ���ļ�������־
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverFilelist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
				// ����¼�Ĵ�����Ϣ��ʾ���û�
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if (errorPartInfo.toString().trim().length() > 0) {
					String noteInfo = "��������" + trueSum + "���ļ���������" + errCount
							+ "�������ļ���������Ϣ���¡���ϸ������Ϣ��鿴" + errName + "����\r\n";
					MessageDialog.openMessageDialog(null, noteInfo
							+ errorPartInfo.toString());
				} else {
					MessageDialog.openMessageDialog(null, "�ļ����سɹ���");
				}

			} else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if (errorPartInfo.toString().trim().length() > 0) {
					MessageDialog.openMessageDialog(null, "��������" + trueSum
							+ "���ļ���������" + errorFileCount + "�������ļ�����Ϣ���£�\r\n"
							+ errorPartInfo.toString());
				}

			}

		} catch (Throwable e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			try {
				// ɾ���������ϴ�ʧ���ļ�
				DeleteServerFileUtil.delFile(commonDataAccessService,
						serverFilelist);
			} catch (ITFEBizException e1) {
				log.error("ɾ���������ļ�ʧ�ܣ�", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			return null;
		}
		filelist = new ArrayList();
		this.editor.fireModelChanged();
		return super.dataImport(o);
	}

	/**
	 * Direction: ����ȷ�� ename: batchCommit ���÷���: viewers: * messages:
	 */
	public String batchCommit(Object o) {

		try {
			if (commonDataAccessService.judgeBatchConfirm(
					BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, loginfo
							.getSorgcode())) {
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
					// ��������
					massdispose();
				}
			} else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "û����������Ȩ�ޣ��������Ա��ϵ��");
				return null;
			}

		} catch (ITFEBizException e) {
			log.error("�����ύʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.batchCommit(o);
	}

	/**
	 * Direction: ���ȷ�� ename: singleCommit ���÷���: viewers: * messages:
	 */
	public String singleCommit(Object o) {
		// ȷ���Ѿ���ѡ��ļ�¼
		if (null == selectedDetaillist || selectedDetaillist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
			return null;
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			try {
				for (int i = 0; i < selectedDetaillist.size(); i++) {
					TbsTvPbcpayDto dto = (TbsTvPbcpayDto) selectedDetaillist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result) {
						showDetaillist.remove(dto);
					}
				}
				selectedDetaillist = new ArrayList();
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
		return super.singleCommit(o);
	}

	/**
	 * Direction: ����ɾ�� ename: batchDelete ���÷���: viewers: * messages:
	 */
	public String batchDelete(Object o) {
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
							BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, dto
									);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showfilelist.remove(dto);

				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				selectedfilelist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "�����ɹ���");
			} catch (ITFEBizException e) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				log.error("����ɾ��ʧ�ܣ�", e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
			return null;
		}
		return super.batchDelete(o);
	}

	/**
	 * Direction: ֱ���ύ ename: directCommit ���÷���: viewers: * messages:
	 */
	public String directCommit(Object o) {
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, loginfo
							.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
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
			return null;
		}
		return super.directCommit(o);
	}

	/**
	 * Direction: ��ѯ���� ename: queryService ���÷���: viewers: ��ѯ���� messages:
	 */
	public String queryService(Object o) {
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		// �����������
		pbcdto.setSbookorgcode(loginfo.getSorgcode());
		// δ����
		pbcdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
		if (null == defvou) {
			defvou = "";
		}
		if (null == endvou) {
			endvou = "";
		}
		pbcdto.setStrecode(trecode);
		pbcdto.setSvouno(defvou.trim() + endvou.trim());
		try {
			singleResList = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, pbcdto);
			if (null == singleResList || singleResList.size() == 0) {
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
	 * Direction: ����ʧ�� ename: setFail ���÷���: viewers: * messages:
	 */
	public String thinkFail(Object o) {
		if (null == _dto || null == _dto.getIvousrlno()) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ����ʧ�ܵļ�¼��");
		} else {

			if (!StateConstant.CONFIRMSTATE_YES.equals(_dto.getSstatus())) {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				_dto.setSstatus(StateConstant.CONFIRMSTATE_YES);
				_dto.setSaddword("����ʧ��");
				_dto.setSpackageno("00000000");
				try {
					commonDataAccessService.updateData(_dto);
					// �ж��ļ����Ƿ�ȫ�����ţ����ȫ�����ţ����÷��ͱ���
					uploadConfirmService.checkAndSendMsg(_dto,
							MsgConstant.MSG_NO_5104,
							TbsTvPbcpayDto.tableName(), _dto.getIvousrlno());
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "��������ʧ�ܳɹ���");
					singleResList.remove(_dto);
					_dto = null;
					editor.fireModelChanged();
				} catch (ITFEBizException e) {
					log.error("��ѯ��ʱ��δ���ż�¼����", e);
					MessageDialog.openErrorDialog(null, e);
				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			} else {
				MessageDialog.openMessageDialog(null, "��ƾ֤�����ţ�����ȷ��ʧ�ܣ�");
			}
		}

		return "";
	}

	/**
	 * Direction: ��ת������� ename: goToSingle ���÷���: viewers: ������� messages:
	 */
	public String goToSingle(Object o) {
		if (StateConstant.SPECIAL_AREA_SHANXI.equals(loginfo.getArea())) { // ��ȡ����ģʽ
			return super.openFJMode(o);
		}
		return super.goToSingle(o);
	}

	/**
	 * Direction: ��ѡһ����¼ ename: selOneRecord ���÷���: viewers: * messages:
	 */
	public String selOneRecord(Object o) {
		_dto = (TbsTvPbcpayDto) o;
		return super.selOneRecord(o);
	}

	/**
	 * Direction: ȫѡ ename: selectAll ���÷���: viewers: * messages:
	 */
	public String selectAll(Object o) {
		if (null == selectedDetaillist || selectedDetaillist.size() == 0) {
			selectedDetaillist = new ArrayList();
			selectedDetaillist.addAll(showDetaillist);
		} else {
			selectedDetaillist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.selectAll(o);
	}

	/**
	 * Direction: �������Ų�ѯ ename: batchQuery ���÷���: viewers: * messages:
	 */
	public String batchQuery(Object o) {

		return super.batchQuery(o);
	}

	/**
	 * Direction: ������Ų�ѯ ename: singleQuery ���÷���: viewers: * messages:
	 */
	public String singleQuery(Object o) {
		selectedDetaillist = new ArrayList();
		showDetaillist = new ArrayList();
		try {
			// �����������
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// δ����
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showDetaillist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, searchdto);
			// ͳ�Ʊ������
			List<TbsTvPbcpayDto> mlist = (List<TbsTvPbcpayDto>) showDetaillist;
			totalmoney = new BigDecimal(0.00);
			totalnum = 0;
			for (TbsTvPbcpayDto mdto : mlist) {
				totalmoney = totalmoney.add(mdto.getFamt());
				totalnum++;
			}
			if (showDetaillist.size() == 0) {
				MessageDialog.openMessageDialog(null, "û�в�ѯ���������������ݣ�");
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				return null;
			}
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("������Ų�ѯʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.singleQuery(o);
	}

	/**
	 * Direction: ͳ����Ϣ ename: countInfo ���÷���: viewers: * messages:
	 */
	public String countInfo(Object o) {
		List<TbsTvPbcpayDto> mlist = (List<TbsTvPbcpayDto>) selectedDetaillist;
		totalmoney = new BigDecimal(0.00);
		totalnum = 0;
		for (TbsTvPbcpayDto mdto : mlist) {
			totalmoney = totalmoney.add(mdto.getFamt());
			totalnum++;
		}
		this.editor.fireModelChanged();
		return super.countInfo(o);
	}

	/**
	 * ������������
	 * 
	 * @throws ITFEBizException
	 */
	private void massdispose() throws ITFEBizException {

		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.batchConfirm(
						BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, dto
								);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result) {
					showfilelist.remove(dto);
				}
			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "����ɹ���");
		}

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

	/**
	 * У���ļ���(���ȡ�ҵ�����͡�������)
	 */
	private String validateFileByName(String biztype, String filename)
			throws ITFEBizException {
		String fileN = filename.trim().toLowerCase();
		StringBuffer sb = new StringBuffer("");
		// 13��15λ�����ּ���.txt
		if (fileN.length() == 17 || fileN.length() == 19) {
			// �ļ�����ҵ������
			String biz = fileN
					.substring(fileN.length() - 7, fileN.length() - 5);
			// �ļ����е����ڱ�־
			String trimflag = fileN.substring(fileN.length() - 5, fileN
					.length() - 4);
			if (!BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(biz)) {
				sb.append("�ļ� " + filename + " ҵ�����Ͳ���\r\n");
			} else {
				if (!MsgConstant.TIME_FLAG_NORMAL.equals(trimflag)
						&& !MsgConstant.TIME_FLAG_TRIM.equals(trimflag)) {
					sb.append("�ļ� " + filename
							+ " �����ڱ�־�����ϣ�����Ϊ��0�������� �� ��1�������ڣ�\r\n");
				} else {
					if (MsgConstant.TIME_FLAG_TRIM.equals(trimflag)) {
						if (DateUtil.after(systemDate, adjustdate)) {
							sb.append("�ļ� "
									+ filename
									+ " ������"
									+ com.cfcc.deptone.common.util.DateUtil
											.date2String(adjustdate)
									+ "�ѹ������ܴ��������ҵ��!\r\n");
						}
					}
				}
			}
		} else {
			sb.append("�ļ� " + filename + " �ļ������Ȳ���\r\n");
		}
		return sb.toString();
	}

	/**
	 * �󶨼����������ڻس�ʱִ��
	 */
	public void setSumamt(BigDecimal sumamt) {
		if(null == vouCount || null == sumamt){
			MessageDialog.openMessageDialog(null, "ƾ֤�������ܽ���Ϊ�գ�");
			return ;
		}
		this.sumamt = sumamt;
		if (sumamt == null || sumamt.compareTo(new BigDecimal(0)) == 0) {
		} else {
			try {
				showfilelist = new ArrayList();
				selectedfilelist = new ArrayList();
				showfilelist = uploadConfirmService.batchQuery(
						BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, sumamt);
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

	public int getTotalnum() {
		return totalnum;
	}

	public void setTotalnum(int totalnum) {
		this.totalnum = totalnum;
	}

	public BigDecimal getTotalmoney() {
		return totalmoney;
	}

	public void setTotalmoney(BigDecimal totalmoney) {
		this.totalmoney = totalmoney;
	}

}
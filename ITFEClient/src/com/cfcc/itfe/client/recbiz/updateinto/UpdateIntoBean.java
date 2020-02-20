package com.cfcc.itfe.client.recbiz.updateinto;

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
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
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
 * @author Administrator
 * @time 12-02-21 15:15:51 ��ϵͳ: RecBiz ģ��:updateInto ���:UpdateInto
 */
public class UpdateIntoBean extends AbstractUpdateIntoBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(UpdateIntoBean.class);
	private ITFELoginInfo loginfo;
	private BigDecimal sumamt;
	// ƾ֤�ܱ���
	private Integer vouCount;
	private Date adjustdate;
	private Date systemDate;

	public UpdateIntoBean() {
		super();
		filepath = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		filedata = new FileResultDto();
		searchdto = new TbsTvInCorrhandbookDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();

	}

	/**
	 * Direction: ���ݼ��� ename: dateload ���÷���: viewers: * messages:
	 */
	public String dateload(Object o) {
		String interfacetype = ""; // �ӿ�����
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer errorPartInfo = new StringBuffer("");
		StringBuffer errorTotalInfo = new StringBuffer("");
		int errorFileCount = 0;
		// �ж��Ƿ�ѡ���ļ�
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			filepath = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			return null;
		}
		try {
			adjustdate = commonDataAccessService.getAdjustDate();
			String str = commonDataAccessService.getSysDBDate();
			systemDate = Date.valueOf(str.substring(0, 4)+ "-" + str.substring(4, 6) + "-" + str.substring(6, 8));
			// ���ݼ���
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				String tmpfilename = tmpfile.getName().toLowerCase(); // �ļ�������
				String tmpfilepath = tmpfile.getAbsolutePath(); // ��ȡ�ļ���·��
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}
				// �����ļ�����
				interfacetype = getFileObjByFileNameBiz(
						BizTypeConstant.BIZ_TYPE_CORRECT, tmpfilename);
				// �ж��ļ��Ƿ��Ѿ�����
//				if (commonDataAccessService.verifyImportRepeat(tmpfilename)) {
//					errorFileCount ++;
//					if(errorFileCount < 15) {
//						errorPartInfo.append("�����ļ�["+tmpfilename+"]У��Ϊ�ظ�����!\r\n");
//						errorTotalInfo.append("�����ļ�["+tmpfilename+"]У��Ϊ�ظ�����!\r\n");
//					} else {
//						errorTotalInfo.append("�����ļ�["+tmpfilename+"]У��Ϊ�ظ�����!\r\n");
//					}					
//					continue;
//				}
				// �ļ��ϴ�
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			// ����������
			MulitTableDto bizDto = fileResolveCommonService.loadFile(fileList,
					BizTypeConstant.BIZ_TYPE_CORRECT, interfacetype, null);
			int tmp_errcount = errorFileCount;
			String errName = "";
			if(null != bizDto.getErrorList() && bizDto.getErrorList().size() > 0) {
				for(int m = 0 ; m < bizDto.getErrorList().size() ; m++) {
					tmp_errcount ++ ;
					if(tmp_errcount < 15) {
						errorPartInfo.append(bizDto.getErrorList().get(m).substring(2) +"\r\n");
						errorTotalInfo.append(bizDto.getErrorList().get(m) +"\r\n");
					} else {
						errorTotalInfo.append(bizDto.getErrorList().get(m) +"\r\n");
					}
				}
			}
			if(fileList != null && fileList.size() > 0) {
				errName = StateConstant.Import_Errinfo_DIR 
				+  "�����ļ����������Ϣ("+new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new java.util.Date())+").txt";
				if(!"".equals(errorTotalInfo.toString())) {
					FileUtil.getInstance().writeFile(errName, errorTotalInfo.toString());
				}
				FileUtil.getInstance().deleteFileWithDays(StateConstant.Import_Errinfo_DIR, Integer.parseInt(StateConstant.Import_Errinfo_BackDays));
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil.wipeFileOut(serverpathlist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_CORRECT);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if (errorPartInfo.toString().trim().length() > 0) {	
					String noteInfo = "��������" + filepath.size() + "���ļ���������" + (errorFileCount+bizDto.getErrorCount()) + "�������ļ���������Ϣ���¡���ϸ������Ϣ��鿴"+errName+"����\r\n";				
					MessageDialog.openMessageDialog(null, noteInfo + errorPartInfo.toString());
				}else {
					MessageDialog.openMessageDialog(null, "�ļ����سɹ�,���ι����سɹ� "+fileList.size()+" ���ļ���");
				}
			}else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if(null != filepath && filepath.size() > 0) {
					String errmsg="";
					if (errorFileCount > 0) {
					    errmsg = "������" + errorFileCount + "�������ļ�����Ϣ���£�\r\n" + errorPartInfo.toString();
					}
					MessageDialog.openMessageDialog(null, "��������" + filepath.size() + "���ļ�" +errmsg);
				}
			}
			
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
			//ɾ���������ϴ�ʧ���ļ�
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService, serverpathlist);
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
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_CORRECT, loginfo.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_CORRECT);
				if(StateConstant.SUBMITSTATE_SUCCESS == result){
					MessageDialog.openMessageDialog(null, "�����ɹ���");
				}else if(StateConstant.SUBMITSTATE_DONE == result){
					MessageDialog.openMessageDialog(null, "�뵼�����ݣ�");
				}else{
					MessageDialog.openMessageDialog(null, "����ʧ�ܣ�");
				}
			} else {
				MessageDialog.openMessageDialog(null, "û��ֱ���ύȨ�ޣ��������Ա��ϵ��");
			}
		} catch (ITFEBizException e) {
			log.error("ֱ���ύʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.submit(o);
	}

	/**
	 * Direction: ����Ĭ�Ͻ��� ename: goback ���÷���: viewers: �������� messages:
	 */
	public String goback(Object o) {
		return super.goback(o);
	}

	/**
	 * Direction: ����ȷ�� ename: plsubmit ���÷���: viewers: * messages:
	 */
	public String plsubmit(Object o) {

		try {
			if (commonDataAccessService.judgeBatchConfirm(
					BizTypeConstant.BIZ_TYPE_CORRECT, loginfo.getSorgcode())) {

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
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ��?")) {
			try {
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					int result = uploadConfirmService.batchDelete(
							BizTypeConstant.BIZ_TYPE_CORRECT, dto
									);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showfilelist.remove(dto);

				}
				selectedfilelist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "����ɹ���");
			} catch (ITFEBizException e) {
				log.error("����ɾ��ʧ�ܣ�", e);
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
			try {
				for (int i = 0; i < selecteddatalist.size(); i++) {
					TbsTvInCorrhandbookDto dto = (TbsTvInCorrhandbookDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_CORRECT, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showdatalist.remove(dto);

				}
				selecteddatalist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "����ɹ���");
			} catch (ITFEBizException e) {
				log.error("����ύʧ�ܣ�", e);
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
	// ���������ӿڻ�ȡshowfilelist�����showfilelist��������¼����ѡ�У�����ֱ��ѡ��
	// try {
	// showfilelist = new ArrayList();
	// selectedfilelist = new ArrayList();
	// showfilelist = uploadConfirmService.batchQuery(
	// BizTypeConstant.BIZ_TYPE_CORRECT, sumamt);
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
			//�����������
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			//δ����
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_CORRECT, searchdto);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("������Ų�ѯʧ�ܣ�", e);
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

	// ///////////////////////////////////////////////////////////////////////////////////////
	private String getFileObjByFileNameBiz(String biztype, String filename)
			throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();
		if (tmpfilename_new.indexOf(".txt") > 0) {
			String tmpfilename = tmpfilename_new.toLowerCase().replace(".txt", "");
			// ʮ��λ˵�����п����ǵط�������tbs boolΪtrue ��Ϊtbs�ӿڷ���Ϊ�ط�����
			if (tmpfilename.length() == 13 || tmpfilename.length() == 15 || tmpfilename.length()==14) {
				// 8λ���ڣ�2λ���κţ�2λҵ�����ͣ�1λ�����ڱ�־���
				fileobj.setSdate(tmpfilename.substring(0, 8)); // ����
				if (tmpfilename.length() == 13 ) {
					fileobj.setSbatch(tmpfilename.substring(8, 10)); // ���κ�
					fileobj.setSbiztype(tmpfilename.substring(10, 12)); // ҵ������
					fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // �����ڱ�־
				} else if(tmpfilename.length() == 15){
					fileobj.setSbatch(tmpfilename.substring(8, 12)); // ���κ�
					fileobj.setSbiztype(tmpfilename.substring(12, 14)); // ҵ������
					fileobj.setCtrimflag(tmpfilename.substring(14, 15)); // �����ڱ�־
				}else{
					fileobj.setSbatch(tmpfilename.substring(8, 10)); // ���κ�
					fileobj.setSbiztype(tmpfilename.substring(10, 12)); // ҵ������
					fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // ҵ������
				}
				interficetype = MsgConstant.GENGZHENG_DAORU;// tbs�ӿ�
				if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(fileobj
						.getSbiztype())) {
				} else {
					throw new ITFEBizException("ҵ�����Ͳ�����!");
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					throw new ITFEBizException("�����ڱ�־�����ϣ�����Ϊ��0�������� �� ��1��������!");
				}else{
					if(MsgConstant.TIME_FLAG_TRIM.equals(fileobj.getCtrimflag())){
						if(DateUtil.after(systemDate,adjustdate)){
							throw new ITFEBizException("������" + com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate) + "�ѹ������ܴ��������ҵ��");
						}
					}
				}
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
						BizTypeConstant.BIZ_TYPE_CORRECT, sumamt);
				if (null != showfilelist) {
					if (1 == showfilelist.size()) {
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
						// �������Ŵ���
						//massdispose();
					}
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}
				if(showfilelist==null||showfilelist.size()<=0){
					MessageDialog.openMessageDialog(null, "û�з������������ݣ�");
				}
			} catch (ITFEBizException e) {
				log.error("��������ʧ�ܣ�", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
	}

	// �������Ŵ���
	private void massdispose() throws ITFEBizException {
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.batchConfirm(
						BizTypeConstant.BIZ_TYPE_CORRECT, dto);
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
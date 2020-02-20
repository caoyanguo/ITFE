package com.cfcc.itfe.client.subsysmanage.datarestore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.dialog.UnknowProcessDialog;
import com.cfcc.itfe.client.login.ReLoginAction;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.GZipUtilBean;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.RunnableWithException;

/**
 * codecomment:
 * 
 * @author zouyutang
 * @time 11-12-30 09:33:48 ��ϵͳ: SubSysManage ģ��:dataRestore ���:DataRestoreUI
 */
public class DataRestoreUIBean extends AbstractDataRestoreUIBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(DataRestoreUIBean.class);
	private String sorgcode;

	private ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	
	private List<File> filelist;

	public DataRestoreUIBean() {
		super();
		filelist = new ArrayList<File>();
		sorgcode = loginfo.getSorgcode();
	}

	/**
	 * Direction: ���ݻָ� ename: datarestore ���÷���: viewers: * messages:
	 */
	public String datarestore(Object o) {
		try {
			if (StringUtils.isBlank(sorgcode)) {
				MessageDialog.openMessageDialog(null, "�������벻��Ϊ��");
				return null;
			}
			if ((null == filelist) || (filelist.size() == 0)) {
				MessageDialog.openMessageDialog(null, "����ѡ�񱸷��ļ���");
				return null;
			}
			// ����ļ��ĸ�Ŀ¼��
			String dirName = filelist.get(0).getName();

			// �ж�ѡ����ļ��Ƿ�Ϊ�û������������屸���ļ�
			if (!checkDataBackupDirectoryName(sorgcode, dirName)) {
				MessageDialog.openMessageDialog(null, "ѡ��ı����ļ�����ȷ");
				return null;
			}
			int result = UnknowProcessDialog.openUnkownProgressDialogRunUI(
					Display.getDefault().getActiveShell(),
					new RunnableWithException() {
						public void run() throws Throwable {
							// �ϴ����������˵ı����ļ� ��� ·���б� ȥ��ClientFileTransferUtil
							// rootPath
							List<String> relServerPathlist = new ArrayList<String>();

							// ѭ���ͻ����ļ������ͻ����ļ����δ����������
							File clientFile = filelist.get(0);
							if (clientFile.getName().endsWith(
									GZipUtilBean.ZIP_EXT)) {
								relServerPathlist.add(ClientFileTransferUtil
										.uploadFile(clientFile
												.getAbsolutePath().replace(
														"\\", "/")));
							}
							String res = dataRestoreService.datarestore(
									sorgcode, relServerPathlist);
							if (res.trim().length() > 0) {
								throw new Throwable(res);
							}
						}
					});

			if (StateConstant.UNKNOWPROCESSDIALOG_RESULT_YES == result) {
				MessageDialog.openMessageDialog(null, "�ָ��ɹ��������µ�¼��");
				ReLoginAction action = new ReLoginAction();
				action.run();
				return super.datarestore(o);
			} else {
				return null;
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	/**
	 * �ж��Ƿ��Ǹû���������
	 * 
	 * @param sbookorgcode
	 * @param dirName
	 * @return
	 */
	private boolean checkDataBackupDirectoryName(String sorgcode,
			String dirName) {
		if (StringUtils.isBlank(dirName)) {
			return false;
		} else if (!dirName.startsWith(sorgcode)) {
			return false;
		}
		return true;
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

	public String getSorgcode() {
		return sorgcode;
	}

	public void setSorgcode(String sorgcode) {
		this.sorgcode = sorgcode;
	}

	public List<File> getFilelist() {
		return filelist;
	}

	public void setFilelist(List<File> filelist) {
		if (filelist == null || filelist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��Ŀ¼��û���ļ�");
			return;
		}
		this.filelist = filelist;
	}

	
	
}
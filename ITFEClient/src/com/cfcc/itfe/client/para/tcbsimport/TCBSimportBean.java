package com.cfcc.itfe.client.para.tcbsimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.cfcc.deptone.common.util.FileUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.ParamConstant;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;

import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.RunnableWithException;


/**
 * codecomment:
 * 
 * @author wangtuo
 * @time 10-04-11 11:07:59 ��ϵͳ: Para ģ��:TCBSimport ���:TCBSimport
 */
public class TCBSimportBean extends AbstractTCBSimportBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TCBSimportBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	String msg= "";

	@SuppressWarnings("unchecked")
	public TCBSimportBean() {
		super();
		fileList = new ArrayList();
		tbsbankList = new ArrayList();
		bankCodeList = new ArrayList();
		msg ="1��֧��ϵͳ�����к�ֻ�������ĵ��롢2��������������뵼�����ջ��ش�������ջ��ع����Ӧ��ϵ ��Ԥ���Ŀ�����˴����������ҵ��У�� 3��������������Ե���TCBS����ֳɲ��������д��롢��ƿ�Ŀ���롢����˻���Ϣ����˰����Ԥ�㵥λ�˺�ά�����TBS��ʽ�������ط�����";
	}

	/**
	 * Direction: ���� ename: fileImport ���÷���: viewers: * messages:
	 */
	public String fileImport(Object o) {

		// ��ȡ�����ļ��ľ���·��������
		if (null == fileList || fileList.size() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ������ļ���");
			return null;
		}
		StringBuffer promptMsg = new StringBuffer("");
		for (int i = 0; i < fileList.size(); i++) {
			File f = (File) this.fileList.get(i); // �ļ�����
			String fName = f.getName().toLowerCase(); // �ļ�����
			final String fPath = f.getAbsolutePath(); // �ļ�·��
			if (null == f || null == fName || null == fPath) {
				MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ������ļ���");
				return null;
			}
			if (!fName.contains(".csv") && !fName.contains(".txt")
					&& !fName.contains(".xml")) {
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�������ȷ�ļ���ʽ��");
				return null;
			}
	        String fileUploadPath = null;
		
					
			try {
				if (fName.contains(".xml")) {
					if (!loginfo.getSorgcode().equals("000000000000")) {
						MessageDialog.openMessageDialog(null,
								"�����в�����Ҫ�����Ĺ���Ա���룡");
						return null;
					}
					if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
							this.editor.getCurrentComposite().getShell(), "��ʾ",
							"�ò�����ɾ��ԭ�����������ݣ��Ƿ�������룿")) {
						return null;
					}
						fileUploadPath = ClientFileTransferUtil.uploadFile(fPath);
					    tCBSimportService.banknoImport(fileUploadPath);
					
				} else {
					if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
							this.editor.getCurrentComposite().getShell(), "��ʾ",
							"�ò�����ɾ����������ԭ�д˲������ݣ��Ƿ�������룿")) {
						return null;
					}
					fileUploadPath = ClientFileTransferUtil.uploadFile(fPath);
					if (fName.contains("Ԥ���Ŀ") || fName.toUpperCase().contains("Ԥ���Ŀ_TBS.CSV")) {
						if(loginfo.getPublicparam().indexOf(",Budgetsubject=public,")>=0)
						{
							String sorgcode = loginfo.getSorgcode();
							if(sorgcode.equals(sorgcode.substring(0,2)+"0000000002")||sorgcode.equals(sorgcode.substring(0,2)+"0000000003"))
								tCBSimportService.fileImport(fileUploadPath);
							else
							{
								MessageDialog.openMessageDialog(null,"Ԥ���Ŀ����ֻ��ʡ���Ĺ���Ա���룡");
								return null;
							}
						}else
							tCBSimportService.fileImport(fileUploadPath);
					}  else if (fName.contains("���˴���ά��") || fName.toUpperCase().contains("���˴���_TBS.CSV")) {
						tCBSimportService.tdCorpImport(fileUploadPath);
					}else if (fName.contains("���ջ��ش����")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[1][0]);
					}else if (fName.contains("����ֳɲ���")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[3][0]);
					}else if (fName.contains("���д���")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[4][0]);
					}else if (fName.contains("����˻���Ϣ")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[5][0]);
					}else if (fName.contains("�����˻�ά��")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[8][0]);
					}else if (fName.contains("��ƿ�Ŀ��Ϣ")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[9][0]);
					}else if (fName.contains("֧��ϵͳ����������")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[10][0]);
					} else if (fName.contains("����֧�����˵�")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[11][0]);
					}else if (fName.contains("����ԭ��")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[12][0]);
					}else if (fName.contains("�˿�ԭ��")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[13][0]);
					}else if (fName.contains("���ջ��ع����Ӧ��ϵ") || fName.toUpperCase().contains("���ջ��ع����Ӧ��ϵ_TBS.CSV")) {
						tCBSimportService.taxFileImport(fileUploadPath);
					}else if (fName.contains("������־���ձ�")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[14][0]);
					}else {
						MessageDialog.openMessageDialog(null,
								"��֧�ֵĽӿ��ļ�����ѡ����ȷ�ĵ����ļ���");
						return null;
					}
				}
			} catch (Throwable e) {
				log.error(e);
				promptMsg.append(fName + "�ļ�����ʧ�ܣ�\n" + e.getCause() + "\n");
			}
			
			if (!"".equals(promptMsg.toString())) {
				if(promptMsg.toString().contains("HttpInvokeServletException")||promptMsg.toString().contains("HttpInvokerException")){
					MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧ��\r\n�����µ�¼��");
				}else{
					MessageDialog.openMessageDialog(null, promptMsg.toString());
				}
			} else {
				MessageDialog.openMessageDialog(null, "����ɹ�");
			}
		}
		return super.fileImport(o);

	}
	
	/**
	 * Direction: ����TBS��ʽ�����к� ename: tbsBankImport ���÷���: viewers: * messages:
	 */
    public String tbsBankImport(Object o){
    	// ��ȡ�����ļ��ľ���·��������
		if (null == tbsbankList || tbsbankList.size() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ������ļ���");
			return null;
		}
		StringBuffer promptMsg = new StringBuffer("");
		if(tbsbankList.size() > 1) {
			MessageDialog.openMessageDialog(null, "������ֻ����������TBS��ʽ�����к��ļ�����ʽΪ*.CSV,ÿ��ֻ�ܵ���һ���ļ������ø��Ƿ�ʽ!");
			return "";
		}
		File f = (File)tbsbankList.get(0);
		String fPath = f.getAbsolutePath(); //�ļ�·��
		String fName = f.getName().toLowerCase(); //�ļ�����
		
		if(!f.exists() || null == f || null == fName || null == fPath) {
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ������ļ�,��ȷ���ļ����ڣ�");
			return null;
		}
		if (!fName.endsWith(".csv")) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�������ȷ�ļ���ʽ��");
			return null;
		}
		if (!loginfo.getSorgcode().equals("000000000000")) {
			MessageDialog.openMessageDialog(null,
					"�����в�����Ҫ�����Ĺ���Ա���룡");
			return null;
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
				this.editor.getCurrentComposite().getShell(), "��ʾ","�ò�����ɾ��ԭ�����������ݣ��Ƿ�������룿")) {
			return null;
		}
		
		
		try {
			String fileUploadPath = ClientFileTransferUtil.uploadFile(fPath);
			tCBSimportService.tbsBankImport(fileUploadPath);
			MessageDialog.openMessageDialog(null, "����ɹ�");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		} catch (FileTransferException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
        return super.tbsBankImport(o);
    }
    
    
	
    /**
	 * Direction: ���� ename: bankCodeImport ���÷���: viewers: * messages:
	 */
	public String bankCodeImport(Object o) {
		// ��ȡ�����ļ��ľ���·��������
		if (null == bankCodeList || bankCodeList.size() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ������ļ���");
			return null;
		}
		StringBuffer promptMsg = new StringBuffer("");
		for (int i = 0; i < bankCodeList.size(); i++) {
			File f = (File) this.bankCodeList.get(i); // �ļ�����
			String fName = f.getName().toLowerCase(); // �ļ�����
			final String fPath = f.getAbsolutePath(); // �ļ�·��
			if (null == f || null == fName || null == fPath) {
				MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ������ļ���");
				return null;
			}
			if (!fName.toLowerCase().contains(".xml")) {
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�������ȷ�ļ���ʽ��");
				return null;
			}
	        String fileUploadPath = null;
			try {
				if (fName.toLowerCase().contains(".xml")) {
					if (!loginfo.getSorgcode().equals("000000000000")) {
						MessageDialog.openMessageDialog(null,"�����в�����Ҫ�����Ĺ���Ա���룡");
						return null;
					}
					if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
							this.editor.getCurrentComposite().getShell(), "��ʾ","�ò�����ɾ��ԭ�����������ݣ��Ƿ�������룿")) {
						return null;
					}
					fileUploadPath = ClientFileTransferUtil.uploadFile(fPath);
				    tCBSimportService.bankCodeImport(fileUploadPath);
				} 
			} catch (Throwable e) {
				log.error(e);
				promptMsg.append(fName + "�ļ�����ʧ�ܣ�\n" + (String) (e.getCause()==null?e.getMessage():e.getCause()) + "\n");
			}
			
			if (!"".equals(promptMsg.toString())) {
				if(promptMsg.toString().contains("HttpInvokeServletException")||promptMsg.toString().contains("HttpInvokerException")){
					MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧ��\r\n�����µ�¼��");
				}else{
					MessageDialog.openMessageDialog(null, promptMsg.toString());
				}
			} else {
				MessageDialog.openMessageDialog(null, "����ɹ�");
			}
		}
		return super.bankCodeImport(o);
	}

	/**
	 * �����ж�ȡ�ļ���ÿ�и���split���зָ���ַ�������
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String> readFileWithLine(String fileName)
			throws FileOperateException {
		List<String> listStr = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName)));
			String data = null;
			int i = 0;
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				listStr.add(data);
			}

			br.close();
			return listStr;
		} catch (Exception e) {
			throw new FileOperateException("��ȡ�ļ������쳣", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					throw new FileOperateException("��ȡ�ļ������쳣", e);
				}

			}
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

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TCBSimportBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
package com.cfcc.itfe.client.sendbiz.exporttbsforbj;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * @author hua
 * @time   14-09-10 10:08:23
 * ��ϵͳ: SendBiz
 * ģ��:exportTBSForBJ
 * ���:ExportTBSForBJUI
 */
@SuppressWarnings("unchecked")
public class ExportTBSForBJUIBean extends AbstractExportTBSForBJUIBean implements IPageDataProvider {
	private static Log log = LogFactory.getLog(ExportTBSForBJUIBean.class);
	
	/** ��½��Ϣ **/
	private ITFELoginInfo loginfo;
	
	/** ��ʼ�����ݵ��������� **/
	private BaseTBSFileExportUtil exportUtil;
	
	/** TBSҵ������ **/
	private String bizType = "";
	
	/** ������� **/
	private String treCode = "";
	
	/** �������ڣ�Ĭ��Ϊ���� **/
	private Date acctDate = new Date();
	
	/** �����ڱ�־��Ĭ��Ϊ������ **/
	private String trimFlag = "0";
	
	/** ����TBS��ִ�ļ����� **/
	private List<File> fileList = new ArrayList<File>();
	
    public ExportTBSForBJUIBean() {
    	super();
		init();
    }
    
    private void init(){
    	loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    	try {
    		/**Ĭ��Ϊ��ǰ��½������������������**/
    		treCode = loginfo.getSorgcode().substring(0, 10);
		} catch (Exception e) {
			log.error(e);
			treCode = "";
		}
    }
    
    /**
	 * Direction: ����
	 * ����TBS��ʽ���ļ���Ŀǰ��Ҫ�����㱱������TBSҵ����
	 */
    public String exportTBS(Object o){
        /**1���ȶ��û�������Ϣ����У�� **/
    	String noteInfo = validate();
    	if(!"".equals(noteInfo)) {
    		MessageDialog.openMessageDialog(null, noteInfo);
    		return "";
    	}
    	
    	/**2��У��ͨ������÷���ʼ�����ļ� **/
    	try {
    		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
					.getActiveShell());
			String filePath = path.open();
			if ((null == filePath) || (filePath.length() == 0)) {
				MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
				return "";
			}
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			
			/**2.1 ����������ѯ����Ϣ **/
			List<IDto> list = exportTBSForBJService.exportTBS(bizType, treCode, new java.sql.Date(acctDate.getTime()), trimFlag, null);
			if(list==null || list.size() == 0) {
				MessageDialog.openMessageDialog(null, "��Ҫ���������ݲ�����!");
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				return "";
			}
			
			/**2.2 ��乤����������Ϣ,��Ҫ�������ļ���ʹ�� **/
			exportUtil = new BaseTBSFileExportUtil(); //��ȡ�¶���ķ�ʽ,��ֹ���ֶ��̲߳�������
			exportUtil.setAcctDate(new java.sql.Date(acctDate.getTime()));
			exportUtil.setBizType(bizType);
			exportUtil.setTrimFlag(trimFlag);
			
			/**2.3 ��ʼ��ʽ�����ļ� **/
			String finalSaveDir = exportUtil.expdata(list, filePath);
			
			/**2.4 �����ļ������������״̬ **/
			exportTBSForBJService.updateVouStatus(list);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, "�ļ������ɹ�\n���·����" + finalSaveDir);
			exportUtil = null;
		} catch (Exception e) {
			log.error(e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			exportUtil = null;
			return "";
		} 
        return "";
    }
    
    /**
	 * Direction: ����TBS��ִ�ļ�
	 */
    public String importTBS(Object o){
    	try {
    		exportUtil = new BaseTBSFileExportUtil();
    		
    		/** 1.У���ļ��б��Ƿ�Ϸ� */
			String noteInfo = exportUtil.validateImportFile(fileList);
			if(!"".equals(noteInfo)) {
				MessageDialog.openMessageDialog(null, noteInfo);
				return "";
			}
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			
			/** 2.���ڼ�ס�ϴ������������ļ��ķ�����·��**/
			List<String> serverPathList = new ArrayList<String>();
			for(int i=0; i<fileList.size(); i++) {
				File file = fileList.get(i);
				String absFilePath = file.getAbsolutePath();
				
				/** 3.�жϷ��������Ƿ��Ѵ��ڸ��ļ�,������ɾ��**/
				String serverFilePath=commonDataAccessService.getServerRootPath(absFilePath, loginfo.getSorgcode());
				exportTBSForBJService.deleteServerFile(serverFilePath);
				
				/** 4.��ס�ļ��ڷ�������·��**/
				serverPathList.add(ClientFileTransferUtil.uploadFile(absFilePath));
			}
			
			/** 5.���÷���˷��������ִ���������ҵ������**/
			MulitTableDto resultDto = exportTBSForBJService.importTBS(serverPathList, null);
			
			/** 6.��Խ������������ʾ��Ϣ���������û�**/
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, exportUtil.generateMessage(resultDto,serverPathList));
			refresh();
			exportUtil = null;
		} catch (Exception e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			exportUtil = null;
			return super.importTBS(o);
		} 
        return super.importTBS(o);
    }
    
	/**���û���������ݽ���У��**/
    private String validate(){
    	StringBuffer sb = new StringBuffer("������ʾ:\n");
    	if(StringUtils.isBlank(bizType)) {
    		sb.append("ҵ�����ͱ�ѡ!\n");
    	}
    	if(StringUtils.isBlank(treCode)) {
    		sb.append("���������ѡ!\n");
    	}
    	if(acctDate!=null) {
    		if(acctDate.after(new Date())){
    			sb.append("�����������ڴ��ڵ�ǰ����!\n");
    		}
    	}else {
    		sb.append("�������ڱ���!\n");
    	}
    	if(StringUtils.isBlank(trimFlag)) {
    		sb.append("�����ڱ�־����!\n");
    	}
    	return sb.toString().equals("������ʾ:\n")?"":sb.toString();
    }
    
    /**���ѡ����ļ�,��ˢ�½���**/
    private void refresh(){
    	fileList = new ArrayList<File>();
		editor.fireModelChanged();
    }
    
    public PageResponse retrieve(PageRequest arg0) {
		return super.retrieve(arg0);
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getTreCode() {
		return treCode;
	}

	public void setTreCode(String treCode) {
		this.treCode = treCode;
	}

	public Date getAcctDate() {
		return acctDate;
	}

	public void setAcctDate(Date acctDate) {
		this.acctDate = acctDate;
	}

	public String getTrimFlag() {
		return trimFlag;
	}

	public void setTrimFlag(String trimFlag) {
		this.trimFlag = trimFlag;
	}

	public List<File> getFileList() {
		return fileList;
	}

	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}
	
}
package com.cfcc.itfe.client.subsysmanage.databackup;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.dialog.UnknowProcessDialog;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.RunnableWithException;

/**
 * codecomment: 
 * @author zouyutang
 * @time   11-12-30 09:33:48
 * ��ϵͳ: SubSysManage
 * ģ��:dataBackup
 * ���:DataBackupUI
 */
public class DataBackupUIBean extends AbstractDataBackupUIBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(DataBackupUIBean.class);
    
    private String sorgcode;
    
    private ITFELoginInfo loginfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    
    public DataBackupUIBean() {
      super();
      sorgcode = loginfo.getSorgcode();               
    }
    
	/**
	 * Direction: ���ݱ���
	 * ename: databackup
	 * ���÷���: 
	 * viewers: ���ݱ���
	 * messages: 
	 */
    public String databackup(Object o){
    	try {
			if (StringUtils.isBlank(sorgcode)) {
				MessageDialog.openMessageDialog(null, "�������벻��Ϊ��");
				return null;
			}

			// ѡ�񱣴�·��
			DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
					.getActiveShell());
			
			final String filePath = path.open();
			if ((null == filePath) || (filePath.length() == 0)) {
				MessageDialog.openMessageDialog(null, "��ѡ�񵼳�����Ŀ¼��");
				return null;
			}
			
			int result = UnknowProcessDialog.openUnkownProgressDialogRunUI(Display
					.getDefault().getActiveShell(),
					new RunnableWithException() {
						public void run() throws Throwable {
							//�ڷ�������ɱ����ļ������ҽ����ݵ������ļ������·������
							List<String> serverFilePathList = dataBackupService.databackup(sorgcode);	
							
							File clientFile =null;
							for(String serverFilePath :serverFilePathList){
								String clientFileName = filePath+File.separator+FileUtil.getStringBehindLastFileSeparator(serverFilePath);
								clientFile = new File(clientFileName);
								if(clientFile.exists()){
									clientFile.delete();
								}
								if(!clientFile.getParentFile().exists()){
									clientFile.getParentFile().mkdirs();
								}
								ClientFileTransferUtil.downloadFile(serverFilePath, clientFileName);
							}
						}
					});
			
			if(StateConstant.UNKNOWPROCESSDIALOG_RESULT_YES==result){
				MessageDialog.openMessageDialog(null, "�ļ��ɹ����ݵ�·��["+filePath+"]�¡�");
				return super.databackup(o);
			}else{
				return null;
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
    
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
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

    
    
}
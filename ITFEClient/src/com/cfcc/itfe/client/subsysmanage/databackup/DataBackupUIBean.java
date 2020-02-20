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
 * 子系统: SubSysManage
 * 模块:dataBackup
 * 组件:DataBackupUI
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
	 * Direction: 数据备份
	 * ename: databackup
	 * 引用方法: 
	 * viewers: 数据备份
	 * messages: 
	 */
    public String databackup(Object o){
    	try {
			if (StringUtils.isBlank(sorgcode)) {
				MessageDialog.openMessageDialog(null, "机构代码不能为空");
				return null;
			}

			// 选择保存路径
			DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
					.getActiveShell());
			
			final String filePath = path.open();
			if ((null == filePath) || (filePath.length() == 0)) {
				MessageDialog.openMessageDialog(null, "请选择导出备份目录。");
				return null;
			}
			
			int result = UnknowProcessDialog.openUnkownProgressDialogRunUI(Display
					.getDefault().getActiveShell(),
					new RunnableWithException() {
						public void run() throws Throwable {
							//在服务端生成备份文件，并且将备份的所有文件的相对路径返回
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
				MessageDialog.openMessageDialog(null, "文件成功备份到路径["+filePath+"]下。");
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
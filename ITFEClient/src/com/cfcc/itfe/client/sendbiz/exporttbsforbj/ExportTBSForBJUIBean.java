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
 * 子系统: SendBiz
 * 模块:exportTBSForBJ
 * 组件:ExportTBSForBJUI
 */
@SuppressWarnings("unchecked")
public class ExportTBSForBJUIBean extends AbstractExportTBSForBJUIBean implements IPageDataProvider {
	private static Log log = LogFactory.getLog(ExportTBSForBJUIBean.class);
	
	/** 登陆信息 **/
	private ITFELoginInfo loginfo;
	
	/** 初始化数据导出工具类 **/
	private BaseTBSFileExportUtil exportUtil;
	
	/** TBS业务类型 **/
	private String bizType = "";
	
	/** 国库代码 **/
	private String treCode = "";
	
	/** 账务日期：默认为当天 **/
	private Date acctDate = new Date();
	
	/** 调整期标志：默认为正常期 **/
	private String trimFlag = "0";
	
	/** 导入TBS回执文件集合 **/
	private List<File> fileList = new ArrayList<File>();
	
    public ExportTBSForBJUIBean() {
    	super();
		init();
    }
    
    private void init(){
    	loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    	try {
    		/**默认为当前登陆核算主体的主国库代码**/
    		treCode = loginfo.getSorgcode().substring(0, 10);
		} catch (Exception e) {
			log.error(e);
			treCode = "";
		}
    }
    
    /**
	 * Direction: 导出
	 * 导出TBS格式的文件，目前主要是满足北京区县TBS业务处理
	 */
    public String exportTBS(Object o){
        /**1、先对用户输入信息进行校验 **/
    	String noteInfo = validate();
    	if(!"".equals(noteInfo)) {
    		MessageDialog.openMessageDialog(null, noteInfo);
    		return "";
    	}
    	
    	/**2、校验通过则调用服务开始导出文件 **/
    	try {
    		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
					.getActiveShell());
			String filePath = path.open();
			if ((null == filePath) || (filePath.length() == 0)) {
				MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
				return "";
			}
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			
			/**2.1 根据条件查询到信息 **/
			List<IDto> list = exportTBSForBJService.exportTBS(bizType, treCode, new java.sql.Date(acctDate.getTime()), trimFlag, null);
			if(list==null || list.size() == 0) {
				MessageDialog.openMessageDialog(null, "需要导出的数据不存在!");
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				return "";
			}
			
			/**2.2 填充工具类所需信息,主要是生成文件名使用 **/
			exportUtil = new BaseTBSFileExportUtil(); //采取新对象的方式,防止出现多线程并发问题
			exportUtil.setAcctDate(new java.sql.Date(acctDate.getTime()));
			exportUtil.setBizType(bizType);
			exportUtil.setTrimFlag(trimFlag);
			
			/**2.3 开始正式导出文件 **/
			String finalSaveDir = exportUtil.expdata(list, filePath);
			
			/**2.4 导出文件后更新索引表状态 **/
			exportTBSForBJService.updateVouStatus(list);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, "文件导出成功\n存放路径：" + finalSaveDir);
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
	 * Direction: 导入TBS回执文件
	 */
    public String importTBS(Object o){
    	try {
    		exportUtil = new BaseTBSFileExportUtil();
    		
    		/** 1.校验文件列表是否合法 */
			String noteInfo = exportUtil.validateImportFile(fileList);
			if(!"".equals(noteInfo)) {
				MessageDialog.openMessageDialog(null, noteInfo);
				return "";
			}
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			
			/** 2.用于记住上传到服务器的文件的服务器路径**/
			List<String> serverPathList = new ArrayList<String>();
			for(int i=0; i<fileList.size(); i++) {
				File file = fileList.get(i);
				String absFilePath = file.getAbsolutePath();
				
				/** 3.判断服务器端是否已存在该文件,存在则删除**/
				String serverFilePath=commonDataAccessService.getServerRootPath(absFilePath, loginfo.getSorgcode());
				exportTBSForBJService.deleteServerFile(serverFilePath);
				
				/** 4.记住文件在服务器的路径**/
				serverPathList.add(ClientFileTransferUtil.uploadFile(absFilePath));
			}
			
			/** 5.调用服务端方法导入回执并更新相关业务数据**/
			MulitTableDto resultDto = exportTBSForBJService.importTBS(serverPathList, null);
			
			/** 6.针对结果对象生成提示信息并反馈给用户**/
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
    
	/**对用户输入的数据进行校验**/
    private String validate(){
    	StringBuffer sb = new StringBuffer("错误提示:\n");
    	if(StringUtils.isBlank(bizType)) {
    		sb.append("业务类型必选!\n");
    	}
    	if(StringUtils.isBlank(treCode)) {
    		sb.append("国库主体必选!\n");
    	}
    	if(acctDate!=null) {
    		if(acctDate.after(new Date())){
    			sb.append("输入账务日期大于当前日期!\n");
    		}
    	}else {
    		sb.append("账务日期必填!\n");
    	}
    	if(StringUtils.isBlank(trimFlag)) {
    		sb.append("调整期标志必填!\n");
    	}
    	return sb.toString().equals("错误提示:\n")?"":sb.toString();
    }
    
    /**清空选择的文件,并刷新界面**/
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
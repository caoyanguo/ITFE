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
 * @time 10-04-11 11:07:59 子系统: Para 模块:TCBSimport 组件:TCBSimport
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
		msg ="1、支付系统行名行号只允许中心导入、2、各核算主体必须导入征收机关代码表、征收机关国库对应关系 、预算科目、法人代码参数用于业务校验 3、各核算主体可以导入TCBS共享分成参数、银行代码、会计科目代码、会计账户信息、纳税人与预算单位账号维护表后TBS格式导出给地方横联";
	}

	/**
	 * Direction: 导入 ename: fileImport 引用方法: viewers: * messages:
	 */
	public String fileImport(Object o) {

		// 提取加载文件的绝对路径和名称
		if (null == fileList || fileList.size() == 0) {
			MessageDialog.openMessageDialog(null, " 请选择要导入的文件！");
			return null;
		}
		StringBuffer promptMsg = new StringBuffer("");
		for (int i = 0; i < fileList.size(); i++) {
			File f = (File) this.fileList.get(i); // 文件对象
			String fName = f.getName().toLowerCase(); // 文件名称
			final String fPath = f.getAbsolutePath(); // 文件路径
			if (null == f || null == fName || null == fPath) {
				MessageDialog.openMessageDialog(null, " 请选择要导入的文件！");
				return null;
			}
			if (!fName.contains(".csv") && !fName.contains(".txt")
					&& !fName.contains(".xml")) {
				MessageDialog.openMessageDialog(null, "请选择要导入的正确文件格式！");
				return null;
			}
	        String fileUploadPath = null;
		
					
			try {
				if (fName.contains(".xml")) {
					if (!loginfo.getSorgcode().equals("000000000000")) {
						MessageDialog.openMessageDialog(null,
								"清算行参数需要由中心管理员导入！");
						return null;
					}
					if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
							this.editor.getCurrentComposite().getShell(), "提示",
							"该操作会删除原有清算行数据，是否继续导入？")) {
						return null;
					}
						fileUploadPath = ClientFileTransferUtil.uploadFile(fPath);
					    tCBSimportService.banknoImport(fileUploadPath);
					
				} else {
					if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
							this.editor.getCurrentComposite().getShell(), "提示",
							"该操作会删除本机构下原有此参数数据，是否继续导入？")) {
						return null;
					}
					fileUploadPath = ClientFileTransferUtil.uploadFile(fPath);
					if (fName.contains("预算科目") || fName.toUpperCase().contains("预算科目_TBS.CSV")) {
						if(loginfo.getPublicparam().indexOf(",Budgetsubject=public,")>=0)
						{
							String sorgcode = loginfo.getSorgcode();
							if(sorgcode.equals(sorgcode.substring(0,2)+"0000000002")||sorgcode.equals(sorgcode.substring(0,2)+"0000000003"))
								tCBSimportService.fileImport(fileUploadPath);
							else
							{
								MessageDialog.openMessageDialog(null,"预算科目代码只能省中心管理员导入！");
								return null;
							}
						}else
							tCBSimportService.fileImport(fileUploadPath);
					}  else if (fName.contains("法人代码维护") || fName.toUpperCase().contains("法人代码_TBS.CSV")) {
						tCBSimportService.tdCorpImport(fileUploadPath);
					}else if (fName.contains("征收机关代码表")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[1][0]);
					}else if (fName.contains("共享分成参数")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[3][0]);
					}else if (fName.contains("银行代码")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[4][0]);
					}else if (fName.contains("会计账户信息")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[5][0]);
					}else if (fName.contains("法人账户维护")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[8][0]);
					}else if (fName.contains("会计科目信息")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[9][0]);
					}else if (fName.contains("支付系统参与者主表")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[10][0]);
					} else if (fName.contains("集中支付对账单")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[11][0]);
					}else if (fName.contains("更正原因")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[12][0]);
					}else if (fName.contains("退库原因")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[13][0]);
					}else if (fName.contains("征收机关国库对应关系") || fName.toUpperCase().contains("征收机关国库对应关系_TBS.CSV")) {
						tCBSimportService.taxFileImport(fileUploadPath);
					}else if (fName.contains("辅助标志对照表")) {
						tCBSimportService.paramImport(fileUploadPath,ParamConstant.TABS[14][0]);
					}else {
						MessageDialog.openMessageDialog(null,
								"不支持的接口文件，请选择正确的导入文件！");
						return null;
					}
				}
			} catch (Throwable e) {
				log.error(e);
				promptMsg.append(fName + "文件导入失败！\n" + e.getCause() + "\n");
			}
			
			if (!"".equals(promptMsg.toString())) {
				if(promptMsg.toString().contains("HttpInvokeServletException")||promptMsg.toString().contains("HttpInvokerException")){
					MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失败\r\n请重新登录！");
				}else{
					MessageDialog.openMessageDialog(null, promptMsg.toString());
				}
			} else {
				MessageDialog.openMessageDialog(null, "导入成功");
			}
		}
		return super.fileImport(o);

	}
	
	/**
	 * Direction: 导入TBS格式行名行号 ename: tbsBankImport 引用方法: viewers: * messages:
	 */
    public String tbsBankImport(Object o){
    	// 提取加载文件的绝对路径和名称
		if (null == tbsbankList || tbsbankList.size() == 0) {
			MessageDialog.openMessageDialog(null, " 请选择要导入的文件！");
			return null;
		}
		StringBuffer promptMsg = new StringBuffer("");
		if(tbsbankList.size() > 1) {
			MessageDialog.openMessageDialog(null, "本功能只能用来导入TBS格式行名行号文件，格式为*.CSV,每次只能导入一个文件，采用覆盖方式!");
			return "";
		}
		File f = (File)tbsbankList.get(0);
		String fPath = f.getAbsolutePath(); //文件路径
		String fName = f.getName().toLowerCase(); //文件名称
		
		if(!f.exists() || null == f || null == fName || null == fPath) {
			MessageDialog.openMessageDialog(null, " 请选择要导入的文件,并确认文件存在！");
			return null;
		}
		if (!fName.endsWith(".csv")) {
			MessageDialog.openMessageDialog(null, "请选择要导入的正确文件格式！");
			return null;
		}
		if (!loginfo.getSorgcode().equals("000000000000")) {
			MessageDialog.openMessageDialog(null,
					"清算行参数需要由中心管理员导入！");
			return null;
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
				this.editor.getCurrentComposite().getShell(), "提示","该操作会删除原有清算行数据，是否继续导入？")) {
			return null;
		}
		
		
		try {
			String fileUploadPath = ClientFileTransferUtil.uploadFile(fPath);
			tCBSimportService.tbsBankImport(fileUploadPath);
			MessageDialog.openMessageDialog(null, "导入成功");
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
	 * Direction: 导入 ename: bankCodeImport 引用方法: viewers: * messages:
	 */
	public String bankCodeImport(Object o) {
		// 提取加载文件的绝对路径和名称
		if (null == bankCodeList || bankCodeList.size() == 0) {
			MessageDialog.openMessageDialog(null, " 请选择要导入的文件！");
			return null;
		}
		StringBuffer promptMsg = new StringBuffer("");
		for (int i = 0; i < bankCodeList.size(); i++) {
			File f = (File) this.bankCodeList.get(i); // 文件对象
			String fName = f.getName().toLowerCase(); // 文件名称
			final String fPath = f.getAbsolutePath(); // 文件路径
			if (null == f || null == fName || null == fPath) {
				MessageDialog.openMessageDialog(null, " 请选择要导入的文件！");
				return null;
			}
			if (!fName.toLowerCase().contains(".xml")) {
				MessageDialog.openMessageDialog(null, "请选择要导入的正确文件格式！");
				return null;
			}
	        String fileUploadPath = null;
			try {
				if (fName.toLowerCase().contains(".xml")) {
					if (!loginfo.getSorgcode().equals("000000000000")) {
						MessageDialog.openMessageDialog(null,"清算行参数需要由中心管理员导入！");
						return null;
					}
					if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
							this.editor.getCurrentComposite().getShell(), "提示","该操作会删除原有清算行数据，是否继续导入？")) {
						return null;
					}
					fileUploadPath = ClientFileTransferUtil.uploadFile(fPath);
				    tCBSimportService.bankCodeImport(fileUploadPath);
				} 
			} catch (Throwable e) {
				log.error(e);
				promptMsg.append(fName + "文件导入失败！\n" + (String) (e.getCause()==null?e.getMessage():e.getCause()) + "\n");
			}
			
			if (!"".equals(promptMsg.toString())) {
				if(promptMsg.toString().contains("HttpInvokeServletException")||promptMsg.toString().contains("HttpInvokerException")){
					MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失败\r\n请重新登录！");
				}else{
					MessageDialog.openMessageDialog(null, promptMsg.toString());
				}
			} else {
				MessageDialog.openMessageDialog(null, "导入成功");
			}
		}
		return super.bankCodeImport(o);
	}

	/**
	 * 按照行读取文件，每行根据split进行分割成字符串数组
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
			throw new FileOperateException("读取文件出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					throw new FileOperateException("读取文件出现异常", e);
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
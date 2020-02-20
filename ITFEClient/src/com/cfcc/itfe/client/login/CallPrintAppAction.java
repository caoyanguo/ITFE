package com.cfcc.itfe.client.login;

import java.io.File;

import org.eclipse.jface.action.Action;

import com.cfcc.itfe.util.CallShellApplicationUtil;

public class CallPrintAppAction extends Action {
	
	public void run() {
		try {
			String appPath=CallShellApplicationUtil.getInstance().getAppPath();
//			String appPath="C:/program files/szpbc/exchequer/exchequer2.exe";
			File file=new File(appPath);
			if(!file.exists()){
				com.cfcc.jaf.rcp.util.MessageDialog.openMessageDialog(null,"��Ӧ�ó��򲻴��ڻ�Ӧ�ó�������·������ȷ��");
				return;
			}
			String[] str=new String[]{"cmd","/c",appPath}; 
			Process child = Runtime.getRuntime().exec(str);
			child.waitFor();
		} catch (Exception e) {
			com.cfcc.jaf.rcp.util.MessageDialog.openMessageDialog(null, e.getMessage());
		}

	}
	
}

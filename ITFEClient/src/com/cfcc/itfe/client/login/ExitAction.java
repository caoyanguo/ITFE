package com.cfcc.itfe.client.login;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.dataquery.operlogquery.ISystemdateCheckService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;


public class ExitAction extends Action {

	public void run() {
		Boolean result = MessageDialog.openConfirm(null, "确认", "确认退出系统吗？");
		if (result.booleanValue()) {
			ITFELoginInfo loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
			ISystemdateCheckService syslogService = (ISystemdateCheckService)ServiceFactory.getService(ISystemdateCheckService.class);
			String remoteUserCode = loginInfo.getSuserCode();
			String remoteFunctionCode = "9999";
			String menuName = "退出";
			try {
				//记录退出日志并更新用户表的登录状态和退出时间
				syslogService.operLog(remoteUserCode, remoteFunctionCode, menuName, "", false, loginInfo);
				PlatformUI.getWorkbench().close();
			} catch (Throwable e) {
				PlatformUI.getWorkbench().close();
			}
		
		}

	}
}

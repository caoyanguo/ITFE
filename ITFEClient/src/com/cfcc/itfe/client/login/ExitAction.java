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
		Boolean result = MessageDialog.openConfirm(null, "ȷ��", "ȷ���˳�ϵͳ��");
		if (result.booleanValue()) {
			ITFELoginInfo loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
			ISystemdateCheckService syslogService = (ISystemdateCheckService)ServiceFactory.getService(ISystemdateCheckService.class);
			String remoteUserCode = loginInfo.getSuserCode();
			String remoteFunctionCode = "9999";
			String menuName = "�˳�";
			try {
				//��¼�˳���־�������û���ĵ�¼״̬���˳�ʱ��
				syslogService.operLog(remoteUserCode, remoteFunctionCode, menuName, "", false, loginInfo);
				PlatformUI.getWorkbench().close();
			} catch (Throwable e) {
				PlatformUI.getWorkbench().close();
			}
		
		}

	}
}

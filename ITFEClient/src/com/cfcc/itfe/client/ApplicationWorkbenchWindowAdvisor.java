package com.cfcc.itfe.client;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

import com.cfcc.itfe.client.login.LoginTitleDialog;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.dataquery.operlogquery.ISystemdateCheckService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.rcp.JAFActivator;
import com.cfcc.jaf.rcp.JAFApplicationWorkbenchWindowAdvisor;

/**
 * 
 */
public class ApplicationWorkbenchWindowAdvisor extends
		JAFApplicationWorkbenchWindowAdvisor {
	
	private Log log = LogFactory.getLog(FuncInvoker.class);
	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		ApplicationActionBarAdvisor barAdvisor = new ApplicationActionBarAdvisor(
				configurer);
		ApplicationActionBarAdvisor.setDefault(barAdvisor);
		return barAdvisor;
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		IDialogSettings settings = JAFActivator.getDefault()
				.getDialogSettings();
		String TITLE = "title";
		IDialogSettings titleSettings = settings.getSection(TITLE);
		//δ�ҵ���Ӧ������Ϣ ����ʱ��ע������
//		if (titleSettings == null) {
			titleSettings = settings.addNewSection(TITLE);
			titleSettings.put(TITLE, "��ӭʹ�� --����֧����ֽ����Ŀ");
//		}
		configurer.setTitle(titleSettings.get(TITLE));
		//����Ĭ����־·��
		File file = new File("C:/itfe/logs");
		if (!file.exists()){
			file.mkdirs();
		}
		// configurer.setShowProgressIndicator(true);
		// ContextFactory.setContextFile("config/ContextLoader_Client_Local.xml");
		ContextFactory.setContextFile("config/ContextLoader.xml");
	}

	public void postWindowOpen() {
		getWindowConfigurer().getWindow().getShell().setMaximized(true);
		addPartListener();
		setShellColor(null);
		setMenuBG(null);
		setToorbarAndStausLineBG(null, null);
		setEditorTabFolderColor(null);
		setPageAreaColor(null);
		setPageSelectColor(null);
		// Display.getDefault().getActiveShell().redraw();
		ApplicationActionBarAdvisor.getDefault().getLogin().run();
	}
	
	public boolean preWindowShellClose(){
		return MessageDialog.openConfirm(null, "ȷ��", "ȷ���˳�ϵͳ��");
	}
	
	public void postWindowClose(){
		ITFELoginInfo loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		ISystemdateCheckService syslogService = (ISystemdateCheckService)ServiceFactory.getService(ISystemdateCheckService.class);
		String remoteUserCode = loginInfo.getSuserCode();
		String remoteFunctionCode = "9999";
		String menuName = "�˳�";
		try {
			//��¼�˳���־�������û���ĵ�¼״̬���˳�ʱ��
			syslogService.operLog(remoteUserCode, remoteFunctionCode, menuName, "", false, loginInfo);
		} catch (ITFEBizException e) {
			log.error("��¼�˳���־����", e);
			com.cfcc.jaf.rcp.util.MessageDialog.openErrorDialog(null, e);
		}
	}
	
}

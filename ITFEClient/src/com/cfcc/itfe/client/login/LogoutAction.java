package com.cfcc.itfe.client.login;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.SimpleImageUtil;

public class LogoutAction extends Action {
	
	com.cfcc.itfe.client.ApplicationActionBarAdvisor advisor;
	private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) ServiceFactory.getService(ICommonDataAccessService.class);
	private static Log log = LogFactory.getLog(LogoutAction.class);
	public LogoutAction() {
		super("×¢Ïú");
		setId("LogoutAction");
		setImageDescriptor(SimpleImageUtil.getDescriptor(SimpleImageUtil.LOGOUT));
	}
	
	public void run() {
		Workbench workbench = (Workbench) PlatformUI.getWorkbench();
		WorkbenchWindow window = (WorkbenchWindow) workbench.getActiveWorkbenchWindow();
		WorkbenchPage page = (WorkbenchPage) window.getActivePage();
		page.closeAllEditors(true);
		advisor = ApplicationActionBarAdvisor.getDefault();
		ITFELoginInfo loginfo = ((ITFELoginInfo)advisor.getLoginInfo());
		try {
			TsUsersPK pk = new TsUsersPK();
			pk.setSusercode(loginfo.getSuserCode());
			pk.setSorgcode(loginfo.getSorgcode());
			TsUsersDto userDto = (TsUsersDto) commonDataAccessService.find(pk);
			userDto.setSuserstatus(StateConstant.USERSTATUS_1);
			commonDataAccessService.updateData(userDto);
		} catch (ITFEBizException e) {
			log.debug(e);
		}
		advisor.setLoginInfo(null);
		advisor.setPermissionChecker(null);
		advisor.fillStartupMenuBar();
		
	}
}

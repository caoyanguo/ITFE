package com.cfcc.itfe.client.login;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;

public class ReLoginAction extends Action {
	
	private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) ServiceFactory.getService(ICommonDataAccessService.class);
	private static Log log = LogFactory.getLog(ReLoginAction.class);

	public void run() {
		/**
		 * 关闭所有打开的page
		 */
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		window.getActivePage().closeAllEditors(false);
		ApplicationActionBarAdvisor advisor = ApplicationActionBarAdvisor.getDefault();
		ITFELoginInfo loginfo = ((ITFELoginInfo)advisor.getLoginInfo());
		try {
			TsUsersPK pk = new TsUsersPK();
			pk.setSusercode(loginfo.getSuserCode());
			pk.setSorgcode(loginfo.getSorgcode());
			TsUsersDto userDto = (TsUsersDto) commonDataAccessService.find(pk);
			userDto.setSuserstatus(StateConstant.USERSTATUS_1);
			commonDataAccessService.updateData(userDto);
		} catch (Throwable e) {
			log.debug(e);
		}
		LoginAction loginAction = new LoginAction();
		loginAction.rerun();
	}
}

package com.cfcc.itfe.client.login;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IReFreshCacheInfoService;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;



public class ReloadAction extends Action {

	public void run() {
		
		ITFELoginInfo  loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
		
		/** service */
	    IReFreshCacheInfoService iService = (IReFreshCacheInfoService)getService(IReFreshCacheInfoService.class);
		
		Boolean result = MessageDialog.openConfirm(null, "确认", "确认是否同步参数？\n" +
				                                                 "该功能用于保证参数及时生效。" );
		String sResult = "";
		if (result.booleanValue()) {
			try {
				sResult = iService.reloadBuffer(loginfo.getSorgcode());
				com.cfcc.jaf.rcp.util.MessageDialog.openMessageDialog( null, sResult );
				
			} catch (Throwable e) {
				com.cfcc.jaf.rcp.util.MessageDialog.openErrorDialog(null, e);
			}
		}

	}
	
	private IService getService(Class clazz){
		return ServiceFactory.getService(clazz);
	}
}

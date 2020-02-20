package com.cfcc.itfe.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.cfcc.jaf.core.interfaces.IServiceInvoker;
import com.cfcc.jaf.core.interfaces.IServiceRequest;
import com.cfcc.jaf.core.interfaces.IServiceResponse;
import com.cfcc.jaf.core.invoker.aop.IInterceptor;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.util.Constants;
import com.cfcc.itfe.service.ITFERequestExtInfo;

public class FuncInvoker implements IInterceptor, IServiceInvoker {
	private Log log = LogFactory.getLog(FuncInvoker.class);
	private IServiceInvoker invoker = null;
	// ÅÐ¶ÏÊÇ·ñÖØ¸´²Ù×÷
	AbstractMetaDataEditorPart initEditor = null;

	public void setInvoker(IServiceInvoker arg0) {
		this.invoker = arg0;
	}

	public IServiceResponse invoke(IServiceRequest arg0) {
		AbstractMetaDataEditorPart ix = null;
		ITFERequestExtInfo info = null;
		if (Display.getCurrent() == null
				|| PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage() == null) {
			info = (ITFERequestExtInfo) arg0.getRequestExtInfo();
		} else {
			ix = (AbstractMetaDataEditorPart) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			info = (ITFERequestExtInfo) arg0.getRequestExtInfo();
		}
		
		if (ix == null) {
			info.setLog(true);
		} else {
			
				info.setFuncCode((String) ix
						.getData(Constants.KEY_FUNCTION_CODE));
				info.setMenuName((String) ix
						.getData(Constants.KEY_MENU_NAME));
				info.setLog(true);
			

		}
		IServiceResponse response = invoker.invoke(arg0);

		return response;
	}

}

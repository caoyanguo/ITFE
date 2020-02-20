package com.cfcc.itfe.client.common.dialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;

import com.cfcc.jaf.rcp.util.SimpleImageUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.EditionUtil;
import com.cfcc.itfe.util.PublishTime;


public class AboutSysAction extends Action {

	private static Log log = LogFactory.getLog(AboutSysAction.class);

	public AboutSysAction() {
		setImageDescriptor(SimpleImageUtil.getDescriptor(SimpleImageUtil.HELP));
	}
	
	public void run() {
		ITFELoginInfo loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		AboutSysDialog aboutSysDialog = new AboutSysDialog(null);
		aboutSysDialog.setSedition(EditionUtil.getInstance().getEdition()+loginInfo.getVersion());
		aboutSysDialog.setPubtime(new PublishTime().getTime());
		aboutSysDialog.open();
	}

}

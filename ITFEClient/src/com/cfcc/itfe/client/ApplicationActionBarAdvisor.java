package com.cfcc.itfe.client;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.cfcc.itfe.client.login.LoginAction;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.rcp.JAFActionBarAdvisor;

/**
 *
 */
public class ApplicationActionBarAdvisor extends JAFActionBarAdvisor {
	
	private Action login;
	private StatusLineContributionItem statusItem;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	public static ApplicationActionBarAdvisor getDefault() {
		return (ApplicationActionBarAdvisor) instance;
	}
	
	public void fillCoolBar(ICoolBarManager coolBar) {
		super.fillCoolBar(coolBar);
		super.configMenuBar();
		fillStartupMenuBar();
	}

    public void fillMenuBar(IMenuManager menuBar) {
		super.fillMenuBar(menuBar);
	}
	
	public static void setDefault(ApplicationActionBarAdvisor advisor) {
		instance = advisor;
	}
	public void fillStartupMenuBar() {
		getMenuBar().removeAll();
		MenuManager login = new MenuManager("登录");
		this.login = new LoginAction();
		login.add(this.login);
		getMenuBar().add(login);
		//getMenuBar().insertBefore("0af3960f-4bef-45fd-a07d-21881f7bbaa4",this.login );
		//getCoolBar().removeAll();
		refersh();
	}

	protected void fillStatusLine(IStatusLineManager statusLine) {
		super.fillStatusLine(statusLine);

		 statusItem = new StatusLineContributionItem(
				"statusItem1", 140);
		statusItem.setText(this.statusInfo());
		statusLine.add(statusItem);

	}

	public Action getLogin() {
		return login;
	}

	public void setLogin(Action login) {
		this.login = login;
	}

	private String statusInfo() {
		StringBuffer sb = new StringBuffer();
//		sb.append("核算主体代码：").append(tasLoginInfo.getBookOrgCode()+"   ");
//		sb.append("用户名称：").append(tasLoginInfo.getUserName()+"   ");
		sb.append("工作日期：").append(DateUtil.currentDate()).append("   ");
		sb.append("java版本：").append(System.getProperty("java.vm.version"));
		return sb.toString();
	}

	public StatusLineContributionItem getStatusItem() {
		return statusItem;
	}

	public void setStatusItem(StatusLineContributionItem statusItem) {
		this.statusItem = statusItem;
	}
    
}
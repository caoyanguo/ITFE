package com.cfcc.itfe.client.subsysmanage.loginouttips;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.Sleak;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.ContentDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.subsysmanage.loginouttips.ILogInOutTipsService;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-11-23 09:15:48 子系统: SubSysManage 模块:LogInOutTips 组件:LogInOutTips
 */
public class LogInOutTipsBean extends AbstractLogInOutTipsBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(LogInOutTipsBean.class);
	String password = "";
	String newpassword = "";
	private ApplicationActionBarAdvisor advisor;
	private ITFELoginInfo  loginfo;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public LogInOutTipsBean() {
		super();
		advisor =ApplicationActionBarAdvisor.getDefault();
		loginfo = (ITFELoginInfo) advisor.getLoginInfo();
		
	}

	/**
	 * Direction: 登陆TIPS ename: login 引用方法: viewers: * messages:
	 */
	public String login(Object o) {
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		String result = loginout(MsgConstant.MSG_NO_9006);
		try {
			Thread current = Thread.currentThread();
    		current.sleep(5000);
			result =logInOutTipsService.sendLogOutMsg(null);
			if (null==result) {
				MessageDialog.openMessageDialog(null, "登陆报文发送成功,请到发送日志表查询结果");
			}else{
				String info = loginfo.toString().replace("已登录", result).replace("已签退",result );
				advisor.getStatusItem().setText(info);
				MessageDialog.openMessageDialog(null, "登陆报文发送成功,请到发送日志表查询结果\n系统查询到 TIPS当前状态为：【"+result+"】\n ");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		} catch (Exception e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
		} 
		
		return result;
			
		
	}
	/**
	 * Direction: 签退TIPS
	 * ename: logout
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String logout(Object o){
    	DisplayCursor.setCursor(SWT.CURSOR_WAIT);
    	String result= loginout(MsgConstant.MSG_NO_9008);
    	try {
    		Thread current = Thread.currentThread();
    		current.sleep(5000);
			result =logInOutTipsService.sendLogOutMsg(null);
			if (null==result) {
				MessageDialog.openMessageDialog(null, "签退报文发送成功，请到发送日志表查询结果");
			}else{
				String info = loginfo.toString().replace("已登录", result).replace("已签退",result );
				advisor.getStatusItem().setText(info);	
				MessageDialog.openMessageDialog(null, "签退报文发送成功,请在发送日志表查询详细结果\n系统查询到TIPS当前状态为：【"+result+"】\n ");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		} catch (Exception e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
		} 
    	
		return result;
			
        
    }
    /**
	 * 登陆签退的公共方法 
	 */
    private String loginout(String msgno){
    	if (null == password || password.trim().equals("")) {
			return "登陆密码不能为空";
		}else if(password.length()>=33||password.length()<=7){
			return "登陆密码长度必须在8-32位字符范围之内";
		}
    	if(StringUtils.isNotBlank(newpassword)&&(newpassword.length()>=33||newpassword.length()<=7)){
    		return "新登陆密码长度必须在8-32位字符范围之内";
    	}
		ContentDto dto = new ContentDto();
		dto.set_logPass(getPassword());
		dto.set_logNewPass(getNewpassword());
		dto.set_Msgno(msgno);
		String logreturn;
		try {
			logreturn = logInOutTipsService.sendLoginMsg(dto);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return "发送报文失败";
		}
		return null;
    }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

}
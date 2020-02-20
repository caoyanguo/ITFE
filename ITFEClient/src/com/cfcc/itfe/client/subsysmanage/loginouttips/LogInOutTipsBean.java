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
 * @time 09-11-23 09:15:48 ��ϵͳ: SubSysManage ģ��:LogInOutTips ���:LogInOutTips
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
	 * Direction: ��½TIPS ename: login ���÷���: viewers: * messages:
	 */
	public String login(Object o) {
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		String result = loginout(MsgConstant.MSG_NO_9006);
		try {
			Thread current = Thread.currentThread();
    		current.sleep(5000);
			result =logInOutTipsService.sendLogOutMsg(null);
			if (null==result) {
				MessageDialog.openMessageDialog(null, "��½���ķ��ͳɹ�,�뵽������־���ѯ���");
			}else{
				String info = loginfo.toString().replace("�ѵ�¼", result).replace("��ǩ��",result );
				advisor.getStatusItem().setText(info);
				MessageDialog.openMessageDialog(null, "��½���ķ��ͳɹ�,�뵽������־���ѯ���\nϵͳ��ѯ�� TIPS��ǰ״̬Ϊ����"+result+"��\n ");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		} catch (Exception e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
		} 
		
		return result;
			
		
	}
	/**
	 * Direction: ǩ��TIPS
	 * ename: logout
	 * ���÷���: 
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
				MessageDialog.openMessageDialog(null, "ǩ�˱��ķ��ͳɹ����뵽������־���ѯ���");
			}else{
				String info = loginfo.toString().replace("�ѵ�¼", result).replace("��ǩ��",result );
				advisor.getStatusItem().setText(info);	
				MessageDialog.openMessageDialog(null, "ǩ�˱��ķ��ͳɹ�,���ڷ�����־���ѯ��ϸ���\nϵͳ��ѯ��TIPS��ǰ״̬Ϊ����"+result+"��\n ");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		} catch (Exception e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
		} 
    	
		return result;
			
        
    }
    /**
	 * ��½ǩ�˵Ĺ������� 
	 */
    private String loginout(String msgno){
    	if (null == password || password.trim().equals("")) {
			return "��½���벻��Ϊ��";
		}else if(password.length()>=33||password.length()<=7){
			return "��½���볤�ȱ�����8-32λ�ַ���Χ֮��";
		}
    	if(StringUtils.isNotBlank(newpassword)&&(newpassword.length()>=33||newpassword.length()<=7)){
    		return "�µ�½���볤�ȱ�����8-32λ�ַ���Χ֮��";
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
			return "���ͱ���ʧ��";
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
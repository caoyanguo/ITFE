package com.cfcc.itfe.client.subsysmanage.testconnection;

import java.util.ArrayList;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author zhouchuan
 * @time 09-11-09 17:53:57 ��ϵͳ: SubSysManage ģ��:testConnection ���:TestConnection
 */
public class TestConnectionBean extends AbstractTestConnectionBean implements
		IPageDataProvider {

	public TestConnectionBean() {
		super();
		srcvmsgnode = MsgConstant.MSG_HEAD_DES;
		smsgdate = TimeFacade.getCurrentStringTime();
		resultList = new ArrayList<TvSendlogDto>();
	}

	/**
	 * Direction: �����Բ��� ename: testConnect ���÷���: viewers: * messages:
	 */
	public String testConnect(Object o) {
		if (null == srcvmsgnode || "".equals(srcvmsgnode)) {
			MessageDialog.openMessageDialog(null, "���սڵ������д��");
			return super.testConnect(o);
		}

		try {
			testConnectionService.sendTestMsg(srcvmsgnode);
			MessageDialog.openMessageDialog(null, "���������Ա����Ѿ����ͣ���Ⱥ�һ��ʱ���ѯ���Խ����");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.testConnect(o);
	}
	
	/**
	 * Direction: ���������Բ���
	 * ename: connect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String connect(Object o){
    	try {
			testConnectionService.testMsg();
			MessageDialog.openMessageDialog(null, "���������Ա����Ѿ����ͣ���Ⱥ�һ��ʱ���ѯ���Խ����");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return "";
    }
	
	

	/**
	 * Direction: ���Խ����ѯ ename: testResult ���÷���: viewers: * messages:
	 */
	public String testResult(Object o) {
		if (null == smsgdate || "".equals(smsgdate)) {
			MessageDialog.openMessageDialog(null, "��ѯ���ڱ�����д��");
			return super.testConnect(o);
		}

		try {
			resultList = testConnectionService.searchTestResult(
					MsgConstant.MSG_NO_9005, smsgdate);
			if(null == resultList || resultList.size() == 0){
				MessageDialog.openMessageDialog(null, "û���ҵ���Ӧ�ļ�¼ ��");
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		editor.fireModelChanged();
		
		return super.testResult(o);
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
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
 * @time 09-11-09 17:53:57 子系统: SubSysManage 模块:testConnection 组件:TestConnection
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
	 * Direction: 连接性测试 ename: testConnect 引用方法: viewers: * messages:
	 */
	public String testConnect(Object o) {
		if (null == srcvmsgnode || "".equals(srcvmsgnode)) {
			MessageDialog.openMessageDialog(null, "接收节点必须填写！");
			return super.testConnect(o);
		}

		try {
			testConnectionService.sendTestMsg(srcvmsgnode);
			MessageDialog.openMessageDialog(null, "测试连接性报文已经发送，请等候一段时间查询测试结果！");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.testConnect(o);
	}
	
	/**
	 * Direction: 北京连接性测试
	 * ename: connect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String connect(Object o){
    	try {
			testConnectionService.testMsg();
			MessageDialog.openMessageDialog(null, "测试连接性报文已经发送，请等候一段时间查询测试结果！");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return "";
    }
	
	

	/**
	 * Direction: 测试结果查询 ename: testResult 引用方法: viewers: * messages:
	 */
	public String testResult(Object o) {
		if (null == smsgdate || "".equals(smsgdate)) {
			MessageDialog.openMessageDialog(null, "查询日期必须填写！");
			return super.testConnect(o);
		}

		try {
			resultList = testConnectionService.searchTestResult(
					MsgConstant.MSG_NO_9005, smsgdate);
			if(null == resultList || resultList.size() == 0){
				MessageDialog.openMessageDialog(null, "没有找到对应的纪录 ！");
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
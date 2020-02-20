package com.cfcc.itfe.client.sendbiz.payoutcheckrequest;


import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcp.util.MessageDialog;


/**
 * codecomment: 
 * @author t60
 * @time   12-02-24 10:44:42
 * 子系统: SendBiz
 * 模块:payoutCheckRequest
 * 组件:PayoutCheckRequest
 */
public class PayoutCheckRequestBean extends AbstractPayoutCheckRequestBean implements IPageDataProvider {
	private static Log log = LogFactory.getLog(PayoutCheckRequestBean.class);
	private ITFELoginInfo loginfo = null;

	private String sendorgcode = null; // 发起机构代码
	private String entrustdate = null; // 委托日期
	private String oripackmsgno = null; // 原包报文编号
	private String orichkdate = null; // 原核对日期
	private String oripackno = null; // 原包流水号
	private String orgtype = null; // 机构类型

	public PayoutCheckRequestBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		this.setEntrustdate(TimeFacade.getCurrentStringTime());
	}

	/**
	 * Direction: 重发请求 ename: payoutRequest 引用方法: viewers: * messages:
	 */
	public String payoutRequest(Object o) {

		if (null == sendorgcode || "".equals(sendorgcode.trim())) {
			MessageDialog.openMessageDialog(null, "请填写发起机构代码！");
			return super.payoutRequest(o);
		}
		
		//中心机构代码
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		/*if (!loginfo.getSorgcode().equals(centerorg)) {
			if(!sendorgcode.equals(loginfo.getSorgcode())){
				MessageDialog.openMessageDialog(null, " 权限不够，请填写当前用户所属的机构代码！");
				return super.payoutRequest(o);
			}
		}*/
		
		if (null == entrustdate || "".equals(entrustdate.trim())) {
			MessageDialog.openMessageDialog(null, "请填写委托日期！");
			return super.payoutRequest(o);
		}
		if (null == oripackmsgno || "".equals(oripackmsgno.trim())) {
			MessageDialog.openMessageDialog(null, "请填写原包报文编号！");
			return super.payoutRequest(o);
		}
		if (null == orichkdate || "".equals(orichkdate.trim())) {
			MessageDialog.openMessageDialog(null, "请填写原核对日期！");
			return super.payoutRequest(o);
		}
		if (null == oripackno || "".equals(oripackno.trim())) {
			MessageDialog.openMessageDialog(null, "请填写原包流水号！");
			return super.payoutRequest(o);
		}
		if (null == orgtype || "".equals(orgtype.trim())) {
			MessageDialog.openMessageDialog(null, "请填写机构类型！");
			return super.payoutRequest(o);
		}

		try {
			payoutCheckRequestService.payoutCheckRequest(sendorgcode,
					entrustdate, oripackmsgno, orichkdate, oripackno, orgtype);
			MessageDialog.openMessageDialog(null, "支出核对包重发请求成功！");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.payoutRequest(o);

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

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		PayoutCheckRequestBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public String getSendorgcode() {
		return sendorgcode;
	}

	public void setSendorgcode(String sendorgcode) {
		this.sendorgcode = sendorgcode;
	}

	public String getEntrustdate() {
		return entrustdate;
	}

	public void setEntrustdate(String entrustdate) {
		this.entrustdate = entrustdate;
	}

	public String getOripackmsgno() {
		return oripackmsgno;
	}

	public void setOripackmsgno(String oripackmsgno) {
		this.oripackmsgno = oripackmsgno;
	}

	public String getOrichkdate() {
		return orichkdate;
	}

	public void setOrichkdate(String orichkdate) {
		this.orichkdate = orichkdate;
	}

	public String getOripackno() {
		return oripackno;
	}

	public void setOripackno(String oripackno) {
		this.oripackno = oripackno;
	}

	public String getOrgtype() {
		return orgtype;
	}

	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}


}
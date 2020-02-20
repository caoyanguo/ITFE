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
 * ��ϵͳ: SendBiz
 * ģ��:payoutCheckRequest
 * ���:PayoutCheckRequest
 */
public class PayoutCheckRequestBean extends AbstractPayoutCheckRequestBean implements IPageDataProvider {
	private static Log log = LogFactory.getLog(PayoutCheckRequestBean.class);
	private ITFELoginInfo loginfo = null;

	private String sendorgcode = null; // �����������
	private String entrustdate = null; // ί������
	private String oripackmsgno = null; // ԭ�����ı��
	private String orichkdate = null; // ԭ�˶�����
	private String oripackno = null; // ԭ����ˮ��
	private String orgtype = null; // ��������

	public PayoutCheckRequestBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		this.setEntrustdate(TimeFacade.getCurrentStringTime());
	}

	/**
	 * Direction: �ط����� ename: payoutRequest ���÷���: viewers: * messages:
	 */
	public String payoutRequest(Object o) {

		if (null == sendorgcode || "".equals(sendorgcode.trim())) {
			MessageDialog.openMessageDialog(null, "����д����������룡");
			return super.payoutRequest(o);
		}
		
		//���Ļ�������
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		/*if (!loginfo.getSorgcode().equals(centerorg)) {
			if(!sendorgcode.equals(loginfo.getSorgcode())){
				MessageDialog.openMessageDialog(null, " Ȩ�޲���������д��ǰ�û������Ļ������룡");
				return super.payoutRequest(o);
			}
		}*/
		
		if (null == entrustdate || "".equals(entrustdate.trim())) {
			MessageDialog.openMessageDialog(null, "����дί�����ڣ�");
			return super.payoutRequest(o);
		}
		if (null == oripackmsgno || "".equals(oripackmsgno.trim())) {
			MessageDialog.openMessageDialog(null, "����дԭ�����ı�ţ�");
			return super.payoutRequest(o);
		}
		if (null == orichkdate || "".equals(orichkdate.trim())) {
			MessageDialog.openMessageDialog(null, "����дԭ�˶����ڣ�");
			return super.payoutRequest(o);
		}
		if (null == oripackno || "".equals(oripackno.trim())) {
			MessageDialog.openMessageDialog(null, "����дԭ����ˮ�ţ�");
			return super.payoutRequest(o);
		}
		if (null == orgtype || "".equals(orgtype.trim())) {
			MessageDialog.openMessageDialog(null, "����д�������ͣ�");
			return super.payoutRequest(o);
		}

		try {
			payoutCheckRequestService.payoutCheckRequest(sendorgcode,
					entrustdate, oripackmsgno, orichkdate, oripackno, orgtype);
			MessageDialog.openMessageDialog(null, "֧���˶԰��ط�����ɹ���");
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
package com.cfcc.itfe.client.sendbiz.taxbureau;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-01-10 11:24:38 ��ϵͳ: SendBiz ģ��:TaxBureau ���:TaxBureau
 */
public class TaxBureauBean extends AbstractTaxBureauBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TaxBureauBean.class);
	private String sendorgcode;
	private String reportDate;
	private String reportArea;
	private ITFELoginInfo loginfo = null;
	List<TsTaxorgDto> finddtolist = null;

	public TaxBureauBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		this.reportArea = "0";
		reportDate = TimeFacade.getCurrentStringTime();
	}

	@Override
	public String applyReport(Object o) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isBlank(sendorgcode)) {
			sb.append("����������벻��Ϊ��\n");
		}
		Date tmp = null;
		try {
			tmp = TimeFacade.parseDate(reportDate, "yyyyMMdd");
		} catch (Exception e) {
			sb.append("������ڸ�ʽ����\n");
		}
		if (tmp != null && tmp.after(TimeFacade.getCurrentDate())) {
			sb.append("������ڲ��ܳ�����ǰ����\n");
		}
		if (!"".equals(sb.toString())) {
			MessageDialog.openMessageDialog(null, sb.toString());
			return null;
		}
		try {
			// ���Ļ�������
			String centerorg = itfeCacheService.cacheGetCenterOrg();
			// �жϵ�ǰ���ջ��ش����Ƿ����
			TsTaxorgDto finddto = new TsTaxorgDto();
			finddto.setStaxorgcode(sendorgcode); // ���ջ��ش���
			if (!loginfo.getSorgcode().equals(centerorg)) {
				finddto.setSorgcode(loginfo.getSorgcode()); // �����������
			}
			finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto,
					"2");
			if (null == finddtolist || finddtolist.size() == 0) {
				MessageDialog.openMessageDialog(null,
						"����������벻���ڻ�Ȩ�޲���,����д��ȷ�ķ���������룡");
				return null;
			}
			this.taxBureauService.sendMsg(sendorgcode, reportDate, reportArea, MsgConstant.MSG_NO_1025);
			MessageDialog.openMessageDialog(null, "���뱨�ķ��ͳɹ�");
		} catch (Exception e) {
			log.error("1025���ķ���ʧ��!", e);
			MessageDialog.openErrorDialog(null, e);
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

	public String getSendorgcode() {
		return sendorgcode;
	}

	public void setSendorgcode(String sendorgcode) {
		this.sendorgcode = sendorgcode;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getReportArea() {
		return reportArea;
	}

	public void setReportArea(String reportArea) {
		this.reportArea = reportArea;
	}

}
package com.cfcc.itfe.facade.data;

import java.sql.Date;


public class FileObj {
	String bizType;//ҵ������vips
	Date acctDate;
	String reportDate;//��������vips
	String payStyle;// ֧����ʽvips
	String reportKind;// ��������vips
	String strCode;// �������vips
	String adjustFlag;// �����ڱ�־vips
	String reportStyle;// ��������vips
	

	public Date getAcctDate() {
		return acctDate;
	}

	public void setAcctDate(Date acctDate) {
		this.acctDate = acctDate;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getReportStyle() {
		return reportStyle;
	}

	public void setReportStyle(String reportStyle) {
		this.reportStyle = reportStyle;
	}

	public String getAdjustFlag() {
		return adjustFlag;
	}

	public void setAdjustFlag(String adjustFlag) {
		this.adjustFlag = adjustFlag;
	}

	public String getStrCode() {
		return strCode;
	}

	public void setStrCode(String strCode) {
		this.strCode = strCode;
	}

	public String getReportKind() {
		return reportKind;
	}

	public void setReportKind(String reportKind) {
		this.reportKind = reportKind;
	}

	public String getPayStyle() {
		return payStyle;
	}

	public void setPayStyle(String payStyle) {
		this.payStyle = payStyle;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}


}

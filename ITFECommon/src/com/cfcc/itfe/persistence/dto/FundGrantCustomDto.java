package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class FundGrantCustomDto implements IDto {
	/**
	 * Ԥ�㵥λ����
	 */
	private String sbudgetunitcode;
	/**
	 * ��ϸ���
	 */
	private Integer idetailseqno;
	
	/**
	 * �к�
	 */
	private Integer rowid;
	
	/**
	 * ƾ֤����
	 */
	private String voudate;
	
	/**
	 * ƾ֤���
	 */
	private String vouno;

	/**
	 * ����״̬
	 */
	private String sstatus;
	
	/**
	 * ���
	 */
	private BigDecimal nmoney;
	/**
	 * ��ϸ���
	 */
	private BigDecimal submoney;
	

	/**
	 * ���λ ����
	 */
	private String spayername;
	
	/**
	 * ���λ �˺�
	 */
	private String spayeracct;
	
	/**
	 * �տλ ��������
	 */
	private String srecbankname;
	
	/**
	 * �տλ ����
	 */
	private String srecname;
	
	/**
	 * �տλ �˺�
	 */
	private String srecacct;
	
	/**
	 * ��������
	 */
	private String spaybankname;
	
	/**
	 * ժҪ
	 */
	private String spaysummaryname;
	
	/**
	 * ���ܿ�Ŀ
	 */
	private String sfunsubjectcode;
	
	private String saddword;
	private String spayerbankname;
	public Integer getRowid() {
		return rowid;
	}

	public void setRowid(Integer rowid) {
		this.rowid = rowid;
	}

	public String getVoudate() {
		return voudate;
	}

	public void setVoudate(String voudate) {
		this.voudate = voudate;
	}

	public String getVouno() {
		return vouno;
	}

	public void setVouno(String vouno) {
		this.vouno = vouno;
	}
	public BigDecimal getSubmoney() {
		return submoney;
	}
	
	public void setSubmoney(BigDecimal submoney) {
		this.submoney = submoney;
	}

	public String getSstatus() {
		return sstatus;
	}

	public void setSstatus(String sstatus) {
		this.sstatus = sstatus;
	}

	public BigDecimal getNmoney() {
		return nmoney;
	}

	public void setNmoney(BigDecimal nmoney) {
		this.nmoney = nmoney;
	}

	public String getSpayername() {
		return spayername;
	}

	public void setSpayername(String spayername) {
		this.spayername = spayername;
	}

	public String getSpayeracct() {
		return spayeracct;
	}

	public void setSpayeracct(String spayeracct) {
		this.spayeracct = spayeracct;
	}

	public String getSrecbankname() {
		return srecbankname;
	}

	public void setSrecbankname(String srecbankname) {
		this.srecbankname = srecbankname;
	}

	public String getSrecname() {
		return srecname;
	}

	public void setSrecname(String srecname) {
		this.srecname = srecname;
	}

	public String getSrecacct() {
		return srecacct;
	}

	public void setSrecacct(String srecacct) {
		this.srecacct = srecacct;
	}

	public String getSpaybankname() {
		return spaybankname;
	}

	public void setSpaybankname(String spaybankname) {
		this.spaybankname = spaybankname;
	}

	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValid(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValidExcept(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDto[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPK getPK() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isParent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setChildren(IDto[] arg0) {
		// TODO Auto-generated method stub
		
	}

	public String getSpaysummaryname() {
		return spaysummaryname;
	}

	public void setSpaysummaryname(String spaysummaryname) {
		this.spaysummaryname = spaysummaryname;
	}

	public String getSfunsubjectcode() {
		return sfunsubjectcode;
	}

	public void setSfunsubjectcode(String sfunsubjectcode) {
		this.sfunsubjectcode = sfunsubjectcode;
	}
	public String getSbudgetunitcode() {
		return sbudgetunitcode;
	}
	
	public void setSbudgetunitcode(String sbudgetunitcode) {
		this.sbudgetunitcode = sbudgetunitcode;
	}
	
	public Integer getIdetailseqno() {
		return idetailseqno;
	}
	
	public void setIdetailseqno(Integer idetailseqno) {
		this.idetailseqno = idetailseqno;
	}

	public String getSaddword() {
		return saddword;
	}

	public void setSaddword(String saddword) {
		this.saddword = saddword;
	}

	public String getSpayerbankname() {
		return spayerbankname;
	}

	public void setSpayerbankname(String spayerbankname) {
		this.spayerbankname = spayerbankname;
	}

}

/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import java.sql.Date;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * @author Administrator
 * 
 */
public class ShanghaiReport implements IDto {

	/**
	 * ��������
	 */
	private String bookorgcode;
	/**
	 * �������
	 */
	private String trecode;
	/**
	 * Ͻ����־
	 */
	private String sbelongflag;

	/**
	 * Ԥ�㼶�δ��� S_BUDGETLEVELCODE CHARACTER , PK , NOT NULL
	 */
	private String slevelcode;
	/**
	 * Ԥ���Ŀ S_BUDGETSUBCODE VARCHAR , PK , NOT NULL
	 */
	private String subcode;
	/**
	 * Ԥ���Ŀ���� S_BUDGETSUBNAME VARCHAR
	 */
	private String subname;
	/**
	 * �����ڱ�־ S_TRIMFLAG CHARACTER , NOT NULL
	 */
	private String strimflag;
	/**
	 * ��ʼʱ��
	 */
	private Date startdate;
	/**
	 * ����ʱ��
	 */
	private Date enddate;
	/**
	 * ��λ
	 */
	private String moneyunit;
	
	/**
	 * ��������   �ձ����±���Ѯ�������걨���걨
	 */
	private String reporttype;
	/**
	 * ��������  ��Ѯ ��Ѯ ��Ѯ  �ϰ��� �°���
	 */
	private String reportkind;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValid()
	 */
	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.persistence.jaform.parent.IDto#checkValid(java.lang.String
	 * [])
	 */
	public String checkValid(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.persistence.jaform.parent.IDto#checkValidExcept(java.lang
	 * .String[])
	 */
	public String checkValidExcept(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#getChildren()
	 */
	public IDto[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#getPK()
	 */
	public IPK getPK() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#isParent()
	 */
	public boolean isParent() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.persistence.jaform.parent.IDto#setChildren(com.cfcc.jaf.
	 * persistence.jaform.parent.IDto[])
	 */
	public void setChildren(IDto[] arg0) {
		// TODO Auto-generated method stub

	}

	public String getBookorgcode() {
		return bookorgcode;
	}

	public void setBookorgcode(String bookorgcode) {
		this.bookorgcode = bookorgcode;
	}

	public String getTrecode() {
		return trecode;
	}

	public void setTrecode(String trecode) {
		this.trecode = trecode;
	}

	public String getSbelongflag() {
		return sbelongflag;
	}

	public void setSbelongflag(String sbelongflag) {
		this.sbelongflag = sbelongflag;
	}

	

	

	public String getSlevelcode() {
		return slevelcode;
	}

	public void setSlevelcode(String slevelcode) {
		this.slevelcode = slevelcode;
	}

	

	public String getSubcode() {
		return subcode;
	}

	public void setSubcode(String subcode) {
		this.subcode = subcode;
	}

	public String getSubname() {
		return subname;
	}

	public void setSubname(String subname) {
		this.subname = subname;
	}

	public String getStrimflag() {
		return strimflag;
	}

	public void setStrimflag(String strimflag) {
		this.strimflag = strimflag;
	}

	
	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public String getMoneyunit() {
		return moneyunit;
	}

	public void setMoneyunit(String moneyunit) {
		this.moneyunit = moneyunit;
	}

	public String getReporttype() {
		return reporttype;
	}

	public void setReporttype(String reporttype) {
		this.reporttype = reporttype;
	}

	public String getReportkind() {
		return reportkind;
	}

	public void setReportkind(String reportkind) {
		this.reportkind = reportkind;
	}
	
	

}

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
	 * 机构代码
	 */
	private String bookorgcode;
	/**
	 * 国库代码
	 */
	private String trecode;
	/**
	 * 辖属标志
	 */
	private String sbelongflag;

	/**
	 * 预算级次代码 S_BUDGETLEVELCODE CHARACTER , PK , NOT NULL
	 */
	private String slevelcode;
	/**
	 * 预算科目 S_BUDGETSUBCODE VARCHAR , PK , NOT NULL
	 */
	private String subcode;
	/**
	 * 预算科目名称 S_BUDGETSUBNAME VARCHAR
	 */
	private String subname;
	/**
	 * 调整期标志 S_TRIMFLAG CHARACTER , NOT NULL
	 */
	private String strimflag;
	/**
	 * 开始时间
	 */
	private Date startdate;
	/**
	 * 结束时间
	 */
	private Date enddate;
	/**
	 * 金额单位
	 */
	private String moneyunit;
	
	/**
	 * 报表类型   日报、月报、旬报、半年报、年报
	 */
	private String reporttype;
	/**
	 * 报表种类  上旬 中旬 下旬  上半年 下半年
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

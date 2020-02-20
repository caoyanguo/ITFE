package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class IncomeReportDto  implements IDto{

	/**
	  * 帐务日期 */
	protected String saccdate;
	/**
	  * 国库名称  */
	protected String strename;
	/**
	  * 收款国库名称  */
	protected String srecvtrename;
	/**
	 * 征收机关名称  */
	protected String staxorgname;
	/**
	 * 级次  */
	protected String sbudgetlevelcode;
	/**
	 * 预算种类  */
	protected String sbudgettype;
	/**
	 * 辅助标志  */
	protected String sassitsign;
	/**
	 * 科目代码  */
	protected String sbudgetsubcode;
	/**
	 * 科目名称  */
	protected String sbudgetsubname;
	/**
	 * 本日收入  */
	protected BigDecimal nmoney;
	
	public String getSrecvtrename() {
		return srecvtrename;
	}
	public void setSrecvtrename(String srecvtrename) {
		this.srecvtrename = srecvtrename;
	}
	public String getStrename() {
		return strename;
	}
	public void setStrename(String strename) {
		this.strename = strename;
	}
	public String getStaxorgname() {
		return staxorgname;
	}
	public void setStaxorgname(String staxorgname) {
		this.staxorgname = staxorgname;
	}
	public BigDecimal getNmoney() {
		return nmoney;
	}
	public void setNmoney(BigDecimal nmoney) {
		this.nmoney = nmoney;
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
	public String getSaccdate() {
		return saccdate;
	}
	public void setSaccdate(String saccdate) {
		this.saccdate = saccdate;
	}
	public String getSbudgettype() {
		return sbudgettype;
	}
	public void setSbudgettype(String sbudgettype) {
		this.sbudgettype = sbudgettype;
	}
	public String getSbudgetlevelcode() {
		return sbudgetlevelcode;
	}
	public void setSbudgetlevelcode(String sbudgetlevelcode) {
		this.sbudgetlevelcode = sbudgetlevelcode;
	}
	public String getSassitsign() {
		return sassitsign;
	}
	public void setSassitsign(String sassitsign) {
		this.sassitsign = sassitsign;
	}
	public String getSbudgetsubcode() {
		return sbudgetsubcode;
	}
	public void setSbudgetsubcode(String sbudgetsubcode) {
		this.sbudgetsubcode = sbudgetsubcode;
	}
	public String getSbudgetsubname() {
		return sbudgetsubname;
	}
	public void setSbudgetsubname(String sbudgetsubname) {
		this.sbudgetsubname = sbudgetsubname;
	}
}

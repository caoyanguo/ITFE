package com.cfcc.itfe.facade.data;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class BizDataCountDto implements IDto{
	private String sorgname;
	private String bizname;
	private String totalcount;
	private BigDecimal totalfamt;
	private String succcount;
	private String detailsucccount;
	private String detailfailcount;
	private String failcount;
	private BigDecimal succfamt;
	private BigDecimal failfamt;
	private String strecode;
	public String getBizname() {
		return bizname;
	}
	public void setBizname(String bizname) {
		this.bizname = bizname;
	}
	public String getTotalcount() {
		return totalcount;
	}
	public void setTotalcount(String totalcount) {
		this.totalcount = totalcount;
	}
	public BigDecimal getTotalfamt() {
		return totalfamt;
	}
	public void setTotalfamt(BigDecimal totalfamt) {
		this.totalfamt = totalfamt;
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
	public String getSorgname() {
		return sorgname;
	}
	public void setSorgname(String sorgname) {
		this.sorgname = sorgname;
	}
	public String getStrecode() {
		return strecode;
	}
	public void setStrecode(String strecode) {
		this.strecode = strecode;
	}
	public String getSucccount() {
		return succcount;
	}
	public void setSucccount(String succcount) {
		this.succcount = succcount;
	}
	public String getFailcount() {
		return failcount;
	}
	public void setFailcount(String failcount) {
		this.failcount = failcount;
	}
	public BigDecimal getSuccfamt() {
		return succfamt;
	}
	public void setSuccfamt(BigDecimal succfamt) {
		this.succfamt = succfamt;
	}
	public BigDecimal getFailfamt() {
		return failfamt;
	}
	public void setFailfamt(BigDecimal failfamt) {
		this.failfamt = failfamt;
	}
	public String getDetailsucccount() {
		return detailsucccount;
	}
	public void setDetailsucccount(String detailsucccount) {
		this.detailsucccount = detailsucccount;
	}
	public String getDetailfailcount() {
		return detailfailcount;
	}
	public void setDetailfailcount(String detailfailcount) {
		this.detailfailcount = detailfailcount;
	}
	
	
	
}

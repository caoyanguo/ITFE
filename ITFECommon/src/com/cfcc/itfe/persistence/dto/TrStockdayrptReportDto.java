package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class TrStockdayrptReportDto implements IDto {

	/**
    * 账户代码 S_ACCNO VARCHAR , PK   , NOT NULL       */
    protected String saccno;
    /**
    * 账户名称 S_ACCNAME VARCHAR         */
    protected String saccname;
    /**
     * 本日余额累计 N_TOTALMONEYTODAY DECIMAL         */
    protected BigDecimal ntotalmoneytoday;
	    
	public String getSaccno() {
		return saccno;
	}

	public void setSaccno(String saccno) {
		this.saccno = saccno;
	}

	public String getSaccname() {
		return saccname;
	}

	public void setSaccname(String saccname) {
		this.saccname = saccname;
	}

	public BigDecimal getNtotalmoneytoday() {
		return ntotalmoneytoday;
	}

	public void setNtotalmoneytoday(BigDecimal ntotalmoneytoday) {
		this.ntotalmoneytoday = ntotalmoneytoday;
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

}

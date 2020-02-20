package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class SummaryReportDto implements IDto {
	
	private static final long serialVersionUID = -962326516824135487L;
	
	private String squerydate;  //日期
	private String sbdgsbtcode; //科目代码
	private Integer icount;     //笔数
	private BigDecimal famt;    //金额
	
	public String getSquerydate() {
		return squerydate;
	}

	public void setSquerydate(String squerydate) {
		this.squerydate = squerydate;
	}

	public String getSbdgsbtcode() {
		return sbdgsbtcode;
	}

	public void setSbdgsbtcode(String sbdgsbtcode) {
		this.sbdgsbtcode = sbdgsbtcode;
	}

	public Integer getIcount() {
		return icount;
	}

	public void setIcount(Integer icount) {
		this.icount = icount;
	}

	public BigDecimal getFamt() {
		return famt;
	}

	public void setFamt(BigDecimal famt) {
		this.famt = famt;
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

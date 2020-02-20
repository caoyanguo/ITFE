package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class ExcelReportDto implements IDto {
	
	/**
    * 核算主体代码 S_BOOKORGCODE CHARACTER   , NOT NULL       */
    protected String sbookorgcode;
    /**
    * 国库主体代码 S_TRECODE CHARACTER   , NOT NULL       */
    protected String strecode;  
	/**
    * 一般预算收入        */
    private BigDecimal incomemoney;
    /**
    * 税收收入          */
    private BigDecimal taxrevenuemoney;
    /**
     * 月份          */
     private String month;
     
     public void setall(String sbookorgcode,String strecode,BigDecimal incomemoney,BigDecimal taxrevenuemoney,String month){
    	 this.sbookorgcode=sbookorgcode;
    	 this.strecode=strecode;
    	 this.incomemoney=incomemoney;
    	 this.taxrevenuemoney=taxrevenuemoney;
    	 this.month=month;
     }
     
	/**
	 * @return the sbookorgcode
	 */
	public String getSbookorgcode() {
		return sbookorgcode;
	}
	/**
	 * @param sbookorgcode the sbookorgcode to set
	 */
	public void setSbookorgcode(String sbookorgcode) {
		this.sbookorgcode = sbookorgcode;
	}
	/**
	 * @return the strecode
	 */
	public String getStrecode() {
		return strecode;
	}
	/**
	 * @param strecode the strecode to set
	 */
	public void setStrecode(String strecode) {
		this.strecode = strecode;
	}
	/**
	 * @return the incomemoney
	 */
	public BigDecimal getIncomemoney() {
		return incomemoney;
	}
	/**
	 * @param incomemoney the incomemoney to set
	 */
	public void setIncomemoney(BigDecimal incomemoney) {
		this.incomemoney = incomemoney;
	}
	/**
	 * @return the taxrevenuemoney
	 */
	public BigDecimal getTaxrevenuemoney() {
		return taxrevenuemoney;
	}
	/**
	 * @param taxrevenuemoney the taxrevenuemoney to set
	 */
	public void setTaxrevenuemoney(BigDecimal taxrevenuemoney) {
		this.taxrevenuemoney = taxrevenuemoney;
	}

	
	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * @param month the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
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

/**
 * 
 */
package com.cfcc.itfe.client.dataquery.interestratemsg;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * @author Administrator
 *
 */
public class InterestrateReportDto implements IDto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bankName;
	private String bankCode;
	private String payAcctCode;
	private String jxAcctCode;
	private String payAcctName;
	private String jxAcctName;
	private BigDecimal payInterestRateCount;
	private BigDecimal jxInterestRateCount;
	private BigDecimal payInterestRates;
	private BigDecimal jxInterestRates;
	private BigDecimal payInterestvalue;
	private BigDecimal jxInterestvalue;
	private String finOrgName;
	
	
	
	

	public String getFinOrgName() {
		return finOrgName;
	}

	public void setFinOrgName(String finOrgName) {
		this.finOrgName = finOrgName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getPayAcctCode() {
		return payAcctCode;
	}

	public void setPayAcctCode(String payAcctCode) {
		this.payAcctCode = payAcctCode;
	}

	public String getJxAcctCode() {
		return jxAcctCode;
	}

	public void setJxAcctCode(String jxAcctCode) {
		this.jxAcctCode = jxAcctCode;
	}

	public String getPayAcctName() {
		return payAcctName;
	}

	public void setPayAcctName(String payAcctName) {
		this.payAcctName = payAcctName;
	}

	public String getJxAcctName() {
		return jxAcctName;
	}

	public void setJxAcctName(String jxAcctName) {
		this.jxAcctName = jxAcctName;
	}

	public BigDecimal getPayInterestRateCount() {
		return payInterestRateCount;
	}

	public void setPayInterestRateCount(BigDecimal payInterestRateCount) {
		this.payInterestRateCount = payInterestRateCount;
	}

	public BigDecimal getJxInterestRateCount() {
		return jxInterestRateCount;
	}

	public void setJxInterestRateCount(BigDecimal jxInterestRateCount) {
		this.jxInterestRateCount = jxInterestRateCount;
	}

	public BigDecimal getPayInterestRates() {
		return payInterestRates;
	}

	public void setPayInterestRates(BigDecimal payInterestRates) {
		this.payInterestRates = payInterestRates;
	}

	public BigDecimal getJxInterestRates() {
		return jxInterestRates;
	}

	public void setJxInterestRates(BigDecimal jxInterestRates) {
		this.jxInterestRates = jxInterestRates;
	}

	public BigDecimal getPayInterestvalue() {
		return payInterestvalue;
	}

	public void setPayInterestvalue(BigDecimal payInterestvalue) {
		this.payInterestvalue = payInterestvalue;
	}

	public BigDecimal getJxInterestvalue() {
		return jxInterestvalue;
	}

	public void setJxInterestvalue(BigDecimal jxInterestvalue) {
		this.jxInterestvalue = jxInterestvalue;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValid()
	 */
	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValid(java.lang.String[])
	 */
	public String checkValid(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValidExcept(java.lang.String[])
	 */
	public String checkValidExcept(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#getChildren()
	 */
	public IDto[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#getPK()
	 */
	public IPK getPK() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#isParent()
	 */
	public boolean isParent() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#setChildren(com.cfcc.jaf.persistence.jaform.parent.IDto[])
	 */
	public void setChildren(IDto[] arg0) {
		// TODO Auto-generated method stub

	}

}

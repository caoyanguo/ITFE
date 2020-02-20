/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * @author Administrator
 *
 */
public class ShanghaiReport2Data implements IDto {
	
	private String srptdate;
	private BigDecimal num1;
	private BigDecimal num2;
	private BigDecimal num3;
	private String sbudgetsubcode;

	public String getSrptdate() {
		return srptdate;
	}

	public void setSrptdate(String srptdate) {
		this.srptdate = srptdate;
	}

	public BigDecimal getNum1() {
		return num1;
	}

	public void setNum1(BigDecimal num1) {
		this.num1 = num1;
	}

	public BigDecimal getNum2() {
		return num2;
	}

	public void setNum2(BigDecimal num2) {
		this.num2 = num2;
	}

	
	

	

	public BigDecimal getNum3() {
		return num3;
	}

	public void setNum3(BigDecimal num3) {
		this.num3 = num3;
	}

	public String getSbudgetsubcode() {
		return sbudgetsubcode;
	}

	public void setSbudgetsubcode(String sbudgetsubcode) {
		this.sbudgetsubcode = sbudgetsubcode;
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

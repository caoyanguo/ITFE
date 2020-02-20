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
public class BudgetsubcodemonthDto implements IDto {

	private String yearcode;
	private String sbudgetsubcode;
	private String sbudgetsubname;
	private BigDecimal yiamt;
	private BigDecimal eramt;
	private BigDecimal sanamt;
	private BigDecimal siamt;
	private BigDecimal wuamt;
	private BigDecimal liuamt;
	private BigDecimal qiamt;
	private BigDecimal baamt;
	private BigDecimal jiuamt;
	private BigDecimal shiamt;
	private BigDecimal shiyiamt;
	private BigDecimal shieramt;
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

	

	public String getYearcode() {
		return yearcode;
	}

	public void setYearcode(String yearcode) {
		this.yearcode = yearcode;
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

	public BigDecimal getYiamt() {
		return yiamt;
	}

	public void setYiamt(BigDecimal yiamt) {
		this.yiamt = yiamt;
	}

	public BigDecimal getEramt() {
		return eramt;
	}

	public void setEramt(BigDecimal eramt) {
		this.eramt = eramt;
	}

	public BigDecimal getSanamt() {
		return sanamt;
	}

	public void setSanamt(BigDecimal sanamt) {
		this.sanamt = sanamt;
	}

	public BigDecimal getSiamt() {
		return siamt;
	}

	public void setSiamt(BigDecimal siamt) {
		this.siamt = siamt;
	}

	public BigDecimal getWuamt() {
		return wuamt;
	}

	public void setWuamt(BigDecimal wuamt) {
		this.wuamt = wuamt;
	}

	public BigDecimal getLiuamt() {
		return liuamt;
	}

	public void setLiuamt(BigDecimal liuamt) {
		this.liuamt = liuamt;
	}

	public BigDecimal getQiamt() {
		return qiamt;
	}

	public void setQiamt(BigDecimal qiamt) {
		this.qiamt = qiamt;
	}

	public BigDecimal getBaamt() {
		return baamt;
	}

	public void setBaamt(BigDecimal baamt) {
		this.baamt = baamt;
	}

	public BigDecimal getJiuamt() {
		return jiuamt;
	}

	public void setJiuamt(BigDecimal jiuamt) {
		this.jiuamt = jiuamt;
	}


	public BigDecimal getShiamt() {
		return shiamt;
	}

	public void setShiamt(BigDecimal shiamt) {
		this.shiamt = shiamt;
	}

	public BigDecimal getShiyiamt() {
		return shiyiamt;
	}

	public void setShiyiamt(BigDecimal shiyiamt) {
		this.shiyiamt = shiyiamt;
	}

	public BigDecimal getShieramt() {
		return shieramt;
	}

	public void setShieramt(BigDecimal shieramt) {
		this.shieramt = shieramt;
	}

	
}

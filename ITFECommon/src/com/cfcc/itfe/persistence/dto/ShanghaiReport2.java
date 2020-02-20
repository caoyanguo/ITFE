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
public class ShanghaiReport2 implements IDto {
	
	private String srptdate;
	private String sbudgetsubcode;
	private String sbudgetsubname;
	private BigDecimal nmoneyday;
	
	

	public String getSrptdate() {
		return srptdate;
	}

	public void setSrptdate(String srptdate) {
		this.srptdate = srptdate;
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

	public BigDecimal getNmoneyday() {
		return nmoneyday;
	}

	public void setNmoneyday(BigDecimal nmoneyday) {
		this.nmoneyday = nmoneyday;
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

package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class BusinessStatDto implements IDto{

	/**
	  * 帐务日期 */
	protected String saccdate;
	/**
	  * 业务量  */
	protected Long nbusinessnum;
	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}
	public String checkValid(String[] names) {
		// TODO Auto-generated method stub
		return null;
	}
	public String checkValidExcept(String[] names) {
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
	public void setChildren(IDto[] dtos) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	public String getSaccdate() {
		return saccdate;
	}
	public void setSaccdate(String saccdate) {
		this.saccdate = saccdate;
	}
	public Long getNbusinessnum() {
		return nbusinessnum;
	}
	public void setNbusinessnum(Long nbusinessnum) {
		this.nbusinessnum = nbusinessnum;
	}
	
}

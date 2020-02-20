package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class AddColumnPayOutDto extends TvPayoutmsgmainDto{

	/**
	  * 预算科目代码 */
	protected String sfunsubjectcode;
	/**
	  * 经济科目代码  */
	protected String  secnomicsubjectcode;
	/**
	  * 经济科目代码  */
	protected String  sbiztype;
	
	public String getSfunsubjectcode() {
		return sfunsubjectcode;
	}
	public void setSfunsubjectcode(String sfunsubjectcode) {
		this.sfunsubjectcode = sfunsubjectcode;
	}
	public String getSecnomicsubjectcode() {
		return secnomicsubjectcode;
	}
	public void setSecnomicsubjectcode(String secnomicsubjectcode) {
		this.secnomicsubjectcode = secnomicsubjectcode;
	}
	public String getSbiztype() {
		return sbiztype;
	}
	public void setSbiztype(String sbiztype) {
		this.sbiztype = sbiztype;
	}
	
	
	
	
	
}

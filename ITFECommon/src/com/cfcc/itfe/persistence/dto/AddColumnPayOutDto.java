package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class AddColumnPayOutDto extends TvPayoutmsgmainDto{

	/**
	  * Ԥ���Ŀ���� */
	protected String sfunsubjectcode;
	/**
	  * ���ÿ�Ŀ����  */
	protected String  secnomicsubjectcode;
	/**
	  * ���ÿ�Ŀ����  */
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

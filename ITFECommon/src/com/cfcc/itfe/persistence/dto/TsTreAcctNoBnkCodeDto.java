package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class TsTreAcctNoBnkCodeDto implements IDto {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 核算主体代码 S_ORGCODE VARCHAR , PK   , NOT NULL       */
    protected String sorgcode;
    /**
     * 对应支付系统行号 S_PAYBANKNO VARCHAR         */
    protected String spaybankno;
    /**
	 * 会计账号 S_BOOKACCT VARCHAR , PK   , NOT NULL       */
	protected String sbookacct;
	/**
	 * 会计账户名称 S_BOOKACCTNAME VARCHAR   , NOT NULL       */
	protected String sbookacctname;
	
	public String getSorgcode() {
		return sorgcode;
	}

	public void setSorgcode(String sorgcode) {
		this.sorgcode = sorgcode;
	}

	public String getSpaybankno() {
		return spaybankno;
	}

	public void setSpaybankno(String spaybankno) {
		this.spaybankno = spaybankno;
	}

	public String getSbookacct() {
		return sbookacct;
	}

	public void setSbookacct(String sbookacct) {
		this.sbookacct = sbookacct;
	}

	public String getSbookacctname() {
		return sbookacctname;
	}

	public void setSbookacctname(String sbookacctname) {
		this.sbookacctname = sbookacctname;
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

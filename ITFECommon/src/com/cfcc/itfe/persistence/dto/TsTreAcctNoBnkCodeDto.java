package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class TsTreAcctNoBnkCodeDto implements IDto {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * ����������� S_ORGCODE VARCHAR , PK   , NOT NULL       */
    protected String sorgcode;
    /**
     * ��Ӧ֧��ϵͳ�к� S_PAYBANKNO VARCHAR         */
    protected String spaybankno;
    /**
	 * ����˺� S_BOOKACCT VARCHAR , PK   , NOT NULL       */
	protected String sbookacct;
	/**
	 * ����˻����� S_BOOKACCTNAME VARCHAR   , NOT NULL       */
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

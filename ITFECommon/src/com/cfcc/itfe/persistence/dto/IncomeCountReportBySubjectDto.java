package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class IncomeCountReportBySubjectDto implements IDto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ��Ŀ����  */
	protected String sbudgetsubcode;
	/**
	 * ��Ŀ����  */
	protected String sbudgetsubname;
	/**
    *  ���ڽ��  */
    protected BigDecimal nmoneycurrent;
    /**
    *  ����ͬ������   */
    protected BigDecimal dmoneygrowth;
    /**
    *  ��������������        */
    protected BigDecimal dmoneycontribution;
    /**
    *  ����ִ�н��ռ��         */
    protected BigDecimal dmoneyratio;
    /**
     *  �����ۼƽ��  */
     protected BigDecimal nmoneyyear;
     /**
     *  ����ͬ������   */
     protected BigDecimal dmoneyyeargrowth;
     /**
     *  ��������������        */
     protected BigDecimal dmoneyyearcontribution;
     /**
     *  ����ִ�н��ռ��         */
     protected BigDecimal dmoneyyearratio;

    
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

	public BigDecimal getNmoneycurrent() {
		return nmoneycurrent;
	}

	public void setNmoneycurrent(BigDecimal nmoneycurrent) {
		this.nmoneycurrent = nmoneycurrent;
	}

	public BigDecimal getDmoneygrowth() {
		return dmoneygrowth;
	}

	public void setDmoneygrowth(BigDecimal dmoneygrowth) {
		this.dmoneygrowth = dmoneygrowth;
	}

	public BigDecimal getDmoneycontribution() {
		return dmoneycontribution;
	}

	public void setDmoneycontribution(BigDecimal dmoneycontribution) {
		this.dmoneycontribution = dmoneycontribution;
	}

	public BigDecimal getDmoneyratio() {
		return dmoneyratio;
	}

	public void setDmoneyratio(BigDecimal dmoneyratio) {
		this.dmoneyratio = dmoneyratio;
	}

	
	
	public BigDecimal getNmoneyyear() {
		return nmoneyyear;
	}

	public void setNmoneyyear(BigDecimal nmoneyyear) {
		this.nmoneyyear = nmoneyyear;
	}

	public BigDecimal getDmoneyyeargrowth() {
		return dmoneyyeargrowth;
	}

	public void setDmoneyyeargrowth(BigDecimal dmoneyyeargrowth) {
		this.dmoneyyeargrowth = dmoneyyeargrowth;
	}

	public BigDecimal getDmoneyyearcontribution() {
		return dmoneyyearcontribution;
	}

	public void setDmoneyyearcontribution(BigDecimal dmoneyyearcontribution) {
		this.dmoneyyearcontribution = dmoneyyearcontribution;
	}

	public BigDecimal getDmoneyyearratio() {
		return dmoneyyearratio;
	}

	public void setDmoneyyearratio(BigDecimal dmoneyyearratio) {
		this.dmoneyyearratio = dmoneyyearratio;
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

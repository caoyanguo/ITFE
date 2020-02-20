package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class IncomeMoneyReportDto implements IDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ��Ŀ����
	 */
	protected String sbudgetsubcode;
	/**
	 * ��Ŀ����
	 */
	protected String sbudgetsubname;
	/**
	 * ���ڽ��
	 */
	protected BigDecimal nmoneycurrent;
	/**
	 * �����ۼƽ��
	 */
	protected BigDecimal nmoneyyear;
	/**
	 * �����ۼ�ͬ������
	 */
	protected BigDecimal nmoneyincremental;
	/**
	 * �����ۼ�ͬ������
	 */
	protected BigDecimal dmoneygrowthrate;

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

	public BigDecimal getNmoneyyear() {
		return nmoneyyear;
	}

	public void setNmoneyyear(BigDecimal nmoneyyear) {
		this.nmoneyyear = nmoneyyear;
	}

	public BigDecimal getNmoneyincremental() {
		return nmoneyincremental;
	}

	public void setNmoneyincremental(BigDecimal nmoneyincremental) {
		this.nmoneyincremental = nmoneyincremental;
	}

	public BigDecimal getDmoneygrowthrate() {
		return dmoneygrowthrate;
	}

	public void setDmoneygrowthrate(BigDecimal dmoneygrowthrate) {
		this.dmoneygrowthrate = dmoneygrowthrate;
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

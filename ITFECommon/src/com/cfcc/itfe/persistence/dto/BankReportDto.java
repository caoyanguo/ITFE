package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class BankReportDto implements IDto{
	//����������
	private String spaybankname;
	//ֱ��֧��ҵ�����
	private int directpaynum;
	//ֱ��֧�����
	private BigDecimal directpayamt;
	//Ԥ���ڲ���ҵ�����
	private int payoutnum;
	//Ԥ���ڲ�����
	private BigDecimal payoutamt;
	//��Ȩ֧��ҵ�����
	private int grantpaynum;
	//��Ȩ֧�����
	private BigDecimal grantpayamt;
	//ҵ�񿨱���
	private int workcardnum;
	//ҵ�񿨽��
	private BigDecimal workcardamt;
	//�ܱ���
	private int totalnum;
	//�ܽ��
	private BigDecimal totalamt;
	
	public BankReportDto() {
		// TODO Auto-generated constructor stub
	}

	public BankReportDto(String spaybankname, int directpaynum,
			BigDecimal directpayamt, int payoutnum, BigDecimal payoutamt,
			int grantpaynum, BigDecimal grantpayamt, int workcardnum,
			BigDecimal workcardamt, int totalnum, BigDecimal totalamt) {
		super();
		this.spaybankname = spaybankname;
		this.directpaynum = 0;
		this.directpayamt = new BigDecimal(0.00);
		this.payoutnum = 0;
		this.payoutamt = new BigDecimal(0.00);
		this.grantpaynum = 0;
		this.grantpayamt = new BigDecimal(0.00);
		this.workcardnum = 0;
		this.workcardamt = new BigDecimal(0.00);
		this.totalnum = totalnum;
		this.totalamt = new BigDecimal(0.00);
	}

	public int getDirectpaynum() {
		return directpaynum;
	}

	public void setDirectpaynum(int directpaynum) {
		this.directpaynum = directpaynum;
	}

	public BigDecimal getDirectpayamt() {
		return directpayamt;
	}

	public void setDirectpayamt(BigDecimal directpayamt) {
		this.directpayamt = directpayamt;
	}

	public int getPayoutnum() {
		return payoutnum;
	}

	public void setPayoutnum(int payoutnum) {
		this.payoutnum = payoutnum;
	}

	public BigDecimal getPayoutamt() {
		return payoutamt;
	}

	public void setPayoutamt(BigDecimal payoutamt) {
		this.payoutamt = payoutamt;
	}

	public int getGrantpaynum() {
		return grantpaynum;
	}

	public void setGrantpaynum(int grantpaynum) {
		this.grantpaynum = grantpaynum;
	}

	public BigDecimal getGrantpayamt() {
		return grantpayamt;
	}

	public void setGrantpayamt(BigDecimal grantpayamt) {
		this.grantpayamt = grantpayamt;
	}

	public int getWorkcardnum() {
		return workcardnum;
	}

	public void setWorkcardnum(int workcardnum) {
		this.workcardnum = workcardnum;
	}

	public BigDecimal getWorkcardamt() {
		return workcardamt;
	}

	public void setWorkcardamt(BigDecimal workcardamt) {
		this.workcardamt = workcardamt;
	}

	public int getTotalnum() {
		return totalnum;
	}

	public void setTotalnum(int totalnum) {
		this.totalnum = totalnum;
	}

	public BigDecimal getTotalamt() {
		return totalamt;
	}

	public void setTotalamt(BigDecimal totalamt) {
		this.totalamt = totalamt;
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

	public void setSpaybankname(String spaybankname) {
		this.spaybankname = spaybankname;
	}

	public String getSpaybankname() {
		return spaybankname;
	}

}

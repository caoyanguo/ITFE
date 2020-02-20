package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class PayreckCountDto implements IDto{
	private String orgcode;//机关代码
	private String trecode;//国库代码
	private String clearaccno;//清算账号
	private String clearaccname;//清算账户名称
	private String fundtypecode;//资金性质编码
	private String fundtypename;//资金性质名称
	private String paytypecode;//支付方式编码
	private String paytypename;//支付方式名称
	private BigDecimal payamt;//清算金额
	private BigDecimal amt;//新增额度
	
	
	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	public String getTrecode() {
		return trecode;
	}

	public void setTrecode(String trecode) {
		this.trecode = trecode;
	}

	public String getClearaccno() {
		return clearaccno;
	}

	public void setClearaccno(String clearaccno) {
		this.clearaccno = clearaccno;
	}

	public String getClearaccname() {
		return clearaccname;
	}

	public void setClearaccname(String clearaccname) {
		this.clearaccname = clearaccname;
	}

	public String getFundtypecode() {
		return fundtypecode;
	}

	public void setFundtypecode(String fundtypecode) {
		this.fundtypecode = fundtypecode;
	}

	public String getFundtypename() {
		return fundtypename;
	}

	public void setFundtypename(String fundtypename) {
		this.fundtypename = fundtypename;
	}

	public String getPaytypecode() {
		return paytypecode;
	}

	public void setPaytypecode(String paytypecode) {
		this.paytypecode = paytypecode;
	}

	public String getPaytypename() {
		return paytypename;
	}

	public void setPaytypename(String paytypename) {
		this.paytypename = paytypename;
	}

	public BigDecimal getPayamt() {
		return payamt;
	}

	public void setPayamt(BigDecimal payamt) {
		this.payamt = payamt;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public String checkValid() {
		return null;
	}

	public String checkValid(String[] arg0) {
		return null;
	}

	public String checkValidExcept(String[] arg0) {
		return null;
	}

	public IDto[] getChildren() {
		return null;
	}

	public IPK getPK() {
		return null;
	}

	public boolean isParent() {
		return false;
	}

	public void setChildren(IDto[] arg0) {
		
	}

}

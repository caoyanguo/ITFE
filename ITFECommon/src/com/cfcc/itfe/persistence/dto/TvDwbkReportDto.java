package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class TvDwbkReportDto implements IDto {
	/**
	 * 凭证流水号 I_VOUSRLNO BIGINT , PK , NOT NULL
	 */
	protected Long ivousrlno;
	/**
	 * 退库凭证号 S_DWBKVOUCODE VARCHAR , NOT NULL
	 */
	protected String sdwbkvoucode;
	/**
	 * 征收机关代码 S_TAXORGCODE VARCHAR , NOT NULL
	 */
	protected String staxorgcode;
	/**
	 *收款人代码 S_PAYEECODE VARCHAR
	 */
	protected String spayeecode;
	/**
	 * 征收机关代码 S_TAXORGCODE VARCHAR , NOT NULL
	 */
	protected String staxorgname;
	/**
	 * 付款国库代码 S_PAYERTRECODE CHARACTER , NOT NULL
	 */
	protected String spayertrecode;
	/**
	 * 目的国库代码 S_AIMTRECODE CHARACTER , NOT NULL
	 */
	protected String saimtrecode;
	/**
	 * 预算种类 C_BDGKIND CHARACTER , NOT NULL
	 */
	protected String cbdgkind;
	/**
	 * 预算科目代码 S_BDGSBTCODE VARCHAR , NOT NULL
	 */
	protected String sbdgsbtcode;
	/**
	 * 退库原因代码 S_DWBKREASONCODE VARCHAR , NOT NULL
	 */
	protected String sdwbkreasoncode;
	/**
	 * 退回标志 C_BCKFLAG CHARACTER , NOT NULL
	 */
	protected String cbckflag;
	/**
	 * 收款人账号 S_PAYEEACCT VARCHAR , NOT NULL
	 */
	protected String spayeeacct;
	/**
	 * 收款人名称 S_PAYEENAME VARCHAR , NOT NULL
	 */
	protected String spayeename;
	/**
	 * 收款人开户行行号 S_PAYEEOPNBNKNO VARCHAR , NOT NULL
	 */
	protected String spayeeopnbnkno;
	/**
	 * 交易状态 S_STATUS CHARACTER
	 */
	protected String sstatus;
	/**
	 * 金额 F_AMT DECIMAL , NOT NULL
	 */
	protected BigDecimal famt;
	/**
	 * 导入文件名 S_FILENAME VARCHAR , NOT NULL
	 */
	protected String sfilename;
	/**
	 * 付款国库名称
	 */
	protected String spayertrename;

	public String getSpayertrename() {
		return spayertrename;
	}

	public void setSpayertrename(String spayertrename) {
		this.spayertrename = spayertrename;
	}

	public String getSfilename() {
		return sfilename;
	}

	public void setSfilename(String sfilename) {
		this.sfilename = sfilename;
	}

	public Long getIvousrlno() {
		return ivousrlno;
	}

	public void setIvousrlno(Long ivousrlno) {
		this.ivousrlno = ivousrlno;
	}

	public String getSdwbkvoucode() {
		return sdwbkvoucode;
	}

	public void setSdwbkvoucode(String sdwbkvoucode) {
		this.sdwbkvoucode = sdwbkvoucode;
	}

	public String getStaxorgcode() {
		return staxorgcode;
	}

	public void setStaxorgcode(String staxorgcode) {
		this.staxorgcode = staxorgcode;
	}

	public String getStaxorgname() {
		return staxorgname;
	}

	public void setStaxorgname(String staxorgname) {
		this.staxorgname = staxorgname;
	}

	public String getSpayertrecode() {
		return spayertrecode;
	}

	public void setSpayertrecode(String spayertrecode) {
		this.spayertrecode = spayertrecode;
	}

	public String getSaimtrecode() {
		return saimtrecode;
	}

	public void setSaimtrecode(String saimtrecode) {
		this.saimtrecode = saimtrecode;
	}

	public String getCbdgkind() {
		return cbdgkind;
	}

	public void setCbdgkind(String cbdgkind) {
		this.cbdgkind = cbdgkind;
	}

	public String getSbdgsbtcode() {
		return sbdgsbtcode;
	}

	public void setSbdgsbtcode(String sbdgsbtcode) {
		this.sbdgsbtcode = sbdgsbtcode;
	}

	public String getSdwbkreasoncode() {
		return sdwbkreasoncode;
	}

	public void setSdwbkreasoncode(String sdwbkreasoncode) {
		this.sdwbkreasoncode = sdwbkreasoncode;
	}

	public String getCbckflag() {
		return cbckflag;
	}

	public void setCbckflag(String cbckflag) {
		this.cbckflag = cbckflag;
	}

	public String getSpayeeacct() {
		return spayeeacct;
	}

	public void setSpayeeacct(String spayeeacct) {
		this.spayeeacct = spayeeacct;
	}

	public String getSpayeename() {
		return spayeename;
	}

	public void setSpayeename(String spayeename) {
		this.spayeename = spayeename;
	}

	public String getSpayeeopnbnkno() {
		return spayeeopnbnkno;
	}

	public void setSpayeeopnbnkno(String spayeeopnbnkno) {
		this.spayeeopnbnkno = spayeeopnbnkno;
	}

	public String getSstatus() {
		return sstatus;
	}

	public void setSstatus(String sstatus) {
		this.sstatus = sstatus;
	}

	public BigDecimal getFamt() {
		return famt;
	}

	public void setFamt(BigDecimal famt) {
		this.famt = famt;
	}

	public String getSpayeecode() {
		return spayeecode;
	}

	public void setSpayeecode(String spayeecode) {
		this.spayeecode = spayeecode;
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

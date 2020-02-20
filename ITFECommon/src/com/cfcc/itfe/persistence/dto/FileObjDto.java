package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class FileObjDto implements IDto {
	
	/**
	 * 日期
	 */
	private String sdate;
	/**
	 * 批次
	 */
	private String sbatch;
	/**
	 * 业务类型
	 */
	private String sbiztype;
	/**
	 * 调整期标志
	 */
	private String ctrimflag;
	/**
	 * 发送标志
	 */
	private String ssendflag;
	/**
	 * 报文编号
	 */
	private String smsgno;
	
	/**
	 * 国库代码
	 */
	private String strecode;
	
	/**
	 * 机关代码
	 */
	private String staxorgcode;
	
	/**
	 * 包流水号
	 */
	private String spackno;
	
	/**
	 * 数据来源标志
	 */
	private String csourceflag;

	public String getSdate() {
		return sdate;
	}

	public void setSdate(String sdate) {
		this.sdate = sdate;
	}

	public String getSbatch() {
		return sbatch;
	}

	public void setSbatch(String sbatch) {
		this.sbatch = sbatch;
	}

	public String getSbiztype() {
		return sbiztype;
	}

	public void setSbiztype(String sbiztype) {
		this.sbiztype = sbiztype;
	}

	public String getCtrimflag() {
		return ctrimflag;
	}

	public void setCtrimflag(String ctrimflag) {
		this.ctrimflag = ctrimflag;
	}

	public String getSsendflag() {
		return ssendflag;
	}

	public void setSsendflag(String ssendflag) {
		this.ssendflag = ssendflag;
	}

	public String getSmsgno() {
		return smsgno;
	}

	public void setSmsgno(String smsgno) {
		this.smsgno = smsgno;
	}

	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
	}

	public String getStaxorgcode() {
		return staxorgcode;
	}

	public void setStaxorgcode(String staxorgcode) {
		this.staxorgcode = staxorgcode;
	}

	public String getCsourceflag() {
		return csourceflag;
	}

	public void setCsourceflag(String csourceflag) {
		this.csourceflag = csourceflag;
	}

	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSpackno() {
		return spackno;
	}

	public void setSpackno(String spackno) {
		this.spackno = spackno;
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

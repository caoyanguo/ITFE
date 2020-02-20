package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

@SuppressWarnings("serial")
public class StatusOfReportDownLoad implements IDto {
	/**
	 * 
	 */
	private String sdates;
	private String strecode;
	private String strename;
	private String sshuipiao;
	private String sliushui;
	private String sribao;
	private String skucun;
	private String szhichu;
	private String sext1;
	private String sext2;
	private String sext3;
	private String sishaiguan;
	private Integer ituikucount;
	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
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

	public String getSdates() {
		return sdates;
	}

	public void setSdates(String sdates) {
		this.sdates = sdates;
	}

	public String getSshuipiao() {
		return sshuipiao;
	}

	public void setSshuipiao(String sshuipiao) {
		this.sshuipiao = sshuipiao;
	}

	public String getSliushui() {
		return sliushui;
	}

	public void setSliushui(String sliushui) {
		this.sliushui = sliushui;
	}

	public String getSribao() {
		return sribao;
	}

	public void setSribao(String sribao) {
		this.sribao = sribao;
	}

	public String getStrename() {
		return strename;
	}

	public void setStrename(String strename) {
		this.strename = strename;
	}

	public String getSkucun() {
		return skucun;
	}

	public void setSkucun(String skucun) {
		this.skucun = skucun;
	}

	public String getSzhichu() {
		return szhichu;
	}

	public void setSzhichu(String szhichu) {
		this.szhichu = szhichu;
	}

	public String getSext1() {
		return sext1;
	}

	public void setSext1(String sext1) {
		this.sext1 = sext1;
	}

	public String getSext2() {
		return sext2;
	}

	public void setSext2(String sext2) {
		this.sext2 = sext2;
	}

	public String getSext3() {
		return sext3;
	}

	public void setSext3(String sext3) {
		this.sext3 = sext3;
	}

	public String getSishaiguan() {
		return sishaiguan;
	}

	public void setSishaiguan(String sishaiguan) {
		this.sishaiguan = sishaiguan;
	}

	public Integer getItuikucount() {
		return ituikucount;
	}

	public void setItuikucount(Integer ituikucount) {
		this.ituikucount = ituikucount;
	}

}

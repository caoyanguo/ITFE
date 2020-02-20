package com.cfcc.itfe.facade.data;

import java.io.Serializable;
import java.math.BigDecimal;


public class NontaxDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6547083904336374264L;

	private String strecode; //收缴国库
	
	private String sfinorgcode; //征收机关
	
	private String spaybankno; //银行代码
	
	private String svoudate; //凭证日期
	
	private String shold1; //缴款识别码
	
	private String shold4; //执收单位名称
	
	private String shold2; //缴款人名称
	
	private String sitemcode; //预算科目代码
	
	private String sxaddword; //预算级次
	
	private String svicesign; //辅助标志
	
	private BigDecimal ntraamt; //金额
	
	private String sstartdate; //起始日期
	
	private String senddate; //结束日期

	public NontaxDto() {
		super();
	}

	public NontaxDto(String strecode, String sfinorgcode, String spaybankno, String svoudate, String shold1, String shold4, String shold2, String sitemcode,
			String sxaddword, String svicesign, BigDecimal ntraamt, String sstartdate, String senddate) {
		super();
		this.strecode = strecode;
		this.sfinorgcode = sfinorgcode;
		this.spaybankno = spaybankno;
		this.svoudate = svoudate;
		this.shold1 = shold1;
		this.shold4 = shold4;
		this.shold2 = shold2;
		this.sitemcode = sitemcode;
		this.sxaddword = sxaddword;
		this.svicesign = svicesign;
		this.ntraamt = ntraamt;
		this.sstartdate = sstartdate;
		this.senddate = senddate;
	}


	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
	}

	public String getSfinorgcode() {
		return sfinorgcode;
	}

	public void setSfinorgcode(String sfinorgcode) {
		this.sfinorgcode = sfinorgcode;
	}

	public String getSpaybankno() {
		return spaybankno;
	}

	public void setSpaybankno(String spaybankno) {
		this.spaybankno = spaybankno;
	}

	public String getSvoudate() {
		return svoudate;
	}

	public void setSvoudate(String svoudate) {
		this.svoudate = svoudate;
	}

	public String getShold1() {
		return shold1;
	}

	public void setShold1(String shold1) {
		this.shold1 = shold1;
	}

	public String getShold4() {
		return shold4;
	}

	public void setShold4(String shold4) {
		this.shold4 = shold4;
	}

	public String getShold2() {
		return shold2;
	}

	public void setShold2(String shold2) {
		this.shold2 = shold2;
	}

	public String getSitemcode() {
		return sitemcode;
	}

	public void setSitemcode(String sitemcode) {
		this.sitemcode = sitemcode;
	}

	public String getSxaddword() {
		return sxaddword;
	}

	public void setSxaddword(String sxaddword) {
		this.sxaddword = sxaddword;
	}

	public String getSvicesign() {
		return svicesign;
	}

	public void setSvicesign(String svicesign) {
		this.svicesign = svicesign;
	}

	public BigDecimal getNtraamt() {
		return ntraamt;
	}

	public void setNtraamt(BigDecimal ntraamt) {
		this.ntraamt = ntraamt;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getSstartdate() {
		return sstartdate;
	}

	public void setSstartdate(String sstartdate) {
		this.sstartdate = sstartdate;
	}

	public String getSenddate() {
		return senddate;
	}

	public void setSenddate(String senddate) {
		this.senddate = senddate;
	}
}

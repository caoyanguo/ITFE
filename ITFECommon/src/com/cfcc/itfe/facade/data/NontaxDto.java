package com.cfcc.itfe.facade.data;

import java.io.Serializable;
import java.math.BigDecimal;


public class NontaxDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6547083904336374264L;

	private String strecode; //�սɹ���
	
	private String sfinorgcode; //���ջ���
	
	private String spaybankno; //���д���
	
	private String svoudate; //ƾ֤����
	
	private String shold1; //�ɿ�ʶ����
	
	private String shold4; //ִ�յ�λ����
	
	private String shold2; //�ɿ�������
	
	private String sitemcode; //Ԥ���Ŀ����
	
	private String sxaddword; //Ԥ�㼶��
	
	private String svicesign; //������־
	
	private BigDecimal ntraamt; //���
	
	private String sstartdate; //��ʼ����
	
	private String senddate; //��������

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

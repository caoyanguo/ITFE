package com.cfcc.itfe.facade.data;

import java.sql.Date;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class TipsParamDto  implements IDto  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sorgcode ; //�������ش���
	private String staxorgcode ;//���ջ��ش���
	private String strecode ; //�������
	private String sbeflag ; //Ͻ����־
	private String ifsub ; //�Ƿ�����¼�
	private Date startdate ;//��ʼ����
	private Date enddate ;//��������
	private String paymode;//֧����ʽ
	private String sbankcode;//��������
	private String exptype ; //���ÿ���ʽ����ø�����ʽ
	private String sbudgettype;//Ԥ������
	private String datatype;//��������0��������1��������
	public TipsParamDto(String sorgcode, String staxorgcode, String strecode,
			String sbeflag, String ifsub, Date startdate, Date enddate, String exptype,String sbudgettype) {
		super();
		this.sorgcode = sorgcode;
		this.staxorgcode = staxorgcode;
		this.strecode = strecode;
		this.sbeflag = sbeflag;
		this.ifsub = ifsub;
		this.startdate = startdate;
		this.enddate = enddate;
		this.exptype = exptype;
		this.sbudgettype=sbudgettype;
	}

	public String getSorgcode() {
		return sorgcode;
	}

	public void setSorgcode(String sorgcode) {
		this.sorgcode = sorgcode;
	}

	public String getStaxorgcode() {
		return staxorgcode;
	}

	public void setStaxorgcode(String staxorgcode) {
		this.staxorgcode = staxorgcode;
	}

	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
	}

	public String getSbeflag() {
		return sbeflag;
	}

	public void setSbeflag(String sbeflag) {
		this.sbeflag = sbeflag;
	}

	public String getIfsub() {
		return ifsub;
	}

	public void setIfsub(String ifsub) {
		this.ifsub = ifsub;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public String getExptype() {
		return exptype;
	}

	public void setExptype(String exptype) {
		this.exptype = exptype;
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

	public String getPaymode() {
		return paymode;
	}

	public void setPaymode(String paymode) {
		this.paymode = paymode;
	}

	public String getSbankcode() {
		return sbankcode;
	}

	public void setSbankcode(String sbankcode) {
		this.sbankcode = sbankcode;
	}
	public String getSbudgettype() {
		return sbudgettype;
	}

	public void setSbudgettype(String sbudgettype) {
		this.sbudgettype = sbudgettype;
	}

	public TipsParamDto() {
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

}

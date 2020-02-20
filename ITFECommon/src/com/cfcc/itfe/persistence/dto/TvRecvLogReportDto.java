/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * ��ʾ������־ͳ����Ϣʹ�õ�Dto
 * @author sjz
 *
 */
public class TvRecvLogReportDto implements IDto {
	private static final long serialVersionUID = 3767936062457093569L;
	//��������
	private String orgcode;
	//��������
	private String orgname;
	//�����ļ�����
	private String qscode;
	//�����ļ�����
	private String qsname;
	//�����ļ�����
	private int qscount;
	//�˿��ļ�����
	private String tkcode;
	//�˿��ļ�����
	private String tkname;
	//�˿��ļ�����
	private int tkcount;
	//�����ļ����루�����˺������ļ������Է���һ�� 
	private String qtcode;
	//�����ļ�����
	private String qtname;
	//�����ļ�����
	private int qtcount;

	public TvRecvLogReportDto(){
		orgcode = "";
		orgname = "";
		qscode = "QS";
		qsname = "�����ļ�";
		qscount = 0;
		tkcode = "TK";
		tkname = "�˿��ļ�";
		tkcount = 0;
		qtcode = "99";
		qtname = "�����ļ�";
		qtcount = 0;
	}
	
	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public String getQscode() {
		return qscode;
	}

	public void setQscode(String qscode) {
		this.qscode = qscode;
	}

	public String getQsname() {
		return qsname;
	}

	public void setQsname(String qsname) {
		this.qsname = qsname;
	}

	public int getQscount() {
		return qscount;
	}

	public void setQscount(int qscount) {
		this.qscount = qscount;
	}

	public String getTkcode() {
		return tkcode;
	}

	public void setTkcode(String tkcode) {
		this.tkcode = tkcode;
	}

	public String getTkname() {
		return tkname;
	}

	public void setTkname(String tkname) {
		this.tkname = tkname;
	}

	public int getTkcount() {
		return tkcount;
	}

	public void setTkcount(int tkcount) {
		this.tkcount = tkcount;
	}

	public String getQtcode() {
		return qtcode;
	}

	public void setQtcode(String qtcode) {
		this.qtcode = qtcode;
	}

	public String getQtname() {
		return qtname;
	}

	public void setQtname(String qtname) {
		this.qtname = qtname;
	}

	public int getQtcount() {
		return qtcount;
	}

	public void setQtcount(int qtcount) {
		this.qtcount = qtcount;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValid()
	 */
	public String checkValid() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValid(java.lang.String[])
	 */
	public String checkValid(String[] arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValidExcept(java.lang.String[])
	 */
	public String checkValidExcept(String[] arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#getChildren()
	 */
	public IDto[] getChildren() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#getPK()
	 */
	public IPK getPK() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#isParent()
	 */
	public boolean isParent() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#setChildren(com.cfcc.jaf.persistence.jaform.parent.IDto[])
	 */
	public void setChildren(IDto[] arg0) {
	}

}

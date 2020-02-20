/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * 显示接收日志统计信息使用的Dto
 * @author sjz
 *
 */
public class TvRecvLogReportDto implements IDto {
	private static final long serialVersionUID = 3767936062457093569L;
	//机构代码
	private String orgcode;
	//机构名称
	private String orgname;
	//清算文件代码
	private String qscode;
	//清算文件名称
	private String qsname;
	//清算文件数量
	private int qscount;
	//退款文件代码
	private String tkcode;
	//退款文件名称
	private String tkname;
	//退款文件数量
	private int tkcount;
	//其他文件代码（含对账和其他文件，可以放在一起） 
	private String qtcode;
	//其他文件名称
	private String qtname;
	//其他文件数量
	private int qtcount;

	public TvRecvLogReportDto(){
		orgcode = "";
		orgname = "";
		qscode = "QS";
		qsname = "清算文件";
		qscount = 0;
		tkcode = "TK";
		tkname = "退款文件";
		tkcount = 0;
		qtcode = "99";
		qtname = "其他文件";
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

/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * @author Administrator
 *
 */
public class TvInFileTmpDto implements IDto {

	private String strecode;   //国库代码
	private String strecodename;   //国库名称
	private String bankno;       //银行代码
	private String bankname;   //银行名称
	private String biztype;    //业务类型
	private String placename;  //税票类型
	private int sunnum;       //总数
	private BigDecimal sumamt;  //总金额
	
	
	
	
	public String getBiztype() {
		return biztype;
	}

	public void setBiztype(String biztype) {
		this.biztype = biztype;
	}

	public String getPlacename() {
		return placename;
	}

	public void setPlacename(String placename) {
		this.placename = placename;
	}

	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
	}

	public String getStrecodename() {
		return strecodename;
	}

	public void setStrecodename(String strecodename) {
		this.strecodename = strecodename;
	}

	public String getBankno() {
		return bankno;
	}

	public void setBankno(String bankno) {
		this.bankno = bankno;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public int getSunnum() {
		return sunnum;
	}

	public void setSunnum(int sunnum) {
		this.sunnum = sunnum;
	}

	public BigDecimal getSumamt() {
		return sumamt;
	}

	public void setSumamt(BigDecimal sumamt) {
		this.sumamt = sumamt;
	}

	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValid(String[] as) {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValidExcept(String[] as) {
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

	public void setChildren(IDto[] aidto) {
		// TODO Auto-generated method stub
		
	}

	
}

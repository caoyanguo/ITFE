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

	private String strecode;   //�������
	private String strecodename;   //��������
	private String bankno;       //���д���
	private String bankname;   //��������
	private String biztype;    //ҵ������
	private String placename;  //˰Ʊ����
	private int sunnum;       //����
	private BigDecimal sumamt;  //�ܽ��
	
	
	
	
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

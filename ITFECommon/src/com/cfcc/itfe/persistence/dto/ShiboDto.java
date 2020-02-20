/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * @author Administrator
 *
 */
public class ShiboDto implements IDto {
	
	private String payeracct;     //付款人账户
	private String payername;    // 付款人名称
	private String payeraddress;  //付款人地址 
	
	

	public String getPayeracct() {
		return payeracct;
	}

	public void setPayeracct(String payeracct) {
		this.payeracct = payeracct;
	}

	public String getPayername() {
		return payername;
	}

	public void setPayername(String payername) {
		this.payername = payername;
	}

	public String getPayeraddress() {
		return payeraddress;
	}

	public void setPayeraddress(String payeraddress) {
		this.payeraddress = payeraddress;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValid()
	 */
	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValid(java.lang.String[])
	 */
	public String checkValid(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValidExcept(java.lang.String[])
	 */
	public String checkValidExcept(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#getChildren()
	 */
	public IDto[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#getPK()
	 */
	public IPK getPK() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#isParent()
	 */
	public boolean isParent() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#setChildren(com.cfcc.jaf.persistence.jaform.parent.IDto[])
	 */
	public void setChildren(IDto[] arg0) {
		// TODO Auto-generated method stub

	}

}

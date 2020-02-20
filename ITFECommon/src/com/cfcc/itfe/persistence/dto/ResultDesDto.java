package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class ResultDesDto implements IDto{
	private String colName;  //×Ö¶ÎÃû³Æ
	private String colValue; //×Ö¶ÎÖµ
	
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getColValue() {
		return colValue;
	}
	public void setColValue(String colValue) {
		this.colValue = colValue;
	}
	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
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

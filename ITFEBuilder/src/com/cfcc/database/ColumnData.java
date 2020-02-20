package com.cfcc.database;

public class ColumnData {
	private String colid;
	private String colname;
	private int colLeng;
	private String colType;
	private boolean isnull;
	public boolean isIsnull() {
		return isnull;
	}
	public void setIsnull(boolean isnull) {
		this.isnull = isnull;
	}
	public String getColid() {
		return colid;
	}
	public void setColid(String colid) {
		this.colid = colid;
	}
	public String getColname() {
		return colname;
	}
	public void setColname(String colname) {
		this.colname = colname;
	}

	public int getColLeng() {
		return colLeng;
	}
	public void setColLeng(int colLeng) {
		this.colLeng = colLeng;
	}
	public String getColType() {
		return colType;
	}
	public void setColType(String colType) {
		this.colType = colType;
	}
}

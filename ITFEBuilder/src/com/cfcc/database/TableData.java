package com.cfcc.database;

import java.util.List;

public class TableData {
	private String tabID;
	private List<ColumnData> colList;

	public List<ColumnData> getColList() {
		return colList;
	}

	public void setColList(List<ColumnData> colList) {
		this.colList = colList;
	}

	public String getTabID() {
		return tabID;
	}

	public void setTabID(String tabID) {
		this.tabID = tabID;
	}

	private boolean isNull;
	private String tabName;

	private String colName;

	private String colType;
	private String colCName;
	private String dtoName;

	public String getDtoName() {
		return dtoName;
	}

	public void setDtoName(String dtoName) {
		this.dtoName = dtoName;
	}

	public String getColCName() {
		return colCName;
	}

	public void setColCName(String colCName) {
		this.colCName = colCName;
	}

	private int colLength;

	public int getColLength() {
		return colLength;
	}

	public void setColLength(int colLength) {
		this.colLength = colLength;
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColType() {
		return colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

}

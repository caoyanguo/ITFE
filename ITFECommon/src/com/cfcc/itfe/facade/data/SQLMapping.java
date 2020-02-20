package com.cfcc.itfe.facade.data;

import java.util.List;

/**
 * 封装SQL的数据结构
 * 
 */
public class SQLMapping {
	// SQL identity
	private String sqlID;
	public String getFilterKey() {
		return filterKey;
	}

	public void setFilterKey(String filterKey) {
		this.filterKey = filterKey;
	}

	private String filterKey;

	public String getSqlID() {
		return sqlID;
	}

	public void setSqlID(String sqlID) {
		this.sqlID = sqlID;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getOperContent() {
		return operContent;
	}

	public void setOperContent(String operContent) {
		this.operContent = operContent;
	}

	public String getOperSql() {
		return operSql;
	}

	public void setOperSql(String operSql) {
		this.operSql = operSql;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public List<SQLParam> getSqlParams() {
		return sqlParams;
	}

	public void setSqlParams(List<SQLParam> sqlParams) {
		this.sqlParams = sqlParams;
	}

	// 操作类型
	private String operType;

	// 操作的描述
	private String operContent;
	// 操作的SQL
	private String operSql;
	//
	private String sqlType;
	private String tableName;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	// 操作SQL的字段列表
	private List<SQLParam> sqlParams;
}

package com.cfcc.itfe.persistence.dto;

public class TfUnitrecordCustomDto extends TfUnitrecordmainDto {
	/**
    * 预算单位编码 S_AGENCYCODE VARCHAR   , NOT NULL       */
    protected String sagencycode;
    /**
    * 预算单位名称 S_AGENCYNAME VARCHAR   , NOT NULL       */
    protected String sagencyname;
	public String getSagencycode() {
		return sagencycode;
	}
	public void setSagencycode(String sagencycode) {
		this.sagencycode = sagencycode;
	}
	public String getSagencyname() {
		return sagencyname;
	}
	public void setSagencyname(String sagencyname) {
		this.sagencyname = sagencyname;
	}
    
}

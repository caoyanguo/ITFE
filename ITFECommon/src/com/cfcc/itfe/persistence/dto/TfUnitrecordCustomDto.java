package com.cfcc.itfe.persistence.dto;

public class TfUnitrecordCustomDto extends TfUnitrecordmainDto {
	/**
    * Ԥ�㵥λ���� S_AGENCYCODE VARCHAR   , NOT NULL       */
    protected String sagencycode;
    /**
    * Ԥ�㵥λ���� S_AGENCYNAME VARCHAR   , NOT NULL       */
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

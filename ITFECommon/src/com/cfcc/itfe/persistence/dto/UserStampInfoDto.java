package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class UserStampInfoDto extends TsUserstampDto {

	/**
	 * �û�����
	 */
	private String susername;;
	/**
	 * ǩ����������
	 */
	private String sstamptypename;
	/**
	 * ģ��ID S_MODELID VARCHAR , PK , NOT NULL
	 */
	private String smodelid;
	/**
	 * ����ʶ S_FORMID VARCHAR , NOT NULL
	 */
	private String sformid;
	/**
	 * ǩ��λ�ñ�ʶ S_PLACEID VARCHAR , PK , NOT NULL
	 */
	private String splaceid;
	/**
	 * ǩ��λ������ S_PLACETYPE CHARACTER , NOT NULL
	 */
	private String splacetype;
	
	/**
	 * ǩ��λ������ S_PLACEDESC VARCHAR
	 */
	private String splacedesc;
	
	private String soperationtypename;
	public String getSoperationtypename() {
		return soperationtypename;
	}

	public void setSoperationtypename(String soperationtypename) {
		this.soperationtypename = soperationtypename;
	}

	public String getSmodelid() {
		return smodelid;
	}

	public void setSmodelid(String smodelid) {
		this.smodelid = smodelid;
	}

	public String getSformid() {
		return sformid;
	}

	public void setSformid(String sformid) {
		this.sformid = sformid;
	}

	public String getSplaceid() {
		return splaceid;
	}

	public void setSplaceid(String splaceid) {
		this.splaceid = splaceid;
	}

	public String getSplacetype() {
		return splacetype;
	}

	public void setSplacetype(String splacetype) {
		this.splacetype = splacetype;
	}


	public String getSplacedesc() {
		return splacedesc;
	}

	public void setSplacedesc(String splacedesc) {
		this.splacedesc = splacedesc;
	}

	

	public String getSusername() {
		return susername;
	}

	public void setSusername(String susername) {
		this.susername = susername;
	}

	public String getSstamptypename() {
		return sstamptypename;
	}

	public void setSstamptypename(String sstamptypename) {
		this.sstamptypename = sstamptypename;
	}

}

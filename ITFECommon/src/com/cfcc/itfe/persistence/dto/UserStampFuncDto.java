/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

/**
 * �û�����Ȩ����Ϣ
 * �û���ÿ������ӡ�������ܸ��µ�λ��id
 * @author sjz
 * 
 */
public class UserStampFuncDto extends TsUserstampDto {
	private static final long serialVersionUID = 5868166593313242993L;
	/**
	 * ģ��ID S_MODELID VARCHAR , PK , NOT NULL
	 */
	protected String smodelid;
	/**
	 * ǩ��λ�ñ�ʶ S_PLACEID VARCHAR , PK , NOT NULL
	 */
	protected String splaceid;
	
	public String getSmodelid() {
		return smodelid;
	}
	public void setSmodelid(String smodelid) {
		this.smodelid = smodelid;
	}
	public String getSplaceid() {
		return splaceid;
	}
	public void setSplaceid(String splaceid) {
		this.splaceid = splaceid;
	}

}

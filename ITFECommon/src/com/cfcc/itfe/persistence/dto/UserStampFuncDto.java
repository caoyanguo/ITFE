/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

/**
 * 用户盖章权限信息
 * 用户的每个电子印鉴，所能盖章的位置id
 * @author sjz
 * 
 */
public class UserStampFuncDto extends TsUserstampDto {
	private static final long serialVersionUID = 5868166593313242993L;
	/**
	 * 模版ID S_MODELID VARCHAR , PK , NOT NULL
	 */
	protected String smodelid;
	/**
	 * 签章位置标识 S_PLACEID VARCHAR , PK , NOT NULL
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

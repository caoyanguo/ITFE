/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import java.sql.Timestamp;

/**
 * 显示接收日志使用的Dto
 * @author sjz
 *
 */
public class TvRecvLogShowDto extends TvRecvlogDto {
	private static final long serialVersionUID = 6632497006422948567L;
	//发送流水号
	private String sendno;
	//发送机构名称
	private String sorgname;
	//业务类型名称
	private String soperationtypename;
	//机构代码
	private String sorgcode;
	//发送时间
	private Timestamp ssendtime; 
	public void setSsendtime(Timestamp ssendtime) {
		this.ssendtime = ssendtime;
	}
	//操作类型
	private String sopertype;
	public String getSopertype() {
		return sopertype;
	}
	public void setSopertype(String sopertype) {
		this.sopertype = sopertype;
	}
//	public String getSsendtime() {
//		return ssendtime;
//	}
//	public void setSsendtime(String ssendtime) {
//		this.ssendtime = ssendtime;
//	}
	public String getSorgcode() {
		return sorgcode;
	}
	public void setSorgcode(String sorgcode) {
		this.sorgcode = sorgcode;
	}
	public String getSorgname() {
		return sorgname;
	}
	public void setSorgname(String sendOrgName) {
		this.sorgname = sendOrgName;
	}
	public String getSoperationtypename() {
		return soperationtypename;
	}
	public void setSoperationtypename(String operationTypeName) {
		this.soperationtypename = operationTypeName;
	}
	public Timestamp getSsendtime() {
		return ssendtime;
	}
	public String getSendno() {
		return sendno;
	}
	public void setSendno(String sendno) {
		this.sendno = sendno;
	}
	
	
}

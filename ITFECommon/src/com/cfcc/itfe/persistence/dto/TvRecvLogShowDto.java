/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import java.sql.Timestamp;

/**
 * ��ʾ������־ʹ�õ�Dto
 * @author sjz
 *
 */
public class TvRecvLogShowDto extends TvRecvlogDto {
	private static final long serialVersionUID = 6632497006422948567L;
	//������ˮ��
	private String sendno;
	//���ͻ�������
	private String sorgname;
	//ҵ����������
	private String soperationtypename;
	//��������
	private String sorgcode;
	//����ʱ��
	private Timestamp ssendtime; 
	public void setSsendtime(Timestamp ssendtime) {
		this.ssendtime = ssendtime;
	}
	//��������
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

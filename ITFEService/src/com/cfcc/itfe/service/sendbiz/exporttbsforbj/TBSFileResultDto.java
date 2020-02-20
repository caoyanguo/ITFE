package com.cfcc.itfe.service.sendbiz.exporttbsforbj;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**TBS��ִ�ļ�����������Dto**/
public class TBSFileResultDto implements IDto{
	private static final long serialVersionUID = 1L;
	/**�������**/
	private String treCode;
	/**���������к�**/
	private String sagentbnkcode;
	/**��������**/
	private String acctDate;
	/**ƾ֤���**/
	private String vouno;
	/**�������˺�**/
	private String payerAcct;
	/**֧������ ����ʵ������1��һ��֧����2������,���ڼ���֧������1ֱ��2��Ȩ**/
	private String payKind;
	/**���**/
	private BigDecimal famt = new BigDecimal("0.00");
	/**С���ֽ�����**/
	private BigDecimal smallFamt = new BigDecimal("0.00");
	/**֧��ԭ�����**/
	private String payReason;
	/**Ԥ�㼶��**/
	private String cbdglevel;
	/**�˻ر�־**/
	private String backFlag;
	/**ҵ������ 01 02 17 23 25 26 27 28**/
	private String bizType;
	/**��������(ƾ֤��)**/
	private String vtcode;
	/**��Ӧҵ���**/
	private String tableName;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getVtcode() {
		return vtcode;
	}
	public void setVtcode(String vtcode) {
		this.vtcode = vtcode;
	}
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	public String getTreCode() {
		return treCode;
	}
	public void setTreCode(String treCode) {
		this.treCode = treCode;
	}
	public String getSagentbnkcode() {
		return sagentbnkcode;
	}
	public void setSagentbnkcode(String sagentbnkcode) {
		this.sagentbnkcode = sagentbnkcode;
	}
	public String getAcctDate() {
		return acctDate;
	}
	public void setAcctDate(String acctDate) {
		this.acctDate = acctDate;
	}
	public String getVouno() {
		return vouno;
	}
	public void setVouno(String vouno) {
		this.vouno = vouno;
	}
	public String getPayerAcct() {
		return payerAcct;
	}
	public void setPayerAcct(String payerAcct) {
		this.payerAcct = payerAcct;
	}
	public String getPayKind() {
		return payKind;
	}
	public void setPayKind(String payKind) {
		this.payKind = payKind;
	}
	public BigDecimal getFamt() {
		return famt;
	}
	public void setFamt(BigDecimal famt) {
		this.famt = famt;
	}
	public BigDecimal getSmallFamt() {
		return smallFamt;
	}
	public void setSmallFamt(BigDecimal smallFamt) {
		this.smallFamt = smallFamt;
	}
	public String getPayReason() {
		return payReason;
	}
	public void setPayReason(String payReason) {
		this.payReason = payReason;
	}
	public String getCbdglevel() {
		return cbdglevel;
	}
	public void setCbdglevel(String cbdglevel) {
		this.cbdglevel = cbdglevel;
	}
	public String getBackFlag() {
		return backFlag;
	}
	public void setBackFlag(String backFlag) {
		this.backFlag = backFlag;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	public String checkValid() {
		return null;
	}
	public String checkValid(String[] names) {
		return null;
	}
	public String checkValidExcept(String[] names) {
		return null;
	}
	public IDto[] getChildren() {
		return null;
	}
	public IPK getPK() {
		return null;
	}
	public boolean isParent() {
		return false;
	}
	public void setChildren(IDto[] dtos) {
	}
}

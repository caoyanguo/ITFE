/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * @author Administrator
 *
 */
public class VoucherVerifyDto implements IDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * �����ֶ�
	 */

	/**
     * ����������� 12λ����, NOT NULL       
     */
	private String orgcode;
	/**
     * ����������� 10λ����, NOT NULL       
     */
	private String trecode;
	
	/**
     * ����������� ������12λ����, NOT NULL       
     */
	private String finorgcode;
	
	/**
     * ƾ֤���� NOT NULL       
     */
	private String voudate;
	
	/**
     * ���д��� 12λ���� NOT NULL
     */
	private String paybankno;
	
	/**
     * ��������  NOT NULL
     */
	private String paybankname;
	
   //��ҵ������У���ֶ�
	/**
     * ƾ֤��� NOT NULL       
     */
	private String voucherno;
	
	/**
     * ֧����ʽ NOT NULL������Ϊ11����12   
     */
	private String paytypecode;
	
	/**
     * �տ������˺ţ���ӦTIPS�տ����˺ţ� NOT NULL���32λ��  
     */
	private String agentAcctNo;
	
	/**
     * �տ������˻����ƣ���ӦTIPS�տ������ƣ� NOT NULL���60λ   
     */
	private String agentAcctName;
	
	/**
     * �����˺ţ���ӦTIPS�������˺ţ� NOT NULL���32λ   
     */
	private String clearAcctNo;	
	
	/**
     * �����˻����ƣ���ӦTIPS���������ƣ� NOT NULL���60λ   
     */
	private String clearAcctName;
	//�����������
	private String clearAcctBankName;
	
	/**
	 * ������
	 */
	private String famt;
	
	/**
     * ������ȣ�4λ����   
     */
	private String ofyear;
	
	/**
     * �����·ݣ�2λ����   
     */
	private String ofmonth;
	
	/**
     * Ԥ�㵥λ���� NOT NULL���32λ   
     */
	private String agencyName;
			
	/**
     * �ʽ����ʱ��� NOT NULL������Ϊ1   
     */
	private String fundTypeCode;
	
	/**
     * Ԥ��������� NOT NULL������Ϊ1   
     */
	private String budgettype; 
	/****************����Ϊ�Ϻ���չУ���ֶ�****************/
	
	/**
     * ҵ�����ͱ���(1-���ʣ�3-����ҵ�� 4-����)���1λ����
     */
	private String businessTypeCode;
	
	/**
     * ҵ����������,�30λ
     */
	private String businessTypeName;
	
	/**
     * ���Ķ�Ӧ��ԭƾ֤���� NOT NULL���4λ   
     */
	private String originalVtCode;
	
	/**
     * ֧��ƾ֤�ţ�����ƾ֤�ţ� NOT NULL
     */
	private String PayVoucherNo;
	
	public String getTrecode() {
		return trecode;
	}
	public void setTrecode(String trecode) {
		this.trecode = trecode;
	}
	public String getFinorgcode() {
		return finorgcode;
	}
	public void setFinorgcode(String finorgcode) {
		this.finorgcode = finorgcode;
	}
	public String getVoucherno() {
		return voucherno;
	}
	public void setVoucherno(String voucherno) {
		this.voucherno = voucherno;
	}
	public String getVoudate() {
		return voudate;
	}
	public void setVoudate(String voudate) {
		this.voudate = voudate;
	}
	public String getPaybankno() {
		return paybankno;
	}
	public void setPaybankno(String paybankno) {
		this.paybankno = paybankno;
	}
	public String getPaytypecode() {
		return paytypecode;
	}
	public void setPaytypecode(String paytypecode) {
		this.paytypecode = paytypecode;
	}
	public String getAgentAcctNo() {
		return agentAcctNo;
	}
	public void setAgentAcctNo(String agentAcctNo) {
		this.agentAcctNo = agentAcctNo;
	}
	public String getAgentAcctName() {
		return agentAcctName;
	}
	public void setAgentAcctName(String agentAcctName) {
		this.agentAcctName = agentAcctName;
	}
	public String getClearAcctNo() {
		return clearAcctNo;
	}
	public void setClearAcctNo(String clearAcctNo) {
		this.clearAcctNo = clearAcctNo;
	}
	public String getClearAcctName() {
		return clearAcctName;
	}
	public void setClearAcctName(String clearAcctName) {
		this.clearAcctName = clearAcctName;
	}
	public String getOfyear() {
		return ofyear;
	}
	public void setOfyear(String ofyear) {
		this.ofyear = ofyear;
	}
	public String getOfmonth() {
		return ofmonth;
	}
	public void setOfmonth(String ofmonth) {
		this.ofmonth = ofmonth;
	}
	public String getFamt() {
		return famt;
	}
	public void setFamt(String famt) {
		this.famt = famt;
	}
	public String getPaybankname() {
		return paybankname;
	}
	public void setPaybankname(String paybankname) {
		this.paybankname = paybankname;
	}
	public String checkValid(String[] arg0) {
		return null;
	}
	public String checkValidExcept(String[] arg0) {
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
	public void setChildren(IDto[] arg0) {
		
	}
	public String checkValid() {
		return null;
	}
	public String getAgencyName() {
		return agencyName;
	}
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}
	public String getBusinessTypeCode() {
		return businessTypeCode;
	}
	public void setBusinessTypeCode(String businessTypeCode) {
		this.businessTypeCode = businessTypeCode;
	}
	public String getBusinessTypeName() {
		return businessTypeName;
	}
	public void setBusinessTypeName(String businessTypeName) {
		this.businessTypeName = businessTypeName;
	}
	public String getFundTypeCode() {
		return fundTypeCode;
	}
	public void setFundTypeCode(String fundTypeCode) {
		this.fundTypeCode = fundTypeCode;
	}
	public String getOriginalVtCode() {
		return originalVtCode;
	}
	public void setOriginalVtCode(String originalVtCode) {
		this.originalVtCode = originalVtCode;
	}
	public String getPayVoucherNo() {
		return PayVoucherNo;
	}
	public void setPayVoucherNo(String payVoucherNo) {
		PayVoucherNo = payVoucherNo;
	}
	
	public static void main(String[] args){
		
		Pattern pattern=Pattern.compile("[0-9]{10}");//ƥ��10λ����
		Matcher match1=pattern.matcher("010000000");
		if(match1.matches()==true){
			System.out.println("ƥ����ȷ");
		}else{
			System.out.println("ƥ�����");
		}
		
		Pattern pattern2=Pattern.compile("[0-9]{1,12}");//���12λ����
		Matcher match2=pattern2.matcher("2");
		if(match2.matches()==true){
			System.out.println("ƥ����ȷ");
		}else{
			System.out.println("ƥ�����");
		}
	}
	public void setBudgettype(String budgettype) {
		this.budgettype = budgettype;
	}
	public String getBudgettype() {
		return budgettype;
	}
	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}
	public String getOrgcode() {
		return orgcode;
	}
	public String getClearAcctBankName() {
		return clearAcctBankName;
	}
	public void setClearAcctBankName(String clearAcctBankName) {
		this.clearAcctBankName = clearAcctBankName;
	}
	
}

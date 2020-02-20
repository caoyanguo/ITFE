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
	 * 公共字段
	 */

	/**
     * 核算主体代码 12位数字, NOT NULL       
     */
	private String orgcode;
	/**
     * 国库主体代码 10位数字, NOT NULL       
     */
	private String trecode;
	
	/**
     * 财政代码代码 不超过12位数字, NOT NULL       
     */
	private String finorgcode;
	
	/**
     * 凭证日期 NOT NULL       
     */
	private String voudate;
	
	/**
     * 银行代码 12位数字 NOT NULL
     */
	private String paybankno;
	
	/**
     * 银行名称  NOT NULL
     */
	private String paybankname;
	
   //分业务类型校验字段
	/**
     * 凭证编号 NOT NULL       
     */
	private String voucherno;
	
	/**
     * 支付方式 NOT NULL，必须为11或者12   
     */
	private String paytypecode;
	
	/**
     * 收款银行账号（对应TIPS收款人账号） NOT NULL，最长32位；  
     */
	private String agentAcctNo;
	
	/**
     * 收款银行账户名称（对应TIPS收款人名称） NOT NULL，最长60位   
     */
	private String agentAcctName;
	
	/**
     * 付款账号（对应TIPS付款人账号） NOT NULL，最长32位   
     */
	private String clearAcctNo;	
	
	/**
     * 付款账户名称（对应TIPS付款人名称） NOT NULL，最长60位   
     */
	private String clearAcctName;
	//付款开户行名称
	private String clearAcctBankName;
	
	/**
	 * 发生额
	 */
	private String famt;
	
	/**
     * 所属年度，4位数字   
     */
	private String ofyear;
	
	/**
     * 所属月份，2位数字   
     */
	private String ofmonth;
	
	/**
     * 预算单位名称 NOT NULL，最长32位   
     */
	private String agencyName;
			
	/**
     * 资金性质编码 NOT NULL，必须为1   
     */
	private String fundTypeCode;
	
	/**
     * 预算种类编码 NOT NULL，必须为1   
     */
	private String budgettype; 
	/****************以下为上海扩展校验字段****************/
	
	/**
     * 业务类型编码(1-单笔，3-工资业务 4-批量)，最长1位数字
     */
	private String businessTypeCode;
	
	/**
     * 业务类型名称,最长30位
     */
	private String businessTypeName;
	
	/**
     * 报文对应的原凭证类型 NOT NULL，最长4位   
     */
	private String originalVtCode;
	
	/**
     * 支付凭证号（调整凭证号） NOT NULL
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
		
		Pattern pattern=Pattern.compile("[0-9]{10}");//匹配10位数字
		Matcher match1=pattern.matcher("010000000");
		if(match1.matches()==true){
			System.out.println("匹配正确");
		}else{
			System.out.println("匹配错误");
		}
		
		Pattern pattern2=Pattern.compile("[0-9]{1,12}");//最多12位数字
		Matcher match2=pattern2.matcher("2");
		if(match2.matches()==true){
			System.out.println("匹配正确");
		}else{
			System.out.println("匹配错误");
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

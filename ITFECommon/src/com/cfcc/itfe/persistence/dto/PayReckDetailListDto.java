package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class PayReckDetailListDto implements IDto {
	/*
	 * ֧��ƾ֤��
	 */
	protected String VoucherNo;
	/*
	 * һ��Ԥ�㵥λ����
	 */
	protected String SupDepCode;
	/*
	 * һ��Ԥ�㵥λ����
	 */
	protected String SupDepName;
	/*
	 * ����Ԥ�㵥λ����
	 */
	protected String baseDepCode;
	/*
	 * ����Ԥ�㵥λ����
	 */
	protected String baseDepName;
	/*
	 * Ԥ���Ŀ����
	 */
	protected String ExpFuncCode;
	/*
	 * Ԥ���Ŀ����
	 */
	protected String ExpFuncName;
	/*
	 * ����������
	 */
	protected String ClearAcctName;
	/*
	 * �����˿������к�
	 */
	protected String ClearAcctBankCode;
	/*
	 * �����˿���������
	 */
	protected String ClearAcctBankName;
	/*
	 * �տ�������
	 */
	protected String AgentAcctName;
	/*
	 * �տ����˺�
	 */
	protected String AgentAcctcode;
	/*
	 * �տ��˿������к�
	 */
	protected String AgentAcctBankCode;
	/*
	 * �տ��˿���������
	 */
	protected String AgentAcctBankName;
	/*
	 * �տ�����֯��������
	 */
	protected String AgentAcctOrgCode;
	/*
	 * ���
	 */
	protected String PlanAmt;
	
	public String getVoucherNo() {
		return VoucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		VoucherNo = voucherNo;
	}

	public String getSupDepCode() {
		return SupDepCode;
	}

	public void setSupDepCode(String supDepCode) {
		SupDepCode = supDepCode;
	}

	public String getSupDepName() {
		return SupDepName;
	}

	public void setSupDepName(String supDepName) {
		SupDepName = supDepName;
	}

	public String getBaseDepCode() {
		return baseDepCode;
	}

	public void setBaseDepCode(String baseDepCode) {
		this.baseDepCode = baseDepCode;
	}

	public String getBaseDepName() {
		return baseDepName;
	}

	public void setBaseDepName(String baseDepName) {
		this.baseDepName = baseDepName;
	}

	public String getExpFuncCode() {
		return ExpFuncCode;
	}

	public void setExpFuncCode(String expFuncCode) {
		ExpFuncCode = expFuncCode;
	}

	public String getExpFuncName() {
		return ExpFuncName;
	}

	public void setExpFuncName(String expFuncName) {
		ExpFuncName = expFuncName;
	}

	public String getClearAcctName() {
		return ClearAcctName;
	}

	public void setClearAcctName(String clearAcctName) {
		ClearAcctName = clearAcctName;
	}

	public String getClearAcctBankCode() {
		return ClearAcctBankCode;
	}

	public void setClearAcctBankCode(String clearAcctBankCode) {
		ClearAcctBankCode = clearAcctBankCode;
	}

	public String getClearAcctBankName() {
		return ClearAcctBankName;
	}

	public void setClearAcctBankName(String clearAcctBankName) {
		ClearAcctBankName = clearAcctBankName;
	}

	public String getAgentAcctName() {
		return AgentAcctName;
	}

	public void setAgentAcctName(String agentAcctName) {
		AgentAcctName = agentAcctName;
	}

	public String getAgentAcctBankCode() {
		return AgentAcctBankCode;
	}

	public void setAgentAcctBankCode(String agentAcctBankCode) {
		AgentAcctBankCode = agentAcctBankCode;
	}

	public String getAgentAcctBankName() {
		return AgentAcctBankName;
	}

	public void setAgentAcctBankName(String agentAcctBankName) {
		AgentAcctBankName = agentAcctBankName;
	}

	public String getAgentAcctOrgCode() {
		return AgentAcctOrgCode;
	}

	public void setAgentAcctOrgCode(String agentAcctOrgCode) {
		AgentAcctOrgCode = agentAcctOrgCode;
	}

	public String getPlanAmt() {
		return PlanAmt;
	}

	public void setPlanAmt(String planAmt) {
		PlanAmt = planAmt;
	}

	public String checkValid() {
		return null;
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

	public String getAgentAcctcode() {
		return AgentAcctcode;
	}

	public void setAgentAcctcode(String agentAcctcode) {
		AgentAcctcode = agentAcctcode;
	}
	
}

package com.cfcc.itfe.persistence.dto;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * ��������ƾ֤У��(8202|5201 �� 2301)
 * 
 * @author hua
 * 
 */
public class BankValidateDto implements IDto {
	private static final long serialVersionUID = 1L;

	private String sagentbnkcode; // ��������
	private String dacctdate; // ��������
	private String voucherno; // ����ƾ֤���
	private String bizType; // ҵ������
	private String detailvoucherno; // ��ϸƾ֤�ţ�Ψһ��
	private String result; // �ȶԽ��
	private String supdepcode; // Ԥ�㵥λ
	private String expfunccode; //��Ŀ����
	private String payamt;//���
	
	public String getSagentbnkcode() {
		return sagentbnkcode;
	}

	public void setSagentbnkcode(String sagentbnkcode) {
		this.sagentbnkcode = sagentbnkcode;
	}

	public String getDacctdate() {
		return dacctdate;
	}

	public void setDacctdate(String dacctdate) {
		this.dacctdate = dacctdate;
	}

	public String getVoucherno() {
		return voucherno;
	}

	public void setVoucherno(String voucherno) {
		this.voucherno = voucherno;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getDetailvoucherno() {
		return detailvoucherno;
	}

	public void setDetailvoucherno(String detailvoucherno) {
		this.detailvoucherno = detailvoucherno;
	}

	public String getSupdepcode() {
		return supdepcode;
	}

	public void setSupdepcode(String supdepcode) {
		this.supdepcode = supdepcode;
	}

	public String getExpfunccode() {
		return expfunccode;
	}

	public void setExpfunccode(String expfunccode) {
		this.expfunccode = expfunccode;
	}

	public String getResult() {
		if (StateConstant.MERGE_VALIDATE_SUCCESS.equals(result)) {
			return "�ȶԳɹ�";
		} else if (StateConstant.MERGE_VALIDATE_FAILURE.equals(result)) {
			return "�ȶ�ʧ��";
		} else if (StateConstant.MERGE_VALIDATE_NOTCOMPARE.equals(result) || StringUtils.isBlank(result)) {
			return "δ�ȶ�";
		}
		return result;
	}

	public String getRealResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "MapTest [bizType=" + bizType + ", dacctdate=" + dacctdate + ", voucherNo=" + voucherno + ", result=" + result + ", sagentbnkcode=" + sagentbnkcode + ", detailVoucherNo=" + detailvoucherno + ", supdepcode=" + supdepcode + ", expfunccode=" + expfunccode + ", payamt=" + payamt + "]";
	}

	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValid(String[] names) {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValidExcept(String[] names) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDto[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPK getPK() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isParent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setChildren(IDto[] dtos) {
		// TODO Auto-generated method stub

	}

	public String getPayamt() {
		return payamt;
	}

	public void setPayamt(String payamt) {
		this.payamt = payamt;
	}
	
	
}

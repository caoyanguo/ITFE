package com.cfcc.itfe.persistence.dto;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * 用于商行凭证校验(8202|5201 和 2301)
 * 
 * @author hua
 * 
 */
public class BankValidateDto implements IDto {
	private static final long serialVersionUID = 1L;

	private String sagentbnkcode; // 代理银行
	private String dacctdate; // 账务日期
	private String voucherno; // 主表凭证编号
	private String bizType; // 业务类型
	private String detailvoucherno; // 明细凭证号（唯一）
	private String result; // 比对结果
	private String supdepcode; // 预算单位
	private String expfunccode; //科目代码
	private String payamt;//金额
	
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
			return "比对成功";
		} else if (StateConstant.MERGE_VALIDATE_FAILURE.equals(result)) {
			return "比对失败";
		} else if (StateConstant.MERGE_VALIDATE_NOTCOMPARE.equals(result) || StringUtils.isBlank(result)) {
			return "未比对";
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

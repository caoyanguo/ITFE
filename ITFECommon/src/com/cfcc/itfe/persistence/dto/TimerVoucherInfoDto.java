package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * 用于客户端定时提醒凭证处理情况
 * 
 * @author hua
 * 
 */
public class TimerVoucherInfoDto implements IDto {
	
	private static final long serialVersionUID = 1L;
	private String svtcode; // 业务类型
	private String sbizname; // 业务类型名称
	private String strecode;//国库代码
	private String startDate; // 开始日期
	private String endDate; // 结束日期
	private BigDecimal totalamt; // 总金额
	private String hold1;//备用
	private String hold2;//备用
	private int count1; // 校验成功数量
	private int count2; // 处理中数量
	private int count3; // 处理失败数量
	private int count4; // 已回单数量

	public BigDecimal getTotalamt() {
		return totalamt;
	}

	public void setTotalamt(BigDecimal totalamt) {
		this.totalamt = totalamt;
	}

	public String getSvtcode() {
		return svtcode;
	}

	public void setSvtcode(String svtcode) {
		this.svtcode = svtcode;
	}

	public String getSbizname() {
		return sbizname;
	}

	public void setSbizname(String sbizname) {
		this.sbizname = sbizname;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getCount1() {
		return count1;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public int getCount3() {
		return count3;
	}

	public void setCount3(int count3) {
		this.count3 = count3;
	}

	public int getCount4() {
		return count4;
	}

	public void setCount4(int count4) {
		this.count4 = count4;
	}
	
	@Override
	public String toString() {
		return "TimerVoucherInfoDto [svtcode=" + svtcode + ", sbizname=" + sbizname+",totalamt="+totalamt + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", count1=" + count1 + ", count2=" + count2 + ", count3=" + count3 + ", count4=" + count4 +",hold1="+hold1+",hold2="+hold2+ "]";
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

	public String getHold1() {
		return hold1;
	}

	public void setHold1(String hold1) {
		this.hold1 = hold1;
	}

	public String getHold2() {
		return hold2;
	}

	public void setHold2(String hold2) {
		this.hold2 = hold2;
	}

	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
	}

}

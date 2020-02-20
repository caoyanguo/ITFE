package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class StockCountAnalysicsReportDto implements IDto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 年度  */
	protected String syear;
	/**
	 * 平均值  */
	protected BigDecimal naveragevalue;
	/**
    *  最大值  */
    protected BigDecimal nmaximumvalue;
    /**
    *  最小值  */
    protected BigDecimal nminimumvalue;
    /**
    *  标准差  */
    protected BigDecimal nstandarddeviation;
    /**
    *  偏度    */
    protected BigDecimal nskewness;
    /**
     * 峰度    */
     protected BigDecimal nkurtosis;


	public String getSyear() {
		return syear;
	}

	public void setSyear(String syear) {
		this.syear = syear;
	}

	public BigDecimal getNaveragevalue() {
		return naveragevalue;
	}

	public void setNaveragevalue(BigDecimal naveragevalue) {
		this.naveragevalue = naveragevalue;
	}

	public BigDecimal getNmaximumvalue() {
		return nmaximumvalue;
	}

	public void setNmaximumvalue(BigDecimal nmaximumvalue) {
		this.nmaximumvalue = nmaximumvalue;
	}

	public BigDecimal getNminimumvalue() {
		return nminimumvalue;
	}

	public void setNminimumvalue(BigDecimal nminimumvalue) {
		this.nminimumvalue = nminimumvalue;
	}

	public BigDecimal getNstandarddeviation() {
		return nstandarddeviation;
	}

	public void setNstandarddeviation(BigDecimal nstandarddeviation) {
		this.nstandarddeviation = nstandarddeviation;
	}

	public BigDecimal getNskewness() {
		return nskewness;
	}

	public void setNskewness(BigDecimal nskewness) {
		this.nskewness = nskewness;
	}

	public BigDecimal getNkurtosis() {
		return nkurtosis;
	}

	public void setNkurtosis(BigDecimal nkurtosis) {
		this.nkurtosis = nkurtosis;
	}

	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValid(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValidExcept(String[] arg0) {
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

	public void setChildren(IDto[] arg0) {
		// TODO Auto-generated method stub

	}

}

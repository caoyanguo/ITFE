/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * @author Administrator
 */
public class VoucherCompareDto implements IDto {
	/**
     * 凭证编号
     */
	private String voucherno;
	/**
     * 凭证日期
     */
	private String voudate;
	/**
     * 国库主体代码
     */
	private String trecode;
	
	/**
     * 财政代码  
     */
	private String finorgcode;
	
	/**
     * 付款银行名称
     */
	private String payacctbankname;
	
	/**
     * 收款银行名称  
     */
	private String payeeacctbankname;
	
	/**
     * 付款账号  
     */
	private String payacctno;
	/**
     * 清算金额
     */
	private BigDecimal payamt;
	/**
     * 预算单位编码
     */
	private String supdepcode;
	/**
     * 功能分类科目编码
     */
	private String expfunccode;
	/**
     * 清算额度金额
     */
	private BigDecimal planamt;
	
	/**
	 * 申请划款凭证        
	 */
	private String id;
	
	/**
	 * 行政区划代码  
	 */
	private String admdivcode;
	/**
	 * 所属年度
	 */
	private String styear;
	/**
	 * 计划月份
	 */
	private String setMonth;
	/**
	 * 凭证类型编号     
	 */
	private String vtcode;
	/**
	 * 资金性质编码     
	 */
	private String fundtypecode;
	/**
	 * 资金性质名称     
	 */
	private String fundtypename;
	/**
	 * 收款人账号    
     */
	private String payeeacctno;
	/**
	 * 收款人名称     
	 */
	private String payeeacctname;
	/**
	 * 付款人名称     
	 */
	private String payacctname;
	/**
	 * 代理银行编码     
	 */
	private String paybankcode;
	/**
	 * 代理银行名称     
	 */
	private String paybankname;
	/**
	 * 代理银行行号     
	 */
	private String paybankno;
	/**
	 * 预留字段1     
	 */
	private String hold1;
	/**
	 * 预留字段2     
	 */
	private String hold2;
	/**
	 * 一级预算单位数量    
	 */
	private String deptnum;
	/**
	 * 人民银行编码    
	 */
	private String clearbankcode;
	/**
	 * 人民银行名称    
	 */
	private String clearbankname;
	/**
	 * 一级预算单位名称
	 */
	private String supdepname;
	/**
	 * 支出功能分类科目名称   
	 */
	private String expfuncname;
	/**
	 * 支出功能分类科目项名称
	 */
	private String expfuncname3;
	/**
	 * 收支管理编码    
	 */
	private String procatcode;
	/**
	 * 收支管理名称    
	 */
	private String procatname;
	
	/**
     * 清算金额(子表)
     */
	private BigDecimal subpayamt;

	/**
     * 清算额度金额(子表)
     */
	private BigDecimal subplanamt;

	/**
	 * 预留字段1(子表)     
	 */
	private String subhold1;
	/**
	 * 预留字段2(子表)     
	 */
	private String subhold2;
	/**
	 * 预留字段3(子表)     
	 */
	private String subhold3;
	/**
	 * 预留字段4(子表)     
	 */
	private String subhold4;
	
	public String getPayacctbankname() {
		return payacctbankname;
	}
	public void setPayacctbankname(String payacctbankname) {
		this.payacctbankname = payacctbankname;
	}
	public String getPayeeacctbankname() {
		return payeeacctbankname;
	}
	public void setPayeeacctbankname(String payeeacctbankname) {
		this.payeeacctbankname = payeeacctbankname;
	}
	public String getPayacctno() {
		return payacctno;
	}
	public void setPayacctno(String payacctno) {
		this.payacctno = payacctno;
	}
	public BigDecimal getPayamt() {
		return payamt;
	}
	public void setPayamt(BigDecimal payamt) {
		this.payamt = payamt;
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
	public BigDecimal getPlanamt() {
		return planamt;
	}
	public void setPlanamt(BigDecimal planamt) {
		this.planamt = planamt;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAdmdivcode() {
		return admdivcode;
	}
	public void setAdmdivcode(String admdivcode) {
		this.admdivcode = admdivcode;
	}
	public String getStyear() {
		return styear;
	}
	public void setStyear(String styear) {
		this.styear = styear;
	}
	public String getVtcode() {
		return vtcode;
	}
	public void setVtcode(String vtcode) {
		this.vtcode = vtcode;
	}
	public String getFundtypecode() {
		return fundtypecode;
	}
	public void setFundtypecode(String fundtypecode) {
		this.fundtypecode = fundtypecode;
	}
	public String getFundtypename() {
		return fundtypename;
	}
	public void setFundtypename(String fundtypename) {
		this.fundtypename = fundtypename;
	}
	public String getPayeeacctno() {
		return payeeacctno;
	}
	public void setPayeeacctno(String payeeacctno) {
		this.payeeacctno = payeeacctno;
	}
	public String getPayeeacctname() {
		return payeeacctname;
	}
	public void setPayeeacctname(String payeeacctname) {
		this.payeeacctname = payeeacctname;
	}
	public String getPayacctname() {
		return payacctname;
	}
	public void setPayacctname(String payacctname) {
		this.payacctname = payacctname;
	}
	public String getPaybankcode() {
		return paybankcode;
	}
	public void setPaybankcode(String paybankcode) {
		this.paybankcode = paybankcode;
	}
	public String getPaybankname() {
		return paybankname;
	}
	public void setPaybankname(String paybankname) {
		this.paybankname = paybankname;
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
	public String getDeptnum() {
		return deptnum;
	}
	public void setDeptnum(String deptnum) {
		this.deptnum = deptnum;
	}
	public String getClearbankcode() {
		return clearbankcode;
	}
	public void setClearbankcode(String clearbankcode) {
		this.clearbankcode = clearbankcode;
	}
	public String getClearbankname() {
		return clearbankname;
	}
	public void setClearbankname(String clearbankname) {
		this.clearbankname = clearbankname;
	}
	public String getSupdepname() {
		return supdepname;
	}
	public void setSupdepname(String supdepname) {
		this.supdepname = supdepname;
	}
	public String getExpfuncname() {
		return expfuncname;
	}
	public void setExpfuncname(String expfuncname) {
		this.expfuncname = expfuncname;
	}
	public String getExpfuncname3() {
		return expfuncname3;
	}
	public void setExpfuncname3(String expfuncname3) {
		this.expfuncname3 = expfuncname3;
	}
	public String getProcatcode() {
		return procatcode;
	}
	public void setProcatcode(String procatcode) {
		this.procatcode = procatcode;
	}
	public String getProcatname() {
		return procatname;
	}
	public void setProcatname(String procatname) {
		this.procatname = procatname;
	}
	public BigDecimal getSubpayamt() {
		return subpayamt;
	}
	public void setSubpayamt(BigDecimal subpayamt) {
		this.subpayamt = subpayamt;
	}
	public BigDecimal getSubplanamt() {
		return subplanamt;
	}
	public void setSubplanamt(BigDecimal subplanamt) {
		this.subplanamt = subplanamt;
	}
	public String getSubhold1() {
		return subhold1;
	}
	public void setSubhold1(String subhold1) {
		this.subhold1 = subhold1;
	}
	public String getSubhold2() {
		return subhold2;
	}
	public void setSubhold2(String subhold2) {
		this.subhold2 = subhold2;
	}
	public String getSubhold3() {
		return subhold3;
	}
	public void setSubhold3(String subhold3) {
		this.subhold3 = subhold3;
	}
	public String getSubhold4() {
		return subhold4;
	}
	public void setSubhold4(String subhold4) {
		this.subhold4 = subhold4;
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
	public String getPaybankno() {
		return payacctbankname;
	}
	
	public void setPaybankno(String paybankno) {
		this.payacctbankname = paybankno;
	}
	
	public String getAgentacctno() {
		return payeeacctbankname;
	}
	
	public void setAgentacctno(String agentacctno) {
		this.payeeacctbankname = agentacctno;
	}
	
	public String getClearacctno() {
		return payacctno;
	}
	
	public void setClearacctno(String clearacctno) {
		this.payacctno = clearacctno;
	}
	
	public BigDecimal getSumamt() {
		return payamt;
	}
	
	public void setSumamt(BigDecimal sumamt) {
		this.payamt = sumamt;
	}
	
	public String getBudgetunitcode() {
		return supdepcode;
	}
	
	public void setBudgetunitcode(String budgetunitcode) {
		this.supdepcode = budgetunitcode;
	}
	
	public String getFunsubjectcode() {
		return expfunccode;
	}
	
	public void setFunsubjectcode(String funsubjectcode) {
		this.expfunccode = funsubjectcode;
	}
	public BigDecimal getMoney() {
		return planamt;
	}
	public void setMoney(BigDecimal money) {
		this.planamt = money;
	}
	public String getSetMonth() {
		return setMonth;
	}
	public void setSetMonth(String setMonth) {
		this.setMonth = setMonth;
	}
	/*******************************************************
	*
	*  supplementary methods
	*
	*****************************************************/


    /* Indicates whether some other object is "equal to" this one. */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof VoucherCompareDto))
			return false;
		VoucherCompareDto bean = (VoucherCompareDto) obj;

		// compare field voucherno
		if ((this.voucherno == null && bean.voucherno != null)
				|| (this.voucherno != null && bean.voucherno == null))
			return false;
		else if (this.voucherno == null && bean.voucherno == null) {
		} else {
			if (!bean.voucherno.equals(this.voucherno))
				return false;
		}
		// compare field voudate
		if ((this.voudate == null && bean.voudate != null)
				|| (this.voudate != null && bean.voudate == null))
			return false;
		else if (this.voudate == null && bean.voudate == null) {
		} else {
			if (!bean.voudate.equals(this.voudate))
				return false;
		}
		// compare field trecode
		if ((this.trecode == null && bean.trecode != null)
				|| (this.trecode != null && bean.trecode == null))
			return false;
		else if (this.trecode == null && bean.trecode == null) {
		} else {
			if (!bean.trecode.equals(this.trecode))
				return false;
		}
		// compare field finorgcode
		if ((this.finorgcode == null && bean.finorgcode != null)
				|| (this.finorgcode != null && bean.finorgcode == null))
			return false;
		else if (this.finorgcode == null && bean.finorgcode == null) {
		} else {
			if (!bean.finorgcode.equals(this.finorgcode))
				return false;
		}
		// compare field paybankno
		if ((this.payacctbankname == null && bean.payacctbankname != null)
				|| (this.payacctbankname != null && bean.payacctbankname == null))
			return false;
		else if (this.payacctbankname == null && bean.payacctbankname == null) {
		} else {
			if (!bean.payacctbankname.equals(this.payacctbankname))
				return false;
		}
		// compare field agentacctno
		if ((this.payeeacctbankname == null && bean.payeeacctbankname != null)
				|| (this.payeeacctbankname != null && bean.payeeacctbankname == null))
			return false;
		else if (this.payeeacctbankname == null && bean.payeeacctbankname == null) {
		} else {
			if (!bean.payeeacctbankname.equals(this.payeeacctbankname))
				return false;
		}
		//compare field clearacctno
	      if((this.payacctno==null && bean.payacctno!=null) || (this.payacctno!=null && bean.payacctno==null))
	          return false;
	        else if(this.payacctno==null && bean.payacctno==null){
	        }
	        else{
	          if(!bean.payacctno.equals(this.payacctno))
	               return false;
	     }
	      //compare field sumamt
	      if((this.payamt==null && bean.payamt!=null) || (this.payamt!=null && bean.payamt==null))
	          return false;
	        else if(this.payamt==null && bean.payamt==null){
	        }
	        else{
	          if(!bean.payamt.equals(this.payamt))
	               return false;
	     }
	      //compare field budgetunitcode
	      if((this.supdepcode==null && bean.supdepcode!=null) || (this.supdepcode!=null && bean.supdepcode==null))
	          return false;
	        else if(this.supdepcode==null && bean.supdepcode==null){
	        }
	        else{
	          if(!bean.supdepcode.equals(this.supdepcode))
	               return false;
	     }
	      //compare field funsubjectcode
	      if((this.expfunccode==null && bean.expfunccode!=null) || (this.expfunccode!=null && bean.expfunccode==null))
	          return false;
	        else if(this.expfunccode==null && bean.expfunccode==null){
	        }
	        else{
	          if(!bean.expfunccode.equals(this.expfunccode))
	               return false;
	     }
	    //compare field money
	      if((this.planamt==null && bean.planamt!=null) || (this.planamt!=null && bean.planamt==null))
	          return false;
	        else if(this.planamt==null && bean.planamt==null){
	        }
	        else{
	          if(!bean.planamt.equals(this.planamt))
	               return false;
	     }
		return true;
		
		
//		if((this.expfunccode==null && bean.expfunccode!=null) || (this.expfunccode!=null && bean.expfunccode==null))
//	          return false;
//	        else if(this.expfunccode==null && bean.expfunccode==null){
//	        }
//	        else{
//	          if(!bean.expfunccode.equals(this.expfunccode))
//	               return false;
//	     }
	}
	    
	    /* Returns a string representation of the object. */
	    public String toString()
	    {
	        String sep = "; ";
	        StringBuffer sb = new StringBuffer();
	        sb.append("VoucherCompareDto").append(sep);
	        sb.append("[voucherno]").append(" = ").append(voucherno).append(sep);
	        sb.append("[voudate]").append(" = ").append(voudate).append(sep);
	        sb.append("[trecode]").append(" = ").append(trecode).append(sep);
	        sb.append("[finorgcode]").append(" = ").append(finorgcode).append(sep);
	        sb.append("[paybankno]").append(" = ").append(payacctbankname).append(sep);
	        sb.append("[agentacctno]").append(" = ").append(payeeacctbankname).append(sep);
	        sb.append("[clearacctno]").append(" = ").append(payacctno).append(sep);
	        sb.append("[sumamt]").append(" = ").append(payamt).append(sep);
	        sb.append("[budgetunitcode]").append(" = ").append(supdepcode).append(sep);
	        sb.append("[funsubjectcode]").append(" = ").append(expfunccode).append(sep);
	        sb.append("[money]").append(" = ").append(planamt).append(sep);
	            return sb.toString();
	    }

	    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
		public int hashCode()
		{
			int _hash_ = 1;
	        if(this.voucherno!=null)
	          _hash_ = _hash_ * 31+ this.voucherno.hashCode() ;
	        if(this.voudate!=null)
	          _hash_ = _hash_ * 31+ this.voudate.hashCode() ;
	        if(this.trecode!=null)
	          _hash_ = _hash_ * 31+ this.trecode.hashCode() ;
	        if(this.finorgcode!=null)
	          _hash_ = _hash_ * 31+ this.finorgcode.hashCode() ;
	        if(this.payacctbankname!=null)
	          _hash_ = _hash_ * 31+ this.payacctbankname.hashCode() ;
	        if(this.payeeacctbankname!=null)
	          _hash_ = _hash_ * 31+ this.payeeacctbankname.hashCode() ;
	        if(this.payacctno!=null)
	            _hash_ = _hash_ * 31+ this.payacctno.hashCode() ;
	        if(this.payamt!=null)
	           _hash_ = _hash_ * 31+ this.payamt.hashCode() ;
	        if(this.supdepcode!=null)
	           _hash_ = _hash_ * 31+ this.supdepcode.hashCode() ;
	        if(this.expfunccode!=null)
	           _hash_ = _hash_ * 31+ this.expfunccode.hashCode() ;
	        if(this.planamt!=null)
		       _hash_ = _hash_ * 31+ this.planamt.hashCode() ;
			return _hash_;
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
}

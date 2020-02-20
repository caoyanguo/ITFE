package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class TvPayOutFinanceWZReportDto implements IDto{
	 /**
	    * 凭证流水号 I_VOUSRLNO BIGINT , PK   , NOT NULL       */
	    protected Long ivousrlno;
	    /**
	     * 支出凭证类型 0无纸凭证 1有纸凭证 S_PAYOUTVOUTYPE CHARACTER   , NOT NULL       */
	     protected String spayoutvoutype;
	     /**
	     * 凭证编号 S_VOUNO VARCHAR   , NOT NULL       */
	     protected String svouno;
	     /**
	     * 凭证日期 S_VOUDATE CHARACTER   , NOT NULL       */
	     protected String svoudate;
	     /**
	      * 付款行帐号 S_PAYERACCT VARCHAR   , NOT NULL       */
	      protected String spayeracct;
	      /**
	      * 付款行名称 S_PAYERNAME VARCHAR   , NOT NULL       */
	      protected String spayername;
	      /**
	       * 收款人账号 S_PAYEEACCT VARCHAR   , NOT NULL       */
	       protected String spayeeacct;
	       /**
	       * 收款人名称 S_PAYEENAME VARCHAR   , NOT NULL       */
	       protected String spayeename;
	       /**
	       * 收款开户行行号 S_PAYEEOPNBNKNO VARCHAR   , NOT NULL       */
	       protected String spayeeopnbnkno;
	       /**
	       * 交易金额 N_SUBAMT DECIMAL   , NOT NULL       */
	       protected BigDecimal nsubamt;
	       /**
	        * 处理结果 S_RESULT VARCHAR         */
	        protected String sresult;
	        

	public Long getIvousrlno() {
			return ivousrlno;
		}

		public void setIvousrlno(Long ivousrlno) {
			this.ivousrlno = ivousrlno;
		}

		public String getSpayoutvoutype() {
			return spayoutvoutype;
		}

		public void setSpayoutvoutype(String spayoutvoutype) {
			this.spayoutvoutype = spayoutvoutype;
		}

		public String getSvouno() {
			return svouno;
		}

		public void setSvouno(String svouno) {
			this.svouno = svouno;
		}

		public String getSvoudate() {
			return svoudate;
		}

		public void setSvoudate(String svoudate) {
			this.svoudate = svoudate;
		}

		public String getSpayeracct() {
			return spayeracct;
		}

		public void setSpayeracct(String spayeracct) {
			this.spayeracct = spayeracct;
		}

		public String getSpayername() {
			return spayername;
		}

		public void setSpayername(String spayername) {
			this.spayername = spayername;
		}

		public String getSpayeeacct() {
			return spayeeacct;
		}

		public void setSpayeeacct(String spayeeacct) {
			this.spayeeacct = spayeeacct;
		}

		public String getSpayeename() {
			return spayeename;
		}

		public void setSpayeename(String spayeename) {
			this.spayeename = spayeename;
		}

		public String getSpayeeopnbnkno() {
			return spayeeopnbnkno;
		}

		public void setSpayeeopnbnkno(String spayeeopnbnkno) {
			this.spayeeopnbnkno = spayeeopnbnkno;
		}

		public BigDecimal getNsubamt() {
			return nsubamt;
		}

		public void setNsubamt(BigDecimal nsubamt) {
			this.nsubamt = nsubamt;
		}

		public String getSresult() {
			return sresult;
		}

		public void setSresult(String sresult) {
			this.sresult = sresult;
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

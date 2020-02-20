package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class TvPayOutFinanceReportDto implements IDto {
	 /**
	    *  S_SEQNO INTEGER , PK   , NOT NULL       */
	    protected Integer sseqno;
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
	       * 附言 S_ADDWORD VARCHAR         */
	       protected String saddword;
	       /**
	        * 处理结果 S_RESULT VARCHAR         */
	        protected String sresult;
	        /**
	         * 付款行帐号 S_PAYERACCT VARCHAR   , NOT NULL       */
	         protected String spayeracct;
	         /**
	         * 付款行名称 S_PAYERNAME VARCHAR   , NOT NULL       */
	         protected String spayername;
	         protected String strecode;
	         protected String strename;
	         protected String sfilename;
	         protected String sbookorgcode;
	         protected String sorgname;
	         protected Long ivousrlno;
	         protected BigDecimal namt;
	         protected String sbdgorgcode;
	         protected String ssubjectname;
	         
	         
	         
	        
	public Long getIvousrlno() {
				return ivousrlno;
			}

			public void setIvousrlno(Long ivousrlno) {
				this.ivousrlno = ivousrlno;
			}

			public BigDecimal getNamt() {
				return namt;
			}

			public void setNamt(BigDecimal namt) {
				this.namt = namt;
			}

			public String getSbdgorgcode() {
				return sbdgorgcode;
			}

			public void setSbdgorgcode(String sbdgorgcode) {
				this.sbdgorgcode = sbdgorgcode;
			}

			public String getSsubjectname() {
				return ssubjectname;
			}

			public void setSsubjectname(String ssubjectname) {
				this.ssubjectname = ssubjectname;
			}

	public String getSbookorgcode() {
				return sbookorgcode;
			}

			public void setSbookorgcode(String sbookorgcode) {
				this.sbookorgcode = sbookorgcode;
			}

			public String getSorgname() {
				return sorgname;
			}

			public void setSorgname(String sorgname) {
				this.sorgname = sorgname;
			}

	public String getSfilename() {
				return sfilename;
			}

			public void setSfilename(String sfilename) {
				this.sfilename = sfilename;
			}

	public String getStrecode() {
				return strecode;
			}

			public void setStrecode(String strecode) {
				this.strecode = strecode;
			}

			public String getStrename() {
				return strename;
			}

			public void setStrename(String strename) {
				this.strename = strename;
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

	public Integer getSseqno() {
			return sseqno;
		}

		public void setSseqno(Integer sseqno) {
			this.sseqno = sseqno;
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

		public String getSaddword() {
			return saddword;
		}

		public void setSaddword(String saddword) {
			this.saddword = saddword;
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

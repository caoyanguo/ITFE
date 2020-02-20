package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class TvPayOutReportDto implements IDto {
	protected Integer sseqno;//序号
	/**
	    * 付款人帐号 S_PAYERACCT VARCHAR   , NOT NULL       */
	protected String spayeracct;
	    /**
	     * 收款人帐号 S_RECACCT VARCHAR   , NOT NULL       */
	protected String srecacct;
	/**
	    * 经济科目代码 S_ECNOMICSUBJECTCODE VARCHAR         */
	    protected String secnomicsubjectcode;
	    /**
	    * 预算项目代码 S_BUDGETPRJCODE VARCHAR         */
	    protected String sbudgetprjcode;
	    /**
	    * 交易金额 N_MONEY DECIMAL   , NOT NULL       */
	    protected BigDecimal nmoney;
	    /**
	     * 交易状态 S_STATUS CHARACTER   , NOT NULL       */
	     protected String sstatus;
	     /**
	      * 付款人名称 S_PAYERNAME VARCHAR   , NOT NULL       */
	      protected String spayername;
	      /**
	       * 收款人名称 S_RECNAME VARCHAR   , NOT NULL       */
	       protected String srecname;
	       /**
	        * 功能科目代码 S_FUNSUBJECTCODE VARCHAR   , NOT NULL       */
	        protected String sfunsubjectcode;
	        protected String sfilename;
	        protected String organ;
	        protected String sorgname;
	        protected String treasury;
	        protected String strename;
	        protected String sbizno;
	        protected String saddword;
	        
	     
	public String getSaddword() {
				return saddword;
			}

			public void setSaddword(String saddword) {
				this.saddword = saddword;
			}

	public String getSfilename() {
				return sfilename;
			}

			public void setSfilename(String sfilename) {
				this.sfilename = sfilename;
			}

			public String getOrgan() {
				return organ;
			}

			public void setOrgan(String organ) {
				this.organ = organ;
			}

			public String getSorgname() {
				return sorgname;
			}

			public void setSorgname(String sorgname) {
				this.sorgname = sorgname;
			}

			public String getTreasury() {
				return treasury;
			}

			public void setTreasury(String treasury) {
				this.treasury = treasury;
			}

			public String getStrename() {
				return strename;
			}

			public void setStrename(String strename) {
				this.strename = strename;
			}

			public String getSbizno() {
				return sbizno;
			}

			public void setSbizno(String sbizno) {
				this.sbizno = sbizno;
			}

	public String getSpayername() {
			return spayername;
		}

		public void setSpayername(String spayername) {
			this.spayername = spayername;
		}

		public String getSrecname() {
			return srecname;
		}

		public void setSrecname(String srecname) {
			this.srecname = srecname;
		}

		public String getSfunsubjectcode() {
			return sfunsubjectcode;
		}

		public void setSfunsubjectcode(String sfunsubjectcode) {
			this.sfunsubjectcode = sfunsubjectcode;
		}

	public Integer getSseqno() {
			return sseqno;
		}

		public void setSseqno(Integer sseqno) {
			this.sseqno = sseqno;
		}

		public String getSpayeracct() {
			return spayeracct;
		}

		public void setSpayeracct(String spayeracct) {
			this.spayeracct = spayeracct;
		}

		public String getSrecacct() {
			return srecacct;
		}

		public void setSrecacct(String srecacct) {
			this.srecacct = srecacct;
		}

		public String getSecnomicsubjectcode() {
			return secnomicsubjectcode;
		}

		public void setSecnomicsubjectcode(String secnomicsubjectcode) {
			this.secnomicsubjectcode = secnomicsubjectcode;
		}

		public String getSbudgetprjcode() {
			return sbudgetprjcode;
		}

		public void setSbudgetprjcode(String sbudgetprjcode) {
			this.sbudgetprjcode = sbudgetprjcode;
		}

		public BigDecimal getNmoney() {
			return nmoney;
		}

		public void setNmoney(BigDecimal nmoney) {
			this.nmoney = nmoney;
		}

		public String getSstatus() {
			return sstatus;
		}

		public void setSstatus(String sstatus) {
			this.sstatus = sstatus;
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

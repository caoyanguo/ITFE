package com.cfcc.itfe.persistence.dto;


@SuppressWarnings("serial")
public class TvPbcpayReportDto extends TvPbcpayMainDto{
	     /**
	     * 组内序号 I_SEQNO INTEGER , PK   , NOT NULL       */
	     protected Integer iseqno;
	     /**
	     * 预算单位代码 S_BDGORGCODE VARCHAR   , NOT NULL       */
	     protected String sbdgorgcode;
	     /**
	     * 功能类科目代码 S_FUNCSBTCODE VARCHAR   , NOT NULL       */
	     protected String sfuncsbtcode;
	     /**
	     * 经济类科目代码 S_ECOSBTCODE VARCHAR         */
	     protected String secosbtcode;
	     /**
	     * 账户性质 C_ACCTPROP CHARACTER   , NOT NULL       */
	     protected String cacctprop;
	     
	     
		public Integer getIseqno() {
			return iseqno;
		}
		public void setIseqno(Integer iseqno) {
			this.iseqno = iseqno;
		}
		public String getSbdgorgcode() {
			return sbdgorgcode;
		}
		public void setSbdgorgcode(String sbdgorgcode) {
			this.sbdgorgcode = sbdgorgcode;
		}
		public String getSfuncsbtcode() {
			return sfuncsbtcode;
		}
		public void setSfuncsbtcode(String sfuncsbtcode) {
			this.sfuncsbtcode = sfuncsbtcode;
		}
		public String getSecosbtcode() {
			return secosbtcode;
		}
		public void setSecosbtcode(String secosbtcode) {
			this.secosbtcode = secosbtcode;
		}
		public String getCacctprop() {
			return cacctprop;
		}
		public void setCacctprop(String cacctprop) {
			this.cacctprop = cacctprop;
		}

	     
}

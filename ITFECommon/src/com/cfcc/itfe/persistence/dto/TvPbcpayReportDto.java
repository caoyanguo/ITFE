package com.cfcc.itfe.persistence.dto;


@SuppressWarnings("serial")
public class TvPbcpayReportDto extends TvPbcpayMainDto{
	     /**
	     * ������� I_SEQNO INTEGER , PK   , NOT NULL       */
	     protected Integer iseqno;
	     /**
	     * Ԥ�㵥λ���� S_BDGORGCODE VARCHAR   , NOT NULL       */
	     protected String sbdgorgcode;
	     /**
	     * �������Ŀ���� S_FUNCSBTCODE VARCHAR   , NOT NULL       */
	     protected String sfuncsbtcode;
	     /**
	     * �������Ŀ���� S_ECOSBTCODE VARCHAR         */
	     protected String secosbtcode;
	     /**
	     * �˻����� C_ACCTPROP CHARACTER   , NOT NULL       */
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

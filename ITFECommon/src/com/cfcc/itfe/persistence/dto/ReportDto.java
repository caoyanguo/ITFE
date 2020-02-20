package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;

public class ReportDto extends TrIncomedayrptDto{

	/**
	    * 中央级预算收入        */
	    private BigDecimal ncentralmoney;
	    /**
	    * 省级预算收入          */
	    private BigDecimal nprovincemoney;
	    /**
	    * 市级预算收入          */
	    private BigDecimal ncitymoney;
	    /**
	    * 区县级收入        */
	    private BigDecimal ncountymoney;
	    /**
	    * 乡级收入          */
	    private BigDecimal ntownmoney;
		/**
		 * @return the ncentralmoney
		 */
		public BigDecimal getNcentralmoney() {
			return ncentralmoney;
		}
		/**
		 * @param ncentralmoney the ncentralmoney to set
		 */
		public void setNcentralmoney(BigDecimal ncentralmoney) {
			this.ncentralmoney = ncentralmoney;
		}
		/**
		 * @return the nprovincemoney
		 */
		public BigDecimal getNprovincemoney() {
			return nprovincemoney;
		}
		/**
		 * @param nprovincemoney the nprovincemoney to set
		 */
		public void setNprovincemoney(BigDecimal nprovincemoney) {
			this.nprovincemoney = nprovincemoney;
		}
		/**
		 * @return the ncitymoney
		 */
		public BigDecimal getNcitymoney() {
			return ncitymoney;
		}
		/**
		 * @param ncitymoney the ncitymoney to set
		 */
		public void setNcitymoney(BigDecimal ncitymoney) {
			this.ncitymoney = ncitymoney;
		}
		/**
		 * @return the ncountymoney
		 */
		public BigDecimal getNcountymoney() {
			return ncountymoney;
		}
		/**
		 * @param ncountymoney the ncountymoney to set
		 */
		public void setNcountymoney(BigDecimal ncountymoney) {
			this.ncountymoney = ncountymoney;
		}
		/**
		 * @return the ntownmoney
		 */
		public BigDecimal getNtownmoney() {
			return ntownmoney;
		}
		/**
		 * @param ntownmoney the ntownmoney to set
		 */
		public void setNtownmoney(BigDecimal ntownmoney) {
			this.ntownmoney = ntownmoney;
		}
	    
}

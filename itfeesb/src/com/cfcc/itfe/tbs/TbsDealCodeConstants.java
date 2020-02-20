package com.cfcc.itfe.tbs;

public class TbsDealCodeConstants {
	
	/**
	 * 凭证状态[10] TBS已读取
	 */
	public static final String VOUCHER_ACCEPT = "10" ;
	/**
	 * 凭证状态[20] TBS校验成功
	 */
	public static final String VOUCHER_VALIDAT_SUCCESS = "20" ;
	/**
	 * 凭证状态[30] TBS校验失败或预处理失败
	 */
	public static final String VOUCHER_VALIDAT_FAIL = "30" ;
	/**
	 * 凭证状态[40] 清算中   等待清算结果信息
	 */
	public static final String VOUCHER_LIQUIDATION_ING = "40" ;
	/**
	 * 凭证状态[41] 清算成功   等待清算结果信息
	 */
	public static final String VOUCHER_LIQUIDATION_SUCCEED = "41" ;
	/**
	 * 凭证状态[42] 清算失败   等待清算结果信息
	 */
	public static final String VOUCHER_LIQUIDATION_FAILE = "42" ;
	
	/**
	 * 凭证状态[61] 已收妥   tbs已读取
	 */
	public static final String VOUCHER_READ_SUCESS = "61" ;
	
	/**
	 * 凭证状态[71] 处理成功 tbs处理完成，清算业务也已收到清算回执
	 */
	public static final String VOUCHER_DEALWITH_SUCESS = "71" ;
	
	/**
	 * 凭证状态[80] 回单成功
	 */
	public static final String VOUCHER_SUCCESS = "80" ;
	/**
	 * 凭证状态[85] 读取回单
	 */
	public static final String VOUCHER_ACCEPT_RETURNVOUCHER = "85" ;
	/**
	 * 凭证状态[90] 退回成功
	 */
	public static final String VOUCHER_FAIL = "90" ;
	
	
	/**
	 * 凭证状态[100] 凭证库操作失败
	 */
	public static final String VOUCHER_APP_FAIL = "100" ;
	/**
	 * 凭证状态[110] 凭证锁定状态   重新读取未同步状态   不允许操作该凭证
	 */
	public static final String VOUCHER_LOCKING = "110" ;
	
	
	
	
	
	/**
	 * 调用接口通用回执
	 * 0:操作成功
	 * -1:操作失败
	 */
	public static final String OPERATION_SUCCESS = "0";
	public static final String OPERATION_FAIL = "-1";
	
	
	
	
}

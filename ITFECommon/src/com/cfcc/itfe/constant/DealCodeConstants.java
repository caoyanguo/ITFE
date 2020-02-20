package com.cfcc.itfe.constant;

public class DealCodeConstants {
	
	/** 
	 * TIPS处理结果 [90000] 处理成功 
	 */
	public static final String DEALCODE_TIPS_SUCCESS = "90000";
	/** 
	 * ITFE处理结果 [80000] 处理成功 
	 */
	public static final String DEALCODE_ITFE_SUCCESS = "80000";
	/** 
	 * ITFE处理结果 [80001] 处理失败 
	 */
	public static final String DEALCODE_ITFE_FAIL = "80001";
	/** 
	 * ITFE处理结果 [80002] 处理中 
	 */
	public static final String DEALCODE_ITFE_DEALING = "80002";
	/** 
	 * ITFE处理结果 [80003] 已重发 
	 */
	public static final String DEALCODE_ITFE_REPEAT_SEND = "80003";
	/** 
	 * ITFE包处理结果 [80004] 已收妥 
	 */
	public static final String DEALCODE_ITFE_RECEIVER = "80004";
	/** 
	 * ITFE处理结果 [80005] 未对账 
	 */
	public static final String DEALCODE_ITFE_NO_CHECK = "80005";
	/** 
	 * ITFE处理结果 [80006] 已发送 
	 */
	public static final String DEALCODE_ITFE_SEND = "80006";
	/** 
	 * ITFE处理结果 [80008] 未发送 
	 */
	public static final String DEALCODE_ITFE_NO_SEND = "80008";
	/** 
	 * ITFE处理结果 [80009] 待处理 
	 */
	public static final String DEALCODE_ITFE_WAIT_DEALING = "80009";
	
	/** 
	 * ITFE处理结果 [80010] 已作废
	 */
	public static final String DEALCODE_ITFE_CANCELLATION= "80010";
	/** 
	 * ITFE处理结果 [80011] 已回执
	 */
	public static final String DEALCODE_ITFE_RECEIPT= "80011";
	/** 
	 * ITFE处理结果 [80012] 已对账
	 */
	public static final String DEALCODE_ITFE_CHECK= "80012";
	
	/** 
	 * ITFE处理结果 [80013] 已对账
	 */
	public static final String DEALCODE_ITFE_BACK= "80013";
	
	/** 
	 * ITFE处理结果 [00000] 其他错误
	 */
	public static final String DEALCODE_ITFE_OTHER= "00000";

	/** 
	 * 退库TIPS处理结果 [90] 处理成功 
	 */
	public static final String DEALCODE_DWBK_SUCCESS= "90";
	/** 
	
	/**
	 * 数据库错误码:[SQL3125W] 数据被截断 
	 */
	public static final String DB2_SQL3125W = "SQL3125W" ;
	
	/**
	 * 数据库错误码:[SQL3124W] 数据转化成金额字段错误
	 */
	public static final String DB2_SQL3124W = "SQL3124W" ;
	
	/**
	 * 数据库错误码:[SQL3137W] 行数据太短,缺少字段
	 */
	public static final String DB2_SQL3137W = "SQL3137W" ;
	
	/**
	 * IMPORT执行结果:[读取行数] 
	 */
	public static final String DB2_READ_LINE = "读取行数" ;
	
	/**
	 * IMPORT执行结果:[跳过行数] 
	 */
	public static final String DB2_LEAP_LINE = "跳过行数" ;
	
	/**
	 * IMPORT执行结果:[插入行数] 
	 */
	public static final String DB2_INSERT_LINE = "插入行数" ;
	
	/**
	 * IMPORT执行结果:[更新行数] 
	 */
	public static final String DB2_UPDATE_LINE = "更新行数" ;
	
	/**
	 * IMPORT执行结果:[拒绝行数] 
	 */
	public static final String DB2_REJECT_LINE = "拒绝行数" ;
	
	/**
	 * IMPORT执行结果:[落实行数] 
	 */
	public static final String DB2_CARRY_LINE = "落实行数" ;
	
	/**
	 * IMPORT执行结果:[插入行数] inserted
	 */
	public static final String DB2_INSERT_LINE_ENGLISH = "inserted" ;
	
	/**
	 * IMPORT执行结果:[更新行数] updated
	 */
	public static final String DB2_UPDATE_LINE_ENGLISH = "updated" ;
	
	/**
	 * IMPORT执行结果:[拒绝行数] rejected
	 */
	public static final String DB2_REJECT_LINE_ENGLISH = "Number of rows rejected" ;
	
	/**
	 * IMPORT执行结果:[落实行数] committed
	 */
	public static final String DB2_CARRY_LINE_ENGLISH = "committed" ;
	
	/**
	 * 销号失败
	 */
	public static final String DEALCODE_CHECK_FAIL = "80013" ;
	
	/**
	 * 处理成功
	 */
	public static final String PROCESS_SUCCESS = "处理成功" ;
	/**
	 * 处理失败
	 */
	public static final String PROCESS_FAIL = "处理失败" ;
	
	/** 
	 * ITFE处理凭证状态 [80010] 凭证待处理 
	 */
	public static final String DEALCODE_VOUCHER_ITFE_WAIT_DEALING = "80010";
	
	/**
	 * 凭证状态[10] 已读取
	 */
	public static final String VOUCHER_ACCEPT = "10" ;
	
	/**
	 * 凭证状态[15] 签收成功
	 */
	public static final String VOUCHER_RECEIVE_SUCCESS = "15" ;
	
	/**
	 * 凭证状态[16] 签收失败
	 */
	public static final String VOUCHER_RECEIVE_FAIL = "16" ;
	
	/**
	 * 凭证状态[17] 校验中
	 */
	public static final String VOUCHER_VALIDAT = "17" ;
	
	/**
	 * 凭证状态[20] 校验成功
	 */
	public static final String VOUCHER_VALIDAT_SUCCESS = "20" ;
	/**
	 * 凭证状态[30] 校验失败
	 */
	public static final String VOUCHER_VALIDAT_FAIL = "30" ;
	/**
	 * 凭证状态[40] 审核通过
	 */
	public static final String VOUCHER_CHECKSUCCESS = "40" ;
	/**
	 * 凭证状态[45] 审核失败
	 */
	public static final String VOUCHER_CHECKFAULT = "45" ;
	/**
	 * 凭证状态[50] 复核成功
	 */
	public static final String VOUCHER_CHECK_SUCCESS = "50" ;
	/**
	 * 凭证状态[55] 监管成功
	 */
	public static final String VOUCHER_REGULATORY_SUCCESS = "55" ;
	/**
	 * 凭证状态[56] 监管失败
	 */
	public static final String VOUCHER_REGULATORY_FAULT = "56" ;
	/**
	 * 凭证状态[57] 监管中
	 */
	public static final String VOUCHER_REGULATORY_AUDITING = "57" ;
	/**
	 * 凭证状态[62] TIPS处理失败
	 */
	public static final String VOUCHER_FAIL_TIPS = "62" ;
	/**
	 * 凭证状态[61] 已收妥
	 */
	public static final String VOUCHER_RECIPE = "61" ;

	/**
	 * 凭证状态[60] 已发送
	 */
	public static final String VOUCHER_SENDED = "60" ;
	
	/**
	 * 凭证状态[66] 生成回单成功-对账生成回单
	 */
	public static final String VOUCHER_GENRETURN = "66" ;
	/**
	 * 凭证状态[72] TCBS处理失败
	 */
	public static final String VOUCHER_FAIL_TCBS = "72" ;
	
	/**
	 * 凭证状态[71] 处理成功
	 */
	public static final String VOUCHER_SUCCESS_NO_BACK = "71" ;

	
	/**
	 * 凭证状态[73] 签章成功
	 */
	public static final String VOUCHER_STAMP = "73" ;
	/**
	 * 凭证状态[80] 回单成功
	 */
	public static final String VOUCHER_SUCCESS = "80" ;
	/**
	 * 凭证状态[90] 退回成功
	 */
	public static final String VOUCHER_FAIL = "90" ;
	/**
	 * 凭证状态[74] 待补录
	 */
	public static final String VOUCHER_BANKRECORD = "74" ;
	/**
	 * 凭证状态[75] 退票成功
	 */
	public static final String VOUCHER_SUCCESS_BACK = "75" ;
	/**
	 * 凭证状态[98] 读取财政回单
	 */
	public static final String VOUCHER_READFIN = "98" ;
	/**
	 * 凭证状态[99] 读取商行回单
	 */
	public static final String VOUCHER_READBANK = "99" ;
	/**
	 * 凭证状态[100] 读取回单成功
	 */
	public static final String VOUCHER_READRETURN = "100" ;
	
	/**
	 * 凭证自动控制参数 [0] 自动 [1] 本地
	 */
	public static final String VOUCHER_AUTO = "0" ;
	
	/**
	 * 凭证回单状态[0] 未接收
	 */
	public static final String VOUCHER_RETURN_NO_RECEIVE = "0" ;

	/**
	 * 凭证回单状态[1] 已接收
	 */
	public static final String VOUCHER_RETURN_RECEIVE = "1" ;

	/**
	 * 凭证回单状态[2] 接收失败
	 */
	public static final String VOUCHER_RETURN_FAIL_RECEIVE = "2" ;

	/**
	 * 凭证回单状态[3] 签收成功
	 */
	public static final String VOUCHER_RETURN_SUCCESS_SIGNIN = "3" ;

	/**
	 * 凭证回单状态[4] 签收失败
	 */
	public static final String VOUCHER_RETURN_FAIL_SIGNIN = "4" ;

	/**
	 * 凭证回单状态[5] 已回单
	 */
	public static final String VOUCHER_RETURN_SUCCESS_ALREADY = "5" ;

	/**
	 * 凭证回单状态[6] 被退回 
	 */
	public static final String VOUCHER_RETURN_RETREAT = "6" ;
	
	/**
	 * 凭证状态[75] 对账成功
	 */
	public static final String VOUCHER_CHECKACCOUNT_SUCCESS = "75" ;
	/**
	 * 凭证状态[76] 对账失败
	 */
	public static final String VOUCHER_CHECKACCOUNT_FAIL = "76" ;
	
	/**
	 * voucherflag:0 发送单 1 回单
	 */
	public static final String VOUCHER_FLAT_1 = "1";
	public static final String VOUCHER_FLAT_0 = "0";
	
	
	
}

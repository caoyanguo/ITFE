package com.cfcc.itfe.constant;

public class ParamConstant {
	
	public static final String[][] TABS = {{"TS_TREASURY","国库代码表"},{"TD_TAXORG_PARAM","征收机关代码表"},{"TS_BUDGETSUBJECT","预算科目代码表"},
		                                   {"TP_SHARE_DIVIDE","共享分成参数表"},{"TD_BANK","银行代码维护表"},{"TD_BOOKACCT_MAIN","会计账户信息"},
		                                   {"TD_CORP","纳税人与预算单位编码维护表"},{"TD_CORPACCTPAYEE","收付款单位代码维护表"},{"TD_CORPACCT","纳税人与预算单位账号维护表"},{"TD_BOOKSBT","会计科目维护表"}
		                                   ,{"TS_PAYBANK","支付系统参与者表"},{"TN_CONPAYCHECKBILL","集中支付额度对账单"},{"TS_CORRREASON","更正原因"}
		                                   ,{"TS_DRAWBACKREASON","退库原因"},{"TS_CONVERTASSITSIGN","辅助标志对照表"},{"TS_TREACCTNOBNKCODE","库款账户与行号"},{"TS_CONVERTTAXORG","征收机关对照表"}};
	
	
	public static final String TAB_CODE = "TABLECODE";
	public static final String TAB_NAME = "TABLENAME";
	public static final String SEPARATOR = "LINESEPARATOR";
	public static final String ORGCODE = "ORGCODE";
	
	public static final String DEFAULTPARAMPREFIX = "com.cfcc.itfe.param.service.ParamInOut"; 
	public static final String CENTERORG = "000000000000";


}

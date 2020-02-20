package com.cfcc.itfe.constant;

public interface SequenceName {
	
	//纵向联网导入文件凭证流水号(主表和子表)
	public final static String VERTICAL_IVOUSRLNO="VERTICAL_IVOUSRLNO";
	
	//纵向联网导入文件组内序号（子表）
	public final static String VERTICAL_IGRPINNERNO="VERTICAL_IGRPINNERNO";
	
	//帐户户名参数序号
	public final static String ACCTNAME_PARAMSNO = "ACCTNAME_PARAMSNO";
	
	// 差错流水号（参数）
	public final static String ERRSRL = "ERRSRL";

	// 差错流水号（业务）
	public final static String BIZ_ERRSRL = "BIZERRSRL";

	// 参数序号
	public final static String PARAMSNO = "PARAMSNO";
	// 横联导入文件组序号
	public final static String TIPSGRPSEQ = "TIPSGRPSEQ";
	// 交易流水号8位
	public final static String TRA_ID_SEQUENCE_KEY = "TRAID_SEQ";
	// 凭证序号
	public final static String VOU_SEQ = "VOU_SEQ";
	public final static String FILE_SEQUENCE_KEY = "FILE_SEQUENCE";
	//操作日志等级流水号
	public final static String OPER_SEQ="OPERSEQ";
	
	// 设置序列的缓存和起始值
	public final static int TRAID_SEQ_CACHE = 200;
	public final static int TRAID_SEQ_STARTWITH = 1 ;
	
	//包流水号
	public final static String FILENAME_PACKNO_REF_SEQ = "FILENAME_PACK_SEQ";  //包流水号

	//地方横连版流水号
	public final static String DIRECTPAY_SEQ = "DIRECTPAYSEQ"; //直接支付额度
	public final static String GRANTPAY_SEQ = "GRANTPAYSEQ" ; //授权支付额度
	public final static String RETUTREA_SD_SEQ = "RETUTREASDSEQ"; //退库 山东
	public final static String RETUTREA_FJ_SEQ = "RETUTREAFJSEQ"; //退库 福建
	public final static String RETUTREA_XM_SEQ = "RETUTREAXMSEQ"; //退库 厦门
	public final static String CORRECT_SEQ = "CORRECTSEQ"; //更正
	public final static String PAYOUT_SD_SEQ = "PAYOUTSDSEQ"; //实拨 山东
	public final static String PAYOUT_FJ_SEQ = "PAYOUTFJSEQ"; //实拨 福建
	public final static String PAYOUT_XM_SEQ = "PAYOUTXMSEQ"; //实拨 厦门
	public final static String QUANTITY_SEQ = "QUANTITYSEQ"; //批量拨付
	public final static String HKSQ_SEQ = "HKSQSEQ"; //划款申请
	public final static String HKSQ_BACK_SEQ = "HKSQBACKSEQ"; //划款申请退回
	public final static String TAXFREE_SEQ = "TAXFREESEQ"; //免抵调
	public final static String VOUCHER_SEQ = "VOUCHER"; //凭证序列号
	
	
	//导出TBS文件生成业务批次流水号
	public final static String TBS_SEQ = "NUMFORTBSFILE"; //凭证序列号
	
	
	
	
}

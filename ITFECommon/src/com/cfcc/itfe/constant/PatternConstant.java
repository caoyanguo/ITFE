package com.cfcc.itfe.constant;
/**
 * 定义正则表达式的常量，命名规则如下
 * PATTERN_{1}_{2}
 * {1}代表与其相关的常量，如果为db2数据库的查询，应填写DB2,如果与文件有关的常量，应该填写FILE
 * @author zouyutang
 *
 */
public class PatternConstant {
	/**
	 * db2 SQL码的正则表达式
	 * SQL3104N
	 */
	public static final String PATTERN_DB2_SQLCODE= "SQL\\d+\\p{Upper}{1}";
	
	/**
	 * 文件 导入tbs的文件正则表达式
	 * s＋收款国库代码（10位）＋
	 * m＋目的国库代码（10位，如果没有目的国库的话，则没有该部分）＋
	 * 下划线＋
	 * 1位报表名称（1－预算收入报表，2－支出报表，3－调拨收入报表，4－共享分成报表，
	 * 6－收入退库报表，0－总额分成报表，7－预算收入对帐报表，
	 * 8－收入税票，y－月调整期报表，c－财政库存报表，
	 * f－分户帐对帐单，t－退库凭证，z－支出凭证，g-更正凭证）
	 * ＋1位报表范围（0－本级，1－全辖）
	 * ＋1位预算种类（1－预算内，此时文件名中没有该部分；2－预算外）
	 * ＋8位日期
	 * ＋[下划线＋辅助信息（征收机关的性质等）]
	 * （中括号的信息可以没有）＋“*.txt”构成。
	 * 
	 * TBS导出一般报表(6104)YB_导出国库代码（10）_日期（8）.xml
	 */
	
	public static final String PATTERN_FILE_TBSREPORTFILEIMPORT = "^[st][2]?[s]?(\\d{10}(m\\d{10})?_[14tg][01]2?\\d{6,8}(_\\d+)?\\.txt)|((YB|NT){1}_\\d{10}_\\d{8}\\.xml)$";
	

}

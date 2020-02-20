package com.cfcc.itfe.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.cfcc.itfe.config.ITFECommonConstant;

@SuppressWarnings("unchecked")
public class MsgConstant {
	
	
	/**
	 * 集中支付额度对账单
	 */
	public final static String MSG_NO_JZZF = "JZZF";
	/**
	 * 广州报表下载配置
	 */
	public final static String PLACE_GZ = "_GZ";
	
	/**
	 * DB IMPORT方式导入错误时提示用户错误信息条数
	 */
	public final static int DB_IMPORT_ERROR_NUM = 50;
	
	/**
	 * TIPS每包设置的最大数
	 */
	public final static int TIPS_MAX_OF_PACK = 1000;
	
	/**
	 * 调用CALLSHELL方式返回的结果集合转化为字符串的最大值(单位(K))
	 */
	public final static int MAX_CALLSHELL_RS = 100;
	
	/****************************** 征收机关大类 **********************************/
	/**
	 * 国税大类
	 */
	public static final String MSG_TAXORG_NATION_CLASS = "111111111111";

	/**
	 * 地税大类
	 */
	public static final String MSG_TAXORG_PLACE_CLASS = "222222222222";

	/**
	 * 海关大类
	 */
	public static final String MSG_TAXORG_CUSTOM_CLASS = "333333333333";

	/**
	 * 财政大类
	 */
	public static final String MSG_TAXORG_FINANCE_CLASS = "444444444444";

	/**
	 * 其他大类
	 */
	public static final String MSG_TAXORG_OTHER_CLASS = "555555555555";

	/**
	 * 不分征收机关大类
	 */
	public static final String MSG_TAXORG_SHARE_CLASS = "000000000000";
	
	/**
	 * 不分征收机关大类(按照TBS类型统计)
	 */
	public static final String MSG_TAXORG_SHARE_CLASS_TBS = "999999999999";
	
	/****************************** 征收机关性质 **********************************/
	/**
	 * 国税
	 */
	public static final String MSG_TAXORG_NATION_PROP = "1";

	/**
	 * 地税
	 */
	public static final String MSG_TAXORG_PLACE_PROP = "2";

	/**
	 * 海关
	 */
	public static final String MSG_TAXORG_CUSTOM_PROP = "3";

	/**
	 * 财政
	 */
	public static final String MSG_TAXORG_FINANCE_PROP = "4";
	
	/**
	 * 其他
	 */
	public static final String MSG_TAXORG_OTHER_PROP = "5";
	
	/**
	 * 不分
	 */
	public static final String MSG_TAXORG_SHARE_PROP = "0";
	
	/********************************   SPRING CONFIG   ********************************/ 
	/**
	 * 报文服务处理配置头
	 */
	public static final String SPRING_MSG_SERVER = "MSG_";
	
	/**
	 * 定时任务调用报文头
	 */
	public static final String SPRING_MSG_PROC_SERVER = "PROC_";
	
	/**
	 * 报表导出配置
	 */
	public static final String SPRING_EXP_REPORT = "REPORT_";
	/**
	 * SERVICE服务处理配置头
	 */
	public static final String SPRING_SERVICE_SERVER = "SERVICE_";
	/**
	 * 文件解析处理配置头
	 */
	public static final String SPRING_FILEPRA_SERVER = "TIPS_";
	/**
	 * 陕西财政统一银行接口处理配置头
	 */
	public static final String SPRING_SXXMLPRA_SERVER = "VOUCHER_SX_";
	/********************************   SPRING CONFIG   ********************************/

	/********************************   MSG HEAD INFO   ********************************/ 
	/**
	 * ITFE报文节点
	 */
	public static final String MSG_HEAD_SRC = "202057100007";

	/**
	 * TIPS报文节点
	 */
	public static final String MSG_HEAD_DES = "100000000000";

	/**
	 * 应用名称
	 */
	public static final String MSG_HEAD_APP = "TIPS";

	/**
	 * 报文VER
	 */
	public static final String MSG_HEAD_VER = "1.0";
	
	/**
	 * TIPS节点代码
	 */
	public static final String TIPSNODE_GUANGXI = ITFECommonConstant.SRC_NODE;//原来是广西节点号，修改为全部

	/********************************   MSG HEAD INFO   ********************************/ 

	/********************************   MSG        NO   ********************************/
	/**
	 * 无效报文的报文编号
	 */
	public static final String MSG_NO_0000 = "0000";
	/**
	 * 1000 实拨资金业务(北京)
	 */
	public static final String MSG_NO_1000 = "1000";
	/**
	 * 批量扣税
	 */
	public static final String MSG_NO_1102 = "1102";
	/**
	 * 1103 自缴核销
	 */
	public static final String MSG_NO_1103 = "1103";
	/**
	 * 1104 退库请求
	 */
	public static final String MSG_NO_1104 = "1104";
	/**
	 * 1105 更正请求
	 */
	public static final String MSG_NO_1105 = "1105";
	/**
	 * 1106 免抵调请求
	 */
	public static final String MSG_NO_1106 = "1106";
	/**
	 * 7211 税票收入(横联业务)
	 */
	public static final String MSG_NO_7211 = "7211";
	/**
	 * 征收机关申请入库流水
	 */
	public static final String MSG_NO_1024 = "1024";
	
	/**
	 * 2000 资金清算回执(北京)
	 */
	public static final String MSG_NO_2000 = "2000";
	/**
	 * 3123　与征收机关退库核对通知
	 */
	public static final String MSG_NO_3123 = "3123";
	/**
	 * 3124　与征收机关更正核对通知
	 */
	public static final String MSG_NO_3124 = "3124";
	/**
	 * 3128 日报申请回执
	 */
	public static final String MSG_NO_3128 = "3128";
	/**
	 * 3131 实拨资金回执
	 */
	public static final String MSG_NO_3131 = "3131";
	/**
	 * 3133 直接支付额度回执
	 */
	public static final String MSG_NO_3133 = "3133";
	/**
	 * 3134 授权支付额度回执
	 */
	public static final String MSG_NO_3134 = "3134";
	/**
	 * 3136 代发财政性款项回执
	 */
	public static final String MSG_NO_3136 = "3136";
	/**
	 * 3200 TIPS与财政、社保、商业银行支出信息包核对通知
	 */
	public static final String MSG_NO_3200 = "3200";
	/**
	 * 3201 TIPS与财政、社保、商业银行处理结果信息包核对通知
	 */
	public static final String MSG_NO_3201 = "3201";
	/**
	 * Tips向财政发起的3122与征收机关、社保税（费）票明细核对通知报文
	 */
	public static final String MSG_NO_3122 = "3122";
	/**
	 * 征收机关申请入库流水信息(3126)
	 */
	public static final String MSG_NO_3126 = "3126";
	/**
	 * 征收机关申请报表信息(3127)
	 */
	public static final String MSG_NO_3127 = "3127";
	/**
	 * 3190 　TCBS批量处理结果通知(3190)
	 */
	public static final String MSG_NO_3190 = "3190";
	/**
	 * 3139 　财政入库流水明细
	 */
	public static final String MSG_NO_3139 = "3139";
	
	/**
	 * 1025　征收机关申请报表
	 */
	public static final String MSG_NO_1025 = "1025";
	/**
	 * 5001 财政收入日报申请
	 */
	public static final String MSG_NO_5001 = "5001";
	/**
	 * 5002 财政申请入库流水
	 */
	public static final String MSG_NO_5002 = "5002";
	/**
	 * 5003 财政申请电子税票信息
	 */
	public static final String MSG_NO_5003 = "5003";
	/**
	 * 5101 实拨资金业务
	 */
	public static final String MSG_NO_5101 = "5101";
	/**
	 * 5101 实拨资金退回
	 */
	public static final String MSG_NO_51011 = "51011";
	/**
	 * 5102 直接支付额度
	 */
	public static final String MSG_NO_5102 = "5102";
	/**
	 * 5103 授权支付额度
	 */
	public static final String MSG_NO_5103 = "5103";
	/**
	 * 5104 人行办理直接支付
	 */
	public static final String MSG_NO_5104 = "5104";
	/**
	 * 5112 代发财政性款项请求
	 */
	public static final String MSG_NO_5112 = "5112";
	
	/**
	 * 8000 对帐报文(北京)
	 */
	public static final String MSG_NO_8000 = "8000";
	/**
	 * 9000 申请报文(北京)
	 */
	public static final String MSG_NO_9000 = "9000";
	/**
	 * 9003 交易状态查询请求
	 */
	public static final String MSG_NO_9003 = "9003";
	/**
	 * 9004 交易状态查询应答
	 */
	public static final String MSG_NO_9004 = "9004";
	/**
	 * 9005 连接性测试报文
	 */
	public static final String MSG_NO_9005 = "9005";
	/**
	 * 9006 登陆报文
	 */
	public static final String MSG_NO_9006 = "9006";
	/**
	 * 9007 登陆报文回执
	 */
	public static final String MSG_NO_9007 = "9007";
	/**
	 * 9008 签退报文
	 */
	public static final String MSG_NO_9008 = "9008";
	/**
	 * 9009 签退回执报文
	 */
	public static final String MSG_NO_9009 = "9009";
	/**
	 * 9105 自由格式报文
	 */
	public static final String MSG_NO_9105 = "9105";
	/**
	 * 9110 通用应答报文
	 */
	public static final String MSG_NO_9110 = "9110";
	/**
	 * 9111 包明细重发请求
	 */
	public static final String MSG_NO_9111 = "9111";
	/**
	 * 9120 通用应答
	 */
	public static final String MSG_NO_9120 = "9120";
	/**
	 * 9121 通用确认应答
	 */
	public static final String MSG_NO_9121 = "9121";
	/**
	 * 9122 通用处理结果通知
	 */
	public static final String MSG_NO_9122 = "9122";
	
	/**
	 * 3129 电子税票
	 */
	public static final String MSG_NO_3129 = "3129";
	
	/**
	 * 3143划款申请回执
	 */
	public static final String MSG_NO_3143 = "3143";
	
	/**
	 * 3144划款申请退回回执
	 */
	public static final String MSG_NO_3144 = "3144";
	
	/**
	 * 3146人行退回
	 */
	public static final String MSG_NO_3146 = "3146";
	
	/**
	 * 2201划款申请
	 */
	public static final String MSG_NO_2201 = "2201";
	
	/**
	 * 2201划款退回
	 */
	public static final String MSG_NO_2202 = "2202";
	
	/** 3452国库往来票据修改前 吉林长春
	 */
	public static final String MSG_NO_3452 = "3452";
	
	/** 3403国库往来票据修改后
	 */
	public static final String MSG_NO_3403 = "3403";
	
	
	/**
	 * 连接测试请求 (3000)
	 */
	public static final String MSG_TBS_NO_3000 = "3000";
	/**
	 * 通用回执报文 (3001）
	 */
	public static final String MSG_TBS_NO_3001 = "3001";
	/**
	 * 退款通知报文（2001）
	 */
	public static final String MSG_TBS_NO_2001 = "2001";
	/**
	 * 对账结果接口 (1002)
	 */
	public static final String MSG_TBS_NO_1002 = "1002";
	/**
	 * 对账报文接口 (2002)
	 */
	public static final String MSG_TBS_NO_2002 = "2002";
	/**
	 * 资金清算回执（2000）
	 */
	public static final String MSG_TBS_NO_2000 = "2000";
	/**
	 * 资金拨付接口（1000）
	 */
	public static final String MSG_TBS_NO_1000 = "1000";
	
	
	
	
	
	
	
	/**
	 * 报文重发
	 */
	public static final String MSG_NO_RESEND = "RESEND";
	/**
	 * 文件对象
	 */
	public static final String MSG_FILE = "FILE";
	
	/********************************   MSG        NO   ********************************/
	
	/****************************直接支付/授权支付额度文件解析****************************/
	
	/**
	 * 文件头开始标志
	 */
	public static final String MSG_FLAG_HEAD_START = "<pub";
	
	/**
	 * 文件头结束标志
	 */
	public static final String MSG_FLAG_HEAD_END = "</pub>";
	
	/**
	 * 文件主体开始标志
	 */
	public static final String MSG_FLAG_MAIN_START = "<main";
	
	/**
	 * 文件主体结束标志
	 */
	public static final String MSG_FLAG_MAIN_END = "</main>";
	
	/**
	 * 文件明细开始标志
	 */
	public static final String MSG_FLAG_DETAIL_START = "<detail";
	
	/**
	 * 文件明细结束标志
	 */
	public static final String MSG_FLAG_DETAIL_END = "</detail>";
	
	/**
	 * 财政局
	 */
	public static final String MSG_FLAG_CZJNAME = "czjname";
	
	/**
	 * 接收单位
	 */
	public static final String MSG_FLAG_RECEIVER = "receiver";
	
	/**
	 * 文件内容标志 - 头
	 */
	public static final String MSG_FLAG_CONTENT_START = "<value>";
	
	/**
	 * 文件内容标志 - 尾
	 */
	public static final String MSG_FLAG_CONTENT_END = "</value>";
	
	/**
	 * 财政局标志 - 头
	 */
	public static final String MSG_FLAG_CZJNAME_START = "<czjname>";
	
	/**
	 * 财政局标志 - 尾
	 */
	public static final String MSG_FLAG_CZJNAME_END = "</czjname>";
	
	/**
	 * 接收单位标志 - 头
	 */
	public static final String MSG_FLAG_RECEIVER_START = "<receiver>";

	/**
	 * 接收单位标志 - 尾
	 */
	public static final String MSG_FLAG_RECEIVER_END = "</receiver>";
	
	/****************************直接支付/授权支付额度文件解析****************************/
	
	/**
	 * 预算种类 1 预算内
	 */
	public final static String BDG_KIND_IN = "1";
	/**
	 * 预算种类 2 预算外
	 */
	public final static String BDG_KIND_OUT = "2";
	/**
	 * 预算种类 6 预算内暂存
	 */
	public final static String BDG_KIND_IN_CASH = "6";

	/**
	 * 调整期标志 0 本年度(正常期)
	 */
	public final static String TIME_FLAG_NORMAL = "0";
	/**
	 * 调整期标志 1 上年度(调整期)
	 */
	public final static String TIME_FLAG_TRIM = "1";
	
	/**
	 * 额度种类 0 人行办理直接支付
	 */
	public final static String AMT_KIND_PEOPLE = "0";
	/**
	 * 额度种类 1 商业银行办理支付
	 */
	public final static String AMT_KIND_BANK = "1";
	
	/**
	 * 账户性质 1 零余额	
	 */
	public final static String ACCT_PROP_ZERO = "1";
	/**
	 * 账户性质 2小额现金	
	 */
	public final static String ACCT_PROP_SMALL = "2";

	/**
	 * 支出凭证类型   0	无纸凭证
	 */
	public final static String PAYOUT_VOU_TYPE_PAPER_NO = "0";
	/**
	 * 支出凭证类型   1	有纸凭证
	 */
	public final static String PAYOUT_VOU_TYPE_PAPER_YES = "1";
	
	/**
	 * 凭证类型(同城清算接口)
	 * 1实拨, 2退库, 3商行划款, 4其他
	 */
	public final static String SAME_BANK_PAYOUT_VOU_TYPE1 = "1" ;
	public final static String SAME_BANK_PAYOUT_VOU_TYPE2 = "2" ;
	public final static String SAME_BANK_PAYOUT_VOU_TYPE3 = "3" ;

	/**
	 * 税款类型 1 当期收入
	 */
	public final static String CORP_TYPE_DANGQI = "1";
	/**
	 * 税款类型 2 补缴欠税
	 */
	public final static String CORP_TYPE_BUJIAO = "2";
	/**
	 * 税款类型 3 三代结缴(委托代征、代扣、代售)
	 */
	public final static String CORP_TYPE_DAIJIAO = "3";
	/**
	 * 税款类型 4 检查补税
	 */
	public final static String CORP_TYPE_BUSHUI = "4";

	/**
	 * 预算级次 0 共享
	 */
	public final static String BUDGET_LEVEL_SHARE = "0";
	/**
	 * 预算级次 1 中央
	 */
	public final static String BUDGET_LEVEL_CENTER = "1";
	/**
	 * 预算级次 2 省
	 */
	public final static String BUDGET_LEVEL_PROVINCE = "2";
	/**
	 * 预算级次 3 市
	 */
	public final static String BUDGET_LEVEL_DISTRICT = "3";
	/**
	 * 预算级次 4 县
	 */
	public final static String BUDGET_LEVEL_PREFECTURE = "4";
	/**
	 * 预算级次 5 乡
	 */
	public final static String BUDGET_LEVEL_VILLAGE = "5";
	/**
	 * 预算级次 6 地方
	 */
	public final static String BUDGET_LEVEL_PLACE = "6";
	
	
	/**
	 * 全部 
	 */
	public final static String BUDGET_LEVEL_ALL = "A";
	/**
	 * 报表下载 1:需要下载
	 */
	public final static String REPORT_DAY_DOWN_YES = "1";
	/**
	 * 报表下载 0:不需下载
	 */
	public final static String REPORT_DAY_DOWN_NO = "0";
	
	/**
	 * 辖属标志 1 全辖
	 */
	public static final String RULE_SIGN_ALL = "1";
	/**
	 * 辖属标志 0 本级
	 */ 
	public static final String RULE_SIGN_SELF = "0";
	
	/**
	 * 辖属标志 3 全辖非本级
	 */
	public static final String RULE_SIGN_ALLNOTSELF = "3";
	
	/**
	 * 报文处理状态 成功
	 */ 
	public static final String MSG_STATE = "90001";
	
	/**
	 * 报表类型 - 收入 income
	 */
	public static final String REPORT_TYPE_INCOME = "income" ;
	/**
	 * 报表类型 - 退库 drawbk
	 */
	public static final String REPORT_TYPE_DRAWBK = "drawbk" ;
	/**
	 * 报表类型 - 库存 stock
	 */
	public static final String REPORT_TYPE_STOCK = "stock" ;
	
	/**
	 * 机构类型 - 0	财政
	 */
	public static final String MSG_ORG_TYPE_FINA = "0" ;
	/**
	 * 机构类型 - 1	社保
	 */
	public static final String MSG_ORG_TYPE_SOCIAL = "1" ;
	/**
	 * 机构类型 - 2	其它	指TCBS
	 */
	public static final String MSG_ORG_TYPE_OTHRE = "2" ;
	/**
	 * 机构类型 - 3	商业银行
	 */
	public static final String MSG_ORG_TYPE_BANK = "3" ;
	
	/**
	 * 其他信息报文用于查询首发日志使用
	 */
	public static final String OTHER_INFO_MSG = "99" ;
	
	/**
	 * 业务模式 0	TIPS联机税票 
	 */
	public static final String MSG_BIZ_MODEL_TIPS = "0";
	/**
	 * 业务模式 1	TIPS自缴核销税票 
	 */
	public static final String MSG_BIZ_MODEL_SELF = "1";
	/**
	 * 业务模式 2	地方横向联网税票 
	 */
	public static final String MSG_BIZ_MODEL_NET = "2";

	/**
	 * 数据来源标志 0	TBS接口 
	 */
	public static final String MSG_SOURCE_TBS = "0";
	
	/**
	 * 数据来源标志 1	国税接口 
	 */
	public static final String MSG_SOURCE_NATION = "1";
	/**
	 * 数据来源标志 2	地税接口 
	 */
	public static final String MSG_SOURCE_PLACE = "2";
	/**
	 * 数据来源标志 3    TIPS接口
	 */
	public static final String MSG_SOURCE_TIPS = "3";
	/**
	 * 数据来源标志  4 山东地方横连接口
	 */
	public static final String MSG_SOURCE_SHANDONG = "4";
	/**
	 * 调拨标志 0 非调拨
	 */
	public static final String MOVE_FUND_SIGN_NO = "0";
	/**
	 * 调拨标志 1 调拨
	 */
	public static final String MOVE_FUND_SIGN_YES = "1";
	/**
	 * 调拨标志 2 转移
	 */
	public static final String MOVE_FUND_SIGN_SHIFT = "2";
	
	/**
	 * 录入标志 0 不可录入
	 */
	public static final String INPUT_SIGN_NO = "0";
	/**
	 * 录入标志  1 可录入
	 */
	public static final String INPUT_SIGN_YES = "1";
	
	/**
	 * 预算种类 1 预算内
	 */
	public static final String BUDGET_TYPE_IN_BDG = "1";
	/**
	 * 预算种类 2 预算外
	 */
	public static final String BUDGET_TYPE_OUT_BDG = "2";
	/**
	 * 预算种类 3 共用
	 */
	public static final String BUDGET_TYPE_SHARE = "3";

	/**
	 * 预算科目属性 1 中央固定
	 */
	public static final String BUDGET_SBT_ATTRIB_CENTER_FIX = "1";
	/**
	 * 预算科目属性 2 地方固定
	 */
	public static final String BUDGET_SBT_ATTRIB_PLACE_FIX = "2";
	/**
	 * 预算科目属性 3 共享
	 */
	public static final String BUDGET_SBT_ATTRIB_SHARE = "3";
	/**
	 * 预算科目属性 4 共用
	 */
	public static final String BUDGET_SBT_ATTRIB_COMMON = "4";

	/**
	 * 9117 支出核对包重发请求
	 */
	public static final String MSG_NO_9117 = "9117";
	/**
	 * 9113 收入核对包信息重发请求
	 */
	public static final String MSG_NO_9113 = "9113";
	/**
	 * 9116 财政收入核对包重发请求
	 */
	public static final String MSG_NO_9116= "9116";
	/**
	 * 横联导入直接支付额度
	 */
	public static final String ZHIJIE_DAORU = "5102";
	/**
	 * 横联导入授权支付额度
	 */
	public static final String SHOUQUAN_DAORU = "5103";
	/**
	 * 横联导入批量拨付
	 */
	public static final String PILIANG_DAORU = "5112";
	/**
	 * 横联导入更正
	 */
	public static final String GENGZHENG_DAORU = "1105";
	
	/**
	 * 横联导入划款申请
	 */
	public static final String APPLYPAY_DAORU = "2201";
	
	/**
	 * 横联导入划款申请退回
	 */
	public static final String APPLYPAY_BACK_DAORU = "2202";
	
	/**
	 * 横联导入退库(山东)
	 */
	public static final String TUIKU_SHANDONG_DAORU = "1104_SD";
	/**
	 * 横联导入退库(福建)
	 */
	public static final String TUIKU_FUJIAN_DAORU = "1104_FJ";
	/**
	 * 横联导入退库(厦门)
	 */
	public static final String TUIKU_XIAMEN_DAORU = "1104_XM";
	/**
	 * 横联导入实拨资金(山东)
	 */
	public static final String SHIBO_SHANDONG_DAORU = "5101_SD";
	/**
	 * 横联导入实拨资金(福建)
	 */
	public static final String SHIBO_FUJIAN_DAORU = "5101_FJ";
	/**
	 * 横联导入实拨资金(厦门)
	 */
	public static final String SHIBO_XIAMEN_DAORU = "5101_XM";
	/**
	 * 横连导入人行办理直接支付
	 */
	public static final String PBC_DIRECT_IMPORT = "5104";
	
	/**
	 * 横连导入免抵调
	 */
	public static final String TAX_FREE_DAORU = "1106";
	/**
	 *  业务类型 - 预算支出日报表(3127)
	 */
	public static final String TAXORG_PAYOUT = "3127";
	/**
	 * 保流水号MAX_VALUE
	 */
	public static final int SEQUENCE_MAX_DEF_VALUE=99999999;
	
	/**
	 * 日志附言
	 */
	public static final String LOG_ADDWORD_RECV="接收外机构报文(非TIPS)";
	public static final String LOG_ADDWORD_SEND="转发接收的外机构报文";
	public static final String LOG_ADDWORD_RECV_TIPS="接收TIPS报文";
	public static final String LOG_ADDWORD_RUN_NOPAPER="接收外机构报文(已上线无纸化不转发)";
	public static final String LOG_ADDWORD_RUN_UNKNOW="接收外机构报文(未知上线情况不转发)";
	public static final String ITFE_REQ="前置申请";
	public static final String ITFE_AUTO_SEND="前置定时发送";
	public static final String ITFE_SEND_9120="接收外机构报文，前置处理异常，返回通知报文";
	/**
	 * 转发标志
	 */
	public static final String ITFE_SEND="0";
	public static final String OTHER_SEND="1";
	
	/**
	 * 支付方式
	 */
	public static final String directPay="0"; //直接支付
	public static final String grantPay="1"; //授权支付
	
	/**
	 * 报文处理状态 无法识别的报文
	 */
	public static final String MSG_STATE_FAIL = "92001";
	/**
	 * 报文处理状态 无法识别的报文
	 */
	public static final String MSG_STATE_FAIL_ADDWORD = "ITFE无法识别的报文";
	
	/**
	 * 2201、2202地方横联标识
	 */
	public static final String CFCCHL="CFCCHL";
	
	/**
	 * 与财政的远程队列
	 */
	public static final String QUEUE_BATCH = ITFECommonConstant.MQTOMOFBATCH;
	public static final String QUEUE_ONLINE = ITFECommonConstant.MQTOMOFONLINE;
	public static final String QUEUE_BATCHCITY = ITFECommonConstant.MQTOMOFBATCHCITY;
	public static final String QUEUE_ONLINECITY = ITFECommonConstant.MQTOMOFONLINECITY;
	/**
	 * 批量队列报文列表
	 */
	public static final Set BATCH_MSG_NO = Collections.unmodifiableSet(new HashSet(
	        Arrays.asList(new String[]{MSG_NO_1102, MSG_NO_1104, MSG_NO_1105})));
	
	/**
	 * 实拨拨款凭证5207
	 */
	public static final String VOUCHER_NO_5207="5207";
	/**
	 * 实拨拨款汇总凭证5287
	 */
	public static final String VOUCHER_NO_5287="5287";
	/**
	 * 集中支付清算划款清单5551
	 */
	public static final String VOUCHER_NO_5551="5551";
	/**
	 * 集中支付清算退款清单5552
	 */
	public static final String VOUCHER_NO_5552="5552";
	/**
	 * 国库实拨拨款汇总清单5553
	 */
	public static final String VOUCHER_NO_5553="5553";
	/**
	 * 厦门专户拨款凭证5267
	 */
	public static final String VOUCHER_NO_5267="5267";
	
	/**
	 * 实拨拨款清单5257
	 */
	public static final String VOUCHER_NO_5257="5257";
	
	
	/**
	 * 授权支付清算额度通知单5106
	 */
	public static final String VOUCHER_NO_5106="5106";
	/**
	 * 人民银行与财政凭证对账3501
	 */
	public static final String VOUCHER_NO_3501="3501";
	/**
	 * 人民银行与代理银行凭证对账3502
	 */
	public static final String VOUCHER_NO_3502="3502";
	
	/**
	 * 人民银行与财政凭证对账结果查询5502
	 */
	public static final String VOUCHER_NO_5502="5502";
	/**
	 * 人民银行与代理银行凭证对账结果查询2502
	 */
	public static final String VOUCHER_NO_2502="2502";
	/**
	 * 非税收入缴库报文通知5671
	 */
	public static final String VOUCHER_NO_5671="5671";
	
	/**
	 * 直接支付清算额度通知单5108
	 */
	public static final String VOUCHER_NO_5108="5108";
	/**
	 * 直接支付日报表
	 */
	public static final String VOUCHER_NO_2206="2206";
	/**
	 * 申请划款凭证回单2301
	 */
	public static final String VOUCHER_NO_2301="2301";
	
	/**
	 * 申请退款凭证回单2302
	 */
	public static final String VOUCHER_NO_2302="2302";
	
	/**
	 * 收入退付凭证5209
	 */
	public static final String VOUCHER_NO_5209="5209";
	/**
	 * 厦门代理库退库 3209
	 */
	public static final String VOUCHER_NO_3209="3209";
	
	/**
	 * 退款通知报文2003(北京)
	 */
	public static final String VOUCHER_NO_2003="2003";
	
	/**
	 * 库存日报表3402
	 */
	public static final String VOUCHER_NO_3402="3402";
	/**
	 * 全省库存日报表3453修改前
	 */
	public static final String VOUCHER_NO_3453="3453";
	/**
	 * 全省库存日报表3453修改后
	 */
	public static final String VOUCHER_NO_3404="3404";
	/**
	 * 清算信息对账
	 */
	public static final String VOUCHER_NO_3507="3507";
	/**
	 * 收入月报--长春（由3507修改而来报文规范变更引起报文号冲突）
	 */
	public static final String VOUCHER_NO_3567="3567";
	/**
	 * 收入日报3401
	 */
	public static final String VOUCHER_NO_3401 = "3401";
	
	/**
	 * 额度信息对账3503
	 */
	public static final String VOUCHER_NO_3503 = "3503";
	
	/**
	 * 清算信息对账3504
	 */
	public static final String VOUCHER_NO_3504 = "3504";
	
	/**
	 * 实拨信息对账3505
	 */
	public static final String VOUCHER_NO_3505 = "3505";
	
	/**
	 *  收入退付对账3506
	 */
	public static final String VOUCHER_NO_3506 = "3506";
	/**
	 * 支出日报3410
	 */
	public static final String VOUCHER_NO_3410 = "3410";
	/**
	 * 国库与财政集中支付额度余额对账单3551
	 */
	public static final String VOUCHER_NO_3551 = "3551";

	/**
	 * 库款账户月度对账单3552
	 */
	public static final String VOUCHER_NO_3552 = "3552";
	
	/**
	 * 支出日报表3553
	 */
	public static final String VOUCHER_NO_3553 = "3553"; 
	
	/**
	 * 支出月报表3508
	 */
	public static final String VOUCHER_NO_3508 = "3508";
	
	/**
	 * 收入退付退款通知书修改前
	 */
	public static final String VOUCHER_NO_3251 = "3251";
	/**
	 * 收入退付退款通知书修改后
	 */
	public static final String VOUCHER_NO_3210 = "3210";
	/**
	 * 商业清算对账单3554
	 */
	public static final String VOUCHER_NO_3554 = "3554";
	
	/**
	 * 总额分成报表3454修改前
	 */
	public static final String VOUCHER_NO_3454="3454";
	/**
	 * 总额分成报表3405修改后
	 */
	public static final String VOUCHER_NO_3405="3405";
	/**
	 * 两税返还报表3455修改前
	 */
	public static final String VOUCHER_NO_3455="3455";
	/**
	 * 两税返还报表3406修改后
	 */
	public static final String VOUCHER_NO_3406="3406";
	/**
	 * 福建个性化新增额度对账报文3550
	 */
	public static final String VOUCHER_NO_3550="3550";
	/**
	 * 福建个性化新增实拨资金对账报文3558
	 */
	public static final String VOUCHER_NO_3558="3558";
	/**
	 * 各地收入年报3562
	 */
	public static final String VOUCHER_NO_3562="3562";
	/**
	 * 各地资金留用情况表3560
	 */
	public static final String VOUCHER_NO_3560 = "3560";
	/**
	 * 山西新增清算明细对账3587
	 */
	public static final String VOUCHER_NO_3587 = "3587";
	
	/**
	 * 山西新增实拨资金对账3588
	 */
	public static final String VOUCHER_NO_3588 = "3588";
	/**
	 * 山西新增清算额度对账3580
	 */
	public static final String VOUCHER_NO_3580 = "3580";
	/**
	 * 山西新增库款对账3583
	 */
	public static final String VOUCHER_NO_3583 = "3583";
	/**
	 * 山西新增清算月度对账
	 */
	public static final String VOUCHER_NO_3582 = "3582";
	/**
	 * 人民银行与财政部门支付对账单4004
	 */
	public static final String VOUCHER_NO_4004 = "4004";
	/**
	 * 人民银行与代理银行集中支付业务对账单4005
	 */
	public static final String VOUCHER_NO_4005 = "4005";
	/**
	 * 商业银行代理集中支付月报表4006
	 */
	public static final String VOUCHER_NO_4006 = "4006";
	/**
	 * 财政批量业务支付明细8207(上海特色)
	 */
	public static final String VOUCHER_NO_8207 = "8207";
	/**
	 * 财政直接支付凭证5201(上海特色)
	 */
	public static final String VOUCHER_NO_5201 = "5201";
	/**
	 * 财政授权支付凭证8202(上海特色)
	 */
	public static final String VOUCHER_NO_8202 = "8202";
	/**
	 * 收款银行退款通知2252(上海特色)
	 */
	public static final String VOUCHER_NO_2252 = "2252";
	/**
	 * 授权支付调整书5351(上海特色)
	 */
	public static final String VOUCHER_NO_5351 = "5351";
	/**
	 * 财政直接支付调整凭证5253(上海特色)
	 */
	public static final String VOUCHER_NO_5253 = "5253";
	/**
	 * 财政直接支付退款通知书2203(上海特色)
	 */
	public static final String VOUCHER_NO_2203 = "2203";
	/**
	 * 单位清册5951(上海)
	 */
	public static final String VOUCHER_NO_5951 = "5951";
	
	
	/**
	 * 厦门特色 更正、调库报文规范5801
	 */
	public static final String VOUCHER_NO_5407 = "5407";
	
	/**
	 * 一般缴款书5408
	 */
	public static final String VOUCHER_NO_5408="5408";
	
	
	
	/**
	 * 无纸化发送凭证附件
	 */
	public static final String VOUCHER_ATTACH_SEND = "0001";
	/**
	 * 凭证报文服务处理配置头
	 */
	public static final String VOUCHER_MSG_SERVER = "VOUCHER_";//
	
	/**
	 * 凭证java转化xml报文服务处理配置头
	 */
	public static final String VOUCHER_DTO2MAP = "VoucherDto2MapFor";
	
	/**
	 * 凭证工厂类
	 */
	public static final String VOUCHER = "VOUCHER";

	/**
	 * 凭证签章类型 [10]  转讫章 
	 */
	public static final String VOUCHERSAMP_ROTARY = "zqz";
	
	/**
	 * 凭证签章类型 附件章 上海的附件章
	 */
	public static final String VOUCHERSAMP_ATTACH = "fjz";
	/**
	 * 凭证签章类型 业务专用章
	 */
	public static final String VOUCHERSAMP_BUSS = "ywzyz";
	/**
	 * 凭证签章类型 [20]  公 章 
	 */
	public static final String VOUCHERSAMP_OFFICIAL = "gz";
	
	/**
	 * 凭证签章类型 []  记账员 私 章 
	 */
	public static final String VOUCHERSAMP_PRIVATE = "sz";
	/**
	 * 凭证签章类型 []  签名
	 */
	public static final String VOUCHERSIGN_PRIVATE = "qm";
	/**
	 * 凭证签章类型 [30]  记账员 私 章 
	 */
	public static final String VOUCHERSAMP_PRIVATE_JZ = "jzysz";
	/**
	 * 凭证签章类型 [40]  复核员 私 章 
	 */
	public static final String VOUCHERSAMP_PRIVATE_FH = "fhysz";
	
	/**
	 * 凭证签章类型 [50]  记账员 签名 
	 */
	public static final String VOUCHERSIGN_PRIVATE_JZ = "jzyqm";
	
	/**
	 * 凭证签章类型 [60]  复核员 签名 
	 */
	public static final String VOUCHERSIGN_PRIVATE_FH = "fhyqm";
	
	/**
	 * 转讫章签章方式 0-服务器签章  1-客户端签章 
	 */
	public static final String VOUCHER_ROTARYSTAMP = "0";
	
	/**
	 * 公章签章方式 0-服务器签章  1-客户端签章  
	 */
	public static final String VOUCHER_OFFICIALSTAMP = "0";
	
	/**
	 * 附件章签章方式 0-服务器签章  1-客户端签章 
	 */
	public static final String VOUCHER_ATTACHSTAMP = "0";
	/**
	 * 是否进行实拨资金调拨科目校验0 -不校验 1校验
	 */
	public static final String VOUCHER_CHECKPAYOUTSUBJECT="1";
	/**
	 * 报表名称 1 一般预算支出
	 */
	public static final String MSG_RPORT_NAME1 = "1";
	public static final String MSG_RPORT_NAME1CMT = "一般预算支出";
	/**
	 * 报表名称 2 人行办理授权支付
	 */
	public static final String MSG_RPORT_NAME2 = "2";
	public static final String MSG_RPORT_NAME2CMT = "人行办理授权支付";
	
	/**
	 * 实拨退款通知书
	 */
	public static final String VOUCHER_NO_3208 = "3208";
	/**
	 * 厦门专户退款通知书
	 */
	public static final String VOUCHER_NO_3268 = "3268";
	/**
	 * 资金业务类型  99-中央及省市往来票据
	 */
	public static final String FUND_BIZ_TYPE_CENTREAREADATA = "99";
	public static final String FUND_BIZ_TYPE_CENTREAREADATA_CMT = "国库往来票据";
	
	
	/**
	 * 资金业务类型  00-核算主体间调拨收入
	 */
	public static final String FUND_BIZ_TYPE_ORGCODEINNERINCOME = "00";
	public static final String FUND_BIZ_TYPE_ORGCODEINNERINCOME_CMT = "核算主体间调拨收入";
	
	
	//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)
	//3-国税大类(111111111111),4-地税大类(222222222222),5-海关大类(333333333333),6-其它大类(555555555555),
	public static final String FIN_BIGKIND = "0";
	public static final String FIN_NO_LEV = "1";
	public static final String FIN_LEV_SLEF = "2";
	
	
	/**
	 * 1-本行业务 2-同城跨行 3-异地跨行
	 */
	public static final String MSG_1000_FLAG1 = "1";
	public static final String MSG_1000_FLAG2 = "2";
	public static final String MSG_1000_FLAG3 = "3";
	
	/**
	 *清算额度对帐
	 */
	public static final String VOUCHER_NO_3510 = "3510";
	/**
	 *清算额度对帐回执
	 */
	public static final String VOUCHER_NO_5510 = "5510";
	/**
	 *清算信息对帐
	 */
	/**
	 *清算额度明细对帐
	 */
	public static final String VOUCHER_NO_3515 = "3515";
	/**
	 *清算额度明细对帐回执
	 */
	public static final String VOUCHER_NO_5515 = "5515";
	/**
	 *清算信息对帐
	 */
//	public static final String VOUCHER_NO_3507 = "3507";
	/**
	 *清算信息对帐财政回执
	 */
	public static final String VOUCHER_NO_5507 = "5507";
	/**
	 *清算信息对帐商行回执
	 */
	public static final String VOUCHER_NO_2507 = "2507";
	/**
	 *实拨信息对帐
	 */
//	public static final String VOUCHER_NO_3508 = "3508";
	/**
	 *实拨信息对帐回执
	 */
	public static final String VOUCHER_NO_5508 = "5508";
	/**
	 *收入退付对帐
	 */
	public static final String VOUCHER_NO_3509 = "3509";
	/**
	 *收入退付对帐回执
	 */
	public static final String VOUCHER_NO_5509 = "5509";
	/**
	 * 收入报表对帐
	 */
	public static final String VOUCHER_NO_3511 = "3511";
	/**
	 *收入报表对帐回执
	 */
	public static final String VOUCHER_NO_5511 = "5511";
	/**
	 *支出报表对帐
	 */
	public static final String VOUCHER_NO_3512 = "3512";
	/**
	 *支出报表对帐回执
	 */
	public static final String VOUCHER_NO_5512 = "5512";
	/**
	 *库款账户对帐
	 */
	public static final String VOUCHER_NO_3513 = "3513";
	/**
	 *库款账户对帐回执
	 */
	public static final String VOUCHER_NO_5513 = "5513";
	/**
	 *基础元数据
	 */
	public static final String VOUCHER_NO_5901 = "5901";
	
	/**
	 *分户账对帐
	 */
	public static final String VOUCHER_NO_3514 = "3514";
	
	/**
	 * 对账结果0-相符 1-不符
	 */
	public static final String VOUCHER_RECONCIIATION_YES = "0";
	
	/**
	 * 对账结果0-相符 1-不符
	 */
	public static final String VOUCHER_RECONCIIATION_NO = "1";
	
	/**
	 * 更正、调库处理类型   
	 * 1-电子 继续发往TIPS  2-手工
	 */
	public static final String VOUCHER_CORRHANDBOOK_DEALTYPE = "1";
	/**
	 *实拨资金退回
	 */
	public static final String MSG_NO_3208 = "3208";
	/**
	 *调拨收入
	 */
	public static final String MSG_NO_51044 = "51044";
	
	/****************************** 代理银行 **********************************/
	/**
	 *中国工商银行上海市分行营业部
	 */
	public static final String MSG_NO_102 = "102";
	/**
	 *中国农业银行上海市分行
	 */
	public static final String MSG_NO_103 = "103";
	/**
	 *中国建设银行股份有限公司上海分行运行中心
	 */
	public static final String MSG_NO_105 = "105";
	/**
	 *交通银行上海分行账务中心
	 */
	public static final String MSG_NO_301 = "301";
	/**
	 *上海浦东发展银行第一营业部
	 */
	public static final String MSG_NO_310 = "310";
	/**
	 *中国银行上海市分行营业部
	 */
	public static final String MSG_NO_104 = "104";
	/**
	 *中信实业银行上海分行会计清算部
	 */
	public static final String MSG_NO_302 = "302";
	/**
	 *招商银行上海分行会计部清算中心
	 */
	public static final String MSG_NO_308 = "308";
	/**
	 *中国光大银行上海分行本部机构
	 */
	public static final String MSG_NO_303 = "303";
	/**
	 *华夏银行股份有限公司上海分行会计部
	 */
	public static final String MSG_NO_304 = "304";
	/**
	 *中国民生银行上海分行清算中心
	 */
	public static final String MSG_NO_305 = "305";
	/**
	 *兴业银行上海分行
	 */
	public static final String MSG_NO_309 = "309";
	/**
	 *广发银行上海分行营业部
	 */
	public static final String MSG_NO_306 = "306";
	/**
	 *平安银行上海分行清算中心
	 */
	public static final String MSG_NO_307 = "307";
	/**
	 *上海银行
	 */
	public static final String MSG_NO_313 = "313";
	/**
	 *上海农商银行业务处理中心
	 */
	public static final String MSG_NO_322 = "322";
	/**
	 *中国邮政储蓄银行股份有限公司上海分行营业部
	 */
	public static final String MSG_NO_403 = "403";
	
	
}

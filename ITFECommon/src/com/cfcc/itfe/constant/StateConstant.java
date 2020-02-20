package com.cfcc.itfe.constant;

public interface StateConstant {

	// 文件删除方式
	public static final String FILEDELETEBYFILE = "0";
	public static final String FILEDELETEBYPATH= "1";
	//参数提示信息
	public static final String PRIMAYKEY = "保存记录出错,主键重复！";
	public static final String INPUTFAIL= "保存记录出错";
	public static final String DELETEFAIL = "删除记录出错";
	public static final String MODIFYFAIL= "修改记录出错";
	public static final String ONLYONEREC = "该参数只能维护一条记录";
	public static final String DELETESELECT= "请选择一条记录";
	public static final String DELETESAVE = "删除成功";
	public static final String INPUTSAVE= "录入保存成功";
	public static final String CHECKVALID = "不能为空的字段录入了空值";
	public static final String MODIFYSAVE= "修改保存成功";
	
	
	public static final String TIPS= "提示信息";
	public static final String DELETECONFIRM= "确定删除？";
	
	// 登陆状态
	public static final String LOGINSTATE_FLAG_LOGOUT = "0";// 已签退
	public static final String LOGINSTATE_FLAG_LOGIN = "1";// 已登录
	
	// 登陆状态
	public static final String LOGINSTATE_FLAG_LOGOUT_NAME = "已签退";// 已签退
	public static final String LOGINSTATE_FLAG_LOGIN_NAME = "已登录";// 已登录
    
	// 用户状态
	public static final String USERSTATUS_0 = "0";// 禁用
	public static final String USERSTATUS_1 = "1";// 可用
	public static final String USERSTATUS_2 = "2";// 工作
	public static final String USERSTATUS_3 = "3";// 冻结
	
	// 日志发送接收标志
	public static final String LOG_SEND = "1";//发送
	public static final String LOG_RECV = "2";// 接收
	
	public static final String INCOME_SPLIT = ","; // 收入业务的分割符号
	public static final String PAYOUT_SPLIT = "|"; // 支出业务的分割符号
	public static final String SPACE_SPLIT = "\r\n"; // 支出业务的分割符号
	
	//中心机构级次
	public static final String ORG_CENTER_CODE = "000000000000"; // 中心机构代码
	public static final String STAT_CENTER_CODE = "999999999999"; // 监控中心机构代码
	public static final String ORG_CENTER_LEVEL = "0"; //中心机构级次
	public static final String ORG_STATE = "1" ;//可用
	
	//用户默认密码
	public static final String User_Default_PWD = "Aa+00000";
	
	//山东签名校验默认机构代码
	public static final String INFOCONNORG_DECRYPT_SD = "bbbbbbbbbbbb";
	
	//文件解析属性
	public static final String FILEKIND_DIRECT = "1"; //直接支付额度
	
	public static final String FILEKIND_GRANT = "2"; //授权支付额度
	
	public static final String FILEKIND_RETUTREA_SD  = "31";  //退库山东接口
	public static final String FILEKIND_RETUTREA_FJ = "32"; //退库福建接口
	public static final String FILEKIND_RETUTREA_XM = "33"; //退库厦门接口
	
	public static final String FILEKIND_CORRECT = "4"; //更正
	
	public static final String FILEKIND_PAYOUT_SD = "51"; //实拨山东接口
	public static final String FILEKIND_PAYOUT_FJ = "52"; //实拨福建接口
	public static final String FILEKIND_PAYOUT_XM = "53"; //实拨厦门接口
	
	public static final String FILEKIND_QUANTITY = "6"; //批量拨付接口

	// 设置报文发起方 前置发起为0，Tips发起为1，财政发起为2,Tips文件加载为3
	public static final String MSG_SENDER_FLAG_0 = "0"; // 前置发起为0
	public static final String MSG_SENDER_FLAG_1 = "1"; // Tips发起为1
	public static final String MSG_SENDER_FLAG_2 = "2"; // 财政发起为2
	public static final String MSG_SENDER_FLAG_3 = "3"; // Tips文件加载为3
	public static final String MSG_SENDER_FLAG_9 = "9"; // 其他模式主要是内部报文处理区分使用
	

	// 是否采用定时发送报文
	public static final String TIMERSENDMSG = "1"; // 定时发送报文
	
	/**
	 * 销号标志
	 */
	public static final String CONFIRMSTATE_YES = "1"; // 已销号
	public static final String CONFIRMSTATE_NO = "0"; // 未销号
	public static final String CONFIRMSTATE_FAIL = "2"; // 销号失败
	/**
	 * 销号确认提交结果
	 */
	public static final Integer SUBMITSTATE_SUCCESS = 1; // 成功
	public static final Integer SUBMITSTATE_FAILURE = 0; // 失败
	public static final Integer SUBMITSTATE_DONE = 2; // 已确认提交过
	
	//是否采用新接口 0--不采用  1--采用
	public static final String IFNEWINTERFACE_FALSE = "0";
	public static final String IFNEWINTERFACE_TRUE = "1";
	
	//山东退库字段数目
	public static final Integer DWBK_CONTENTNUM_SD = 20;
	
	// 电子税票信息 整理期标志

	public final static String TRIMSIGN_FLAG_NORMAL = "0";// 0 本年度(正常期)
	public final static String TRIMSIGN_FLAG_TRIM = "1";// 1 上年度(调整期)
	//报表类型 枚举值 0400
	public final static String BILL_TYPE = "0400";
	//缓存参数类型
	public final static String CachePayBank = "0";// 支付行号
	public final static String CacheBdgSbt = "1";// 预算科目代码
	public final static String CacheTDCrop = "2";// 法人代码
	//入库流水是否更新税票 0--不采用  1--采用
	public static final String IFUPDATEVOU_FALSE = "0";
	public static final String IFUPDATEVOU_TRUE = "1";
	
	
	/**
	 * 收入日报表信息 报表种类
	 */	
	
	/**
	 * 收入日报表信息 报表种类
	 */
	public static final String REPORTTYPE_FLAG_NRBUDGETBILL = "1";// 正常期预算收入报表体
	public static final String REPORTTYPE_FLAG_NRDRAWBACKBILL = "2";// 正常期退库报表体
	public static final String REPORTTYPE_FLAG_NRREMOVEBILL = "3";// 正常期调拨收入报表体
	public static final String REPORTTYPE_FLAG_AMOUNTBILL = "4";// 总额分成报表体
	public static final String REPORTTYPE_FLAG_NRSHAREBILL = "5";// 正常期共享分成报表体
	public static final String REPORTTYPE_FLAG_TRBUDGETBILL = "6";// 调整期预算收入报表体
	public static final String REPORTTYPE_FLAG_TRDRAWBACKBILL = "7";// 调整期退库报表体
	public static final String REPORTTYPE_FLAG_TRREMOVEBILL = "8";// 调整期调拨收入报表体
	public static final String REPORTTYPE_FLAG_TRSHAREBILL = "9";// 调整期共享分成报表体
	
	/**
	 * 导出报表种类
	 */
	public static final String REPORTTYPE_1 = "1";// 预算收入报表体
	public static final String REPORTTYPE_2=  "2";// 预算支出报表体
	public static final String REPORTTYPE_3 = "3";// 调拨收入报表体
	public static final String REPORTTYPE_4=  "4";// 共享分成报表体
	public static final String REPORTTYPE_5 = "5";// 财政库存报表
	public static final String REPORTTYPE_6 = "6";// 预算收入对账报表
	public static final String REPORTTYPE_7=  "7";// 总额分成报表
	public static final String REPORTTYPE_8 = "8";// 收入退库报表
	public static final String REPORTTYPE_9 = "9";// 预算收入凭证
	public static final String REPORTTYPE_10 = "10";// 预算支出凭证
	public static final String REPORTTYPE_11 = "11";// 收入退库凭证
	public static final String REPORTTYPE_12=  "12";// 收入更正凭证
	public static final String REPORTTYPE_13=  "13";// 人行办理直接支付凭证
	//导出文件命名中用到得报表种类
	
	public static final String ReportName_1 = "1";// 预算收入报表体
	public static final String ReportName_2=  "2";// 预算支出报表体
	public static final String ReportName_3 = "3";// 调拨收入报表体
	public static final String ReportName_4=  "4";// 总额分成报表体
	public static final String ReportName_c = "c";// 财政库存报表
	public static final String ReportName_6 = "6";// 收入退库报表
	public static final String ReportName_7=  "7";// 预算收入对账报表
	public static final String ReportName_8 = "8";// 收入税票
	public static final String ReportName_t = "t";// 退库凭证
	public static final String ReportName_z = "z";// 预算支出凭证
	public static final String ReportName_g = "g";// 收入退库凭证
	public static final String ReportName_0 = "0";// 总额分成报表
	
	//前置接收的TIPS数据种类
	public static final String RecvTips_3128_SR = "0";//3128财政申请收入日报表查询
	public static final String RecvTips_3128_KC = "1";//3128财政库存日报表查询
	public static final String RecvTips_3129 = "2";//3129财政电子税票信息查询
	public static final String RecvTips_3139 = "3";//3139财政申请入库流水明细查询
	public static final String RecvTips_3127="4";//tcbs导出预算支出报表导入的数据
	//前置导出TIPS数据的字段名称
	public static final String RecvTips_3128_SR_ColName = "财政机关代码,征收机关代码,国库主体代码,报表日期,预算种类,预算级次代码,预算科目,预算科目名称,日累计金额,旬累计金额,月累计金额,季累计金额,年累计金额,辖属标志,调整期标志,分成组标志,报表种类";//3128财政申请收入日报表
	public static final String RecvTips_3128_KC_ColName = "财政机关代码,国库代码,报表日期,帐户代码,帐户名称,帐户日期,上日余额,本日收入,本日支出,本日余额";//3128财政库存日报表查询
	public static final String RecvTips_3129_ColName = "财政机关代码,申请日期,包流水号,国库代码,国库名称,征收机关代码,付款行行号,交易流水号,原报文编号,交易金额,付款人开户行行号,付款开户行名称,缴款单位名称,付款人账号,税票号码,开票日期,纳税人编号,纳税人名称,预算种类,调整期标志,企业代码,企业名称,企业类型,预算科目代码,预算科目名称,限缴日期,税种代码,税种名称,预算级次,预算级次名称,税款所属日期起,税款所属日期止,辅助标志,税款类型,账务日期,处理状态,备注,备注1,备注2,录入员代码,系统更新时间,序号";//3129财政电子税票信息查询
	public static final String RecvTips_3139_ColName = "财政机关代码,国库代码,导入委托日期,包流水号,凭证编号,导出凭证类型,预算级次,预算科目代码,预算种类,金额,凭证来源,征收机关代码,序号";//3139财政申请入库流水明细查询
	public static final String RecvTips_3127_ColName="征收机关代码,国库主体代码,报表日期,预算种类,预算级次代码,预算科目,预算科目名称,经济分类科目,经济科目名称,日累计金额,旬累计金额,月累计金额,季累计金额,年累计金额,财政机关代码";//tcbs导出预算支出报表导入的数据
	//生成文件路径的设置
	public static final String sr_3128 = "3128sr/";
	public static final String kc_3128 = "3128kc/";
	public static final String on_3129 = "3129/";
	public static final String in_3139 = "3139/";
	public static final String in_3127 = "3127/";
	//前置错误信息文件路径
	public static final String Import_Errinfo_DIR = "c:/client/errInfo/";
	
	//前置错误信息文件保存天数
	public static final String Import_Errinfo_BackDays = "10";
	
	//3129电子税票需要刷选出的状态
	public static final String STATE_OF_3129 = "'20','30','40','50','60'";
	
	//前置统计业务量类型
	public static final String Count_Statics_Type = "'01','02','13','15','17','23','33','25','26','27','28','1106'";
	
	// 预算种类
	public static final String BudgetType_IN = "1";// 预算内
	public static final String BudgetType_OUT= "2";// 预算外
	
	//加密方式 0-不加密 1山东格式逐行加密 2、山西的加密方式 3、电子签名方式4、其他
	public static final String ENCRYPT_FLAG_NO = "0";
	public static final String ENCRYPT_FLAG_SDM = "1";
	public static final String ENCRYPT_FLAG_SXM = "2";
	public static final String ENCRYPT_FLAG_CA = "3";
	public static final String ENCRYPT_FLAG_ORTHER = "4";
	

	// 是否转发财政
	public static final String SendFinYes = "1";//转发
	public static final String SendFinNo= "0";// 不转发
	
	// 实时扣税表 处理状态
	public static final String DATA_FLAG_CHECK = "01"; // 已校验接收
	public static final String DATA_FLAG_NOCHECK = "00"; // 未通过校验

	// 实时扣税表 冲正状态
	public static final String CANCEL_FLAG_NOCHECK = "0"; // 未冲正

	// 实时冲正信息表 冲正应答
	public static final String CANCELANSWER_FLAG_NOCHECK = "0"; // 未应答
	// 实时扣税表 清算渠道
	public static final String RECKONTYPE_FLAG_ONE = "1"; // 商业银行直接连接
	public static final String RECKONTYPE_FLAG_TWO = "2"; // 小额支付连接
	//移植鞍山处理吗
	public static final String DEAL_CODE_0000 = "0000"; // 已收妥，校验成功
	public static final String DEAL_CODE_0002 = "0002"; // 报文要素错
	
	//通用是否判断
	public static final String COMMON_YES = "1"; // 通用是
	public static final String COMMON_NO = "0"; // 通用否
	
	//密钥维护模式
	public static final String KEY_ALL = "0"; // 全省统一
	public static final String KEY_BOOK = "1"; // 核算主体统一
	public static final String KEY_TRECODE = "2"; // 按国库维护
	public static final String KEY_BILLORG = "3"; // 按出票单位
	public static final String KEY_GENBANK = "5"; // 代理银行
	public static final String KEY_TAXORG = "4"; // 征收机关
	public static final String KEY_OTHER = "6"; // 按其他
	//加密方式# 0-不加密 1 逐行签名，山东格式1、山西的MD5—3DES加密方式 2、电子签名方式3、其他
	//			#0-不加密 1 逐行签名，山东格式 2、山西的MD5—3DES加密方式 3、电子签名方式4、其他
	public static final String NO_ENCRYPT = "0";
	public static final String SD_ENCRYPT = "1";
	public static final String DES3_ENCRYPT = "2";
	public static final String SIGN_ENCRYPT = "3";
	public static final String SM3_ENCRYPT = "4";
	public static final String OTHER_ENCRYPT = "5";
	
	//加密失败提示信息
	public static final String ENCRYPT_FAIL_INFO = "解密或者验证签名失败!";
	public static final String ENCRYPT_FIAL_INFO_NOKEY = "进行校验码验证时没有找到对应密钥";  //没有找到密钥的提示
	
	
	//特色地域标识 ,主要用于区分销号方式等 目前有 SHANDONG、FUZHOU模式
	public static final String SPECIAL_AREA_SHANDONG = "SHANDONG";
	public static final String SPECIAL_AREA_FUZHOU = "FUZHOU";
	public static final String SPECIAL_AREA_SHANXI = "FUZHOU"; //山西销号方式和福建一样
	public static final String SPECIAL_AREA_GUANGDONG = "GUANGDONG";  //广东标示
	
	//人行办理直接支付退回标识
	public static final String PBC_DERICT_BACK_NO = "0";
	public static final String PBC_DERICT_BACK_YES = "1";
	
	//批量拨付分包数
	public static final int QUANTITY_PACKAGE_COUNT= 499;
	
	//销号待定字符串
	public static final String Check_AddWord_Wait = "销号待定";
	
	//是否强制禁止实拨批量销号 0--禁止  1--不禁止
	public static final String IfStopPayoutSubmit_false="0";
	public static final String IfStopPayoutSubmit_true="1";
	
	//用户类型
	public static final String User_Type_Admin = "0";  //管理员
	public static final String User_Type_Normal ="1";  //操作员
	public static final String User_Type_MainBiz ="2";  //业务主管
	public static final String User_Type_Stat ="3";  //监控员
	
	//实拨资金退回标识
	public static final String MSG_BACK_FLAG_YES = "1";  //实拨退回
	public static final String MSG_BACK_FLAG_NO = "0";  //实拨
	
	//密钥机构状态
	public static final int KEYORG_STATUS_NO = 0;  //密钥未生成
	public static final int KEYORG_STATUS_AFF= 1;  //密钥已生效
	public static final int KEYORG_STATUS_UPD= 2;  //密钥已更新
	//报文编号
	public static final String CMT100= "100";  //大额支付
	public static final String CMT103= "103";  //大额支付
	public static final String PKG001= "001";  //小额支付
	public static final String PKG007= "007";
	public static final String CMT108= "108";  //小额支付
	public static final String TYPE999 = "999";  //核算主体间调拨收入
	public static final String CMT100NAME= "CMT100";  //大额支付
	public static final String CMT103NAME= "CMT103";  //大额支付
	public static final String PKG001NAME= "PKG001";  //小额支付
	public static final String PKG007NAME= "PKG007";
	public static final String CMT108NAME= "CMT108";  //小额支付
	public static final String TYPE999NAME = "TYPE999";  //核算主体间调拨收入
	public static final String HVPS111 = "111";  //二代大额111
	public static final String HVPS112 = "112";  //二代大额112
	public static final String BEPS121 = "121";  //二代小额121
	public static final String BEPS122 = "122";  //二代小额122
	public static final String HVPS111NAME = "HVPS111";  //二代大额111
	public static final String HVPS112NAME = "HVPS112";  //二代大额112
	public static final String BEPS121NAME = "BEPS121";  //二代小额121
	public static final String BEPS122NAME = "BEPS122";  //二代小额122
	
	public static final String CONPAY_ZEROBALANCE= "1";  //零余额
	
	//用户是否登录
	public static final String LOGIN_STATUE_1 = "1"; //已登录
	public static final String LOGIN_STATUE_0 = "0"; //未登录
	
	//是否汇总
	public static final String IS_COLLECT_YES = "0"; //汇总
	public static final String IS_COLLECT_NO = "1"; //未汇总
	
	//在权限表中把权限分为7类 1、业务操作类 2、参数类 3、查询类 4、用户管理 5、日志类,6 公共类，每个人都需要的功能 0 中心特有
	public static final String BIZ_OPER = "1"; //
	public static final String PARAM_MAIN = "2"; //
	public static final String BIZ_QUERY = "3"; //
	public static final String USER_MANAG = "4"; //
	public static final String BIZ_LOGS = "5"; //
	public static final String Mod_PASS = "6"; //
	public static final String ZERO_ONLY = "7"; //角色0独有
	public static final String ZERO_ONE = "8"; //角色0和1共有
	public static final String ZERO_TWO = "9"; //角色0和2共有
	public static final String CENTER_PRIFUNC = "0"; //
	//共享级次
	public static final String shareBudgetLevel ="0";
	//支出报表类型
	public static final String REPORT_PAYOUT_TYPE_1=  "1";// 一般预算支出报表体
	public static final String REPORT_PAYOUT_TYPE_2=  "2";// 实拨资金支出报表体
	public static final String REPORT_PAYOUT_TYPE_3=  "3";// 调拨预算支出报表体
	public static final String REPORT_PAYOUT_TYPE_4=  "4";// 直接支付日报表
	public static final String REPORT_PAYOUT_TYPE_5=  "5";// 授权支付日报表
	public static final String REPORT_PAYOUT_TYPE_6=  "6";// 直接支付退回日报表
	public static final String REPORT_PAYOUT_TYPE_7=  "7";// 授权支付退回日报表
    //额度种类
	public static final String Conpay_Grant= "2";// 授权支付日报表
	public static final String Conpay_Direct= "1";// 直接支付日报表
	//TCBS中心支付行号
	public static final String TCBSCENTERRECKBANKNO= "011100000011";// 
	
	//针对上海设置是否补录标志 0--不补录，1--补录
	/** 不补录*/
	public static final String IF_MATCHBNKNAME_NO = "0";
	/** 补录*/
	public static final String IF_MATCHBNKNAME_YES = "1";
	
	//补录复核状态
	/** 未补录*/
	public static final String CHECKSTATUS_0= "0";
	/** 已补录*/
	public static final String CHECKSTATUS_1 = "1";
	/** 审核失败*/
	public static final String CHECKSTATUS_2 = "2";
	/** 审核成功*/
	public static final String CHECKSTATUS_3 = "3";
	/** 已复核*/
	public static final String CHECKSTATUS_4 = "4";
	
	//系统监控范围 0--操作系统，1--数据库，2--MQ，3--应用服务器，4--CA证书，5--应用日志
	public static final String MONITOR_SYSTEM="0";
	public static final String MONITOR_DB="1";
	public static final String MONITOR_MQ="2";
	public static final String MONITOR_SERVER="3";
	public static final String MONITOR_CA="4";
	public static final String MONITOR_LOG="5";
	
	//导出收入日报范围 0--不包含下级国库 1--包含下级国库
	public static final String SELF_AREA="0";
	public static final String ALL_AREA="1";
	
	// 报表种类
	public static final String REPORT_YEAR = "6"; // 年报
	public static final String REPORT_HALFYEAR = "5"; // 半年报
	public static final String REPORT_QUAR = "4"; // 季报
	public static final String REPORT_MONTH = "3"; // 月报
	public static final String REPORT_TEN = "2"; // 旬报
	public static final String REPORT_DAY = "1"; // 日报
	
	// 栏式类型 
	public static final String REPORT_FOUR = "4"; // 四栏式
	public static final String REPORT_THREE = "3"; // 三栏式
	public static final String REPORT_TWO = "2"; // 二栏式

	// 金额单位（元、万元、亿元，默认亿元）
//	public static final String MONEY_UNIT_1 = "1"; // 元
//	public static final String MONEY_UNIT_2 = "2"; // 万元
//	public static final String MONEY_UNIT_3 = "3"; // 亿元
	// 金额单位
	public static final String MONEY_UNIT_5 = "5"; // 元
	public static final String MONEY_UNIT_4 = "4"; // 万元
	public static final String MONEY_UNIT_3 = "3"; // 百万
	public static final String MONEY_UNIT_2 = "2"; // 十万
	public static final String MONEY_UNIT_1 = "1"; // 千元
	
	//报表名称
	/** 收入月报*/
	public static final String RPT_TYPE_NAME_1 = "预算收入月报";
	/** 支出月报*/
	public static final String RPT_TYPE_NAME_2 = "预算支出月报";
	/** 收支旬报*/
	public static final String RPT_TYPE_NAME_3 = "预算收支旬报";
	/** 全口径税收*/
	public static final String RPT_TYPE_NAME_4 = "全口径税收";
	/** 年末收支调度表*/
	public static final String RPT_TYPE_NAME_5 = "年末收支调度表";
	
	// 科目种类 's_SubjectClass'
	/** 科目种类 :收入*/
	public static final String S_SUBJECTCLASS_INCOME = "1"; // 收入
	/** 科目种类 :支出功能*/
	public static final String S_SUBJECTCLASS_PAYOUT2 = "2"; // '支出功能'
	/** 科目种类 :支出经济*/
	public static final String S_SUBJECTCLASS_PAYOUT3 = "3"; // '支出经济'
	
	/**
	 * 是否含项合计
	 */
	public static final String REPORTTYPE_0405_NO = "1";// '不含项合计'
	public static final String REPORTTYPE_0405_YES = "2";// '含项合计'
	
	/**
	 * 是否含款合计
	 */
	public static final String REPORTTYPE_0406_NO = "1";// '不含款合计'
	public static final String REPORTTYPE_0406_YES = "2";// '含款合计'
	
	// 科目类型 s_SubjectType
	/** 科目类型:经济支出*/
	public static final String SBT_TYPE_PAYOUT_ALL = "00"; 
	/** 科目类型:一般预算内*/
	public static final String SBT_TYPE_BUDGET_IN = "01"; // 一般预算内
	/** 科目类型:一般预算外 */
	public static final String SBT_TYPE_BUDGET_OUT = "02"; // 一般预算外
	/** 科目类型:一般预算基金*/
	public static final String SBT_TYPE_FUND_IN = "03"; // 一般预算基金
	/** 科目类型:一般预算债务*/
	public static final String SBT_TYPE_DEBT_IN = "04"; // 一般预算债务
	/** 科目类型:预算外基金*/
	public static final String SBT_TYPE_FUND_OUT = "05"; // 预算外基金
	/** 科目类型:预算外债务*/
	public static final String SBT_TYPE_DEBT_OUT = "06"; // 预算外债务
	/** 科目类型:国有资本经营预算支出*/
	public static final String SBT_TYPE_STATE_OWNED = "08"; // 国有资本经营预算支出
	// 科目分类 s_ClassFlag
	/** 科目分类 :税收收入*/
	public static final String SBT_CLASS_TAXINCOME = "01"; // 税收收入
	/** 科目分类 : 社保基金收入*/
	public static final String SBT_CLASS_FUNDINCOME = "02"; // 社保基金收入
	/** 科目分类 : 非税收入*/
	public static final String SBT_CLASS_TAXNO = "03"; // 非税收入
	/** 科目分类 : 贷款转贷回收本金*/
	public static final String SBT_CLASS_CAPINCOME = "04"; // 贷款转贷回收本金
	/** 科目分类 :债务收入*/
	public static final String SBT_CLASS_DEBTINCOME = "05"; // 债务收入
	/** 科目分类 : 转移性收入*/
	public static final String SBT_CLASS_TRANSINCOME = "06"; // 转移性收入
	/** 科目分类 :支出功能*/
	public static final String SBT_CLASS_FUNCPAY = "07"; // 支出功能
	/** 科目分类 :支出经济*/
	public static final String SBT_CLASS_ECOMPAY = "08"; // 支出经济
	
	
	//报表名称type
	/** 收入月报*/
	public static final String RPT_TYPE_1 = "1";
	/** 支出月报*/
	public static final String RPT_TYPE_2 = "2";
	/** 收支旬报*/
	public static final String RPT_TYPE_3 = "3";
	/** 全口径税收*/
	public static final String RPT_TYPE_4 = "4";
	/** 年末收支调度表*/
	public static final String RPT_TYPE_5 = "5";
	
	// 科目分类
	public static final String SUBJECT_INCOME = "01"; // 税收收入
	
	// 报表级次（上海）1--中央级、2--市级、4--区级、6--地方级、7--不分级次
    //地方级=市级+区级         不分级次=中央级+市级+区级
	public static final String SUBJECTLEVEL_CENTER = "1"; // 中央级
	public static final String SUBJECTLEVEL_CITY = "2"; // 市级
	public static final String SUBJECTLEVEL_AREA = "4"; // 区级
	public static final String SUBJECTLEVEL_LOCAL = "6"; // 地方级
	public static final String SUBJECTLEVEL_ANY = "7"; // 不分级次
	
	//科目属性（1-类、2-款、3-项、4-目）
	public static final String SUBJECTATTR_CLASS="1";
	public static final String SUBJECTATTR_PAGE="2";
	public static final String SUBJECTATTR_PROJECT="3";
	public static final String SUBJECTATTR_ITEM="4";
	
	//旬报类型（1-上旬、2-中旬、3-下旬）
	public static final String TENRPT_TOP="x1";
	public static final String TENRPT_MIDDLE="x2";
	public static final String TENRPT_BOTTOM="x3";
	
	//季报类型（1-第一季度、2-第二季度、3-第三季度、4-第四季度）
	public static final String QUARTERRPT_FIRST="j1";
	public static final String QUARTERRPT_SECOND="j2";
	public static final String QUARTERRPT_THREE="j3";
	public static final String QUARTERRPT_FOUR="j4";
	
	//半年报类型（1-上半年、2-下半年）
	public static final String HALFYEARRPT_TOP="n1";
	public static final String HALFYEARRPT_BOTTOM="n2";
	
	//调拨标志（0-非调拨、1-调拨、2-不区分）
	public static final String MOVEFLAG_NOMOVE="0";
	public static final String MOVEFLAG_MOVE="1";
	public static final String MOVEFLAG_NORMAL="2";
	
	/**
	 * 凭证回单接收机构代码(财政)
	 */
	public static final String ORGCODE_FIN="011";
	/**
	 * 凭证回单发送机构代码(人行)
	 */
	public static final String ORGCODE_RH="001";
	
	//导出TBS文件
	public static final String TBS_SBZJ="0";//实拨资金
	public static final String TBS_ZJZFED="1";//直接支付额度
	public static final String TBS_SQZFED="2";//授权支付额度
	public static final String TBS_JZZFHKSQ="3";//集中支付划款申请
	
	//山东一本通批量拨付文件ftp状态列表
	public static final String FTPFILESTATE_DOWNLOAD="1";//从ftp下载文件到系统
	public static final String FTPFILESTATE_ADDLOAD="2";//已经加载
	public static final String FTPFILESTATE_RETURN="3";//已经回执
	public static final String FTPFILESTATE_SUMMARY="4";//汇总文件
	public static final String FTPFILESTATE_UNKNOWN="5";//未知文件
	
	/**定义TBS回执文件类型**/
	public static final String TBS_FILESLIP_YSZC = "YSZC"; //实拨资金
	public static final String TBS_FILESLIP_ZJED = "ZJED"; //直接额度
	public static final String TBS_FILESLIP_SQED = "SQED"; //授权额度
	public static final String TBS_FILESLIP_ZFQS = "ZFQS"; //集中支付清算
	public static final String TBS_FILESLIP_ZFTH = "ZFTH"; //集中支付退回
	public static final String TBS_FILESLIP_SRTK = "SRTK"; //收入退库
	
	/**
	 * 凭证处理状态  0-正常 
	 */
	public static final String VOUCHE_PROSTATE_NORMAL="0";
	/**
	 * 凭证处理状态  1-退回
	 */
	public static final String VOUCHE_PROSTATE_RETURN="1";
	
	
	/**
	 * 是否转发TIPS  0-转发[地方横联]
	 */
	public static final String MSG_RELAY_TIPS="0";
	/**
	 * 是否转发TIPS  1-不转发[无纸化]
	 */
	public static final String MSG_NORELAY_TIPS="1";
	
	
	/**
	 * 大额税源支出统计表查询方式  0-大额税源查询
	 */
	public static final String LARGE_TAX_QUERY="0";
	/**
	 * 大额税源支出统计表查询方式  1-大额支出查询
	 */
	public static final String LARGE_PAY_QUERY="1";
	
	
	/**
	 *  TCBS收入类报表文件种类  0-收入日报
	 */
	public static final String REPORT_STYLE_INCOME="0";
	/**
	 * TCBS收入类报表文件种类  1-库存日报
	 */
	public static final String REPORT_STYLE_STOCK="1";
	
	/**
	 * 向财政自动发送的报表种类 报表类型+级次
	 */
	public static final String IncomeReport_11 = "11";// 中央级预算收入日报表
	public static final String IncomeReport_12=  "12";// 省级预算收入日报表
	public static final String IncomeReport_13 = "13";// 地市级预算收入日报表
	public static final String IncomeReport_14=  "14";// 区县级预算收入日报表
	public static final String IncomeReport_15 = "15";// 乡镇预算收入日报表
	public static final String IncomeReport_DF = "DF";// 地方级收入日报表
	public static final String PayOutReport_20= "20";//  一般预算支出日报表
	public static final String FinReport=  "90";//财政库存日报表
	
	
	/**
	 * 业务类型编码(上海扩展)  1-单笔 
	 */
	public static final String BIZTYPE_CODE_SINGLE="1";
	/**
	 * 业务类型编码(上海扩展)  3-工资
	 */
	public static final String BIZTYPE_CODE_SALARY="3";
	/**
	 * 业务类型编码(上海扩展)  4-批量
	 */
	public static final String BIZTYPE_CODE_BATCH="4";
	
	/**
	 * 单笔退款1
	 * 全部退款2
	 * 不确定退款5
	 */
	public static String REFUNDTYPE_1 = "1";
	public static String REFUNDTYPE_2 = "2";
	public static String REFUNDTYPE_5 = "5";
	
	/**
	 * 单位清册 全量标志  1-全量   2-增量
	 */
	public static final String ALLFLAG_FULL = "1";
	public static final String ALLFLAG_INCREMENT = "2";
	
	
	/**
	 * 支付方式编码 11-直接支付  12-授权支付  91-实拨
	 */
	public static final String DIRECT_PAY_CODE = "11";
	public static final String GRANT_PAY_CODE = "12";
	public static final String PAYOUT_PAY_CODE = "91";	
	
	/**
	 * 退款类型
	 * 1 正常退款
	 * 2 负凭证
	 */
	public static final String TKLX_1 = "1";
	public static final String TKLX_2 = "2";
	
	/**
	 * 接收对账填写索引表数据来源用
	 */
	public static final String ACCOUNT_CHECK = "3508,3509,3510,3511,3512,3513";
	/**
	 * 
	 * 直接支付退款种类
	 */
	public static final String NOREFUNDTYPE = "5";
	
	/**
	 * 
	 *金额单位
	 */
	public static final String MoneyUnit_YUAN = "1";
	public static final String MoneyUnit_WAN = "2";
	public static final String MoneyUnit_BW = "3";
	public static final String MoneyUnit_QW = "4";
	public static final String MoneyUnit_YI = "5";
	/**
	 * 一般预算内、预算内基金、转移性收入虚拟科目代码
	 */
	public static final String COMMON_BUGGET_IN = "-";
	public static final String BUGGET_IN_FUND = "--";
	public static final String STATE_OWNED = "---";
	public static final String MOVE_INCOME = "----";

	/** 数据比对成功状态 */
	public static final String MERGE_VALIDATE_SUCCESS = "1"; // 比对成功
	public static final String MERGE_VALIDATE_FAILURE = "0"; // 比对失败
	public static final String MERGE_VALIDATE_NOTCOMPARE = "2"; // 未必对
	// 调用未知进度的返回结果
	public static final int UNKNOWPROCESSDIALOG_RESULT_YES = 0;// 成功
	public static final int UNKNOWPROCESSDIALOG_RESULT_NO = -1;// 失败
	
	/**
	 *数据备份文件的目录长度 12核算主体代码+yyyyMMddHHmmss
	 */
	public final static int DATA_BACKUP_FILEDIRECTORYNAME_SIZE = 26;
	// db2数据库导入的modifiedBy子句
	// 字符分隔符优先级高
	public final static String DATA_IMPORT_MODIFIEDBY_DELPRIORITYCHAR = "modified by delprioritychar";
	public final static String DATA_IMPORT_MODIFIEDBY_DELPRIORITYCHARANDLOB = "modified by lobsinfile delprioritychar";
	
	// db2数据库导入的几种方式
	public final static String DATA_IMPORT_MODE_INSERT = "insert";
	public final static String DATA_IMPORT_MODE_INSERT_UPDATE = "insert_update";
	public final static String DATA_IMPORT_MODE_REPLACE = "replace";
	public final static String DATA_IMPORT_MODE_REPLACE_CREATE = "repalce_create";
	public final static String DATA_IMPORT_MODE_CREATE = "create";
}
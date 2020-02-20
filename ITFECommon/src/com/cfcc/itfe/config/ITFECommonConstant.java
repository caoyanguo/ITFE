package com.cfcc.itfe.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;

@SuppressWarnings("unchecked")
public class ITFECommonConstant {
	
	public static HashMap<String, String> TBS_011 = (HashMap<String, String>) ContextFactory
	.getApplicationContext().getBean("TBS_011");
	
	public static HashMap<String, String> TBS_111 = (HashMap<String, String>) ContextFactory
	.getApplicationContext().getBean("TBS_111");
	
	public static HashMap<String, String> TBS_TREANDBANK = (HashMap<String, String>) ContextFactory
	.getApplicationContext().getBean("TBS_TREANDBANK");
	
	public static HashMap<String, String> sCONSTANTCODEMap = (HashMap<String, String>) ContextFactory
			.getApplicationContext().getBean("ITFE.CONFIG");

	private static List<String> ADMINFUNC = (ArrayList<String>) ContextFactory
			.getApplicationContext().getBean("ADMIN_FUNC");
	
	private static HashMap<String, String> msgNoList = (HashMap<String, String>) ContextFactory
	.getApplicationContext().getBean("MsgNoConfig");
	
	private static HashMap<String, String> bizMsgNo = (HashMap<String, String>) ContextFactory
	.getApplicationContext().getBean("BizMsgNo");
	
	private static HashMap<String, String> TimerbizMsgNo = (HashMap<String, String>) ContextFactory
	.getApplicationContext().getBean("TimerBizMsgNo");
	
	private static HashMap<String, String> timerConf = (HashMap<String, String>) ContextFactory
	.getApplicationContext().getBean("TimerProcMsg");
	
	private static HashMap<String, String> timerCollectConf = (HashMap<String, String>) ContextFactory
	.getApplicationContext().getBean("TimerCollectTaxOrgBill");

	//XML转换成java的配置信息列表
	private static HashMap<String, String> xml2javaMsgConfig = (HashMap<String, String>) ContextFactory
			.getApplicationContext().getBean("xml2javaMsgConfig");
	
	//java转换成XML的配置信息列表
	private static HashMap<String, String> java2xmlMsgConfig = (HashMap<String, String>) ContextFactory
			.getApplicationContext().getBean("java2xmlMsgConfig");
	//定时读取凭证列表
	public static HashMap<String, String> TimerbizVoucherMsgNo = (HashMap<String, String>) ContextFactory
			.getApplicationContext().getBean("TimerbizVoucherMsgNo");
	
	//定时读取凭证列表
	public static HashMap<String, String>  dbparam= (HashMap<String, String>) ContextFactory
			.getApplicationContext().getBean("DBPARAM");
	
	// 系统备份恢复的参数设置
	public final static String SYS_BACKUP_PATH = FileUtil
			.transFileSeparatorToSysSeparator(sCONSTANTCODEMap
					.get("SYS_BACKUP_PATH"));
	public final static String SYS_BACKUP_FILE_TYPE = sCONSTANTCODEMap
			.get("SYS_BACKUP_FILE_TYPE");
	public final static String SYS_BACKUP_SQL_FILE_NAME = sCONSTANTCODEMap
			.get("SYS_BACKUP_SQL_FILE_NAME");
	public final static String SYS_RESTORE_PATH = FileUtil
			.transFileSeparatorToSysSeparator(sCONSTANTCODEMap
					.get("SYS_RESTORE_PATH"));
	public final static String SYS_RESTORE_FILE_TYPE = sCONSTANTCODEMap
			.get("SYS_RESTORE_FILE_TYPE");
	public final static String SYS_RESTORE_SQL_FILE_NAME = sCONSTANTCODEMap
			.get("SYS_RESTORE_SQL_FILE_NAME");
	public final static String LOB_FILE_DIRECTORY_NAME = sCONSTANTCODEMap
			.get("LOB_FILE_DIRECTORY_NAME");
	public final static String LOB_FILE_NAME = sCONSTANTCODEMap
			.get("LOB_FILE_NAME");
	// 数据备份恢复
	// ClientFileTransferUtil 根路径
	public static String FILE_TRANSFER_CONFIG_ROOT;
	
	
	//数据库
	public static String DBTYPE;
	//WEBSERVICE的服务地址
	public static String WEBSERVICE_URL;
	//ocx访问WEBSERVICE的服务地址
	public static String OCXSERVICE_URL;
	//ocx访问签章服务的地址
	public static String OCXSERVICE_STAMPURL;
	//pdf文件导出服务地址
	public static String PDFDERVICE_URL;
	//客户端访问ocx服务是否使用代理
	public static String OCXSERVICE_ISCLIENTPROXY;
	//报表数据发送财政端WebService地址
	public static String FINSERVICE_URL;
	//转讫章签章方式
	public static String ROTARYSTAMP;
	//公章签章方式
	public static String OFFICIALSTAMP;
	//是否进行实拨资金调拨科目校验
	public static String CHECKPAYOUTSUBJECT;
	//所有报文编号
	public static HashMap<String, String> msgNoConfig;
	//业务表报文编号
	public static HashMap<String, String> bizMsgNoList;
	//定时解包报文列表
	public static HashMap<String, String> timerBizMsgNoList;
	//定时任务触发时间列表
	public static HashMap<String, String> timerConfList;
	
	//定时任务汇总征收机关报表时间列表
	public static HashMap<String, String> timerCollectConfList;
	//客户端版本号clientedition
	public static String CLIENTEDITION;
	// 系统名称
	public static String ITFE_NAME;
	//系统管理员
	public static String ADMIN;
	// 系统根路径
	public static String FILE_ROOT_PATH;
	// 地税文件APP服务器路径
	//public static String FILE_PLACE_PATH;
	// 每次取出的 最大记录数
	public static int MAX_RECORD_RRETRIEVE;
	// 税率保留的小数位
	public static int TAX_RATE_SCALE;
	// 字符集
	public static String CHARSET_NAME;
	// 文件读入缓冲区大小
	public static int FILE_BUF_SIZE;
	//文件上传路径 -->
	public static String FILE_UPLOAD;
	//文件解压缩路径
	public static String FILE_UNZIP_PATH;
	//数据库别名
	public static String DB_ALIAS;
	//数据库用户名称
	public static String DB_USERNAME;
	//数据库密码
	public static String DB_PASSWORD;
	//源节点
	public static String SRC_NODE;
	//源节点
	public static String SRCCITY_NODE;
	//目标节点
	public static String DES_NODE;
	//每次提交税票包数量 
	public static String COMMIT_NUM;
	
	public static String default_pwd;
	//划款退回文件是否自动生成原凭证编号、原凭证日期
	public static String IFAUTOGENORIVOUNO;
	
	//判断是否采用定时发送报文
	public static String IFSENDBYTIMER;
	//判断给财政转发报表
	public static String IFSENDMSGTOFIN;
	//判断是否采用新接口
	public static String IFNEWINTERFACE;
	//判断是否需要更新税票
	public static String IFUPDATETAXVOU;
	//是否强制禁止实拨资金批量销号
	public static String IFSTOPPAYOUTSUBMIT;
	// 接收报文文件路径
	public static String MSG_FILE_RECV_PATH;
	// 发送报文文件路径
	public static String MSG_FILE_SEND_PATH;
	// 报文临时文件保留的最大月数
	public static int MSG_LOG_FILE_RESERVE_MONTHS;
	//程序版本
	public static String EDITION;
	//是否进行国库级次与预算级次的校验
	public static String IFVERIFYTRELEVEL;
	//是否根据行名进行行号补录
	public static String ISMATCHBANKNAME;
	//设定中国人民银行的网间互联平台(或TIPS系统外部网关)节点代码标识
	public static String PBCNODECODE;
	//TIPS队列管理器名称
	public static String MGRNAME;
	//证书的有效期
	public static String CAVALIDDATE;
	//是否校验额度
	public static String ISCHECKPAYPLAN;
	//是否比对数据
	public static String ISCOMPAREDATA;
	//系统标识
	public static String SYSFLAG;
	//是否通过前置提交划款申请
	public static String ISITFECOMMIT;
	//财政批量队列名
	public static String MQTOMOFBATCH;
	//财政实时队列名
	public static String MQTOMOFONLINE;
	//市财政批量队列名
	public static String MQTOMOFBATCHCITY;
	//市财政实时队列名
	public static String MQTOMOFONLINECITY;
	//山东一本通批量拨付开始
	public static String FTPIPSTRING = "10.1.5.181";//远程Ftp IP
	public static String FTPUSERSTRING = "root";//远程Ftp登录用户
	public static String FTPPASSWORDSTRING = "rootroot";//远程Ftp登录密码
	public static String FTPPATHSTRING = "/itfe";//远程Ftp目录
	public static String LOCALPATHSTRING = "D:";//下载到本地的路径
	public static String FTPUPLOADPATH = "/itfe/pbc";//上传到ftp的路径
	public static String PUBLICPARAM;//公共配置参数避免一直加参数
	// 数据库用户表模式名
	public static String DB_TABLE_SCHEMA;
	//山东一本通批量拨付结束
	/**
	 * 登陆方式，0-用户名口令；1-用户名，UK；2-用户名口令，UK
	 */
	public static char LOGIN_TYPE = '0';
	
	public static List<String> ADMIN_FUNC;
	
	/**
	 * 业务凭证报文未处理时的处理码
	 */
	public static final String STATUS_BEGIN = "Z0000";
	/**
	 * 业务凭证报文发送失败时的处理码
	 */
	public static final String STATUS_FAILED = "Z0009";
	/**
	 * 业务凭证报文发送成功时的处理码
	 */
	public static final String STATUS_SUCCESS = "Z0010";
	/**
	 * 总库报文处理完毕处理码
	 */
	public static final String STATUS_FINISHED = "Z0050";
	/**
	 * 总库报文已作废处理码
	 */
	public static final String STATUS_CANCELED = "Z0051";

	// 通过Shell调用db2
	public static String CALL_DB2 = "db2 -tvf ";
	
	//是否对征收机关代码、辅助标志进行转换 0不转换 1 转换
	public static String ISCONVERT="1";

	static {
		ADMIN=sCONSTANTCODEMap.get("ADMIN");
		ADMIN_FUNC=ADMINFUNC;
		ITFE_NAME = sCONSTANTCODEMap.get("ITFE_NAME");
		FILE_ROOT_PATH = sCONSTANTCODEMap.get("FILE_ROOT_PATH");
		//FILE_PLACE_PATH = sCONSTANTCODEMap.get("FILE_PLACE_PATH");
		MAX_RECORD_RRETRIEVE = new Integer(sCONSTANTCODEMap
				.get("MAX_RECORD_RRETRIEVE")).intValue();
		CHARSET_NAME = sCONSTANTCODEMap.get("CHARSET_NAME");
		LOGIN_TYPE = (sCONSTANTCODEMap.get("LOGINTYPE")==null?'0':sCONSTANTCODEMap.get("LOGINTYPE").charAt(0));
		FILE_BUF_SIZE = new Integer(sCONSTANTCODEMap.get("FILE_BUF_SIZE"))
				.intValue();
		String os = System.getProperties().getProperty("os.name");
		if (os.indexOf("Win") >= 0) {
			CALL_DB2 = "db2cmd.exe -c -w -i db2 -tvf ";
		} else {
			CALL_DB2 = "db2 -tvf ";
		}
		FILE_UPLOAD=sCONSTANTCODEMap.get("FILE_UPLOAD");
		FILE_UNZIP_PATH=sCONSTANTCODEMap.get("FILE_UNZIP_PATH");
		DB_ALIAS=sCONSTANTCODEMap.get("DB_ALIAS");
		DB_USERNAME = sCONSTANTCODEMap.get("DB_USERNAME");
		DB_PASSWORD = sCONSTANTCODEMap.get("DB_PASSWORD");
		default_pwd = sCONSTANTCODEMap.get("default_pwd");
		SRC_NODE=sCONSTANTCODEMap.get("SRC_NODE");
		SRCCITY_NODE=sCONSTANTCODEMap.get("SRCCITY_NODE");
		DES_NODE=sCONSTANTCODEMap.get("DES_NODE");
		COMMIT_NUM = sCONSTANTCODEMap.get("COMMIT_NUM");
		IFSENDBYTIMER = sCONSTANTCODEMap.get("IFSENDBYTIMER");
		MSG_FILE_RECV_PATH = sCONSTANTCODEMap.get("MSG_FILE_RECV_PATH");
		MSG_FILE_SEND_PATH = sCONSTANTCODEMap.get("MSG_FILE_SEND_PATH");
		MSG_LOG_FILE_RESERVE_MONTHS = Integer.parseInt(sCONSTANTCODEMap.get("MSG_LOG_FILE_RESERVE_MONTHS"));
		IFNEWINTERFACE =sCONSTANTCODEMap.get("IFNEWINTERFACE");
		IFSENDMSGTOFIN = sCONSTANTCODEMap.get("IFSENDMSGTOFIN");
		EDITION =  sCONSTANTCODEMap.get("EDITION");
		CLIENTEDITION = sCONSTANTCODEMap.get("CLIENTEDITION");
		msgNoConfig = msgNoList;
		DB_TABLE_SCHEMA = sCONSTANTCODEMap.get("DB_TABLE_SCHEMA");
		bizMsgNoList =bizMsgNo;
		timerConfList=timerConf;
		timerCollectConfList=timerCollectConf;
		IFUPDATETAXVOU =sCONSTANTCODEMap.get("IFUPDATETAXVOU");
		IFSTOPPAYOUTSUBMIT =sCONSTANTCODEMap.get("IFSTOPPAYOUTSUBMIT");
		IFVERIFYTRELEVEL=sCONSTANTCODEMap.get("IFVERIFYTRELEVEL");
		timerBizMsgNoList = TimerbizMsgNo;
		ISCONVERT = sCONSTANTCODEMap.get("ISCONVERT");
		IFAUTOGENORIVOUNO =sCONSTANTCODEMap.get("IFAUTOGENORIVOUNO");
		ISMATCHBANKNAME = sCONSTANTCODEMap.get("ISMATCHBANKNAME");
		PBCNODECODE = sCONSTANTCODEMap.get("PBCNODECODE");
		MGRNAME = sCONSTANTCODEMap.get("MGRNAME");
		CAVALIDDATE =sCONSTANTCODEMap.get("CAVALIDDATE");
		WEBSERVICE_URL=sCONSTANTCODEMap.get("WEBSERVICE_URL");
		OCXSERVICE_URL=sCONSTANTCODEMap.get("OCXSERVICE_URL");
		OCXSERVICE_STAMPURL=sCONSTANTCODEMap.get("OCXSERVICE_STAMPURL");
		PDFDERVICE_URL=sCONSTANTCODEMap.get("PDFDERVICE_URL");
		OCXSERVICE_ISCLIENTPROXY=sCONSTANTCODEMap.get("OCXSERVICE_ISCLIENTPROXY");
		FINSERVICE_URL=sCONSTANTCODEMap.get("FINSERVICE_URL");
		ISCHECKPAYPLAN=sCONSTANTCODEMap.get("ISCHECKPAYPLAN");
		ISCOMPAREDATA=sCONSTANTCODEMap.get("ISCOMPAREDATA");
		ROTARYSTAMP=sCONSTANTCODEMap.get("ROTARYSTAMP");
		OFFICIALSTAMP=sCONSTANTCODEMap.get("OFFICIALSTAMP");
		CHECKPAYOUTSUBJECT=sCONSTANTCODEMap.get("CHECKPAYOUTSUBJECT");
		SYSFLAG=sCONSTANTCODEMap.get("SYSFLAG");
		ISITFECOMMIT =sCONSTANTCODEMap.get("ISITFECOMMIT");
		MQTOMOFBATCH =sCONSTANTCODEMap.get("MQTOMOFBATCH");
		MQTOMOFONLINE =sCONSTANTCODEMap.get("MQTOMOFONLINE");
		MQTOMOFBATCHCITY =sCONSTANTCODEMap.get("MQTOMOFBATCHCITY");
		MQTOMOFONLINECITY =sCONSTANTCODEMap.get("MQTOMOFONLINECITY");
		FTPIPSTRING =sCONSTANTCODEMap.get("FTPIPSTRING");
		FTPUSERSTRING =sCONSTANTCODEMap.get("FTPUSERSTRING");
		FTPPASSWORDSTRING =sCONSTANTCODEMap.get("FTPPASSWORDSTRING");
		FTPPATHSTRING =sCONSTANTCODEMap.get("FTPPATHSTRING");
		LOCALPATHSTRING =sCONSTANTCODEMap.get("LOCALPATHSTRING");
		FTPUPLOADPATH = sCONSTANTCODEMap.get("FTPUPLOADPATH");
		PUBLICPARAM = sCONSTANTCODEMap.get("PUBLICPARAM");//公共配置参数避免一直加参数
		DBTYPE = dbparam.get("DBTYPE");
		FILE_TRANSFER_CONFIG_ROOT = FileUtil
		.transFileSeparatorToSysSeparator(sCONSTANTCODEMap
				.get("FILE_TRANSFER_CONFIG_ROOT"));
	}
	public static HashMap<String, String> getXml2javaMsgConfig() {
		return xml2javaMsgConfig;
	}

	public static HashMap<String, String> getJava2xmlMsgConfig() {
		return java2xmlMsgConfig;
	}
}

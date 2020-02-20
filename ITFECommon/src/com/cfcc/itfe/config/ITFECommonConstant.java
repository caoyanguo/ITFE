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

	//XMLת����java��������Ϣ�б�
	private static HashMap<String, String> xml2javaMsgConfig = (HashMap<String, String>) ContextFactory
			.getApplicationContext().getBean("xml2javaMsgConfig");
	
	//javaת����XML��������Ϣ�б�
	private static HashMap<String, String> java2xmlMsgConfig = (HashMap<String, String>) ContextFactory
			.getApplicationContext().getBean("java2xmlMsgConfig");
	//��ʱ��ȡƾ֤�б�
	public static HashMap<String, String> TimerbizVoucherMsgNo = (HashMap<String, String>) ContextFactory
			.getApplicationContext().getBean("TimerbizVoucherMsgNo");
	
	//��ʱ��ȡƾ֤�б�
	public static HashMap<String, String>  dbparam= (HashMap<String, String>) ContextFactory
			.getApplicationContext().getBean("DBPARAM");
	
	// ϵͳ���ݻָ��Ĳ�������
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
	// ���ݱ��ݻָ�
	// ClientFileTransferUtil ��·��
	public static String FILE_TRANSFER_CONFIG_ROOT;
	
	
	//���ݿ�
	public static String DBTYPE;
	//WEBSERVICE�ķ����ַ
	public static String WEBSERVICE_URL;
	//ocx����WEBSERVICE�ķ����ַ
	public static String OCXSERVICE_URL;
	//ocx����ǩ�·���ĵ�ַ
	public static String OCXSERVICE_STAMPURL;
	//pdf�ļ����������ַ
	public static String PDFDERVICE_URL;
	//�ͻ��˷���ocx�����Ƿ�ʹ�ô���
	public static String OCXSERVICE_ISCLIENTPROXY;
	//�������ݷ��Ͳ�����WebService��ַ
	public static String FINSERVICE_URL;
	//ת����ǩ�·�ʽ
	public static String ROTARYSTAMP;
	//����ǩ�·�ʽ
	public static String OFFICIALSTAMP;
	//�Ƿ����ʵ���ʽ������ĿУ��
	public static String CHECKPAYOUTSUBJECT;
	//���б��ı��
	public static HashMap<String, String> msgNoConfig;
	//ҵ����ı��
	public static HashMap<String, String> bizMsgNoList;
	//��ʱ��������б�
	public static HashMap<String, String> timerBizMsgNoList;
	//��ʱ���񴥷�ʱ���б�
	public static HashMap<String, String> timerConfList;
	
	//��ʱ����������ջ��ر���ʱ���б�
	public static HashMap<String, String> timerCollectConfList;
	//�ͻ��˰汾��clientedition
	public static String CLIENTEDITION;
	// ϵͳ����
	public static String ITFE_NAME;
	//ϵͳ����Ա
	public static String ADMIN;
	// ϵͳ��·��
	public static String FILE_ROOT_PATH;
	// ��˰�ļ�APP������·��
	//public static String FILE_PLACE_PATH;
	// ÿ��ȡ���� ����¼��
	public static int MAX_RECORD_RRETRIEVE;
	// ˰�ʱ�����С��λ
	public static int TAX_RATE_SCALE;
	// �ַ���
	public static String CHARSET_NAME;
	// �ļ����뻺������С
	public static int FILE_BUF_SIZE;
	//�ļ��ϴ�·�� -->
	public static String FILE_UPLOAD;
	//�ļ���ѹ��·��
	public static String FILE_UNZIP_PATH;
	//���ݿ����
	public static String DB_ALIAS;
	//���ݿ��û�����
	public static String DB_USERNAME;
	//���ݿ�����
	public static String DB_PASSWORD;
	//Դ�ڵ�
	public static String SRC_NODE;
	//Դ�ڵ�
	public static String SRCCITY_NODE;
	//Ŀ��ڵ�
	public static String DES_NODE;
	//ÿ���ύ˰Ʊ������ 
	public static String COMMIT_NUM;
	
	public static String default_pwd;
	//�����˻��ļ��Ƿ��Զ�����ԭƾ֤��š�ԭƾ֤����
	public static String IFAUTOGENORIVOUNO;
	
	//�ж��Ƿ���ö�ʱ���ͱ���
	public static String IFSENDBYTIMER;
	//�жϸ�����ת������
	public static String IFSENDMSGTOFIN;
	//�ж��Ƿ�����½ӿ�
	public static String IFNEWINTERFACE;
	//�ж��Ƿ���Ҫ����˰Ʊ
	public static String IFUPDATETAXVOU;
	//�Ƿ�ǿ�ƽ�ֹʵ���ʽ���������
	public static String IFSTOPPAYOUTSUBMIT;
	// ���ձ����ļ�·��
	public static String MSG_FILE_RECV_PATH;
	// ���ͱ����ļ�·��
	public static String MSG_FILE_SEND_PATH;
	// ������ʱ�ļ��������������
	public static int MSG_LOG_FILE_RESERVE_MONTHS;
	//����汾
	public static String EDITION;
	//�Ƿ���й��⼶����Ԥ�㼶�ε�У��
	public static String IFVERIFYTRELEVEL;
	//�Ƿ�������������кŲ�¼
	public static String ISMATCHBANKNAME;
	//�趨�й��������е����以��ƽ̨(��TIPSϵͳ�ⲿ����)�ڵ�����ʶ
	public static String PBCNODECODE;
	//TIPS���й���������
	public static String MGRNAME;
	//֤�����Ч��
	public static String CAVALIDDATE;
	//�Ƿ�У����
	public static String ISCHECKPAYPLAN;
	//�Ƿ�ȶ�����
	public static String ISCOMPAREDATA;
	//ϵͳ��ʶ
	public static String SYSFLAG;
	//�Ƿ�ͨ��ǰ���ύ��������
	public static String ISITFECOMMIT;
	//��������������
	public static String MQTOMOFBATCH;
	//����ʵʱ������
	public static String MQTOMOFONLINE;
	//�в�������������
	public static String MQTOMOFBATCHCITY;
	//�в���ʵʱ������
	public static String MQTOMOFONLINECITY;
	//ɽ��һ��ͨ����������ʼ
	public static String FTPIPSTRING = "10.1.5.181";//Զ��Ftp IP
	public static String FTPUSERSTRING = "root";//Զ��Ftp��¼�û�
	public static String FTPPASSWORDSTRING = "rootroot";//Զ��Ftp��¼����
	public static String FTPPATHSTRING = "/itfe";//Զ��FtpĿ¼
	public static String LOCALPATHSTRING = "D:";//���ص����ص�·��
	public static String FTPUPLOADPATH = "/itfe/pbc";//�ϴ���ftp��·��
	public static String PUBLICPARAM;//�������ò�������һֱ�Ӳ���
	// ���ݿ��û���ģʽ��
	public static String DB_TABLE_SCHEMA;
	//ɽ��һ��ͨ������������
	/**
	 * ��½��ʽ��0-�û������1-�û�����UK��2-�û������UK
	 */
	public static char LOGIN_TYPE = '0';
	
	public static List<String> ADMIN_FUNC;
	
	/**
	 * ҵ��ƾ֤����δ����ʱ�Ĵ�����
	 */
	public static final String STATUS_BEGIN = "Z0000";
	/**
	 * ҵ��ƾ֤���ķ���ʧ��ʱ�Ĵ�����
	 */
	public static final String STATUS_FAILED = "Z0009";
	/**
	 * ҵ��ƾ֤���ķ��ͳɹ�ʱ�Ĵ�����
	 */
	public static final String STATUS_SUCCESS = "Z0010";
	/**
	 * �ܿⱨ�Ĵ�����ϴ�����
	 */
	public static final String STATUS_FINISHED = "Z0050";
	/**
	 * �ܿⱨ�������ϴ�����
	 */
	public static final String STATUS_CANCELED = "Z0051";

	// ͨ��Shell����db2
	public static String CALL_DB2 = "db2 -tvf ";
	
	//�Ƿ�����ջ��ش��롢������־����ת�� 0��ת�� 1 ת��
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
		PUBLICPARAM = sCONSTANTCODEMap.get("PUBLICPARAM");//�������ò�������һֱ�Ӳ���
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

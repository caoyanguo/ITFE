package com.cfcc.itfe.constant;

public interface StateConstant {

	// �ļ�ɾ����ʽ
	public static final String FILEDELETEBYFILE = "0";
	public static final String FILEDELETEBYPATH= "1";
	//������ʾ��Ϣ
	public static final String PRIMAYKEY = "�����¼����,�����ظ���";
	public static final String INPUTFAIL= "�����¼����";
	public static final String DELETEFAIL = "ɾ����¼����";
	public static final String MODIFYFAIL= "�޸ļ�¼����";
	public static final String ONLYONEREC = "�ò���ֻ��ά��һ����¼";
	public static final String DELETESELECT= "��ѡ��һ����¼";
	public static final String DELETESAVE = "ɾ���ɹ�";
	public static final String INPUTSAVE= "¼�뱣��ɹ�";
	public static final String CHECKVALID = "����Ϊ�յ��ֶ�¼���˿�ֵ";
	public static final String MODIFYSAVE= "�޸ı���ɹ�";
	
	
	public static final String TIPS= "��ʾ��Ϣ";
	public static final String DELETECONFIRM= "ȷ��ɾ����";
	
	// ��½״̬
	public static final String LOGINSTATE_FLAG_LOGOUT = "0";// ��ǩ��
	public static final String LOGINSTATE_FLAG_LOGIN = "1";// �ѵ�¼
	
	// ��½״̬
	public static final String LOGINSTATE_FLAG_LOGOUT_NAME = "��ǩ��";// ��ǩ��
	public static final String LOGINSTATE_FLAG_LOGIN_NAME = "�ѵ�¼";// �ѵ�¼
    
	// �û�״̬
	public static final String USERSTATUS_0 = "0";// ����
	public static final String USERSTATUS_1 = "1";// ����
	public static final String USERSTATUS_2 = "2";// ����
	public static final String USERSTATUS_3 = "3";// ����
	
	// ��־���ͽ��ձ�־
	public static final String LOG_SEND = "1";//����
	public static final String LOG_RECV = "2";// ����
	
	public static final String INCOME_SPLIT = ","; // ����ҵ��ķָ����
	public static final String PAYOUT_SPLIT = "|"; // ֧��ҵ��ķָ����
	public static final String SPACE_SPLIT = "\r\n"; // ֧��ҵ��ķָ����
	
	//���Ļ�������
	public static final String ORG_CENTER_CODE = "000000000000"; // ���Ļ�������
	public static final String STAT_CENTER_CODE = "999999999999"; // ������Ļ�������
	public static final String ORG_CENTER_LEVEL = "0"; //���Ļ�������
	public static final String ORG_STATE = "1" ;//����
	
	//�û�Ĭ������
	public static final String User_Default_PWD = "Aa+00000";
	
	//ɽ��ǩ��У��Ĭ�ϻ�������
	public static final String INFOCONNORG_DECRYPT_SD = "bbbbbbbbbbbb";
	
	//�ļ���������
	public static final String FILEKIND_DIRECT = "1"; //ֱ��֧�����
	
	public static final String FILEKIND_GRANT = "2"; //��Ȩ֧�����
	
	public static final String FILEKIND_RETUTREA_SD  = "31";  //�˿�ɽ���ӿ�
	public static final String FILEKIND_RETUTREA_FJ = "32"; //�˿⸣���ӿ�
	public static final String FILEKIND_RETUTREA_XM = "33"; //�˿����Žӿ�
	
	public static final String FILEKIND_CORRECT = "4"; //����
	
	public static final String FILEKIND_PAYOUT_SD = "51"; //ʵ��ɽ���ӿ�
	public static final String FILEKIND_PAYOUT_FJ = "52"; //ʵ�������ӿ�
	public static final String FILEKIND_PAYOUT_XM = "53"; //ʵ�����Žӿ�
	
	public static final String FILEKIND_QUANTITY = "6"; //���������ӿ�

	// ���ñ��ķ��� ǰ�÷���Ϊ0��Tips����Ϊ1����������Ϊ2,Tips�ļ�����Ϊ3
	public static final String MSG_SENDER_FLAG_0 = "0"; // ǰ�÷���Ϊ0
	public static final String MSG_SENDER_FLAG_1 = "1"; // Tips����Ϊ1
	public static final String MSG_SENDER_FLAG_2 = "2"; // ��������Ϊ2
	public static final String MSG_SENDER_FLAG_3 = "3"; // Tips�ļ�����Ϊ3
	public static final String MSG_SENDER_FLAG_9 = "9"; // ����ģʽ��Ҫ���ڲ����Ĵ�������ʹ��
	

	// �Ƿ���ö�ʱ���ͱ���
	public static final String TIMERSENDMSG = "1"; // ��ʱ���ͱ���
	
	/**
	 * ���ű�־
	 */
	public static final String CONFIRMSTATE_YES = "1"; // ������
	public static final String CONFIRMSTATE_NO = "0"; // δ����
	public static final String CONFIRMSTATE_FAIL = "2"; // ����ʧ��
	/**
	 * ����ȷ���ύ���
	 */
	public static final Integer SUBMITSTATE_SUCCESS = 1; // �ɹ�
	public static final Integer SUBMITSTATE_FAILURE = 0; // ʧ��
	public static final Integer SUBMITSTATE_DONE = 2; // ��ȷ���ύ��
	
	//�Ƿ�����½ӿ� 0--������  1--����
	public static final String IFNEWINTERFACE_FALSE = "0";
	public static final String IFNEWINTERFACE_TRUE = "1";
	
	//ɽ���˿��ֶ���Ŀ
	public static final Integer DWBK_CONTENTNUM_SD = 20;
	
	// ����˰Ʊ��Ϣ �����ڱ�־

	public final static String TRIMSIGN_FLAG_NORMAL = "0";// 0 �����(������)
	public final static String TRIMSIGN_FLAG_TRIM = "1";// 1 �����(������)
	//�������� ö��ֵ 0400
	public final static String BILL_TYPE = "0400";
	//�����������
	public final static String CachePayBank = "0";// ֧���к�
	public final static String CacheBdgSbt = "1";// Ԥ���Ŀ����
	public final static String CacheTDCrop = "2";// ���˴���
	//�����ˮ�Ƿ����˰Ʊ 0--������  1--����
	public static final String IFUPDATEVOU_FALSE = "0";
	public static final String IFUPDATEVOU_TRUE = "1";
	
	
	/**
	 * �����ձ�����Ϣ ��������
	 */	
	
	/**
	 * �����ձ�����Ϣ ��������
	 */
	public static final String REPORTTYPE_FLAG_NRBUDGETBILL = "1";// ������Ԥ�����뱨����
	public static final String REPORTTYPE_FLAG_NRDRAWBACKBILL = "2";// �������˿ⱨ����
	public static final String REPORTTYPE_FLAG_NRREMOVEBILL = "3";// �����ڵ������뱨����
	public static final String REPORTTYPE_FLAG_AMOUNTBILL = "4";// �ܶ�ֳɱ�����
	public static final String REPORTTYPE_FLAG_NRSHAREBILL = "5";// �����ڹ���ֳɱ�����
	public static final String REPORTTYPE_FLAG_TRBUDGETBILL = "6";// ������Ԥ�����뱨����
	public static final String REPORTTYPE_FLAG_TRDRAWBACKBILL = "7";// �������˿ⱨ����
	public static final String REPORTTYPE_FLAG_TRREMOVEBILL = "8";// �����ڵ������뱨����
	public static final String REPORTTYPE_FLAG_TRSHAREBILL = "9";// �����ڹ���ֳɱ�����
	
	/**
	 * ������������
	 */
	public static final String REPORTTYPE_1 = "1";// Ԥ�����뱨����
	public static final String REPORTTYPE_2=  "2";// Ԥ��֧��������
	public static final String REPORTTYPE_3 = "3";// �������뱨����
	public static final String REPORTTYPE_4=  "4";// ����ֳɱ�����
	public static final String REPORTTYPE_5 = "5";// ������汨��
	public static final String REPORTTYPE_6 = "6";// Ԥ��������˱���
	public static final String REPORTTYPE_7=  "7";// �ܶ�ֳɱ���
	public static final String REPORTTYPE_8 = "8";// �����˿ⱨ��
	public static final String REPORTTYPE_9 = "9";// Ԥ������ƾ֤
	public static final String REPORTTYPE_10 = "10";// Ԥ��֧��ƾ֤
	public static final String REPORTTYPE_11 = "11";// �����˿�ƾ֤
	public static final String REPORTTYPE_12=  "12";// �������ƾ֤
	public static final String REPORTTYPE_13=  "13";// ���а���ֱ��֧��ƾ֤
	//�����ļ��������õ��ñ�������
	
	public static final String ReportName_1 = "1";// Ԥ�����뱨����
	public static final String ReportName_2=  "2";// Ԥ��֧��������
	public static final String ReportName_3 = "3";// �������뱨����
	public static final String ReportName_4=  "4";// �ܶ�ֳɱ�����
	public static final String ReportName_c = "c";// ������汨��
	public static final String ReportName_6 = "6";// �����˿ⱨ��
	public static final String ReportName_7=  "7";// Ԥ��������˱���
	public static final String ReportName_8 = "8";// ����˰Ʊ
	public static final String ReportName_t = "t";// �˿�ƾ֤
	public static final String ReportName_z = "z";// Ԥ��֧��ƾ֤
	public static final String ReportName_g = "g";// �����˿�ƾ֤
	public static final String ReportName_0 = "0";// �ܶ�ֳɱ���
	
	//ǰ�ý��յ�TIPS��������
	public static final String RecvTips_3128_SR = "0";//3128�������������ձ����ѯ
	public static final String RecvTips_3128_KC = "1";//3128��������ձ����ѯ
	public static final String RecvTips_3129 = "2";//3129��������˰Ʊ��Ϣ��ѯ
	public static final String RecvTips_3139 = "3";//3139�������������ˮ��ϸ��ѯ
	public static final String RecvTips_3127="4";//tcbs����Ԥ��֧�������������
	//ǰ�õ���TIPS���ݵ��ֶ�����
	public static final String RecvTips_3128_SR_ColName = "�������ش���,���ջ��ش���,�����������,��������,Ԥ������,Ԥ�㼶�δ���,Ԥ���Ŀ,Ԥ���Ŀ����,���ۼƽ��,Ѯ�ۼƽ��,���ۼƽ��,���ۼƽ��,���ۼƽ��,Ͻ����־,�����ڱ�־,�ֳ����־,��������";//3128�������������ձ���
	public static final String RecvTips_3128_KC_ColName = "�������ش���,�������,��������,�ʻ�����,�ʻ�����,�ʻ�����,�������,��������,����֧��,�������";//3128��������ձ����ѯ
	public static final String RecvTips_3129_ColName = "�������ش���,��������,����ˮ��,�������,��������,���ջ��ش���,�������к�,������ˮ��,ԭ���ı��,���׽��,�����˿������к�,�����������,�ɿλ����,�������˺�,˰Ʊ����,��Ʊ����,��˰�˱��,��˰������,Ԥ������,�����ڱ�־,��ҵ����,��ҵ����,��ҵ����,Ԥ���Ŀ����,Ԥ���Ŀ����,�޽�����,˰�ִ���,˰������,Ԥ�㼶��,Ԥ�㼶������,˰������������,˰����������ֹ,������־,˰������,��������,����״̬,��ע,��ע1,��ע2,¼��Ա����,ϵͳ����ʱ��,���";//3129��������˰Ʊ��Ϣ��ѯ
	public static final String RecvTips_3139_ColName = "�������ش���,�������,����ί������,����ˮ��,ƾ֤���,����ƾ֤����,Ԥ�㼶��,Ԥ���Ŀ����,Ԥ������,���,ƾ֤��Դ,���ջ��ش���,���";//3139�������������ˮ��ϸ��ѯ
	public static final String RecvTips_3127_ColName="���ջ��ش���,�����������,��������,Ԥ������,Ԥ�㼶�δ���,Ԥ���Ŀ,Ԥ���Ŀ����,���÷����Ŀ,���ÿ�Ŀ����,���ۼƽ��,Ѯ�ۼƽ��,���ۼƽ��,���ۼƽ��,���ۼƽ��,�������ش���";//tcbs����Ԥ��֧�������������
	//�����ļ�·��������
	public static final String sr_3128 = "3128sr/";
	public static final String kc_3128 = "3128kc/";
	public static final String on_3129 = "3129/";
	public static final String in_3139 = "3139/";
	public static final String in_3127 = "3127/";
	//ǰ�ô�����Ϣ�ļ�·��
	public static final String Import_Errinfo_DIR = "c:/client/errInfo/";
	
	//ǰ�ô�����Ϣ�ļ���������
	public static final String Import_Errinfo_BackDays = "10";
	
	//3129����˰Ʊ��Ҫˢѡ����״̬
	public static final String STATE_OF_3129 = "'20','30','40','50','60'";
	
	//ǰ��ͳ��ҵ��������
	public static final String Count_Statics_Type = "'01','02','13','15','17','23','33','25','26','27','28','1106'";
	
	// Ԥ������
	public static final String BudgetType_IN = "1";// Ԥ����
	public static final String BudgetType_OUT= "2";// Ԥ����
	
	//���ܷ�ʽ 0-������ 1ɽ����ʽ���м��� 2��ɽ���ļ��ܷ�ʽ 3������ǩ����ʽ4������
	public static final String ENCRYPT_FLAG_NO = "0";
	public static final String ENCRYPT_FLAG_SDM = "1";
	public static final String ENCRYPT_FLAG_SXM = "2";
	public static final String ENCRYPT_FLAG_CA = "3";
	public static final String ENCRYPT_FLAG_ORTHER = "4";
	

	// �Ƿ�ת������
	public static final String SendFinYes = "1";//ת��
	public static final String SendFinNo= "0";// ��ת��
	
	// ʵʱ��˰�� ����״̬
	public static final String DATA_FLAG_CHECK = "01"; // ��У�����
	public static final String DATA_FLAG_NOCHECK = "00"; // δͨ��У��

	// ʵʱ��˰�� ����״̬
	public static final String CANCEL_FLAG_NOCHECK = "0"; // δ����

	// ʵʱ������Ϣ�� ����Ӧ��
	public static final String CANCELANSWER_FLAG_NOCHECK = "0"; // δӦ��
	// ʵʱ��˰�� ��������
	public static final String RECKONTYPE_FLAG_ONE = "1"; // ��ҵ����ֱ������
	public static final String RECKONTYPE_FLAG_TWO = "2"; // С��֧������
	//��ֲ��ɽ������
	public static final String DEAL_CODE_0000 = "0000"; // �����ף�У��ɹ�
	public static final String DEAL_CODE_0002 = "0002"; // ����Ҫ�ش�
	
	//ͨ���Ƿ��ж�
	public static final String COMMON_YES = "1"; // ͨ����
	public static final String COMMON_NO = "0"; // ͨ�÷�
	
	//��Կά��ģʽ
	public static final String KEY_ALL = "0"; // ȫʡͳһ
	public static final String KEY_BOOK = "1"; // ��������ͳһ
	public static final String KEY_TRECODE = "2"; // ������ά��
	public static final String KEY_BILLORG = "3"; // ����Ʊ��λ
	public static final String KEY_GENBANK = "5"; // ��������
	public static final String KEY_TAXORG = "4"; // ���ջ���
	public static final String KEY_OTHER = "6"; // ������
	//���ܷ�ʽ# 0-������ 1 ����ǩ����ɽ����ʽ1��ɽ����MD5��3DES���ܷ�ʽ 2������ǩ����ʽ3������
	//			#0-������ 1 ����ǩ����ɽ����ʽ 2��ɽ����MD5��3DES���ܷ�ʽ 3������ǩ����ʽ4������
	public static final String NO_ENCRYPT = "0";
	public static final String SD_ENCRYPT = "1";
	public static final String DES3_ENCRYPT = "2";
	public static final String SIGN_ENCRYPT = "3";
	public static final String SM3_ENCRYPT = "4";
	public static final String OTHER_ENCRYPT = "5";
	
	//����ʧ����ʾ��Ϣ
	public static final String ENCRYPT_FAIL_INFO = "���ܻ�����֤ǩ��ʧ��!";
	public static final String ENCRYPT_FIAL_INFO_NOKEY = "����У������֤ʱû���ҵ���Ӧ��Կ";  //û���ҵ���Կ����ʾ
	
	
	//��ɫ�����ʶ ,��Ҫ�����������ŷ�ʽ�� Ŀǰ�� SHANDONG��FUZHOUģʽ
	public static final String SPECIAL_AREA_SHANDONG = "SHANDONG";
	public static final String SPECIAL_AREA_FUZHOU = "FUZHOU";
	public static final String SPECIAL_AREA_SHANXI = "FUZHOU"; //ɽ�����ŷ�ʽ�͸���һ��
	public static final String SPECIAL_AREA_GUANGDONG = "GUANGDONG";  //�㶫��ʾ
	
	//���а���ֱ��֧���˻ر�ʶ
	public static final String PBC_DERICT_BACK_NO = "0";
	public static final String PBC_DERICT_BACK_YES = "1";
	
	//���������ְ���
	public static final int QUANTITY_PACKAGE_COUNT= 499;
	
	//���Ŵ����ַ���
	public static final String Check_AddWord_Wait = "���Ŵ���";
	
	//�Ƿ�ǿ�ƽ�ֹʵ���������� 0--��ֹ  1--����ֹ
	public static final String IfStopPayoutSubmit_false="0";
	public static final String IfStopPayoutSubmit_true="1";
	
	//�û�����
	public static final String User_Type_Admin = "0";  //����Ա
	public static final String User_Type_Normal ="1";  //����Ա
	public static final String User_Type_MainBiz ="2";  //ҵ������
	public static final String User_Type_Stat ="3";  //���Ա
	
	//ʵ���ʽ��˻ر�ʶ
	public static final String MSG_BACK_FLAG_YES = "1";  //ʵ���˻�
	public static final String MSG_BACK_FLAG_NO = "0";  //ʵ��
	
	//��Կ����״̬
	public static final int KEYORG_STATUS_NO = 0;  //��Կδ����
	public static final int KEYORG_STATUS_AFF= 1;  //��Կ����Ч
	public static final int KEYORG_STATUS_UPD= 2;  //��Կ�Ѹ���
	//���ı��
	public static final String CMT100= "100";  //���֧��
	public static final String CMT103= "103";  //���֧��
	public static final String PKG001= "001";  //С��֧��
	public static final String PKG007= "007";
	public static final String CMT108= "108";  //С��֧��
	public static final String TYPE999 = "999";  //����������������
	public static final String CMT100NAME= "CMT100";  //���֧��
	public static final String CMT103NAME= "CMT103";  //���֧��
	public static final String PKG001NAME= "PKG001";  //С��֧��
	public static final String PKG007NAME= "PKG007";
	public static final String CMT108NAME= "CMT108";  //С��֧��
	public static final String TYPE999NAME = "TYPE999";  //����������������
	public static final String HVPS111 = "111";  //�������111
	public static final String HVPS112 = "112";  //�������112
	public static final String BEPS121 = "121";  //����С��121
	public static final String BEPS122 = "122";  //����С��122
	public static final String HVPS111NAME = "HVPS111";  //�������111
	public static final String HVPS112NAME = "HVPS112";  //�������112
	public static final String BEPS121NAME = "BEPS121";  //����С��121
	public static final String BEPS122NAME = "BEPS122";  //����С��122
	
	public static final String CONPAY_ZEROBALANCE= "1";  //�����
	
	//�û��Ƿ��¼
	public static final String LOGIN_STATUE_1 = "1"; //�ѵ�¼
	public static final String LOGIN_STATUE_0 = "0"; //δ��¼
	
	//�Ƿ����
	public static final String IS_COLLECT_YES = "0"; //����
	public static final String IS_COLLECT_NO = "1"; //δ����
	
	//��Ȩ�ޱ��а�Ȩ�޷�Ϊ7�� 1��ҵ������� 2�������� 3����ѯ�� 4���û����� 5����־��,6 �����࣬ÿ���˶���Ҫ�Ĺ��� 0 ��������
	public static final String BIZ_OPER = "1"; //
	public static final String PARAM_MAIN = "2"; //
	public static final String BIZ_QUERY = "3"; //
	public static final String USER_MANAG = "4"; //
	public static final String BIZ_LOGS = "5"; //
	public static final String Mod_PASS = "6"; //
	public static final String ZERO_ONLY = "7"; //��ɫ0����
	public static final String ZERO_ONE = "8"; //��ɫ0��1����
	public static final String ZERO_TWO = "9"; //��ɫ0��2����
	public static final String CENTER_PRIFUNC = "0"; //
	//������
	public static final String shareBudgetLevel ="0";
	//֧����������
	public static final String REPORT_PAYOUT_TYPE_1=  "1";// һ��Ԥ��֧��������
	public static final String REPORT_PAYOUT_TYPE_2=  "2";// ʵ���ʽ�֧��������
	public static final String REPORT_PAYOUT_TYPE_3=  "3";// ����Ԥ��֧��������
	public static final String REPORT_PAYOUT_TYPE_4=  "4";// ֱ��֧���ձ���
	public static final String REPORT_PAYOUT_TYPE_5=  "5";// ��Ȩ֧���ձ���
	public static final String REPORT_PAYOUT_TYPE_6=  "6";// ֱ��֧���˻��ձ���
	public static final String REPORT_PAYOUT_TYPE_7=  "7";// ��Ȩ֧���˻��ձ���
    //�������
	public static final String Conpay_Grant= "2";// ��Ȩ֧���ձ���
	public static final String Conpay_Direct= "1";// ֱ��֧���ձ���
	//TCBS����֧���к�
	public static final String TCBSCENTERRECKBANKNO= "011100000011";// 
	
	//����Ϻ������Ƿ�¼��־ 0--����¼��1--��¼
	/** ����¼*/
	public static final String IF_MATCHBNKNAME_NO = "0";
	/** ��¼*/
	public static final String IF_MATCHBNKNAME_YES = "1";
	
	//��¼����״̬
	/** δ��¼*/
	public static final String CHECKSTATUS_0= "0";
	/** �Ѳ�¼*/
	public static final String CHECKSTATUS_1 = "1";
	/** ���ʧ��*/
	public static final String CHECKSTATUS_2 = "2";
	/** ��˳ɹ�*/
	public static final String CHECKSTATUS_3 = "3";
	/** �Ѹ���*/
	public static final String CHECKSTATUS_4 = "4";
	
	//ϵͳ��ط�Χ 0--����ϵͳ��1--���ݿ⣬2--MQ��3--Ӧ�÷�������4--CA֤�飬5--Ӧ����־
	public static final String MONITOR_SYSTEM="0";
	public static final String MONITOR_DB="1";
	public static final String MONITOR_MQ="2";
	public static final String MONITOR_SERVER="3";
	public static final String MONITOR_CA="4";
	public static final String MONITOR_LOG="5";
	
	//���������ձ���Χ 0--�������¼����� 1--�����¼�����
	public static final String SELF_AREA="0";
	public static final String ALL_AREA="1";
	
	// ��������
	public static final String REPORT_YEAR = "6"; // �걨
	public static final String REPORT_HALFYEAR = "5"; // ���걨
	public static final String REPORT_QUAR = "4"; // ����
	public static final String REPORT_MONTH = "3"; // �±�
	public static final String REPORT_TEN = "2"; // Ѯ��
	public static final String REPORT_DAY = "1"; // �ձ�
	
	// ��ʽ���� 
	public static final String REPORT_FOUR = "4"; // ����ʽ
	public static final String REPORT_THREE = "3"; // ����ʽ
	public static final String REPORT_TWO = "2"; // ����ʽ

	// ��λ��Ԫ����Ԫ����Ԫ��Ĭ����Ԫ��
//	public static final String MONEY_UNIT_1 = "1"; // Ԫ
//	public static final String MONEY_UNIT_2 = "2"; // ��Ԫ
//	public static final String MONEY_UNIT_3 = "3"; // ��Ԫ
	// ��λ
	public static final String MONEY_UNIT_5 = "5"; // Ԫ
	public static final String MONEY_UNIT_4 = "4"; // ��Ԫ
	public static final String MONEY_UNIT_3 = "3"; // ����
	public static final String MONEY_UNIT_2 = "2"; // ʮ��
	public static final String MONEY_UNIT_1 = "1"; // ǧԪ
	
	//��������
	/** �����±�*/
	public static final String RPT_TYPE_NAME_1 = "Ԥ�������±�";
	/** ֧���±�*/
	public static final String RPT_TYPE_NAME_2 = "Ԥ��֧���±�";
	/** ��֧Ѯ��*/
	public static final String RPT_TYPE_NAME_3 = "Ԥ����֧Ѯ��";
	/** ȫ�ھ�˰��*/
	public static final String RPT_TYPE_NAME_4 = "ȫ�ھ�˰��";
	/** ��ĩ��֧���ȱ�*/
	public static final String RPT_TYPE_NAME_5 = "��ĩ��֧���ȱ�";
	
	// ��Ŀ���� 's_SubjectClass'
	/** ��Ŀ���� :����*/
	public static final String S_SUBJECTCLASS_INCOME = "1"; // ����
	/** ��Ŀ���� :֧������*/
	public static final String S_SUBJECTCLASS_PAYOUT2 = "2"; // '֧������'
	/** ��Ŀ���� :֧������*/
	public static final String S_SUBJECTCLASS_PAYOUT3 = "3"; // '֧������'
	
	/**
	 * �Ƿ���ϼ�
	 */
	public static final String REPORTTYPE_0405_NO = "1";// '������ϼ�'
	public static final String REPORTTYPE_0405_YES = "2";// '����ϼ�'
	
	/**
	 * �Ƿ񺬿�ϼ�
	 */
	public static final String REPORTTYPE_0406_NO = "1";// '������ϼ�'
	public static final String REPORTTYPE_0406_YES = "2";// '����ϼ�'
	
	// ��Ŀ���� s_SubjectType
	/** ��Ŀ����:����֧��*/
	public static final String SBT_TYPE_PAYOUT_ALL = "00"; 
	/** ��Ŀ����:һ��Ԥ����*/
	public static final String SBT_TYPE_BUDGET_IN = "01"; // һ��Ԥ����
	/** ��Ŀ����:һ��Ԥ���� */
	public static final String SBT_TYPE_BUDGET_OUT = "02"; // һ��Ԥ����
	/** ��Ŀ����:һ��Ԥ�����*/
	public static final String SBT_TYPE_FUND_IN = "03"; // һ��Ԥ�����
	/** ��Ŀ����:һ��Ԥ��ծ��*/
	public static final String SBT_TYPE_DEBT_IN = "04"; // һ��Ԥ��ծ��
	/** ��Ŀ����:Ԥ�������*/
	public static final String SBT_TYPE_FUND_OUT = "05"; // Ԥ�������
	/** ��Ŀ����:Ԥ����ծ��*/
	public static final String SBT_TYPE_DEBT_OUT = "06"; // Ԥ����ծ��
	/** ��Ŀ����:�����ʱ���ӪԤ��֧��*/
	public static final String SBT_TYPE_STATE_OWNED = "08"; // �����ʱ���ӪԤ��֧��
	// ��Ŀ���� s_ClassFlag
	/** ��Ŀ���� :˰������*/
	public static final String SBT_CLASS_TAXINCOME = "01"; // ˰������
	/** ��Ŀ���� : �籣��������*/
	public static final String SBT_CLASS_FUNDINCOME = "02"; // �籣��������
	/** ��Ŀ���� : ��˰����*/
	public static final String SBT_CLASS_TAXNO = "03"; // ��˰����
	/** ��Ŀ���� : ����ת�����ձ���*/
	public static final String SBT_CLASS_CAPINCOME = "04"; // ����ת�����ձ���
	/** ��Ŀ���� :ծ������*/
	public static final String SBT_CLASS_DEBTINCOME = "05"; // ծ������
	/** ��Ŀ���� : ת��������*/
	public static final String SBT_CLASS_TRANSINCOME = "06"; // ת��������
	/** ��Ŀ���� :֧������*/
	public static final String SBT_CLASS_FUNCPAY = "07"; // ֧������
	/** ��Ŀ���� :֧������*/
	public static final String SBT_CLASS_ECOMPAY = "08"; // ֧������
	
	
	//��������type
	/** �����±�*/
	public static final String RPT_TYPE_1 = "1";
	/** ֧���±�*/
	public static final String RPT_TYPE_2 = "2";
	/** ��֧Ѯ��*/
	public static final String RPT_TYPE_3 = "3";
	/** ȫ�ھ�˰��*/
	public static final String RPT_TYPE_4 = "4";
	/** ��ĩ��֧���ȱ�*/
	public static final String RPT_TYPE_5 = "5";
	
	// ��Ŀ����
	public static final String SUBJECT_INCOME = "01"; // ˰������
	
	// �����Σ��Ϻ���1--���뼶��2--�м���4--������6--�ط�����7--���ּ���
    //�ط���=�м�+����         ���ּ���=���뼶+�м�+����
	public static final String SUBJECTLEVEL_CENTER = "1"; // ���뼶
	public static final String SUBJECTLEVEL_CITY = "2"; // �м�
	public static final String SUBJECTLEVEL_AREA = "4"; // ����
	public static final String SUBJECTLEVEL_LOCAL = "6"; // �ط���
	public static final String SUBJECTLEVEL_ANY = "7"; // ���ּ���
	
	//��Ŀ���ԣ�1-�ࡢ2-�3-�4-Ŀ��
	public static final String SUBJECTATTR_CLASS="1";
	public static final String SUBJECTATTR_PAGE="2";
	public static final String SUBJECTATTR_PROJECT="3";
	public static final String SUBJECTATTR_ITEM="4";
	
	//Ѯ�����ͣ�1-��Ѯ��2-��Ѯ��3-��Ѯ��
	public static final String TENRPT_TOP="x1";
	public static final String TENRPT_MIDDLE="x2";
	public static final String TENRPT_BOTTOM="x3";
	
	//�������ͣ�1-��һ���ȡ�2-�ڶ����ȡ�3-�������ȡ�4-���ļ��ȣ�
	public static final String QUARTERRPT_FIRST="j1";
	public static final String QUARTERRPT_SECOND="j2";
	public static final String QUARTERRPT_THREE="j3";
	public static final String QUARTERRPT_FOUR="j4";
	
	//���걨���ͣ�1-�ϰ��ꡢ2-�°��꣩
	public static final String HALFYEARRPT_TOP="n1";
	public static final String HALFYEARRPT_BOTTOM="n2";
	
	//������־��0-�ǵ�����1-������2-�����֣�
	public static final String MOVEFLAG_NOMOVE="0";
	public static final String MOVEFLAG_MOVE="1";
	public static final String MOVEFLAG_NORMAL="2";
	
	/**
	 * ƾ֤�ص����ջ�������(����)
	 */
	public static final String ORGCODE_FIN="011";
	/**
	 * ƾ֤�ص����ͻ�������(����)
	 */
	public static final String ORGCODE_RH="001";
	
	//����TBS�ļ�
	public static final String TBS_SBZJ="0";//ʵ���ʽ�
	public static final String TBS_ZJZFED="1";//ֱ��֧�����
	public static final String TBS_SQZFED="2";//��Ȩ֧�����
	public static final String TBS_JZZFHKSQ="3";//����֧����������
	
	//ɽ��һ��ͨ���������ļ�ftp״̬�б�
	public static final String FTPFILESTATE_DOWNLOAD="1";//��ftp�����ļ���ϵͳ
	public static final String FTPFILESTATE_ADDLOAD="2";//�Ѿ�����
	public static final String FTPFILESTATE_RETURN="3";//�Ѿ���ִ
	public static final String FTPFILESTATE_SUMMARY="4";//�����ļ�
	public static final String FTPFILESTATE_UNKNOWN="5";//δ֪�ļ�
	
	/**����TBS��ִ�ļ�����**/
	public static final String TBS_FILESLIP_YSZC = "YSZC"; //ʵ���ʽ�
	public static final String TBS_FILESLIP_ZJED = "ZJED"; //ֱ�Ӷ��
	public static final String TBS_FILESLIP_SQED = "SQED"; //��Ȩ���
	public static final String TBS_FILESLIP_ZFQS = "ZFQS"; //����֧������
	public static final String TBS_FILESLIP_ZFTH = "ZFTH"; //����֧���˻�
	public static final String TBS_FILESLIP_SRTK = "SRTK"; //�����˿�
	
	/**
	 * ƾ֤����״̬  0-���� 
	 */
	public static final String VOUCHE_PROSTATE_NORMAL="0";
	/**
	 * ƾ֤����״̬  1-�˻�
	 */
	public static final String VOUCHE_PROSTATE_RETURN="1";
	
	
	/**
	 * �Ƿ�ת��TIPS  0-ת��[�ط�����]
	 */
	public static final String MSG_RELAY_TIPS="0";
	/**
	 * �Ƿ�ת��TIPS  1-��ת��[��ֽ��]
	 */
	public static final String MSG_NORELAY_TIPS="1";
	
	
	/**
	 * ���˰Դ֧��ͳ�Ʊ��ѯ��ʽ  0-���˰Դ��ѯ
	 */
	public static final String LARGE_TAX_QUERY="0";
	/**
	 * ���˰Դ֧��ͳ�Ʊ��ѯ��ʽ  1-���֧����ѯ
	 */
	public static final String LARGE_PAY_QUERY="1";
	
	
	/**
	 *  TCBS�����౨���ļ�����  0-�����ձ�
	 */
	public static final String REPORT_STYLE_INCOME="0";
	/**
	 * TCBS�����౨���ļ�����  1-����ձ�
	 */
	public static final String REPORT_STYLE_STOCK="1";
	
	/**
	 * ������Զ����͵ı������� ��������+����
	 */
	public static final String IncomeReport_11 = "11";// ���뼶Ԥ�������ձ���
	public static final String IncomeReport_12=  "12";// ʡ��Ԥ�������ձ���
	public static final String IncomeReport_13 = "13";// ���м�Ԥ�������ձ���
	public static final String IncomeReport_14=  "14";// ���ؼ�Ԥ�������ձ���
	public static final String IncomeReport_15 = "15";// ����Ԥ�������ձ���
	public static final String IncomeReport_DF = "DF";// �ط��������ձ���
	public static final String PayOutReport_20= "20";//  һ��Ԥ��֧���ձ���
	public static final String FinReport=  "90";//��������ձ���
	
	
	/**
	 * ҵ�����ͱ���(�Ϻ���չ)  1-���� 
	 */
	public static final String BIZTYPE_CODE_SINGLE="1";
	/**
	 * ҵ�����ͱ���(�Ϻ���չ)  3-����
	 */
	public static final String BIZTYPE_CODE_SALARY="3";
	/**
	 * ҵ�����ͱ���(�Ϻ���չ)  4-����
	 */
	public static final String BIZTYPE_CODE_BATCH="4";
	
	/**
	 * �����˿�1
	 * ȫ���˿�2
	 * ��ȷ���˿�5
	 */
	public static String REFUNDTYPE_1 = "1";
	public static String REFUNDTYPE_2 = "2";
	public static String REFUNDTYPE_5 = "5";
	
	/**
	 * ��λ��� ȫ����־  1-ȫ��   2-����
	 */
	public static final String ALLFLAG_FULL = "1";
	public static final String ALLFLAG_INCREMENT = "2";
	
	
	/**
	 * ֧����ʽ���� 11-ֱ��֧��  12-��Ȩ֧��  91-ʵ��
	 */
	public static final String DIRECT_PAY_CODE = "11";
	public static final String GRANT_PAY_CODE = "12";
	public static final String PAYOUT_PAY_CODE = "91";	
	
	/**
	 * �˿�����
	 * 1 �����˿�
	 * 2 ��ƾ֤
	 */
	public static final String TKLX_1 = "1";
	public static final String TKLX_2 = "2";
	
	/**
	 * ���ն�����д������������Դ��
	 */
	public static final String ACCOUNT_CHECK = "3508,3509,3510,3511,3512,3513";
	/**
	 * 
	 * ֱ��֧���˿�����
	 */
	public static final String NOREFUNDTYPE = "5";
	
	/**
	 * 
	 *��λ
	 */
	public static final String MoneyUnit_YUAN = "1";
	public static final String MoneyUnit_WAN = "2";
	public static final String MoneyUnit_BW = "3";
	public static final String MoneyUnit_QW = "4";
	public static final String MoneyUnit_YI = "5";
	/**
	 * һ��Ԥ���ڡ�Ԥ���ڻ���ת�������������Ŀ����
	 */
	public static final String COMMON_BUGGET_IN = "-";
	public static final String BUGGET_IN_FUND = "--";
	public static final String STATE_OWNED = "---";
	public static final String MOVE_INCOME = "----";

	/** ���ݱȶԳɹ�״̬ */
	public static final String MERGE_VALIDATE_SUCCESS = "1"; // �ȶԳɹ�
	public static final String MERGE_VALIDATE_FAILURE = "0"; // �ȶ�ʧ��
	public static final String MERGE_VALIDATE_NOTCOMPARE = "2"; // δ�ض�
	// ����δ֪���ȵķ��ؽ��
	public static final int UNKNOWPROCESSDIALOG_RESULT_YES = 0;// �ɹ�
	public static final int UNKNOWPROCESSDIALOG_RESULT_NO = -1;// ʧ��
	
	/**
	 *���ݱ����ļ���Ŀ¼���� 12�����������+yyyyMMddHHmmss
	 */
	public final static int DATA_BACKUP_FILEDIRECTORYNAME_SIZE = 26;
	// db2���ݿ⵼���modifiedBy�Ӿ�
	// �ַ��ָ������ȼ���
	public final static String DATA_IMPORT_MODIFIEDBY_DELPRIORITYCHAR = "modified by delprioritychar";
	public final static String DATA_IMPORT_MODIFIEDBY_DELPRIORITYCHARANDLOB = "modified by lobsinfile delprioritychar";
	
	// db2���ݿ⵼��ļ��ַ�ʽ
	public final static String DATA_IMPORT_MODE_INSERT = "insert";
	public final static String DATA_IMPORT_MODE_INSERT_UPDATE = "insert_update";
	public final static String DATA_IMPORT_MODE_REPLACE = "replace";
	public final static String DATA_IMPORT_MODE_REPLACE_CREATE = "repalce_create";
	public final static String DATA_IMPORT_MODE_CREATE = "create";
}
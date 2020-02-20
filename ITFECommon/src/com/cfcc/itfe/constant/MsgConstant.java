package com.cfcc.itfe.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.cfcc.itfe.config.ITFECommonConstant;

@SuppressWarnings("unchecked")
public class MsgConstant {
	
	
	/**
	 * ����֧����ȶ��˵�
	 */
	public final static String MSG_NO_JZZF = "JZZF";
	/**
	 * ���ݱ�����������
	 */
	public final static String PLACE_GZ = "_GZ";
	
	/**
	 * DB IMPORT��ʽ�������ʱ��ʾ�û�������Ϣ����
	 */
	public final static int DB_IMPORT_ERROR_NUM = 50;
	
	/**
	 * TIPSÿ�����õ������
	 */
	public final static int TIPS_MAX_OF_PACK = 1000;
	
	/**
	 * ����CALLSHELL��ʽ���صĽ������ת��Ϊ�ַ��������ֵ(��λ(K))
	 */
	public final static int MAX_CALLSHELL_RS = 100;
	
	/****************************** ���ջ��ش��� **********************************/
	/**
	 * ��˰����
	 */
	public static final String MSG_TAXORG_NATION_CLASS = "111111111111";

	/**
	 * ��˰����
	 */
	public static final String MSG_TAXORG_PLACE_CLASS = "222222222222";

	/**
	 * ���ش���
	 */
	public static final String MSG_TAXORG_CUSTOM_CLASS = "333333333333";

	/**
	 * ��������
	 */
	public static final String MSG_TAXORG_FINANCE_CLASS = "444444444444";

	/**
	 * ��������
	 */
	public static final String MSG_TAXORG_OTHER_CLASS = "555555555555";

	/**
	 * �������ջ��ش���
	 */
	public static final String MSG_TAXORG_SHARE_CLASS = "000000000000";
	
	/**
	 * �������ջ��ش���(����TBS����ͳ��)
	 */
	public static final String MSG_TAXORG_SHARE_CLASS_TBS = "999999999999";
	
	/****************************** ���ջ������� **********************************/
	/**
	 * ��˰
	 */
	public static final String MSG_TAXORG_NATION_PROP = "1";

	/**
	 * ��˰
	 */
	public static final String MSG_TAXORG_PLACE_PROP = "2";

	/**
	 * ����
	 */
	public static final String MSG_TAXORG_CUSTOM_PROP = "3";

	/**
	 * ����
	 */
	public static final String MSG_TAXORG_FINANCE_PROP = "4";
	
	/**
	 * ����
	 */
	public static final String MSG_TAXORG_OTHER_PROP = "5";
	
	/**
	 * ����
	 */
	public static final String MSG_TAXORG_SHARE_PROP = "0";
	
	/********************************   SPRING CONFIG   ********************************/ 
	/**
	 * ���ķ���������ͷ
	 */
	public static final String SPRING_MSG_SERVER = "MSG_";
	
	/**
	 * ��ʱ������ñ���ͷ
	 */
	public static final String SPRING_MSG_PROC_SERVER = "PROC_";
	
	/**
	 * ����������
	 */
	public static final String SPRING_EXP_REPORT = "REPORT_";
	/**
	 * SERVICE����������ͷ
	 */
	public static final String SPRING_SERVICE_SERVER = "SERVICE_";
	/**
	 * �ļ�������������ͷ
	 */
	public static final String SPRING_FILEPRA_SERVER = "TIPS_";
	/**
	 * ��������ͳһ���нӿڴ�������ͷ
	 */
	public static final String SPRING_SXXMLPRA_SERVER = "VOUCHER_SX_";
	/********************************   SPRING CONFIG   ********************************/

	/********************************   MSG HEAD INFO   ********************************/ 
	/**
	 * ITFE���Ľڵ�
	 */
	public static final String MSG_HEAD_SRC = "202057100007";

	/**
	 * TIPS���Ľڵ�
	 */
	public static final String MSG_HEAD_DES = "100000000000";

	/**
	 * Ӧ������
	 */
	public static final String MSG_HEAD_APP = "TIPS";

	/**
	 * ����VER
	 */
	public static final String MSG_HEAD_VER = "1.0";
	
	/**
	 * TIPS�ڵ����
	 */
	public static final String TIPSNODE_GUANGXI = ITFECommonConstant.SRC_NODE;//ԭ���ǹ����ڵ�ţ��޸�Ϊȫ��

	/********************************   MSG HEAD INFO   ********************************/ 

	/********************************   MSG        NO   ********************************/
	/**
	 * ��Ч���ĵı��ı��
	 */
	public static final String MSG_NO_0000 = "0000";
	/**
	 * 1000 ʵ���ʽ�ҵ��(����)
	 */
	public static final String MSG_NO_1000 = "1000";
	/**
	 * ������˰
	 */
	public static final String MSG_NO_1102 = "1102";
	/**
	 * 1103 �Խɺ���
	 */
	public static final String MSG_NO_1103 = "1103";
	/**
	 * 1104 �˿�����
	 */
	public static final String MSG_NO_1104 = "1104";
	/**
	 * 1105 ��������
	 */
	public static final String MSG_NO_1105 = "1105";
	/**
	 * 1106 ��ֵ�����
	 */
	public static final String MSG_NO_1106 = "1106";
	/**
	 * 7211 ˰Ʊ����(����ҵ��)
	 */
	public static final String MSG_NO_7211 = "7211";
	/**
	 * ���ջ������������ˮ
	 */
	public static final String MSG_NO_1024 = "1024";
	
	/**
	 * 2000 �ʽ������ִ(����)
	 */
	public static final String MSG_NO_2000 = "2000";
	/**
	 * 3123�������ջ����˿�˶�֪ͨ
	 */
	public static final String MSG_NO_3123 = "3123";
	/**
	 * 3124�������ջ��ظ����˶�֪ͨ
	 */
	public static final String MSG_NO_3124 = "3124";
	/**
	 * 3128 �ձ������ִ
	 */
	public static final String MSG_NO_3128 = "3128";
	/**
	 * 3131 ʵ���ʽ��ִ
	 */
	public static final String MSG_NO_3131 = "3131";
	/**
	 * 3133 ֱ��֧����Ȼ�ִ
	 */
	public static final String MSG_NO_3133 = "3133";
	/**
	 * 3134 ��Ȩ֧����Ȼ�ִ
	 */
	public static final String MSG_NO_3134 = "3134";
	/**
	 * 3136 ���������Կ����ִ
	 */
	public static final String MSG_NO_3136 = "3136";
	/**
	 * 3200 TIPS��������籣����ҵ����֧����Ϣ���˶�֪ͨ
	 */
	public static final String MSG_NO_3200 = "3200";
	/**
	 * 3201 TIPS��������籣����ҵ���д�������Ϣ���˶�֪ͨ
	 */
	public static final String MSG_NO_3201 = "3201";
	/**
	 * Tips����������3122�����ջ��ء��籣˰���ѣ�Ʊ��ϸ�˶�֪ͨ����
	 */
	public static final String MSG_NO_3122 = "3122";
	/**
	 * ���ջ������������ˮ��Ϣ(3126)
	 */
	public static final String MSG_NO_3126 = "3126";
	/**
	 * ���ջ������뱨����Ϣ(3127)
	 */
	public static final String MSG_NO_3127 = "3127";
	/**
	 * 3190 ��TCBS����������֪ͨ(3190)
	 */
	public static final String MSG_NO_3190 = "3190";
	/**
	 * 3139 �����������ˮ��ϸ
	 */
	public static final String MSG_NO_3139 = "3139";
	
	/**
	 * 1025�����ջ������뱨��
	 */
	public static final String MSG_NO_1025 = "1025";
	/**
	 * 5001 ���������ձ�����
	 */
	public static final String MSG_NO_5001 = "5001";
	/**
	 * 5002 �������������ˮ
	 */
	public static final String MSG_NO_5002 = "5002";
	/**
	 * 5003 �����������˰Ʊ��Ϣ
	 */
	public static final String MSG_NO_5003 = "5003";
	/**
	 * 5101 ʵ���ʽ�ҵ��
	 */
	public static final String MSG_NO_5101 = "5101";
	/**
	 * 5101 ʵ���ʽ��˻�
	 */
	public static final String MSG_NO_51011 = "51011";
	/**
	 * 5102 ֱ��֧�����
	 */
	public static final String MSG_NO_5102 = "5102";
	/**
	 * 5103 ��Ȩ֧�����
	 */
	public static final String MSG_NO_5103 = "5103";
	/**
	 * 5104 ���а���ֱ��֧��
	 */
	public static final String MSG_NO_5104 = "5104";
	/**
	 * 5112 ���������Կ�������
	 */
	public static final String MSG_NO_5112 = "5112";
	
	/**
	 * 8000 ���ʱ���(����)
	 */
	public static final String MSG_NO_8000 = "8000";
	/**
	 * 9000 ���뱨��(����)
	 */
	public static final String MSG_NO_9000 = "9000";
	/**
	 * 9003 ����״̬��ѯ����
	 */
	public static final String MSG_NO_9003 = "9003";
	/**
	 * 9004 ����״̬��ѯӦ��
	 */
	public static final String MSG_NO_9004 = "9004";
	/**
	 * 9005 �����Բ��Ա���
	 */
	public static final String MSG_NO_9005 = "9005";
	/**
	 * 9006 ��½����
	 */
	public static final String MSG_NO_9006 = "9006";
	/**
	 * 9007 ��½���Ļ�ִ
	 */
	public static final String MSG_NO_9007 = "9007";
	/**
	 * 9008 ǩ�˱���
	 */
	public static final String MSG_NO_9008 = "9008";
	/**
	 * 9009 ǩ�˻�ִ����
	 */
	public static final String MSG_NO_9009 = "9009";
	/**
	 * 9105 ���ɸ�ʽ����
	 */
	public static final String MSG_NO_9105 = "9105";
	/**
	 * 9110 ͨ��Ӧ����
	 */
	public static final String MSG_NO_9110 = "9110";
	/**
	 * 9111 ����ϸ�ط�����
	 */
	public static final String MSG_NO_9111 = "9111";
	/**
	 * 9120 ͨ��Ӧ��
	 */
	public static final String MSG_NO_9120 = "9120";
	/**
	 * 9121 ͨ��ȷ��Ӧ��
	 */
	public static final String MSG_NO_9121 = "9121";
	/**
	 * 9122 ͨ�ô�����֪ͨ
	 */
	public static final String MSG_NO_9122 = "9122";
	
	/**
	 * 3129 ����˰Ʊ
	 */
	public static final String MSG_NO_3129 = "3129";
	
	/**
	 * 3143���������ִ
	 */
	public static final String MSG_NO_3143 = "3143";
	
	/**
	 * 3144���������˻ػ�ִ
	 */
	public static final String MSG_NO_3144 = "3144";
	
	/**
	 * 3146�����˻�
	 */
	public static final String MSG_NO_3146 = "3146";
	
	/**
	 * 2201��������
	 */
	public static final String MSG_NO_2201 = "2201";
	
	/**
	 * 2201�����˻�
	 */
	public static final String MSG_NO_2202 = "2202";
	
	/** 3452��������Ʊ���޸�ǰ ���ֳ���
	 */
	public static final String MSG_NO_3452 = "3452";
	
	/** 3403��������Ʊ���޸ĺ�
	 */
	public static final String MSG_NO_3403 = "3403";
	
	
	/**
	 * ���Ӳ������� (3000)
	 */
	public static final String MSG_TBS_NO_3000 = "3000";
	/**
	 * ͨ�û�ִ���� (3001��
	 */
	public static final String MSG_TBS_NO_3001 = "3001";
	/**
	 * �˿�֪ͨ���ģ�2001��
	 */
	public static final String MSG_TBS_NO_2001 = "2001";
	/**
	 * ���˽���ӿ� (1002)
	 */
	public static final String MSG_TBS_NO_1002 = "1002";
	/**
	 * ���˱��Ľӿ� (2002)
	 */
	public static final String MSG_TBS_NO_2002 = "2002";
	/**
	 * �ʽ������ִ��2000��
	 */
	public static final String MSG_TBS_NO_2000 = "2000";
	/**
	 * �ʽ𲦸��ӿڣ�1000��
	 */
	public static final String MSG_TBS_NO_1000 = "1000";
	
	
	
	
	
	
	
	/**
	 * �����ط�
	 */
	public static final String MSG_NO_RESEND = "RESEND";
	/**
	 * �ļ�����
	 */
	public static final String MSG_FILE = "FILE";
	
	/********************************   MSG        NO   ********************************/
	
	/****************************ֱ��֧��/��Ȩ֧������ļ�����****************************/
	
	/**
	 * �ļ�ͷ��ʼ��־
	 */
	public static final String MSG_FLAG_HEAD_START = "<pub";
	
	/**
	 * �ļ�ͷ������־
	 */
	public static final String MSG_FLAG_HEAD_END = "</pub>";
	
	/**
	 * �ļ����忪ʼ��־
	 */
	public static final String MSG_FLAG_MAIN_START = "<main";
	
	/**
	 * �ļ����������־
	 */
	public static final String MSG_FLAG_MAIN_END = "</main>";
	
	/**
	 * �ļ���ϸ��ʼ��־
	 */
	public static final String MSG_FLAG_DETAIL_START = "<detail";
	
	/**
	 * �ļ���ϸ������־
	 */
	public static final String MSG_FLAG_DETAIL_END = "</detail>";
	
	/**
	 * ������
	 */
	public static final String MSG_FLAG_CZJNAME = "czjname";
	
	/**
	 * ���յ�λ
	 */
	public static final String MSG_FLAG_RECEIVER = "receiver";
	
	/**
	 * �ļ����ݱ�־ - ͷ
	 */
	public static final String MSG_FLAG_CONTENT_START = "<value>";
	
	/**
	 * �ļ����ݱ�־ - β
	 */
	public static final String MSG_FLAG_CONTENT_END = "</value>";
	
	/**
	 * �����ֱ�־ - ͷ
	 */
	public static final String MSG_FLAG_CZJNAME_START = "<czjname>";
	
	/**
	 * �����ֱ�־ - β
	 */
	public static final String MSG_FLAG_CZJNAME_END = "</czjname>";
	
	/**
	 * ���յ�λ��־ - ͷ
	 */
	public static final String MSG_FLAG_RECEIVER_START = "<receiver>";

	/**
	 * ���յ�λ��־ - β
	 */
	public static final String MSG_FLAG_RECEIVER_END = "</receiver>";
	
	/****************************ֱ��֧��/��Ȩ֧������ļ�����****************************/
	
	/**
	 * Ԥ������ 1 Ԥ����
	 */
	public final static String BDG_KIND_IN = "1";
	/**
	 * Ԥ������ 2 Ԥ����
	 */
	public final static String BDG_KIND_OUT = "2";
	/**
	 * Ԥ������ 6 Ԥ�����ݴ�
	 */
	public final static String BDG_KIND_IN_CASH = "6";

	/**
	 * �����ڱ�־ 0 �����(������)
	 */
	public final static String TIME_FLAG_NORMAL = "0";
	/**
	 * �����ڱ�־ 1 �����(������)
	 */
	public final static String TIME_FLAG_TRIM = "1";
	
	/**
	 * ������� 0 ���а���ֱ��֧��
	 */
	public final static String AMT_KIND_PEOPLE = "0";
	/**
	 * ������� 1 ��ҵ���а���֧��
	 */
	public final static String AMT_KIND_BANK = "1";
	
	/**
	 * �˻����� 1 �����	
	 */
	public final static String ACCT_PROP_ZERO = "1";
	/**
	 * �˻����� 2С���ֽ�	
	 */
	public final static String ACCT_PROP_SMALL = "2";

	/**
	 * ֧��ƾ֤����   0	��ֽƾ֤
	 */
	public final static String PAYOUT_VOU_TYPE_PAPER_NO = "0";
	/**
	 * ֧��ƾ֤����   1	��ֽƾ֤
	 */
	public final static String PAYOUT_VOU_TYPE_PAPER_YES = "1";
	
	/**
	 * ƾ֤����(ͬ������ӿ�)
	 * 1ʵ��, 2�˿�, 3���л���, 4����
	 */
	public final static String SAME_BANK_PAYOUT_VOU_TYPE1 = "1" ;
	public final static String SAME_BANK_PAYOUT_VOU_TYPE2 = "2" ;
	public final static String SAME_BANK_PAYOUT_VOU_TYPE3 = "3" ;

	/**
	 * ˰������ 1 ��������
	 */
	public final static String CORP_TYPE_DANGQI = "1";
	/**
	 * ˰������ 2 ����Ƿ˰
	 */
	public final static String CORP_TYPE_BUJIAO = "2";
	/**
	 * ˰������ 3 �������(ί�д��������ۡ�����)
	 */
	public final static String CORP_TYPE_DAIJIAO = "3";
	/**
	 * ˰������ 4 ��鲹˰
	 */
	public final static String CORP_TYPE_BUSHUI = "4";

	/**
	 * Ԥ�㼶�� 0 ����
	 */
	public final static String BUDGET_LEVEL_SHARE = "0";
	/**
	 * Ԥ�㼶�� 1 ����
	 */
	public final static String BUDGET_LEVEL_CENTER = "1";
	/**
	 * Ԥ�㼶�� 2 ʡ
	 */
	public final static String BUDGET_LEVEL_PROVINCE = "2";
	/**
	 * Ԥ�㼶�� 3 ��
	 */
	public final static String BUDGET_LEVEL_DISTRICT = "3";
	/**
	 * Ԥ�㼶�� 4 ��
	 */
	public final static String BUDGET_LEVEL_PREFECTURE = "4";
	/**
	 * Ԥ�㼶�� 5 ��
	 */
	public final static String BUDGET_LEVEL_VILLAGE = "5";
	/**
	 * Ԥ�㼶�� 6 �ط�
	 */
	public final static String BUDGET_LEVEL_PLACE = "6";
	
	
	/**
	 * ȫ�� 
	 */
	public final static String BUDGET_LEVEL_ALL = "A";
	/**
	 * �������� 1:��Ҫ����
	 */
	public final static String REPORT_DAY_DOWN_YES = "1";
	/**
	 * �������� 0:��������
	 */
	public final static String REPORT_DAY_DOWN_NO = "0";
	
	/**
	 * Ͻ����־ 1 ȫϽ
	 */
	public static final String RULE_SIGN_ALL = "1";
	/**
	 * Ͻ����־ 0 ����
	 */ 
	public static final String RULE_SIGN_SELF = "0";
	
	/**
	 * Ͻ����־ 3 ȫϽ�Ǳ���
	 */
	public static final String RULE_SIGN_ALLNOTSELF = "3";
	
	/**
	 * ���Ĵ���״̬ �ɹ�
	 */ 
	public static final String MSG_STATE = "90001";
	
	/**
	 * �������� - ���� income
	 */
	public static final String REPORT_TYPE_INCOME = "income" ;
	/**
	 * �������� - �˿� drawbk
	 */
	public static final String REPORT_TYPE_DRAWBK = "drawbk" ;
	/**
	 * �������� - ��� stock
	 */
	public static final String REPORT_TYPE_STOCK = "stock" ;
	
	/**
	 * �������� - 0	����
	 */
	public static final String MSG_ORG_TYPE_FINA = "0" ;
	/**
	 * �������� - 1	�籣
	 */
	public static final String MSG_ORG_TYPE_SOCIAL = "1" ;
	/**
	 * �������� - 2	����	ָTCBS
	 */
	public static final String MSG_ORG_TYPE_OTHRE = "2" ;
	/**
	 * �������� - 3	��ҵ����
	 */
	public static final String MSG_ORG_TYPE_BANK = "3" ;
	
	/**
	 * ������Ϣ�������ڲ�ѯ�׷���־ʹ��
	 */
	public static final String OTHER_INFO_MSG = "99" ;
	
	/**
	 * ҵ��ģʽ 0	TIPS����˰Ʊ 
	 */
	public static final String MSG_BIZ_MODEL_TIPS = "0";
	/**
	 * ҵ��ģʽ 1	TIPS�Խɺ���˰Ʊ 
	 */
	public static final String MSG_BIZ_MODEL_SELF = "1";
	/**
	 * ҵ��ģʽ 2	�ط���������˰Ʊ 
	 */
	public static final String MSG_BIZ_MODEL_NET = "2";

	/**
	 * ������Դ��־ 0	TBS�ӿ� 
	 */
	public static final String MSG_SOURCE_TBS = "0";
	
	/**
	 * ������Դ��־ 1	��˰�ӿ� 
	 */
	public static final String MSG_SOURCE_NATION = "1";
	/**
	 * ������Դ��־ 2	��˰�ӿ� 
	 */
	public static final String MSG_SOURCE_PLACE = "2";
	/**
	 * ������Դ��־ 3    TIPS�ӿ�
	 */
	public static final String MSG_SOURCE_TIPS = "3";
	/**
	 * ������Դ��־  4 ɽ���ط������ӿ�
	 */
	public static final String MSG_SOURCE_SHANDONG = "4";
	/**
	 * ������־ 0 �ǵ���
	 */
	public static final String MOVE_FUND_SIGN_NO = "0";
	/**
	 * ������־ 1 ����
	 */
	public static final String MOVE_FUND_SIGN_YES = "1";
	/**
	 * ������־ 2 ת��
	 */
	public static final String MOVE_FUND_SIGN_SHIFT = "2";
	
	/**
	 * ¼���־ 0 ����¼��
	 */
	public static final String INPUT_SIGN_NO = "0";
	/**
	 * ¼���־  1 ��¼��
	 */
	public static final String INPUT_SIGN_YES = "1";
	
	/**
	 * Ԥ������ 1 Ԥ����
	 */
	public static final String BUDGET_TYPE_IN_BDG = "1";
	/**
	 * Ԥ������ 2 Ԥ����
	 */
	public static final String BUDGET_TYPE_OUT_BDG = "2";
	/**
	 * Ԥ������ 3 ����
	 */
	public static final String BUDGET_TYPE_SHARE = "3";

	/**
	 * Ԥ���Ŀ���� 1 ����̶�
	 */
	public static final String BUDGET_SBT_ATTRIB_CENTER_FIX = "1";
	/**
	 * Ԥ���Ŀ���� 2 �ط��̶�
	 */
	public static final String BUDGET_SBT_ATTRIB_PLACE_FIX = "2";
	/**
	 * Ԥ���Ŀ���� 3 ����
	 */
	public static final String BUDGET_SBT_ATTRIB_SHARE = "3";
	/**
	 * Ԥ���Ŀ���� 4 ����
	 */
	public static final String BUDGET_SBT_ATTRIB_COMMON = "4";

	/**
	 * 9117 ֧���˶԰��ط�����
	 */
	public static final String MSG_NO_9117 = "9117";
	/**
	 * 9113 ����˶԰���Ϣ�ط�����
	 */
	public static final String MSG_NO_9113 = "9113";
	/**
	 * 9116 ��������˶԰��ط�����
	 */
	public static final String MSG_NO_9116= "9116";
	/**
	 * ��������ֱ��֧�����
	 */
	public static final String ZHIJIE_DAORU = "5102";
	/**
	 * ����������Ȩ֧�����
	 */
	public static final String SHOUQUAN_DAORU = "5103";
	/**
	 * ����������������
	 */
	public static final String PILIANG_DAORU = "5112";
	/**
	 * �����������
	 */
	public static final String GENGZHENG_DAORU = "1105";
	
	/**
	 * �������뻮������
	 */
	public static final String APPLYPAY_DAORU = "2201";
	
	/**
	 * �������뻮�������˻�
	 */
	public static final String APPLYPAY_BACK_DAORU = "2202";
	
	/**
	 * ���������˿�(ɽ��)
	 */
	public static final String TUIKU_SHANDONG_DAORU = "1104_SD";
	/**
	 * ���������˿�(����)
	 */
	public static final String TUIKU_FUJIAN_DAORU = "1104_FJ";
	/**
	 * ���������˿�(����)
	 */
	public static final String TUIKU_XIAMEN_DAORU = "1104_XM";
	/**
	 * ��������ʵ���ʽ�(ɽ��)
	 */
	public static final String SHIBO_SHANDONG_DAORU = "5101_SD";
	/**
	 * ��������ʵ���ʽ�(����)
	 */
	public static final String SHIBO_FUJIAN_DAORU = "5101_FJ";
	/**
	 * ��������ʵ���ʽ�(����)
	 */
	public static final String SHIBO_XIAMEN_DAORU = "5101_XM";
	/**
	 * �����������а���ֱ��֧��
	 */
	public static final String PBC_DIRECT_IMPORT = "5104";
	
	/**
	 * ����������ֵ�
	 */
	public static final String TAX_FREE_DAORU = "1106";
	/**
	 *  ҵ������ - Ԥ��֧���ձ���(3127)
	 */
	public static final String TAXORG_PAYOUT = "3127";
	/**
	 * ����ˮ��MAX_VALUE
	 */
	public static final int SEQUENCE_MAX_DEF_VALUE=99999999;
	
	/**
	 * ��־����
	 */
	public static final String LOG_ADDWORD_RECV="�������������(��TIPS)";
	public static final String LOG_ADDWORD_SEND="ת�����յ����������";
	public static final String LOG_ADDWORD_RECV_TIPS="����TIPS����";
	public static final String LOG_ADDWORD_RUN_NOPAPER="�������������(��������ֽ����ת��)";
	public static final String LOG_ADDWORD_RUN_UNKNOW="�������������(δ֪���������ת��)";
	public static final String ITFE_REQ="ǰ������";
	public static final String ITFE_AUTO_SEND="ǰ�ö�ʱ����";
	public static final String ITFE_SEND_9120="������������ģ�ǰ�ô����쳣������֪ͨ����";
	/**
	 * ת����־
	 */
	public static final String ITFE_SEND="0";
	public static final String OTHER_SEND="1";
	
	/**
	 * ֧����ʽ
	 */
	public static final String directPay="0"; //ֱ��֧��
	public static final String grantPay="1"; //��Ȩ֧��
	
	/**
	 * ���Ĵ���״̬ �޷�ʶ��ı���
	 */
	public static final String MSG_STATE_FAIL = "92001";
	/**
	 * ���Ĵ���״̬ �޷�ʶ��ı���
	 */
	public static final String MSG_STATE_FAIL_ADDWORD = "ITFE�޷�ʶ��ı���";
	
	/**
	 * 2201��2202�ط�������ʶ
	 */
	public static final String CFCCHL="CFCCHL";
	
	/**
	 * �������Զ�̶���
	 */
	public static final String QUEUE_BATCH = ITFECommonConstant.MQTOMOFBATCH;
	public static final String QUEUE_ONLINE = ITFECommonConstant.MQTOMOFONLINE;
	public static final String QUEUE_BATCHCITY = ITFECommonConstant.MQTOMOFBATCHCITY;
	public static final String QUEUE_ONLINECITY = ITFECommonConstant.MQTOMOFONLINECITY;
	/**
	 * �������б����б�
	 */
	public static final Set BATCH_MSG_NO = Collections.unmodifiableSet(new HashSet(
	        Arrays.asList(new String[]{MSG_NO_1102, MSG_NO_1104, MSG_NO_1105})));
	
	/**
	 * ʵ������ƾ֤5207
	 */
	public static final String VOUCHER_NO_5207="5207";
	/**
	 * ʵ���������ƾ֤5287
	 */
	public static final String VOUCHER_NO_5287="5287";
	/**
	 * ����֧�����㻮���嵥5551
	 */
	public static final String VOUCHER_NO_5551="5551";
	/**
	 * ����֧�������˿��嵥5552
	 */
	public static final String VOUCHER_NO_5552="5552";
	/**
	 * ����ʵ����������嵥5553
	 */
	public static final String VOUCHER_NO_5553="5553";
	/**
	 * ����ר������ƾ֤5267
	 */
	public static final String VOUCHER_NO_5267="5267";
	
	/**
	 * ʵ�������嵥5257
	 */
	public static final String VOUCHER_NO_5257="5257";
	
	
	/**
	 * ��Ȩ֧��������֪ͨ��5106
	 */
	public static final String VOUCHER_NO_5106="5106";
	/**
	 * �������������ƾ֤����3501
	 */
	public static final String VOUCHER_NO_3501="3501";
	/**
	 * �����������������ƾ֤����3502
	 */
	public static final String VOUCHER_NO_3502="3502";
	
	/**
	 * �������������ƾ֤���˽����ѯ5502
	 */
	public static final String VOUCHER_NO_5502="5502";
	/**
	 * �����������������ƾ֤���˽����ѯ2502
	 */
	public static final String VOUCHER_NO_2502="2502";
	/**
	 * ��˰����ɿⱨ��֪ͨ5671
	 */
	public static final String VOUCHER_NO_5671="5671";
	
	/**
	 * ֱ��֧��������֪ͨ��5108
	 */
	public static final String VOUCHER_NO_5108="5108";
	/**
	 * ֱ��֧���ձ���
	 */
	public static final String VOUCHER_NO_2206="2206";
	/**
	 * ���뻮��ƾ֤�ص�2301
	 */
	public static final String VOUCHER_NO_2301="2301";
	
	/**
	 * �����˿�ƾ֤�ص�2302
	 */
	public static final String VOUCHER_NO_2302="2302";
	
	/**
	 * �����˸�ƾ֤5209
	 */
	public static final String VOUCHER_NO_5209="5209";
	/**
	 * ���Ŵ�����˿� 3209
	 */
	public static final String VOUCHER_NO_3209="3209";
	
	/**
	 * �˿�֪ͨ����2003(����)
	 */
	public static final String VOUCHER_NO_2003="2003";
	
	/**
	 * ����ձ���3402
	 */
	public static final String VOUCHER_NO_3402="3402";
	/**
	 * ȫʡ����ձ���3453�޸�ǰ
	 */
	public static final String VOUCHER_NO_3453="3453";
	/**
	 * ȫʡ����ձ���3453�޸ĺ�
	 */
	public static final String VOUCHER_NO_3404="3404";
	/**
	 * ������Ϣ����
	 */
	public static final String VOUCHER_NO_3507="3507";
	/**
	 * �����±�--��������3507�޸Ķ������Ĺ淶��������ĺų�ͻ��
	 */
	public static final String VOUCHER_NO_3567="3567";
	/**
	 * �����ձ�3401
	 */
	public static final String VOUCHER_NO_3401 = "3401";
	
	/**
	 * �����Ϣ����3503
	 */
	public static final String VOUCHER_NO_3503 = "3503";
	
	/**
	 * ������Ϣ����3504
	 */
	public static final String VOUCHER_NO_3504 = "3504";
	
	/**
	 * ʵ����Ϣ����3505
	 */
	public static final String VOUCHER_NO_3505 = "3505";
	
	/**
	 *  �����˸�����3506
	 */
	public static final String VOUCHER_NO_3506 = "3506";
	/**
	 * ֧���ձ�3410
	 */
	public static final String VOUCHER_NO_3410 = "3410";
	/**
	 * �������������֧����������˵�3551
	 */
	public static final String VOUCHER_NO_3551 = "3551";

	/**
	 * ����˻��¶ȶ��˵�3552
	 */
	public static final String VOUCHER_NO_3552 = "3552";
	
	/**
	 * ֧���ձ���3553
	 */
	public static final String VOUCHER_NO_3553 = "3553"; 
	
	/**
	 * ֧���±���3508
	 */
	public static final String VOUCHER_NO_3508 = "3508";
	
	/**
	 * �����˸��˿�֪ͨ���޸�ǰ
	 */
	public static final String VOUCHER_NO_3251 = "3251";
	/**
	 * �����˸��˿�֪ͨ���޸ĺ�
	 */
	public static final String VOUCHER_NO_3210 = "3210";
	/**
	 * ��ҵ������˵�3554
	 */
	public static final String VOUCHER_NO_3554 = "3554";
	
	/**
	 * �ܶ�ֳɱ���3454�޸�ǰ
	 */
	public static final String VOUCHER_NO_3454="3454";
	/**
	 * �ܶ�ֳɱ���3405�޸ĺ�
	 */
	public static final String VOUCHER_NO_3405="3405";
	/**
	 * ��˰��������3455�޸�ǰ
	 */
	public static final String VOUCHER_NO_3455="3455";
	/**
	 * ��˰��������3406�޸ĺ�
	 */
	public static final String VOUCHER_NO_3406="3406";
	/**
	 * �������Ի�������ȶ��˱���3550
	 */
	public static final String VOUCHER_NO_3550="3550";
	/**
	 * �������Ի�����ʵ���ʽ���˱���3558
	 */
	public static final String VOUCHER_NO_3558="3558";
	/**
	 * ���������걨3562
	 */
	public static final String VOUCHER_NO_3562="3562";
	/**
	 * �����ʽ����������3560
	 */
	public static final String VOUCHER_NO_3560 = "3560";
	/**
	 * ɽ������������ϸ����3587
	 */
	public static final String VOUCHER_NO_3587 = "3587";
	
	/**
	 * ɽ������ʵ���ʽ����3588
	 */
	public static final String VOUCHER_NO_3588 = "3588";
	/**
	 * ɽ�����������ȶ���3580
	 */
	public static final String VOUCHER_NO_3580 = "3580";
	/**
	 * ɽ������������3583
	 */
	public static final String VOUCHER_NO_3583 = "3583";
	/**
	 * ɽ�����������¶ȶ���
	 */
	public static final String VOUCHER_NO_3582 = "3582";
	/**
	 * �����������������֧�����˵�4004
	 */
	public static final String VOUCHER_NO_4004 = "4004";
	/**
	 * ����������������м���֧��ҵ����˵�4005
	 */
	public static final String VOUCHER_NO_4005 = "4005";
	/**
	 * ��ҵ���д�����֧���±���4006
	 */
	public static final String VOUCHER_NO_4006 = "4006";
	/**
	 * ��������ҵ��֧����ϸ8207(�Ϻ���ɫ)
	 */
	public static final String VOUCHER_NO_8207 = "8207";
	/**
	 * ����ֱ��֧��ƾ֤5201(�Ϻ���ɫ)
	 */
	public static final String VOUCHER_NO_5201 = "5201";
	/**
	 * ������Ȩ֧��ƾ֤8202(�Ϻ���ɫ)
	 */
	public static final String VOUCHER_NO_8202 = "8202";
	/**
	 * �տ������˿�֪ͨ2252(�Ϻ���ɫ)
	 */
	public static final String VOUCHER_NO_2252 = "2252";
	/**
	 * ��Ȩ֧��������5351(�Ϻ���ɫ)
	 */
	public static final String VOUCHER_NO_5351 = "5351";
	/**
	 * ����ֱ��֧������ƾ֤5253(�Ϻ���ɫ)
	 */
	public static final String VOUCHER_NO_5253 = "5253";
	/**
	 * ����ֱ��֧���˿�֪ͨ��2203(�Ϻ���ɫ)
	 */
	public static final String VOUCHER_NO_2203 = "2203";
	/**
	 * ��λ���5951(�Ϻ�)
	 */
	public static final String VOUCHER_NO_5951 = "5951";
	
	
	/**
	 * ������ɫ ���������ⱨ�Ĺ淶5801
	 */
	public static final String VOUCHER_NO_5407 = "5407";
	
	/**
	 * һ��ɿ���5408
	 */
	public static final String VOUCHER_NO_5408="5408";
	
	
	
	/**
	 * ��ֽ������ƾ֤����
	 */
	public static final String VOUCHER_ATTACH_SEND = "0001";
	/**
	 * ƾ֤���ķ���������ͷ
	 */
	public static final String VOUCHER_MSG_SERVER = "VOUCHER_";//
	
	/**
	 * ƾ֤javaת��xml���ķ���������ͷ
	 */
	public static final String VOUCHER_DTO2MAP = "VoucherDto2MapFor";
	
	/**
	 * ƾ֤������
	 */
	public static final String VOUCHER = "VOUCHER";

	/**
	 * ƾ֤ǩ������ [10]  ת���� 
	 */
	public static final String VOUCHERSAMP_ROTARY = "zqz";
	
	/**
	 * ƾ֤ǩ������ ������ �Ϻ��ĸ�����
	 */
	public static final String VOUCHERSAMP_ATTACH = "fjz";
	/**
	 * ƾ֤ǩ������ ҵ��ר����
	 */
	public static final String VOUCHERSAMP_BUSS = "ywzyz";
	/**
	 * ƾ֤ǩ������ [20]  �� �� 
	 */
	public static final String VOUCHERSAMP_OFFICIAL = "gz";
	
	/**
	 * ƾ֤ǩ������ []  ����Ա ˽ �� 
	 */
	public static final String VOUCHERSAMP_PRIVATE = "sz";
	/**
	 * ƾ֤ǩ������ []  ǩ��
	 */
	public static final String VOUCHERSIGN_PRIVATE = "qm";
	/**
	 * ƾ֤ǩ������ [30]  ����Ա ˽ �� 
	 */
	public static final String VOUCHERSAMP_PRIVATE_JZ = "jzysz";
	/**
	 * ƾ֤ǩ������ [40]  ����Ա ˽ �� 
	 */
	public static final String VOUCHERSAMP_PRIVATE_FH = "fhysz";
	
	/**
	 * ƾ֤ǩ������ [50]  ����Ա ǩ�� 
	 */
	public static final String VOUCHERSIGN_PRIVATE_JZ = "jzyqm";
	
	/**
	 * ƾ֤ǩ������ [60]  ����Ա ǩ�� 
	 */
	public static final String VOUCHERSIGN_PRIVATE_FH = "fhyqm";
	
	/**
	 * ת����ǩ�·�ʽ 0-������ǩ��  1-�ͻ���ǩ�� 
	 */
	public static final String VOUCHER_ROTARYSTAMP = "0";
	
	/**
	 * ����ǩ�·�ʽ 0-������ǩ��  1-�ͻ���ǩ��  
	 */
	public static final String VOUCHER_OFFICIALSTAMP = "0";
	
	/**
	 * ������ǩ�·�ʽ 0-������ǩ��  1-�ͻ���ǩ�� 
	 */
	public static final String VOUCHER_ATTACHSTAMP = "0";
	/**
	 * �Ƿ����ʵ���ʽ������ĿУ��0 -��У�� 1У��
	 */
	public static final String VOUCHER_CHECKPAYOUTSUBJECT="1";
	/**
	 * �������� 1 һ��Ԥ��֧��
	 */
	public static final String MSG_RPORT_NAME1 = "1";
	public static final String MSG_RPORT_NAME1CMT = "һ��Ԥ��֧��";
	/**
	 * �������� 2 ���а�����Ȩ֧��
	 */
	public static final String MSG_RPORT_NAME2 = "2";
	public static final String MSG_RPORT_NAME2CMT = "���а�����Ȩ֧��";
	
	/**
	 * ʵ���˿�֪ͨ��
	 */
	public static final String VOUCHER_NO_3208 = "3208";
	/**
	 * ����ר���˿�֪ͨ��
	 */
	public static final String VOUCHER_NO_3268 = "3268";
	/**
	 * �ʽ�ҵ������  99-���뼰ʡ������Ʊ��
	 */
	public static final String FUND_BIZ_TYPE_CENTREAREADATA = "99";
	public static final String FUND_BIZ_TYPE_CENTREAREADATA_CMT = "��������Ʊ��";
	
	
	/**
	 * �ʽ�ҵ������  00-����������������
	 */
	public static final String FUND_BIZ_TYPE_ORGCODEINNERINCOME = "00";
	public static final String FUND_BIZ_TYPE_ORGCODEINNERINCOME_CMT = "����������������";
	
	
	//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)
	//3-��˰����(111111111111),4-��˰����(222222222222),5-���ش���(333333333333),6-��������(555555555555),
	public static final String FIN_BIGKIND = "0";
	public static final String FIN_NO_LEV = "1";
	public static final String FIN_LEV_SLEF = "2";
	
	
	/**
	 * 1-����ҵ�� 2-ͬ�ǿ��� 3-��ؿ���
	 */
	public static final String MSG_1000_FLAG1 = "1";
	public static final String MSG_1000_FLAG2 = "2";
	public static final String MSG_1000_FLAG3 = "3";
	
	/**
	 *�����ȶ���
	 */
	public static final String VOUCHER_NO_3510 = "3510";
	/**
	 *�����ȶ��ʻ�ִ
	 */
	public static final String VOUCHER_NO_5510 = "5510";
	/**
	 *������Ϣ����
	 */
	/**
	 *��������ϸ����
	 */
	public static final String VOUCHER_NO_3515 = "3515";
	/**
	 *��������ϸ���ʻ�ִ
	 */
	public static final String VOUCHER_NO_5515 = "5515";
	/**
	 *������Ϣ����
	 */
//	public static final String VOUCHER_NO_3507 = "3507";
	/**
	 *������Ϣ���ʲ�����ִ
	 */
	public static final String VOUCHER_NO_5507 = "5507";
	/**
	 *������Ϣ�������л�ִ
	 */
	public static final String VOUCHER_NO_2507 = "2507";
	/**
	 *ʵ����Ϣ����
	 */
//	public static final String VOUCHER_NO_3508 = "3508";
	/**
	 *ʵ����Ϣ���ʻ�ִ
	 */
	public static final String VOUCHER_NO_5508 = "5508";
	/**
	 *�����˸�����
	 */
	public static final String VOUCHER_NO_3509 = "3509";
	/**
	 *�����˸����ʻ�ִ
	 */
	public static final String VOUCHER_NO_5509 = "5509";
	/**
	 * ���뱨�����
	 */
	public static final String VOUCHER_NO_3511 = "3511";
	/**
	 *���뱨����ʻ�ִ
	 */
	public static final String VOUCHER_NO_5511 = "5511";
	/**
	 *֧���������
	 */
	public static final String VOUCHER_NO_3512 = "3512";
	/**
	 *֧��������ʻ�ִ
	 */
	public static final String VOUCHER_NO_5512 = "5512";
	/**
	 *����˻�����
	 */
	public static final String VOUCHER_NO_3513 = "3513";
	/**
	 *����˻����ʻ�ִ
	 */
	public static final String VOUCHER_NO_5513 = "5513";
	/**
	 *����Ԫ����
	 */
	public static final String VOUCHER_NO_5901 = "5901";
	
	/**
	 *�ֻ��˶���
	 */
	public static final String VOUCHER_NO_3514 = "3514";
	
	/**
	 * ���˽��0-��� 1-����
	 */
	public static final String VOUCHER_RECONCIIATION_YES = "0";
	
	/**
	 * ���˽��0-��� 1-����
	 */
	public static final String VOUCHER_RECONCIIATION_NO = "1";
	
	/**
	 * ���������⴦������   
	 * 1-���� ��������TIPS  2-�ֹ�
	 */
	public static final String VOUCHER_CORRHANDBOOK_DEALTYPE = "1";
	/**
	 *ʵ���ʽ��˻�
	 */
	public static final String MSG_NO_3208 = "3208";
	/**
	 *��������
	 */
	public static final String MSG_NO_51044 = "51044";
	
	/****************************** �������� **********************************/
	/**
	 *�й����������Ϻ��з���Ӫҵ��
	 */
	public static final String MSG_NO_102 = "102";
	/**
	 *�й�ũҵ�����Ϻ��з���
	 */
	public static final String MSG_NO_103 = "103";
	/**
	 *�й��������йɷ����޹�˾�Ϻ�������������
	 */
	public static final String MSG_NO_105 = "105";
	/**
	 *��ͨ�����Ϻ�������������
	 */
	public static final String MSG_NO_301 = "301";
	/**
	 *�Ϻ��ֶ���չ���е�һӪҵ��
	 */
	public static final String MSG_NO_310 = "310";
	/**
	 *�й������Ϻ��з���Ӫҵ��
	 */
	public static final String MSG_NO_104 = "104";
	/**
	 *����ʵҵ�����Ϻ����л�����㲿
	 */
	public static final String MSG_NO_302 = "302";
	/**
	 *���������Ϻ����л�Ʋ���������
	 */
	public static final String MSG_NO_308 = "308";
	/**
	 *�й���������Ϻ����б�������
	 */
	public static final String MSG_NO_303 = "303";
	/**
	 *�������йɷ����޹�˾�Ϻ����л�Ʋ�
	 */
	public static final String MSG_NO_304 = "304";
	/**
	 *�й����������Ϻ�������������
	 */
	public static final String MSG_NO_305 = "305";
	/**
	 *��ҵ�����Ϻ�����
	 */
	public static final String MSG_NO_309 = "309";
	/**
	 *�㷢�����Ϻ�����Ӫҵ��
	 */
	public static final String MSG_NO_306 = "306";
	/**
	 *ƽ�������Ϻ�������������
	 */
	public static final String MSG_NO_307 = "307";
	/**
	 *�Ϻ�����
	 */
	public static final String MSG_NO_313 = "313";
	/**
	 *�Ϻ�ũ������ҵ��������
	 */
	public static final String MSG_NO_322 = "322";
	/**
	 *�й������������йɷ����޹�˾�Ϻ�����Ӫҵ��
	 */
	public static final String MSG_NO_403 = "403";
	
	
}

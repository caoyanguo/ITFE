package com.cfcc.itfe.config;

import java.util.HashMap;

import com.cfcc.jaf.core.loader.ContextFactory;

public class BizConfigInfo {
	private static HashMap<String, String> TABLE_MAP = null;

	private static HashMap<String, String> tableToDto = null;
	private static HashMap<String, String> dtoToTable = null;

	static {
		TABLE_MAP = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("TABLE_MAP");// TableNameConfig.xml
		tableToDto = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("TABLE_DTO");

		dtoToTable = (HashMap<String, String>) ContextFactory
				.getApplicationContext().getBean("DTO_TABLE");
	}

	public static HashMap<String, String> TableToDtoMap() {
		return tableToDto;
	}

	public static HashMap<String, String> TableDesMap() {
		return TABLE_MAP;
	}

	public static HashMap<String, String> DtoToTableMap() {
		return dtoToTable;
	}

	/**
	 * ����ҵ�����ͻ�ȡ�����ļ�BizConfig.xml����Ϣ
	 * 
	 * @param biz
	 *            ҵ������
	 * @return
	 */
	public static HashMap<String, String> BizTable(String biz) {
		return (HashMap<String, String>) ContextFactory.getApplicationContext()
				.getBean(biz);
	}

	/**
	 * �ʽ�ҵ������ö��ֵ������ҵ�����ͻ�ȡ�ʽ���˷�ʽ
	 * 
	 * @return
	 */

	public static HashMap<String, String> FoundTypeMap() {
		HashMap<String, String> foundTypeMap = new HashMap<String, String>();
		// ҵ���������ʽ����Ͷ��չ�ϵ
		// 510 ͬ�����Ʊ�ݴ��� 11
		// 550 �����ڲ��������˽����Ϣ 10
		// 551 �����ڲ�����������Ϣ 09
		// 560 �����ڲ��������˴�����Ϣ 10
		// 710 ���֧���������˴��� 02
		// 711 ���֧���������˴��� 01
		// 720 �����ʽ�㻮(����)����CMT103���˴��� 02
		// 721 �����ʽ�㻮(����)�������˴��� 01
		// 0010 ������ͨ���Ǳ��� 04
		// 0020 ������ͨ��Ǳ��� 04
		// 0030 ����ʵʱ���Ǳ��� 04
		// 0070 ���ʴ����˻㱨�� 04
		// 0040 ����ʵʱ��Ǳ��� 04
		// 0011 ������ͨ���Ǳ��� 03
		// 0021 ������ͨ��Ǳ��� 03
		// 0031 ����ʵʱ���Ǳ��� 03
		// 0071 ���ʴ����˻㱨�� 03
		// 0041 ����ʵʱ��Ǳ��� 03
		// 030 ��ϸ��ֱ��ת���ල
		// 031 ��ϸ����תת��ල

		// 035 �ʽ�����˼��˼ල
		// 036 �ʽ�����˼��˼ල

		// 038 �ʽ��������ת�ල

		foundTypeMap.put("510", "11");
		foundTypeMap.put("550", "10");
		foundTypeMap.put("551", "09");
		foundTypeMap.put("560", "10");
		foundTypeMap.put("710", "02");
		foundTypeMap.put("711", "01");
		foundTypeMap.put("720", "02");
		foundTypeMap.put("721", "01");
		foundTypeMap.put("0010", "04");
		foundTypeMap.put("0020", "04");
		foundTypeMap.put("0030", "04");
		foundTypeMap.put("0070", "04");
		foundTypeMap.put("0040", "04");
		foundTypeMap.put("0011", "03");
		foundTypeMap.put("0021", "03");
		foundTypeMap.put("0031", "03");
		foundTypeMap.put("0071", "03");
		foundTypeMap.put("0041", "03");

		foundTypeMap.put("030", "11");
		foundTypeMap.put("031", "11");
		foundTypeMap.put("035", "11");
		foundTypeMap.put("038", "11");

		return foundTypeMap;
	}

	public static HashMap<String, String> TypeSubjectMap() {
		HashMap<String, String> typeSubjectMap = new HashMap<String, String>();
		// ҵ����������˿�Ŀ���չ�ϵ
		// 510 ͬ�����Ʊ�ݴ��� s_CityExchgSbt
		// 550 �����ڲ��������˽����Ϣ s_InnerSbt
		// 551 �����ڲ�����������Ϣ s_InnerSbt
		// 560 �����ڲ��������˴�����Ϣ s_InnerSbt
		// 710 ���֧���������˴��� s_BigPaySbt
		// 711 ���֧���������˴��� s_BigPaySbt
		// 720 �����ʽ�㻮(����)����CMT103���˴��� s_BigPaySbt
		// 721 �����ʽ�㻮(����)�������˴��� s_BigPaySbt
		// 0010 ������ͨ���Ǳ��� s_SmallWillReckSbt
		// 0020 ������ͨ��Ǳ��� s_SmallWillReckSbt
		// 0030 ����ʵʱ���Ǳ��� s_SmallWillReckSbt
		// 0070 ���ʴ����˻㱨�� s_SmallWillReckSbt
		// 0040 ����ʵʱ��Ǳ��� s_SmallWillReckSbt
		// 0011 ������ͨ���Ǳ��� s_SmallWillReckSbt
		// 0021 ������ͨ��Ǳ��� s_SmallWillReckSbt
		// 0031 ����ʵʱ���Ǳ��� s_SmallWillReckSbt
		// 0071 ���ʴ����˻㱨�� s_SmallWillReckSbt
		// 0041 ����ʵʱ��Ǳ��� s_SmallWillReckSbt
		typeSubjectMap.put("510", "scityexchgsbt");
		typeSubjectMap.put("550", "sinnersbt");
		typeSubjectMap.put("551", "sinnersbt");
		typeSubjectMap.put("560", "sinnersbt");
		typeSubjectMap.put("710", "sbigpaysbt");
		typeSubjectMap.put("711", "sbigpaysbt");
		typeSubjectMap.put("720", "sbigpaysbt");
		typeSubjectMap.put("721", "sbigpaysbt");
		typeSubjectMap.put("0010", "ssmallwillrecksbt");
		typeSubjectMap.put("0020", "ssmallwillrecksbt");
		typeSubjectMap.put("0030", "ssmallwillrecksbt");
		typeSubjectMap.put("0070", "ssmallwillrecksbt");
		typeSubjectMap.put("0040", "ssmallwillrecksbt");
		typeSubjectMap.put("0011", "ssmallwillrecksbt");
		typeSubjectMap.put("0021", "ssmallwillrecksbt");
		typeSubjectMap.put("0031", "ssmallwillrecksbt");
	
		typeSubjectMap.put("0071", "ssmallwillrecksbt");
		typeSubjectMap.put("0041", "ssmallwillrecksbt");
		
		typeSubjectMap.put("038", "ssmallwillrecksbt");/////////////////����������������������������

		return typeSubjectMap;
	}

	public static HashMap<String, String> LevelSubjectMap() {
		HashMap<String, String> levelSubjectMap = new HashMap<String, String>();
		// Ԥ�㼶������˿�Ŀ���չ�ϵ
		// 0-�����⹲��
		// 1-����������
		// 2-������ʡ
		// 3-��������
		// 4-��������
		// 5-��������
		levelSubjectMap.put("0", "swillsharesbt");
		levelSubjectMap.put("1", "swillnavelsbt");
		levelSubjectMap.put("2", "swillprovsbt");
		levelSubjectMap.put("3", "swillcitysbt");
		levelSubjectMap.put("4", "swillcountysbt");
		levelSubjectMap.put("5", "swillcountysidesbt");
		return levelSubjectMap;
	}
}

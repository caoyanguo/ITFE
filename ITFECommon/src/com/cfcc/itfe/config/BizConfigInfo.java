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
	 * 根据业务类型获取配置文件BizConfig.xml的信息
	 * 
	 * @param biz
	 *            业务类型
	 * @return
	 */
	public static HashMap<String, String> BizTable(String biz) {
		return (HashMap<String, String>) ContextFactory.getApplicationContext()
				.getBean(biz);
	}

	/**
	 * 资金业务类型枚举值，根据业务类型获取资金记账方式
	 * 
	 * @return
	 */

	public static HashMap<String, String> FoundTypeMap() {
		HashMap<String, String> foundTypeMap = new HashMap<String, String>();
		// 业务类型与资金类型对照关系
		// 510 同城提出票据处理 11
		// 550 国库内部往来往账借记信息 10
		// 551 国库内部往来来账信息 09
		// 560 国库内部往来往账贷记信息 10
		// 710 汇兑支付报文往账处理 02
		// 711 汇兑支付报文来账处理 01
		// 720 国库资金汇划(贷记)报文CMT103往账处理 02
		// 721 国库资金汇划(贷记)报文来账处理 01
		// 0010 往帐普通贷记报文 04
		// 0020 往帐普通借记报文 04
		// 0030 往帐实时贷记报文 04
		// 0070 往帐贷记退汇报文 04
		// 0040 往帐实时借记报文 04
		// 0011 来帐普通贷记报文 03
		// 0021 来帐普通借记报文 03
		// 0031 来帐实时贷记报文 03
		// 0071 来帐贷记退汇报文 03
		// 0041 来帐实时借记报文 03
		// 030 明细岗直接转出监督
		// 031 明细岗内转转入监督

		// 035 资金岗来账记账监督
		// 036 资金岗往账记账监督

		// 038 资金岗往账内转监督

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
		// 业务类型与记账科目对照关系
		// 510 同城提出票据处理 s_CityExchgSbt
		// 550 国库内部往来往账借记信息 s_InnerSbt
		// 551 国库内部往来来账信息 s_InnerSbt
		// 560 国库内部往来往账贷记信息 s_InnerSbt
		// 710 汇兑支付报文往账处理 s_BigPaySbt
		// 711 汇兑支付报文来账处理 s_BigPaySbt
		// 720 国库资金汇划(贷记)报文CMT103往账处理 s_BigPaySbt
		// 721 国库资金汇划(贷记)报文来账处理 s_BigPaySbt
		// 0010 往帐普通贷记报文 s_SmallWillReckSbt
		// 0020 往帐普通借记报文 s_SmallWillReckSbt
		// 0030 往帐实时贷记报文 s_SmallWillReckSbt
		// 0070 往帐贷记退汇报文 s_SmallWillReckSbt
		// 0040 往帐实时借记报文 s_SmallWillReckSbt
		// 0011 来帐普通贷记报文 s_SmallWillReckSbt
		// 0021 来帐普通借记报文 s_SmallWillReckSbt
		// 0031 来帐实时贷记报文 s_SmallWillReckSbt
		// 0071 来帐贷记退汇报文 s_SmallWillReckSbt
		// 0041 来帐实时借记报文 s_SmallWillReckSbt
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
		
		typeSubjectMap.put("038", "ssmallwillrecksbt");/////////////////？？？？？？？？？？？有问题

		return typeSubjectMap;
	}

	public static HashMap<String, String> LevelSubjectMap() {
		HashMap<String, String> levelSubjectMap = new HashMap<String, String>();
		// 预算级次与记账科目对照关系
		// 0-待报解共享
		// 1-待报解中央
		// 2-待报解省
		// 3-待报解市
		// 4-待报解县
		// 5-待报解乡
		levelSubjectMap.put("0", "swillsharesbt");
		levelSubjectMap.put("1", "swillnavelsbt");
		levelSubjectMap.put("2", "swillprovsbt");
		levelSubjectMap.put("3", "swillcitysbt");
		levelSubjectMap.put("4", "swillcountysbt");
		levelSubjectMap.put("5", "swillcountysidesbt");
		return levelSubjectMap;
	}
}

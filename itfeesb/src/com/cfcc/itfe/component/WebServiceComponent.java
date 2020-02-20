package com.cfcc.itfe.component;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface WebServiceComponent {
	
	String sayHi(@WebParam(name = "text") String text);

	/**
	 * 数据读取
	 * 读取指定报文标识号的数据。
	 * 
	 * @param finorgcode 财政机构代码
	 * @param reportdate 报表日期 格式YYYYMMDD
	 * @param biztype    业务类型 （入库流水：3139，电子税票：3129，收入日报：3128）
	 * @param msgid      报文标识号
	 * 
	 * @return 报文数据集合
	 * @throws Exception 
	 */
	byte[] readReport(
			@WebParam(name = "finorgcode") String finorgcode,
			@WebParam(name = "reportdate") String reportdate,
			@WebParam(name = "biztype") String biztype,
			@WebParam(name = "msgid") String msgid) throws Exception;
	
	/**
	 * 数据读取
	 * 读取指定财政某报表日期的报文标识号的集合，用于没有收到报表时，主动发起请求。
	 * 
	 * @param finorgcode  财政机构代码
	 * @param reportdate  报表日期 格式YYYYMMDD
	 * @param biztype     业务类型 （入库流水：3139，电子税票：3129，收入日报：3128）
	 * @throws Exception 
	 */
	List readReportMsgID(
			@WebParam(name = "finorgcode") String finorgcode,
			@WebParam(name = "reportdate") String reportdate,
			@WebParam(name = "biztype") String biztype) throws Exception;
	
	/**
	 * 数据读取(指定用户名密码)
	 * 读取指定财政某报表日期的报文标识号的集合，用于没有收到报表时，主动发起请求。
	 * 
	 * @param finorgcode  财政机构代码
	 * @param reportdate  报表日期 格式YYYYMMDD
	 * @param biztype     业务类型 （入库流水：3139，电子税票：3129，收入日报：3128）
	 * @param usercode    用户名
	 * @param password    密码
	 * @throws Exception 
	 */
	List readReportMsgIDByUser(
			@WebParam(name = "finorgcode") String finorgcode,
			@WebParam(name = "reportdate") String reportdate,
			@WebParam(name = "biztype") String biztype,
			@WebParam(name = "usercode") String usercode,
			@WebParam(name = "password") String password) throws Exception;
	
	/**
	 * 数据读取回执
	 * 
	 * @param finorgcode 财政机构代码
	 * @param reportdate 报表日期 格式YYYYMMDD
	 * @param biztype    业务类型 （入库流水：3139，电子税票：3129，收入日报：3128）
	 * @param msgid      报文标识号
	 * @param status     处理状态 （成功：0 ，失败：1）
	 * 
	 */
	void readReportReceipt(
			@WebParam(name = "finorgcode") String finorgcode,
			@WebParam(name = "reportdate") String reportdate,
			@WebParam(name = "biztype") String biztype,
			@WebParam(name = "msgid") String msgid,
			@WebParam(name = "status") String status);
	
}

package com.cfcc.itfe.service.util;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsCheckdeloptlogDto;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

public class WriteOperLog {
	private static Log logger = LogFactory.getLog(WriteOperLog.class);

	/**
	 * 记录日志
	 * 
	 * @param userID
	 * @param sFuncCode
	 * @param menuName
	 * @param serviceName
	 * @param operFlag
	 * @param loginInfo
	 * @throws ITFEBizException 
	 * @throws TASBizException
	 */
	public static void operLog(String userID, String sFuncCode,
			String menuName, String serviceName, boolean operFlag,
			ITFELoginInfo loginInfo) throws ITFEBizException {

		TsSyslogDto log = new TsSyslogDto();
		// 流水号
		try {
			log.setIno(new Integer(MsgSeqFacade.getlogSeq()));
		} catch (NumberFormatException e2) {
			logger.error("取SEQ异常", e2);
			logger.error("日志内容:" + log.toString());
			throw new ITFEBizException("日志内容:" + log.toString(),e2);
		} catch (SequenceException e2) {
			logger.error("取SEQ异常", e2);
			logger.error("日志内容:" + log.toString());
			throw new ITFEBizException("日志内容:" + log.toString(),e2);
		}
		// 用户id
		log.setSusercode(userID);
		// 日期
		log.setSdate(TimeFacade.getCurrentStringTime());
		// 时间
		try {
			log.setStime(TSystemFacade.getDBSystemTime());
		} catch (JAFDatabaseException e1) {
			logger.error("取时间戳发生异常!", e1);
			throw new ITFEBizException("日志内容:" + log.toString(),e1);
		}
		// 业务类型码
		log.setSoperationtypecode(sFuncCode);
		// 核算主体代码
		log.setSorgcode(loginInfo.getSorgcode());
		// 操作说明
		log.setSoperationdesc(menuName);
		// 备注
		if (operFlag) {
			log.setSdemo("操作未成功");
		}
		if (serviceName.equals("batchConfirm")) {
			log.setSdemo("批量销号:确认提交");
		} else if (serviceName.equals("batchDelete")) {
			log.setSdemo("批量销号:删除");
		} else if (serviceName.equals("eachConfirm")) {
			log.setSdemo("逐笔销号：确认提交");
		} else if (serviceName.equals("eachDelete")) {
			log.setSdemo("逐笔销号：删除");
		} else if (serviceName.equals("directSubmit")) {
			log.setSdemo("直接提交");
		}
		// 写数据库操作
		try {
			DatabaseFacade.getDb().create(log);
		} catch (JAFDatabaseException e) {
			logger.error("记录数据库日志发生异常", e);
			throw new ITFEBizException("日志内容:" + log.toString(),e);
		}
	}

	/**
	 * 写销号删除操作日志
	 * 
	 * @param s_biztype
	 * @param s_filename
	 * @param s_vouno
	 * @param loginInfo
	 * @throws ITFEBizException 
	 */
	public static void checkDelOptLog(String s_biztype, String s_filename,
			String s_vouno,BigDecimal famt, ITFELoginInfo loginInfo) throws ITFEBizException {

		TsCheckdeloptlogDto log = new TsCheckdeloptlogDto();
		// 机构代码
		log.setSorgcode(loginInfo.getSorgcode());
		//业务类型
		log.setSbiztype(s_biztype);
		// 用户代码
		log.setSusercode(loginInfo.getSuserCode());
		//用户名称
		log.setSusername(loginInfo.getSuserName());
		//文件名
		log.setSfilename(s_filename);
		//凭证编号
		log.setSvouno(s_vouno);
		// 操作时间
		try {
			log.setTopetime(TSystemFacade.getDBSystemTime());
		} catch (JAFDatabaseException e1) {
			logger.error("记录数据库日志发生异常", e1);
			throw new ITFEBizException("日志内容:" + log.toString(),e1);
		}
		//金额
		log.setFamt(famt);
		// 写数据库操作
		try {
			DatabaseFacade.getDb().create(log);
		} catch (JAFDatabaseException e) {
			logger.error("记录数据库日志发生异常", e);
			throw new ITFEBizException("日志内容:" + log.toString(),e);
		}
	}
}

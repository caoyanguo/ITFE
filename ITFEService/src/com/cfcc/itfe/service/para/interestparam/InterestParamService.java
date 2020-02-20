package com.cfcc.itfe.service.para.interestparam;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.math.*;
import java.util.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TfInterestParamDto;
import com.cfcc.itfe.persistence.dto.TsJxAcctinfoDto;
import com.cfcc.itfe.service.para.interestparam.AbstractInterestParamService;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author Administrator
 * @time 14-09-24 14:58:34 codecomment:
 */

public class InterestParamService extends AbstractInterestParamService {
	private static Log log = LogFactory.getLog(InterestParamService.class);

	/*
	 * 返回值0代表日期传入错误，1、2、3、4分别代表四个季度
	 * 
	 * public static String getQuarter(String startDate,String endDate){
	 * SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd"); String
	 * ls_CurrentYear = TimeFacade.getCurrentStringTime().substring(0, 4);
	 * ParsePosition pos = new ParsePosition(0); Date ld_startDate =
	 * formatter.parse(startDate, pos); Date ld_endDate =
	 * formatter.parse(endDate, pos); Date date1 =
	 * formatter.parse(ls_CurrentYear+"0319", pos); Date date2 =
	 * formatter.parse(ls_CurrentYear+"0619", pos); Date date3 =
	 * formatter.parse(ls_CurrentYear+"0919", pos); Date date4 =
	 * formatter.parse(ls_CurrentYear+"1219", pos);
	 * if(date1.after(ld_startDate)&&date1.before(ld_endDate)){ return "1"; }
	 * if(date2.after(ld_startDate)&&date2.before(ld_endDate)){ return "1"; }
	 * return endDate; }
	 */
	/**
	 * 增加
	 * 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException
	 */
	public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			TfInterestParamDto dto = (TfInterestParamDto) dtoInfo;
			if (checkDefaultDto(dto)) {
				// 获取凭证序列号
				String vouno = SequenceGenerator.getNextByDb2(
						SequenceName.GRANTPAY_SEQ,
						SequenceName.TRAID_SEQ_CACHE,
						SequenceName.TRAID_SEQ_STARTWITH);
				dto.setIvousrlno(Long.parseLong(vouno));
				DatabaseFacade.getDb().create(dtoInfo);
				return dtoInfo;
			} else {
				return null;
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if ("23505".equals(e.getSqlState())) {
				throw new ITFEBizException("不能重复录入！", e);
			}
		} catch (SequenceException e) {
			log.error(e);
			throw new ITFEBizException("获取sequence值失败！", e);
		}
		return null;
	}

	private boolean checkDefaultDto(TfInterestParamDto dto)
			throws ITFEBizException {
		try {
			if (StringUtils.isBlank(dto.getSext2())) {
				return true;
			}
			String sql = "SELECT * FROM TF_INTEREST_PARAM WHERE S_EXT3 = '"
					+ dto.getSext3() + "' AND S_STARTDATE  = '" + dto.getSstartdate() +"' AND S_ENDDATE  = '" + dto.getSenddate() +"' AND S_EXT2 IS NULL ";
			SQLExecutor sqlExecutor = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			SQLResults sqlResults = sqlExecutor.runQueryCloseCon(sql);
			if (sqlResults.getRowCount() == 0) {
				return false;
//				throw new ITFEBizException("请先维护国库下默认利率！");
			} else if (sqlResults.getRowCount() == 1) {
				return true;
			} 
			return false;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询数据库失败！", e);
		}

	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException
	 */
	public void delInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().delete(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException
	 */
	public void modInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			TfInterestParamDto dto = (TfInterestParamDto) dtoInfo;
			DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException(" 不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
	}

	public IDto addJXInfo(IDto JXDtoInfo) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().create(JXDtoInfo);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("【核算主体代码+国库主体代码+开户行+计息账户】对应记录 已存在，不能重复录入！", e);
		}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
			}
		return null;
	}

	public void delJXInfo(IDto delInfo) throws ITFEBizException {
		try {
    		DatabaseFacade.getDb().delete(delInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
	}

	public void modJXInfo(IDto tsJxAcctinfoDto,IDto JXDtoInfo) throws ITFEBizException {
		try {
			TsJxAcctinfoDto acctinfoDto=(TsJxAcctinfoDto)tsJxAcctinfoDto;
			TsJxAcctinfoDto jxAcctinfoDto=(TsJxAcctinfoDto)JXDtoInfo;
			StringBuffer insql = new StringBuffer();
			SQLExecutor sqlExec = null;
			SQLResults rs = null;
			insql.append("UPDATE TS_JX_ACCTINFO SET S_ORGCODE = ?,S_TRECODE = ?,S_OPNBANKCODE = ?,S_PAYEEACCT = ?,S_PAYEENAME =?,S_BIZTYPE =?,TS_SYSUPDATE =CURRENT TIMESTAMP  ");
			insql.append(" WHERE S_ORGCODE = ? AND S_TRECODE = ? AND S_OPNBANKCODE = ? AND S_PAYEEACCT = ?");
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
			.getSQLExecutor();
			sqlExec.addParam(jxAcctinfoDto.getSorgcode());
			sqlExec.addParam(jxAcctinfoDto.getStrecode());
			sqlExec.addParam(jxAcctinfoDto.getSopnbankcode());
			sqlExec.addParam(jxAcctinfoDto.getSpayeeacct());
			sqlExec.addParam(jxAcctinfoDto.getSpayeename());
			sqlExec.addParam(jxAcctinfoDto.getSbiztype());
			sqlExec.addParam(acctinfoDto.getSorgcode());
			sqlExec.addParam(acctinfoDto.getStrecode());
			sqlExec.addParam(acctinfoDto.getSopnbankcode());
			sqlExec.addParam(acctinfoDto.getSpayeeacct());
			sqlExec.runQueryCloseCon(insql.toString());
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException(" 【核算主体代码+国库主体代码+开户行+计息账户】对应记录 已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
	}

	
	

}
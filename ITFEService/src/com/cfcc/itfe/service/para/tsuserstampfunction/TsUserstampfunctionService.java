package com.cfcc.itfe.service.para.tsuserstampfunction;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import org.mule.endpoint.UserInfoEndpointURIBuilder;

import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsUsersfuncDto;
import com.cfcc.itfe.persistence.dto.TsUserstampDto;
import com.cfcc.itfe.persistence.dto.TsUserstampfunctionDto;
import com.cfcc.itfe.persistence.dto.UserStampInfoDto;
import com.cfcc.itfe.service.para.tsuserstampfunction.AbstractTsUserstampfunctionService;

import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time 09-10-20 08:42:02 codecomment:
 */

public class TsUserstampfunctionService extends
		AbstractTsUserstampfunctionService {
	private static Log log = LogFactory
			.getLog(TsUserstampfunctionService.class);

	/**
	 * 增加
	 * 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException
	 */

	public IDto addInfo(List listInfo) throws ITFEBizException {
		TsUserstampfunctionDto dto = (TsUserstampfunctionDto) listInfo.get(0);
		try {
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String sql = "DELETE FROM  TS_USERSTAMPFUNCTION WHERE S_ORGCODE =? AND S_USERCODE=? AND S_STAMPTYPECODE =?";
			exec.addParam(dto.getSorgcode());
			exec.addParam(dto.getSusercode());
			exec.addParam(dto.getSstamptypecode());
			exec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e1) {
			log.error(e1);
			throw new ITFEBizException("删除用户功能码出错", e1);
		}
		if (null != dto.getSmodelid() && dto.getSmodelid().trim().length() > 0) {
			try {
				DatabaseFacade.getDb().create(CommonUtil.listTArray(listInfo));
			} catch (JAFDatabaseException e) {
				log.error("数据保存出错", e);
				throw new ITFEBizException("保存数据出错", e);
			}
		}
		return null;
	}

	public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().create(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;

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
			CommonFacade.getODB().deleteRsByDto(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} catch (ValidateException e) {
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
			DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
	}

	public List findUserInfo(IDto dtoInfo) throws ITFEBizException {
		TsUserstampDto dto = (TsUserstampDto) dtoInfo;
		try {
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String sql = "SELECT A.S_ORGCODE,A.S_USERCODE,B.S_USERNAME AS SUSERNAME, "
					+ " A.S_STAMPTYPECODE ,C.S_STAMPTYPENAME AS SSTAMPTYPENAME"
					+ " FROM TS_USERSTAMP A,TS_USERS B,TS_STAMPTYPE C "
					+ " WHERE  A.S_USERCODE = B.S_USERCODE AND A.S_STAMPTYPECODE=C.S_STAMPTYPECODE"
					+ " AND A.S_ORGCODE = B.S_ORGCODE";
			if (null != dto.getSorgcode() && dto.getSorgcode().trim().length()> 0 ) {
				sql = sql + " AND A.S_ORGCODE =? ";
				exec.addParam(dto.getSorgcode());
			}
			List<UserStampInfoDto> list = (List<UserStampInfoDto>) exec
					.runQueryCloseCon(sql, UserStampInfoDto.class)
					.getDtoCollection();
			return list;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询用户签章信息出错", e);
		}
	}
	
	public List findstampPosCheckInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String sql = "SELECT  A.S_MODELID AS SMODELID,C.S_OPERATIONTYPENAME AS SOPERATIONTYPENAME,A.S_PLACEID AS SPLACEID, "
					+ " A.S_STAMPTYPECODE AS SSTAMPTYPECODE ,e.S_PLACEDESC AS SPLACEDESC ,D.S_STAMPTYPENAME AS SSTAMPTYPENAME FROM TS_USERSTAMPFUNCTION A,TS_OPERATIONMODEL B,"
					+ " TS_OPERATIONTYPE C,TS_STAMPTYPE D ,TS_OPERATIONPLACE e WHERE A.S_MODELID = B.S_MODELID AND "
					+ " B.S_OPERATIONTYPECODE = C.S_OPERATIONTYPECODE AND A.S_STAMPTYPECODE = D.S_STAMPTYPECODE  "
					+ " and A.S_PLACEID = e.S_PLACEID AND A.S_ORGCODE = ? ";
			TsUserstampDto dto = (TsUserstampDto) dtoInfo;
			exec.addParam(dto.getSorgcode());
			if (null != dto.getSstamptypecode()
					&& dto.getSstamptypecode() != "") {
				sql += "AND a.S_STAMPTYPECODE = ? ";
				exec.addParam(dto.getSstamptypecode());
			}
			if (null != dto.getSusercode() && dto.getSusercode() != "") {
				sql += "AND A.S_usercode = ? ";
				exec.addParam(dto.getSusercode());
			}
			List<UserStampInfoDto> list = (List<UserStampInfoDto>) exec
					.runQueryCloseCon(sql, UserStampInfoDto.class)
					.getDtoCollection();
			return list;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询用户盖章位置出错", e);
		}
	}

	public List findstampPosInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String sql = "SELECT A.S_MODELID AS SMODELID,C.S_OPERATIONTYPENAME AS SOPERATIONTYPENAME, A.S_PLACEID AS SPLACEID,"
					+ " A.S_STAMPTYPECODE AS SSTAMPTYPECODE ,A.S_PLACEDESC AS SPLACEDESC , D.S_STAMPTYPENAME AS SSTAMPTYPENAME FROM"
					+ " TS_OPERATIONPLACE A,TS_OPERATIONMODEL B, TS_OPERATIONTYPE C,TS_STAMPTYPE D WHERE A.S_MODELID = B.S_MODELID "
					+ " AND B.S_OPERATIONTYPECODE = C.S_OPERATIONTYPECODE AND A.S_STAMPTYPECODE = D.S_STAMPTYPECODE";
			TsUserstampfunctionDto dto = (TsUserstampfunctionDto) dtoInfo;
			if (null != dto.getSstamptypecode()
					&& dto.getSstamptypecode() != "") {
				sql += " AND A.S_STAMPTYPECODE = ? ";
				exec.addParam(dto.getSstamptypecode());
			}
			List<UserStampInfoDto> list = (List<UserStampInfoDto>) exec
					.runQueryCloseCon(sql, UserStampInfoDto.class)
					.getDtoCollection();
			return list;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询用户盖章位置出错", e);
		}
	}

}
package com.cfcc.itfe.service.dataquery.settleaccounts;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import com.cfcc.itfe.service.dataquery.settleaccounts.AbstractSettleAccountsService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.DetailTsUsers;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author Administrator
 * @time 13-03-24 20:53:58 codecomment:
 */

public class SettleAccountsService extends AbstractSettleAccountsService {
	private static Log log = LogFactory.getLog(SettleAccountsService.class);

	/**
	 * 查询
	 * 
	 * @generated
	 * @param detailDto
	 * @return java.util.List
	 * @throws ITFEBizExceptio
	 */
	public List search(DetailTsUsers detailDto) throws ITFEBizException {
		StringBuffer sql = new StringBuffer();
		sql
				.append("SELECT u.*,o.S_ORGNAME,o.S_ORGLEVEL FROM TS_USERS u INNER JOIN TS_ORGAN o ON u.S_ORGCODE = o.S_ORGCODE  where o.S_ORGCODE != '000000000000'  ");
		if (StringUtils.isNotBlank(detailDto.getSusertype())) {
			sql.append(" and  u.S_USERTYPE = '" + detailDto.getSusertype()
					+ "' ");
		}
		if (StringUtils.isNotBlank(detailDto.getSuserstatus())) {
			sql.append(" and u.S_USERSTATUS = '" + detailDto.getSuserstatus()
					+ "' ");
		}
		if (StringUtils.isNotBlank(detailDto.getSloginstatus())) {
			sql.append(" and u.s_loginstatus = '" + detailDto.getSloginstatus()
					+ "' ");
		}
		if (StringUtils.isNotBlank(detailDto.getSorglevel())) {
			sql.append(" and o.S_orglevel = '" + detailDto.getSorglevel()
					+ "' ");
		}

		if (StringUtils.isNotBlank(detailDto.getSorgname())) {
			sql.append(" and o.S_orgcode = '" + detailDto.getSorgname() + "' ");
		}
		SQLExecutor sqlExecutor = null;
		try {
			 sqlExecutor = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			return (List) sqlExecutor.runQuery(sql.toString(),
					DetailTsUsers.class).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询用户信息失败！");
		}finally {
			if (sqlExecutor != null)
				sqlExecutor.closeConnection();
		}
	}

	public List searchByLeve(String sorgcode, String orgleve,
			DetailTsUsers detailDto) throws ITFEBizException {
		StringBuffer sql = new StringBuffer();
		sql
				.append("SELECT u.*,o.S_ORGNAME,o.S_ORGLEVEL FROM TS_USERS u INNER JOIN TS_ORGAN o ON u.S_ORGCODE = o.S_ORGCODE  where o.S_ORGCODE != '000000000000' ");
		if (StringUtils.isNotBlank(detailDto.getSusertype())) {
			sql.append(" and  u.S_USERTYPE = '" + detailDto.getSusertype()
					+ "' ");
		}
		if (StringUtils.isNotBlank(detailDto.getSuserstatus())) {
			sql.append(" and u.S_USERSTATUS = '" + detailDto.getSuserstatus()
					+ "' ");
		}
		if (StringUtils.isNotBlank(detailDto.getSloginstatus())) {
			sql.append(" and u.s_loginstatus = '" + detailDto.getSloginstatus()
					+ "' ");
		}
//		if (StringUtils.isNotBlank(detailDto.getSorglevel())) {
//			sql.append(" and o.S_orglevel = '" + detailDto.getSorglevel()
//					+ "' ");
//		}
		// 如果机构级次为2 说明找省库
		String levewhere = null;
		if ("2".equals(orgleve)) { // 省库
			sql.append(" and o.S_orgcode like '%" + sorgcode.substring(0, 2)
					+ "%" + orgleve + "'");
		} else if ("3".equals(orgleve)) { // 3代表市库
			if (sorgcode.endsWith("2")) { // 如果是省库则 查出 结尾为3的 否则截取结尾为3和机构代码的钱四位
				sql.append(" and o.S_orgcode like '%"
						+ sorgcode.substring(0, 2) + "%" + orgleve + "'");
			} else {
				sql.append(" and o.S_orgcode like '%"
						+ sorgcode.substring(0, 4) + "%" + orgleve + "'");
			}
		} else if ("4".equals(orgleve)) { // 4:代表县库
			sql.append(" and o.S_orgcode like '%" + sorgcode.substring(0, 4)
					+ "%" + orgleve + "'");
		} else if ("5".equals(orgleve)) { // 乡库
			sql.append(" and o.S_orgcode like '%" + sorgcode.substring(0, 4)
					+ "%" + orgleve + "'");
		}
		SQLExecutor sqlExecutor = null;
		try {
			sqlExecutor = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			return (List) sqlExecutor.runQuery(sql.toString(),
					DetailTsUsers.class).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询用户信息失败！");
		}finally {
			if (sqlExecutor != null)
				sqlExecutor.closeConnection();
		}
	}

}
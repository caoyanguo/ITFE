package com.cfcc.itfe.facade;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public class TSystemFacade {
	static Log logger = LogFactory.getLog(TSystemFacade.class);

	/**
	 * 获取系统时间戳
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static Timestamp getDBSystemTime() throws JAFDatabaseException {
		String sql = "select current timestamp from sysibm.sysdummy1 with ur";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			SQLResults res = sqlExec.runQueryCloseCon(sql);
			return res.getTimestamp(0, 0);
		} catch (JAFDatabaseException e) {
			logger.error("查询系统时间发生数据库异常：" + e);
			throw e;
		}
	}

	/**
	 * 获取数据库当前日期
	 * 
	 * @return java.sql.Date
	 * @throws JAFDatabaseException
	 */
	public static Date findDBSystemDate() throws JAFDatabaseException {
		String sql = "select current date from sysibm.sysdummy1 with ur";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			SQLResults res = sqlExec.runQueryCloseCon(sql);
			return res.getDate(0, 0);
		} catch (JAFDatabaseException e) {
			logger.error("查询系统日期发生数据库异常：" + e);
			throw e;
		}
	}

	/**
	 * 获取数据库当前日期后X天的日期
	 * 
	 * @param x
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static Date findAfterDBSystemDate(int x) throws JAFDatabaseException {
		String sql = "select current date + " + x
				+ " days from sysibm.sysdummy1 with ur";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();

			SQLResults res = sqlExec.runQueryCloseCon(sql);
			return res.getDate(0, 0);
		} catch (JAFDatabaseException e) {
			logger.error("查询系统日期发生数据库异常：" + e);
			throw e;
		}
	}

	/**
	 * 获取数据库当前日期后X天的时间
	 * 
	 * @param x
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static Date findAfterDBSystemTime(int x) throws JAFDatabaseException {
		String sql = "select current timestamp + " + x
				+ " days from sysibm.sysdummy1 with ur";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			SQLResults res = sqlExec.runQueryCloseCon(sql);
			return res.getDate(0, 0);
		} catch (JAFDatabaseException e) {
			logger.error("查询系统日期发生数据库异常：" + e);
			throw e;
		}
	}
	
	/**
	 * 获取所属业务种类枚举类型的枚举值(根据枚举类型编码)
	 * @param typecode
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static List<TdEnumvalueDto> findEnumValuebyTypeCode(String typecode)
			throws JAFDatabaseException {
        //修改为只取可用的枚举值，不再全部获取，因为无纸化仅仅包含支出业务
		String sql = "select distinct s_value,S_VALUECMT from td_enumvalue where S_TYPECODE=? AND S_IFAVAILABLE = '1'";
		SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();
		exec.addParam(typecode);
		SQLResults res = exec.runQueryCloseCon(sql, TdEnumvalueDto.class);
		return (List<TdEnumvalueDto>) res.getDtoCollection();

	}
	
	/**
	 * 根据核算主体代码和用户编号查询用户与权限的对照关系-返回s_funccode
	 * 
	 * @author caoyg
	 * @param userInfo
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static List<String> findFuncCodeByUserAndOrg(String usercode,
			String sorgcode) throws JAFDatabaseException {
		try {
			String sql = "select S_FUNCCODE from TS_USERSFUNC where S_ORGCODE=? and S_USERCODE=?";
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			exec.addParam(sorgcode);
			exec.addParam(usercode);
			SQLResults res = exec.runQueryCloseCon(sql);
			int rowCount = res.getRowCount();
			List<String> funcCodeList = new ArrayList<String>();
			for (int i = 0; i < rowCount; i++) {
				funcCodeList.add(res.getString(0, i));
			}
			return funcCodeList;
		} catch (JAFDatabaseException e) {
			logger.error("查询异常：" + e);
			throw e;
		}
	}
	
	/**
	 * 查找中心机构代码
	 * 
	 * @author caoyg
	 * @param userInfo
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static String  findCenterOrg() throws JAFDatabaseException {
		try {
			String sql = "select * from ts_organ where s_orglevel=? and S_ORGSTATUS =?";
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			exec.addParam(StateConstant.ORG_CENTER_LEVEL);
			exec.addParam(StateConstant.ORG_STATE);
			SQLResults res = exec.runQueryCloseCon(sql);
			int rowCount = res.getRowCount();
			if (rowCount> 0 ) {
				return res.getString(0, 0);
			}
			
		} catch (JAFDatabaseException e) {
			logger.error("查询异常：" + e);
			throw e;
		}
		return null;
	}

	
}

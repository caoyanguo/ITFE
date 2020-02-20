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
	 * ��ȡϵͳʱ���
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
			logger.error("��ѯϵͳʱ�䷢�����ݿ��쳣��" + e);
			throw e;
		}
	}

	/**
	 * ��ȡ���ݿ⵱ǰ����
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
			logger.error("��ѯϵͳ���ڷ������ݿ��쳣��" + e);
			throw e;
		}
	}

	/**
	 * ��ȡ���ݿ⵱ǰ���ں�X�������
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
			logger.error("��ѯϵͳ���ڷ������ݿ��쳣��" + e);
			throw e;
		}
	}

	/**
	 * ��ȡ���ݿ⵱ǰ���ں�X���ʱ��
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
			logger.error("��ѯϵͳ���ڷ������ݿ��쳣��" + e);
			throw e;
		}
	}
	
	/**
	 * ��ȡ����ҵ������ö�����͵�ö��ֵ(����ö�����ͱ���)
	 * @param typecode
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static List<TdEnumvalueDto> findEnumValuebyTypeCode(String typecode)
			throws JAFDatabaseException {
        //�޸�Ϊֻȡ���õ�ö��ֵ������ȫ����ȡ����Ϊ��ֽ����������֧��ҵ��
		String sql = "select distinct s_value,S_VALUECMT from td_enumvalue where S_TYPECODE=? AND S_IFAVAILABLE = '1'";
		SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();
		exec.addParam(typecode);
		SQLResults res = exec.runQueryCloseCon(sql, TdEnumvalueDto.class);
		return (List<TdEnumvalueDto>) res.getDtoCollection();

	}
	
	/**
	 * ���ݺ������������û���Ų�ѯ�û���Ȩ�޵Ķ��չ�ϵ-����s_funccode
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
			logger.error("��ѯ�쳣��" + e);
			throw e;
		}
	}
	
	/**
	 * �������Ļ�������
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
			logger.error("��ѯ�쳣��" + e);
			throw e;
		}
		return null;
	}

	
}

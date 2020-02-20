package com.cfcc.itfe.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.itfe.facade.DBOperUtil;
import com.cfcc.itfe.facade.data.SQLMapping;

/**
 * 
 * ���������������
 * 
 */
public class DataServiceUtil {
	/**
	02 * ���ݷ��ʷ��񹫹�����
	 * @param beanid �����ļ�����ID
	 * @param filterMap ��Ҫ���й��˵�Map������key����Ҫ�أ����null,�����й���
	 * @param sqlExec SQLExecutor ���Ϊ�գ�ϵͳ�Զ�����һ�� 
	 * @param params SQL�����б� ��Ϊ��
	 * @throws JAFDatabaseException
	 */
	public static void accessDataService(String beanid,
			HashMap<String, Object> filterMap, SQLExecutor sqlExec,
			List<Object> params) throws JAFDatabaseException {

		List<SQLMapping> dbSQLs = (List<SQLMapping>) ContextFactory
				.getApplicationContext().getBean(beanid);
		if (dbSQLs == null || dbSQLs.size() <= 0) {
			return;
		}
		if (filterMap == null) {
			DBOperUtil.exec(dbSQLs, sqlExec, params);
		} else {
			List<SQLMapping> sqlMapList = new ArrayList<SQLMapping>();
			for (SQLMapping sqlmap : dbSQLs) {
				if (filterMap.containsKey(sqlmap.getSqlID())) {
					sqlMapList.add(sqlmap);
				}
			}
			DBOperUtil.exec(sqlMapList, sqlExec, params);
		}

	}

}

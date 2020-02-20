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
 * 数据清理服务公用类
 * 
 */
public class DataServiceUtil {
	/**
	02 * 数据访问服务公共方法
	 * @param beanid 配置文件配置ID
	 * @param filterMap 需要进行过滤的Map，根据key过滤要素，如果null,不进行过滤
	 * @param sqlExec SQLExecutor 如果为空，系统自动生成一个 
	 * @param params SQL参数列表 可为空
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

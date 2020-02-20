package com.cfcc.itfe.service.dataquery.selectrecord1;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.service.dataquery.selectrecord1.AbstractSelectRecordGDService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutorFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
/**
 * @author db2admin
 * @time   16-01-14 09:09:45
 * codecomment: 
 */

public class SelectRecordGDService extends AbstractSelectRecordGDService {
	private static Log log = LogFactory.getLog(SelectRecordGDService.class);

	public List selectGroupResultByCondition(String sql)
			throws ITFEBizException {
		List<TvPayreckBankDto> list = null;
		SQLResults res = null;
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			res = sqlExec.runQueryCloseCon(sql, TvPayreckBankDto.class);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};

		if (res.getDtoCollection() == null
				&& res.getDtoCollection().size() == 0) {
			return null;
		}
		list = (List<TvPayreckBankDto>) res.getDtoCollection();
		//return res.getDtoCollection();
		// TODO Auto-generated method stub
		return list;
	}	


}
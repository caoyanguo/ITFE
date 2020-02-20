package com.cfcc.itfe.service.dataquery.bizdatasearch;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.BizDataSearchDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.dataquery.bizdatasearch.AbstractBizdatasearchService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
/**
 * @author Administrator
 * @time   12-05-30 17:26:34
 * codecomment: 
 */

public class BizdatasearchService extends AbstractBizdatasearchService {
	private static Log log = LogFactory.getLog(BizdatasearchService.class);	


	/**
	 * 获取查询结果
	 	 
	 * @generated
	 * @param sql
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List getResult(String sql) throws ITFEBizException {
    	try {
			SQLExecutor sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			SQLResults sqlresults = sqlExecutor.runQueryCloseCon(sql, BizDataSearchDto.class);
			if(null != sqlresults && sqlresults.getRowCount() > 0){
				return (List<BizDataSearchDto>) sqlresults.getDtoCollection();
			}else{
				return null;
			}
		} catch (JAFDatabaseException e) {
			log.error("查询数据库错误！");
			throw new ITFEBizException("查询数据库错误！",e);
		}
    }

}